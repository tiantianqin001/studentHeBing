package com.telit.zhkt_three.CustomView.QuestionView.QuestionType;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Activity.MistakesCollection.MistakesImproveActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.PerfectAnswerActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.QuestionView.FillBlankToDoView;
import com.telit.zhkt_three.CustomView.QuestionView.SubjectiveToDoView;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;

import org.sufficientlysecure.htmltextview.HtmlHttpImageGetter;
import org.sufficientlysecure.htmltextview.HtmlTextView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/23 18:57
 * <p>
 * QuestionInfo类型，2.0的答题类型
 * <p>
 * 通过GreenDao保存答案状态
 * <p>
 * todo 提交后需要删除对应的保存记录
 */
public class SubjectItemQuestionView extends RelativeLayout implements View.OnClickListener {

    private static final String TAG = "TotalQuestionView";
    /**
     * 作答方式：
     * 图片出题 true
     * 题库出题 false
     */
    private boolean isImgQuestion;

    /**
     * 0未提交  1 已提交  2 已批阅
     * 未做：做题的视图界面 todoView
     * 提交：查看已做视图界面 showView
     * 批阅：查看已做视图及答案视图界面 showView plus AnswerView
     */
    private String taskStatus;

    private boolean isMistakesShown;

    /**
     * 单个题型数据
     */
    private QuestionInfo questionInfo;

    /**
     * 一页的总题目数
     */
    private int totalQuestionCount;

    /**
     * 当前的题目位置下标
     */
    private int curPosition;

    /**
     * 随堂练习头
     */
    private LinearLayout practice_head_layout;
    private TextView practice_head_type;//随堂练习的题型文本
    private TextView practice_head_index;//随堂练习的题型个数
    private TextView practise_head_score;//随堂练习的题目分值

    /**
     * 作业头---即题库
     */
    private RelativeLayout work_head_layout;
    private TextView total_work_title;//作业的题目类型
    private HtmlTextView total_work_content;//作业的题目内容
    private TextView total_work_promote;//作业的题目巩固
    private TextView total_work_good_answer;//优秀答案

    /**
     * 作业试题图片说明的展示
     */
    private LinearLayout practice_img_layout;
    private ImageView practice_img_one;
    private ImageView practice_img_two;
    private ImageView practice_img_three;
    private FrameLayout practice_img_frame;
    private ImageView practice_img_four;
    private TextView practice_img_count;

    /**
     * 题型的选项或者说作答方式
     */
    private LinearLayout total_options_layout;

    /**
     * 随堂练习选择判断的答案
     */
    private TextView practice_select_judge_answer;

    /**
     * 随堂练习填空的答案
     */
    private LinearLayout practice_blank_answer_layout;
    private LinearLayout practice_blank_answer_option_layout;

    /**
     * 作业的答案----即题库
     */
    private RelativeLayout work_answer_layout;
    private ImageView total_work_answer_icon;//对错图片
    private TextView work_my_answer_title;//错了是红色、对了是绿色
    private LinearLayout work_my_answer;//我的答案
    private LinearLayout work_right_answer;//正确答案
    private ImageView work_answer_analysis;//答案解析，图片
    private LinearLayout total_work_answer_desc;//难易、时间线描述背景
    private TextView work_desc_date;//时间描述
    private TextView work_desc_difficulty;//难易描述

    /**
     * 需要传递的Bundle
     */
    private Bundle bundle;
    private FillBlankToDoView fillBlankToDoView;
    private TextView tv_get_current_quint;
    private RelativeLayout option_do_tv_one;
    private RelativeLayout option_do_tv_two;

    public Bundle getBundle() {
        return bundle;
    }

    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    /**
     * 设置显示时间
     */
    private String showAnswerDate;

    public void setShowAnswerDate(String showAnswerDate) {
        this.showAnswerDate = showAnswerDate;
    }

    /**
     * 设置题型数据
     *
     * @param totalQuestionCount 一次作业中总的题数
     * @param curPosition        当前题型所在的位置下标
     * @param status             作业的状态
     * @param isImgQuestion      是否图片出题模式
     * @param isMistakesShown    是否是错题集展示界面
     * @param questionInfo       该题型的详细数据信息
     */
    public void setQuestionInfo(int totalQuestionCount, int curPosition, String status, boolean isImgQuestion,
                                boolean isMistakesShown, QuestionInfo questionInfo) {
        this.totalQuestionCount = totalQuestionCount;
        this.curPosition = curPosition;
        taskStatus = status;
        this.isImgQuestion = isImgQuestion;
        this.isMistakesShown = isMistakesShown;
        this.questionInfo = questionInfo;
        initData();
    }

    /**
     * 依据提问显示答案的标志，提问答案填写后直接显示答案
     */
    private boolean isShowTiWenAnswer = false;

    /**
     * 设置提问答案的显示
     */
    public void needShowTiWenAnswer() {
        isShowTiWenAnswer = true;
    }

    /**
     * 主观题题目ID
     */
    public static String subjQuestionId;

    /**
     * 额外的练习类型
     */
    private boolean isPracticeType = false;

    public void setPracticeType(boolean isPracticeType) {
        this.isPracticeType = isPracticeType;
    }

    public SubjectItemQuestionView(Context context) {
        this(context, null);
    }

    public SubjectItemQuestionView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SubjectItemQuestionView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        LayoutInflater.from(context).inflate(R.layout.total_question_view_layout, this, true);
        //注意这里不可以初始化数据，因为没有数据，questionInfo是空的，需要通过set方法塞入
        initView();
    }

    private void initView() {
        //PingFang字体
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "PingFang-SimpleBold.ttf");

        practice_head_layout = findViewById(R.id.practice_head_layout);
        practice_head_type = findViewById(R.id.practice_head_type);
        practice_head_index = findViewById(R.id.practice_head_index);
        practise_head_score = findViewById(R.id.practise_head_score);

        practice_head_type.setTypeface(typeface);
        practice_head_index.setTypeface(typeface);
        practise_head_score.setTypeface(typeface);

        work_head_layout = findViewById(R.id.work_head_layout);
        total_work_title = findViewById(R.id.total_work_title);
        total_work_content = findViewById(R.id.total_work_content);
        total_work_promote = findViewById(R.id.total_work_promote);
        total_work_good_answer = findViewById(R.id.total_work_good_answer);

        total_work_title.setTypeface(typeface);
        total_work_content.setTypeface(typeface);
        total_work_promote.setTypeface(typeface);

        practice_img_layout = findViewById(R.id.practice_img_layout);
        practice_img_one = findViewById(R.id.practice_img_one);
        practice_img_two = findViewById(R.id.practice_img_two);
        practice_img_three = findViewById(R.id.practice_img_three);
        practice_img_frame = findViewById(R.id.practice_img_frame);
        practice_img_four = findViewById(R.id.practice_img_four);
        practice_img_count = findViewById(R.id.practice_img_count);
        practice_img_count.setTypeface(typeface);

        total_options_layout = findViewById(R.id.total_options_layout);

        practice_select_judge_answer = findViewById(R.id.practice_select_judge_answer);
        practice_select_judge_answer.setTypeface(typeface);
        //显示判断题的答案
        option_do_tv_one = (RelativeLayout) findViewById(R.id.option_do_tv_one);
        option_do_tv_two = (RelativeLayout) findViewById(R.id.option_do_tv_two);

        practice_blank_answer_layout = findViewById(R.id.practice_blank_answer_layout);
        TextView practice_right_answer = findViewById(R.id.practice_right_answer);
        practice_right_answer.setTypeface(typeface);
        practice_blank_answer_option_layout = findViewById(R.id.practice_blank_answer_option_layout);

        work_answer_layout = findViewById(R.id.work_answer_layout);
        total_work_answer_icon = findViewById(R.id.total_work_answer_icon);
        work_my_answer = findViewById(R.id.work_my_answer);
        work_my_answer_title = findViewById(R.id.work_my_answer_title);
        work_my_answer_title.setTypeface(typeface);

        work_right_answer = findViewById(R.id.work_right_answer);
        work_answer_analysis = findViewById(R.id.work_answer_analysis);
        total_work_answer_desc = findViewById(R.id.total_work_answer_desc);
        work_desc_date = findViewById(R.id.work_desc_date);
        work_desc_date.setTypeface(typeface);
        work_desc_difficulty = findViewById(R.id.work_desc_difficulty);
        work_desc_difficulty.setTypeface(typeface);

        //手动全部隐藏掉---因为RecyclerView的复用特性
        practice_head_layout.setVisibility(GONE);//随堂练习头部
        work_head_layout.setVisibility(GONE);//题库作业头部
        practice_select_judge_answer.setVisibility(GONE);//随堂练习选择判断答案
        practice_select_judge_answer.setTypeface(typeface);

        practice_blank_answer_layout.setVisibility(GONE);//随堂练习填空答案
        work_answer_layout.setVisibility(GONE);//题库作业答案

        practice_img_one.setOnClickListener(this);
        practice_img_two.setOnClickListener(this);
        practice_img_three.setOnClickListener(this);
        practice_img_frame.setOnClickListener(this);
        //去巩固
        total_work_promote.setOnClickListener(this);
        total_work_promote.setTypeface(typeface);

        //优秀答案
        total_work_good_answer.setOnClickListener(this);
        //获取正确答案
        tv_get_current_quint = (TextView) findViewById(R.id.tv_get_current_quint);
    }
    private void initData() {
        //清空之前的题型
        total_options_layout.removeAllViews();
        work_my_answer.removeAllViews();
        work_right_answer.removeAllViews();
        //题目类型
        int questionType = questionInfo.getQuestionType();

        //区分图片出题模式和题库出题模式---隐藏该隐藏的视图模块
        if (isImgQuestion) {
            //初始化头部
            practice_head_layout.setVisibility(VISIBLE);
            showPracticeHeadInfo(questionType);

            //当前时间
            Date date = new Date();
            //批阅完毕需要显示答案
            if (taskStatus.equals(Constant.Review_Status) ||
                    (!TextUtils.isEmpty(showAnswerDate)
                            && QZXTools.getDateValue(showAnswerDate, "yyyy-MM-dd HH:mm:ss") <= date.getTime())) {
                //批阅后的正确答案   todo   再这里显示答案
                String rightAnswer = questionInfo.getAnswer();
                //当答案不为空字符串时显示答案
                if (!TextUtils.isEmpty(rightAnswer)) {
                    if (questionType == Constant.Fill_Blank) {
                        practice_blank_answer_layout.setVisibility(VISIBLE);
                        if (rightAnswer.contains("|")) {
                            //多道填空答案
                            String[] rightAnswers = rightAnswer.split("\\|");
                            for (int i = 0; i < rightAnswers.length; i++) {
                                showPracticeFillBlankAnswer(i, rightAnswers[i]);
                            }
                        } else {
                            //只有一道填空答案 todo
                            showPracticeFillBlankAnswer(0, rightAnswer);
                        }
                    } else if (questionType == Constant.Judge_Item) {
                        //这里是判断题
                        if (rightAnswer.equals("0")) {
                            //错误选A todo  这个是周斌加的判断的显示，我在我没有在用了
                           // option_do_tv_one.setVisibility(VISIBLE);
                        } else {
                            //正确选B
                            //option_do_tv_two.setVisibility(VISIBLE);
                        }
                    } else if (questionType == Constant.Single_Choose) {
                        practice_select_judge_answer.setVisibility(VISIBLE);
                        practice_select_judge_answer.setText("正确答案：" + rightAnswer);
                    } else if (questionType == Constant.Multi_Choose) {
                        practice_select_judge_answer.setVisibility(VISIBLE);
                        StringBuffer stringBuffer = new StringBuffer();
                        stringBuffer.append("正确答案：");
                        String[] rightAnswers = rightAnswer.split("\\|");
                        for (int i = 0; i < rightAnswers.length; i++) {
                            stringBuffer.append(rightAnswers[i]);
                            if (i < (rightAnswers.length - 1)) {
                                stringBuffer.append("、");
                            }
                        }
                        practice_select_judge_answer.setText(stringBuffer.toString());
                    }
                }
            }
        } else {
            //初始化头部
            work_head_layout.setVisibility(VISIBLE);
            showItemBankHeadInfo(questionType, isMistakesShown);

            //判断是否存在题目的图片说明
            if (!TextUtils.isEmpty(questionInfo.getImage())) {
                practice_img_layout.setVisibility(VISIBLE);
                showPracticeImgs();
            } else {
                practice_img_layout.setVisibility(GONE);
            }

            if (taskStatus.equals(Constant.Review_Status)) {
                //显示我的答案
                showItemBankAnswer(questionType);
            }
        }

        //初始化选项内容---依据题型来加载题型选项   0 单项选择题 1多项选择题 2填空题 3解答题 4连线题 5判断题
        List<QuestionInfo.SelectBean> selectBeans = questionInfo.getList();
        /**
         * 主观题设计拍照和前面的HomeWorkDetailActivity有关联，可以参看一下，
         * 同时答案保存既需要LocalText
         * */
        //获取正确答案 获取类型  0是互动  1 是作业
        int types = bundle.getInt("types",0);
        if (taskStatus.equals(Constant.Todo_Status)) {
            //查询保存的答案,这是多选，所以存在多个答案
            LocalTextAnswersBean localTextAnswersBean = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                    .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfo.getId()),
                            LocalTextAnswersBeanDao.Properties.HomeworkId.eq(questionInfo.getHomeworkId()),
                            LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

            //塞入主观题数据
            SubjectiveToDoView subjectiveToDoView = new SubjectiveToDoView(getContext());
            subjectiveToDoView.setQuestionInfo(questionInfo);
            //答案回显,在方法内部判断localTextAnswersBean是否空指针
            subjectiveToDoView.showImgsAndContent(localTextAnswersBean, types,taskStatus);

            subjectiveToDoView.hideAnswerTools(false);

            total_options_layout.addView(subjectiveToDoView);
            if (types==0){
                //只有在互动的时候显示
                tv_get_current_quint.setVisibility(GONE);

            }
            if (localTextAnswersBean==null){
                tv_get_current_quint.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //获取答案
                        EditText subjective_input=   subjectiveToDoView.findViewById(R.id.subjective_input);
                        if (TextUtils.isEmpty(subjective_input.getText())){
                            QZXTools.popToast(getContext(),"输入文字说明不能为空",true);
                            return;
                        }
                        tv_get_current_quint.setText("正确答案: "+questionInfo.getAnswer());
                    }
                });
            }else {
                tv_get_current_quint.setText("正确答案: "+localTextAnswersBean.getAnswerContent());
            }


        } else {
            total_work_good_answer.setVisibility(VISIBLE);

            //塞入已提交或者批阅的选项数据
            SubjectiveToDoView subjectiveToDoView = new SubjectiveToDoView(getContext());
            subjectiveToDoView.hideAnswerTools(true);

            //塞入文本答案以及图片集合答案
            String textAnswer = "";
            if (questionInfo.getOwnList() != null && questionInfo.getOwnList().size() > 0) {
                textAnswer = questionInfo.getOwnList().get(0).getAnswerContent();
            }
            List<String> imgFile = questionInfo.getImgFile();
            LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
            localTextAnswersBean.setAnswerContent(textAnswer);
            localTextAnswersBean.setImageList(imgFile);
            localTextAnswersBean.setAnswer(questionInfo.getAnswer());
            //这个主要显示互动提问中的作业
            //显示图片和学生答案
            subjectiveToDoView.showImgsAndContent(localTextAnswersBean,types,taskStatus);



            total_options_layout.addView(subjectiveToDoView);
        }
    }

    /**
     * 如果是图片出题，则展示练习题头,没有连线题
     */
    private void showPracticeHeadInfo(int questionType) {
        practice_head_type.setText("[主观]");
        practice_head_index.setText("第" + (curPosition + 1) + "题 共" + totalQuestionCount + "题");
        practise_head_score.setText("(" + questionInfo.getScore() + "分)");
    }

    /**
     * 显示作业填空题的答案选项
     *
     * @param optionIndex 选项标题序号，从1开始
     */
    private void showPracticeFillBlankAnswer(int optionIndex, String optionAnswer) {

        optionIndex = optionIndex + 1;

        View view = LayoutInflater.from(getContext()).inflate(R.layout.fill_blank_answer_right, null);
        TextView fill_blank_answer_option = view.findViewById(R.id.fill_blank_answer_option);
        TextView fill_blank_answer_content = view.findViewById(R.id.fill_blank_answer_content);

        //设置数据
        fill_blank_answer_option.setText(optionIndex + "");
        fill_blank_answer_content.setText(optionAnswer);

        practice_blank_answer_option_layout.addView(view);
    }

    private void showItemBankHeadInfo(int questionType, boolean isFromMistakes) {
        //错题集才展示巩固按钮
        if (isFromMistakes && !TextUtils.isEmpty(questionInfo.getKnowledge())) {
            total_work_promote.setVisibility(VISIBLE);
        } else {
            total_work_promote.setVisibility(INVISIBLE);
        }

        total_work_good_answer.setVisibility(INVISIBLE);

        //设置题型和题序
        total_work_title.setText((curPosition + 1) + "、【主观题】");

        //设置题目内容
        if (!TextUtils.isEmpty(questionInfo.getQuestionContent())) {
            total_work_content.setHtml(questionInfo.getQuestionContent(), new HtmlHttpImageGetter(total_work_content));
        }
    }

    /**
     * 展示题库作业的答案：我的答案和正确答案
     */
    private void showItemBankAnswer(int questionType) {
        //主观题不显示答案展示
        if (questionType == Constant.Subject_Item) {
            work_answer_layout.setVisibility(GONE);
        } else {
            work_answer_layout.setVisibility(VISIBLE);
        }

        //展示答案解析
        String analysis = questionInfo.getAnalysis();
        Glide.with(getContext()).load(analysis).into(work_answer_analysis);
        //展示时间和难易度
//        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
//        String dateTime = simpleDateFormat.format(questionInfo.getCreateDate());
        String dateTime = questionInfo.getCreateDate();
        //判空指针
        if (!TextUtils.isEmpty(questionInfo.getLevel())) {
            total_work_answer_desc.setVisibility(VISIBLE);
            switch (questionInfo.getLevel()) {
                case Constant.Level_Easy:
                    //换背景色
                    total_work_answer_desc.setBackground(getResources().getDrawable(R.drawable.shape_easy_tag));
                    work_desc_difficulty.setText("简单");
                    break;
                case Constant.Level_Normal:
                    //换背景色
                    total_work_answer_desc.setBackground(getResources().getDrawable(R.drawable.shape_middle_tag));
                    work_desc_difficulty.setText("中等");
                    break;
                case Constant.Level_Hard:
                    //换背景色
                    total_work_answer_desc.setBackground(getResources().getDrawable(R.drawable.shape_hard_tag));
                    work_desc_difficulty.setText("困难");
                    break;
            }
        } else {
            total_work_answer_desc.setVisibility(GONE);
        }
        //时间线
        work_desc_date.setText(dateTime);
    }

    /**
     * 显示题目附带的图片展示
     * 用竖线分割
     */
    private void showPracticeImgs() {
        //缩略图
        String thumbnails = questionInfo.getImageopted();
        if (thumbnails.contains("|")) {
            String[] thumbSplit = thumbnails.split("\\|");
            for (int i = 0; i < thumbSplit.length; i++) {
                switch (i) {
                    case 0:
                        practice_img_one.setVisibility(VISIBLE);
                        Glide.with(getContext()).load(thumbSplit[0]).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(practice_img_one);
                        break;
                    case 1:
                        practice_img_two.setVisibility(VISIBLE);
                        Glide.with(getContext()).load(thumbSplit[1]).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(practice_img_two);
                        break;
                    case 2:
                        practice_img_three.setVisibility(VISIBLE);
                        Glide.with(getContext()).load(thumbSplit[2]).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(practice_img_three);
                        break;
                    case 3:
                        practice_img_frame.setVisibility(VISIBLE);
                        Glide.with(getContext()).load(thumbSplit[3]).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(practice_img_four);
                        int remainCount = thumbSplit.length - 4;
                        if (remainCount > 0) {
                            practice_img_count.setVisibility(VISIBLE);
                            StringBuilder stringBuilder = new StringBuilder();
                            stringBuilder.append("+");
                            stringBuilder.append(remainCount);
                            stringBuilder.append("张");
                            practice_img_count.setText(stringBuilder.toString());
                        } else {
                            practice_img_count.setVisibility(GONE);
                        }
                        break;
                }
            }
        } else {
            practice_img_one.setVisibility(VISIBLE);
            Glide.with(getContext()).load(thumbnails).placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(practice_img_one);
        }

        if (imgFilePathList == null) {
            imgFilePathList = new ArrayList<>();
        }

        //原图
        String originals = questionInfo.getImage();
        if (originals.contains("|")) {
            String[] originalSplit = originals.split("\\|");
            for (String original : originalSplit) {
//                QZXTools.logE("original=" + original, null);
                imgFilePathList.add(original);
            }
        } else {
            imgFilePathList.add(originals);
        }
    }

    //图片查看器文件集合
    private ArrayList<String> imgFilePathList;

    /**
     * 图片查看器显示
     */
    private void showPhotoView(int curIndex) {
        Intent intent = new Intent(getContext(), ImageLookActivity.class);
        intent.putStringArrayListExtra("imgResources", imgFilePathList);
        intent.putExtra("curImgIndex", curIndex);
        getContext().startActivity(intent);
        //转场动画透明度是非透明的，不符合要求
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation((Activity) mContext, this, "");
//        mContext.startActivity(intent, options.toBundle());
    }

    @Override
    public void onClick(View v) {
        if (QZXTools.canClick()) {
            switch (v.getId()) {
                case R.id.practice_img_one:
                    showPhotoView(0);
                    break;
                case R.id.practice_img_two:
                    showPhotoView(1);
                    break;
                case R.id.practice_img_three:
                    showPhotoView(2);
                    break;
                case R.id.practice_img_frame:
                    showPhotoView(3);
                    break;
                case R.id.total_work_promote:
                    //知识点为空则不提供错题巩固
                    if (TextUtils.isEmpty(questionInfo.getKnowledge())) {
                        QZXTools.popCommonToast(getContext(), "暂时没有巩固提醒", false);
                        return;
                    }

                    /**
                     * 进入错题集做题界面
                     * */
                    Intent intent = new Intent(getContext(), MistakesImproveActivity.class);
                    intent.putExtra("improvement", getBundle());
                    intent.putExtra("questionType", questionInfo.getQuestionType());
                    getContext().startActivity(intent);
                    break;
                case R.id.total_work_good_answer:
                    Intent intent_good = new Intent(getContext(), PerfectAnswerActivity.class);
                    intent_good.putExtra("questionId", questionInfo.getId());
                    intent_good.putExtra("homeworkId", questionInfo.getHomeworkId());
                    getContext().startActivity(intent_good);
                    break;
            }
        }
    }
}
