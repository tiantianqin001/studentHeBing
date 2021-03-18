package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.TimeTableInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: qzx
 * Date: 2019/4/16 9:15
 * <p>
 * 一、绘制星期头
 * 二、绘制上午、下午、晚上
 * 三、绘制节次和时间线
 * 四、绘制课表主体
 */
public class TimeTableView extends View {

    private int singleWidth;
    private int singleHeight;

    private Paint mPaint;

    private int weekCount;
    private int sectionCount;
    private int morningCount;
    private int afternoonCount;
    private int nightCount;

    //----------------------------------
    private int fixedDistance;
    private int fixedSectionDistance;
    private int firstDistance;
    private int intervalDistance;

    private int sectionWidth;
    private int sectionHeight;

    private int NormalSize;
    private int SmallSize;

    private Rect rect_date;

    /**
     * 从外界获取课程表信息
     */
    private TimeTableInfo timeTableInfo;

    public void setTimeTableInfo(TimeTableInfo timeTableInfo) {
        this.timeTableInfo = timeTableInfo;
    }

    public TimeTableView(Context context) {
        this(context, null);
    }

    public TimeTableView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TimeTableView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        int widthScreen = context.getResources().getDisplayMetrics().widthPixels;
//        int heightScreen = context.getResources().getDisplayMetrics().heightPixels;
//        QZXTools.logE("ScreenWidth=" + widthScreen + ";ScreenHeight=" + heightScreen, null);

        fixedDistance = getResources().getDimensionPixelSize(R.dimen.y345);
        fixedSectionDistance = getResources().getDimensionPixelSize(R.dimen.y168);
        sectionWidth = getResources().getDimensionPixelSize(R.dimen.y177);
        sectionHeight = getResources().getDimensionPixelSize(R.dimen.x108);
        firstDistance = getResources().getDimensionPixelSize(R.dimen.y84);
        intervalDistance = getResources().getDimensionPixelSize(R.dimen.x48);

        NormalSize = getResources().getDimensionPixelSize(R.dimen.x36);
        SmallSize = getResources().getDimensionPixelSize(R.dimen.x24);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);

        //测量文本的矩形
        rect_date = new Rect();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //这样写的原因是并非全屏处理的
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        QZXTools.logE("widthSize=" + widthSize + ";heightSize=" + heightSize, null);

        if (timeTableInfo != null && timeTableInfo.getWeekInfoList().size() > 0) {
            weekCount = timeTableInfo.getWeekInfoList().size();
            sectionCount = timeTableInfo.getTotal();
            morningCount = timeTableInfo.getMorningCount();
            afternoonCount = timeTableInfo.getAfternoonCount();
            nightCount = timeTableInfo.getNightCount();

            //注意为什么这样处理单个singleWidth以及singleHeight
            singleWidth = (widthSize - fixedDistance) / weekCount;
            singleHeight = (heightSize - intervalDistance * 2) / (sectionCount + 1);
        }

        QZXTools.logE("singleWidth=" + singleWidth + ";singleHeight=" + singleHeight, null);

        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (timeTableInfo == null || timeTableInfo.getWeekInfoList().size() <= 0) {
            return;
        }

        mPaint.setColor(Color.WHITE);
        //------------------------绘制星期头
        for (int i = 0; i < weekCount; i++) {
            canvas.save();
            //绘制星期一
            canvas.translate(fixedDistance + singleWidth * i, 0);
            String weekStr = null;
            switch (i) {
                case 0:
                    weekStr = "星期一";
                    break;
                case 1:
                    weekStr = "星期二";
                    break;
                case 2:
                    weekStr = "星期三";
                    break;
                case 3:
                    weekStr = "星期四";
                    break;
                case 4:
                    weekStr = "星期五";
                    break;
                case 5:
                    weekStr = "星期六";
                    break;
                case 6:
                    weekStr = "星期日";
                    break;
            }
            drawText(weekStr, canvas, 1, 1, NormalSize);
            canvas.restore();
        }

        //--------------------------上午下午和晚上
        if (morningCount > 0) {
            canvas.save();
            canvas.translate(firstDistance, singleHeight);

            drawDayName(canvas, "上", "午", morningCount);

            canvas.restore();
        }

        if (afternoonCount > 0) {
            canvas.save();
            //这个要注意下
            canvas.translate(firstDistance, singleHeight * (morningCount + 1) + intervalDistance);

            drawDayName(canvas, "下", "午", afternoonCount);

            canvas.restore();
        }

        if (nightCount > 0) {
            canvas.save();
            canvas.translate(firstDistance, singleHeight * (morningCount + afternoonCount + 1) + intervalDistance * 2);

            drawDayName(canvas, "晚", "上", nightCount);

            canvas.restore();
        }

        //--------------------------节次
        for (int i = 0; i < sectionCount; i++) {
            if (i < timeTableInfo.getWeekInfoList().get(0).getSubjectList().size()) {
                canvas.save();
                if (i < morningCount) {
                    canvas.translate(fixedSectionDistance,
                            singleHeight * (i + 1) + (singleHeight - sectionHeight) / 2);
                } else if (i < (morningCount + afternoonCount)) {
                    canvas.translate(fixedSectionDistance,
                            singleHeight * (i + 1) + intervalDistance + (singleHeight - sectionHeight) / 2);
                } else {
                    canvas.translate(fixedSectionDistance,
                            singleHeight * (i + 1) + intervalDistance * 2 + (singleHeight - sectionHeight) / 2);
                }
                //背景图片
                drawImgBg(canvas, i);

                drawCenterText(canvas, "第" + (i + 1) + "节", NormalSize, false, 0);

                String startTime = timeTableInfo.getWeekInfoList().get(0).getSubjectList().get(i).getStartTime();
                String endTime = timeTableInfo.getWeekInfoList().get(0).getSubjectList().get(i).getEndTime();
                String secondWord = startTime + "～" + endTime;
                drawCenterText(canvas, secondWord, SmallSize, true, 0);

                canvas.restore();
            }
        }
        //--------------------------课程
        for (int j = 0; j < weekCount; j++) {
            if (j < timeTableInfo.getWeekInfoList().size()) {
                for (int k = 0; k < sectionCount; k++) {
                    if (k < timeTableInfo.getWeekInfoList().get(j).getSubjectList().size()) {
                        canvas.save();

                        String subjectName = timeTableInfo.getWeekInfoList().get(j).getSubjectList().get(k).getSubjectName();

                        float textWidth = mPaint.measureText(subjectName);

                        if (k < morningCount) {
                            canvas.translate(fixedDistance + singleWidth * j, singleHeight * (k + 1));
                        } else if (k < (morningCount + afternoonCount)) {
                            canvas.translate(fixedDistance + singleWidth * j, singleHeight * (k + 1)
                                    + intervalDistance);
                        } else {
                            canvas.translate(fixedDistance + singleWidth * j, singleHeight * (k + 1)
                                    + intervalDistance * 2);
                        }

                        drawText(subjectName, canvas, 1, 1, NormalSize);

                        canvas.restore();
                    }
                }
            }
        }

        //------------------画分割横线：上午和下午、下午和晚上
        mPaint.setColor(0xff162A76);
        String text = "星期一";
        float textWidth = mPaint.measureText(text);
        canvas.save();
        canvas.translate(fixedDistance + (singleWidth - textWidth) / 2, singleHeight * (morningCount + 1) + intervalDistance / 2);
        RectF rectF = new RectF(0, 0, singleWidth * weekCount - (singleWidth - textWidth), 2);
        canvas.drawRect(rectF, mPaint);
        canvas.restore();

        if (nightCount > 0) {
            canvas.save();
            canvas.translate(fixedDistance + (singleWidth - textWidth) / 2,
                    singleHeight * (morningCount + afternoonCount + 1) + intervalDistance + intervalDistance / 2);
            canvas.drawRect(rectF, mPaint);
            canvas.restore();
        }
    }

    /**
     * 上午、下午、晚上，分开模式的上下对齐排列
     */
    private void drawDayName(Canvas canvas, String firstWord, String secondWord, int spanCount) {
        mPaint.setTextSize(NormalSize);
        //字体尺寸
        Paint.FontMetrics fontMetrics_one = mPaint.getFontMetrics();
        //测量文本的矩形
        mPaint.getTextBounds(firstWord, 0, firstWord.length(), rect_date);

        canvas.drawText(firstWord, 0, spanCount * singleHeight / 2
                + (fontMetrics_one.bottom - fontMetrics_one.top) / 2 - (rect_date.bottom - rect_date.top), mPaint);

        mPaint.setTextSize(NormalSize);
        //字体尺寸
        Paint.FontMetrics fontMetrics_two = mPaint.getFontMetrics();
        //测量文本的矩形
        mPaint.getTextBounds(secondWord, 0, secondWord.length(), rect_date);

        canvas.drawText(secondWord, 0, spanCount * singleHeight / 2
                + (fontMetrics_two.bottom - fontMetrics_two.top) / 2 + (rect_date.bottom - rect_date.top), mPaint);
    }

    private void drawImgBg(Canvas canvas, int i) {
        int sectionIndex;
        if (i < morningCount) {
            //上午
            sectionIndex = i % morningCount;
        } else if (i < (morningCount + afternoonCount)) {
            //下午
            sectionIndex = (i - morningCount) % afternoonCount;
        } else {
            //晚上
            sectionIndex = (i - morningCount - afternoonCount) % nightCount;
        }

        Bitmap bitmap = null;
        switch (sectionIndex) {
            case 0:
//                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.first_section);
                bitmap = getBitmapFromVector(R.drawable.ic_icon_first);
                break;
            case 1:
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.second_section);
                break;
            case 2:
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.third_section);
                break;
            case 3:
                bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.four_section);
                break;
        }

        RectF rectF = new RectF(0, 0, sectionWidth, sectionHeight);
        canvas.drawBitmap(bitmap, null, rectF, mPaint);
    }

    // 由于版本的原因，bitmapfactory转矢量图会出现问题
    private Bitmap getBitmapFromVector(int resId) {
        Bitmap bitmap = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Drawable vectorDrawable = getResources().getDrawable(resId);
        bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        vectorDrawable.draw(canvas);
        return bitmap;
    }


    /**
     * 居中文本
     */
    private void drawCenterText(Canvas canvas, String word, int fontSize, boolean isTop, float divideDistance) {
        mPaint.setTextSize(fontSize);
        //返回文本的宽度
        float textWidth = mPaint.measureText(word);
        //字体尺寸
        Paint.FontMetrics fontMetrics_one = mPaint.getFontMetrics();
        //测量文本的矩形
        mPaint.getTextBounds(word, 0, word.length(), rect_date);

        float offset;
        if (isTop) {
            offset = (rect_date.bottom - rect_date.top) + divideDistance;
        } else {
            offset = -(rect_date.bottom - rect_date.top) - divideDistance;
        }

        //注意这里的是sectionHeight
        canvas.drawText(word, (sectionWidth - textWidth) / 2, sectionHeight / 2
                + (fontMetrics_one.bottom - fontMetrics_one.top) / 2 + offset, mPaint);
    }


    private void drawText(String text, Canvas canvas, float widthCount, float heightCount, int textSize) {
        mPaint.setTextSize(textSize);
        //返回文本的宽度
        float textWidth = mPaint.measureText(text);
        //字体尺寸
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        //测量文本的矩形
        mPaint.getTextBounds(text, 0, text.length(), rect_date);
        //占两个长度一个宽度
        canvas.drawText(text, (widthCount * singleWidth - textWidth) / 2,
                heightCount * singleHeight / 2
                        + (fontMetrics.bottom - fontMetrics.top) / 2 - (rect_date.bottom - rect_date.top) / 2, mPaint);
    }
}
