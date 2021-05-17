package com.telit.zhkt_three.Utils;

import com.lzy.okgo.model.Progress;
import com.lzy.okserver.download.DownloadListener;

import java.io.File;

/**
 * *****************************************************************
 * author: Administrator
 * time: 2021/4/7 8:46
 * name;
 * overview:
 * usage:
 * ******************************************************************
 */
public class LogDownloadListener extends DownloadListener {

    public LogDownloadListener() {
        super("LogDownloadListener");
    }

    @Override
    public void onStart(Progress progress) {
        System.out.println("onStart: " + progress);
    }

    @Override
    public void onProgress(Progress progress) {
        System.out.println("onProgress: " + progress);
    }

    @Override
    public void onError(Progress progress) {
        System.out.println("onError: " + progress);
        progress.exception.printStackTrace();
    }

    @Override
    public void onFinish(File file, Progress progress) {
        System.out.println("onFinish: " + progress);
    }

    @Override
    public void onRemove(Progress progress) {
        System.out.println("onRemove: " + progress);
    }
}

