package com.telit.zhkt_three.customNetty;

import com.telit.zhkt_three.Utils.QZXTools;

import java.util.concurrent.TimeUnit;

import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;

/**
 * author: qzx
 * Date: 2019/4/28 9:22
 */
public class SimpleClientHandler extends SimpleChannelInboundHandler<String> {

    private SimpleClientListener simpleClientListener;

    private ChannelHandlerContext channelHandlerContext;
    private int reCount = 0;
    private boolean isUsabled = true;
    /**
     * 主动断开重连机制，用户退出
     */
    private volatile boolean autoClosed = false;


    public void setAutoClosed(boolean autoClosed) {
        this.autoClosed = autoClosed;
    }

    private volatile boolean isDestroy = false;

    public boolean isDestroy() {
        return isDestroy;
    }

    public void setDestroy(boolean destroy) {
        isDestroy = destroy;
    }

    public SimpleClientHandler(SimpleClientListener simpleClientListener) {
        super();
        this.simpleClientListener = simpleClientListener;
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent idleStateEvent = (IdleStateEvent) evt;
            QZXTools.logE( "userEventTriggered: "+idleStateEvent.state(),null);
            if (idleStateEvent.state() == IdleState.WRITER_IDLE) {
                //写超时,即设定的写时间内没有进行write操作会自动调用到这个方法中，主动write发送心跳方法给服务端
                SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_HEART, MsgUtils.heartMsg());
            } else if (idleStateEvent.state() == IdleState.READER_IDLE) {
                //读超时，即设定的读时间内没有read操作的话，表名服务端停止了，主动断开连接
                QZXTools.logE("超过90秒没有接收到服务端的信息，主动关闭", null);
                // ctx.channel().close();
                 ctx.channel().close();
                if (simpleClientListener != null) {
                    simpleClientListener.isNoUser();
                }
            }
        } else {
            super.userEventTriggered(ctx, evt);
        }
    }
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, String stringData) throws Exception {
        // Do something with msg
        QZXTools.logE("channelRead0 stringData=" + stringData, null);
        if (simpleClientListener != null) {
            simpleClientListener.receiveData(stringData);
            isUsabled = true;
        }
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        QZXTools.logE("channelActive Connected to: " + ctx.channel().remoteAddress(), null);

        this.channelHandlerContext = ctx;
        if (simpleClientListener != null) {
            simpleClientListener.onLine();
            reCount = 0;

        }
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        QZXTools.logE("channelInactive Disconnected from: " + ctx.channel().remoteAddress(), null);
        if (simpleClientListener != null) {
            simpleClientListener.offLine();
            //使用过程中断线重连
            final EventLoop eventLoop = ctx.channel().eventLoop();
            eventLoop.schedule(new Runnable() {
                @Override
                public void run() {
                    // imConnection.connect(ImClientApp.HOST, ImClientApp.PORT);
                    SimpleClientNetty.getInstance().reConnect();


                }
            }, 2, TimeUnit.SECONDS);
        }
    }
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        QZXTools.logE("channelUnregistered " + Thread.currentThread().getName() + ";"
                + Thread.currentThread().getId()
                + ";autoClosed=" + autoClosed + "我是在掉线重连设置重连的标记，就不应该加入班级", null);

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        QZXTools.logE("exceptionCaught", (Exception) cause);
        ctx.close();
    }

    public void sendMsg(String msgInfo) {
        if (channelHandlerContext != null) {
            QZXTools.logE("channelHandlerContext sendMsg 线程Name:" + Thread.currentThread().getName() + "........" + msgInfo, null);
            ChannelFuture channelFuture = channelHandlerContext.writeAndFlush(msgInfo);
            channelFuture.addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture channelFuture) throws Exception {
                    if (channelFuture.isSuccess()) {
                        //成功
//                        QZXTools.logE("发送Msg成功", null);
                    } else if (channelFuture.cause() != null) {
                        //失败 会调用channelUnregistered方法
                        QZXTools.logE("发送Msg失败", null);
                        channelFuture.channel().pipeline().fireChannelUnregistered();
                    }
                }
            });
        }
    }


}
