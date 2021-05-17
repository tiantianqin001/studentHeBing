package com.telit.zhkt_three.Adapter.NewKnowQuestion;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.NewKnowledge.SingleBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.UserUtils;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class NewSingleChooseTwoAdapter extends RecyclerView.Adapter<NewSingleChooseTwoAdapter.ViewHolder> {
    private Context mContext;
    private QuestionBank questionBank;
    private String status;

    private List<SingleBean> singleBeans = new ArrayList<>();

    private List<String> tags = new ArrayList<>();

    public NewSingleChooseTwoAdapter(Context mContext, QuestionBank questionBank, String status) {

        this.mContext = mContext;
        this.questionBank = questionBank;
        this.status = status;


        String optionJson = questionBank.getAnswerOptions();

        if (status.equals(Constant.Todo_Status) || status.equals(Constant.Retry_Status)) {
            //解析选项   设置题中的内容
            if (!TextUtils.isEmpty(optionJson)) {
                Gson gson = new Gson();
                Map<String, String> optionMap = gson.fromJson(optionJson, new TypeToken<Map<String, String>>() {
                }.getType());
                Iterator<Map.Entry<String, String>> iterator = optionMap.entrySet().iterator();

                while (iterator.hasNext()) {
                    Map.Entry<String, String> entry = iterator.next();
                    SingleBean singleBean = new SingleBean();
                    singleBean.setKeys(entry.getKey());
                    singleBean.setWords(entry.getValue());
                    singleBeans.add(singleBean);
                }
            }
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.judge_select_option_complete_layout, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.option_do_tv.setText(singleBeans.get(i).getKeys());
        viewHolder.option_do_tv.setTag(singleBeans.get(i).getKeys());
        viewHolder.option_do_htv.setHtml(singleBeans.get(i).getWords(), new HtmlHttpImageGetter(viewHolder.option_do_htv));
        if (tags.contains(singleBeans.get(i).getKeys())) {
            viewHolder.option_do_tv.setSelected(true);
        } else {
            viewHolder.option_do_tv.setSelected(false);

        }
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //取消所有选中的
                tags.clear();

                if (viewHolder.option_do_tv.isSelected()) {
                    viewHolder.option_do_tv.setSelected(false);
                } else {
                    String tag = (String) viewHolder.option_do_tv.getTag();

                    tags.add(tag);

                    //保存单选题数据
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    localTextAnswersBean.setHomeworkId(questionBank.getHomeworkId());
                    localTextAnswersBean.setQuestionId(questionBank.getId() + "");
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionBank.getQuestionChannelType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    AnswerItem answerItem = new AnswerItem();
                    answerItem.setContent(tags.get(0));
                    answerItems.add(answerItem);
                    localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                }
                notifyDataSetChanged();

            }
        });

        //做过答案的回显
        //查询保存的答案
/*        LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionBank.getId() + ""),
                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionBank.getHomeworkId()),
                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
        if (localTextAnswersBean != null) {
            List<AnswerItem> answersBeanList = localTextAnswersBean.getList();

            if (answersBeanList != null) {
                for (AnswerItem answerItem : answersBeanList) {
                    if (singleBeans.get(i).getKeys().equals(answerItem.getContent())) {
                        viewHolder.option_do_tv.setSelected(true);
                    }
                }
            }
        }*/


    }


    @Override
    public int getItemCount() {
        return singleBeans.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        private TextView option_do_tv;
        private HtmlTextView option_do_htv;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            option_do_tv = itemView.findViewById(R.id.option_do_tv);
            option_do_htv = itemView.findViewById(R.id.option_do_htv);
        }
    }
}
