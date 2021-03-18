package com.telit.zhkt_three.Activity.HomeScreen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.OauthMy.ProviceActivity;
import com.telit.zhkt_three.Adapter.HomeViewPagerAdapter;
import com.telit.zhkt_three.Adapter.vp_transformer.CustomPageTransformer;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.FileReceiveDialog;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.Fragment.Dialog.UrlUpdateDialog;
import com.telit.zhkt_three.Fragment.HomeStopOneFragment;
import com.telit.zhkt_three.Fragment.HomeStopTwoFragment;
import com.telit.zhkt_three.Fragment.SysyemFragment;
import com.telit.zhkt_three.Fragment.SysyemFragment1;
import com.telit.zhkt_three.Fragment.SysyemFragment2;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.JavaBean.AppListBean;
import com.telit.zhkt_three.JavaBean.AppUpdate.UpdateBean;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Service.AppInfoService;
import com.telit.zhkt_three.Utils.ApkListInfoUtils;
import com.telit.zhkt_three.Utils.CheckVersionUtil;
import com.telit.zhkt_three.Utils.Jpush.TagAliasOperatorHelper;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.AppInfoDao;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.telit.zhkt_three.receiver.AppChangeReceiver;
import com.telit.zhkt_three.receiver.NetworkChangeBroadcastReceiver;
import com.zbv.basemodel.LingChuangUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;

/**
 * 奇怪现象：设置为SingleInstance,在RVHomeAdapter启动mContext.startActivity(new Intent(mContext, Test2Activity.class));
 * 如果进入onPause后再点击后按返回直接退出了，不再进入MainActivity
 * <p>
 * UserManager,sInstance的泄露没有查清楚
 * <p>
 */
public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private Unbinder unbinder;
    @BindView(R.id.home_avatar)
    CircleImageView home_avatar;
    @BindView(R.id.home_nickname)
    TextView home_nickname;
    @BindView(R.id.home_clazz)
    TextView home_clazz;
    @BindView(R.id.home_time)
    TextView home_time;
    @BindView(R.id.home_date)
    TextView home_date;
    @BindView(R.id.home_weekend)
    TextView home_weekend;
    @BindView(R.id.home_wifi)
    ImageView home_wifi;
    @BindView(R.id.home_timetable)
    ImageView home_timetable;


    @BindView(R.id.home_viewpager)
    ViewPager home_viewpager;
    @BindView(R.id.home_dots_linear)
    LinearLayout home_dot_linear;
    @BindView(R.id.home_IndicatorDotView)
    ImageView home_dotview;

    protected static final String[] weekends = {"星期一", "星期二", "星期三", "星期四", "星期五", "星期六", "星期日"};
    private ScheduledExecutorService scheduledExecutorService;
    private HomeViewPagerAdapter homeViewPagerAdapter;
    private List<AppInfo> datas ;
    private int vp_page_count;
    private int distance_dots;
    private ScheduledExecutorService timeExecutor;
    private static final int FIREST_UPDATE_VP = 0x7;
    private static final int UPDATE_TIME = 0x8;
    /**
     * 是否是初始化Vp
     */
    private boolean isInitVp = true;
    //网络改变广播
    private NetworkChangeBroadcastReceiver netConnectChangedReceiver;
    //因为应用中心只是管控应用中的一个Activity，所以当从包名集合中轮询到此字符串后，特殊处理一下
    private static final String HAT_APP_MARKET_CLASS = "com.example.edcationcloud.edcationcloud.activity.DownloadCenterActivity";

    //App安装和卸载
    private AppChangeReceiver appChangeReceiver;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Update_App_Dialog = 0;
    private static final int Close_Ling_Chuang_brocast = 0x98;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Close_Ling_Chuang_brocast:
                    if (executorLingChuangService!=null){
                        executorLingChuangService.shutdownNow();
                    }
                    break;
                case FIREST_UPDATE_VP:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    initVpAdapter();
                    ApkListInfoUtils.getInstance().onStop();
                    break;
                case UPDATE_TIME:
                    Bundle bundle = (Bundle) msg.obj;
                    home_weekend.setText(bundle.getString("weekend"));
                    home_time.setText(bundle.getString("time"));
                    int month = bundle.getInt("month");
                    int day = bundle.getInt("day");
                    String date = month + "月" + day + "日";
                    home_date.setText(date);
                    break;
                case Update_App_Dialog:
                    //有新版本
                    UpdateBean updateBean = (UpdateBean) msg.obj;
                    int currentCode = msg.arg1;
                    TipsDialog tipsDialog = new TipsDialog();
                    tipsDialog.setTipsStyle("有新版本更新！\n最新版本：" + updateBean.getVersionName() +
                                    "\n当前版本：" + QZXTools.getVerionName(MainActivity.this) + "\n版本描述：" + updateBean.getDescription(),
                            "忽略更新", "立即更新", -1);
                    tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                        @Override
                        public void cancle() {
                            tipsDialog.dismissAllowingStateLoss();
                        }

                        @Override
                        public void confirm() {
                            tipsDialog.dismissAllowingStateLoss();
                            downloadNewApp(updateBean.getUpdateUrl());
                        }
                    });
                    tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                    break;
            }
        }
    };
    private ExecutorService executorLingChuangService;
    private LingChuangAppsListReceiver lingChuangAppsListReceiver;

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        QZXTools.logE("MainActivity onNewIntent", null);
    }

    private static boolean isUpData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        QZXTools.logDeviceInfo(this);
        QZXTools.logE("MainActivity onCreate " + getTaskId(), null);
        isUpData = true;
        //更新Url地址
        updateUrl();



        //   LingChuangUtils.getInstance().startList(MyApplication.getInstance());
        //初始化领创的广播信息
        initLingChuangInfo();
        //判断是否已经登录
        if (!UserUtils.isLoginIn()) {
           // startActivity(new Intent(this, LoginActivity.class));
            startActivity(new Intent(this, ProviceActivity.class));
            finish();
            return;
        }

        unbinder = ButterKnife.bind(this);

        //检测系统版本
        if (isUpData) {
            CheckVersionUtil.getInstance().requestCheckVersion(this);
            isUpData = false;
        }

        //-----------------------------------------------------开启网络改变广播监听
        IntentFilter filter = new IntentFilter();
        //监听wifi连接（手机与路由器之间的连接）
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        //监听互联网连通性（也就是是否已经可以上网了），当然只是指wifi网络的范畴
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        //这个是监听网络状态的，包括了wifi和移动网络。
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        netConnectChangedReceiver = new NetworkChangeBroadcastReceiver();
        registerReceiver(netConnectChangedReceiver, filter);
        //-----------------------------------------------------开启网络改变广播监听
        //-----------------------------------------------------App安装和卸载、更改
        //Andoird8.0+需要广播动态注册
        IntentFilter appFilter = new IntentFilter();
        appFilter.addAction("android.intent.action.PACKAGE_ADDED");
        appFilter.addAction("android.intent.action.PACKAGE_REPLACED");
        appFilter.addAction("android.intent.action.PACKAGE_REMOVED");
        appFilter.addDataScheme("package");
        appChangeReceiver = new AppChangeReceiver();
        registerReceiver(appChangeReceiver, appFilter);
        //-----------------------------------------------------App安装和卸载
        //开启AppService
        //如果安卓o,api26（8.0）则需要使用 AppInfoService.enqueueWork
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            AppInfoService.enqueueWork(this, new Intent(this, AppInfoService.class));
        } else {
            startService(new Intent(this, AppInfoService.class));
        }

        //注册EventBus
        EventBus.getDefault().register(this);
        //再领创的管控平台下禁用禁用 recent 键
        // LingChuangUtils.getInstance().stopRecent(MyApplication.getInstance());

        StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
        StudentInfo studentInfo = studentInfoDao.queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
        if (studentInfo != null) {
            home_nickname.setText(studentInfo.getStudentName());
            if (studentInfo.getClassName() != null) {
                if (studentInfo.getGradeName() != null) {
                    home_clazz.setText(studentInfo.getGradeName().concat(studentInfo.getClassName()));
                } else {
                    home_clazz.setText(studentInfo.getClassName());
                }
            }
            if (studentInfo.getPhoto() == null) {
                home_avatar.setImageResource(R.mipmap.icon_user);
            } else {
                Glide.with(this).load(studentInfo.getPhoto()).
                        placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(home_avatar);
            }
        }
        //查询GreenDao数据库看是否存在保存的本地数据
        AppInfoDao appInfoDao = MyApplication.getInstance().getDaoSession().getAppInfoDao();
        //排序升序
        datas = appInfoDao.queryBuilder().orderAsc(AppInfoDao.Properties.OrderNum).list();
        if (datas == null || datas.size() <= 0) {
             datas= new ArrayList<>();
            if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                circleProgressDialogFragment.dismissAllowingStateLoss();
                circleProgressDialogFragment = null;
            }
            circleProgressDialogFragment = new CircleProgressDialogFragment();
            circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

            //获取列表
            ApkListInfoUtils.getInstance().onStart();
             ApkListInfoUtils.getInstance().getAppSystem("lingchang", appsLists);
            //datas = ApkListInfoUtils.getInstance().getAppSystem("lingchang", appsLists);



        } else {
            initVpAdapter();
        }
        home_viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) home_dotview.getLayoutParams();
                layoutParams.leftMargin = (int) (distance_dots * (positionOffset + position)
                        + QZXTools.dp2px(MainActivity.this, 5));
                home_dotview.setLayoutParams(layoutParams);
            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });
        timeExecutor = Executors.newSingleThreadScheduledExecutor();
        timeExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
//                QZXTools.logE("fixedReate", null);
                //获取星期和时间 注意：如果因为某次意外可能导致定时线程终止，例如在子线程更新UI
                fillWeekAndTime();
            }
        }, 0, 30000, TimeUnit.MILLISECONDS);

        home_avatar.setOnClickListener(this);
        home_wifi.setOnClickListener(this);
        home_timetable.setOnClickListener(this);

        //一秒内三连击进入修改Url界面
        home_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                count++;
                long curTime = System.currentTimeMillis();
                if (count == 1) {
                    touchFirstTime = curTime;
                }
                if (count == 3 && curTime - touchFirstTime <= 1000) {
                    count = 0;
                    //进入修改服务和通讯IP界面
                    UrlUpdateDialog urlUpdateDialog = new UrlUpdateDialog();
                    urlUpdateDialog.show(getSupportFragmentManager(), UrlUpdateDialog.class.getSimpleName());
                } else if (curTime - touchFirstTime > 1000) {
                    //重置
                    count = 0;
                } else if (count > 3) {
                    //重置
                    count = 0;
                }
            }
        });
    }

    private long touchFirstTime;
    private int count;

    @Override
    protected void onResume() {
        super.onResume();

        //判断是否已经登录
        if (!UserUtils.isLoginIn()) {
            startActivity(new Intent(this, ProviceActivity.class));
            finish();
            return;
        }

        if (JPushInterface.isPushStopped(MyApplication.getInstance())) {
            QZXTools.logE("JPush resume", null);
            JPushInterface.resumePush(MyApplication.getInstance());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        QZXTools.logE("MainActivity onDestroy " + getTaskId(), null);

        EventBus.getDefault().unregister(this);
        if (unbinder != null) {
            unbinder.unbind();
        }


        if (timeExecutor != null) {
            timeExecutor.shutdown();
            timeExecutor = null;
        }

        //解除动态广播
        if (netConnectChangedReceiver != null) {
            unregisterReceiver(netConnectChangedReceiver);
        }


        //解除App安装和卸载广播
        if (appChangeReceiver != null) {
            unregisterReceiver(appChangeReceiver);
        }
        TagAliasOperatorHelper.getInstance().releaseHandler();

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.dismissAllowingStateLoss();
            circleProgressDialogFragment = null;
        }

        mHandler.removeCallbacksAndMessages(null);
    }
    /**
     * 更新Url
     */
    private void updateUrl() {
        String path = QZXTools.getExternalStorageForFiles(this, null) + "/config.txt";
        Properties properties = QZXTools.getConfigProperties(path);
        QZXTools.logE("rootIp=" + properties.getProperty("rootIp"), null);
        String rootIp = properties.getProperty("rootIp");
        String socketIp = properties.getProperty("socketIp");
        String socketPort = properties.getProperty("socketPort");
        String imgIp = properties.getProperty("imgIp");
        String pointIp = properties.getProperty("pointIp");
        if (!TextUtils.isEmpty(rootIp)) {
            UrlUtils.BaseUrl = rootIp;
        } else {
            //一开始没有设置属性配置
            properties.setProperty("rootIp", UrlUtils.BaseUrl);
            properties.setProperty("socketIp", UrlUtils.SocketIp);
            properties.setProperty("socketPort", UrlUtils.SocketPort + "");
            properties.setProperty("imgIp", UrlUtils.ImgBaseUrl);
            properties.setProperty("pointIp", UrlUtils.MaiDianUrl);

            try {
                FileOutputStream fos = new FileOutputStream(path);
                properties.store(new OutputStreamWriter(fos, "UTF-8"),
                        "Config");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (!TextUtils.isEmpty(socketIp)) {
            UrlUtils.SocketIp = socketIp;
        }

        if (!TextUtils.isEmpty(socketPort)) {
            UrlUtils.SocketPort = Integer.parseInt(socketPort);
        }

        if (!TextUtils.isEmpty(imgIp)) {
            UrlUtils.ImgBaseUrl = imgIp;
        }

        if (!TextUtils.isEmpty(pointIp)) {
            UrlUtils.MaiDianUrl = pointIp;
        }
    }

    /**
     * 添加订阅者  必须搞个服务，不然进入后台就接收不到讯息了,所以采用下面onAppInt(int type)方法
     */
    @Subscriber(tag = Constant.EVENT_TAG_APP, mode = ThreadMode.MAIN)
    public void onAppInt(int type) {
        switch (type) {
            case Constant.APP_NEW_ADD:
                AppInfoDao appInfoDao_add = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                homeViewPagerAdapter.setmList(appInfoDao_add.queryBuilder().orderAsc(AppInfoDao.Properties.OrderNum).list());
                homeViewPagerAdapter.setNeedChange(true);
                homeViewPagerAdapter.notifyDataSetChanged();
                homeViewPagerAdapter.setNeedChange(false);
                break;
            case Constant.APP_UPDATE:
                AppInfoDao appInfoDao_update = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                homeViewPagerAdapter.setmList(appInfoDao_update.queryBuilder().orderAsc(AppInfoDao.Properties.OrderNum).list());
                homeViewPagerAdapter.setNeedChange(true);
                homeViewPagerAdapter.notifyDataSetChanged();
                homeViewPagerAdapter.setNeedChange(false);
                break;
            case Constant.APP_DELETE:
                AppInfoDao appInfoDao_del = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                homeViewPagerAdapter.setmList(appInfoDao_del.queryBuilder().orderAsc(AppInfoDao.Properties.OrderNum).list());
                homeViewPagerAdapter.setNeedChange(true);
                homeViewPagerAdapter.notifyDataSetChanged();
                homeViewPagerAdapter.setNeedChange(false);
                break;
        }
    }

    @Subscriber(tag = "delete_app", mode = ThreadMode.MAIN)
    public void deleteApp(String packageName) {
        /**
         * 因为执行删除代码就包含系统的删除视图，这个可以省略
         * */
        QZXTools.uninstallApk(this, packageName);
    }

    private List<Fragment> fragments = new ArrayList<>();

    private void initVpAdapter() {
        Log.i(TAG, "initVpAdapter:2222 " + distance_dots);
        int systemCom = 0;
        fragments.clear();
        //添加切换效果
        home_viewpager.setPageTransformer(false, new CustomPageTransformer());


        HomeStopOneFragment homeStopOneFragment = new HomeStopOneFragment();
        HomeStopTwoFragment homeStopTwoFragment = new HomeStopTwoFragment();
        fragments.add(homeStopOneFragment);
        fragments.add(homeStopTwoFragment);
        //创建系统的fragment
        if (datas.size() > 0 && datas.size()>=12) {
            if (datas.size() % 12 == 0) {
                systemCom = datas.size() / 12;
            } else {
                systemCom = datas.size() / 12;
                systemCom++;
            }
        }

        AppListBean appListBean = new AppListBean();
        appListBean.setDatas(datas);
        if (systemCom == 1) {
            SysyemFragment sysyemFragment = new SysyemFragment();
            fragments.add(sysyemFragment);

            Bundle bundle = new Bundle();
            bundle.putSerializable("applist", appListBean);
            sysyemFragment.setArguments(bundle);

        } else if (systemCom == 2) {
            SysyemFragment sysyemFragment = new SysyemFragment();
            SysyemFragment1 sysyemFragment1 = new SysyemFragment1();
            fragments.add(sysyemFragment);
            fragments.add(sysyemFragment1);

            Bundle bundle = new Bundle();
            bundle.putSerializable("applist", appListBean);
            sysyemFragment.setArguments(bundle);
            sysyemFragment1.setArguments(bundle);

        } else if (systemCom == 3) {
            SysyemFragment sysyemFragment = new SysyemFragment();
            SysyemFragment1 sysyemFragment1 = new SysyemFragment1();
            SysyemFragment2 sysyemFragment2 = new SysyemFragment2();
            fragments.add(sysyemFragment);
            fragments.add(sysyemFragment1);
            fragments.add(sysyemFragment2);

            Bundle bundle = new Bundle();
            bundle.putSerializable("applist", appListBean);
            sysyemFragment.setArguments(bundle);
            sysyemFragment1.setArguments(bundle);
            sysyemFragment2.setArguments(bundle);
        }
        homeViewPagerAdapter = new HomeViewPagerAdapter(getSupportFragmentManager(), fragments);
        vp_page_count = homeViewPagerAdapter.totalPage();

        Log.i("qin", "initVpAdapter: " + vp_page_count);

        home_viewpager.setAdapter(homeViewPagerAdapter);
        //只有一页即首页的话就不显示原点了
        if (vp_page_count > 0) {
            home_dotview.setVisibility(View.VISIBLE);
            home_dot_linear.removeAllViews();
            for (int i = 0; i < vp_page_count; i++) {
                addDot(false);
            }
            //在onMeasure onLayout后收到监听回调之后onDraw
            home_dotview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    //移除布局改变监听
                    home_dotview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    distance_dots = home_dot_linear.getChildAt(1).getLeft() - home_dot_linear.getChildAt(0).getLeft();

                }
            });
        } else {
            home_dotview.setVisibility(View.GONE);
        }
    }
    /**
     * 填充时间和星期
     */
    private void fillWeekAndTime() {
        Date date = new Date();
        int week = QZXTools.judgeWeekFromDate(date);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");
        String time = simpleDateFormat.format(date);

        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        Message message = mHandler.obtainMessage();
        message.what = UPDATE_TIME;
        Bundle bundle = new Bundle();
        bundle.putString("weekend", weekends[week - 1]);
        bundle.putInt("month", month);
        bundle.putInt("day", day);
        bundle.putString("time", time);
        message.obj = bundle;
        mHandler.sendMessage(message);
    }

    /**
     * 动态添加dot
     */
    private void addDot(boolean isEnable) {
        ImageView imageView = new ImageView(this);
        imageView.setImageResource(R.drawable.shape_enable_dot);
        imageView.setPadding(QZXTools.dp2px(this, 5),
                QZXTools.dp2px(this, 5),
                QZXTools.dp2px(this, 5),
                QZXTools.dp2px(this, 5));
        home_dot_linear.addView(imageView);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.home_avatar:
                startActivity(new Intent(this, PersonInfoActivity.class));
                overridePendingTransition(R.anim.activity_enter_from_left_to_right, R.anim.out_fade);
                break;
            case R.id.home_wifi:
                QZXTools.enterWifiSetting(this);
                break;
            case R.id.home_timetable:
                startActivity(new Intent(this, TimeTableActivity.class));
                break;
        }
    }

    //----------------------------------------------AppUpdate-------------------------------------

    private String installFilePath;

    @Subscriber(tag = Constant.CAN_INSTALL, mode = ThreadMode.MAIN)
    public void getInstallPath(String path) {
        installFilePath = path;
    }

    /**
     * 申请权限回调
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constant.INSTALL_PACKAGES_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    QZXTools.logE("onRequestPermissionsResult 获取到安装权限", null);
                    QZXTools.installApk(this, installFilePath);

                } else {
                    QZXTools.logE("onRequestPermissionsResult 引导用户手动开启安装权限", null);
                    //  引导用户手动开启安装权限
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    startActivityForResult(intent, Constant.GET_UNKNOWN_APP_SOURCES);
                }
                break;
            default:
                break;

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constant.GET_UNKNOWN_APP_SOURCES) {
            QZXTools.logE("onActivityResult GET_UNKNOWN_APP_SOURCES", null);
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = getPackageManager().canRequestPackageInstalls();
                if (b) {
                    QZXTools.installApk(this, installFilePath);
                } else {
                    QZXTools.popToast(this, "您没有授权，更新失败", false);
                }
            }
        }
    }

    /**
     * 下载安装新版App
     */
    private void downloadNewApp(String downloadUrl) {
        FileReceiveDialog fileReceiveDialog = new FileReceiveDialog();
        fileReceiveDialog.setFileBodyString(true, downloadUrl, null);
        fileReceiveDialog.show(getSupportFragmentManager(), FileReceiveDialog.class.getSimpleName());
    }

    //------------------------------------------------双击退出

    /**
     * todo 如果处于更新apk中如何处理，肯定不能退出apk
     */
    @Override
    public void onBackPressed() {
    }

    /**
     * 在个人空间更新头像后，主界面也要更新
     */
    @Subscriber(tag = Constant.Update_Avatar, mode = ThreadMode.MAIN)
    public void updateAvatar(String urlImg) {
        Glide.with(this).load(urlImg).
                placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(home_avatar);
    }


    private void initLingChuangInfo() {
        //禁用home 件
        LingChuangUtils.getInstance().stopHome(MyApplication.getInstance());
        // 启用 recent 键(
        LingChuangUtils.getInstance().startRecent(MyApplication.getInstance());
        //启动back 键
        LingChuangUtils.getInstance().startBack(MyApplication.getInstance());
        // LingChuangUtils.getInstance().stopDisablenavigationbar(MyApplication.getInstance());
        //获取第三方app 的图标
      /*  lingChuangAppsListReceiver = new LingChuangAppsListReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.launcher3.mdm.hide_show_apps");
        registerReceiver(lingChuangAppsListReceiver, filter);*/
        //获取系统app 图标
        /*LingChuangSystemReceiver lingChuangSystemReceiver=new LingChuangSystemReceiver();
        IntentFilter intentFilterSystem=new IntentFilter();
        intentFilterSystem.addAction("com.android.launcher3.mdm.control_default_apps");
        registerReceiver(lingChuangSystemReceiver,intentFilterSystem);*/

        //默认让home 件触发10次
        executorLingChuangService = ApkListInfoUtils.getInstance().onStart();
        Log.i(TAG, "initLingChuangInfo: "+executorLingChuangService);
        executorLingChuangService.execute(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i <= 15; i++) {
                    try {
                        Thread.sleep(2000);
                        //领创要发home的广播
                        lingChang();

                        if (i==15){
                            Message message = Message.obtain();
                            message.what=Close_Ling_Chuang_brocast;
                            mHandler.sendMessage(message);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }


            }
        });


    }

    private List<String> appsLists = new ArrayList<>();

    private class LingChuangAppsListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            appsLists.clear();
            //这个必须点击10次   也就是调10次home键才能收到广播  这个是获取第三方app 的应用
            List<String> hide_show_apps = intent.getStringArrayListExtra("hide_show_apps");

            Log.i("qin0520", "onReceive: " + hide_show_apps);
            if (hide_show_apps != null) {
                for (String app : hide_show_apps) {
                    appsLists.add(app);
                }
            }
            initlingchaungList(appsLists);
        }


    }

    private List<String> appsSystems = new ArrayList<>();

    private class LingChuangSystemReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            appsSystems.clear();
            List<Map<String, Integer>> systemData = (List<Map<String, Integer>>) intent.getSerializableExtra("apps_status_list");
            Log.i("qin", "onReceive: " + systemData);
        }
    }

    private void initlingchaungList(List<String> appsListss) {
        ApkListInfoUtils.getInstance().onStart();
        if (!datas.isEmpty() && datas.size() > 0) {
            datas.clear();
        }
        appsListss.add("com.ndwill.swd.appstore");
        ApkListInfoUtils.getInstance().getAppSystem("lingchang", appsListss);
    }

    @Subscriber(tag = Constant.LINGCHUANG_APP_LIST, mode = ThreadMode.MAIN)
    public void lingChuangList(List<AppInfo> appInfoList) {
        Log.i("qin", "lingChuangList: " + appInfoList);
        if (homeViewPagerAdapter != null) {
            homeViewPagerAdapter.notifyDataSetChanged();
        }else {
            datas.clear();
            datas.addAll(appInfoList);
           mHandler.sendEmptyMessage(FIREST_UPDATE_VP);
           // initVpAdapter();
        }

    }
    private void lingChang() {
        Intent intent = new Intent("com.linspirer.edu.homeaction");
        intent.setPackage("com.android.launcher3");
        sendBroadcast(intent);
    }
}
