package com.telit.zhkt_three.CustomView.QuestionView.matching;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/7/17 19:00
 * <p>
 * 连线的视图
 * <p>
 * 显示自己的答案？
 * 显示正确答案？
 */
public class ToLineView extends View {

    private PathMeasure pathMeasure;

    private Paint mLinePaint;
    private Paint mPaint;

    /**
     * 点、线、点
     */
    public Path mStartDotPath;
    public Path mLinePath;
    public Path mEndDotPath;

    /**
     * 记录自己绘制的痕迹
     */
    private List<Path> dotPathList;
    private List<Path> linePathList;

    /**
     * 正确答案路径
     */
    private List<Path> answerDotPathlist;
    private List<Path> answerLinePathList;

    /**
     * 绘制颜色 #5D5D5D
     * 错误颜色 #FF3232
     * 正确颜色 #32B16C
     */
    private int normalColor;
    private int wrongColor;
    private int rightColor;

    public ToLineView(Context context) {
        this(context, null);
    }

    public ToLineView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ToLineView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        normalColor = 0xFF5D5D5D;
        wrongColor = 0xFFFF3232;
        rightColor = 0xFF32B16C;

        dotPathList = new ArrayList<>();
        linePathList = new ArrayList<>();
        answerDotPathlist = new ArrayList<>();
        answerLinePathList = new ArrayList<>();

        mPaint = new Paint();
        mPaint.setColor(normalColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mLinePaint = new Paint();
        mLinePaint.setColor(normalColor);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setAntiAlias(true);

        initDst();
    }

    /**
     * PathMeasure的绘制Path必须要有lineTo初始化，否则画不出来
     */
    private void initDst() {
        mStartDotPath = new Path();
        mStartDotPath.lineTo(0, 0);

        mEndDotPath = new Path();
        mEndDotPath.lineTo(0, 0);

        mLinePath = new Path();
        mLinePath.lineTo(0, 0);
    }

    /**
     * 添加点路径
     */
    public void addDotPath(Path path, boolean isAnswer) {
        if (isAnswer) {
            answerDotPathlist.add(path);
        } else {
            dotPathList.add(path);
        }
    }

    /**
     * 添加线路径
     */
    public void addLinePath(Path path, boolean isAnswer) {
        if (isAnswer) {
            answerLinePathList.add(path);
        } else {
            linePathList.add(path);
        }
    }

    /**
     * 重置视图
     */
    public void resetDrawLine() {
        mStartDotPath.reset();
        mStartDotPath.lineTo(0, 0);
        mEndDotPath.reset();
        mEndDotPath.lineTo(0, 0);
        mLinePath.reset();
        mLinePath.lineTo(0, 0);

        dotPathList.clear();
        linePathList.clear();

        invalidate();
    }

    private int drawStatus;

    /**
     * 设置绘制的状态
     */
    public void setDrawStatus(int drawStatus) {
        this.drawStatus = drawStatus;
        invalidate();
    }

    /**
     * 0 表示绘制状态
     * 1 表示绘制完成但未批阅状态
     * 2 表示批阅了状态，可以显示答案
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (drawStatus == 0) {
            mPaint.setColor(normalColor);
            mLinePaint.setColor(normalColor);

            //绘制之前的轨迹
            for (Path dotPath : dotPathList) {
                canvas.drawPath(dotPath, mPaint);
            }
            for (Path linePath : linePathList) {
                canvas.drawPath(linePath, mLinePaint);
            }

            canvas.drawPath(mStartDotPath, mPaint);
            canvas.drawPath(mLinePath, mLinePaint);
            canvas.drawPath(mEndDotPath, mPaint);
        } else if (drawStatus == 1) {
            mPaint.setColor(normalColor);
            mLinePaint.setColor(normalColor);

            for (Path dotPath : dotPathList) {
                canvas.drawPath(dotPath, mPaint);
            }
            for (Path linePath : linePathList) {
                canvas.drawPath(linePath, mLinePaint);
            }
        } else if (drawStatus == 2) {
            //自己的答案，如果和正确答案一样就不会被覆盖
            mPaint.setColor(wrongColor);
            mLinePaint.setColor(wrongColor);

            for (Path dotPath : dotPathList) {
                canvas.drawPath(dotPath, mPaint);
            }
            for (Path linePath : linePathList) {
                canvas.drawPath(linePath, mLinePaint);
            }

            //正确答案
            mPaint.setColor(rightColor);
            mLinePaint.setColor(rightColor);

            for (Path dotPath : answerDotPathlist) {
                canvas.drawPath(dotPath, mPaint);
            }
            for (Path linePath : answerLinePathList) {
                canvas.drawPath(linePath, mLinePaint);
            }
        }
    }

    public void getDrawPath(Path path) {
        if (isAnimRunning) {
            return;
        }
        pathMeasure = new PathMeasure(path, false);
        lineAnimation();
//        this.path = path;
//        invalidate();
    }

    /**
     * 绘制路径个数的计数
     */
    private int pathCount = 0;
    private boolean isAnimRunning = false;

    public boolean isAnimRunning() {
        return isAnimRunning;
    }

    private void lineAnimation() {
        isAnimRunning = true;
        ValueAnimator valueAnimator = ValueAnimator.ofFloat(0, 1);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animValue = (float) animation.getAnimatedValue();
                float valueLength = pathMeasure.getLength() * animValue;
//                QZXTools.logE("length=" + pathMeasure.getLength() + ";valueLength=" + valueLength, null);

                if (pathCount == 0) {
                    //不会重写Path，是追加
                    pathMeasure.getSegment(0, valueLength, mStartDotPath, true);
                } else if (pathCount == 1) {
                    pathMeasure.getSegment(0, valueLength, mLinePath, true);
                } else if (pathCount == 2) {
                    pathMeasure.getSegment(0, valueLength, mEndDotPath, true);
                }

                invalidate();
            }
        });
        valueAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {
                pathCount++;
                pathMeasure.nextContour();

                if (pathMeasure.getLength() == 0) {
                    animation.end();
                    pathCount = 0;
                    isAnimRunning = false;
                }
            }
        });

        valueAnimator.setDuration(500);
        valueAnimator.setRepeatCount(ValueAnimator.INFINITE);
        //开启动画
        valueAnimator.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        QZXTools.logE("toLine onAttachedToWindow......", null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
//        QZXTools.logE("toLine onDetachedFromWindow......", null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        QZXTools.logE("ToLine onSizeChanged......", null);
        if (listner != null) {
            listner.onSizeChange();
        }
    }

    /**
     * 为了展示已画的划线，这时尺寸才非零
     */
    private OnSizeChangedCallback listner;

    public void setOnSizeChangedCallback(OnSizeChangedCallback listner) {
        this.listner = listner;
    }

    public interface OnSizeChangedCallback {
        void onSizeChange();
    }
}
