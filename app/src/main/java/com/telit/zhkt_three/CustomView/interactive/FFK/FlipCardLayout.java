package com.telit.zhkt_three.CustomView.interactive.FFK;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

/**
 * author: qzx
 * Date: 2019/8/2 8:40
 */
public class FlipCardLayout extends ViewGroup implements View.OnTouchListener, View.OnClickListener {

    private Context mContext;
    /**
     * 非真实的屏幕宽度
     */
    private int screenWidth;
    /**
     * 非真实的屏幕高度
     */
    private int screenHeight;

    public FlipCardLayout(Context context) {
        this(context, null);
    }

    public FlipCardLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlipCardLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        //宽度和高度一开始设置为屏幕宽高
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;

        FFK_Max_Width = getResources().getDimensionPixelOffset(R.dimen.x400);
        FFK_Max_Height = getResources().getDimensionPixelOffset(R.dimen.x500);
        spin_height = getResources().getDimensionPixelOffset(R.dimen.x110);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int mode_width = MeasureSpec.getMode(widthMeasureSpec);
        int size_width = MeasureSpec.getSize(widthMeasureSpec);//具体值或者Match_Parent
        int mode_height = MeasureSpec.getMode(heightMeasureSpec);
        int size_height = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        //单行的宽高
        int lineWidth = 0;
        int lineHeight = 0;

//        QZXTools.logE("zbv", "childCount=" + getChildCount(), null);
        //得到layout中的子类个数
        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = getChildAt(i);
            //判断是否INVISIBLE或者GONE
            if (child.getVisibility() != View.VISIBLE) {
                //最后一个的话需要总结一下
                if (i == (childCount - 1)) {
                    width = Math.max(width, lineWidth);
                    height += lineHeight;
                }
                continue;
            }
            //测量子类
            measureChild(child, widthMeasureSpec, heightMeasureSpec);
            //子类的宽度+左右的padding+左右的margin
//            Log.d("zbv","getWidth="+child.getWidth()+";getMeasuredWidth="+child.getMeasuredWidth());

            int childWidth = child.getMeasuredWidth();
            int childHeight = child.getMeasuredHeight();

            if (lineWidth + childWidth > size_width - getPaddingLeft() - getPaddingRight()) {
                //超过了父类的宽度->getPaddingLeft和Right是Layout自己的所以需要减去
                width = Math.max(width, lineWidth);
                lineWidth = childWidth;//另起一行的宽度
                lineHeight += childHeight;
            } else {
                lineWidth += childWidth;
                //可能一行排列中有的视图高度会比之前高
                lineHeight = Math.max(lineHeight, childHeight);
            }
            //最后再比较一下宽度
            if (i == (childCount - 1)) {
                width = Math.max(width, lineWidth);
                height += lineHeight;
            }
        }

        setMeasuredDimension((mode_width == MeasureSpec.EXACTLY) ? size_width : width,
                (mode_height == MeasureSpec.EXACTLY) ? size_height : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();

        //具体化Layout的宽度和高度
        screenWidth = getMeasuredWidth();
        screenHeight = getMeasuredHeight();
//        QZXTools.logE("onLayout measureWidth=" + getMeasuredWidth() + ";measureHeight=" + getMeasuredHeight(), null);

        for (int i = 0; i < childCount; i++) {

            View childView = getChildAt(i);

            MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();

//            QZXTools.logE("left=" + childView.getLeft() + ";top=" + childView.getTop()
//                    + ";right=" + childView.getRight() + ";bottom=" + childView.getBottom()
//                    + ";width=" + childView.getMeasuredWidth() + ";height=" + childView.getHeight(), null);

            childView.layout(layoutParams.leftMargin, layoutParams.topMargin,
                    layoutParams.leftMargin + childView.getMeasuredWidth(), layoutParams.topMargin + childView.getMeasuredHeight());

        }
    }

    @Override
    protected LayoutParams generateLayoutParams(LayoutParams lp) {
        return new MarginLayoutParams(lp);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new MarginLayoutParams(getContext(), attrs);
    }

    private static final int MAX_HORIZONTAL_COUNT = 3;
    private int FFK_Max_Width;
    private int FFK_Max_Height;

    //减除时间显示空间距离
    private int spin_height;

    //水平间距
    private int x_interval;
    //垂直间距
    private int y_interval;

    private int itemHeightCount = 1;
    private int itemWidthCount = 1;

    /**
     * 添加一张翻翻卡：
     * 图片、文字
     * 正面、反面
     * 默认首先塞入正面，然后塞入反面
     *
     * @param signOne 标记是图片还是文字 0文字，1图片
     * @param signOne 标记是图片还是文字 0文字，1图片
     */
    public void addFlipCard(int signOne, String forward, int resIdOne, int signTwo, String backward, int resIdTwo, int totalSize, int index) {
        if (totalSize > 9) {
            return;
        }

        FrameLayout frameLayout = new FrameLayout(getContext());
        //高度测定和宽度绑定：5:4
        if (totalSize <= 3) {
            FFK_Max_Height = 500;
            FFK_Max_Width = 400;
            itemHeightCount = 1;
            itemWidthCount = totalSize;
        } else if (totalSize > 3 && totalSize <= 6) {
            FFK_Max_Height = 400;
            FFK_Max_Width = 320;
            itemHeightCount = 2;
            itemWidthCount = MAX_HORIZONTAL_COUNT;
        } else if (totalSize > 6) {
            FFK_Max_Height = 300;
            FFK_Max_Width = 240;
            itemHeightCount = 3;
            itemWidthCount = MAX_HORIZONTAL_COUNT;
        }

        x_interval = (screenWidth - FFK_Max_Width * itemWidthCount) / (itemWidthCount + 1);
        y_interval = (screenHeight - spin_height - FFK_Max_Height * itemHeightCount) / (itemHeightCount + 1);

        frameLayout.setBackground(getResources().getDrawable(R.drawable.shape_ffk_test));

        MarginLayoutParams layoutParams = new MarginLayoutParams(FFK_Max_Width, FFK_Max_Height);
        layoutParams.leftMargin = x_interval * (index % 3 + 1) + FFK_Max_Width * (index % 3);
        layoutParams.topMargin = y_interval * (index / 3 + 1) + FFK_Max_Height * (index / 3);


        //正面
        if (signOne == 0) {
            TextView textView = new TextView(getContext());
            FrameLayout.LayoutParams lp_txt = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            textView.setText(forward);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x48));
            frameLayout.addView(textView, lp_txt);
        } else if (signOne == 1) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams lp_img = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setBackgroundResource(resIdOne);
            frameLayout.addView(imageView, lp_img);
        }

        //反面
        if (signTwo == 0) {
            TextView textView = new TextView(getContext());
            FrameLayout.LayoutParams lp_txt = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            textView.setText(backward);
            textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x48));
            textView.setGravity(Gravity.CENTER);
            //反面先隐藏
            textView.setVisibility(INVISIBLE);
            frameLayout.addView(textView, lp_txt);
        } else if (signTwo == 1) {
            ImageView imageView = new ImageView(getContext());
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            FrameLayout.LayoutParams lp_img = new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            imageView.setBackgroundResource(resIdTwo);
            //反面先隐藏
            imageView.setVisibility(INVISIBLE);
            frameLayout.addView(imageView, lp_img);
        }

        frameLayout.setOnClickListener(this);
        //一开始处于正面
        frameLayout.setTag(true);

        this.addView(frameLayout, layoutParams);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }

    private View clickView;

    @Override
    public void onClick(View v) {
        if (mOpenFlipAnimation == null) {
            initOpenAnim(v);
        }

        //用作判断当前点击事件发生时动画是否正在执行
        if (mOpenFlipAnimation.hasStarted() && !mOpenFlipAnimation.hasEnded()) {
            return;
        }

        clickView = v;
        //判断动画执行
        clickView.startAnimation(mOpenFlipAnimation);
    }

    private Rotate3dAnimation mOpenFlipAnimation;

    /**
     * 这里的view参数仅供测量大小即宽高
     */
    private void initOpenAnim(View view) {
        mOpenFlipAnimation = new Rotate3dAnimation
                (0, 90, view.getWidth() / 2, view.getHeight() / 2, 0, true);
        mOpenFlipAnimation.setDuration(200);
        mOpenFlipAnimation.setFillAfter(true);
        mOpenFlipAnimation.setInterpolator(new AccelerateInterpolator());
        mOpenFlipAnimation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                FrameLayout frameLayout = (FrameLayout) clickView;
                boolean isFront = (boolean) frameLayout.getTag();
                if (isFront) {
                    frameLayout.getChildAt(0).setVisibility(View.INVISIBLE);
                    frameLayout.getChildAt(1).setVisibility(View.VISIBLE);

                    frameLayout.setTag(false);
                } else {
                    frameLayout.getChildAt(0).setVisibility(View.VISIBLE);
                    frameLayout.getChildAt(1).setVisibility(View.INVISIBLE);

                    frameLayout.setTag(true);
                }

                //从270到360度，顺时针旋转视图，此时reverse参数为false，达到360度动画结束时视图变得可见，depthZ代表缩放原先是400
                Rotate3dAnimation rotateAnimation = new Rotate3dAnimation
                        (270, 360, view.getWidth() / 2, view.getHeight() / 2, 0, false);
                rotateAnimation.setDuration(200);
                rotateAnimation.setFillAfter(true);
                rotateAnimation.setInterpolator(new DecelerateInterpolator());
                clickView.startAnimation(rotateAnimation);
            }
        });
    }
}
