package com.telit.zhkt_three.Utils;

import android.app.Activity;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.MyApplication;
import com.zbv.basemodel.FileReceiveDialog;
import com.zbv.basemodel.OkHttp3_0Utils;
import com.zbv.basemodel.QZXTools;
import com.zbv.basemodel.TipsDialog;
import com.zbv.basemodel.UpdateAppBean;
import com.zbv.basemodel.UpdateBean;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Properties;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2020/3/19 16:58
 * <p>
 * notes：引入的Context要是ActivityCompat的Context,因为tipDialog需要getSupportFragmentManager()
 */
public class CheckVersionUtil {
    private static final int NEET_SERVER = 3;
    private  MyHandler myHandler=new MyHandler();;
    private static final int Update_App_Dialog = 0;
    private static final int Update_Check_Failed = 1;
    private static final int Is_New_Version = 2;
    private Activity context;
    public static final String TAG="CheckVersionUtil";
    private CheckVersionUtil(){}
    private static CheckVersionUtil instance=new CheckVersionUtil();

    public static CheckVersionUtil getInstance(){
        synchronized (CheckVersionUtil.class){
           return instance;
        }
    }
    public  void requestCheckVersion(final Activity context)  {
        Log.i(TAG, "requestCheckVersion: 我多次被调用");
        this.context = context;
        //获取版本升级的url
        String path = QZXTools.getExternalStorageForFiles(context, null) + "/config.txt";
        Properties properties = QZXTools.getConfigProperties(path);
       String url =  UrlUtils.BaseUrl + UrlUtils.AppUpdate;
      // url=UrlUtils.BaseUrl+url;
       // String url = "http://192.168.110.207:8080/download/wisdomclass.apk";
        //String url = "http://resource.ahtelit.com/filesystem/softupdate/wisdomclass-v3.0.apk";

        OkHttp3_0Utils.getInstance(context).asyncGetOkHttp(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e.getCause() instanceof SocketTimeoutException){
                    return;
                }
                QZXTools.logE("检测更新版本失败 " + e, null);
                myHandler.sendEmptyMessage(Update_Check_Failed);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String resultJson = response.body().string();
                    QZXTools.logE("resultJson=" + resultJson, null);
                    try {
                        Gson gson = new Gson();
                        UpdateAppBean updateAppBean = gson.fromJson(resultJson, UpdateAppBean.class);
                        UpdateBean updateBean = updateAppBean.getResult().get(0);
                        int currentCode = QZXTools.getVersionCode(context);
                        int newCode = updateBean.getVersionCode();
                        if (currentCode < newCode) {
                            Message message = myHandler.obtainMessage();
                            message.what = Update_App_Dialog;
                            message.obj = updateBean;
                            myHandler.sendMessage(message);
                        } else {
                            Log.i(TAG, "onResponse: 获取版本更新");
                            myHandler.sendEmptyMessage(Is_New_Version);
                        }
                    }catch (Exception e){
                        e.fillInStackTrace();
                        myHandler.sendEmptyMessage(NEET_SERVER);
                    }

                } else {
                    QZXTools.logE("检测版本应该是404", null);
                    myHandler.sendEmptyMessage(Update_Check_Failed);
                }
            }
        });
    }
    public  class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case NEET_SERVER:
                  //  QZXTools.popToast(MyApplication.getInstance(),"当前网络不佳....",true);
                    break;
                case Update_App_Dialog:

                    //有新版本
                    UpdateBean updateBean = (UpdateBean) msg.obj;
                    TipsDialog tipsDialog = new TipsDialog();
                    tipsDialog.setTipsStyle("有新版本更新！\n最新版本：" + updateBean.getVersionName() +
                                    "\n当前版本：" + QZXTools.getVerionName(MyApplication.getInstance()) +
                                    "\n版本描述：" + updateBean.getDescription(),
                            "忽略更新", "立即更新", -1);
                    tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                        @Override
                        public void cancle() {
                            tipsDialog.dismissAllowingStateLoss();
                        }

                        @Override
                        public void confirm() {
                            tipsDialog.dismissAllowingStateLoss();
                            //进入下载
                            FileReceiveDialog fileReceiveDialog = new FileReceiveDialog();
                            fileReceiveDialog.setFileBodyString(updateBean.getUpdateUrl(), context);

                            fileReceiveDialog.show(((FragmentActivity)context ).getSupportFragmentManager(),
                                    FileReceiveDialog.class.getSimpleName());
                        }
                    });
                    tipsDialog.show(((FragmentActivity) context).getSupportFragmentManager(),
                            TipsDialog.class.getSimpleName());
                    break;
                case Update_Check_Failed:
                    Toast.makeText(MyApplication.getInstance(), "检测更新失败，请重试！", Toast.LENGTH_SHORT).show();
                    break;
                case Is_New_Version:
                    //Log.i(TAG, ": Is_New_Version"+Is_New_Version);
                    //Toast.makeText(MyApplication.getInstance(), "已是最新版本", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    }
}
