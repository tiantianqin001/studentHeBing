package com.telit.zhkt_three.CustomView.JobReport;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/6/5 10:53
 */
public class JobReportCircleView extends View {

    private Paint mPaint;

    private RectF rectF;

    private static final int Boder_Color = 0xFFACC6DF;
    private static final int Big_Circle = 0xFFE9EFF5;
    private static final int Fill_Circle = 0xFF1282FF;
    private static final int Fill_Circle_Two = 0xFF13B5B1;

    private static final int INCREMENT = 10;
    private static final int SWEEP_SPEED = 10;

    private static final String Tips_One = "完成率";
    private static final String Tips_Two = "正确率";

    private float Boder_Stroke_Width;
    private float Big_Stroke_Width;
    private float Fill_Stroke_Width;

    private float Percent_Text_Size;
    private float Tips_Text_Size;

    private float Total_Width;

    private float Divide_Width;

    private float Offset_Size;

    private boolean needStartAnimation = false;

    /**
     * 实际扫过的角度，动画使用
     */
    private int actualSweepAngle = 0;

    private int sweepAngle;

    private int type;

    private String percentText;

    private Rect rect_date;

    /**
     * 传入扫过的角度
     */
    public void setSweepAngleAndTypeResult(int sweepAngle, int type) {
        actualSweepAngle = 0;
        this.sweepAngle = sweepAngle;
        this.type = type;
        needStartAnimation = true;
        percentText = Math.round(sweepAngle / 360.0f * 100) + "%";

        invalidate();
    }

    public JobReportCircleView(Context context) {
        this(context, null);
    }

    public JobReportCircleView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public JobReportCircleView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        //测量文本的矩形
        rect_date = new Rect();

        Total_Width = getResources().getDimensionPixelSize(R.dimen.x240);

        Boder_Stroke_Width = getResources().getDimensionPixelSize(R.dimen.x2);
        Big_Stroke_Width = getResources().getDimensionPixelSize(R.dimen.x30);
        Fill_Stroke_Width = getResources().getDimensionPixelSize(R.dimen.x24);

        Percent_Text_Size = getResources().getDimensionPixelSize(R.dimen.x60);
        Tips_Text_Size = getResources().getDimensionPixelSize(R.dimen.x24);

        Divide_Width = getResources().getDimensionPixelSize(R.dimen.x5);
        Offset_Size = getResources().getDimensionPixelSize(R.dimen.x15);

        rectF = new RectF();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) Total_Width, (int) Total_Width);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        /**
         * mPaint绘制是从画笔粗度的一般开始
         * */
        mPaint.setColor(Boder_Color);
        mPaint.setStrokeWidth(Boder_Stroke_Width);
        rectF.set(Boder_Stroke_Width / 2, Boder_Stroke_Width / 2,
                Total_Width - Boder_Stroke_Width / 2,
                Total_Width - Boder_Stroke_Width / 2);
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        mPaint.setColor(Big_Circle);
        mPaint.setStrokeWidth(Big_Stroke_Width);
        rectF.set(Boder_Stroke_Width + Big_Stroke_Width / 2, Boder_Stroke_Width + Big_Stroke_Width / 2,
                Total_Width - (Boder_Stroke_Width + Big_Stroke_Width / 2),
                Total_Width - (Boder_Stroke_Width + Big_Stroke_Width / 2));
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        mPaint.setColor(Boder_Color);
        mPaint.setStrokeWidth(Boder_Stroke_Width);
        rectF.set(Boder_Stroke_Width + Big_Stroke_Width + Boder_Stroke_Width / 2,
                Boder_Stroke_Width + Big_Stroke_Width + Boder_Stroke_Width / 2,
                Total_Width - (Boder_Stroke_Width + Big_Stroke_Width + Boder_Stroke_Width / 2),
                Total_Width - (Boder_Stroke_Width + Big_Stroke_Width + Boder_Stroke_Width / 2));
        canvas.drawArc(rectF, 0, 360, false, mPaint);

        if (type == 0) {
            mPaint.setColor(Fill_Circle);
        } else {
            mPaint.setColor(Fill_Circle_Two);
        }
        mPaint.setStrokeWidth(Fill_Stroke_Width);
        rectF.set(Boder_Stroke_Width + (Big_Stroke_Width - Fill_Stroke_Width) / 2 + Fill_Stroke_Width / 2,
                Boder_Stroke_Width + (Big_Stroke_Width - Fill_Stroke_Width) / 2 + Fill_Stroke_Width / 2,
                Total_Width - (Boder_Stroke_Width + (Big_Stroke_Width - Fill_Stroke_Width) / 2 + Fill_Stroke_Width / 2),
                Total_Width - (Boder_Stroke_Width + (Big_Stroke_Width - Fill_Stroke_Width) / 2 + Fill_Stroke_Width / 2));
        canvas.drawArc(rectF, -90, actualSweepAngle, false, mPaint);

        //绘制百分比
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        if (type == 0) {
            mPaint.setColor(Fill_Circle);
        } else {
            mPaint.setColor(Fill_Circle_Two);
        }
        mPaint.setTextSize(Percent_Text_Size);
        //返回文本的宽度
        float textWidth = mPaint.measureText(percentText);
        //测量文本的矩形
        mPaint.getTextBounds(percentText, 0, percentText.length(), rect_date);
        //字体尺寸
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        /**
         * 居中：
         canvas.drawText(percentText, (Total_Width - textWidth) / 2,
         (Total_Width - (rect_date.bottom - rect_date.top))/2 + (fontMetrics.bottom - fontMetrics.top) / 2, mPaint);
         * */
        canvas.drawText(percentText, (Total_Width - textWidth) / 2,
                Total_Width / 2 - (rect_date.bottom - rect_date.top) + Offset_Size + (fontMetrics.bottom - fontMetrics.top) / 2, mPaint);

        //绘制文本
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        String toDrawText;
        if (type == 0) {
            toDrawText = Tips_One;
        } else {
            toDrawText = Tips_Two;
        }
        mPaint.setColor(0xFF93A5B9);
        mPaint.setTextSize(Tips_Text_Size);
        //返回文本的宽度
        float textWidth_again = mPaint.measureText(toDrawText);
        //测量文本的矩形
        mPaint.getTextBounds(toDrawText, 0, toDrawText.length(), rect_date);
        //字体尺寸
        Paint.FontMetrics fontMetrics_again = mPaint.getFontMetrics();
        //Divide_Width 是一个拉开间距
        canvas.drawText(toDrawText, (Total_Width - textWidth_again) / 2,
                Total_Width / 2 + Divide_Width + Offset_Size + (fontMetrics_again.bottom - fontMetrics_again.top) / 2, mPaint);

        if (needStartAnimation) {
            //延迟50ms刷新一次视图
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    //先叠加常量算进度是否超了
                    if (actualSweepAngle + INCREMENT < sweepAngle) {
                        actualSweepAngle += INCREMENT;
                    } else {
                        int thisPlus = sweepAngle - actualSweepAngle;
                        actualSweepAngle += thisPlus;
                    }
                    if (actualSweepAngle >= sweepAngle) {
                        needStartAnimation = false;
                        return;
                    }
                    invalidate();
                }
            }, SWEEP_SPEED);
        }
    }
}
