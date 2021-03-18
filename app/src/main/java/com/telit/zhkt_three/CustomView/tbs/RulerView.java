package com.telit.zhkt_three.CustomView.tbs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
 */
public class RulerView extends View {
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

    public RulerView(Context context) {
        this(context, null);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RulerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
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
                break;
        }
        return true;
    }

    private boolean isFirst = true;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        QZXTools.logE("onDraw...", null);

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

        //中间的标准原点
        canvas.drawCircle(getWidth() / 2, height / 2, standardDot, mPaint);

        float midWidth = mPaint.measureText(text_mid);
        canvas.drawText(text_mid, (getWidth() - midWidth) / 2, fontMetrics.bottom - fontMetrics.top, mPaint);

        //绘制滑块
        canvas.drawBitmap(slideImg, slideRectF.left, slideRectF.top, mPaint);

        if (rulerCallback != null) {
            int ts = (int) (slideRectF.left / getWidth() * STANDARD_VALUE + 50);
//            QZXTools.logE("ts=" + ts, null);
            rulerCallback.tbsTextSize(ts);
        }
    }

    public RulerCallback rulerCallback;

    public void setRulerCallback(RulerCallback rulerCallback) {
        this.rulerCallback = rulerCallback;
    }

    public interface RulerCallback {
        void tbsTextSize(int textSize);
    }
}
