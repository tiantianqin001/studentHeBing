package com.telit.zhkt_three.CustomView;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.telit.zhkt_three.Adapter.QuestionAdapter.RVQuestionTvAnswerAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.MulitBean;
import com.telit.zhkt_three.JavaBean.SingleBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import java.util.ArrayList;
import java.util.List;

public class TowMulipleChoiseView extends LinearLayout {
    private static final String TAG = "MulipleChoiseView";
    private Context mContext;

    private final LinearLayout ll_single_image_one;
    private final LinearLayout ll_single_image_two;
    private final LinearLayout ll_single_image_three;
    private final LinearLayout ll_single_image_fore;
    private final LinearLayout ll_single_image_five;
    private final LinearLayout ll_single_image_sex;
    private final TextView tv_single_image_one;
    private final TextView tv_single_image_two;
    private final TextView tv_single_image_three;
    private final TextView tv_single_image_fore;
    private final TextView tv_single_image_five;
    private final TextView tv_single_image_sex;
    private final TextView practice_select_judge_answer;
    private final TextView practice_head_index;
    private String taskStatus;
    private LocalTextAnswersBean linkLocal;

    public TowMulipleChoiseView(Context context) {
        this(context, null);
    }

    public TowMulipleChoiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TowMulipleChoiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;


        View itemView = LayoutInflater.from(context).inflate(R.layout.mulit_choose_image_layout,
                this, true);



        ll_single_image_one = itemView.findViewById(R.id.ll_single_image_one);
        ll_single_image_two = itemView.findViewById(R.id.ll_single_image_two);
        ll_single_image_three = itemView.findViewById(R.id.ll_single_image_three);
        ll_single_image_fore = itemView.findViewById(R.id.ll_single_image_fore);
        ll_single_image_five = itemView.findViewById(R.id.ll_single_image_five);
        ll_single_image_sex = itemView.findViewById(R.id.ll_single_image_sex);


        tv_single_image_one = itemView.findViewById(R.id.tv_single_image_one);
        tv_single_image_two = itemView.findViewById(R.id.tv_single_image_two);
        tv_single_image_three = itemView.findViewById(R.id.tv_single_image_three);
        tv_single_image_fore = itemView.findViewById(R.id.tv_single_image_fore);
        tv_single_image_five = itemView.findViewById(R.id.tv_single_image_five);
        tv_single_image_sex = itemView.findViewById(R.id.tv_single_image_sex);
        practice_select_judge_answer = itemView.findViewById(R.id.practice_select_judge_answer);
        practice_head_index = itemView.findViewById(R.id.practice_head_index);

    }

    public void setTaskStatus(String taskStatus) {

        this.taskStatus = taskStatus;
    }

    //设置数据
    public void setViewData(List<QuestionInfo.SelectBean> selectBeans, List<QuestionInfo> questionInfoList, int i,
                            String homeworkId, int homeWorkType) {
        if (selectBeans.size() == 1) {
            ll_single_image_one.setVisibility(VISIBLE);
        } else if (selectBeans.size() == 2) {
            ll_single_image_one.setVisibility(VISIBLE);
            ll_single_image_two.setVisibility(VISIBLE);
        } else if (selectBeans.size() == 3) {
            ll_single_image_one.setVisibility(VISIBLE);
            ll_single_image_two.setVisibility(VISIBLE);
            ll_single_image_three.setVisibility(VISIBLE);
        } else if (selectBeans.size() == 4) {
            ll_single_image_one.setVisibility(VISIBLE);
            ll_single_image_two.setVisibility(VISIBLE);
            ll_single_image_three.setVisibility(VISIBLE);
            ll_single_image_fore.setVisibility(VISIBLE);

        } else if (selectBeans.size() == 5) {
            ll_single_image_one.setVisibility(VISIBLE);
            ll_single_image_two.setVisibility(VISIBLE);
            ll_single_image_three.setVisibility(VISIBLE);
            ll_single_image_fore.setVisibility(VISIBLE);
            ll_single_image_five.setVisibility(VISIBLE);
        } else if (selectBeans.size() == 6) {
            ll_single_image_one.setVisibility(VISIBLE);
            ll_single_image_two.setVisibility(VISIBLE);
            ll_single_image_three.setVisibility(VISIBLE);
            ll_single_image_fore.setVisibility(VISIBLE);
            ll_single_image_five.setVisibility(VISIBLE);
            ll_single_image_sex.setVisibility(VISIBLE);
        }

        //0未提交  1 已提交  2 已批阅
        //设置作业头信息
        practice_head_index.setText("第" + (i + 1) + "题 共" + questionInfoList.size() + "题");
        if (taskStatus.equals(Constant.Todo_Status) || taskStatus.equals(Constant.Save_Status)) {
            List<String> checkViews = new ArrayList<>();

            if (taskStatus.equals(Constant.Todo_Status)){
                //答案的回显
                //查询保存的答案,这是多选，所以存在多个答案

                //只有是作业显示  互动不获取
                if (homeWorkType == 1){
                    linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                            .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                    LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                    LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
                }


                if (linkLocal != null && linkLocal.questionId.equals(questionInfoList.get(i).getId()) && linkLocal.getList() != null) {
                    Log.i(TAG, "onBindViewHolder: " + linkLocal);
                    List<String> wordsType = new ArrayList<>();
                    for (int j = 0; j < linkLocal.getList().size(); j++) {
                        wordsType.add(linkLocal.getList().get(j).content);
                    }

                    if (wordsType.contains("A")) {
                        tv_single_image_one.setSelected(true);
                        checkViews.add("tv_single_image_one");
                    }
                    if (wordsType.contains("B")) {
                        tv_single_image_two.setSelected(true);
                        checkViews.add("tv_single_image_two");
                    }
                    if (wordsType.contains("C")) {
                        tv_single_image_three.setSelected(true);
                        checkViews.add("tv_single_image_three");
                    }
                    if (wordsType.contains("D")) {
                        tv_single_image_fore.setSelected(true);
                        checkViews.add("tv_single_image_fore");
                    }
                    if (wordsType.contains("E")) {
                        tv_single_image_five.setSelected(true);
                        checkViews.add("tv_single_image_five");
                    }
                    if (wordsType.contains("F")) {
                        tv_single_image_sex.setSelected(true);
                        checkViews.add("tv_single_image_sex");

                    }

                    if (wordsType.size() == 0) {
                        if (selectBeans.size() == 1) {
                            tv_single_image_one.setSelected(false);
                        } else if (selectBeans.size() == 2) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                        } else if (selectBeans.size() == 3) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                        } else if (selectBeans.size() == 4) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                            tv_single_image_fore.setSelected(false);
                        } else if (selectBeans.size() == 5) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                            tv_single_image_fore.setSelected(false);
                            tv_single_image_five.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                            tv_single_image_fore.setSelected(false);
                            tv_single_image_five.setSelected(false);
                            tv_single_image_sex.setSelected(false);
                        }
                    } else {
                        if (wordsType.contains("A")) {
                            tv_single_image_one.setSelected(true);
                        }
                        if (wordsType.contains("B")) {
                            tv_single_image_two.setSelected(true);
                        }
                        if (wordsType.contains("C")) {
                            tv_single_image_three.setSelected(true);
                        }
                        if (wordsType.contains("D")) {
                            tv_single_image_fore.setSelected(true);
                        }
                        if (wordsType.contains("E")) {
                            tv_single_image_five.setSelected(true);
                        }
                        if (wordsType.contains("F")) {
                            tv_single_image_sex.setSelected(true);
                        }
                    }
                }

                //去掉复用的问题
                List<MulitBean> mulitBeans = MyApplication.getInstance().getDaoSession().getMulitBeanDao().loadAll();
                List<String> contents = new ArrayList<>();
                for (int j = 0; j < mulitBeans.size(); j++) {
                    String id = questionInfoList.get(i).getId();
                    if (mulitBeans.get(j).getId().equals(questionInfoList.get(i).getId())) {
                        String checkViewsCount = mulitBeans.get(j).getCheckViews();
                        if (checkViewsCount.contains("|")) {
                            checkViewsCount = checkViewsCount.substring(0, checkViewsCount.length() - 1);
                            String[] strings = checkViewsCount.split("|");
                            for (int k = 0; k < strings.length; k++) {
                                contents.add(strings[k]);
                            }
                        } else {
                            contents.add(checkViewsCount);
                        }

                        if (contents.contains("A")) {
                            if (selectBeans.size() == 1) {
                                tv_single_image_one.setSelected(true);
                            } else if (selectBeans.size() == 2) {
                                tv_single_image_one.setSelected(true);

                            } else if (selectBeans.size() == 3) {
                                tv_single_image_one.setSelected(true);

                            } else if (selectBeans.size() == 4) {
                                tv_single_image_one.setSelected(true);

                            } else if (selectBeans.size() == 5) {
                                tv_single_image_one.setSelected(true);

                            } else if (selectBeans.size() == 6) {
                                tv_single_image_one.setSelected(true);

                            }

                        }
                        if (contents.contains("B")) {
                            if (selectBeans.size() == 2) {

                                tv_single_image_two.setSelected(true);
                            } else if (selectBeans.size() == 3) {
                                tv_single_image_two.setSelected(true);
                            } else if (selectBeans.size() == 4) {
                                tv_single_image_two.setSelected(true);

                            } else if (selectBeans.size() == 5) {

                                tv_single_image_two.setSelected(true);

                            } else if (selectBeans.size() == 6) {

                                tv_single_image_two.setSelected(true);

                            }


                        }
                        if (contents.contains("C")) {
                            if (selectBeans.size() == 3) {

                                tv_single_image_three.setSelected(true);
                            } else if (selectBeans.size() == 4) {

                                tv_single_image_three.setSelected(true);

                            } else if (selectBeans.size() == 5) {

                                tv_single_image_three.setSelected(true);

                            } else if (selectBeans.size() == 6) {
                                tv_single_image_three.setSelected(true);
                            }


                        }
                        if (contents.contains("D")) {
                            if (selectBeans.size() == 4) {
                                tv_single_image_fore.setSelected(true);
                            } else if (selectBeans.size() == 5) {
                                tv_single_image_fore.setSelected(true);

                            } else if (selectBeans.size() == 6) {
                                tv_single_image_fore.setSelected(true);

                            }


                        }
                        if (contents.contains("E")) {
                            if (selectBeans.size() == 5) {

                                tv_single_image_five.setSelected(true);
                            } else if (selectBeans.size() == 6) {
                                tv_single_image_five.setSelected(true);

                            }


                        } else if (contents.contains("F")) {
                            if (selectBeans.size() == 6) {
                                tv_single_image_sex.setSelected(true);
                            }

                        }
                        break;
                    } else {
                        //说明这个题没有被点击，要去掉复用
                        if (selectBeans.size() == 1) {
                            tv_single_image_one.setSelected(false);
                        } else if (selectBeans.size() == 2) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                        } else if (selectBeans.size() == 3) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                        } else if (selectBeans.size() == 4) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                            tv_single_image_fore.setSelected(false);
                        } else if (selectBeans.size() == 5) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                            tv_single_image_fore.setSelected(false);
                            tv_single_image_five.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            tv_single_image_one.setSelected(false);
                            tv_single_image_two.setSelected(false);
                            tv_single_image_three.setSelected(false);
                            tv_single_image_fore.setSelected(false);
                            tv_single_image_five.setSelected(false);
                            tv_single_image_sex.setSelected(false);
                        }
                    }
                }
            }else if (taskStatus.equals(Constant.Save_Status)){
                //答案的保存
                List<String> wordLists = new ArrayList<>();
                List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();
                for (WorkOwnResult workOwnResult : ownList) {
                    wordLists.add(workOwnResult.getAnswerContent());
                }
                // String answerContent = questionInfoList.get(i).getOwnList().get(0).getAnswerContent();
                if (wordLists.contains("A")) {
                    tv_single_image_one.setSelected(true);
                }
                if (wordLists.contains("B")) {
                    tv_single_image_two.setSelected(true);

                }
                if (wordLists.contains("C")) {
                    tv_single_image_three.setSelected(true);

                }
                if (wordLists.contains("D")) {
                    tv_single_image_fore.setSelected(true);

                }
                if (wordLists.contains("E")) {
                    tv_single_image_five.setSelected(true);

                }
                if (wordLists.contains("F")) {
                    tv_single_image_sex.setSelected(true);

                }

                //数据的保存
                LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                localTextAnswersBean.setHomeworkId(questionInfoList.get(i).getHomeworkId());
                localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                localTextAnswersBean.setUserId(UserUtils.getUserId());
                localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                List<AnswerItem> answerItems = new ArrayList<>();
                for (int j = 0; j < ownList.size(); j++) {
                    AnswerItem answerItem = new AnswerItem();
                    answerItem.setItemId(ownList.get(j).getAnswerId());
                    answerItem.setContent(ownList.get(j).getAnswerContent());
                    answerItems.add(answerItem);
                }
                localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                //插入或者更新数据库
                MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

            }


            tv_single_image_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    MulitBean mulitBean = new MulitBean();
                    if (tv_single_image_one.isSelected()) {
                        tv_single_image_one.setSelected(false);
                        checkViews.remove("tv_single_image_one");

                    } else {
                        tv_single_image_one.setSelected(true);
                        checkViews.add("tv_single_image_one");
                    }
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    for (int j = 0; j < checkViews.size(); j++) {
                        String word = checkViews.get(j);
                        if (word.equals("tv_single_image_one")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_two")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_three")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_fore")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_five")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_sex")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);

                        }

                        localTextAnswersBean.setList(answerItems);
                    }

                    mulitBean.setId(questionInfoList.get(i).getId());
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < answerItems.size(); j++) {
                        stringBuffer.append(answerItems.get(j).content);
                        stringBuffer.append("|");
                    }
                    mulitBean.setCheckViews(stringBuffer.toString());
                    MyApplication.getInstance().getDaoSession().getMulitBeanDao().insertOrReplace(mulitBean);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                }
            });
            tv_single_image_two.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    MulitBean mulitBean = new MulitBean();
                    if (tv_single_image_two.isSelected()) {
                        tv_single_image_two.setSelected(false);
                        checkViews.remove("tv_single_image_two");

                    } else {
                        tv_single_image_two.setSelected(true);
                        checkViews.add("tv_single_image_two");
                    }
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    for (int j = 0; j < checkViews.size(); j++) {
                        String word = checkViews.get(j);
                        if (word.equals("tv_single_image_one")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_two")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_three")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_fore")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_five")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_sex")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);

                        }

                        localTextAnswersBean.setList(answerItems);
                    }
                    mulitBean.setId(questionInfoList.get(i).getId());
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < answerItems.size(); j++) {
                        stringBuffer.append(answerItems.get(j).content);
                        stringBuffer.append("|");
                    }
                    mulitBean.setCheckViews(stringBuffer.toString());
                    MyApplication.getInstance().getDaoSession().getMulitBeanDao().insertOrReplace(mulitBean);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            tv_single_image_three.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    MulitBean mulitBean = new MulitBean();
                    if (tv_single_image_three.isSelected()) {
                        tv_single_image_three.setSelected(false);
                        checkViews.remove("tv_single_image_three");

                    } else {
                        tv_single_image_three.setSelected(true);
                        checkViews.add("tv_single_image_three");

                    }
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    for (int j = 0; j < checkViews.size(); j++) {
                        String word = checkViews.get(j);
                        if (word.equals("tv_single_image_one")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_two")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_three")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_fore")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_five")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_sex")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);

                        }

                        localTextAnswersBean.setList(answerItems);
                    }
                    mulitBean.setId(questionInfoList.get(i).getId());
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < answerItems.size(); j++) {
                        stringBuffer.append(answerItems.get(j).content);
                        stringBuffer.append("|");
                    }
                    mulitBean.setCheckViews(stringBuffer.toString());
                    MyApplication.getInstance().getDaoSession().getMulitBeanDao().insertOrReplace(mulitBean);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            tv_single_image_fore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    MulitBean mulitBean = new MulitBean();
                    if (tv_single_image_fore.isSelected()) {
                        tv_single_image_fore.setSelected(false);
                        checkViews.remove("tv_single_image_fore");

                    } else {
                        tv_single_image_fore.setSelected(true);
                        checkViews.add("tv_single_image_fore");
                    }
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    for (int j = 0; j < checkViews.size(); j++) {
                        String word = checkViews.get(j);
                        if (word.equals("tv_single_image_one")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_two")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_three")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_fore")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_five")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_sex")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);

                        }

                        localTextAnswersBean.setList(answerItems);
                    }
                    mulitBean.setId(questionInfoList.get(i).getId());
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < answerItems.size(); j++) {
                        stringBuffer.append(answerItems.get(j).content);
                        stringBuffer.append("|");
                    }
                    mulitBean.setCheckViews(stringBuffer.toString());
                    MyApplication.getInstance().getDaoSession().getMulitBeanDao().insertOrReplace(mulitBean);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            tv_single_image_five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    MulitBean mulitBean = new MulitBean();
                    if (tv_single_image_five.isSelected()) {
                        tv_single_image_five.setSelected(false);
                        checkViews.remove("tv_single_image_five");

                    } else {
                        tv_single_image_five.setSelected(true);
                        checkViews.add("tv_single_image_five");

                    }
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    for (int j = 0; j < checkViews.size(); j++) {
                        String word = checkViews.get(j);
                        if (word.equals("tv_single_image_one")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_two")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_three")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_fore")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_five")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_sex")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);

                        }

                        localTextAnswersBean.setList(answerItems);
                    }
                    mulitBean.setId(questionInfoList.get(i).getId());
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < answerItems.size(); j++) {
                        stringBuffer.append(answerItems.get(j).content);
                        stringBuffer.append("|");
                    }
                    mulitBean.setCheckViews(stringBuffer.toString());
                    MyApplication.getInstance().getDaoSession().getMulitBeanDao().insertOrReplace(mulitBean);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
            tv_single_image_sex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //-------------------------答案保存，依据作业题目id
                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                    MulitBean mulitBean = new MulitBean();
                    if (tv_single_image_sex.isSelected()) {
                        tv_single_image_sex.setSelected(false);
                        checkViews.remove("tv_single_image_sex");

                    } else {
                        tv_single_image_sex.setSelected(true);
                        checkViews.add("tv_single_image_sex");
                    }
                    localTextAnswersBean.setHomeworkId(homeworkId);
                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                    List<AnswerItem> answerItems = new ArrayList<>();
                    for (int j = 0; j < checkViews.size(); j++) {
                        String word = checkViews.get(j);
                        if (word.equals("tv_single_image_one")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_two")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_three")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_fore")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_five")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);

                        } else if (word.equals("tv_single_image_sex")) {
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);

                        }

                        localTextAnswersBean.setList(answerItems);
                    }
                    mulitBean.setId(questionInfoList.get(i).getId());
                    StringBuffer stringBuffer = new StringBuffer();
                    for (int j = 0; j < answerItems.size(); j++) {
                        stringBuffer.append(answerItems.get(j).content);
                        stringBuffer.append("|");
                    }
                    mulitBean.setCheckViews(stringBuffer.toString());
                    MyApplication.getInstance().getDaoSession().getMulitBeanDao().insertOrReplace(mulitBean);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                }
            });
        } else {

            //作业单选已经批改了的状态  显示学生做的答案和正确答案
            practice_select_judge_answer.setVisibility(VISIBLE);
            List<String> wordLists = new ArrayList<>();
            //学生回答的答案
            if (taskStatus.equals("2") || taskStatus.equals("1") ) {
                List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();
                for (WorkOwnResult workOwnResult : ownList) {
                    wordLists.add(workOwnResult.getAnswerContent());
                }
                // String answerContent = questionInfoList.get(i).getOwnList().get(0).getAnswerContent();
                if (wordLists.contains("A")) {
                    tv_single_image_one.setSelected(true);
                }
                if (wordLists.contains("B")) {
                    tv_single_image_two.setSelected(true);

                }
                if (wordLists.contains("C")) {
                    tv_single_image_three.setSelected(true);

                }
                if (wordLists.contains("D")) {
                    tv_single_image_fore.setSelected(true);

                }
                if (wordLists.contains("E")) {
                    tv_single_image_five.setSelected(true);

                }
                if (wordLists.contains("F")) {
                    tv_single_image_sex.setSelected(true);

                }

                practice_select_judge_answer.setText("正确答案：" + questionInfoList.get(i).getAnswer());
            }else if (taskStatus.equals(Constant.Retry_Status)){
                //如果当前状态是打回重做
                if (questionInfoList!=null && questionInfoList.size()>0){
                    QuestionInfo questionInfo = questionInfoList.get(i);
                    List<WorkOwnResult> ownList = questionInfo.getOwnList();
                    if (ownList!=null && ownList.size()>0){
                        for (WorkOwnResult workOwnResult : ownList) {
                            wordLists.add(workOwnResult.getAnswerContent());
                        }

                        if (wordLists.contains("A")) {
                            tv_single_image_one.setSelected(true);
                        }
                        if (wordLists.contains("B")) {
                            tv_single_image_two.setSelected(true);

                        }
                        if (wordLists.contains("C")) {
                            tv_single_image_three.setSelected(true);

                        }
                        if (wordLists.contains("D")) {
                            tv_single_image_fore.setSelected(true);

                        }
                        if (wordLists.contains("E")) {
                            tv_single_image_five.setSelected(true);

                        }
                        if (wordLists.contains("F")) {
                            tv_single_image_sex.setSelected(true);

                        }
                    }
                }
            }

        }
    }
}
