package com.telit.zhkt_three.Activity.ClassRecord;

import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.interactive.DiscussCommunicationRVAdapter;
import com.telit.zhkt_three.Adapter.interactive.DiscussFileRVAdapter;
import com.telit.zhkt_three.Adapter.interactive.DiscussMemberRVAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.Fragment.Interactive.OvershootFromRightAnim;
import com.telit.zhkt_three.JavaBean.Gson.GroupListBean;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussBean;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussListBeanTwo;
import com.telit.zhkt_three.MediaTools.audio.AudioPlayActivity;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.DiscussBeanDao;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * todo 排序
 * 分组讨论详情页面
 * 一、没有发送栏
 * todo 二、新增文件查询入口
 * todo 三、新增查看结论入口 ： 结论文件和文字结论
 * <p>
 * 附件URL是全的
 */
public class RecordDiscussShowActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;

    @BindView(R.id.record_discuss_back)
    ImageView record_discuss_back;
    @BindView(R.id.discuss_topic_name)
    TextView discuss_topic_name;
    @BindView(R.id.discuss_file_frame)
    FrameLayout discuss_file_frame;
    @BindView(R.id.discuss_file_dot)
    ImageView discuss_file_dot;
    @BindView(R.id.discuss_member_info)
    TextView discuss_member_info;
    @BindView(R.id.discuss_rv_member)
    RecyclerView discuss_rv_member;
    @BindView(R.id.discuss_rv_communication)
    RecyclerView discuss_rv_communication;
    @BindView(R.id.show_discuss_file_relative)
    RelativeLayout show_discuss_file_relative;
    @BindView(R.id.show_discuss_conclusion)
    TextView show_discuss_conclusion;

    //组员信息
    private DiscussMemberRVAdapter discussMemberRVAdapter;
    private List<DiscussListBeanTwo> discussMemberList;

    //讨论内容
    private DiscussCommunicationRVAdapter discussCommunicationRVAdapter;
    private List<DiscussBean> discussBeanList;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    /**
     * 分组讨论ID
     */
    private String discussId;

    /**
     * 类型，需要传递给服务端
     */
    private String type;

    //附带的文件集合，只有一个文件
    private List<String> fileList;

    private String conclusionFileUrl;

    public void setDiscussId(String discussId) {
        QZXTools.logE("discussId=" + discussId, null);
        this.discussId = discussId;
    }

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Member_success = 2;

    private static boolean  isShow=false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(RecordDiscussShowActivity.this, getResources().getString(R.string.current_net_err), false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }


                    NoSercerDialog noSercerDialog=new NoSercerDialog();
                    noSercerDialog.show(getSupportFragmentManager(), NoSercerDialog.class.getSimpleName());
                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(RecordDiscussShowActivity.this, "没有相关资源！", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                    }


                    break;
                case Operator_Member_success:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        //把头像和学生ID的关系MAP赋给讨论
                        HashMap<String, String> avatarBindMap = (HashMap<String, String>) msg.obj;
                        discussCommunicationRVAdapter.setAvatarMap(avatarBindMap);

                        //是否显示文件
                        if (TextUtils.isEmpty(discussMemberList.get(0).getFileUrl())) {
                            discuss_file_frame.setVisibility(View.GONE);
                        } else {
                            discuss_file_frame.setVisibility(View.VISIBLE);
                            if (fileList == null) {
                                fileList = new ArrayList<>();
                            }
                            fileList.add(discussMemberList.get(0).getFileUrl());
                        }

                        //设置小组信息
                        String groupName = discussMemberList.get(0).getGroupName();
                        int memberCount = discussMemberList.size();

                        StringBuilder stringBuilder = new StringBuilder();
                        stringBuilder.append(groupName);
                        stringBuilder.append("讨论组成员(");
                        stringBuilder.append(memberCount);
                        stringBuilder.append("人)");
                        discuss_member_info.setText(stringBuilder.toString());

                        //主题
                        String topic = discussMemberList.get(0).getTheme();
                        if (TextUtils.isEmpty(topic)) {
                            topic = "自由讨论";
                        }
                        discuss_topic_name.setText(topic);

                        //结论展示
                        String conclusionFile = discussMemberList.get(0).getEnclosure();
                        if (TextUtils.isEmpty(conclusionFile)) {
                            show_discuss_file_relative.setVisibility(View.GONE);
                        } else {
                            conclusionFileUrl = conclusionFile;
                            show_discuss_file_relative.setVisibility(View.VISIBLE);
                        }

                        String conclusionText = discussMemberList.get(0).getGroupConclusion();
                        if (TextUtils.isEmpty(conclusionText)) {
                            show_discuss_conclusion.setVisibility(View.GONE);
                        } else {
                            show_discuss_conclusion.setVisibility(View.VISIBLE);
                            show_discuss_conclusion.setText(conclusionText);
                        }

                        //为了开启动画
                        for (int i = 0; i < discussMemberList.size(); i++) {
                            discussMemberRVAdapter.notifyItemChanged(i);
                        }
                    }

                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record_discuss_show);
        unbinder = ButterKnife.bind(this);
        isShow=true;
        record_discuss_back.setOnClickListener(this);

        discussId = getIntent().getStringExtra("recordId");
        type = getIntent().getStringExtra("type");
        if (TextUtils.isEmpty(discussId)) {
            QZXTools.popToast(this, "讨论ID无效", false);
            return;
        }

        //获取讨论内容
        discussBeanList = MyApplication.getInstance().getDaoSession().getDiscussBeanDao().queryBuilder()
                .where(DiscussBeanDao.Properties.DiscussId.eq(discussId)).list();
        QZXTools.logE("discussBeanList=" + discussBeanList, null);
        if (discussBeanList == null) {
            discussBeanList = new ArrayList<>();
        }

        discuss_rv_member.setLayoutManager(new LinearLayoutManager(this));
        discuss_rv_member.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //recyclerView的Item的添加动画
//        OvershootInRightAnimator overshootFromRightAnim = new OvershootInRightAnimator();
        OvershootFromRightAnim overshootFromRightAnim = new OvershootFromRightAnim();
        overshootFromRightAnim.setAddDuration(1000);
        discuss_rv_member.setItemAnimator(overshootFromRightAnim);
        discussMemberList = new ArrayList<>();
        discussMemberRVAdapter = new DiscussMemberRVAdapter(this, discussMemberList);
        discuss_rv_member.setAdapter(discussMemberRVAdapter);

        discuss_rv_communication.setLayoutManager(new LinearLayoutManager(this));
        discuss_rv_communication.setOverScrollMode(View.OVER_SCROLL_NEVER);

        discussCommunicationRVAdapter = new DiscussCommunicationRVAdapter(this, discussBeanList);
        discuss_rv_communication.setAdapter(discussCommunicationRVAdapter);

        //点击文件按钮
        discuss_file_frame.setOnClickListener(this);

        //点击结论文件按钮
        show_discuss_file_relative.setOnClickListener(this);

        //获取小组信息
        fetchGroupInfo();
    }

    @Override
    protected void onDestroy() {
        isShow=false;
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    private PopupWindow menuPopup;

    /**
     * 点击文件图标显示所有下发的文件
     */
    private void popupSelectMenu(View v) {
        if (menuPopup != null) {
            menuPopup.dismiss();
        }

        View menuView = LayoutInflater.from(this).inflate(R.layout.pull_rv_menu_layout, null);

        menuPopup = new PopupWindow(menuView, (int) getResources().getDimension(R.dimen.x400), ViewGroup.LayoutParams.WRAP_CONTENT);

        menuPopup.setBackgroundDrawable(new ColorDrawable());
        menuPopup.setOutsideTouchable(true);

        ConstraintLayout constraintLayout = menuView.findViewById(R.id.pull_menu_bg);
        //设置弹出框背景色
        constraintLayout.setBackgroundColor(getResources().getColor(R.color.word_gray));

        RecyclerView recyclerView = menuView.findViewById(R.id.pull_menu_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        DiscussFileRVAdapter discussFileRVAdapter = new DiscussFileRVAdapter(this, fileList);
        recyclerView.setAdapter(discussFileRVAdapter);

        //popup只有具体的尺寸，底部空间不够才会在上面显示
        menuPopup.showAsDropDown(v, 0, 0);
    }

    private void fetchGroupInfo() {
        if (circleProgressDialogFragment == null) {
            circleProgressDialogFragment = new CircleProgressDialogFragment();
        }
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());


//        String url = UrlUtils.BaseUrl + UrlUtils.QueryRecordInfo;
        String url=UrlUtils.BaseUrl+UrlUtils.DiscussGroupTwo;
        Map<String, String> paraMap = new LinkedHashMap<>();

        paraMap.put("userId", UserUtils.getUserId());
        paraMap.put("discussId", discussId);
        paraMap.put("classId", UserUtils.getClassId());

//        paraMap.put("recordId", discussId);
//        paraMap.put("type", type);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                if (call.isCanceled()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QZXTools.popToast(getApplicationContext(), "取消了请求", false);
                        }
                    });
                }

                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
                fetchGroupInfo();
                CrashReport.postCatchedException(e);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
                    QZXTools.logE("resultJson=" + resultJson, null);
                    Gson gson = new Gson();
                    GroupListBean groupListBean = gson.fromJson(resultJson, GroupListBean.class);
                    if (groupListBean.getResult() != null && groupListBean.getResult().size() > 0) {
                        for (DiscussListBeanTwo discussListBeanTwo : groupListBean.getResult()) {
                            discussMemberList.add(discussListBeanTwo);
                        }
                        //头像和学生ID绑定
                        Map<String, String> avatarBindMap = new HashMap<>();
                        for (DiscussListBeanTwo discussListBeanTwo : discussMemberList) {
                            avatarBindMap.put(discussListBeanTwo.getUserId(), discussListBeanTwo.getPhoto());
                        }
                        QZXTools.logE("map=" + avatarBindMap, null);
                        Message message = mHandler.obtainMessage();
                        message.what = Operator_Member_success;
                        message.obj = avatarBindMap;
                        mHandler.sendMessage(message);
                    } else {
                        mHandler.sendEmptyMessage(Error404);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.record_discuss_back:
                finish();
                break;
            case R.id.discuss_file_frame:
                popupSelectMenu(v);
                break;
            case R.id.show_discuss_file_relative:
                //mp3 mp4 jpg 因为结论提交只有这三种格式，学生端提交的
                String format = conclusionFileUrl.substring(conclusionFileUrl.lastIndexOf(".") + 1);
                conclusionFileUrl = /*UrlUtils.BaseUrl +*/ conclusionFileUrl;
                if (format.equals("mp3")) {
                    Intent intent = new Intent(this, AudioPlayActivity.class);
                    intent.putExtra("AudioFilePath", conclusionFileUrl);
                    intent.putExtra("AudioFileName", "分组讨论音频");
                    startActivity(intent);
                } else if (format.equals("mp4")) {
                    Intent intent_video = new Intent(this, VideoPlayerActivity.class);
                    intent_video.putExtra("VideoFilePath", conclusionFileUrl);
                    intent_video.putExtra("VideoTitle", "分组讨论视频");
                    startActivity(intent_video);
                } else if (format.equals("jpg") || format.equals("png") || format.equals("gif")) {
                    Intent intent_img = new Intent(this, ImageLookActivity.class);
                    ArrayList<String> imgFilePathList = new ArrayList<>();
                    imgFilePathList.add(conclusionFileUrl);
                    intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                    intent_img.putExtra("curImgIndex", 0);
                    startActivity(intent_img);
                }
                break;
        }
    }
}
