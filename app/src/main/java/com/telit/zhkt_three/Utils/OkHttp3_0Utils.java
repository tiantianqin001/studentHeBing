package com.telit.zhkt_three.Utils;

import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.RequiresApi;
import android.text.TextUtils;

import com.telit.zhkt_three.MyApplication;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.ConnectionPool;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
public class OkHttp3_0Utils {
    private static volatile OkHttp3_0Utils okHttp3_0Utils;

    private OkHttpClient okHttpClient;

    /**
     * 默认不适用缓存的，需改代码转换
     */
    private static final boolean OPEN_CACHE = false;

    /**
     * 缓存的网络拦截器
     */
    class CacheControlInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request().newBuilder().addHeader("Connection","close").build();
            if (QZXTools.isNetworkAvailable()) {
                QZXTools.logE("有网络", null);
            } else {
                QZXTools.logE("无网络", null);
                QZXTools.popToast(MyApplication.getInstance().getApplicationContext(), "请打开网络！", false);
            }
            Response response = chain.proceed(request);
            return response;
        }
    }

    /**
     * 日志拦截器
     */
    class LoggingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            long t1 = System.nanoTime();
            QZXTools.logE(String.format("Sending request %s on %s%n%s",
                    request.url(), chain.connection(), request.headers()), null);

            Response response = chain.proceed(request);

            long t2 = System.nanoTime();
            QZXTools.logE(String.format("Received response for %s in %.1fms%n%s",
                    response.request().url(), (t2 - t1) / 1e6d, response.headers()), null);

            return response;
        }
    }


    private static final int CONNECT_TIMEOUT = 100;
    private static final int READ_TIMEOUT =60;
    private static final int WRITE_TIMEOUT = 60;


    private OkHttp3_0Utils() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.retryOnConnectionFailure(true);
        //默认超时都是10秒
        builder.connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS);//连接超时
        builder.readTimeout(READ_TIMEOUT, TimeUnit.SECONDS);//读取超时
        builder.writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS); //写超时
        //设置4次重连
        builder.addInterceptor(new RetryIntercepter(3));//重试

        //自定义连接池最大空闲连接数和等待时间大小，否则默认最大5个空闲连接
        builder.connectionPool(new ConnectionPool(32,5,TimeUnit.MINUTES));

        if (OPEN_CACHE) {
            int cacheSize = 10 * 1024 * 1024;//10M
            //内部的缓存文件区域：/data/data/包名/cache
            Cache cache = new Cache(new File(QZXTools.getInternalStorageForCache
                    (MyApplication.getInstance().getApplicationContext())), cacheSize);
            //缓存
            builder.cache(cache);
        }

        //拦截器可以重写Reqest以及Response，对于重写Response需要注意这个危险性

        //应用拦截器
        builder.addInterceptor(new LoggingInterceptor());
        //网络拦截器
        builder.addNetworkInterceptor(new CacheControlInterceptor());

        okHttpClient = builder.build();
    }

    public static OkHttp3_0Utils getInstance() {
        if (okHttp3_0Utils == null) {
            synchronized (OkHttp3_0Utils.class) {
                if (okHttp3_0Utils == null) {
                    okHttp3_0Utils = new OkHttp3_0Utils();
                }
            }
        }
        return okHttp3_0Utils;
    }

    /**
     * 取消指定tag的请求
     */
    public void cancelTagRequest(Object tag) {
        if (okHttpClient != null && tag != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                if (tag.equals(call.request().tag())) {
                    call.cancel();
                }
            }
        }
    }

    /**
     * 取消所有网络请求
     */
    public void cancelAllRequest() {
        if (okHttpClient != null) {
            for (Call call : okHttpClient.dispatcher().queuedCalls()) {
                call.cancel();
            }

            for (Call call : okHttpClient.dispatcher().runningCalls()) {
                call.cancel();
            }
        }
    }

    /**
     * 封装异步GET模式---普通模式
     * <p>
     * 一、header会覆盖同Key的值、addHeader允许存在同Key多值
     * 二、使用HttpUrl来类似于Post请求增加Query参数
     *
     * @param url              请求的Url地址
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void asyncGetOkHttp(String url, Callback responseCallback) {
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步GET模式---询问参数模式
     * <p>
     * 使用HttpUrl来类似于Post请求增加Query参数
     *
     * @param url              请求的Url地址
     * @param queryParams      拼接的Get询问参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncGetOkHttp(String url, Map<String, String> queryParams, Callback responseCallback) {
        //借助于HttpUrl的Builder添加Get模式的查询参数
        Iterator<Map.Entry<String, String>> iterator = queryParams.entrySet().iterator();
        HttpUrl.Builder builder = HttpUrl.parse(url).newBuilder();
        while (iterator.hasNext()) {
            Map.Entry<String, String> entry = iterator.next();
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        HttpUrl httpUrl = builder.build();
        //把HttpUrl转化成Url
        Request request = new Request.Builder()
                .url(httpUrl.toString())
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步GET模式---添加Header模式
     * <p>
     * 一、header会覆盖同Key的值、addHeader允许存在同Key多值
     *
     * @param url              请求的Url地址
     * @param headerParams     需要添加的请求头参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncGetOkHttpHadHeader(String url, Map<String, String> headerParams, Callback responseCallback) {
        Request request = new Request.Builder()
                .headers(Headers.of(headerParams))
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步POST模式---普通的表单模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param params           POST请求所需要的请求体参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostOkHttp(String url, Map<String, String> params, Callback responseCallback) {
        if (params==null || params.size()==0)return;
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (TextUtils.isEmpty(entry.getValue())){
                continue;
            }
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步POST模式---普通的表单模式+Header参数模式
     * 一、必传请求体参数
     *
     * @param url              请求的Url地址
     * @param params           POST请求所需要的请求体参数
     * @param headerParams     需要添加的请求头参数
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostOkHttp(String url, Map<String, String> params,
                                Map<String, String> headerParams, Callback responseCallback) {
        FormBody.Builder builder = new FormBody.Builder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            builder.add(entry.getKey(), entry.getValue());
        }
        FormBody formBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(formBody)
                .headers(Headers.of(headerParams))
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    //以二进制的形式上传文件
    public static final MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");

    /**
     * 根据文件名获取MIME类型
     */
    private MediaType guessMimeType(String fileName) {
        FileNameMap fileNameMap = URLConnection.getFileNameMap();
        fileName = fileName.replace("#", "");   //解决文件名中含有#号异常的问题
        String contentType = fileNameMap.getContentTypeFor(fileName);
        if (contentType == null) {
            return MEDIA_TYPE_STREAM;
        }
        return MediaType.parse(contentType);
    }

    /**
     * 封装异步POST模式---多种类型的请求模式
     * <p>
     * 一、常用于文件上传
     * 说明：Multipart/form-data是上传文件的一种方式，是浏览器用表单上传文件的方式
     * 二、如果没有要上传的问文件就不要用此方法
     * <p>
     * <p>
     * 添加要上传的参数
     * 方式一：
     * addPart(Headers.of("Content-Disposition","form-data; name=\"file\";filename=\"file.png\"),
     * RequestBody.create(MEDIA_TYPE_PNG, files.get(j))
     * 方式二：
     * //内部做了字符串的拼接处理了
     * addFormDataPart(String name, @Nullable String filename, RequestBody body)
     *
     * @param url              请求的Url地址
     * @param fileKeyName      上传文件时和服务端匹配的key
     * @param params           POST请求所需要的请求体参数
     * @param fileMap          要上传的带MIME类型的文件,第一个参数是文件名：file.getName()
     *                         通过guessMimeType解析得到MIME类型
     *                         例如PNG图片文件：
     *                         MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
     *                         例如二进制流文件：
     *                         MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
     * @param responseCallback 请求的异步回调，注意：这是在子线程中
     */
    public void asyncPostMultiOkHttp(String url, String fileKeyName, Map<String, String> params,
                                     Map<String, File> fileMap, Callback responseCallback) {
        //multipart/form-data 必须这种类型
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (params != null) {
            //添加一般参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }
        //添加文件参数
        for (Map.Entry<String, File> file : fileMap.entrySet()) {
            /*
             * 例如PNG图片文件：
             * MediaType MEDIA_TYPE_PNG = MediaType.parse("image/png");
             * 例如二进制流文件：
             * MediaType MEDIA_TYPE_STREAM = MediaType.parse("application/octet-stream");
             * */
            RequestBody requestBody = RequestBody.create(guessMimeType(file.getKey()), file.getValue());
            //注意name:"file" 对应服务端要求的文件参数
            builder.addFormDataPart(fileKeyName, file.getValue().getName(), requestBody);
        }
        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }

    /**
     * 封装异步POST模式---多种类型的请求模式,单个文件模式
     */
    public void asyncPostSingleOkHttp(String url, String fileKeyName, Map<String, String> params,
                                      File file, Callback responseCallback) {
        //multipart/form-data 必须这种类型
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);
        if (params != null) {
            //添加一般参数
            for (Map.Entry<String, String> entry : params.entrySet()) {
                builder.addFormDataPart(entry.getKey(), entry.getValue());
            }
        }

        if (file != null && fileKeyName != null) {
            RequestBody requestBody = RequestBody.create(guessMimeType(file.getName()), file);
            //注意name:"file" 对应服务端要求的文件参数
            builder.addFormDataPart(fileKeyName, file.getName(), requestBody);
        }

        MultipartBody multipartBody = builder.build();
        Request request = new Request.Builder()
                .url(url)
                .post(multipartBody)
                .build();
        okHttpClient.newCall(request).enqueue(responseCallback);
    }


    /**
     * 一次性下载单个文件,记录下载的进度
     * 需要在new DownloadCallback的类中的Handler的处理消息方法中手动加入Looper.prepare()以及Looper.loop()
     *
     * @param url              请求服务端的地址
     * @param downloadCallback 回调函数
     */
    public void downloadSingleFileForOnce(final String url, String fileDir, DownloadCallback downloadCallback) {
        handlerCallback = downloadCallback;

        Request request = new Request.Builder()
                .url(url)
                .tag("123")
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                /**
                 * error 1、下载失败java.net.ConnectException: Failed to connect to localhost/127.0.0.1:8080
                 * error 2、下载失败java.net.SocketTimeoutException: failed to connect to /172.16.5.72 (port 8080) after 10000ms
                 * */
                QZXTools.logE("下载失败" + e, null);
                mHandler.sendEmptyMessage(Fail);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                long contentLenght = response.body().contentLength();
                MediaType mediaType = response.body().contentType();

//                if (mediaType.toString().equals("text/html")) {
//                    //文本
//                    String bodyString = response.body().string();
//                }

                // contentLength=9464904;mediaType=audio/mpeg
                /**
                 * contentLength=24;mediaType=text/html;charset=UTF-8;bodyString=资源文件不存在！
                 * */
                QZXTools.logE("contentLength=" + contentLenght + ";mediaType=" + mediaType, null);

                //全部下载完成才会继续下一步
//                byte[] resultByte = response.body().bytes();
//                fos.write(resultByte);

                //  Content-Disposition: attachment;filename=0ffd3bf46566469e850ab3e52e566797.mp4
                String headNeed = response.header("Content-Disposition");
                QZXTools.logE("start headNeed=" + headNeed, null);
                if (!TextUtils.isEmpty(headNeed) && headNeed.contains("filename")) {
                    headNeed = headNeed.substring(headNeed.indexOf("filename=") + "filename=".length());
                    QZXTools.logE("end headNeed=" + headNeed, null);
                }

                //保存的下载文件
                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String parentDir = QZXTools.getExternalStorageForFiles(MyApplication.getInstance()
                            .getApplicationContext(), null) + File.separator;

                    if (!TextUtils.isEmpty(fileDir)) {
                        parentDir = parentDir + fileDir + File.separator;
                        File file = new File(parentDir);
                        QZXTools.logE("isExist=" + file.exists() + ";isDir=" + file.isDirectory(), null);
                        if (!file.exists()) {
                            boolean isDirSuccess = file.mkdir();
                            QZXTools.logE("isDirSuccess=" + isDirSuccess, null);
                        }
                    }

                    String name;
                    if (TextUtils.isEmpty(headNeed)) {
                        name = url.substring(url.lastIndexOf("/") + 1);
                    } else {
                        name = headNeed;
                    }
                    QZXTools.logE("parentDir=" + parentDir + ";name=" + name, null);
                    File file = new File(parentDir, name);

                    FileOutputStream fos = new FileOutputStream(file);

                    byte[] bytes = new byte[1024];
                    int len = -1;
                    InputStream is = response.body().byteStream();

                    long sum = 0;

                    while ((len = is.read(bytes)) != -1) {
                        fos.write(bytes, 0, len);
                        sum += len;
                        int process = Math.round(sum * 1.0f / contentLenght * 100);
//                        QZXTools.logE("sum=" + sum + ";len=" + len + ";percent=" +
//                                process + "%", null);

                        Message message = mHandler.obtainMessage();
                        message.what = MsgProcess;
                        message.arg1 = process;
                        mHandler.sendMessage(message);
                    }

                    fos.flush();

                    fos.close();

                    Message message = mHandler.obtainMessage();
                    message.what = CommonComplete;
                    message.obj = file.getAbsolutePath();
                    mHandler.sendMessage(message);

                }
            }
        });
    }

    private int threadCount = 4;

    public void setThreadCount(int threadCount) {
        this.threadCount = threadCount;
    }

    //---------------------------------多线程下载单个大文件----------------------------------------

    /**
     * 多线程下载单个大文件
     *
     * @param url              请求服务端的地址
     * @param downloadCallback 回调函数
     */
    public void downloadSingleFileForMultiThread(String url, DownloadCallback downloadCallback) {
        handlerCallback = downloadCallback;
        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("下载失败" + e, null);

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                progressArray = new int[threadCount];
                long contentLenght = response.body().contentLength();
                //将总体的文件大小按照线程个数分配，把分配不均匀的留给最后一个线程
                long averageLength = contentLenght / threadCount;
                for (int i = 0; i < threadCount; i++) {
                    //多个线程的不同位置获取写入流，相当于开启多个线程开始下载同一个文件的不同区域
                    long startIndex = i * averageLength;
                    long endIndex = (i + 1) * averageLength;
                    if (i == threadCount - 1) {
                        endIndex = contentLenght;
                    }
                    downloadSingleFramStartToEnd(url, startIndex, endIndex, i);
                }
            }
        });
    }

    private static final int Fail = 17;
    private static final int Process = 18;
    private static final int Complete = 19;
    private static final int MsgProcess = 20;
    private static final int CommonComplete = 21;
    private int count = 0;
    private int[] progressArray;
    private DownloadCallback handlerCallback;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (handlerCallback == null) {
                return;
            }
            switch (msg.what) {
                case Fail:
                    handlerCallback.downloadFailure();
                    break;
                case Process:
                    int value = 0;
                    for (int i = 0; i < progressArray.length; i++) {
                        value += progressArray[i];
                    }
                    handlerCallback.downloadProcess(value);
                    break;
                case Complete:
                    count++;
                    if (count == threadCount) {
                        handlerCallback.downloadComplete(null);
                    }
                    break;
                case MsgProcess:
                    handlerCallback.downloadProcess(msg.arg1);
                    break;
                case CommonComplete:
                    if (call != null) {
                        call = null;
                    }

                    if (msg.obj != null) {
                        String filePath = (String) msg.obj;
                        handlerCallback.downloadComplete(filePath);
                    } else {
                        handlerCallback.downloadComplete(null);
                    }
                    break;
            }
        }
    };

    /**
     * 如果使用new Handler的匿名内部类就要在Activity中的onDestroy方法中调用该方法
     */
    public void cleanHandler() {
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            handlerCallback = null;
        }
    }


    /**
     * 多线程下载的获取不同位置的内容
     *
     * @param url   请求的服务端地址
     * @param start 开始的流位置
     * @param end   结束的流位置
     */
    private void downloadSingleFramStartToEnd(String url, long start, long end, int index) {

        Request request = new Request.Builder()
                .url(url)
                .addHeader("Range", "bytes=" + start + "-" + end)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("下载失败" + e, null);
                mHandler.sendEmptyMessage(Fail);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {

                if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                    String parentDir = QZXTools.getExternalStorageForFiles(MyApplication.getInstance()
                            .getApplicationContext(), null) + File.separator;
                    String name = url.substring(url.lastIndexOf("/") + 1);
                    QZXTools.logE("parentDir=" + parentDir + ";name=" + name, null);
                    File file = new File(parentDir, name);

                    RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

                    randomAccessFile.seek(start);

                    long contentLenght = response.body().contentLength();

                    byte[] bytes = new byte[1024];
                    int len = -1;
                    InputStream inputStream = response.body().byteStream();

                    long sum = 0;

                    while ((len = inputStream.read(bytes)) != -1) {
                        randomAccessFile.write(bytes, 0, len);

                        sum += len;

                        int progress = Math.round(sum * 1.0f / contentLenght / threadCount * 100);

                        progressArray[index] = progress;

                        mHandler.sendEmptyMessage(Process);

//                        QZXTools.logE("threadId=" + Thread.currentThread() + "sum=" + sum + ";len=" + len + ";percent=" +
//                                progress + "%", null);

                    }

                    randomAccessFile.close();

                    mHandler.sendEmptyMessage(Complete);
                }
            }
        });
    }
    //---------------------------------多线程下载单个大文件----------------------------------------

    //------------------------------------断点下载单个文件-----------------------------------------
    private Call call;

    /**
     * 断点下载单个文件
     *
     * @param url              请求的服务端地址
     * @param fileName         文件名，如果为空则区url的最后/字符串
     * @param downloadCallback 回调函数
     */
    public void downloadSingleFileCanMulti(final String url, String fileName, DownloadCallback downloadCallback) {

        handlerCallback = downloadCallback;

        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            String parentDir = QZXTools.getExternalStorageForFiles(MyApplication.getInstance()
                    .getApplicationContext(), null) + File.separator;

            String name;
            if (TextUtils.isEmpty(fileName)) {
                name = url.substring(url.lastIndexOf("/") + 1);
            } else {
                name = fileName;
            }
            QZXTools.logE("parentDir=" + parentDir + ";name=" + name, null);
            File file = new File(parentDir, name);

            try {
                RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

                QZXTools.logE("file Length=" + file.length(), null);

                randomAccessFile.seek(file.length());

                Request request = new Request.Builder()
                        .url(url)
                        .addHeader("Range", "bytes=" + file.length() + "-")
                        .build();

                call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        if (call.isCanceled()) {
                            QZXTools.logE("取消请求..." + e, null);
                        } else {
                            QZXTools.logE("下载失败" + e, null);
                        }
                        mHandler.sendEmptyMessage(Fail);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        /**
                         * 检查服务端返回的响应头是否含有Content-Disposition=attachment;filename=FileName.txt
                         *
                         * 服务端返回的响应码是206，注意是206，这个很重要，只有206才能实现断点下载，表示本次返回的是部分响应体，并不是全部的数据
                         *
                         * 例如： Content-Disposition: attachment;filename=0ffd3bf46566469e850ab3e52e566797.mp4
                         * */
                        String headNeed = response.header("Content-Disposition");
                        if (!TextUtils.isEmpty(headNeed)) {
                            QZXTools.logE("headNeed=" + headNeed, null);
                        }

                        long contentLength = response.body().contentLength();

                        QZXTools.logE("this download contentLength=" + contentLength + ";reponse cod=" + response.code(), null);

                        byte[] bytes = new byte[1024];
                        int len = -1;
                        InputStream inputStream = response.body().byteStream();


                        long sum = file.length();

                        while ((len = inputStream.read(bytes)) != -1) {
                            randomAccessFile.write(bytes, 0, len);
                            sum += len;
                            int process = Math.round(sum * 1.0f / contentLength * 100);

                            Message message = mHandler.obtainMessage();
                            message.what = MsgProcess;
                            message.arg1 = process;
                            mHandler.sendMessage(message);

                            QZXTools.logE("sum=" + sum + ";len=" + len + ";percent=" +
                                    process + "%", null);

                        }

                        randomAccessFile.close();

                        Message message = mHandler.obtainMessage();
                        message.what = CommonComplete;
                        message.obj = file.getAbsolutePath();
                        mHandler.sendMessage(message);
                    }
                });
            } catch (FileNotFoundException e) {
                QZXTools.logE("FileNotFound exception...", null);
                e.printStackTrace();
            } catch (IOException e) {
                QZXTools.logE("exception...", null);
                e.printStackTrace();
            }
        }
    }

    /**
     * 取消断点下载请求
     */
    public void cancleDownloadMulti() {
        if (call != null) {
            call.cancel();
            call = null;
        }
    }
    //------------------------------------断点下载单个文件-----------------------------------------

    /**
     * 文件下载回调接口
     */
    public interface DownloadCallback {

        /**
         * 下载的进度
         */
        void downloadProcess(int value);

        /**
         * 下载完成
         */
        void downloadComplete(String filePath);

        /**
         * 下载失败
         */
        void downloadFailure();

    }


    /**
     * 重试拦截器
     */
    public class RetryIntercepter implements Interceptor {

        public int maxRetry;//最大重试次数
        private int retryNum = 0;//假如设置为3次重试的话，则最大可能请求4次（默认1次+3次重试）

        public RetryIntercepter(int maxRetry) {
            this.maxRetry = maxRetry;
        }

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();
           // request.newBuilder().addHeader("Connection", "close").build();
            System.out.println("retryNum=" + retryNum);
            Response response = chain.proceed(request);
            while (!response.isSuccessful() && retryNum < maxRetry) {
                retryNum++;
                System.out.println("retryNum=" + retryNum);
                response = chain.proceed(request);
            }
            return response;
        }


    }



}
