package com.telit.zhkt_three.Activity.HomeScreen;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hjq.toast.ToastUtils;
import com.iflytek.oauth.EduOauth;
import com.iflytek.oauth.IRequestListener;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.OauthMy.ProviceActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.Fragment.Dialog.CameraAlbumPopupFragment;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.AppInfoUtils;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.CheckVersionUtil;
import com.telit.zhkt_three.Utils.Jpush.JpushApply;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.manager.AppManager;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.zbv.basemodel.AutoUpdateAccessService;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class PersonInfoActivity extends BaseActivity implements View.OnClickListener {
    private Unbinder unbinder;

    @BindView(R.id.person_back)
    ImageView person_back;
    @BindView(R.id.person_avatar)
    CircleImageView person_avadar;
    @BindView(R.id.tv_appName)
    TextView tv_appName;
    @BindView(R.id.person_version)
    TextView person_version;
    @BindView(R.id.person_name)
    TextView person_name;
    @BindView(R.id.person_loginOut)
    ImageView person_loginOut;

    @BindView(R.id.person_school)
    TextView tv_school;
    @BindView(R.id.person_clazz)
    TextView tv_clazz;
    @BindView(R.id.person_address)
    TextView tv_address;
    @BindView(R.id.person_sex)
    TextView tv_sex;
    @BindView(R.id.person_birthday)
    TextView tv_birthday;
    @BindView(R.id.person_remark)
    TextView tv_remark;

    @BindView(R.id.switch_on_off)
    Switch switch_on_off;

    @BindView(R.id.person_check_version)
    TextView person_check_version;

    @BindView(R.id.tv_popsition)
    TextView tv_popsition;

    //    private CameraAlbumPopupFragment cameraAlbumPopupFragment;
    private Uri outputUri;

    private CircleProgressDialogFragment circleProgressDialogFragment;

    private static final int Server_Error = 0;
    private static final int Error404 = 1;
    private static final int Alter_Photo_Result = 2;
    private static final int OffLine_Success = 3;
    private static final int OffLine_Failed = 4;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    QZXTools.popToast(PersonInfoActivity.this, "当前网络不佳....", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
             /*       NoSercerDialog noSercerDialog=new NoSercerDialog();
                    noSercerDialog.show(getSupportFragmentManager(), NoSercerDialog.class.getSimpleName());*/
                    break;
                case Error404:
                    QZXTools.popToast(PersonInfoActivity.this, "没有相关资源！", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                 /*   NoResultDialog noResultDialog=new NoResultDialog();
                    noResultDialog.show(getSupportFragmentManager(), NoResultDialog.class.getSimpleName());*/
                    break;
                case Alter_Photo_Result:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    Bundle bundle = msg.getData();
                    String msgInfo = bundle.getString("msg");
                    String avatarUrl = bundle.getString("avatarUrl");

                    Glide.with(PersonInfoActivity.this).load(avatarUrl)
                            .placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(person_avadar);

                    //保存到sp
                    UserUtils.setStringTypeSpInfo(getSharedPreferences("student_info", MODE_PRIVATE), "avatarUrl", avatarUrl);

                    //获取到student信息
                    StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                            .where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).list().get(0);

//                    QZXTools.logE("query studentInfo=" + studentInfo, null);

                    studentInfo.setPhoto(avatarUrl);
                    //更新StudentInfoDao
                    MyApplication.getInstance().getDaoSession().getStudentInfoDao().update(studentInfo);

                    EventBus.getDefault().post(avatarUrl, Constant.Update_Avatar);

                    QZXTools.popToast(PersonInfoActivity.this, msgInfo, false);
                    break;
                case OffLine_Failed:
                case OffLine_Success:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    //设置未登录标志
                    SharedPreferences sharedPreferences = getSharedPreferences("student_info", MODE_PRIVATE);
//                    UserUtils.setBooleanTypeSpInfo(sharedPreferences, "isLoginIn", false);
                    UserUtils.setOauthId(sharedPreferences, "oauth_id", "");
                    UserUtils.removeTgt();

                    SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("getTgt","");

                    //领创管控唤起管理员
                    lingChang();

                    /**
                     * 登出解除极光推送
                     * */
                    JpushApply.getIntance().unRegistJpush(MyApplication.getInstance());

                    //撤销别名
                    MiPushClient.unsetAlias(PersonInfoActivity.this, UserUtils.getUserId(), null);

                    //退出登录的埋点
                    BuriedPointUtils.buriedPoint("2002","","","","");


                    startActivity(new Intent(PersonInfoActivity.this, ProviceActivity.class));

                    AppManager.getAppManager().finishAllActivity();
                    break;
            }
        }
    };

    private void lingChang() {
        Intent intent = new Intent("com.android.launcher3.mdm.OPEM_ADMIN");
        intent.setPackage("com.android.launcher3");

        sendBroadcast(intent);

        // Toast.makeText(mContext,"领创发com.linspirer.edu.homeaction广播",Toast.LENGTH_LONG).show();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_info);
        unbinder = ButterKnife.bind(this);
        isUpData=true;
        StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
        StudentInfo studentInfo = studentInfoDao.queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

        QZXTools.logE("person center studentInfo=" + studentInfo, null);

        person_name.setText(studentInfo.getStudentName());
        tv_school.setText(studentInfo.getSchoolName());
        if (studentInfo.getClassName() != null) {
            if (studentInfo.getGradeName() != null) {
                tv_clazz.setText(studentInfo.getGradeName().concat(studentInfo.getClassName()));
            } else {
                tv_clazz.setText(studentInfo.getClassName());
            }
        }
        tv_birthday.setText(studentInfo.getDateOfBirth());
        tv_sex.setText(studentInfo.getSex());
        tv_remark.setText(studentInfo.getRemark());

        tv_appName.setText("名称："+AppInfoUtils.getAppName(this));
        person_version.setText("版本号：V"+QZXTools.getVerionName(this));

        //设置头像
        if (studentInfo.getPhoto() != null) {
            Glide.with(this).load(studentInfo.getPhoto())
                    .placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(person_avadar);
        } else {
            person_avadar.setImageResource(R.mipmap.icon_user);
        }

        person_avadar.setOnClickListener(this);
        person_back.setOnClickListener(this);
        person_loginOut.setOnClickListener(this);
        person_check_version.setOnClickListener(this);
        tv_popsition.setOnClickListener(this);

        //switch 开发辅助更新模式监听
        switch_on_off.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    boolean hasAccess = isAccessibilitySettingsOn(PersonInfoActivity.this);
                    if (hasAccess) {
                        AutoUpdateAccessService.INVOKE_TYPE = AutoUpdateAccessService.TYPE_INSTALL_APP;
                        SharedPreferences sharedPreferences = getSharedPreferences("access_mode", Context.MODE_PRIVATE);
                        UserUtils.setBooleanTypeSpInfo(sharedPreferences, "had_access", true);
                    } else {
                        //跳转打开辅助功能
                        QZXTools.popToast(PersonInfoActivity.this, "请打开辅助功能", false);
                        Intent intent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
                        startActivityForResult(intent, ACCESS_CODE);
                    }
                } else {
                    AutoUpdateAccessService.reset();
                    SharedPreferences sharedPreferences = getSharedPreferences("access_mode", Context.MODE_PRIVATE);
                    UserUtils.setBooleanTypeSpInfo(sharedPreferences, "had_access", false);
                }
            }
        });

        //辅助模式
        boolean isAccess = isAccessibilitySettingsOn(this);
        if (isAccess) {
            if (UserUtils.getAccessMode()) {
                //辅助模式已开启
                switch_on_off.setChecked(true);
            } else {
                //辅助模式关闭
                switch_on_off.setChecked(false);
            }
        } else {
            switch_on_off.setChecked(false);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZBVPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QZXTools.logE("PersonInfoActivity requestCode=" + requestCode + ";resultCode=" + resultCode, null);

        //resultCode=0
        if (requestCode == ACCESS_CODE) {
            boolean isAccess = isAccessibilitySettingsOn(this);
            if (isAccess) {
                switch_on_off.setChecked(true);
            } else {
                switch_on_off.setChecked(false);
                QZXTools.popToast(this, "辅助功能不可用", false);
            }
        }

        //0为取消，-1为选择好
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case ZBVPermission.ACTIVITY_REQUEST_CODE:
                    //这一点注意，我们使用系统相机和相册的请求码不能和  public static final int ACTIVITY_REQUEST_CODE = 0x9;一样
                    ZBVPermission.getInstance().onActivityResult(requestCode, resultCode, data);
                    break;
                case CameraAlbumPopupFragment.CODE_SYS_CAMERA:
                    //data为null

                    outputUri = createCropUri();


                    QZXTools.logE("cameraUri:" + CameraAlbumPopupFragment.cameraUri, null);

                    CameraAlbumPopupFragment.cropPhoto(this, CameraAlbumPopupFragment.cameraUri, outputUri);

                    break;
                case CameraAlbumPopupFragment.CODE_SYS_ALBUM:
                    if (data == null) {
                        return;
                    }

                    outputUri = createCropUri();

                    CameraAlbumPopupFragment.cropPhoto(this, data.getData(), outputUri);

                    break;
                case CameraAlbumPopupFragment.CODE_SYS_CROP:
                    //通过剪裁保存的路径Uri来得到Bitmap
//                    try {
                    //方式一：
//                        Bitmap bitmap = BitmapFactory.decodeStream(getContentResolver().openInputStream(outputUri));

//                    } catch (FileNotFoundException e) {
//                        e.printStackTrace();
//                    }

                    //方式二：
                    String filePath = QZXTools.getRealFilePath(this, outputUri);

                    QZXTools.logE("cropPath=" + filePath + ";data=" + data.getData(), null);

                    if (data.getData() != null) {
                        filePath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));
                    }
                    Bitmap cropBm = BitmapFactory.decodeFile(filePath);

                    if (cropBm == null) {
                        QZXTools.logE("bitmap==null first", null);
                        cropBm = BitmapFactory.decodeFile(filePath);
                        if (cropBm == null) {
                            //裁剪有问题：在华瑞安的Android7.1.2版本系统裁剪有问题
                            filePath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                    + CameraAlbumPopupFragment.cameraUri.toString()
                                    .substring(CameraAlbumPopupFragment.cameraUri.toString().lastIndexOf("/"));
                            cropBm = BitmapFactory.decodeFile(filePath);

                            QZXTools.logE("bitmap==null " + CameraAlbumPopupFragment.cameraUri + ";filePath=" + filePath,
                                    null);
                        }
                    }


                    compressQuality(filePath, cropBm);

                    if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    circleProgressDialogFragment = new CircleProgressDialogFragment();
                    circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

                    //上传修改过的头像
                    String url = UrlUtils.BaseUrl + UrlUtils.AlterAvatar;
                    Map<String, String> paramMap = new LinkedHashMap<>();
                    paramMap.put("userid", UserUtils.getUserId());
                    OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "attachement", paramMap, new File(filePath), new Callback() {
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
                                /*
                                {"success":true,"errorCode":"1","msg":"上传成功",
                                "result":["http://172.16.4.40:8090/filesystem/headImg/66666702496IMG_20190530_091649.jpg"],"total":0,"pageNo":0}
                                */
                                QZXTools.logE("resultJson=" + resultJson, null);

                                Gson gson = new Gson();
                                Map<String, Object> results = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                                }.getType());

                                Message message = mHandler.obtainMessage();
                                message.what = Alter_Photo_Result;
                                Bundle bundle = new Bundle();
                                bundle.putString("msg", (String) results.get("msg"));
                                bundle.putString("avatarUrl", ((List<String>) results.get("result")).get(0));
                                message.setData(bundle);
                                mHandler.sendMessage(message);
                            } else {
                                mHandler.sendEmptyMessage(Error404);
                            }
                        }
                    });

                    //不在DCIM或者别的公众路径下是看不到的
//                    QZXTools.galleryAddPic(this, outputUri);

                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        //防止内存泄露
        mHandler.removeCallbacksAndMessages(null);
        ZBVPermission.getInstance().recyclerAll();
        QZXTools.setmToastNull();

        if (circleProgressDialogFragment != null) {
            circleProgressDialogFragment.releaseAll();
            circleProgressDialogFragment = null;
        }

//        if (cameraAlbumPopupFragment != null) {
//            cameraAlbumPopupFragment = null;
//        }

        super.onDestroy();
    }

    private static boolean isUpData=true;
    private int count=0;
    private long touchFirstTime;
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.person_avatar:
                if (ViewUtils.isFastClick(1000)) {
                    // 进行点击事件后的逻辑操作
                    CameraAlbumPopupFragment cameraAlbumPopupFragment = CameraAlbumPopupFragment.newInstance();
                    cameraAlbumPopupFragment.show(getSupportFragmentManager(), CameraAlbumPopupFragment.class.getSimpleName());
                }
                break;
            case R.id.person_back:
                finish();
                overridePendingTransition(R.anim.in_fade, R.anim.activity_exit_from_right_to_left);
                break;
            case R.id.person_loginOut:
                //弹出确认框
                TipsDialog tipsDialog = new TipsDialog();
                tipsDialog.setTipsStyle("是否确认退出该账号？", "再想想", "确定", -1);
                tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                    @Override
                    public void cancle() {
                        tipsDialog.dismissAllowingStateLoss();
                    }

                    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                    @Override
                    public void confirm() {
                        tipsDialog.dismissAllowingStateLoss();

                        //领创管控的退出 todo
                        Intent intent = new Intent("com.linspirer.edu.logout");
                        intent.setPackage("com.android.launcher3");
                        sendBroadcast(intent);

                        boolean IsOauthMode = UserUtils.getOauthMode();


                        if (IsOauthMode) {
//                            OauthLoginOut();
                            loginOut();
                            QZXTools.logE("IsOauthMode ="+IsOauthMode ,null);
                        } else {
                            if (circleProgressDialogFragment != null && circleProgressDialogFragment.isVisible()) {
                                circleProgressDialogFragment.dismissAllowingStateLoss();
                                circleProgressDialogFragment = null;
                            }
                            circleProgressDialogFragment = new CircleProgressDialogFragment();
                            circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

                            String url = UrlUtils.BaseUrl + UrlUtils.LoginInOutRecord;
                            Map<String, String> paramMap = new LinkedHashMap<>();
                            paramMap.put("classid", UserUtils.getClassId());
                            paramMap.put("userid", UserUtils.getUserId());
                            paramMap.put("roletype", "1");
                            paramMap.put("delflag", "1");
                            OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paramMap, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    QZXTools.logE("onFailure e=" + e, null);
                                    mHandler.sendEmptyMessage(Server_Error);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    if (response.isSuccessful()) {
                                        QZXTools.logE("result=" + response.body().string(), null);
                                        mHandler.sendEmptyMessage(OffLine_Success);
                                    } else {
                                        mHandler.sendEmptyMessage(OffLine_Failed);
                                    }
                                }
                            });
                        }
                    }
                });
                tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                break;
            case R.id.person_check_version:
                if (ViewUtils.isFastClick(3000)) {
                    CheckVersionUtil.getInstance().requestCheckVersion(this);
                }
            case R.id.tv_popsition:
                //个人中心的点击事件
                //个人中心的点击事件  点击15次切换成内网
                count++;
                long curTime = System.currentTimeMillis();
                if (count == 1) {
                    touchFirstTime = curTime;
                }
                if (count == 15 && curTime - touchFirstTime <= 50000) {
                    count = 0;

                    //更新成本地下载的url
                    if (UrlUtils.BaseUrl.equals("http://wisdomclass.ahtelit.com")){
                        ToastUtils.show("家里服务器地址");
                    }else {
                        ToastUtils.show("正式服务器地址");
                    }
                }else if (count > 15) {
                    //重置
                    count = 0;
                }
                break;
        }
    }

    private static final int ACCESS_CODE = 0x77;

    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + AutoUpdateAccessService.class.getCanonicalName();

        QZXTools.logE("service name=" + service, null);

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ACCESSIBILITY_ENABLED);

            QZXTools.logE("accessibilityEnabled = " + accessibilityEnabled, null);
        } catch (Settings.SettingNotFoundException e) {
            QZXTools.logE("Error finding setting, default accessibility to not found: " + e.getMessage(), null);
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            QZXTools.logE("***ACCESSIBILITY IS ENABLED*** -----------------", null);
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    QZXTools.logE("-------------- > accessibilityService :: " + accessibilityService + " " + service,
                            null);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        QZXTools.logE("We've found the correct setting - accessibility is switched on!", null);
                        return true;
                    }
                }
            }
        } else {
            QZXTools.logE("***ACCESSIBILITY IS DISABLED***", null);
        }
        return false;
    }

    //-------------------------------------------------------------------------------------------------

    /**
     * 创建裁剪Uri
     */
    private Uri createCropUri() {
        String fileDir = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".jpg");
        File outputFile = new File(fileDir, stringBuilder.toString());

        QZXTools.logE("outputPATH=" + outputFile.getAbsolutePath(), null);

        return Uri.fromFile(outputFile);
    }

    /**
     * 质量压缩并存入本地或者上传数据库
     */
    private void compressQuality(String filePath, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 服务端退出成功后，客户端将缓存或者数据库中最近登录的用户信息删除，回到登录界面。
     */
    private void OauthLoginOut() {

        EduOauth.getInstance().logout(new IRequestListener() {
            @Override
            public void onResponse(String response) {
                QZXTools.logE("logout onResponse s=" + response, null);

                mHandler.sendEmptyMessage(OffLine_Success);

            }

            @Override
            public void onError(String error) {
                QZXTools.logE("logout onError s=" + error, null);
                mHandler.sendEmptyMessage(OffLine_Failed);
            }
        });
    }

    /**
     * OauthMy/TestActivity.java中的登出
     */
    public void loginOut() {
        String url = "http://open.ahjygl.gov.cn/sso-oauth/client/logout";

        Map<String, String> paramMap = new LinkedHashMap<>();
     //   paramMap.put("tgt", UserUtils.getTgt());
        paramMap.put("tgt", SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("getTgt"));


        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, paramMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("失败", null);
                mHandler.sendEmptyMessage(OffLine_Failed);

                QZXTools.logD("tiantianqinLogin...........省平台退出..."+e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();//只能使用一次response.body().string()
                    QZXTools.logE("response=" + resultJson, null);

                    QZXTools.logD("tiantianqinLogin...........省平台退出..."+resultJson);

                    Gson gson = new Gson();
                    Map<String, Object> results = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                    }.getType());

                    if (results.get("code").equals("1") || results.get("message").equals("success")) {
                        mHandler.sendEmptyMessage(OffLine_Success);
                    } else {
                        mHandler.sendEmptyMessage(OffLine_Failed);
                    }
                }
            }
        });
    }
}
