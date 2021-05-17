package com.telit.zhkt_three.receiver;

import android.content.Context;
import android.content.Intent;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.HomeWork.HomeWorkDetailActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.MistakesCollectionActivity;
import com.telit.zhkt_three.Activity.OauthMy.ProviceActivity;
import com.telit.zhkt_three.JavaBean.Gson.JpushExtrasInfo;
import com.telit.zhkt_three.Utils.Jpush.TagAliasOperatorHelper;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

/**
 * author: qzx
 * Date: 2019/5/22 9:19
 * <p>
 * 使用新版的tags/alias接收的话，老版的就不再接收到消息
 */
public class PushMessageReceiver extends JPushMessageReceiver {
    public static final String OFFLINE = "1001";//被迫下线
    public static final String HOMEWORK_PUBLISH = "1002";//作业发布
    public static final String HOMEWORK_RUSH = "1003";//作业催缴
    public static final String NOTIFICATION_PUSH = "1004";//通知推送
    public static final String PRIVATE_LETTER = "1005";//私信

    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        // CustomMessage{messageId='2251858181088123', extra='', message='{"operation":"jumpMistakeCollection"}',
        // contentType='', title='', senderId='ee725b9ec7f56fd88a66740a', appId='com.telit.smartclass.desktop'}
        QZXTools.logE("[onMessage] " + customMessage, null);
        String message = customMessage.message;
        try {
            JSONObject jsonObject = new JSONObject(message);
            String operation = jsonObject.getString("operation");
            if (operation != null) {
                if (operation.equals("jumpMistakeCollection")) {
                    //进入错题集界面
                    Intent intent = new Intent(context, MistakesCollectionActivity.class);
                    //必须加上这条属性
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            QZXTools.logE("customMessage json parse exception", e);
        }
    }
    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage message) {
        QZXTools.logE("[onNotifyMessageOpened] " + message, null);
        String extrasInfo = message.notificationExtras;
        Gson gson = new Gson();
        JpushExtrasInfo jpushExtrasInfo = gson.fromJson(extrasInfo, JpushExtrasInfo.class);
        if (jpushExtrasInfo.getWarn() == null || jpushExtrasInfo.getWarn().equals("")) {
            QZXTools.logE("onNotifyMessageOpened warn is null or empty but extral = " + jpushExtrasInfo.getExtra1(), null);
            return;
        }
        switch (jpushExtrasInfo.getWarn()) {
            case HOMEWORK_PUBLISH:
            case HOMEWORK_RUSH:
                QZXTools.logE("作业id：" + jpushExtrasInfo.getResult(), null);
                QZXTools.logE("byHand：" + jpushExtrasInfo.getByhand(), null);
                //进入作业详情界面
                Intent intent = new Intent(context, HomeWorkDetailActivity.class);
                intent.putExtra("homeworkId", jpushExtrasInfo.getResult());
                intent.putExtra("status", "0");
                intent.putExtra("byHand", jpushExtrasInfo.getByhand());//1-byHand
                //必须加上这条属性
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
                break;
            case NOTIFICATION_PUSH:
                QZXTools.logE("通知id：" + jpushExtrasInfo.getExtra1(), null);
                break;
            case PRIVATE_LETTER:
                QZXTools.logE("私信：" + jpushExtrasInfo.getExtra1(), null);
                break;
        }
    }

    /**
     * {notificationId=459735870, msgId='9007208030346991', appkey='2e78927fe4ddb3692628a55b', notificationContent='你有新的作业！',
     * notificationAlertType=-1,notificationTitle='zhkt_three', notificationSmallIcon='', notificationLargeIcon='',
     * notificationExtras='{"extra1":"1","result":"2a7f455741ac4a0ab1ede27a88d3b8ec","warn":"1002"}',
     * notificationStyle=0, notificationBuilderId=0, notificationBigText='', notificationBigPicPath='', notificationInbox='',
     * notificationPriority=0, notificationCategory='',developerArg0='developerArg0', platform=0, notificationType=0}
     */

    //这个手机极光真正在的入口
    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage message) {
        QZXTools.logE("onNotifyMessageArrived" + message, null);

        if (!UserUtils.isLoginIn()) {
            QZXTools.logE("该用户还没有登录...", null);
            context.startActivity(new Intent(context, ProviceActivity.class));
        }

        String extrasInfo = message.notificationExtras;
        Gson gson = new Gson();
        JpushExtrasInfo jpushExtrasInfo = gson.fromJson(extrasInfo, JpushExtrasInfo.class);
        if (jpushExtrasInfo.getWarn() == null || jpushExtrasInfo.getWarn().equals("")) {
            QZXTools.logE("onNotifyMessageArrived warn is null or empty but extral = " + jpushExtrasInfo.getExtra1(), null);
            return;
        }
        if (jpushExtrasInfo.getWarn().equals(OFFLINE)) {
            /*//如果是被迫下线
            QZXTools.popCommonToast(context, context.getResources().getString(R.string.offline_tips), true);

            Intent intent = new Intent(context, OffLineWarningActivity.class);
            //必须加上这条属性
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);*/

            //因为Context无法弹出DialogFragment 所以无法使用TipsDialog

            //Android6.0以上默认悬浮窗是关闭的 WindowManager.LayoutParams.TYPE_SYSTEM_ALERT，所这个也不用
//            AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle("警告").setCancelable(false)
//                    .setMessage("您的账号已在另一台平板上登录，您已被迫下线！").setNegativeButton("确定", (dialog, which) -> {
//                        //------------------处理下线操作-------------------
//                        //第一步登录标记清除以及认证ID清除以及tgt
//                        SharedPreferences sp_student = context.getSharedPreferences("student_info", Context.MODE_PRIVATE);
//                        UserUtils.setBooleanTypeSpInfo(sp_student, "isLoginIn", false);
//                        UserUtils.setOauthId(sp_student, "oauth_id", "");
//                        UserUtils.removeTgt();
//                        //第二步解除极光推送
//                        JpushApply.getIntance().unRegistJpush(context);
//                        //第三步如果处于互动状态的话，还要清除互动Netty---这一步在Activity销毁时已实现
//                        AppManager.getAppManager().finishAllActivity();
//                        //------------------处理下线操作-------------------
//                    });
//            AlertDialog alertDialog = builder.create();
//            alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//            alertDialog.show();
        }
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage message) {
        QZXTools.logE("[onNotifyMessageDismiss] " + message, null);
    }

    @Override
    public void onRegister(Context context, String registrationId) {
        QZXTools.logE("[onRegister] " + registrationId, null);
    }

    /**
     * 不是指 Android 系统的网络连接状态
     */
    @Override
    public void onConnected(Context context, boolean isConnected) {
        QZXTools.logE("[onConnected] " + isConnected, null);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        QZXTools.logE("[onCommandResult] " + cmdMessage, null);
    }

    @Override
    public void onTagOperatorResult(Context context, JPushMessage jPushMessage) {
        QZXTools.logE("[onTagOperatorResult] " + jPushMessage, null);
        TagAliasOperatorHelper.getInstance().onTagOperatorResult(context, jPushMessage);
    }

    @Override
    public void onCheckTagOperatorResult(Context context, JPushMessage jPushMessage) {
        QZXTools.logE("[onCheckTagOperatorResult] " + jPushMessage, null);
        TagAliasOperatorHelper.getInstance().onCheckTagOperatorResult(context, jPushMessage);
    }

    /**
     * JPushMessage{alias='', tags=null, checkTag='null', errorCode=0, tagCheckStateResult=false,
     * isTagCheckOperator=false, sequence=2, mobileNumber=null}
     */
    @Override
    public void onAliasOperatorResult(Context context, JPushMessage jPushMessage) {
        QZXTools.logE("[onAliasOperatorResult] " + jPushMessage, null);
        TagAliasOperatorHelper.getInstance().onAliasOperatorResult(context, jPushMessage);
    }

    /**
     * 手机标记结果反馈，目前不需要
     */
    @Override
    public void onMobileNumberOperatorResult(Context context, JPushMessage jPushMessage) {
        QZXTools.logE("[onMobileNumberOperatorResult] " + jPushMessage, null);
    }
}
