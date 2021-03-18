package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
 * 判断题展示视图
 */
public class JudgeSelectToShowView extends RelativeLayout {

    private HtmlTextView option_show_htv;

    public JudgeSelectToShowView(Context context) {
        this(context, null);
    }

    public JudgeSelectToShowView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JudgeSelectToShowView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.judge_select_option_show_layout, this, true);

        option_show_htv = view.findViewById(R.id.option_show_htv);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "PingFang-SimpleBold.ttf");
        option_show_htv.setTypeface(typeface);

    }

    /**
     * 填充选项数据
     * 例如：A、答案是就是A
     */
    public void fillDatas(String optionString) {
        option_show_htv.setHtml(optionString, new HtmlHttpImageGetter(option_show_htv));
    }
}
