package com.telit.zhkt_three.ScreenLive;

/**
 * Created by gavin on 2017/12/30.
 */

public interface EasyVideoStreamCallback {
    void videoDataBack(long timestamp, byte[] pBuffer, int offset, int length);
}
