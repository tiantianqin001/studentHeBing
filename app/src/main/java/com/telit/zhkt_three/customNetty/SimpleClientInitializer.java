package com.telit.zhkt_three.customNetty;

import com.telit.zhkt_three.Constant.Constant;

import java.util.concurrent.TimeUnit;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * author: qzx
 * Date: 2019/4/28 9:14
 */
@Deprecated
public class SimpleClientInitializer extends ChannelInitializer {

    private SimpleClientListener simpleClientListener;

    public SimpleClientInitializer(SimpleClientListener simpleClientListener) {
        super();
        this.simpleClientListener = simpleClientListener;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {

        ChannelPipeline channelPipeline = channel.pipeline();

        // netty提供了多种解码器用于处理半包问题
        channelPipeline.addLast("frameDecoder", new LineBasedFrameDecoder(1024 * 1024));
        channelPipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));

        channelPipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

        channelPipeline.addLast("ping", new IdleStateHandler(Constant.READ_TIME_OUT,
                Constant.READ_TIME_OUT, Constant.READ_TIME_OUT, TimeUnit.SECONDS));

        channelPipeline.addLast("myHandler", new SimpleClientHandler(simpleClientListener));
    }
}
