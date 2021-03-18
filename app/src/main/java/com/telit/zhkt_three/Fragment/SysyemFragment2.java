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

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.SummerLayout;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.JavaBean.AppListBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

public class SysyemFragment2 extends Fragment {

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

    private void initview(View view) {
        summerLayout = view.findViewById(R.id.vp_item_summerlayout);
    }
    private void initData() {
        Bundle bundle = getArguments();
        AppListBean appListBean= (AppListBean) bundle.getSerializable("applist");
        List<AppInfo> applists = appListBean.getDatas();

        List<AppInfo> curAppInfos = new ArrayList<>();
        List<AppInfo> appInfos = applists.subList(24, applists.size());
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

        summerLayout.setAppInfos(curAppInfos);
    }

}
