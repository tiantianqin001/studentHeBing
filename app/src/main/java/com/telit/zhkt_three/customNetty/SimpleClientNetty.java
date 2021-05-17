package com.telit.zhkt_three.customNetty;

import android.text.TextUtils;

import com.telit.zhkt_three.Activity.InteractiveScreen.InteractiveActivity;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.FileLogUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.zbv.meeting.util.SharedPreferenceUtil;

import java.io.IOException;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LineBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.timeout.IdleStateHandler;
import io.netty.util.CharsetUtil;

/**
 * author: qzx
 * Date: 2019/4/28 9:01
 * //--------------------------
 * 修缮：Date：2019/11/06 14:32
 * 一、修改协议格式由：Command + " " + dataBody\r\n 转变为 Command + " " + SequenceId + " " + dataBody\r\n
 * 修改理由：SequenceId标志唯一消息标志，去重
 * 二、并发队列存储每一条发送的消息【不包括心跳消息以及客户端回执消息】，收到服务端回执Acknowledge后删除队列头
 * 三、重连间隔设置为0，即出错了马上重连，然后先发送队列所有消息，后发送正在进行的消息
 * 四、判断机器联网状态：遇到无法连接时，提示用户网络未连接【目前想在重连时判断】
 * 五、客户端每隔一定时间发送心跳包给服务端，服务端会回心跳信息但是不要管
 * 六、新增若干新字段：例如 PPTCommand 命令头
 * 七、新增服务端断了处理机制
 */
public class SimpleClientNetty implements InteractiveActivity.onCellNettyListener {

    private static volatile SimpleClientNetty instance;
    private EventLoopGroup workGroup1;


    private SimpleClientNetty() {

    }

    public static SimpleClientNetty getInstance() {
        if (instance == null) {
            synchronized (SimpleClientNetty.class) {
                if (instance == null) {
                    instance = new SimpleClientNetty();
                }
            }
        }
        return instance;
    }

    private SimpleClientListener simpleClientListener;

    public void setSimpleClientListener(SimpleClientListener simpleClientListener) {
        this.simpleClientListener = simpleClientListener;
    }

    public void recyclerAll() {
        instance = null;
    }

    private SimpleClientHandler simpleClientHandler;

    public SimpleClientHandler getSimpleClientHandler() {
        return simpleClientHandler;
    }


    /**
     * netty默认是30秒，这里改成5秒连接超时
     */
    public SimpleClientNetty init(String host, int port) {

        try {
            workGroup1 = new NioEventLoopGroup(4);
            bootstrap = new Bootstrap();
            bootstrap.group(workGroup1)
                    //.remoteAddress("192.16.5.98",8004 )
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .option(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                    .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 10000)
                    // 设置禁用nagle算法
                    .option(ChannelOption.TCP_NODELAY, true)
                    // 设置该选项以后，如果在两小时内没有数据的通信时，TCP会自动发送一个活动探测数据报文
                    .option(ChannelOption.SO_KEEPALIVE, true)
                    .handler(new ChannelInitializer<Channel>() {
                        @Override
                        protected void initChannel(Channel channel) throws Exception {
                            ChannelPipeline channelPipeline = channel.pipeline();
                            //一秒接收不到写操作则发送心跳
                            channelPipeline.addLast("heart", new IdleStateHandler(90,
                                    10, 0, TimeUnit.SECONDS));

                            // netty提供了多种解码器用于处理半包问题
                            channelPipeline.addLast("frameDecoder", new LineBasedFrameDecoder(1024 * 1024));
                            channelPipeline.addLast("stringDecoder", new StringDecoder(CharsetUtil.UTF_8));
                            channelPipeline.addLast("stringEncoder", new StringEncoder(CharsetUtil.UTF_8));

                            //添加日志
                            channelPipeline.addLast(new LoggingHandler(LogLevel.INFO));
                            //添加重连的adapter
                            // channelPipeline.addLast(new ReconnectHandler());
                            simpleClientHandler = new SimpleClientHandler(simpleClientListener);
                            channelPipeline.addLast("myHandler", simpleClientHandler);


                        }
                    });

            channel = bootstrap.connect(host, port).sync().channel();


              //重连
           /* if (channel == null && executorService != null) {
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        while (isNetUser()) {
                            try {
                                Thread.sleep(10000);
                              //  reConnect();
                                if (channel != null) {
                                    break;
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }

                        }
                    }
                });
            }*/
        } catch (Exception e) {
            e.printStackTrace();
            if (channel != null) {
                channel.close();
                channel = null;
            }
            //教师端关闭了
            if (simpleClientListener != null && e instanceof IOException) {
                simpleClientListener.isNoUser();
            }



        }

        return instance;
    }

    private Bootstrap bootstrap;
    private Channel channel;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();
    public synchronized void connectAsyncTwo() {


        fileLogUtils.saveLogs("connectAsyncTwo，现在就是重连");
        //异步 同步需要加sync()
        String socketIp = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("socketIp");
        int port = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getInt("port");
        if (channel != null && channel.isActive()) {
            if (executorService != null) {
                executorService.shutdownNow();
            }
            return;
        }

        init(socketIp,port);
    }

    /**
     * 主动关闭整个工作
     */
    public void shutdownAllWork() {
        if (channel != null) {
            if (bootstrap != null) {
                try {
                    channel.eventLoop().shutdownGracefully();
                    bootstrap.group().shutdownGracefully();
                    bootstrap = null;
                    // channel.close();

                    if (workGroup1 != null) {
                        workGroup1.shutdownGracefully();
                        workGroup1 = null;
                    }

                    QZXTools.logE("shutdownGracefully", null);
                } catch (Exception e) {
                    fileLogUtils.saveLogs("sent msg ===> " + "我是shutdownAllWork" + e.getMessage());
                }
            }
        }
    }
    /**
     * 断线重连,如果执行init操作的话，NioEventLoopGroup多次创建导致OOM
     */
    public void reConnect() {
        //先关闭
        shutdownAllWork();
        disconnectSocket();
        //如果连接成功就跳出循环
        fileLogUtils.saveLogs("sent msg ===> " + "我是TimerTask 中的断线重连我是执行了断线重连");
        QZXTools.logE("sent msg ===> \" + \"我是TimerTask 中的断线重连我是执行了断线重连 ", null);
        SimpleClientNetty.getInstance().connectAsyncTwo();

        QZXTools.logD("sent msg ===>" + "....reConnect");
    }
    /**
     * 主动断开连接
     */
    public void disconnectSocket() {
        try {
            if (channel != null) {
                if (channel.pipeline().get("heart") != null) {
                    channel.pipeline().remove("heart");
                }
                if (channel.pipeline().get("frameDecoder") != null) {
                    channel.pipeline().remove("frameDecoder");
                }
                if (channel.pipeline().get("stringDecoder") != null) {
                    channel.pipeline().remove("stringDecoder");
                }
                if (channel.pipeline().get("stringEncoder") != null) {
                    channel.pipeline().remove("stringEncoder");
                }
                if (channel.pipeline().get("myHandler") != null) {
                    channel.pipeline().remove("myHandler");
                }
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            fileLogUtils.saveLogs("sent msg ===> " + "我是disconnectSocket" + e.getMessage());
        } finally {
            if (channel != null) {
                try {
                    channel.close();
                    channel = null;
                } catch (Exception e) {

                }

            }
        }

    }

    /**
     * 连接是否连接中
     */
    public boolean isConnected() {
        if (channel != null) {
            return channel.isActive();
        } else {
            return false;
        }
    }

    /**
     * 是否断线重连
     */
    private volatile boolean isReconnected = false;

    public boolean isReconnected() {
        return isReconnected;
    }

    public void setReconnected(boolean reconnected) {
        isReconnected = reconnected;
    }

    private ConcurrentLinkedQueue<String> concurrentLinkedQueue = new ConcurrentLinkedQueue<String>();

    public ConcurrentLinkedQueue<String> getConcurrentLinkedQueue() {
        return concurrentLinkedQueue;
    }


    private FileLogUtils fileLogUtils = FileLogUtils.getInstance();

    /**
     * 发送消息给服务端
     * 发送格式为：MsgUtils.Command + MsgUtils.GeneratedMethod
     * 例如：心跳  SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_HEART, MsgUtils.heartMsg());
     */
    public void sendMsgToServer(String head, String message) {
        if (simpleClientHandler != null && !TextUtils.isEmpty(message)) {
            //如果处于断线状态将消息添加至队列,否则如果是断线重连成功则把队列数据一次性发送
            QZXTools.logE(";SimpleClientNetty sendMsgToServer 线程Name:" + Thread.currentThread().getName()
                    + ";And message=" + message
                    + ";And isConnected()=" + isConnected(), null);
            if (!isConnected()) {
                //------------------剔除心跳消息和回执消息
                if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                    concurrentLinkedQueue.offer(message);
                }
            } else {
                //发送的消息记录 ============================================================》
                if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                    fileLogUtils.saveLogs("sent msg ===> " + message);
                }

                if (isReconnected) {
                    //setReconnected(false);
                    //先发送队列的消息
                    for (String q : concurrentLinkedQueue) {
                        QZXTools.logE("重发queue:" + q, null);
                        simpleClientHandler.sendMsg(q);
                    }
                }

                //然后把当前消息加入队列，TCP协议保证先发先收到的机制？
                // 剔除心跳消息,和客户端回执消息
                if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                    concurrentLinkedQueue.offer(message);
                }

                //再次确认是否处于连接状态,发送当前消息
                if (isConnected()) {
                    simpleClientHandler.sendMsg(message);
                }
            }
        }
    }


    @Override
    public void stopNetty(boolean nettyClose) {
        //先关闭
        if (nettyClose) {
            shutdownAllWork();
            disconnectSocket();

            if (executorService != null) {
                executorService.shutdownNow();
                executorService = null;
            }

        }

    }

    private boolean isNet = false;

    public boolean isNetUser() {
        //判断是否联网

        try {
            Process process = Runtime.getRuntime().exec("/system/bin/ping -c 1 -w 5 www.baid.com");
            int status = process.waitFor();
            if (status == 0) {
                //网络可用
                isNet = true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return isNet;
    }


}
