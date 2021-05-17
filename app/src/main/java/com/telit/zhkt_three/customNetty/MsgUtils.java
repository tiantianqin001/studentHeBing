package com.telit.zhkt_three.customNetty;

import com.google.gson.Gson;
import com.telit.zhkt_three.JavaBean.Communication.StudentJoinClassBean;
import com.telit.zhkt_three.JavaBean.InterActive.DiscussBean;
import com.telit.zhkt_three.JavaBean.InterActive.PaperStatus;
import com.telit.zhkt_three.JavaBean.StudentInfo;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.greendao.StudentInfoDao;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * author: qzx
 * Date: 2019/4/28 11:40
 */
public class MsgUtils {
    public static final String SEPARATOR = " ";//分隔符

    public static final String HEAD_HEART = "HeartBeat";//心跳

    public static final String HEAD_ACKNOWLEDGE = "Acknowledgement";//消息反馈

    public static final String HEAD_JOINCLASS = "JoinClass";//连接服务成功后加入班级
    public static final String HEAD_RECONNECT = "Reconnect";//第二次连接的时候发送重连命令
    public static final String HEAD_JOIN_CLASS_SUCCESS = "JoinClassSucess";//加入班级成功
    public static final String HEAD_RECONNECT_SUCCESS = "ReconnectSucess";//重新加入班级成功

    public static final String HEAD_OUT_OF_CLASS = "OutOfClass";//退出登录时引用

    public static final String HEAD_PRAISE = "Praise";//表扬
    public static final String HEAD_CRITICISM = "Criticism";//批评

    public static final String HEAD_START_CLASS = "StartClass";//上课
    public static final String HEAD_END_CLASS = "EndClass";//下课

    public static final String HEAD_LOCK = "LockScreen";//锁屏
    public static final String HEAD_UNLOCK = "UnlockScreen";//解锁

    public static final String HEAD_START_VOTE = "StartVote";//开始投票
    public static final String HEAD_END_VOTE = "EndVote";//结束投票

    public static final String HEAD_FIRST_ANSWER = "FirstAnswer";//开始抢答
    public static final String HEAD_SUCCESS_ANSWER = "SuccessAnswer";//显示谁抢答成功了
    public static final String HEAD_END_ANSWER = "EndAnswer";//结束抢答

    public static final String HEAD_RANDOM_NAME = "SuccessRoleCall";//随机点名结果

    public static final String HEAD_FILERECIEVE = "FileRecieve";//文件接收

    public static final int PAPER_RECIEVE = 1;
    public static final int PAPER_DOING = 2;
    public static final int PAPER_COMMIT = 3;
    public static final String HEAD_START_PRACTICE = "StartPractice";//开始练习
    public static final String HEAD_END_PRACTICE = "EndPractice";//结束练习
    public static final String HEAD_PRACTICE_STATUS = "PracticeStatus ";//练习状态

    public static final int TYPE_TEXT = 0;//聊天文本
    public static final int TYPE_PICTURE = 1;//聊天图片
    public static final String HEAD_START_DISCUSS = "StartDiscuss";//开始聊天
    public static final String HEAD_DISCUSS = "Discuss";//聊天信息
    public static final String HEAD_END_DISCUSS = "EndDiscuss";//结束聊天

    public static final String HEAD_FREE_DISCUSS = "FreeJoinGroup";//自由分组
    public static final String HEAD_FREE_JOIN_GROUP = "JoinDiscussGroup";//加入小组
    public static final String HEAD_DISCUSS_COMMIT_CONCLUSION = "SubmitDiscussConclusion";//提交结论通知
    public static final String SubmitQuestion = "SubmitQuestion"; // 学生端提交提问后给教师端发送这个消息
    public static final String EndJoinAnswerGroup = "EndJoinAnswerGroup"; // 学生端提交提问后给教师端结束这个消息

    /**
     * 接收的示例：PPTCommand SequenceNumber Type Id/r/n
     */
    public static final String HEAD_PPT_COMMAND = "PPTCommand";//WebView加载PPT互动

    public static final String HEAD_STOP_SCREEN_CAST = "StopScreenCast";//停止投屏

    public static final String HEAD_SCREEN_CAST = "ScreenCast";//开始投屏

    public static final String HEAD_STOP_BROADCAST = "StopScreenbroadcast";//停止屏幕广播

    public static final String HEAD_BROADCAST = "Screenbroadcast";//开始屏幕广播

    public static final String HEAD_SHARE_SHOT = "ShareScreenshot";//教师端截屏分享

    public static final String HEAD_SHUTDOWN = "ShutdownSdtDevice";//关机

    public static final String HEAD_FOCUS_SHARE = "FocusShare";//聚焦分享

    public static final String HEAD_START_ANSWERE = "StartAnswer";//提问
    public static final String HEAD_TUI_LIU_TEACHER = "TUI_LIU_TEACHER";//推流到教师端
    public static final String HEAD_WHILD_BOARDPUSH = "WhiteboardPush"; //接收到白班的推送
    public static final String HEAD_EndQuestion = "EndQuestion"; //教师端结束答题，不能提交了学生答题也要结束
    public static final String HEAD_StudentPadScreenCast = "StudentPadScreenCast"; //学生接收到教师端后开始推流
    public static final String HEAD_ScreenCastAddress = "ScreenCastAddress"; //学生发送到教师端后开始推流
    public static final String HEAD_StopStudentScreenCast = "StopStudentScreenCast"; //教师端结束推流
    public static final String HEAD_ANSWER_FENZHU_START = "FreeJoinAnswerGroup";//开始分组作答
    public static final String HEAD_ANSWER_FENZHU_END = "EndJoinAnswerGroup";//开始分组作答


    /**
     * @describe 消息反馈
     * @author luxun
     * create at 2017/5/5 0005 17:21
     * <p>
     * 注意：这个uuid是来自服务端带过来的,服务端去重,这个不要
     */
    public static String createAcknowledge(String fromServerUUID) {
        return HEAD_ACKNOWLEDGE + SEPARATOR + fromServerUUID + "\r\n";
    }

    /**
     * @describe 心跳信息
     * @author luxun
     * create at 2017/3/21 16:51
     */
    public static String heartMsg() {

//        QZXTools.logE("heartMsg=" + UserUtils.getStudentId(), null);
        return HEAD_HEART + SEPARATOR + MsgUtils.uuid() + "\r\n";
    }

    /**
     * @describe 退出登录命令
     */
    public static String outOfClass() {
        return HEAD_OUT_OF_CLASS + SEPARATOR + uuid() +
                SEPARATOR + UserUtils.getUserId() + "\r\n";
    }

    /*
     * 教师端推送地址
     *
     * */


    /**
     * 加入班级
     *
     * @param isReconnected 是否重连
     */
    public static String joinClass(boolean isReconnected) {
        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().
                queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

        StudentJoinClassBean studentJoinClassBean = new StudentJoinClassBean();
        studentJoinClassBean.setStudentName(studentInfo.getStudentName());
        studentJoinClassBean.setPhoto(studentInfo.getPhoto());
        studentJoinClassBean.setSex(studentInfo.getSex());
        studentJoinClassBean.setStudentId(studentInfo.getUserId());
        //加入ip   //本机IP
        studentJoinClassBean.setIp(QZXTools.getIPAddress());

//        StudentJoinClassBean studentJoinClassBean = new StudentJoinClassBean();
//        studentJoinClassBean.setStudentName("张青雪");
//        studentJoinClassBean.setPhoto("http://112.27.234.67:8089/uploadImage/1.jpg");
//        studentJoinClassBean.setSex("男");
//        studentJoinClassBean.setStudentId("70b45737e82b4e5d9b8fb0bc3605b7bd");
//        QZXTools.logE("studentJsonClass=" + studentJoinClassBean, null);
        Gson gson = new Gson();
        String infoJson = gson.toJson(studentJoinClassBean);

        QZXTools.logE("json=" + infoJson + "......." + isReconnected, null);

        if (isReconnected) {
            return HEAD_RECONNECT + SEPARATOR + uuid() + SEPARATOR + infoJson + "\r\n";
        } else {
            return HEAD_JOINCLASS + SEPARATOR + uuid() + SEPARATOR + infoJson + "\r\n";
        }
    }

    public static String uuid() {
        String uuid = UUID.randomUUID().toString();
        uuid = uuid.replace("-", "");
        return uuid;
    }

    /**
     * @describe 生成快速抢答消息
     * @author luxun
     * create at 2017/4/20 0020 19:44
     * <p>
     * 更改：由原来的用户ID变成完整的Student信息
     */
    public static String createFirstAnswer() {

        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().
                queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

        StudentJoinClassBean studentJoinClassBean = new StudentJoinClassBean();
        studentJoinClassBean.setStudentName(studentInfo.getStudentName());
        studentJoinClassBean.setPhoto(studentInfo.getPhoto());
        studentJoinClassBean.setSex(studentInfo.getSex());
        studentJoinClassBean.setStudentId(studentInfo.getUserId());

        Gson gson = new Gson();
        String infoJson = gson.toJson(studentJoinClassBean);

        return HEAD_FIRST_ANSWER + SEPARATOR + uuid() + SEPARATOR + infoJson + "\r\n";
    }

    /**
     * 随堂练习的状态
     */
    public static String createPracticeStatus(int status) {
        Gson gson = new Gson();
        return HEAD_PRACTICE_STATUS + SEPARATOR + gson.
                toJson(new PaperStatus(UserUtils.getStudentId(), status)) + "\r\n";
    }

    /**
     * @describe 生成聊天的消息对象
     * @author luxun
     * create at 2017/3/21 16:48
     */
    public static DiscussBean getDiscussBean(String content, String thumbnail, int type, int discussGroupId, String groupIndex) {
        StudentInfoDao studentInfoDao = MyApplication.getInstance().getDaoSession().getStudentInfoDao();
        StudentInfo studentInfo = studentInfoDao.queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

        //注意聊天信息是自增的所以不要给id赋值
        DiscussBean discussBean = new DiscussBean();
        discussBean.setContent(content);
        discussBean.setDiscussId("");
        discussBean.setDiscussGroupId(discussGroupId + "");
        discussBean.setGroupIndex(groupIndex);
        discussBean.setPhoto(studentInfo.getPhoto());
        discussBean.setStudentId(studentInfo.getUserId());
        discussBean.setType(type);
        discussBean.setTime(System.currentTimeMillis());
        discussBean.setThumbnail(thumbnail);
        discussBean.setStudentName(studentInfo.getStudentName());

        return discussBean;
    }

    /**
     * @describe 生成聊天消息
     * @author luxun
     * create at 2017/3/17 19:04
     */
    public static String createDiscuss(DiscussBean bean) {
        Gson gson = new Gson();
        return HEAD_DISCUSS + SEPARATOR + uuid() + SEPARATOR + gson.toJson(bean) + "\r\n";
    }

    /**
     * 新版的讨论内容发送给服务端
     * 示例：Discuss SequenceNumber GroupId StudentName Content/r/n
     */
    public static String createNewDiscuss() {
        StringBuffer stringBuffer = new StringBuffer();

        return "";
    }


    /**
     * 添加发送给教师端的信息
     *
     * @param headInfo
     * @return
     */
    public static String teacherAddress(String headInfo, String trspUrl) {
        StudentInfo studentInfo = MyApplication.getInstance().getDaoSession().getStudentInfoDao().
                queryBuilder().where(StudentInfoDao.Properties.UserId.eq(UserUtils.getUserId())).unique();

        StudentJoinClassBean studentJoinClassBean = new StudentJoinClassBean();
        studentJoinClassBean.setStudentName(studentInfo.getStudentName());
      /*  studentJoinClassBean.setPhoto(studentInfo.getPhoto());
        studentJoinClassBean.setSex(studentInfo.getSex());*/
        studentJoinClassBean.setStudentId(studentInfo.getUserId());
        //加入ip   //本机IP
        studentJoinClassBean.setIp(QZXTools.getIPAddress());
        studentJoinClassBean.setRtspUrl(trspUrl);

//        StudentJoinClassBean studentJoinClassBean = new StudentJoinClassBean();
//        studentJoinClassBean.setStudentName("张青雪");
//        studentJoinClassBean.setPhoto("http://112.27.234.67:8089/uploadImage/1.jpg");
//        studentJoinClassBean.setSex("男");
//        studentJoinClassBean.setStudentId("70b45737e82b4e5d9b8fb0bc3605b7bd");
//        QZXTools.logE("studentJsonClass=" + studentJoinClassBean, null);
        Gson gson = new Gson();
        String infoJson = gson.toJson(studentJoinClassBean);


        return headInfo + SEPARATOR + uuid() +
                SEPARATOR + infoJson + "\r\n";
    }


    /**
     * 发送选择的组
     */
    public static String selectedGroup(String json) {
        return HEAD_FREE_JOIN_GROUP + SEPARATOR + uuid() + SEPARATOR + json + "\r\n";
    }

    /**
     * 发送提问消息
     */
    public static String SubmitQuestion() {
        return SubmitQuestion + SEPARATOR + uuid() + SEPARATOR + UserUtils.getUserId() + "\r\n";
    }


    /*结束提问的消息*/
    public static String EndJoinAnswerGroup() {
        return EndJoinAnswerGroup + SEPARATOR + uuid() + SEPARATOR + UserUtils.getUserId() + "\r\n";
    }

    /**
     * 分组结论上传反馈
     */
    public static String feedbackConclusionEnd(String groupId, String groupIndex) {

        String data = "{\"discussGroupId\":" + groupId + ",\"groupIndex\":" + groupIndex + "}";

        QZXTools.logE("pre msg=" + data, null);

//        try {
//            new URI(data).getPath();
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//            QZXTools.logE("UnsupportedEncodingException", null);
//        }

        try {
            data = URLEncoder.encode(data, String.valueOf(Charset.forName("UTF-8")));
            QZXTools.logE("last msg=" + data, null);
        } catch (Exception e) {
            e.printStackTrace();
            QZXTools.logE("UnsupportedEncodingException", null);
        }

        String msg = HEAD_DISCUSS_COMMIT_CONCLUSION + SEPARATOR + uuid() + SEPARATOR + data + "\r\n";

        return msg;
    }
}
