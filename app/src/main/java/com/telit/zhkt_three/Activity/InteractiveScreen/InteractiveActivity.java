package com.telit.zhkt_three.Activity.InteractiveScreen;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.media.projection.MediaProjectionManager;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.gyf.immersionbar.ImmersionBar;
import com.hjq.toast.ToastUtils;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Activity.HomeScreen.MainActivity;
import com.telit.zhkt_three.Activity.HomeWork.ExtraInfoBean;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.CustomView.CircleImageView;
import com.telit.zhkt_three.CustomView.QuestionView.SubjectiveToDoView;
import com.telit.zhkt_three.Fragment.Dialog.CameraAlbumPopupFragment;
import com.telit.zhkt_three.Fragment.Dialog.CollectDisplayDialog;
import com.telit.zhkt_three.Fragment.Dialog.FileReceiveDialog;
import com.telit.zhkt_three.Fragment.Dialog.PraiseAndCriticismDialog;
import com.telit.zhkt_three.Fragment.Dialog.RandomNameDialog;
import com.telit.zhkt_three.Fragment.Dialog.ReceiveFilesDialog;
import com.telit.zhkt_three.Fragment.Dialog.ScreenShotImgDialog;
import com.telit.zhkt_three.Fragment.Dialog.TipsDialog;
import com.telit.zhkt_three.Fragment.Interactive.AskQueestionFragment;
import com.telit.zhkt_three.Fragment.Interactive.FFmpegFragment;
import com.telit.zhkt_three.Fragment.Interactive.FreeSelectDiscussGroupAnstarFragment;
import com.telit.zhkt_three.Fragment.Interactive.FreeSelectDiscussGroupFragment;
import com.telit.zhkt_three.Fragment.Interactive.GroupDiscussFragment;
import com.telit.zhkt_three.Fragment.Interactive.LockFragment;
import com.telit.zhkt_three.Fragment.Interactive.NewWhiteBoardFragment;
import com.telit.zhkt_three.Fragment.Interactive.PlayingRtspFragment;
import com.telit.zhkt_three.Fragment.Interactive.QuestionFragment;
import com.telit.zhkt_three.Fragment.Interactive.QuestionOnlyPicFragment;
import com.telit.zhkt_three.Fragment.Interactive.ResponderFragment;
import com.telit.zhkt_three.Fragment.Interactive.TeacherShotFragment;
import com.telit.zhkt_three.Fragment.Interactive.VoteFragment;
import com.telit.zhkt_three.Fragment.Interactive.WebViewFragment;
import com.telit.zhkt_three.Fragment.Interactive.WhitBoardPushFragment;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.RtspDisplayPush.DisplayService;
import com.telit.zhkt_three.ScreenLive.PusherContract;
import com.telit.zhkt_three.Service.ScreenRecordService;
import com.telit.zhkt_three.Service.ScreenShotService;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.FileLogUtils;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.ScreenUtils;
import com.telit.zhkt_three.Utils.UriTool;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;
import com.telit.zhkt_three.customNetty.MsgUtils;
import com.telit.zhkt_three.customNetty.SimpleClientListener;
import com.telit.zhkt_three.customNetty.SimpleClientNetty;
import com.telit.zhkt_three.dialoge.CustomDialog;
import com.telit.zhkt_three.floatingview.PopWindows;
import com.telit.zhkt_three.greendao.StudentInfoDao;
import com.telit.zhkt_three.receiver.NotificationBroadcastReceiver;
import com.zbv.basemodel.LingChuangUtils;
import com.zbv.meeting.util.SharedPreferenceUtil;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 一、加入班级成功后就已经处于上课状态了
 * <p>
 * 添加反馈机制？---不需要
 * 初始连接大概率会离线一次？
 * 处理粘包拆包
 * 处理消息累积？例如掉线后重连的累计
 * <p>
 * mediaprojection截屏要另开线程handler
 * <p>
 * 消息格式：CommandWord + " " + UUID + " "+jsonStr+"\r\n"
 * <p>
 * 目前用到的消息命令：
 * 加入班级【JoinClass】
 * 锁屏 【LockScreen 】
 * 解屏 【UnlockScreen】
 * 关机 【ShutdownSdtDevice】
 * 消息反馈 【Acknowledgement】
 * 抢答 【FirstAnswer】
 * 截屏分享 【ShareScreenshot】
 * 点赞 【Praise】
 * 批评 【Criticism】
 * 屏幕广播 【Screenbroadcast】
 * 停止屏幕广播 【StopScreenbroadcast】
 * 投屏讲解 【ScreenCast】
 * 停止投屏讲解 【StopScreenCast】
 * 开始投票 【StartVote】
 * 结束投票 【EndVote】
 * 开始分组讨论 【StartDiscuss】
 * 结束分组讨论 【EndDiscuss】
 * 发送讨论内容 【Discuss】
 * PPT互动 【PPTCommand】
 * <p>
 * 保证断线重连或者重新进入能进入最新的活动页面中:如果期间没有其他活动指令取本地保存的活动页面，反之取离线期间的指令
 * todo 确保Activity在前台下直接显示指令活动，否则发送通知提醒，条件是依然处于在线状态
 */
public class InteractiveActivity extends BaseActivity implements View.OnClickListener,
        SimpleClientListener, SurfaceHolder.Callback, PusherContract.View {
    private static final String TAG = "InteractiveActivity";
    private Unbinder unbinder;
    @BindView(R.id.board_avatar)
    CircleImageView avatar;
    @BindView(R.id.board_name)
    TextView board_name;
    @BindView(R.id.board_clazz)
    TextView board_clazz;
    @BindView(R.id.board_status)
    TextView board_status;
    @BindView(R.id.board_wifi)
    ImageView board_wifi;
    @BindView(R.id.board_more)
    ImageView board_more;
    @BindView(R.id.board_rl_head)
    RelativeLayout board_rl_head;

    //tv_address_ip
    @BindView(R.id.tv_address_ip)
    TextView tv_address_ip;
    //tv_wifi_name
    @BindView(R.id.tv_wifi_name)
    TextView tv_wifi_name;


    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.iv_home)
    ImageView iv_home;

    private static final int Media_Projection_Shot_RequestCode = 11;
    private static final int Media_Projection_Record_RequestCode = 12;

    static public Intent mResultIntent;
    static public int mResultCode;

    private MyHandler mHandler;

    //引入接收消息的Map
    private Map<String, String> receiveMsgMap;
    private List<String> receiveMsgKeyList;


    //----------------------NEW整理-----------------------------
    private SharedPreferences sp_last_msg;
    private File videoFile_jieping;
    private RandomNameDialog randomNameDialog;
    private LockFragment lockFragment;
    private CustomDialog customDialog;
    private QuestionOnlyPicFragment questionOnlyPicFragment;
    private onCellNettyListener listener;
    private String sp_msg;
    private NotificationManager notificationManager;
    private String receiveHead;
    private PopWindows popWindows;
    private ImageView iv_san_suo;
    private boolean isClock = true;
    private FreeSelectDiscussGroupAnstarFragment freeSelectDiscussGroupAnstarFragment;
    // HEAD_END_CLASS 结束上课清空,所以教师下课要发送该指令给学生端

    private class MyHandler extends Handler {
        private WeakReference<InteractiveActivity> activity;

        public MyHandler(InteractiveActivity testActivity) {
            super(Looper.getMainLooper());
            this.activity = new WeakReference<InteractiveActivity>(testActivity);
        }

        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void handleMessage(Message msg) {
//            QZXTools.logE("handler thread=" + Thread.currentThread() + ";msg=" + msg, null);
            super.handleMessage(msg);
            InteractiveActivity context = activity.get();
            if (null == context) {
                return;
            }
            switch (msg.what) {
                case Constant.OnLine:
                    if (context.board_status != null) {
                        context.board_status.setText("在线");
                        //只有再不是掉线重连的时候才加入班级
                        Log.i(TAG, "sendJoinClass: ");
                        context.sendJoinClass();
                    }
                    break;
                case Constant.OffLine:
                    //因为会出现界面关闭，这个handler还在执行的情况，空指针
                    SimpleClientNetty.getInstance().setReconnected(true);
                    boolean netAvail = QZXTools.isNetworkAvailable();
                    if (!netAvail) {
                        if (context.board_status != null) {
                            context.board_status.setText("断网");
                            //这个时候要进入搜索班级界面
                            finish();
                            Intent intent1 = new Intent(InteractiveActivity.this, SelectClassActivity.class);
                            startActivity(intent1);

                        }
//                        QZXTools.popToast(context, "断线因为网络断了，请检查网络！", false);
                    } else {
//                        QZXTools.popToast(context, (String) msg.obj, false);
                        if (context.board_status != null) {
                            context.board_status.setText("离线");
                            //这种状态教师端已经关闭了不能连接了
                        }
                    }

                    break;
                case Constant.isNotUser:
                    finish();
                    Intent intent1 = new Intent(InteractiveActivity.this, SelectClassActivity.class);
                    startActivity(intent1);

                    break;

                case Constant.ReceiveMessage:
                    if (context.isDestroy) {
                        return;
                    }
                    //引入LinkedMap去重，key是uuid,value是msg
                    if (context.receiveMsgKeyList == null) {
                        context.receiveMsgKeyList = new LinkedList<>();
                    }

                    if (context.receiveMsgMap == null) {
                        context.receiveMsgMap = new LinkedHashMap<>();
                    }

                    String stringData = (String) msg.obj;
                    //如果没有收到内容
                    if (TextUtils.isEmpty(stringData)) return;
                    String[] splitString = stringData.trim().split(MsgUtils.SEPARATOR);
                    String head = splitString[0];
                    receiveHead = head;
                    //如果没有发送头指令就结束
                    if (TextUtils.isEmpty(head) || head.equals(MsgUtils.HEAD_HEART) || splitString.length <= 1)
                        return;
                    String seqId = splitString[1];
                    String body = stringData.substring(head.length() + seqId.length() + 1).trim();

                    QZXTools.logD("receiveMsg ===> stringData=" + stringData.trim()
                            + ";head=" + head + ";seqId=" + seqId + ";body=" + body, null);

                    if (!TextUtils.isEmpty(head)) {

                        //收到的消息写入消息记录=发心跳======================================================》
                        if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                            if (context.fileLogUtils != null) {
                                context.fileLogUtils.saveLogs("received msg ===> " + stringData);
                            }
                            /**
                             * 保存最新一条信息给SP,重连或者再次进入互动界面恢复之前的操作活动界面
                             *  如果是提问 提问没有结束 就还显示提问 ，如果提问结束就进入白板不保留状态
                             * */
                            //context.sp_last_msg.edit().putString("Last_Msg", stringData).commit();
                            //不再记录最后一个状态
                            QZXTools.logD("jieshouInfo              " + stringData.trim() +
                                    ";head=" + head + ";seqId=" + seqId + ";body=" + body, null);

                            //只有在不锁屏和截屏 ，不关闭广播，不关闭投票，不关闭分组 ,不关闭截屏,不关闭随机点名
                            // 等我才保留最后一个状态  屏幕广播
                            //点击了屏幕广播 没有结束
                       /*     if (!head.equals(MsgUtils.HEAD_LOCK) && !head.equals(MsgUtils.HEAD_UNLOCK)) {
                                if (head.equals(MsgUtils.HEAD_BROADCAST)
                                        || head.equals(MsgUtils.HEAD_FREE_DISCUSS)
                                        || head.equals(MsgUtils.HEAD_SHARE_SHOT)) {
                                    context.sp_last_msg.edit().putString("Last_Msg", stringData).commit();

                                } else {
                                    context.sp_last_msg.edit().clear().commit();
                                }

                            }*/
                            //启动返回键 领创的管控
                            LingChuangUtils.getInstance().startBack(MyApplication.getInstance());
                        }
                        //判断服务器是否返回心跳，判断是心跳则发送心跳数据保活
                        if (MsgUtils.HEAD_HEART.equals(head)) {
                            QZXTools.logD("---接收到服务端的心跳信息---" + stringData);
                        } else if (MsgUtils.HEAD_JOIN_CLASS_SUCCESS.equals(head)) {
                            //加入班级成功
                            context.popToastInfo("成功加入班级...");
                        } else if (MsgUtils.HEAD_RECONNECT_SUCCESS.equals(head)) {
                            //重连成功
                            context.popToastInfo("重连成功啦...");
                        } else if (MsgUtils.HEAD_PRAISE.equals(head)) {
                            //收到表扬
                            context.receivePraiseOrCriticism(true, body);
                        } else if (MsgUtils.HEAD_CRITICISM.equals(head)) {
                            //收到批评
                            context.receivePraiseOrCriticism(false, "");
                        } else if (MsgUtils.HEAD_START_CLASS.equals(head)) {
                            //开始上课
                            context.popToastInfo("开始上课啦...");
                            context.enterWhiteBoard(MsgUtils.HEAD_START_CLASS);
                        } else if (MsgUtils.HEAD_END_CLASS.equals(head)) {
                            //结束上课
                            context.popToastInfo("结束上课啦...");
                        } else if (MsgUtils.HEAD_LOCK.equals(head)) {
                            //锁屏
                            context.enterLock();
                            isClock = true;
                            BuriedPointUtils.buriedPoint("2012", "", "", "", "");
                        } else if (MsgUtils.HEAD_UNLOCK.equals(head)) {
                            //解屏即进入白板和上课一样一样地
                            //context.enterWhiteBoard(MsgUtils.HEAD_UNLOCK);
                            if (customDialog != null && customDialog.isShowing()) {
                                customDialog.dismiss();
                            }

                            BuriedPointUtils.buriedPoint("2013", "", "", "", "");
                            isClock = false;
                        } else if (MsgUtils.HEAD_FIRST_ANSWER.equals(head)) {
                            //开始抢答
                            context.startResponder();
                            //抢答的埋点
                            BuriedPointUtils.buriedPoint("2006", "", "", "", "");
                        } else if (MsgUtils.HEAD_SUCCESS_ANSWER.equals(head)) {
                            //SuccessAnswer ca153fb835ca4a0b899dc180b12e696a 张青雪18
                            context.showResponder(body);
                        } else if (MsgUtils.HEAD_END_ANSWER.equals(head)) {
                            //结束抢答
                            context.enterWhiteBoard(MsgUtils.HEAD_END_ANSWER);
                        } else if (MsgUtils.HEAD_RANDOM_NAME.equals(head)) {
                            //SuccessRoleCall e32f20d6cd4d409b975668aadb9df405 张青雪48
                            //随机点名
                            context.randomName(body);
                        } else if (MsgUtils.HEAD_FILERECIEVE.equals(head)) {
                            //文件接收
                            context.judgeFileReceivePermission(body);
                        } else if (MsgUtils.HEAD_START_VOTE.equals(head)) {
                            //开始投票
                            context.startVote(body);
                            //投票埋点
                            BuriedPointUtils.buriedPoint("2008", "", "", "", "");
                        } else if (MsgUtils.HEAD_END_VOTE.equals(head)) {
                            //结束投票
                            // context.popToastInfo("投票结束");
                            context.endVote();
                        } else if (MsgUtils.HEAD_START_PRACTICE.equals(head)) {
                            //开始随堂练习
                            context.startPractice(body);
                        } else if (MsgUtils.HEAD_END_PRACTICE.equals(head)) {
                            //结束随堂练习
                            context.endPractice();
                        } else if (MsgUtils.HEAD_START_DISCUSS.equals(head)) {
                            //开启分组讨论
                            context.startDiscuss(body);
                        } else if (MsgUtils.HEAD_END_DISCUSS.equals(head)) {
                            //结束分组讨论
                            context.endDiscuss();
                        } else if (MsgUtils.HEAD_FREE_DISCUSS.equals(head)) {
                            //自由分组的命令
                            context.toFreeDiscuss(body);
                            //分组埋点
                            BuriedPointUtils.buriedPoint("2007", "", "", "", "");
                        } else if (MsgUtils.HEAD_DISCUSS.equals(head)) {
                            //接收到讨论的信息
                            EventBus.getDefault().post(body.trim(), Constant.Discuss_Message);
                        } else if (MsgUtils.HEAD_ACKNOWLEDGE.equals(head)) {
                            //收到服务端的回执,删除队列中的头
                            if (!SimpleClientNetty.getInstance().getConcurrentLinkedQueue().isEmpty()) {
                                SimpleClientNetty.getInstance().getConcurrentLinkedQueue().remove();
                            }
                        } else if (MsgUtils.HEAD_PPT_COMMAND.equals(head)) {
                            //ppt指令
//                            QZXTools.logE("ppt command body=" + body, null);
                            String[] splitStrs = body.split(" ");
                            QZXTools.logE("qin1223......" + splitStrs[0] + "...." + splitString[1], null);
                            if (splitStrs.length <= 1) {
                                QZXTools.popToast(context, "你传的ppt不合法", true);
                                return;
                            }
                            //发送ppt
                            context.enterPPTCommand(splitStrs[0], splitStrs[1]);
                        } else if (MsgUtils.HEAD_BROADCAST.equals(head)) {
                            //开启广播
                            context.startBroadcast(body);
                            BuriedPointUtils.buriedPoint("2011", "", "", "", "");
                        } else if (MsgUtils.HEAD_STOP_BROADCAST.equals(head)) {
                            //停止广播
                            context.stopBroadcast();
                            //context.enterWhiteBoard();
                        } else if (MsgUtils.HEAD_SCREEN_CAST.equals(head)) {
                            //开始投屏
                            context.startStudnetCast(body);
                        } else if (MsgUtils.HEAD_STOP_SCREEN_CAST.equals(head)) {
                            //结束投屏
                            context.stopStudnetCast();
                        } else if (MsgUtils.HEAD_SHUTDOWN.equals(head)) {
                            //关机
                            context.receiveShutdown();
                        } else if (MsgUtils.HEAD_SHARE_SHOT.equals(head)) {
                            //截屏分享，图片的Url地址
                            context.showTeacherShot(body, false);
                            BuriedPointUtils.buriedPoint("2010", "", "", "", "");
                        } else if (MsgUtils.HEAD_FOCUS_SHARE.equals(head)) {
                            context.showTeacherShot(body, true);
                            //发布提问
                        } else if (MsgUtils.HEAD_START_ANSWERE.equals(head)) {
                            Log.i("qin0509", "handleMessage: ");
                            context.answerQuestion(body);
                            //这里主要完成的是横屏推流
                            //提问埋点
                            BuriedPointUtils.buriedPoint("2005", "", "", "", "");
                        } else if (MsgUtils.HEAD_TUI_LIU_TEACHER.equals(head)) {
                            context.tuiLliuTeacher();
                        } else if (MsgUtils.HEAD_WHILD_BOARDPUSH.equals(head)) {
                            //白班的推送
                            context.WhiteboardPush(body);
                        } else if (MsgUtils.HEAD_EndQuestion.equals(head)) {
                            //教师端结束答题，不能提交了学生答题也要结束
                            // EventBus.getDefault().post("questEnd",Constant.Homework_Commit_end);
                            //进入白板
                            enterWhiteBoard("");
                        } else if (MsgUtils.HEAD_StudentPadScreenCast.equals(head)) {
                            //学生接收到教师端发送的消息要开始推流

                            if (!DisplayService.Companion.isStreaming()) {
                                startActivityForResult(DisplayService.Companion.sendIntent(), REQUEST_CODE_STREAM_RTSP);
                                BuriedPointUtils.buriedPoint("2014", "", "", "", "");


                                // LingChuangUtils.getInstance().startHome(MyApplication.getInstance());
                                //同时要放开home 建
                                SharedPreferenceUtil.getInstance(MyApplication.getInstance())
                                        .setBoolean("openHome", true);

                                enterWhiteBoard("");
                                if (popWindows == null) {
                                    popWindows = new PopWindows(getApplication());
                                    popWindows.setView(R.layout.popwindoeview)
                                            .setGravity(Gravity.LEFT | Gravity.TOP)
                                            .setYOffset(100)
                                            .show();
                                    iv_san_suo = (ImageView) popWindows.findViewById(R.id.iv_san_suo);
                                }
                                showReadTime();
                                //解屏
                                if (customDialog != null && customDialog.isShowing()) {
                                    customDialog.dismiss();
                                }

                            }

                        } else if (MsgUtils.HEAD_StopStudentScreenCast.equals(head)) {
                            //教师端结束推流
                            stopService(new Intent(InteractiveActivity.this, DisplayService.class));
                            if (popWindows != null) popWindows.cancel();
                            popWindows = null;
                        } else if (MsgUtils.HEAD_ANSWER_FENZHU_START.equals(head)) {
                            //提问分组讨论
                            context.toFreeDiscussAnswear(body);

                        }

                        //客户端消息收到的反馈,服务端的心跳包不要回执,服务端回执也不需要回执
                        if (!head.equals(MsgUtils.HEAD_HEART) && !head.equals(MsgUtils.HEAD_ACKNOWLEDGE)) {
                            SimpleClientNetty.getInstance().sendMsgToServer
                                    (MsgUtils.HEAD_ACKNOWLEDGE, MsgUtils.createAcknowledge(seqId));
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 显示录制的时间
     */
    private Handler timeHandler = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case TIMES_SEND:
                    if (times % 2 == 0) {
                        iv_san_suo.setBackgroundColor(getResources().getColor(R.color.transGray));
                    } else {
                        iv_san_suo.setBackgroundColor(getResources().getColor(R.color.colorDarkBlue));
                    }
                    break;
            }
        }
    };
    private static final int TIMES_SEND = 0X100;
    private Timer timer;
    int times = 0;

    private void showReadTime() {
        if (timer == null) {

            timer = new Timer();
        }
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // System.out.println("系统正在运行……");
                times++;
                //发送到主线程
                timeHandler.sendEmptyMessage(TIMES_SEND);

            }
        }, 100, 100);
        /*当启动定时器后，5s之后开始每隔2s执行一次定时器任务*/
    }

    private PusherContract.Presenter presenter;
    static public SurfaceView mSurfaceView;

    //todo  默认要先启动服务   CapScreenService
    private void tuiLliuTeacher() {
        //同屏把学生端的信息发送给老师
        mSurfaceView.getHolder().addCallback(this);
        if (presenter != null) {
            presenter.onStartPush(this, 0,
                    new Intent(),
                    -1,
                    mSurfaceView);
        }
    }

    private void robotWifi() {
        WifiManager mWifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        mWifiManager.setWifiEnabled(false);
        mWifiManager.setWifiEnabled(true);
    }

    private MediaProjectionManager projectionManager;
    private FileLogUtils fileLogUtils;
    private boolean isFileLog = false;
    private Handler WifiHandler = new Handler();
    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            tv_wifi_name.setText(getWIFIName(InteractiveActivity.this));
            WifiHandler.postDelayed(this, 5000);

            //本机IP
            String ownIP = QZXTools.getIPAddress();
            tv_address_ip.setText("加入课堂" + ownIP);
        }
    };

    @BindView(R.id.rl_navigationBar)
    RelativeLayout rl_navigationBar;

    @BindView(R.id.rl_interactive)
    RelativeLayout rl_interactive;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        //QZXTools.logE("本机IP = " + ownIP, null);
        //QZXTools.popCommonToast(this, "本机Ip = " + ownIP, false);

        setContentView(R.layout.interactive_board);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);
        sp_last_msg = getSharedPreferences("sp_last_msg", MODE_PRIVATE);
        //显示wifi 的名称
        WifiHandler.postDelayed(runnable, 200);

        EventBus.getDefault().register(this);

        ScreenUtils.setNavigationListener(rl_interactive, new ScreenUtils.NavigationListener() {
            @Override
            public void show() {
                QZXTools.logE("底部导航栏显示", null);
                rl_navigationBar.setVisibility(View.GONE);
            }

            @Override
            public void hide() {
                QZXTools.logE("底部导航栏隐藏", null);
                rl_navigationBar.setVisibility(View.VISIBLE);
            }
        });

       // LingChuangUtils.getInstance().startHome(MyApplication.getInstance());

        //保持屏幕常亮，也可以再布局文件顶层：android:keepScreenOn="true"
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
//        //开启连接服务
//        Intent serverIntent = new Intent(this, SimpleSocketLinkServerTwo.class);
//        serverIntent.setAction(Constant.SOCKET_CONNECT_ACTION);
//        serverIntent.setPackage(getPackageName());
//        //如果安卓o,api26（8.0）
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
////            startForegroundService(serverIntent);
//            SimpleSocketLinkServerTwo.enqueueWork(this, serverIntent);
//        } else {
//            QZXTools.logE("Thread=" + Thread.currentThread().getName(), null);
//            startService(serverIntent);
//        }


//        //todo 连接服务会阻塞主线程
//        SimpleClientNetty.getInstance().init(UrlUtils.SocketIp, UrlUtils.SocketPort).connectAsync();
        // 目前先结束一下
        stopService(new Intent(InteractiveActivity.this, DisplayService.class));

        board_wifi.setOnClickListener(this);
        board_more.setOnClickListener(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            projectionManager = (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        }
        setonCellNettyListener(SimpleClientNetty.getInstance());


        //初始化服务 这个是学生端推流
        DisplayService.Companion.init(this);

        popWindows = new PopWindows(getApplication());
        popWindows.setView(R.layout.popwindoeview)
                .setGravity(Gravity.LEFT | Gravity.TOP)
                .setYOffset(100)
                .show();
        iv_san_suo = (ImageView) popWindows.findViewById(R.id.iv_san_suo);
        showReadTime();
        registerHomeKeyReceiver(this);

        iv_back.setOnClickListener(this);
        iv_home.setOnClickListener(this);

    }


    @androidx.annotation.RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private ScheduledExecutorService messageExecutorService;

    @Override
    protected void onStart() {
        super.onStart();
        //点击了提问的主题作业
        if (isSubjective) {
            return;
        }

        //通用的头布局信息展示
     /*   StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
        StudentInfo studentInfo = studentInfoDao.queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();
        if (studentInfo != null) {
            board_name.setText(studentInfo.getStudentName());
            if (studentInfo.getClassName() != null) {
                if (studentInfo.getGradeName() != null) {
                    board_clazz.setText(studentInfo.getGradeName().concat(studentInfo.getClassName()));
                } else {
                    board_clazz.setText(studentInfo.getClassName());
                }
            }
            if (studentInfo.getPhoto() == null) {
                avatar.setImageResource(R.mipmap.icon_user);
            } else {
                Glide.with(this).load(studentInfo.getPhoto()).
                        placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(avatar);
            }
        }*/
        board_name.setText(UserUtils.getStudentName());
        board_clazz.setText(UserUtils.getClassName());
        Glide.with(this).load(UserUtils.getAvatarUrl()).
                placeholder(R.mipmap.icon_user).error(R.mipmap.icon_user).into(avatar);


        //引入log文件
        //  ZBVPermission.getInstance().setPermPassResult(this);

        if (!ZBVPermission.getInstance().hadPermissions(this, WriteReadPermissions)) {
            isFileLog = true;
            ZBVPermission.getInstance().requestPermissions(this, WriteReadPermissions);
        } else {
            fileLogUtils = FileLogUtils.getInstance();
        }

        if (messageExecutorService == null) {
            messageExecutorService = Executors.newSingleThreadScheduledExecutor();

        }
        messageExecutorService.execute(new Runnable() {
            @Override
            public void run() {
                //Handler
                mHandler = new MyHandler(InteractiveActivity.this);

                //通讯服务接口
                SimpleClientNetty.getInstance().setSimpleClientListener(InteractiveActivity.this);

                String path = QZXTools.getExternalStorageForFiles(InteractiveActivity.this, null) + "/config.txt";
                Properties properties = QZXTools.getConfigProperties(path);
                String socketIp = properties.getProperty("socketIp");
                String socketPort = properties.getProperty("SocketPort");
                if (TextUtils.isEmpty(socketIp)) {
                    socketIp = UrlUtils.SocketIp;
                }
                if (TextUtils.isEmpty(socketPort)) {
                    socketPort = UrlUtils.SocketPort + "";
                }
                int port = Integer.valueOf(socketPort);

                //保存连接的ip和端口
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setString("socketIp", socketIp);
                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setInt("port", port);
                SimpleClientNetty.getInstance().init(UrlUtils.SocketIp, port);
                // SimpleClientNetty.getInstance().reConnect();
                //进入白板
                enterWhiteBoard("");
                isClock = true;

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //点击了提问的主题作业
        if (isSubjective) {
            return;
        }


        if (mResultCode == 0 && mResultIntent == null) {
            checkCapScreenPermission();
        }
        //关闭home 件
        LingChuangUtils.getInstance().stopHome(MyApplication.getInstance());

    }

    private void checkCapScreenPermission() {
        //TODO

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZBVPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private boolean isDestroy = false;

    /**
     * 主动退出互动界面
     */
    @Override
    protected void onDestroy() {
        isDestroy = true;
        if (unbinder != null) {
            unbinder.unbind();
        }
        if (scheduledExecutorService != null) {
            scheduledExecutorService.shutdown();
            scheduledExecutorService = null;
        }
        if (messageExecutorService != null) {
            messageExecutorService.shutdown();
            messageExecutorService = null;
        }

        EventBus.getDefault().unregister(this);

        //退出班级,服务端会主动关闭连接
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_OUT_OF_CLASS, MsgUtils.outOfClass());

        if (SimpleClientNetty.getInstance().getSimpleClientHandler() != null) {
//            SimpleClientNetty.getInstance().getSimpleClientHandler().setDestroy(true);
            //学生主动退出
            SimpleClientNetty.getInstance().getSimpleClientHandler().setAutoClosed(true);
        }

        //关闭netty
        if (listener != null) {
            listener.stopNetty(true);
        }

        //清空sp
        sp_last_msg.edit().clear().commit();

//        //关闭通讯服务
//        Intent serverIntent = new Intent(this, SimpleSocketLinkServerTwo.class);
//        stopService(serverIntent);

        mHandler = null;
        WifiHandler.removeCallbacks(runnable);
        WifiHandler = null;
        QZXTools.setmToastNull();

        if (customDialog != null) {
            customDialog.dismiss();
        }
        //退出班级的埋点
        String joinClassStudent = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getString("joinClassStudent");
        BuriedPointUtils.buriedPoint("2004", "", "", "", joinClassStudent);

        if (popWindows != null) popWindows.cancel();
        popWindows = null;

        if (timer != null) {
            timer.cancel();
            timer = null;
        }
        //关闭推流
        Intent intent=new Intent(this,DisplayService.class);
        stopService(intent);

        unregisterHomeKeyReceiver(this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        //弹出tips
       /* TipsDialog tipsDialog = new TipsDialog();
        tipsDialog.setTipsStyle("是否退出该互动班级?\n警告：如果退出，下次进入将不会收到当前进行的状态",
                "返回", "退出", -1);
        tipsDialog.setBackNoMiss();
        tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
            @Override
            public void cancle() {
                tipsDialog.dismissAllowingStateLoss();
            }

            @Override
            public void confirm() {
                tipsDialog.dismissAllowingStateLoss();
               // InteractiveActivity.super.onBackPressed();
                InteractiveActivity.this.finish();
            }
        });
       tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());*/
    }

    //是否是截屏授权操作
    private boolean isShotGrant = false;

    //是否是录屏授权操作
    private boolean isRecordScreen = false;
    private ScheduledExecutorService scheduledExecutorService;
    /**
     * 是否正在录屏:
     */
    private boolean popIsRecordScreen = false;

    /**
     * screen shot和screen record再次点击在操作中做了处理
     */
    @Override
    public void onClick(View v) {
        if (QZXTools.canClick()) {
            switch (v.getId()) {
                case R.id.iv_back:
                    // QZXTools.logE("qin123返回上一级",null);
                    // ToastUtils.show("qin123返回上一级");
                    //如果还在录屏直接结束
                    if (popIsRecordScreen) {
                        //停止录屏---Service
                        Intent intent = new Intent(this, ScreenRecordService.class);
                        stopService(intent);
                    }
                    if (!isClock) {


                        com.zbv.basemodel.TipsDialog tipsDialog = new com.zbv.basemodel.TipsDialog();
                        tipsDialog.setTipsStyle("你确定要退出互动?",
                                "取消", "确定", -1);
                        tipsDialog.setClickInterface(new com.zbv.basemodel.TipsDialog.ClickInterface() {
                            @Override
                            public void cancle() {
                                tipsDialog.dismissAllowingStateLoss();
                            }

                            @Override
                            public void confirm() {
                                SharedPreferenceUtil.getInstance(MyApplication.getInstance()).setBoolean("openHome", false);
                                finish();
                            }
                        });
                        tipsDialog.show(getSupportFragmentManager(),
                                com.zbv.basemodel.TipsDialog.class.getSimpleName());


                    } else {
                        ToastUtils.show("请先解屏才能退出");
                    }

                    break;
                case R.id.iv_home:
                    // QZXTools.logE("qin123返回主界面",null);
                    //ToastUtils.show("qin123返回主界面");
                    //如果当前是学生投屏点击home 进入主界面
                    boolean openHome = SharedPreferenceUtil.getInstance(MyApplication.getInstance()).getBoolean("openHome");

                    if (openHome) {
                        Intent intent = new Intent(this, MainActivity.class);
                        startActivity(intent);
                    }
                    break;

                case R.id.board_wifi:
                    QZXTools.enterWifiSetting(this);


//                    String url = "http://test.download.cycore.cn/edc/openapi/avatar_default_teacher_200_m_2.png";

//                    showTeacherShot(url, false);
                    break;
                case R.id.board_more:
                    if (scheduledExecutorService == null) {
                        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
                    }
                    popMorePan(v);
                    break;
                case R.id.new_board_shot:
                    morePop.dismiss();

                    isShotGrant = true;

                    //  ZBVPermission.getInstance().setPermPassResult(this);

                    if (!ZBVPermission.getInstance().hadPermissions(this, WriteReadPermissions)) {
                        ZBVPermission.getInstance().requestPermissions(this, WriteReadPermissions);
                    } else {
                        //截屏保存SD
                        QZXTools.logD("已拥有权限读写SDCard");
                        screenSnap();
                    }
                    break;
                case R.id.new_board_record:
                    //开始录屏

                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                        QZXTools.popToast(InteractiveActivity.this, "Android5.0以下不支持录屏！！！", false);
                        return;
                    }
                    if (popIsRecordScreen) {
                        //停止录屏---Service
                        Intent intent = new Intent(this, ScreenRecordService.class);
                        stopService(intent);
                        popIsRecordScreen = false;
                        morePop.dismiss();
                    } else {

//                    if (recordScreenOperator != null && recordScreenOperator.isScreenRecord()) {
//                        recordScreenOperator.stopScreenRecord();
//                        morePop.dismiss();
//                    } else {
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                            QZXTools.popToast(this, "Android5.0以下不支持录屏", false);
                            return;
                        } else {
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                //Android6.0+需要授权危险权限
                                isRecordScreen = true;
                                // ZBVPermission.getInstance().setPermPassResult(this);
                                if (!ZBVPermission.getInstance().hadPermissions(this, RecordPermissions)) {
                                    ZBVPermission.getInstance().requestPermissions(this, RecordPermissions);
                                } else {
                                    QZXTools.logD("已拥有权限录屏");
                                    recordScreen();
                                }
                            } else {
                                //5.0 5.1不需要权限
                                recordScreen();
                            }
                        }
                    }
                    break;
                case R.id.new_board_file_receive:
                    morePop.dismiss();
                    ReceiveFilesDialog receiveFilesDialog = new ReceiveFilesDialog();
                    receiveFilesDialog.show(getSupportFragmentManager(), ReceiveFilesDialog.class.getSimpleName());
                    break;
                case R.id.new_board_collect_practice:
                    morePop.dismiss();
                    //进入习题收藏查看界面
                    CollectDisplayDialog collectDisplayDialog = new CollectDisplayDialog();
                    collectDisplayDialog.show(getSupportFragmentManager(), CollectDisplayDialog.class.getSimpleName());
                    break;
            }
        }

    }

    @Subscriber(tag = Constant.Free_Theme_Over, mode = ThreadMode.MAIN)
    public void endFreeSelect(String msg) {
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_FREE_JOIN_GROUP,
                MsgUtils.selectedGroup(msg));

        //进入白板
        getoWhitBoardFragmeng();
    }

    /**
     * 除了记录员，其余都进入白板界面
     */
    @Subscriber(tag = Constant.Show_Conclusion, mode = ThreadMode.MAIN)
    public void showConclusion(String nothing) {
        //todo 这个没判断
        enterWhiteBoard("");
    }

    @Subscriber(tag = Constant.Screen_Record_file, mode = ThreadMode.MAIN)
    public void getScreenRecordFile(String filePath) {
        recordPath = filePath;
    }

    private String recordPath;

    /**
     * ======================> Js录屏
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Subscriber(tag = Constant.Show_Js_Record, mode = ThreadMode.MAIN)
    public void recordScreen(JsRecordScreenBean jsRecordScreenBean) {
        int command = jsRecordScreenBean.getCommand();
        String json = jsRecordScreenBean.getJson();

        QZXTools.logE("command:" + command, null);
        QZXTools.logE("json:" + json, null);

        if (command == 1) {
            //开始录屏
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                QZXTools.popToast(this, "Android5.0以下不支持录屏", false);
                return;
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    //Android6.0+需要授权危险权限
                    isRecordScreen = true;
                    //  ZBVPermission.getInstance().setPermPassResult(this);
                    if (!ZBVPermission.getInstance().hadPermissions(this, RecordPermissions)) {
                        ZBVPermission.getInstance().requestPermissions(this, RecordPermissions);
                    } else {
                        QZXTools.logD("已拥有权限录屏");
                        recordScreen();
                    }
                } else {
                    //5.0 5.1不需要权限
                    recordScreen();
                }
            }
        } else if (command == 0) {
            if (TextUtils.isEmpty(recordPath)) {
                QZXTools.logE("发生错误，录屏保存文件出错", null);
                return;
            }

            //结束录屏
            Intent intent = new Intent(this, ScreenRecordService.class);
            stopService(intent);
            popIsRecordScreen = false;

            //提交答案给服务端
            String url = UrlUtils.BaseUrl + UrlUtils.CommitJsPractice;

            Gson gson = new Gson();
            InteractionAnswerVO interactionAnswerVO = gson.fromJson(json, InteractionAnswerVO.class);

            interactionAnswerVO.setClassId(UserUtils.getClassId());
            interactionAnswerVO.setClassName(UserUtils.getClassName());
            interactionAnswerVO.setStudentName(UserUtils.getStudentName());

            String resultJson = gson.toJson(interactionAnswerVO);

            QZXTools.logE("resultJson=" + resultJson + ";name=" + UserUtils.getStudentName(), null);

            Map<String, String> map = new HashMap<>();
            map.put("interactionAnswer", resultJson);

            File file = new File(recordPath);

            OkHttp3_0Utils.getInstance().asyncPostSingleOkHttp(url, "file", map, file, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    QZXTools.logE("onFailure", e);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    QZXTools.logE("asyncPostSingleOkHttp str=" + response.body().string(), null);
                }
            });

        } else if (command == 2) {
            //仅仅结束录屏
            //停止录屏---Service
            Intent intent = new Intent(this, ScreenRecordService.class);
            stopService(intent);
            popIsRecordScreen = false;
        }
    }


    /**
     * 获取到权限后开始录屏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void recordScreen() {
        Intent captureIntent = projectionManager.createScreenCaptureIntent();
        startActivityForResult(captureIntent, Media_Projection_Record_RequestCode);
        isRecordScreen = false;
    }

    /**
     * 截屏快照：除了状态栏一下的屏幕
     * 截屏和录屏
     * todo 截屏模式的MediaProjection为什么很慢？22秒 因为阻塞了主线程，需要在服务中执行截屏
     */
    private void screenSnap() {

        QZXTools.logE("screenSnap date start =" + new Date().getTime(), null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            //使用MediaProjection截屏
            shotMediaProjection();//6s
        } else {
            //使用画板方式的截屏
            shotDrawView();//431ms
        }

        QZXTools.logE("screenSnap date end =" + new Date().getTime(), null);

        //重置
        isShotGrant = false;
    }

    /**
     * 使用MediaProjection截屏
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void shotMediaProjection() {
        startActivityForResult(projectionManager.createScreenCaptureIntent(), Media_Projection_Shot_RequestCode);
    }

    private String shotFilePath;

    /**
     * 画板模式截屏，对View的操作缺点如下：
     * 一、无法截取SurfaceView的内容
     * 二、无法截取状态栏内容
     */
    private void shotDrawView() {
        View view = getWindow().getDecorView();
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);

        shotFilePath = QZXTools.getExternalStorageForFiles(this, null) + "/zhkt_shot";
        File dirFile = new File(shotFilePath);
        if (!dirFile.exists()) {
            boolean dirSuccess = dirFile.mkdir();
            if (!dirSuccess) {
                QZXTools.popToast(this, "截屏路径出错，截屏失败", false);
                return;
            }
        }

        FileOutputStream fos = null;
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
            String dateStr = simpleDateFormat.format(new Date());
            String fileName = "shotImg_" + dateStr + ".png";
            File shotFile = new File(shotFilePath, fileName);
            fos = new FileOutputStream(shotFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

            QZXTools.savePictureToSystemDCIM(InteractiveActivity.this, shotFile, "");

            //发送通知
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationChannel channel = null;
            if (Build.VERSION.SDK_INT >= 26) {
                channel = new NotificationChannel("zhkt", "screen_shot", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(channel);
            }

            NotificationCompat.Builder builder;
            if (channel != null && Build.VERSION.SDK_INT >= 26) {
                builder = new NotificationCompat.Builder(this);
            } else {
                builder = new NotificationCompat.Builder(this);
            }


            Intent intentClick = new Intent(this, NotificationBroadcastReceiver.class);
            intentClick.setAction(NotificationBroadcastReceiver.ACTION_CLICK);
            intentClick.putExtra(NotificationBroadcastReceiver.TYPE, 7);
            intentClick.putExtra("shot_path", shotFile.getAbsolutePath());
            PendingIntent pendingIntentClick = PendingIntent.getBroadcast(this, 0, intentClick, PendingIntent.FLAG_ONE_SHOT);
            Intent intentCancel = new Intent(this, NotificationBroadcastReceiver.class);
            intentCancel.setAction(NotificationBroadcastReceiver.ACTION_CANCEL);
            intentCancel.putExtra(NotificationBroadcastReceiver.TYPE, 7);
            PendingIntent pendingIntentCancel = PendingIntent.getBroadcast(this, 0, intentCancel, PendingIntent.FLAG_ONE_SHOT);

            //小图标必加，否则闪退
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("截屏");
            builder.setContentText("课堂互动的截屏信息");
            builder.setWhen(System.currentTimeMillis());
            builder.setDefaults(Notification.DEFAULT_LIGHTS);
            builder.setContentIntent(pendingIntentClick);
            builder.setDeleteIntent(pendingIntentCancel);

            Notification notification = builder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;//发起正在运行事件
            notificationManager.notify(7, notification);

            QZXTools.popToast(InteractiveActivity.this, "截屏成功", false);
            ScreenShotImgDialog screenShotImgDialog = new ScreenShotImgDialog();
            screenShotImgDialog.setImgFilePath(shotFile.getAbsolutePath());
            screenShotImgDialog.show(getSupportFragmentManager(), ScreenShotImgDialog.class.getSimpleName());

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (bitmap != null) {
                bitmap.recycle();
            }
        }
    }


    private PopupWindow morePop;

    /**
     * 弹出更多内容选择弹框
     */
    private void popMorePan(View v) {
        if (morePop != null) {
            morePop.dismiss();
        }
        View moreView = LayoutInflater.from(this).inflate(R.layout.pop_white_board_more_vertical_layout, null);
        int moreWidth = getResources().getDimensionPixelSize(R.dimen.y126);
        int moreHeight = getResources().getDimensionPixelSize(R.dimen.x466);
        morePop = new PopupWindow(moreView, moreWidth, moreHeight);

        morePop.setBackgroundDrawable(new ColorDrawable());
        morePop.setOutsideTouchable(true);

        LinearLayout new_board_shot = moreView.findViewById(R.id.new_board_shot);
        LinearLayout new_board_record = moreView.findViewById(R.id.new_board_record);
        LinearLayout new_board_file_receive = moreView.findViewById(R.id.new_board_file_receive);
        LinearLayout new_board_collect_practice = moreView.findViewById(R.id.new_board_collect_practice);

        new_board_shot.setOnClickListener(this);
        new_board_record.setOnClickListener(this);
        new_board_file_receive.setOnClickListener(this);
        new_board_collect_practice.setOnClickListener(this);

        //这里粗劣计算头布局的高度一半
        int offsetHeight = getResources().getDimensionPixelSize(R.dimen.x35);
        morePop.showAsDropDown(v, 0, offsetHeight);
    }

    //-------------------------通讯连接------------------------------
    @Override
    public void onLine() {
        if (mHandler == null) {
            return;
        }

        Message message = mHandler.obtainMessage();
        message.what = Constant.OnLine;
        message.obj = "在线";
        mHandler.sendMessage(message);
    }

    @Override
    public void offLine() {
        if (mHandler == null) {
            return;
        }

        Message message = mHandler.obtainMessage();
        message.what = Constant.OffLine;
        message.obj = "离线";
        mHandler.sendMessage(message);
    }

    @Override
    public void receiveData(String msgInfo) {
        if (mHandler == null) {
            return;
        }
        Log.i("qin002", "receiveData: " + msgInfo);
        Message message = mHandler.obtainMessage();
        message.what = Constant.ReceiveMessage;
        message.obj = msgInfo;
        mHandler.sendMessage(message);
    }

    @Override
    public void isNoUser() {
        if (mHandler == null) {
            return;
        }

        Message message = mHandler.obtainMessage();
        message.what = Constant.isNotUser;
        mHandler.sendMessage(message);
    }


    /**
     * 教师截屏分享,展示截屏的图片，并且回传给服务端便于课堂记录的数据展示
     *
     * @param isFocus 是否是聚焦截屏
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void showTeacherShot(String body, boolean isFocus) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }

//        Intent intent_img = new Intent(this, ImageLookActivity.class);
//        ArrayList<String> imgFilePathList = new ArrayList<>();
//        imgFilePathList.add(body);
//        intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
//        intent_img.putExtra("curImgIndex", 0);
//        startActivity(intent_img);

        TeacherShotFragment teacherShotFragment = new TeacherShotFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url_img", body);
        teacherShotFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, teacherShotFragment);
        fragmentTransaction.commitAllowingStateLoss();

        sendShotRecordToServer(body, isFocus);
    }


    //白班的推送消息
    private void WhiteboardPush(String body) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        Log.i(TAG, "WhiteboardPush001: " + body);
        WhitBoardPushFragment whitBoardPushFragment = new WhitBoardPushFragment();
        Bundle bundle = new Bundle();
        bundle.putString("url_img", body);
        whitBoardPushFragment.setArguments(bundle);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, whitBoardPushFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 教师截屏分享相关
     * 这里学生端主动发送一条记录给服务端
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void sendShotRecordToServer(String fileUrl, boolean isFocus) {
        String url = UrlUtils.BaseUrl + UrlUtils.ToServerShotShare;
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("userId", UserUtils.getUserId());
        paraMap.put("classId", UserUtils.getClassId());
        if (isFocus) {
            paraMap.put("type", "19");
        } else {
            paraMap.put("type", "18");
        }
        paraMap.put("fileUrl", fileUrl);
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                QZXTools.logE("sendShotRecordToServer onFailure", null);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                QZXTools.logE("result = " + response.body().string(), null);
            }
        });
    }

    /**
     * 接收到关机指令
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void receiveShutdown() {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setMessage("---接收到关机指令---").setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        }, 1000);

        LingChuangUtils.getInstance().closeDevice(MyApplication.getInstance());


    }

    /**
     * 接收广播
     */
    private void startBroadcast(String body) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        if (TextUtils.isEmpty(body)) {
          /*  AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder.setMessage("---接收到教师端的广播---").setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismiss();
                }
            }, 1000);*/

        } else {
            FFmpegFragment fFmpegFragment=new FFmpegFragment();
            Bundle bundle = new Bundle();
//        bundle.putString("rtsp_url", "rtsp://172.16.5.158/1/");

            // texture_view.setUp("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", null);
            //  texture_view.setUp("rtmp://202.69.69.180:443/webcast/bshdlive-pc", null);
            bundle.putString("rtsp_url", body.trim());
            //bundle.putString("rtsp_url", "rtmp://192.168.3.15/live/tiantainqin");
            //bundle.putString("rtsp_url", "rtmp://202.69.69.180:443/webcast/bshdlive-pc");
            //   bundle.putString("rtsp_url", "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
            fFmpegFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.board_frame, fFmpegFragment);
            fragmentTransaction.commitAllowingStateLoss();


          /*  PlayingRtspFragment playingRtspFragment = new PlayingRtspFragment();

            Bundle bundle = new Bundle();
//        bundle.putString("rtsp_url", "rtsp://172.16.5.158/1/");

            // texture_view.setUp("rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov", null);
            //  texture_view.setUp("rtmp://202.69.69.180:443/webcast/bshdlive-pc", null);
            bundle.putString("rtsp_url", body.trim());
            //bundle.putString("rtsp_url", "rtmp://192.168.3.15/live/tiantainqin");
            //bundle.putString("rtsp_url", "rtmp://202.69.69.180:443/webcast/bshdlive-pc");
            //   bundle.putString("rtsp_url", "rtsp://wowzaec2demo.streamlock.net/vod/mp4:BigBuckBunny_115k.mov");
            playingRtspFragment.setArguments(bundle);
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.board_frame, playingRtspFragment);
            fragmentTransaction.commitAllowingStateLoss();*/
        }
    }

    /**
     * 结束接收广播
     */
    private void stopBroadcast() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        AlertDialog alertDialog = builder.setMessage("---停止广播---").setCancelable(true)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).show();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (alertDialog != null && alertDialog.isShowing())
                    alertDialog.dismiss();
            }
        }, 1000);

        //进入白班
        //进入白板
        getoWhitBoardFragmeng();
    }

    private final int REQUEST_CODE_STREAM_RTSP = 199; //random num
    private String rtsp_url;

    /**
     * 开始投屏
     */
    private void startStudnetCast(String body) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        if (TextUtils.isEmpty(body)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            AlertDialog alertDialog = builder.setMessage("---开始学生端投屏---").setCancelable(true)
                    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    }).show();

            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (alertDialog != null && alertDialog.isShowing())
                        alertDialog.dismiss();
                }
            }, 1000);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                if (!DisplayService.Companion.isStreaming()) {
                    rtsp_url = body.trim();
                    startActivityForResult(DisplayService.Companion.sendIntent(), REQUEST_CODE_STREAM_RTSP);
                } else {
                    stopService(new Intent(this, DisplayService.class));
                }
            } else {
                QZXTools.popToast(getApplicationContext(), "版本低于5.0,不支持投屏", false);
            }
        }
    }

    /**
     * 结束投屏
     */
    private void stopStudnetCast() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (!DisplayService.Companion.isStreaming()) {
                stopService(new Intent(this, DisplayService.class));
            }
        }
    }

    /**
     * 锁屏界面
     */
    private boolean isunClock = true;

    private void enterLock() {
        if (customDialog != null && customDialog.isShowing()) {
            return;
        }
        customDialog = new CustomDialog(this);
        customDialog.show();

        Log.i("qin0513", "enterLock: ");
        //设置返回键的监听
        //设置取消的监听
        setDialogeCallListener();
        //禁用back键
        //LingChuangUtils.getInstance().stopBack(MyApplication.getInstance());

    }

    private long tem1 = 0;

    private void setDialogeCallListener() {
        customDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
                if (i == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                    //todo 主要是这个地方调2次
                    Calendar calendar = Calendar.getInstance();
                    long timeInMillis = calendar.getTimeInMillis();
                    if (tem1 != 0 && timeInMillis - tem1 < 700) {
                        isunClock = false;
                    } else {
                        isunClock = true;
                    }
                    tem1 = timeInMillis;

                    // backClock(isunClock);
                    return true;


                }
                return false;
            }
        });
    }

    private void backClock(boolean isunClock) {
        if (isunClock) {
            Log.i(TAG, "onKey: ");
            //弹出tips
            TipsDialog tipsDialog = new TipsDialog();
            tipsDialog.setTipsStyle("是否退出锁屏?\n警告：如果退出，下次进入将不会收到当前进行的状态",
                    "返回", "退出", -1);
            //   tipsDialog.setBackNoMiss();
            tipsDialog.setClickInterface(new TipsDialog.ClickInterface() {
                @Override
                public void cancle() {
                    tipsDialog.dismissAllowingStateLoss();
                    setDialogeCallListener();
                }

                @Override
                public void confirm() {
                    tipsDialog.dismissAllowingStateLoss();
                    customDialog.dismiss();
                    InteractiveActivity.super.onBackPressed();
                }
            });
            tipsDialog.show(getSupportFragmentManager(), TipsDialog.class.getSimpleName());
        }
    }

    /**
     * 呈现白板画面
     *
     * @param type
     */
    private void enterWhiteBoard(String type) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }

        //先判断又没有点击屏幕广播

        /**
         * 如果sp没有则显示白板，否则显示sp界面
         * */
        //清空sp
        sp_last_msg.edit().clear().commit();
        sp_msg = sp_last_msg.getString("Last_Msg", null);

        QZXTools.logE("qin123" + sp_msg, null);
        //进入sp界面
        if (!TextUtils.isEmpty(sp_msg)) {
            Message message = mHandler.obtainMessage();
            message.what = Constant.ReceiveMessage;
            message.obj = sp_msg;
            mHandler.sendMessage(message);
        } else {
            if (TextUtils.isEmpty(type)) {
                //进入白板
                getoWhitBoardFragmeng();
                return;
            }
            //结束投票，进入白板
            if (!TextUtils.isEmpty(type) && type.equals(MsgUtils.HEAD_END_VOTE)) {
                //进入白板
                getoWhitBoardFragmeng();
                return;
            }
            //是开始上课了 进入白板
            if (!TextUtils.isEmpty(type) && type.equals(MsgUtils.HEAD_START_CLASS)) {
                //进入白板
                getoWhitBoardFragmeng();
                return;
            }
            //如果当前是解屏  HEAD_UNLOCK
            if (!TextUtils.isEmpty(type) && type.equals(MsgUtils.HEAD_UNLOCK)) {
                //进入白板
                getoWhitBoardFragmeng();
                //启动返回键
                LingChuangUtils.getInstance().startBack(MyApplication.getInstance());
                return;
            }
            //如果是结束抢答进入白板
            if (!TextUtils.isEmpty(type) && type.equals(MsgUtils.HEAD_END_ANSWER)) {
                //进入白板
                getoWhitBoardFragmeng();
                return;
            }
        }


    }

    private void getoWhitBoardFragmeng() {
        NewWhiteBoardFragment whiteBoardFragment = new NewWhiteBoardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, whiteBoardFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 吐司提示
     */
    private void popToastInfo(String msg) {
        // QZXTools.popToast(this, msg, false);
    }

    /**
     * 连接上后发送班级信息
     */
    private void sendJoinClass() {
        // boolean isReconnected = sp_last_msg.getBoolean("isReconnected", false);


        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.HEAD_JOINCLASS,
                MsgUtils.joinClass(SimpleClientNetty.getInstance().isReconnected()));
        //重连标志写入sp
        // sp_last_msg.edit().putBoolean("isReconnected", true).commit();
        //判断是不是重连不是重连就进入锁屏
      /*  if (!SimpleClientNetty.getInstance().isReconnected()) {
            //锁屏
            enterLock();
        }*/
    }

    /**
     * 接收表扬或者批评
     */
    private void receivePraiseOrCriticism(boolean isPraised, String stName) {
        PraiseAndCriticismDialog dialog = new PraiseAndCriticismDialog();
        dialog.setDialogType(isPraised, stName.trim());
        dialog.show(getSupportFragmentManager(), PraiseAndCriticismDialog.class.getSimpleName());
    }

    private ResponderFragment responderFragment;

    /**
     * 展示抢答界面
     */
    private void startResponder() {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        responderFragment = new ResponderFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, responderFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 展示抢答结果
     */
    private void showResponder(String name) {
        if (responderFragment != null) {
            responderFragment.showResponseResult(name.trim());
        }
    }

    /**
     * 随机点名
     */
    private void randomName(String name) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        randomNameDialog = new RandomNameDialog();
        randomNameDialog.setName(name.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, randomNameDialog);
        fragmentTransaction.commitAllowingStateLoss();


    }


    //需要动态申请读写sdcard权限
    private static final String[] WriteReadPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    //请求录屏权限
    private static final String[] RecordPermissions = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.RECORD_AUDIO
    };

    /**
     * 文件接收模块
     */
    private void fileReceive(String body) {

        enterWhiteBoard("");

        FileReceiveDialog fileReceiveDialog = new FileReceiveDialog();
        fileReceiveDialog.setFileBodyString(false, null, body);
        fileReceiveDialog.show(getSupportFragmentManager(), FileReceiveDialog.class.getSimpleName());
    }

    private String fileReceiveBody = null;

    private void judgeFileReceivePermission(String body) {
        //ZBVPermission.getInstance().setPermPassResult(this);

        if (!ZBVPermission.getInstance().hadPermissions(this, WriteReadPermissions)) {
            fileReceiveBody = body;
            ZBVPermission.getInstance().requestPermissions(this, WriteReadPermissions);
        } else {
            //直接打开相机
            QZXTools.logD("已拥有权限读写SDCard");
            fileReceive(body);
        }
    }

    private VoteFragment voteFragment;

    /**
     * 开始投票
     */
    private void startVote(String body) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        voteFragment = new VoteFragment();
        //请确保body没有多余的空格
        voteFragment.setVoteId(body.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, voteFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 结束投票
     */
    private void endVote() {
        //提交投票结果
      /*  if (voteFragment != null && voteFragment.getVoteDialog() != null && voteFragment.getVoteDialog().isVisible()) {
            voteFragment.getVoteDialog().commitVote();
        }*/
        QZXTools.popToast(this, "投票结束了", true);
        EventBus.getDefault().post("close_discuss", Constant.Close_Discuss_Img);

        //进入白板界面  结束投票
        enterWhiteBoard(MsgUtils.HEAD_END_VOTE);
    }

    private QuestionFragment questionFragment;

    /**
     * 开启随堂练习
     */
    private void startPractice(String body) {
        //发送状态
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.PAPER_RECIEVE + "",
                MsgUtils.createPracticeStatus(MsgUtils.PAPER_RECIEVE));

        questionFragment = new QuestionFragment();
        //请确保body没有多余的空格
        questionFragment.setPracticeId(body.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, questionFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 结束随堂练习
     */
    private void endPractice() {
        //先提交
        if (questionFragment != null)
            questionFragment.commitAnswer();
        //切换到白板界面
        enterWhiteBoard("");
    }

    private GroupDiscussFragment groupDiscussFragment;

    /**
     * 开始分组讨论
     * 一、区分自由分组和随机分组
     * 二、提交选组的信息给通讯服务端
     */
    private void startDiscuss(String body) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        groupDiscussFragment = new GroupDiscussFragment();
        //请确保body没有多余的空格
        groupDiscussFragment.setDiscussId(body.trim());
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, groupDiscussFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 自由选组
     */
    private void toFreeDiscuss(String disucssId) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        FreeSelectDiscussGroupFragment freeSelectDiscussGroupFragment = new FreeSelectDiscussGroupFragment();
        freeSelectDiscussGroupFragment.setDiscussId(disucssId);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, freeSelectDiscussGroupFragment);
        fragmentTransaction.commitAllowingStateLoss();

        //Dialog形式
//        SelectGroupFragment selectGroupFragment = new SelectGroupFragment();
//        selectGroupFragment.setDiscussId(disucssId);
//        selectGroupFragment.show(getSupportFragmentManager(), SelectGroupFragment.class.getSimpleName());
    }

    /**
     * 提问的分组
     *
     * @param body
     */

    private void toFreeDiscussAnswear(String body) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        FreeSelectDiscussGroupAnstarFragment
                freeSelectDiscussGroupAnstarFragment = new FreeSelectDiscussGroupAnstarFragment();
        freeSelectDiscussGroupAnstarFragment.setDiscussId(body);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, freeSelectDiscussGroupAnstarFragment);
        fragmentTransaction.commitAllowingStateLoss();

    }

    /**
     * 结束分组讨论：记录员显示结论Dialog,之后都回到白板界面
     */
    private void endDiscuss() {
        //结束讨论前记录员总结
        if (groupDiscussFragment != null && groupDiscussFragment.isVisible()) {
            groupDiscussFragment.showConclusionView();
        }
        //如果有ImageActivity在前台则关闭
        EventBus.getDefault().post("close_discuss", Constant.Close_Discuss_Img);
    }

    WebViewFragment webViewFragment;

    /**
     * 进入PPT互动界面
     */
    private void enterPPTCommand(String type, String id) {
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        webViewFragment = new WebViewFragment();
        webViewFragment.setFlag(Integer.parseInt(type));
        webViewFragment.setInteractId(id);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, webViewFragment);
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * 提问界面
     * <p>
     * StartAnswer 2b32707b834b4e1fb473cddba67deab6 4ec51ba42642496caf9d1ab400df1a9c;head=StartAnswer;body=4ec51ba42642496caf9d1ab400df1a9c
     * <p>
     * http://172.16.5.160:8090/wisdomclass/interface/homework/handDetail?homeworkid=c872c974cbea4074983cf921c759c27c&status=0
     */
    private long tem = 0;

    private void answerQuestion(String body) {
        Log.i("qin0509", "handleMessage:2222222222 " + body);
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        //获取系统的当前时间
        Calendar calendar = Calendar.getInstance();
        long timeInMillis = calendar.getTimeInMillis();
        if (tem != 0 && timeInMillis - tem < 2500) {
            return;
        } else {
            Log.i("qin0509", "handleMessage:11111111 ");
            questionOnlyPicFragment = new QuestionOnlyPicFragment();
            questionOnlyPicFragment.setPracticeId(body.trim());
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.board_frame, questionOnlyPicFragment);
            Bundle bundle = new Bundle();
            bundle.putString("homeworkId", body);
            questionOnlyPicFragment.setArguments(bundle);

            fragmentTransaction.commitAllowingStateLoss();
        }
        tem = timeInMillis;
    }
    //-------------------------通讯连接------------------------------

    //-------------------------权限----------------------

    private Uri outputUri;


    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        isSubjective = true;
        if (resultCode == Activity.RESULT_OK) {

            switch (requestCode) {
                case REQUEST_CODE_STREAM_RTSP:
                    if (data != null && (requestCode == REQUEST_CODE_STREAM_RTSP
                            && resultCode == Activity.RESULT_OK)) {
                        //   initNotification();
                        DisplayService.Companion.setData(resultCode, data);
                        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().
                                queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

                        String path = QZXTools.getExternalStorageForFiles(MyApplication.getInstance(), null) + "/config.txt";
                        Properties properties = QZXTools.getConfigProperties(path);
                        String socketIp = properties.getProperty("socketIp");
                        Intent intent = new Intent(this, DisplayService.class);
                        Constant.RtmpUrl = "rtmp://" + socketIp + "/live/" + studentInfo.getUserId();
                        intent.putExtra("endpoint", Constant.RtmpUrl);
                        startService(intent);
                    } else {
                        Toast.makeText(this, "No permissions available", Toast.LENGTH_SHORT).show();
                        //button.setText(R.string.start_button);
                    }


                    break;
                case ZBVPermission.ACTIVITY_REQUEST_CODE:
                    //这一点注意，我们使用系统相机和相册的请求码不能和  public static final int ACTIVITY_REQUEST_CODE = 0x9;一样
                    ZBVPermission.getInstance().onActivityResult(requestCode, resultCode, data);

                    break;
                case SubjectiveToDoView.CODE_SYS_CAMERA:
                    //data为null,因为自己设定了拍好照图片的保存位置
                    EventBus.getDefault().post("CAMERA_CALLBACK", Constant.Subjective_Camera_Callback);
                    if (questionOnlyPicFragment!=null){
                        questionOnlyPicFragment.fromCameraCallback("CAMERA_CALLBACK");
                    }

                    break;
                case GroupDiscussFragment.CODE_SYS_CAMERA:
                    //data为null
                    outputUri = createCropUri();

                    QZXTools.logE("cameraUri:" + GroupDiscussFragment.cameraUri, null);

//                    GroupDiscussFragment.cropPhoto(this, GroupDiscussFragment.cameraUri, outputUri);
                    GroupDiscussFragment.cropPhotoTwo(this, GroupDiscussFragment.cameraUri, outputUri);

                    break;
                case GroupDiscussFragment.CODE_SYS_ALBUM:
                    if (data == null) {
                        return;
                    }
                    outputUri = createCropUri();
                    GroupDiscussFragment.cameraUri = data.getData();

//                    GroupDiscussFragment.cropPhoto(this, data.getData(), outputUri);
                    GroupDiscussFragment.cropPhotoTwo(this, data.getData(), outputUri);

                    break;
                case GroupDiscussFragment.CODE_SYS_CROP:
                    String filePath = QZXTools.getRealFilePath(this, outputUri);
                    QZXTools.logE("filePath " + filePath + ";outputUri=" + outputUri, null);

                    if (data.getData() != null) {
                        QZXTools.logE("data " + data.getData(), null);
                        filePath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));
                    }
                    QZXTools.logE("end filePath " + filePath, null);
                    Bitmap bitmap = BitmapFactory.decodeFile(filePath);

                    if (bitmap == null) {
                        QZXTools.logE("bitmap==null first", null);
                        bitmap = BitmapFactory.decodeFile(filePath);
                        if (bitmap == null) {
                            //裁剪有问题：在华瑞安的Android7.1.2版本系统裁剪有问题
                            filePath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                    + GroupDiscussFragment.cameraUri.toString()
                                    .substring(GroupDiscussFragment.cameraUri.toString().lastIndexOf("/"));
                            bitmap = BitmapFactory.decodeFile(filePath);

                            QZXTools.logE("bitmap==null " + GroupDiscussFragment.cameraUri + ";filePath=" + filePath, null);
                        }
                    }

                    compressQuality(filePath, bitmap);

                    EventBus.getDefault().post(filePath, Constant.Discuss_Send_Pic);
//                    groupDiscussFragment.sendDiscussMsg(filePath, MsgUtils.TYPE_PICTURE);

                    break;
                case GroupDiscussFragment.CODE_CUSTOM_CROP:
                    String newFilePath = QZXTools.getRealFilePath(this, data.getData());
                    EventBus.getDefault().post(newFilePath, Constant.Discuss_Send_Pic);

                    break;
                case Media_Projection_Shot_RequestCode:
                    //获取用户截屏授权
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        mediaProjection = projectionManager.getMediaProjection(resultCode, data);
//                        if (mediaProjection != null) {

//                        QZXTools.logE("Projection_Shot date start =" + new Date().getTime(), null);

//                            startScreenShot();

                        Intent intent = new Intent(this, ScreenShotService.class);
                        intent.putExtra("result_code", resultCode);
                        intent.putExtra("data_intent", data);
                        intent.setPackage(getPackageName());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }

//                        QZXTools.logE("Projection_Shot date end =" + new Date().getTime(), null);


//                        }
                    }

                    break;
                case Media_Projection_Record_RequestCode:
                    //获取用户录屏授权
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        popIsRecordScreen = true;
                        if (morePop != null) {
                            morePop.dismiss();
                        }
                        Intent intent = new Intent(this, ScreenRecordService.class);
                        intent.putExtra("result_code", resultCode);
                        intent.putExtra("data_intent", data);
                        intent.setPackage(getPackageName());
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(intent);
                        } else {
                            startService(intent);
                        }
                    }

                    break;
                //记录员拍照或者视频
                case CameraAlbumPopupFragment.CODE_SYS_CAMERA:
                    QZXTools.logE("camera img data=" + data, null);
                    outputUri = createCropUri();
//                    CameraAlbumPopupFragment.cropPhoto(this, CameraAlbumPopupFragment.cameraUri, outputUri);
                    CameraAlbumPopupFragment.cropPhotoTwo(this, CameraAlbumPopupFragment.cameraUri, outputUri);

                    break;
                case CameraAlbumPopupFragment.CODE_SYS_CROP:
                    String cropPath = QZXTools.getRealFilePath(this, outputUri);
//                    QZXTools.logE("cropPath=" + cropPath + ";data=" + data.getData(), null);
                    if (data.getData() != null) {
                        cropPath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));
                    }
                    Bitmap cropBm = BitmapFactory.decodeFile(cropPath);
                    if (cropBm == null) {
                        QZXTools.logE("bitmap==null first", null);
                        cropBm = BitmapFactory.decodeFile(cropPath);
                        if (cropBm == null) {
                            //裁剪有问题：在华瑞安的Android7.1.2版本系统裁剪有问题
                            cropPath = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES)
                                    + CameraAlbumPopupFragment.cameraUri.toString()
                                    .substring(CameraAlbumPopupFragment.cameraUri.toString().lastIndexOf("/"));
                            cropBm = BitmapFactory.decodeFile(cropPath);

                            QZXTools.logE("bitmap==null " + CameraAlbumPopupFragment.cameraUri + ";filePath=" + cropPath, null);
                        }
                    }
                    compressQuality(cropPath, cropBm);
                    EventBus.getDefault().post(cropPath, Constant.Group_Conclusion_Pic);

                    break;
                case CameraAlbumPopupFragment.CODE_CUSTOM_CROP:
                    String newCropPath = QZXTools.getRealFilePath(this, data.getData());
                    EventBus.getDefault().post(newCropPath, Constant.Group_Conclusion_Pic);

                    break;
                case CameraAlbumPopupFragment.CODE_SYS_CAMERA_VIDEO:
                    //todo 拍视频压缩？
                    QZXTools.logE("camera video data=" + data.getData(), null);
//content://com.telit.smartclass.desktop.fileprovider/camera_photos/Android/data/com.telit.smartclass.desktop/files/VIDEO_20191219_101652.mp4
//                    String videoPath = UriTool.getFilePathByUri(this, data.getData());
                    String videoPath = QZXTools.getRealFilePath(this, CameraAlbumPopupFragment.cameraUri);
                    String videoPath2 = UriTool.getFilePathByUri(this, CameraAlbumPopupFragment.cameraUri);
                    QZXTools.logE("camera video actual path =" + videoPath
                            + ";uri=" + CameraAlbumPopupFragment.cameraUri
                            + ";video2=" + videoPath2, null);

                    String actualPath = QZXTools.getExternalStorageForFiles(this, null)
                            + data.getData().toString().substring(data.getData().toString().lastIndexOf("/"));

                    QZXTools.logE("actualPath=" + actualPath, null);

                    EventBus.getDefault().post(actualPath, Constant.Group_Conclusion_Video);

                    break;
            }
        }
    }

    private void initNotification() {
        Notification.Builder notificationBuilder =
                new Notification.Builder(this).setSmallIcon(R.drawable.notification_anim)
                        .setContentTitle("Streaming")
                        .setContentText("Display mode stream")
                        .setTicker("Stream in progress");
        notificationBuilder.setAutoCancel(true);
        if (notificationManager != null)
            notificationManager.notify(12345, notificationBuilder.build());
    }

    /**
     * 创建裁剪Uri
     */
    private Uri createCropUri() {
        String fileDir = QZXTools.getExternalStorageForFiles(this, Environment.DIRECTORY_PICTURES);
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("IMG_");
        stringBuilder.append(simpleDateFormat.format(new Date()));
        stringBuilder.append(".jpg");
        File outputFile = new File(fileDir, stringBuilder.toString());

        QZXTools.logE("outputPATH=" + outputFile.getAbsolutePath(), null);

        return Uri.fromFile(outputFile);
    }

    /**
     * 质量压缩并存入本地或者上传数据库
     */
    private void compressQuality(String filePath, Bitmap bitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(filePath);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    /**
     * 获取当前连接的wifi名称
     *
     * @param context
     * @return
     */
    public static String getWIFIName(Context context) {
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiId = info != null ? info.getSSID().replace("\"", "") : null;
        return wifiId;
    }

    public interface onCellNettyListener {
        void stopNetty(boolean closeNetty);
    }

    public void setonCellNettyListener(onCellNettyListener listener) {

        this.listener = listener;
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    //同屏 屏幕广播的问题
    @Override
    public void changeViewStatus(int status, String URL) {

    }

    @Override
    public void setPresenter(PusherContract.Presenter presenter) {

    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public void showProgress() {

    }

    @Override
    public void dismissProgress() {

    }

    @Override
    public void showTip(String message) {

    }


    @Subscriber(tag = Constant.Homework_Commit_Success, mode = ThreadMode.MAIN)
    public void SubmitQuestionSucess(String homeworkId) {
        QZXTools.logE("提交提问作业成功: " + homeworkId, null);

        //进入提问作业详情
        if (customDialog != null && customDialog.isShowing()) {
            customDialog.dismiss();
        }
        AskQueestionFragment askQueestionFragment = new AskQueestionFragment();
        //请确保body没有多余的空格
        askQueestionFragment.setHomeWordId(homeworkId);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.board_frame, askQueestionFragment);
        fragmentTransaction.commitAllowingStateLoss();

        //发送提问消息给教师端  内容学生id
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.SubmitQuestion,
                MsgUtils.SubmitQuestion());
    }

    //作业在提问是提交成功
    @Subscriber(tag = Constant.Homework_Commit_Success_Tijiao, mode = ThreadMode.MAIN)
    public void SubmitQuestion(String teacher_end) {
        Log.i("qin", "提交提问作业成功: " + teacher_end);

        //发送提问消息给教师端  内容学生id
        SimpleClientNetty.getInstance().sendMsgToServer(MsgUtils.SubmitQuestion,
                MsgUtils.SubmitQuestion());
        if (teacher_end.equals("teacher_end")) {
            Toast.makeText(this, "作业时间一到必须提交", Toast.LENGTH_LONG).show();
            enterWhiteBoard("");
        } else {
            Toast.makeText(this, "提问作业提交成功", Toast.LENGTH_LONG).show();
        }
    }

    //点击了提问的主题作业
    private boolean isSubjective = false;

    @Subscriber(tag = Constant.Subjective_Board_Callback, mode = ThreadMode.MAIN)
    public void SubmitSubjective(ExtraInfoBean anster) {
        Log.i("qin", "点击了提问的主题作业主题 " + anster);
        isSubjective = true;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        WebViewFragment mAgentWebFragment = this.webViewFragment;
       /* if (mAgentWebFragment != null) {
            FragmentKeyDown mFragmentKeyDown = mAgentWebFragment;
            if (mFragmentKeyDown.onFragmentKeyDown(keyCode, event)) {
                return true;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }*/

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void setRequestedOrientation(int requestedOrientation) {
        return;
    }


    private HomeWatcherReceiver mHomeKeyReceiver = null;

    private void registerHomeKeyReceiver(Context context) {
        QZXTools.logD("registerHomeKeyReceiver");

        mHomeKeyReceiver = new HomeWatcherReceiver();
        final IntentFilter homeFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);

        context.registerReceiver(mHomeKeyReceiver, homeFilter);
    }

    private void unregisterHomeKeyReceiver(Context context) {
        QZXTools.logD("unregisterHomeKeyReceiver");

        if (null != mHomeKeyReceiver) {
            context.unregisterReceiver(mHomeKeyReceiver);
        }
    }

    public class HomeWatcherReceiver extends BroadcastReceiver {
        private static final String LOG_TAG = "HomeReceiver";

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        //action内的某些reason
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";//home键旁边的最近程序列表键
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";//按下home键
        private static final String SYSTEM_DIALOG_REASON_LOCK = "lock";//锁屏键
        private static final String SYSTEM_DIALOG_REASON_ASSIST = "assist";//某些三星手机的程序列表键

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
//        App app = (App) context.getApplicationContext();
            Log.i(LOG_TAG, "onReceive: action: " + action);
            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {//Action
                // android.intent.action.CLOSE_SYSTEM_DIALOGS
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                QZXTools.logD("reason" + reason);


                if (SYSTEM_DIALOG_REASON_HOME_KEY.equals(reason)) { // 短按Home键
                    //可以在这里实现关闭程序操作。。。
                    QZXTools.logD("homekey");


                } else if (SYSTEM_DIALOG_REASON_RECENT_APPS.equals(reason)) {//Home键旁边的显示最近的程序的按钮
                    // 长按Home键 或者 activity切换键
                    QZXTools.logD("long press home key or activity switch");

                } else if (SYSTEM_DIALOG_REASON_LOCK.equals(reason)) {  // 锁屏，似乎是没有反应，监听Intent.ACTION_SCREEN_OFF这个Action才有用
                    QZXTools.logD("lock");

                } else if (SYSTEM_DIALOG_REASON_ASSIST.equals(reason)) {   // samsung 长按Home键
                    QZXTools.logD("assist");

                }

            }
        }

    }
}
