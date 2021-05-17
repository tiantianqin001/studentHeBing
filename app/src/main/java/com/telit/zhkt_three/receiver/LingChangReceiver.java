package com.telit.zhkt_three.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.Jpush.JpushApply;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.manager.AppManager;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static android.content.Context.MODE_PRIVATE;

public class LingChangReceiver extends BroadcastReceiver {

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
                    QZXTools.popToast(MyApplication.getInstance(), "当前网络不佳....", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    break;
                case Error404:
                    QZXTools.popToast(MyApplication.getInstance(), "没有相关资源！", false);
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }
                    break;
                case Alter_Photo_Result:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    Bundle bundle = msg.getData();
                    String msgInfo = bundle.getString("msg");
                    String avatarUrl = bundle.getString("avatarUrl");

                    //保存到sp
                    UserUtils.setStringTypeSpInfo(MyApplication.getInstance().getSharedPreferences("student_info", MODE_PRIVATE), "avatarUrl", avatarUrl);

                    //获取到student信息
                    StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().queryBuilder()
                            .where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).list().get(0);

//                    QZXTools.logE("query studentInfo=" + studentInfo, null);

                    studentInfo.setPhoto(avatarUrl);
                    //更新StudentInfoDao
                    MyApplication.getInstance().getDaoSession().getStudentInfoDao().update(studentInfo);

                    EventBus.getDefault().post(avatarUrl, Constant.Update_Avatar);

                    QZXTools.popToast(MyApplication.getInstance(), msgInfo, false);
                    break;
                case OffLine_Success:
                    if (circleProgressDialogFragment != null) {
                        circleProgressDialogFragment.dismissAllowingStateLoss();
                        circleProgressDialogFragment = null;
                    }

                    //设置未登录标志
                    SharedPreferences sharedPreferences = MyApplication.getInstance().getSharedPreferences("student_info", MODE_PRIVATE);
//                    UserUtils.setBooleanTypeSpInfo(sharedPreferences, "isLoginIn", false);
                    UserUtils.setOauthId(sharedPreferences, "oauth_id", "");

                    UserUtils.removeTgt();

                    /**
                     * 登出解除极光推送
                     * */
                    JpushApply.getIntance().unRegistJpush(MyApplication.getInstance());

                    //解绑云管控
//                    ManagerControllerClient.getInstance().destroy(MyApplication.getInstance());

                    //撤销别名
                    MiPushClient.unsetAlias(MyApplication.getInstance(), UserUtils.getUserId(), null);

                    AppManager.getAppManager().AppExit();


                    break;
                case OffLine_Failed:
                    QZXTools.popToast(MyApplication.getInstance(), "退出登录失败！", false);
                    break;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        //领创管控初始化 动态注册广播
        //推出登录
      //  Toast.makeText(context,"我是收到了LOGOUT的广播推出登录",Toast.LENGTH_LONG).show();

        //EventBus.getDefault().post("logout", Constant.EVENT_LOGOUT_APP);

        loginOut();
    }

    /**
     * OauthMy/TestActivity.java中的登出
     */
    public void loginOut() {
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
