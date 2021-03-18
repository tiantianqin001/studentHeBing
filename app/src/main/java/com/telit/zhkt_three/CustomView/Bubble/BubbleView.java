package com.telit.zhkt_three.CustomView.Bubble;

import android.content.Context;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/6/20 10:25
 */
public class BubbleView extends View {

    private Paint mBubblePaint;
    private Path mBubblePath;
    private RectF mBubbleRectF;
    private Rect mRect;
    private String mProgressText = "";

    public BubbleView(Context context) {
        this(context, null);
    }

    public BubbleView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BubbleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mBubblePaint = new Paint();
        mBubblePaint.setAntiAlias(true);
        mBubblePaint.setTextAlign(Paint.Align.CENTER);

        mBubblePath = new Path();
        mBubbleRectF = new RectF();
        mRect = new Rect();


        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.BubbleView, defStyleAttr, 0);

        mBubbleColor = typedArray.getColor(R.styleable.BubbleView_bubble_bg_color, 0xFF4562CF);
        mBubbleTextColor = typedArray.getColor(R.styleable.BubbleView_bubble_text_color, Color.WHITE);
        mBubbleTextSize = typedArray.getDimensionPixelSize(R.styleable.BubbleView_bubble_text_size,
                getResources().getDimensionPixelSize(R.dimen.x24));
        minProgress = typedArray.getInteger(R.styleable.BubbleView_bubble_min_value, 0);
        maxProgress = typedArray.getInteger(R.styleable.BubbleView_bubble_max_value, 100);

        typedArray.recycle();

        calculateRadiusOfBubble();

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        setMeasuredDimension(3 * mBubbleRadius, 3 * mBubbleRadius);

        mBubbleRectF.set(getMeasuredWidth() / 2f - mBubbleRadius, 0,
                getMeasuredWidth() / 2f + mBubbleRadius, 2 * mBubbleRadius);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mBubblePath.reset();
        float x0 = getMeasuredWidth() / 2f;
        float y0 = getMeasuredHeight() - mBubbleRadius / 3f;
        mBubblePath.moveTo(x0, y0);
        float x1 = (float) (getMeasuredWidth() / 2f - Math.sqrt(3) / 2f * mBubbleRadius);
        float y1 = 3 / 2f * mBubbleRadius;
        mBubblePath.quadTo(
                x1 - dp2px(2), y1 - dp2px(2),
                x1, y1
        );
        mBubblePath.arcTo(mBubbleRectF, 150, 240);

        float x2 = (float) (getMeasuredWidth() / 2f + Math.sqrt(3) / 2f * mBubbleRadius);
        mBubblePath.quadTo(
                x2 + dp2px(2), y1 - dp2px(2),
                x0, y0
        );
        mBubblePath.close();

        mBubblePaint.setColor(mBubbleColor);
        canvas.drawPath(mBubblePath, mBubblePaint);

        mBubblePaint.setTextSize(mBubbleTextSize);
        mBubblePaint.setColor(mBubbleTextColor);
        mBubblePaint.getTextBounds(mProgressText, 0, mProgressText.length(), mRect);
        Paint.FontMetrics fm = mBubblePaint.getFontMetrics();
        float baseline = mBubbleRadius + (fm.descent - fm.ascent) / 2f - fm.descent;
        canvas.drawText(mProgressText, getMeasuredWidth() / 2f, baseline, mBubblePaint);
    }

    void setProgressText(String progressText) {
        if (progressText != null && !mProgressText.equals(progressText)) {
            mProgressText = progressText;
            invalidate();
        }
    }

    private int mBubbleRadius;
    private int mBubbleColor;// color of bubble
    private int mBubbleTextSize; // text size of bubble-progress
    private int mBubbleTextColor; // text color of bubble-progress

    private int maxProgress;
    private int minProgress;

    /**
     * Calculate radius of bubble according to the Min and the Max
     */
    private void calculateRadiusOfBubble() {
        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mBubbleTextSize);

        int mTextSpace = dp2px(2);

        Rect mRectText = new Rect();

        String maxText = maxProgress + "";
        String minText = minProgress + "";

        mPaint.getTextBounds(maxText, 0, maxText.length(), mRectText);
        int w1 = (mRectText.width() + mTextSpace * 2) >> 1;

        mPaint.getTextBounds(minText, 0, minText.length(), mRectText);
        int w2 = (mRectText.width() + mTextSpace * 2) >> 1;


        mBubbleRadius = dp2px(14); // default 14dp
        int max = Math.max(mBubbleRadius, Math.max(w1, w2));
        mBubbleRadius = max + mTextSpace;
    }

    private int dp2px(int dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp,
                Resources.getSystem().getDisplayMetrics());
    }
}
