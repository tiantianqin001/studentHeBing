package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/5/29 16:17
 * t头像+姓名+班级的样式，作为界面头布局的重要的一部分
 */
public class CustomHeadLayout extends RelativeLayout {
    private CircleImageView avatar;
    private TextView tv_name;
    private TextView tv_clazz;

    public CustomHeadLayout(Context context) {
        this(context, null);
    }

    public CustomHeadLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CustomHeadLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.custom_head_layout, this, true);
        avatar = findViewById(R.id.head_avatar);
        tv_name = findViewById(R.id.head_name);
        tv_clazz = findViewById(R.id.head_clazz);
    }

    /**
     * 设置头布局数据
     */
    public void setHeadInfo(String photo, String name, String clazz) {
        Glide.with(this).load(photo).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(avatar);
        tv_name.setText(name);
        tv_clazz.setText(clazz);
    }
}
