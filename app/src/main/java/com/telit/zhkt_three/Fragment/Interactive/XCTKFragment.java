package com.telit.zhkt_three.Fragment.Interactive;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/7/30 8:52
 */
public class XCTKFragment extends Fragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.xctk_time)
    TextView xctk_time;
    @BindView(R.id.xctk_viewpager)
    ViewPager xctk_viewpager;
    @BindView(R.id.xctk_left)
    ImageView xctk_left;
    @BindView(R.id.xctk_right)
    ImageView xctk_right;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_xctk_layout, container, false);
        ButterKnife.bind(this, view);


        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.xctk_left:
                break;
            case R.id.xctk_right:
                break;
        }
    }
}
