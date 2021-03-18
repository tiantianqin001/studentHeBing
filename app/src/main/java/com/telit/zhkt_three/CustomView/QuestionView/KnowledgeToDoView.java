package com.telit.zhkt_three.CustomView.QuestionView;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
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
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.TempSaveItemInfo;
import com.telit.zhkt_three.R;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * author: qzx
 * Date: 2019/6/15 17:00
 * <p>
 * 只保存当前在使用的，下次进入就清除了痕迹
 */
public class KnowledgeToDoView extends RelativeLayout {
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

    public KnowledgeToDoView(Context context) {
        this(context, null);
    }

    public KnowledgeToDoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KnowledgeToDoView(Context context, AttributeSet attrs, int defStyleAttr) {
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

    /**
     * 头部数据化以及选项栏
     *
     * 同样以父类题型为准 type
     */
    public void fillDatas(QuestionBank questionBank, int curPosition,int type) {
        this.questionBank = questionBank;
        //题目信息
        String ItemBankTitle = questionBank.getQuestionText();
        ItemBankTitle = ItemBankTitle.replaceAll("\\{#blank#\\}\\d*\\{#/blank#\\}", "_______");
        Item_Bank_list_title_content.setHtml("(" + (curPosition + 1) + ")" + ItemBankTitle, new HtmlHttpImageGetter(Item_Bank_list_title_content));

        String optionJson = questionBank.getAnswerOptions();
        //题型信息
        switch (type) {
            case Constant.Single_Choose:
            case Constant.Judge_Item:
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
                                //因为是单选取消之前选中的
                                for (int i = 0; i < Item_Bank_list_options_layout.getChildCount(); i++) {
                                    JudgeSelectToDoView childJudeSelectedToDoView = (JudgeSelectToDoView) Item_Bank_list_options_layout.getChildAt(i);
                                    if (childJudeSelectedToDoView.isSelected()) {
                                        childJudeSelectedToDoView.handSelectedStatus();
                                    }
                                }

                                JudgeSelectToDoView selectedView = (JudgeSelectToDoView) v;

                                selectedView.handSelectedStatus();


                                //保存当前的痕迹
                                int selectedIndex = Item_Bank_list_options_layout.indexOfChild(selectedView);  //当前选中的下标
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
                        Item_Bank_list_options_layout.addView(judgeSelectToDoView);
                    }
                }
                break;
            case Constant.Multi_Choose:
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
                                for (int j = 0; j < Item_Bank_list_options_layout.getChildCount(); j++) {
                                    //是否选中
                                    if (Item_Bank_list_options_layout.getChildAt(j).isSelected()) {
                                        JudgeSelectToDoView todoView = (JudgeSelectToDoView) Item_Bank_list_options_layout.getChildAt(j);
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
                        Item_Bank_list_options_layout.addView(judgeSelectToDoView);
                    }
                }
                break;
            case Constant.Fill_Blank:
                break;
            case Constant.Subject_Item:
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
