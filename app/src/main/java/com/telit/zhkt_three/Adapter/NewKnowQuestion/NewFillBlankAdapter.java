package com.telit.zhkt_three.Adapter.NewKnowQuestion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NewFillBlankAdapter extends RecyclerView.Adapter<NewFillBlankAdapter.ViewHolder> {
    private Context mContext;
    private QuestionBank questionBank;
    private String status;

    private List<Integer> quints = new ArrayList<>();
    private static Map<String, String> tags = new HashMap<>();


    public NewFillBlankAdapter(Context mContext, QuestionBank questionBank, String status) {

        this.mContext = mContext;
        this.questionBank = questionBank;
        this.status = status;
        String ItemBankTitle = questionBank.getQuestionText();
        //使用"^__\\d+__$"不行
        String reg = "__\\d+__";
        Pattern pattern = Pattern.compile(reg);
        Matcher matcher = pattern.matcher(ItemBankTitle);
        int i = -1;

        //根据正则判断有几道题
        while (matcher.find()) {
            i++;
            quints.add(i);
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.fill_blank_option_complete_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.fill_blank_option.setText(String.valueOf(quints.get(i) + 1));
        viewHolder.fill_blank_content.setTag(i + "");
        //数据的回显


        if (status.equals(Constant.Todo_Status)) {

            //查询保存的答案,这是多选，所以存在多个答案
            LocalTextAnswersBean localTextAnswersBean1 = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                            LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                            LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

            //如果保存过答案回显
            if (localTextAnswersBean1 != null) {
//                            QZXTools.logE("fill blank Answer localTextAnswersBean=" + localTextAnswersBean, null);
                List<AnswerItem> answerItems = localTextAnswersBean1.getList();
                for (AnswerItem answerItem : answerItems) {
                    String content = answerItem.getContent();
                    String[] splitContent = content.split(":");
                    tags.put(splitContent[0], splitContent[1]);
                    //一对一
                    if (Integer.parseInt(splitContent[0]) == i) {
                        //因为如果没有填写则为(数字+冒号 )后面是空白的情况
                        if (splitContent.length > 1) {
                            //纯粹的显示填空题的已填写过的答案痕迹
                            viewHolder.fill_blank_content.setText(splitContent[1]);
                        } else {
                            viewHolder.fill_blank_content.setText("");
                        }
                    }
                }
            }

            viewHolder.fill_blank_content.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //保存数据
                    String tag = (String) viewHolder.fill_blank_content.getTag();
                    tags.put(tag, s.toString());

                    //保存数据
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                    localTextAnswersBean.setQuestionId(questionBank.getId() + "");
                    localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    List<AnswerItem> answerItems = new ArrayList<>();

                    //保存文本内容:采用index:content形式
                    // Iterating entries using a For Each loop
                    for (Map.Entry<String, String> entry : tags.entrySet()) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setContent(entry.getKey() + ":" + entry.getValue());
                        answerItem.setBlanknum(i + 1 + "");
                        answerItems.add(answerItem);
                    }
                    localTextAnswersBean.setList(answerItems);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                    Log.i("", "afterTextChanged: " + localTextAnswersBean + "....." + tags);
                }
            });
        }
        if (status.equals(Constant.Commit_Status) || status.equals(Constant.Review_Status) || status.equals(Constant.Retry_Status)) {
            viewHolder.fill_blank_content.setFocusable(false);
            viewHolder.fill_blank_content.setFocusableInTouchMode(false);
            viewHolder.fill_blank_content.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return true;
                }
            });

            //查询保存的答案,这是多选，所以存在多个答案
            LocalTextAnswersBean localTextAnswersBean1 = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getQuestionId() + ""),
                            LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                            LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

            //如果保存过答案回显
            if (localTextAnswersBean1 != null) {
//                            QZXTools.logE("fill blank Answer localTextAnswersBean=" + localTextAnswersBean, null);
                List<AnswerItem> answerItems = localTextAnswersBean1.getList();
                for (AnswerItem answerItem : answerItems) {
                    String content = answerItem.getContent();
                    String[] splitContent = content.split(":");
                    tags.put(splitContent[0], splitContent[1]);
                    //一对一
                    if (Integer.parseInt(splitContent[0]) == i) {
                        //因为如果没有填写则为(数字+冒号 )后面是空白的情况
                        if (splitContent.length > 1) {
                            //纯粹的显示填空题的已填写过的答案痕迹
                            viewHolder.fill_blank_content.setText(splitContent[1]);
                        } else {
                            viewHolder.fill_blank_content.setText("");
                        }
                    }
                }
            }

        } else if (status.equals(Constant.Save_Status)) {
            //当前状态是保存
            List<WorkOwnResult> ownList = questionBank.getOwnList();
            if (ownList != null && ownList.size() > 0) {
                if (ownList.size() - 1 >= i) {
                    WorkOwnResult workOwnResult = ownList.get(i);
                    String answerContent = workOwnResult.getAnswerContent();
                    if (TextUtils.isEmpty(answerContent)) {

                        viewHolder.fill_blank_content.setText("");
                    } else {
                        viewHolder.fill_blank_content.setText(answerContent.substring(2, answerContent.length()));

                    }
                }
            }

            //数据保存到本地
            //保存数据
            LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
            localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
            localTextAnswersBean.setQuestionId(questionBank.getId() + "");
            localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
            localTextAnswersBean.setUserId(UserUtils.getUserId());
            List<AnswerItem> answerItems = new ArrayList<>();

            //保存文本内容:采用index:content形式
            // Iterating entries using a For Each loop

            for (int j = 0; j < ownList.size(); j++) {
                AnswerItem answerItem = new AnswerItem();
                answerItem.setContent(j + ":" + ownList.get(j).getAnswerContent());
                answerItem.setBlanknum(i + 1 + "");
                answerItems.add(answerItem);
            }
            localTextAnswersBean.setList(answerItems);
            //插入或者更新数据库
            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);


            viewHolder.fill_blank_content.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    //保存数据
                    String tag = (String) viewHolder.fill_blank_content.getTag();
                    tags.put(tag, s.toString());

                    //保存数据
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                    localTextAnswersBean.setQuestionId(questionBank.getId() + "");
                    localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    List<AnswerItem> answerItems = new ArrayList<>();

                    //保存文本内容:采用index:content形式
                    // Iterating entries using a For Each loop
                    for (Map.Entry<String, String> entry : tags.entrySet()) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setContent(entry.getKey() + ":" + entry.getValue());
                        answerItem.setBlanknum(i + 1 + "");
                        answerItems.add(answerItem);
                    }
                    localTextAnswersBean.setList(answerItems);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                    Log.i("", "afterTextChanged: " + localTextAnswersBean + "....." + tags);
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return quints.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView fill_blank_option;
        public EditText fill_blank_content;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            fill_blank_option = itemView.findViewById(R.id.fill_blank_option);
            fill_blank_content = itemView.findViewById(R.id.fill_blank_content);
        }
    }
}
