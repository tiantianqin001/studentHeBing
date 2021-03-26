package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Path;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;


import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.TotalQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.matching.ToLineView;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.AnswerItem;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.LineMatchBean;
import com.telit.zhkt_three.JavaBean.SingleBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 16:44
 * <p>
 * 注意：这个和拍照出题公用Adapter，所以这里判断是否是错题集入口分别进入拍照出题/题库出题
 */
public class RVQuestionTvAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final String TAG = "RVQuestionTvAnswerAdapter";
    private Context mContext;

    //0未提交  1 已提交  2 已批阅
    private String taskStatus;

    //是否是错题集展示界面
    private boolean isMistakesShown;
    private int types;
    private String comType;

    /**
     * 是否是图片出题模式即随堂练习模式
     */
    private boolean isImageTask;

    private List<QuestionInfo> questionInfoList;

    private String homeworkId;

    private String showAnswerDate;

    /**
     * 选中的第一个item
     */
    private View firstChooseView = null;

    /**
     * 如果homeworkid为空字符串表示没有homeworkid，在QuestionInfo中存在homeworkid
     */
    public void setQuestionInfoList(List<QuestionInfo> questionInfoList, String homeworkId, String showAnswerDate) {
        //这个是提真正的数据
        this.questionInfoList = questionInfoList;
        this.homeworkId = homeworkId;
        this.showAnswerDate = showAnswerDate;
    }

    private String xd;
    private String chid;
    private String difficulty;
    private String type;

    //保存连线题 在复用后不能显示的问题
    private List<LineMatchBean> lineMatchs = new ArrayList<>();
    //单选题，复用的问题
    private List<SingleBean> singleBeans=new ArrayList<>();

    //保存作答痕迹 连线题
    private String saveTrack;

    /**
     * 塞入错题巩固必传信息:
     * 学段、学科、难易度以及题型
     */
    public void fetchNeedParam(String xd, String subjective, String difficulty, String questionType) {
        this.xd = xd;
        chid = subjective;
        this.difficulty = difficulty;
        type = questionType;
        QZXTools.logE("xd=" + xd + ";chid=" + chid + ";difficulty=" + difficulty + ";type=" + type, null);
    }

    /**
     * @param status          作业的状态
     * @param isImageQuestion 是否图片出题模式
     * @param mistakesShown   是否是错题集展示界面
     * @param types
     * @param comType
     */
    public RVQuestionTvAnswerAdapter(Context context, String status, boolean isImageQuestion, boolean mistakesShown, int types, String comType) {
        mContext = context;
        taskStatus = status;
        isImageTask = isImageQuestion;
        isMistakesShown = mistakesShown;
        this.types = types;
        this.comType = comType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == Constant.Single_Choose) {
            return new SingleChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_image_layout, viewGroup, false));
        } else if (i == Constant.Fill_Blank) {
            return new FillBlankHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.fill_blank_image_layout, viewGroup, false));
        } else if (i == Constant.Subject_Item) {
            return new SubjectItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.subject_item_inage_layout, viewGroup, false));
        } else if (i == Constant.Linked_Line) {
            return new LinkedLineHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.linked_line_image_layout, viewGroup, false));
        } else if (i == Constant.Judge_Item) {
            return new JudgeItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.judgeselect_two_image_layout, viewGroup, false));
        } else if (i == Constant.Multi_Choose) {
            return new MultiChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.mulit_choose_image_layout, viewGroup, false));
        }
        return null;

    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SingleChooseHolder) {
         //   viewHolder.setIsRecyclable(false);
            //单选题
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            if (selectBeans.size() == 1) {
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 2) {
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);

            } else if (selectBeans.size() == 3) {
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 4) {
                ((SingleChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 5) {
                ((SingleChooseHolder) viewHolder).ll_single_image_five.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);

            } else if (selectBeans.size() == 6) {

                ((SingleChooseHolder) viewHolder).ll_single_image_sex.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_five.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
            }

            //0未提交  1 已提交  2 已批阅
            //设置作业头信息
            ((SingleChooseHolder) viewHolder).practice_head_index.setText("第"+(i+1)+"题 共"+questionInfoList.size()+"题");

            if (taskStatus.equals(Constant.Todo_Status)) {
                //答案的回显
                //查询保存的答案,这是多选，所以存在多个答案
                LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                        .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                if (linkLocal!=null && linkLocal.questionId.equals(questionInfoList.get(i).getId())){
                    Log.i(TAG, "onBindViewHolder: "+linkLocal);
                    String content = linkLocal.getList().get(0).content;
                    if (content.equals("A")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                    }else if (content.equals("B")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                    }else if (content.equals("C")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);
                    }else if (content.equals("D")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);
                    }else if (content.equals("E")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(true);
                    }else if (content.equals("F")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(true);

                    }
                }else {
                    if (selectBeans.size() == 1) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                    } else  if (selectBeans.size() == 2) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                    } else if (selectBeans.size() == 3) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                    } else if (selectBeans.size() == 4) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                    } else if (selectBeans.size() == 5) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                    } else if (selectBeans.size() == 6) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                        ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                    }
                }



                ((SingleChooseHolder) viewHolder).tv_single_image_one.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        SingleBean singleBean=new SingleBean();
                        if (((SingleChooseHolder) viewHolder).tv_single_image_one.isSelected()) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);
                            singleBeans.remove(singleBean);
                        } else {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);

                            localTextAnswersBean.setHomeworkId(homeworkId);
                            localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                            localTextAnswersBean.setUserId(UserUtils.getUserId());
                            localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                            List<AnswerItem> answerItems = new ArrayList<>();
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(0).getId());
                            answerItem.setContent(selectBeans.get(0).getOptions());
                            answerItems.add(answerItem);
                            localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                            //插入或者更新数据库
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                            singleBean.setId(questionInfoList.get(i).getId());
                            singleBean.setPosition(0);
                            singleBeans.add(singleBean);


                        }

                        if (selectBeans.size() == 2) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        } else if (selectBeans.size() == 3) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        } else if (selectBeans.size() == 4) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                        } else if (selectBeans.size() == 5) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                        }


                    }
                });
                ((SingleChooseHolder) viewHolder).tv_single_image_two.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        SingleBean singleBean=new SingleBean();
                        if (((SingleChooseHolder) viewHolder).tv_single_image_two.isSelected()) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);
                            singleBeans.remove(singleBean);
                        } else {
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);

                            localTextAnswersBean.setHomeworkId(homeworkId);
                            localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                            localTextAnswersBean.setUserId(UserUtils.getUserId());
                            localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                            List<AnswerItem> answerItems = new ArrayList<>();
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(1).getId());
                            answerItem.setContent(selectBeans.get(1).getOptions());
                            answerItems.add(answerItem);
                            localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                            //插入或者更新数据库
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                            singleBean.setId(questionInfoList.get(i).getId());
                            singleBean.setPosition(1);
                            singleBeans.add(singleBean);
                        }

                        if (selectBeans.size() == 2) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        } else if (selectBeans.size() == 3) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        } else if (selectBeans.size() == 4) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                        } else if (selectBeans.size() == 5) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                        }
                    }
                });
                ((SingleChooseHolder) viewHolder).tv_single_image_three.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        SingleBean singleBean=new SingleBean();
                        if (((SingleChooseHolder) viewHolder).tv_single_image_three.isSelected()) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);
                            singleBeans.remove(singleBean);
                        } else {
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);

                            localTextAnswersBean.setHomeworkId(homeworkId);
                            localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                            localTextAnswersBean.setUserId(UserUtils.getUserId());
                            localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                            List<AnswerItem> answerItems = new ArrayList<>();
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(2).getId());
                            answerItem.setContent(selectBeans.get(2).getOptions());
                            answerItems.add(answerItem);
                            localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                            //插入或者更新数据库
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                            singleBean.setId(questionInfoList.get(i).getId());
                            singleBean.setPosition(2);
                            singleBeans.add(singleBean);
                        }

                        if (selectBeans.size() == 3) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        } else if (selectBeans.size() == 4) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                        } else if (selectBeans.size() == 5) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                        }
                    }
                });
                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        SingleBean singleBean=new SingleBean();
                        if (((SingleChooseHolder) viewHolder).tv_single_image_fore.isSelected()) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);
                            singleBeans.remove(singleBean);
                        } else {
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);

                            localTextAnswersBean.setHomeworkId(homeworkId);
                            localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                            localTextAnswersBean.setUserId(UserUtils.getUserId());
                            localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                            List<AnswerItem> answerItems = new ArrayList<>();
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(3).getId());
                            answerItem.setContent(selectBeans.get(3).getOptions());
                            answerItems.add(answerItem);
                            localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                            //插入或者更新数据库
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                            singleBean.setId(questionInfoList.get(i).getId());
                            singleBean.setPosition(3);
                            singleBeans.add(singleBean);
                        }

                        if (selectBeans.size() == 4) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        } else if (selectBeans.size() == 5) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                        }
                    }
                });
                ((SingleChooseHolder) viewHolder).tv_single_image_five.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        SingleBean singleBean=new SingleBean();
                        if (((SingleChooseHolder) viewHolder).tv_single_image_five.isSelected()) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);
                            singleBeans.remove(singleBean);
                        } else {
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(true);

                            localTextAnswersBean.setHomeworkId(homeworkId);
                            localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                            localTextAnswersBean.setUserId(UserUtils.getUserId());
                            localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                            List<AnswerItem> answerItems = new ArrayList<>();
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(4).getId());
                            answerItem.setContent(selectBeans.get(4).getOptions());
                            answerItems.add(answerItem);
                            localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                            //插入或者更新数据库
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                            singleBean.setId(questionInfoList.get(i).getId());
                            singleBean.setPosition(4);
                            singleBeans.add(singleBean);
                        }
                        if (selectBeans.size() == 5) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        } else if (selectBeans.size() == 6) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                        }
                    }
                });
                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        SingleBean singleBean=new SingleBean();
                        if (((SingleChooseHolder) viewHolder).tv_single_image_sex.isSelected()) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().delete(localTextAnswersBean);

                            singleBeans.remove(singleBean);
                        } else {
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(true);

                            localTextAnswersBean.setHomeworkId(homeworkId);
                            localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                            localTextAnswersBean.setUserId(UserUtils.getUserId());
                            localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                            List<AnswerItem> answerItems = new ArrayList<>();
                            AnswerItem answerItem = new AnswerItem();
                            answerItem.setItemId(selectBeans.get(5).getId());
                            answerItem.setContent(selectBeans.get(5).getOptions());
                            answerItems.add(answerItem);
                            localTextAnswersBean.setList(answerItems);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                            //插入或者更新数据库
                            MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                            singleBean.setId(questionInfoList.get(i).getId());
                            singleBean.setPosition(5);
                            singleBeans.add(singleBean);
                        }

                        if (selectBeans.size() == 6) {
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        }
                    }
                });

                //去掉复用的问题  todo  目前singleBeans  在退出再打开清空了，要想解决就存到数据库中
                for (int j = 0; j <singleBeans.size() ; j++) {
                    String id = questionInfoList.get(i).getId();
                    if (singleBeans.get(j).getId().equals(questionInfoList.get(i).getId())){
                        int position = singleBeans.get(j).getPosition();
                        if (position == 0){
                            if (singleBeans.size() == 1){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                            }else if (selectBeans.size() == 2){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            }else if (selectBeans.size() == 3){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            }else if (selectBeans.size() == 4){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            }else if (selectBeans.size() == 5){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            }else if (selectBeans.size() == 6){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                            }

                        }else if (position == 1){
                             if (selectBeans.size() == 2){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                            }else if (selectBeans.size() == 3){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            }else if (selectBeans.size() == 4){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            }else if (selectBeans.size() == 5){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            }else if (selectBeans.size() == 6){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                            }




                        }else if (position == 2){
                             if (selectBeans.size() == 3){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);
                            }else if (selectBeans.size() == 4){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            }else if (selectBeans.size() == 5){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            }else if (selectBeans.size() == 6){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                 ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                            }


                        }else if (position == 3){
                            if (selectBeans.size() == 4){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);
                            }else if (selectBeans.size() == 5){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            }else if (selectBeans.size() == 6){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                            }


                        }else if (position == 4){
                            if (selectBeans.size() == 5){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(true);
                            }else if (selectBeans.size() == 6){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                            }


                        }else if (position == 5){
                            if (selectBeans.size() == 6){
                                ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                                ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(true);
                                ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(true);
                            }

                        }
                        break;
                    }else {
                        //说明这个题没有被点击，要去掉复用
                        if (selectBeans.size() == 1){
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                        }else if (selectBeans.size() == 2){
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                        }else if (selectBeans.size() == 3){
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                        }else if (selectBeans.size() == 4){
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                        }else if (selectBeans.size() == 5){
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                        }else if (selectBeans.size() == 6){
                            ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);
                            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);
                        }
                    }
                }


            }else {
                //作业单选已经批改了的状态  显示学生做的答案和正确答案
                ((SingleChooseHolder) viewHolder).practice_select_judge_answer.setVisibility(View.VISIBLE);
                //学生回答的答案
                if (taskStatus.equals("2")){
                    String answerContent = questionInfoList.get(i).getOwnList().get(0).getAnswerContent();
                    if (answerContent.equals("A")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                    }else if (answerContent.equals("B")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);

                    }else if (answerContent.equals("C")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(true);

                    }else if (answerContent.equals("D")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);

                    }else if (answerContent.equals("E")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(true);

                    }else if (answerContent.equals("F")){
                        ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(true);

                    }

                    ((SingleChooseHolder) viewHolder).practice_select_judge_answer.setText("正确答案："+questionInfoList.get(i).getAnswer());
                }




            }



        } else if (viewHolder instanceof MultiChooseHolder) {
            //多选题
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            if (selectBeans.size() == 1) {
                ((MultiChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 2) {
                ((MultiChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 3) {
                ((MultiChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 4) {
                ((MultiChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);

            } else if (selectBeans.size() == 5) {
                ((MultiChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_five.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 6) {
                ((MultiChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_five.setVisibility(View.VISIBLE);
                ((MultiChooseHolder) viewHolder).ll_single_image_sex.setVisibility(View.VISIBLE);
            }

            ((MultiChooseHolder) viewHolder).tv_single_image_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_one.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_one.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder).tv_single_image_one.setSelected(true);
                    }

                }
            });
            ((MultiChooseHolder) viewHolder).tv_single_image_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_two.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_two.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                    }
                }
            });
            ((MultiChooseHolder) viewHolder).tv_single_image_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_three.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_three.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder).tv_single_image_three.setSelected(true);
                    }
                }
            });
            ((MultiChooseHolder) viewHolder).tv_single_image_fore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_fore.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);
                    }
                }
            });
            ((MultiChooseHolder) viewHolder).tv_single_image_five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_five.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_five.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder).tv_single_image_five.setSelected(true);
                    }
                }
            });

            ((MultiChooseHolder) viewHolder).tv_single_image_sex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_sex.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder).tv_single_image_sex.setSelected(true);
                    }
                }
            });
        } else if (viewHolder instanceof FillBlankHolder) {
            //填空题
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            if (selectBeans.size() == 1) {
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);

            } else if (selectBeans.size() == 2) {
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);

            } else if (selectBeans.size() == 3) {
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);

            } else if (selectBeans.size() == 4) {
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_fore.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 5) {
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_fore.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_five.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 6) {
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_fore.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_five.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_sex.setVisibility(View.VISIBLE);
            }

        } else if (viewHolder instanceof JudgeItemHolder) {
            //判断题
            // ((JudgeItemHolder) viewHolder)
        } else if (viewHolder instanceof SubjectItemHolder) {
            //主观题

        } else if (viewHolder instanceof LinkedLineHolder) {
            //连线题
            // viewHolder.setIsRecyclable(false);
            QZXTools.logE("ToLine viewHolder instanceof LinkedLineHolder......" + questionInfoList.get(i), null);

            Log.i("qin008", "onBindViewHolder: " + questionInfoList.get(i));
            List<Integer> leftPositions = new ArrayList<>();
            List<Integer> rightPositions = new ArrayList<>();
            //作业回显正确答案 左边view 的集合，右边view 的集合
            List<View> leftViews=new ArrayList<>();
            //连线题
            List<QuestionInfo.LeftListBean> leftList = questionInfoList.get(i).getLeftList();
            List<QuestionInfo.RightListBean> rightList = questionInfoList.get(i).getRightList();
            LineLeftAdapter lineLeftAdapter = new LineLeftAdapter(mContext, leftList, rightList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext) {
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            ((LinkedLineHolder) viewHolder).rv_matching_show.setLayoutManager(layoutManager);
            ((LinkedLineHolder) viewHolder).rv_matching_show.setAdapter(lineLeftAdapter);


            //0未提交  1 已提交  2 已批阅
             if (taskStatus.equals(Constant.Todo_Status)){
                 //重置的点击事件
                 ((LinkedLineHolder) viewHolder).matching_reset.setOnClickListener(new View.OnClickListener() {
                     @Override
                     public void onClick(View v) {
                         if (firstChooseView != null) {
                             firstChooseView = null;
                         }
                         saveTrack = "";
                         ((LinkedLineHolder) viewHolder).matching_toLine.resetDrawLine();

                         //同时要清空保存的集合数据
                         Iterator<LineMatchBean> iterator = lineMatchs.iterator();
                         while (iterator.hasNext()) {
                             LineMatchBean lineMatchBean = iterator.next();
                             if (lineMatchBean.getTypeId().equals(questionInfoList.get(i).getId())) {
                                 iterator.remove();
                                 MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().delete(lineMatchBean);
                             }
                         }

                         MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().deleteAll();
                         //TODO 重置要删除数据库中的数据
                         MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().deleteAll();
                         //todo 现在的问题是回显返回的saveTrack  还不对，还在想怎么正确获取

                     }


                 });

                 //左边点击连线
                 lineLeftAdapter.setOnLeftOnClickListener(new LineLeftAdapter.onLeftOnClickListener() {
                     private TextView rightTextView;
                     private TextView liftTextView;
                     float sx;
                     float sy;
                     float ex;
                     float ey;
                     Path path = null;
                     int leftPosition;
                     int rightPosition;
                     boolean isLiftContains = false;
                     boolean isRightContains = false;

                     @Override
                     public void onLeftItemCheck(int position) {
                         //  ((LinkedLineHolder) viewHolder).matching_toLine.resetDrawLine();
                         View view = layoutManager.findViewByPosition(position);
                         //从左边开始点击画线
                         StringBuffer stringBuffer = new StringBuffer();
                         //清空一下
                         stringBuffer.setLength(0);
                         LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                 .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                         LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                         LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                         Log.i(TAG, "onBindViewHolder: " + linkLocal);
                         if (linkLocal != null) {
                             //回显上次点击的数据  根据id 获取是左边的列表还是右边的列表
                             //获取到下标，然后开始连线
                             String ownAnswer = linkLocal.getAnswerContent();
                             stringBuffer.append(ownAnswer);
                         }
                         if (!TextUtils.isEmpty(saveTrack)) {
                             //stringBuffer.append(saveTrack);
                             stringBuffer.append("|");
                         }

                         liftTextView = view.findViewById(R.id.tv_item_line_word_left);
                         if (isRightContains) {
                             //点击左边第一个已经被连线了，右边的也就不能连线了
                             isRightContains = false;
                             return;
                         }
                         //同一区域的不能选中
                         if (firstChooseView != null && firstChooseView == liftTextView) {
                             isLiftContains = true;
                             return;
                         }
                         isLiftContains = false;
                         //如果集合中有视图表示已经连接过了,或者正在动画中也不执行
                         if (leftPositions.contains(position) || ((LinkedLineHolder) viewHolder).matching_toLine.isAnimRunning()) {
                             return;
                         }


                         if (firstChooseView == null) {
                             firstChooseView = liftTextView;
                             sx = liftTextView.getLeft() + liftTextView.getWidth();
                             sy = view.getTop() + liftTextView.getHeight() * 1.0f / 2.0f;
                             path = new Path();
                             leftPosition = position;
                             liftTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_bg_with_border));
                         }
                         if (firstChooseView == rightTextView) {

                             //点击第一个是右边的view
                             ex = liftTextView.getLeft() + liftTextView.getWidth();
                             ey = view.getTop() + liftTextView.getHeight() * 1.0f / 2.0f;
                             path.addCircle(sx, sy, 10, Path.Direction.CW);
                             path.moveTo(sx, sy);
                             path.lineTo(ex, ey);
                             path.addCircle(ex, ey, 10, Path.Direction.CW);

                             //绘制Path   这里开始画
                             ((LinkedLineHolder) viewHolder).matching_toLine.getDrawPath(path);
                             //初始化左边的view
                             firstChooseView = null;
                             //处理集合中有视图表示已经连接过了就不要再连接了
                             leftPositions.add(position);
                             rightPositions.add(rightPosition);
                             rightTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_item_bg));

                             //保存当前的下表的连线题 状态下的 连线的所有的坐标，然后再滑动的时候回显
                             LineMatchBean lineMatchBean = new LineMatchBean();
                             lineMatchBean.setTypeId(questionInfoList.get(i).getId());
                             lineMatchBean.setPosition(i);
                             lineMatchBean.setStartX(sx);
                             lineMatchBean.setStartY(sy);
                             lineMatchBean.setEndX(ex);
                             lineMatchBean.setEndY(ey);

                             lineMatchBean.setLeftId(leftList.get(position).getId());
                             lineMatchBean.setRightId(rightList.get(rightPosition).getId());

                             lineMatchs.add(lineMatchBean);
                             MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().insertOrReplace(lineMatchBean);
                             // saveTrack 是显示绘制的状态 的保留  点击每一个获取点击的id的保存

                             stringBuffer.append(questionInfoList.get(i).getRightList().get(rightPosition).getId());
                             stringBuffer.append(",");
                             stringBuffer.append(questionInfoList.get(i).getLeftList().get(position).getId());
                             saveTrack = stringBuffer.toString();

                             //连线题把连线画好的线保存数据库下次回显
                             //-------------------------答案保存，依据作业题目id   主要就是这个作业id 不一样
                             LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                             localTextAnswersBean.setHomeworkId(homeworkId);
                             localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                             localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                             localTextAnswersBean.setAnswerContent(saveTrack);
                             localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                             //插入或者更新数据库
                             MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);


                             //关闭的时候保留saveTrack
                             ((LinkedLineHolder) viewHolder).matching_toLine.setLocalSave(homeworkId, questionInfoList.get(i), saveTrack);

                         }


                     }

                     @Override
                     public void onRightItemClick(int position) {
                         RelativeLayout view = (RelativeLayout) layoutManager.findViewByPosition(position);
                         rightTextView = view.findViewById(R.id.tv_item_line_word_right);

                         StringBuffer stringBuffer = new StringBuffer();
                         //清空一下
                         stringBuffer.setLength(0);
                         LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                 .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                         LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                         LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                         Log.i(TAG, "onBindViewHolder: " + linkLocal);
                         if (linkLocal != null) {
                             //回显上次点击的数据  根据id 获取是左边的列表还是右边的列表
                             //获取到下标，然后开始连线
                             String ownAnswer = linkLocal.getAnswerContent();
                             stringBuffer.append(ownAnswer);
                         }
                         if (!TextUtils.isEmpty(saveTrack)) {
                             //  stringBuffer.append(saveTrack);
                             stringBuffer.append("|");
                         }
                         if (isLiftContains) {
                             //点击左边第一个已经被连线了，右边的也就不能连线了 下次再次点击就还原
                             isLiftContains = false;
                             return;
                         }
                         //同一区域的不能选中
                         if (firstChooseView != null && firstChooseView == rightTextView) {
                             return;
                         }

                         //如果集合中有视图表示已经连接过了,或者正在动画中也不执行
                         if (rightPositions.contains(position) || ((LinkedLineHolder) viewHolder).matching_toLine.isAnimRunning()) {
                             isRightContains = true;
                             return;
                         }
                         isRightContains = false;
                         //从右边第一次点击画view
                         if (firstChooseView == null) {
                             firstChooseView = rightTextView;
                             sx = rightTextView.getLeft();
                             sy = view.getTop() + rightTextView.getHeight() * 1.0f / 2.0f;
                             path = new Path();
                             rightPosition = position;
                             rightTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_bg_with_border));
                         }
                         QZXTools.logE("sx=" + sx + ";sy=" + sy + ";ex=" + ex + ";ey=" + ey, null);
                         if (firstChooseView == liftTextView) {

                             //点击第一个是左边的view
                             ex = rightTextView.getLeft();
                             ey = view.getTop() + rightTextView.getHeight() * 1.0f / 2.0f;
                             path.addCircle(sx, sy, 10, Path.Direction.CW);
                             path.moveTo(sx, sy);
                             path.lineTo(ex, ey);
                             path.addCircle(ex, ey, 10, Path.Direction.CW);

                             //绘制Path   这里开始画
                             ((LinkedLineHolder) viewHolder).matching_toLine.getDrawPath(path);
                             //初始化左边的view
                             firstChooseView = null;
                             rightPositions.add(position);
                             leftPositions.add(leftPosition);
                             liftTextView.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_item_bg));

                             //保存当前的下表的连线题 状态下的 连线的所有的坐标，然后再滑动的时候回显
                             LineMatchBean lineMatchBean = new LineMatchBean();
                             //设置当前题型的id
                             lineMatchBean.setTypeId(questionInfoList.get(i).getId());
                             lineMatchBean.setLeftId(leftList.get(leftPosition).getId());
                             lineMatchBean.setRightId(rightList.get(position).getId());
                             lineMatchBean.setPosition(i);
                             lineMatchBean.setStartX(sx);
                             lineMatchBean.setStartY(sy);
                             lineMatchBean.setEndX(ex);
                             lineMatchBean.setEndY(ey);



                             lineMatchs.add(lineMatchBean);
                             //把所有的点保存到数据库
                             MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().insertOrReplace(lineMatchBean);

                             stringBuffer.append(questionInfoList.get(i).getLeftList().get(leftPosition).getId());
                             stringBuffer.append(",");
                             stringBuffer.append(questionInfoList.get(i).getRightList().get(position).getId());
                             saveTrack = stringBuffer.toString();

                             //连线题把连线画好的线保存数据库下次回显
                             //-------------------------答案保存，依据作业题目id   主要就是这个作业id 不一样
                             LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                             localTextAnswersBean.setHomeworkId(homeworkId);
                             localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                             localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                             localTextAnswersBean.setAnswerContent(saveTrack);
                             localTextAnswersBean.setUserId(UserUtils.getUserId());
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                             //插入或者更新数据库
                             MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                             //关闭的时候保留saveTrack
                             ((LinkedLineHolder) viewHolder).matching_toLine.setLocalSave(homeworkId, questionInfoList.get(i), saveTrack);

                         }
                     }
                 });
                 ((LinkedLineHolder) viewHolder).ll_match_bind_tag.setTag(questionInfoList.get(i).getId());
                 ((LinkedLineHolder) viewHolder).rv_matching_show.setLayoutManager(layoutManager);
                 ((LinkedLineHolder) viewHolder).rv_matching_show.setAdapter(lineLeftAdapter);

                 //设置当前连线题是不是已经绘制过；
                 List<LineMatchBean> currentLineMatchBeans = new ArrayList<>();

                 //保存连接的数据已经保存了，现在要回显
                 //数据的保存就是根据后台返回的数据题的id 封装成一个javabean  然后遍历回显当前条目是不是保存了 主要是数据驱动视图
                 if (lineMatchs.size() > 0) {
                     for (int j = 0; j < lineMatchs.size(); j++) {
                         LineMatchBean lineMatchBean = lineMatchs.get(j);
                         if (lineMatchBean.getTypeId().equals(questionInfoList.get(i).getId())) {
                             //当前的连线已经连线过现在要回显、
                             Path pathC = new Path();
                             pathC.addCircle(lineMatchBean.getStartX(), lineMatchBean.getStartY(), 10, Path.Direction.CW);
                             pathC.addCircle(lineMatchBean.getEndX(), lineMatchBean.getEndY(), 10, Path.Direction.CW);
                             Path pathL = new Path();
                             pathL.moveTo(lineMatchBean.getStartX(), lineMatchBean.getStartY());
                             pathL.lineTo(lineMatchBean.getEndX(), lineMatchBean.getEndY());
                             //添加点路径和线路径
                             ((LinkedLineHolder) viewHolder).matching_toLine.addDotPath(pathC, false, questionInfoList.get(i).getId());
                             ((LinkedLineHolder) viewHolder).matching_toLine.addLinePath(pathL, false);

                             currentLineMatchBeans.add(lineMatchBean);

                         } else {
                             if (currentLineMatchBeans.size() > 0) {
                                 ((LinkedLineHolder) viewHolder).matching_toLine.setDrawStatus(0);
                             } else {
                                 ((LinkedLineHolder) viewHolder).matching_toLine.resetDrawLine(questionInfoList.get(i).getId());
                             }
                         }
                     }
                 }

                 //答案的回显
                 //查询保存的答案,这是多选，所以存在多个答案
                 LocalTextAnswersBean linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                         .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                 LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                 LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                 List<LineMatchBean> lineMatchBeans = MyApplication.getInstance().getDaoSession().getLineMatchBeanDao().loadAll();
                 Log.i(TAG, "onBindViewHolder: " + linkLocal + ".............." + lineMatchBeans);
                 for (int j = 0; j <lineMatchBeans.size() ; j++) {
                     LineMatchBean lineMatchBean = lineMatchBeans.get(j);
                     if (lineMatchBean.getTypeId().equals(questionInfoList.get(i).getId())){
                         Path pathC = new Path();
                         pathC.addCircle(lineMatchBean.getStartX(), lineMatchBean.getStartY(), 10, Path.Direction.CW);
                         pathC.addCircle(lineMatchBean.getEndX(), lineMatchBean.getEndY(), 10, Path.Direction.CW);
                         Path pathL = new Path();
                         pathL.moveTo(lineMatchBean.getStartX(), lineMatchBean.getStartY());
                         pathL.lineTo(lineMatchBean.getEndX(), lineMatchBean.getEndY());
                         //添加点路径和线路径
                         ((LinkedLineHolder) viewHolder).matching_toLine.addDotPath(pathC);
                         ((LinkedLineHolder) viewHolder).matching_toLine.addLinePath(pathL);
                     }
                 }

                 //开始划线
                 ((LinkedLineHolder) viewHolder).matching_toLine.setDrawStatus(0);
             }else {
                 //1 已提交  2 已批阅
                 ((LinkedLineHolder) viewHolder).matching_reset.setBackground(mContext.getResources().getDrawable(R.drawable.shape_line_reset_disable));
                 ((LinkedLineHolder) viewHolder).matching_reset.setTextColor(0xFFD5D5D5);
                 ((LinkedLineHolder) viewHolder).matching_reset.setOnClickListener(null);



                 //先更具id 判断是左边第一个和右边第几个连接
                 //View加载完成时回调
                 ((LinkedLineHolder) viewHolder).rv_matching_show.getViewTreeObserver()
                         .addOnGlobalLayoutListener(new ViewTreeObserver
                         .OnGlobalLayoutListener() {
                     @Override
                     public void onGlobalLayout() {
                         //获取所有的recycleview 的item  的布局添加到view 中
                         for (int j = 0; j <leftList.size() ; j++) {
                             View view = layoutManager.findViewByPosition(j);
                             leftViews.add(view);


                         }

                         //OnGlobalLayoutListener可能会被多次触发
                         //所以完成了需求后需要移除OnGlobalLayoutListener
                         ((LinkedLineHolder) viewHolder).rv_matching_show.getViewTreeObserver()
                                 .removeOnGlobalLayoutListener(this);

                        //todo 连线提做完了，回显的状态还有点小问题，目前之显示出正确答案
                         //更具id  获取到左边的坐标和右边的左边
                         View leftView=null;
                         View rightView=null;
                         View leftTextView=null;
                         View rightTextView=null;

                         String answer = questionInfoList.get(i).getAnswer();
                        // answer=answer.substring(0,answer.length()-1);
                         if (answer.contains("|")){
                             String[] split = answer.split("\\|");
                             for (String item : split) {
                                 String[] trackStr = item.split(",");
                                 //drawLigature(trackStr[0], trackStr[1], false);
                                 for (int j = 0; j <leftList.size() ; j++) {
                                     if (trackStr[0].equals(leftList.get(j).getId())){
                                         leftView= leftViews.get(j);
                                         //获取左边的view
                                         leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                         break;

                                     }
                                 }

                                 for (int j = 0; j < rightList.size(); j++) {
                                     if (trackStr[1].equals(rightList.get(j).getId())){
                                         rightView = leftViews.get(j);
                                         //获取左边的view
                                         rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                         break;

                                     }
                                 }

                                 float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                                 float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                                 float ex = rightView.getLeft() + rightTextView.getLeft();
                                 float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                                 //放置同侧相连接
                                 if (sx == ex) {
                                     return;
                                 }
                                 //画园
                                 Path pathC = new Path();
                                 pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                                 pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                                 //画线
                                 Path pathL = new Path();
                                 pathL.moveTo(sx, sy);
                                 pathL.lineTo(ex, ey);
                                 //添加点路径和线路径
                                 ((LinkedLineHolder) viewHolder).matching_toLine.addDotPath(pathC);
                                 ((LinkedLineHolder) viewHolder).matching_toLine.addLinePath(pathL);

                             }
                         }else {
                             //只有一对曾经作答过
                             String[] trackStr = answer.split(",");
                             for (int j = 0; j <leftList.size() ; j++) {
                                 if (trackStr[0].equals(leftList.get(j).getId())){
                                     leftView= leftViews.get(j);
                                     //获取左边的view
                                     leftTextView = leftView.findViewById(R.id.tv_item_line_word_left);
                                     break;

                                 }
                             }

                             for (int j = 0; j < rightList.size(); j++) {
                                 if (trackStr[1].equals(rightList.get(j).getId())){
                                     rightView = leftViews.get(j);
                                     //获取左边的view
                                     rightTextView = rightView.findViewById(R.id.tv_item_line_word_right);
                                     break;

                                 }
                             }

                             float sx = leftView.getLeft() + leftTextView.getLeft() + leftTextView.getWidth();
                             float sy = leftView.getTop() + leftTextView.getTop() + (leftTextView.getHeight() * 1.0f) / 2.0f;
                             float ex = rightView.getLeft() + rightTextView.getLeft();
                             float ey = rightView.getTop() + rightTextView.getTop() + (rightTextView.getHeight() * 1.0f) / 2.0f;


                             //放置同侧相连接
                             if (sx == ex) {
                                 return;
                             }
                             //画园
                             Path pathC = new Path();
                             pathC.addCircle(sx, sy, 10, Path.Direction.CW);
                             pathC.addCircle(ex, ey, 10, Path.Direction.CW);
                             //画线
                             Path pathL = new Path();
                             pathL.moveTo(sx, sy);
                             pathL.lineTo(ex, ey);
                             //添加点路径和线路径
                             ((LinkedLineHolder) viewHolder).matching_toLine.addDotPath(pathC);
                             ((LinkedLineHolder) viewHolder).matching_toLine.addLinePath(pathL);
                         }

                         //开始连线  这个主要是显示正确答案
                         ((LinkedLineHolder) viewHolder).matching_toLine.setDrawStatus(1);

                     }
                 });



             }




        }

    }


    @Override
    public int getItemCount() {
        return questionInfoList != null ? questionInfoList.size() : 0;
    }

    public class RVQuestionTvAnswerViewHolder extends RecyclerView.ViewHolder {

        private TotalQuestionView totalQuestionView;
        //        private PhotoView attach_photo;
//        private ScrollView scrollView;
        private LinearLayout linearLayout;

        public RVQuestionTvAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            NewKnowledgeQuestionView newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);

//            scrollView = itemView.findViewById(R.id.item_scroll);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
        }
    }

    public class SingleChooseHolder extends RecyclerView.ViewHolder {

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

        public SingleChooseHolder(@NonNull View itemView) {


            super(itemView);
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
            practice_head_index = itemView.findViewById(R.id.practice_head_index);

            //正确答案
            practice_select_judge_answer = itemView.findViewById(R.id.practice_select_judge_answer);


        }
    }

    public class MultiChooseHolder extends RecyclerView.ViewHolder {
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

        public MultiChooseHolder(@NonNull View itemView) {
            super(itemView);

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
        }
    }

    public class SubjectItemHolder extends RecyclerView.ViewHolder {

        public SubjectItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    //填空题
    public class FillBlankHolder extends RecyclerView.ViewHolder {

        private final LinearLayout ll_fill_balank_one;
        private final LinearLayout ll_fill_balank_two;
        private final LinearLayout ll_fill_balank_three;
        private final LinearLayout ll_fill_balank_fore;
        private final LinearLayout ll_fill_balank_five;
        private final LinearLayout ll_fill_balank_sex;

        public FillBlankHolder(@NonNull View itemView) {
            super(itemView);
            ll_fill_balank_one = itemView.findViewById(R.id.ll_fill_balank_one);
            ll_fill_balank_two = itemView.findViewById(R.id.ll_fill_balank_two);
            ll_fill_balank_three = itemView.findViewById(R.id.ll_fill_balank_three);
            ll_fill_balank_fore = itemView.findViewById(R.id.ll_fill_balank_fore);
            ll_fill_balank_five = itemView.findViewById(R.id.ll_fill_balank_five);
            ll_fill_balank_sex = itemView.findViewById(R.id.ll_fill_balank_sex);
        }
    }

    public class LinkedLineHolder extends RecyclerView.ViewHolder {

        private final TextView matching_reset;
        private final ToLineView matching_toLine;
        private final RecyclerView rv_matching_show;
        private final LinearLayout ll_match_bind_tag;


        public LinkedLineHolder(@NonNull View itemView) {
            super(itemView);
            //重置
            matching_reset = itemView.findViewById(R.id.matching_reset);
            //连线的view
            matching_toLine = itemView.findViewById(R.id.matching_toLine);
            rv_matching_show = itemView.findViewById(R.id.rv_matching_show);
            ll_match_bind_tag = itemView.findViewById(R.id.ll_match_bind_tag);


        }
    }

    public class JudgeItemHolder extends RecyclerView.ViewHolder {

        public JudgeItemHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    /**
     * 图片出题需要新增图片,改用一个LinearLayout模式
     */
    private void addAttachImgs(LinearLayout linearLayout, String src) {
        //先移除以前的
        linearLayout.removeAllViews();

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams
                (LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        ImageView photoView = new ImageView(mContext);
        Glide.with(mContext).load(src).into(photoView);
        linearLayout.addView(photoView, layoutParams);
    }

    private boolean needShowAnswer = false;

    /**
     * 提问专用：查看答案
     */
    public void needShowAnswer() {
        needShowAnswer = true;
    }


    /**
     * 每一个位置的item都作为单独一项来设置
     * viewType 设置为position
     *
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        switch (questionInfoList.get(position).getQuestionType()) {
            case Constant.Single_Choose:
                //单选题
                return Constant.Single_Choose;
            case Constant.Multi_Choose:

                return Constant.Multi_Choose;
            case Constant.Fill_Blank:

                return Constant.Fill_Blank;
            case Constant.Subject_Item:

                return Constant.Subject_Item;
            case Constant.Linked_Line:

                return Constant.Linked_Line;
            case Constant.Judge_Item:

                return Constant.Judge_Item;
        }
        return -1;
    }


}
