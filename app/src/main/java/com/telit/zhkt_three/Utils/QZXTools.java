package com.telit.zhkt_three.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ActivityNotFoundException;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.ColorInt;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.telit.zhkt_three.MyApplication;

import net.sourceforge.pinyin4j.PinyinHelper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;

/**
 * Created by Administrator on 2018/5/16.
 * qzx
 * 工具类
 */

public class QZXTools {

    /**
     private AppInfo getAppInfoFromPackageName(String packageName) {
     AppInfo appInfo = new AppInfo();
     AppInfo appInfo = new AppInfo();
     PackageManager pm = getPackageManager();
     try {
     PackageInfo packageInfo = pm.getPackageInfo(packageName, 0);
     if ((packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
     appInfo.setIsSystemApp(true);
     } else {
     appInfo.setIsSystemApp(false);
     }
     appInfo.setName(packageInfo.applicationInfo.loadLabel(pm).toString());
     appInfo.setPackageName(packageName);
     } catch (PackageManager.NameNotFoundException e) {
     e.printStackTrace();
     }
     return appInfo;
     }

     private List<AppInfo> getAllApp() {
     List<AppInfo> appInfoList = new ArrayList<>();
     PackageManager pm = getPackageManager();
     Intent intent = new Intent();
     intent.setAction(Intent.ACTION_MAIN);
     intent.addCategory(Intent.CATEGORY_LAUNCHER);
     List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent, 0);
     for (int i = 0; i < resolveInfos.size(); i++) {
     ResolveInfo resolveInfo = resolveInfos.get(i);
     String packageName = resolveInfo.activityInfo.packageName;
     if (packageName.equals(getPackageName())) {
     //是本应用剔除
     continue;
     }

     AppInfo appInfo = new AppInfo();

     if ((resolveInfo.activityInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) > 0) {
     //说明是系统应用
     appInfo.setIsSystemApp(true);
     //使用resolveInfo.activityInfo.applicationInfo.name获取的是null
     //                QZXTools.logD("System AppInfo=" + appInfo + ";class name=" + resolveInfo.activityInfo.name);
     } else {
     appInfo.setIsSystemApp(false);
     }
     appInfo.setName(resolveInfo.activityInfo.applicationInfo.loadLabel(pm).toString());
     appInfo.setPackageName(resolveInfo.activityInfo.packageName);
     appInfo.setOrderNum(i);
     //使用resolveInfo.activityInfo.applicationInfo.name获取的是null
     //            QZXTools.logD("Third AppInfo=" + appInfo + ";class name=" + resolveInfo.activityInfo.name);
     appInfoList.add(appInfo);

     //插入GreenDao数据库
     AppInfoDao appInfoDao = MyApplication.getInstance().getDaoSession().getAppInfoDao();
     appInfoDao.insertOrReplaceInTx(appInfoList);
     }
     return appInfoList;
     }*/

    /**
     * 双击退出应用示例----Activity
     * <p>
     * //    private int clickCount = 0;
     * //    private long firstTime;
     * //
     * //    //双击退出
     * //    @Override
     * //    public void onBackPressed() {
     * //        clickCount++;//从一开始
     * //        if (clickCount == 2 && (SystemClock.elapsedRealtime() - firstTime) < 2000) {
     * //            super.onBackPressed();
     * //        } else {
     * //            clickCount = 1;
     * //            firstTime = SystemClock.elapsedRealtime();
     * //            QZXTools.popToast(this, "再点击一次退出应用", false);
     * //        }
     * //    }
     */


    /**
     * 隐藏虚拟按键，并且全屏
     * <p>
     * protected void hideBottomUIMenu() {
     * //隐藏虚拟按键，并且全屏
     * if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
     * View v = this.getWindow().getDecorView();
     * v.setSystemUiVisibility(View.GONE);
     * } else if (Build.VERSION.SDK_INT >= 19) {
     * //for new api versions.
     * View decorView = getWindow().getDecorView();
     * int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
     * | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
     * decorView.setSystemUiVisibility(uiOptions);
     * }
     * }
     */

    /**
     * 判断导航栏的存在与否
     * <p>
     * 如果存在导航栏，那么设置底部的padding
     */
//    boolean isExistNavigation = checkExistNavigationBar(this);
//        logE("isExistNavigation=" + isExistNavigation
//                + ";density=" + getResources().getDisplayMetrics().densityDpi, null);
//
//        if (isExistNavigation) {
//        int navigationHeight = getNavigationHeight(this);
//        logE("navigationHeight=" + navigationHeight, null);
//        mainLayout.setPadding(0, 0, 0, navigationHeight);
//    }
//
//    //获取是否存在NavigationBar,EasyUi中存在一个不一样的判断方式可供参考
//    private boolean checkExistNavigationBar(Context context) {
//        boolean hasNavigationBar = false;
//        Resources rs = context.getResources();
//        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
//        if (id > 0) {
//            hasNavigationBar = rs.getBoolean(id);
//        }
//        try {
//            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
//            Method m = systemPropertiesClass.getMethod("get", String.class);
//            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
//            if ("1".equals(navBarOverride)) {
//                hasNavigationBar = false;
//            } else if ("0".equals(navBarOverride)) {
//                hasNavigationBar = true;
//            }
//        } catch (Exception e) {
//
//        }
//        return hasNavigationBar;
//    }


    //----------------------------Android防止多次快速的点击---------------------------
    private static long firstTime;
    private static boolean isStart = false;

    /**
     * 防止过快的多次点击
     */
    public static boolean canClick() {
        long curTime = System.currentTimeMillis();
        if (curTime - firstTime >= 1000) {
            isStart = true;
        }

        if (isStart) {
            isStart = false;
            firstTime = System.currentTimeMillis();
            return true;
        }

        return false;
    }


    //-----------------------------序列化对象--------------------------

    /**
     * @describe 序列化对象
     * @author luxun
     * create at 2017/4/12 0012 10:53
     */
    public static void saveObject(File file, Serializable value) {
        ByteArrayOutputStream baos = null;
        ObjectOutputStream oos = null;
        FileOutputStream out = null;
        try {
            baos = new ByteArrayOutputStream();
            oos = new ObjectOutputStream(baos);
            out = new FileOutputStream(file);
            oos.writeObject(value);
            out.write(baos.toByteArray());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                try {
                    out.close();
                    oos.close();
                    baos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    public static Object readObject(File file) {
        if (file == null || !file.exists() || file.length() <= 0) {
            return null;
        }
        FileInputStream fis = null;
        ObjectInputStream ois = null;
        try {
            fis = new FileInputStream(file);
            ois = new ObjectInputStream(fis);
            Object reObject = ois.readObject();
            return reObject;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (ois != null)
                    ois.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    /**
     * 将一个InputStream流转换成字符串
     *
     * @param is
     * @return
     */
    public static String toConvertString(InputStream is) throws UnsupportedEncodingException {
        StringBuffer res = new StringBuffer();
        InputStreamReader isr = new InputStreamReader(is, "utf-8");
        BufferedReader read = new BufferedReader(isr);
        String sTempOneLine;
        try {
            while ((sTempOneLine = read.readLine()) != null) {
                res.append(sTempOneLine);
            }
//            String line;
//            line = read.readLine();
//            while (line != null) {
//                res.append(line);
//                line = read.readLine();
//            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (null != isr) {
                    isr.close();
                    isr.close();
                }
                if (null != read) {
                    read.close();
                    read = null;
                }
                if (null != is) {
                    is.close();
                    is = null;
                }
            } catch (IOException e) {
            }
        }
        return res.toString();
    }

    //-----------------------------序列化对象--------------------------

    /**
     * 打印设备信息
     */
    public static void logDeviceInfo(Context context) {
        logE("densityDpi=" + context.getResources().getDisplayMetrics().densityDpi
                + ";widthpixel=" + context.getResources().getDisplayMetrics().widthPixels
                + ";heightpixel=" + context.getResources().getDisplayMetrics().heightPixels
                + ";scaleDensity=" + context.getResources().getDisplayMetrics().scaledDensity, null);
    }

    /**
     * 判断当前设备是手机还是平板，代码来自 Google I/O App for Android
     *
     * @param context
     * @return 平板返回 True，手机返回 False
     * <p>
     * 或者通过计算设备尺寸大于6寸
     */
    public static boolean isPad(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK)
                >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * 依据包名获取App的ICON
     */
    public static Drawable getIconFromPackageName(Context context, String packageName) {
        Drawable drawable = null;
        try {
            drawable = context.getPackageManager().getApplicationIcon(packageName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return drawable;
    }

    /**
     * 得到启动意图依据包名
     */
    public static Intent getLauncherIntent(String pkgName) {
        if (TextUtils.isEmpty(pkgName)) {
            return null;
        }
        return MyApplication.getInstance().getApplicationContext()
                .getPackageManager().getLaunchIntentForPackage(pkgName);
    }

    /**
     * 获取虚拟功能键高度
     */
    public static int getVirtualBarHeigh(Context context) {
        int vh = 0;
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        DisplayMetrics dm = new DisplayMetrics();
        try {
            @SuppressWarnings("rawtypes")
            Class c = Class.forName("android.view.Display");
            @SuppressWarnings("unchecked")
            Method method = c.getMethod("getRealMetrics", DisplayMetrics.class);
            method.invoke(display, dm);
            vh = dm.heightPixels - windowManager.getDefaultDisplay().getHeight();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vh;
    }

    //------------------------Android Setting
    //系统的应用详情页面
    public static void appDetailInfo(Activity activity, int RequestCode) {
        Intent localIntent = new Intent();
        //另起一个堆栈会导致onActivityResuolt立即返回的
//        localIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= 9) {
            localIntent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
            localIntent.setData(Uri.fromParts("package", activity.getPackageName(), null));
        } else if (Build.VERSION.SDK_INT <= 8) {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName("com.android.settings", "com.android.settings.InstalledAppDetails");
            localIntent.putExtra("com.android.settings.ApplicationPkgName", activity.getPackageName());
        }
        activity.startActivityForResult(localIntent, RequestCode);
    }

    public static Intent defaultApi(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", context.getPackageName(), null));
        return intent;
    }

    //---------------------------各手机的权限管理界面
    public static Intent huaweiApi(Context context) {
        Intent intent = new Intent();
        intent.setClassName("com.huawei.systemmanager", "com.huawei.permissionmanager.ui.MainActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    public static Intent xiaomiApi(Context context) {
        Intent intent = new Intent("miui.intent.action.APP_PERM_EDITOR");
        intent.putExtra("extra_pkgname", context.getPackageName());
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.AppPermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.miui.securitycenter", "com.miui.permcenter.permissions.PermissionsEditorActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    public static Intent vivoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packagename", context.getPackageName());
        intent.setClassName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.iqoo.secure", "com.iqoo.secure.safeguard.SoftPermissionDetailActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    private static Intent oppoApi(Context context) {
        Intent intent = new Intent();
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.color.safecenter", "com.color.safecenter.permission.PermissionManagerActivity");
        if (hasActivity(context, intent)) return intent;

        intent.setClassName("com.oppo.safe", "com.oppo.safe.permission.PermissionAppListActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }

    public static Intent meizuApi(Context context) {
        Intent intent = new Intent("com.meizu.safe.security.SHOW_APPSEC");
        intent.putExtra("packageName", context.getPackageName());
        intent.setClassName("com.meizu.safe", "com.meizu.safe.security.AppSecActivity");
        if (hasActivity(context, intent)) return intent;

        return defaultApi(context);
    }
    //---------------------------各手机的权限管理界面

    /**
     * 判断是否存在可以执行intent的Activity
     */
    public static boolean hasActivity(Context context, Intent intent) {
        PackageManager packageManager = context.getPackageManager();
        return packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY).size() > 0;
    }

    /**
     * 进入带“完成”的WIFI设置界面
     */
    public static void enterWifiSetting(Context context) {
        /**
         //是否显示button bar,传递值为true的话是显示
         private static final String EXTRA_PREFS_SHOW_BUTTON_BAR = "extra_prefs_show_button_bar";
         //自定义按钮的名字，不传递的话，默认为下一步
         private static final String EXTRA_PREFS_SET_NEXT_TEXT = "extra_prefs_set_next_text";
         //自定义按钮的名字，不传递的话，默认为上一步
         private static final String EXTRA_PREFS_SET_BACK_TEXT = "extra_prefs_set_back_text";
         //是否打开网络连接检测功能（如果连上wifi，则下一步按钮可被点击）
         private static final String EXTRA_ENABLE_NEXT_ON_CONNECT = "wifi_enable_next_on_connect";
         * */
        Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        intent.putExtra("extra_prefs_show_button_bar", true);
        intent.putExtra("extra_prefs_set_next_text", "完成");
        intent.putExtra("extra_prefs_set_back_text", "");
        intent.putExtra("wifi_enable_next_on_connect", true);
        context.startActivity(intent);


        //todo  如果是管控就用这个设置
      /*  Intent intent = new Intent();
        intent.setClassName("com.hat.settings", "com.hat.settings.SettingsActivity");
        context.startActivity(intent);*/
    }

    //------------------------Android Setting

    //---------------------------Toast----------------------

    private static Field sField_TN;
    private static Field sField_TN_Handler;

    static {
        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            try {
                sField_TN = Toast.class.getDeclaredField("mTN");
                sField_TN.setAccessible(true);

                sField_TN_Handler = sField_TN.getType().getDeclaredField("mHandler");
                sField_TN_Handler.setAccessible(true);
            } catch (Exception e) {
            }
        }
    }

    private static void hook(Toast toast) {
        try {
            Object tn = sField_TN.get(toast);
            Handler preHandler = (Handler) sField_TN_Handler.get(tn);
            sField_TN_Handler.set(tn, new SafelyHandlerWarpper(preHandler));
        } catch (Exception e) {
        }
    }

    private static class SafelyHandlerWarpper extends Handler {

        private Handler impl;

        public SafelyHandlerWarpper(Handler impl) {
            this.impl = impl;
        }

        @Override
        public void handleMessage(Message msg) {
            try {
                impl.handleMessage(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private static Toast mToast;

    /**
     * 置空防止静态变量持有Context泄露
     */
    public static void setmToastNull() {
        mToast = null;
    }

    /**
     * 静态变量toast,记得最终释放，置空
     */
    public static void popToast(Context context, String content, boolean isShowLong) {
        //这个方式会有内存泄露，因为mToast是静态变量，会永久携带context，还有一种方式是：单例模式
//        if (mToast == null) {
//            mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
//        }

        //防止界面退出，请求抵达后弹出吐司Context.getResources()为空
        if (context == null) {
            return;
        }

        //现修改为一般的吐司模式
        Toast mToast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        if (isShowLong) {
            //7000ms
            mToast.setDuration(Toast.LENGTH_LONG);
        } else {
            //4000ms
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
//        mToast.setText(content);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(mToast);
        }

        mToast.show();
    }

    /**
     * 弱引用的方式
     */
    public static void popToastTwo(Context context, String content, boolean isShowLong) {
        //弱引用
        WeakReference<Context> weakContext = new WeakReference<Context>(context);
        if (mToast == null) {
            mToast = Toast.makeText(weakContext.get(), content, Toast.LENGTH_SHORT);
        }
        if (isShowLong) {
            //7000ms
            mToast.setDuration(Toast.LENGTH_LONG);
        } else {
            //4000ms
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.setText(content);

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(mToast);
        }

        mToast.show();
    }

    public static void popCommonToast(Context context, String content, boolean isShowLong) {
        Toast toast = null;
        if (isShowLong) {
            //7000ms
            toast = Toast.makeText(context, content, Toast.LENGTH_LONG);
        } else {
            //4000ms
            toast = Toast.makeText(context, content, Toast.LENGTH_SHORT);
        }

        if (Build.VERSION.SDK_INT == Build.VERSION_CODES.N_MR1) {
            hook(toast);
        }

        toast.show();
    }


    /**
     * Snackbar
     * 仅支持一个action
     */
    public static void popSnackbar(View view, String content, boolean isShowLong,
                                   String action, View.OnClickListener onClickListener) {
        Snackbar snackbar = Snackbar.make(view, content, Snackbar.LENGTH_SHORT);
        if (isShowLong) {
            snackbar.setDuration(Snackbar.LENGTH_LONG);
        } else {
            snackbar.setDuration(Snackbar.LENGTH_SHORT);
        }

        if (action != null && !action.isEmpty()) {
            snackbar.setAction(action, onClickListener);
        }
        snackbar.show();
    }

    //---------------------------Toast----------------------

    //这个工具类的公用日志标签
    private static final String TAG = "zbv";

    //--------------------------DIMENSION---------------------

    public static int px2dp(Context context, int px) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (px / scale + 0.5f);
    }

    public static int dp2px(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int dp2px(Context context, float dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + 0.5f);
    }

    public static int sp2px(Context context, int sp) {
        float scaleDensity = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * scaleDensity + 0.5f);
    }
    //--------------------------DIMENSION---------------------

    //------------------------LOGCAT----------------------------
    public static  boolean openLog = true;

    /**
     * 如果传入的Exception不为空就会打印出异常信息，否则置空普通log处理
     */
    public static void logE(String content, Exception e) {
        if (openLog) {
            if (e == null) {
                Log.e(TAG, content);
            } else {
                Log.e(TAG, content, e);
            }
        }
    }

    public static void logD(String content) {
        if (openLog) {
            Log.d(TAG, content);
        }
    }



    public static void logD(String content, Exception e) {
        if (openLog) {
            Log.d(TAG, content);
            if (e == null) {
                Log.i(TAG, content);
            } else {
                Log.i(TAG, content, e);
            }
        }
    }

    public static void logDFromBytes(byte[] bytes, String tips) {
        if (openLog) {
            //转换bytes
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < bytes.length; i++) {
                //一个byte:00000000~11111111
                String single = Integer.toHexString(bytes[i] & 0xFF);
                if (single.length() == 1) {
                    sb.append("0").append(single);
                } else {
                    sb.append(single);
                }
                if (i != (bytes.length - 1))
                    sb.append(" ");
            }
            logD(tips + "=" + sb.toString());
        }
    }
    //------------------------LOGCAT----------------------------

//	//------------------------软键盘的显示和隐藏------------------------
//	//显示软键盘
//	public static void showKeyboard(){
//        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//        inputMethodManager.showSoftInput(editText, 0);
//    }
//
//	//关闭软键盘
//	public static void closeKeyboard(Activity activity) {
//        View view =activity.getWindow().peekDecorView();
//        if (view != null) {
//            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
//        }
//    }
//	//------------------------软键盘的显示和隐藏------------------------

    //------------------------SIGN-------------------------
    public static String getSignInfoFromSelf(Context context) {
        PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> packageInfos = packageManager.getInstalledPackages(PackageManager.GET_SIGNATURES);

        logD("local package name=" + context.getPackageName());

        for (PackageInfo packageInfo : packageInfos) {
            if (packageInfo.packageName.equals(context.getPackageName())) {
                return packageInfo.signatures[0].toCharsString();
            }
        }
        return null;
    }

    public static String getSpecifyPackageName(Context context, String packageName) {
        PackageManager packageManager = context.getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_SIGNATURES);
            return packageInfo.signatures[0].toCharsString();
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /*
     * 此处的type为数值摘要的类型：MD5、SHA1、SHA256
     * */
    public static String getSignMessageDigestValue(byte[] digest, String type) {
        StringBuffer sb = new StringBuffer();
        try {
            //MD5、SHA1、SHA256
            MessageDigest messageDigest = MessageDigest.getInstance("MD5");
            byte[] md5Bytes = messageDigest.digest(digest);

            logDFromBytes(md5Bytes, "md5字节数组");

            //把字节数组转化成十六进制字符串
            for (int i = 0; i < md5Bytes.length; i++) {
                String string = Integer.toHexString(0xFF & md5Bytes[i]);
                if (string.length() == 1) {
                    sb.append("0").append(string);
                } else {
                    sb.append(string);
                }
            }
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    /**
     * 对字符串进行md5加密
     */
    public static String generateCodeFromMD5(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        StringBuffer buffer = new StringBuffer();
        try {
            MessageDigest digest = MessageDigest.getInstance("md5");
            digest.update(url.getBytes());
            byte[] cipher = digest.digest();
            for (byte b : cipher) {
                String hexStr = Integer.toHexString(b & 0xff);
                buffer.append(hexStr.length() == 1 ? "0" + hexStr : hexStr);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

        return buffer.toString();
    }

    //------------------------SIGN-------------------------

    //------------------------FILE-------------------------
    /*
     * 删除一个文件或者文件夹
     * 依据Path删除其子类最底层的文件和空文件夹
     * */
    public static boolean deleteFileOrDirectory(String path) {
        if (path == null) {
            return false;
        }

        boolean isDelSuccess = false;
        File file = new File(path);
        if (file.exists()) {
            logD("当前Path存在=" + path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFileOrDirectory(files[i].getAbsolutePath());
                }
            } else {
                isDelSuccess = file.delete();
            }
        } else {
            logD("不存在Path的文件");
        }

        return isDelSuccess;
    }

    /*
     *删除path目录中的所有文件以及文件夹
     * 思路是：因为delete能删除文件以及空文件夹
     * 那么从下往上删除，当得到要删除的总的path时，一级一级向上递归删除
     *
     * 另外麻烦一点的方法就是：while循环判断Path是否还存在子类，存在再执行删除
     * 例如删除/data/data/xxx包名下的所有文件和文件夹，那么就可以循环判断/data/data/xxx包名下的
     * 子类文件和文件夹是否为0，不是的话就执行一般的递归删除
     * */
    public static void deleteAllFileOrDirectory(String path) {
        File file = new File(path);
        if (file.exists()) {
            logD("当前Path存在=" + path);
            if (file.isDirectory()) {
                File[] files = file.listFiles();
                logD("filesLength=" + files.length + ";files==null=" + (files == null));
                //如果是空文件夹的话
                if (files.length == 0) {
                    String delPath = file.getAbsolutePath();
                    String[] strs = delPath.split("/");
                    file.delete();
                    //获取删除后的最后的path值
                    String subString = delPath.substring(0, delPath.lastIndexOf("/") + 1);
                    logD("subString=" + subString);
                    //再次遍历
                    deleteAllFileOrDirectory(subString);
                }
                //文件夹存在子类
                for (int i = 0; i < files.length; i++) {
                    deleteAllFileOrDirectory(files[i].getAbsolutePath());
                }
            } else {
                //递归的执行者
                String delPath = file.getAbsolutePath();
                String[] strs = delPath.split("/");
                file.delete();
                String subString = delPath.substring(0, delPath.lastIndexOf("/") + 1);
                logD("subString=" + subString);
                deleteAllFileOrDirectory(subString);
            }
        } else {
            logD("不存在Path的文件");
        }
    }

    /**
     * 如果参数 dirctory或者txtName为Null或者空字符串则表示不创建文件或者文件夹
     * <br/><br/>
     * 权限如下：<br/>
     * android.permission.WRITE_EXTERNAL_STORAGE
     * android.permission.READ_EXTERNAL_STORAGE
     * android.permission.MOUNT_UNMOUNT_FILESYSTEMS(创建和删除文件的权限--SDCard)
     */
    public static String createSDCardDirectory(String directory, String txtName) {
        String desPath = null;
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            logD("SDCard已挂载 path=" + Environment.getExternalStorageDirectory());
            String base_path = "/sdcard/" + directory;
            File file = new File(base_path);

            if (!file.exists()) {
                logD("当前不存在");
                boolean success = false;
                if (directory != null && !directory.equals("")) {
                    success = file.mkdirs();
                }

                logD("sdcard---创建文件夹是否成功=" + success);

                if (success) {

                    if (txtName != null && !txtName.equals("")) {
                        desPath = base_path + "/" + txtName;
                    } else {
                        desPath = base_path + "/";
                    }

                } else {
                    if (txtName != null && !txtName.equals("")) {
                        desPath = "/sdcard/" + txtName;
                    } else {
                        desPath = "/sdcard/";
                    }

                }
            } else {
                logD("已存在");
                if (txtName != null && !txtName.equals("")) {
                    desPath = base_path + "/" + txtName;
                } else {
                    desPath = base_path + "/";
                }
            }
        } else {
            logD("SDCard没有挂载");
        }
        return desPath;
    }

    /***
     * 可依据directory创建多个自定义目录层级
     * <br/><br/>
     * 传入的context参数是为了获取该应用的包名
     * <br/><br/>
     * 如果参数 dirctory或者txtName为Null或者空字符串则表示不创建文件或者文件夹
     */
    public static String createLocalDirectory(Context context, String directory, String txtName) {
        String desPath = null;
        //必须要首先创建zbv这一层目录
        String base_path = "/data/data/" + context.getPackageName() + "/" + directory;
        File file = new File(base_path);
        if (!file.exists()) {
            logD("当前不存在");

            boolean success = false;
            if (directory != null && !directory.equals("")) {
                success = file.mkdirs();
            }

            logD("local---创建文件夹是否成功=" + success);

            if (success) {

                if (txtName != null && !txtName.equals("")) {
                    desPath = base_path + "/" + txtName;
                } else {
                    desPath = base_path + "/";
                }

            } else {
                if (txtName != null && !txtName.equals("")) {
                    desPath = "/data/data/" + context.getPackageName() + "/" + txtName;
                } else {
                    desPath = "/data/data/" + context.getPackageName() + "/";
                }
            }
        } else {
            logD("已存在");
            if (txtName != null && !txtName.equals("")) {
                desPath = base_path + "/" + txtName;
            } else {
                desPath = base_path + "/";
            }
        }

        return desPath;
    }

    /**
     * assetsPath表示从Assets目录下这个路径读取数据流写入desPath
     * <br/><br/>
     * 这个desPath是可读可写的路径(local或者sdcard)
     * <br/><br/>
     * 示例如下：我在Assets根目录下放置一份test_file.ecwx文件<br/><br/>
     * QZXTools.writeDataToPath(this,"/data/data/"+getPackageName()+"/test_file.ecwx","test_file.ecwx");
     * <br/><br/>
     * 如果指定的desPath路径不存在即无法依据该路径创建文件则不进行操作
     */
    public static void writeAssetsDataToPath(Context context, String desPath, String assetsPath) {
        logD("desPath=" + desPath);
        try {
            File file = new File(desPath);
            if (file == null || !file.exists()) {
                boolean isSuccess = file.createNewFile();
                if (!isSuccess) {
                    return;
                }

                logD("该路径的文件为空或者不存在" + "   isSuccess=" + isSuccess);
            }

            InputStream is = context.getResources().getAssets().open(assetsPath);

            if (is == null) {
                logD("打开Assets流失败");
                return;
            }

            FileOutputStream fos = new FileOutputStream(file);
            byte[] bytes = new byte[1024];
            int len = -1;
            while ((len = is.read(bytes)) != -1) {
                fos.write(bytes, 0, len);
            }
            fos.close();
            is.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //读取Path中的数据流转换成字符串
    public static String readDataFromPath(String desPath) {
        String text = null;
        File file = new File(desPath);
        if (file == null || !file.exists()) {
            logD("不存在该文件，不可读取了。。。");
        } else {
            try {
                FileInputStream fis = new FileInputStream(file);
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                byte[] bytes = new byte[1024];
                int len = -1;
                while ((len = fis.read(bytes)) != -1) {
                    baos.write(bytes, 0, len);
                }
                text = baos.toString();
                logD("读取而来的字符串为=" + text);
                baos.close();
                fis.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return text;
    }

    //把字符串写入一个path路径的流中
    public static void writeStringToPath(String content, String path) {
        logD("path=" + path + ";content=" + content);
        try {
            File file = new File(path);
            if (file == null || !file.exists()) {
                boolean isSuccess = file.createNewFile();
                if (!isSuccess) {
                    return;
                }

                logD("该路径的文件为空或者不存在" + "   isSuccess=" + isSuccess);
            }

            FileOutputStream fos = new FileOutputStream(file);
            DataOutputStream dos = new DataOutputStream(fos);

            //之前使用writeUTF(content)出现乱码
            dos.write(content.getBytes("utf-8"));

            dos.close();
            fos.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //------------------------FILE-------------------------

    //---------内部存储和外部存储-》好处是删除APK会一并删除缓存文件----------------------
    ///data/user/0/com.ahtelit.zbv.myapplication/cache
    public static String getInternalStorageForCache(Context context) {
        return context.getCacheDir().getAbsolutePath();
    }

    //需要SDCARD的读写权限 /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/cache
    public static String getExternalStorageForCache(Context context) {
        return context.getExternalCacheDir().getAbsolutePath();
    }

    ///data/user/0/com.ahtelit.zbv.myapplication/files
    public static String getInternalStorageForFiles(Context context) {
        return context.getFilesDir().getAbsolutePath();
    }

    /*
    type可以为空表示根目录 /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files
    type=Environment.DIRECTORY_PICTURES等
    ===》 /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/Pictures
     */
    public static String getExternalStorageForFiles(Context context, String type) {
        return context.getExternalFilesDir(type).getAbsolutePath();
    }
    //-----------------------内部存储和外部存储----------------------

    //-----------------------字节转换格式工具---------------------------
    public static String formatByteUnit(String size) {
        float byteSize = Float.parseFloat(size);
        //2M限制
        if (byteSize < 1024) {
            //字节
            return String.format("%.2f", byteSize) + "B";
        } else if (byteSize < (1024 * 1024)) {
            return String.format("%.2f", byteSize / 1024) + "K";
        } else {
            return String.format("%.2f", byteSize / 1024 / 1024) + "M";
        }

    }

    /**
     * 字节转换
     */
    public static String transformBytes(long bytes) {
//        Log.e("zbv", "integer max value=" + Integer.MAX_VALUE + ";integer mini value=" + Integer.MIN_VALUE
//                + ";Long max value=" + Long.MAX_VALUE + ";Long min value=" + Long.MIN_VALUE);
        if (bytes > Long.MAX_VALUE || bytes < 0) {
            return "传入的字节有误";
        }

        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < (1024 * 1024)) {
            if (bytes % 1024 != 0) {
                double result = bytes / 1024.0;
                return String.format("%.1fKB", result);
            } else {
                int result = (int) (bytes / 1024);
                return result + "KB";
            }

        } else if (bytes < (1024 * 1024 * 1024)) {
            if (bytes % (1024 * 1024) != 0) {
                double result = bytes / 1024 / 1024.0;
                return String.format("%.1fMB", result);
            } else {
                int result = (int) (bytes / 1024 / 1024);
                return result + "MB";
            }

        } else {
            if (bytes % (1024 * 1024 * 1024) != 0) {
                double result = bytes / 1024 / 1024 / 1024.0;
                return String.format("%.1fGB", result);
            } else {
                long result = bytes / 1024 / 1024 / 1024;
                return result + "GB";
            }
        }
    }
    //-----------------------字节转换格式工具---------------------------

    //----------------------------NETWORK------------------------
    public static String getIPAddress() {
        NetworkInfo info = ((ConnectivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE)).getActiveNetworkInfo();
        if (info != null && info.isConnected()) {
            if (info.getType() == ConnectivityManager.TYPE_MOBILE) {//当前使用2G/3G/4G网络
                try {
                    //Enumeration<NetworkInterface> en=NetworkInterface.getNetworkInterfaces();
                    for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
                         en.hasMoreElements(); ) {
                        NetworkInterface intf = en.nextElement();
                        for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
                             enumIpAddr.hasMoreElements(); ) {
                            InetAddress inetAddress = enumIpAddr.nextElement();
                            if (!inetAddress.isLoopbackAddress() && inetAddress instanceof Inet4Address) {
                                return inetAddress.getHostAddress();
                            }
                        }
                    }
                } catch (SocketException e) {
                    e.printStackTrace();
                }

            } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {//当前使用无线网络
                WifiManager wifiManager = (WifiManager) MyApplication.getInstance().getApplicationContext()
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();

                int ip = wifiInfo.getIpAddress();
                //IP2StringIP
                String ipAddress = (ip & 0xFF) + "." +
                        ((ip >> 8) & 0xFF) + "." +
                        ((ip >> 16) & 0xFF) + "." +
                        (ip >> 24 & 0xFF);//得到IPV4地址

                return ipAddress;
            }
        } else {
            //当前无网络连接,请在设置中打开网络
        }
        return null;
    }

    //反馈类型
    private static final int NETWORK_INVALID = 0;
    private static final int NETWORK_WIFI = 1;
    private static final int NETWORK_2G = 2;
    private static final int NETWORK_3G = 3;
    private static final int NETWORK_4G = 4;

    //检测网络是否链接
    //需要权限<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    public static int checkNetworkConnected() {
        ConnectivityManager cManager = (ConnectivityManager) MyApplication.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            String typeNet = networkInfo.getTypeName();
            if (typeNet.equals("WIFI")) {
                return NETWORK_WIFI;
            } else if (typeNet.equals("MOBILE")) {
                switch (networkInfo.getSubtype()) {
                    case TelephonyManager.NETWORK_TYPE_CDMA:  // telcom
                    case TelephonyManager.NETWORK_TYPE_1xRTT: // telecom
                    case TelephonyManager.NETWORK_TYPE_GPRS:  // unicom
                    case TelephonyManager.NETWORK_TYPE_EDGE:  // cmcc
                        return NETWORK_2G;
                    case TelephonyManager.NETWORK_TYPE_EHRPD:  // telecom
                    case TelephonyManager.NETWORK_TYPE_EVDO_0: // telecom
                    case TelephonyManager.NETWORK_TYPE_EVDO_A: // telecom 3.5G
                    case TelephonyManager.NETWORK_TYPE_EVDO_B: // telecom 3.5G
                    case TelephonyManager.NETWORK_TYPE_HSPA:   // unicom
                    case TelephonyManager.NETWORK_TYPE_HSPAP:  // unicom
                    case TelephonyManager.NETWORK_TYPE_HSDPA:  // unicom 3.5G
                    case TelephonyManager.NETWORK_TYPE_HSUPA:  // unicom 3.5G
                    case TelephonyManager.NETWORK_TYPE_UMTS:   // unicom
                        return NETWORK_3G;
                    case TelephonyManager.NETWORK_TYPE_LTE:
                        return NETWORK_4G;
                }
            }
        }
        return NETWORK_INVALID;
    }

    public static boolean isNetworkAvailable() {
        boolean isLinked = false;
        //注意这里最好使用Application,如果传入Activity的Context会存在泄露
        ConnectivityManager connectivityManager = (ConnectivityManager) MyApplication.getInstance()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] allNetworkInfo = connectivityManager.getAllNetworkInfo();
            if (allNetworkInfo != null) {
                for (NetworkInfo networkInfo : allNetworkInfo) {
                    if (networkInfo.getState() == NetworkInfo.State.CONNECTED) {
                        isLinked = true;
                    }
                }
            }
        }

        //处理连上网络，但是网络不一定可用
//        if (!isLinked) {
//            return false;
//        }

//        try {
//            Socket s=null;
//            if (s == null) {
//                s = new Socket();
//            }
//            InetAddress host = InetAddress.getByName("8.8.8.8");//国内使用114.114.114.114，如果全球通用google：8.8.8.8
//            s.connect(new InetSocketAddress(host, 53), 5000);//google:53
//            s.close();
//        } catch (IOException e) {
//
//        }

        //判断是否联网
//        try {
//            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 5 www.baid.com");
//            int status = process.waitFor();
//            if (status == 0) {
//                //网络可用
//                return true;
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }

        return isLinked;
    }

    public static boolean isActiveNetworkMobile(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        }
        return false;
    }

    public static boolean isActiveNetworkWifi(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            }
        }
        return false;
    }

    //----------------------------NETWORK------------------------

    //----------------------------Time/Date----------------------

    //当前的时间到秒
    public static String getFormatCurrentDateToSecond() {
        //年-y 月-M 日-d 时-h(1-12) H(0-23) 分-m 秒-s 毫秒-S
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String date = simpleDateFormat.format(new Date());
        return date;
    }

    /**
     * 依据时间戳字符串获取格式化的yyyy-MM-dd hh:mm:ss格式的时间字符串
     */
    public static String getFormatDateFromTimeStamp(String timeStamp) {
        try {
            //将ms的时间转成Date
            Date date = DateFormat.getDateInstance().parse(timeStamp);
            return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            logE("e=" + e, null);
        }
        return "";
    }

    /**
     * 12:30:12
     */
    public static String getFormatTime(long time) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(new Date(time));
    }

    /**
     * 获取Date时间值Long
     *
     * @param content 要格式化的字符串
     * @param format  格式化样式
     */
    public static long getDateValue(String content, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        try {
            Date date = simpleDateFormat.parse(content);
            return date.getTime();
        } catch (ParseException e) {
            e.printStackTrace();
            logE("e=" + e, null);
        }
        return -1;
    }

    /**
     * 依据当前日期获取星期号，星期日到星期六---》1~7
     */
    public static int judgeWeekFromDate(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int week = calendar.get(Calendar.DAY_OF_WEEK);
        if (week == 1) {
            week = 7;
        } else {
            week--;
        }
        return week;
    }


    /**
     * 返回该年该月有多少天
     *
     * @param month 1~12
     */
    public static int calculate(int year, int month) {
        boolean yearleap = judge(year);
        int day;
        if (yearleap && month == 2) {
            day = 29;
        } else if (!yearleap && month == 2) {
            day = 28;
        } else if (month == 4 || month == 6 || month == 9 || month == 11) {
            day = 30;
        } else {
            day = 31;
        }
        return day;
    }

    /**
     * 判断是否是闰年
     */
    private static boolean judge(int year) {
        boolean yearleap = (year % 400 == 0) || (year % 4 == 0)
                && (year % 100 != 0);// 采用布尔数据计算判断是否能整除
        return yearleap;
    }

    /**
     * 时间转换
     * 00:00:07
     * 用处在于传入数值转化时间
     * {@link #getFormatTime}
     */
    public static String getTransmitTime(long count) {
        if (count > Long.MAX_VALUE || count < 0) {
            return "传入的Count有误";
        }

        if (count < 60) {
            //秒
            String result;
            if (count < 10) {
                result = "00:0" + count;
            } else {
                result = "00:" + count;
            }
            return result;
        } else if (count < 60 * 60) {
            //分秒
            long minute = count / (60);
            String minuteResult;
            if (minute < 10) {
                minuteResult = "0" + minute;
            } else {
                minuteResult = minute + "";
            }
            long second = count % (60);
            String secondResult;
            if (second < 10) {
                secondResult = "0" + second;
            } else {
                secondResult = second + "";
            }
            return minuteResult + ":" + secondResult;
        } else {
            long hour = count / (60 * 60);
            String hourResult;
            if (hour < 10) {
                hourResult = "0" + hour;
            } else {
                hourResult = hour + "";
            }

            long minute = count % (60 * 60) / (60);
            String minuteResult;
            if (minute < 10) {
                minuteResult = "0" + minute;
            } else {
                minuteResult = minute + "";
            }

            long second = count % (60 * 60) % (60);
            String secondResult;
            if (second < 10) {
                secondResult = "0" + second;
            } else {
                secondResult = second + "";
            }

            return hourResult + ":" + minuteResult + ":" + secondResult;
        }
    }

    /**
     * 想法：
     * 同一天的就显示当天的time
     * 不同天但是在一周内显示星期+time
     * 其余显示date+time
     *
     * @param inputTime Date的long值
     */
    public static String DateOrTimeStrShow(long inputTime) {
        long curChatTime;
        if (inputTime == -1) {
            curChatTime = System.currentTimeMillis();
        } else {
            curChatTime = inputTime;
        }
        Calendar calendar = Calendar.getInstance();
        //当前的时间
        calendar.setTime(new Date());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);

        int week = calendar.get(Calendar.WEEK_OF_MONTH);
        int offsetDay;
        if (week == 1) {
            week = 7;
        } else {
            week--;
        }
        offsetDay = week - 1;

        //今天凌晨的时间
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(year);
        stringBuilder.append("-");
        stringBuilder.append(month);
        stringBuilder.append("-");
        stringBuilder.append(day);
        stringBuilder.append(" 00:00:00");
        String todayStartTime = stringBuilder.toString();
        long startDateValue = getDateValue(todayStartTime, "yyyy-MM-dd HH:mm:ss");

        stringBuilder.setLength(0);
        //计算周一的日期
        if (day - offsetDay < 0) {
            month--;
            if (month < 1) {
                month = 12;
            }
            int maxDay = calculate(year, month);
            day = maxDay - (day - offsetDay);
        }
        //周一的凌晨时间
        stringBuilder.append(year);
        stringBuilder.append("-");
        stringBuilder.append(month);
        stringBuilder.append("-");
        stringBuilder.append(day);
        stringBuilder.append(" 00:00:00");
        String mondayTime = stringBuilder.toString();
        long mondayDateValue = getDateValue(mondayTime, "yyyy-MM-dd HH:mm:ss");

        QZXTools.logE("year=" + year + ";month=" + month + ";day=" + day
                + ";hour=" + hour + ";minute=" + minute + ";second=" + second + ";week=" + week
                + ";startDateValue=" + startDateValue + ";mondayDateValue=" + mondayDateValue, null);

        //重置用于保存结果字符串
        stringBuilder.setLength(0);
        calendar.setTime(new Date(curChatTime));
        int cur_year = calendar.get(Calendar.YEAR);
        int cur_month = calendar.get(Calendar.MONTH) + 1;
        int cur_day = calendar.get(Calendar.DAY_OF_MONTH);

        int cur_hour = calendar.get(Calendar.HOUR_OF_DAY);
        int cur_minute = calendar.get(Calendar.MINUTE);
        int cur_second = calendar.get(Calendar.SECOND);

        int cur_week = calendar.get(Calendar.WEEK_OF_MONTH);
        String weekStr = "";
        switch (cur_week) {
            case 1:
                weekStr = "周日";
                break;
            case 2:
                weekStr = "周一";
                break;
            case 3:
                weekStr = "周二";
                break;
            case 4:
                weekStr = "周三";
                break;
            case 5:
                weekStr = "周四";
                break;
            case 6:
                weekStr = "周五";
                break;
            case 7:
                weekStr = "周六";
                break;
        }

        if (curChatTime - startDateValue > 0) {
            //在一天之内
            stringBuilder.append(cur_hour);
            stringBuilder.append(":");
            stringBuilder.append(cur_minute);
            stringBuilder.append(":");
            stringBuilder.append(cur_second);
        } else if (curChatTime - mondayDateValue > 0) {
            //一周之内
            stringBuilder.append(weekStr);
            stringBuilder.append(" ");
            stringBuilder.append(cur_hour);
            stringBuilder.append(":");
            stringBuilder.append(cur_minute);
            stringBuilder.append(":");
            stringBuilder.append(cur_second);
        } else {
            stringBuilder.append(cur_year);
            stringBuilder.append("/");
            stringBuilder.append(cur_month);
            stringBuilder.append("/");
            stringBuilder.append(cur_day);
            stringBuilder.append(" ");
            stringBuilder.append(cur_hour);
            stringBuilder.append(":");
            stringBuilder.append(cur_minute);
            stringBuilder.append(":");
            stringBuilder.append(cur_second);
        }
        return stringBuilder.toString();
    }
    //----------------------------Time/Date----------------------

    //-------------------------配置文件Properties---------------------
    public static Properties getConfigProperties(String path) {
        Properties properties = new Properties();
//        InputStream inputStream = QZXTools.class.getResourceAsStream(path);//获取的是assets等资源路径
        try {
            FileInputStream fis = new FileInputStream(path);
            //统一编码
            properties.load(new InputStreamReader(fis, "UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public static void addDataToProperties(String packageName, String fileName, String key, String value) {
        String path = "/data/data/" + packageName + "/" + fileName;
        Properties properties = new Properties();

        properties.setProperty(key, value);

        try {
            FileOutputStream fos = new FileOutputStream(path);
            properties.store(new OutputStreamWriter(fos, "UTF-8"),
                    "添加key=" + key + ";value=" + value + "的属性");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //-------------------------Properties---------------------

    //-----------------------保存图片到系统图片相册中------------------------

    /**
     * 将Uri转化为path
     *
     * @param context
     * @param uri
     * @return
     */
    public static String getRealFilePath(final Context context, final Uri uri) {
        if (null == uri) return null;
        final String scheme = uri.getScheme();
        String data = null;
        if (scheme == null)
            data = uri.getPath();
        else if (ContentResolver.SCHEME_FILE.equals(scheme)) {
            data = uri.getPath();
        } else if (ContentResolver.SCHEME_CONTENT.equals(scheme)) {
            Cursor cursor = context.getContentResolver().query(uri, new String[]{
                    MediaStore.Images.ImageColumns.DATA}, null, null, null);
            if (null != cursor) {
                if (cursor.moveToFirst()) {
                    int index = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
                    if (index > -1) {
                        data = cursor.getString(index);
                    }
                }
                cursor.close();
            }
        }
        return data;
    }

    /**
     * MediaStore.Images.Media.insertImage(InteractiveActivity.this.getContentResolver(), bitmap, "", "");
     * InteractiveActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE,
     * Uri.parse("file://" + destFile.getAbsolutePath())));
     * <p>
     * 将图片保存到系统的Pictures目录
     */
    public static void savePictureToSystemDCIM(Context context, File fileImage, String nameImage) {
        // 其次把文件插入到系统图库
        try {
            MediaStore.Images.Media.insertImage(context.getContentResolver(),
                    fileImage.getAbsolutePath(), nameImage, null);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        // 最后通知图库更新
        Intent intent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        Uri uri = Uri.fromFile(fileImage);
        intent.setData(uri);
        context.sendBroadcast(intent);
    }

    //调用系统相机---Camera以及SD卡权限--->这里6.0/7.0注意
    public static final int CODE_TAKE_PHOTO = 1;//相机RequestCode

    public static void accessSystemCamera(Activity activity, Uri photoUri) {
        Intent takeIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //设置拍照保存的路径，需要特别注意的是在onActivityResult中获取的Intent为空
        takeIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
        activity.startActivityForResult(takeIntent, CODE_TAKE_PHOTO);
    }

    /**
     * 因为是以Date为图片名称的所以注意图片存不存在呀！！！
     */
    public static Uri getMediaUri(Activity activity) {
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory
                (Environment.DIRECTORY_PICTURES), "ME");

        if (mediaStorageDir == null || !mediaStorageDir.exists()) {
            boolean success = mediaStorageDir.mkdir();
            if (success) {
                logD("mkdir is success");
            } else {
                popToast(activity, "创建相机存储目录失败", false);
            }
        }

        //创建Media File
        String timeStamp = new SimpleDateFormat("yyyy_MMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

//        //系统相册
//        File mediaFile = new File(Environment.getExternalStorageDirectory() + File.separator +
//                Environment.DIRECTORY_DCIM + File.separator + "Camera" + File.separator + "IMG_" + timeStamp + ".jpg");

        Uri photoUri = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            photoUri = FileProvider.getUriForFile(activity, activity.getPackageName()
                    + ".fileprovider", mediaFile);
        } else {
            photoUri = Uri.fromFile(mediaFile);
        }

        logD("uriPath=" + photoUri.getPath());

        return photoUri;
    }


    //调用系统相册
    public static final int CODE_SELECT_IMAGE = 2;//相册RequestCode

    public static void accessSystemAlbum(Activity activity) {
        Intent albumIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        albumIntent.setType("image/*");
        activity.startActivityForResult(albumIntent, CODE_SELECT_IMAGE);
    }

    public static final int CROP_REQUEST_CODE = 3;//裁剪RequestCode

    public static void cropPhoto(Uri uri, Activity activity) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 300);
        intent.putExtra("outputY", 300);
        intent.putExtra("return-data", true);
        activity.startActivityForResult(intent, CROP_REQUEST_CODE);
    }

    //通知系统刷新相册
    public static void galleryAddPic(Activity activity, Uri contentUri) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(contentUri);
        activity.sendBroadcast(mediaScanIntent);
    }


    /**
     * 压缩图片显示
     */
    public static Bitmap compressBitmap(Bitmap image, float userWidth, float userHeight) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if (baos.toByteArray().length / 1024 > 1024) {//判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, 50, baos);//这里压缩50%，把压缩后的数据存放到baos中
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //开始读入图片，此时把options.inJustDecodeBounds 设回true了
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;

        float hh = userWidth;//这里设置高度为100f
        float ww = userHeight;//这里设置宽度为100f
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;

        logD("w=" + w + ";h=" + h + ";be=" + be);

        newOpts.inSampleSize = be;//设置缩放比例
        //重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImage(bitmap);//压缩好比例大小后再进行质量压缩
    }

    public static Bitmap compressImage(Bitmap image) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;
        while (baos.toByteArray().length / 1024 > 100) {    //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            options -= 10;//每次都减少10
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    /**
     * 使用Matrix
     *
     * @param bitmap 原始的Bitmap
     * @param width  目标宽度
     * @param height 目标高度
     * @return 缩放后的Bitmap
     */
    public static Bitmap scaleMatrix(Bitmap bitmap, int width, int height) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();
        float scaleW = width * 1.0f / w;
        float scaleH = height * 1.0f / h;
        Matrix matrix = new Matrix();
        matrix.postScale(scaleW, scaleH); // 长和宽放大缩小的比例
        return Bitmap.createBitmap(bitmap, 0, 0, w, h, matrix, true);
    }

    /**
     * 使用Canvas
     *
     * @param bitmap 原始的Bitmap
     * @param rect   Bitmap被缩放放置的Rect
     * @return 缩放后的Bitmap
     */
    public static Bitmap scaleCanvas(Bitmap bitmap, Rect rect) {
        Bitmap newBitmap = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);//创建和目标相同大小的空Bitmap
        Canvas canvas = new Canvas(newBitmap);
        Paint paint = new Paint();
        Bitmap temp = bitmap;

        //针对绘制bitmap添加抗锯齿
        PaintFlagsDrawFilter pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        paint.setFilterBitmap(true); //对Bitmap进行滤波处理
        paint.setAntiAlias(true);//设置抗锯齿
        canvas.setDrawFilter(pfd);
        canvas.drawBitmap(temp, null, rect, paint);

        return newBitmap;
    }

    //-----------------------保存图片到系统图片相册中------------------------

    //------------------------------第三方调用打开文件

    /**
     * 调用第三方打开文件
     * <p>
     * 原来从Android 7.0开始，谷歌收回了访问文件的权限，即一个应用提供自身资源文件给其它应用使用时，
     * 如果给出 file://xxx 这样格式的URI的话，谷歌会认为目标应用不具备访问此文件的权限，
     * 便会抛出 FileUriExposedException 的异常。我这里的解决方法是使用 FileProvider，
     * 来生成一个content://xxx 格式的URI，并授予此 URI 临时访问权限
     *
     * @param file
     * @param context
     */
    public static void openFile(File file, Context context) {
        try {
            Intent intent = new Intent();
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // 设置intent的Action属性
            intent.setAction(Intent.ACTION_VIEW);
            // 获取文件file的MIME类型
            String type = getMIMEType(file);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        "com.telit.smartclass.desktop.fileprovider",
                        file);//file即为所要共享的文件的file
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);//授予临时权限别忘了
                intent.setDataAndType(photoURI, type);
            } else {
                // 设置intent的data和Type属性。
                intent.setDataAndType(Uri.fromFile(file), type);
            }
            // 跳转
            context.startActivity(intent);
            Intent.createChooser(intent, "请选择对应的软件打开该附件！");
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public static String getMIMEType(File file) {
        String type = "*/*";
        String fName = file.getName();
        // 获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
        /* 获取文件的后缀名 */
        String end = fName.substring(dotIndex + 1, fName.length())
                .toLowerCase();
        if (end == "")
            return type;
        // 在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_Table.length; i++) {
            if (end.equals(MIME_Table[i][0]))
                type = MIME_Table[i][1];
        }
        return type;
    }

    /**
     * 文件MIME类型(主要用做打开操作时，指定打开的指定文件对应所属的MIME类型)
     */
    private static final String[][] MIME_Table = {
            // {后缀名，MIME类型}
            {"aab", "application/x-authoware-bin"},
            {"aam", "application/x-authoware-map"},
            {"aas", "application/x-authoware-seg"},
            {"amc", "application/x-mpeg"},
            {"ani", "application/octet-stream"},
            {"apk", "application/vnd.android.package-archive"},
            {"asd", "application/astound"}, {"asn", "application/astound"},
            {"asp", "application/x-asap"},
            {"ai", "application/postscript"},
            {"avb", "application/octet-stream"},
            {"bcpio", "application/x-bcpio"},
            {"bin", "application/octet-stream"},
            {"bld", "application/bld"}, {"bld2", "application/bld2"},
            {"aif", "audio/x-aiff"}, {"aifc", "audio/x-aiff"},
            {"aiff", "audio/x-aiff"}, {"als", "audio/X-Alpha5"},
            {"au", "audio/basic"}, {"awb", "audio/amr-wb"},
            {"3gp", "video/3gpp"}, {"asf", "video/x-ms-asf"},
            {"asx", "video/x-ms-asf"}, {"avi", "video/x-msvideo"},
            {"asc", "text/plain"}, {"bmp", "image/bmp"},
            {"bpk", "application/octet-stream"},
            {"bz2", "application/x-bzip2"}, {"c", "text/x-csrc"},
            {"cpp", "text/x-c++src"}, {"cal", "image/x-cals"},
            {"ccn", "application/x-cnc"}, {"cco", "application/x-cocoa"},
            {"cdf", "application/x-netcdf"},
            {"cgi", "magnus-internal/cgi"}, {"chat", "application/x-chat"},
            {"class", "application/octet-stream"},
            {"clp", "application/x-msclip"}, {"cmx", "application/x-cmx"},
            {"co", "application/x-cult3d-object"},
            {"cod", "image/cis-cod"}, {"csh", "application/x-csh"},
            {"csm", "chemical/x-csml"}, {"csml", "chemical/x-csml"},
            {"css", "text/css"}, {"dcm", "x-lml/x-evm"},
            {"cpio", "application/x-cpio"},
            {"cpt", "application/mac-compactpro"},
            {"crd", "application/x-mscardfile"},
            {"cur", "application/octet-stream"},
            {"dcr", "application/x-director"},
            {"dir", "application/x-director"},
            {"dll", "application/octet-stream"},
            {"dmg", "application/octet-stream"},
            {"dms", "application/octet-stream"},
            {"doc", "application/msword"}, {"dot", "application/x-dot"},
            {"dvi", "application/x-dvi"}, {"dwg", "application/x-autocad"},
            {"dxf", "application/x-autocad"},
            {"dxr", "application/x-director"},
            {"ebk", "application/x-expandedbook"},
            {"etc", "application/x-earthtime"}, {"dcx", "image/x-dcx"},
            {"dhtml", "text/html"}, {"dwf", "drawing/x-dwf"},
            {"emb", "chemical/x-embl-dl-nucleotide"},
            {"embl", "chemical/x-embl-dl-nucleotide"},
            {"eps", "application/postscript"}, {"eri", "image/x-eri"},
            {"es", "audio/echospeech"}, {"esl", "audio/echospeech"},
            {"etx", "text/x-setext"}, {"evm", "x-lml/x-evm"},
            {"evy", "application/x-envoy"},
            {"exe", "application/octet-stream"},
            {"fh4", "image/x-freehand"}, {"fh5", "image/x-freehand"},
            {"fhc", "image/x-freehand"}, {"fif", "image/fif"},
            {"fm", "application/x-maker"}, {"fpx", "image/x-fpx"},
            {"fvi", "video/isivideo"},
            {"gau", "chemical/x-gaussian-input"},
            {"gca", "application/x-gca-compressed"},
            {"gdb", "x-lml/x-gdb"}, {"gif", "image/gif"},
            {"gps", "application/x-gps"}, {"gtar", "application/x-gtar"},
            {"gz", "application/x-gzip"}, {"h", "text/x-chdr"},
            {"hdf", "application/x-hdf"}, {"hdm", "text/x-hdml"},
            {"hdml", "text/x-hdml"}, {"hlp", "application/winhlp"},
            {"hqx", "application/mac-binhex40"}, {"htm", "text/html"},
            {"html", "text/html"}, {"hts", "text/html"},
            {"ice", "x-conference/x-cooltalk"},
            {"ico", "application/octet-stream"}, {"ief", "image/ief"},
            {"ifm", "image/gif"}, {"ifs", "image/ifs"},
            {"imy", "audio/melody"}, {"ins", "application/x-NET-Install"},
            {"ips", "application/x-ipscript"},
            {"ipx", "application/x-ipix"}, {"it", "audio/x-mod"},
            {"itz", "audio/x-mod"}, {"ivr", "i-world/i-vrml"},
            {"j2k", "image/j2k"},
            {"jad", "text/vnd.sun.j2me.app-descriptor"},
            {"jam", "application/x-jam"}, {"java", "application/x-java"},
            {"jar", "application/java-archive"},
            {"jnlp", "application/x-java-jnlp-file"},
            {"jpe", "image/jpeg"}, {"jpeg", "image/jpeg"},
            {"jpg", "image/jpeg"}, {"jpz", "image/jpeg"},
            {"js", "application/x-javascript"}, {"jwc", "application/jwc"},
            {"kjx", "application/x-kjx"}, {"lak", "x-lml/x-lak"},
            {"latex", "application/x-latex"},
            {"lcc", "application/fastman"},
            {"lcl", "application/x-digitalloca"},
            {"lcr", "application/x-digitalloca"},
            {"lgh", "application/lgh"},
            {"lha", "application/octet-stream"}, {"lml", "x-lml/x-lml"},
            {"lmlpack", "x-lml/x-lmlpack"}, {"lsf", "video/x-ms-asf"},
            {"lsx", "video/x-ms-asf"}, {"lzh", "application/x-lzh"},
            {"m13", "application/x-msmediaview"},
            {"m14", "application/x-msmediaview"}, {"m15", "audio/x-mod"},
            {"m3u", "audio/x-mpegurl"}, {"m3url", "audio/x-mpegurl"},
            {"ma1", "audio/ma1"}, {"ma2", "audio/ma2"},
            {"ma3", "audio/ma3"}, {"ma5", "audio/ma5"},
            {"man", "application/x-troff-man"},
            {"map", "magnus-internal/imagemap"},
            {"mbd", "application/mbedlet"},
            {"mct", "application/x-mascot"},
            {"mdb", "application/x-msaccess"}, {"mdz", "audio/x-mod"},
            {"me", "application/x-troff-me"}, {"mel", "text/x-vmel"},
            {"mi", "application/x-mif"}, {"mid", "audio/midi"},
            {"midi", "audio/midi"}, {"mif", "application/x-mif"},
            {"mil", "image/x-cals"}, {"mio", "audio/x-mio"},
            {"mmf", "application/x-skt-lbs"}, {"mng", "video/x-mng"},
            {"mny", "application/x-msmoney"},
            {"moc", "application/x-mocha"},
            {"mocha", "application/x-mocha"}, {"mod", "audio/x-mod"},
            {"mof", "application/x-yumekara"},
            {"mol", "chemical/x-mdl-molfile"},
            {"mop", "chemical/x-mopac-input"}, {"mov", "video/quicktime"},
            {"movie", "video/x-sgi-movie"}, {"mp2", "audio/x-mpeg"},
            {"mp3", "audio/x-mpeg"}, {"mp4", "video/mp4"},
            {"mpc", "application/vnd.mpohun.certificate"},
            {"mpe", "video/mpeg"}, {"mpeg", "video/mpeg"},
            {"mpg", "video/mpeg"}, {"mpg4", "video/mp4"},
            {"mpga", "audio/mpeg"},
            {"mpn", "application/vnd.mophun.application"},
            {"mpp", "application/vnd.ms-project"},
            {"mps", "application/x-mapserver"}, {"mrl", "text/x-mrml"},
            {"mrm", "application/x-mrm"}, {"ms", "application/x-troff-ms"},
            {"mts", "application/metastream"},
            {"mtx", "application/metastream"},
            {"mtz", "application/metastream"},
            {"mzv", "application/metastream"}, {"nar", "application/zip"},
            {"nbmp", "image/nbmp"}, {"nc", "application/x-netcdf"},
            {"ndb", "x-lml/x-ndb"}, {"ndwn", "application/ndwn"},
            {"nif", "application/x-nif"}, {"nmz", "application/x-scream"},
            {"nokia-op-logo", "image/vnd.nok-oplogo-color"},
            {"npx", "application/x-netfpx"}, {"nsnd", "audio/nsnd"},
            {"nva", "application/x-neva1"}, {"oda", "application/oda"},
            {"oom", "application/x-AtlasMate-Plugin"},
            {"pac", "audio/x-pac"}, {"pae", "audio/x-epac"},
            {"pan", "application/x-pan"},
            {"pbm", "image/x-portable-bitmap"}, {"pcx", "image/x-pcx"},
            {"pda", "image/x-pda"}, {"pdb", "chemical/x-pdb"},
            {"pdf", "application/pdf"}, {"pfr", "application/font-tdpfr"},
            {"pgm", "image/x-portable-graymap"}, {"pict", "image/x-pict"},
            {"pm", "application/x-perl"}, {"pmd", "application/x-pmd"},
            {"png", "image/png"}, {"pnm", "image/x-portable-anymap"},
            {"pnz", "image/png"}, {"pot", "application/vnd.ms-powerpoint"},
            {"ppm", "image/x-portable-pixmap"},
            {"pps", "application/vnd.ms-powerpoint"},
            {"ppt", "application/vnd.ms-powerpoint"},
            {"pqf", "application/x-cprplayer"},
            {"pqi", "application/cprplayer"}, {"prc", "application/x-prc"},
            {"proxy", "application/x-ns-proxy-autoconfig"},
            {"ps", "application/postscript"},
            {"ptlk", "application/listenup"},
            {"pub", "application/x-mspublisher"},
            {"pvx", "video/x-pv-pvx"}, {"qcp", "audio/vnd.qcelp"},
            {"qt", "video/quicktime"}, {"qti", "image/x-quicktime"},
            {"qtif", "image/x-quicktime"},
            {"r3t", "text/vnd.rn-realtext3d"},
            {"ra", "audio/x-pn-realaudio"},
            {"ram", "audio/x-pn-realaudio"},
            {"rar", "application/x-rar-compressed"},
            {"ras", "image/x-cmu-raster"}, {"rdf", "application/rdf+xml"},
            {"rf", "image/vnd.rn-realflash"}, {"rgb", "image/x-rgb"},
            {"rlf", "application/x-richlink"},
            {"rm", "audio/x-pn-realaudio"}, {"rmf", "audio/x-rmf"},
            {"rmm", "audio/x-pn-realaudio"},
            {"rmvb", "audio/x-pn-realaudio"},
            {"rnx", "application/vnd.rn-realplayer"},
            {"roff", "application/x-troff"},
            {"rp", "image/vnd.rn-realpix"},
            {"rpm", "audio/x-pn-realaudio-plugin"},
            {"rt", "text/vnd.rn-realtext"}, {"rte", "x-lml/x-gps"},
            {"rtf", "application/rtf"}, {"rtg", "application/metastream"},
            {"rtx", "text/richtext"}, {"rv", "video/vnd.rn-realvideo"},
            {"rwc", "application/x-rogerwilco"}, {"s3m", "audio/x-mod"},
            {"s3z", "audio/x-mod"}, {"sca", "application/x-supercard"},
            {"scd", "application/x-msschedule"},
            {"sdf", "application/e-score"},
            {"sea", "application/x-stuffit"}, {"sgm", "text/x-sgml"},
            {"sgml", "text/x-sgml"}, {"sh", "application/x-sh"},
            {"shar", "application/x-shar"},
            {"shtml", "magnus-internal/parsed-html"},
            {"shw", "application/presentations"}, {"si6", "image/si6"},
            {"si7", "image/vnd.stiwap.sis"},
            {"si9", "image/vnd.lgtwap.sis"},
            {"sis", "application/vnd.symbian.install"},
            {"sit", "application/x-stuffit"},
            {"skd", "application/x-Koan"}, {"skm", "application/x-Koan"},
            {"skp", "application/x-Koan"}, {"skt", "application/x-Koan"},
            {"slc", "application/x-salsa"}, {"smd", "audio/x-smd"},
            {"smi", "application/smil"}, {"smil", "application/smil"},
            {"smp", "application/studiom"}, {"smz", "audio/x-smd"},
            {"snd", "audio/basic"}, {"spc", "text/x-speech"},
            {"spl", "application/futuresplash"},
            {"spr", "application/x-sprite"},
            {"sprite", "application/x-sprite"},
            {"spt", "application/x-spt"},
            {"src", "application/x-wais-source"},
            {"stk", "application/hyperstudio"}, {"stm", "audio/x-mod"},
            {"sv4cpio", "application/x-sv4cpio"},
            {"sv4crc", "application/x-sv4crc"}, {"svf", "image/vnd"},
            {"svg", "image/svg-xml"}, {"svh", "image/svh"},
            {"svr", "x-world/x-svr"},
            {"swf", "application/x-shockwave-flash"},
            {"swfl", "application/x-shockwave-flash"},
            {"t", "application/x-troff"},
            {"tad", "application/octet-stream"}, {"talk", "text/x-speech"},
            {"tar", "application/x-tar"}, {"taz", "application/x-tar"},
            {"tbp", "application/x-timbuktu"},
            {"tbt", "application/x-timbuktu"},
            {"tcl", "application/x-tcl"}, {"tex", "application/x-tex"},
            {"texi", "application/x-texinfo"},
            {"texinfo", "application/x-texinfo"},
            {"tgz", "application/x-tar"},
            {"thm", "application/vnd.eri.thm"}, {"tif", "image/tiff"},
            {"tiff", "image/tiff"}, {"tki", "application/x-tkined"},
            {"tkined", "application/x-tkined"}, {"toc", "application/toc"},
            {"toy", "image/toy"}, {"tr", "application/x-troff"},
            {"trk", "x-lml/x-gps"}, {"trm", "application/x-msterminal"},
            {"tsi", "audio/tsplayer"}, {"tsp", "application/dsptype"},
            {"tsv", "text/tab-separated-values"},
            {"tsv", "text/tab-separated-values"},
            {"ttf", "application/octet-stream"},
            {"ttz", "application/t-time"}, {"txt", "text/plain"},
            {"ult", "audio/x-mod"}, {"ustar", "application/x-ustar"},
            {"uu", "application/x-uuencode"},
            {"uue", "application/x-uuencode"},
            {"vcd", "application/x-cdlink"}, {"vcf", "text/x-vcard"},
            {"vdo", "video/vdo"}, {"vib", "audio/vib"},
            {"viv", "video/vivo"}, {"vivo", "video/vivo"},
            {"vmd", "application/vocaltec-media-desc"},
            {"vmf", "application/vocaltec-media-file"},
            {"vmi", "application/x-dreamcast-vms-info"},
            {"vms", "application/x-dreamcast-vms"},
            {"vox", "audio/voxware"}, {"vqe", "audio/x-twinvq-plugin"},
            {"vqf", "audio/x-twinvq"}, {"vql", "audio/x-twinvq"},
            {"vre", "x-world/x-vream"}, {"vrml", "x-world/x-vrml"},
            {"vrt", "x-world/x-vrt"}, {"vrw", "x-world/x-vream"},
            {"vts", "workbook/formulaone"}, {"wav", "audio/x-wav"},
            {"wax", "audio/x-ms-wax"}, {"wbmp", "image/vnd.wap.wbmp"},
            {"web", "application/vnd.xara"}, {"wi", "image/wavelet"},
            {"wis", "application/x-InstallShield"},
            {"wm", "video/x-ms-wm"}, {"wma", "audio/x-ms-wma"},
            {"wmd", "application/x-ms-wmd"},
            {"wmf", "application/x-msmetafile"},
            {"wml", "text/vnd.wap.wml"},
            {"wmlc", "application/vnd.wap.wmlc"},
            {"wmls", "text/vnd.wap.wmlscript"},
            {"wmlsc", "application/vnd.wap.wmlscriptc"},
            {"wmlscript", "text/vnd.wap.wmlscript"},
            {"wmv", "audio/x-ms-wmv"}, {"wmx", "video/x-ms-wmx"},
            {"wmz", "application/x-ms-wmz"}, {"wpng", "image/x-up-wpng"},
            {"wpt", "x-lml/x-gps"}, {"wri", "application/x-mswrite"},
            {"wrl", "x-world/x-vrml"}, {"wrz", "x-world/x-vrml"},
            {"ws", "text/vnd.wap.wmlscript"},
            {"wsc", "application/vnd.wap.wmlscriptc"},
            {"wv", "video/wavelet"}, {"wvx", "video/x-ms-wvx"},
            {"wxl", "application/x-wxl"}, {"x-gzip", "application/x-gzip"},
            {"xar", "application/vnd.xara"}, {"xbm", "image/x-xbitmap"},
            {"xdm", "application/x-xdma"}, {"xdma", "application/x-xdma"},
            {"xdw", "application/vnd.fujixerox.docuworks"},
            {"xht", "application/xhtml+xml"},
            {"xhtm", "application/xhtml+xml"},
            {"xhtml", "application/xhtml+xml"},
            {"xla", "application/vnd.ms-excel"},
            {"xlc", "application/vnd.ms-excel"},
            {"xll", "application/x-excel"},
            {"xlm", "application/vnd.ms-excel"},
            {"xls", "application/vnd.ms-excel"},
            {"xlt", "application/vnd.ms-excel"},
            {"xlw", "application/vnd.ms-excel"}, {"xm", "audio/x-mod"},
            {"xml", "text/xml"}, {"xmz", "audio/x-mod"},
            {"xpi", "application/x-xpinstall"}, {"xpm", "image/x-xpixmap"},
            {"xsit", "text/xml"}, {"xsl", "text/xml"},
            {"xul", "text/xul"}, {"xwd", "image/x-xwindowdump"},
            {"xyz", "chemical/x-pdb"}, {"yz1", "application/x-yz1"},
            {"z", "application/x-compress"},
            {"zac", "application/x-zaurus-zac"},
            {"zip", "application/zip"},};
    //------------------------------第三方调用打开文件

    //------------------------图片处理
    public static Drawable bitmapToDrawable(Bitmap bmp) {
        return new BitmapDrawable(bmp);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }
        int w = drawable.getIntrinsicWidth();
        int h = drawable.getIntrinsicHeight();
        System.out.println("Drawable转Bitmap");
        Bitmap.Config config =
                drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
                        : Bitmap.Config.RGB_565;
        Bitmap bitmap = Bitmap.createBitmap(w, h, config);
        //注意，下面三行代码要用到，否则在View或者SurfaceView里的canvas.drawBitmap会看不到图
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, w, h);
        drawable.draw(canvas);
        return bitmap;
    }
    //------------------------图片处理

    //------------------------little tools

    /**
     * 获取状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        int resourcesId = context.getResources().getIdentifier("status_bar_height",
                "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourcesId);
    }

    /**
     * 获取ActionBar高度
     */
    public static int getActionBarHeight(Context context) {
        TypedValue tv = new TypedValue();
        int actionBarHeight = 0;
        if (context.getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data,
                    context.getResources().getDisplayMetrics());
        }
        return actionBarHeight;
    }

    /**
     * 获取导航栏高度：就是类似华为底部的虚拟键
     */
    public static int getNavigationHeight(Context context) {
        //首先判断是否显示了导航栏：
        int rid = context.getResources().getIdentifier("config_showNavigationBar",
                "bool", "android");
        //如果rid非零
        int resourceId = context.getResources().getIdentifier("navigation_bar_height",
                "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 代码的沉浸式实现---一般的情况
     * api19[安卓4.4.2]以上才支持沉浸式 api21[安卓5.0.1]才算真正简单的实现沉浸式
     */
    public static void unifyStatusColor(Activity activity, @ColorInt int color, boolean lightStatusColor) {
        Window window = activity.getWindow();
        //安卓5.0不适用于图片，适合纯状态栏
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            //透明化状态栏这样便拥有使用statusbar的空间
            window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //为在状态栏填充一个同样大小的视图---必须配合着fitSystemWindow
            ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
            View statusView = new View(activity);
            int statusHeight = getStatusBarHeight(activity);
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT, statusHeight);
            layoutParams.gravity = Gravity.TOP;
            statusView.setLayoutParams(layoutParams);
            statusView.setBackgroundColor(color);
            decorViewGroup.addView(statusView, layoutParams);
        } else {
            return;
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decorView = window.getDecorView();
            int flag = decorView.getSystemUiVisibility();
            if (lightStatusColor) {
                flag |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            } else {
                flag &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            }
            decorView.setSystemUiVisibility(flag);
        }

        /*关于fitSystemWindow="true/false"的重要说明：
        一、只有将statusbar设为透明，或者界面设为全屏显示（设置View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN flag)时，
            fitsSystemWindows才会起作用。不然statusbar的空间轮不到用户处理，这时会由ContentView的父控件处理，
            如果用HierarchyView 工具查看，将会看到，ContentView的父控件的paddingTop将会被设置。

        二、如果多个view同时设置了fitsSystemWindows，只有第一个会起作用。这是一般情况，后面会介绍特殊情况。

        三、CoordinatorLayout对fitsSystemWindows的个性化。API 21 以上可以通过调用View的
            setOnApplyWindowInsetsListener(OnApplyWindowInsetsListener)函数，改变fitsSystemWindows的默认行为。
            在OnApplyWindowInsetsListener的onApplyWindowInsets函数，可以决定如何处理statusbar的空间。
        */

//        ViewGroup mContentView = (ViewGroup) window.findViewById(Window.ID_ANDROID_CONTENT);
//        View mChildView = mContentView.getChildAt(0);
//        if (mChildView != null) {
//            //注意不是设置 ContentView 的 FitsSystemWindows, 而是设置 ContentView 的第一个子 View .
//            // 预留出系统 View 的空间.
//            mChildView.setFitsSystemWindows(true);
//        }

    }

    /**
     * 单独抽离给StatusBar填充一个色块
     */
    public static void addStatusColor(Activity activity, @ColorInt int color) {
        //透明化状态栏这样便拥有使用statusbar的空间
        Window window = activity.getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        //为在状态栏填充一个同样大小的视图---必须配合着fitSystemWindow
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusView = new View(activity);
        int statusHeight = getStatusBarHeight(activity);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, statusHeight);
        layoutParams.gravity = Gravity.TOP;
        statusView.setLayoutParams(layoutParams);
        statusView.setBackgroundColor(color);
        decorViewGroup.addView(statusView, layoutParams);
    }


    //------------------------little tools


    //------------------------Android

    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static String getVerionName(Context context) {
        String verName = "";
        try {
            verName = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }

    //安卓5.0以上只能得到自己的信息
    public static boolean judgeAppOnForgound(String packageName, Context context) {

        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        List<ActivityManager.RunningAppProcessInfo> runningAppProcessInfos = activityManager.getRunningAppProcesses();

        for (ActivityManager.RunningAppProcessInfo runningAppProcessInfo : runningAppProcessInfos) {

            int curProcessPid = runningAppProcessInfo.pid;

            Log.e("zbv", "processName=" + runningAppProcessInfo.processName
                    + ";curProcessPid=" + curProcessPid);

            if (runningAppProcessInfo.processName.equals(packageName)) {

                if (runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND_SERVICE
                        || runningAppProcessInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_TOP_SLEEPING) {

                    return true;

                }

            }
        }
        return false;
    }

    /**
     * 获取进程号对应的进程名
     *
     * @param pid 进程号
     * @return 进程名
     */
    public static String getProcessName(int pid) {
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader("/proc/" + pid + "/cmdline"));
            String processName = reader.readLine();
            if (!TextUtils.isEmpty(processName)) {
                processName = processName.trim();
            }
            return processName;
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                }
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }

    //------------------------Android Install And UnInstall
    /* 安装apk
     * android自带了一个安装程序---/system/app/PackageInstaller.apk.大多数情况下，我们手机上安装应用都是通过这个apk来安装
     */
    public static void installApk(Activity context, String fileName) {
        // /storage/emulated/0/Android/data/com.telit.smartclass.desktop/files/wisdomclass-v3.0.apk
        QZXTools.logE("installApk fileName=" + fileName, null);
        //方式一
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            Uri uri = FileProvider.getUriForFile(context, context.getPackageName()
                    + ".fileprovider", new File(fileName));
            intent.setDataAndType(uri, "application/vnd.android.package-archive");
        } else {
            intent.setDataAndType(Uri.parse("file://" + fileName), "application/vnd.android.package-archive");
        }
        context.startActivity(intent);

        //方式二
//        Uri packageURI = Uri.fromFile(new File(fileName));
//        if (packageURI != null) {
//            Intent installIntent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
//            installIntent.setData(packageURI);
//            installIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(installIntent);
//        }
    }


    /**
     * 安装应用
     *
     * @param file
     */
    public static void installApk(Context context, File file) {
        Log.i(TAG, "安装应用");

        try {
            Intent intent = new Intent();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(context.getApplicationContext(), context.getPackageName() + ".fileprovider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
            }
            intent.setAction(Intent.ACTION_VIEW);
            context.startActivity(intent);

        } catch (Exception e) {
            Log.i("OkGo ", e.toString());
        }
    }


    /* 卸载apk */
    public static void uninstallApk(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }
	
	/*-----------------px适配各种分辨率方案------------------------
    import java.io.File;
    import java.io.FileNotFoundException;
    import java.io.FileOutputStream;
    import java.io.PrintWriter;

    public class MakeXml {

        private final static String rootPath = "C:\\Users\\Administrator\\Desktop\\layoutroot\\values-{0}x{1}\\";

        private final static float dw = 320f;
        private final static float dh = 480f;

        private final static String WTemplate = "<dimen name=\"x{0}\">{1}px</dimen>\n";
        private final static String HTemplate = "<dimen name=\"y{0}\">{1}px</dimen>\n";

        public static void main(String[] args) {
            makeString(320, 480);
            makeString(480,800);
            makeString(480, 854);
            makeString(540, 960);
            makeString(600, 1024);
            makeString(720, 1184);
            makeString(720, 1196);
            makeString(720, 1280);
            makeString(768, 1024);
            makeString(800, 1280);
            makeString(1080, 1812);
            makeString(1080, 1920);
            makeString(1440, 2560);
        }

        public static void makeString(int w, int h) {

            StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            sb.append("<resources>");
            float cellw = w / dw;
            for (int i = 1; i < 320; i++) {
                sb.append(WTemplate.replace("{0}", i + "").replace("{1}",
                        change(cellw * i) + ""));
            }
            sb.append(WTemplate.replace("{0}", "320").replace("{1}", w + ""));
            sb.append("</resources>");

            StringBuffer sb2 = new StringBuffer();
            sb2.append("<?xml version=\"1.0\" encoding=\"utf-8\"?>\n");
            sb2.append("<resources>");
            float cellh = h / dh;
            for (int i = 1; i < 480; i++) {
                sb2.append(HTemplate.replace("{0}", i + "").replace("{1}",
                        change(cellh * i) + ""));
            }
            sb2.append(HTemplate.replace("{0}", "480").replace("{1}", h + ""));
            sb2.append("</resources>");

            String path = rootPath.replace("{0}", h + "").replace("{1}", w + "");
            File rootFile = new File(path);
            if (!rootFile.exists()) {
                rootFile.mkdirs();
            }
            File layxFile = new File(path + "lay_x.xml");
            File layyFile = new File(path + "lay_y.xml");
            try {
                PrintWriter pw = new PrintWriter(new FileOutputStream(layxFile));
                pw.print(sb.toString());
                pw.close();
                pw = new PrintWriter(new FileOutputStream(layyFile));
                pw.print(sb2.toString());
                pw.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

        }

        public static float change(float a) {
            int temp = (int) (a * 100);
            return temp / 100f;
        }
    }
    *-----------------px适配各种分辨率方案------------------------
    */

    /**
     * 提取每个汉字的首字母
     *
     * @param str
     * @return
     */
    public static String getPinYinHeadChar(String str) {
        String convert = "";
        for (int i = 0; i < str.length(); i++) {
            char word = str.charAt(i);
            //提取汉字的首字母
            String[] pinyinArray = PinyinHelper.toHanyuPinyinStringArray(word);
            if (pinyinArray != null) {
                convert += pinyinArray[0].charAt(0);
            } else {
                convert += word;
            }
        }
        return convert.toUpperCase();
    }

    public static String getDeviceSN() {
        String serialNumber = Build.SERIAL;
        return serialNumber;
    }

    /* @author suncat
  2  * @category 判断是否有外网连接（普通方法不能判断外网的网络是否连接，比如连接上局域网）
  3  * @return
  4  */
    public static final boolean ping() {

        String result = null;
        try {
            String ip = "www.baidu.com";// ping 的地址，可以换成任何一种可靠的外网
            Process p = Runtime.getRuntime().exec("ping -c 3 -w 100 " + ip);// ping网址3次
            // 读取ping的内容，可以不加
            InputStream input = p.getInputStream();
            BufferedReader in = new BufferedReader(new InputStreamReader(input));
            StringBuffer stringBuffer = new StringBuffer();
            String content = "";
            while ((content = in.readLine()) != null) {
                stringBuffer.append(content);
            }
            Log.d("------ping-----", "result content : " + stringBuffer.toString());
            // ping的状态
            int status = p.waitFor();
            if (status == 0) {
                result = "success";
                return true;
            } else {
                result = "failed";
            }
        } catch (IOException e) {
            result = "IOException";
        } catch (InterruptedException e) {
            result = "InterruptedException";
        } finally {
            Log.d("----result---", "result = " + result);
        }
        return false;
    }

}
