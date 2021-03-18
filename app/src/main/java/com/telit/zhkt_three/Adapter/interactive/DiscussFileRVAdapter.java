package com.telit.zhkt_three.Adapter.interactive;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Fragment.Dialog.TBSDownloadDialog;
import com.telit.zhkt_three.JavaBean.PreView.RecordStatus;
import com.telit.zhkt_three.MediaTools.audio.AudioPlayActivity;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.SerializeUtil;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/28 15:48
 */
public class DiscussFileRVAdapter extends RecyclerView.Adapter<DiscussFileRVAdapter.DiscussFileViewHolder>
        implements ZBVPermission.PermPassResult, ValueCallback<String> {

    private Activity activity;
    private List<String> fileList;
    private String fileUrl;

    private static final String[] needPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    public DiscussFileRVAdapter(Activity activity, List<String> fileList) {
        this.activity = activity;
        this.fileList = fileList;
    }

    @NonNull
    @Override
    public DiscussFileViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        return new DiscussFileViewHolder(LayoutInflater.from(activity)
                .inflate(R.layout.adapter_item_discuss_file, viewGroup, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DiscussFileViewHolder discussFileViewHolder, int i) {

    }

    @Override
    public int getItemCount() {
        return fileList != null ? fileList.size() : 0;
    }

    @Override
    public void grantPermission() {
        QZXTools.logD("已授权SD读写权限");
        handlerTBSShow();
    }

    @Override
    public void denyPermission() {
        QZXTools.logD("未完全授权");
        Toast.makeText(activity, "因为您未授权，所以该操作这暂时不可用", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onReceiveValue(String s) {
        QZXTools.logE("DiscussFileAdapter onReceiveValue s=" + s, null);
    }

    public class DiscussFileViewHolder extends RecyclerView.ViewHolder {
        private TextView textView;
        private ImageView imageView;

        public DiscussFileViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.dicuss_file_name);
            imageView = itemView.findViewById(R.id.discuss_file_sign);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = fileList.get(getLayoutPosition());
                    String format = url.substring(url.lastIndexOf(".") + 1);

                    if (format.equals("mp4") || format.equals("avi")) {

                        Intent intent_video = new Intent(activity, VideoPlayerActivity.class);
                        intent_video.putExtra("VideoFilePath", url);
                        activity.startActivity(intent_video);

                    } else if (format.equals("mp3")) {

                        Intent intent = new Intent(activity, AudioPlayActivity.class);
                        intent.putExtra("AudioFilePath", url);
                        activity.startActivity(intent);

                    } else if (format.equals("jpg") || format.equals("png") || format.equals("gif")) {

                        Intent intent_img = new Intent(activity, ImageLookActivity.class);
                        ArrayList<String> imgFilePathList = new ArrayList<>();
                        imgFilePathList.add(url);
                        intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                        intent_img.putExtra("curImgIndex", 0);
                        activity.startActivity(intent_img);

                    } else {
                        ZBVPermission.getInstance().setPermPassResult(DiscussFileRVAdapter.this);
                        if (ZBVPermission.getInstance().hadPermissions(activity, needPermissions)) {
                            fileUrl = url;
                            handlerTBSShow();
                        } else {
                            ZBVPermission.getInstance().requestPermissions(activity, needPermissions);
                        }
                    }
                }
            });
        }
    }

    private TBSDownloadDialog tbsDownloadDialog;
    private ArrayList<RecordStatus> recordStatuses = null;

    private void handlerTBSShow() {
        if (TextUtils.isEmpty(fileUrl)) {
            return;
        }

        String preViewUrl = fileUrl;

        recordStatuses = null;
        //先判断是否存有记录
        String saveRecordPath = QZXTools.getExternalStorageForFiles(activity, null) + File.separator + "discuss/preRecord.txt";
        File file = new File(saveRecordPath);
        if (file.exists()) {
            recordStatuses = (ArrayList<RecordStatus>)
                    SerializeUtil.deSerializeFromFile(file.getAbsolutePath());
            for (RecordStatus recordStatus : recordStatuses) {
                if (recordStatus.getPreviewUrl().equals(preViewUrl)) {
                    //不需要下载
                    //tbs打开
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("local", "true");
                    params.put("allowAutoDestory", "true");
                    JSONObject Object = new JSONObject();
                    try {
                        Object.put("pkgName", activity.getApplicationContext().getPackageName());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    params.put("menuData", Object.toString());
                    //目前去掉了腾讯的x5 用wps todo  这个要延期处理
                    /*QbSdk.getMiniQBVersion(activity);
                    int ret = QbSdk.openFileReader(activity, recordStatus.getSavedFilePath(),
                            params, DiscussFileRVAdapter.this);*/
                    return;
                }
            }
        }

        if (tbsDownloadDialog == null) {
            tbsDownloadDialog = new TBSDownloadDialog();
        }
        tbsDownloadDialog.show(((FragmentActivity) activity).getSupportFragmentManager(), TBSDownloadDialog.class.getSimpleName());

        OkHttp3_0Utils.getInstance().downloadSingleFileForOnce(preViewUrl,
                "discuss", new OkHttp3_0Utils.DownloadCallback() {
                    @Override
                    public void downloadProcess(int value) {
                        if (tbsDownloadDialog != null) {
                            tbsDownloadDialog.download(value);
                        }
                    }

                    @Override
                    public void downloadComplete(String filePath) {
                        if (tbsDownloadDialog != null) {
                            tbsDownloadDialog.dismissAllowingStateLoss();
                            tbsDownloadDialog = null;
                        }
                        QZXTools.popToast(activity, "下载成功地址为：" + filePath, false);

                        //保存到记录中
                        if (!file.exists()) {
                            try {
                                boolean success = file.createNewFile();
                                if (success) {
                                    RecordStatus recordStatus = new RecordStatus();
                                    recordStatus.setSavedFilePath(filePath);
                                    recordStatus.setPreviewUrl(preViewUrl);
                                    ArrayList<RecordStatus> recordStatuses = new ArrayList<>();
                                    recordStatuses.add(recordStatus);

                                    //序列化到文件中
                                    SerializeUtil.toSerializeToFile(recordStatuses, file.getAbsolutePath());
                                }
                            } catch (IOException e) {
                                e.printStackTrace();
                                QZXTools.logE("createNewFile Failed", null);
                            }
                        } else {
                            RecordStatus recordStatus = new RecordStatus();
                            recordStatus.setSavedFilePath(filePath);
                            recordStatus.setPreviewUrl(preViewUrl);
                            recordStatuses.add(recordStatus);

                            //序列化到文件中
                            SerializeUtil.toSerializeToFile(recordStatuses, file.getAbsolutePath());

                        }

                        //更新主界面的缓存
                        EventBus.getDefault().post("update_cache", Constant.UPDATE_CACHE_VIEW);

                        //tbs打开
                        HashMap<String, String> params = new HashMap<String, String>();
                        params.put("local", "true");
                        params.put("allowAutoDestory", "true");
                        JSONObject Object = new JSONObject();
                        try {
                            Object.put("pkgName", activity.getApplicationContext().getPackageName());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        params.put("menuData", Object.toString());
                        //目前去掉了腾讯的x5 用wps todo
                  /*      QbSdk.getMiniQBVersion(activity);
                        int ret = QbSdk.openFileReader(activity, filePath, params, DiscussFileRVAdapter.this);*/

                    }

                    @Override
                    public void downloadFailure() {
                        QZXTools.popToast(activity, "下载失败", false);
                        if (tbsDownloadDialog != null) {
                            tbsDownloadDialog.dismissAllowingStateLoss();
                            tbsDownloadDialog = null;
                        }
                    }
                });
    }

}
