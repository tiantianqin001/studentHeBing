package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.QuestionView.external.ReplaceSpan;
import com.telit.zhkt_three.CustomView.QuestionView.external.SpansManager;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.TempSaveItemInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author: qzx
 * Date: 2019/5/19 10:38
 * <p>
 * QuestionBank类型---题库类型
 * 主要用于临时作答的痕迹保存
 * <p>
 * 新增题库选择题直接输入答题
 */
public class KnowledgeQuestionView extends RelativeLayout implements View.OnClickListener, ReplaceSpan.OnClickListener {

    //题库数据集
    private QuestionBank questionBank;

    /**
     * 当前的题目位置下标
     */
    private int curPosition;

    private EditText et_input;

    /**
     * 头部标题
     */
    private TextView Item_Bank_head_title;
    private HtmlTextView Item_Bank_head_content;

    /**
     * 无List的选项
     */
    private LinearLayout Item_Bank_options_layout;

    /**
     * List的题型
     */
    private LinearLayout Item_Bank_list_question_layout;

    /**
     * 答案
     */
    private TextView Item_Bank_tv_answer_show;
    private LinearLayout Item_Bank_Answer_Layout;
    private TextView Item_Bank_my_Answer;
    private TextView Item_Bank_right_Answer;
    private LinearLayout Item_Bank_Point;
    private ImageView Item_Bank_Img_Point;
    private LinearLayout Item_Bank_Analysis;
    private ImageView Item_Bank_Img_Analysis;
    private LinearLayout Item_Bank_Answer;
    private ImageView Item_Bank_Img_Answer;

    /**
     * 设置题型数据
     *
     * @param questionBank 题库详细题型内容
     */
    public void setQuestionInfo(QuestionBank questionBank, int curPosition) {
        this.questionBank = questionBank;
        this.curPosition = curPosition;
        initData();
    }

    public KnowledgeQuestionView(Context context) {
        this(context, null);
    }

    public KnowledgeQuestionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KnowledgeQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.item_bank_view_layout, this, true);
        //注意这里不可以初始化数据，因为没有数据，questionInfo是空的，需要通过set方法塞入
        initView();
    }

    private void initView() {
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "PingFang-SimpleBold.ttf");

        et_input = findViewById(R.id.et_input);
        et_input.setTypeface(typeface);

        Item_Bank_head_title = findViewById(R.id.Item_Bank_head_title);
        Item_Bank_head_content = findViewById(R.id.Item_Bank_head_content);
        Item_Bank_head_title.setTypeface(typeface);
        Item_Bank_head_content.setTypeface(typeface);

        Item_Bank_options_layout = findViewById(R.id.Item_Bank_options_layout);
        Item_Bank_list_question_layout = findViewById(R.id.Item_Bank_list_question_layout);

        Item_Bank_tv_answer_show = findViewById(R.id.Item_Bank_tv_answer_show);
        Item_Bank_tv_answer_show.setTypeface(typeface);

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

        TextView Item_Bank_Tv_Point = findViewById(R.id.Item_Bank_Tv_Point);
        TextView Item_Bank_Tv_Analysis = findViewById(R.id.Item_Bank_Tv_Analysis);
        TextView Item_Bank_Tv_Answer = findViewById(R.id.Item_Bank_Tv_Answer);
        Item_Bank_Tv_Point.setTypeface(typeface);
        Item_Bank_Tv_Analysis.setTypeface(typeface);
        Item_Bank_Tv_Answer.setTypeface(typeface);


        Item_Bank_tv_answer_show.setOnClickListener(this);

        //显示答案按钮
        Item_Bank_tv_answer_show.setVisibility(VISIBLE);


    }

    /**
     * {
     * "id": 80287,
     * "ids": null,
     * "xd": 1,
     * "chid": 0,
     * "questionId": 10017599,
     * "questionType": null,
     * "questionChannelType": 2,
     * "questionTypeName": null,
     * "title": "",
     * "questionText": "把下面的句子按顺序排好，在横线上标上序号\n\n<p>{#blank#}1{#/blank#}最后尾巴变短，变成了小青蛙。</p>\n\n<p>{#blank#}2{#/blank#}小蝌蚪有个大脑袋，黑灰色的身子，一条长长的尾巴。</p>\n\n<p>{#blank#}3{#/blank#}然后又长出了两条前腿。</p>\n\n<p>{#blank#}4{#/blank#}它先长出了两条后腿。</p>",
     * "answerOptions": "",
     * "answerJson": "['', '', '', '']",
     * "answer": "1/2/4/answer/56ba5d8815f496a32f72cc93bdf9_10017599an.png",
     * "explanation": "",
     * "knowledge": "1/2/4/knowledge/56ba5d8815f496a32f72cc93bdf9_10017599kn.png",
     * "tKnowledge": "[{'tid': '4602', 'name': '基础知识'}, {'tid': '18488', 'name': '课文库'}, {'tid': '4617', 'name': '课文内容理解'}]",
     * "tKnowledgeIds": null,
     * "category": null,
     * "parentId": null,
     * "paperId": null,
     * "examType": null,
     * "examName": null,
     * "difficultIndex": null,
     * "difficultName": "容易",
     * "isObjective": null,
     * "isCollect": null,
     * "extraFile": null,
     * "saveNum": 5,
     * "newFlag": null,
     * "isUse": 0,
     * "questionSource": "0",
     * "list": "NULL",
     * "questionBanks": null,
     * "chapterIds": null,
     * "gradeId": 2,
     * "sortType": null,
     * "answerText": null
     * }
     */
    private void initData() {
        Item_Bank_options_layout.removeAllViews();
        Item_Bank_list_question_layout.removeAllViews();

        getHeadAndOptionsInfo();
        getAnswerInfo();
    }

    /**
     * 头部数据化以及选项栏
     */
    private void getHeadAndOptionsInfo() {
        if (TextUtils.isEmpty(questionBank.getList()) || questionBank.getList().equals("NULL")) {
            // --------------没有子List
            Item_Bank_options_layout.setVisibility(VISIBLE);
            Item_Bank_list_question_layout.setVisibility(GONE);
            ((LayoutParams) Item_Bank_tv_answer_show.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.Item_Bank_options_layout);

            String optionJson = questionBank.getAnswerOptions();
            //题型信息
            switch (questionBank.getQuestionChannelType()) {
                case Constant.Single_Choose:
                case Constant.Judge_Item:
                    if (questionBank.getQuestionChannelType() == Constant.ItemBank_Judge) {
                        Item_Bank_head_title.setText((curPosition + 1) + "、[判断题]");
                    } else {
                        Item_Bank_head_title.setText((curPosition + 1) + "、[单选题]");
                    }

                    //解析选项
                    if (!TextUtils.isEmpty(optionJson)) {
                        Gson gson = new Gson();

                        QZXTools.logE("optionJson" + optionJson, null);

                        Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();

                            JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                            judgeSelectToDoView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //因为是单选取消之前选中的
                                    for (int i = 0; i < Item_Bank_options_layout.getChildCount(); i++) {
                                        JudgeSelectToDoView childJudeSelectedToDoView = (JudgeSelectToDoView) Item_Bank_options_layout.getChildAt(i);
                                        if (childJudeSelectedToDoView.isSelected()) {
                                            childJudeSelectedToDoView.handSelectedStatus();
                                        }
                                    }

                                    JudgeSelectToDoView selectedView = (JudgeSelectToDoView) v;

                                    selectedView.handSelectedStatus();


                                    //保存当前的痕迹
                                    int selectedIndex = Item_Bank_options_layout.indexOfChild(selectedView);  //当前选中的下标
                                    List<TempSaveItemInfo> infoList = new ArrayList<>();
                                    TempSaveItemInfo tempSaveItemInfo = new TempSaveItemInfo();
                                    tempSaveItemInfo.setIndex(selectedIndex);
                                    tempSaveItemInfo.setKey(selectedView.getMyAnswer());
                                    infoList.add(tempSaveItemInfo);
                                    questionBank.setSaveInfos(infoList);

                                }
                            });
                            judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());
                            //刻入痕迹，我的答案
                            if (questionBank.getSaveInfos() != null) {
                                TempSaveItemInfo tempSaveItemInfo = questionBank.getSaveInfos().get(0);
                                if (tempSaveItemInfo.getKey().equals(entry.getKey())) {
                                    judgeSelectToDoView.handSelectedStatus();
                                }
                            }
                            Item_Bank_options_layout.addView(judgeSelectToDoView);
                        }
                    }
                    break;
                case Constant.Multi_Choose:
                    Item_Bank_head_title.setText((curPosition + 1) + "、[多选题]");

                    //解析选项
                    if (!TextUtils.isEmpty(optionJson)) {
                        Gson gson = new Gson();
                        Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();

                            JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                            judgeSelectToDoView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    JudgeSelectToDoView selectedView = (JudgeSelectToDoView) v;

                                    selectedView.handSelectedStatus();

                                    //保存当前的痕迹
                                    List<TempSaveItemInfo> infoList = new ArrayList<>();
                                    for (int j = 0; j < Item_Bank_options_layout.getChildCount(); j++) {
                                        //是否选中
                                        if (Item_Bank_options_layout.getChildAt(j).isSelected()) {
                                            JudgeSelectToDoView todoView = (JudgeSelectToDoView) Item_Bank_options_layout.getChildAt(j);
                                            TempSaveItemInfo tempSaveItemInfo = new TempSaveItemInfo();
                                            tempSaveItemInfo.setIndex(j);
                                            tempSaveItemInfo.setKey(todoView.getMyAnswer());
                                            infoList.add(tempSaveItemInfo);
                                        }
                                    }
                                    questionBank.setSaveInfos(infoList);
                                }
                            });
                            judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());
                            //刻入痕迹，我的答案
                            if (questionBank.getSaveInfos() != null) {
                                List<TempSaveItemInfo> tempSaveItemInfos = questionBank.getSaveInfos();
                                for (TempSaveItemInfo tempSaveItemInfo : tempSaveItemInfos) {
                                    if (tempSaveItemInfo.getKey().equals(entry.getKey())) {
                                        judgeSelectToDoView.handSelectedStatus();
                                    }
                                }
                            }
                            Item_Bank_options_layout.addView(judgeSelectToDoView);
                        }
                    }
                    break;
                case Constant.Fill_Blank:
                    Item_Bank_head_title.setText((curPosition + 1) + "、[填空题]");
                    break;
                case Constant.Subject_Item:
                    Item_Bank_head_title.setText((curPosition + 1) + "、[主观题]");
                    break;
            }
        } else {
            // --------------处理子list
            Item_Bank_options_layout.setVisibility(GONE);
            Item_Bank_list_question_layout.setVisibility(VISIBLE);
            ((LayoutParams) Item_Bank_tv_answer_show.getLayoutParams()).addRule(RelativeLayout.BELOW, R.id.Item_Bank_list_question_layout);

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
                    break;
                case Constant.Judge_Item:
                    Item_Bank_head_title.setText((curPosition + 1) + "、[判断题]");
                    break;
            }

//            String ListJson = questionBank.getList();
//            //注意这里的ListJson字符串要用fastjson解析，因为属性名时小驼峰格式，bug存在NULL解析有问题
//            List<QuestionBank> questionBankList = JSONObject.parseArray(ListJson, QuestionBank.class);
            List<QuestionBank> questionBankList = questionBank.getQuestionBanks();
            for (int i = 0; i < questionBankList.size(); i++) {
                KnowledgeToDoView knowledgeToDoView = new KnowledgeToDoView(getContext());
                knowledgeToDoView.fillDatas(questionBankList.get(i), i, questionBank.getQuestionChannelType());
                Item_Bank_list_question_layout.addView(knowledgeToDoView);

                //可以用，不过改的也多
//                KnowledgeQuestionView knowledgeQuestionView = new KnowledgeQuestionView(getContext());
//                knowledgeQuestionView.setQuestionInfo(questionBankList.get(i), i);
//                Item_Bank_list_question_layout.addView(knowledgeQuestionView);

            }
        }
        //题目信息
        String ItemBankTitle = questionBank.getQuestionText();
//        ItemBankTitle = ItemBankTitle.replaceAll("\\{#blank#\\}\\d*\\{#/blank#\\}", "____");
        ItemBankTitle = ItemBankTitle.replaceAll("__\\d+__", "____,");

        //单独设置题库填空题
        if (questionBank.getQuestionChannelType() == Constant.Fill_Blank) {
            mSpansManager = new SpansManager(this, Item_Bank_head_content, et_input);
            mSpansManager.doFillBlank(ItemBankTitle);
        } else {
            Item_Bank_head_content.setHtml(ItemBankTitle, new HtmlHttpImageGetter(Item_Bank_head_content));
        }
    }

    /**
     * 答案赋值
     */
    private void getAnswerInfo() {

        //判断答案显示
        if (questionBank.isShownAnswer()) {
            Item_Bank_Answer_Layout.setVisibility(VISIBLE);
        } else {
            Item_Bank_Answer_Layout.setVisibility(GONE);
        }

        //我的答案:判断、单选和多选
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
            Item_Bank_right_Answer.setText("正确答案：" + questionBank.getAnswerText());
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.Item_Bank_tv_answer_show:
                //显示答案list
                if (Item_Bank_list_question_layout.getVisibility() == VISIBLE) {
                    for (int i = 0; i < Item_Bank_list_question_layout.getChildCount(); i++) {
                        KnowledgeToDoView knowledgeToDoView = (KnowledgeToDoView) Item_Bank_list_question_layout.getChildAt(i);

                        if (knowledgeToDoView.getQuestionBank().isShownAnswer()) {
                            knowledgeToDoView.getQuestionBank().setShownAnswer(false);
                        } else {
                            knowledgeToDoView.getQuestionBank().setShownAnswer(true);
                        }

                        knowledgeToDoView.getAnswerInfo();
                    }
                }

                //显示答案
                if (questionBank.isShownAnswer()) {
                    questionBank.setShownAnswer(false);

                } else {
                    questionBank.setShownAnswer(true);
                }
                getAnswerInfo();
                break;
        }
    }

    private SpansManager mSpansManager;

    /**
     * 填空题点击响应事件
     */
    @Override
    public void OnClick(TextView v, int id, ReplaceSpan span) {
        Log.e("zbv", "id=" + id);
        mSpansManager.setData(et_input.getText().toString(), null, mSpansManager.mOldSpan);
        mSpansManager.mOldSpan = id;
        //如果当前span身上有值，先赋值给et身上
        et_input.setText(TextUtils.isEmpty(span.mText) ? "" : span.mText);
        et_input.setSelection(span.mText.length());
        span.mText = "";
        //通过rf计算出et当前应该显示的位置
        RectF rf = mSpansManager.drawSpanRect(span);
        //设置EditText填空题中的相对位置
        mSpansManager.setEtXY(rf);
        mSpansManager.setSpanChecked(id);
    }

    private String getMyAnswerStr() {
        mSpansManager.setLastCheckedSpanText(et_input.getText().toString());
        return mSpansManager.getMyAnswer().toString();
    }
}
