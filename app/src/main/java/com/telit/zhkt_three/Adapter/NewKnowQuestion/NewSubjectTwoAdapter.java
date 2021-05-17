package com.telit.zhkt_three.Adapter.NewKnowQuestion;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.content.FileProvider;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Activity.HomeWork.WhiteBoardActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.QuestionView.NewKnowledgeQuestionView;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class NewSubjectTwoAdapter extends RecyclerView.Adapter<NewSubjectTwoAdapter.ViewHolder> {
    private Context mContext;
    private QuestionBank questionInfo;
    private String status;


    private FrameLayout subjective_answer_frame_one;
    private FrameLayout subjective_answer_frame_two;
    private FrameLayout subjective_answer_frame_three;

    private ImageView subjective_img_one;
    private ImageView subjective_img_two;
    private ImageView subjective_img_three;


    private RelativeLayout subjective_del_layout_one;
    private RelativeLayout subjective_del_layout_two;
    private RelativeLayout subjective_del_layout_three;
    private ImageView subjective_del_one;
    private ImageView subjective_del_two;
    private ImageView subjective_del_three;


    private RelativeLayout subjective_answer_tool_layout;
    private TextView subjective_camera;
    private TextView subjective_board;

    private EditText subjective_input;

    private static final String[] needPermissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //图片文件
    private ArrayList<String> imgFilePathList;
    public static final int CODE_SYS_CAMERA = 1;//系统相机RequestCode

    private File cameraFile;
    private boolean isClickCamera = false;
    public NewSubjectTwoAdapter(Context mContext, QuestionBank questionBank, String status) {

        this.mContext = mContext;
        this.questionInfo = questionBank;
        this.status = status;



        String optionJson = questionBank.getAnswerOptions();

        if (status.equals(Constant.Todo_Status) || status.equals(Constant.Retry_Status)) {
            //解析选项   设置题中的内容
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.subjective_option_complete_layout,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        showImgsAndContent(null);
    }



    @Override
    public int getItemCount() {
        return 1;
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, ZBVPermission.PermPassResult {

        public ViewHolder(@NonNull View view) {
            super(view);

            subjective_answer_frame_one = view.findViewById(R.id.subjective_answer_frame_one);
            subjective_answer_frame_two = view.findViewById(R.id.subjective_answer_frame_two);
            subjective_answer_frame_three = view.findViewById(R.id.subjective_answer_frame_three);

            subjective_img_one = view.findViewById(R.id.subjective_img_one);
            subjective_img_two = view.findViewById(R.id.subjective_img_two);
            subjective_img_three = view.findViewById(R.id.subjective_img_three);

            subjective_del_layout_one = view.findViewById(R.id.subjective_del_layout_one);
            subjective_del_layout_two = view.findViewById(R.id.subjective_del_layout_two);
            subjective_del_layout_three = view.findViewById(R.id.subjective_del_layout_three);

            subjective_del_one = view.findViewById(R.id.subjective_del_one);
            subjective_del_two = view.findViewById(R.id.subjective_del_two);
            subjective_del_three = view.findViewById(R.id.subjective_del_three);


            subjective_answer_tool_layout = view.findViewById(R.id.subjective_answer_tool_layout);
            subjective_camera = view.findViewById(R.id.subjective_camera);
            subjective_board = view.findViewById(R.id.subjective_board);

            subjective_input = view.findViewById(R.id.subjective_input);

            subjective_answer_frame_one.setVisibility(View.GONE);
            subjective_answer_frame_two.setVisibility(View.GONE);
            subjective_answer_frame_three.setVisibility(View.GONE);

            subjective_camera.setOnClickListener(this);
            subjective_board.setOnClickListener(this);

            subjective_img_one.setOnClickListener(this);
            subjective_img_two.setOnClickListener(this);
            subjective_img_three.setOnClickListener(this);

            subjective_del_one.setOnClickListener(this);
            subjective_del_two.setOnClickListener(this);
            subjective_del_three.setOnClickListener(this);

            Typeface typeface = Typeface.createFromAsset(mContext.getAssets(), "PingFang-SimpleBold.ttf");
            subjective_camera.setTypeface(typeface);
            subjective_board.setTypeface(typeface);
            subjective_input.setTypeface(typeface);

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.subjective_camera:
                    if (imgFilePathList.size() >= 3) {
                        QZXTools.popCommonToast(mContext, "图片答案不得超过三张", false);
                        return;
                    }

                    //设置主观题ID
                    NewKnowledgeQuestionView.subjQuestionId = questionInfo.getId() + "";

                    isClickCamera = true;

                    ZBVPermission.getInstance().setPermPassResult(this);
                    if (!ZBVPermission.getInstance().hadPermissions((Activity) mContext, needPermissions)) {
                        ZBVPermission.getInstance().requestPermissions((Activity) mContext, needPermissions);
                    } else {
                        //直接打开相机
                        QZXTools.logD("已拥有权限直接打开相机");
                        openCamera();
                    }

                    break;
                case R.id.subjective_board:
                    if (imgFilePathList.size() >= 3) {
                        QZXTools.popCommonToast(mContext, "图片答案不得超过三张", false);
                        return;
                    }

                    isClickCamera = false;

                    ZBVPermission.getInstance().setPermPassResult(this);
                    if (!ZBVPermission.getInstance().hadPermissions((Activity) mContext, needPermissions)) {
                        ZBVPermission.getInstance().requestPermissions((Activity) mContext, needPermissions);
                    } else {
                        Intent intent = new Intent(mContext, WhiteBoardActivity.class);
                        intent.putExtra("extra_info", questionInfo.getId() + "");
                       mContext.startActivity(intent);
                    }
                    break;
                case R.id.subjective_img_one:
                    showPhotoView(0);
                    break;
                case R.id.subjective_img_two:
                    showPhotoView(1);
                    break;
                case R.id.subjective_img_three:
                    showPhotoView(2);
                    break;
                case R.id.subjective_del_one:
                    imgFilePathList.remove(0);
                    //同样删除数据库中的信息
                    showImgsSaveAnswer();
                    break;
                case R.id.subjective_del_two:
                    imgFilePathList.remove(1);
                    showImgsSaveAnswer();
                    break;
                case R.id.subjective_del_three:
                    imgFilePathList.remove(2);
                    showImgsSaveAnswer();
                    break;
            }
        }

        @Override
        public void grantPermission() {

        }

        @Override
        public void denyPermission() {

        }
    }

    /**
     * 显示图片
     */
    private void showImgsSaveAnswer() {

        if (imgFilePathList != null && imgFilePathList.size() <= 0) {
            subjective_answer_frame_one.setVisibility(View.GONE);
            subjective_answer_frame_two.setVisibility(View.GONE);
            subjective_answer_frame_three.setVisibility(View.GONE);
        }

        for (int i = 0; i < imgFilePathList.size(); i++) {
            switch (i) {
                case 0:
                    subjective_answer_frame_one.setVisibility(View.VISIBLE);
                    subjective_answer_frame_two.setVisibility(View.GONE);
                    subjective_answer_frame_three.setVisibility(View.GONE);
                    subjective_img_one.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                    break;
                case 1:
                    subjective_answer_frame_two.setVisibility(View.VISIBLE);
                    subjective_answer_frame_three.setVisibility(View.GONE);
                    subjective_img_two.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                    break;
                case 2:
                    subjective_answer_frame_three.setVisibility(View.VISIBLE);
                    subjective_img_three.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                    break;
                default:
                    QZXTools.popCommonToast(mContext, "imgFileList大小超过3个了", false);
                    break;
            }
        }

        //-------------------------答案保存，依据作业题目id
        LocalTextAnswersBean localTextAnswersBean = new LocalTextAnswersBean();
        localTextAnswersBean.setHomeworkId(questionInfo.getHomeworkId());
        localTextAnswersBean.setQuestionId(questionInfo.getId() + "");
        localTextAnswersBean.setUserId(UserUtils.getUserId());
        localTextAnswersBean.setQuestionType(questionInfo.getQuestionChannelType());
        localTextAnswersBean.setAnswerContent(subjective_input.getText().toString().trim());
        localTextAnswersBean.setImageList(imgFilePathList);
        QZXTools.logE("subjective Save localTextAnswersBean=" + localTextAnswersBean, null);
        //插入或者更新数据库
        MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
        //-------------------------答案保存，依据作业题目id
    }


    /**
     * 打开相机
     */
    //是不是拍照
    private boolean isOpenCamera;
    private void openCamera() {
        isOpenCamera = true;

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
//        cameraIntent.putExtra("extra_info", questionInfo.getId());
        ((Activity) mContext).startActivityForResult(cameraIntent, CODE_SYS_CAMERA);
    }


    /**
     * 图片查看器显示
     */
    private void showPhotoView(int curIndex) {
        Intent intent = new Intent(mContext, ImageLookActivity.class);
        intent.putStringArrayListExtra("imgResources", imgFilePathList);
        intent.putExtra("NeedComment", false);
        intent.putExtra("curImgIndex", curIndex);
        mContext.startActivity(intent);
        //转场动画透明度是非透明的，不符合要求
//        ActivityOptionsCompat options = ActivityOptionsCompat.
//                makeSceneTransitionAnimation((Activity) mContext, this, "");
//        mContext.startActivity(intent, options.toBundle());
    }

    /**
     * 图片、文本的答案回显
     * <p>
     * notes:如果后台返回了，使用后台的结果；否则使用本地数据库保存的结果；
     * <p>
     * 正常的逻辑：未答题前的OwnList和ImgFile应该为null;学生作答之后就应该有值了;
     */
    public void showImgsAndContent(LocalTextAnswersBean localTextAnswersBean) {

        //塞入文本
        if (questionInfo.getOwnList() != null && questionInfo.getOwnList().size() > 0) {
            String textAnswer = questionInfo.getOwnList().get(0).getAnswerContent();
            subjective_input.setText(textAnswer);
            subjective_input.setSelection(textAnswer.length());
        } else {
            //回显文本答案
            if (localTextAnswersBean != null) {
                String textAnswer = localTextAnswersBean.getAnswerContent();
                subjective_input.setText(textAnswer);
                subjective_input.setSelection(textAnswer.length());
            }
        }

        //塞入图片
        if (questionInfo.getImgFile() != null && questionInfo.getImgFile().size() > 0) {
            imgFilePathList = (ArrayList<String>) questionInfo.getImgFile();
        } else {
            if (localTextAnswersBean != null) {
                imgFilePathList = (ArrayList<String>) localTextAnswersBean.getImageList();
            }
        }

        //防止数据库中的imgs为空
        if (imgFilePathList == null) {
            imgFilePathList = new ArrayList<>();
        }

        if (imgFilePathList != null && imgFilePathList.size() > 0) {
            for (int i = 0; i < imgFilePathList.size(); i++) {
                switch (i) {
                    case 0:
                        subjective_answer_frame_one.setVisibility(View.VISIBLE);
                        subjective_answer_frame_two.setVisibility(View.GONE);
                        subjective_answer_frame_three.setVisibility(View.GONE);
                        Glide.with(mContext).load(imgFilePathList.get(i)).into(subjective_img_one);
//                        subjective_img_one.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                        break;
                    case 1:
                        subjective_answer_frame_two.setVisibility(View.VISIBLE);
                        subjective_answer_frame_three.setVisibility(View.GONE);
                        Glide.with(mContext).load(imgFilePathList.get(i)).into(subjective_img_two);
//                        subjective_img_two.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                        break;
                    case 2:
                        subjective_answer_frame_three.setVisibility(View.VISIBLE);
                        Glide.with(mContext).load(imgFilePathList.get(i)).into(subjective_img_three);
//                        subjective_img_three.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                        break;
                    default:
                        QZXTools.popCommonToast(mContext, "imgFileList大小超过3个了", false);
                        break;
                }
            }
        }
    }


    /**
     * 添加订阅者   画板保存回调这里存粹保存整个画板位图，没有做其他处理，分辨率是平板分辨率，大小还可以(KB)
     */
    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
        QZXTools.logE("Bank fromBoardCallback ExtraInfoBean=" + extraInfoBean + ";id=" + questionInfo.getId(), null);
        //之所以判断是因为所有的其他SubjectiveToDoView也可以接受到这个反馈
        if (extraInfoBean.getQuestionId().equals(questionInfo.getId() + "")) {
            imgFilePathList.add(extraInfoBean.getFilePath());
            showImgsSaveAnswer();
        }
    }

    /**
     * 添加订阅者   相机保存回调，注意这里纯粹调用相机拍照存储原图，没有剪裁压缩比较大(MB)
     * <p>
     * 注意：需要压缩处理但是不考虑剪裁，因为呈现的大小问题
     * <p>
     * 问题：多个主观题连在一起的话拍照的这个list有问题
     */

    public void fromCameraCallback(String flag) {
        if (flag.equals("CAMERA_CALLBACK")) {
            QZXTools.logE("fromCameraCallback filePath=" + cameraFile.getAbsolutePath(), null);
            //之所以判断是因为所有的其他SubjectiveToDoView也可以接受到这个反馈
            if (NewKnowledgeQuestionView.subjQuestionId.equals(questionInfo.getId() + "")) {
                //质量压缩处理
//                File compressFile = compressImage(BitmapFactory.decodeFile(cameraFile.getAbsolutePath()));

                //比例尺寸压缩 notes:这个比质量压缩的要快，效果也很不错；800的时候大约325k,400的时候大约85k
                File compressFile = compressBitmapToFile(cameraFile.getAbsolutePath(),
                        mContext.getResources().getDimensionPixelSize(R.dimen.x800));

                imgFilePathList.add(compressFile.getAbsolutePath());
                showImgsSaveAnswer();
            }
        }
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


}
