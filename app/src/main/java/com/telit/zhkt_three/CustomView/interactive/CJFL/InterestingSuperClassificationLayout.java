package com.telit.zhkt_three.CustomView.interactive.CJFL;

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
import android.widget.FrameLayout;
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
public class InterestingSuperClassificationLayout extends ViewGroup implements View.OnTouchListener {

    private Context mContext;
    /**
     * 非真实的屏幕宽度
     */
    private int screenWidth;
    /**
     * 非真实的屏幕高度
     */
    private int screenHeight;

    public InterestingSuperClassificationLayout(Context context) {
        this(context, null);
    }

    public InterestingSuperClassificationLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public InterestingSuperClassificationLayout(Context context, AttributeSet attrs, int defStyleAttr) {
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
    private int itemHeight;

    private boolean isFirstMeasure = true;

    /**
     * 选项视图的矩形边界
     */
    private Rect itemRect;

    private List<View> itemsView;

    private static final int MAX_HORIZONTAL_COUNT = 7;

    private int initItemHeightCount = 1;

    /**
     * item颜色背景，从一到七循环
     */
    private int itemColorCount = 0;

    /**
     * 添加分散的选项视图
     *
     * @param tagIndex 代表着容器的大类下标
     */
    public void addItemView(String txt, int total, int tagIndex) {

        itemCount = total;

        if (itemRect == null) {
            itemRect = new Rect();
        }

        if (itemsView == null) {
            itemsView = new ArrayList<>();
        }

        //方式一
        ItemView view = new ItemView(mContext);

        view.setOnTouchListener(this);

        itemColorCount++;
        view.setTVTxt(txt, itemColorCount % 7);

        //设置是正确答案绑定
        view.setTag(tagIndex);

        if (isFirstMeasure) {
            isFirstMeasure = false;
            view.measure(0, 0);
            QZXTools.logE("measureWidth=" + view.getMeasuredWidth(), null);
            itemWidth = view.getMeasuredWidth();
            itemHeight = view.getMeasuredHeight();
        }
//        QZXTools.logE("screenWidth=" + screenWidth + ";screenHeight=" + screenHeight, null);

        //位置
        if (itemColorCount > MAX_HORIZONTAL_COUNT * initItemHeightCount) {
            initItemHeightCount++;
        }

        int index = itemColorCount % 7;
        if (itemColorCount % 7 == 0) {
            index = 7;
        }
        int left = (int) (screenWidth / MAX_HORIZONTAL_COUNT * index - itemWidth - Math.random() * (screenWidth / total - itemWidth));
        int top = screenHeight / 4 * 3 - itemHeight * (initItemHeightCount - 1) * 2;

        MarginLayoutParams layoutParams = new MarginLayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

        layoutParams.leftMargin = (int) left;
        layoutParams.topMargin = (int) top;

        itemsView.add(view);

        this.addView(view, layoutParams);
    }

    /**
     * 所有容器视图
     */
    private List<View> containerList;

    /**
     * 所有试题容器的矩形边界:注意矩形代表容器几
     */
    private List<Rect> rectList;

    /**
     * 子项的统计和，当统计和每次消失一个就减一，到零为止就弹出结果框
     */
    private int itemCount;

    /**
     * 添加容器视图
     *
     * @param totalContainerSize 总的容器个数
     * @param index              从一开始
     */
    public void addContainerImages(String title, int totalContainerSize, int index) {
        Rect rect_container = new Rect();
        MarginLayoutParams layoutParams = new MarginLayoutParams
                (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        View view = LayoutInflater.from(mContext).inflate(R.layout.view_cjfl_item_home, null);
        TextView textView = view.findViewById(R.id.cjfl_item_home_tv);
        textView.setText(title);
        view.measure(0, 0);
        QZXTools.logE("view width=" + view.getMeasuredWidth() + ";view height=" + view.getMeasuredHeight(), null);
        layoutParams.leftMargin = screenWidth / totalContainerSize * (index - 1)
                + (screenWidth / totalContainerSize - view.getMeasuredWidth()) / 2;
        layoutParams.topMargin = screenHeight / 5 - view.getMeasuredHeight() / 2;
        this.addView(view, layoutParams);

        if (containerList == null) {
            containerList = new ArrayList<>();
        }

        containerList.add(view);

        int left = layoutParams.leftMargin + 10;
        int top = layoutParams.topMargin + 10;
        int right = layoutParams.leftMargin + view.getMeasuredWidth() - 10;
        int bottom = layoutParams.topMargin + view.getMeasuredHeight() - 10;
        rect_container.set(left, top, right, bottom);

        if (rectList == null) {
            rectList = new ArrayList<>();
        }

        rectList.add(rect_container);
    }

    private float firstX;
    private float firstY;

    /**
     * 是否正确，正确则吸入，错误则反弹出
     */
    private boolean isRight = false;

    private boolean isEnd = false;

    //是否找到相交的对象
    private boolean hasIntersect = false;

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
                    hasIntersect = false;

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

                    itemRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());

                    for (int i = 0; i < rectList.size(); i++) {
                        //相交依据tag=i判断属于哪个分类类型
                        if (rectList.get(i).intersects(itemRect.left, itemRect.top, itemRect.right, itemRect.bottom)) {
                            int tag = (int) v.getTag();
                            QZXTools.logE("tag=" + tag + ";i=" + i, null);
                            if (tag == (i + 1)) {
                                isRight = true;
                            } else {
                                isRight = false;
                            }
                            isEnd = false;
                            scaleInOut(containerList.get(i));
                            //结束这个for循环，因为找到了相交的边界对象
                            hasIntersect = true;
                            break;
                        } else {
                            hasIntersect = false;
                        }
                    }

                    if (!hasIntersect) {
                        isStart = false;
                        isEnd = true;

                        if (curAnimView != null) {
                            curAnimView.clearAnimation();
                        }
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
                        hasIntersect = false;

                        if (curAnimView != null) {
                            curAnimView.clearAnimation();
                        }

                    } else {
                        isStart = false;
                        isEnd = true;

                        if (curAnimView != null) {
                            curAnimView.clearAnimation();
                        }
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
        itemColorCount = 0;
        initItemHeightCount = 1;
        for (View view : itemsView) {
            this.removeView(view);
        }
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

                if (curAnimView != null) {
                    curAnimView.clearAnimation();
                }

                itemCount--;
                if (itemCount == 0) {
                    //弹出结果框：分类标题和分类集合
                    if (cjflResultInterface != null) {
                        cjflResultInterface.popCJFLResultDialog();
                    }
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(scaleMiss);
    }

    private View curAnimView;

    /**
     * 放大缩小动画
     */
    private void scaleInOut(View view) {
        if (isStart) {
            return;
        }

        curAnimView = view;

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
            View view = LayoutInflater.from(context).inflate(R.layout.view_cjfl_item, this, true);
            textView = view.findViewById(R.id.cjfl_item_tv);
        }

        /**
         * 设置文本文字
         */
        private void setTVTxt(String text, int colorIndex) {
            textView.setText(text);
            switch (colorIndex) {
                case 0:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_one));
                    break;
                case 1:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_two));
                    break;
                case 2:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_three));
                    break;
                case 3:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_four));
                    break;
                case 4:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_five));
                    break;
                case 5:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_six));
                    break;
                case 6:
                    textView.setBackground(getResources().getDrawable(R.drawable.shape_cjfl_item_seven));
                    break;
            }
        }
    }

    private CJFLResultInterface cjflResultInterface;

    public void setCJFLResultInterface(CJFLResultInterface cjflResultInterface) {
        this.cjflResultInterface = cjflResultInterface;
    }

    public interface CJFLResultInterface {
        void popCJFLResultDialog();
    }
}