package com.telit.zhkt_three.CustomView.interactive.QWFL;

import android.content.Context;
import android.graphics.Rect;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/4/30 8:26
 * <p>
 * 趣味分类使用实例如下：
 * InterestingClassificationLayout interestingClassificationLayout = findViewById(R.id.qwfl_itemLayout);
 * interestingClassificationLayout.addContainerImages();
 * for (int i = 0; i < 6; i++) {
 * interestingClassificationLayout.addItemView(i + "", i, 6);
 * }
 * <p>
 * 建议采用xml布局：
 * android:layout_width="match_parent"
 * android:layout_height="match_parent"
 */
public class InterestingClassificationLayout extends ViewGroup implements View.OnTouchListener {

    private Context mContext;
    /**
     * 非真实的屏幕宽度
     */
    private int screenWidth;
    /**
     * 非真实的屏幕高度
     */
    private int screenHeight;

    public InterestingClassificationLayout(Context context) {
        this(context, null);
    }

    public InterestingClassificationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterestingClassificationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;

        //宽度和高度一开始设置为屏幕宽高
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
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


    /**
     * 选项视图的宽度
     */
    private int itemWidth;

    private boolean isFirstMeasure = true;

    /**
     * 选项视图的矩形边界
     */
    private Rect itemRect;

    private List<View> itemsView;

    /**
     * 添加分散的选项视图
     *
     * @param index 水平放置的位置下标
     */
    public void addItemView(String txt, int index, int total, boolean isLeft) {

        itemCount = total;

        if (itemRect == null) {
            itemRect = new Rect();
        }

        if (itemsView == null) {
            itemsView = new ArrayList<>();
        }

        //方式一
        ItemView view = new ItemView(mContext);
//        //方式二
//        View view = LayoutInflater.from(mContext).inflate(R.layout.qwfl_items_layout, null);

        view.setOnTouchListener(this);

        view.setTVTxt(txt);

        //设置是正确答案绑定：Tag为真表示左边的视图集，为假表示右边的视图集
        view.setTag(isLeft);

        if (isFirstMeasure) {
            isFirstMeasure = false;
            view.measure(0, 0);
            QZXTools.logE("measureWidth=" + view.getMeasuredWidth(), null);
            itemWidth = view.getMeasuredWidth();
        }

//        QZXTools.logE("screenWidth=" + screenWidth + ";screenHeight=" + screenHeight, null);

        //todo 看能否在宽度方向随机放置不重叠不越界
        int left = (int) (screenWidth / total * (index + 1) - itemWidth - Math.random() * (screenWidth / total - itemWidth));
//        int left = screenWidth / total * (index + 1) - itemWidth;
        int top = (int) (screenHeight / 2 + (screenHeight / 3 - itemWidth) * Math.random());

        MarginLayoutParams layoutParams = new MarginLayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        layoutParams.leftMargin = (int) left;
        layoutParams.topMargin = (int) top;

        itemsView.add(view);

        this.addView(view, layoutParams);
    }

    /**
     * 左右两个视图容器
     */
    private View containerOne;
    private View containerTwo;

    /**
     * 容器视图一的矩形边界
     */
    private Rect rectOne;
    /**
     * 容器视图二的矩形边界
     */
    private Rect rectTwo;

    private int bgIndex;

    /**
     * 子项的统计和，当统计和每次消失一个就减一，到零为止就弹出结果框
     */
    private int itemCount;

    /**
     * 添加容器视图
     */
    public void addContainerImages(int bgIndex, String leftTitle, String rightTitle) {

        this.bgIndex = bgIndex;

        rectOne = new Rect();
        rectTwo = new Rect();

        ImageView imgOne = null;
        ImageView imgTwo = null;

        MarginLayoutParams lp_one = new MarginLayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        MarginLayoutParams lp_two = new MarginLayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        switch (bgIndex) {
            case 0:
                View start_left = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_star_home, null);
                TextView start_left_tv = start_left.findViewById(R.id.qwfl_star_home_title);

                start_left_tv.setText(leftTitle);

                imgOne = start_left.findViewById(R.id.qwfl_star_home_img);
                imgOne.setImageResource(R.mipmap.qwfl_star_left);

                start_left.measure(0, 0);
                QZXTools.logE("start_left width=" + start_left.getMeasuredWidth(), null);

                lp_one.leftMargin = (screenWidth / 2 - start_left.getMeasuredWidth()) / 2;
                lp_one.topMargin = screenHeight / 3 - start_left.getMeasuredHeight() / 2;
                this.addView(start_left, lp_one);

                View start_right = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_star_home, null);
                TextView start_right_tv = start_right.findViewById(R.id.qwfl_star_home_title);

                start_right_tv.setText(rightTitle);

                imgTwo = start_right.findViewById(R.id.qwfl_star_home_img);
                imgTwo.setImageResource(R.mipmap.qwfl_star_right);

                start_right.measure(0, 0);
                QZXTools.logE("start_right width=" + start_right.getMeasuredWidth(), null);

                lp_two.leftMargin = screenWidth / 2 + (screenWidth / 2 - start_right.getMeasuredWidth()) / 2;
                lp_two.topMargin = screenHeight / 3 - start_right.getMeasuredHeight() / 2;
                this.addView(start_right, lp_two);

                containerOne = start_left;
                containerTwo = start_right;

                break;
            case 1:
                View plane_left = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_plane_home, null);
                TextView plane_left_tv = plane_left.findViewById(R.id.qwfl_plane_home_title);

                plane_left_tv.setText(leftTitle);

                imgOne = plane_left.findViewById(R.id.qwfl_plane_home_img);
                imgOne.setImageResource(R.mipmap.qwfl_plane);

                plane_left.measure(0, 0);

                lp_one.leftMargin = (screenWidth / 2 - plane_left.getMeasuredWidth()) / 2;
                lp_one.topMargin = screenHeight / 3 - plane_left.getMeasuredHeight() / 2;
                this.addView(plane_left, lp_one);

                View plane_right = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_plane_home, null);
                TextView plane_right_tv = plane_right.findViewById(R.id.qwfl_plane_home_title);

                plane_right_tv.setText(rightTitle);

                imgTwo = plane_right.findViewById(R.id.qwfl_plane_home_img);
                imgTwo.setImageResource(R.mipmap.qwfl_plane);

                plane_right.measure(0, 0);

                lp_two.leftMargin = screenWidth / 2 + (screenWidth / 2 - plane_right.getMeasuredWidth()) / 2;
                lp_two.topMargin = screenHeight / 3 - plane_right.getMeasuredHeight() / 2;
                this.addView(plane_right, lp_two);

                containerOne = plane_left;
                containerTwo = plane_right;

                break;
            case 2:
                View frog_left = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_frog_home, null);
                TextView frog_left_tv = frog_left.findViewById(R.id.qwfl_frog_home_title);

                frog_left_tv.setText(leftTitle);

                imgOne = frog_left.findViewById(R.id.qwfl_frog_home_img);
                imgOne.setImageResource(R.mipmap.qwfl_frog_left);

                frog_left.measure(0, 0);

                lp_one.leftMargin = (screenWidth / 2 - frog_left.getMeasuredWidth()) / 2;
                lp_one.topMargin = screenHeight / 3 - frog_left.getMeasuredHeight() / 2;
                this.addView(frog_left, lp_one);

                View frog_right = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_frog_home, null);
                TextView frog_right_tv = frog_right.findViewById(R.id.qwfl_frog_home_title);

                frog_right_tv.setText(rightTitle);

                imgTwo = frog_right.findViewById(R.id.qwfl_frog_home_img);
                imgTwo.setImageResource(R.mipmap.qwfl_frog_right);

                frog_right.measure(0, 0);

                lp_two.leftMargin = screenWidth / 2 + (screenWidth / 2 - frog_right.getMeasuredWidth()) / 2;
                lp_two.topMargin = screenHeight / 3 - frog_right.getMeasuredHeight() / 2;
                this.addView(frog_right, lp_two);

                containerOne = frog_left;
                containerTwo = frog_right;

                break;
            case 3:
                View jellyfish_left = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_jellyfish_home, null);
                TextView jellyfish_left_tv = jellyfish_left.findViewById(R.id.qwfl_jellyfish_home_title);

                jellyfish_left_tv.setText(leftTitle);

                imgOne = jellyfish_left.findViewById(R.id.qwfl_jellyfish_home_img);
                imgOne.setImageResource(R.mipmap.qwfl_jellyfish);

                jellyfish_left.measure(0, 0);

                lp_one.leftMargin = (screenWidth / 2 - jellyfish_left.getMeasuredWidth()) / 2;
                lp_one.topMargin = screenHeight / 3 - jellyfish_left.getMeasuredHeight() / 2;
                this.addView(jellyfish_left, lp_one);

                View jellyfish_right = LayoutInflater.from(mContext).inflate(R.layout.view_qwfl_jellyfish_home, null);
                TextView jellyfish_right_tv = jellyfish_right.findViewById(R.id.qwfl_jellyfish_home_title);

                jellyfish_right_tv.setText(rightTitle);

                imgTwo = jellyfish_right.findViewById(R.id.qwfl_jellyfish_home_img);
                imgTwo.setImageResource(R.mipmap.qwfl_jellyfish);

                jellyfish_right.measure(0, 0);

                lp_two.leftMargin = screenWidth / 2 + (screenWidth / 2 - jellyfish_right.getMeasuredWidth()) / 2;
                lp_two.topMargin = screenHeight / 3 - jellyfish_right.getMeasuredHeight() / 2;
                this.addView(jellyfish_right, lp_two);

                containerOne = jellyfish_left;
                containerTwo = jellyfish_right;

                break;
        }

        rectOne.set(lp_one.leftMargin + 10, lp_one.topMargin + 10,
                lp_one.leftMargin + imgOne.getMeasuredWidth() - 10, lp_one.topMargin + imgOne.getMeasuredHeight() - 10);

        int left = lp_two.leftMargin + 10;
        int top = lp_two.topMargin + 10;
        int right = lp_two.leftMargin + imgTwo.getMeasuredWidth() - 10;
        int bottom = lp_two.topMargin + imgTwo.getMeasuredHeight() - 10;
        rectTwo.set(left, top, right, bottom);
    }

    private float firstX;
    private float firstY;

    /**
     * 是否正确，正确则吸入，错误则反弹出
     */
    private boolean isRight = false;

    private boolean isEnd = false;

    /**
     * 动画开始
     */
    private boolean isStart = false;

    private int originalLeft;
    private int originalTop;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        /**
         * 因为只有选项视图才设置了触摸监听所以没有必要类型判断了
         * 使用方式一添加选项视图就开启类型判断，否则注释掉即可
         */
        if (v instanceof ItemView) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    firstX = event.getX();
                    firstY = event.getY();

                    isEnd = false;

                    originalLeft = v.getLeft();
                    originalTop = v.getTop();
                    break;
                case MotionEvent.ACTION_MOVE:
                    float moveX = event.getX();
                    float moveY = event.getY();

                    int offsetX = (int) (moveX - firstX);
                    int offsetY = (int) (moveY - firstY);

                    MarginLayoutParams layoutParams = (MarginLayoutParams) v.getLayoutParams();
//                    QZXTools.logE("getLeft=" + v.getLeft() + ";getTop=" + v.getTop(), null);

                    int leftMargin = v.getLeft() + offsetX;
                    int topMargin = v.getTop() + offsetY;

                    //防止移出边界
                    if (leftMargin < 0) {
                        leftMargin = 0;
                    } else if (leftMargin + itemWidth > screenWidth) {
                        leftMargin = screenWidth - itemWidth;
                    }
                    if (topMargin < 0) {
                        topMargin = 0;
                    } else if (topMargin + itemWidth > screenHeight) {
                        topMargin = screenHeight - itemWidth;
                    }

                    layoutParams.leftMargin = leftMargin;
                    layoutParams.topMargin = topMargin;

                    v.setLayoutParams(layoutParams);

//                    QZXTools.logE(
//                            "firstX=" + firstX + ";firstY=" + firstY
//                                    + "offsetX=" + offsetX + ";offsetY=" + offsetY
//                                    + ";one left=" + rectOne.left + ";top=" + rectOne.top
//                                    + ";right=" + rectOne.right + ";bottom=" + rectOne.bottom
//                                    + ";=>two left=" + rectTwo.left + ";top=" + rectTwo.top
//                                    + ";right=" + rectTwo.right + ";bottom=" + rectTwo.bottom
//                                    + ";moveX=" + moveX + ";moveY=" + moveY, null);

//                    QZXTools.logE("item left=" + v.getLeft() + "item top=" + v.getTop()
//                            + ";item right=" + v.getRight() + ";item bottom=" + v.getBottom()
//                            + ";==>rectTwo left=" + rectTwo.left + ";top=" + rectTwo.top + ";right="
//                            + rectTwo.right + ";bottom=" + rectTwo.bottom, null);

                    itemRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

                    if (rectOne.intersects(itemRect.left, itemRect.top, itemRect.right, itemRect.bottom)) {
//                        QZXTools.logE("rectOne touch", null);
                        //是左才正确
                        boolean tag = (boolean) v.getTag();
                        if (tag == true) {
                            isRight = true;
                        } else {
                            isRight = false;
                        }
                        isEnd = false;
                        scaleInOut(containerOne);
                    } else if (rectTwo.intersects(itemRect.left, itemRect.top, itemRect.right, itemRect.bottom)) {
//                        QZXTools.logE("rectTwo touch", null);
                        //是右才正确
                        boolean tag = (boolean) v.getTag();
                        if (tag == false) {
                            isRight = true;
                        } else {
                            isRight = false;
                        }
                        isEnd = false;
                        scaleInOut(containerTwo);
                    } else {
//                        QZXTools.logE("stop...", null);
                        isStart = false;
                        isEnd = true;

                        containerOne.clearAnimation();
                        containerTwo.clearAnimation();
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    if (isRight && !isEnd) {
                        //正确结果且在动画中，缩小消失
                        scaleInAndMiss(v);
                    } else if (!isRight && !isEnd) {
                        //错误的结果且在动画中，反弹至最近的起点位置

//                        translateToOrigin(v);

                        MarginLayoutParams lp = (MarginLayoutParams) v.getLayoutParams();
                        lp.leftMargin = originalLeft;
                        lp.topMargin = originalTop;
                        v.setLayoutParams(lp);

                        isStart = false;
                        isEnd = true;

                        containerOne.clearAnimation();
                        containerTwo.clearAnimation();

                    }else{
                        isStart = false;
                        isEnd = true;

                        containerOne.clearAnimation();
                        containerTwo.clearAnimation();
                    }
                    break;
            }
            return true;
        }
        return false;
    }

    /**
     * 重置视图
     */
    public void resetView() {
        for (View view : itemsView) {
            this.removeView(view);
        }
    }

    /**
     * 补间动画和设置属性混用有点问题，特别是setFillAfter(true)
     */
    private void translateToOrigin(View view) {

        QZXTools.logE("originalLeft=" + originalLeft + ";originalTop=" + originalTop
                + ";nowLeft=" + view.getLeft() + ";noeTop=" + view.getTop(), null);

        //原始减去当前的
        TranslateAnimation translateAnimation = new TranslateAnimation(Animation.ABSOLUTE, 0,
                Animation.ABSOLUTE, originalLeft - view.getLeft(),
                Animation.ABSOLUTE, 0, Animation.ABSOLUTE, originalTop - view.getTop());
        translateAnimation.setDuration(500);
//        translateAnimation.setFillAfter(true);

        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {

                MarginLayoutParams layoutParams = (MarginLayoutParams) view.getLayoutParams();
                layoutParams.leftMargin = originalLeft;
                layoutParams.topMargin = originalTop;
                view.setLayoutParams(layoutParams);


                isStart = false;
                isEnd = true;

                containerOne.clearAnimation();
                containerTwo.clearAnimation();

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(translateAnimation);

    }

    private void scaleInAndMiss(View view) {
        ScaleAnimation scaleMiss = new ScaleAnimation(1, 0.0f, 1, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scaleMiss.setDuration(500);
        scaleMiss.setFillAfter(true);

        scaleMiss.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                view.setVisibility(View.GONE);

                isStart = false;
                isEnd = true;

                containerOne.clearAnimation();
                containerTwo.clearAnimation();

                itemCount--;
                if (itemCount == 0) {
                    //弹出结果框：分类标题和分类集合
                    if (qwflResultInterface != null) {
                        qwflResultInterface.popQWFLResultDialog();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(scaleMiss);
    }

    /**
     * 放大缩小动画
     */
    private void scaleInOut(View view) {
        if (isStart) {
            return;
        }

        isStart = true;

        ScaleAnimation scale_big = new ScaleAnimation(1, 1.3f, 1, 1.3f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale_big.setDuration(100);
        scale_big.setFillAfter(true);

        ScaleAnimation scale_normal = new ScaleAnimation(1.3f, 1.0f, 1.3f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        scale_normal.setDuration(100);
        scale_normal.setFillAfter(true);

        scale_big.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {


            }

            @Override
            public void onAnimationEnd(Animation animation) {
//                QZXTools.logE("isRight=" + isRight + ";isEnd=" + isEnd, null);
                if (!isRight && !isEnd) {
                    view.startAnimation(scale_normal);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        scale_normal.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (!isRight && !isEnd) {
                    view.startAnimation(scale_big);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(scale_big);
    }

    /**
     * ItemView内部类
     */
    public class ItemView extends FrameLayout {
        private TextView textView;

        public ItemView(@NonNull Context context) {
            this(context, null);
        }

        public ItemView(@NonNull Context context, @Nullable AttributeSet attrs) {
            this(context, attrs, 0);
        }

        public ItemView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
            switch (bgIndex) {
                case 0:
                    View view_star = LayoutInflater.from(context).inflate(R.layout.view_qwfl_star, this, true);
                    textView = view_star.findViewById(R.id.qwfl_star_tv);
                    break;
                case 1:
                    View view_plane = LayoutInflater.from(context).inflate(R.layout.view_qwfl_plane, this, true);
                    textView = view_plane.findViewById(R.id.qwfl_plane_tv);
                    break;
                case 2:
                    View view_frog = LayoutInflater.from(context).inflate(R.layout.view_qwfl_frog, this, true);
                    textView = view_frog.findViewById(R.id.qwfl_frog_tv);
                    break;
                case 3:
                    View view_jellyfish = LayoutInflater.from(context).inflate(R.layout.view_qwfl_jellyfish, this, true);
                    textView = view_jellyfish.findViewById(R.id.qwfl_jellyfish_tv);
                    break;
            }
        }

        /**
         * 设置文本文字
         */
        private void setTVTxt(String text) {
            textView.setText(text);
        }
    }

    private QWFLResultInterface qwflResultInterface;

    public void setQwflResultInterface(QWFLResultInterface qwflResultInterface) {
        this.qwflResultInterface = qwflResultInterface;
    }

    public interface QWFLResultInterface {
        void popQWFLResultDialog();
    }
}