package com.telit.zhkt_three.CustomView.VoteResult;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/6/24 11:11
 * <p>
 * {"success":true,"errorCode":"0","msg":"查询学生投票结果成功","result":[{"title":"请输入主题......",
 * "index":"0","image":null,"numberOfVote":"0","percentage":"0","optionContent":null,"imageUrl":"","text":"gghhh"},
 * {"title":"请输入主题......","index":"1","image":null,"numberOfVote":"0","percentage":"0",
 * "optionContent":null,"imageUrl":"","text":"jjjj"},{"title":"请输入主题......","index":"2","image":null,"numberOfVote":"0",
 * "percentage":"0","optionContent":null,"imageUrl":"","text":"yyyyy"},{"title":"请输入主题......","index":"3",
 * "image":null,"numberOfVote":"1","percentage":"100","optionContent":null,"imageUrl":"","text":"gggg"}],"total":0,"pageNo":0}
 */
public class VoteProgressView extends View {
    //设定的长度
    private float Progress_Width;
    private float Progress_Height;

    private float Inner_Progress_Height;

    private float IntervalSize;

    private static final int INCREMENT = 10;
    private static final int SWEEP_SPEED = 10;

    //变化的进度
    private float Changed_Progress_Width;

    //当前项投票数
    private int curItemVoteCount;

    /**
     * 设置票数和总共应该投的票数
     */
    public void setVoteCount(int curVoteCount, int totalVoteCount) {
        curItemVoteCount = curVoteCount;
        needStartAnimation = true;
        Changed_Progress_Width = Progress_Width * (curItemVoteCount * 1.0f / totalVoteCount);
        QZXTools.logE("curItemVoteCount=" + curItemVoteCount + ";Changed_Progress_Width=" + Changed_Progress_Width, null);
        invalidate();
    }

    private float inner_width;

    private boolean needStartAnimation = false;

    private Paint mPaint;

    private Rect rect;

    //边框宽度划线
    private float Boder_Stroke_Width;

    public VoteProgressView(Context context) {
        this(context, null);
    }

    public VoteProgressView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoteProgressView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        Progress_Width = getResources().getDimensionPixelSize(R.dimen.y1335);
        Progress_Height = getResources().getDimensionPixelSize(R.dimen.x36);
        Inner_Progress_Height = getResources().getDimensionPixelSize(R.dimen.x33);

        Boder_Stroke_Width = getResources().getDimensionPixelSize(R.dimen.x2);
        IntervalSize = getResources().getDimensionPixelSize(R.dimen.x3);

        rect = new Rect();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(widthSize, (int) Progress_Height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(Boder_Stroke_Width);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(0xFFE5E5E5);
        RectF rectF = new RectF(Boder_Stroke_Width / 2, Boder_Stroke_Width / 2,
                Progress_Width - Boder_Stroke_Width / 2,
                Progress_Height - Boder_Stroke_Width / 2);
        canvas.drawRoundRect(rectF, Progress_Height / 2, Progress_Height / 2, mPaint);
//        canvas.drawRoundRect(Boder_Stroke_Width / 2, Boder_Stroke_Width / 2,
//                Progress_Width - Boder_Stroke_Width / 2,
//                Progress_Height - Boder_Stroke_Width / 2, Progress_Height / 2, Progress_Height / 2, mPaint);

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setColor(0xFFF3F4F5);

        RectF rectF2 = new RectF(Boder_Stroke_Width, Boder_Stroke_Width,
                Progress_Width - Boder_Stroke_Width,
                Progress_Height - Boder_Stroke_Width);
        canvas.drawRoundRect(rectF2, Progress_Height / 2, Progress_Height / 2, mPaint);

//        canvas.drawRoundRect(Boder_Stroke_Width, Boder_Stroke_Width,
//                Progress_Width - Boder_Stroke_Width,
//                Progress_Height - Boder_Stroke_Width, Progress_Height / 2, Progress_Height / 2, mPaint);

        if (inner_width != 0) {
            canvas.save();
            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStyle(Paint.Style.FILL);
            //设置线冒样式
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            LinearGradient linearGradient = new LinearGradient(inner_width, 0, inner_width, Inner_Progress_Height,
                    0xFF81F5FF, 0xFF0A89FF, Shader.TileMode.CLAMP);
            mPaint.setShader(linearGradient);
            RectF rectF3 = new RectF(IntervalSize, IntervalSize,
                    inner_width,
                    Inner_Progress_Height);
            canvas.drawRoundRect(rectF3, Inner_Progress_Height / 2, Inner_Progress_Height / 2, mPaint);
//        canvas.drawRoundRect(IntervalSize, IntervalSize,
//                inner_width,
//                Inner_Progress_Height, Inner_Progress_Height / 2, Inner_Progress_Height / 2, mPaint);
            canvas.restore();

        }

        //填进度文字

        mPaint.reset();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.FILL);
        //设置线冒样式
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.x36));
        mPaint.setColor(0xFF1391FF);

        String maxProgressWord = curItemVoteCount + "票";
        String progressWord = Math.round(inner_width / Changed_Progress_Width * curItemVoteCount) + "票";

        //字体尺寸
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();

        mPaint.getTextBounds(maxProgressWord, 0, maxProgressWord.length(), rect);

        //之所以这么写x、y参数是因为高度和字体大小等大
        canvas.save();
        canvas.translate(Progress_Width + getResources().getDimensionPixelSize(R.dimen.x24), (rect.bottom - rect.top) / 2);
        canvas.drawText(progressWord, 0, (rect.bottom - rect.top) / 2, mPaint);
        canvas.restore();

        if (needStartAnimation) {
            //延迟50ms刷新一次视图
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (inner_width >= Changed_Progress_Width) {
                        needStartAnimation = false;
                        return;
                    }

                    //先叠加常量算进度是否超了
                    if (inner_width + INCREMENT < Changed_Progress_Width) {
                        inner_width += INCREMENT;
                    } else {
                        float thisPlus = Changed_Progress_Width - inner_width;
                        inner_width += thisPlus;
                    }

                    if (inner_width >= Changed_Progress_Width) {
                        needStartAnimation = false;
                        return;
                    }

                    invalidate();
                }
            }, SWEEP_SPEED);
        }
    }
}
