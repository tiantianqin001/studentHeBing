package com.telit.zhkt_three.Adapter.AutoLearning;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadTask;
import com.telit.zhkt_three.Activity.AutonomousLearning.ItemBankBookActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.Download.DownloadProgressBar;
import com.telit.zhkt_three.CustomView.RoundCornerImageView;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.JavaBean.Resource.LocalResourceRecord;
import com.telit.zhkt_three.MediaTools.audio.AudioPlayActivity;
import com.telit.zhkt_three.MediaTools.ebook.FlipEBookResourceActivity;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.MediaTools.video.VideoPlayerActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.LogDownloadListener;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.ViewUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.greendao.LocalResourceRecordDao;
import com.telit.zhkt_three.listener.autolearning.NormalResourceDownloadListener;
import com.telit.zhkt_three.listener.autolearning.TeachingMaterialDownloadListener;
import com.zbv.meeting.util.SharedPreferenceUtil;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;

/**
 * author: qzx
 * Date: 2019/6/10 8:47
 * <p>
 * 暂断点续传不支持
 * todo 下载封装有些疑问待解决
 */
public class AutoLearningAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private List<DownloadTask> mDatas;

    private static final int TeachingMaterial = 1;
    private static final int Other = 2;

    private String flag;

    public AutoLearningAdapter(Context context, List<DownloadTask> list, String flag) {
        mContext = context;
        mDatas = list;
        this.flag = flag;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        /**
         * 注意这里的int是使用getItemViewType方法计算后的type值了
         * */
        if (viewType == TeachingMaterial) {
            return new RVAutoLearningTeachingMaterialViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_teaching_material_layout, viewGroup, false));
        } else {
            return new RVAutoLearningNormalResourceViewHolder(LayoutInflater.from(mContext)
                    .inflate(R.layout.item_homework_audio_pic_video_layout, viewGroup, false));
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof RVAutoLearningNormalResourceViewHolder) {
            RVAutoLearningNormalResourceViewHolder holder = (RVAutoLearningNormalResourceViewHolder) viewHolder;
            DownloadTask task = mDatas.get(i);
            String tag = createTag(task);
            task.register(new NormalResourceDownloadListener(tag, holder)).register(new LogDownloadListener());
            holder.setTag(tag);
            holder.setTask(task);
            holder.bind();
            holder.refresh(task.progress);
        } else if (viewHolder instanceof RVAutoLearningTeachingMaterialViewHolder) {
            RVAutoLearningTeachingMaterialViewHolder holder =
                    (RVAutoLearningTeachingMaterialViewHolder) viewHolder;
            DownloadTask task = mDatas.get(i);
            String tag = createTag(task);
            task.register(new TeachingMaterialDownloadListener(tag, holder)).register(new LogDownloadListener());
            holder.setTag(tag);
            holder.setTask(task);
            holder.bind();
            holder.refresh(task.progress);
        }
    }

    private int type;

    private String createTag(DownloadTask task) {
        return type + "_" + task.progress.tag;
    }

    /**
     * 处理视频图片音频
     *
     * @param rvAutoLearningNormalResourceViewHolder
     * @param fillResource
     */
    private void handleNormalResource(RVAutoLearningNormalResourceViewHolder rvAutoLearningNormalResourceViewHolder, FillResource fillResource,Progress progress,DownloadTask task) {
        /**
         * 微课：3
         * 音频：2
         * 图片：1
         * 电子课本：1010
         */
        if (fillResource.getType().equals("3")) {
            rvAutoLearningNormalResourceViewHolder.rv_item_main_content.setBackgroundResource(R.mipmap.image_video_default);

            rvAutoLearningNormalResourceViewHolder.rv_item_img_video.setVisibility(View.VISIBLE);
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_type.setVisibility(View.GONE);

            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_video_bg);
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.video_text_color));
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setText("视频");
        } else if (fillResource.getType().equals("2")) {
            rvAutoLearningNormalResourceViewHolder.rv_item_main_content.setBackgroundResource(R.mipmap.audio_bg);

            rvAutoLearningNormalResourceViewHolder.rv_item_img_video.setVisibility(View.GONE);
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_type.setVisibility(View.VISIBLE);

            rvAutoLearningNormalResourceViewHolder.rv_item_tv_type.setText("音频");

            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_audio_bg);
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.audio_text_color));
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setText("音频");
        } else if (fillResource.getType().equals("1")) {
            rvAutoLearningNormalResourceViewHolder.rv_item_main_content.setBackgroundResource(R.mipmap.pic_bg);

            rvAutoLearningNormalResourceViewHolder.rv_item_img_video.setVisibility(View.GONE);
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_type.setVisibility(View.VISIBLE);

            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setBackgroundResource(R.drawable.shape_round_corner_pic_bg);
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setTextColor(mContext.getResources().getColor(R.color.picture_text_color));
            rvAutoLearningNormalResourceViewHolder.rv_item_tv_type.setText("图片");

            rvAutoLearningNormalResourceViewHolder.rv_item_tv_colorType.setText("图片");
        }

        //之前的是素材提供的教师以及提供时间，现在改成年级和出版社
        rvAutoLearningNormalResourceViewHolder.rv_item_tv_teacher_name.setText(fillResource.getPressname());
        rvAutoLearningNormalResourceViewHolder.rv_item_tv_date.setText(fillResource.getGradename().concat(fillResource.getTermname()));

        String topicTitle = fillResource.getTitle();

        //如果是正则应该是\\|，但这里是字符串不是正则
        int indexTwo = topicTitle.indexOf("-");
        int indexOne = topicTitle.indexOf("｜");
        if (indexTwo != -1 && indexOne != -1) {
            topicTitle = topicTitle.substring(indexTwo + 1, indexOne - 1);
        }
        rvAutoLearningNormalResourceViewHolder.rv_item_tv_topic.setText(topicTitle);
        QZXTools.logE("flag: " + flag, null);
        rvAutoLearningNormalResourceViewHolder.rv_item_tv_subType.setText(fillResource.getSubjectName());

        //下载状态
        LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();

        QZXTools.logE("fillResource=" + new Gson().toJson(fillResource), null);

        if (fillResource.getId() != null && fillResource.getType() != null) {
            LocalResourceRecord localResourceRecord = localResourceRecordDao.queryBuilder().where
                    (LocalResourceRecordDao.Properties.ResourceId.eq(fillResource.getId()),
                            LocalResourceRecordDao.Properties.ResourceType.eq(fillResource.getType())).unique();

            if (localResourceRecord != null) {
                QZXTools.logE("localResourceRecord11=" + localResourceRecord + ";fillResource=" + new Gson().toJson(fillResource), null);
                rvAutoLearningNormalResourceViewHolder.rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_FINISH);
                fillResource.setFilePath(localResourceRecord.getResourceFilePath());

                progress.status = Progress.FINISH;
            } else {
                switch (progress.status) {
                    case Progress.NONE:
                        QZXTools.logE("NONE", null);
                        rvAutoLearningNormalResourceViewHolder.rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_READY);
                        break;
                    case Progress.PAUSE:
                    case Progress.ERROR:
                        task.restart();
                        break;
                    case Progress.WAITING:
                        rvAutoLearningNormalResourceViewHolder.rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_WATING);
                        QZXTools.logE("WAITING", null);
                        break;
                    case Progress.FINISH:
                        QZXTools.logE("FINISH", null);
                        rvAutoLearningNormalResourceViewHolder.rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_FINISH);
                        break;
                    case Progress.LOADING:
                        QZXTools.logE("LOADING", null);
                        rvAutoLearningNormalResourceViewHolder.rv_item_download_tags.setProgress(progress.currentSize/(progress.totalSize/1f));
                        break;
                }
            }
        }
    }

    /**
     * 处理教材阅读
     *
     * @param rvAutoLearningTeachingMaterialViewHolder
     * @param fillResource
     */
    private void handleTeachingMaterial(RVAutoLearningTeachingMaterialViewHolder rvAutoLearningTeachingMaterialViewHolder, FillResource fillResource,Progress progress,DownloadTask task) {
        //如果是题库的话就不需要下载展示
        if (fillResource.isItemBank()) {
            rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setVisibility(View.GONE);
        } else {
            rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setVisibility(View.VISIBLE);

            LocalResourceRecordDao localResourceRecordDao = MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao();
            LocalResourceRecord localResourceRecord = localResourceRecordDao.queryBuilder().where
                    (LocalResourceRecordDao.Properties.ResourceId.eq(fillResource.getId()),
                            LocalResourceRecordDao.Properties.ResourceType.eq(fillResource.getType())).unique();

            if (localResourceRecord != null) {
                QZXTools.logE("book localResourceRecord=" + localResourceRecord + ";fillResource=" + new Gson().toJson(fillResource), null);
                rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_FINISH);
                fillResource.setFilePath(localResourceRecord.getResourceFilePath());

                progress.status = Progress.FINISH;
            } else {
                switch (progress.status) {
                    case Progress.NONE:
                        QZXTools.logE("NONE", null);
                        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_READY);
                        break;
                    case Progress.PAUSE:
                    case Progress.ERROR:
                        task.restart();
                        break;
                    case Progress.WAITING:
                        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_WATING);
                        QZXTools.logE("WAITING", null);
                        break;
                    case Progress.FINISH:
                        QZXTools.logE("FINISH", null);
                        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_FINISH);
                        break;
                    case Progress.LOADING:
                        QZXTools.logE("LOADING", null);
                        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_download_tags.setProgress(progress.currentSize/(progress.totalSize/1f));
                        break;
                }
            }
        }

        Glide.with(mContext).load(fillResource.getCover()).placeholder(R.mipmap.no_cover).error(R.mipmap.no_cover)
                .into(rvAutoLearningTeachingMaterialViewHolder.rv_item_book_face);

        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_book.setText(fillResource.getGradename().concat(fillResource.getTermname()));
        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_press.setText(fillResource.getPressname());

        String topicTitle = fillResource.getTitle();

        //如果是正则应该是\\|，但这里是字符串不是正则
        int indexTwo = topicTitle.indexOf("-");
        int indexOne = topicTitle.indexOf("｜");
        if (indexTwo != -1 && indexOne != -1) {
            topicTitle = topicTitle.substring(indexTwo + 1, indexOne - 1);
        }
        rvAutoLearningTeachingMaterialViewHolder.rv_item_book_topic.setText(topicTitle);
        if ("1".equals(flag)) {
            rvAutoLearningTeachingMaterialViewHolder.rv_item_book_subType.setText(fillResource.getSubjectName());
        } else {
            rvAutoLearningTeachingMaterialViewHolder.rv_item_book_subType.setText(fillResource.getPressname());
        }
    }

    @Override
    public int getItemCount() {
        return mDatas != null ? mDatas.size() : 0;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * 如果存在多种ViewHolder这个视图类型还是需要的，不然会部分重影
     */
    @Override
    public int getItemViewType(int position) {
        FillResource fillResource = (FillResource) mDatas.get(position).progress.extra1;
        if (fillResource.isTeachingMaterial()) {
            return TeachingMaterial;
        } else {
            return Other;
        }
    }

    /**
     * 视频、音频和图片
     */
    public class RVAutoLearningNormalResourceViewHolder extends RecyclerView.ViewHolder
            implements TipsDialog.ClickInterface, View.OnClickListener {

        //换背景色
        private RelativeLayout rv_item_main_content;
        private TextView rv_item_tv_tags;
        private ImageView rv_item_img_video;
        private TextView rv_item_tv_type;
        private TextView rv_item_tv_teacher_name;
        private TextView rv_item_tv_date;
        private DownloadProgressBar rv_item_download_tags;
        private TextView rv_item_tv_topic;
        private TextView rv_item_tv_subType;
        private TextView rv_item_tv_colorType;

        private TipsDialog tipsDialog;

        private DownloadTask task;
        private String tag;

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public void setTask(DownloadTask task) {
            this.task = task;
        }

        public void bind() {
            Progress progress = task.progress;
            FillResource fillResource = (FillResource) progress.extra1;
            handleNormalResource(this, fillResource,progress,task);
        }

        public void refresh(Progress progress) {
            switch (progress.status) {
                case Progress.NONE:
                    QZXTools.logE("NONE", null);
                    break;
                case Progress.PAUSE:
                    QZXTools.logE("PAUSE", null);
                    rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_PAUSE);
                    break;
                case Progress.ERROR:
                    QZXTools.logE("ERROR", null);
                    rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_ERROR);
                    break;
                case Progress.WAITING:
                    rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_WATING);
                    QZXTools.logE("WAITING", null);
                    break;
                case Progress.FINISH:
                    QZXTools.logE("FINISH", null);
                    rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_FINISH);
                    break;
                case Progress.LOADING:
                    QZXTools.logE("LOADING", null);
                    rv_item_download_tags.setProgress(progress.currentSize/(progress.totalSize/1f));
                    break;
            }
        }

        public RVAutoLearningNormalResourceViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_item_main_content = itemView.findViewById(R.id.rv_item_main_content);
            rv_item_tv_tags = itemView.findViewById(R.id.rv_item_tv_tags);
            rv_item_img_video = itemView.findViewById(R.id.rv_item_img_video);
            rv_item_tv_type = itemView.findViewById(R.id.rv_item_tv_type);
            rv_item_tv_teacher_name = itemView.findViewById(R.id.rv_item_tv_teacher_name);
            rv_item_tv_date = itemView.findViewById(R.id.rv_item_tv_date);
            rv_item_download_tags = itemView.findViewById(R.id.rv_item_download_tags);
            rv_item_tv_topic = itemView.findViewById(R.id.rv_item_tv_topic);
            rv_item_tv_subType = itemView.findViewById(R.id.rv_item_tv_subType);
            rv_item_tv_colorType = itemView.findViewById(R.id.rv_item_tv_colorType);

            rv_item_main_content.setOnClickListener(this);
            rv_item_download_tags.setOnClickListener(this);
        }

        private void handleDownloadTags(Progress progress) {
            QZXTools.logE("status;"+progress.status,null);

            switch (progress.status) {
                case Progress.PAUSE:
                case Progress.NONE:
                case Progress.ERROR:
                    task.restart();
                    refresh(progress);
                    break;
                case Progress.FINISH:
                    //弹出下载提示
                    TipsDialog tipsDialog = new TipsDialog();
                    tipsDialog.setTipsStyle("可以删除本地保存的该资源，删除后需要重新下载该资源，" +
                                    "您要删除还是打开该资源？",
                            "删除本地下载", "打开该资源", -1);
                    tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                        @Override
                        public void cancle() {
                            FillResource fillResource = (FillResource) progress.extra1;

                            tipsDialog.dismissAllowingStateLoss();
                            //删除本地
                            QZXTools.deleteFileOrDirectory(fillResource.getFilePath());

                            //删除本地数据库
                            LocalResourceRecord localResourceRecord = MyApplication.getInstance().getDaoSession().
                                    getLocalResourceRecordDao().queryBuilder().where
                                    (LocalResourceRecordDao.Properties.ResourceId.eq(fillResource.getId())).unique();
                            MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao().delete(localResourceRecord);

                            QZXTools.popToast(mContext, "删除成功！", false);

                            rv_item_download_tags.setStatus(DownloadProgressBar.STATUS_READY);

                            task.progress.status = Progress.NONE;

                            EventBus.getDefault().post(localResourceRecord.getResourceType(), Constant.Auto_Learning_Update);
                        }

                        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void confirm() {
                            tipsDialog.dismissAllowingStateLoss();

                            FillResource fillResource = (FillResource) progress.extra1;

                            switch (fillResource.getType()) {
                                case "3":
                                    QZXTools.logE("filepath=" + fillResource.getFilePath(), null);

                                    Intent intent_video = new Intent(mContext, VideoPlayerActivity.class);
                                    intent_video.putExtra("VideoFilePath", fillResource.getFilePath());
                                    intent_video.putExtra("VideoTitle", fillResource.getTitle());
                                    if (!TextUtils.isEmpty(fillResource.getCover())) {
                                        intent_video.putExtra("VideoThumbnail", fillResource.getCover());
                                    }
                                    mContext.startActivity(intent_video);
                                    break;
                                case "2":
                                    Intent intent = new Intent(mContext, AudioPlayActivity.class);
                                    intent.putExtra("AudioFilePath", fillResource.getFilePath());
                                    intent.putExtra("AudioFileName", fillResource.getTitle());
                                    mContext.startActivity(intent);
                                    break;
                                case "1":
                                    String filePath = fillResource.getFilePath();
                                    if (filePath.substring(filePath.lastIndexOf(".") + 1).equals("zip")) {
                                        //解压zip文件
                                        String parentDir = QZXTools.getExternalStorageForFiles(mContext, null);
                                        String destinationDir = parentDir + File.separator +
                                                fillResource.getId() + File.separator;
                                        File file = new File(destinationDir);
                                        QZXTools.logE("destination=" + destinationDir + ";file is exist=" + file.exists(), null);

                                        ArrayList<String> imgFilePathList = new ArrayList<>();

                                        if (file.exists() && file.isDirectory()) {
                                            File[] files = file.listFiles();
                                            for (File f : files) {
                                                imgFilePathList.add(f.getAbsolutePath());
                                            }
                                        } else {
                                            zipFileRead(filePath, destinationDir, imgFilePathList);
                                        }

                                        Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                                        intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                                        intent_img.putExtra("curImgIndex", 0);
                                        mContext.startActivity(intent_img);
                                    } else {
                                        Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                                        ArrayList<String> imgFilePathList = new ArrayList<>();
                                        imgFilePathList.add(filePath);
                                        intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                                        intent_img.putExtra("curImgIndex", 0);
                                        mContext.startActivity(intent_img);
                                    }
                                    break;
                                default:
                                    QZXTools.openFile(new File(fillResource.getFilePath()), mContext);
                                    break;
                            }

                            //视频、音频和图片学习资源条数埋点
                            int subjectId = -1;
                            if (!TextUtils.isEmpty(fillResource.getSubjectId())) {
                                subjectId = Integer.parseInt(fillResource.getSubjectId());
                            }
                            MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FOUR,
                                    -1, subjectId, fillResource.getSubjectName());
                        }
                    });
                    tipsDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                    break;
            }
        }

        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void handleMainContent(Progress progress) {
            if (!ViewUtils.isFastClick(1000)) {
                return;
            }

            FillResource fillResource = (FillResource) progress.extra1;

            if (fillResource.isItemBank()) {
                //进入教材的详情页面
                Intent intent = new Intent(mContext, ItemBankBookActivity.class);
                //传入学段和学科
                String learning_section = fillResource.getXd();
                String subject = fillResource.getChid();
                String chapterId = fillResource.getKnowledgeId();
                QZXTools.logE("learning_section=" + learning_section + ";subject="
                        + subject + ";chapterId=" + chapterId, null);
                intent.putExtra("learning_section", learning_section);
                intent.putExtra("subject", subject);
                intent.putExtra("chapterId", chapterId);
                mContext.startActivity(intent);

                //题库教材学习资源条数埋点
                int subjectId = -1;
                if (!TextUtils.isEmpty(fillResource.getSubjectId())) {
                    subjectId = Integer.parseInt(fillResource.getSubjectId());
                }
                      /*  MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FOUR,
                                -1, subjectId, mDatas.get(getLayoutPosition() - 1).getSubjectName());*/
                //自主学习埋点成功打开一个资源
                String uuid = UUID.randomUUID().toString();
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("SelfLearning", uuid);
                BuriedPointUtils.buriedPoint("2032", "", "", "", uuid);
            } else {
                //准备阶段弹出下载提示
                if (task.progress.status==Progress.NONE) {
                    //弹出下载提示
                    tipsDialog = new TipsDialog();
                    tipsDialog.setTipsStyle("要下载后才能获取资源，请问是否下载？",
                            "取消", "去下载", -1);
                    tipsDialog.setClickInterface(this);
                    tipsDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                } else if (task.progress.status==Progress.FINISH) {
                    switch (fillResource.getType()) {
                        case "3":
                            QZXTools.logE("filepath=" + fillResource.getFilePath(), null);

                            Intent intent_video = new Intent(mContext, VideoPlayerActivity.class);
                            intent_video.putExtra("VideoFilePath", fillResource.getFilePath());
                            intent_video.putExtra("VideoTitle", fillResource.getTitle());
                            if (!TextUtils.isEmpty(fillResource.getCover())) {
                                intent_video.putExtra("VideoThumbnail", fillResource.getCover());
                            }
                            mContext.startActivity(intent_video);

                            //直接播放有问题：例如播放完成后的重复播放以及返回键退出
//                                    JZVideoPlayer.setMediaInterface(new IjkMediaEngine());
//                                    JZVideoPlayerStandard.NORMAL_ORIENTATION = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
//                                    JZVideoPlayer.startFullscreen(mContext, CustomeJZVideoPlayerStandard.class,
//                                            mDatas.get(getLayoutPosition() - 1).getFilePath(), "");
                            break;
                        case "2":
                            Intent intent = new Intent(mContext, AudioPlayActivity.class);
                            intent.putExtra("AudioFilePath", fillResource.getFilePath());
                            intent.putExtra("AudioFileName", fillResource.getTitle());
                            mContext.startActivity(intent);
                            break;
                        case "1":
                            String filePath = fillResource.getFilePath();
                            if (filePath.substring(filePath.lastIndexOf(".") + 1).equals("zip")) {
                                //解压zip文件
                                String parentDir = QZXTools.getExternalStorageForFiles(mContext, null);
                                String destinationDir = parentDir + File.separator +
                                        fillResource + File.separator;
                                File file = new File(destinationDir);
                                QZXTools.logE("destination=" + destinationDir + ";file is exist=" + file.exists(), null);

                                ArrayList<String> imgFilePathList = new ArrayList<>();

                                if (file.exists() && file.isDirectory()) {
                                    File[] files = file.listFiles();
                                    for (File f : files) {
                                        imgFilePathList.add(f.getAbsolutePath());
                                    }
                                } else {
                                    zipFileRead(filePath, destinationDir, imgFilePathList);
                                }

                                Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                                intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                                intent_img.putExtra("curImgIndex", 0);
                                mContext.startActivity(intent_img);
                            } else {
                                Intent intent_img = new Intent(mContext, ImageLookActivity.class);
                                ArrayList<String> imgFilePathList = new ArrayList<>();
                                imgFilePathList.add(filePath);
                                intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
                                intent_img.putExtra("curImgIndex", 0);
                                mContext.startActivity(intent_img);
                            }
                            break;
                        default:
                            QZXTools.openFile(new File(fillResource.getFilePath()), mContext);
                            break;
                    }

                    //视频、音频和图片学习资源条数埋点
                    int subjectId = -1;
                    if (!TextUtils.isEmpty(fillResource.getSubjectId())) {
                        subjectId = Integer.parseInt(fillResource.getSubjectId());
                    }
                    MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FOUR,
                            -1, subjectId, fillResource.getSubjectName());
                }
            }
        }

        @Override
        public void cancle() {
            tipsDialog.dismissAllowingStateLoss();
            tipsDialog.setClickInterface(null);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void confirm() {
            tipsDialog.dismissAllowingStateLoss();
            tipsDialog.setClickInterface(null);
            task.restart();
        }

        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rv_item_main_content:
                    handleMainContent(task.progress);
                    break;
                case R.id.rv_item_download_tags:
                    handleDownloadTags(task.progress);
                    break;
            }
        }
    }

    /**
     * 教材
     */
    public class RVAutoLearningTeachingMaterialViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private RelativeLayout rv_item_book_main_content;
        private RoundCornerImageView rv_item_book_face;
        private TextView rv_item_book_book;
        private TextView rv_item_book_press;
        private DownloadProgressBar rv_item_book_download_tags;
        private TextView rv_item_book_topic;
        private TextView rv_item_book_subType;
        private TextView rv_item_book_colorType;

        private DownloadTask task;
        private String tag;

        public void setTag(String tag) {
            this.tag = tag;
        }

        public String getTag() {
            return tag;
        }

        public void setTask(DownloadTask task) {
            this.task = task;
        }

        public void bind() {
            Progress progress = task.progress;
            FillResource fillResource = (FillResource) progress.extra1;
            handleTeachingMaterial(this, fillResource,progress,task);
        }

        public void refresh(Progress progress) {
            switch (progress.status) {
                case Progress.NONE:
                    QZXTools.logE("NONE", null);
                    break;
                case Progress.PAUSE:
                    QZXTools.logE("PAUSE", null);
                    rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_PAUSE);
                    break;
                case Progress.ERROR:
                    QZXTools.logE("ERROR", null);
                    rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_ERROR);
                    break;
                case Progress.WAITING:
                    rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_WATING);
                    QZXTools.logE("WAITING", null);
                    break;
                case Progress.FINISH:
                    QZXTools.logE("FINISH", null);
                    rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_FINISH);
                    break;
                case Progress.LOADING:
                    QZXTools.logE("LOADING", null);
                    rv_item_book_download_tags.setProgress(progress.currentSize/(progress.totalSize/1f));
                    break;
            }
        }

        public RVAutoLearningTeachingMaterialViewHolder(@NonNull View itemView) {
            super(itemView);
            rv_item_book_main_content = itemView.findViewById(R.id.rv_item_book_main_content);
            rv_item_book_face = itemView.findViewById(R.id.rv_item_book_face);
            rv_item_book_book = itemView.findViewById(R.id.rv_item_book_book);
            rv_item_book_press = itemView.findViewById(R.id.rv_item_book_press);
            rv_item_book_download_tags = itemView.findViewById(R.id.rv_item_book_download_tags);
            rv_item_book_topic = itemView.findViewById(R.id.rv_item_book_topic);
            rv_item_book_subType = itemView.findViewById(R.id.rv_item_book_subType);
            rv_item_book_colorType = itemView.findViewById(R.id.rv_item_book_colorType);

            rv_item_book_main_content.setOnClickListener(this);
            rv_item_book_download_tags.setOnClickListener(this);
        }

        private void handleDownloadTags(Progress progress) {
            switch (progress.status) {
                case Progress.PAUSE:
                case Progress.NONE:
                case Progress.ERROR:
                    task.restart();
                    refresh(progress);
                    break;
                case Progress.FINISH:
                    //弹出下载提示
                    TipsDialog tipsDialog = new TipsDialog();
                    tipsDialog.setTipsStyle("可以删除本地保存的该资源，删除后需要重新下载该资源，" +
                                    "您要删除还是打开该资源？",
                            "删除本地下载", "打开该资源", -1);
                    tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                        @Override
                        public void cancle() {
                            FillResource fillResource = (FillResource) progress.extra1;

                            tipsDialog.dismissAllowingStateLoss();
                            //删除本地
                            QZXTools.deleteFileOrDirectory(fillResource.getFilePath());

                            //删除本地数据库
                            LocalResourceRecord localResourceRecord = MyApplication.getInstance().getDaoSession().
                                    getLocalResourceRecordDao().queryBuilder().where
                                    (LocalResourceRecordDao.Properties.ResourceId.eq(fillResource.getId())).unique();
                            MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao().delete(localResourceRecord);

                            QZXTools.popToast(mContext, "删除成功！", false);

                            rv_item_book_download_tags.setStatus(DownloadProgressBar.STATUS_READY);

                            task.progress.status = Progress.NONE;

                            EventBus.getDefault().post(localResourceRecord.getResourceType(), Constant.Auto_Learning_Update);
                        }

                        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
                        @Override
                        public void confirm() {
                            tipsDialog.dismissAllowingStateLoss();

                            FillResource fillResource = (FillResource) progress.extra1;

                            Intent intent = new Intent(mContext, FlipEBookResourceActivity.class);
                            intent.putExtra("EBookFilePath",fillResource.getFilePath());
                            intent.putExtra("CoverUrl", fillResource.getCover());
                            mContext.startActivity(intent);

                            //教材学习资源条数埋点
                            int subjectId = -1;
                            if (!TextUtils.isEmpty(fillResource.getSubjectId())) {
                                subjectId = Integer.parseInt(fillResource.getSubjectId());
                            }
                            MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FOUR,
                                    -1, subjectId, fillResource.getSubjectName());
                        }
                    });
                    tipsDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                    break;
            }
        }

        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        private void handleMainContent(Progress progress) {
            if (!ViewUtils.isFastClick(1000)) {
                return;
            }

            FillResource fillResource = (FillResource) progress.extra1;

            //之所以减一是因为XRecyclerView有个HeadView
            if (fillResource.isItemBank()) {
                //进入教材的详情页面
                Intent intent = new Intent(mContext, ItemBankBookActivity.class);
                //传入学段和学科
                String learning_section = fillResource.getXd();
                String subject = fillResource.getChid();
                String chapterId = fillResource.getKnowledgeId();
                QZXTools.logE("learning_section=" + learning_section + ";subject="
                        + subject + ";chapterId=" + chapterId, null);
                intent.putExtra("learning_section", learning_section);
                intent.putExtra("subject", subject);
                intent.putExtra("chapterId", chapterId);
                mContext.startActivity(intent);

                //题库教材学习资源条数埋点
                int subjectId = -1;
                if (!TextUtils.isEmpty(fillResource.getSubjectId())) {
                    subjectId = Integer.parseInt(fillResource.getSubjectId());
                }
                MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FOUR,
                        -1, subjectId, fillResource.getSubjectName());
            } else {
                //准备阶段弹出下载提示
                if (task.progress.status==Progress.NONE) {
                    //弹出下载提示
                    TipsDialog tipsDialog = new TipsDialog();
                    tipsDialog.setTipsStyle("要下载后才能获取资源，请问是否下载？",
                            "取消", "去下载", -1);
                    tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                        @Override
                        public void cancle() {
                            tipsDialog.dismissAllowingStateLoss();
                        }

                        @Override
                        public void confirm() {
                            tipsDialog.dismissAllowingStateLoss();

                            task.restart();
                        }
                    });
                    tipsDialog.show(((FragmentActivity) mContext).getSupportFragmentManager(), TipsDialog.class.getSimpleName());
                } else if (task.progress.status==Progress.FINISH) {
                    Intent intent = new Intent(mContext, FlipEBookResourceActivity.class);
                    intent.putExtra("EBookFilePath", fillResource.getFilePath());
                    intent.putExtra("CoverUrl", fillResource.getCover());
                    mContext.startActivity(intent);

                    //教材学习资源条数埋点
                    int subjectId = -1;
                    if (!TextUtils.isEmpty(fillResource.getSubjectId())) {
                        subjectId = Integer.parseInt(fillResource.getSubjectId());
                    }
                    MyApplication.getInstance().AutoLearningMaiDian(MyApplication.FLAG_AUTO_LEARNING_FOUR,
                            -1, subjectId, fillResource.getSubjectName());
                }
            }
        }

        @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.rv_item_book_main_content:
                    handleMainContent(task.progress);
                    break;
                case R.id.rv_item_book_download_tags:
                    handleDownloadTags(task.progress);
                    break;
            }
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(读取Zip信息 ， 获得zip中所有的目录文件信息)
     * @param设定文件
     */
    public void zipFileRead(String file, String saveRootDirectory, ArrayList<String> imgList) {
        try {
            // 获得zip信息
            ZipFile zipFile = new ZipFile(file, "GBK");
            Enumeration<ZipEntry> enu = (Enumeration<ZipEntry>) zipFile.getEntries();
            while (enu.hasMoreElements()) {
                ZipEntry zipElement = (ZipEntry) enu.nextElement();
                InputStream read = zipFile.getInputStream(zipElement);
                String fileName = zipElement.getName();
                if (fileName != null && fileName.indexOf(".") != -1) {// 是否为文件
                    unZipFile(zipElement, read, saveRootDirectory, imgList);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @return void 返回类型
     * @throws
     * @Description: TODO(找到文件并读取解压到指定目录)
     */
    public void unZipFile(ZipEntry ze, InputStream read, String saveRootDirectory, ArrayList<String> imgList) {
        // 如果只读取图片，自行判断就OK.
        String fileName = ze.getName();
        imgList.add(saveRootDirectory.concat(fileName));

        // 指定要解压出来的文件格式（这些格式可抽取放置在集合或String数组通过参数传递进来，方法更通用）
        File file = new File(saveRootDirectory + fileName);
        if (!file.exists()) {
            File rootDirectoryFile = new File(file.getParent());
            // 创建目录
            if (!rootDirectoryFile.exists()) {
                boolean ifSuccess = rootDirectoryFile.mkdirs();
                if (ifSuccess) {
                    System.out.println("文件夹创建成功!");
                } else {
                    System.out.println("文件创建失败!");
                }
            }
            // 创建文件
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        // 写入文件
        BufferedOutputStream write = null;
        try {
            write = new BufferedOutputStream(new FileOutputStream(file));
            int cha = 0;
            while ((cha = read.read()) != -1) {
                write.write(cha);
            }
            // 要注意IO流关闭的先后顺序
            write.flush();
            write.close();
            read.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void unRegister() {
        if (mDatas==null||mDatas.size()==0){
            return;
        }
        for (DownloadTask task : mDatas) {
            task.unRegister(createTag(task));
        }
    }

    public List<DownloadTask> getDownloadingTasks(){
        List<DownloadTask> tasks = new ArrayList<>();
        if (mDatas==null||mDatas.size()==0){
            return tasks;
        }
        for (DownloadTask task : mDatas) {
            if (task.progress.status==Progress.LOADING){
                tasks.add(task);
            }
        }
        return tasks;
    }
}