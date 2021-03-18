package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Activity.MistakesCollection.MistakesImproveActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.PerfectAnswerActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.TempSaveItemInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import org.json.JSONException;
import org.json.JSONObject;
import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTagHandler;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private LinearLayout Item_Bank_options_layout;

    /**
     * List的题型
     */
    private LinearLayout Item_Bank_list_question_layout;

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

    private ImageView iv_collect;

    private TextView Item_Bank_Show_Remark;

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

    /**
     * 设置题型数据
     *
     * @param questionBank 题库详细题型内容
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
        Item_Bank_head_title.setTypeface(typeface);
        Item_Bank_head_content.setTypeface(typeface);
//        Item_Bank_head_score.setTypeface(typeface);
        Item_Bank_head_promote.setTypeface(typeface);


        Item_Bank_options_layout = findViewById(R.id.Item_Bank_options_layout);
        Item_Bank_list_question_layout = findViewById(R.id.Item_Bank_list_question_layout);

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

        Item_Bank_head_good_answer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), PerfectAnswerActivity.class);
                intent.putExtra("questionId", questionBank.getQuestionId() + "");
                intent.putExtra("homeworkId", questionBank.getHomeworkId());
                getContext().startActivity(intent);
            }
        });
    }

    private void initData() {
        Item_Bank_options_layout.removeAllViews();
        Item_Bank_list_question_layout.removeAllViews();

        if (isMistaken && bundle != null && !TextUtils.isEmpty(bundle.getString("knowledge_json"))) {
            Item_Bank_head_promote.setVisibility(VISIBLE);
        } else {
            Item_Bank_head_promote.setVisibility(INVISIBLE);
        }

        //优秀答案只有主观题才能看见
        Item_Bank_head_good_answer.setVisibility(INVISIBLE);

        //是否显示教师批阅
        if (questionBank.getTeaDescFile() == null || questionBank.getTeaDescFile().size() <= 0) {
            Item_Bank_Show_Remark.setVisibility(GONE);
        } else {
            Item_Bank_Show_Remark.setVisibility(VISIBLE);
        }

        getHeadAndOptionsInfo();

        if ("2".equals(status) || !TextUtils.isEmpty(showAnswerDate)) {
            showResumeAnswer();
        }

        if ("0".equals(status)) {
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
        //是否存在List
//        QZXTools.logE("List=" + questionBank.getList(), null);
        if (TextUtils.isEmpty(questionBank.getList()) || questionBank.getList().equals("NULL")) {
            //放在Item_Bank_options_layout下面
            LayoutParams layoutParams = (LayoutParams) Item_Bank_Answer_Scroll.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, Item_Bank_options_layout.getId());

            Item_Bank_options_layout.setVisibility(VISIBLE);
            Item_Bank_list_question_layout.setVisibility(GONE);

            String optionJson = questionBank.getAnswerOptions();
            //题型信息
            switch (questionBank.getQuestionChannelType()) {
                case Constant.Single_Choose:
                case Constant.Judge_Item:
                    if (questionBank.getQuestionChannelType() == Constant.Judge_Item) {
                        Item_Bank_head_title.setText((curPosition + 1) + "、[判断题]");
                    } else {
                        Item_Bank_head_title.setText((curPosition + 1) + "、[单选题]");
                    }

                    if (status.equals(Constant.Todo_Status) || status.equals(Constant.Retry_Status)) {
                        //解析选项
                        if (!TextUtils.isEmpty(optionJson)) {
                            Gson gson = new Gson();
                            Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                            }.getType());
                            Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                            while (iterator.hasNext()) {
                                Map.Entry<String, String> entry = iterator.next();

                                //查询保存的答案
                                LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                        .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                                LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                                LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

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

                                        //当前选中的下标
                                        int selectedIndex = Item_Bank_options_layout.indexOfChild(selectedView);

                                        //-------------------------答案保存，依据作业题目id
                                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                                        localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                                        localTextAnswersBean.setQuestionId(questionBank.getId() + "");
                                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                                        localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                                        List<AnswerItem> answerItems = new ArrayList<>();
                                        AnswerItem answerItem = new AnswerItem();
                                        answerItem.setContent(entry.getKey());
                                        answerItems.add(answerItem);
                                        localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                                        //插入或者更新数据库
                                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                                        //-------------------------答案保存，依据作业题目id
                                    }
                                });
                                judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());
                                //如果保存过答案回显--------------只保存答案的选项，依据答案选项判断
                                if (localTextAnswersBean != null) {
//                            QZXTools.logE("Answer localTextAnswersBean=" + localTextAnswersBean, null);
                                    List<AnswerItem> answerItems = localTextAnswersBean.getList();
                                    for (AnswerItem answerItem : answerItems) {
                                        if (entry.getKey().equals(answerItem.getContent())) {
                                            judgeSelectToDoView.handSelectedStatus();
                                        }
                                    }
                                }
                                Item_Bank_options_layout.addView(judgeSelectToDoView);
                            }
                        }
                    } else {
                        //做完题仅仅展示
                        if (!TextUtils.isEmpty(optionJson)) {
                            Gson gson = new Gson();
                            Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                            }.getType());
                            Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                            while (iterator.hasNext()) {
                                Map.Entry<String, String> entry = iterator.next();

                                if (questionBank.getOwnList() != null && questionBank.getOwnList().size() > 0) {
                                    JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                                    judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());
                                    //这个答案是selectBeans.get(i).getOptions()
                                    String myAnswer = questionBank.getOwnList().get(0).getAnswerContent();
                                    if (myAnswer.equals(entry.getKey())) {
                                        judgeSelectToDoView.handSelectedStatus();
                                    }
                                    Item_Bank_options_layout.addView(judgeSelectToDoView);
                                } else {
                                    //查询保存的答案
                                    LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                                    JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                                    judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());

                                    //如果保存过答案回显--------------只保存答案的选项，依据答案选项判断
                                    if (localTextAnswersBean != null) {
//                            QZXTools.logE("Answer localTextAnswersBean=" + localTextAnswersBean, null);
                                        List<AnswerItem> answerItems = localTextAnswersBean.getList();
                                        for (AnswerItem answerItem : answerItems) {
                                            if (entry.getKey().equals(answerItem.getContent())) {
                                                judgeSelectToDoView.handSelectedStatus();
                                            }
                                        }
                                    }
                                    Item_Bank_options_layout.addView(judgeSelectToDoView);
                                }
                            }
                        }
                    }
                    break;
                case Constant.Multi_Choose:
                    Item_Bank_head_title.setText((curPosition + 1) + "、[多选题]");

                    if (status.equals(Constant.Todo_Status)) {

                        //解析选项
                        if (!TextUtils.isEmpty(optionJson)) {
                            Gson gson = new Gson();
                            Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                            }.getType());

                            //查询保存的答案,这是多选，所以存在多个答案
                            LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                            LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                            LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                            Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                            while (iterator.hasNext()) {

                                Map.Entry<String, String> entry = iterator.next();
//                                QZXTools.logE("Multi_Choose key=" + entry.getKey() + ";value=" + entry.getValue(), null);

                                saveMultiList.add(entry.getKey());

                                JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                                judgeSelectToDoView.setOnClickListener(new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        JudgeSelectToDoView selectedView = (JudgeSelectToDoView) v;

                                        selectedView.handSelectedStatus();

                                        //-------------------------答案保存，依据作业题目id
                                        //选中或者取消
                                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                                        localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                                        localTextAnswersBean.setQuestionId(questionBank.getId() + "");
                                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                                        localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                                        List<AnswerItem> answerItems = new ArrayList<>();
                                        for (int j = 0; j < Item_Bank_options_layout.getChildCount(); j++) {
                                            //是否选中
                                            if (Item_Bank_options_layout.getChildAt(j).isSelected()) {
                                                AnswerItem answerItem = new AnswerItem();
                                                answerItem.setContent(saveMultiList.get(j));
                                                answerItems.add(answerItem);
                                            }
                                        }

//                                        QZXTools.logE("save multi choose answerItems=" + answerItems, null);

                                        localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                                        //插入或者更新数据库
                                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                                        //-------------------------答案保存，依据作业题目id

                                    }
                                });
                                judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());
                                //如果保存过答案回显
                                if (localTextAnswersBean != null) {
//                            QZXTools.logE("Answer localTextAnswersBean=" + localTextAnswersBean, null);
                                    List<AnswerItem> answerItems = localTextAnswersBean.getList();
                                    for (AnswerItem answerItem : answerItems) {
                                        if (entry.getKey().equals(answerItem.getContent())) {
                                            judgeSelectToDoView.handSelectedStatus();
                                        }
                                    }
                                }
                                Item_Bank_options_layout.addView(judgeSelectToDoView);
                            }
                        }

                    } else {

                        //解析选项
                        if (!TextUtils.isEmpty(optionJson)) {
                            Gson gson = new Gson();
                            Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                            }.getType());
                            Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                            while (iterator.hasNext()) {
                                Map.Entry<String, String> entry = iterator.next();

                                if (questionBank.getOwnList() != null && questionBank.getOwnList().size() > 0) {
                                    JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                                    judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());
                                    //这个答案是selectBeans.get(i).getOptions()
                                    List<WorkOwnResult> myAnswers = questionBank.getOwnList();
                                    for (WorkOwnResult myAnswer : myAnswers) {
                                        if ((myAnswer.getAnswerContent()).equals(entry.getKey())) {
                                            judgeSelectToDoView.handSelectedStatus();
                                        }
                                    }
                                    Item_Bank_options_layout.addView(judgeSelectToDoView);
                                } else {
                                    //查询保存的答案,这是多选，所以存在多个答案
                                    LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                                    JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                                    judgeSelectToDoView.fillOptionAndContent(entry.getKey(), entry.getValue());

                                    //如果保存过答案回显
                                    if (localTextAnswersBean != null) {
//                            QZXTools.logE("Answer localTextAnswersBean=" + localTextAnswersBean, null);
                                        List<AnswerItem> answerItems = localTextAnswersBean.getList();
                                        for (AnswerItem answerItem : answerItems) {
                                            if (entry.getKey().equals(answerItem.getContent())) {
                                                judgeSelectToDoView.handSelectedStatus();
                                            }
                                        }
                                    }
                                    Item_Bank_options_layout.addView(judgeSelectToDoView);
                                }
                            }
                        }

                    }

                    break;
                case Constant.Fill_Blank:
                    Item_Bank_head_title.setText((curPosition + 1) + "、[填空题]");


                    if (status.equals(Constant.Todo_Status)) {

                        //查询保存的答案,这是多选，所以存在多个答案
                        LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        String ItemBankTitle = questionBank.getQuestionText();
                        //使用"^__\\d+__$"不行
                        String reg = "__\\d+__";
                        Pattern pattern = Pattern.compile(reg);
                        Matcher matcher = pattern.matcher(ItemBankTitle);

                        int i = -1;
                        while (matcher.find()) {
                            i++;

                            //塞入填空题数据
                            FillBlankToDoView fillBlankToDoView = new FillBlankToDoView(getContext());

                            //这里不同：保存写的答案痕迹在视图中进行
                            fillBlankToDoView.fillDatas(i);
                            //如果保存过答案回显
                            if (localTextAnswersBean != null) {
//                            QZXTools.logE("fill blank Answer localTextAnswersBean=" + localTextAnswersBean, null);
                                List<AnswerItem> answerItems = localTextAnswersBean.getList();
                                for (int k = 0; k < answerItems.size(); k++) {
//                                    String content = answerItem.getContent();
//                                    String[] splitContent = content.split(":");
//                                    //一对一
//                                    if (Integer.parseInt(splitContent[0]) == i) {
//                                        //因为如果没有填写则为(数字+冒号 )后面是空白的情况
//                                        if (splitContent.length > 1) {
//                                            //纯粹的显示填空题的已填写过的答案痕迹
//                                            fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), splitContent[1]);
//                                        } else {
//                                            fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), "");
//                                        }
//                                    }

                                    if ((i + 1) == Integer.parseInt(answerItems.get(k).getBlanknum())) {
                                        //因为填空题增信了字段，所以修改
                                        String content = answerItems.get(k).getContent();
                                        String blankNum = answerItems.get(k).getBlanknum();

                                        if (content == null) {
                                            fillBlankToDoView.fillDatas(Integer.parseInt(blankNum) - 1, "");
                                        } else {
                                            fillBlankToDoView.fillDatas(Integer.parseInt(blankNum) - 1, content);
                                        }

                                        break;
                                    }
                                }
                            }

                            //问题点：如果设置文本改变监听在答案回显前面的话会触发
                            fillBlankToDoView.fill_blank_content.addTextChangedListener(new TextWatcher() {
                                @Override
                                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                                }

                                @Override
                                public void onTextChanged(CharSequence s, int start, int before, int count) {

                                }

                                @Override
                                public void afterTextChanged(Editable s) {
                                    //文本改变后实时保存
                                    //-------------------------答案保存，依据作业题目id
                                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                                    localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                                    localTextAnswersBean.setQuestionId(questionBank.getId() + "");
                                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                                    localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                                    List<AnswerItem> answerItems = new ArrayList<>();
                                    for (int j = 0; j < Item_Bank_options_layout.getChildCount(); j++) {
                                        //以及遍历的选项布局获取子类
                                        FillBlankToDoView blankedView = (FillBlankToDoView) Item_Bank_options_layout.getChildAt(j);
                                        AnswerItem answerItem = new AnswerItem();
                                        //保存文本内容:采用index:content形式,blanknum从1开始，因为从零开始服务端拼写有问题
                                        answerItem.setBlanknum((j + 1) + "");
                                        answerItem.setContent(blankedView.fill_blank_content.getText().toString().trim());
                                        //填空题，新增一个字段
//                                        answerItem.setContent(j + ":" + blankedView.fill_blank_content.getText().toString().trim());
                                        answerItems.add(answerItem);
                                    }
                                    localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                                    //插入或者更新数据库
                                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                                    //-------------------------答案保存，依据作业题目id
                                }
                            });
                            Item_Bank_options_layout.addView(fillBlankToDoView);
                        }

                    } else {

                        if (questionBank.getOwnList() != null && questionBank.getOwnList().size() > 0) {

                            //一个方法是转成一个对象，这样依据key取值，可是太难办了，每一个都是对象
                            List<WorkOwnResult> myAnswers = questionBank.getOwnList();


                            StringBuilder stringBuilder = new StringBuilder();

                            for (int i = 0; i < myAnswers.size(); i++) {
                                try {
                                    JSONObject jsonObject1 = new JSONObject(myAnswers.get(i).getAnswerContent());
                                    String s = jsonObject1.toString();
                                    // {"2":"唱个歌"}
                                    if (i > 0) {
                                        s = s.replace("{", "");
                                    }

                                    if (i < (myAnswers.size() - 1)) {
                                        s = s.replace("}", ",");
                                    }
                                    QZXTools.logE("s=" + s, null);

                                    stringBuilder.append(s);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }

                            try {
                                JSONObject jsonObject = new JSONObject(stringBuilder.toString());

                                String ItemBankTitle = questionBank.getQuestionText();
                                //使用"^__\\d+__$"不行
                                String reg = "__\\d+__";
                                Pattern pattern = Pattern.compile(reg);
                                Matcher matcher = pattern.matcher(ItemBankTitle);

                                int i = -1;
                                while (matcher.find()) {
                                    i++;

                                    //塞入填空题数据
                                    FillBlankToDoView fillBlankToDoView = new FillBlankToDoView(getContext());
                                    fillBlankToDoView.setNormalTV();

                                    //如果自己写的答案就填，没有就是空白
                                    if (i < myAnswers.size()) {
                                        //注意json从一开始
                                        fillBlankToDoView.fillDatas(i, jsonObject.getString((i + 1) + ""));
                                    } else {
                                        fillBlankToDoView.fillDatas(i);
                                    }

                                    Item_Bank_options_layout.addView(fillBlankToDoView);
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        } else {

                            //查询保存的答案,这是多选，所以存在多个答案
                            LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                            LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                            LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                            String ItemBankTitle = questionBank.getQuestionText();
                            //使用"^__\\d+__$"不行
                            String reg = "__\\d+__";
                            Pattern pattern = Pattern.compile(reg);
                            Matcher matcher = pattern.matcher(ItemBankTitle);

                            int i = -1;
                            while (matcher.find()) {
                                i++;

                                //塞入填空题数据
                                FillBlankToDoView fillBlankToDoView = new FillBlankToDoView(getContext());
                                fillBlankToDoView.setNormalTV();

                                //这里不同：保存写的答案痕迹在视图中进行
                                fillBlankToDoView.fillDatas(i);
                                //如果保存过答案回显
                                if (localTextAnswersBean != null) {
//                            QZXTools.logE("fill blank Answer localTextAnswersBean=" + localTextAnswersBean, null);
                                    List<AnswerItem> answerItems = localTextAnswersBean.getList();
                                    for (int k = 0; k < answerItems.size(); k++) {
//                                        String content = answerItem.getContent();
//                                        String[] splitContent = content.split(":");
//                                        //一对一
//                                        if (Integer.parseInt(splitContent[0]) == i) {
//                                            //因为如果没有填写则为(数字+冒号 )后面是空白的情况
//                                            if (splitContent.length > 1) {
//                                                //纯粹的显示填空题的已填写过的答案痕迹
//                                                fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), splitContent[1]);
//                                            } else {
//                                                fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), "");
//                                            }
//                                        }

                                        if ((i + 1) == Integer.parseInt(answerItems.get(k).getBlanknum())) {
                                            //因为填空题增信了字段，所以修改
                                            String content = answerItems.get(k).getContent();
                                            String blankNum = answerItems.get(k).getBlanknum();

                                            if (content == null) {
                                                fillBlankToDoView.fillDatas(Integer.parseInt(blankNum), "");
                                            } else {
                                                fillBlankToDoView.fillDatas(Integer.parseInt(blankNum), content);
                                            }

                                            break;
                                        }
                                    }
                                }
                                Item_Bank_options_layout.addView(fillBlankToDoView);
                            }

                        }

                    }
                    break;
                case Constant.Subject_Item:

                    if (status.equals(Constant.Todo_Status)) {

                        Item_Bank_head_title.setText((curPosition + 1) + "、[主观题]");

                        //查询保存的答案,这是多选，所以存在多个答案
                        LocalTextAnswersBean localTextAnswersBean_sub = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        //塞入主观题数据
                        BankSubjectiveToDoView subjectiveToDoView = new BankSubjectiveToDoView(getContext());
                        subjectiveToDoView.setQuestionInfo(questionBank);
                        //答案回显,在方法内部判断localTextAnswersBean是否空指针
                        subjectiveToDoView.showImgsAndContent(localTextAnswersBean_sub);
                        //调整到最后
                        subjectiveToDoView.hideAnswerTools(false);

                        Item_Bank_options_layout.addView(subjectiveToDoView);

                    } else {

                        //优秀答案可见
                        Item_Bank_head_good_answer.setVisibility(VISIBLE);

                        Item_Bank_head_title.setText((curPosition + 1) + "、[主观题]");

                        //查询保存的答案,这是多选，所以存在多个答案
                        LocalTextAnswersBean localTextAnswersBean_sub = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        //塞入主观题数据
                        BankSubjectiveToDoView subjectiveToDoView = new BankSubjectiveToDoView(getContext());
                        subjectiveToDoView.hideAnswerTools(true);
                        subjectiveToDoView.setQuestionInfo(questionBank);
                        //答案回显,在方法内部判断localTextAnswersBean是否空指针
                        subjectiveToDoView.showImgsAndContent(localTextAnswersBean_sub);

                        Item_Bank_options_layout.addView(subjectiveToDoView);

                    }
                    break;
            }
        } else {
            //放在Item_Bank_list_question_layout下面
            LayoutParams layoutParams = (LayoutParams) Item_Bank_Answer_Scroll.getLayoutParams();
            layoutParams.addRule(RelativeLayout.BELOW, Item_Bank_list_question_layout.getId());

            Item_Bank_options_layout.setVisibility(GONE);
            Item_Bank_list_question_layout.setVisibility(VISIBLE);

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

            List<QuestionBank> questionBankList = questionBank.getQuestionBanks();
            for (int i = 0; i < questionBankList.size(); i++) {
                NewKnowledgeToDoView newKnowledgeToDoView = new NewKnowledgeToDoView(getContext());
                newKnowledgeToDoView.fillDatas(questionBankList.get(i), i, status, questionBank.getQuestionChannelType(),
                        questionBank.getHomeworkId());
                Item_Bank_list_question_layout.addView(newKnowledgeToDoView);
            }
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

    /**
     * 批阅后可以显示答案
     */
    private void showResumeAnswer() {
        //显示答案list
        if (Item_Bank_list_question_layout.getVisibility() == VISIBLE) {
            for (int i = 0; i < Item_Bank_list_question_layout.getChildCount(); i++) {
                NewKnowledgeToDoView newKnowledgeToDoView = (NewKnowledgeToDoView) Item_Bank_list_question_layout.getChildAt(i);

                if (newKnowledgeToDoView.getQuestionBank().isShownAnswer()) {
                    newKnowledgeToDoView.getQuestionBank().setShownAnswer(false);
                } else {
                    newKnowledgeToDoView.getQuestionBank().setShownAnswer(true);
                }

                newKnowledgeToDoView.getAnswerInfo();
            }
        }

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
}
