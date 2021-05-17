package com.telit.zhkt_three.Fragment.Interactive;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.ValueCallback;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Adapter.interactive.DiscussCommunicationRVAdapter;
import com.telit.zhkt_three.Adapter.interactive.DiscussMemberRVAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.DiscussConclusionFragment;
import com.telit.zhkt_three.Fragment.Dialog.TBSDownloadDialog;
import com.telit.zhkt_three.JavaBean.Gson.GroupListBean;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussBean;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussListBeanTwo;
import com.telit.zhkt_three.JavaBean.PreView.RecordStatus;
import com.telit.zhkt_three.MediaTools.CropActivity;
import com.telit.zhkt_three.MediaTools.audio.AudioPlayActivity;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.CyptoUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.SerializeUtil;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/6/24 16:42
 * <p>
 * todo 弹出软键盘有点丑，全部上移了
 * todo 发送的聊天文本信息加密
 * todo 发送图片清晰度的问题：1000x600
 * <p>
 * 一、分组前知道是学生自己选择主题分组模式还是已经分好组了
 * <p>
 * 设置了两次的连接超时再次请求
 * <p>
 * 资源文件全路径
 */
public class GroupDiscussFragment extends Fragment implements View.OnClickListener
        , ZBVPermission.PermPassResult, ValueCallback<String> {

    private Unbinder unbinder;

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
    @BindView(R.id.discuss_edit)
    EditText discuss_edit;
    @BindView(R.id.discuss_send_pics)
    ImageView discuss_send_pics;
    @BindView(R.id.discuss_send_btn)
    Button discuss_send_btn;

    /**
     * 分组讨论ID
     */
    private String discussId;

    public void setDiscussId(String discussId) {
        QZXTools.logE("discussId=" + discussId, null);
        this.discussId = discussId;
    }

    //组的ID
    private int discussGroupId;
    private String groupIndex;


    //组员信息
    private DiscussMemberRVAdapter discussMemberRVAdapter;
    private List<DiscussListBeanTwo> discussMemberList;

    //讨论内容
    private DiscussCommunicationRVAdapter discussCommunicationRVAdapter;
    private List<DiscussBean> discussBeanList;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Member_success = 2;
    private static final int Alter_Photo_Result = 3;

    //判断这个界面是不是还显示，主要是解决网络请求闪退的问题
    private static boolean  isShow=false;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(getContext(), "当前网络不佳....", false);
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(getContext(), "没有相关资源！", false);
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
                            if (discuss_file_frame!=null)
                            discuss_file_frame.setVisibility(View.GONE);
                        } else {
                            if (discuss_file_frame!=null)
                            discuss_file_frame.setVisibility(View.VISIBLE);
                            if (fileList == null) {
                                fileList = new ArrayList<>();
                            }
                            fileList.add(discussMemberList.get(0).getFileUrl());
                        }

                        discussGroupId = discussMemberList.get(0).getDiscussGroupId();
                        groupIndex = discussMemberList.get(0).getGroupIndex();

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

                        //为了开启动画
                        for (int i = 0; i < discussMemberList.size(); i++) {
                            discussMemberRVAdapter.notifyItemChanged(i);
                        }
                    }

                    break;
                case Alter_Photo_Result:
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        DiscussBean discussBean = (DiscussBean) msg.obj;
                        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_DISCUSS, MsgUtils.createDiscuss(discussBean));
                    }

                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_group_discuss_layout, container, false);
        unbinder = ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        isShow=true;
        //软键盘弹出挤压视图
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        discuss_rv_member.setLayoutManager(new LinearLayoutManager(getContext()));
        discuss_rv_member.setOverScrollMode(View.OVER_SCROLL_NEVER);
        //recyclerView的Item的添加动画
        discussMemberList = new ArrayList<>();
        OvershootFromRightAnim overshootFromRightAnim = new OvershootFromRightAnim();
        overshootFromRightAnim.setAddDuration(1000);
        discuss_rv_member.setItemAnimator(overshootFromRightAnim);
        discussMemberRVAdapter = new DiscussMemberRVAdapter(getContext(), discussMemberList);
        discuss_rv_member.setAdapter(discussMemberRVAdapter);

        discuss_rv_communication.setLayoutManager(new LinearLayoutManager(getContext()));
        discuss_rv_communication.setOverScrollMode(View.OVER_SCROLL_NEVER);
        discussBeanList = new ArrayList<>();
        discussCommunicationRVAdapter = new DiscussCommunicationRVAdapter(getContext(), discussBeanList);
        discuss_rv_communication.setAdapter(discussCommunicationRVAdapter);

        discuss_send_btn.setOnClickListener(this);
        discuss_send_pics.setOnClickListener(this);
        discuss_file_frame.setOnClickListener(this);

        //文本监听
        discuss_edit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    //用户输入了文本
                    discuss_send_pics.setVisibility(View.GONE);
                    discuss_send_btn.setVisibility(View.VISIBLE);
                } else {
                    discuss_send_pics.setVisibility(View.VISIBLE);
                    discuss_send_btn.setVisibility(View.GONE);
                }
            }
        });

        fetchGroupInfo();

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //防止出现界面消失，网络请求才反馈到
        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment = null;
        }

        ZBVPermission.getInstance().recyclerAll();
        EventBus.getDefault().unregister(this);
        //防止泄露
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();
        //不要显示网路请求
        isShow=false;
        super.onDestroyView();
    }

    /**
     * 讨论结束展示记录员总结界面
     */
    public void showConclusionView() {
        if (discussMemberList == null || discussMemberList.size() <= 0) {
            return;
        }
        //是否是记录员

        String recorderUserId = discussMemberList.get(0).getGroupLeader();
        String ownUserId = UserUtils.getUserId();

        if (recorderUserId.equals(ownUserId)) {
            DiscussConclusionFragment discussConclusionFragment = new DiscussConclusionFragment();
            Bundle bundle = new Bundle();
            bundle.putInt("discussGroupId", discussGroupId);
            bundle.putString("discussId", discussId);
            bundle.putString("groupIndex", groupIndex);
            discussConclusionFragment.setArguments(bundle);
            discussConclusionFragment.show(getChildFragmentManager(), DiscussConclusionFragment.class.getSimpleName());
        } else {
            //其余进入白板界面
            EventBus.getDefault().post("", Constant.Show_Conclusion);
        }
    }

    @Subscriber(tag = Constant.Discuss_Send_Pic, mode = ThreadMode.MAIN)
    public void sendPic(String filePath) {
        sendDiscussMsg(filePath, MsgUtils.TYPE_PICTURE);
    }

    /**
     * 接收发送的讨论消息：注意自己发送的消息自己也能接收到
     * 这里采用receiveMsgInfo更新，而不是自己发的自己先更新
     */
    @Subscriber(tag = Constant.Discuss_Message, mode = ThreadMode.MAIN)
    public void receiveMsgInfo(String discussMsg) {
        QZXTools.logE("receive=" + discussMsg, null);
        Gson gson = new Gson();
        DiscussBean discussBean = gson.fromJson(discussMsg, DiscussBean.class);
//        //获取接收的消息
//        if (discussBean.getType() == MsgUtils.TYPE_TEXT) {
//            //解密
//            String content = discussBean.getContent();
//            discussBean.setContent(CyptoUtils.decode(Constant.DESKey, content));
//        }
        discussBeanList.add(discussBean);
        discussCommunicationRVAdapter.notifyDataSetChanged();
        discuss_rv_communication.smoothScrollToPosition(discussBeanList.size() - 1);

        //更新发言时间
        for (DiscussListBeanTwo discussListBean : discussMemberList) {
            if (discussListBean.getUserId().equals(discussBean.getStudentId())) {
                discussListBean.setSpeakTime(QZXTools.DateOrTimeStrShow(discussBean.getTime()));
            }
        }
        discussMemberRVAdapter.notifyDataSetChanged();

        //保存本地数据库
        discussBean.setDiscussId(discussId);
        QZXTools.logE("start save receive msg="+discussMsg,null);
        MyApplication.getInstance().getDaoSession().getDiscussBeanDao().insertOrReplace(discussBean);
        QZXTools.logE("end save receive msg="+discussMsg,null);
    }

    private static final String[] needPermissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private boolean isDiscussFile = false;

    private boolean isCamera = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.discuss_send_btn:
                if (discuss_edit.getText().toString().trim().equals("")) {
                    QZXTools.popCommonToast(getContext(), "不能发送空消息", false);
                    return;
                }

                sendDiscussMsg(discuss_edit.getText().toString().trim(), MsgUtils.TYPE_TEXT);

                //置空
                discuss_edit.setText("");
                //发送的点击事件 隐藏那个软键盘
                InputMethodManager inputMethodManager = (InputMethodManager) getContext().getSystemService(Activity.INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(discuss_send_btn.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                break;
            case R.id.discuss_send_pics:
                if (picPopup != null && picPopup.isShowing()) {
                    picPopup.dismiss();
                    picPopup = null;
                } else {
                    popupPic(v);
                }
                break;
            case R.id.discuss_file_frame:
                //文件预览
                popupSelectMenu(v);
                break;
            case R.id.group_pic_camera:
                //相机拍照
                ZBVPermission.getInstance().setPermPassResult(this);

                if (!ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                    isCamera = true;
                    isDiscussFile = false;
                    ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                } else {
                    //直接打开相机
                    QZXTools.logD("已拥有权限直接打开相机");
                    openCamera();
                }
                break;
            case R.id.group_pic_album:
                //打开相册
                ZBVPermission.getInstance().setPermPassResult(this);

                if (!ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                    isDiscussFile = false;
                    isCamera = false;
                    ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                } else {
                    //直接打开相册
                    QZXTools.logD("已拥有权限直接打开相机");
                    openSysAlbum();
                }
                break;
        }
    }

    private PopupWindow picPopup;

    /**
     * 拍照或者相册弹框
     */
    private void popupPic(View v) {
        if (picPopup != null) {
            picPopup.dismiss();
            picPopup = null;
        }

        View picView = LayoutInflater.from(getContext()).inflate(R.layout.group_pop_pic, null);

        picPopup = new PopupWindow(picView, (int) getResources().getDimension(R.dimen.x150),
                (int) getResources().getDimension(R.dimen.x120));

        picPopup.setBackgroundDrawable(new ColorDrawable());
        picPopup.setOutsideTouchable(true);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "PingFang-SimpleBold.ttf");

        TextView pic_camera = picView.findViewById(R.id.group_pic_camera);
        TextView pic_album = picView.findViewById(R.id.group_pic_album);

        pic_camera.setTypeface(typeface);
        pic_album.setTypeface(typeface);

        pic_camera.setOnClickListener(this);
        pic_album.setOnClickListener(this);

        // picPop.isShowing不管用,因为pop需要焦点 dialog.setFocusable(true);
        picPopup.setFocusable(true);

        picView.measure(0, 0);

        picPopup.showAsDropDown(v, -(picView.getMeasuredWidth() - v.getWidth()) / 2,
                -(int) getResources().getDimension(R.dimen.x5));
    }


    private PopupWindow menuPopup;

    /**
     * 点击文件图标显示所有下发的文件
     */
    private void popupSelectMenu(View v) {
        if (menuPopup != null) {
            menuPopup.dismiss();
            menuPopup = null;
        }

        View menuView = LayoutInflater.from(getContext()).inflate(R.layout.pull_rv_menu_layout, null);

        menuPopup = new PopupWindow(menuView, (int) getResources().getDimension(R.dimen.x400), ViewGroup.LayoutParams.WRAP_CONTENT);

        menuPopup.setBackgroundDrawable(new ColorDrawable());
        menuPopup.setOutsideTouchable(true);

        ConstraintLayout constraintLayout = menuView.findViewById(R.id.pull_menu_bg);
        //设置弹出框背景色
        constraintLayout.setBackgroundColor(getResources().getColor(R.color.word_gray));

        RecyclerView recyclerView = menuView.findViewById(R.id.pull_menu_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        RVPullMenuAdapter adapter = new RVPullMenuAdapter();
        recyclerView.setAdapter(adapter);

        //popup只有具体的尺寸，底部空间不够才会在上面显示
        menuPopup.showAsDropDown(v, 0, 0);
    }

    private List<String> fileList;

    private String fileUrl;

    //显示用户查看了几个文件
    private int dotCount;

    /**
     * author: qzx
     * Date: 2019/5/15 15:16
     */
    public class RVPullMenuAdapter extends RecyclerView.Adapter<RVPullMenuAdapter.RVPullMenuViewHolder> {

        @NonNull
        @Override
        public RVPullMenuViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            return new RVPullMenuViewHolder(LayoutInflater.from(getContext())
                    .inflate(R.layout.adapter_item_discuss_file, viewGroup, false));
        }

        @Override
        public void onBindViewHolder(@NonNull RVPullMenuViewHolder rvPullMenuViewHolder, int i) {
            rvPullMenuViewHolder.textView.setText("文件资源" + (i + 1));
            String url = fileList.get(i);
            String format = url.substring(url.lastIndexOf(".") + 1);
            if (format.equals("mp4") || format.equals("avi")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.video);
            } else if (format.equals("mp3")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.voice);
            } else if (format.equals("jpg") || format.equals("png") || format.equals("gif")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.picture);
            } else if (format.equals("ppt") || format.equals("pptx")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.ppt);
            } else if (format.equals("doc") || format.equals("docx") || format.equals("txt")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.word);
            } else if (format.equals("xls") || format.equals("xlsx")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.excel);
            } else if (format.equals("pdf")) {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.pdf);
            } else {
                rvPullMenuViewHolder.imageView.setImageResource(R.mipmap.file);
            }
        }

        @Override
        public int getItemCount() {
            return fileList != null ? fileList.size() : 0;
        }

        public class RVPullMenuViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;
            private ImageView imageView;

            public RVPullMenuViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.dicuss_file_name);
                imageView = itemView.findViewById(R.id.discuss_file_sign);

                itemView.setTag("false");

                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (menuPopup != null && menuPopup.isShowing()) {
                            menuPopup.dismiss();
                            menuPopup = null;
                        }

                        if (v.getTag().equals("false")) {
                            v.setTag("true");
                            dotCount++;

                            if (dotCount == fileList.size()) {
                                changeDot();
                            }
                        }

                        String url = fileList.get(getLayoutPosition());
                        String format = url.substring(url.lastIndexOf(".") + 1);

                        String actualUrl = url;

                        if (format.equals("mp4") || format.equals("avi")) {

                            Intent intent_video = new Intent(getContext(), VideoPlayerActivity.class);
                            intent_video.putExtra("VideoFilePath", actualUrl);
                            getContext().startActivity(intent_video);

                        } else if (format.equals("mp3")) {

                            Intent intent = new Intent(getContext(), AudioPlayActivity.class);
                            intent.putExtra("AudioFilePath", actualUrl);
                            getContext().startActivity(intent);

                        } else if (format.equals("jpg") || format.equals("png") || format.equals("gif") || format.equals("jpeg")) {

                            Intent intent_img = new Intent(getContext(), ImageLookActivity.class);
                            ArrayList<String> imgFilePathList = new ArrayList<>();
                            imgFilePathList.add(actualUrl);
                            intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                            intent_img.putExtra("curImgIndex", 0);
                            getContext().startActivity(intent_img);

                        } else {
                            ZBVPermission.getInstance().setPermPassResult(GroupDiscussFragment.this);
                            if (ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                                fileUrl = actualUrl;
                                handlerTBSShow();
                            } else {
                                isDiscussFile = true;
                                ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                            }
                        }
                    }
                });

            }
        }
    }

    private TBSDownloadDialog tbsDownloadDialog;
    private ArrayList<RecordStatus> recordStatuses = null;

    private void handlerTBSShow() {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }

        String preViewUrl = fileUrl;

        recordStatuses = null;
        //先判断是否存有记录
        String saveRecordPath = QZXTools.getExternalStorageForFiles(getContext(), null) + File.separator + "discuss/preRecord.txt";
        File file = new File(saveRecordPath);
        if (file.exists()) {
            recordStatuses = (ArrayList<RecordStatus>)
                    SerializeUtil.deSerializeFromFile(file.getAbsolutePath());
            for (RecordStatus recordStatus : recordStatuses) {
                if (recordStatus.getPreviewUrl().equals(preViewUrl)) {
                    //不需要下载
                    //tbs打开
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("local", "true");
                    params.put("allowAutoDestory", "true");
                    JSONObject Object = new JSONObject();
                    try {
                        Object.put("pkgName", getActivity().getApplicationContext().getPackageName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    params.put("menuData", Object.toString());

                    //todo  这里以前用的是 腾讯的x5  现在下载到本地用wps  打开
                   /* QbSdk.getMiniQBVersion(getActivity());
                    int ret = QbSdk.openFileReader(getActivity(), recordStatus.getSavedFilePath(),
                            params, GroupDiscussFragment.this);*/
                    return;
                }
            }
        }

        if (tbsDownloadDialog == null) {
            tbsDownloadDialog = new TBSDownloadDialog();
        }
        tbsDownloadDialog.show(getChildFragmentManager(), TBSDownloadDialog.class.getSimpleName());

        OkHttp3_0Utils.getInstance().downloadSingleFileForOnce(preViewUrl,
                "discuss", new OkHttp3_0Utils.DownloadCallback() {
                    @Override
                    public void downloadProcess(int value) {
                        if (tbsDownloadDialog != null) {
                            tbsDownloadDialog.download(value);
                        }
                    }

                    @Override
                    public void downloadComplete(String filePath) {
                        if (tbsDownloadDialog != null) {
                            tbsDownloadDialog.dismissAllowingStateLoss();
                            tbsDownloadDialog = null;
                        }
                        QZXTools.popToast(getContext(), "下载成功地址为：" + filePath, false);

                        //保存到记录中
                        if (!file.exists()) {
                            try {
                                boolean success = file.createNewFile();
                                if (success) {
                                    RecordStatus recordStatus = new RecordStatus();
                                    recordStatus.setSavedFilePath(filePath);
                                    recordStatus.setPreviewUrl(preViewUrl);
                                    ArrayList<RecordStatus> recordStatuses = new ArrayList<>();
                                    recordStatuses.add(recordStatus);

                                    //序列化到文件中
                                    SerializeUtil.toSerializeToFile(recordStatuses, file.getAbsolutePath());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                QZXTools.logE("createNewFile Failed", null);
                            }
                        } else {
                            RecordStatus recordStatus = new RecordStatus();
                            recordStatus.setSavedFilePath(filePath);
                            recordStatus.setPreviewUrl(preViewUrl);
                            recordStatuses.add(recordStatus);

                            //序列化到文件中
                            SerializeUtil.toSerializeToFile(recordStatuses, file.getAbsolutePath());

                        }

                        //更新主界面的缓存
                        EventBus.getDefault().post("update_cache", Constant.UPDATE_CACHE_VIEW);

                        //tbs打开
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("local", "true");
                        params.put("allowAutoDestory", "true");
                        JSONObject Object = new JSONObject();
                        try {
                            Object.put("pkgName", getActivity().getApplicationContext().getPackageName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.put("menuData", Object.toString());
                        //todo  这里以前用的是 腾讯的x5  现在下载到本地用wps  打开
                     /*   QbSdk.getMiniQBVersion(getActivity());
                        int ret = QbSdk.openFileReader(getActivity(), filePath, params, GroupDiscussFragment.this);*/

                    }

                    @Override
                    public void downloadFailure() {
                        QZXTools.popToast(getContext(), "下载失败", false);
                        if (tbsDownloadDialog != null) {
                            tbsDownloadDialog.dismissAllowingStateLoss();
                            tbsDownloadDialog = null;
                        }
                    }
                });
    }

    public void changeDot() {
        if (discuss_file_dot != null)
            discuss_file_dot.setVisibility(View.GONE);
    }

    @Override
    public void onReceiveValue(String s) {
        QZXTools.logE("discuss receiveValue=" + s, null);
    }

    /**
     * 发送讨论的信息
     *
     * @param data 文本模式传递的就是消息内容 图片模式传递的则是本地图片地址然后上传给web服务端
     * @param type 文本模式0 图片模式1
     */
    StringBuffer stringBuffer=new StringBuffer();
    StringBuffer webStrings=new StringBuffer();
    public void sendDiscussMsg(String data, int type) {

//        //先自己显示
//        discussBeanList.add(discussBean);
//        discussCommunicationRVAdapter.notifyDataSetChanged();
            //去掉所有的空格 目前的规则事根据空格判断的



        if (type == MsgUtils.TYPE_TEXT) {
            String[] strings = data.split("");
            for (String string : strings) {
                if (TextUtils.isEmpty(string) || string.equals(" ")){
                    if (string.equals(" ")){
                        stringBuffer.append("\b");
                        webStrings.append("&nbsp");
                    }
                    continue;
                }
                stringBuffer.append(string);
                webStrings.append(string);
            }
            String originalData = stringBuffer.toString();
            //String originalData = webStrings.toString();
            //DES加密
            String desData = CyptoUtils.encode(Constant.DESKey, originalData);
           // QZXTools.logE("originalData=" + originalData + ";desData=" + originalData, null);
            DiscussBean discussBean = MsgUtils.getDiscussBean(originalData, "", type, discussGroupId, groupIndex);
            discussBean.setDiscussId(discussId);
            SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_DISCUSS, MsgUtils.createDiscuss(discussBean));
            stringBuffer.setLength(0);
            webStrings.setLength(0);
        } else if (type == MsgUtils.TYPE_PICTURE) {
            uploadDiscussPic(data);
        }
    }

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "操作成功",
     * "result": ["http://172.16.4.40:8090/filesystem/liaotian/opted_9e9d45090095f04bac0aae30a09e3f10bef1.jpg",
     * "http://172.16.4.40:8090/filesystem/liaotian/9e9d45090095f04bac0aae30a09e3f10bef1.jpg"],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 上传聊天的图片
     */
    private void uploadDiscussPic(String filePath) {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        //上传修改过的头像
        String url = UrlUtils.BaseUrl + UrlUtils.DiscussImgUpload;
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("userId", UserUtils.getUserId());
        OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "attachement",
                paraMap, new File(filePath), new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        QZXTools.logE("onFailure e=" + e, null);
                        //服务端错误,例如连接、读取、写入超时等
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if (response.isSuccessful()) {
                            String resultJson = response.body().string();
                            QZXTools.logE("resultJson=" + resultJson, null);

                            Gson gson = new Gson();
                            Map<String, Object> results = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                            }.getType());

                            List<String> imgsString = (List<String>) results.get("result");

                            if (imgsString == null || imgsString.size() == 0) {
                                //上传图片 失败
                                mHandler.sendEmptyMessage(Server_Error);
                            } else {
                                DiscussBean discussBean = MsgUtils.getDiscussBean(imgsString.get(1), imgsString.get(0),
                                        MsgUtils.TYPE_PICTURE, discussGroupId, groupIndex);
                                discussBean.setDiscussId(discussId);
                                Message message = mHandler.obtainMessage();
                                message.what = Alter_Photo_Result;
                                message.obj = discussBean;
                                mHandler.sendMessage(message);
                            }
                        } else {
                            mHandler.sendEmptyMessage(Error404);
                        }
                    }
                });
    }

    private int curRetryCount = 0;

    /**
     * {
     * "success": true,
     * "errorCode": "1",
     * "msg": "操作成功",
     * "result": [{
     * "studentid": "0442b5d559f74afeada49f51bdfc229a",
     * "studentName": "张青雪1",
     * "photo": "http://172.16.4.40:8090/filesystem/headImg/66666702496IMG_20190530_095504.jpg",
     * "groupName": "1",
     * "isLeader": 1
     * }, {
     * "studentid": "70b45737e82b4e5d9b8fb0bc3605b7bd",
     * "studentName": "张青雪",
     * "photo": null,
     * "groupName": "1",
     * "isLeader": 0
     * }],
     * "total": 0,
     * "pageNo": 0
     * }
     * <p>
     * 获取分组情况的信息
     */
    private void fetchGroupInfo() {
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getChildFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        curRetryCount++;

//        String url = UrlUtils.BaseUrl + UrlUtils.DiscussGroup;
        String url = UrlUtils.BaseUrl + UrlUtils.DiscussGroupTwo;

        Map<String, String> paraMap = new LinkedHashMap<>();
//        paraMap.put("studentid", UserUtils.getStudentId());
//        paraMap.put("id", discussId);

        paraMap.put("userId", UserUtils.getUserId());
        paraMap.put("discussId", discussId);
        paraMap.put("classId", UserUtils.getClassId());

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                    //服务端错误
                    mHandler.sendEmptyMessage(Server_Error);
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

                            //处理组长设置在第一位，之后按照姓名的字母排序

                            discussMemberList.add(discussListBeanTwo);
                        }

                        Collections.sort(discussMemberList);

                        //头像和学生ID绑定
                        Map<String, String> avatarBindMap = new HashMap<>();
                        DiscussListBeanTwo newBeanTwo = null;
                        DiscussListBeanTwo oldBeanTwo = null;
                        for (DiscussListBeanTwo discussListBeanTwo : discussMemberList) {
                            avatarBindMap.put(discussListBeanTwo.getUserId(), discussListBeanTwo.getPhoto());
                            if (discussListBeanTwo.getGroupLeader().equals(discussListBeanTwo.getUserId())) {
                                //排第一
                                newBeanTwo = discussListBeanTwo;
                                oldBeanTwo = discussListBeanTwo;
                            }
                        }

                        if (oldBeanTwo != null && newBeanTwo != null) {
                            discussMemberList.remove(oldBeanTwo);
                            discussMemberList.add(0, newBeanTwo);
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

    public static final int CODE_SYS_ALBUM = 2;//系统相册RequestCode

    public static final int CODE_SYS_CROP = 3;//系统裁剪RequestCode

    public static final int CODE_SYS_CAMERA = 5;//系统相机RequestCode


    public static final int CODE_CUSTOM_CROP = 7;//自定义裁剪RequestCode

    /**
     * 自定义裁剪界面
     *
     * @param originalUri 源Uri
     * @param savedUri    保存的Uri
     */
    public static void cropPhotoTwo(Activity activity, Uri originalUri, Uri savedUri) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.setDataAndType(originalUri, "image/*");
        intent.putExtra("save_path", savedUri);
        activity.startActivityForResult(intent, CODE_SYS_CROP);
    }

    /**
     * 裁剪
     *
     * @param sourceUri 表示图片源的Uri（拍照或者相册）
     * @param outputUri 表示剪裁保存的Uri
     */
    public static void cropPhoto(Activity activity, Uri sourceUri, Uri outputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //安卓7.0的临时授权，也可以不需要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
        //传入要裁剪的Uri以及类型
        intent.setDataAndType(sourceUri, "image/*");
        //发送裁剪信号，“true”表示启用裁剪
        intent.putExtra("crop", "true");
        //X方向比例
        intent.putExtra("aspectX", 1);
        //Y方向比例
        intent.putExtra("aspectY", 1);
        //裁剪区的宽
        intent.putExtra("outputX", 1000);
        //裁剪区的高
        intent.putExtra("outputY", 600);
        //是否保留比例
        intent.putExtra("scale", true);
        //是否将裁剪数据保留在Bitmap中返回
        intent.putExtra("return-data", false);
        //裁剪数据输出位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        //设置裁剪后的图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //取消人脸识别
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, CODE_SYS_CROP);
    }

    public static Uri cameraUri;

    /**
     * 打开相机拍照
     * /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/PicturesVIDEO_yyyMMdd_HHmmss.mp4
     */
    private void openCamera() {
        if (picPopup != null && picPopup.isShowing()) {
            picPopup.dismiss();
            picPopup = null;
        }

        String fileDir = QZXTools.getExternalStorageForFiles(MyApplication.getInstance(), Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".jpg");
        File cameraFile = new File(fileDir, stringBuilder.toString());
        cameraUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()
                    + ".fileprovider", cameraFile);
        } else {
            cameraUri = Uri.fromFile(cameraFile);
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        //设置拍照保存的路径，需要特别注意的是在onActivityResult中获取的Intent为空
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        getActivity().startActivityForResult(cameraIntent, CODE_SYS_CAMERA);
    }

    /**
     * 打开系统相册
     */
    private void openSysAlbum() {
        if (picPopup != null && picPopup.isShowing()) {
            picPopup.dismiss();
            picPopup = null;
        }

        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        getActivity().startActivityForResult(albumIntent, CODE_SYS_ALBUM);

        //这种选取图片可以参见：https://blog.csdn.net/qq_38228254/article/details/79623618
//        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        albumIntent.setType("image/*");
//        getActivity().startActivityForResult(albumIntent, CODE_SYS_ALBUM);
    }

    @Override
    public void grantPermission() {
        QZXTools.logD("已授权SD读写权限");
        if (isDiscussFile) {
            handlerTBSShow();
        } else {
            if (isCamera) {
                openCamera();
            } else {
                openSysAlbum();
            }
        }
    }

    @Override
    public void denyPermission() {
        QZXTools.logD("未完全授权");
        Toast.makeText(getActivity(), "因为您未授权，所以该操作这暂时不可用", Toast.LENGTH_SHORT).show();
    }


}
