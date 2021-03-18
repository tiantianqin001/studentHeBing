package com.telit.zhkt_three.CustomView.FileReceive;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/6/24 11:11
 */
public class FileReceiveProgressView extends View {

    //设定的长度
    private float Progress_Width;
    private float Progress_Height;

    private Paint mPaint;

    private Bitmap moveBitmap;

    private Rect rect;

    private int firstProgressColor;
    private int secondProgressColor;

    private int textSize;

    private int curProgress;

    public void setCurProgress(int curProgress) {
        this.curProgress = curProgress;
        invalidate();
    }

    private int maxProgress;

    private int offset;

    public FileReceiveProgressView(Context context) {
        this(context, null);
    }

    public FileReceiveProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FileReceiveProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        rect = new Rect();

        Progress_Width = getResources().getDimensionPixelSize(R.dimen.y585);
        Progress_Height = getResources().getDimensionPixelSize(R.dimen.x22);

        textSize = getResources().getDimensionPixelSize(R.dimen.x18);

        moveBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.receive_slide);

        firstProgressColor = 0xFFAADD3A;//浅绿色
        secondProgressColor = 0xFFFDC500;//淡黄色

        maxProgress = 100;

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);

        offset = moveBitmap.getWidth() / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.save();
        canvas.translate(offset, 0);
        //绘制first层
        mPaint.setColor(firstProgressColor);
        canvas.drawRoundRect(0, 0, Progress_Width, Progress_Height, Progress_Height / 2, Progress_Height / 2, mPaint);
        //绘制second层
        float width = Progress_Width * curProgress * 1.0f / maxProgress;
        mPaint.setColor(secondProgressColor);
        canvas.drawRoundRect(0, 0, width, Progress_Height, Progress_Height / 2, Progress_Height / 2, mPaint);
        canvas.restore();

        //绘制移动块
        canvas.save();
        canvas.translate(width, Progress_Height);
        canvas.drawBitmap(moveBitmap, 0, 0, mPaint);
        //绘制文字
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(textSize);
        String progress = curProgress + "%";
        float textWidth = mPaint.measureText(progress);
        mPaint.getTextBounds(progress, 0, progress.length(), rect);
        canvas.drawText(progress, (moveBitmap.getWidth() - textWidth) / 3,
                moveBitmap.getHeight() / 2 + (rect.bottom - rect.top) / 3, mPaint);
        canvas.restore();
    }
}
