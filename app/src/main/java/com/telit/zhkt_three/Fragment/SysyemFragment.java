package com.telit.zhkt_three.Fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.CustomView.SummerLayout;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.JavaBean.AppListBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

public class SysyemFragment extends Fragment {

    private SummerLayout summerLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view=  (LinearLayout) LayoutInflater.from
                (container.getContext()).inflate(R.layout.vp_item_layout_landscape, container, false);


        initview(view);
        initData();
        return view;
    }

    private void initData() {
        List<AppInfo> appInfos;
        Bundle bundle = getArguments();
        AppListBean appListBean= (AppListBean) bundle.getSerializable("applist");
        List<AppInfo> applists = appListBean.getDatas();

        List<AppInfo> curAppInfos = new ArrayList<>();
        if (applists.size()<12){
            appInfos = applists.subList(0, applists.size());
        }else {
             appInfos = applists.subList(0, 12);
        }

     //  appInfos = applists.subList(0, 12);

        for (int i = 0; i < appInfos.size(); i++) {

            View view = LayoutInflater.from(getContext()).inflate(R.layout.vp_item_child_layout, null);
            ImageView iconImg = view.findViewById(R.id.vp_item_icon);
            TextView titleTv = view.findViewById(R.id.vp_item_text);


            Drawable drawable = QZXTools.getIconFromPackageName(getContext(),
                    appInfos.get(i).getPackageName());
            if (drawable != null) {
                iconImg.setImageDrawable(drawable);
            }


            titleTv.setText(appInfos.get(i).getName());
            summerLayout.addView(view);

            AppInfo appInfo = new AppInfo();
            appInfo.setIsSystemApp(appInfos.get(i).getIsSystemApp());
            appInfo.setPackageName(appInfos.get( i).getPackageName());
            appInfo.setOrderNum(appInfos.get(i).getOrderNum());
            appInfo.setName(appInfos.get( i).getName());
            curAppInfos.add(appInfo);
        }

        QZXTools.logE("curAppInfo=" + curAppInfos, null);

        QZXTools.logE("appInfos=" + new Gson().toJson(appInfos), null);

        summerLayout.setAppInfos(curAppInfos);
    }

    private void initview(View view) {
        summerLayout = view.findViewById(R.id.vp_item_summerlayout);
    }
}
