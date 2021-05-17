package com.telit.zhkt_three.listener.autolearning;

import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;
import com.telit.zhkt_three.Adapter.AutoLearning.AutoLearningAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.Resource.FillResource;
import com.telit.zhkt_three.JavaBean.Resource.LocalResourceRecord;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/4/7 9:32
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class TeachingMaterialDownloadListener extends DownloadListener {
    private AutoLearningAdapter.RVAutoLearningTeachingMaterialViewHolder viewHolder;

    public TeachingMaterialDownloadListener(Object tag, AutoLearningAdapter.RVAutoLearningTeachingMaterialViewHolder viewHolder) {
        super(tag);
        this.viewHolder = viewHolder;
    }

    @Override
    public void onStart(Progress progress) {
    }

    @Override
    public void onProgress(Progress progress) {
        if (tag == viewHolder.getTag()) {
            viewHolder.refresh(progress);
        }
    }

    @Override
    public void onError(Progress progress) {
        Throwable throwable = progress.exception;
        if (throwable != null) throwable.printStackTrace();
        progress.status = Progress.ERROR;

        QZXTools.logE("message:"+throwable.getMessage(),null);
    }

    @Override
    public void onFinish(File file, Progress progress) {
        progress.status = Progress.FINISH;
        FillResource fillResource = (FillResource) progress.extra1;
        //设置文件名
        fillResource.setFilePath(file.getPath());

        //保存数据库
        LocalResourceRecord localResourceRecord = new LocalResourceRecord();
        //QZXTools.logE("layoutPosition=" + getLayoutPosition(), null);//+1
        localResourceRecord.setResourceType(fillResource.getType());
        localResourceRecord.setResourceId(fillResource.getId());

        localResourceRecord.setResourceName(fillResource.getTitle());
        localResourceRecord.setImageUrl(fillResource.getCover());
        localResourceRecord.setIsChoosed(false);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String updateDate = simpleDateFormat.format(new Date());
        localResourceRecord.setResourceUpdateDate(updateDate);

        localResourceRecord.setCanChecked(false);

        localResourceRecord.setResourceFilePath(file.getPath());
        MyApplication.getInstance().getDaoSession().getLocalResourceRecordDao().insertOrReplace(localResourceRecord);

        EventBus.getDefault().post(localResourceRecord.getResourceType(), Constant.Auto_Learning_Update);

        QZXTools.logE("downloadComplete", null);
    }

    @Override
    public void onRemove(Progress progress) {
    }
}
