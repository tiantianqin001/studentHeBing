package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2018/5/17.
 * qzx
 */

public class MultiColorPBView extends View {
    public MultiColorPBView(Context context) {
        this(context, null);
    }

    public MultiColorPBView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MultiColorPBView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeWidth(STROKE_SIZE);
        mPaint.setColor(0xFFFF7F00);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        myHandler = new MyHandler(this);
        timer = new Timer();
        MyTimerTask task = new MyTimerTask();
        timer.schedule(task, 0, SWEEP_SPEED);
    }

    private boolean isNeedStop = false;

    public void setNeedStop(boolean needStop) {
        isNeedStop = needStop;
    }

    private Paint mPaint;
    private static final int STROKE_SIZE = 8;
    private float sweepAngle = 0;
    private static final int INCREMENT = 10;
    private int colorCount = 0;
    private static final int SWEEP_SPEED = 10;

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!isNeedStop) {
            if (sweepAngle >= 360) {
                colorCount++;
                if (colorCount >= 3) {
                    colorCount = 0;
                }
                //FF7F00---橙色 #EE0000---红色 #EEEE00---黄色
                if (colorCount == 1) {
                    mPaint.setColor(0xFFEEEE00);
                } else if (colorCount == 2) {
                    mPaint.setColor(0xFFEE0000);
                } else if (colorCount == 0) {
                    mPaint.setColor(0xFFFF7F00);
                }
                sweepAngle = 0;
            }

//            QZXTools.logE("zbv","sweepAngle="+sweepAngle+";thread name="+Thread.currentThread().getName(),null);

//            Log.d("zbv","width="+getWidth()+";height="+getHeight());
//
            //3点钟方向为0度角---userCenter表示是否画出扇形的边
            canvas.drawArc(new RectF(0 + STROKE_SIZE, 0 + STROKE_SIZE,
                            getWidth() - STROKE_SIZE, getHeight() - STROKE_SIZE),
                    -90, sweepAngle, false, mPaint);
            //延迟50ms刷新一次视图
//            postDelayed(new MyRunnable(), SWEEP_SPEED);
        }
    }

    private MyHandler myHandler;

    public static class MyHandler extends Handler {
        private WeakReference<MultiColorPBView> multiColorPBViewWeakReference;

        public MyHandler(MultiColorPBView multiColorPBView) {
            this.multiColorPBViewWeakReference = new WeakReference<>(multiColorPBView);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 8:
                    if (multiColorPBViewWeakReference!=null && multiColorPBViewWeakReference.get() != null) {
                        multiColorPBViewWeakReference.get().invalidate();
                    }
                    break;
            }
        }
    }

    private Timer timer;

    private class MyTimerTask extends TimerTask {

        @Override
        public void run() {
            try {
                if (isNeedStop) {
                    timer.cancel();
                    timer = null;
                    return;
                }
                sweepAngle += INCREMENT;
                if (myHandler != null)
                    myHandler.sendEmptyMessage(8);
            }catch (Exception e){
                e.fillInStackTrace();
            }

        }
    }

    public void releaseAll() {
        if (timer != null) {
            timer.cancel();
            ;
            timer = null;
        }

        if (myHandler != null) {
            myHandler = null;
        }
    }
}
