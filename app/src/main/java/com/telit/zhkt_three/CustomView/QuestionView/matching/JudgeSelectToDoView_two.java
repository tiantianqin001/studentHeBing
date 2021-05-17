package com.telit.zhkt_three.CustomView.QuestionView.matching;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

/**
 * author: qzx
 * Date: 2019/5/25 9:38
 * <p>
 * 判断题
 */
public class JudgeSelectToDoView_two extends RelativeLayout {

    public RelativeLayout option_do_tv_one;
    public RelativeLayout option_do_tv_two;
    public LinearLayout ll_current_quint_show;
    public ImageView iv_current_quint_show;



    public JudgeSelectToDoView_two(Context context) {
        this(context, null);
    }

    public JudgeSelectToDoView_two(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JudgeSelectToDoView_two(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.judge_select_option_complete_layout_two, this, true);

        option_do_tv_one = view.findViewById(R.id.option_do_tv_one);
        option_do_tv_two = view.findViewById(R.id.option_do_tv_two);
        ll_current_quint_show = view.findViewById(R.id.ll_current_quint_show);
        iv_current_quint_show = view.findViewById(R.id.iv_current_quint_show);


    }

}
