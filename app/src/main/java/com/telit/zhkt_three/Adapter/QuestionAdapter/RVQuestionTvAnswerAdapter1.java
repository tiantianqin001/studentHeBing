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
import com.telit.zhkt_three.Activity.HomeWork.ItemHomeWorkView;
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
public class RVQuestionTvAnswerAdapter1 extends RecyclerView.Adapter<RVQuestionTvAnswerAdapter1.RVQuestionTvAnswerViewHolder> {
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

    private List<ItemHomeWorkView> itemHomeWorkViews;
    private List<QuestionInfo> questionInfoList;

    private String homeworkId;

    private String showAnswerDate;


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
    public RVQuestionTvAnswerAdapter1(Context context, String status, boolean isImageQuestion, boolean mistakesShown, int types, String comType) {
        mContext = context;
        taskStatus = status;
        isImageTask = isImageQuestion;
        isMistakesShown = mistakesShown;
        this.types = types;
        this.comType = comType;
    }

    /**
     * 如果homeworkid为空字符串表示没有homeworkid，在QuestionInfo中存在homeworkid
     */
    public void setQuestionInfoList(List<ItemHomeWorkView> itemHomeWorkViews, List<QuestionInfo> questionInfoList, String homeworkId, String showAnswerDate) {
        this.itemHomeWorkViews = itemHomeWorkViews;
        //这个是提真正的数据
        this.questionInfoList = questionInfoList;
        this.homeworkId = homeworkId;
        this.showAnswerDate = showAnswerDate;


    }

    @NonNull
    @Override
    public RVQuestionTvAnswerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View view = itemHomeWorkViews.get(i).getView(mContext, viewGroup, questionInfoList.get(i).getQuestionType());
        return new RVQuestionTvAnswerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RVQuestionTvAnswerViewHolder rvQuestionTvAnswerViewHolder, int i) {
      /*  if (!TextUtils.isEmpty(xd)) {
            Bundle bundle = new Bundle();
            bundle.putString("xd", "1");
            bundle.putString("subject", questionInfoList.get(i).getChid() + "");
            bundle.putString("difficulty", difficulty);
            bundle.putString("type", type);
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);

            if (!TextUtils.isEmpty(questionInfoList.get(i).getKnowledge())) {
                bundle.putString("knowledge_json", questionInfoList.get(i).getKnowledge());
            }
            rvQuestionTvAnswerViewHolder.totalQuestionView.setBundle(bundle);
            rvQuestionTvAnswerViewHolder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //设置homeworkId
        if (!TextUtils.isEmpty(homeworkId)) {
            questionInfoList.get(i).setHomeworkId(homeworkId);
            Bundle bundle = new Bundle();
            //types 0 是互动，  1是作业
            bundle.putInt("types",types);
            //判断作业是不是已经完成
            bundle.putString("comType",comType);
            rvQuestionTvAnswerViewHolder.totalQuestionView.setBundle(bundle);
            rvQuestionTvAnswerViewHolder.newKnowledgeQuestionView.setBundle(bundle);
        }

        //如果是错题集，接下来判断是否图片出题
        if (isMistakesShown) {
            //这里是错题集的适配器入口

            int byHand = questionInfoList.get(i).getByhand();
            if (byHand != 1) {
                //题库出题
                rvQuestionTvAnswerViewHolder.linearLayout.setVisibility(View.GONE);
                isImageTask = false;

                rvQuestionTvAnswerViewHolder.totalQuestionView.setVisibility(View.GONE);
                rvQuestionTvAnswerViewHolder.newKnowledgeQuestionView.setVisibility(View.VISIBLE);

                QuestionBank questionBank = new QuestionBank();
                questionBank.setHomeworkId(questionInfoList.get(i).getHomeworkId());
                questionBank.setQuestionId(Integer.parseInt(questionInfoList.get(i).getId()));
                questionBank.setQuestionChannelType(questionInfoList.get(i).getQuestionType());
                questionBank.setQuestionText(questionInfoList.get(i).getQuestionContent());
                questionBank.setAnswerOptions(questionInfoList.get(i).getAnswerOption());
                questionBank.setAnswerText(questionInfoList.get(i).getAnswer());
                questionBank.setAnswer(questionInfoList.get(i).getAnswerImg());
                questionBank.setExplanation(questionInfoList.get(i).getAnalysis());
                questionBank.setOwnList(questionInfoList.get(i).getOwnList());
                questionBank.setScore(questionInfoList.get(i).getScore());
                questionBank.setOwnscore(questionInfoList.get(i).getOwnscore());
                questionBank.setQuestionBanks(questionInfoList.get(i).getQuestionBanks());
                //传入主观题图片
                questionBank.setImgFile(questionInfoList.get(i).getImgFile());
                //因为之前根据List是否存在判断是否有子List的
                questionBank.setList(questionInfoList.get(i).getqBankList());
                questionBank.setShownAnswer(true);
                rvQuestionTvAnswerViewHolder.newKnowledgeQuestionView.setQuestionInfo(questionBank,
                        i, "2", true);
            } else {
                //图片出题
                rvQuestionTvAnswerViewHolder.linearLayout.setVisibility(View.VISIBLE);
                String attachUrl = questionInfoList.get(i).getAttachment();
                if (!TextUtils.isEmpty(attachUrl)) {
                    addAttachImgs(rvQuestionTvAnswerViewHolder.linearLayout, attachUrl);
                    isImageTask = true;
                }

                rvQuestionTvAnswerViewHolder.newKnowledgeQuestionView.setVisibility(View.GONE);
                rvQuestionTvAnswerViewHolder.totalQuestionView.setQuestionInfo(questionInfoList.size(), i, taskStatus, isImageTask,
                        isMistakesShown, questionInfoList.get(i));
            }
        } else {
//            rvQuestionTvAnswerViewHolder.scrollView.setVisibility(View.GONE);
            rvQuestionTvAnswerViewHolder.linearLayout.setVisibility(View.GONE);
            rvQuestionTvAnswerViewHolder.newKnowledgeQuestionView.setVisibility(View.GONE);

            if (needShowAnswer) {
                rvQuestionTvAnswerViewHolder.totalQuestionView.needShowTiWenAnswer();
            }
            //另外写答案公布时间   主要是显示答案时间
            rvQuestionTvAnswerViewHolder.totalQuestionView.setShowAnswerDate(showAnswerDate);
            //设置数据
            rvQuestionTvAnswerViewHolder.totalQuestionView.setQuestionInfo(questionInfoList.size(), i, taskStatus, isImageTask,
                    isMistakesShown, questionInfoList.get(i));
        }*/
    }

    /**
     * 每一个位置的item都作为单独一项来设置
     * viewType 设置为position
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return position;
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
          /*  totalQuestionView = itemView.findViewById(R.id.item_total_question);
            newKnowledgeQuestionView = itemView.findViewById(R.id.item_total_banks);

//            scrollView = itemView.findViewById(R.id.item_scroll);
            linearLayout = itemView.findViewById(R.id.item_scroll_linear);*/
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
