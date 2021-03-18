package com.telit.zhkt_three.CustomView.interactive.CCYX;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/8/6 13:56
 */
public class GuessingGameLayout extends RelativeLayout {
    public GuessingGameLayout(Context context) {
        this(context, null);
    }

    public GuessingGameLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuessingGameLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_ccyx_layout, this, true);
    }
}
