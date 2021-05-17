package com.telit.zhkt_three.Adapter.interactive;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hjq.toast.ToastUtils;
import com.telit.zhkt_three.Activity.InteractiveScreen.InteractiveActivity;
import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.InterActive.ServerIpInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.customNetty.SimpleClientListener;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;



/**
 * author: qzx
 * Date: 2019/5/13 16:44
 */
public class RVSelectClazzAdapter extends RecyclerView.Adapter<RVSelectClazzAdapter.RVSelectClazzHolder> {

    private SelectClassActivity mContext;
    private CopyOnWriteArrayList<ServerIpInfo> mData;

    public RVSelectClazzAdapter(SelectClassActivity context, CopyOnWriteArrayList<ServerIpInfo> list) {
        mContext = context;
        mData = list;
    }

    @NonNull
    @Override
    public RVSelectClazzHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new RVSelectClazzHolder(LayoutInflater.from(mContext).inflate(R.layout.rv_clazz_item_layout, viewGroup, false));
    }
    @Override
    public void onBindViewHolder(@NonNull RVSelectClazzHolder rvSelectClazzHolder, int i) {
        rvSelectClazzHolder.tv_clazz.setText(mData.get(i).getClassName());
    }

    @Override
    public int getItemCount() {
        return mData != null ? mData.size() : 0;
    }

    public void setData(CopyOnWriteArrayList<ServerIpInfo> valueServerIpInfos) {
        mData = valueServerIpInfos;
    }
    public class RVSelectClazzHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tv_clazz;
        private TextView join_clazz;

        public RVSelectClazzHolder(@NonNull View itemView) {
            super(itemView);
            tv_clazz = itemView.findViewById(R.id.tv_select_class);
            join_clazz = itemView.findViewById(R.id.tv_join_class);

            join_clazz.setOnClickListener(this);
        }
        @Override
        public void onClick(View v) {

            try {

                //开启连接服务
                UrlUtils.SocketIp = mData.get(getLayoutPosition()).getDeviceIp();
                UrlUtils.SocketPort = Integer.valueOf(mData.get(getLayoutPosition()).getDevicePort());

//                Intent intent = new Intent(mContext, SimpleSocketLinkServer.class);
//                intent.setAction(Constant.SOCKET_CONNECT_ACTION);
//                intent.setPackage(mContext.getPackageName());
//                mContext.startService(intent);
                //把IP和端口保存到本地

                String path = QZXTools.getExternalStorageForFiles(mContext, null) + "/config.txt";
                Properties properties = QZXTools.getConfigProperties(path);
                properties.setProperty("rootIp", UrlUtils.BaseUrl);
                properties.setProperty("socketIp", UrlUtils.SocketIp);
                properties.setProperty("socketPort", UrlUtils.SocketPort + "");
                properties.setProperty("imgIp", UrlUtils.ImgBaseUrl);
                properties.setProperty("pointIp", UrlUtils.MaiDianUrl);
                FileOutputStream fos = new FileOutputStream(path);
                properties.store(new OutputStreamWriter(fos, "UTF-8"),
                        "Config");

                //关闭服务
                EventBus.getDefault().post("closeSelverStop","closeSelverStop");
              /*  SimpleClientNetty.getInstance().init( UrlUtils.SocketIp ,UrlUtils.SocketPort);
                //教师端已经关闭
                SimpleClientNetty.getInstance().setSimpleClientListener(new SimpleClientListener() {
                    @Override
                    public void onLine() {

                    }

                    @Override
                    public void offLine() {

                    }

                    @Override
                    public void receiveData(String msgInfo) {

                    }

                    @Override
                    public void isNoUser() {
                        ToastUtils.show("教师端已关闭");
                        mData.remove(mData.get(getLayoutPosition()));
                        notifyDataSetChanged();

                    }
                });*/


                Intent intent1 = new Intent(mContext, InteractiveActivity.class);
                mContext.startActivity(intent1);
                mContext.finish();





                //加入课堂埋点
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("teacherId",mData.get(getLayoutPosition()).getTeacherId());
                String uuid = UUID.randomUUID().toString();
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("joinClassStudent",uuid);
                BuriedPointUtils.buriedPoint("2003","","","",uuid);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }



        }
    }



}
