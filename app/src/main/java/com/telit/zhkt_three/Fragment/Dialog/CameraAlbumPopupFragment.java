package com.telit.zhkt_three.Fragment.Dialog;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.telit.zhkt_three.MediaTools.CropActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.ZBVPermission;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/4/9 8:59
 * <p>
 * 之前有相册选取图片、拍照图片
 * <p>
 * 新增录像
 * <p>
 * 注意notes:
 * getActivity()/getParentFragment的不同
 * getActivity只有Activity中的才能收到
 * getParentFragment只有ParentFragment中的才是正确的requestCode
 * 而getTargetFragment极有可能为null
 * <p>
 * todo 存在泄露在PersonInfoActivity中,因为Activity持有CameraAlbumPopuopFragment引用 ---解决
 * todo handlerThread中message泄露
 * todo 相机拍照和录像的onActivityResult的Intent data以及Uri的处理【7.0后文件的Provider】
 */
public class CameraAlbumPopupFragment extends DialogFragment implements View.OnClickListener, ZBVPermission.PermPassResult {

    private Unbinder unbinder;
    @BindView(R.id.tv_camera)
    TextView tv_camera;
    @BindView(R.id.tv_camera_video)
    TextView tv_camera_video;
    @BindView(R.id.tv_album)
    TextView tv_album;
    @BindView(R.id.tv_dismiss)
    TextView tv_dismiss;

    public static CameraAlbumPopupFragment newInstance(){
        CameraAlbumPopupFragment fragmentOne = new CameraAlbumPopupFragment();
        return fragmentOne;
    }

    private static final String IS_ALBUM = "album_img";
    private static final String IS_CAMERA_IMG = "camera_img";
    private static final String IS_CAMERA_VEDIO = "camera_video";

    //默认拍照选取
    private String selectedType = IS_CAMERA_IMG;

    private static final String[] needPermissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //STYLE_NO_FRAME表示onCreateView所绘制的View就是Dialog
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.camera_album_layout, container, false);

        unbinder = ButterKnife.bind(this, view);

        tv_camera.setOnClickListener(this);
        tv_camera_video.setOnClickListener(this);
        tv_album.setOnClickListener(this);
        tv_dismiss.setOnClickListener(this);

        if (showCameraImg) {
            tv_camera.setVisibility(View.VISIBLE);
        } else {
            tv_camera.setVisibility(View.GONE);
        }

        if (showCameraVideo) {
            tv_camera_video.setVisibility(View.VISIBLE);
        } else {
            tv_camera_video.setVisibility(View.GONE);
        }

        if (showAlbum) {
            tv_album.setVisibility(View.VISIBLE);
        } else {
            tv_album.setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setGravity(Gravity.TOP);
//        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        if (unbinder != null) {
            unbinder.unbind();
        }

        ZBVPermission.getInstance().recyclerAll();
    }

    private boolean showCameraImg = true;
    private boolean showCameraVideo = false;
    private boolean showAlbum = true;

    /**
     * 界面显示设置,记住在onCreateView中处理界面显示，因为new的对象View还没有生成
     */
    public void showSetting(boolean showCameraImg, boolean showCameraVideo, boolean showAlbum) {
        this.showCameraImg = showCameraImg;
        this.showCameraVideo = showCameraVideo;
        this.showAlbum = showAlbum;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_camera:
                ZBVPermission.getInstance().setPermPassResult(this);

                if (!ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                    ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                } else {
                    //直接打开相机
                    QZXTools.logD("已拥有权限直接打开相机");
                    openCamera();
                    dismiss();
                }

                selectedType = IS_CAMERA_IMG;

                break;
            case R.id.tv_camera_video:
                ZBVPermission.getInstance().setPermPassResult(this);

                if (!ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                    ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                } else {
                    //直接打开相机
                    QZXTools.logD("已拥有权限直接打开相机");
                    openCameraForVideo();
                    dismiss();
                }

                selectedType = IS_CAMERA_VEDIO;

                break;
            case R.id.tv_album:
                ZBVPermission.getInstance().setPermPassResult(this);

                if (!ZBVPermission.getInstance().hadPermissions(getActivity(), needPermissions)) {
                    ZBVPermission.getInstance().requestPermissions(getActivity(), needPermissions);
                } else {
                    //直接打开相册
                    QZXTools.logD("已拥有权限直接打开相机");
                    openSysAlbum();
                    dismiss();
                }

                selectedType = IS_ALBUM;

                break;
            case R.id.tv_dismiss:
                dismiss();
                break;
        }

    }

    //------------------------------------------------------------------------------------------

    public static final int CODE_SYS_CAMERA = 0x1007;//系统相机RequestCode

    /**
     * 静态公共参数，是否妥当？
     */
    public static Uri cameraUri;

    /**
     * 打开相机拍照
     * /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/PicturesVIDEO_yyyMMdd_HHmmss.mp4
     */
    private void openCamera() {
        String fileDir = QZXTools.getExternalStorageForFiles(MyApplication.getInstance(), Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".jpg");
        File cameraFile = new File(fileDir, stringBuilder.toString());
        cameraUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()
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
        getActivity().startActivityForResult(cameraIntent, CODE_SYS_CAMERA);
    }

    public static final int CODE_SYS_CAMERA_VIDEO = 0x1010;//系统相机RequestCode

    /**
     * 打开相机录像
     * /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/VIDEO_yyyMMdd_HHmmss.mp4
     */
    private void openCameraForVideo() {
        String fileDir = QZXTools.getExternalStorageForFiles(getActivity(), null);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("VIDEO_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".mp4");
        File cameraFile = new File(fileDir, stringBuilder.toString());
        cameraUri = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraUri = FileProvider.getUriForFile(getActivity(), getActivity().getPackageName()
                    + ".fileprovider", cameraFile);
        } else {
            cameraUri = Uri.fromFile(cameraFile);
        }

        Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION); //添加这一句表示对目标应用临时授权该Uri所代表的文件
        }
        //设置拍照保存的路径，需要特别注意的是在onActivityResult中获取的Intent为空
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, cameraUri);
        // 录制视频最大时长15s
        cameraIntent.putExtra(MediaStore.EXTRA_DURATION_LIMIT, 15);
        getActivity().startActivityForResult(cameraIntent, CODE_SYS_CAMERA_VIDEO);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        QZXTools.logE("CameraAlbumPopupFragment requestCode=" + requestCode + ";resultCode=" + requestCode, null);
    }

    public static final int CODE_SYS_ALBUM = 0x1008;//系统相册RequestCode

    /**
     * 打开系统相册
     */
    private void openSysAlbum() {
        Intent albumIntent = new Intent(Intent.ACTION_PICK);
        albumIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        getActivity().startActivityForResult(albumIntent, CODE_SYS_ALBUM);

        //这种选取图片可以参见：https://blog.csdn.net/qq_38228254/article/details/79623618
//        Intent albumIntent = new Intent(Intent.ACTION_GET_CONTENT);
//        albumIntent.setType("image/*");
//        getActivity().startActivityForResult(albumIntent, CODE_SYS_ALBUM);
    }

    public static final int CODE_SYS_CROP = 0x1009;//系统裁剪RequestCode

    /**
     * 裁剪
     *
     * @param sourceUri 表示图片源的Uri（拍照或者相册）
     * @param outputUri 表示剪裁保存的Uri
     */
    public static void cropPhoto(Activity activity, Uri sourceUri, Uri outputUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        //安卓7.0的临时授权，也可以不需要
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        }
        //传入要裁剪的Uri以及类型
        intent.setDataAndType(sourceUri, "image/*");
        //发送裁剪信号，“true”表示启用裁剪
        intent.putExtra("crop", "true");
        //X方向比例
        intent.putExtra("aspectX", 0);
        //Y方向比例
        intent.putExtra("aspectY", 0);
        //裁剪区的宽
        intent.putExtra("outputX", 300);
        //裁剪区的高
        intent.putExtra("outputY", 300);
        //是否保留比例
        intent.putExtra("scale", true);
        //是否将裁剪数据保留在Bitmap中返回
        intent.putExtra("return-data", false);
        //裁剪数据输出位置
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outputUri);
        //设置裁剪后的图片输出格式
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        //取消人脸识别
        intent.putExtra("noFaceDetection", true);
        activity.startActivityForResult(intent, CODE_SYS_CROP);
    }

    public static final int CODE_CUSTOM_CROP = 0x1011;//自定义裁剪RequestCode

    /**
     * 自定义裁剪界面
     *
     * @param originalUri 源Uri
     * @param savedUri    保存的Uri
     */
    public static void cropPhotoTwo(Activity activity, Uri originalUri, Uri savedUri) {
        Intent intent = new Intent(activity, CropActivity.class);
        intent.setDataAndType(originalUri, "image/*");
        intent.putExtra("save_path", savedUri);
        activity.startActivityForResult(intent, CODE_SYS_CROP);
    }

    //---------------------------------------------------------------------------------------------
    @Override
    public void grantPermission() {
        QZXTools.logD("已授权直接打开相机");
        if (IS_ALBUM.equals(selectedType)) {
            openSysAlbum();
        } else if (IS_CAMERA_IMG.equals(selectedType)) {
            openCamera();
        } else if (IS_CAMERA_VEDIO.equals(selectedType)) {
            openCameraForVideo();
        }
        dismiss();
    }

    @Override
    public void denyPermission() {
        QZXTools.logD("未完全授权");
        Toast.makeText(getActivity(), "因为您未授权，所以该操作这暂时不可用", Toast.LENGTH_SHORT).show();
    }

}
