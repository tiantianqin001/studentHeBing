package com.telit.zhkt_three.CustomView.interactive.CCYX;

import android.content.Context;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/8/6 14:18
 *
 <com.telit.zhkt_three.CustomView.interactive.CCYX.GuessGameView
 android:background="#5500ff00"
 android:id="@+id/guessGameView"
 android:layout_width="@dimen/x1065"
 android:layout_height="@dimen/x750"></com.telit.zhkt_three.CustomView.interactive.CCYX.GuessGameView>

 private String disturbItems = "赵,钱,孙,李,七,吴,郑,王,赵,钱,孙,李,周,吴,郑,王,子,钱,孙,李,周,吴,郑,王,赵,笑,孙,李,周,吴,郑,王";
 private String guessWords = "七,子,笑";

 private String engDisturbItems = "a,o,e,i,q,y,b,p,a,o,e,i,w,y,b,p,z,o,e,i,w,y,b,p,a,o,e,i,w,x,b,p";
 private String engGuessWords = "q,z,x,a,b";

 GuessGameView guessGameView = findViewById(R.id.guessGameView);

 guessGameView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
 @Override
 public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
 QZXTools.logE("onLayoutChange", null);

 guessGameView.addWordView(false, engGuessWords);
 guessGameView.addDisturbItems(8, engDisturbItems, R.mipmap.keyboard, R.mipmap.disturb_keyboard);

 guessGameView.removeOnLayoutChangeListener(this);
 }
 });

 guessGameView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
 @Override
 public void onGlobalLayout() {
 QZXTools.logE("OnGlobalLayoutListener", null);
 guessGameView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
 }
 });
 */
public class GuessGameView extends ViewGroup implements View.OnClickListener {

    private Context mContext;

    /**
     * 非真实的屏幕宽度
     */
    private int screenWidth;
    /**
     * 非真实的屏幕高度
     */
    private int screenHeight;

    public GuessGameView(Context context) {
        this(context, null);
    }

    public GuessGameView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public GuessGameView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        mContext = context;

        chineseInterval = getResources().getDimensionPixelOffset(R.dimen.x30);
        disturbInterval = getResources().getDimensionPixelOffset(R.dimen.x24);
        disturbRowInterval = getResources().getDimensionPixelOffset(R.dimen.x12);

        answerList = new ArrayList<>();
        disturbList = new ArrayList<>();
        guessView = new ArrayList<>();
        rectList = new ArrayList<>();

        //宽度和高度一开始设置为屏幕宽高
        screenWidth = getResources().getDisplayMetrics().widthPixels;
        screenHeight = getResources().getDisplayMetrics().heightPixels;
    }

    //-----------------------------------------------------
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
        QZXTools.logE("onLayout measureWidth=" + getMeasuredWidth() + ";measureHeight=" + getMeasuredHeight(), null);

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
    //-----------------------------------------------------

    /**
     * 是否是中文
     */
    private boolean isChinese;

    /**
     * 猜词的标准答案：例如 七子笑/qzx
     * 用，分割
     */
    private String standardAnswers;

    private List<String> answerList;

    /**
     * 干扰的行数和列数
     */
    private int disturbRow;
    private int disturbColumn = 8;

    /**
     * 干扰的字符串
     * 用，分割
     */
    private String disturbItems;

    private List<String> disturbList;

//    /**
//     * 填充所需数据
//     */
//    public void fillData(boolean isChinese, String standardAnswers, int disturbRow, int disturbColumn, String disturbItems) {
//        this.isChinese = isChinese;
//        this.standardAnswers = standardAnswers;
//        this.disturbRow = disturbRow;
//        this.disturbColumn = disturbColumn;
//        this.disturbItems = disturbItems;
//    }

    private boolean isFirstMeasure = true;
    private int chineseInterval;

    /**
     * 要猜的词的集合
     */
    private List<View> guessView;
    /**
     * 猜词的位置集合
     */
    private List<Rect> rectList;

    /**
     * 添加猜词
     * 中文或者英文
     * 个数
     * 正确答案
     */
    public void addWordView(boolean isChinese, String standardAnswers) {
        if (TextUtils.isEmpty(standardAnswers)) {
            return;
        }

        //英文的逗号
        if (standardAnswers.contains(",")) {
            String[] splitAnswers = standardAnswers.split(",");
            for (int i = 0; i < splitAnswers.length; i++) {
                answerList.add(splitAnswers[i]);
            }
        } else {
            answerList.add(standardAnswers);


        }

        int itemWidth = 0;
        int itemHeight = 0;

        //添加猜词
        for (int i = 0; i < answerList.size(); i++) {
            if (isChinese) {
                MIWordView miWordView = new MIWordView(getContext());
//                miWordView.setText(answerList.get(i));
                if (isFirstMeasure) {
                    isFirstMeasure = false;
                    miWordView.measure(0, 0);
                    itemWidth = miWordView.getMeasuredWidth();
                    itemHeight = miWordView.getMeasuredHeight();
                }
                MarginLayoutParams layoutParams = new MarginLayoutParams
                        (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                layoutParams.leftMargin = screenWidth / answerList.size() * i + (screenWidth / answerList.size() - itemWidth) / 2;
                layoutParams.topMargin = (screenHeight / 3 - itemHeight) / 2;

                Rect rect = new Rect(layoutParams.leftMargin, layoutParams.topMargin,
                        layoutParams.leftMargin + itemWidth, layoutParams.topMargin + itemHeight);
                rectList.add(rect);

                guessView.add(miWordView);

                this.addView(miWordView, layoutParams);
            } else {
                FourLineWordView fourLineWordView = new FourLineWordView(getContext());
//                fourLineWordView.setText(answerList.get(i));
                if (isFirstMeasure) {
                    isFirstMeasure = false;
                    fourLineWordView.measure(0, 0);
                    itemWidth = fourLineWordView.getMeasuredWidth();
                    itemHeight = fourLineWordView.getMeasuredHeight();
                }
                MarginLayoutParams layoutParams = new MarginLayoutParams
                        (LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
                if (answerList.size() % 2 == 0) {
                    //偶数
                    if (i < answerList.size() / 2) {
                        layoutParams.leftMargin = screenWidth / 2 - itemWidth * (answerList.size() / 2 - i);
                    } else {
                        layoutParams.leftMargin = screenWidth / 2 + itemWidth * (i - answerList.size() / 2);
                    }
                } else {
                    //奇数
                    if (i < answerList.size() / 2) {
                        layoutParams.leftMargin = screenWidth / 2 - itemWidth * (answerList.size() / 2 - i) - itemWidth / 2;
                    } else {
                        layoutParams.leftMargin = screenWidth / 2 + itemWidth * (i - 1 - answerList.size() / 2) + itemWidth / 2;
                    }
                }
//                layoutParams.leftMargin = screenWidth / answerList.size() * i + (screenWidth / answerList.size() - itemWidth) / 2;
                layoutParams.topMargin = (screenHeight / 3 - itemHeight) / 2;

                guessView.add(fourLineWordView);

                Rect rect = new Rect(layoutParams.leftMargin, layoutParams.topMargin,
                        layoutParams.leftMargin + itemWidth, layoutParams.topMargin + itemHeight);
                rectList.add(rect);

                this.addView(fourLineWordView, layoutParams);
            }
        }
    }

    private int disturbInterval;
    private int disturbRowInterval;

    /**
     * 添加干扰词汇
     * 几行几列
     * 干扰词汇
     *
     * @param resBgId   干扰词大背景图
     * @param resItemId 干扰词的背景
     */
    public void addDisturbItems(int disturbColumn, String disturbItems, int resBgId, int resItemId) {
        if (TextUtils.isEmpty(disturbItems)) {
            return;
        }
        String[] disturbSplit = disturbItems.split(",");
        for (int i = 0; i < disturbSplit.length; i++) {
            disturbList.add(disturbSplit[i]);
        }

        //添加干扰词大背景
        if (resBgId != -1) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(resBgId);
            MarginLayoutParams layoutParams = new MarginLayoutParams
                    (LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            layoutParams.leftMargin = 0;
            layoutParams.topMargin = screenHeight / 3;
            this.addView(imageView, layoutParams);
        }

        //正方形的块
        int disturbWidth = (screenWidth - (disturbColumn + 1) * disturbInterval) / disturbColumn;

        //添加干扰词
        for (int j = 0; j < disturbList.size(); j++) {
            TextView textView = new TextView(getContext());
            textView.setOnClickListener(this);
            textView.setGravity(Gravity.CENTER);
            textView.setTextSize(getResources().getDimensionPixelSize(R.dimen.x36));
            textView.setText(disturbList.get(j));
            textView.setBackground(getResources().getDrawable(resItemId));
            MarginLayoutParams layoutParams = new MarginLayoutParams(disturbWidth, disturbWidth);
            layoutParams.leftMargin = disturbInterval * (j % disturbColumn + 1) + disturbWidth * (j % disturbColumn);
            layoutParams.topMargin = screenHeight / 3 + disturbInterval + (j / disturbColumn) * (disturbWidth + disturbRowInterval);
            this.addView(textView, layoutParams);
        }
    }

    @Override
    public void onClick(View v) {
        if (v instanceof TextView) {
            QZXTools.logE("width=" + v.getWidth() + ";left=" + v.getLeft() + ";right=" + v.getRight()
                    + ";top=" + v.getTop() + ";rect left=" + rectList.get(0).left + ";rect top=" + rectList.get(0).top, null);

            TranslateAnimation translateAnimation = new TranslateAnimation(
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, rectList.get(0).left - v.getLeft(),
                    Animation.ABSOLUTE, 0, Animation.ABSOLUTE, rectList.get(0).top - v.getTop());
            translateAnimation.setDuration(1000);
            translateAnimation.setFillAfter(false);
            v.startAnimation(translateAnimation);

            ((FourLineWordView) guessView.get(0)).setText((String) ((TextView) v).getText());
        }
    }

    /**
     * 点击选项的字母移动的动画
     */
    private void translateAnim() {


    }
}
