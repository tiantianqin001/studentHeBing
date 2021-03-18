package com.telit.zhkt_three.Fragment;

import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.Adapter.RVHomeAdapter;
import com.telit.zhkt_three.R;

import java.util.ArrayList;
import java.util.List;

public class HomeStopTwoFragment extends Fragment {
    private static final int[] TYPES_IMGS = {R.mipmap.personal_space, R.mipmap.class_record,
            R.mipmap.zhizhu, R.mipmap.micro_class, R.mipmap.expected};
    private RecyclerView recyclerView;

    private List<Integer> typesList=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from
                (container.getContext()).inflate(R.layout.vp_item_layout_rv1, container, false);

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

    }

    private void initView(View layout) {
        recyclerView = layout.findViewById(R.id.vp_item_recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 4);
        recyclerView.setOverScrollMode(View.OVER_SCROLL_NEVER);
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.set(0, 15, 0, 15);
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
    }


}
