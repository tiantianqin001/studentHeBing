package com.telit.zhkt_three.CustomView.ClassRecord;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/12/26 20:23
 * <p>
 * 一、背景
 * 二、点击跳转
 */
public class ClassRecordItemView extends RelativeLayout {

    private RelativeLayout class_record_view_relative;
    private TextView class_record_view_time;
    private TextView class_record_view_title;

    public ClassRecordItemView(Context context) {
        this(context, null);
    }

    public ClassRecordItemView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ClassRecordItemView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_class_record, this, true);

        class_record_view_relative = findViewById(R.id.class_record_view_relative);
        class_record_view_time = findViewById(R.id.class_record_view_time);
        class_record_view_title = findViewById(R.id.class_record_view_title);
    }

    /**
     * 填充数据
     */
    public void setDatas(String time, String title, int type) {
        switch (type) {
            case Constant.Class_Record_Shot:
                class_record_view_relative.setBackgroundResource(R.mipmap.jieping);
                break;
            case Constant.Class_Record_Vote:
                class_record_view_relative.setBackgroundResource(R.mipmap.toupiao);
                break;
            case Constant.Class_Record_Discuss:
                class_record_view_relative.setBackgroundResource(R.mipmap.taolun);
                break;
        }

        class_record_view_time.setText(time);
        class_record_view_title.setText(title);
    }

}
