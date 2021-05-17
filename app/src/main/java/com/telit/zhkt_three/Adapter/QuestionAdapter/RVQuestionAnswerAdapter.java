package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.QuestionType.FillBlankQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.QuestionType.JudgeItemQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.QuestionType.LinkedLineQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.QuestionType.MultiChooseQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.QuestionType.SingleChooseQuestionView;
import com.telit.zhkt_three.CustomView.QuestionView.QuestionType.SubjectItemQuestionView;
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
public class RVQuestionAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;

    //0未提交  1 已提交  2 已批阅
    private String taskStatus;

    //是否是错题集展示界面
    private boolean isMistakesShown;
    private int types;
    private String comType;

    public static final int VIEW_TYPE_ITEM_SINGLE_CHOOSE = 0;
    public static final int VIEW_TYPE_ITEM_MULTI_CHOOSE = 1;
    public static final int VIEW_TYPE_ITEM_FILL_BLANK = 2;
    public static final int VIEW_TYPE_ITEM_SUBJECT_ITEM = 3;
    public static final int VIEW_TYPE_ITEM_LINKED_LINE = 4;
    public static final int VIEW_TYPE_ITEM_JUDGE_ITEM = 5;

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
    public RVQuestionAnswerAdapter(Context context, String status, boolean isImageQuestion, boolean mistakesShown, int types, String comType) {
        mContext = context;
        taskStatus = status;
        isImageTask = isImageQuestion;
        isMistakesShown = mistakesShown;
        this.types = types;
        this.comType = comType;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM_SINGLE_CHOOSE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_single_choose_question_item_layout, parent, false);
            return new RVQuestionSingleChooseAnswerViewHolder(view) {
            };
        }else if (viewType == VIEW_TYPE_ITEM_MULTI_CHOOSE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_multi_choose_question_item_layout, parent, false);
            return new RVQuestionMultiChooseAnswerViewHolder(view) {
            };
        }else if (viewType == VIEW_TYPE_ITEM_FILL_BLANK) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_fill_blank_question_item_layout, parent, false);
            return new RVQuestionFillBlankAnswerViewHolder(view) {
            };
        }else if (viewType == VIEW_TYPE_ITEM_SUBJECT_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_subject_item_question_item_layout, parent, false);
            return new RVQuestionSubjectItemAnswerViewHolder(view) {
            };
        }else if (viewType == VIEW_TYPE_ITEM_LINKED_LINE) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_linked_line_question_item_layout, parent, false);
            return new RVQuestionLinkedLineAnswerViewHolder(view) {
            };
        }else if (viewType == VIEW_TYPE_ITEM_JUDGE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_judge_item_question_item_layout, parent, false);
            return new RVQuestionJudgeItemAnswerViewHolder(view) {
            };
        }else {
            View emptyView = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_no_empty_view, parent, false);
            return new RecyclerView.ViewHolder(emptyView) {
            };
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof RVQuestionSingleChooseAnswerViewHolder){
            RVQuestionSingleChooseAnswerViewHolder viewHolder = (RVQuestionSingleChooseAnswerViewHolder) holder;

            //处理单选
            handleSingleChoose(questionInfoList.get(position),position,viewHolder);

        }else if (holder instanceof RVQuestionMultiChooseAnswerViewHolder){
            RVQuestionMultiChooseAnswerViewHolder viewHolder = (RVQuestionMultiChooseAnswerViewHolder) holder;

            //处理多选
            handleMultiChoose(questionInfoList.get(position),position,viewHolder);

        }else if (holder instanceof RVQuestionFillBlankAnswerViewHolder){
            RVQuestionFillBlankAnswerViewHolder viewHolder = (RVQuestionFillBlankAnswerViewHolder) holder;

            //处理填空
            handleFillBlank(questionInfoList.get(position),position,viewHolder);

        }else if (holder instanceof RVQuestionSubjectItemAnswerViewHolder){
            RVQuestionSubjectItemAnswerViewHolder viewHolder = (RVQuestionSubjectItemAnswerViewHolder) holder;

            //处理主观
            handleSubjectItem(questionInfoList.get(position),position,viewHolder);

        }else if (holder instanceof RVQuestionLinkedLineAnswerViewHolder){
            RVQuestionLinkedLineAnswerViewHolder viewHolder = (RVQuestionLinkedLineAnswerViewHolder) holder;

            //处理连线
            handleLinkedLink(questionInfoList.get(position),position,viewHolder);

        }else if (holder instanceof RVQuestionJudgeItemAnswerViewHolder){
            RVQuestionJudgeItemAnswerViewHolder viewHolder = (RVQuestionJudgeItemAnswerViewHolder) holder;

            //处理判断
            handleJudgeItem(questionInfoList.get(position),position,viewHolder);

        }
    }

    /**
     * 处理判断
     *
     * @param questionInfo
     * @param holder
     */
    private void handleJudgeItem(QuestionInfo questionInfo,int position, RVQuestionJudgeItemAnswerViewHolder holder){
        if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfo.getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            if (!TextUtils.isEmpty(questionInfo.getKnowledge())) {
                bundle.putString("knowledge_json", questionInfo.getKnowledge());
            }
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfo.setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfo.getByhand();
            if (byHand != 1) {
                //题库出题
                holder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                holder.totalQuestionView.setVisibility(View.GONE);
                holder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfo.getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfo.getId()));
                questionBank.setQuestionChannelType(questionInfo.getQuestionType());
                questionBank.setQuestionText(questionInfo.getQuestionContent());
                questionBank.setAnswerOptions(questionInfo.getAnswerOption());
                questionBank.setAnswerText(questionInfo.getAnswer());
                questionBank.setAnswer(questionInfo.getAnswerImg());
                questionBank.setExplanation(questionInfo.getAnalysis());
                questionBank.setOwnList(questionInfo.getOwnList());
                questionBank.setScore(questionInfo.getScore());
                questionBank.setOwnscore(questionInfo.getOwnscore());
                questionBank.setQuestionBanks(questionInfo.getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfo.getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfo.getqBankList());
                questionBank.setShownAnswer(true);
                holder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        position, "2", true);
            } else {
                //图片出题
                holder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfo.getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(holder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                holder.newKnowledgeQuestionView.setVisibility(View.GONE);
                holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                        isMistakesShown, questionInfo);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                holder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            holder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                    isMistakesShown, questionInfo);
        }
    }

    /**
     * 处理连线
     *
     * @param questionInfo
     * @param holder
     */
    private void handleLinkedLink(QuestionInfo questionInfo,int position, RVQuestionLinkedLineAnswerViewHolder holder){
        if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfo.getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            bundle.putInt("questionType", questionInfo.getQuestionType());

            if (!TextUtils.isEmpty(questionInfo.getKnowledge())) {
                bundle.putString("knowledge_json", questionInfo.getKnowledge());
            }
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfo.setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfo.getByhand();
            if (byHand != 1) {
                //题库出题
                holder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                holder.totalQuestionView.setVisibility(View.GONE);
                holder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfo.getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfo.getId()));
                questionBank.setQuestionChannelType(questionInfo.getQuestionType());
                questionBank.setQuestionText(questionInfo.getQuestionContent());
                questionBank.setAnswerOptions(questionInfo.getAnswerOption());
                questionBank.setAnswerText(questionInfo.getAnswer());
                questionBank.setAnswer(questionInfo.getAnswerImg());
                questionBank.setExplanation(questionInfo.getAnalysis());
                questionBank.setOwnList(questionInfo.getOwnList());
                questionBank.setScore(questionInfo.getScore());
                questionBank.setOwnscore(questionInfo.getOwnscore());
                questionBank.setQuestionBanks(questionInfo.getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfo.getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfo.getqBankList());
                questionBank.setShownAnswer(true);
                holder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        position, "2", true);
            } else {
                //图片出题
                holder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfo.getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(holder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                holder.newKnowledgeQuestionView.setVisibility(View.GONE);
                holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                        isMistakesShown, questionInfo);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                holder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            holder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                    isMistakesShown, questionInfo);
        }
    }

    /**
     * 处理主观
     *
     * @param questionInfo
     * @param holder
     */
    private void handleSubjectItem(QuestionInfo questionInfo,int position, RVQuestionSubjectItemAnswerViewHolder holder){
        if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfo.getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            if (!TextUtils.isEmpty(questionInfo.getKnowledge())) {
                bundle.putString("knowledge_json", questionInfo.getKnowledge());
            }
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfo.setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfo.getByhand();
            if (byHand != 1) {
                //题库出题
                holder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                holder.totalQuestionView.setVisibility(View.GONE);
                holder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfo.getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfo.getId()));
                questionBank.setQuestionChannelType(questionInfo.getQuestionType());
                questionBank.setQuestionText(questionInfo.getQuestionContent());
                questionBank.setAnswerOptions(questionInfo.getAnswerOption());
                questionBank.setAnswerText(questionInfo.getAnswer());
                questionBank.setAnswer(questionInfo.getAnswerImg());
                questionBank.setExplanation(questionInfo.getAnalysis());
                questionBank.setOwnList(questionInfo.getOwnList());
                questionBank.setScore(questionInfo.getScore());
                questionBank.setOwnscore(questionInfo.getOwnscore());
                questionBank.setQuestionBanks(questionInfo.getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfo.getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfo.getqBankList());
                questionBank.setShownAnswer(true);
                holder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        position, "2", true);
            } else {
                //图片出题
                holder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfo.getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(holder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                holder.newKnowledgeQuestionView.setVisibility(View.GONE);
                holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                        isMistakesShown, questionInfo);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                holder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            holder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                    isMistakesShown, questionInfo);
        }
    }

    /**
     * 处理填空
     *
     * @param questionInfo
     * @param holder
     */
    private void handleFillBlank(QuestionInfo questionInfo,int position, RVQuestionFillBlankAnswerViewHolder holder){
        if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfo.getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            if (!TextUtils.isEmpty(questionInfo.getKnowledge())) {
                bundle.putString("knowledge_json", questionInfo.getKnowledge());
            }
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfo.setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfo.getByhand();
            if (byHand != 1) {
                //题库出题
                holder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                holder.totalQuestionView.setVisibility(View.GONE);
                holder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfo.getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfo.getId()));
                questionBank.setQuestionChannelType(questionInfo.getQuestionType());
                questionBank.setQuestionText(questionInfo.getQuestionContent());
                questionBank.setAnswerOptions(questionInfo.getAnswerOption());
                questionBank.setAnswerText(questionInfo.getAnswer());
                questionBank.setAnswer(questionInfo.getAnswerImg());
                questionBank.setExplanation(questionInfo.getAnalysis());
                questionBank.setOwnList(questionInfo.getOwnList());
                questionBank.setScore(questionInfo.getScore());
                questionBank.setOwnscore(questionInfo.getOwnscore());
                questionBank.setQuestionBanks(questionInfo.getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfo.getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfo.getqBankList());
                questionBank.setShownAnswer(true);
                holder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        position, "2", true);
            } else {
                //图片出题
                holder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfo.getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(holder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                holder.newKnowledgeQuestionView.setVisibility(View.GONE);
                holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                        isMistakesShown, questionInfo);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                holder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            holder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                    isMistakesShown, questionInfo);
        }
    }

    /**
     * 处理多选
     *
     * @param questionInfo
     * @param holder
     */
    private void handleMultiChoose(QuestionInfo questionInfo,int position, RVQuestionMultiChooseAnswerViewHolder holder){
        if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfo.getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            if (!TextUtils.isEmpty(questionInfo.getKnowledge())) {
                bundle.putString("knowledge_json", questionInfo.getKnowledge());
            }
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfo.setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfo.getByhand();
            if (byHand != 1) {
                //题库出题
                holder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                holder.totalQuestionView.setVisibility(View.GONE);
                holder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfo.getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfo.getId()));
                questionBank.setQuestionChannelType(questionInfo.getQuestionType());
                questionBank.setQuestionText(questionInfo.getQuestionContent());
                questionBank.setAnswerOptions(questionInfo.getAnswerOption());
                questionBank.setAnswerText(questionInfo.getAnswer());
                questionBank.setAnswer(questionInfo.getAnswerImg());
                questionBank.setExplanation(questionInfo.getAnalysis());
                questionBank.setOwnList(questionInfo.getOwnList());
                questionBank.setScore(questionInfo.getScore());
                questionBank.setOwnscore(questionInfo.getOwnscore());
                questionBank.setQuestionBanks(questionInfo.getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfo.getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfo.getqBankList());
                questionBank.setShownAnswer(true);
                holder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        position, "2", true);
            } else {
                //图片出题
                holder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfo.getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(holder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                holder.newKnowledgeQuestionView.setVisibility(View.GONE);
                holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                        isMistakesShown, questionInfo);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                holder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            holder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                    isMistakesShown, questionInfo);
        }
    }

    /**
     * 处理单选
     *
     * @param questionInfo
     * @param holder
     */
    private void handleSingleChoose(QuestionInfo questionInfo,int position, RVQuestionSingleChooseAnswerViewHolder holder){
        if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfo.getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            if (!TextUtils.isEmpty(questionInfo.getKnowledge())) {
                bundle.putString("knowledge_json", questionInfo.getKnowledge());
            }
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfo.setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            bundle.putInt("questionType", questionInfo.getQuestionType());
            holder.totalQuestionView.setBundle(bundle);
            holder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfo.getByhand();
            if (byHand != 1) {
                //题库出题
                holder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                holder.totalQuestionView.setVisibility(View.GONE);
                holder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfo.getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfo.getId()));
                questionBank.setQuestionChannelType(questionInfo.getQuestionType());
                questionBank.setQuestionText(questionInfo.getQuestionContent());
                questionBank.setAnswerOptions(questionInfo.getAnswerOption());
                questionBank.setAnswerText(questionInfo.getAnswer());
                questionBank.setAnswer(questionInfo.getAnswerImg());
                questionBank.setExplanation(questionInfo.getAnalysis());
                questionBank.setOwnList(questionInfo.getOwnList());
                questionBank.setScore(questionInfo.getScore());
                questionBank.setOwnscore(questionInfo.getOwnscore());
                questionBank.setQuestionBanks(questionInfo.getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfo.getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfo.getqBankList());
                questionBank.setShownAnswer(true);
                holder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        position, "2", true);
            } else {
                //图片出题
                holder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfo.getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(holder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                holder.newKnowledgeQuestionView.setVisibility(View.GONE);
                holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                        isMistakesShown, questionInfo);
            }
        } else {
            holder.linearLayout.setVisibility(View.GONE);
            holder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                holder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            holder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            holder.totalQuestionView.setQuestionInfo(questionInfoList.size(), position, taskStatus, isImageTask,
                    isMistakesShown, questionInfo);
        }
    }

    @Override
    public int getItemCount() {
        return questionInfoList != null ? questionInfoList.size() : 0;
    }

    @Override
    public int getItemViewType(int position) {
        switch (questionInfoList.get(position).getQuestionType()) {
            case 0:
                return VIEW_TYPE_ITEM_SINGLE_CHOOSE;
            case 1:
                return VIEW_TYPE_ITEM_MULTI_CHOOSE;
            case 2:
                return VIEW_TYPE_ITEM_FILL_BLANK;
            case 3:
                return VIEW_TYPE_ITEM_SUBJECT_ITEM;
            case 4:
                return VIEW_TYPE_ITEM_LINKED_LINE;
            case 5:
                return VIEW_TYPE_ITEM_JUDGE_ITEM;
            default:
                return -1;
        }
    }

    public class RVQuestionJudgeItemAnswerViewHolder extends RecyclerView.ViewHolder {

        private JudgeItemQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        private LinearLayout linearLayout;

        public RVQuestionJudgeItemAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
        }
    }

    public class RVQuestionLinkedLineAnswerViewHolder extends RecyclerView.ViewHolder {

        private LinkedLineQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        private LinearLayout linearLayout;

        public RVQuestionLinkedLineAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
        }
    }

    public class RVQuestionSubjectItemAnswerViewHolder extends RecyclerView.ViewHolder {

        private SubjectItemQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        private LinearLayout linearLayout;

        public RVQuestionSubjectItemAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
        }
    }

    public class RVQuestionFillBlankAnswerViewHolder extends RecyclerView.ViewHolder {

        private FillBlankQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        private LinearLayout linearLayout;

        public RVQuestionFillBlankAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
        }
    }

    public class RVQuestionMultiChooseAnswerViewHolder extends RecyclerView.ViewHolder {

        private MultiChooseQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        private LinearLayout linearLayout;

        public RVQuestionMultiChooseAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
        }
    }

    public class RVQuestionSingleChooseAnswerViewHolder extends RecyclerView.ViewHolder {

        private SingleChooseQuestionView totalQuestionView;
        private NewKnowledgeQuestionView newKnowledgeQuestionView;
        private LinearLayout linearLayout;

        public RVQuestionSingleChooseAnswerViewHolder(@NonNull View itemView) {
            super(itemView);
            totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);
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
}
