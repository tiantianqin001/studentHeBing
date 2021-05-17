package com.zbv.basemodel;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import static android.content.ContentValues.TAG;

/**
 * author: qzx
 * Date: 2019/5/15 19:57
 * <p>
 * Dialog的布局必须要布局下的每个孩子都是宽度Match_parent,这样才能保证显示还不错
 * <p>
 * 文件接收一律放入/storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/SaveFile/下
 * <p>
 * 按返回键加载apk界面不能消失
 */
public class FileReceiveDialog extends DialogFragment {

    private static final String TAG = "FileReceiveDialog";

    private TextView receive_title;
    private ApkInstallProgressView receive_progressBar;

    private String localFilePath;

    public static final int INSTALL_PACKAGES_REQUEST_CODE = 0x7;
    public static final int GET_UNKNOWN_APP_SOURCES = 0x8;

    /**
     * 更新App的下载地址
     */
    private String downloadUrl;

    private Activity mContext;

    public void setFileBodyString(String downloadUrl, Activity context) {
        this.downloadUrl = downloadUrl;
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwdTwo);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_apk_install_layout, container, false);

        receive_title = view.findViewById(R.id.file_receive_title);
        receive_progressBar = view.findViewById(R.id.apk_receive_progress);



        if (downloadUrl != null) {
            receive_title.setText("下载新版本");

            OkHttp3_0Utils.getInstance(mContext).downloadSingleFileForOnce(downloadUrl, null,
                    new OkHttp3_0Utils.DownloadCallback() {
                        @Override
                        public void downloadProcess(int value) {
                            receive_progressBar.setCurProgress(value);
                        }

                        @Override
                        public void downloadComplete(String filePath) {

                            localFilePath = filePath;

                            SharedPreferences sharedPreferences = mContext
                                    .getSharedPreferences("access_mode", Context.MODE_PRIVATE);
                            boolean hadAccess = sharedPreferences.getBoolean("had_access", false);

                            Log.i(TAG, "downloadComplete: "+hadAccess);
                            if (hadAccess) {
                                AutoUpdateAccessService.INVOKE_TYPE = AutoUpdateAccessService.TYPE_INSTALL_APP;

                            } else {
                                AutoUpdateAccessService.reset();
                            }

                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                boolean b = getActivity().getPackageManager().canRequestPackageInstalls();
                                if (b) {
                                    QZXTools.installApk(getActivity(), filePath);
                                } else {
                                    //请求安装未知应用来源的权限
                                    ActivityCompat.requestPermissions((Activity) mContext,
                                            new String[]{Manifest.permission.REQUEST_INSTALL_PACKAGES},
                                            INSTALL_PACKAGES_REQUEST_CODE);
                                }
                            } else {
                                QZXTools.installApk(getActivity(), filePath);
                            }

                            dismiss();
                        }

                        @Override
                        public void downloadFailure() {
                            Toast.makeText(mContext, "apk包下载失败！", Toast.LENGTH_SHORT).show();
                            dismiss();
                        }
                    });
        }
        return view;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case INSTALL_PACKAGES_REQUEST_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    QZXTools.logE("FileReceiveDialog onRequestPermissionsResult 获取到安装权限", null);
                    QZXTools.installApk(mContext, localFilePath);
                } else {
                    QZXTools.logE("FileReceiveDialog onRequestPermissionsResult 引导用户手动开启安装权限", null);
                    //  引导用户手动开启安装权限
                    Intent intent = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES);
                    this.startActivityForResult(intent, GET_UNKNOWN_APP_SOURCES);
                }
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_UNKNOWN_APP_SOURCES) {
            QZXTools.logE("FileReceiveDialog onActivityResult GET_UNKNOWN_APP_SOURCES", null);
            if (Build.VERSION.SDK_INT >= 26) {
                boolean b = mContext.getPackageManager().canRequestPackageInstalls();
                if (b) {
                    QZXTools.installApk(mContext, localFilePath);
                } else {
                    Toast.makeText(mContext, "您没有授权，无法下载更新包！", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


    @Override
    public void onResume() {
        super.onResume();
        //在Dialog生成后使用
        setBackNoEffect();
    }


    /**
     * 返回键不消失
     */
    private void setBackNoEffect() {
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }


    /**
     * 检测辅助功能是否开启<br>
     * 方 法 名：isAccessibilitySettingsOn <br>
     * 创 建 人 <br>
     * 创建时间：2016-6-22 下午2:29:24 <br>
     * 修 改 人： <br>
     * 修改日期： <br>
     *
     * @param mContext
     * @return boolean
     */
    private boolean isAccessibilitySettingsOn(Context mContext) {
        int accessibilityEnabled = 0;
        final String service = mContext.getPackageName() + "/" + AutoUpdateAccessService.class.getCanonicalName();

        Log.e(TAG, "service name=" + service);

        try {
            accessibilityEnabled = Settings.Secure.getInt(mContext.getApplicationContext().getContentResolver(),
                    android.provider.Settings.Secure.ACCESSIBILITY_ENABLED);
            Log.v(TAG, "accessibilityEnabled = " + accessibilityEnabled);
        } catch (Settings.SettingNotFoundException e) {
            Log.e(TAG, "Error finding setting, default accessibility to not found: " + e.getMessage());
        }

        TextUtils.SimpleStringSplitter mStringColonSplitter = new TextUtils.SimpleStringSplitter(':');

        if (accessibilityEnabled == 1) {
            Log.v(TAG, "***ACCESSIBILITY IS ENABLED*** -----------------");
            String settingValue = Settings.Secure.getString(mContext.getApplicationContext().getContentResolver(),
                    Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES);
            if (settingValue != null) {
                mStringColonSplitter.setString(settingValue);
                while (mStringColonSplitter.hasNext()) {
                    String accessibilityService = mStringColonSplitter.next();

                    Log.v(TAG, "-------------- > accessibilityService :: " + accessibilityService + " " + service);
                    if (accessibilityService.equalsIgnoreCase(service)) {
                        Log.v(TAG, "We've found the correct setting - accessibility is switched on!");
                        return true;
                    }
                }
            }
        } else {
            Log.v(TAG, "***ACCESSIBILITY IS DISABLED***");
        }
        return false;
    }


    @Override
    public void show(FragmentManager manager, String tag) {
        try {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, tag);
            ft.commitAllowingStateLoss();
        } catch (IllegalStateException e) {
            Log.d("ABSDIALOGFRAG", "Exception", e);
        }
    }
}
