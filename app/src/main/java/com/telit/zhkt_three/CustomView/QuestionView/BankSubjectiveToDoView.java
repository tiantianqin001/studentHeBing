package com.telit.zhkt_three.CustomView.QuestionView;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.content.FileProvider;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Activity.HomeWork.WhiteBoardActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * author: qzx
 * Date: 2019/5/25 9:38
 * <p>
 * 主观题
 * <p>
 * 图片保存的地址：/storage/emulated/0/Android/data/com.telit.smartclass.desktop/files/Pictures/IMG_XXXX.jpg
 * <p>
 * 注意：这里直接调用相机拍照，图片大小很大
 * 画板的分辨率和平板一致，图片大小不是很大
 */
public class BankSubjectiveToDoView extends RelativeLayout implements View.OnClickListener, ZBVPermission.PermPassResult {

    private RelativeLayout subjective_imgs_layout;

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

    //图片文件
    private ArrayList<String> imgFilePathList;

    private Context mContext;

    private QuestionBank questionInfo;
    private TextView tv_teacher_answer_content;
    private String status;


    /**
     * 传题型信息，用于保存答案
     */
    public void setQuestionInfo(QuestionBank questionInfo) {
        this.questionInfo = questionInfo;
    }

    private static final String[] needPermissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public BankSubjectiveToDoView(Context context) {
        this(context, null);
    }

    public BankSubjectiveToDoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BankSubjectiveToDoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        QZXTools.logE("SubjectiveToDoView", null);

        mContext = context;

        View view = LayoutInflater.from(context).inflate(R.layout.subjective_option_complete_layout, this, true);

        subjective_imgs_layout = view.findViewById(R.id.subjective_imgs_layout);

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

        subjective_answer_frame_one.setVisibility(GONE);
        subjective_answer_frame_two.setVisibility(GONE);
        subjective_answer_frame_three.setVisibility(GONE);

        subjective_camera.setOnClickListener(this);
        subjective_board.setOnClickListener(this);

        subjective_img_one.setOnClickListener(this);
        subjective_img_two.setOnClickListener(this);
        subjective_img_three.setOnClickListener(this);

        subjective_del_one.setOnClickListener(this);
        subjective_del_two.setOnClickListener(this);
        subjective_del_three.setOnClickListener(this);

        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "PingFang-SimpleBold.ttf");
        subjective_camera.setTypeface(typeface);
        subjective_board.setTypeface(typeface);
        subjective_input.setTypeface(typeface);



        tv_teacher_answer_content = findViewById(R.id.tv_teacher_answer_content);

    }

    private boolean isClickCamera = false;

    /**
     * 注意按delete删除图片并没有删除本地的文件哦
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.subjective_camera:
                if (imgFilePathList.size() >= 3) {
                    QZXTools.popCommonToast(getContext(), "图片答案不得超过三张", false);
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
                    QZXTools.popCommonToast(getContext(), "图片答案不得超过三张", false);
                    return;
                }

                isClickCamera = false;

                ZBVPermission.getInstance().setPermPassResult(this);
                if (!ZBVPermission.getInstance().hadPermissions((Activity) mContext, needPermissions)) {
                    ZBVPermission.getInstance().requestPermissions((Activity) mContext, needPermissions);
                } else {
                    Intent intent = new Intent(getContext(), WhiteBoardActivity.class);
                    intent.putExtra("extra_info", questionInfo.getId() + "");
                    getContext().startActivity(intent);
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

    public static final int CODE_SYS_CAMERA = 1;//系统相机RequestCode

    private File cameraFile;

    //是不是拍照
    private boolean isOpenCamera;

    /**
     * 打开相机
     */
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
     * 添加订阅者   画板保存回调这里存粹保存整个画板位图，没有做其他处理，分辨率是平板分辨率，大小还可以(KB)
     */
    @Subscriber(tag = Constant.Subjective_Board_Callback, mode = ThreadMode.MAIN)
    public void fromBoardCallback(ExtraInfoBean extraInfoBean) {
        QZXTools.logE("Bank fromBoardCallback ExtraInfoBean=" + extraInfoBean + ";id=" + questionInfo.getId(), null);
        //之所以判断是因为所有的其他SubjectiveToDoView也可以接受到这个反馈
        if (extraInfoBean.getQuestionId().equals(questionInfo.getId() + "")) {
            imgFilePathList.add(extraInfoBean.getFilePath());
            showImgsSaveAnswer();

            if (status.equals(Constant.Retry_Status)){
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setBoolean("isClearComData",false);

            }
        }
    }

    /**
     * 添加订阅者   相机保存回调，注意这里纯粹调用相机拍照存储原图，没有剪裁压缩比较大(MB)
     * <p>
     * 注意：需要压缩处理但是不考虑剪裁，因为呈现的大小问题
     * <p>
     * 问题：多个主观题连在一起的话拍照的这个list有问题
     */
    @Subscriber(tag = Constant.Subjective_Camera_Callback, mode = ThreadMode.MAIN)
    public void fromCameraCallback(String flag) {
        if (flag.equals("CAMERA_CALLBACK")) {
            QZXTools.logE("fromCameraCallback filePath=" + cameraFile.getAbsolutePath(), null);
            //之所以判断是因为所有的其他SubjectiveToDoView也可以接受到这个反馈
            if (NewKnowledgeQuestionView.subjQuestionId.equals(questionInfo.getId() + "")) {
                //质量压缩处理
//                File compressFile = compressImage(BitmapFactory.decodeFile(cameraFile.getAbsolutePath()));

                //比例尺寸压缩 notes:这个比质量压缩的要快，效果也很不错；800的时候大约325k,400的时候大约85k
                File compressFile = compressBitmapToFile(cameraFile.getAbsolutePath(),
                        getResources().getDimensionPixelSize(R.dimen.x800));

                imgFilePathList.add(compressFile.getAbsolutePath());
                showImgsSaveAnswer();

                if (status.equals(Constant.Retry_Status)){
                    SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setBoolean("isClearComData",false);

                }
            }
        }
    }


    //---------------------------------------------

    /**
     * 按尺寸压缩图片
     *
     * @param srcPath  图片路径
     * @param desWidth 压缩的图片宽度
     * @return Bitmap 对象
     */

    public static Bitmap compressImageFromFile(String srcPath, float desWidth) {
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
        return bitmap;
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


    /**
     * 压缩图片（质量压缩）
     *
     * @param image
     */

    public File compressImage(Bitmap image) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);//质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        int options = 100;

        while (baos.toByteArray().length / 1024 > 100) {  //循环判断如果压缩后图片是否大于100kb,大于继续压缩
            baos.reset();//重置baos即清空baos
            options -= 10;//每次都减少10
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);//这里压缩options%，把压缩后的数据存放到baos中
            long length = baos.toByteArray().length;
        }
//        long length = baos.toByteArray().length;
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());//把压缩后的数据baos存放到ByteArrayInputStream中
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);//把ByteArrayInputStream数据生成图片


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

    //---------------------------------------------


    /**
     * 图片、文本的答案回显
     * <p>
     * notes:如果后台返回了，使用后台的结果；否则使用本地数据库保存的结果；
     * <p>
     * 正常的逻辑：未答题前的OwnList和ImgFile应该为null;学生作答之后就应该有值了;
     */
    public void showImgsAndContent(LocalTextAnswersBean localTextAnswersBean,
                                   String status) {
        this.status = status;

        //塞入文本
        if (questionInfo.getOwnList() != null && questionInfo.getOwnList().size() > 0) {
            String textAnswer = questionInfo.getOwnList().get(0).getAnswerContent();
            if (status == Constant.Save_Status){

                subjective_input.setText(textAnswer);
            }else {

                subjective_input.setText("我的答案: "+textAnswer);
            }
            subjective_input.setSelection(textAnswer.length());
        } else {
            //回显文本答案
            if (localTextAnswersBean != null&& !TextUtils.isEmpty(localTextAnswersBean.getAnswerContent())) {
                String textAnswer = localTextAnswersBean.getAnswerContent();
                subjective_input.setText(textAnswer);
                subjective_input.setSelection(textAnswer.length());
            }
        }
        //当前已经批阅

        if (status.equals(Constant.Review_Status)){
            imgFilePathList = (ArrayList<String>) localTextAnswersBean.getImageList();

        }else {
            //塞入图片
            if (questionInfo.getImgFile() != null && questionInfo.getImgFile().size() > 0) {
                imgFilePathList = (ArrayList<String>) questionInfo.getImgFile();
            } else {
                if (localTextAnswersBean != null) {
                    imgFilePathList = (ArrayList<String>) localTextAnswersBean.getImageList();
                }
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
                        subjective_answer_frame_one.setVisibility(VISIBLE);
                        subjective_answer_frame_two.setVisibility(GONE);
                        subjective_answer_frame_three.setVisibility(GONE);
                        Glide.with(getContext()).load(imgFilePathList.get(i)).into(subjective_img_one);
//                        subjective_img_one.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                        break;
                    case 1:
                        subjective_answer_frame_two.setVisibility(VISIBLE);
                        subjective_answer_frame_three.setVisibility(GONE);
                        Glide.with(getContext()).load(imgFilePathList.get(i)).into(subjective_img_two);
//                        subjective_img_two.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                        break;
                    case 2:
                        subjective_answer_frame_three.setVisibility(VISIBLE);
                        Glide.with(getContext()).load(imgFilePathList.get(i)).into(subjective_img_three);
//                        subjective_img_three.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                        break;
                    default:
                        QZXTools.popCommonToast(getContext(), "imgFileList大小超过3个了", false);
                        break;
                }
            }
        }


        //显示批注的答案和老师的答
        if (status.equals(Constant.Commit_Status)){
            //   Glide.with(getContext()).load(UrlUtils.ImgBaseUrl+questionInfo.getAnswer()).into(iv_teacher_answer_content);
            if (questionInfo.getOwnList().size()>0){
                if (!TextUtils.isEmpty(questionInfo.getOwnList().get(0).getComment())){

                    tv_teacher_answer_content.setText("老师评语: "+questionInfo.getOwnList().get(0).getComment());
                }
            }
        }
        ///已经批注
        if (status.equals(Constant.Review_Status)){
            if (questionInfo.getOwnList().size()>0){

                if (!TextUtils.isEmpty(questionInfo.getOwnList().get(0).getComment())){

                    tv_teacher_answer_content.setText("老师评语: "+questionInfo.getOwnList().get(0).getComment());
                }
            }
        }
        //打回重做
        if (status.equals(Constant.Retry_Status)&& questionInfo.getOwnList().size()==0){
            subjective_answer_tool_layout.setVisibility(VISIBLE);
            subjective_input.setFocusableInTouchMode(true);
        }
    }

    /**
     * 显示图片
     */
    private void showImgsSaveAnswer() {

        if (imgFilePathList != null && imgFilePathList.size() <= 0) {
            subjective_answer_frame_one.setVisibility(GONE);
            subjective_answer_frame_two.setVisibility(GONE);
            subjective_answer_frame_three.setVisibility(GONE);
        }

        for (int i = 0; i < imgFilePathList.size(); i++) {
            switch (i) {
                case 0:
                    subjective_answer_frame_one.setVisibility(VISIBLE);
                    subjective_answer_frame_two.setVisibility(GONE);
                    subjective_answer_frame_three.setVisibility(GONE);
                    subjective_img_one.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                    break;
                case 1:
                    subjective_answer_frame_two.setVisibility(VISIBLE);
                    subjective_answer_frame_three.setVisibility(GONE);
                    subjective_img_two.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                    break;
                case 2:
                    subjective_answer_frame_three.setVisibility(VISIBLE);
                    subjective_img_three.setImageBitmap(BitmapFactory.decodeFile(imgFilePathList.get(i)));
                    break;
                default:
                    QZXTools.popCommonToast(getContext(), "imgFileList大小超过3个了", false);
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
     * 主观题提交后或者批阅后隐藏作答工具并且EditText不能编辑
     */
    public void hideAnswerTools(boolean needHideTool) {
        if (needHideTool) {
            //隐藏作答工具
            subjective_answer_tool_layout.setVisibility(GONE);
            //EditText不能编辑
            subjective_input.setFocusable(false);
            //不显示垃圾桶
            subjective_del_layout_one.setVisibility(GONE);
            subjective_del_layout_two.setVisibility(GONE);
            subjective_del_layout_three.setVisibility(GONE);
        } else {
            //添加文本输入改变监听
            subjective_input.addTextChangedListener(new TextWatcher() {
                //输入表情前的光标位置
                private int cursorPos;
                //输入表情前EditText中的文本
                private String inputAfterText;
                //是否重置了EditText的内容
                private boolean resetText;

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
                                    QZXTools.logE("inputAfterText:"+inputAfterText,null);
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
                    localTextAnswersBean.setHomeworkId(questionInfo.getHomeworkId());
                    localTextAnswersBean.setQuestionId(questionInfo.getId() + "");
                    localTextAnswersBean.setUserId(UserUtils.getUserId());
                    localTextAnswersBean.setAnswer(questionInfo.getAnswer());
                    localTextAnswersBean.setQuestionType(questionInfo.getQuestionChannelType());
                    localTextAnswersBean.setAnswerContent(subjective_input.getText().toString());
                    localTextAnswersBean.setImageList(imgFilePathList);
//                                QZXTools.logE("Save localTextAnswersBean=" + localTextAnswersBean, null);
                    //插入或者更新数据库
                    MyApplication.getInstance().getDaoSession().getLocalTextAnswersBeanDao().insertOrReplace(localTextAnswersBean);
                    //-------------------------答案保存，依据作业题目id
                }
            });
        }
    }

    @Override
    protected void onAttachedToWindow() {
//        QZXTools.logE("onAttachedToWindow", null);
        super.onAttachedToWindow();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onDetachedFromWindow() {
//        QZXTools.logE("onDetachedFromWindow", null);
        super.onDetachedFromWindow();
        EventBus.getDefault().unregister(this);
    }

    /**
     * 这个没用，原本想通过焦点改变来注册EventBus以及相关回调处理的，因为焦点没有改变所以不会调用
     */
    @Override
    protected void onFocusChanged(boolean gainFocus, int direction, @Nullable Rect previouslyFocusedRect) {
//        QZXTools.logE("onFocusChanged", null);
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
    }

    //------------------------------------------------------------------------------------------------
    @Override
    public void grantPermission() {
        QZXTools.logD("已授权直接打开相机");
        if (isClickCamera) {
            openCamera();
        } else {
            getContext().startActivity(new Intent(getContext(), WhiteBoardActivity.class));
        }
    }

    @Override
    public void denyPermission() {
        QZXTools.logD("未完全授权");
        Toast.makeText(mContext, "因为您未授权，所以该操作这暂时不可用", Toast.LENGTH_SHORT).show();
    }
}
