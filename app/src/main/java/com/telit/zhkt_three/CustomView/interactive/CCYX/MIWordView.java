package com.telit.zhkt_three.CustomView.interactive.CCYX;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/8/5 14:19
 * <p>
 * 单个米字格文字，带错误和正确标志
 */
public class MIWordView extends View {

    //实体边框的画笔，矩形一般为正方形
    private Paint borderPaint;
    //米子虚线画笔
    private Paint dashPaint;
    //文字画笔
    private Paint wordPaint;
    //正确错误标志画笔
    private Paint resultPaint;

    private int borderSize;
    private int dashSize;

    //默认xx
    private int paddingSize;

    private Path path;

    /**
     * 文本
     */
    private String text = "";
    private int TextSize;

    public void setText(String text) {
        this.text = text;
        invalidate();
    }

    public void setTextSize(int textSize) {
        TextSize = textSize;
        wordPaint.setTextSize(TextSize);
        invalidate();
    }

    public MIWordView(Context context) {
        this(context, null);
    }

    public MIWordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MIWordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        path = new Path();

        borderSize = getResources().getDimensionPixelSize(R.dimen.x3);
        dashSize = getResources().getDimensionPixelSize(R.dimen.x2);
        TextSize = getResources().getDimensionPixelSize(R.dimen.x48);
        paddingSize = getResources().getDimensionPixelSize(R.dimen.x20);

        initPaint();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //依据文本画笔确定宽高
        float textWidth = wordPaint.measureText(text);
        Paint.FontMetrics fontMetrics = wordPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;

        float viewWidth = textWidth + paddingSize * 2;
        float viewHeight = textHeight + paddingSize * 2;

        if (viewWidth >= viewHeight) {
            actualSize = (int) viewWidth;
        } else {
            actualSize = (int) viewHeight;
        }

        setMeasuredDimension(actualSize, actualSize);
    }

    private void initPaint() {
        borderPaint = new Paint();
        borderPaint.setColor(Color.GRAY);
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderSize);
        borderPaint.setStyle(Paint.Style.STROKE);

        dashPaint = new Paint();
        dashPaint.setColor(Color.GRAY);
        dashPaint.setAntiAlias(true);
        dashPaint.setStrokeWidth(dashSize);
        dashPaint.setStyle(Paint.Style.STROKE);
        //DashPathEffect 中 intervals是实线、虚线间隔交替的大小
        dashPaint.setPathEffect(new DashPathEffect(new float[]{15, 7}, 0));

        wordPaint = new Paint();
        wordPaint.setTextSize(TextSize);
        wordPaint.setAntiAlias(true);
        wordPaint.setColor(Color.CYAN);
        wordPaint.setTextAlign(Paint.Align.CENTER);

        resultPaint = new Paint();
        resultPaint.setAntiAlias(true);
    }

    private int actualSize;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制边框矩形,画笔的一半宽度画在x,y外
        canvas.drawRect(borderSize / 2, borderSize / 2,
                actualSize - borderSize / 2, actualSize - borderSize / 2, borderPaint);

        //绘制米字虚线
        path.moveTo(0, 0);
        path.lineTo(actualSize, actualSize);
        canvas.drawPath(path, dashPaint);
        path.reset();
        path.moveTo(0, actualSize);
        path.lineTo(actualSize, 0);
        canvas.drawPath(path, dashPaint);
        path.reset();
        path.moveTo(0, actualSize / 2);
        path.lineTo(actualSize, actualSize / 2);
        canvas.drawPath(path, dashPaint);
        path.reset();
        path.moveTo(actualSize / 2, 0);
        path.lineTo(actualSize / 2, actualSize);
        canvas.drawPath(path, dashPaint);

        //绘制文本
        Paint.FontMetrics fontMetrics = wordPaint.getFontMetrics();
        canvas.drawText(text, actualSize / 2, actualSize / 2
                + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom, wordPaint);
    }
}
