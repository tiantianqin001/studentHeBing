package com.telit.zhkt_three.Fragment.Dialog;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.Gson.SocketIpBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/7/3 9:54
 * <p>
 * 添加这个属性：setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwd);
 * 会导致TextInputLayout显示的颜色有问题
 */
public class UrlUpdateDialog extends DialogFragment {

    private Unbinder unbinder;

    @BindView(R.id.url_tv_auto)
    TextView url_tv_auto;
    @BindView(R.id.url_change_server_ip)
    EditText url_change_server_ip;
    @BindView(R.id.url_change_socket_ip)
    EditText url_change_socket_ip;
    @BindView(R.id.url_change_socket_port)
    EditText url_change_socket_port;
    @BindView(R.id.url_change_img_server_ip)
    EditText url_change_img_server_ip;
    @BindView(R.id.url_change_point_server_ip)
    EditText url_change_point_server_ip;
    @BindView(R.id.url_change_cancel)
    TextView url_change_cancel;
    @BindView(R.id.url_change_confirm)
    TextView url_change_confirm;

    @BindView(R.id.url_change_point_server_out)
    EditText url_change_point_server_out;

    private String req_socketIp;
    private String req_socketPort;

    private Handler myHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0x7:
                    SocketIpBean.SocketIp socketIp = (SocketIpBean.SocketIp) msg.obj;
                    req_socketIp = socketIp.getServerIp();
                    req_socketPort = socketIp.getServerPort();

                    url_change_server_ip.setText(req_socketIp);
                    url_change_socket_port.setText(req_socketPort);

                    url_change_confirm.performClick();
                    break;
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_url_update_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        url_change_server_ip.setSelection(url_change_server_ip.getText().toString().trim().length());
        url_change_img_server_ip.setSelection(url_change_img_server_ip.getText().toString().trim().length());
        url_change_point_server_ip.setSelection(url_change_point_server_ip.getText().toString().trim().length());

        /**
         * {
         "success": true,
         "errorCode": "1",
         "msg": "操作成功",
         "result": {
         "serverPort": "17113",
         "serverIp": "172.16.5.136"
         },
         "total": 0,
         "pageNo": 0
         }
         * */
        url_tv_auto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = UrlUtils.BaseUrl + UrlUtils.CommunicationIp;
                Map<String, String> paramMap = new HashMap<>();
                paramMap.put("classid", UserUtils.getClassId());
                OkHttp3_0Utils.getInstance().asyncGetOkHttp(url, paramMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        QZXTools.logE("获取通讯Ip失败", null);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        Gson gson = new Gson();
                        String json = response.body().string();
                        SocketIpBean socketIpBean = gson.fromJson(json, SocketIpBean.class);
                        String socketIp = socketIpBean.getResult().getServerIp();
                        String socketPort = socketIpBean.getResult().getServerPort();
                        QZXTools.logE("socketIp=" + socketIp + ";socketPort=" + socketPort, null);

                        Message message = myHandler.obtainMessage();
                        message.what = 0x7;
                        message.obj = socketIpBean.getResult();
                        message.sendToTarget();
                    }
                });
            }
        });

        url_change_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        url_change_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String rootIp = url_change_server_ip.getText().toString().trim();
                String socketIp = url_change_socket_ip.getText().toString().trim();
                String socketPort = url_change_socket_port.getText().toString().trim();
                String imgIp = url_change_img_server_ip.getText().toString().trim();
                //这个是内网的地址
                String pointIp = url_change_point_server_ip.getText().toString().trim();

                //修改成外网的地址
                String outAddress = url_change_point_server_out.getText().toString().trim();

                //配置文件
                String path = QZXTools.getExternalStorageForFiles(getContext(), null) + "/config.txt";
                Properties properties = new Properties();

                if (!TextUtils.isEmpty(rootIp) && !rootIp.equals("http://")) {
                    UrlUtils.BaseUrl = rootIp;
                }

                if (!TextUtils.isEmpty(socketIp)) {
                    UrlUtils.SocketIp = socketIp;
                } else {
                    //判断网络请求IP的是否存在
                    if (!TextUtils.isEmpty(req_socketIp)) {
                        UrlUtils.SocketIp = req_socketIp;
                    }
                }

                if (!TextUtils.isEmpty(socketPort)) {
                    UrlUtils.SocketPort = Integer.parseInt(socketPort);
                } else {
                    //判断网络请求PORT的是否存在
                    if (!TextUtils.isEmpty(req_socketPort)) {
                        UrlUtils.SocketPort = Integer.parseInt(req_socketPort);
                    }
                }

                if (!TextUtils.isEmpty(imgIp) && !imgIp.equals("http://")) {
                    UrlUtils.ImgBaseUrl = imgIp;
                }
                //这个是设置版本升级是内网的地址
                if (!TextUtils.isEmpty(pointIp)) {
                    UrlUtils.AppUpdate="/wisdomclass/interface/soft/softwareUpdate?softType=1&updateType=0";
                }
                //设置版本升级是外网的地址
                if (!TextUtils.isEmpty(outAddress)){
                    UrlUtils.AppUpdate="/wisdomclass/interface/soft/softwareUpdate?softType=1&updateType=1";
                }

                properties.setProperty("rootIp", UrlUtils.BaseUrl);
               /* properties.setProperty("socketIp", UrlUtils.SocketIp);
                properties.setProperty("socketPort", UrlUtils.SocketPort + "");
                properties.setProperty("imgIp", UrlUtils.ImgBaseUrl);*/
                properties.setProperty("uPAddressIp", UrlUtils.AppUpdate);

                try {
                    FileOutputStream fos = new FileOutputStream(path);
                    properties.store(new OutputStreamWriter(fos, "UTF-8"),
                            "Config");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                QZXTools.logE("rootIp=" + UrlUtils.BaseUrl + ";socketIp=" + UrlUtils.SocketIp
                        + ";socketPort=" + UrlUtils.SocketPort + ";imgIp=" + UrlUtils.ImgBaseUrl + ";pointIp=" + UrlUtils.MaiDianUrl, null);

                QZXTools.popCommonToast(getContext(), "连接地址修改成功，请退出后重试", false);

                dismiss();
            }
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        myHandler.removeCallbacksAndMessages(null);
        super.onDestroyView();
    }
}
