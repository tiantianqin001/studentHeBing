package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.Settings;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.xtoast.OnClickListener;
import com.hjq.xtoast.XToast;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.interactive.RVSelectClazzAdapter;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.CustomView.EmojiEditText;
import com.telit.zhkt_three.CustomView.RippleBackground;
import com.telit.zhkt_three.JavaBean.Gson.IpPortBean;
import com.telit.zhkt_three.JavaBean.InterActive.ServerIpInfo;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Service.SockUserServer;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.tencent.mars.comm.NetStatusUtil;
import com.zbv.meeting.util.SharedPreferenceUtil;

import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CountDownLatch;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * 通过SSDP得到的Location如：http:172.16.5.158:7777/三年级一班
 */
public class SelectClassActivity extends BaseActivity {

    private Unbinder unbinder;

    @BindView(R.id.board_wifi)
    ImageView board_wifi;

    @BindView(R.id.tv_classCode)
    TextView tv_classCode;

    @BindView(R.id.clazz_recycler)
    RecyclerView recyclerView;

    //-----------无资源
    @BindView(R.id.leak_resource_layout)
    LinearLayout leak_resource_layout;

    @BindView(R.id.head_name)
    TextView home_nickname;
    @BindView(R.id.head_clazz)
    TextView home_clazz;

    @BindView(R.id.tv_address_ip)
    TextView tv_address_ip;

    @BindView(R.id.tv_wifi_name)
    TextView tv_wifi_name;
    @BindView(R.id.head_avatar)
    CircleImageView home_avatar;

    //切换网络      tv_wifi_shouse  tv_wifi_name1
    @BindView(R.id.tv_wifi_shouse)
    TextView tv_wifi_shouse;

    @BindView(R.id.tv_wifi_name1)
    TextView tv_wifi_name1;

    @BindView(R.id.leak_resource)
    ImageView leak_resource;
    private RVSelectClazzAdapter selectClazzAdapter;
    public static final String TAG = "SelectClassActivity";
    private static boolean isShow = false;
    private static final int Server_Error = 0;
    private static final int Operator_Success_Two = 5;
    private static final int Operator_Err = 4;
    private static boolean is_join_Multicast = true;


    //存当前数据的集合
    ConcurrentHashMap<String, ServerIpInfo> currentServerInfos = new ConcurrentHashMap<>();
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Operator_Success_Two:
                    if (isShow) {
                        valueServerIpInfos.clear();
                        ConcurrentHashMap<String, ServerIpInfo> currentServerInfos = (ConcurrentHashMap<String, ServerIpInfo>) msg.obj;
                        if (currentServerInfos == null) {
                            if (leak_resource_layout != null && recyclerView != null) {
                                Log.i(TAG, "run: wwwwwServerIp我是刷新数据ccccccc");
                                leak_resource_layout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                leak_resource.setVisibility(View.VISIBLE);
                            }
                            return;
                        }
                        Log.i(TAG, "handleMessage123: " + Operator_Success_Two);
                        //遍历map
                        for (Map.Entry<String, ServerIpInfo> entry : currentServerInfos.entrySet()) {
                            valueServerIpInfos.add(entry.getValue());
                        }


                        if (valueServerIpInfos.size() <= 0) {
                            if (leak_resource_layout != null && recyclerView != null) {

                                leak_resource_layout.setVisibility(View.VISIBLE);
                                recyclerView.setVisibility(View.GONE);
                                leak_resource.setVisibility(View.VISIBLE);
                            }
                        } else {
                            if (leak_resource_layout != null) {
                                leak_resource_layout.setVisibility(View.GONE);
                                leak_resource.setVisibility(View.GONE);
                            }
                            if (recyclerView != null) {
                                recyclerView.setVisibility(View.VISIBLE);
                                selectClazzAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                    break;
                case Operator_Err:
                    if (leak_resource_layout != null) {
                        leak_resource_layout.setVisibility(View.VISIBLE);
                    }
                    break;
                case Server_Error:
                    QZXTools.popToast(SelectClassActivity.this, "加入班级失败", false);
                    break;
            }
        }
    };
    private volatile ConcurrentHashMap<String, ServerIpInfo> ServerIpInfos1 = new ConcurrentHashMap<>();
    private volatile ConcurrentHashMap<String, ServerIpInfo> ServerIpInfos2 = new ConcurrentHashMap<>();

    private CopyOnWriteArrayList<ServerIpInfo> valueServerIpInfos = new CopyOnWriteArrayList<>();

    //private static final String Multicast_IP = "224.5.6.7";

    private Timer timerTask;
    private RippleBackground rippleBackground;
    private CountDownLatch endLatch;
    private static final int REQUEST_OVERLAY = 4444;
    private MyServerConn myServerConn;

    private String wifiName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_class);
        //本机IP
        isShow = true;
        is_join_Multicast = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //获取悬乎的权限
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, REQUEST_OVERLAY);
            } else {

            }
        }

        //  QZXTools.popCommonToast(this, "本机Ip = " + ownIP, false);

        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();

        EventBus.getDefault().register(this);

        //绑定服务
        Intent service = new Intent(this, SockUserServer.class);
        // startService(service);
        myServerConn = new MyServerConn();
        bindService(service, myServerConn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();

        unbinder = ButterKnife.bind(this);
        wifiName = getWIFIName(this);
        String ownIP = QZXTools.getIPAddress();
        QZXTools.logE("本机IP = " + ownIP, null);
        //获取设备名称
        if (!TextUtils.isEmpty(wifiName)) {
            tv_wifi_name.setText(wifiName);
            tv_wifi_name1.setText(wifiName);
        }
        tv_address_ip.setText("我的课堂 " + ownIP);

        tv_wifi_shouse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QZXTools.enterWifiSetting(SelectClassActivity.this);
            }
        });
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

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        if (selectClazzAdapter == null) {
            selectClazzAdapter = new RVSelectClazzAdapter(SelectClassActivity.this, valueServerIpInfos);
        }
        recyclerView.setAdapter(selectClazzAdapter);
        if (recyclerView != null) {
            recyclerView.setVisibility(View.GONE);
        }
        if (leak_resource_layout != null) {
            leak_resource_layout.setVisibility(View.GONE);
            leak_resource.setVisibility(View.GONE);
        }
        rippleBackground = (RippleBackground) findViewById(R.id.content);
        rippleBackground.startRippleAnimation();
        is_join_Multicast = true;
        isShow = true;
        //上一次集合的数据
        ServerIpInfos2.clear();


        //5s后把数据给清除了 获取新的数据    主要就是5s 比对一下两个集合是不是一样
        timerTask = new Timer();
        timerTask.schedule(new TimerTask() {
            @Override
            public void run() {
                //当前的集合
                recycleInfoView();
            }
        }, 3000, 6000);

        if (!TextUtils.isEmpty(wifiName)) {
            if (tv_wifi_name != null && tv_wifi_name1 != null) {
                tv_wifi_name.setText(wifiName);
                tv_wifi_name1.setText(wifiName);
            }
        }
        if (!TextUtils.isEmpty(ownIP)) {
            if (tv_address_ip != null) {

                tv_address_ip.setText("我的课堂 " + ownIP);
            }
        }
    }

    private synchronized void recycleInfoView() {

        currentServerInfos.clear();
        currentServerInfos.putAll(ServerIpInfos1);


            Iterator<Map.Entry<String, ServerIpInfo>> iterator = ServerIpInfos1.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                String classId = (String) entry.getKey();
                if (!ServerIpInfos2.containsKey(classId)) {
                     ServerIpInfos1.clear();

                    QZXTools.logD(TAG+ "recycleInfoView: ServerIpInfos2"+ServerIpInfos2.size() +".....ServerIpInfos1"+ServerIpInfos1.size());
                }
            }

        if (!isMapEaquse(currentServerInfos, ServerIpInfos2) && currentServerInfos.size() >= 1) {
            if (mHandler != null) {
                Message message = Message.obtain();
                message.obj = currentServerInfos;
                message.what = Operator_Success_Two;
                mHandler.sendMessage(message);
            }
        } else {
            //这里主要是可能教师端关闭了
            if (mHandler != null && currentServerInfos.size() == 0) {
                Message message = Message.obtain();
                message.obj = null;
                message.what = Operator_Success_Two;
                mHandler.sendMessage(message);
            }
        }
        //上一次的集合
            ServerIpInfos2.clear();
            ServerIpInfos2.putAll(currentServerInfos);


    }

    //失去焦点
    @Override
    protected void onPause() {
        super.onPause();
        is_join_Multicast = false;
        isShow = false;

        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }

        if (rippleBackground != null) {
            rippleBackground.stopRippleAnimation();
            rippleBackground = null;
        }
        if (unbinder != null) {
            unbinder.unbind();
        }

    }

    @Override
    protected void onDestroy() {
        isShow = false;


        if (timerTask != null) {
            timerTask.cancel();
            timerTask = null;
        }
        mHandler.removeCallbacksAndMessages(null);
        mHandler = null;
        QZXTools.setmToastNull();

        synchronized (SelectClassActivity.class) {
            ServerIpInfos1.clear();
        }
        ServerIpInfos2.clear();

        if (rippleBackground != null) {
            rippleBackground.stopRippleAnimation();
            rippleBackground = null;
        }
        EventBus.getDefault().unregister(this);

        if (isServiceRunning("com.telit.zhkt_three.Service.SockUserServer",this)){
            unbindService(myServerConn);
        }

//初始化状态
       // unbindService(myServerConn);

        super.onDestroy();

    }
    /**
     * 判断服务是否正在运行
     *
     * @param serviceName 服务类的全路径名称 例如： com.jaychan.demo.service.PushService
     * @param context 上下文对象
     * @return
     */
    public  boolean isServiceRunning(String serviceName, Context context) {
        //活动管理器
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> runningServices = am.getRunningServices(100); //获取运行的服务,参数表示最多返回的数量

        for (ActivityManager.RunningServiceInfo runningServiceInfo : runningServices) {
            String className = runningServiceInfo.service.getClassName();
            if (className.equals(serviceName)) {
                return true; //判断服务是否运行
            }
        }
        return false;
    }
    @Override
    protected void onStop() {
        super.onStop();
        isShow = false;
        if (leak_resource_layout != null) {
            leak_resource_layout.setVisibility(View.GONE);
            leak_resource.setVisibility(View.GONE);
        }
    }

    /**
     * 获取当前连接的wifi名称
     *
     * @param context
     * @return
     */
    public static String getWIFIName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        return wifiId;
    }

    //判断两个map  是不是一样
    private static boolean isMapEaquse(ConcurrentHashMap<String, ServerIpInfo> currentServerInfos,
                                       ConcurrentHashMap<String, ServerIpInfo> serverIpInfos2) {
        if (currentServerInfos.size() != serverIpInfos2.size()) return false;
        Iterator<String> iterator = currentServerInfos.keySet().iterator();

        if (iterator.hasNext()) {
            String next = iterator.next();
            Log.i(TAG, "isMapEaquse: " + next);
            if (serverIpInfos2.containsKey(next)) {
                return true;
            }
        }
        return false;
    }

    class MyServerConn implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            SockUserServer.MyBinder myBinder = (SockUserServer.MyBinder) service;
            myBinder.getService().setDataCallback(new SockUserServer.DataCallback() {
                @Override
                public void dataChanged(String message) {
                    if (!TextUtils.isEmpty(message)) {
                        try {
                            JSONObject jsonObject = new JSONObject(message);
                            String className = jsonObject.optString("className");
                            String ip = jsonObject.optString("address");
                            String port = jsonObject.optString("port");
                            String teacherId = jsonObject.optString("teacherId");
                            String classId = jsonObject.optString("classId");
                            // String serviceName = jsonObject.optString("serviceName");
                            ServerIpInfo serverIpInfo = new ServerIpInfo();
                            serverIpInfo.setClassName(className);
                            serverIpInfo.setDevicePort(port);
                            serverIpInfo.setDeviceIp(ip);
                            serverIpInfo.setClassName(className);
                            serverIpInfo.setTeacherId(teacherId);
                            serverIpInfo.setClassId(classId);
                            //多线程操作这个集合  添加一个等待
                            synchronized (SelectClassActivity.this) {
                                if (!ServerIpInfos1.containsKey(classId)){
                                    ServerIpInfos1.put(classId, serverIpInfo);
                                }

                            }
                            Iterator<String> iterator = ServerIpInfos1.keySet().iterator();

                            if (iterator.hasNext()) {
                                String next = iterator.next();
                                QZXTools.logD(ServerIpInfos1.size()+"isMapEaquse111: ServerIpInfos1=" + next+"....."+ServerIpInfos1.get(next).toString());
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    @Subscriber(tag = "closeSelverStop", mode = ThreadMode.MAIN)
    public void closeSelverStop(String closeSelverStop) {
        Log.i("qin", "closeSelverStop: " + closeSelverStop);

        if (myServerConn!=null){
            unbindService(myServerConn);
        }
    }

    /**
     * 班级码输入输入框
     *
     * @param view
     */
    public void showInputClassCodeDialog(View view){
        //判断是否连网
        if (!NetStatusUtil.isNetworkConnected(this)){
            QZXTools.popCommonToast(this,"请连接网络",false);
            return;
        }

        if (ViewUtils.isFastClick(1000)){
            XToast toast = new XToast(getApplication())
                    .setView(R.layout.toast_input_class_code)
                    .setOutsideTouchable(false)
                    .setBackgroundDimAmount(0.5f)
                    .setText(R.id.tv_wifi,wifiName)
                    .setAnimStyle(android.R.style.Animation_Translucent)
                    .setGravity(Gravity.CENTER)
                    .setOnClickListener(R.id.iv_dismiss, new OnClickListener<ImageView>() {
                        @Override
                        public void onClick(final XToast toast, ImageView view) {
                            toast.cancel();
                        }
                    })
                    .setOnClickListener(R.id.tv_sure, new OnClickListener<TextView>() {
                        @Override
                        public void onClick(final XToast toast, TextView view) {
                            toast.cancel();

                            EmojiEditText et_code = toast.getView().findViewById(R.id.et_code);

                            getIpAndPort(et_code.getText().toString());
                        }
                    })
                    .show();

            EmojiEditText et_code = toast.getView().findViewById(R.id.et_code);
            TextView tv_sure = toast.getView().findViewById(R.id.tv_sure);

            et_code.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (!TextUtils.isEmpty(s)&&s.toString().length()==6){
                        tv_sure.setEnabled(true);
                        tv_sure.setAlpha(1f);
                    }else {
                        tv_sure.setEnabled(false);
                        tv_sure.setAlpha(0.5f);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            showKeyboard(et_code);
        }
    }

    /**
     * 获取ip和端口
     *
     * @param key
     */
    private void getIpAndPort(String key){
        String url = UrlUtils.BaseUrl + UrlUtils.GetIpAndPort;

        Map<String, String> mapParams = new LinkedHashMap<>();
        mapParams.put("key", key);

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();
                        QZXTools.logE("todo homework resultJson=" + resultJson, null);
                        IpPortBean ipPortBean = new Gson().fromJson(resultJson, IpPortBean.class);

                        if (ipPortBean!=null&&ipPortBean.getResult()!=null){
                            QZXTools.logE("ip:"+ipPortBean.getResult().getAddress(),null);
                            QZXTools.logE("port:"+ipPortBean.getResult().getPort(),null);
                            QZXTools.logE("teacherId:"+ipPortBean.getResult().getTeacherId(),null);

                            forwardInteractiveActivity(ipPortBean.getResult().getAddress(),Integer.parseInt(ipPortBean.getResult().getPort()),ipPortBean.getResult().getTeacherId());
                        }else {
                            mHandler.sendEmptyMessage(Server_Error);
                        }

                    }catch (Exception e){
                        mHandler.sendEmptyMessage(Server_Error);

                        QZXTools.logE("=======",e);
                    }
                } else {
                    mHandler.sendEmptyMessage(Server_Error);
                }
            }
        });
    }

    /**
     * 跳转互动界面
     *
     * @param ip
     * @param port
     * @param teacherId
     */
    private void forwardInteractiveActivity(String ip,int port,String teacherId){
        try {
            //开启连接服务
            UrlUtils.SocketIp = ip;
            UrlUtils.SocketPort = port;

            String path = QZXTools.getExternalStorageForFiles(this, null) + "/config.txt";
            Properties properties = QZXTools.getConfigProperties(path);
            properties.setProperty("rootIp", UrlUtils.BaseUrl);
            properties.setProperty("socketIp", UrlUtils.SocketIp);
            properties.setProperty("socketPort", UrlUtils.SocketPort + "");
            properties.setProperty("imgIp", UrlUtils.ImgBaseUrl);
            properties.setProperty("pointIp", UrlUtils.MaiDianUrl);
            FileOutputStream fos = new FileOutputStream(path);
            properties.store(new OutputStreamWriter(fos, "UTF-8"),
                    "Config");

            //关闭服务
            EventBus.getDefault().post("closeSelverStop","closeSelverStop");
            Intent intent1 = new Intent(this, InteractiveActivity.class);
            startActivity(intent1);
            finish();

            //加入课堂埋点
            SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("teacherId",teacherId);
            String uuid = UUID.randomUUID().toString();
            SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("joinClassStudent",uuid);
            BuriedPointUtils.buriedPoint("2003","","","",uuid);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //弹出软键盘
    private void showKeyboard(EditText editText) {
        //其中editText为dialog中的输入框的 EditText
        if(editText!=null){
            //设置可获得焦点
            editText.setFocusable(true);
            editText.setFocusableInTouchMode(true);
            //请求获得焦点
            editText.requestFocus();
            //调用系统输入法
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(editText, 0);
        }
    }
}
