package com.telit.zhkt_three.Fragment.Interactive;


import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.widget.ProgressBar;

import com.telit.zhkt_three.Activity.InteractiveScreen.JsRecordScreenBean;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.XwalkFragment;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import org.xwalk.core.JavascriptInterface;
import org.xwalk.core.XWalkCookieManager;
import org.xwalk.core.XWalkHttpAuthHandler;
import org.xwalk.core.XWalkJavascriptResult;
import org.xwalk.core.XWalkPreferences;
import org.xwalk.core.XWalkResourceClient;
import org.xwalk.core.XWalkSettings;
import org.xwalk.core.XWalkUIClient;
import org.xwalk.core.XWalkView;
import org.xwalk.core.XWalkWebResourceRequest;
import org.xwalk.core.XWalkWebResourceResponse;
/**
 * author: qzx
 * Date: 2019/11/5 9:15
 * <p>
 * 互动加载的WebView
 * <p>
 * 传递参数方式：一、设置 二、setArgument
 */
public class WebViewFragment extends XwalkFragment{
    private String ip;
    private XWalkView webView;
    private String loadUrl;

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    private String interactId;

    private int flag;

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public String getInteractId() {
        return interactId;
    }

    public void setInteractId(String interactId) {
        this.interactId = interactId;
    }



//    private CircleProgressDialogFragment circleProgressDialogFragment;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_webview_interact, container, false);

        loadUrl = UrlUtils.BaseUrl + UrlUtils.WebViewInteract + "?flag=" + flag + "&id=" + interactId
                + "&sign=view&type=student&studentId=" + UserUtils.getUserId();

        QZXTools.logE("loadUrl:"+loadUrl,null);

        if (!TextUtils.isEmpty(ip)) {
            loadUrl = ip;
        }
        webView = view.findViewById(R.id.webView);
        webView.addJavascriptInterface(new RecordScreen(),"androidToCallBack");

        mBar = view.findViewById(R.id.progress_Bar);

        EventBus.getDefault().register(this);
        return view;
    }

    public class RecordScreen {
        /**
         * @param command 1表示开始 0表示结束 2表示暂停
         * @param json    提交答案的Json,录制视频不需要传
         */
        @JavascriptInterface
        public void getRecordScreenCommand(int command, String json) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    QZXTools.logE("js call android ===> json=" + json, null);
                    JsRecordScreenBean jsRecordScreenBean = new JsRecordScreenBean(command, json);
                    EventBus.getDefault().post(jsRecordScreenBean, Constant.Show_Js_Record);
                }
            });
        }
    }

    private XWalkSettings xWVSettings;
    private ProgressBar mBar;
    @SuppressLint("SetJavaScriptEnabled")
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
        xWVSettings = webView.getSettings();
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

        //处理居中的问题
        xWVSettings.setLayoutAlgorithm(XWalkSettings.LayoutAlgorithm.SINGLE_COLUMN);
        xWVSettings.setUseWideViewPort(true);
        xWVSettings.setLoadWithOverviewMode(true);

        xWVSettings.setAllowUniversalAccessFromFileURLs(true);
//        xWVSettings.setMediaPlaybackRequiresUserGesture(true);

        webView.setUIClient(new XWalkUIClient(webView) {
            @Override
            public void onPageLoadStarted(XWalkView view, String url) {
                super.onPageLoadStarted(view, url);
            }

            @Override
            public boolean onJsAlert(XWalkView view, String url, String message, XWalkJavascriptResult result) {
                //Toast.makeText(ProciapalActivity.this, message, Toast.LENGTH_SHORT).show();
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


        });

        webView.setResourceClient(new XWalkResourceClient(webView) {
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
            public void onLoadFinished(XWalkView view, String url) {
                super.onLoadFinished(view, url);
                //加载完成消失

            }

            @Override
            public void onLoadStarted(XWalkView view, String url) {
                super.onLoadStarted(view, url);
            }

            @Override
            public void onProgressChanged(XWalkView view, int progressInPercent) {
                super.onProgressChanged(view, progressInPercent);
                if (progressInPercent == 100) {
                    mBar.setVisibility(View.GONE);
                } else {
                    mBar.setVisibility(View.VISIBLE);
                    mBar.setProgress(progressInPercent);
                }

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

        XWalkCookieManager xWalkCookieManager=new XWalkCookieManager();
        xWalkCookieManager.removeAllCookie();
        //开始加载地址
        webView.loadUrl(loadUrl);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (isXWalkReady()){
            webView.onDestroy();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (isXWalkReady()){
            webView.pauseTimers();
            webView.onHide();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isXWalkReady()){
            webView.resumeTimers();
            webView.onShow();
        }
    }
}
