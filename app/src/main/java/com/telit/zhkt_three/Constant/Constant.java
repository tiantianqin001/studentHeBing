package com.telit.zhkt_three.Constant;

/**
 * author: qzx
 * Date: 2019/5/10 13:37
 */
public class Constant {


    /**
     * 设置okhttp请求超时后重发的次数,可以重发两次，共三次机会
     * 有这项设定的在互动页面的：随堂练习、投票、分组讨论
     */
    public static final int TIME_OUT_RETRY_COUNT = 2;

    public static final int INSTALL_PACKAGES_REQUEST_CODE = 0x7;
    public static final int GET_UNKNOWN_APP_SOURCES = 0x8;
    public static final String CAN_INSTALL = "CAN_INSTALL";

    public static final String Update_Avatar = "CAN_UPDATE_AVATAR";
    //发送我的组播班级地址
    public static final String SEND_MULTICAST = "SEND_MULTICAST";
        //领创app 列表
    public static final String LINGCHUANG_APP_LIST = "LINGCHUANG_APP_LIST";
    //更新状态
    public static final String upDataState = "upDataState";


    //------------------------------System App----------------------------------
    /**
     * App新增、更新、删除的EVENTBUS标签
     */
    public static final String EVENT_TAG_APP = "app_change";

    //添加ssdp 的协议名称
   // public static final String SSDP_XIE_YI="_telitWisdomClass._tcp.";
    public static final String SSDP_XIE_YI="_p2pchat._udp.";

    /**
     * 有APP安装完成
     */
    public static final int APP_NEW_ADD = 1;
    /**
     * 有APP删除
     */
    public static final int APP_DELETE = 2;
    /**
     * 有APP更新完成
     */
    public static final int APP_UPDATE = 3;
    //--------------------------------System App----------------------------------

    //------------------------------SummerLayout------------------------------
    /**
     * SummerLayout固定的子视图个数
     */
    public static final int SPIN_VP_COUNT = 12;
    //------------------------------SummerLayout------------------------------

    //------------------------------------Socket Link---------------------------------------------------
    /**
     * 开启Socket服务连接
     */
    public static final String SOCKET_CONNECT_ACTION = "com.telit.zhkt_three.socket.connect";


    public static final int Socket_Fix_Port = 8004;
    //public static final int Socket_Fix_Port = 17113;

    /**
     * 心跳间隔
     */
    public static final int HEART_INTERVAL = 5;

    /**
     * 重连延迟
     */
    public static final int RECONNECT_DELAY = 5;

    /**
     * 读取超时
     */
    public static final int READ_TIME_OUT = 8;

    public static final int OnLine = 0;
    public static final int OffLine = 1;
    public static final int ReceiveMessage = 2;
    public static final int isNotUser = 3;
    //------------------------------------Socket Link---------------------------------------------------

    //-----------------------------------QuestionType---------------------------------------------------
    public static final int Single_Choose = 0;
    public static final int Multi_Choose = 1;
    public static final int Fill_Blank = 2;
    public static final int Subject_Item = 3;
    public static final int Linked_Line = 4;
    public static final int Judge_Item = 5;

    public static final String Retry_Status = "-1";//重做
    public static final String Todo_Status = "0";//未做
    public static final String Commit_Status = "1";//提交
    public static final String Review_Status = "2";//批阅
    public static final String Save_Status = "-2";//保存

    public static final String Level_Easy = "0";//易
    public static final String Level_Normal = "1";//中等
    public static final String Level_Hard = "2";//难

    //EventBus
    public static final String Subjective_Camera_Callback = "from_sub_camera";
    public static final String Subjective_Board_Callback = "from_sub_board";
    public static final String Homework_Commit = "commit_homework";
    //接收到作业结束的
    public static final String Homework_Commit_end="Homework_Commit_end";
    public static final String Homework_Commit_Success="Homework_Commit_Success";
    //作业提交
    public static final String Homework_Commit_Success_Tijiao="Homework_Commit_Success_Tijiao";
    public static final String Question_Collect_Success="Question_Collect_Success";

    public static final String Cloud_Share_Collect_Success="Cloud_Share_Collect_Success";
    public static final String Resource_Share_Collect_Success="Resource_Share_Collect_Success";

    //ItemBank
    public static final int ItemBank_Single = 1;//题库单选
    public static final int ItemBank_Multi = 2;//题库多选
    public static final int ItemBank_Judge = 3;//题库判断
    public static final int ItemBank_Fill = 4;//题库填空
    public static final int ItemBank_Subject = 9;//题库问答
    //-----------------------------------QuestionType---------------------------------------------------

    //----------------------------------Module    废弃不用了---------------------------------------------
//    public static final int Interactive = 0;//课堂互动
//    public static final int Preview = 1;//课前预习
//    public static final int Homework = 2;//课后作业
//    public static final int MistakesCollection = 3;//错题集
//    public static final int AutoLearning = 4;//自主学习
//    public static final int PersonalSpace = 5;//个人空间说明
//    public static final int ClassRecord = 6;//课堂记录
//    public static final int ToExpected = 7;
//    public static final int MicroClazz = 8;//微课中心
    //----------------------------------Module---------------------------------------------

    //---------------------------------AutonomousLearning----------------------------------
    public static final int Micro_Lesson = 0;//微课
    public static final int Audio_Resource = 1;
    public static final int Picture_Resource = 2;
    public static final int Teaching_Metarial = 3;
    public static final int Item_Bank = 4;
    //---------------------------------AutonomousLearning----------------------------------

    //------------------------------Oauth2.0------------------------------------

    //    public static final String EduAuthAppKey = "d51c6b2272fc4075ba2b3471549fb64b";
    public static final String EduAuthAppKey = "5e220af8efc04c1bbc6ba97f6f7cf9f7";
    public static final String EduAuthPassword = "http://open.ahjygl.gov.cn";

    /**
     * 是否是省平台登录模式
     */
//    public static boolean IsOauthMode = false;

    //------------------------------Oauth2.0------------------------------------

    public static final String Event_Choose_Tree = "choose_tree";

    public static final String Vote_Commit = "vote_commit";

    public static final String Discuss_Message = "discuss_msg";

    public static final String Discuss_Send_Pic = "discuss_send_pic";

    public static final String DESKey = "zhihuiketang3.0";

    public static final String StopJpush = "stop_jpush";

    public static final String Auto_Learning_Update = "Auto_Learning_Update";

    public static final String Group_Conclusion_Pic = "group_conclusion_pic";

    public static final String Group_Conclusion_Video = "group_conclusion_video";

    public static final String Group_Conclusion_Words = "group_conclusion_words";

    public static final String Play_Audio_Completed = "audio_play_completed";

    public static final String Show_Conclusion = "ShowConclusion";

    public static final String Show_Js_Record = "JsRecordScreen";

    public static final String Free_Theme_Over = "FreeThemeOver";

    public static final String Close_Discuss_Img = "CloseDiscussImg";

    public static final String Screen_Record_file = "ScreenRecordFile";

    //-------------------------PPT Type---------------------------------
    public static final int InterestClassification = 1;//趣味分类
    public static final int GuessingGgame = 2;//猜词游戏
    public static final int Matching = 3;//连线题
    public static final int FlipCard = 4;//翻翻卡
    public static final int XMind = 5;//思维导图
    public static final int SuperClassification = 6;//超级分类
    public static final int FillBlank = 7;//填空题
    public static final int Classwork = 8;//课堂作业
    public static final int NetResource = 9;//网络资源


    //---------------------------PreCloud
    public static final String CLICK_CLOUD_ITEM = "click_cloud_item";
    public static final String click_cloud_item_ping_jia = "click_cloud_item_ping_jia";
    public static final String UPDATE_CACHE_VIEW = "update_cache_view";
    public static final String click_cloud_item_ping_jia_submit = "click_cloud_item_ping_jia_submit";
    public static final String CLICK_CLOUD_COLLECTION_ITEM = "click_cloud_item";

    //----------------------------ClassRecord Type
    public static final int Class_Record_Shot = 1;//来自截屏
    public static final int Class_Record_Vote = 2;//来自投票
    public static final int Class_Record_Discuss = 3;//来自分组讨论

    // NewRVClassRecordAdapter 的ViewHolder类型
    public static final int Head_ClassRecord = 1;
    public static final int Mid_ClassRecord = 2;
    public static final int Foot_ClassRecord = 3;
    public static final int End_Foot = 4;
    public static final int Only_One_Row = 5;

    public static  String RtmpUrl="rtmp://";

}
