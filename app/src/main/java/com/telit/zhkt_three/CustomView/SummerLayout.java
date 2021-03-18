package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.graphics.Point;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.greendao.AppInfoDao;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/3/18 15:58
 * <p>
 * item间动画交互式的换位网格式Layout
 * </p>
 */
public class SummerLayout extends ViewGroup implements View.OnTouchListener {
    private List<Point> viewListPoint;
    private List<View> views;
    private List<AppInfo> appInfos;

    private int rowNum;
    private int columnNum;

    private RectF delRect;

    public void setAppInfos(List<AppInfo> appInfos) {
        this.appInfos = appInfos;
    }

    public SummerLayout(Context context) {
        this(context, null);
    }

    public SummerLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SummerLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SummerLayout, defStyleAttr, 0);
        rowNum = typedArray.getInt(R.styleable.SummerLayout_rowNum, -1);
        columnNum = typedArray.getInt(R.styleable.SummerLayout_columnNum, -1);
        typedArray.recycle();
        viewListPoint = new ArrayList<>();
        views = new ArrayList<>();
        delRect = new RectF();
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

    /**
     * 暂定传入的为图标文本类型，宽高一致
     */
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        //依据子类的宽度以及自身的宽度动态调整
        int childCount = getChildCount();

        int leftPadding = getPaddingLeft();
        int rightPadding = getPaddingRight();
        int topPadding = getPaddingTop();
        int bottomPadding = getPaddingBottom();

        if (rowNum == -1 || columnNum == -1) {
            int spanCount;

            //除去padding的大小
            int layoutWidth = this.getMeasuredWidth() - leftPadding - rightPadding;

            if (childCount >= 1) {
                View view = getChildAt(0);
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();

                spanCount = (int) Math.floor(layoutWidth * 1.0f / childWidth);

                int averageWidthHalf = Math.round(layoutWidth / spanCount / 2 * 1.0f);

                QZXTools.logE("width=" + childWidth + ";layout width=" + layoutWidth
                        + ";spanCount=" + spanCount + ";averageWidthHalf=" + averageWidthHalf, null);

                //todo 需要更新
                if (viewListPoint.size() > 0) {

                    for (int j = 0; j < viewListPoint.size(); j++) {
                        View childView = getChildAt(j);

                        childView.setOnTouchListener(this);

                        childView.setTag(j);

                        QZXTools.logE("point=" + viewListPoint, null);

                        childView.layout(viewListPoint.get(j).x, viewListPoint.get(j).y,
                                viewListPoint.get(j).x + childWidth, viewListPoint.get(j).y + childHeight);
                    }
                } else {
                    for (int i = 0; i < childCount; i++) {

                        View childView = getChildAt(i);

                        childView.setOnTouchListener(this);

                        childView.setTag(i);

                        float left = 2 * averageWidthHalf * (i % spanCount + 0.5f) - childWidth / 2 + leftPadding;
                        float top = (i / spanCount) * childWidth + topPadding;

                        float right = left + childWidth;
                        float bottom = top + childHeight;

                        Point point = new Point((int) left, (int) top);
                        viewListPoint.add(point);

                        views.add(childView);

                        QZXTools.logE("left=" + left + ";top=" + top
                                + ";right=" + right + ";bottom=" + bottom, null);

                        childView.layout((int) left, (int) top, (int) right, (int) bottom);
                    }
                }
            }
        } else {
            //指定了行数和列数

            //除去padding的大小
            int layoutWidth = this.getMeasuredWidth() - leftPadding - rightPadding;
            int layoutHeight = this.getMeasuredHeight() - topPadding - bottomPadding;

            if (columnNum * rowNum >= 1) {
                View view = getChildAt(0);
                if (view == null) {
                    return;
                }
                int childWidth = view.getMeasuredWidth();
                int childHeight = view.getMeasuredHeight();

                int averageWidthHalf = Math.round(layoutWidth / columnNum / 2 * 1.0f);
                int averageHeightHalf = Math.round(layoutHeight / rowNum / 2 * 1.0f);

                //todo 需要更新
                if (viewListPoint.size() > 0) {

                    for (int j = 0; j < viewListPoint.size(); j++) {
                        View childView = getChildAt(j);

                        childView.setOnTouchListener(this);

                        childView.setTag(j);

//                        QZXTools.logE("point=" + viewListPoint, null);

                        childView.layout(viewListPoint.get(j).x, viewListPoint.get(j).y,
                                viewListPoint.get(j).x + childWidth, viewListPoint.get(j).y + childHeight);

                    }
                } else {
                    for (int i = 0; i < columnNum * rowNum; i++) {
                        View childView = getChildAt(i);
                        if (childView != null) {
                            childView.setOnTouchListener(this);

                            childView.setTag(i);

                            float left = 2 * averageWidthHalf * (i % columnNum + 0.5f) - childWidth / 2 + leftPadding;
                            float top = 2 * averageHeightHalf * (i / columnNum + 0.5f) - childHeight / 2 + topPadding;

                            float right = left + childWidth;
                            float bottom = top + childHeight;

                            Point point = new Point((int) left, (int) top);
                            viewListPoint.add(point);
                            views.add(childView);

//                            QZXTools.logE("left=" + left + ";top=" + top
//                                    + ";right=" + right + ";bottom=" + bottom, null);

                            childView.layout((int) left, (int) top, (int) right, (int) bottom);
                        }
                    }
                }
            }


        }
    }
    private Runnable longTapRunnable = new Runnable() {
        @Override
        public void run() {
            if (!touchUp && !touchMove) {
//                QZXTools.logE("BOOLEAN=" + appInfos.get(views.indexOf(preView)).getIsSystemApp()
//                        + "packagename=" + appInfos.get(views.indexOf(preView)).getPackageName(), null);
                //长按显示删除图标
                if ((!appInfos.get(views.indexOf(preView)).getIsSystemApp())
                        && preView instanceof FrameLayout) {
                    View imgView = ((FrameLayout) preView).getChildAt(1);
                    imgView.setVisibility(VISIBLE);
                }
                longTapEnable = true;
                isShaked = true;
                ShakeAnim(preView, 10.0f);
                int tag = (Integer) preView.getTag();
                firstPoint = viewListPoint.get(tag);
            } else {
                isSelect = true;
                preView = null;
            }
        }
    };

    private boolean isSelect = true;
    private View preView;
    private Point firstPoint;

    private int swapOne;
    private int swapTwo;

    private long firstTime;
    private boolean longTapEnable = false;

    private float firstX;
    private float firstY;

    private boolean touchMove = false;
    private boolean touchUp = false;

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

                touchUp = false;
                touchMove = false;

                firstX = event.getX();
                firstY = event.getY();
//                QZXTools.logE("start X=" + event.getX() + ";Y=" + event.getY(), null);

                firstTime = System.currentTimeMillis();

                if (v instanceof FrameLayout) {
                    View imgView = ((FrameLayout) v).getChildAt(1);
                    if (imgView instanceof ImageView && imgView.getVisibility() == VISIBLE) {
                        int left = v.getMeasuredWidth() - imgView.getMeasuredWidth();
                        int right = v.getMeasuredWidth();
                        int bottom = imgView.getMeasuredHeight();
                        delRect.set(left, 0, right, bottom);
//                        QZXTools.logE("left=" + left + ";right=" + right + ";bottom=" + bottom, null);
                        if (delRect.contains(firstX, firstY)) {
                            //触碰删除按钮
                            EventBus.getDefault().post(appInfos.get(views.indexOf(v)).getPackageName(), "delete_app");

                            if (preView instanceof FrameLayout) {
                                View imageView = ((FrameLayout) preView).getChildAt(1);
                                imageView.setVisibility(INVISIBLE);
                            }

                            longTapEnable = false;
                            isShaked = false;
                            preView.clearAnimation();
                            isSelect = true;
                            preView = null;

                            return false;
                        }
                    }
                    View cView = ((FrameLayout) v).getChildAt(0);
                    if (cView instanceof LinearLayout) {
                        ((LinearLayout) cView).getChildAt(0).setAlpha(0.7f);
                    }
                }

                if (!longTapEnable)
                   // postDelayed(longTapRunnable, 1000);

                if (isSelect && preView == null) {
                    isSelect = false;
                    preView = v;
                } else if (preView != null && preView != v) {
                    if (longTapEnable) {

                        if (preView instanceof FrameLayout) {
                            View imgView = ((FrameLayout) preView).getChildAt(1);
                            imgView.setVisibility(INVISIBLE);
                        }

                        int tag = (Integer) v.getTag();
                        Point secondPoint = viewListPoint.get(tag);

                        //开始动画
                        translate(preView, firstPoint, secondPoint, false);

                        translate(v, secondPoint, firstPoint, true);

                        swapOne = (int) preView.getTag();
                        swapTwo = (int) v.getTag();

                        isSelect = true;
                        preView = null;
                    } else {
                        isSelect = true;
                        preView = null;
                    }
                } else {
                    isShaked = false;
                    preView.clearAnimation();
                    isSelect = true;
                    preView = null;
                }
                break;
            case MotionEvent.ACTION_MOVE:

                float moveX = event.getX();
                float moveY = event.getY();

//                QZXTools.logE("move X=" + event.getX() + ";Y=" + event.getY(), null);

                if (moveX == firstX && moveY == firstY) {
                    touchMove = false;
                } else {
                    touchMove = true;
                }
                break;
            case MotionEvent.ACTION_UP:
                touchUp = true;

                if (v instanceof FrameLayout) {
                    View cView = ((FrameLayout) v).getChildAt(0);
                    if (cView instanceof LinearLayout) {
                        ((LinearLayout) cView).getChildAt(0).setAlpha(1.0f);
                    }
                }

                long endTime = System.currentTimeMillis();

                if (longTapEnable) {
                    if (preView == null) {
                        if (v instanceof FrameLayout) {
                            View imgView = ((FrameLayout) v).getChildAt(1);
                            imgView.setVisibility(INVISIBLE);
                        }
                        longTapEnable = false;
                    }
                } else {
                    if (endTime - firstTime < 500) {
                        //点击事件
                        Intent intent = getContext().getPackageManager().
                                getLaunchIntentForPackage(appInfos.get(views.indexOf(v)).getPackageName());
                        if (intent != null) {
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            getContext().startActivity(intent);
                        }
                    }
                }

                break;
        }
        return true;
    }

    private boolean isShaked = false;

    private void ShakeAnim(final View view, float changeDegree) {
        final RotateAnimation rotate_start = new RotateAnimation(0, changeDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
        rotate_start.setDuration(100);
        rotate_start.setFillAfter(true);

        final RotateAnimation rotate_mid = new RotateAnimation(changeDegree, -changeDegree,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
        rotate_mid.setDuration(200);
        rotate_mid.setFillAfter(true);

        final RotateAnimation rotate_end = new RotateAnimation(-changeDegree, 0,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0);
        rotate_end.setDuration(100);
        rotate_end.setFillAfter(true);

        rotate_start.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShaked) {
//                    rotate_start.reset();
//                    view.startAnimation(rotate_mid);
                    animation.reset();
                    view.startAnimation(rotate_mid);
                }

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rotate_mid.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShaked) {
//                    rotate_mid.reset();
//                    view.startAnimation(rotate_end);
                    animation.reset();
                    view.startAnimation(rotate_end);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        rotate_end.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (isShaked) {
//                    rotate_end.reset();
//                    view.startAnimation(rotate_start);
                    animation.reset();
                    view.startAnimation(rotate_start);
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        view.startAnimation(rotate_start);
    }

    /**
     * 从左上角开始对比移动
     * TranslateAnimation.ABSOLUTE 屏幕的绝对位置
     * TranslateAnimation.RELATIVE_TO_PARENT 相对父类
     * TranslateAnimation.RELATIVE_TO_SELF 相对自身
     */
    private void translate(final View view, Point prePoint, Point nowPoint, final boolean swapChange) {
        //起始的相对位置都是0，相对移动了多少是差值
        TranslateAnimation anim_trans = new TranslateAnimation(
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, nowPoint.x - prePoint.x,
                TranslateAnimation.ABSOLUTE, 0,
                TranslateAnimation.ABSOLUTE, nowPoint.y - prePoint.y);
        anim_trans.setDuration(500);
        anim_trans.setFillAfter(true);
        anim_trans.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                isShaked = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {

                if (swapChange) {
                    longTapEnable = false;
                    //交换数据
                    Collections.swap(viewListPoint, swapOne, swapTwo);
                    requestLayout();
                    invalidate();

                    //更新交换后的数据到dao
                    //这个交换的仅仅是在列表中的位置，不管用，我实际想要的是内部数据改变更新到数据库
//                    Collections.swap(appInfos, appInfos.get(swapOne).getOrderNum(), appInfos.get(swapTwo).getOrderNum());

                    int tempOderNum = appInfos.get(swapTwo).getOrderNum();
                    appInfos.get(swapTwo).setOrderNum(appInfos.get(swapOne).getOrderNum());
                    appInfos.get(swapOne).setOrderNum(tempOderNum);

                    AppInfoDao appInfoDao = MyApplication.getInstance().getDaoSession().getAppInfoDao();
                    /**
                     * 插入和更新不行，总是插入的原因是：我的主键是自增的，
                     * 它依据主键判断是否是已存在的还是待新增的
                     * */
                    appInfoDao.insertOrReplaceInTx(appInfos.get(swapOne), appInfos.get(swapTwo));
                }

                //清除动画属性
                view.clearAnimation();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        view.startAnimation(anim_trans);
    }
}
