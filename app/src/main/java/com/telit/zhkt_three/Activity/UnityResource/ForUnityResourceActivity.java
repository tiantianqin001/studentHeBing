package com.telit.zhkt_three.Activity.UnityResource;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.SSI.UnityAndroid.IMyAidlInterface;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.RVUnityChapterAdapter;
import com.telit.zhkt_three.Adapter.RVUnityContentGridAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.FlowLayout;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.NoResultDialog;
import com.telit.zhkt_three.Fragment.Dialog.NoSercerDialog;
import com.telit.zhkt_three.JavaBean.Gson.UnityConditionChapterBean;
import com.telit.zhkt_three.JavaBean.Gson.UnityConditionXDSubjectBean;
import com.telit.zhkt_three.JavaBean.Gson.UnityContentBean;
import com.telit.zhkt_three.JavaBean.UnityResource.Subject;
import com.telit.zhkt_three.JavaBean.UnityResource.UnityContent;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.IOException;
import java.util.ArrayList;
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
 * 与Unity端资源对接，主要展示Unity的资源
 */
public class ForUnityResourceActivity extends BaseActivity implements View.OnClickListener,
        RVUnityChapterAdapter.IChapterClick, TextView.OnEditorActionListener,
        CompoundButton.OnCheckedChangeListener, RVUnityContentGridAdapter.UnityClickInterface {

    private Unbinder unbinder;
    @BindView(R.id.img_res_back)
    ImageView img_res_back;
    @BindView(R.id.linear_pull)
    LinearLayout linear_pull;
    @BindView(R.id.tv_xd_subject)
    TextView tv_xd_subject;
    @BindView(R.id.et_search)
    EditText ed_search;
    @BindView(R.id.tv_chapter_none)
    TextView tv_chapter_none;
    @BindView(R.id.recycler_chapter)
    RecyclerView recycler_chapter;
    @BindView(R.id.cb_video)
    CheckBox cb_video;
    @BindView(R.id.cb_vr)
    CheckBox cb_vr;
    @BindView(R.id.cb_ar)
    CheckBox cb_ar;
    @BindView(R.id.recycler_content)
    XRecyclerView recycler_content;

    //-----------无网络或者无资源
    @BindView(R.id.request_retry_layout)
    LinearLayout request_retry_layout;
    @BindView(R.id.request_retry)
    TextView request_retry;
    @BindView(R.id.leak_resource_layout)
    LinearLayout leak_resource_layout;
    @BindView(R.id.leak_net_layout)
    LinearLayout leak_net_layout;
    @BindView(R.id.link_network)
    TextView link_network;
    private static  boolean isShow=false;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operator_Success = 2;
    private static final int Operate_XD_Subject_Success = 3;
    private static final int Operate_Chapter_Success = 4;
    private Handler mHandler = new Handler() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(ForUnityResourceActivity.this, "当前网络不佳....", false);

                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (recycler_content != null) {
                            recycler_content.refreshComplete();
                            recycler_content.loadMoreComplete();
                        }

                        if (leak_resource_layout != null || request_retry_layout != null) {
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popToast(ForUnityResourceActivity.this, "没有相关资源！", false);

                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (recycler_content != null) {
                            recycler_content.refreshComplete();
                            recycler_content.loadMoreComplete();
                        }

                        if (leak_resource_layout != null || request_retry_layout != null) {
                            leak_resource_layout.setVisibility(View.GONE);
                            request_retry_layout.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
                case Operator_Success:
                    //之所以去掉isVisible的判断是因为：会存在不为空但是界面未显示的情况
                    if (isShow){
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }

                        if (recycler_content != null) {
                            recycler_content.refreshComplete();
                            recycler_content.loadMoreComplete();
                        }

                        if (contentList.size() > 0) {
                            request_retry_layout.setVisibility(View.GONE);
                            leak_resource_layout.setVisibility(View.GONE);
                        } else {
                            request_retry_layout.setVisibility(View.GONE);
                            leak_resource_layout.setVisibility(View.VISIBLE);
                        }
                        //通知刷新视图
                        contentGridAdapter.notifyDataSetChanged();
                    }

                    break;
                case Operate_XD_Subject_Success:
                    if (isShow){
                        if (xdAndsubjects != null && xdAndsubjects.size() > 0) {
                            //一开始默认取第一个xd和subject
                            if (curXD == -1 && curSubject == -1) {
                                curXD = xdAndsubjects.get(0).getXd().getXdId();
                                curSubject = xdAndsubjects.get(0).getSubject().get(0).getSubjectId();

                                //填充数据可下拉的视图
                                tv_xd_subject.setText(xdAndsubjects.get(0).getXd().getXdName()
                                        + "/" + xdAndsubjects.get(0).getSubject().get(0).getSubjectName());
                            }
                            //再次请求对应的章节
                            fetchAvailableChapterByXDSubject();
                        }
                    }

                    break;
                case Operate_Chapter_Success:
                    if (isShow){
                        if (chapters != null && chapters.size() > 0) {

                            recycler_chapter.setVisibility(View.VISIBLE);
                            tv_chapter_none.setVisibility(View.GONE);

                            if (TextUtils.isEmpty(curChapter)) {
                                curChapter = chapters.get(0);
                            }
                            //展示章节信息
                            chapterAdapter = new RVUnityChapterAdapter(curChapter, ForUnityResourceActivity.this, chapters);
                            chapterAdapter.setiChapterClick(ForUnityResourceActivity.this);
                            recycler_chapter.setAdapter(chapterAdapter);
                            //再次请求资源内容
                            fetchAvailableResourceContent(true);
                        } else {
                            recycler_chapter.setVisibility(View.GONE);
                            tv_chapter_none.setVisibility(View.VISIBLE);
                        }
                    }

                    break;
            }
        }
    };

    /**
     * AIDL接口
     */
    private IMyAidlInterface myAidlInterface;

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.e("zbv", "onServiceConnected");
            Toast.makeText(ForUnityResourceActivity.this, "服务连接成功", Toast.LENGTH_SHORT).show();
            myAidlInterface = IMyAidlInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("zbv", "onServiceDisconnected");
            Toast.makeText(ForUnityResourceActivity.this, "服务连接失败", Toast.LENGTH_SHORT).show();
            myAidlInterface = null;
        }
    };
    private NoSercerDialog noSercerDialog;

    private void startAIDL() {
        /*
         * android5.0之后，如果servicer不在同一个App的包中，需要设置service所在程序的包名
         * */
        Intent intent = new Intent();
        intent.setAction("com.telit.aidlprovider.ProviderService");
        intent.setPackage("com.SSI.UnityAndroid");
        boolean result = bindService(intent, conn, Service.BIND_AUTO_CREATE);
        QZXTools.logE("result=" + result, null);
    }

    private void terminalAIDL() {
        unbindService(conn);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unity_resource);
        isShow=true;
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        //开启AIDL
        startAIDL();
        unbinder = ButterKnife.bind(this);

        //获取之前保存的学段、学科、章节信息
        SharedPreferences sharedPreferences = getSharedPreferences("unity_resource", MODE_PRIVATE);
        curXD = sharedPreferences.getInt("xd", -1);
        curSubject = sharedPreferences.getInt("subject", -1);
        curChapter = sharedPreferences.getString("chapter", "");

        String xdName = sharedPreferences.getString("xdName", "");
        String subjectName = sharedPreferences.getString("subjectName", "");
        if (!TextUtils.isEmpty(xdName) && !TextUtils.isEmpty(subjectName)) {
            tv_xd_subject.setText(xdName + "/" + subjectName);
        }
        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());
        initData();
    }

    @SuppressLint("MissingPermission")
    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //保存学段、学科
        SharedPreferences sharedPreferences = getSharedPreferences("unity_resource", MODE_PRIVATE);
        if (curXD != -1) {
            sharedPreferences.edit().putInt("xd", curXD).commit();
        }
        if (curSubject != -1) {
            sharedPreferences.edit().putInt("subject", curSubject).commit();
        }
        if (!TextUtils.isEmpty(curChapter)) {
            sharedPreferences.edit().putString("chapter", curChapter).commit();
        }

        //防止内存泄露 ---放置于onDestroy()中
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();

        terminalAIDL();
        isShow=false;
        //关闭刘冉的apk
        try {
            ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            am.killBackgroundProcesses("com.SSI.UnityAndroid");
        }catch (Exception e){
            e.fillInStackTrace();
        }

        super.onDestroy();
    }

    /**
     * 内容页码
     */
    private int curPageNo = 1;

    /**
     * 当前的学段
     */
    private int curXD = -1;

    /**
     * 当前的学科
     */
    private int curSubject = -1;

    /**
     * 当前的章节
     */
    private String curChapter;

    /**
     * 传入的类型字符串：0视频 1VR 2AR 如果是null表示所有资源类型
     */
//    private String curType;
    private List<String> rTypeList;

    /**
     * 模糊查询的内容名称为空或者为空字符串则表示不模糊查询
     */
    private String queryContentName = "";

    //学段及其对应的学科
    private List<UnityConditionXDSubjectBean.xdAndsubject> xdAndsubjects;

    private List<String> chapters;
    private RVUnityChapterAdapter chapterAdapter;

    private List<UnityContent> contentList;
    private RVUnityContentGridAdapter contentGridAdapter;

    /**
     * 初始化数据
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void initData() {
        img_res_back.setOnClickListener(this);
        request_retry.setOnClickListener(this);
        link_network.setOnClickListener(this);

        linear_pull.setOnClickListener(this);

        cb_video.setOnCheckedChangeListener(this);
        cb_vr.setOnCheckedChangeListener(this);
        cb_ar.setOnCheckedChangeListener(this);

        //设置软键盘Action监听
        ed_search.setOnEditorActionListener(this);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recycler_chapter.setLayoutManager(linearLayoutManager);
        recycler_chapter.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recycler_chapter.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent,
                                       @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 7, 0, 7);
            }
        });

        rTypeList = new ArrayList<>();

        //初始化内容集合
        contentList = new ArrayList<>();

        contentGridAdapter = new RVUnityContentGridAdapter(this, contentList);
        contentGridAdapter.setUnityClickInterface(this);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycler_content.setLayoutManager(gridLayoutManager);
        recycler_content.setAdapter(contentGridAdapter);

        recycler_content.setOverScrollMode(View.OVER_SCROLL_NEVER);

        //加载更多回调
        recycler_content.setLoadingListener(new XRecyclerView.LoadingListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onRefresh() {
                curPageNo = 1;
                fetchAvailableResourceContent(true);
            }

            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onLoadMore() {
                curPageNo++;
                fetchAvailableResourceContent(false);
            }
        });



        //获取所有的可用学段和对应的学科
        fetchAvailableXDAndSubject();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_res_back:
                if (noSercerDialog!=null){
                    noSercerDialog.dismissAllowingStateLoss();
                }
                finish();
                break;
            case R.id.request_retry:
                //重新请求所有的请求
                fetchAvailableXDAndSubject();
                break;
            case R.id.link_network:
                QZXTools.enterWifiSetting(ForUnityResourceActivity.this);
                break;
            case R.id.linear_pull:
                popupXdAndSubjectSelectedWindow(v);
                break;
        }
    }

    private PopupWindow xdAndSubjectPopup;

    /**
     * 学段及其对应的学科的弹出框
     */
    private void popupXdAndSubjectSelectedWindow(View v) {
        if (xdAndsubjects == null) {
            return;
        }

        if (xdAndSubjectPopup != null) {
            xdAndSubjectPopup.dismiss();
        }
        View xdAndSubjectView = LayoutInflater.from(this).inflate(R.layout.pull_xd_subject_layout, null);
        LinearLayout popup_linear = xdAndSubjectView.findViewById(R.id.popup_linear);

        for (int i = 0; i < xdAndsubjects.size(); i++) {
            createOneXdAndSubjectLayout(i, xdAndsubjects.get(i), popup_linear);
        }

        xdAndSubjectPopup = new PopupWindow(xdAndSubjectView,
                (int) getResources().getDimensionPixelSize(R.dimen.x400), LinearLayout.LayoutParams.WRAP_CONTENT);
        xdAndSubjectPopup.setBackgroundDrawable(new ColorDrawable());
        //外部触摸使能消失
        xdAndSubjectPopup.setOutsideTouchable(true);

        xdAndSubjectPopup.showAsDropDown(v, getResources().getDimensionPixelSize(R.dimen.x10),
                getResources().getDimensionPixelSize(R.dimen.x10));
    }

//    /**
//     * 选中的学段学科视图
//     */
//    private View selectedView;

    /**
     * 代码创建一个学段及其对应学科的布局
     * 因为也是按照xdAndsubjects顺序塞入的
     *
     * @param xdTag 这个tag仅仅用于指示在xdAndsubjects中的位置
     */
    private void createOneXdAndSubjectLayout(int xdTag, UnityConditionXDSubjectBean.xdAndsubject xdAndsubject, LinearLayout parent) {
        View subView = LayoutInflater.from(this).inflate(R.layout.xd_subject_sub_layout, null);

        TextView flow_title = subView.findViewById(R.id.flow_title);
        FlowLayout flowLayout = subView.findViewById(R.id.flowLayout);

        flow_title.setText(xdAndsubject.getXd().getXdName());

        int tag = xdTag;

        List<Subject> subjects = xdAndsubject.getSubject();

        for (int i = 0; i < subjects.size(); i++) {
            TextView textView = new TextView(this);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            textView.setText(subjects.get(i).getSubjectName());
            if (curXD == xdAndsubject.getXd().getXdId() && curSubject == xdAndsubject.getSubject().get(i).getSubjectId()) {
                textView.setSelected(true);
            }
            //这里拼接的i代表subjects中的位置，统一由xdAndsubjects获取
            textView.setTag(tag + ":" + i);
            textView.setPadding(getResources().getDimensionPixelSize(R.dimen.x10), getResources().getDimensionPixelSize(R.dimen.x10),
                    getResources().getDimensionPixelSize(R.dimen.x10), getResources().getDimensionPixelSize(R.dimen.x10));
            textView.setBackground(getResources().getDrawable(R.drawable.selector_xd_subject_bg));
            textView.setTextColor(R.drawable.selector_text_color);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimensionPixelSize(R.dimen.x36));
            textView.setOnClickListener(new View.OnClickListener() {
                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                @Override
                public void onClick(View v) {
                    String tv_tag = (String) v.getTag();
                    QZXTools.logE("tv_tag=" + tv_tag, null);
                    v.setSelected(true);

                    String[] splits = tv_tag.split(":");

                    int xdIndex = Integer.parseInt(splits[0]);
                    int subjectIndex = Integer.parseInt(splits[1]);

                    curXD = xdAndsubjects.get(xdIndex).getXd().getXdId();
                    curSubject = xdAndsubjects.get(xdIndex).getSubject().get(subjectIndex).getSubjectId();

                    SharedPreferences sharedPreferences = ForUnityResourceActivity.this.
                            getSharedPreferences("unity_resource", MODE_PRIVATE);
                    sharedPreferences.edit().putString("xdName", xdAndsubjects.get(xdIndex).getXd().getXdName())
                            .putString("subjectName", xdAndsubjects.get(xdIndex).getSubject().get(subjectIndex).getSubjectName())
                            .commit();

                    //填充数据可下拉的视图
                    tv_xd_subject.setText(xdAndsubjects.get(xdIndex).getXd().getXdName()
                            + "/" + xdAndsubjects.get(xdIndex).getSubject().get(subjectIndex).getSubjectName());

                    //重置章节
                    curChapter = "";

//                    selectedView = v;

                    fetchAvailableChapterByXDSubject();

                    xdAndSubjectPopup.dismiss();
                }
            });
            flowLayout.addView(textView, lp);
        }

        parent.addView(subView);
    }

    /**
     * 请求学段以及对应的学科信息
     * <p>
     * <p>
     * {
     * "msg": "操作成功",
     * "result": [
     * {
     * "xd": {
     * "xdId": 0,
     * "xdName": "小学"
     * },
     * "subject": [
     * 1,
     * 2,
     * 3
     * ]
     * }
     * ],
     * "code": 0
     * }
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchAvailableXDAndSubject() {
        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);

            if (circleProgressDialogFragment != null) {
                circleProgressDialogFragment.dismissAllowingStateLoss();
                circleProgressDialogFragment = null;
            }

            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (xdAndsubjects != null) {
            xdAndsubjects.clear();
        }

        String url = UrlUtils.BaseUrl + UrlUtils.UnityResourceConditionXDAndSubject;
        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        UnityConditionXDSubjectBean unityConditionXDSubjectBean =
                                gson.fromJson(resultJson, UnityConditionXDSubjectBean.class);
                        if (unityConditionXDSubjectBean.getCode() == 0) {
                            //成功，失败code=1
                            xdAndsubjects = unityConditionXDSubjectBean.getResult();
                            mHandler.sendEmptyMessage(Operate_XD_Subject_Success);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });

    }

    /**
     * 学段学科请求完毕依据条件请求默认的第一个章节
     * <p>
     * {
     * "msg": "操作成功",
     * "result": [
     * "识字"
     * ],
     * "code": 0
     * }
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchAvailableChapterByXDSubject() {
        if (curXD == -1 || curSubject == -1) {
            QZXTools.popCommonToast(this, "出错：学段或者学科为-1", false);
            return;
        }

        if (chapters == null) {
            chapters = new ArrayList<>();
        }

        //清空章节集合
        chapters.clear();

        String url = UrlUtils.BaseUrl + UrlUtils.UnityResourceConditionChapter;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("xd", curXD + "");
        paraMap.put("subject", curSubject + "");

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        UnityConditionChapterBean unityConditionChapterBean = gson.fromJson(resultJson, UnityConditionChapterBean.class);
                        if (unityConditionChapterBean.getCode() == 0) {
                            //成功，失败code=1
                            chapters = unityConditionChapterBean.getResult();
                            mHandler.sendEmptyMessage(Operate_Chapter_Success);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                    }

                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    /**
     * 查询可用的资源内容
     * <p>
     * {
     * "msg": "操作成功",
     * "result": [
     * {
     * "id": 3,
     * "contentName": "细胞的生长",
     * "contentType": 1,
     * "contentSee": 0,
     * "contentImgUrl": "http://172.16.5.158:8080/courses/images/chinese.png"
     * }
     * ],
     * "code": 0,
     * "pageNo": 1,
     * "pageSize": 9
     * }
     *
     * @param isInit 判断是加载更多还是刷新
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void fetchAvailableResourceContent(boolean isInit) {
        if (curXD == -1 || curSubject == -1 || TextUtils.isEmpty(curChapter)) {
            QZXTools.popCommonToast(this, "出错：学段或者学科为-1,章节信息缺失", false);
            return;
        }

        if (!QZXTools.isNetworkAvailable()) {
            leak_net_layout.setVisibility(View.VISIBLE);
            return;
        } else {
            leak_net_layout.setVisibility(View.GONE);
        }

        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }
        circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        //如果是初始化则清空内容集合
        if (isInit) {
            contentList.clear();
            // 解决 Scrapped or attached views may not be recycled
            contentGridAdapter.notifyDataSetChanged();
        }

        String url = UrlUtils.BaseUrl + UrlUtils.UnityResourceContent;

        Map<String, String> paraMap = new LinkedHashMap<>();
        paraMap.put("xd", curXD + "");
        paraMap.put("subject", curSubject + "");
        paraMap.put("chapter", curChapter);
        if (rTypeList != null && rTypeList.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (int i = 0; i < rTypeList.size(); i++) {
                stringBuilder.append(rTypeList.get(i));
                if (i < (rTypeList.size() - 1)) {
                    stringBuilder.append(",");
                }
            }
            paraMap.put("resourceType", stringBuilder.toString().trim());
        }
        if (!TextUtils.isEmpty(queryContentName)) {
            paraMap.put("contentName", queryContentName);
        }
        paraMap.put("pageNo", curPageNo + "");

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //服务端错误
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("resultJson=" + resultJson, null);
                        Gson gson = new Gson();
                        UnityContentBean unityContentBean = gson.fromJson(resultJson, UnityContentBean.class);
                        if (unityContentBean.getCode() == 0) {
                            //成功，失败code=1
                            for (UnityContent unityContent : unityContentBean.getResult()) {
                                contentList.add(unityContent);
                            }
                            mHandler.sendEmptyMessage(Operator_Success);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                        if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                            circleProgressDialogFragment = null;
                        }
                        mHandler.sendEmptyMessage(Error404);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void clickChapterView(String chapter) {
        //查询资源内容
        curChapter = chapter;
        QZXTools.logE("clickChapterView = " + chapter, null);
        fetchAvailableResourceContent(true);
    }

    /**
     * 对软键盘的搜索相应
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_SEARCH) {
            queryContentName = v.getText().toString();
            QZXTools.logE("onEditorAction IME_ACTION_SEARCH = " + queryContentName, null);
            fetchAvailableResourceContent(true);
        }
        //返回false表示后续按照系统的方式执行
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()) {
            case R.id.cb_video:
                QZXTools.logE("checkBox video", null);
                if (isChecked) {
                    rTypeList.add("0");
                } else {
                    rTypeList.remove("0");
                }
                break;
            case R.id.cb_vr:
                QZXTools.logE("checkBox vr", null);
                if (isChecked) {
                    rTypeList.add("1");
                } else {
                    rTypeList.remove("1");
                }
                break;
            case R.id.cb_ar:
                QZXTools.logE("checkBox ar", null);
                if (isChecked) {
                    rTypeList.add("2");
                } else {
                    rTypeList.remove("2");
                }
                break;
        }
        fetchAvailableResourceContent(true);
    }

    /**
     * grid item点击反应，跳转进入unity_app
     */
    @Override
    public void unity_click(String name) {
        try {
            if (myAidlInterface != null) {
                myAidlInterface.fetchResource(name);
            } else {
                Toast.makeText(this, "aidl服务未绑定", Toast.LENGTH_SHORT).show();
            }
        } catch (RemoteException e) {
            QZXTools.logE("exception e=" + e, null);
            e.printStackTrace();
        }
    }
}
