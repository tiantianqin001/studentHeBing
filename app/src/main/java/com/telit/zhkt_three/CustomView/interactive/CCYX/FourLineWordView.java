package com.telit.zhkt_three.CustomView.interactive.CCYX;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/8/5 14:19
 * <p>
 * 单个四线字格字母，带错误和正确标志
 */
public class FourLineWordView extends View {

    //实体边框的画笔，矩形一般为正方形
    private Paint linePaint;
    //文字画笔
    private Paint wordPaint;
    //正确错误标志画笔
    private Paint resultPaint;

    /**
     * 粗细线，上下两根粗，中间两根细
     */
    private int bigLineSize;
    private int smallLineSize;

    //默认xx
    private int paddingSize;

    private Path path;

    private Rect rect;

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

    public FourLineWordView(Context context) {
        this(context, null);
    }

    public FourLineWordView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FourLineWordView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        path = new Path();
        rect = new Rect();

        bigLineSize = getResources().getDimensionPixelSize(R.dimen.x3);
        smallLineSize = getResources().getDimensionPixelSize(R.dimen.x1);
        TextSize = getResources().getDimensionPixelSize(R.dimen.x48);
        paddingSize = getResources().getDimensionPixelSize(R.dimen.x5);

        initPaint();
    }

    private void initPaint() {
        linePaint = new Paint();
        linePaint.setColor(Color.GRAY);
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(smallLineSize);
        linePaint.setStyle(Paint.Style.STROKE);

        wordPaint = new Paint();
        wordPaint.setTextSize(TextSize);
        wordPaint.setAntiAlias(true);
        wordPaint.setColor(Color.CYAN);
        wordPaint.setTextAlign(Paint.Align.CENTER);

        resultPaint = new Paint();
        resultPaint.setAntiAlias(true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        String sampleWord = "z";
        wordPaint.getTextBounds(sampleWord, 0, sampleWord.length(), rect);
        midLine = rect.bottom - rect.top;

        float textWidth;
        //依据文本画笔确定宽高
        if (TextUtils.isEmpty(text)) {
            textWidth = wordPaint.measureText(sampleWord);
        } else {
            textWidth = wordPaint.measureText(text);
        }
        Paint.FontMetrics fontMetrics = wordPaint.getFontMetrics();
        float textHeight = fontMetrics.bottom - fontMetrics.top;

        topLine = fontMetrics.top;
        bottomLine = fontMetrics.bottom;
        baseLine = 0.0f;

        bottomLine = -topLine - midLine;

        width = textWidth + paddingSize * 2;
        height = bottomLine - topLine + paddingSize * 2;

        setMeasuredDimension((int) width, (int) height);
    }

    private float baseLine;
    private float midLine;
    private float bottomLine;
    private float topLine;

    private float width;
    private float height;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //测定视图宽高的矩形框
//        Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        mPaint.setStyle(Paint.Style.STROKE);
//        mPaint.setColor(Color.RED);
//        canvas.drawRect(0, 0, width, height, mPaint);
//        QZXTools.logE("rect=" + rect + ";midLine=" + midLine, null);

        canvas.save();
        canvas.translate(0, height - bottomLine - bigLineSize);

        //绘制四线
        linePaint.setStrokeWidth(bigLineSize);
        canvas.drawLine(0, topLine, width, topLine, linePaint);
        linePaint.setStrokeWidth(smallLineSize);
        canvas.drawLine(0, -midLine, width, -midLine, linePaint);
        linePaint.setStrokeWidth(smallLineSize);
        canvas.drawLine(0, baseLine, width, baseLine, linePaint);
        linePaint.setStrokeWidth(bigLineSize);
        canvas.drawLine(0, bottomLine, width, bottomLine, linePaint);

        //绘制字母
        canvas.drawText(text, width / 2, 0, wordPaint);

        canvas.restore();
    }
}
