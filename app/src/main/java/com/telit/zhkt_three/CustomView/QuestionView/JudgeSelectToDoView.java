package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
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
public class JudgeSelectToDoView extends RelativeLayout {

    private TextView option_do_tv;
    private HtmlTextView option_do_htv;

    /**
     * 我的答案，没有选择为空,就是key
     */
    private String myAnswer;

    public String getMyAnswer() {
        return myAnswer;
    }

    public void setMyAnswer(String myAnswer) {
        this.myAnswer = myAnswer;
    }

    public JudgeSelectToDoView(Context context) {
        this(context, null);
    }

    public JudgeSelectToDoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JudgeSelectToDoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.judge_select_option_complete_layout, this, true);

        option_do_tv = view.findViewById(R.id.option_do_tv);
        option_do_htv = view.findViewById(R.id.option_do_htv);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "PingFang-SimpleBold.ttf");
        option_do_tv.setTypeface(typeface);
        option_do_htv.setTypeface(typeface);
    }

    /**
     * 状态选中，单选、多选
     */
    public void handSelectedStatus() {
        if (isSelected()) {
            setSelected(false);
            setMyAnswer(null);
        } else {
            setSelected(true);
            setMyAnswer(option_do_tv.getText().toString());
        }
    }

    /**
     * @param option 选项内容：ABCD...
     */
    public void fillOptionAndContent(String option, String optionString) {
        option_do_tv.setText(option);
        if (optionString == null) {
            optionString = "";
        }
        option_do_htv.setHtml(optionString, new HtmlHttpImageGetter(option_do_htv));



    }

    /**
     * i代表ABCD顺序
     */
    public void fillDefaultOptionAndContent(int i, String optionString) {
        switch (i) {
            case 1:
                option_do_tv.setText("A");
                break;
            case 2:
                option_do_tv.setText("B");
                break;
            case 3:
                option_do_tv.setText("C");
                break;
            case 4:
                option_do_tv.setText("D");
                break;
        }
        option_do_htv.setHtml(optionString, new HtmlHttpImageGetter(option_do_htv));
    }

}
