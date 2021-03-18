package com.telit.zhkt_three.CustomView.interactive.XCTK;

import android.content.ClipData;
import android.content.ClipDescription;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.util.AttributeSet;
import android.view.DragEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/7/29 9:23
 * <p>
 * 选词填空视图，默认答案空格为8个汉字字符长度，utf8格式是24个字符空格
 * <p>
 *
 ChooseFillBlankView chooseFillBlank = findViewById(R.id.chooseFillBlank);

 //8*3
 String content = "纷纷扬扬的（                        ）下了半尺多厚。天地间白茫茫的一片。我顺着铁路工地走了四十多公里，" +
 "只听见各种机器的吼声，可是看不见人影，也看不见工点。一进灵官峡，我就心里发慌。";

 // 选项集合
 List<String> optionList = new ArrayList<>();
 optionList.add("白茫茫");
 optionList.add("雾蒙蒙");
 optionList.add("铁路");
 optionList.add("公路");
 optionList.add("大雪");

 // 答案范围集合
 List<AnswerRange> rangeList = new ArrayList<>();
 rangeList.add(new AnswerRange(6, 30));

 chooseFillBlank.setData(content, optionList, rangeList);
 * todo 有问题，数据结构问题
 */
public class ChooseFillBlankView extends RelativeLayout implements View.OnDragListener, View.OnTouchListener {

    private TextView tv_content;
    private LinearLayout ll_option;

    public ChooseFillBlankView(Context context) {
        this(context, null);
    }

    public ChooseFillBlankView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ChooseFillBlankView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(context).inflate(R.layout.view_xctk, this, true);

        tv_content = view.findViewById(R.id.xctk_tv_content);
        ll_option = view.findViewById(R.id.xctk_ll_option);

    }

    // 填空题内容
    private SpannableStringBuilder content;
    // 选项列表
    private List<String> optionList;
    // 答案范围集合
    private List<AnswerRange> answerRangeList;
    // 答案集合
    private List<String> answerList;
    // 选项位置
    private int optionPosition;

    private int answerPosition = -1;


    private String originalContent;

    /**
     * 设置数据
     *
     * @param originContent   源数据
     * @param optionList      选项列表
     * @param answerRangeList 答案范围集合(这个空是固定的范围大小，默认24个空格，utf8--->8个汉字大小)
     */
    public void setData(String originContent, List<String> optionList, List<AnswerRange> answerRangeList) {
        if (TextUtils.isEmpty(originContent) || optionList == null || optionList.isEmpty()
                || answerRangeList == null || answerRangeList.isEmpty()) {
            return;
        }

        this.originalContent = originContent;

        if (answerList == null) {
            answerList = new ArrayList<>();
            for (int i = 0; i < answerRangeList.size(); i++) {
                answerList.add("");
            }
        }

        // 获取课文内容--->SpannableStringBuilder
        this.content = new SpannableStringBuilder(originContent);

        // 选项列表
        this.optionList = optionList;

        // 答案范围集合
        this.answerRangeList = answerRangeList;

        // 避免重复创建拖拽选项
        if (ll_option.getChildCount() < 1) {
            // 拖拽选项列表
            List<Button> itemList = new ArrayList<>();
            for (String option : optionList) {
                Button btnAnswer = new Button(getContext());
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.setMargins(0, 0, getResources().getDimensionPixelSize(R.dimen.x45), 0);
                btnAnswer.setLayoutParams(params);
                btnAnswer.setBackgroundColor(Color.parseColor("#4DB6AC"));
                btnAnswer.setTextColor(Color.WHITE);
                btnAnswer.setText(option);
                btnAnswer.setOnTouchListener(this);
                itemList.add(btnAnswer);
            }

            // 显示拖拽选项
            for (int i = 0; i < itemList.size(); i++) {
                ll_option.addView(itemList.get(i));
            }
        } else {
            // 不显示已经填空的选项
            for (int i = 0; i < ll_option.getChildCount(); i++) {
                Button button = (Button) ll_option.getChildAt(i);
                String option = button.getText().toString();
                if (!answerList.isEmpty() && answerList.contains(option)) {
                    button.setVisibility(INVISIBLE);
                } else {
                    button.setVisibility(VISIBLE);
                }
            }
        }

        // 设置填空处点击事件
        for (int i = 0; i < this.answerRangeList.size(); i++) {
            AnswerRange range = this.answerRangeList.get(i);
            BlankClickableSpan blankClickableSpan = new BlankClickableSpan(i);
            content.setSpan(blankClickableSpan, range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        }

        // 填空处设置触摸事件
        tv_content.setMovementMethod(new TouchLinkMovementMethod());
        tv_content.setText(content);

        // 目标区域设置拖拽事件监听
        tv_content.setOnDragListener(this);

    }

    /**
     * 触摸事件
     */
    class BlankClickableSpan extends ClickableSpan {

        private int position;

        public BlankClickableSpan(int position) {
            this.position = position;
        }

        @Override
        public void onClick(final View widget) {
            // 显示原有答案
            String oldAnswer = answerList.get(position);
            if (!TextUtils.isEmpty(oldAnswer)) {
                answerList.set(position, "");
                updateAnswer(answerList);
                startDrag(ll_option.getChildAt(getOptionPosition(oldAnswer)));
            }
        }

        @Override
        public void updateDrawState(TextPaint ds) {
            // 不显示下划线
            ds.setUnderlineText(false);
        }
    }

    /**
     * 获取选项位置
     *
     * @param option 选项内容
     * @return 选项位置
     */
    private int getOptionPosition(String option) {
        for (int i = 0; i < ll_option.getChildCount(); i++) {
            Button btnOption = (Button) ll_option.getChildAt(i);
            if (btnOption.getText().toString().equals(option)) {
                return i;
            }
        }
        return 0;
    }

    /**
     * 开始拖拽
     *
     * @param v 当前对象
     */
    private void startDrag(View v) {
        // 选项内容
        String optionContent = ((Button) v).getText().toString();
        // 记录当前答案选项的位置
        optionPosition = getOptionPosition(optionContent);
        // 开始拖拽后在列表中隐藏答案选项
        v.setVisibility(INVISIBLE);

        //携带拖拽的答案字符串
        ClipData.Item item = new ClipData.Item(optionContent);
        ClipData data = new ClipData(null, new String[]{ClipDescription.MIMETYPE_TEXT_PLAIN}, item);

        v.startDrag(data, new DragShadowBuilder(v), null, 0);
    }

    /**
     * 更新答案
     *
     * @param answerList 答案列表
     */
    public void updateAnswer(List<String> answerList) {
        // 重新初始化数据
        setData(originalContent, optionList, answerRangeList);

        // 重新填写已经存在的答案
        if (answerList != null && !answerList.isEmpty()) {
            for (int i = 0; i < answerList.size(); i++) {
                String answer = answerList.get(i);
                if (!TextUtils.isEmpty(answer)) {
                    fillAnswer(answer, i);
                }
            }
        }
    }

    private IconTextSpan iconTextSpan;

    /**
     * 填写答案
     *
     * @param answer   当前填空处答案
     * @param position 填空位置
     */
    private void fillAnswer(String answer, int position) {
        // 替换答案
        AnswerRange range = answerRangeList.get(position);

        //设置背景色
        iconTextSpan = new IconTextSpan(getContext(), R.color.colorAccent, answer);
        iconTextSpan.setNeedBorder(false);
        content.setSpan(iconTextSpan, range.start, range.end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 将答案添加到集合中：答案把空白的字符串去掉
        answerList.set(position, answer.replace(" ", ""));

        // 更新内容
        tv_content.setText(content);
    }

    private FrameSpan frameSpan = new FrameSpan();
    // 一次拖拽填空是否完成
    private boolean isFillBlank;

    @Override
    public boolean onDrag(View v, DragEvent event) {
        final int action = event.getAction();
        switch (action) {
            case DragEvent.ACTION_DRAG_STARTED: // 拖拽开始
//                QZXTools.logE("drag started aim", null);

                // vRect=Rect(0, 0 - 1920, 179);vWidth=1920;vHeight=179;这里的v指的应该是目标区域，即tv_content
//                vRect.set(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
//                QZXTools.logE("vRect=" + vRect + ";vWidth=" + v.getMeasuredWidth() + ";vHeight=" + v.getMeasuredHeight(), null);

                //初始化答案位置
                answerPosition = -1;

                //判断是否是想要拖拽的类型
                return event.getClipDescription().hasMimeType(ClipDescription.MIMETYPE_TEXT_PLAIN);

            case DragEvent.ACTION_DRAG_ENTERED: // 被拖拽View进入目标区域
                QZXTools.logE("drag enter aim", null);
                return true;

            case DragEvent.ACTION_DRAG_LOCATION: // 被拖拽View在目标区域移动
//                QZXTools.logE("drag enter aim move", null);

                answerPosition = -1;

                // 获取TextView的Layout对象,尺寸改变的时候返回可能为null
                Layout layout = tv_content.getLayout();
                if (layout == null) {
                    return true;
                }

                // 当前x、y坐标
                float currentX = event.getX();
                float currentY = event.getY();

//                QZXTools.logE("currentX=" + currentX + ";currentY=" + currentY, null);

                for (int i = 0; i < answerRangeList.size(); i++) {
                    AnswerRange range = answerRangeList.get(i);

                    // 获取TextView中第一个字符的坐标
                    Rect startBound = new Rect();
                    layout.getLineBounds(layout.getLineForOffset(range.start), startBound);

                    // 获取TextView中最后一个字符的坐标
                    Rect endBound = new Rect();
                    layout.getLineBounds(layout.getLineForOffset(range.end), endBound);

//                    QZXTools.logE("startBound=" + startBound + ";endBound=" + endBound + ";lineCount=" + layout.getLineCount(), null);

                    // 字符顶部y坐标
                    int yAxisTop = startBound.top + getResources().getDimensionPixelSize(R.dimen.x10);
                    // 字符底部y坐标
                    int yAxisBottom = endBound.bottom - getResources().getDimensionPixelSize(R.dimen.x10);
                    // 字符左边x坐标
                    float xAxisLeft = layout.getPrimaryHorizontal(range.start) + getResources().getDimensionPixelSize(R.dimen.x10);
                    // 字符右边x坐标
                    float xAxisRight = layout.getSecondaryHorizontal(range.end) - getResources().getDimensionPixelSize(R.dimen.x10);

                    // 一行的文本高度
                    int lineHeight = startBound.bottom - startBound.top;
                    // 当前的文本高度
                    int currentLineHeight = endBound.bottom - startBound.top;

//                    QZXTools.logE("yAxisTop=" + yAxisTop + ";yAxisBottom=" + yAxisBottom
//                            + ";xAxisLeft=" + xAxisLeft + ";xAxisRight=" + xAxisRight, null);

                    if (currentLineHeight <= lineHeight) { // 填空在一行
                        if (currentX > xAxisLeft && currentX < xAxisRight &&
                                currentY < yAxisBottom && currentY > yAxisTop) {
                            answerPosition = i;
                            break;
                        }
                    } else { // 跨行填空
                        if ((currentX > xAxisLeft || currentX < xAxisRight) &&
                                currentY < yAxisBottom && currentY > yAxisTop) {
                            answerPosition = i;
                            break;
                        }
                    }
                }

                QZXTools.logE("answerPosition=" + answerPosition, null);

                //展示答案位置框框
                if (answerPosition != -1) {
                    if (TextUtils.isEmpty(answerList.get(answerPosition))) {
                        content.setSpan(frameSpan, answerRangeList.get(answerPosition).start,
                                answerRangeList.get(answerPosition).end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        // 更新内容
                        tv_content.setText(content);
                    } else {
                        //设置背景色
                        iconTextSpan = new IconTextSpan(getContext(), R.color.colorAccent, answerList.get(answerPosition));
                        iconTextSpan.setNeedBorder(true);
                        content.setSpan(iconTextSpan, answerRangeList.get(answerPosition).start,
                                answerRangeList.get(answerPosition).end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv_content.setText(content);
                    }
                }
                return true;

            case DragEvent.ACTION_DRAG_EXITED: // 被拖拽View离开目标区域
                QZXTools.logE("drag exit aim", null);

                //移除
                iconTextSpan.setNeedBorder(false);
                content.removeSpan(frameSpan);
                tv_content.setText(content);
                answerPosition = -1;
                return true;

            case DragEvent.ACTION_DROP: // 放开被拖拽View
                QZXTools.logE("drag drop aim", null);
                //移除
                content.removeSpan(frameSpan);

                if (answerPosition == -1) {
                    return true;
                }

                // 释放拖放阴影，并获取移动数据
                ClipData.Item item = event.getClipData().getItemAt(0);
                String answer = item.getText().toString();

                // 重复拖拽，在答案列表中显示原答案
                String oldAnswer = answerList.get(answerPosition);
                if (!TextUtils.isEmpty(oldAnswer)) {
                    ll_option.getChildAt(getOptionPosition(oldAnswer)).setVisibility(VISIBLE);
                }

                // 填写答案
                fillAnswer(answer, answerPosition);
                isFillBlank = true;
                return true;

            case DragEvent.ACTION_DRAG_ENDED: // 拖拽完成
                QZXTools.logE("drag ended aim", null);

                //移除
                content.removeSpan(frameSpan);
                tv_content.setText(content);

                if (!isFillBlank) {
                    ll_option.getChildAt(optionPosition).setVisibility(VISIBLE);
                } else {
                    isFillBlank = false;
                }
                return true;
            default:
                break;
        }

        return false;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        startDrag(v);
        return false;
    }
}
