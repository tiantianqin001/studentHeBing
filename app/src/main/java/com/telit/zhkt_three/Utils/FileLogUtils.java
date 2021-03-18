package com.telit.zhkt_three.Utils;

import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * author: qzx
 * Date: 2018/12/10 8:36
 * 将Log日志保存到文件中
 */
public class FileLogUtils {

    private static FileLogUtils instance;

    private static final String SavePath = Environment.getExternalStorageDirectory() + "/" + "SavePath/";

    private FileLogUtils() {

    }

    public static FileLogUtils getInstance() {
        if (instance == null) {
            synchronized (FileLogUtils.class) {
                if (instance == null) {
                    instance = new FileLogUtils();
                }
            }
        }
        return instance;
    }


    /**
     * 清空Log即复写一个空字符串
     */
    public void clearLogs() {
        File file_log = new File(SavePath + "Log.txt");
        if (!file_log.exists()) {
            return;
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file_log);
            fos.write("".getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    /**
     * 追加保存Log
     */
    public void saveLogs(String content) {
        File file_directory = new File(SavePath);
        if (!file_directory.exists()) {
            Log.e("zbv", "mkdir");
            file_directory.mkdir();
        }

        Log.e("zbv", "SavePath=" + SavePath);

        RandomAccessFile randomAccessFile = null;

        try {

            StringBuffer stringBuffer = new StringBuffer();
            stringBuffer.setLength(0);
            //拼接一个日期
            stringBuffer.append("date: ");
            stringBuffer.append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss:SSS").format(new Date()));
            stringBuffer.append("--->");
            stringBuffer.append(content);
            stringBuffer.append("\n");

            File file_log = new File(SavePath + "Log.txt");
            if (!file_log.exists()) {
                file_log.createNewFile();
            }

            randomAccessFile = new RandomAccessFile(file_log, "rw");
            randomAccessFile.seek(file_log.length());
            randomAccessFile.write(stringBuffer.toString().getBytes("UTF-8"));

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (randomAccessFile != null) {
                try {
                    randomAccessFile.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

//        try {
//            FileOutputStream fos = new FileOutputStream(SavePath + "Log.txt");//复写
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }

    }

}
