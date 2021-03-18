package com.telit.zhkt_three.Activity.PersonalSpace;

import android.content.Context;
import android.content.Intent;
import android.net.http.SslError;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;
import android.util.Log;
import android.webkit.CookieSyncManager;
import android.webkit.ValueCallback;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.AfterHomeWork.NewJobReportActivity;
import com.telit.zhkt_three.Activity.HomeScreen.PersonInfoActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.MistakesCollectionActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.tbs.TBSHeadView;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;


import org.json.JSONException;
import org.json.JSONObject;
import org.xwalk.core.XWalkActivity;
import org.xwalk.core.XWalkHttpAuthHandler;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.LinkedHashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 1、进度加载
 * 2、弹出更多：分享、浏览器打开、调节字体【五中枚举类别】
 */
public class PersonalSpaceActivity extends XWalkActivity {



    private TBSHeadView tbsHeadView;
    private XWalkView xWalkWebView;
    private XWalkSettings xWVSettings;
    private static final String TAG="PersonalSpaceActivity";


    private static final int Server_Error = 0x1;
    private static final int Operator_Success = 0x7;
    private static final int Operator_Error = 0x8;

    private static boolean isShow=false;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Server_Error:
                    if (isShow){
                        QZXTools.popToast(PersonalSpaceActivity.this, "服务端错误", false);

                    }

                    break;
                case Operator_Success:
                    if (isShow){
                        String visitUrl = (String) msg.obj;
                        //访问个人空间
                        xWalkWebView.loadUrl(visitUrl);
                    }

                    break;
                case Operator_Error:
                    if (isShow){

                        QZXTools.popToast(PersonalSpaceActivity.this, (String) msg.obj, false);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onXWalkReady() {
        XWalkPreferences.setValue(XWalkPreferences.REMOTE_DEBUGGING, true);
//        XWalkPreferences.setValue(XWalkPreferences.ANIMATABLE_XWALK_VIEW, true);

        XWalkPreferences.setValue(XWalkPreferences.JAVASCRIPT_CAN_OPEN_WINDOW, true);
        XWalkPreferences.setValue(XWalkPreferences.ALLOW_UNIVERSAL_ACCESS_FROM_FILE, true);
        XWalkPreferences.setValue(XWalkPreferences.SUPPORT_MULTIPLE_WINDOWS, true);
//        XWalkPreferences.setValue(XWalkPreferences.PROFILE_NAME, true);
        XWalkPreferences.setValue(XWalkPreferences.SPATIAL_NAVIGATION, true);
//        XWalkPreferences.setValue(XWalkPreferences.ENABLE_THEME_COLOR, true);
        XWalkPreferences.setValue(XWalkPreferences.ENABLE_JAVASCRIPT, true);
        XWalkPreferences.setValue(XWalkPreferences.ENABLE_EXTENSIONS, true);

        //获取setting
        xWVSettings = xWalkWebView.getSettings();
        xWVSettings.setSupportZoom(true);//支持缩放
        xWVSettings.setBuiltInZoomControls(true);//可以任意缩放
        xWVSettings.setLoadWithOverviewMode(true);
        xWVSettings.setUseWideViewPort(true);//将图片调整到适合webview的大小
        xWVSettings.setLoadsImagesAutomatically(true);
        //调用JS方法.安卓版本大于17,加上注解@JavascriptInterface
        xWVSettings.setJavaScriptEnabled(true);//支持JS
        xWVSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        xWVSettings.setSupportMultipleWindows(false);

        xWVSettings.setAllowFileAccess(true);
        xWVSettings.setDomStorageEnabled(true);
        xWVSettings.setCacheMode(WebSettings.LOAD_NO_CACHE);

        xWVSettings.setAllowUniversalAccessFromFileURLs(true);
//        xWVSettings.setMediaPlaybackRequiresUserGesture(true);

        xWalkWebView.setUIClient(new XWalkUIClient(xWalkWebView) {
            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                super.onPageLoadStarted(view, url);
            }

            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                return super.onJsAlert(view, url, message, result);
            }

            @Override
            public void onScaleChanged(XWalkView view, float oldScale, float newScale) {
                if (view != null) {
                    view.invalidate();
                }
                super.onScaleChanged(view, oldScale, newScale);
            }

            @Override
            public void onPageLoadStopped(XWalkView view, String url, LoadStatus status) {
                super.onPageLoadStopped(view, url, status);
            }

            @Override
            public boolean onCreateWindowRequested(XWalkView view, InitiateBy initiator, final ValueCallback<XWalkView> callback) {
                // Create a temporary XWalkView instance and set a custom XWalkUIClient
                // to it with the setUIClient method. The url is passed as a parameter to the
                // XWalkUIClient.onPageLoadStarted method.
                Log.e(TAG, "onCreateWindowRequested　地址：" + view.getUrl());
                return true;
            }
        });

        xWalkWebView.setResourceClient(new XWalkResourceClient(xWalkWebView) {
            @Override
            public boolean shouldOverrideUrlLoading(XWalkView view, String url) {
//                Log.e(TAG, "shouldOverrideUrlLoading url : " + url);
                view.loadUrl(url);
                return true;
            }

            @Override
            public XWalkWebResourceResponse shouldInterceptLoadRequest(XWalkView view, XWalkWebResourceRequest request) {
//                Log.e(TAG, "shouldInterceptLoadRequest url : " + equest.getUrl().toString());
                return super.shouldInterceptLoadRequest(view, request);
            }

            @Override
            public void onReceivedSslError(XWalkView view, ValueCallback<Boolean> callback, SslError error) {
                //super.onReceivedSslError(view, callback, error);
                Log.e(TAG, "onReceivedSslError");

            }

            @Override
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);

            }

            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
            }

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
            }


            @Override
            public void onDocumentLoadedInFrame(XWalkView view, long frameId) {
                super.onDocumentLoadedInFrame(view, frameId);
            }

            @Override
            public void onReceivedHttpAuthRequest(XWalkView view, XWalkHttpAuthHandler handler, String host, String realm) {
                super.onReceivedHttpAuthRequest(view, handler, host, realm);
            }

            @Override
            public void onReceivedLoadError(XWalkView view, int errorCode, String description, String failingUrl) {
                super.onReceivedLoadError(view, errorCode, description, failingUrl);
            }

            @Override
            public void onReceivedResponseHeaders(XWalkView view, XWalkWebResourceRequest request, XWalkWebResourceResponse response) {
                super.onReceivedResponseHeaders(view, request, response);
            }
        });

        //获取单点免登陆权限
        fetchNoLoginPermission();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN
//                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
//                | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);

        setContentView(R.layout.activity_personal_space);
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        isShow=true;

        EventBus.getDefault().register(this);

        tbsHeadView = findViewById(R.id.tbsHeadView);

          xWalkWebView = findViewById(R.id.personal_space_webview);

        ProgressBar mPageLoadingProgressBar = findViewById(R.id.progressBar);
        mPageLoadingProgressBar.setMax(100);
        mPageLoadingProgressBar.setProgressDrawable(this.getResources()
                .getDrawable(R.drawable.color_progressbar));
    }


    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);

        mHandler.removeCallbacksAndMessages(null);
        isShow=false;
        QZXTools.setmToastNull();

        super.onDestroy();
        //退出个人中心埋点
        BuriedPointUtils.buriedPoint("2036","","","","");
    }

    @Subscriber(tag = "reportJson", mode = ThreadMode.MAIN)
    public void getReportJsonCallback(String reportJson) {
        QZXTools.logE("getReportJsonCallback = " + reportJson, null);

        // getReportJsonCallback = {"operation":"jumpMistakeCollection"}
        try {
            JSONObject jsonObject = new JSONObject(reportJson);
            String sign = jsonObject.getString("operation");
            if (!TextUtils.isEmpty(sign)) {
                //进入错题集界面
                Intent intent = new Intent(this, MistakesCollectionActivity.class);
                startActivity(intent);
                finish();
                return;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            QZXTools.logE("getReportJsonCallback Json Exception", e);
        }


        //跳转到个人作业分析报告界面
        Intent intent = new Intent(this, NewJobReportActivity.class);
        intent.putExtra("Report_Json", reportJson);
        startActivity(intent);

        finish();
    }

    private static final String PersonalSpaceUrl = "http://www.ahedu.cn/SNS/index.php";

    /**
     * 获取某Url单点免登陆权限
     */
    private void fetchNoLoginPermission() {
        WifiManager wm = (WifiManager) MyApplication.getInstance().getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        String wlan_mac = wm.getConnectionInfo().getMacAddress();

        String deviceId = md5(wlan_mac);

        String url = "http://open.ahjygl.gov.cn/sso-oauth/client/authorizeUrl";
        Map<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("appkey", Constant.EduAuthAppKey);
        paramMap.put("tgt", UserUtils.getTgt());
        paramMap.put("client", "pc");//一定要传递正确
        paramMap.put("mac", wlan_mac);
        paramMap.put("deviceId", deviceId);
        paramMap.put("url", PersonalSpaceUrl);

        OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, paramMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mHandler.sendEmptyMessage(Server_Error);
            }

            @Override
            public void onResponse(Call call, Response response) {
                Log.i(TAG, "onResponse: "+response.body().toString());
                if (response.isSuccessful()) {
                    try {
                        String resultJson = response.body().string();//只能使用一次response.body().string()
                        QZXTools.logE("response=" + resultJson, null);
                        Gson gson = new Gson();
                        Map<String, Object> map = gson.fromJson(resultJson, new TypeToken<Map<String, Object>>() {
                        }.getType());
                        QZXTools.logE("data=" + map.get("data"), null);
                        if (map.get("code").equals("1")) {
                            //成功
                            Message message = mHandler.obtainMessage();
                            message.what = Operator_Success;
                            message.obj = map.get("data");
                            mHandler.sendMessage(message);
                        } else if (map.get("code").equals("-1")) {
                            //参数丢失
                            Message message = mHandler.obtainMessage();
                            message.what = Operator_Error;
                            message.obj = "参数丢失";
                            mHandler.sendMessage(message);
                        } else if (map.get("code").equals("-2")) {
                            //tgt失效
                            Message message = mHandler.obtainMessage();
                            message.what = Operator_Error;
                            message.obj = "tgt失效";
                            mHandler.sendMessage(message);
                        } else if (map.get("code").equals("-3")) {
                            //参数校验失败
                            Message message = mHandler.obtainMessage();
                            message.what = Operator_Error;
                            message.obj = "参数校验失败";
                            mHandler.sendMessage(message);
                        } else if (map.get("code").equals("-4")) {
                            //用户切换登录失败
                            Message message = mHandler.obtainMessage();
                            message.what = Operator_Error;
                            message.obj = "用户切换登录失败";
                            mHandler.sendMessage(message);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                        mHandler.sendEmptyMessage(Server_Error);
                    }

                }
            }
        });
    }

    // MD5加密，32位小写
    public static String md5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
        md5.update(str.getBytes());
        byte[] md5Bytes = md5.digest();
        StringBuilder hexValue = new StringBuilder();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = ((int) md5Bytes[i]) & 0xff;
            if (val < 16) {
                hexValue.append("0");
            }
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
