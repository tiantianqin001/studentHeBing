package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.bumptech.glide.Glide;


import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.TotalQuestionView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 16:44
 * <p>
 * 注意：这个和拍照出题公用Adapter，所以这里判断是否是错题集入口分别进入拍照出题/题库出题
 */
public class RVQuestionTvAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
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

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SingleChooseHolder) {
            //单选题
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            if (selectBeans.size() == 1) {
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
            } else if (selectBeans.size() == 2) {
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);;
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

            }else if (selectBeans.size() == 6){

                ((SingleChooseHolder) viewHolder).ll_single_image_sex.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_five.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_fore.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_one.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_two.setVisibility(View.VISIBLE);
                ((SingleChooseHolder) viewHolder).ll_single_image_three.setVisibility(View.VISIBLE);
            }

            ((SingleChooseHolder) viewHolder).tv_single_image_one.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SingleChooseHolder) viewHolder).tv_single_image_one.isSelected()) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_one.setSelected(false);

                    } else {
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(true);
                    }

                    if (selectBeans.size() ==2){
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                    }else if (selectBeans.size() == 3){
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                    }else if (selectBeans.size() == 4){
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                    }else if (selectBeans.size() == 5){
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                    }else if (selectBeans.size() == 6){
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_sex.setSelected(false);
                    }
                }
            });
            ((SingleChooseHolder) viewHolder).tv_single_image_two.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SingleChooseHolder) viewHolder).tv_single_image_two.isSelected()) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(false);

                    } else {
                        ((SingleChooseHolder) viewHolder).tv_single_image_two.setSelected(true);
                    }

                    if (selectBeans.size() ==2){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                    }else if (selectBeans.size() == 3){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                    }else if (selectBeans.size() == 4){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                    }else if (selectBeans.size() == 5){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                    }else if (selectBeans.size() == 6){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_sex.setSelected(false);
                    }
                }
            });
            ((SingleChooseHolder) viewHolder). tv_single_image_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SingleChooseHolder) viewHolder).tv_single_image_three.isSelected()) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_three.setSelected(false);

                    } else {
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(true);
                    }

                   if (selectBeans.size() == 3){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                    }else if (selectBeans.size() == 4){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                    }else if (selectBeans.size() == 5){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                    }else if (selectBeans.size() == 6){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_sex.setSelected(false);
                    }
                }
            });
            ((SingleChooseHolder) viewHolder).tv_single_image_fore.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SingleChooseHolder) viewHolder).tv_single_image_fore.isSelected()) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(false);

                    } else {
                        ((SingleChooseHolder) viewHolder).tv_single_image_fore.setSelected(true);
                    }

                   if (selectBeans.size() == 4){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                    }else if (selectBeans.size() == 5){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                    }else if (selectBeans.size() == 6){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_sex.setSelected(false);
                    }
                }
            });
            ((SingleChooseHolder) viewHolder).tv_single_image_five.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SingleChooseHolder) viewHolder).tv_single_image_five.isSelected()) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_five.setSelected(false);

                    } else {
                        ((SingleChooseHolder) viewHolder).  tv_single_image_five.setSelected(true);
                    }
                   if (selectBeans.size() == 5){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                    }else if (selectBeans.size() == 6){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_sex.setSelected(false);
                    }
                }
            });

            ((SingleChooseHolder) viewHolder).tv_single_image_sex.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((SingleChooseHolder) viewHolder).tv_single_image_sex.isSelected()) {
                        ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(false);

                    } else {
                        ((SingleChooseHolder) viewHolder).tv_single_image_sex.setSelected(true);
                    }

                     if (selectBeans.size() == 6){
                        ((SingleChooseHolder) viewHolder). tv_single_image_one.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_three.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_fore.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_five.setSelected(false);
                        ((SingleChooseHolder) viewHolder). tv_single_image_two.setSelected(false);
                    }
                }
            });





        }else if (viewHolder instanceof MultiChooseHolder){
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
            }else if (selectBeans.size() == 6){
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
                        ((MultiChooseHolder) viewHolder). tv_single_image_one.setSelected(true);
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
            ((MultiChooseHolder) viewHolder). tv_single_image_three.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (((MultiChooseHolder) viewHolder).tv_single_image_three.isSelected()) {
                        ((MultiChooseHolder) viewHolder).tv_single_image_three.setSelected(false);

                    } else {
                        ((MultiChooseHolder) viewHolder). tv_single_image_three.setSelected(true);
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
                        ((MultiChooseHolder) viewHolder).  tv_single_image_five.setSelected(true);
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


        }else if (viewHolder instanceof FillBlankHolder){
            //填空题
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            if (selectBeans.size() == 1){
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);

            }else if (selectBeans.size() ==2 ){
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);

            }else if (selectBeans.size() == 3){
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);

            }else if (selectBeans.size() == 4){
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_fore.setVisibility(View.VISIBLE);
            }else if (selectBeans.size() == 5){
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_fore.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_five.setVisibility(View.VISIBLE);
            }else if (selectBeans.size() == 6){
                ((FillBlankHolder) viewHolder).ll_fill_balank_one.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_two.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_three.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_fore.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_five.setVisibility(View.VISIBLE);
                ((FillBlankHolder) viewHolder).ll_fill_balank_sex.setVisibility(View.VISIBLE);
            }

        }else if (viewHolder instanceof JudgeItemHolder){
            //判断题
            // ((JudgeItemHolder) viewHolder)
        }else if (viewHolder instanceof SubjectItemHolder ){
            //主观题

        }else if (viewHolder instanceof   LinkedLineHolder){
            //连线题
            List<QuestionInfo.LeftListBean> leftList = questionInfoList.get(i).getLeftList();
            List<QuestionInfo.RightListBean> rightList = questionInfoList.get(i).getRightList();
         /*   LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mContext.getResources().getDimensionPixelSize(R.dimen.y200), mContext.getResources().getDimensionPixelSize(R.dimen.y55));
            layoutParams.topMargin = mContext.getResources().getDimensionPixelSize(R.dimen.x12);
            layoutParams.bottomMargin = mContext.getResources().getDimensionPixelSize(R.dimen.x12);
            for (int j = 0; j < leftList.size(); j++) {
                View view = addItemView(leftList.get(j).getTitle(),j);
                ((LinkedLineHolder) viewHolder).matching_left.addView(view,layoutParams);
            }

            //右边的布局
            List<QuestionInfo.RightListBean> rightList = questionInfoList.get(i).getRightList();
            for (int j = 0; j <rightList.size() ; j++) {
                View view = addItemView(leftList.get(j).getTitle(),j);
                ((LinkedLineHolder) viewHolder).matching_right.addView(view,layoutParams);
            }*/
            LineLeftAdapter lineLeftAdapter=new LineLeftAdapter(mContext,leftList);
            LineRightAdapter lineRightAdapter=new LineRightAdapter(mContext,rightList);
            LinearLayoutManager layoutManager = new LinearLayoutManager(mContext){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };

            LinearLayoutManager layoutManager1 = new LinearLayoutManager(mContext){
                @Override
                public boolean canScrollVertically() {
                    return false;
                }
            };
            //左边的recycleview
            ((LinkedLineHolder) viewHolder).rv_matching_left.setLayoutManager(layoutManager);
            ((LinkedLineHolder) viewHolder).rv_matching_left.setAdapter(lineLeftAdapter);

            //右边的recycleview
            ((LinkedLineHolder) viewHolder).rv_matching_right.setLayoutManager(layoutManager1);

            ((LinkedLineHolder) viewHolder).rv_matching_right.setAdapter(lineRightAdapter);
        }

    }


    @Override
    public int getItemCount() {
        return questionInfoList != null ? questionInfoList.size() : 0;
    }

    public class RVQuestionTvAnswerViewHolder extends RecyclerView.ViewHolder {

        private TotalQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        //        private PhotoView attach_photo;
//        private ScrollView scrollView;
        private LinearLayout linearLayout;

        public RVQuestionTvAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);

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
        private final RecyclerView rv_matching_left;
        private final RecyclerView rv_matching_right;
        //  private final LinearLayout matching_left;
     //   private final LinearLayout matching_right;

        public LinkedLineHolder(@NonNull View itemView) {
            super(itemView);
            //重置
            matching_reset = itemView.findViewById(R.id.matching_reset);
           //左边的列表
          //  matching_left = itemView.findViewById(R.id.matching_left);
           //右边的列表
          //  matching_right = itemView.findViewById(R.id.matching_right);
            rv_matching_left = itemView.findViewById(R.id.rv_matching_left);
            rv_matching_right = itemView.findViewById(R.id.rv_matching_right);


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
