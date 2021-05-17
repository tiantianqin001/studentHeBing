package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionTvAnswerAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.QuestionView.FillBlankToDoView;
import com.telit.zhkt_three.JavaBean.FillBlankBean;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.MulitBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;
import com.telit.zhkt_three.listener.EdtextListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FullBlankView extends LinearLayout {
    private static final String TAG = "MulipleChoiseView";
    private Context mContext;

    private final LinearLayout ll_fill_balank_one;
    private final LinearLayout ll_fill_balank_two;
    private final LinearLayout ll_fill_balank_three;
    private final LinearLayout ll_fill_balank_fore;
    private final LinearLayout ll_fill_balank_five;
    private final LinearLayout ll_fill_balank_sex;
    private final TextView practice_head_index;
    private final EditText et_fill_balank_one;
    private final EditText et_fill_balank_two;
    private final EditText et_fill_balank_three;
    private final EditText et_fill_balank_fore;
    private final EditText et_fill_balank_five;
    private final EditText et_fill_balank_sex;
    private final LinearLayout ll_show_quint;
    private final LinearLayout ll_fill_quint_one;
    private final LinearLayout ll_fill_quint_two;
    private final LinearLayout ll_fill_quint_three;
    private final LinearLayout ll_fill_quint_fore;
    private final LinearLayout ll_fill_quint_five;
    private final LinearLayout ll_fill_quint_six;
    private final TextView tv_fill_quint_one;
    private final TextView tv_fill_quint_two;
    private final TextView tv_fill_quint_three;
    private final TextView tv_fill_quint_fore;
    private final TextView tv_fill_quint_five;
    private final TextView tv_fill_quint_six;


    private String taskStatus;
    private LocalTextAnswersBean linkLocal;

    public FullBlankView(Context context) {
        this(context, null);
    }

    public FullBlankView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FullBlankView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;


        View itemView = LayoutInflater.from(context).inflate(R.layout.fill_blank_image_layout,
                this, true);


        ll_fill_balank_one = itemView.findViewById(R.id.ll_fill_balank_one);
        ll_fill_balank_two = itemView.findViewById(R.id.ll_fill_balank_two);
        ll_fill_balank_three = itemView.findViewById(R.id.ll_fill_balank_three);
        ll_fill_balank_fore = itemView.findViewById(R.id.ll_fill_balank_fore);
        ll_fill_balank_five = itemView.findViewById(R.id.ll_fill_balank_five);
        ll_fill_balank_sex = itemView.findViewById(R.id.ll_fill_balank_sex);
        //头信息和题目
        practice_head_index = itemView.findViewById(R.id.practice_head_index);
        //输入的文字
        et_fill_balank_one = itemView.findViewById(R.id.et_fill_balank_one);
        et_fill_balank_two = itemView.findViewById(R.id.et_fill_balank_two);
        et_fill_balank_three = itemView.findViewById(R.id.et_fill_balank_three);
        et_fill_balank_fore = itemView.findViewById(R.id.et_fill_balank_fore);
        et_fill_balank_five = itemView.findViewById(R.id.et_fill_balank_five);
        et_fill_balank_sex = itemView.findViewById(R.id.et_fill_balank_sex);
        //查看答案吧
        ll_show_quint = itemView.findViewById(R.id.ll_show_quint);
        ll_fill_quint_one = itemView.findViewById(R.id.ll_fill_quint_one);
        ll_fill_quint_two = itemView.findViewById(R.id.ll_fill_quint_two);
        ll_fill_quint_three = itemView.findViewById(R.id.ll_fill_quint_three);
        ll_fill_quint_fore = itemView.findViewById(R.id.ll_fill_quint_fore);
        ll_fill_quint_five = itemView.findViewById(R.id.ll_fill_quint_five);
        ll_fill_quint_six = itemView.findViewById(R.id.ll_fill_quint_six);
        tv_fill_quint_one = itemView.findViewById(R.id.tv_fill_quint_one);
        tv_fill_quint_two = itemView.findViewById(R.id.tv_fill_quint_two);
        tv_fill_quint_three = itemView.findViewById(R.id.tv_fill_quint_three);
        tv_fill_quint_fore = itemView.findViewById(R.id.tv_fill_quint_fore);
        tv_fill_quint_five = itemView.findViewById(R.id.tv_fill_quint_five);
        tv_fill_quint_six = itemView.findViewById(R.id.tv_fill_quint_six);

    }

    public void setTaskStatus(String taskStatus) {

        this.taskStatus = taskStatus;
    }

    //设置数据
    public void setViewData(List<QuestionInfo.SelectBean> selectBeans, List<QuestionInfo> questionInfoList, int i,
                            String homeworkId, int homeWorkType) {
        if (selectBeans.size() == 1) {
            ll_fill_balank_one.setVisibility(VISIBLE);

        } else if (selectBeans.size() == 2) {
            ll_fill_balank_one.setVisibility(VISIBLE);
            ll_fill_balank_two.setVisibility(VISIBLE);

        } else if (selectBeans.size() == 3) {
            ll_fill_balank_one.setVisibility(VISIBLE);
            ll_fill_balank_two.setVisibility(VISIBLE);
            ll_fill_balank_three.setVisibility(VISIBLE);

        } else if (selectBeans.size() == 4) {
            ll_fill_balank_one.setVisibility(VISIBLE);
            ll_fill_balank_two.setVisibility(VISIBLE);
            ll_fill_balank_three.setVisibility(VISIBLE);
            ll_fill_balank_fore.setVisibility(VISIBLE);
        } else if (selectBeans.size() == 5) {
            ll_fill_balank_one.setVisibility(VISIBLE);
            ll_fill_balank_two.setVisibility(VISIBLE);
            ll_fill_balank_three.setVisibility(VISIBLE);
            ll_fill_balank_fore.setVisibility(VISIBLE);
            ll_fill_balank_five.setVisibility(VISIBLE);
        } else if (selectBeans.size() == 6) {
            ll_fill_balank_one.setVisibility(VISIBLE);
            ll_fill_balank_two.setVisibility(VISIBLE);
            ll_fill_balank_three.setVisibility(VISIBLE);
            ll_fill_balank_fore.setVisibility(VISIBLE);
            ll_fill_balank_five.setVisibility(VISIBLE);
            ll_fill_balank_sex.setVisibility(VISIBLE);
        }

        //0未提交  1 已提交  2 已批阅
        //设置作业头信息
        practice_head_index.setText("第" + (i + 1) + "题 共" +
                questionInfoList.size() + "题");
        if (taskStatus.equals(Constant.Todo_Status) || taskStatus.equals(Constant.Save_Status)) {
            if (taskStatus.equals(Constant.Todo_Status)){
                //答案的回显
                //只有是作业显示  互动不获取
                if (homeWorkType == 1){
                    linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
                }
                if (linkLocal != null && linkLocal.questionId.equals(questionInfoList.get(i).getId()) && linkLocal.getList() != null) {
                    Log.i(TAG, "onBindViewHolder: " + linkLocal);

                    if (linkLocal.getList().size() == 6) {
                        et_fill_balank_one.setText(linkLocal.getList().get(0).content);
                        et_fill_balank_two.setText(linkLocal.getList().get(1).content);
                        et_fill_balank_three.setText(linkLocal.getList().get(2).content);
                        et_fill_balank_fore.setText(linkLocal.getList().get(3).content);
                        et_fill_balank_five.setText(linkLocal.getList().get(4).content);
                        et_fill_balank_sex.setText(linkLocal.getList().get(5).content);

                    } else if (linkLocal.getList().size() == 5) {
                        et_fill_balank_one.setText(linkLocal.getList().get(0).content);
                        et_fill_balank_two.setText(linkLocal.getList().get(1).content);
                        et_fill_balank_three.setText(linkLocal.getList().get(2).content);
                        et_fill_balank_fore.setText(linkLocal.getList().get(3).content);
                        et_fill_balank_five.setText(linkLocal.getList().get(4).content);

                    } else if (linkLocal.getList().size() == 4) {
                        et_fill_balank_one.setText(linkLocal.getList().get(0).content);
                        et_fill_balank_two.setText(linkLocal.getList().get(1).content);
                        et_fill_balank_three.setText(linkLocal.getList().get(2).content);
                        et_fill_balank_fore.setText(linkLocal.getList().get(3).content);

                    } else if (linkLocal.getList().size() == 3) {
                        et_fill_balank_one.setText(linkLocal.getList().get(0).content);
                        et_fill_balank_two.setText(linkLocal.getList().get(1).content);
                        et_fill_balank_three.setText(linkLocal.getList().get(2).content);

                    } else if (linkLocal.getList().size() == 2) {
                        et_fill_balank_one.setText(linkLocal.getList().get(0).content);
                        et_fill_balank_two.setText(linkLocal.getList().get(1).content);


                    } else if (linkLocal.getList().size() == 1) {
                        et_fill_balank_one.setText(linkLocal.getList().get(0).content);
                    }

                }
            }else if (taskStatus.equals(Constant.Save_Status)){
                //设置学生的答案  todo  作业多次保存会有问题    只保存的当前的数据
                List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();

                if (ownList!=null && ownList.size()>0){
                    if (ownList.size() == 6){
                        et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                        et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                        et_fill_balank_three.setText(ownList.get(2).getAnswerContent());
                        et_fill_balank_fore.setText(ownList.get(3).getAnswerContent());
                        et_fill_balank_five.setText(ownList.get(4).getAnswerContent());
                        et_fill_balank_sex.setText(ownList.get(5).getAnswerContent());


                        et_fill_balank_one.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_two.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_three.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_fore.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_five.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_sex.setFocusableInTouchMode(true);//可编辑
                    }else if (ownList.size() == 5){
                        et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                        et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                        et_fill_balank_three.setText(ownList.get(2).getAnswerContent());
                        et_fill_balank_fore.setText(ownList.get(3).getAnswerContent());
                        et_fill_balank_five.setText(ownList.get(4).getAnswerContent());

                        et_fill_balank_one.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_two.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_three.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_fore.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_five.setFocusableInTouchMode(true);//可编辑
                    }else if (ownList.size() == 4){
                        et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                        et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                        et_fill_balank_three.setText(ownList.get(2).getAnswerContent());
                        et_fill_balank_fore.setText(ownList.get(3).getAnswerContent());

                        et_fill_balank_one.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_two.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_three.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_fore.setFocusableInTouchMode(true);//可编辑

                    }else if (ownList.size() == 3){
                        et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                        et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                        et_fill_balank_three.setText(ownList.get(2).getAnswerContent());

                        et_fill_balank_one.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_two.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_three.setFocusableInTouchMode(true);//不可编辑

                    }else if (ownList.size() == 2){
                        et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                        et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                        et_fill_balank_one.setFocusableInTouchMode(true);//可编辑
                        et_fill_balank_two.setFocusableInTouchMode(true);//可编辑

                    }else if (ownList.size() == 1){
                        et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                        et_fill_balank_one.setFocusableInTouchMode(true);//可编辑
                    }
                }

                //数据保存到本地
                LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                localTextAnswersBean.setHomeworkId(questionInfoList.get(i).getHomeworkId());
                localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                localTextAnswersBean.setUserId(UserUtils.getUserId());
                localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                List<AnswerItem> answerItems = new ArrayList<>();

                for (int j = 0; j < ownList.size(); j++) {
                    //以及遍历的选项布局获取子类
                    AnswerItem answerItem = new AnswerItem();
                    answerItem.setItemId(ownList.get(j).getAnswerId());
                    answerItem.setBlanknum((j + 1) + "");
                    answerItem.setContent(ownList.get(j).getAnswerContent());
                    answerItems.add(answerItem);
                }
                localTextAnswersBean.setList(answerItems);
                //插入或者更新数据库
                MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
            }






            List<Integer> indexs = new ArrayList<>();
            HashMap<Integer, String> contentMaps = new HashMap<>();

            et_fill_balank_one.addTextChangedListener(new EdtextListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    indexs.add(0);
                    contentMaps.put(0, et_fill_balank_one.getText().toString().trim());


                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    FillBlankBean fillBlankBean = new FillBlankBean();
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();


                    List<String> answers = new ArrayList<>();

                    for (int j = 0; j < selectBeans.size(); j++) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setItemId(selectBeans.get(j).getId());
                        // blanknum从1开始，因为从零开始服务端拼写有问题
                        answerItem.setBlanknum((j + 1) + "");
                        //设置输入的内容

                        if (indexs.contains(j)) {

                            answerItem.setContent(contentMaps.get(j));
                            answerItems.add(answerItem);
                        } else {

                            //如果之前已经编写了内容要添加上
                            if (linkLocal != null && linkLocal.getList() != null) {
                                if (!TextUtils.isEmpty(linkLocal.getList().get(j).getContent())) {
                                    answerItem.setItemId(linkLocal.getList().get(j).getItemId());
                                    // blanknum从1开始，因为从零开始服务端拼写有问题
                                    answerItem.setBlanknum(linkLocal.getList().get(j).getBlanknum());
                                    //设置输入的内容
                                    answerItem.setContent(linkLocal.getList().get(j).getContent());
                                    answerItems.add(answerItem);
                                } else {
                                    answerItem.setContent("");

                                    answerItems.add(answerItem);
                                }
                            } else {
                                answerItem.setContent("");
                                answerItems.add(answerItem);
                            }

                        }


                        answers.add(s.toString());
                    }


                    localTextAnswersBean.setList(answerItems);
                    //只有填空提保留问题的回显
                    localTextAnswersBean.setAnswers(answers);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    fillBlankBean.setId(questionInfoList.get(i).getId());
                    fillBlankBean.setList(answerItems);
                    MyApplication.getInstance().getDaoSession().getFillBlankBeanDao().insertOrReplace(fillBlankBean);
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                    //Log.i(TAG, "afterTextChanged: "+connects);
                }
            });
            et_fill_balank_two.addTextChangedListener(new EdtextListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    indexs.add(1);
                    contentMaps.put(1, et_fill_balank_two.getText().toString().trim());
                    // connects.add(((FillBlankHolder) viewHolder).et_fill_balank_two.getText().toString().trim());

                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    FillBlankBean fillBlankBean = new FillBlankBean();
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    List<String> answers = new ArrayList<>();
                    for (int j = 0; j < selectBeans.size(); j++) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setItemId(selectBeans.get(j).getId());
                        // blanknum从1开始，因为从零开始服务端拼写有问题
                        answerItem.setBlanknum((j + 1) + "");
                        //设置输入的内容

                        if (indexs.contains(j)) {

                            answerItem.setContent(contentMaps.get(j));
                            answerItems.add(answerItem);
                        } else {

                            //如果之前已经编写了内容要添加上
                            if (linkLocal != null && linkLocal.getList() != null) {
                                if (!TextUtils.isEmpty(linkLocal.getList().get(j).getContent())) {
                                    answerItem.setItemId(linkLocal.getList().get(j).getItemId());
                                    // blanknum从1开始，因为从零开始服务端拼写有问题
                                    answerItem.setBlanknum(linkLocal.getList().get(j).getBlanknum());
                                    //设置输入的内容
                                    answerItem.setContent(linkLocal.getList().get(j).getContent());
                                    answerItems.add(answerItem);
                                } else {
                                    answerItem.setContent("");

                                    answerItems.add(answerItem);
                                }
                            } else {
                                answerItem.setContent("");
                                answerItems.add(answerItem);
                            }

                        }


                        answers.add(s.toString());
                    }


                    localTextAnswersBean.setList(answerItems);
                    //只有填空提保留问题的回显
                    localTextAnswersBean.setAnswers(answers);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    fillBlankBean.setId(questionInfoList.get(i).getId());
                    fillBlankBean.setList(answerItems);
                    MyApplication.getInstance().getDaoSession().getFillBlankBeanDao().insertOrReplace(fillBlankBean);
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            et_fill_balank_three.addTextChangedListener(new EdtextListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    indexs.add(2);
                    contentMaps.put(2, et_fill_balank_three.getText().toString().trim());
                    //connects.add(((FillBlankHolder) viewHolder).et_fill_balank_three.getText().toString().trim());

                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    FillBlankBean fillBlankBean = new FillBlankBean();
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    List<String> answers = new ArrayList<>();
                    for (int j = 0; j < selectBeans.size(); j++) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setItemId(selectBeans.get(j).getId());
                        // blanknum从1开始，因为从零开始服务端拼写有问题
                        answerItem.setBlanknum((j + 1) + "");
                        //设置输入的内容

                        if (indexs.contains(j)) {

                            answerItem.setContent(contentMaps.get(j));
                            answerItems.add(answerItem);
                        } else {

                            //如果之前已经编写了内容要添加上
                            if (linkLocal != null && linkLocal.getList() != null) {
                                if (!TextUtils.isEmpty(linkLocal.getList().get(j).getContent())) {
                                    answerItem.setItemId(linkLocal.getList().get(j).getItemId());
                                    // blanknum从1开始，因为从零开始服务端拼写有问题
                                    answerItem.setBlanknum(linkLocal.getList().get(j).getBlanknum());
                                    //设置输入的内容
                                    answerItem.setContent(linkLocal.getList().get(j).getContent());
                                    answerItems.add(answerItem);
                                } else {
                                    answerItem.setContent("");

                                    answerItems.add(answerItem);
                                }
                            } else {
                                answerItem.setContent("");
                                answerItems.add(answerItem);
                            }

                        }


                        answers.add(s.toString());
                    }

                    localTextAnswersBean.setList(answerItems);
                    //只有填空提保留问题的回显
                    localTextAnswersBean.setAnswers(answers);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    fillBlankBean.setId(questionInfoList.get(i).getId());
                    fillBlankBean.setList(answerItems);
                    MyApplication.getInstance().getDaoSession().getFillBlankBeanDao().insertOrReplace(fillBlankBean);
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            et_fill_balank_fore.addTextChangedListener(new EdtextListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    indexs.add(3);
                    contentMaps.put(3, et_fill_balank_fore.getText().toString().trim());
                    // connects.add(((FillBlankHolder) viewHolder).et_fill_balank_fore.getText().toString().trim());

                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    FillBlankBean fillBlankBean = new FillBlankBean();
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    List<String> answers = new ArrayList<>();
                    for (int j = 0; j < selectBeans.size(); j++) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setItemId(selectBeans.get(j).getId());
                        // blanknum从1开始，因为从零开始服务端拼写有问题
                        answerItem.setBlanknum((j + 1) + "");
                        //设置输入的内容

                        if (indexs.contains(j)) {

                            answerItem.setContent(contentMaps.get(j));
                            answerItems.add(answerItem);
                        } else {

                            //如果之前已经编写了内容要添加上
                            if (linkLocal != null && linkLocal.getList() != null) {
                                if (!TextUtils.isEmpty(linkLocal.getList().get(j).getContent())) {
                                    answerItem.setItemId(linkLocal.getList().get(j).getItemId());
                                    // blanknum从1开始，因为从零开始服务端拼写有问题
                                    answerItem.setBlanknum(linkLocal.getList().get(j).getBlanknum());
                                    //设置输入的内容
                                    answerItem.setContent(linkLocal.getList().get(j).getContent());
                                    answerItems.add(answerItem);
                                } else {
                                    answerItem.setContent("");

                                    answerItems.add(answerItem);
                                }
                            } else {
                                answerItem.setContent("");
                                answerItems.add(answerItem);
                            }

                        }


                        answers.add(s.toString());
                    }

                    localTextAnswersBean.setList(answerItems);
                    //只有填空提保留问题的回显
                    localTextAnswersBean.setAnswers(answers);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    fillBlankBean.setId(questionInfoList.get(i).getId());
                    fillBlankBean.setList(answerItems);
                    MyApplication.getInstance().getDaoSession().getFillBlankBeanDao().insertOrReplace(fillBlankBean);
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            et_fill_balank_five.addTextChangedListener(new EdtextListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    indexs.add(4);
                    contentMaps.put(4, et_fill_balank_five.getText().toString().trim());
                    //  connects.add(((FillBlankHolder) viewHolder).et_fill_balank_five.getText().toString().trim());

                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    FillBlankBean fillBlankBean = new FillBlankBean();
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    List<String> answers = new ArrayList<>();
                    for (int j = 0; j < selectBeans.size(); j++) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setItemId(selectBeans.get(j).getId());
                        // blanknum从1开始，因为从零开始服务端拼写有问题
                        answerItem.setBlanknum((j + 1) + "");
                        //设置输入的内容

                        if (indexs.contains(j)) {

                            answerItem.setContent(contentMaps.get(j));
                            answerItems.add(answerItem);
                        } else {

                            //如果之前已经编写了内容要添加上
                            if (linkLocal != null && linkLocal.getList() != null) {
                                if (!TextUtils.isEmpty(linkLocal.getList().get(j).getContent())) {
                                    answerItem.setItemId(linkLocal.getList().get(j).getItemId());
                                    // blanknum从1开始，因为从零开始服务端拼写有问题
                                    answerItem.setBlanknum(linkLocal.getList().get(j).getBlanknum());
                                    //设置输入的内容
                                    answerItem.setContent(linkLocal.getList().get(j).getContent());
                                    answerItems.add(answerItem);
                                } else {
                                    answerItem.setContent("");

                                    answerItems.add(answerItem);
                                }
                            } else {
                                answerItem.setContent("");
                                answerItems.add(answerItem);
                            }

                        }


                        answers.add(s.toString());
                    }
                    localTextAnswersBean.setList(answerItems);
                    //只有填空提保留问题的回显
                    localTextAnswersBean.setAnswers(answers);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    fillBlankBean.setId(questionInfoList.get(i).getId());
                    fillBlankBean.setList(answerItems);
                    MyApplication.getInstance().getDaoSession().getFillBlankBeanDao().insertOrReplace(fillBlankBean);
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            et_fill_balank_sex.addTextChangedListener(new EdtextListener() {
                @Override
                public void afterTextChanged(Editable s) {
                    indexs.add(5);
                    contentMaps.put(5, et_fill_balank_sex.getText().toString().trim());
                    //  connects.add(((FillBlankHolder) viewHolder).et_fill_balank_sex.getText().toString().trim());

                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    FillBlankBean fillBlankBean = new FillBlankBean();
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    List<String> answers = new ArrayList<>();
                    for (int j = 0; j < selectBeans.size(); j++) {
                        AnswerItem answerItem = new AnswerItem();
                        answerItem.setItemId(selectBeans.get(j).getId());
                        // blanknum从1开始，因为从零开始服务端拼写有问题
                        answerItem.setBlanknum((j + 1) + "");
                        //设置输入的内容

                        if (indexs.contains(j)) {

                            answerItem.setContent(contentMaps.get(j));
                            answerItems.add(answerItem);
                        } else {

                            //如果之前已经编写了内容要添加上
                            if (linkLocal != null && linkLocal.getList() != null) {
                                if (!TextUtils.isEmpty(linkLocal.getList().get(j).getContent())) {
                                    answerItem.setItemId(linkLocal.getList().get(j).getItemId());
                                    // blanknum从1开始，因为从零开始服务端拼写有问题
                                    answerItem.setBlanknum(linkLocal.getList().get(j).getBlanknum());
                                    //设置输入的内容
                                    answerItem.setContent(linkLocal.getList().get(j).getContent());
                                    answerItems.add(answerItem);
                                } else {
                                    answerItem.setContent("");

                                    answerItems.add(answerItem);
                                }
                            } else {
                                answerItem.setContent("");
                                answerItems.add(answerItem);
                            }

                        }


                        answers.add(s.toString());
                    }

                    localTextAnswersBean.setList(answerItems);
                    //只有填空提保留问题的回显
                    localTextAnswersBean.setAnswers(answers);
//                                QZXTools.logE("fill blank Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    fillBlankBean.setId(questionInfoList.get(i).getId());
                    fillBlankBean.setList(answerItems);
                    MyApplication.getInstance().getDaoSession().getFillBlankBeanDao().insertOrReplace(fillBlankBean);
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });


        } else {
            //作业已经提交

            ll_show_quint.setVisibility(VISIBLE);

            //设置学生的答案
            List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();

            if (ownList!=null && ownList.size()>0){
                if (ownList.size() == 6){
                    et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                    et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                    et_fill_balank_three.setText(ownList.get(2).getAnswerContent());
                    et_fill_balank_fore.setText(ownList.get(3).getAnswerContent());
                    et_fill_balank_five.setText(ownList.get(4).getAnswerContent());
                    et_fill_balank_sex.setText(ownList.get(5).getAnswerContent());


                    et_fill_balank_one.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_two.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_three.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_fore.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_five.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_sex.setFocusableInTouchMode(false);//不可编辑
                }else if (ownList.size() == 5){
                    et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                    et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                    et_fill_balank_three.setText(ownList.get(2).getAnswerContent());
                    et_fill_balank_fore.setText(ownList.get(3).getAnswerContent());
                    et_fill_balank_five.setText(ownList.get(4).getAnswerContent());

                    et_fill_balank_one.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_two.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_three.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_fore.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_five.setFocusableInTouchMode(false);//不可编辑
                }else if (ownList.size() == 4){
                    et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                    et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                    et_fill_balank_three.setText(ownList.get(2).getAnswerContent());
                    et_fill_balank_fore.setText(ownList.get(3).getAnswerContent());

                    et_fill_balank_one.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_two.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_three.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_fore.setFocusableInTouchMode(false);//不可编辑

                }else if (ownList.size() == 3){
                    et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                    et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                    et_fill_balank_three.setText(ownList.get(2).getAnswerContent());

                    et_fill_balank_one.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_two.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_three.setFocusableInTouchMode(false);//不可编辑

                }else if (ownList.size() == 2){
                    et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                    et_fill_balank_two.setText(ownList.get(1).getAnswerContent());
                    et_fill_balank_one.setFocusableInTouchMode(false);//不可编辑
                    et_fill_balank_two.setFocusableInTouchMode(false);//不可编辑

                }else if (ownList.size() == 1){
                    et_fill_balank_one.setText(ownList.get(0).getAnswerContent());
                    et_fill_balank_one.setFocusableInTouchMode(false);//不可编辑
                }
            }




            //设置正确答案
            List<String> answers = new ArrayList<>();
            String answer = questionInfoList.get(i).getAnswer();
            if (answer.contains("|")) {
                String[] strings = answer.split("\\|");

                for (int j = 0; j < strings.length; j++) {
                    answers.add(strings[j]);
                }
            } else {
                answers.add(answer);
            }

            if (selectBeans.size() == 6) {
                tv_fill_quint_one.setText(answers.get(0));
                tv_fill_quint_two.setText(answers.get(1));
                tv_fill_quint_three.setText(answers.get(2));
                ;
                tv_fill_quint_fore.setText(answers.get(3));
                tv_fill_quint_five.setText(answers.get(4));
                tv_fill_quint_six.setText(answers.get(5));
            }
            if (selectBeans.size() == 5) {
                tv_fill_quint_one.setText(answers.get(0));
                tv_fill_quint_two.setText(answers.get(1));
                tv_fill_quint_three.setText(answers.get(2));;
                tv_fill_quint_fore.setText(answers.get(3));
                tv_fill_quint_five.setText(answers.get(4));
                ll_fill_quint_six.setVisibility(GONE);

            }
            if (selectBeans.size() == 4) {
                tv_fill_quint_one.setText(answers.get(0));
                tv_fill_quint_two.setText(answers.get(1));
                tv_fill_quint_three.setText(answers.get(2));
                ;
                tv_fill_quint_fore.setText(answers.get(3));
                ll_fill_quint_five.setVisibility(GONE);
                ll_fill_quint_six.setVisibility(GONE);

            }
            if (selectBeans.size() == 3) {
                tv_fill_quint_one.setText(answers.get(0));
                tv_fill_quint_two.setText(answers.get(1));
                tv_fill_quint_three.setText(answers.get(2));
                ll_fill_quint_fore.setVisibility(GONE);
                ll_fill_quint_five.setVisibility(GONE);
                ll_fill_quint_six.setVisibility(GONE);

            }
            if (selectBeans.size() == 2) {
                tv_fill_quint_one.setText(answers.get(0));
                tv_fill_quint_two.setText(answers.get(1));
                ll_fill_quint_three.setVisibility(GONE);
                ll_fill_quint_fore.setVisibility(GONE);
                ll_fill_quint_five.setVisibility(GONE);
                ll_fill_quint_six.setVisibility(GONE);

            }
            if (selectBeans.size() == 1) {
                tv_fill_quint_one.setText(answers.get(0));
                ll_fill_quint_two.setVisibility(GONE);
                ll_fill_quint_three.setVisibility(GONE);
                ll_fill_quint_fore.setVisibility(GONE);
                ll_fill_quint_five.setVisibility(GONE);
                ll_fill_quint_six.setVisibility(GONE);


            }

        }
    }
}
