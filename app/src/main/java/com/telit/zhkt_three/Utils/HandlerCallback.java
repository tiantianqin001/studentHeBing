package com.telit.zhkt_three.Utils;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * author: qzx
 * Date: 2019/8/23 8:45
 * <p>
 * 对OkHttp3.0封装,一般依据服务端返回值封装多余的信息后得到需要的实体类，然后就是对于错误的处理提示
 */
public class HandlerCallback implements Callback {
    @Override
    public void onFailure(Call call, IOException e) {

    }

    @Override
    public void onResponse(Call call, Response response) throws IOException {

    }
}
