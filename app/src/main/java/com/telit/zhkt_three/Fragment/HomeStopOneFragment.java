package com.telit.zhkt_three.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.telit.zhkt_three.Activity.InteractiveScreen.InteractiveActivity;
import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.Adapter.RVHomeAdapter;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeStopOneFragment extends Fragment {
    /*private static final int[] TYPES_IMGS = { R.mipmap.preview, R.mipmap.homework_after, R.mipmap.cuotiji,
            R.mipmap.auto_learning, R.mipmap.personal_space, R.mipmap.class_record};*/

    private static final int[] TYPES_IMGS = { R.mipmap.preview, R.mipmap.homework_after, R.mipmap.cuotiji,
            R.mipmap.auto_learning};
    private RecyclerView recyclerView;

    private List<Integer> typesList=new ArrayList<>();
    private ImageView iv_fist_viewpage_show;
    private ImageView iv_fist_viewpage_shop;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from
                (container.getContext()).inflate(R.layout.vp_item_layout_rv, container, false);

        initView(view);
        initData();

        return view;
    }

    private void initData() {
        typesList.clear();
        for (Integer integer: TYPES_IMGS){
            typesList.add(integer);
        }
        RVHomeAdapter adapter = new RVHomeAdapter(getActivity(),typesList );
        recyclerView.setAdapter(adapter);


        iv_fist_viewpage_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //领创要发home的广播
                lingChang();
                //  mContext.startActivity(new Intent(mContext, InteractiveActivity.class));
                boolean openHome = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getBoolean("openHome");
                if (openHome){

                    getActivity().startActivity(new Intent(getContext(), InteractiveActivity.class));
                }else {

                    getActivity().startActivity(new Intent(getContext(), SelectClassActivity.class));
                }

            }
        });
        iv_fist_viewpage_shop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //领创要发home的广播
                // if (appInfos.size()<=0)return;
                lingChang();
                Intent intent = getContext().getPackageManager().
                        getLaunchIntentForPackage("com.ndwill.swd.appstore");
                if (intent != null) {
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getContext().startActivity(intent);
                }
            }
        });
    }
    private void initView(View layout) {
        recyclerView = layout.findViewById(R.id.vp_item_recyclerview);
        iv_fist_viewpage_show = layout.findViewById(R.id.iv_fist_viewpage_show);
        iv_fist_viewpage_shop = layout.findViewById(R.id.iv_fist_viewpage_shop);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);
       // recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.setLayoutManager(gridLayoutManager);
    }

    private void lingChang() {
        Intent intent = new Intent("com.linspirer.edu.homeaction");
        intent.setPackage("com.android.launcher3");
        getContext().sendBroadcast(intent);
        // Toast.makeText(mContext,"领创发com.linspirer.edu.homeaction广播",Toast.LENGTH_LONG).show();
    }


}
