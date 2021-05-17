package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.EmojiEditText;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/5/25 9:38
 * <p>
 * 填空题
 */
public class FillBlankToDoView extends RelativeLayout {

    private TextView fill_blank_option;
    public EmojiEditText fill_blank_content;

    public FillBlankToDoView(Context context) {
        this(context, null);
    }

    public FillBlankToDoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FillBlankToDoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.fill_blank_option_complete_layout, this, true);
        fill_blank_option = view.findViewById(R.id.fill_blank_option);
        fill_blank_content = view.findViewById(R.id.fill_blank_content);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "PingFang-SimpleBold.ttf");
        fill_blank_option.setTypeface(typeface);
        fill_blank_content.setTypeface(typeface);
    }

    /**
     * 角标文本的答案回显
     *
     * @param titleIndex 代表填空题的前标顺序,从1开始
     */
    public void fillDatas(int titleIndex, String textAnswer) {
        //从1开始
        titleIndex = titleIndex + 1;
        fill_blank_option.setText(titleIndex + "");
        fill_blank_content.setText(textAnswer);
        fill_blank_content.setSelection(textAnswer.length());
    }

    /**
     * 初始化填空题题型数据
     *
     * @param titleIndex 代表填空题的前标顺序,从1开始
     */
    public void fillDatas(int titleIndex) {
        //从1开始
        titleIndex = titleIndex + 1;
        fill_blank_option.setText(titleIndex + "");


    }

    /**
     * 作为普通TV,不聚焦，没有触摸事件响应，例如长按不会有全选等剪切板操作
     */
    public void setNormalTV() {
        fill_blank_content.setFocusable(false);
        fill_blank_content.setFocusableInTouchMode(false);
        fill_blank_content.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });
    }
}
