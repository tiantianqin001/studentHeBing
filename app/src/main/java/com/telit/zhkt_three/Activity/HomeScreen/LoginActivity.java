package com.telit.zhkt_three.Activity.HomeScreen;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.iflytek.oauth.EduOauth;
import com.iflytek.oauth.IUIListener;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.OauthMy.ProviceActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CustomEditText;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.ForgetPwdDialog;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.Jpush.JpushApply;
import com.telit.zhkt_three.Utils.MD5Utils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/5/9 13:49
 * <p>
 * 存在的Bug:
 * 一、软键盘上移出现触摸删除有问题
 * 已修复：
 * // getGlobalVisibleRect(rect);  event.getRawX(); ====》 getLocalVisibleRect(rect);   event.getX()即可
 * <p>
 * 如果values-1920x1200无法适配到据说是因为底部导航栏，改成values-1920x1128即可适配
 * <p>
 * //保存学生id、用户id以及班级id
 * <p>
 * //上传设备id即极光推送注册id，如果用户用同一个账号在别的平板上再次登录进去后，会依据这个设备id将之前的信息剔除掉
 * //所以数据库时先以及学生id查询后如果存在且设备id不同则表示用户在其他设备上登录了，保存当前的设备id,剔除先前的发送离线推送提醒
 * <p>
 * 登录成功后开启别名和标签推送
 * 退出登录、账号在别处登录时候解除推送
 * 注意这里的登出接口只是一次记录，并没有什么作用，其实登出只是强行把任何相关的都关闭罢了
 * <p>
 * //密码加密给服务端直接存放数据库，忘记密码只能重置或者修改数据库
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;

    private static final int Oauth_Request_Code = 7;

    @BindView(R.id.img_wifi)
    ImageView img_wifi;
    @BindView(R.id.login_logo)
    ImageView login_logo;
    @BindView(R.id.login_username_edit)
    CustomEditText username;
    @BindView(R.id.login_pwd_edit)
    CustomEditText password;
    @BindView(R.id.login_btn)
    ImageView img_login;
    @BindView(R.id.login_forget_pwd)
    TextView forget_pwd;
    @BindView(R.id.login_oauth_enter)
    TextView login_oauth_enter;



    /**
     * 以后可能需要回显用户名等
     */
    private SharedPreferences sp_student;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Operate_Success = 2;
    private static final int Other_Error = 3;
    private static final int Oauth_Result = 4;

    private static boolean isShow=false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popCommonToast(LoginActivity.this, "网络比较差！", false);
                        if (circleProgressDialog!=null && circleProgressDialog.isAdded()) {
                            circleProgressDialog.dismissAllowingStateLoss();
                        }
                    }


                    break;
                case Error404:
                    if (isShow){
                        QZXTools.popCommonToast(LoginActivity.this, "请求地址失效，没有相关资源！", false);
                        if (circleProgressDialog!=null && circleProgressDialog.isAdded()) {
                            circleProgressDialog.dismissAllowingStateLoss();
                        }
                    }

                    break;
                case Operate_Success:

                    if (isShow){
                        if (circleProgressDialog!=null && circleProgressDialog.isAdded()) {
                            circleProgressDialog.dismissAllowingStateLoss();
                        }

                        /**
                         * 开启极光推送别名和标签推送
                         * 6006 某一个tag过长，不能超过40字节
                         * */
                        JpushApply.getIntance().registJpush(MyApplication.getInstance());

                        //设置小米推送别名
                        MiPushClient.setAlias(LoginActivity.this, UserUtils.getUserId(), null);

                        QZXTools.popCommonToast(LoginActivity.this, (String) msg.obj, false);

                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        //结束登录页面
                        LoginActivity.this.finish();
                    }


                    break;
                case Other_Error:
                    if (isShow){
                        QZXTools.popCommonToast(LoginActivity.this, (String) msg.obj, false);
                        if (circleProgressDialog!=null && circleProgressDialog.isAdded()) {
                            circleProgressDialog.dismissAllowingStateLoss();
                        }
                    }

                    break;
                case Oauth_Result:
                    if (isShow){
                        String resultJson = (String) msg.obj;
                        Gson gson = new Gson();
                        Map<String, Object> map = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                        }.getType());

                        String Oauth_UserId = (String) map.get("data");

                        sp_student.edit().putString("oauth_id", Oauth_UserId).commit();
                    }

                    break;
            }
        }
    };



    private CircleProgressDialogFragment circleProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        isShow=true;

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;
        QZXTools.logE("width=" + width + ";height=" + height, null);

        EventBus.getDefault().register(this);
        isShow=true;

        sp_student = getSharedPreferences("student_info", MODE_PRIVATE);

        unbinder = ButterKnife.bind(this);

        //显示用户名，如果之前进入过
        String name = UserUtils.getLoginName();
        username.setText(name);
        //光标移动至文本末尾
        username.setSelection(name.length());

        img_wifi.setOnClickListener(this);
        img_login.setOnClickListener(this);
        forget_pwd.setOnClickListener(this);
        login_oauth_enter.setOnClickListener(this);

    }

    /**
     * Oauth认证成功返回userid后请求网络
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onResume() {
        super.onResume();
        QZXTools.logE("onResume", null);
        if (!TextUtils.isEmpty(sp_student.getString("oauth_id", ""))) {
            requestOauthLogin(sp_student.getString("oauth_id", ""));
        }

        if (JPushInterface.isPushStopped(MyApplication.getInstance())) {
            QZXTools.logE("LoginActivity JPush resume", null);
            JPushInterface.resumePush(MyApplication.getInstance());
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        isShow=false;

        EventBus.getDefault().unregister(this);

        /**
         * 防止内存泄露
         * */
        mHandler.removeCallbacksAndMessages(null);
        QZXTools.setmToastNull();

        if (circleProgressDialog!=null && circleProgressDialog.isAdded()) {
            circleProgressDialog.dismissAllowingStateLoss();
        }

        super.onDestroy();
    }

    @Subscriber(tag = Constant.StopJpush, mode = ThreadMode.MAIN)
    public void stopJpush(String tags) {
        QZXTools.logE("tags=" + tags, null);
        if (tags.contains("delete alias success")) {
            QZXTools.logE("Login delete alias success", null);
            JPushInterface.stopPush(MyApplication.getInstance());
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Oauth_Request_Code && resultCode == RESULT_OK) {
            String userId = data.getStringExtra("data_userid");
            requestOauthLogin(userId);
        }
    }

    /**
     * Can not perform this action after onSaveInstanceState
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void requestOauthLogin(String userXfId) {
        //这里是省平台登录
        UserUtils.setBooleanTypeSpInfo(sp_student, "IsOauthMode", true);


        circleProgressDialog = new CircleProgressDialogFragment();
        circleProgressDialog.show(getSupportFragmentManager(),CircleProgressDialogFragment.class.getSimpleName());

        String url = UrlUtils.BaseUrl + UrlUtils.OauthLogin;

//        String url = "http://172.16.4.40:8090/wisdomclass/toLoginByOauth";

        Map<String, String> paraMap = new LinkedHashMap<>();
        // 9c5e8fc2cc6e5a197b4ea823497f3da719ce42bcab3b04bf532573912beb97da4826d86417934d5f5d4a9af096137783f2e175af23ffe065
        //这个user ID 是省平台的
        paraMap.put("userId", userXfId);
        paraMap.put("type", "student");
        //传入极光的注册ID作为设备id
        paraMap.put("deviceId", JPushInterface.getRegistrationID(this));

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("onFailure e=" + e, null);
                //服务端错误,例如连接、读取、写入超时等
                mHandler.sendEmptyMessage(Server_Error);
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {

//                            QZXTools.logE("resultJson=" + resultJson, null);
                    try {
                        String resultJson = response.body().string();
                        JSONObject jsonObject = new JSONObject(resultJson);
                        boolean success = jsonObject.getBoolean("success");
                        String errorCode = jsonObject.getString("errorCode");
                        String msg = jsonObject.getString("msg");
                        int total = jsonObject.getInt("total");
                        int pageNo = jsonObject.getInt("pageNo");

                        String result = jsonObject.getString("result");
//                                QZXTools.logE("result=" + result, null);

                        if (errorCode.equals("1")) {
                            Gson gson = new Gson();
                            StudentInfo studentInfo = gson.fromJson(result, StudentInfo.class);
                            QZXTools.logE("studentInfo=" + studentInfo, null);

                            UserUtils.setStringTypeSpInfo(sp_student, "studentId", studentInfo.getStudentId());
                            UserUtils.setStringTypeSpInfo(sp_student, "loginName", studentInfo.getLoginName());
                            UserUtils.setStringTypeSpInfo(sp_student, "classId", studentInfo.getClassId());
                            UserUtils.setStringTypeSpInfo(sp_student, "userId", studentInfo.getUserId());
                            UserUtils.setStringTypeSpInfo(sp_student, "shot_classId", studentInfo.getClassShortId());
                            //新增className studentName photo
                            UserUtils.setStringTypeSpInfo(sp_student, "className", studentInfo.getGradeName()
                                    + " " + studentInfo.getClassName());
                            UserUtils.setStringTypeSpInfo(sp_student, "studentName", studentInfo.getStudentName());

                            if (studentInfo.getPhoto() != null) {
                                UserUtils.setStringTypeSpInfo(sp_student, "avatarUrl", studentInfo.getPhoto());
                            }

                            //设置成功登录标志
                            UserUtils.setBooleanTypeSpInfo(sp_student, "isLoginIn", true);

                            //保存到本地数据库
                            StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
                            studentInfoDao.insertOrReplace(studentInfo);

                            Message message = mHandler.obtainMessage();
                            message.what = Operate_Success;
                            message.obj = "登录成功";
                            mHandler.sendMessage(message);
                        } else {
                            //用户名或者密码错误等错误信息
                            Message message = mHandler.obtainMessage();
                            message.what = Other_Error;
                            message.obj = msg;
                            mHandler.sendMessage(message);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Message message = mHandler.obtainMessage();
                        message.what = Other_Error;
                        message.obj = "json解析异常";
                        mHandler.sendMessage(message);
                    }
                } else {
                    mHandler.sendEmptyMessage(Error404);
                }
            }
        });
    }
    /**
     * Oauth登录---应该单独放一个Activity
     */
    private void OauthLoginIn() {
        /**
         * 该方法会自动判断本设备有没有已经登录过用户，如果有默认调用自动登录方法，如果没有默认加载登录页面
         * */
        EduOauth.getInstance().loginH5(this, new IUIListener() {
            @Override
            public void onLoginComplete(String response) {
                /**
                 *
                 * 返回值data，返回码为1时为userId，返回码不为1时为空
                 *
                 * loginH5 onLoginComplete s=
                 * {"code":"1","message":"success",
                 * "data":"9c5e8fc2cc6e5a197b4ea823497f3da719ce42bcab3b04bf532573912beb97da4826d86417934d5f5d4a9af096137783f2e175af23ffe065",
                 * "success":true}
                 * */

                QZXTools.logE("loginH5 onLoginComplete s=" + response, null);

                Message message = mHandler.obtainMessage();
                message.what = Oauth_Result;
                message.obj = response;
                mHandler.sendMessage(message);

            }

            @Override
            public void onLoginFailed(String error) {
                QZXTools.logE("loginH5 onLoginFailed s=" + error, null);
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.img_wifi:
                //进入wifi设置界面
                startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                break;
            case R.id.login_btn:
                if (TextUtils.isEmpty(username.getText().toString().trim())) {
                    QZXTools.popCommonToast(this, getResources().getString(R.string.tips_account), false);
                    return;
                } else if (TextUtils.isEmpty(password.getText().toString().trim())) {
                    QZXTools.popCommonToast(this, getResources().getString(R.string.tips_pwd), false);
                    return;
                }

                //是否有网络
                if (!QZXTools.isNetworkAvailable()) {
                    QZXTools.popCommonToast(this, "没有网络耶！", false);
                    return;
                }

                UserUtils.setBooleanTypeSpInfo(sp_student, "IsOauthMode", false);

                circleProgressDialog = new CircleProgressDialogFragment();
                circleProgressDialog.show(getSupportFragmentManager(),CircleProgressDialogFragment.class.getSimpleName());

                //登录请求
                String url = UrlUtils.BaseUrl + UrlUtils.Login;
                Map<String, String> mapParams = new LinkedHashMap<>();
                mapParams.put("username", username.getText().toString().trim());
                mapParams.put("password", password.getText().toString().trim());
                mapParams.put("type", "student");
                //传入极光的注册ID作为设备id
                mapParams.put("deviceId", JPushInterface.getRegistrationID(this));

                OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        QZXTools.logE("onFailure e=" + e, null);
                        //服务端错误,例如连接、读取、写入超时等
                        mHandler.sendEmptyMessage(Server_Error);
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        QZXTools.logE("onResponse: "+response.toString(),null);
                        if (response.isSuccessful()) {
                            String resultJson = response.body().string();
//                            QZXTools.logE("resultJson=" + resultJson, null);
                            try {
                                JSONObject jsonObject = new JSONObject(resultJson);
                                boolean success = jsonObject.getBoolean("success");
                                String errorCode = jsonObject.getString("errorCode");
                                String msg = jsonObject.getString("msg");
                                int total = jsonObject.getInt("total");
                                int pageNo = jsonObject.getInt("pageNo");

                                String result = jsonObject.getString("result");
                                QZXTools.logE("result=" + result, null);

                                if (errorCode.equals("1")) {
                                    Gson gson = new Gson();
                                    StudentInfo studentInfo = gson.fromJson(result, StudentInfo.class);
                                    QZXTools.logE("studentInfo=" + studentInfo, null);

                                    UserUtils.setStringTypeSpInfo(sp_student, "studentId", studentInfo.getStudentId());
                                    UserUtils.setStringTypeSpInfo(sp_student, "loginName", studentInfo.getLoginName());
                                    UserUtils.setStringTypeSpInfo(sp_student, "classId", studentInfo.getClassId());
                                    UserUtils.setStringTypeSpInfo(sp_student, "userId", studentInfo.getUserId());

                                    //新增className studentName
                                    UserUtils.setStringTypeSpInfo(sp_student, "className", studentInfo.getGradeName()
                                            + studentInfo.getClassName());
                                    UserUtils.setStringTypeSpInfo(sp_student, "studentName", studentInfo.getStudentName());

                                    if (studentInfo.getClassShortId() != null) {
                                        UserUtils.setStringTypeSpInfo(sp_student, "short_classId", studentInfo.getClassShortId());
                                    }
                                    if (studentInfo.getPhoto() != null) {
                                        UserUtils.setStringTypeSpInfo(sp_student, "avatarUrl", studentInfo.getPhoto());
                                    }

                                    //设置成功登录标志
                                    UserUtils.setBooleanTypeSpInfo(sp_student, "isLoginIn", true);

                                    //保存到本地数据库
                                    StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
                                    studentInfoDao.insertOrReplace(studentInfo);

                                    Message message = mHandler.obtainMessage();
                                    message.what = Operate_Success;
                                    message.obj = "登录成功";
                                    mHandler.sendMessage(message);
                                    //在登录成功后，我们要管控我们的app设置成开机自启 tudo
                                    Intent intent = new Intent("com.linspirer.edu.loginapkfinish");
                                    intent.putExtra("userid", studentInfo.getUserId());
                                    intent.putExtra("username", studentInfo.getLoginName());
                                    intent.putExtra("schoolid", MD5Utils.MD5(studentInfo.getSchoolId()));
                                    intent.putExtra("classname", studentInfo.getGradeName()
                                            + " " + studentInfo.getClassName());
                                    intent.putExtra("txurl", studentInfo.getPhoto());

                                   // intent.putExtra("useOfflineLogin", true);
                                    intent.setPackage("com.android.launcher3");
                                    sendBroadcast(intent);
                                } else {
                                    //用户名或者密码错误等错误信息
                                    Message message = mHandler.obtainMessage();
                                    message.what = Other_Error;
                                    message.obj = msg;
                                    mHandler.sendMessage(message);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                                Message message = mHandler.obtainMessage();
                                message.what = Other_Error;
                                message.obj = "json解析异常";
                                mHandler.sendMessage(message);
                            }
                        } else {
                            mHandler.sendEmptyMessage(Error404);
                        }
                    }
                });
                break;
            case R.id.login_forget_pwd:

                //使用Activity做成半透明的，这种效果和下面的Dialog一样，activity的自带进入进出动画
//                startActivity(new Intent(this, ForgetPwdActivity.class));

                ForgetPwdDialog forgetPwdDialog = new ForgetPwdDialog();
                forgetPwdDialog.show(getSupportFragmentManager(), ForgetPwdDialog.class.getSimpleName());
                break;
            case R.id.login_oauth_enter:
//                if (TextUtils.isEmpty(sp_student.getString("oauth_id", ""))) {
//                    OauthLoginIn();
//                } else {
//                    requestOauthLogin(sp_student.getString("oauth_id", ""));
//                }

                //自己写的webview展示登录
                Intent intent = new Intent(this, ProviceActivity.class);
                startActivityForResult(intent, Oauth_Request_Code);
              //  OauthLoginIn();
                break;
        }
    }

    @Override
    public void onBackPressed() {
    }
}
