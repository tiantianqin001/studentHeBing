package com.telit.zhkt_three.Utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/2/27 9:23
 * 权限动态申请针对安卓6.0（api23）及以上，用户在安装时不会要求授权权限，所以需要动态授权
 * 安卓5.1.1（api22）及以下，都是在用户安装时提示用户授权所需权限的
 * <p>
 * tips:动态权限申请，第一次弹出提示框时不会显示‘never ask again’的勾选框，在用户第一次拒绝后下一次会显示
 * <p>
 * 注意对应的Activity需要
 * onRequestPermissionsResult【用于权限申请后的结果反馈】和onActivityResult【从设置界面跳回的结果反馈】方法
 * <p>
 * <p>
 * <p>
 * // this指代Activity
 * //0、权限数组集合
 * private static final String[] needPermissions = {Manifest.permission.CAMERA,
 * Manifest.permission.READ_EXTERNAL_STORAGE,
 * Manifest.permission.WRITE_EXTERNAL_STORAGE};
 * //1、申请即将用到的权限
 * ZBVPermission.getInstance().setPermPassResult(this);
 * //2、请求权限必须在设置接口回调后面
 * ZBVPermission.getInstance().requestPermissions(this,needPermissions);
 * //3、在对应的Activity中重载onRequestPermissionsResult和onActivityResult方法在示例如下：
 * <code>
 *
 * @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
 * super.onActivityResult(requestCode, resultCode, data);
 * ZBVPermission.getInstance().onActivityResult(requestCode, resultCode, data);
 * }
 * @Override public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
 * super.onRequestPermissionsResult(requestCode, permissions, grantResults);
 * ZBVPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
 * }
 * </code>
 * //4、如果需要判断是否存在所需的权限
 * ZBVPermission.getInstance().hadPermissions(this, needPermissions)
 * </p>
 * <p>
 * //放置泄露最后调用
 * ZBVPermission.getInstance().recyclerAll();
 */
public class ZBVPermission {
    public static ZBVPermission zbvPermission = null;

    public static final int PERMISSION_REQUEST_CODE = 0x7;
    public static final int ACTIVITY_REQUEST_CODE = 0x9;

    private Activity activity;

    private ZBVPermission() {

    }

    public static ZBVPermission getInstance() {
        if (zbvPermission == null) {
            synchronized (ZBVPermission.class) {
                if (zbvPermission == null) {
                    zbvPermission = new ZBVPermission();
                }
            }
        }
        return zbvPermission;
    }

    public void recyclerAll() {
        if (permPassResult != null) {
            permPassResult = null;
        }
        if (activity != null) {
            activity = null;
        }
    }

    //单独使用
    public boolean hadPermissions(Activity activity, String... appliedPerms) {
        if (activity == null || appliedPerms == null) {
            return false;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> deniedPermission = new ArrayList<>();
            for (String permission : appliedPerms) {
                int checkPermResult = ActivityCompat.checkSelfPermission(activity, permission);
                if (checkPermResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }
            QZXTools.logD("被否定的权限个数：" + deniedPermission.size());
            if (deniedPermission.size() > 0) {
                return false;
            } else {
                return true;
            }
        } else {
            return true;
        }
    }

    public void requestPermissions(Activity activity, String... appliedPerms) {
        if (appliedPerms == null) {
            return;
        }

        this.activity = activity;
        /**
         * 安卓6.0以上需要判断是否属于危险权限
         * Calendar、Camera、Contacts、Location、Microphone、Phone、Sensor、SMS、Storage
         * 九大权限组，申请一个权限，那么隶属该组的其他权限也自动默许了
         * */
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> deniedPermission = new ArrayList<>();
            for (String permission : appliedPerms) {
                int checkPermResult = ActivityCompat.checkSelfPermission(activity, permission);
                if (checkPermResult != PackageManager.PERMISSION_GRANTED) {
                    deniedPermission.add(permission);
                }
            }

            QZXTools.logD("拒绝的权限个数为：" + deniedPermission.size());

            if (deniedPermission.size() > 0) {
                //链表转化成对应类型的数组
                String[] demiedPermissionStrs = new String[deniedPermission.size()];
                //一开始逐一请求权限
                ActivityCompat.requestPermissions(activity, deniedPermission.toArray(demiedPermissionStrs), PERMISSION_REQUEST_CODE);
            } else {
                //申请的权限被通过
                if (permPassResult != null)
                    permPassResult.grantPermission();
            }

        } else {
            QZXTools.logD("低于安卓6.0不需要申请权限");
            //低于安卓6.0在清单文件中申请即可获取到相应的权限
            if (permPassResult != null)
                permPassResult.grantPermission();
        }
    }

    //--------------------onActivityResult
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == ACTIVITY_REQUEST_CODE) {
            if (hadPermissions(activity)) {
                QZXTools.logD("设置界面授予了所有权限");
                if (permPassResult != null)
                    permPassResult.grantPermission();
            } else {
                QZXTools.logD("设置界面没有授予全部所需要的权限");
                if (permPassResult != null)
                    permPassResult.denyPermission();
            }
        }
    }

    //---------------------onRequestPermissionsResult
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PERMISSION_REQUEST_CODE) {

            List<String> rejectTempPerms = new ArrayList<>();
            List<String> rejectWholePerms = new ArrayList<>();

            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //如果用户请求过此权限并且拒绝了请求返回true，勾选了‘Never ask again’返回false,一开始的就是true
                    boolean flag = ActivityCompat.shouldShowRequestPermissionRationale(activity, permissions[i]);
                    QZXTools.logD("flag=" + flag + ";grantResults[i]=" + grantResults[i]);
                    if (flag) {
                        //用户拒绝了且没有勾选“dont ask again”，flag就是true
                        rejectTempPerms.add(permissions[i]);
                        QZXTools.logD("没有勾选 permission=" + permissions[i]);
                    } else {
                        //第一次勾选'Never ask again'并且拒绝返回false，之后不显示系统授权框且默认拒绝
                        //用户拒绝了并且勾选“dont ask again”
                        //因为勾选了以后不要再弹框，所以需要引导用户去设置界面开启权限
                        rejectWholePerms.add(permissions[i]);
                        QZXTools.logD("勾选 permission=" + permissions[i]);
                    }
                }
            }
            if (rejectTempPerms.size() > 0) {
                if (permPassResult != null)
                    permPassResult.denyPermission();
            } else if (rejectWholePerms.size() > 0) {
                popupTips(rejectWholePerms);
            } else {
                if (permPassResult != null)
                    permPassResult.grantPermission();
            }
        }
    }

    private void popupTips(List<String> perms) {
        AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("Tips")
                .setMessage("您有未开启的权限，请开始！\n" + transformText(activity, perms))
                .setNegativeButton("不设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (permPassResult != null)
                            permPassResult.denyPermission();
                    }
                })
                .setPositiveButton("设置", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        QZXTools.appDetailInfo(activity, ACTIVITY_REQUEST_CODE);
                    }
                })
                .create();
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }

    /**
     * Turn permissions into text.
     */
    public static List<String> transformText(Context context, List<String> permissions) {
        List<String> textList = new ArrayList<>();
        for (String permission : permissions) {
            switch (permission) {
                case Manifest.permission.READ_CALENDAR:
                case Manifest.permission.WRITE_CALENDAR: {
                    String message = "日历读写";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }

                case Manifest.permission.CAMERA: {
                    String message = "相机";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_CONTACTS:
                case Manifest.permission.WRITE_CONTACTS: {
                    String message = "通讯录";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.GET_ACCOUNTS: {
                    String message = "手机账号";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.ACCESS_FINE_LOCATION:
                case Manifest.permission.ACCESS_COARSE_LOCATION: {
                    String message = "位置信息";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.RECORD_AUDIO: {
                    String message = "麦克风录音";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_PHONE_STATE:
                case Manifest.permission.CALL_PHONE:
                case Manifest.permission.READ_CALL_LOG:
                case Manifest.permission.WRITE_CALL_LOG:
                case Manifest.permission.ADD_VOICEMAIL:
                case Manifest.permission.USE_SIP:
                case Manifest.permission.PROCESS_OUTGOING_CALLS:
                case Manifest.permission.READ_PHONE_NUMBERS:
                case Manifest.permission.ANSWER_PHONE_CALLS: {
                    String message = "电话";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.BODY_SENSORS: {
                    String message = "身体传感器";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.SEND_SMS:
                case Manifest.permission.RECEIVE_SMS:
                case Manifest.permission.READ_SMS:
                case Manifest.permission.RECEIVE_WAP_PUSH:
                case Manifest.permission.RECEIVE_MMS: {
                    String message = "短信";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
                case Manifest.permission.READ_EXTERNAL_STORAGE:
                case Manifest.permission.WRITE_EXTERNAL_STORAGE: {
                    String message = "外部存储读写";
                    if (!textList.contains(message)) {
                        textList.add(message);
                    }
                    break;
                }
            }
        }
        return textList;
    }

    private PermPassResult permPassResult;

    public PermPassResult getPermPassResult() {
        return permPassResult;
    }

    public void setPermPassResult(PermPassResult permPassResult) {
        this.permPassResult = permPassResult;
    }

    //权限申请的结果
    public interface PermPassResult {
        void grantPermission();

        void denyPermission();
    }

}
