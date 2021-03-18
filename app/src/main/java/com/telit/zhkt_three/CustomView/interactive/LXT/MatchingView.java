package com.telit.zhkt_three.CustomView.interactive.LXT;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/7/16 8:55
 * <p>
 * 1、有多少组（上限）
 * 2、左边和右边数据
 * 3、不能设置具体的值，展示要铺满剩余的整个屏幕
 * 4、连线完毕后自动批阅对错
 */
public class MatchingView extends View {
    /**
     * 允许的最大组数
     */
    private int maxGroupCount = 4;

    /**
     * 多少对（组）
     */
    private int groupCount;

    /**
     * 左侧连线题数据集
     */
    private List<LineBean> leftLineBeans;

    /**
     * 右侧连线题数据集
     */
    private List<LineBean> rightLineBeans;

    /**
     * 占据的上部空间(固定)
     */
    private int occupySpace;

    /**
     * 浮动变化的空间
     */
    private int floatSpace;

    /**
     * 连线题展示的高度空间
     */
    private int displayHeightSpace;

    /**
     * 连线题展示的宽度空间
     */
    private int displayWidthSpace;

    /**
     * 连线题一题的图片背景
     */
    private int img_bg_width;
    private int img_bg_height;

    /**
     * 连线题划线的距离
     */
    private int matchingDistance;

    /**
     * 距离左边的间距
     */
    private int marginLeftDistance;

    /**
     * 划线的颜色
     */
    private int drawColor;
    private int rightColor;
    private int wrongColor;

    /**
     * 文字颜色
     */
    private int wordColor;

    /**
     * 连线题图片位图
     */
    private Bitmap bitmap;

    /**
     * 间隔，每一道连线题间
     */
    private int lineInterval;

    /**
     * 图片的文字放置留白高度
     */
    private int wordSpaceHeight;

    /**
     * 画笔的粗度
     */
    private int strokeWidth;

    /**
     * 连接点的半径
     */
    private int circleDotRadius;

    /**
     * 连线的总数
     */
    private int totalLineLinkCount;

    /**
     * 设置初始化的数据
     *
     * @param leftLineBeans  左边的连线题数据
     * @param rightLineBeans 右边的连线题数据
     * @param wordColor      文字颜色 0xFF6BAA1B
     * @param resId          图片背景
     */
    public void setInitData(int groupCount, List<LineBean> leftLineBeans, List<LineBean> rightLineBeans, int wordColor, int resId) {
        this.leftLineBeans = leftLineBeans;
        this.rightLineBeans = rightLineBeans;
        this.wordColor = wordColor;

        totalLineLinkCount = leftLineBeans.size();

        bitmap = BitmapFactory.decodeResource(getResources(), resId);

        this.groupCount = groupCount;

        setRightAnswer();

        QZXTools.logE("bm width=" + bitmap.getWidth()
                + ";bm height=" + bitmap.getHeight()
                + ";groupCount=" + groupCount, null);

        invalidate();
    }

    /**
     * 画笔
     */
    private Paint mPaint;

    /**
     * 测量文本的矩形
     */
    private Rect rect;

    /**
     * 自己的回答
     */
    private String ownAnswers;

    /**
     * 正确答案
     */
    private String rightAnswer;

    private void setRightAnswer() {
        for (int i = 0; i < leftLineBeans.size(); i++) {
            StringBuilder stringBuilder = new StringBuilder();
            if (!TextUtils.isEmpty(rightAnswer)) {
                stringBuilder.append(rightAnswer);
                stringBuilder.append("|");
            }
            stringBuilder.append(leftLineBeans.get(i).getId());
            stringBuilder.append(",");
            stringBuilder.append(rightLineBeans.get(i).getId());
            rightAnswer = stringBuilder.toString().trim();
        }
    }

    /**
     * 是否显示答案
     */
    private boolean isShowAnswer = false;

    /**
     * 显示答案
     */
    public void showAnswer() {
        isShowAnswer = true;
        invalidate();
    }

    /**
     * 绘制图片的真实矩形大小
     */
    private RectF rectF;

    /**
     * 重置视图
     */
    public void resetView() {
        isShowAnswer = false;
        //清空我的答案
        ownAnswers = "";
        lineCount = 0;
        //清除配对标志
        for (LineBean lineBean : leftLineBeans) {
            lineBean.setHasMatching(false);
        }

        for (LineBean lineBean : rightLineBeans) {
            lineBean.setHasMatching(false);
        }
        invalidate();
    }

    public MatchingView(Context context) {
        this(context, null);
    }

    public MatchingView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchingView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        drawColor = 0xFFFFFFFF;
        rightColor = 0xFF318622;
        wrongColor = 0xFFF39800;

        rectF = new RectF();

        img_bg_width = getResources().getDimensionPixelSize(R.dimen.x492);
        img_bg_height = getResources().getDimensionPixelSize(R.dimen.x162);
        matchingDistance = getResources().getDimensionPixelSize(R.dimen.x285);
        occupySpace = getResources().getDimensionPixelSize(R.dimen.x156);
        lineInterval = getResources().getDimensionPixelSize(R.dimen.x6);
        wordSpaceHeight = getResources().getDimensionPixelSize(R.dimen.x102);
        strokeWidth = getResources().getDimensionPixelSize(R.dimen.x3);
        circleDotRadius = getResources().getDimensionPixelSize(R.dimen.x9);

        QZXTools.logE("imgWidth=" + img_bg_width + ";imgHeight=" + img_bg_height
                + ";matchingDistance=" + matchingDistance
                + ";occupySpace=" + occupySpace
                + ";lineInterval=" + lineInterval
                + ";wordSpaceHeight=" + wordSpaceHeight, null);

        rect = new Rect();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setFilterBitmap(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeJoin(Paint.Join.ROUND);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.getSize(widthMeasureSpec);
        int heightSpec = MeasureSpec.getSize(heightMeasureSpec);

        displayWidthSpace = widthSpec;
        displayHeightSpace = heightSpec - occupySpace;
        marginLeftDistance = (displayWidthSpace - img_bg_width * 2 - matchingDistance) / 2;
        floatSpace = (displayHeightSpace - (img_bg_height + lineInterval) * groupCount) / 2;

        QZXTools.logE("displayWidthSpace=" + displayWidthSpace
                + ";displayHeightSpace=" + displayHeightSpace
                + ";marginLeftDistance=" + marginLeftDistance
                + "floatSpace=" + floatSpace, null);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (groupCount > maxGroupCount) {
            Toast.makeText(getContext(), "超过最大组数了耶！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (leftLineBeans != null && leftLineBeans.size() > 0 && rightLineBeans != null && rightLineBeans.size() > 0) {
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setColor(drawColor);
            //显示我的答案
            if (!TextUtils.isEmpty(ownAnswers)) {
                if (ownAnswers.contains("|")) {
                    String[] splitStr = ownAnswers.split("\\|");
                    for (String split : splitStr) {
                        linkOwnAnswer(canvas, split);
                    }
                } else {
                    //只有一个
                    linkOwnAnswer(canvas, ownAnswers);
                }
            }

            //显示正确答案
            if (isShowAnswer) {
                mPaint.setColor(rightColor);
                if (!TextUtils.isEmpty(rightAnswer)) {
                    if (rightAnswer.contains("|")) {
                        String[] splitStr = rightAnswer.split("\\|");
                        for (String split : splitStr) {
                            linkOwnAnswer(canvas, split);
                        }
                    } else {
                        //只有一个
                        linkOwnAnswer(canvas, rightAnswer);
                    }
                }
            }

            if (lineCount >= totalLineLinkCount) {
                //连线全部完成，需要展示结果
                mPaint.setColor(wrongColor);
                //我的答案用错误表示
                if (!TextUtils.isEmpty(ownAnswers)) {
                    if (ownAnswers.contains("|")) {
                        String[] splitStr = ownAnswers.split("\\|");
                        for (String split : splitStr) {
                            linkOwnAnswer(canvas, split);
                        }
                    } else {
                        //只有一个
                        linkOwnAnswer(canvas, ownAnswers);
                    }
                }

                //再画正确答案
                mPaint.setColor(rightColor);
                if (!TextUtils.isEmpty(rightAnswer)) {
                    if (rightAnswer.contains("|")) {
                        String[] splitStr = rightAnswer.split("\\|");
                        for (String split : splitStr) {
                            linkOwnAnswer(canvas, split);
                        }
                    } else {
                        //只有一个
                        linkOwnAnswer(canvas, rightAnswer);
                    }
                }
            }

            //连线绘制
            if (fromLeftToRight) {
                //从左到右
                mPaint.setColor(drawColor);
                Rect leftRect = leftLineBeans.get(lineStartIndex).getRangeRect();
                canvas.drawLine(leftRect.right, leftRect.top + img_bg_height / 2, moveX, moveY, mPaint);

                //显示右侧的连接点
                if (showNodeDotIndex != -1) {
                    Rect rangeRect = rightLineBeans.get(showNodeDotIndex).getRangeRect();
                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(rangeRect.left - circleDotRadius,
                            rangeRect.top + img_bg_height / 2 - circleDotRadius, circleDotRadius, mPaint);
                }
            } else if (fromRightToLeft) {
                //从右到左
                mPaint.setColor(drawColor);
                Rect rightRect = rightLineBeans.get(lineStartIndex).getRangeRect();
                canvas.drawLine(rightRect.left, rightRect.top + img_bg_height / 2, moveX, moveY, mPaint);

                //显示左侧的连接点
                if (showNodeDotIndex != -1) {
                    Rect rangeRect = leftLineBeans.get(showNodeDotIndex).getRangeRect();
                    mPaint.setStyle(Paint.Style.FILL);
                    canvas.drawCircle(rangeRect.right + circleDotRadius,
                            rangeRect.top + img_bg_height / 2 + circleDotRadius, circleDotRadius, mPaint);
                }
            }

            mPaint.reset();
            mPaint.setAntiAlias(true);
            mPaint.setFilterBitmap(true);
            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setStrokeCap(Paint.Cap.ROUND);
            mPaint.setStrokeJoin(Paint.Join.ROUND);

            mPaint.setStyle(Paint.Style.STROKE);
            mPaint.setColor(wordColor);
            mPaint.setTextSize(QZXTools.sp2px(getContext(), 12));

            //左侧数据
            for (int i = 0; i < leftLineBeans.size(); i++) {
                canvas.save();
                canvas.translate(marginLeftDistance, occupySpace + floatSpace + (img_bg_height + lineInterval) * i);

                rectF.set(0, 0, img_bg_width, img_bg_height);
//                canvas.drawBitmap(bitmap, 0, 0, mPaint);
                canvas.drawBitmap(bitmap, null, rectF, mPaint);

                //wordText的长度
                String wordText = leftLineBeans.get(i).getContent();
                float textWidth = mPaint.measureText(wordText, 0, wordText.length());
                mPaint.getTextBounds(wordText, 0, wordText.length(), rect);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                canvas.drawText(wordText, (img_bg_width - textWidth) / 2,
                        img_bg_height - wordSpaceHeight / 2, mPaint);

                //设置界面的范围
                if (leftLineBeans.get(i).getRangeRect() == null) {
                    Rect rect_left = new Rect();
                    rect_left.set(marginLeftDistance, occupySpace + floatSpace + (img_bg_height + lineInterval) * i,
                            marginLeftDistance + img_bg_width,
                            occupySpace + floatSpace + (img_bg_height + lineInterval) * i + img_bg_height);
                    QZXTools.logE("left rect=" + rect_left, null);
                    leftLineBeans.get(i).setRangeRect(rect_left);
                }

                canvas.restore();
            }

            //右侧数据
            for (int j = 0; j < rightLineBeans.size(); j++) {
                canvas.save();
                canvas.translate(marginLeftDistance + img_bg_width + matchingDistance,
                        occupySpace + floatSpace + (img_bg_height + lineInterval) * j);

                rectF.set(0, 0, img_bg_width, img_bg_height);
//                canvas.drawBitmap(bitmap, 0, 0, mPaint);
                canvas.drawBitmap(bitmap, null, rectF, mPaint);

                //wordText的长度
                String wordText = rightLineBeans.get(j).getContent();
                float textWidth = mPaint.measureText(wordText, 0, wordText.length());
                mPaint.getTextBounds(wordText, 0, wordText.length(), rect);
                Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
                canvas.drawText(wordText, (img_bg_width - textWidth) / 2,
                        img_bg_height - wordSpaceHeight / 2, mPaint);

                //设置界面的范围
                if (rightLineBeans.get(j).getRangeRect() == null) {
                    Rect rect_right = new Rect();
                    rect_right.set(marginLeftDistance + img_bg_width + matchingDistance,
                            occupySpace + floatSpace + (img_bg_height + lineInterval) * j,
                            marginLeftDistance + img_bg_width + matchingDistance + img_bg_width,
                            occupySpace + floatSpace + (img_bg_height + lineInterval) * j + img_bg_height);
                    QZXTools.logE("right rect=" + rect_right, null);
                    rightLineBeans.get(j).setRangeRect(rect_right);
                }

                canvas.restore();
            }
        }
    }

    /**
     * 连线我的回答
     */
    private void linkOwnAnswer(Canvas canvas, String string) {
        String[] left_right = string.split(",");
        for (int i = 0; i < leftLineBeans.size(); i++) {
            for (int j = 0; j < rightLineBeans.size(); j++) {
                //比较id
                if (left_right[0].equals(leftLineBeans.get(i).getId() + "")
                        && left_right[1].equals(rightLineBeans.get(j).getId() + "")) {

                    //设置配对标志
                    leftLineBeans.get(i).setHasMatching(true);
                    rightLineBeans.get(j).setHasMatching(true);

                    Rect leftRect = leftLineBeans.get(i).getRangeRect();
                    Rect rightRect = rightLineBeans.get(j).getRangeRect();
                    canvas.drawLine(leftRect.right, leftRect.top + img_bg_height / 2,
                            rightRect.left, rightRect.top + img_bg_height / 2, mPaint);
                    break;
                }
            }
        }
    }

    private boolean fromLeftToRight = false;
    private boolean fromRightToLeft = false;

    private int showNodeDotIndex = -1;
    private int lineStartIndex = -1;

    private float moveX;
    private float moveY;

    /**
     * 已经连线的个数
     */
    private int lineCount;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                float downX = event.getX();
                float downY = event.getY();
//                QZXTools.logE("downX=" + downX + ";downY=" + downY, null);
                for (int i = 0; i < leftLineBeans.size(); i++) {
//                    QZXTools.logE("left rect=" + leftLineBeans.get(i).getRangeRect(), null);
                    if (!leftLineBeans.get(i).isHasMatching() && leftLineBeans.get(i).getRangeRect().contains((int) downX, (int) downY)) {
                        fromLeftToRight = true;
                        fromRightToLeft = false;
                        lineStartIndex = i;
                        return true;
                    } else {
                        lineStartIndex = -1;
                    }
                }
                for (int i = 0; i < rightLineBeans.size(); i++) {
//                    QZXTools.logE("right rect=" + rightLineBeans.get(i).getRangeRect(), null);
                    if (!rightLineBeans.get(i).isHasMatching() && rightLineBeans.get(i).getRangeRect().contains((int) downX, (int) downY)) {
                        fromRightToLeft = true;
                        fromLeftToRight = false;
                        lineStartIndex = i;
                        return true;
                    } else {
                        lineStartIndex = -1;
                    }
                }
//                QZXTools.logE("fromRightToLeft=" + fromRightToLeft + ";fromLeftToRight=" + fromLeftToRight, null);
                break;
            case MotionEvent.ACTION_MOVE:
                moveX = event.getX();
                moveY = event.getY();
//                QZXTools.logE("moveX=" + moveX + ";moveY=" + moveY, null);
                if (fromLeftToRight) {
                    //从左到右连线
                    for (int i = 0; i < rightLineBeans.size(); i++) {
                        if (!rightLineBeans.get(i).isHasMatching() && rightLineBeans.get(i).getRangeRect().contains((int) moveX, (int) moveY)) {
                            showNodeDotIndex = i;
                            break;
                        } else {
                            showNodeDotIndex = -1;
                        }
                    }
                    //更新界面
                    invalidate();
                } else if (fromRightToLeft) {
                    //从右到左连线
                    for (int i = 0; i < leftLineBeans.size(); i++) {
                        if (!leftLineBeans.get(i).isHasMatching() && leftLineBeans.get(i).getRangeRect().contains((int) moveX, (int) moveY)) {
                            showNodeDotIndex = i;
                            break;
                        } else {
                            showNodeDotIndex = -1;
                        }
                    }
                    //更新界面
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                float upX = event.getX();
                float upY = event.getY();

                if (fromLeftToRight) {
                    //判断右边
                    for (int i = 0; i < rightLineBeans.size(); i++) {
                        if (!rightLineBeans.get(i).isHasMatching() && rightLineBeans.get(i).getRangeRect().contains((int) upX, (int) upY)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            if (!TextUtils.isEmpty(ownAnswers)) {
                                stringBuilder.append(ownAnswers);
                                stringBuilder.append("|");
                            }
                            stringBuilder.append(leftLineBeans.get(lineStartIndex).getId());
                            stringBuilder.append(",");
                            stringBuilder.append(rightLineBeans.get(i).getId());
                            ownAnswers = stringBuilder.toString().trim();
                            lineCount++;
                            break;
                        }
                    }
                } else if (fromRightToLeft) {
                    for (int i = 0; i < leftLineBeans.size(); i++) {
                        if (!leftLineBeans.get(i).isHasMatching() && leftLineBeans.get(i).getRangeRect().contains((int) upX, (int) upY)) {
                            StringBuilder stringBuilder = new StringBuilder();
                            if (!TextUtils.isEmpty(ownAnswers)) {
                                stringBuilder.append(ownAnswers);
                                stringBuilder.append("|");
                            }
                            stringBuilder.append(leftLineBeans.get(i).getId());
                            stringBuilder.append(",");
                            stringBuilder.append(rightLineBeans.get(lineStartIndex).getId());
                            ownAnswers = stringBuilder.toString().trim();
                            lineCount++;
                            break;
                        }
                    }
                }
                fromRightToLeft = false;
                fromLeftToRight = false;
                showNodeDotIndex = -1;
                lineStartIndex = -1;

                invalidate();
                break;
            case MotionEvent.ACTION_CANCEL:
                fromRightToLeft = false;
                fromLeftToRight = false;
                showNodeDotIndex = -1;
                lineStartIndex = -1;
                invalidate();
                break;
        }
        return true;
    }
}
