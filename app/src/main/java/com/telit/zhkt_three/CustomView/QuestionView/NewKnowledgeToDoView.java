package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.TempSaveItemInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * author: qzx
 * Date: 2019/6/15 17:00
 */
public class NewKnowledgeToDoView extends RelativeLayout {
    private HtmlTextView Item_Bank_list_title_content;
    private LinearLayout Item_Bank_list_options_layout;

    /**
     * 答案
     */
    private LinearLayout Item_Bank_List_Answer_Layout;
    private TextView Item_Bank_List_my_Answer;
    private TextView Item_Bank_List_right_Answer;
    private LinearLayout Item_Bank_List_Answer;
    private ImageView Item_Bank_List_Img_Answer;

    private QuestionBank questionBank;

    public QuestionBank getQuestionBank() {
        return questionBank;
    }

    public NewKnowledgeToDoView(Context context) {
        this(context, null);
    }

    public NewKnowledgeToDoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NewKnowledgeToDoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.view_knowledge_todo, this, true);
        Item_Bank_list_title_content = findViewById(R.id.Item_Bank_list_title_content);
        Item_Bank_list_options_layout = findViewById(R.id.Item_Bank_list_options_layout);
        Item_Bank_List_Answer_Layout = findViewById(R.id.Item_Bank_List_Answer_Layout);
        Item_Bank_List_my_Answer = findViewById(R.id.Item_Bank_List_my_Answer);
        Item_Bank_List_right_Answer = findViewById(R.id.Item_Bank_List_right_Answer);
        Item_Bank_List_Answer = findViewById(R.id.Item_Bank_List_Answer);
        Item_Bank_List_Img_Answer = findViewById(R.id.Item_Bank_List_Img_Answer);

        TextView Item_Bank_List_Tv_Answer = findViewById(R.id.Item_Bank_List_Tv_Answer);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "PingFang-SimpleBold.ttf");
        Item_Bank_list_title_content.setTypeface(typeface);
        Item_Bank_List_my_Answer.setTypeface(typeface);
        Item_Bank_List_right_Answer.setTypeface(typeface);

        Item_Bank_List_Tv_Answer.setTypeface(typeface);
    }

    private String homeworkId;

    /**
     * 头部数据化以及选项栏
     * 新增传入来自父类型的type
     */
    public void fillDatas(QuestionBank questionBank, int curPosition, String status, int type, String homeworkId) {
        this.homeworkId = homeworkId;
        this.questionBank = questionBank;

        if (questionBank.getHomeworkId() == null) {
            questionBank.setHomeworkId(homeworkId);
        }

        //题目信息
        String ItemBankTitle = questionBank.getQuestionText();
        Item_Bank_list_title_content.setHtml("(" + (curPosition + 1) + ")" + ItemBankTitle, new HtmlHttpImageGetter(Item_Bank_list_title_content));

        String optionJson = questionBank.getAnswerOptions();

        //题型信息
        switch (type) {
            case Constant.Single_Choose:
            case Constant.Judge_Item:
                if (status.equals(Constant.Todo_Status)) {
                    //解析选项
                    if (!TextUtils.isEmpty(optionJson)) {
                        Gson gson = new Gson();
                        Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();

                            //修改为通过作业ID以及题目ID联合条件
                            //查询保存的答案---List中的小题id为空,使用questionId代替
                            LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                            LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                            LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                            JudgeSelectToDoView judgeSelectToDoView = new JudgeSelectToDoView(getContext());
                            judgeSelectToDoView.setOnClickListener(new OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    //因为是单选取消之前选中的
                                    for (int i = 0; i < Item_Bank_list_options_layout.getChildCount(); i++) {
                                        JudgeSelectToDoView childJudeSelectedToDoView = (JudgeSelectToDoView) Item_Bank_list_options_layout.getChildAt(i);
                                        if (childJudeSelectedToDoView.isSelected()) {
                                            childJudeSelectedToDoView.handSelectedStatus();
                                        }
                                    }

                                    JudgeSelectToDoView selectedView = (JudgeSelectToDoView) v;

                                    selectedView.handSelectedStatus();

                                    //当前选中的下标
                                    int selectedIndex = Item_Bank_list_options_layout.indexOfChild(selectedView);

                                    //-------------------------答案保存，依据作业题目id
                                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                                    localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                                    localTextAnswersBean.setQuestionId(questionBank.getQuestionId() + "");
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
                            Item_Bank_list_options_layout.addView(judgeSelectToDoView);
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

                            //查询保存的答案
                            LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
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
                            Item_Bank_list_options_layout.addView(judgeSelectToDoView);
                        }
                    }
                }
                break;
            case Constant.Multi_Choose:
                if (status.equals(Constant.Todo_Status)) {

                    //解析选项
                    if (!TextUtils.isEmpty(optionJson)) {
                        Gson gson = new Gson();
                        Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                        //查询保存的答案,这是多选，所以存在多个答案
                        LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();

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
                                    localTextAnswersBean.setQuestionId(questionBank.getQuestionId() + "");
                                    localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                                    List<AnswerItem> answerItems = new ArrayList<>();
                                    for (int j = 0; j < Item_Bank_list_options_layout.getChildCount(); j++) {
                                        //是否选中
                                        if (Item_Bank_list_options_layout.getChildAt(j).isSelected()) {
                                            AnswerItem answerItem = new AnswerItem();
                                            answerItem.setContent(entry.getKey());
                                            answerItems.add(answerItem);
                                        }
                                    }
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
                            Item_Bank_list_options_layout.addView(judgeSelectToDoView);
                        }
                    }

                } else {

                    //解析选项
                    if (!TextUtils.isEmpty(optionJson)) {
                        Gson gson = new Gson();
                        Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                        }.getType());
                        Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                        //查询保存的答案,这是多选，所以存在多个答案
                        LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        while (iterator.hasNext()) {
                            Map.Entry<String, String> entry = iterator.next();

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
                            Item_Bank_list_options_layout.addView(judgeSelectToDoView);
                        }
                    }

                }

                break;
            case Constant.Fill_Blank:

                if (status.equals(Constant.Todo_Status)) {

                    //查询保存的答案,这是多选，所以存在多个答案
                    LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                    //使用"^__\\d+__$"不行
                    String reg = "__\\d+__";
                    Pattern pattern = Pattern.compile(reg);
                    Matcher matcher = pattern.matcher(ItemBankTitle);

                    int i = -1;
                    while (matcher.find()) {
                        i++;

                        //塞入填空题数据
                        FillBlankToDoView fillBlankToDoView = new FillBlankToDoView(getContext());
                        //EditText如果不允许聚焦是否就和TextView差不多了？
                        fillBlankToDoView.fill_blank_content.setFocusable(false);

                        //这里不同：保存写的答案痕迹在视图中进行
                        fillBlankToDoView.fillDatas(i);
                        //如果保存过答案回显
                        if (localTextAnswersBean != null) {
//                            QZXTools.logE("fill blank Answer localTextAnswersBean=" + localTextAnswersBean, null);
                            List<AnswerItem> answerItems = localTextAnswersBean.getList();
                            for (AnswerItem answerItem : answerItems) {
                                String content = answerItem.getContent();
                                String[] splitContent = content.split(":");
                                //一对一
                                if (Integer.parseInt(splitContent[0]) == i) {
                                    //因为如果没有填写则为(数字+冒号 )后面是空白的情况
                                    if (splitContent.length > 1) {
                                        //纯粹的显示填空题的已填写过的答案痕迹
                                        fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), splitContent[1]);
                                    } else {
                                        fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), "");
                                    }
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
                                localTextAnswersBean.setQuestionId(questionBank.getQuestionId() + "");
                                localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                                List<AnswerItem> answerItems = new ArrayList<>();
                                for (int j = 0; j < Item_Bank_list_options_layout.getChildCount(); j++) {
                                    //以及遍历的选项布局获取子类
                                    FillBlankToDoView blankedView = (FillBlankToDoView) Item_Bank_list_options_layout.getChildAt(j);
                                    AnswerItem answerItem = new AnswerItem();
                                    //保存文本内容:采用index:content形式
                                    answerItem.setContent(j + ":" + blankedView.fill_blank_content.getText().toString().trim());
                                    answerItems.add(answerItem);
                                }
                                localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                                //插入或者更新数据库
                                MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                                //-------------------------答案保存，依据作业题目id
                            }
                        });
                        Item_Bank_list_options_layout.addView(fillBlankToDoView);
                    }

                } else {

                    //查询保存的答案,这是多选，所以存在多个答案
                    LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

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
                            for (AnswerItem answerItem : answerItems) {
                                String content = answerItem.getContent();
                                String[] splitContent = content.split(":");
                                //一对一
                                if (Integer.parseInt(splitContent[0]) == i) {
                                    //因为如果没有填写则为(数字+冒号 )后面是空白的情况
                                    if (splitContent.length > 1) {
                                        //纯粹的显示填空题的已填写过的答案痕迹
                                        fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), splitContent[1]);
                                    } else {
                                        fillBlankToDoView.fillDatas(Integer.parseInt(splitContent[0]), "");
                                    }
                                }
                            }
                        }
                        Item_Bank_list_options_layout.addView(fillBlankToDoView);
                    }

                }
                break;
            case Constant.Subject_Item:

                if (status.equals(Constant.Todo_Status)) {

                    //查询保存的答案,这是多选，所以存在多个答案
                    LocalTextAnswersBean localTextAnswersBean_sub = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                    //塞入主观题数据
                    BankSubjectiveToDoView subjectiveToDoView = new BankSubjectiveToDoView(getContext());
                    subjectiveToDoView.hideAnswerTools(false);
                    subjectiveToDoView.setQuestionInfo(questionBank);
                    //答案回显,在方法内部判断localTextAnswersBean是否空指针
                    subjectiveToDoView.showImgsAndContent(localTextAnswersBean_sub, status);

                    Item_Bank_list_options_layout.addView(subjectiveToDoView);

                } else {

                    //查询保存的答案,这是多选，所以存在多个答案
                    LocalTextAnswersBean localTextAnswersBean_sub = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                    //塞入主观题数据
                    BankSubjectiveToDoView subjectiveToDoView = new BankSubjectiveToDoView(getContext());
                    subjectiveToDoView.hideAnswerTools(true);
                    subjectiveToDoView.setQuestionInfo(questionBank);
                    //答案回显,在方法内部判断localTextAnswersBean是否空指针
                    subjectiveToDoView.showImgsAndContent(localTextAnswersBean_sub, status);

                    Item_Bank_list_options_layout.addView(subjectiveToDoView);

                }
                break;
        }

        getAnswerInfo();

    }

    /**
     * 答案赋值
     */
    public void getAnswerInfo() {
        if (questionBank == null) {
            return;
        }

        //判断答案显示
        if (questionBank.isShownAnswer()) {
            Item_Bank_List_Answer_Layout.setVisibility(VISIBLE);
        } else {
            Item_Bank_List_Answer_Layout.setVisibility(GONE);
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
                Item_Bank_List_my_Answer.setVisibility(VISIBLE);
                Item_Bank_List_my_Answer.setText("我的答案：" + stringBuffer.toString().trim());
            } else {
                Item_Bank_List_my_Answer.setVisibility(GONE);
            }
        }

        //正确答案
        if (TextUtils.isEmpty(questionBank.getAnswerText())) {
            Item_Bank_List_right_Answer.setVisibility(GONE);
        } else {
            Item_Bank_List_right_Answer.setVisibility(VISIBLE);
            Item_Bank_List_right_Answer.setText("正确答案：" + questionBank.getAnswerText());
        }

        //答案
        if (TextUtils.isEmpty(questionBank.getAnswer())) {
            Item_Bank_List_Answer.setVisibility(GONE);
        } else {
            Item_Bank_List_Answer.setVisibility(VISIBLE);
            //子List采用全Url，不需要拼接，直接使用返回值Answer
            String pointUrl = questionBank.getAnswer();
            Glide.with(getContext()).load(pointUrl).into(Item_Bank_List_Img_Answer);
        }
    }
}
