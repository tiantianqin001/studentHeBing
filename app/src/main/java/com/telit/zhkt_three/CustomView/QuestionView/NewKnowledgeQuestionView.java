package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Activity.AfterHomeWork.LearnResourceActivity;
import com.telit.zhkt_three.Activity.AfterHomeWork.TypicalAnswersActivity;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Activity.MistakesCollection.MistakesImproveActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.PerfectAnswerActivity;
import com.telit.zhkt_three.Adapter.NewKnowledgeAdapter;
import com.telit.zhkt_three.Adapter.NewKnowledgeTwoAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.TempSaveItemInfo;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTagHandler;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/19 10:38
 * <p>
 * QuestionBank类型---题库类型
 * 主要用于临时作答的痕迹保存
 * <p>
 * 提交类型的题库作答  getQuestionChannelType()--->保存着类型
 * <p>
 * 新增答案显示日期
 * <p>
 * 批阅后才会显示分值
 * <p>
 * todo 填空题和主观题无数据可提交bug
 */
public class NewKnowledgeQuestionView extends RelativeLayout {

    //题库数据集
    private QuestionBank questionBank;

    /**
     * 新增单独一个保存多选的选项集合
     */
    private List<String> saveMultiList;

    /**
     * 当前的题目位置下标
     */
    private int curPosition;

    /**
     * 头部标题
     */
    private TextView Item_Bank_head_title;
    private HtmlTextView Item_Bank_head_content;
    //新增题库也显示总分
//    private TextView Item_Bank_head_score;

    private TextView Item_Bank_head_promote;

    private TextView Item_Bank_head_good_answer;

    /**
     * 无List的选项
     */
    private RecyclerView rv_item_bank_options_layout;

    /**
     * List的题型
     */


    /**
     * 答案
     */
    private ScrollView Item_Bank_Answer_Scroll;

    private LinearLayout Item_Bank_Answer_Layout;
    private TextView Item_Bank_my_Answer;
    private TextView Item_Bank_right_Answer;
    private LinearLayout Item_Bank_Point;
    private ImageView Item_Bank_Img_Point;
    private LinearLayout Item_Bank_Analysis;
    private ImageView Item_Bank_Img_Analysis;
    private LinearLayout Item_Bank_Answer;
    private ImageView Item_Bank_Img_Answer;
    private TextView img_total_typical_answers;
    private ImageView iv_collect;

    private TextView Item_Bank_Show_Remark;
    private TextView img_total_learn_resource;
    /**
     * 0未提交  1 已提交  2 已批阅
     * 未做：做题的视图界面 todoView
     * 提交：查看已做视图界面 showView
     * 批阅：查看已做视图及答案视图界面 showView plus AnswerView
     */
    private String status;

    /**
     * 是否来自错题集
     */
    private boolean isMistaken;

    /**
     * 设置显示时间
     */
    private String showAnswerDate;

    private Context context;

    private NewKnowledgeAdapter newKnowledgeAdapter;

    /**
     * 设置题型数据
     *  @param questionBank 题库详细题型内容
     * @param status       新增状态用于是否显示答案
     */
    public void setQuestionInfo(QuestionBank questionBank, int curPosition, String status, boolean isMistaken) {

//        QZXTools.logE("setQuestionInfo status=" + status, null);
        if (status.equals("2")) {
            //批阅了可以显示答案
            questionBank.setShownAnswer(true);
        }
        this.isMistaken = isMistaken;
        this.questionBank = questionBank;
        this.curPosition = curPosition;
        this.status = status;
        showAnswerDate = questionBank.getAnswerPublishDate();
        initData();
    }

    public NewKnowledgeQuestionView(Context context) {
        this(context, null);
    }

    public NewKnowledgeQuestionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewKnowledgeQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.item_new_bank_view_layout, this, true);
        //注意这里不可以初始化数据，因为没有数据，questionInfo是空的，需要通过set方法塞入
        initView();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "PingFang-SimpleBold.ttf");

        saveMultiList = new ArrayList<>();

        Item_Bank_head_title = findViewById(R.id.Item_Bank_head_title);
        Item_Bank_head_content = findViewById(R.id.Item_Bank_head_content);
//        Item_Bank_head_score = findViewById(R.id.Item_Bank_head_score);
        Item_Bank_head_promote = findViewById(R.id.Item_Bank_head_promote);

        Item_Bank_head_good_answer = findViewById(R.id.Item_Bank_head_good_answer);
        img_total_typical_answers = findViewById(R.id.img_total_typical_answers);
        img_total_learn_resource = findViewById(R.id.img_total_learn_resource);

        Item_Bank_head_title.setTypeface(typeface);
        Item_Bank_head_content.setTypeface(typeface);
//        Item_Bank_head_score.setTypeface(typeface);
        Item_Bank_head_promote.setTypeface(typeface);
        Item_Bank_head_good_answer.setTypeface(typeface);

        img_total_learn_resource.setTypeface(typeface);
        img_total_typical_answers.setTypeface(typeface);

        rv_item_bank_options_layout = findViewById(R.id.rv_item_bank_options_layout);
        Item_Bank_Answer_Scroll = findViewById(R.id.Item_Bank_Answer_Scroll);
        Item_Bank_Answer_Layout = findViewById(R.id.Item_Bank_Answer_Layout);
        Item_Bank_my_Answer = findViewById(R.id.Item_Bank_my_Answer);
        Item_Bank_my_Answer.setTypeface(typeface);

        Item_Bank_right_Answer = findViewById(R.id.Item_Bank_right_Answer);
        Item_Bank_right_Answer.setTypeface(typeface);

        Item_Bank_Point = findViewById(R.id.Item_Bank_Point);
        Item_Bank_Img_Point = findViewById(R.id.Item_Bank_Img_Point);
        Item_Bank_Analysis = findViewById(R.id.Item_Bank_Analysis);
        Item_Bank_Img_Analysis = findViewById(R.id.Item_Bank_Img_Analysis);
        Item_Bank_Answer = findViewById(R.id.Item_Bank_Answer);
        Item_Bank_Img_Answer = findViewById(R.id.Item_Bank_Img_Answer);

        Item_Bank_Show_Remark = findViewById(R.id.Item_Bank_Show_Remark);
        Item_Bank_Show_Remark.setTypeface(typeface);

        TextView Item_Bank_Tv_Point = findViewById(R.id.Item_Bank_Tv_Point);
        TextView Item_Bank_Tv_Analysis = findViewById(R.id.Item_Bank_Tv_Analysis);
        TextView Item_Bank_Tv_Answer = findViewById(R.id.Item_Bank_Tv_Answer);
        Item_Bank_Tv_Point.setTypeface(typeface);
        Item_Bank_Tv_Analysis.setTypeface(typeface);
        Item_Bank_Tv_Answer.setTypeface(typeface);

        iv_collect = findViewById(R.id.iv_collect);
        iv_collect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onCollectClickListener!=null){
                    onCollectClickListener.OnCollectClickListener(questionBank,curPosition);
                }
            }
        });
        //查看学习资源
        img_total_learn_resource.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_learn_resource = new Intent(getContext(), LearnResourceActivity.class);
                intent_learn_resource.putExtra("questionId", questionBank.getId() + "");
                intent_learn_resource.putExtra("homeworkId", questionBank.getHomeworkId());
                getContext().startActivity(intent_learn_resource);
            }
        });

        Item_Bank_Show_Remark.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> imgFilePathList = (ArrayList<String>) questionBank.getTeaDescFile();
                Intent intent = new Intent(getContext(), ImageLookActivity.class);
                intent.putStringArrayListExtra("imgResources", imgFilePathList);
                intent.putExtra("NeedComment", false);
                intent.putExtra("curImgIndex", 0);
                getContext().startActivity(intent);
            }
        });

        img_total_typical_answers.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent_typical_answers = new Intent(getContext(), TypicalAnswersActivity.class);
                intent_typical_answers.putExtra("questionId", questionBank.getId() + "");
                intent_typical_answers.putExtra("homeworkId", questionBank.getHomeworkId());
                getContext().startActivity(intent_typical_answers);
            }
        });
        //todo  去巩固  闪退
        Item_Bank_head_promote.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = getBundle();
                String knowledge_json = bundle.getString("knowledge_json");
                if (TextUtils.isEmpty(knowledge_json)) {
                    QZXTools.popCommonToast(getContext(), "暂时没有巩固提醒", false);
                    return;
                }

                /**
                 * 进入错题集做题界面
                 * */
                Intent intent = new Intent(getContext(), MistakesImproveActivity.class);
                intent.putExtra("improvement", getBundle());
                getContext().startActivity(intent);

                //点击复习错题 埋点  TODO 要选中学科
                BuriedPointUtils.buriedPoint("2021","","","","");
            }
        });
        //查看优秀答案
        Item_Bank_head_good_answer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PerfectAnswerActivity.class);
                intent.putExtra("questionId", questionBank.getId() + "");
                intent.putExtra("homeworkId", questionBank.getHomeworkId());
                getContext().startActivity(intent);
            }
        });
    }

    private void initData() {

        //  Item_Bank_list_question_layout.removeAllViews();

        if (isMistaken && bundle != null && !TextUtils.isEmpty(bundle.getString("knowledge_json"))) {
            Item_Bank_head_promote.setVisibility(VISIBLE);
        } else {
            Item_Bank_head_promote.setVisibility(GONE);
        }

        //优秀答案只有主观题才能看见
        Item_Bank_head_good_answer.setVisibility(GONE);
        img_total_typical_answers.setVisibility(GONE);

        //是否显示教师批阅
        if (questionBank.getTeaDescFile() == null || questionBank.getTeaDescFile().size() <= 0) {
            Item_Bank_Show_Remark.setVisibility(GONE);
        } else {
            Item_Bank_Show_Remark.setVisibility(VISIBLE);
        }

        getHeadAndOptionsInfo();

        //如果是错题集 又是题库出题  显示去巩固
        if (isMistaken ){
            if (status.equals(Constant.Commit_Status) || status.equals(Constant.Review_Status)){
                Item_Bank_head_promote.setVisibility(VISIBLE);
            }
        }

        if ("2".equals(status) || !TextUtils.isEmpty(showAnswerDate)) {
            if (!status.equals(Constant.Retry_Status)){
                if (!status.equals(Constant.Save_Status) ){
                    showResumeAnswer();
                }
            }
        }
        if (status.equals(Constant.Retry_Status)){
            //主观题不显示
            if (!questionBank.getQuestionChannelType().equals(Constant.Subject_Item)){
                showResumeAnswer();
                img_total_learn_resource.setVisibility(VISIBLE);
            }

        }
        //当前状态是打回重做

        if ("0".equals(status) ||"-1".equals(status) ||"-2".equals(status) || (bundle != null && "1".equals(bundle.getString("flag")))||isMistaken) {
            iv_collect.setVisibility(GONE);
        }

        setCollect(questionBank.getIsCollect());
    }

    /**
     * 设置收藏
     *
     * @param isCollect
     */
    public void setCollect(String isCollect){
        if ("0".equals(isCollect)){
            iv_collect.setImageResource(R.mipmap.collect_gray_icon);
        }else {
            iv_collect.setImageResource(R.mipmap.collect_red_icon);
        }
    }

    /**
     * 主观题题目ID
     */
    public static String subjQuestionId;

    /**
     * 头部数据化以及选项栏
     * 修改ownList
     * <p>
     * "ownList": [
     * {
     * "state": "2",
     * "score": "0",
     * "answerContent": "Fggb"
     * }
     * ],
     */
    private void getHeadAndOptionsInfo() {
        //题型
        switch (questionBank.getQuestionChannelType()) {
            case Constant.Single_Choose:
                Item_Bank_head_title.setText((curPosition + 1) + "、[单选题]");
                break;
            case Constant.Multi_Choose:
                Item_Bank_head_title.setText((curPosition + 1) + "、[多选题]");
                break;
            case Constant.Fill_Blank:
                Item_Bank_head_title.setText((curPosition + 1) + "、[填空题]");
                break;
            case Constant.Subject_Item:
                Item_Bank_head_title.setText((curPosition + 1) + "、[主观题]");
                if (status.equals(Constant.Commit_Status) || status.equals(Constant.Review_Status)){
                    img_total_typical_answers.setVisibility(VISIBLE);
                    Item_Bank_head_good_answer.setVisibility(VISIBLE);
                }
                break;
            case Constant.Judge_Item:
                Item_Bank_head_title.setText((curPosition + 1) + "、[判断题]");
                break;
        }
        //是否存在List
//        QZXTools.logE("List=" + questionBank.getList(), null);
        if (TextUtils.isEmpty(questionBank.getList()) || questionBank.getList().equals("NULL")) {
            //设置题的中间内容    //题型信息
            rv_item_bank_options_layout.setLayoutManager(new LinearLayoutManager(context));
            newKnowledgeAdapter = new NewKnowledgeAdapter(questionBank,context,status);
            rv_item_bank_options_layout.setAdapter(newKnowledgeAdapter);

            String optionJson = questionBank.getAnswerOptions();


        } else {

            //TODO   这里是一道题包含多道题  延后处理

            rv_item_bank_options_layout.setLayoutManager(new LinearLayoutManager(context));
            NewKnowledgeTwoAdapter newKnowledgeTwoAdapter = new NewKnowledgeTwoAdapter(questionBank,context,status,questionBank.getQuestionChannelType());
            rv_item_bank_options_layout.setAdapter(newKnowledgeTwoAdapter);
        }
        //题目信息
        String ItemBankTitle = questionBank.getQuestionText();
        if (TextUtils.isEmpty(ItemBankTitle))return;
//        Item_Bank_head_content.setHtml(ItemBankTitle, new HtmlHttpImageGetter(Item_Bank_head_content));

        //添加题目分值 ---新增如果是批阅后题库展示学生得分

        /**
         *     SpannableString spannableString = new SpannableString("今天天气不错");
         spannableString.setSpan(new ForegroundColorSpan(Color.parseColor("#FF0000")), 2,
         spannableString.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
         tv5.setText(spannableString);
         * */

        String score = questionBank.getScore();
        String scoreStr;
        if (status.equals("2")) {
            /**
             * notes:注意这里不用ownscore了，改用ownList中的score和
             * */
            String myScore = questionBank.getOwnscore();

//            double totalDScore = 0.0;

//            boolean isException = false;

//            List<WorkOwnResult> workOwnResults = questionBank.getOwnList();
//            if (workOwnResults != null && workOwnResults.size() > 0) {
//                for (WorkOwnResult workOwnResult : workOwnResults) {
//                    String scoreTag = workOwnResult.getScore();
//                    try {
//                        double dScore = Double.parseDouble(scoreTag);
//                        totalDScore += dScore;
//                    } catch (Exception e) {
//                        isException = true;
//                        //排除空指针以及非Double类型的字符串异常
//                        e.printStackTrace();
//                        CrashReport.postCatchedException(e);
//                    }
//                }
//            }
//
//            if (isException) {
//                scoreStr = "(总分是：" + score + "分,我的得分：" + totalDScore + "分;我的得分异常！）";
//            } else {
//                scoreStr = "(总分是：" + score + "分,我的得分：" + totalDScore + "分）";
//            }
//            Item_Bank_head_score.setText("(总分是：" + score + "分,我的得分：" + myScore + "分）");

            scoreStr = "(总分是：" + score + "分,我的得分：" + myScore + "分）";

        } else {
            scoreStr = "(" + score + "分)";
//            Item_Bank_head_score.setText("(" + score + "分)");
        }

        /**
         * 这里附上一般TextView的部分文字修改的代码
         * */
//        SpannableStringBuilder spannableStringBuilder = new SpannableStringBuilder();
//        spannableStringBuilder.append(ItemBankTitle);
//        spannableStringBuilder.append(scoreStr);
//        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#ff4444"));
//        spannableStringBuilder.setSpan(foregroundColorSpan, ItemBankTitle.length(), spannableStringBuilder.length(),
//                Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
//        Item_Bank_head_content.setText(spannableStringBuilder);


        /**
         * 这里是会用HtmlView的setHtml方法,必须用修改的下面方法，不然没有效果
         * */
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ItemBankTitle);
        stringBuilder.append(scoreStr);
        //题目信息加得分信息
        Item_Bank_head_content.setHtml(stringBuilder.toString(), new HtmlHttpImageGetter(Item_Bank_head_content),
                true, new HtmlTagHandler.FillBlankInterface() {
                    @Override
                    public void addSpans(Editable output) {
                        String content = output.toString();
                        ForegroundColorSpan foregroundColorSpan = new ForegroundColorSpan(Color.parseColor("#ff4444"));
                        output.setSpan(foregroundColorSpan, ItemBankTitle.length(), content.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
                    }
                });

    }

    /**
     * 答案赋值
     */
    private void getAnswerInfo() {

        //判断答案显示
        if (questionBank.isShownAnswer() || !TextUtils.isEmpty(showAnswerDate)) {
            Item_Bank_Answer_Layout.setVisibility(VISIBLE);
        } else {
            Item_Bank_Answer_Layout.setVisibility(GONE);
            return;
        }

        //当前时间
        Date date = new Date();
        if (!TextUtils.isEmpty(showAnswerDate) && QZXTools.getDateValue(showAnswerDate, "yyyy-MM-dd HH:mm:ss")
                > date.getTime()) {
            //设置了显示时间但是还没到时间的话也不显示
            Item_Bank_Answer_Layout.setVisibility(GONE);
            return;
        } else if (!TextUtils.isEmpty(showAnswerDate) && QZXTools.getDateValue(showAnswerDate, "yyyy-MM-dd HH:mm:ss")
                <= date.getTime()) {
            //设置了时间且到了该时间
            Item_Bank_Answer_Layout.setVisibility(VISIBLE);
        }

        //我的答案
        if (questionBank.getQuestionChannelType() == Constant.ItemBank_Judge
                || questionBank.getQuestionChannelType() == Constant.Single_Choose
                || questionBank.getQuestionChannelType() == Constant.Multi_Choose) {

            if (questionBank.getSaveInfos() != null) {
                StringBuffer stringBuffer = new StringBuffer();
                for (TempSaveItemInfo tempSaveItemInfo : questionBank.getSaveInfos()) {
                    stringBuffer.append(tempSaveItemInfo.getKey());
                    stringBuffer.append(" ");
                }
                Item_Bank_my_Answer.setVisibility(VISIBLE);
                Item_Bank_my_Answer.setText("我的答案：" + stringBuffer.toString().trim());
            } else {
                Item_Bank_my_Answer.setVisibility(GONE);
            }
        }

        //正确答案
        if (TextUtils.isEmpty(questionBank.getAnswerText())) {
            Item_Bank_right_Answer.setVisibility(GONE);
        } else {
            Item_Bank_right_Answer.setVisibility(VISIBLE);
            //这里判断题要处理一下
            if (questionBank.getQuestionChannelType() ==  Constant.ItemBank_Judge){
                if (questionBank.getAnswerText().equals("正确")){
                    Item_Bank_right_Answer.setText("正确答案：" +"A");
                }else {
                    Item_Bank_right_Answer.setText("正确答案：" +"B");
                }

            }else {

                Item_Bank_right_Answer.setText("正确答案：" + questionBank.getAnswerText());
            }
        }

        //考点
        if (TextUtils.isEmpty(questionBank.getKnowledge())) {
            Item_Bank_Point.setVisibility(GONE);
        } else {
            Item_Bank_Point.setVisibility(VISIBLE);
            String pointUrl = UrlUtils.ImgBaseUrl + questionBank.getKnowledge();
            Glide.with(getContext()).load(pointUrl).into(Item_Bank_Img_Point);
        }

        //解析
        if (TextUtils.isEmpty(questionBank.getExplanation())) {
            Item_Bank_Analysis.setVisibility(GONE);
        } else {
            Item_Bank_Analysis.setVisibility(VISIBLE);
            String pointUrl = UrlUtils.ImgBaseUrl + questionBank.getExplanation();
            Glide.with(getContext()).load(pointUrl).into(Item_Bank_Img_Analysis);
        }

        //答案
        if (TextUtils.isEmpty(questionBank.getAnswer())) {
            Item_Bank_Answer.setVisibility(GONE);
        } else {
            Item_Bank_Answer.setVisibility(VISIBLE);
            String pointUrl = UrlUtils.ImgBaseUrl + questionBank.getAnswer();
            Glide.with(getContext()).load(pointUrl).into(Item_Bank_Img_Answer);
        }
    }

    /**
     * 批阅后可以显示答案
     */
    private void showResumeAnswer() {
        //显示答案list
   /*     if (Item_Bank_list_question_layout.getVisibility() == VISIBLE) {
            for (int i = 0; i < Item_Bank_list_question_layout.getChildCount(); i++) {
                NewKnowledgeToDoView newKnowledgeToDoView = (NewKnowledgeToDoView) Item_Bank_list_question_layout.getChildAt(i);

                if (newKnowledgeToDoView.getQuestionBank().isShownAnswer()) {
                    newKnowledgeToDoView.getQuestionBank().setShownAnswer(false);
                } else {
                    newKnowledgeToDoView.getQuestionBank().setShownAnswer(true);
                }

                newKnowledgeToDoView.getAnswerInfo();
            }
        }*/

        //显示答案
//        if (questionBank.isShownAnswer()) {
//            questionBank.setShownAnswer(false);
//        } else {
//            questionBank.setShownAnswer(true);
//        }
        getAnswerInfo();
    }

    /**
     * 需要传递的Bundle
     */
    private Bundle bundle;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    private OnCollectClickListener onCollectClickListener;

    public interface OnCollectClickListener {
        void OnCollectClickListener(QuestionBank questionBank,int curPosition);
    }

    public void setOnCollectClickListener(OnCollectClickListener onCollectClickListener) {
        this.onCollectClickListener = onCollectClickListener;
    }

    @Override
    protected void onAttachedToWindow() {
//        QZXTools.logE("onAttachedToWindow", null);
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
//        QZXTools.logE("onDetachedFromWindow", null);
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }


    /**
     * 添加订阅者   画板保存回调这里存粹保存整个画板位图，没有做其他处理，分辨率是平板分辨率，大小还可以(KB)
     */
    @Subscriber(tag = Constant.Subjective_Board_Callback, mode = ThreadMode.MAIN)
    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
        QZXTools.logE("Bank fromBoardCallback ExtraInfoBean=" + extraInfoBean + ";id=" , null);

        if (newKnowledgeAdapter!=null){
            newKnowledgeAdapter.fromBoardCallback(extraInfoBean);
        }

    }

    /**
     * 添加订阅者   相机保存回调，注意这里纯粹调用相机拍照存储原图，没有剪裁压缩比较大(MB)
     * <p>
     * 注意：需要压缩处理但是不考虑剪裁，因为呈现的大小问题
     * <p>
     * 问题：多个主观题连在一起的话拍照的这个list有问题
     */
    @Subscriber(tag = Constant.Subjective_Camera_Callback, mode = ThreadMode.MAIN)
    public void fromCameraCallback(String flag) {
        if (flag.equals("CAMERA_CALLBACK")) {
            QZXTools.logE("fromCameraCallback filePath=" , null);
            if (newKnowledgeAdapter!=null){
                newKnowledgeAdapter.fromCameraCallback(flag);
            }
        }
    }
}
