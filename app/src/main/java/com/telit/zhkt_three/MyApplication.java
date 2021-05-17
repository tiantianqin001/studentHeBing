package com.telit.zhkt_three;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.media.projection.MediaProjectionManager;
import android.os.Build;
import android.os.Process;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.multidex.MultiDex;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.hjq.toast.ToastUtils;
import com.iflytek.oauth.EduOauth;
import com.lzy.okgo.OkGo;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.MaiDian.SelfLearningVo;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.DaoMaster;
import com.telit.zhkt_three.greendao.DaoSession;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.tencent.bugly.crashreport.CrashReport;
import com.xiaomi.mipush.sdk.MiPushClient;

import org.greenrobot.greendao.database.Database;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import cn.jpush.android.api.CustomPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/5/9 17:40
 * <p>
 * 如果GreenDao升级会删除之前的所有数据，所以升级一下
 */
public class MyApplication extends Application {

    private static MyApplication myApplication;

    public static MyApplication getInstance() {
        return myApplication;
    }

    private static final String DATABASE_NAME = "greendao.db";

    private static final String APP_ID = "2882303761519875618";
    private static final String APP_KEY = "5991987565618";

    @Override
    public void onCreate() {
        super.onCreate();
        myApplication = this;

        /**
         * 引入bugly
         * bugly主动上报：
         * CrashReport.postCatchedException(thr);  // bugly会将这个throwable上报
         * */
        CrashReport.initCrashReport(getApplicationContext(), "1afa9c957d", true);

//        if (LeakCanary.isInAnalyzerProcess(this)) {
//            // This process is dedicated to LeakCanary for heap analysis.
//            // You should not init your app in this process.
//            return;
//        }
//        LeakCanary.install(this);

        //初始化Oauth2.0，用户统一认证   登录框格式，1-竖屏，2-横屏，不传默认为1
        EduOauth.registInstance(getApplicationContext(), Constant.EduAuthAppKey, Constant.EduAuthPassword, "2");

        JPushInterface.setDebugMode(true);
        //初始化极光推送
        JPushInterface.init(this);
        //设置极光通知栏样式，默认编号依然为0
        setStyleCustom();

        initGreenDAO();
        ToastUtils.init(this);

        //初始化SmartRefreshLayout刷新样式
        initSmartRefreshLayout();

        //关闭日志
       // QZXTools.openLog=false;
        //开启日志
        QZXTools.openLog=true;

        OkGo.getInstance().init(this);

        //初始化小米推送
        initMIPush();
    }

    private void initMIPush(){
        if (shouldInit()) {
            MiPushClient.registerPush(this, APP_ID, APP_KEY);
        }
    }

    private boolean shouldInit() {
        ActivityManager am = ((ActivityManager) getSystemService(Context.ACTIVITY_SERVICE));
        List<ActivityManager.RunningAppProcessInfo> processInfos = am.getRunningAppProcesses();
        String mainProcessName = getPackageName();
        int myPid = Process.myPid();
        for (ActivityManager.RunningAppProcessInfo info : processInfos) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 设置通知栏样式 - 定义通知栏Layout
     * 图标、标题、内容和时间
     */
    private void setStyleCustom() {
        CustomPushNotificationBuilder builder = new CustomPushNotificationBuilder(this,
                R.layout.customer_notitfication_layout, R.id.icon, R.id.title, R.id.text, R.id.time);
        builder.layoutIconDrawable = R.mipmap.ic_launcher;
        builder.statusBarDrawable = R.mipmap.ic_launcher;
        JPushInterface.setDefaultPushNotificationBuilder(builder);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    private DaoSession daoSession;

    public DaoSession getDaoSession() {
        return daoSession;
    }

    /**
     * 初始化greendao
     * <p>
     * 解释一下：
     * 底层还是用到sqlitehelper获得greendao的helper,然后通过helper获取database,通过database创建DaoMaster
     * DaoMaster获取到DaoSession,通过DaoSession获取到想要的dao操作对象，例如UserDao
     */
    private void initGreenDAO() {
//        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, DATABASE_NAME);
        DbOpenHelper helper = new DbOpenHelper(this, DATABASE_NAME);
        Database database = database = helper.getWritableDb();
        DaoMaster daoMaster = new DaoMaster(database);
        daoSession = daoMaster.newSession();
    }

    /**
     * 获取MediaProjectionManager对象
     */
    public MediaProjectionManager getMPM() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        } else {
            throw new IllegalStateException("Android5.0以下不可以使用MediaProjectionManager");
        }
    }


    // 参与人数
    public static final int FLAG_AUTO_LEARNING_ONE = 0;
    // 学习次数
    public static final int FLAG_AUTO_LEARNING_TWO = 1;
    // 完成试题数 todo 怎么样才算触发完成试题？
    public static final int FLAG_AUTO_LEARNING_THREE = 2;
    // 学习资源条数
    public static final int FLAG_AUTO_LEARNING_FOUR = 3;
    // 学习时长
    public static final int FLAG_AUTO_LEARNING_FIVE = 4;

    /**
     * 自主学习埋点
     * <p>
     * flag=0 表示：点击自主学习触发一次；
     * flag=1 同参与人数一样每次进入触发一次；
     * flag=2 题库答题触发一次；
     * flag=3 表示：点击资源触发一次；
     * flag=4 表示 进入计时退出结束时发送的时长触发一次；
     *
     * @param flag         埋点类型标签  0:参与人数 1.学习次数 2.完成试题数 3.学习资源条数 4.学习时长
     * @param learningTime 学习时长，如果不是flag=4,learningTime=-1
     * @param subjectId    -1表示全部
     * @param subjectName  ""表示全部
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void AutoLearningMaiDian(int flag, long learningTime, int subjectId, String subjectName) {
        String url = UrlUtils.MaiDianUrl + UrlUtils.AutoLearingPoint;
        Map<String, String> mapParams = new LinkedHashMap<>();

        //塞入埋点数据
        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                .where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).list().get(0);
        SelfLearningVo selfLearningVo = new SelfLearningVo();
        selfLearningVo.setsId(studentInfo.getStudentId());
        selfLearningVo.setClassId(studentInfo.getClassId());
        selfLearningVo.setGradeId(studentInfo.getGradeId());
        selfLearningVo.setSubjectId(subjectId);
        selfLearningVo.setsUseTime(learningTime);
        selfLearningVo.setSubjectName(subjectName);

        Gson gson = new Gson();
        String jsonStr = gson.toJson(selfLearningVo);
        QZXTools.logE("AutoLearningMaiDian jsonStr=" + jsonStr, null);

        mapParams.put("SelfLearning", jsonStr);
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("自主学习 flag=" + flag + "===> 埋点失败...onFailure", null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    QZXTools.logE("自主学习 flag=" + flag + "===> 埋点成功...", null);
                } else {
                    QZXTools.logE("自主学习 flag=" + flag + "===> 埋点失败...not success", null);
                }
            }
        });
    }

    //学生参与人数
    public static final int FLAG_PRE_ONE = 1002;
    //学生学习次数
    public static final int FLAG_PRE_TWO = 1003;
    //学习资源数
    public static final int FLAG_PRE_THREE = 1004;

    /**
     * 课前预习埋点
     *
     * @param flag  flag=1002 触发时机为点进预习界面触发一次；flag=1003 触发时机同1002；flag=1004 触发时机为点击资源预览
     * @param resId 资源的ID 为-1表示进入
     */
    public void PreMainDian(int flag, String resId) {
        String url = UrlUtils.MaiDianUrl + UrlUtils.PreLearningPoint;
        Map<String, String> mapParams = new LinkedHashMap<>();

        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                .where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).list().get(0);
        mapParams.put("classId", studentInfo.getClassId());
        String gradeId = studentInfo.getGradeId();
        if (!TextUtils.isEmpty(gradeId)) {
            mapParams.put("gradeId", studentInfo.getGradeId());
        }
        if (!TextUtils.isEmpty(resId)) {
            mapParams.put("resIds", resId);
        }
        mapParams.put("tId", studentInfo.getUserId());
        mapParams.put("dealCode", flag + "");

        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, mapParams, new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("课前预习 flag=" + flag + "===> 埋点失败...onFailure", null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    QZXTools.logE("课前预习 flag=" + flag + "===> 埋点成功...", null);
                } else {
                    QZXTools.logE("课前预习 flag=" + flag + "===> 埋点失败...not success", null);
                }
            }
        });
    }

    //初始化SmartRefreshLayout刷新样式
    private void initSmartRefreshLayout() {
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @NonNull
            @Override
            public RefreshHeader createRefreshHeader(@NonNull Context context, @NonNull RefreshLayout layout) {
                layout.setPrimaryColorsId(R.color.primaryColor_fa_bg, R.color.list_bottom_color);//全局设置主题颜色
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });

        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @NonNull
            @Override
            public RefreshFooter createRefreshFooter(@NonNull Context context, @NonNull RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }
}
