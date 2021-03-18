package com.telit.zhkt_three.CustomView.QuestionView.external;

import android.app.Activity;
import android.app.Fragment;
import android.graphics.Paint;
import android.graphics.RectF;
import android.text.Editable;
import android.text.Html;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTagHandler;
import org.sufficientlysecure.htmltextview.HtmlTextView;
import org.xml.sax.XMLReader;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tangminglong on 17/10/19.
 */

public class SpansManager {
    //    private TextView mTv;
    private HtmlTextView mTv;
    private EditText mEt;
    private List<ReplaceSpan> mSpans;
    public int mOldSpan = -1;
    private RectF mRf;
    private int mFontT; // 字体top
    private int mFontB;// 字体bottom
    private ImmFocus mFocus = new ImmFocus();
    private View view;
    private Activity mActivity;
    private Fragment mFragment;
    private ReplaceSpan mCheckedSpan;//当前选中的空格


//    public SpansManager(Activity ac, TextView tv, EditText et) {
//        this.mActivity = ac;
//        this.mTv = tv;
//        this.mEt = et;
//        mSpans = new ArrayList<>();
//    }
//
//    public SpansManager(Fragment fragment, TextView tv, EditText et) {
//        this.mFragment = fragment;
//        this.mTv = tv;
//        this.mEt = et;
//        mSpans = new ArrayList<>();
//    }
//
//    public SpansManager(View view, TextView tv, EditText et) {
//        this.view = view;
//        this.mTv = tv;
//        this.mEt = et;
//        mSpans = new ArrayList<>();
//    }

    public SpansManager(Activity ac, HtmlTextView tv, EditText et) {
        this.mActivity = ac;
        this.mTv = tv;
        this.mEt = et;
        mSpans = new ArrayList<>();
    }

    public SpansManager(Fragment fragment, HtmlTextView tv, EditText et) {
        this.mFragment = fragment;
        this.mTv = tv;
        this.mEt = et;
        mSpans = new ArrayList<>();
    }

    public SpansManager(View view, HtmlTextView tv, EditText et) {
        this.view = view;
        this.mTv = tv;
        this.mEt = et;
        mSpans = new ArrayList<>();
    }

    public void doFillBlank(String examStr) {
        mTv.setMovementMethod(Method);
        mTv.setHtml(examStr, new HtmlHttpImageGetter(mTv), true, new HtmlTagHandler.FillBlankInterface() {
            @Override
            public void addSpans(Editable output) {
                StringBuilder stringBuilder = new StringBuilder();
                String[] splits = output.toString().split("____");
//                int lastIndex = output.toString().lastIndexOf("____");
//                int length = output.toString().length();
                int index = 0;
                for (int i = 0; i < splits.length; i++) {
//                    //如果最后才是大片空白填空怎么办？通过lastindexof判断把后面的空格都加上；这是第一种方法，第二种是下面我采用的使用____,区分
//                    if (i == (splits.length - 1) && lastIndex == (length - 5)) {
//                        stringBuilder.append(splits[i]);
//                        addSpan(stringBuilder.length(), stringBuilder.length() + 5, index++, output);
//                        stringBuilder.append("____");
//                    } else {
                    if (i < (splits.length - 1)) {
                        stringBuilder.append(splits[i]);
                        addSpan(stringBuilder.length(), stringBuilder.length() + 5, index++, output);
                        stringBuilder.append("____");
                    }
//                    }
                }
            }
        });
    }

    private void addSpan(int startIndex, int endIndex, int index, Spannable spannable) {
        TextPaint paint = new TextPaint(mTv.getPaint());
        paint.setColor(mTv.getResources().getColor(R.color.ggfx_dark_blue));
        ReplaceSpan span = new ReplaceSpan(mTv.getContext(), paint);
        if (mActivity instanceof ReplaceSpan.OnClickListener)
            span.mOnClick = (ReplaceSpan.OnClickListener) mActivity;
        else if (mFragment instanceof ReplaceSpan.OnClickListener) {
            span.mOnClick = (ReplaceSpan.OnClickListener) mFragment;
        } else if (view instanceof ReplaceSpan.OnClickListener) {
            span.mOnClick = (ReplaceSpan.OnClickListener) view;
        } else {
            throw new IllegalArgumentException("unImplements ReplaceSpan.OnClickListener");
        }
        span.mText = "";
        span.id = index;
        mSpans.add(span);
        spannable.setSpan(span, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }


    /**
     * 设置选中样式
     *
     * @param position
     */
    public void setSpanChecked(int position) {
        mCheckedSpan = mSpans.get(position);
        for (int i = 0; i < mSpans.size(); i++) {
            ReplaceSpan replaceSpan = mSpans.get(i);
            if (i == position) {
                replaceSpan.setDrawTextColor(R.color.red);
            } else {
                replaceSpan.setDrawTextColor(R.color.ggfx_dark_blue);
            }
            mTv.invalidate();
        }
    }


    //填充缓存的数据
    public void setData(String str, Object o, int i) {
        if (mTv == null || mSpans == null || mSpans.size() == 0 || i < 0 || i > mSpans.size() - 1)
            return;
        ReplaceSpan span = mSpans.get(i);
        span.mText = str;
        span.mObject = o;
        mTv.invalidate();
    }

    //TextView触摸事件-->Span点击事件
    private LinkMovementMethod Method = new LinkMovementMethod() {

        public boolean onTouchEvent(TextView widget, Spannable buffer,
                                    MotionEvent event) {
            int action = event.getAction();

            if (action == MotionEvent.ACTION_UP ||
                    action == MotionEvent.ACTION_DOWN) {
                int x = (int) event.getX();
                int y = (int) event.getY();

                x -= widget.getTotalPaddingLeft();
                y -= widget.getTotalPaddingTop();

                x += widget.getScrollX();
                y += widget.getScrollY();

                Layout layout = widget.getLayout();
                int line = layout.getLineForVertical(y);
                int off = layout.getOffsetForHorizontal(line, x);

                ReplaceSpan[] link = buffer.getSpans(off, off, ReplaceSpan.class);

                if (link.length != 0) {
                    //Span的点击事件
                    if (action == MotionEvent.ACTION_UP) {
                        link[0].onClick(widget, buffer, false, x, y, line, off);
                    } else if (action == MotionEvent.ACTION_DOWN) {
                        link[0].onClick(widget, buffer, true, x, y, line, off);
//                        Selection.setSelection(buffer,
//                                buffer.getSpanStart(link[0]),
//                                buffer.getSpanEnd(link[0]));
                    }
                    return true;
                } else {
//                    Selection.removeSelection(buffer);
                }
            }
            return false;
        }
    };


    //获取出对应Span的RectF数据
    public RectF drawSpanRect(ReplaceSpan s) {

        Layout layout = mTv.getLayout();
        Spannable buffer = (Spannable) mTv.getText();
        int l = buffer.getSpanStart(s);
        int r = buffer.getSpanEnd(s);
        int line = layout.getLineForOffset(l);
        int l2 = layout.getLineForOffset(r);
        if (mRf == null) {
            mRf = new RectF();
            //Rect rt = new Rect();
            Paint.FontMetrics fontMetrics = mTv.getPaint().getFontMetrics();

            //mTv.getPaint().getTextBounds("TgQyYFJ",0,7,rt);
            mFontT = (int) fontMetrics.ascent;
            mFontB = (int) fontMetrics.descent;
        }
        mRf.left = layout.getPrimaryHorizontal(l);
        mRf.right = layout.getSecondaryHorizontal(r);
        // 通过基线去校准
        line = layout.getLineBaseline(line);
        mRf.top = line + mFontT;
        mRf.bottom = line + mFontB;
        return mRf;
    }


    //设置EditText填空题中的相对位置
    public void setEtXY(RectF rf) {
        //设置et w,h的值
        RelativeLayout.LayoutParams lp = (RelativeLayout.LayoutParams) mEt.getLayoutParams();
        lp.width = (int) (rf.right - rf.left);
        lp.height = (int) (rf.bottom - rf.top);
        //设置et 相对于tv x,y的相对位置
        lp.leftMargin = (int) (mTv.getLeft() + rf.left);
        lp.topMargin = (int) (mTv.getTop() + rf.top);
        mEt.setLayoutParams(lp);
        //获取焦点，弹出软键盘
        mEt.setFocusable(true);
        mEt.requestFocus();
        showImm(true, mEt);
    }

    /**
     * 弹出软键盘
     *
     * @param bOn
     * @param focus
     */
    private void showImm(boolean bOn, View focus) {
        try {
            if (bOn) {
                if (focus != null) {
                    ImmFocus.show(true, focus);
                } else {
                    mFocus.setFocus(focus);
                }
            } else {
                ImmFocus.show(false, null);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public List<String> getMyAnswer() {
        List<String> myAnswerList = new ArrayList<>();
        for (int i = 0; i < mSpans.size(); i++) {
            myAnswerList.add(mSpans.get(i).mText);
        }
        return myAnswerList;
    }

    public ReplaceSpan getCheckedReplaceSpan() {
        return mCheckedSpan;
    }

    public void setLastCheckedSpanText(String editText) {
        if (mCheckedSpan != null) {
            mCheckedSpan.mText = editText;
        }
    }
}
