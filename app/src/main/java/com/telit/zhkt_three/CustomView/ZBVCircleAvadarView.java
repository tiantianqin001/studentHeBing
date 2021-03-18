package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;


/**
 * @author qzx
 * @time 2018/10/11 16:35
 * 自定义圆形图片视图
 * 修订 2019/04/10
 */
public class ZBVCircleAvadarView extends View {

    private int minScreenSize;

    //View的尺寸大小
    private int circle_radius = 100;

    //边框的宽度---默认是0
    private int outBorderWidth = 0;

    //边框的颜色---默认是透明
    private int outBorderColor = Color.TRANSPARENT;

    //是否需要加边框---默认不加边框
    private Boolean needBorder = false;

    private Paint mPaint;

    /**
     * 要绘制的位图
     */
    private Bitmap sourceBitmap;

    /**
     * 这种方式制造圆形样式
     */
    private PorterDuffXfermode xfermode;

    /**
     * 绘制圆形的画布
     */
    private Canvas circleCanvas;

    /**
     * 绘制外边距的矩形
     */
    private RectF rectF;

    /**
     * 缩放Bitmap的矩形
     */
    private Rect rect;

    //------------设置属性
    public void setOutBorderWidth(int outBorderWidth) {
        this.outBorderWidth = outBorderWidth;
    }

    public void setOutBorderColor(int outBorderColor) {
        this.outBorderColor = outBorderColor;
    }

    public void setNeedBorder(Boolean needBorder) {
        this.needBorder = needBorder;
    }

    public void setSourceBitmap(Bitmap sourceBitmap) {
        this.sourceBitmap = sourceBitmap;
        //必须要添加，重新测量尺寸
        requestLayout();
        //刷新界面，调用onDraw方法
        invalidate();
    }

    //------------设置属性

    public ZBVCircleAvadarView(Context context) {
        this(context, null);
    }

    public ZBVCircleAvadarView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ZBVCircleAvadarView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

//        QZXTools.logD("zbv", "---ZBVCircleAvadarView---");

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        int screenHeight = getResources().getDisplayMetrics().heightPixels;

        rectF = new RectF();

        rect = new Rect();

        //获取屏幕宽高的最小值，我的手机华为畅玩，1280x720
        minScreenSize = screenWidth > screenHeight ? screenHeight : screenWidth;
//        QZXTools.logD("zbv", "screenWidth=" + screenWidth + ";screenHeight=" + screenHeight);

        mPaint = new Paint();

        xfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);

        circleCanvas = new Canvas();

        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.ZBVCircleAvadarView, defStyleAttr, 0);

        //引入图片
        Drawable drawable = typedArray.getDrawable(R.styleable.ZBVCircleAvadarView_resSrcId);
        if (drawable == null) {
            sourceBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
        } else {
            sourceBitmap = QZXTools.drawableToBitmap(drawable);
        }

        outBorderWidth = typedArray.getDimensionPixelSize(R.styleable.ZBVCircleAvadarView_outBorderWidth, 0);

//        QZXTools.logD("zbv", "outBorderWidth=" + outBorderWidth);

        outBorderColor = typedArray.getColor(R.styleable.ZBVCircleAvadarView_outBorderColor, Color.TRANSPARENT);

        needBorder = typedArray.getBoolean(R.styleable.ZBVCircleAvadarView_needOutBorder, false);

        typedArray.recycle();

    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
//        QZXTools.logD("zbv", "---onLayout---");
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

//        QZXTools.logD("---onMeasure---");

        //如果是WrapContent模式的话，这里的widthSize和heightSize以父类为基础大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);


        int actualWidthSize;
        int actualHeightSize;

        /*
         *原始图片的宽高大小与手机屏幕的对比获取最小的minSize
         * */
        int srcWidth = sourceBitmap.getWidth();
        int srcHeight = sourceBitmap.getHeight();

        int minSrcSize = Math.min(srcWidth, srcHeight);

        // ===> 核心尺寸
        //因为是圆形图片宽高一致，所以必须取原图宽高最小值和屏幕宽高最小中的最小值
        int minSize = Math.min(minScreenSize, minSrcSize);

//        QZXTools.logE("源图的=》sourceWidth=" + srcWidth + ";sourceHeight=" +
//                srcHeight + ";minSrcSize=" + minSrcSize + ";minSize=" + minSize, null);

        //判断是wrap_content还是其他,如果是具体的就按照具体的尺寸进行缩放处理
        if (widthMode == MeasureSpec.EXACTLY) {
            actualWidthSize = widthSize;
        } else {
            //因为wrapcontent模式 其widthSize为父类尺寸，取核心尺寸与父类尺寸的最小值
            actualWidthSize = Math.min(widthSize, minSize);
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            actualHeightSize = heightSize;
        } else {
            actualHeightSize = Math.min(heightSize, minSize);
        }

        //核心的实际最小值
        int actualMinSize = Math.min(actualWidthSize, actualHeightSize);
//        QZXTools.logE("actualMinSize=" + actualMinSize + ";actualWidthSize="
//                + actualWidthSize + ";actualHeightSize=" + actualHeightSize, null);

        //缩放Bitmap
        //方式一：
        sourceBitmap = QZXTools.scaleMatrix(sourceBitmap, actualMinSize, actualMinSize);
        //方式二：
//        rect.set(0, 0, actualMinSize, actualMinSize);
//        sourceBitmap = QZXTools.scaleCanvas(sourceBitmap, rect);

        //确定最终的圆形图片半径
        circle_radius = Math.min(sourceBitmap.getWidth(), sourceBitmap.getHeight()) / 2;


//        QZXTools.logD("circle_radius=" + circle_radius);


        setMeasuredDimension(circle_radius * 2 + outBorderWidth * 2,
                circle_radius * 2 + outBorderWidth * 2);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

//        QZXTools.logD("---onDraw---");

        mPaint.reset();

        mPaint.setFilterBitmap(true);
        mPaint.setAntiAlias(true);

        canvas.save();
        //偏移一个外边距的大小
        canvas.translate(outBorderWidth, outBorderWidth);

        //PorterDuffXfermode-------------------------------------------------------------
        //如果Config的配置是RGB_565是绘制不出来圆形的,一般都会设置为ARGB_8888
        //创建一个画布，在这个画布上绘制PorterDuffXfermode
        //最后把画布中绘制好的Bitmap让View的Canvas绘制出来

        //要知道画布的大小---这里配置设置ARGB_8888
        Bitmap bitmap = Bitmap.createBitmap(sourceBitmap.getWidth(), sourceBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        circleCanvas.setBitmap(bitmap);
        //简单以minRadius为宽高从左上角算起开画
        circleCanvas.drawCircle(circle_radius, circle_radius, circle_radius, mPaint);
        mPaint.setXfermode(xfermode);
        circleCanvas.drawBitmap(sourceBitmap, 0, 0, mPaint);
        mPaint.setXfermode(null);
        canvas.drawBitmap(bitmap, 0, 0, mPaint);
        //PorterDuffXfermode------------------------------------------------------------

        canvas.restore();

        //重置画笔绘制边角
        mPaint.reset();

        if (needBorder) {
            //添加边框
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStrokeWidth(outBorderWidth);
            mPaint.setColor(outBorderColor);

            //visibleOffset是为了削减存在的视觉偏差
            rectF.set(outBorderWidth, outBorderWidth,
                    outBorderWidth + circle_radius * 2,
                    outBorderWidth + circle_radius * 2);
            canvas.drawArc(rectF, 0, 360, false, mPaint);
        }

    }

}
