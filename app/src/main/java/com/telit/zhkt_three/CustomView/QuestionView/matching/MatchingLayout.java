package com.telit.zhkt_three.CustomView.QuestionView.matching;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Path;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/7/17 19:03
 * <p>
 * 连线的布局
 * <p>
 * 连线的数据：
 * leftList : [{"id":"1","title":"222"},{"id":"2","title":"333"}]
 * rightList : [{"id":"3","title":"444"},{"id":"4","title":"555"}]
 * 正确的答案
 * "answer": "B",
 * 学生自己答案[连线题答案唯一所以取get(0)同answer一样是字符串，一对之间用逗号分隔，每对之间用竖线分隔]
 * "ownList": ["B"]
 * <p>
 * 说明：做题阶段动画连线，完成但未审阅显示自己的答题轨迹，审阅后显示正确答案和错误标记
 * <p>
 * todo 如何再做题前打乱顺序  使用视图保存信息而非List
 */
public class MatchingLayout extends RelativeLayout implements View.OnClickListener, ToLineView.OnSizeChangedCallback {

    private TextView tv_reset;
    private LinearLayout leftLayout;
    private LinearLayout rightLayout;
    private ToLineView toLineView;

    /**
     * 已经配对的视图集合
     */
    private List<View> answerLeftList;
    private List<View> answerRightList;

    /**
     * 状态：0要做 1完成 2批阅
     */
    private int status = 0;

    public void setStatus(int status) {
        this.status = status;
        //重置按钮的使能状态
        if (status != 0) {
            tv_reset.setBackground(getResources().getDrawable(R.drawable.shape_line_reset_disable));
            tv_reset.setTextColor(0xFFD5D5D5);
            tv_reset.setOnClickListener(null);
        } else {
            tv_reset.setBackground(getResources().getDrawable(R.drawable.shape_line_reset_normal));
            tv_reset.setTextColor(Color.WHITE);
            tv_reset.setOnClickListener(this);
        }
    }

    /**
     * 选中的第一个item
     */
    private View firstChooseView;

    //连线类的宽高
    private int itemWidth;
    private int itemHeight;

    /**
     * 连线题左右基本数据
     */
    private List<QuestionInfo.LeftListBean> leftLineList;
    private List<QuestionInfo.RightListBean> rightLineList;

    /**
     * 我提交的作答痕迹
     */
    private String ownAnswer;
    /**
     * 正确答案
     */
    private String standardAnswer;

    /**
     * 因为onSizeChange执行多次
     */
    private boolean showFirst = true;

    /**
     * 依据状态显示连线
     */
    public void showMatching() {
        if (status == 0) {
            if (showFirst) {
                showFirst = false;
            } else {
                //获取以前做过的答案，如果作答过了就不用考虑了
//                SharedPreferences sharedPreferences = getContext().getSharedPreferences("matching_track", Context.MODE_PRIVATE);
//                saveTrack = sharedPreferences.getString("my_line_answer", "");

                if (localTextAnswersBean != null) {
                    saveTrack = localTextAnswersBean.getAnswerContent();
                }

                QZXTools.logE("saveTrack=" + saveTrack, null);

                //绘制以前答题的痕迹
                if (!TextUtils.isEmpty(saveTrack)) {
                    fillPrevDraw();
                }
            }
        } else if (status == 1) {
            if (!TextUtils.isEmpty(ownAnswer)) {
                if (ownAnswer.contains("|")) {
                    String[] split = ownAnswer.split("\\|");
                    for (String item : split) {
                        String[] ans = item.split("\\,");
                        if (ans.length > 1) {
                            drawLigature(ans[0], ans[1], false);
                        }
                    }
                } else {
                    String[] ans = ownAnswer.split("\\,");
                    if (ans.length > 1) {
                        drawLigature(ans[0], ans[1], false);
                    }
                }
            }
        } else if (status == 2) {
            //因为至少两对连线题吧,所以不用考虑没有‘|’的情况
            //显示我的答案
            if (!TextUtils.isEmpty(ownAnswer)) {
                if (ownAnswer.contains("|")) {
                    String[] split = ownAnswer.split("\\|");
                    for (String item : split) {
                        String[] ans = item.split("\\,");
                        if (ans.length > 1) {
                            drawLigature(ans[0], ans[1], false);
                        }
                    }
                } else {
                    String[] ans = ownAnswer.split("\\,");
                    if (ans.length > 1) {
                        drawLigature(ans[0], ans[1], false);
                    }
                }
            }

            //显示正确答案
            if (!TextUtils.isEmpty(standardAnswer)) {
                String[] split = standardAnswer.split("\\|");
                for (String item : split) {
                    String[] ans = item.split("\\,");
                    if (ans.length > 1) {
                        drawLigature(ans[0], ans[1], true);
                    }
                }
            }
        }

        toLineView.setDrawStatus(status);
    }

    /**
     * 设置获取的正确答案数据和自己提交作答的痕迹数据
     */
    public void setLineResult(List<WorkOwnResult> ownList, String standardAnswer) {
        if (ownList != null && ownList.size() > 0) {
            ownAnswer = ownList.get(0).getAnswerContent();
        } else {
            ownAnswer = "";
        }

        this.standardAnswer = standardAnswer;
    }

    /**
     * 绘制之前答题的痕迹
     */
    private void fillPrevDraw() {
        if (saveTrack.contains("|")) {
            String[] split = saveTrack.split("\\|");
            for (String item : split) {
                String[] trackStr = item.split(",");
                drawLigature(trackStr[0], trackStr[1], false);
            }
        } else {
            //只有一对曾经作答过
            String[] trackStr = saveTrack.split(",");
            drawLigature(trackStr[0], trackStr[1], false);
        }
    }

    /**
     * 得到我的答案用于提交连线题数据
     */
    public String getMyAnswer() {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < answerLeftList.size(); i++) {
            int index = leftLayout.indexOfChild(answerLeftList.get(i));
            String leftId = (String) answerLeftList.get(index).getTag();
            String rightId = (String) answerRightList.get(index).getTag();
            stringBuilder.append(leftId);
            stringBuilder.append(",");
            stringBuilder.append(rightId);
            if (i < answerLeftList.size() - 1) {
                stringBuilder.append("|");
            }
        }
        return stringBuilder.toString().trim();
    }
    /**
     * 本地数据库保存
     */
    private LocalTextAnswersBean localTextAnswersBean;
    private QuestionInfo questionInfo;

    public void setLocalSave(LocalTextAnswersBean localTextAnswersBean, QuestionInfo questionInfo) {
        this.localTextAnswersBean = localTextAnswersBean;
        this.questionInfo = questionInfo;
    }

    public MatchingLayout(Context context) {
        this(context, null);
    }

    public MatchingLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MatchingLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        QZXTools.logE("MatchingLayout Construction method", null);

        itemWidth = getResources().getDimensionPixelSize(R.dimen.y360);
        itemHeight = getResources().getDimensionPixelSize(R.dimen.x90);

        answerLeftList = new ArrayList<>();
        answerRightList = new ArrayList<>();

        View view = LayoutInflater.from(context).inflate(R.layout.matching_layout, this, true);
        tv_reset = view.findViewById(R.id.matching_reset);
        leftLayout = view.findViewById(R.id.matching_left);
        rightLayout = view.findViewById(R.id.matching_right);
        toLineView = view.findViewById(R.id.matching_toLine);

        toLineView.setOnSizeChangedCallback(this);

        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "PingFang-SimpleBold.ttf");
        tv_reset.setTypeface(typeface);
    }

    public void resetItemWidthAndHeight(int itemWidth, int itemHeight) {
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
    }

    /**
     * 连线：用的是id
     * <p>
     * 左边id和右边的id配对
     *
     * @param isResult 仅仅为了区分答案和正常作答？
     */
    public void drawLigature(String leftId, String rightId, boolean isResult) {
        View left = null;
        View right = null;
        for (int i = 0; i < leftLayout.getChildCount(); i++) {
            QuestionInfo.LeftListBean item = leftLineList.get(i);
            if (leftId.equals(item.getId())) {
                left = leftLayout.getChildAt(i);
                break;
            }
        }
        for (int i = 0; i < rightLayout.getChildCount(); i++) {
            QuestionInfo.RightListBean item = rightLineList.get(i);
            if (rightId.equals(item.getId())) {
                right = rightLayout.getChildAt(i);
                break;
            }
        }
        //只有要做题状态才需要，因为可以点击，要保存已配对的视图集合
        if (status == 0) {
            answerLeftList.add(left);
            answerRightList.add(right);
        }
        float sx = leftLayout.getLeft() + left.getLeft() + left.getWidth();
        float sy = leftLayout.getTop() + left.getTop() + (left.getHeight() * 1.0f) / 2.0f;
        float ex = rightLayout.getLeft() + right.getLeft();
        float ey = rightLayout.getTop() + right.getTop() + (right.getHeight() * 1.0f) / 2.0f;


        QZXTools.logE("drawLigature sx=" + sx + ";sy=" + sy + ";ex=" + ex + ";ey=" + ey
                + ";leftLayout=" + leftLayout.getLeft() + ";left=" + left.getLeft()
                + ";rightLayout=" + rightLayout.getLeft() + ";right=" + right.getLeft(), null);

        //放置同侧相连接
        if (sx == ex) {
            return;
        }

        Path pathC = new Path();
        pathC.addCircle(sx, sy, 10, Path.Direction.CW);
        pathC.addCircle(ex, ey, 10, Path.Direction.CW);
        Path pathL = new Path();
        pathL.moveTo(sx, sy);
        pathL.lineTo(ex, ey);
        //添加点路径和线路径
        //  toLineView.addDotPath(pathC, isResult, questionInfoList.get(i).getId());
        toLineView.addLinePath(pathL, isResult);
    }

    /**
     * 填充连线数据
     */
    public void fillData(List<QuestionInfo.LeftListBean> leftLineList, List<QuestionInfo.RightListBean> rightLineList) {
        this.leftLineList = leftLineList;
        this.rightLineList = rightLineList;

        //左侧视图
        for (QuestionInfo.LeftListBean testLineBean : leftLineList) {
            View view = addItemView(testLineBean.getTitle(), testLineBean.getId());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemWidth, itemHeight);
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.x12);
            layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x12);
            leftLayout.addView(view, layoutParams);
        }

        //右侧视图
        for (QuestionInfo.RightListBean testLineBean : rightLineList) {
            View view = addItemView(testLineBean.getTitle(), testLineBean.getId());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(itemWidth, itemHeight);
            layoutParams.topMargin = getResources().getDimensionPixelSize(R.dimen.x12);
            layoutParams.bottomMargin = getResources().getDimensionPixelSize(R.dimen.x12);
            rightLayout.addView(view, layoutParams);
        }
    }

    /**
     * 有背景的文本
     */
    private View addItemView(String text, String id) {
        TextView textView = new TextView(getContext());
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "PingFang-SimpleBold.ttf");
        textView.setTypeface(typeface);
        textView.setText(text);
        textView.setTag(id);
        textView.setTextColor(getResources().getColor(R.color.word_gray_deep));
        textView.setBackground(getResources().getDrawable(R.drawable.shape_line_item_bg));
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, getResources().getDimension(R.dimen.x36));
        //就算非答题模式可以点击也没关系，因为全部标记为配对状态了，这里还是采用要做状态才可以点击
        if (status == 0) {
            textView.setOnClickListener(this);
        } else {
            textView.setOnClickListener(null);
        }
        return textView;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
//        QZXTools.logE("onSizeChanged......" + saveTrack, null);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        QZXTools.logE("onAttachedToWindow......" + saveTrack, null);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        QZXTools.logE("Matching onDetachedFromWindow......" + saveTrack, null);

//        //提交未完成的痕迹保存
//        SharedPreferences sharedPreferences = getContext().getSharedPreferences("matching_track", Context.MODE_PRIVATE);
//        sharedPreferences.edit().putString("my_line_answer", saveTrack).commit();

        //-------------------------答案保存，依据作业题目id
     /*   LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
        localTextAnswersBean.setHomeworkId(questionInfo.getHomeworkId());
        localTextAnswersBean.setQuestionId(questionInfo.getId());
        localTextAnswersBean.setUserId(UserUtils.getUserId());
        localTextAnswersBean.setQuestionType(questionInfo.getQuestionType());
        localTextAnswersBean.setAnswerContent(saveTrack);*/
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
        //插入或者更新数据库
        //  MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
        //-------------------------答案保存，依据作业题目id
    }

    //保存作答痕迹
    private String saveTrack;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.matching_reset) {
            if (toLineView.isAnimRunning()) {
                QZXTools.popCommonToast(getContext(), "正在连线绘制请稍后重置", false);
                return;
            }

            //重置
            answerLeftList.clear();
            answerRightList.clear();
            saveTrack = "";

            if (firstChooseView != null) {
                firstChooseView.setBackground(getResources().getDrawable(R.drawable.shape_line_item_bg));
                firstChooseView = null;
            }

            //  toLineView.resetDrawLine(questionInfoList.get(i).getId());
        } else {
            //如果集合中有视图表示已经连接过了,或者正在动画中也不执行
            if (answerLeftList.indexOf(v) >= 0 || answerRightList.indexOf(v) >= 0 || toLineView.isAnimRunning()) {
                return;
            }

            if (firstChooseView == null) {
                firstChooseView = v;
                v.setBackground(getResources().getDrawable(R.drawable.shape_line_bg_with_border));
            } else {
                //取消选中
                if (firstChooseView == v) {
                    v.setBackground(getResources().getDrawable(R.drawable.shape_line_item_bg));
                    firstChooseView = null;
                    return;
                }
                //同一区域的不能选中
                else if (firstChooseView.getParent() == v.getParent()) {
                    firstChooseView.setBackground(getResources().getDrawable(R.drawable.shape_line_item_bg));
                    firstChooseView = v;
                    v.setBackground(getResources().getDrawable(R.drawable.shape_line_bg_with_border));
                    return;
                } else {
                    StringBuilder stringBuilder = new StringBuilder();
                    if (!TextUtils.isEmpty(saveTrack)) {
                        stringBuilder.append(saveTrack);
                        stringBuilder.append("|");
                    }
                    float sx;
                    float sy;
                    float ex;
                    float ey;
                    Path path = new Path();
                    if (firstChooseView.getParent() == leftLayout) {
                        //从左到右
                        sx = leftLayout.getLeft() + firstChooseView.getLeft() + firstChooseView.getWidth();
                        sy = leftLayout.getTop() + firstChooseView.getTop() + firstChooseView.getHeight() * 1.0f / 2.0f;

                        ex = rightLayout.getLeft() + v.getLeft();
                        ey = rightLayout.getTop() + v.getTop() + v.getHeight() * 1.0f / 2.0f;

                        QZXTools.logE("sx=" + sx + ";sy=" + sy + ";ex=" + ex + ";ey=" + ey, null);

                        path.addCircle(sx, sy, 10, Path.Direction.CW);
                        path.moveTo(sx, sy);
                        path.lineTo(ex, ey);
                        path.addCircle(ex, ey, 10, Path.Direction.CW);

                        answerLeftList.add(firstChooseView);
                        answerRightList.add(v);

                        //保存作答结果
                        int leftIndex = leftLayout.indexOfChild(firstChooseView);
                        stringBuilder.append(leftLineList.get(leftIndex).getId());

                        stringBuilder.append(",");

                        int rightIndex = rightLayout.indexOfChild(v);
                        stringBuilder.append(rightLineList.get(rightIndex).getId());

                    } else if (firstChooseView.getParent() == rightLayout) {
                        //从右到左
                        sx = rightLayout.getLeft() + firstChooseView.getLeft();
                        sy = rightLayout.getTop() + firstChooseView.getTop() + firstChooseView.getHeight() * 1.0f / 2.0f;

                        ex = leftLayout.getLeft() + v.getLeft() + v.getWidth();
                        ey = leftLayout.getTop() + v.getTop() + v.getHeight() * 1.0f / 2.0f;

                        QZXTools.logE("sx=" + sx + ";sy=" + sy + ";ex=" + ex + ";ey=" + ey, null);

                        path.addCircle(sx, sy, 10, Path.Direction.CW);
                        path.moveTo(sx, sy);
                        path.lineTo(ex, ey);
                        path.addCircle(ex, ey, 10, Path.Direction.CW);

                        answerLeftList.add(v);
                        answerRightList.add(firstChooseView);

                        //保存作答结果
                        int leftIndex = leftLayout.indexOfChild(v);
                        stringBuilder.append(leftLineList.get(leftIndex).getId());

                        stringBuilder.append(",");

                        int rightIndex = rightLayout.indexOfChild(firstChooseView);
                        stringBuilder.append(rightLineList.get(rightIndex).getId());

                    }

                    //赋值给saveTrack
                    saveTrack = stringBuilder.toString().trim();
                    QZXTools.logE("click saveTrack=" + saveTrack, null);

                    //绘制Path
                    toLineView.getDrawPath(path);
                    firstChooseView.setBackground(getResources().getDrawable(R.drawable.shape_line_item_bg));
                    firstChooseView = null;

                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    localTextAnswersBean.setHomeworkId(questionInfo.getHomeworkId());
                    localTextAnswersBean.setQuestionId(questionInfo.getId());
                    localTextAnswersBean.setQuestionType(questionInfo.getQuestionType());
                    localTextAnswersBean.setAnswerContent(saveTrack);
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                    //-------------------------答案保存，依据作业题目id
                }
            }
        }
    }

    @Override
    public void onSizeChange() {
        QZXTools.logE("ToLineView onSizeChange......" + saveTrack, null);
        showMatching();
    }
}
