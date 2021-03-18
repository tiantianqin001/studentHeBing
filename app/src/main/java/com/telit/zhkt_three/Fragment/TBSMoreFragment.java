package com.telit.zhkt_three.Fragment;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.DialogFragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.tbs.FixedRulerView;
import com.telit.zhkt_three.CustomView.tbs.RulerView;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import org.apache.commons.codec.language.bm.Rule;

import java.lang.reflect.Method;

/**
 * author: qzx
 * Date: 2019/9/3 10:32
 * <p>
 * 新增x5浏览器更多选项：原生分享URL、浏览器打开、调节浏览器显示的字体大小[Smallest、Smaller、Normal、Larger、Largest]
 */
public class TBSMoreFragment extends BottomSheetDialogFragment implements View.OnClickListener, FixedRulerView.RulerCallback {

    private LinearLayout tbs_more_share;
    private LinearLayout tbs_more_browser;
    private LinearLayout tbs_more_fontAdjust;

    private LinearLayout tbs_more_lines;

    private FixedRulerView rulerView;
    private View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.view_tbs_more, container, false);

        tbs_more_lines = view.findViewById(R.id.tbs_more_lines);

        tbs_more_share = view.findViewById(R.id.tbs_more_share);
        tbs_more_browser = view.findViewById(R.id.tbs_more_browser);
        tbs_more_fontAdjust = view.findViewById(R.id.tbs_more_fontAdjust);

        rulerView = view.findViewById(R.id.rulerView);
        rulerView.setRulerCallback(this);

        tbs_more_share.setOnClickListener(this);
        tbs_more_browser.setOnClickListener(this);
        tbs_more_fontAdjust.setOnClickListener(this);
        return view;
    }

    public void reset() {
        tbs_more_lines.setVisibility(View.VISIBLE);
        rulerView.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbs_more_share:
                EventBus.getDefault().post("share", "BrowserMore");
                break;
            case R.id.tbs_more_browser:
                EventBus.getDefault().post("browser", "BrowserMore");
                break;
            case R.id.tbs_more_fontAdjust:
                tbs_more_lines.setVisibility(View.GONE);
                rulerView.setVisibility(View.VISIBLE);
                break;
        }
    }

    @Override
    public void tbsTextSize(int level) {
        EventBus.getDefault().post("font:" + level, "BrowserMore");
    }
    @Override
    public void onStart() {
        super.onStart();
        fixHeight();
    }

    private void fixHeight(){
        if(null == view){
            return;
        }

        View parent = (View) view.getParent();
        BottomSheetBehavior behavior = BottomSheetBehavior.from(parent);
        view.measure(0, 0);
        behavior.setPeekHeight(view.getMeasuredHeight());

        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) parent.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        parent.setLayoutParams(params);
    }

}
