package com.telit.zhkt_three.CustomView.tbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/9/3 14:21
 * <p>
 * 固定五个点
 */
public class FixedRulerView extends View {
    private int margin;//左右边距
    private int height;//默认的高度
    private int lineHeihgt;//线的高度
    private int standardDot;//原点半径
    private Paint mPaint;

    private Bitmap slideImg;//滑块

    private RectF slideRectF;//滑块的坐标矩形

    private final static int STANDARD_VALUE = 200;

    private final static String text_mid = "标准";
    private final static String text_small = "小";
    private final static String text_big = "大";

    public FixedRulerView(Context context) {
        this(context, null);
    }

    public FixedRulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FixedRulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        margin = getResources().getDimensionPixelOffset(R.dimen.x20);
        height = getResources().getDimensionPixelSize(R.dimen.x80);
        lineHeihgt = getResources().getDimensionPixelSize(R.dimen.x40);
        standardDot = getResources().getDimensionPixelSize(R.dimen.x10);

        Drawable slideDrawable = getResources().getDrawable(R.drawable.ruler_slider);
        slideImg = QZXTools.drawableToBitmap(slideDrawable);
        slideRectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        QZXTools.logE("onMeasure width=" + MeasureSpec.getSize(widthMeasureSpec), null);
        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec), height);
    }

    private boolean moveSlide = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //触摸判断
                float downX = event.getX();
                float downY = event.getY();
                if (slideRectF.contains(downX, downY)) {
                    moveSlide = true;
                }
                break;
            case MotionEvent.ACTION_MOVE:
                float moveX = event.getX();
                float moveY = event.getY();

                if (Math.abs((moveX - slideRectF.left)) > Math.abs((moveY - slideRectF.top)) && moveX > 0 && moveX < getWidth() - slideImg.getWidth()) {
                    //水平滑动
                    slideRectF.set(moveX, (height - slideImg.getHeight()) / 2,
                            moveX + slideImg.getWidth(), (height + slideImg.getHeight()) / 2);
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                moveSlide = false;
                invalidate();
                //回调等级
                if (rulerCallback != null) {
                    //判断等级归属
                    level = judgeLevel();
                    rulerCallback.tbsTextSize(level);
                }
                break;
        }
        return true;
    }

    /**
     * 判断等级归属
     */
    private int judgeLevel() {
        float levelOne = slideRectF.left;
        float levelTwo = Math.abs(getWidth() / 4 - slideRectF.left);
        float levelThree = Math.abs(getWidth() / 2 - slideRectF.left);
        float levelFour = Math.abs(getWidth() / 4 * 3 - slideRectF.left);
        float levelFive = Math.abs(getWidth() - slideRectF.left);

        int level = 0;
        float minLevel = levelOne;
        if (minLevel >= levelTwo) {
            minLevel = levelTwo;
            level = 1;
        }

        if (minLevel >= levelThree) {
            minLevel = levelThree;
            level = 2;
        }

        if (minLevel >= levelFour) {
            minLevel = levelFour;
            level = 3;
        }

        if (minLevel >= levelFive) {
            minLevel = levelFive;
            level = 4;
        }
        return level;
    }

    private boolean isFirst = true;

    /**
     * 固定五个等级，默认是2代表标准
     * SMALLEST(50),
     * SMALLER(75),
     * NORMAL(100),
     * LARGER(125),
     * LARGEST(150);
     */
    private int level = 2;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        QZXTools.logE("onDraw..." + moveSlide, null);

        if (isFirst) {
            isFirst = false;
            slideRectF.set((getWidth() - slideImg.getWidth()) / 2, (height - slideImg.getHeight()) / 2,
                    (getWidth() + slideImg.getWidth()) / 2, (height + slideImg.getHeight()) / 2);
        }

        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        canvas.save();
        canvas.translate(0, height / 2);
        //中间的一条滚动线
        canvas.drawLine(margin, 0, getWidth() - margin, 0, mPaint);
        canvas.restore();

        //首尾两条线
        canvas.drawLine(margin, (height - lineHeihgt) / 2, margin, lineHeihgt + (height - lineHeihgt) / 2, mPaint);
        canvas.drawLine(getWidth() - margin, (height - lineHeihgt) / 2, getWidth() - margin,
                lineHeihgt + (height - lineHeihgt) / 2, mPaint);

        float smallWidth = mPaint.measureText(text_small);
        canvas.drawText(text_small, margin - smallWidth / 2, fontMetrics.bottom - fontMetrics.top, mPaint);

        float bigWidth = mPaint.measureText(text_big);
        canvas.drawText(text_big, getWidth() - margin - bigWidth / 2, fontMetrics.bottom - fontMetrics.top, mPaint);

        //中间的两个点
        canvas.drawCircle(getWidth() / 4, height / 2, standardDot, mPaint);
        canvas.drawCircle(getWidth() / 4 * 3, height / 2, standardDot, mPaint);

        //中间的标准原点
        canvas.drawCircle(getWidth() / 2, height / 2, standardDot, mPaint);

        float midWidth = mPaint.measureText(text_mid);
        canvas.drawText(text_mid, (getWidth() - midWidth) / 2, fontMetrics.bottom - fontMetrics.top, mPaint);

        //绘制滑块
        if (moveSlide) {
            canvas.drawBitmap(slideImg, slideRectF.left, slideRectF.top, mPaint);
        } else {
            switch (level) {
                case 0:
                    canvas.drawBitmap(slideImg, margin - slideImg.getWidth() / 2, slideRectF.top, mPaint);
                    slideRectF.set(margin - slideImg.getWidth() / 2, (height - slideImg.getHeight()) / 2,
                            margin + slideImg.getWidth() / 2, (height + slideImg.getHeight()) / 2);
                    break;
                case 1:
                    canvas.drawBitmap(slideImg, getWidth() / 4 - slideImg.getWidth() / 2, slideRectF.top, mPaint);
                    slideRectF.set(getWidth() / 4 - slideImg.getWidth() / 2, (height - slideImg.getHeight()) / 2,
                            getWidth() / 4 + slideImg.getWidth() / 2, (height + slideImg.getHeight()) / 2);
                    break;
                case 2:
                    canvas.drawBitmap(slideImg, getWidth() / 2 - slideImg.getWidth() / 2, slideRectF.top, mPaint);
                    slideRectF.set(getWidth() / 2 - slideImg.getWidth() / 2, (height - slideImg.getHeight()) / 2,
                            getWidth() / 2 + slideImg.getWidth() / 2, (height + slideImg.getHeight()) / 2);
                    break;
                case 3:
                    canvas.drawBitmap(slideImg, getWidth() / 4 * 3 - slideImg.getWidth() / 2, slideRectF.top, mPaint);
                    slideRectF.set(getWidth() / 4 * 3 - slideImg.getWidth() / 2, (height - slideImg.getHeight()) / 2,
                            getWidth() / 4 * 3 + slideImg.getWidth() / 2, (height + slideImg.getHeight()) / 2);
                    break;
                case 4:
                    canvas.drawBitmap(slideImg, getWidth() - margin - slideImg.getWidth() / 2, slideRectF.top, mPaint);
                    slideRectF.set(getWidth() - slideImg.getWidth() / 2, (height - slideImg.getHeight()) / 2,
                            getWidth() + slideImg.getWidth() / 2, (height + slideImg.getHeight()) / 2);
                    break;
            }
        }
    }

    public RulerCallback rulerCallback;

    public void setRulerCallback(RulerCallback rulerCallback) {
        this.rulerCallback = rulerCallback;
    }

    public interface RulerCallback {
        void tbsTextSize(int tsLevel);
    }
}
