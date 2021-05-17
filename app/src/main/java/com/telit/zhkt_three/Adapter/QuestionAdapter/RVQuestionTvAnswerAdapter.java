package com.telit.zhkt_three.Adapter.QuestionAdapter;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.AfterHomeWork.LearnResourceActivity;
import com.telit.zhkt_three.Activity.AfterHomeWork.TypicalAnswersActivity;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Activity.HomeWork.WhiteBoardActivity;
import com.telit.zhkt_three.Activity.MistakesCollection.PerfectAnswerActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.FullBlankView;
import com.telit.zhkt_three.CustomView.JudgeImageNewView;
import com.telit.zhkt_three.CustomView.LinkLineView;
import com.telit.zhkt_three.CustomView.MulipleChoiseView;
import com.telit.zhkt_three.CustomView.SubjectImagesView;
import com.telit.zhkt_three.CustomView.TowMulipleChoiseView;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.JavaBean.SubjeatSaveBean;
import com.telit.zhkt_three.JavaBean.SubjectBean;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.greendao.LocalTextAnswersBeanDao;
import com.telit.zhkt_three.greendao.SubjeatSaveBeanDao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;


/**
 * author: qzx
 * Date: 2019/5/23 16:44
 * <p>
 * 注意：这个和拍照出题公用Adapter，所以这里判断是否是错题集入口分别进入拍照出题/题库出题
 */
public class RVQuestionTvAnswerAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements ZBVPermission.PermPassResult {
    private static final String TAG = "RVQuestionTvAnswerAdapter";
    private Context mContext;

    //0未提交  1 已提交  2 已批阅
    private String taskStatus;

    //是否是错题集展示界面
    private boolean isMistakesShown;
    //0 互动  1作业
    private int homeWorkType;

    //输入表情前的光标位置
    private int cursorPos;
    //输入表情前EditText中的文本
    private String inputAfterText;
    //是否重置了EditText的内容
    private boolean resetText;
    private int layoutPosition;

    private static final String[] needPermissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 是否是图片出题模式即随堂练习模式
     */
    private boolean isImageTask;

    private List<QuestionInfo> questionInfoList;

    private String homeworkId;

    private List<SubjectBean> subjectBeanList = new ArrayList<>();

    /**
     * 主观题题目ID
     */
    public static String subjQuestionId;


    private QuestionInfo subjectQuestionInfo;
    private LocalTextAnswersBean linkLocal;

    /**
     * 如果homeworkid为空字符串表示没有homeworkid，在QuestionInfo中存在homeworkid
     */
    public void setQuestionInfoList(List<QuestionInfo> questionInfoList, String homeworkId) {
        //这个是提真正的数据
        this.questionInfoList = questionInfoList;
        this.homeworkId = homeworkId;
    }

    private String imageAnsterType;


    //单选题，复用的问题
    // private List<SingleBean> singleBeans=new ArrayList<>();


    /**
     * 塞入错题巩固必传信息:
     * 学段、学科、难易度以及题型
     */
    public void fetchNeedParam(String imageAnsterType) {
        this.imageAnsterType = imageAnsterType;
        QZXTools.logE("imageAnsterType=" + imageAnsterType, null);
    }

    /**
     * @param status          作业的状态
     * @param isImageQuestion 是否图片出题模式
     * @param mistakesShown   是否是错题集展示界面
     * @param homeWorkType    0是互动   1是作业
     */
    public RVQuestionTvAnswerAdapter(Context context, String status, boolean isImageQuestion, boolean mistakesShown, int homeWorkType) {
        mContext = context;
        taskStatus = status;
        isImageTask = isImageQuestion;
        isMistakesShown = mistakesShown;
        this.homeWorkType = homeWorkType;

    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == Constant.Single_Choose) {
            return new SingleChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.single_choose_view, viewGroup, false));
        } else if (i == Constant.Fill_Blank) {
            return new FillBlankHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.fill_blank_new_layout, null, false));
        } else if (i == Constant.Subject_Item) {
            return new SubjectItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.subject_item_image_show_layout, viewGroup, false));
        } else if (i == Constant.Linked_Line) {
            return new LinkedLineHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.linked_line_new_layout, viewGroup, false));
        } else if (i == Constant.Judge_Item) {
            return new JudgeItemHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.judgeselect_two_new_layout, viewGroup, false));
        } else if (i == Constant.Multi_Choose) {
            return new MultiChooseHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.mulit_choose_layout, viewGroup, false));
        }
        return null;
    }

    @SuppressLint("LongLogTag")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof SingleChooseHolder) {
            //   viewHolder.setIsRecyclable(false);
            //单选题
            //设置状态
            ((SingleChooseHolder) viewHolder).mcv_choise_view.setTaskStatus(taskStatus);
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();

            //设置数据
            ((SingleChooseHolder) viewHolder).mcv_choise_view.setViewData(selectBeans, questionInfoList, i, homeworkId, homeWorkType);

            //如果是错题集
            if (isMistakesShown && imageAnsterType.equals("1")) {
                ((SingleChooseHolder) viewHolder).iv_mistake_my_quint.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(questionInfoList.get(i).getAttachment())
                        .into(((SingleChooseHolder) viewHolder).iv_mistake_my_quint);
            }

            if (homeWorkType==1){//作业
                ((SingleChooseHolder) viewHolder).tv_learn_resource.setVisibility(VISIBLE);

                ((SingleChooseHolder) viewHolder).tv_learn_resource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardLearnResource(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                    }
                });
            }else {
                ((SingleChooseHolder) viewHolder).tv_learn_resource.setVisibility(GONE);
            }
        } else if (viewHolder instanceof MultiChooseHolder) {
            //多选题
            //设置状态
            ((MultiChooseHolder) viewHolder).two_tmcw.setTaskStatus(taskStatus);
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();

            //设置数据
            ((MultiChooseHolder) viewHolder).two_tmcw.setViewData(selectBeans, questionInfoList, i, homeworkId, homeWorkType);

            //如果是错题集 是图片出题
            if (isMistakesShown && imageAnsterType.equals("1")) {
                ((MultiChooseHolder) viewHolder).iv_mistake_my_quint.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(questionInfoList.get(i).getAttachment())
                        .into(((MultiChooseHolder) viewHolder).iv_mistake_my_quint);
            }

            if (homeWorkType==1){//作业
                ((MultiChooseHolder) viewHolder).tv_learn_resource.setVisibility(VISIBLE);

                ((MultiChooseHolder) viewHolder).tv_learn_resource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardLearnResource(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                    }
                });
            }else {
                ((MultiChooseHolder) viewHolder).tv_learn_resource.setVisibility(GONE);
            }
        } else if (viewHolder instanceof FillBlankHolder) {
            //todo  填空题的复用延期处理
            viewHolder.setIsRecyclable(false);
            //填空题

            //设置状态
            ((FillBlankHolder) viewHolder).full_blank_new.setTaskStatus(taskStatus);
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            ((FillBlankHolder) viewHolder).full_blank_new.setViewData(selectBeans, questionInfoList, i, homeworkId, homeWorkType);

            //如果是错题集
            if (isMistakesShown && imageAnsterType.equals("1")) {
                ((FillBlankHolder) viewHolder).iv_mistake_my_quint.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(questionInfoList.get(i).getAttachment())
                        .into(((FillBlankHolder) viewHolder).iv_mistake_my_quint);
            }

            if (homeWorkType==1){//作业
                ((FillBlankHolder) viewHolder).tv_learn_resource.setVisibility(VISIBLE);

                ((FillBlankHolder) viewHolder).tv_learn_resource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardLearnResource(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                    }
                });
            }else {
                ((FillBlankHolder) viewHolder).tv_learn_resource.setVisibility(GONE);
            }
        } else if (viewHolder instanceof JudgeItemHolder) {
            //todo 延期处理复用的问题
            viewHolder.setIsRecyclable(false);
            //判断题
            //设置状态
            ((JudgeItemHolder) viewHolder).judge_image_view.setTaskStatus(taskStatus);
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();

            //设置数据
            ((JudgeItemHolder) viewHolder).judge_image_view.setViewData(selectBeans, questionInfoList, i, homeworkId, homeWorkType);

            //如果是错题集
            if (isMistakesShown && imageAnsterType.equals("1")) {
                ((JudgeItemHolder) viewHolder).iv_mistake_my_quint.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(questionInfoList.get(i).getAttachment())
                        .into(((JudgeItemHolder) viewHolder).iv_mistake_my_quint);
            }

            if (homeWorkType==1){//作业
                ((JudgeItemHolder) viewHolder).tv_learn_resource.setVisibility(VISIBLE);

                ((JudgeItemHolder) viewHolder).tv_learn_resource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardLearnResource(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                    }
                });
            }else {
                ((JudgeItemHolder) viewHolder).tv_learn_resource.setVisibility(GONE);
            }
        } else if (viewHolder instanceof SubjectItemHolder) {
            //主观题
            //todo 延期处理复用的问题
            viewHolder.setIsRecyclable(false);


            //如果是错题集
            if (isMistakesShown && imageAnsterType.equals("1")) {
                ((SubjectItemHolder) viewHolder).iv_mistake_my_quint.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(questionInfoList.get(i).getAttachment())
                        .into(((SubjectItemHolder) viewHolder).iv_mistake_my_quint);
            }

            QZXTools.logD("主观题下标" + viewHolder.getLayoutPosition());
            ((SubjectItemHolder) viewHolder).practice_head_index.setText("第" + (i + 1) + "题 共" + questionInfoList.size() + "题");
            //0 互动  1作业
            if (homeWorkType == 0) {
                siv_images.setHideDel();
            }

            //0未提交  1 已提交  2 已批阅    //如果当前状态是打回重做
            if (taskStatus.equals(Constant.Todo_Status) || taskStatus.equals(Constant.Retry_Status) || taskStatus.equals(Constant.Save_Status)) {
                //答案的回显
                if (taskStatus.equals(Constant.Todo_Status) || taskStatus.equals(Constant.Retry_Status)) {
                    //只有是作业显示  互动不获取
                    if (homeWorkType == 1) {
                        linkLocal = MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao()
                                .queryBuilder().where(LocalTextAnswersBeanDao.Properties.QuestionId.eq(questionInfoList.get(i).getId()),
                                        LocalTextAnswersBeanDao.Properties.HomeworkId.eq(homeworkId),
                                        LocalTextAnswersBeanDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
                        Log.i(TAG, "onBindViewHolder: " + linkLocal);
                    }


                    siv_images.setTag(questionInfoList.get(i).getId());

                    //输入栏的回显
                    if (linkLocal != null && !TextUtils.isEmpty(linkLocal.getAnswerContent())) {
                        subjective_input.setText(linkLocal.getAnswerContent());
                    }


                    //点击具体的一个拍照显示的图片
                    if (notifyItem) {
                        if (i == layoutPosition) {
                            notifyItem = false;

                            SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                                    .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(questionInfoList.get(i).getId())).unique();
                            if (saveBean != null) {
                                if (!TextUtils.isEmpty(saveBean.getImages())) {
                                    String images = saveBean.getImages();
                                    String[] strings = images.split("\\|");
                                    Log.i(TAG, "onClick: " + strings);
                                    imgFilePathList.clear();
                                    for (String string : strings) {
                                        imgFilePathList.add(string);
                                    }

                                    //每次都创建一个新的recycleview
                                    if (imgFilePathList.size() > 0)
                                        siv_images.fromCameraCallback(imgFilePathList);


                                    //保存数据

                                    //-------------------------答案保存，依据作业题目id
                                    LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                                    localTextAnswersBean.setHomeworkId(homeworkId);
                                    localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                                    localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                                    localTextAnswersBean.setAnswerContent("");
                                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                                    localTextAnswersBean.setAnswer(questionInfoList.get(i).getAnswer());
                                    localTextAnswersBean.setImageList(imgFilePathList);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                                    //插入或者更新数据库
                                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                                }
                            }
                        } else {

                            SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                                    .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(questionInfoList.get(i).getId())).unique();
                            if (saveBean != null) {
                                if (!TextUtils.isEmpty(saveBean.getImages())) {
                                    String images = saveBean.getImages();
                                    String[] strings = images.split("\\|");
                                    Log.i(TAG, "onClick: " + strings);
                                    imgFilePathList.clear();
                                    for (String string : strings) {
                                        imgFilePathList.add(string);
                                    }

                                    if (imgFilePathList.size() > 0) {

                                        siv_images.fromCameraCallback(imgFilePathList);
                                    }

                                }
                            }
                        }

                    } else {
                        //数据的回显
                        if (taskStatus.equals(Constant.Todo_Status)) {
                            SubjeatSaveBean saveBeanTo = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                                    .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(questionInfoList.get(i).getId()))
                                    .where(SubjeatSaveBeanDao.Properties.LayoutPosition.eq(i))
                                    .unique();

                            if (saveBeanTo != null && saveBeanTo.getLayoutPosition() == i) {
                                if (!TextUtils.isEmpty(saveBeanTo.getImages())) {
                                    String images = saveBeanTo.getImages();
                                    String[] strings = images.split("\\|");
                                    Log.i(TAG, "onClick: " + strings);
                                    imgFilePathList.clear();
                                    for (String string : strings) {
                                        imgFilePathList.add(string);
                                    }

                                    if (imgFilePathList.size() > 0) {

                                        siv_images.fromCameraCallback(imgFilePathList);
                                    }


                                } else {
                                    imgFilePathList.clear();
                                    siv_images.fromCameraCallback(imgFilePathList);
                                }
                            }
                        } else if (taskStatus.equals(Constant.Retry_Status)) {
                            //当前状态是打回重做
                            imgFilePathList.clear();
                            siv_images.fromCameraCallback(imgFilePathList);
                            MyApplication.getInstance().getDaoSession()
                                    .getLocalTextAnswersBeanDao().deleteByKey(questionInfoList.get(i).getId());
                            MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao().deleteByKey(questionInfoList.get(i).getId());

                        }

                    }
                } else if (taskStatus.equals(Constant.Save_Status)) {
                    List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();
                    if (ownList != null && ownList.size() > 0) {
                        String images = ownList.get(0).getAttachment();
                        if (!TextUtils.isEmpty(images)) {

                            String[] strings = images.split("\\|");
                            Log.i(TAG, "onClick: " + strings);
                            imgFilePathList.clear();
                            for (String string : strings) {
                                imgFilePathList.add(string);
                            }

                            if (imgFilePathList.size() > 0) {

                                siv_images.fromCameraCallback(imgFilePathList);
                            }
                        } else {
                            imgFilePathList.clear();
                            siv_images.fromCameraCallback(imgFilePathList);
                        }
                    }
                    //输入栏的回显 学生答案
                    if (ownList != null && ownList.size() > 0) {
                        subjective_input.setText(ownList.get(0).getAnswerContent());

                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(questionInfoList.get(i).getHomeworkId());
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(ownList.get(0).getAnswerContent());
                        String attachment = ownList.get(0).getAttachment();
                        String[] strings = attachment.split("\\|");
                        if (imgFilePathList == null) imgFilePathList = new ArrayList<>();
                        imgFilePathList.clear();
                        for (String string : strings) {
                            imgFilePathList.add(string);
                        }
                        localTextAnswersBean.setImageList(imgFilePathList);
                        QZXTools.logE("subjective Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);

                    }
                }


                //拍照
                ((SubjectItemHolder) viewHolder).subjective_camera.setOnClickListener(new View.OnClickListener() {


                    @Override
                    public void onClick(View v) {
                        layoutPosition = i;
                        Log.i(TAG, "onClick:layoutPosition= " + layoutPosition + ".............." + i);
                        subjQuestionId = questionInfoList.get(layoutPosition).getId();
                        subjectQuestionInfo = questionInfoList.get(layoutPosition);

                        SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                                .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(questionInfoList.get(i).getId())).unique();
                        if (saveBean != null) {
                            if (!TextUtils.isEmpty(saveBean.getImages())) {
                                String images = saveBean.getImages();
                                String[] strings = images.split("\\|");
                                Log.i(TAG, "onClick: " + strings);
                                imgFilePathList.clear();
                                for (String string : strings) {
                                    imgFilePathList.add(string);
                                }
                            }
                        } else {
                            imgFilePathList.clear();
                        }

                        if (imgFilePathList.size() >= 3) {
                            QZXTools.popCommonToast(MyApplication.getInstance(), "图片答案不得超过三张", false);
                            return;
                        }
                        ZBVPermission.getInstance().setPermPassResult(RVQuestionTvAnswerAdapter.this);
                        if (!ZBVPermission.getInstance().hadPermissions((Activity) mContext, needPermissions)) {
                            ZBVPermission.getInstance().requestPermissions((Activity) mContext, needPermissions);
                        } else {
                            //直接打开相机
                            QZXTools.logD("已拥有权限直接打开相机");
                            openCamera();
                        }
                    }
                });
                //白版的点击事件
                ((SubjectItemHolder) viewHolder).subjective_board.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        layoutPosition = viewHolder.getLayoutPosition();
                        subjQuestionId = questionInfoList.get(layoutPosition).getId();
                        subjectQuestionInfo = questionInfoList.get(layoutPosition);


                        SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                                .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(questionInfoList.get(i).getId())).unique();
                        if (saveBean != null) {
                            if (!TextUtils.isEmpty(saveBean.getImages())) {
                                String images = saveBean.getImages();
                                String[] strings = images.split("\\|");
                                Log.i(TAG, "onClick: " + strings);
                                imgFilePathList.clear();
                                for (String string : strings) {
                                    imgFilePathList.add(string);
                                }
                            }
                        } else {
                            imgFilePathList.clear();
                        }


                        if (imgFilePathList.size() >= 3) {
                            QZXTools.popCommonToast(MyApplication.getInstance(), "图片答案不得超过三张", false);
                            return;
                        }

                        ZBVPermission.getInstance().setPermPassResult(RVQuestionTvAnswerAdapter.this);
                        if (!ZBVPermission.getInstance().hadPermissions((Activity) mContext, needPermissions)) {
                            ZBVPermission.getInstance().requestPermissions((Activity) mContext, needPermissions);
                        } else {
                            Intent intent = new Intent(mContext, WhiteBoardActivity.class);
                            intent.putExtra("extra_info", questionInfoList.get(i).getId());
                            mContext.startActivity(intent);
                        }
                    }
                });

                //添加文本输入改变监听

                subjective_input.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                        if (!resetText) {
                            cursorPos = subjective_input.getSelectionEnd();
                            // 这里用s.toString()而不直接用s是因为如果用s，
                            // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，
                            // inputAfterText也就改变了，那么表情过滤就失败了
                            inputAfterText = s.toString();
                        }
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        if (!resetText) {
                            if (count >= 2) {//表情符号的字符长度最小为2
                                if ((cursorPos + count) <= s.toString().trim().length()) {
                                    CharSequence input = s.subSequence(cursorPos, cursorPos + count);
                                    if (ViewUtils.containsEmoji(input.toString())) {
                                        resetText = true;
                                        Toast.makeText(mContext, "不支持输入Emoji表情符号", Toast.LENGTH_SHORT).show();
                                        //是表情符号就将文本还原为输入表情符号之前的内容
                                        subjective_input.setText(inputAfterText);
                                        QZXTools.logE("inputAfterText:" + inputAfterText, null);
                                        CharSequence text = subjective_input.getText();
                                        if (text.length() > 0) {
                                            if (text instanceof Spannable) {
                                                Spannable spanText = (Spannable) text;
                                                Selection.setSelection(spanText, text.length());
                                            }
                                        }
                                    }
                                }
                            }
                        } else {
                            resetText = false;
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                        //-------------------------答案保存，依据作业题目id
                        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
                        localTextAnswersBean.setHomeworkId(homeworkId);
                        localTextAnswersBean.setQuestionId(questionInfoList.get(i).getId());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                        localTextAnswersBean.setQuestionType(questionInfoList.get(i).getQuestionType());
                        localTextAnswersBean.setAnswerContent(s.toString().trim());
                        localTextAnswersBean.setUserId(UserUtils.getUserId());
                        localTextAnswersBean.setAnswer(questionInfoList.get(i).getAnswer());


                        SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                                .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(questionInfoList.get(i).getId())).unique();
                        if (saveBean != null) {
                            if (!TextUtils.isEmpty(saveBean.getImages())) {
                                String images = saveBean.getImages();
                                String[] strings = images.split("\\|");
                                Log.i(TAG, "onClick: " + strings);
                                imgFilePathList.clear();
                                for (String string : strings) {
                                    imgFilePathList.add(string);
                                }
                            }
                        }


                        localTextAnswersBean.setImageList(imgFilePathList);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                        //插入或者更新数据库
                        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                        //-------------------------答案保存，依据作业题目id

                        QZXTools.logE("保存主观题答案:" + new Gson().toJson(localTextAnswersBean), null);
                    }
                });

            } else {
                //提交后答案的回显
                subjective_answer_tool_layout.setVisibility(GONE);
                siv_images.setHideDel();
                //显示图片和文字

                List<WorkOwnResult> ownList = questionInfoList.get(i).getOwnList();
                if (ownList != null && ownList.size() > 0) {
                    String images = ownList.get(0).getAttachment();
                    if (!TextUtils.isEmpty(images)) {

                        String[] strings = images.split("\\|");
                        Log.i(TAG, "onClick: " + strings);
                        imgFilePathList.clear();
                        for (String string : strings) {
                            imgFilePathList.add(string);
                        }

                        if (imgFilePathList.size() > 0) {

                            siv_images.fromCameraCallback(imgFilePathList);
                        }
                    } else {
                        imgFilePathList.clear();
                        siv_images.fromCameraCallback(imgFilePathList);
                    }
                }
                //输入栏的回显 学生答案
                if (ownList != null && ownList.size() > 0) {
                    subjective_input.setText("我的答案: " + ownList.get(0).getAnswerContent());
                }
                //正确答案
                subjective_input_teacher.setVisibility(VISIBLE);
                subjective_input_teacher.setText("正确答案: " + questionInfoList.get(i).getAnswer());

            }

            if (homeWorkType==1){//作业
                if ("1".equals(taskStatus)||"2".equals(taskStatus)){
                    ((SubjectItemHolder) viewHolder).tv_typical_answers.setVisibility(VISIBLE);
                    ((SubjectItemHolder) viewHolder).tv_work_good_answer.setVisibility(VISIBLE);

                    ((SubjectItemHolder) viewHolder).tv_typical_answers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            forwardTypicalAnswers(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                        }
                    });
                    ((SubjectItemHolder) viewHolder).tv_work_good_answer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            forwardPerfectAnswer(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                        }
                    });

                }else {
                    ((SubjectItemHolder) viewHolder).tv_typical_answers.setVisibility(GONE);
                    ((SubjectItemHolder) viewHolder).tv_work_good_answer.setVisibility(GONE);
                }

                ((SubjectItemHolder) viewHolder).tv_learn_resource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardLearnResource(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                    }
                });
            }else {//互动
                ((SubjectItemHolder) viewHolder).tv_typical_answers.setVisibility(GONE);
                ((SubjectItemHolder) viewHolder).tv_work_good_answer.setVisibility(GONE);
                ((SubjectItemHolder) viewHolder).tv_learn_resource.setVisibility(GONE);
            }
        } else if (viewHolder instanceof LinkedLineHolder) {
            //连线题
            // viewHolder.setIsRecyclable(false);
            QZXTools.logE("ToLine viewHolder instanceof LinkedLineHolder......" + questionInfoList.get(i), null);

            //设置状态
            ((LinkedLineHolder) viewHolder).link_line_new.setTaskStatus(taskStatus);
            List<QuestionInfo.SelectBean> selectBeans = questionInfoList.get(i).getList();
            ((LinkedLineHolder) viewHolder).link_line_new.setViewData(selectBeans, questionInfoList, i, homeworkId);

            //如果是错题集
            if (isMistakesShown && imageAnsterType.equals("1")) {
                ((LinkedLineHolder) viewHolder).iv_mistake_my_quint.setVisibility(VISIBLE);
                Glide.with(mContext)
                        .load(questionInfoList.get(i).getAttachment())
                        .into(((LinkedLineHolder) viewHolder).iv_mistake_my_quint);
            }

            if (homeWorkType==1){//作业
                ((LinkedLineHolder) viewHolder).tv_learn_resource.setVisibility(VISIBLE);

                ((LinkedLineHolder) viewHolder).tv_learn_resource.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        forwardLearnResource(questionInfoList.get(i).getId(),questionInfoList.get(i).getHomeworkId());
                    }
                });
            }else {
                ((LinkedLineHolder) viewHolder).tv_learn_resource.setVisibility(GONE);
            }
        }
    }

    /**
     * 跳转查看学习资源
     *
     * @param questionId
     * @param homeworkId
     */
    private void forwardLearnResource(String questionId,String homeworkId){
        Intent intent = new Intent(mContext, LearnResourceActivity.class);
        intent.putExtra("questionId", questionId);
        intent.putExtra("homeworkId", homeworkId);
        mContext.startActivity(intent);
    }

    /**
     * 跳转典型答题
     *
     * @param questionId
     * @param homeworkId
     */
    private void forwardTypicalAnswers(String questionId,String homeworkId){
        Intent intent = new Intent(mContext, TypicalAnswersActivity.class);
        intent.putExtra("questionId", questionId);
        intent.putExtra("homeworkId", homeworkId);
        mContext.startActivity(intent);
    }

    /**
     * 跳转优秀答案
     *
     * @param questionId
     * @param homeworkId
     */
    private void forwardPerfectAnswer(String questionId,String homeworkId){
        Intent intent = new Intent(mContext, PerfectAnswerActivity.class);
        intent.putExtra("questionId", questionId);
        intent.putExtra("homeworkId", homeworkId);
        mContext.startActivity(intent);
    }


    @Override
    public int getItemCount() {
        return questionInfoList != null ? questionInfoList.size() : 0;
    }

    @Override
    public void grantPermission() {

    }

    @Override
    public void denyPermission() {

    }


    /**
     * 指定比例压缩到文件
     */
    public File compressBitmapToFile(String srcPath, float desWidth) {
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;//只读边,不读内容
        Bitmap bitmap;
        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float desHeight = desWidth * h / w;
        int be = 1;
        if (w > h && w > desWidth) {
            be = (int) (newOpts.outWidth / desWidth);
        } else if (w < h && h > desHeight) {
            be = (int) (newOpts.outHeight / desHeight);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//设置采样率

//        newOpts.inPreferredConfig = Config.ARGB_8888;//该模式是默认的,可不设
        newOpts.inPurgeable = true;// 同时设置才会有效
        newOpts.inInputShareable = true;//。当系统内存不够时候图片自动被回收

        bitmap = BitmapFactory.decodeFile(srcPath, newOpts);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        String fileDir = QZXTools.getExternalStorageForFiles(mContext, Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".jpg");
        File file = new File(fileDir, stringBuilder.toString());

        try {
            FileOutputStream fos = new FileOutputStream(file);
            try {
                fos.write(baos.toByteArray());
                fos.flush();
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return file;
    }


    public class SingleChooseHolder extends RecyclerView.ViewHolder {
        private final MulipleChoiseView mcv_choise_view;
        private final ImageView iv_mistake_my_quint;
        private TextView tv_learn_resource;
        public SingleChooseHolder(@NonNull View itemView) {
            super(itemView);
            mcv_choise_view = itemView.findViewById(R.id.mcv_choise_view);
            //自定义出题的图片
            iv_mistake_my_quint = itemView.findViewById(R.id.iv_mistake_my_quint);
            tv_learn_resource = itemView.findViewById(R.id.tv_learn_resource);
        }
    }

    public class MultiChooseHolder extends RecyclerView.ViewHolder {
        private final TowMulipleChoiseView two_tmcw;
        private final ImageView iv_mistake_my_quint;
        private TextView tv_learn_resource;
        public MultiChooseHolder(@NonNull View itemView) {
            super(itemView);
            two_tmcw = itemView.findViewById(R.id.two_tmcw);

            //自定义出题的图片
            iv_mistake_my_quint = itemView.findViewById(R.id.iv_mistake_my_quint);

            tv_learn_resource = itemView.findViewById(R.id.tv_learn_resource);
        }
    }


    private EditText subjective_input;
    private TextView subjective_input_teacher;
    private SubjectImagesView siv_images;
    private RelativeLayout subjective_answer_tool_layout;


    public class SubjectItemHolder extends RecyclerView.ViewHolder {

        private final TextView practice_head_index;
        private final TextView subjective_camera;
        private final TextView subjective_board;

        private final ImageView iv_mistake_my_quint;

        private  TextView tv_learn_resource;
        private  TextView tv_typical_answers;
        private  TextView tv_work_good_answer;

        public SubjectItemHolder(@NonNull View itemView) {
            super(itemView);
            practice_head_index = itemView.findViewById(R.id.practice_head_index);

            //拍照
            subjective_camera = itemView.findViewById(R.id.subjective_camera);
            //画板
            subjective_board = itemView.findViewById(R.id.subjective_board);
            subjective_answer_tool_layout = itemView.findViewById(R.id.subjective_answer_tool_layout);


            //输入的内容
            subjective_input = itemView.findViewById(R.id.subjective_input);
            subjective_input_teacher = itemView.findViewById(R.id.subjective_input_teacher);
            siv_images = itemView.findViewById(R.id.siv_images);

            //自定义出题的图片
            iv_mistake_my_quint = itemView.findViewById(R.id.iv_mistake_my_quint);

            tv_learn_resource = itemView.findViewById(R.id.tv_learn_resource);
            tv_typical_answers = itemView.findViewById(R.id.tv_typical_answers);
            tv_work_good_answer = itemView.findViewById(R.id.tv_work_good_answer);
        }
    }

    //填空题
    public class FillBlankHolder extends RecyclerView.ViewHolder {
        private final FullBlankView full_blank_new;
        private final ImageView iv_mistake_my_quint;
        private TextView tv_learn_resource;
        public FillBlankHolder(@NonNull View itemView) {
            super(itemView);
            full_blank_new = itemView.findViewById(R.id.full_blank_new);

            //自定义出题的图片
            iv_mistake_my_quint = itemView.findViewById(R.id.iv_mistake_my_quint);

            tv_learn_resource = itemView.findViewById(R.id.tv_learn_resource);
        }
    }

    public class LinkedLineHolder extends RecyclerView.ViewHolder {
        private final LinkLineView link_line_new;
        private final ImageView iv_mistake_my_quint;

        private TextView tv_learn_resource;

        public LinkedLineHolder(@NonNull View itemView) {
            super(itemView);

            link_line_new = itemView.findViewById(R.id.link_line_new);

            //自定义出题的图片
            iv_mistake_my_quint = itemView.findViewById(R.id.iv_mistake_my_quint);

            tv_learn_resource =  itemView.findViewById(R.id.tv_learn_resource);
        }
    }

    public class JudgeItemHolder extends RecyclerView.ViewHolder {
        private final JudgeImageNewView judge_image_view;
        private final ImageView iv_mistake_my_quint;
        private TextView tv_learn_resource;
        public JudgeItemHolder(@NonNull View itemView) {
            super(itemView);

            //自定义出题的图片
            iv_mistake_my_quint = itemView.findViewById(R.id.iv_mistake_my_quint);
            judge_image_view = itemView.findViewById(R.id.judge_image_view);
            tv_learn_resource = itemView.findViewById(R.id.tv_learn_resource);
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

    /**
     * 打开相机
     */
    private File cameraFile;
    public static final int CODE_SYS_CAMERA = 1;//系统相机RequestCode

    private void openCamera() {
        String fileDir = QZXTools.getExternalStorageForFiles(mContext, Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));

        stringBuilder.append(".jpg");
        cameraFile = new File(fileDir, stringBuilder.toString());

        Uri cameraUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraUri = FileProvider.getUriForFile(mContext, mContext.getPackageName()
                    + ".fileprovider", cameraFile);
        } else {
            cameraUri = Uri.fromFile(cameraFile);
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        //设置拍照保存的路径，需要特别注意的是在onActivityResult中获取的Intent为空
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        cameraIntent.putExtra("extra_info", subjectQuestionInfo.getId());
        ((Activity) mContext).startActivityForResult(cameraIntent, CODE_SYS_CAMERA);
    }

    /**
     * 主观题拍照后获取数据
     *
     * @param flag
     */
    //图片文件
    private ArrayList<String> imgFilePathList = new ArrayList<>();

    private boolean notifyItem = false;
    public void fromCameraCallback(String flag) {
        if (flag.equals("CAMERA_CALLBACK")) {
            notifyItem = true;
            QZXTools.logE("fromCameraCallback filePath=" + cameraFile.getAbsolutePath(), null);
            //之所以判断是因为所有的其他SubjectiveToDoView也可以接受到这个反馈
            Log.i("", "fromCameraCallback: " + subjectQuestionInfo);
            if (subjQuestionId.equals(subjectQuestionInfo.getId() + "")) {
                //质量压缩处理
//                File compressFile = compressImage(BitmapFactory.decodeFile(cameraFile.getAbsolutePath()));

                //比例尺寸压缩 notes:这个比质量压缩的要快，效果也很不错
                File compressFile = compressBitmapToFile(cameraFile.getAbsolutePath(),
                        mContext.getResources().getDimensionPixelSize(R.dimen.x800));

                // imgFilePathList.add(compressFile.getAbsolutePath());
                SubjeatSaveBean subjeatSaveBean = new SubjeatSaveBean();
                subjeatSaveBean.setId(subjectQuestionInfo.getId() + "");
                subjeatSaveBean.setLayoutPosition(layoutPosition);
                SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                        .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(subjectQuestionInfo.getId() + "")).unique();
                if (saveBean != null && !TextUtils.isEmpty(saveBean.getImages())) {
                    String beanImages = saveBean.getImages();
                    subjeatSaveBean.setImages(beanImages + "|" + compressFile.getAbsolutePath());
                } else {
                    subjeatSaveBean.setImages(compressFile.getAbsolutePath());
                }

                MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao().insertOrReplace(subjeatSaveBean);


                //notifyItemChanged(layoutPosition);
                notifyDataSetChanged();
            }
        } else {
            notifyDataSetChanged();
        }
    }

    //白班数据的保存
    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
        if (extraInfoBean.getQuestionId().equals(subjectQuestionInfo.getId())) {
            //  imgFilePathList.add(extraInfoBean.getFilePath());
            notifyItem = true;
            SubjeatSaveBean subjeatSaveBean = new SubjeatSaveBean();
            subjeatSaveBean.setId(subjectQuestionInfo.getId() + "");
            subjeatSaveBean.setLayoutPosition(layoutPosition);
            SubjeatSaveBean saveBean = MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao()
                    .queryBuilder().where(SubjeatSaveBeanDao.Properties.Id.eq(subjectQuestionInfo.getId() + "")).unique();
            if (saveBean != null) {
                String beanImages = saveBean.getImages();
                subjeatSaveBean.setImages(beanImages + "|" + extraInfoBean.getFilePath());
            } else {
                subjeatSaveBean.setImages(extraInfoBean.getFilePath());
            }
            MyApplication.getInstance().getDaoSession().getSubjeatSaveBeanDao().insertOrReplace(subjeatSaveBean);
            //notifyItemChanged(layoutPosition);
            notifyDataSetChanged();

        }
    }
}
