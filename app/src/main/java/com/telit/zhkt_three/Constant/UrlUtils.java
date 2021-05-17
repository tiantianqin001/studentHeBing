package com.telit.zhkt_three.Constant;

/**
 * author: qzx
 * Date: 2019/5/9 17:15
 */
public class UrlUtils {
    //家里服务器
    public static String BaseUrl = "http://wisdomclass.ahtelit.com";
    public static String MaiDianUrl = "http://wisdomclass.ahtelit.com";
    public static String ImgBaseUrl = "http://wisdomclass.ahtelit.com/Resource/";
    public static String WSBaseUrl = "ws://wisdomclass.ahtelit.com";

    //正式的服务器
/*    public static String BaseUrl = "http://resource.ahtelit.com";
    public static String MaiDianUrl = "http://resource.ahtelit.com";
    public static String ImgBaseUrl = "http://resource.ahtelit.com/Resource/";
    public static String WSBaseUrl = "ws://resource.ahtelit.com";*/

    //袁艺海
/*    public static String BaseUrl = "http://172.16.4.108:8080";
    public static String MaiDianUrl = "http://172.16.4.108:8080";
    public static String ImgBaseUrl = "http://172.16.4.108:8080/Resource/";
    public static String WSBaseUrl = "ws://resource.ahtelit.com";*/

    //  祁能飞
/*    public static String BaseUrl = "http://172.16.4.168:8080";
    public static String MaiDianUrl = "http://172.16.4.168:8080";
    public static String ImgBaseUrl = "http://172.16.4.168:8080/Resource/";*/
    //正式的服务器
    /*public static String BaseUrl = "http://resource.ahtelit.com";
    public static String MaiDianUrl = "http://resource.ahtelit.com";
    public static String ImgBaseUrl = "http://resource.ahtelit.com/Resource/";*/



/*
      public static String BaseUrl = "http://172.16.4.220:8080";
     public static String MaiDianUrl = "http://172.16.4.220:8080";
     public static String ImgBaseUrl = "http://172.16.4.220:8080/Resource/";*/
/*      public static String BaseUrl = "http://172.16.4.149:8080";
     public static String MaiDianUrl = "http://172.16.4.149:8080";
     public static String ImgBaseUrl = "http://172.16.4.149:8080/Resource/";*/
    //任强

 /*    public static String BaseUrl = "http://192.168.11.23:8080";
    public static String MaiDianUrl = "http://192.168.11.23:8080";

   /*  public static String BaseUrl = "http://192.168.3.89:8080";
    public static String MaiDianUrl = "http://192.168.3.89:8080";
    public static String ImgBaseUrl = "http://192.168.3.89:8080/Resource/";*/


    //昂胜松
     /*public static String BaseUrl = "http://172.16.4.184:8080";
     public static String MaiDianUrl = "http://172.16.4.184:8080";
     public static String ImgBaseUrl = "http://172.16.4.184:8080/Resource/";
    public static String WSBaseUrl = "ws://172.16.4.184:8080";*/

    //蒋福源
  /*  public static String BaseUrl = "http://192.168.11.39:8080";
    public static String MaiDianUrl = "http://192.168.11.39:8080";
    public static String ImgBaseUrl = "http://192.168.11.38:8080/Resource/";
    public static String WSBaseUrl = "ws://wisdomclass.ahtelit.com";*/

    //孙亮亮
 /*      public static String BaseUrl = "http://172.16.4.237:8080";
   /*    public static String BaseUrl = "http://172.16.4.237:8080";
    public static String MaiDianUrl = "http://172.16.4.237:8080";
    public static String ImgBaseUrl = "http://172.16.4.237:8080/Resource/";*/
    //public static String SocketIp = "172.16.5.77";
    //public static String SocketIp = "172.16.5.39";
    //public static String SocketIp = "192.168.43.139";
    //public static String SocketIp = "192.168.0.120";

    //吕冰
/*     public static String BaseUrl = "http://172.16.10.188:8080";
    public static String MaiDianUrl = "http://172.16.10.188:8080";
    public static String ImgBaseUrl = "http://172.16.10.188:8080/Resource/";*/

    /*public static String BaseUrl = "http://172.16.4.123:8080";
    public static String MaiDianUrl = "http://172.16.4.123:8080";
    public static String ImgBaseUrl = "http://172.16.4.123:8080/Resource/";*/

     /*public static String SocketIp = "172.16.4.123";
    public static int SocketPort = 8080;*/

    /*public static String BaseUrl = "http://172.16.4.109:8080";
    public static String MaiDianUrl = "http://172.16.4.109:8080";
    public static String ImgBaseUrl = "http://172.16.4.109:8080/Resource/";*/

/*    public static String BaseUrl = "http://172.16.4.130:8080";
    public static String MaiDianUrl = "http://172.16.4.130:8080";
    public static String ImgBaseUrl = "http://172.16.4.130:8080/Resource/";*/
/*
      public static String BaseUrl = "http://172.16.4.136:8080";
      public static String MaiDianUrl = "http://172.16.4.136:8080";
      public static String ImgBaseUrl = "http://172.16.4.136/Resource/";*/

    public static String OfficeUrl = "https://view.officeapps.live.com/op/view.aspx?src=";

    public static String SocketIp = "192.168.101.50";
    public static int SocketPort = 17113;

    /**
     * 获取班级ip和端口     * 示例：
     * /wisdomclass/interface/interaction/getValueCacheByKey
     */
    public static final String GetIpAndPort = "/wisdomclass/interface/interaction/getValueCacheByKey";

//    public static String OldBaseUrl = "http://60.166.13.165:8088";

    //UnityResource 知筑学院
//    public static String UnityUrl = "http://172.16.5.158:8080/courses";

//    public static final String hcUrl = "http://172.16.5.159:8081";
//    public static final String xbUrl = "http://172.16.5.160:8090";
    /**
     * Js提交随堂练习答案
     */
    public static final String CommitJsPractice = "/wisdomclass/interface/interaction/saveStudentAnswer";

    /**
     * Communication Ip
     * http://172.16.5.159:8081/wisdomclass/interface/usercenter/selectIp?classid=
     */
    public static final String CommunicationIp = "/wisdomclass/interface/usercenter/selectIp";//获取通信服务IP

    /**
     * WebView加载的选词填空
     * 传入参数有：flag 表示互动类型数值 id 表示互动的id
     * sign 表示一种标记：编辑或者展示，学生端用view表示展示
     * type 表示是教师端(teacher)还是学生端(student)
     * studentId 表示学生的唯一标志，这里用userId
     * 示例：
     * http://172.16.5.159:8081/wisdomclass/interface/interaction/jumpToEdit?flag=7&id=123457047&sign=view&type=student&studentId=210
     */
    public static final String WebViewInteract = "/wisdomclass/interface/interaction/jumpToEdit";

    /**
     * 知筑学院 查询可用的所有学段以及对应的学科
     * Get方式 不需要参数
     * 示例：http://localhost:8080/courses/queryUnityCondition
     */
    public static final String UnityResourceConditionXDAndSubject = "/wisdomclass/interface/unity/queryUnityCondition";

    /**
     * 知筑学院 查询依据学段、学科查询的章节信息
     * 必传的两个参数 xd学段 【小学0 初中1 高中2】 subject学科
     * 示例：http://localhost:8080/courses/queryChapterByXdAndSubject?xd=0&subject=1
     */
    public static final String UnityResourceConditionChapter = "/wisdomclass/interface/unity/queryChapterByXdAndSubject";

    /**
     * 知筑学院 查询所有可用的资源内容
     * 要传入的必要参数 xd学段 subject学科 chapter章节
     * 非必要参数
     * resourceType资源类型【视频0 VR 1 AR2】 pageSize一页获取的条数默认是9条 pageNo当前的页码数默认是1，从一开始
     * contentName 可模糊查询的资源内容名称
     * 示例：http://localhost:8080/courses/queryAvailableContent?xd=2&subject=9&chapter=细胞学&resourceType&pageSize&pageNo&contentName
     */
    public static final String UnityResourceContent = "/wisdomclass/interface/unity/queryAvailableContent";
    /**
     * 获取用户信息
     * 示例：http://open.ahjygl.gov.cn/sso-oauth/getUserByUserId?userId=
     */
    public static final String EduGetUserInfo = "http://open.ahjygl.gov.cn/sso-oauth/getUserByUserId";
    /**
     * 学生登录认证模式
     * type="student"
     * 示例：
     * http://172.16.4.102:8080/wisdomclass/toLoginByOauth?userId=9c5e8fc2cc6e5a197b4ea823497f3da7e11c63b33fa2c7c3b1d76c42aa45b88c746fcb62bd7ab0fb00131e0cb389bea9a6b7c3b97860a87a&type=student&deviceId=
     */
    public static final String OauthLogin = "/wisdomclass/toLoginByOauth";

    /**
     * 学生登录
     * 示例：http://60.166.13.165:8088/wisdomclass/toLogin?username=xs006&password=000000&type=student&deviceId=
     */
    public static final String Login = "/wisdomclass/toLogin";

    /**
     * 上传修改的头像
     * 示例：
     * http://60.166.13.165:8088/wisdomclass/interface/usercenter/uploadUserPhoto?userid=&attachement=
     * 注意：attachement是文件集头像图片
     */
    public static final String AlterAvatar = "/wisdomclass/interface/usercenter/uploadUserPhoto";

    /**
     * 自主学习知识点章节查询
     * 参数要传入学段、学科以及知识点ID
     * 示例：http://172.16.4.82:8080/wisdomclass/interface/questionknowledge/queryQuestionKnowledge?xd=1&chid=2&isQueryParams=true&knowledgeId=0
     */
    public static final String QueryKnowledgeChapter = "/wisdomclass/interface/questionknowledge/queryQuestionKnowledge";

    /**
     * 自主学习知识点学段查询
     * 示例：http://172.16.4.82:8080/wisdomclass/interface/questionknowledge/queryQuestionPhase
     */
    public static final String QueryKnowledgeSection = "/wisdomclass/interface/questionknowledge/queryQuestionPhase";

    /**
     * 自主学习知识点学科以及年级查询
     * 参数要传入学段  0:查询知识点  1查询教材
     * 查询知识点只需要xd=1&isSearchChapter=1
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/questionknowledge/queryQuestionParam?xd=1&isSearchChapter=1&chid=3&press=23087
     */
    public static final String QueryKnowledgeSubjectGrade = "/wisdomclass/interface/questionknowledge/queryQuestionParam";

    /**
     * 作业换一个pad 载次登录作业能保存
     */

    public static final String StuSaveHomeWork = "/wisdomclass/interface/homework/stuSaveHomeworks";

    /**
     * 收藏列表
     * <p>
     * <p>
     * 示例：
     * http://172.16.4.40:8090//wisdomclass/interface/homework/listCollectQuestion
     */
    public static final String CollectQuestionList = "/wisdomclass/interface/homework/listCollectQuestion";

    /**
     * 收藏
     * 参数要传入学段  0:查询知识点  1查询教材
     * 查询知识点只需要xd=1&isSearchChapter=1
     * 示例：
     * http://172.16.4.40:8090//wisdomclass/interface/homework/listCollectQuestion
     */
    public static final String CollectQuestionYesOrNo = "/wisdomclass/interface/homework/collectQuestion";

    /**
     * 题库出题导出
     * 参数要传入学段  0:查询知识点  1查询教材
     * 查询知识点只需要xd=1&isSearchChapter=1
     * 示例：
     * http://172.16.4.40:8090//wisdomclass/interface/homework/listCollectQuestion
     */
    public static final String Homework_Export = "/wisdomclass/interface/homework/homeworkExport";
    public static final String Homework_Export_Image = "/wisdomclass/interface/homework/handExport";
    public static final String Mistake_Collection_Export_Image = "/wisdomclass/interface/homework/wrongListExport";

    /**
     * 收藏
     * 参数要传入学段  0:查询知识点  1查询教材
     * 查询知识点只需要xd=1&isSearchChapter=1
     * 示例：
     * http://localhost:8080/api/v3/disk/updateCollectionState?studentId=66666759296&shareId=817952a822a34f74b1d6f0c8be77497f&collectionState=1
     */
    public static final String CollectShareYesOrNo = "/api/v3/disk/updateCollectionState";

    /**
     * 自主学习知识点题目的查询
     * （出版社press 章节chapter 知识点tKnowledge questionChannelType题型 difficultIndex难度
     * gradeId年级 sortType排序字段 pageNo当前页数 pageSize一页数量 chapterIds教材id tKnowledge知识点id）
     * 示例：
     * 教材：必传：学段xd 学科chid
     * http://172.16.4.40:8090/wisdomclass/interface/questionbank/queryQuestion?chapterIds=23089&xd=1&chid=3
     * <p>
     * 知识点：http://172.16.4.82:8080/wisdomclass/interface/questionbank/queryQuestion?xd=1&chid=2&chapter=
     * &pageNo=2&pageSize=10&questionChannelType=0&difficultIndex=0
     */
    public static final String QuestionKnowledgeBankQuery = "/wisdomclass/interface/questionbank/queryQuestion";
    public static final String QuestionKnowledgeBankQuery1 = "/wisdomclass/interface/questionbank/queryQuestionN";

    /**
     * 自主学习教材点击进入查询章节
     * isQueryParams=true表示一并查询题型以及困难程度
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/questionknowledge/queryQuestionChapter?xd=1&chid=2
     * &isQueryParams=true&chapterId=26252
     */
    public static final String QuestionKnoledgeBookChapter = "/wisdomclass/interface/questionknowledge/queryQuestionChapter";

    /**
     * 作业详情
     * 示例：
     * http://60.166.13.165:8088/wisdomclass/interface/homework/handDetail?homeworkid=2a7f455741ac4a0ab1ede27a88d3b8ec
     * &status=0&studentid=00adbceb06ba4a3b9dbbd50b8134e6eb
     */
    public static final String HomeWorkDetailsByHand = "/wisdomclass/interface/homework/handDetail";

    /**
     * 学习资源     * 示例：
     * http://60.166.13.165:8088//wisdomclass/interface/homework/listHomeworkResource
     * &status=0&studentid=00adbceb06ba4a3b9dbbd50b8134e6eb
     */
    public static final String LearnResource = "/wisdomclass/interface/homework/listHomeworkResource";

    /**
     * 典型答案     * 示例：
     * http://60.166.13.165:8088//wisdomclass/interface/homework/listHomeworkResource
     * &status=0&studentid=00adbceb06ba4a3b9dbbd50b8134e6eb
     */
    public static final String TypicalAnswers = "/wisdomclass/interface/homework/listFineAnswerAndWrongAnswer";

    /**
     * 收藏详情
     * 示例：
     * http://60.166.13.165:8088/wisdomclass/interface/homework/handDetail?homeworkid=2a7f455741ac4a0ab1ede27a88d3b8ec
     * &status=0&studentid=00adbceb06ba4a3b9dbbd50b8134e6eb
     */
    public static final String CollectQuestionDetailsByHand = "/wisdomclass/interface/homework/detailCollectQuestion";

    /**
     * 作业详情
     * two
     */
    public static final String HomeWorkDetailsByHandTwo = "/wisdomclass/interface/homework/homeworkDetail";

    /**
     * 登录或退出登录统计
     * delflag 登录：0，下线：1
     * roletype 学生：1，教师：0
     * 示例：
     * http://60.166.13.165:8088/wisdomclass/interface/loginRecord/loginRec?classid=7d83f294-41d1-4149-b0e3-a46fc325be7d
     * &userid=66666727666&roletype=1&delflag=
     */
    public static final String LoginInOutRecord = "/wisdomclass/interface/loginRecord/loginRec";

    /**
     * 课程表
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/usercenter/selectSchedule?classid=a6e3bc77-7865-4a27-a520-eb1d7881446f
     */
    public static final String TimeTable = "/wisdomclass/interface/usercenter/selectSchedule";

    /**
     * 家庭作业提交
     * 示例：
     * http://60.166.13.165:8088/wisdomclass/interface/homework/commitHomeWork?
     * answerlist=&studentid=&classid=&homeworkid=&question_ids=&question_files=
     * <p>
     * answerlist和question_ids是Json字符串
     */
    public static final String HomeWorkCommit = "/wisdomclass/interface/homework/commitHomeWork";

    /**
     * 应用更新
     */
    /**
     * 应用更新         //版本升级 updateType 0是内网   1是外网
     */
    public static String AppUpdate = "/wisdomclass/interface/soft/softwareUpdate?softType=1";

    /**
     * 错题集科目列表
     * 示例：http://172.16.4.40:8090/wisdomclass/interface/usercenter/selectSubjects?studentid=
     */
    public static final String MistakesSubjectList = "/wisdomclass/interface/usercenter/selectSubjects";

    /**
     * 错题集详情列表
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/homework/wrongListDetail?
     * studentid=
     * &questionType=0&difficultIndex=1&startTime=2019-06-18 00:00:00&endTime=2019-6-19 00:00:00&pageNo=1&subjectid=0
     */
    public static final String MistakesDetails = "/wisdomclass/interface/homework/wrongListDetail";

    /**
     * 错题集优秀答案列表
     * 示例：
     * http://172.16.5.160:8090/api/v3/homework/getBestStudentAnswer?questionId=415635&homeworkId=cee7c4230d7b4b38a7a8abdb22f97747
     * &studentId=66666864451
     */
    public static final String PerfectAnswerLists = "/api/v3/homework/getBestStudentAnswer";

    /**
     * 未完成家庭作业
     * 示例：http://60.166.13.165:8088/wisdomclass/interface/homework/unCommitList?studentid=&pageNo=&classid=&pageSize=&token=
     */
    public static final String ToDoHomeWork = "/wisdomclass/interface/homework/unCommitList";

    /**
     * 已完成家庭作业
     * 示例：
     * http://60.166.13.165:8088/wisdomclass/interface/homework/commitedList?studentid=&pageNo=&classid=&pageSize=&token=
     */
    public static final String CompletedHomeWork = "/wisdomclass/interface/homework/commitedList";

    /**
     * 资源条件
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/usercenter/selectResSelOpt?schoolid=
     */
    public static final String ConditionResource = "/wisdomclass/interface/usercenter/selectResSelOpt";

    /**
     * 资源条件
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/dictList/listDictByTypeCodeArray
     */
    public static final String ConditionRead = "/wisdomclass/interface/dictList/listDictByTypeCodeArray";

    /**
     * 资源
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/resource/selectResource?pageNo=&pageSize=30&suffix=
     * &gradeid=&subjectid=&term=&press=
     */
    public static final String OldResource = "/wisdomclass/interface/resource/selectResource";

    /**
     * 阅读
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/resource/selectResource?pageNo=&pageSize=30&suffix=
     * &gradeid=&subjectid=&term=&press=
     */
    public static final String OldRead = "/wisdomclass/interface/resource/selectResource";

    /**
     * 2.0教材资源资源下载
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/resource/downLoadRes?id=&userid=
     */
    public static final String ElectronicBookDownload = "/wisdomclass/interface/resource/downLoadRes";

    /**
     * 2.0普通资源下载
     * 示例：
     * http://112.27.234.68:8088/SrcPlatform/interface/downLoadRes?id=4461529a239a487ca565e1c223451f24&userid=66666702506
     */
    public static final String CommonResourceDownload = "http://139.196.8.226:8086/SrcPlatform/interface/downLoadRes";

    /**
     * 白板上传
     * 示例
     * http://172.16.4.40:8090/wisdomclass/interface/usercenter/uploadWhiteBlockFile?attachement=
     */
    public static final String WhiteBoardUpload = "/wisdomclass/interface/usercenter/uploadWhiteBlockFile";

    /**
     * 互动之投票详情
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/vote/selectVoteDetail?id=e15f213b00e89041000ab03043c8bbe88d42
     */
    public static final String VoteContent = "/wisdomclass/interface/vote/selectVoteDetail";

    /**
     * 新版查询投票：
     * http://172.16.5.159:8081/wisdomclass/interface/vote/queryById?id=15
     */
    public static final String QueryVote = "/wisdomclass/interface/vote/queryById";

    /**
     * 互动之投票提交
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/vote/commitVotes?voteid=&studentid=&answernum=&useTime=
     */
    public static final String VoteCommit = "/wisdomclass/interface/vote/commitVotes";

    /**
     * 互动之投票结果展示
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/vote/getVoteResult?voteid=
     * e15f213b00e89041000ab03043c8bbe88d42&studentid=0442b5d559f74afeada49f51bdfc229a
     */
    public static final String VoteResult = "/wisdomclass/interface/vote/getVoteResult";

    /**
     * 投票结果
     * 172.16.5.159:8081/wisdomclass/interface/vote/findStudentVoteResult?voteId=14
     */
    public static final String VoteResultTwo = "/wisdomclass/interface/vote/findStudentVoteResult";

    /**
     * 互动之随堂练习
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/classexam/classExamDetail?classexamid=&studentid=&status=
     */
    public static final String ClassPractice = "/wisdomclass/interface/classexam/classExamDetail";

    /**
     * 互动之随堂练习提交
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/classexam/commitClassExam?
     * answerlist=&studentid=&classid=&classexamid=&question_ids=&question_files=
     */
    public static final String PracticeCommit = "/wisdomclass/interface/classexam/commitClassExam";

    /**
     * 聊天组分组信息
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/discuss/groupImglist?studentid=&id=
     */
    public static final String DiscussGroup = "/wisdomclass/interface/discuss/groupImglist";

    /**
     * 新版 分组讨论信息
     * 示例：
     * http://172.16.5.159:8081/wisdomclass/interface/discussion/findDiscussionInfo?userId=456&discussId=2&classId=123456
     */
    public static final String DiscussGroupTwo = "/wisdomclass/interface/discussion/findDiscussionInfo";

    /**
     * 自由讨论选主题加入
     * 示例：
     * http://172.16.5.159:8081/wisdomclass/interface/discussion/findFreeDiscussionInfo?discussId=143
     */
    public static final String DiscussSelectTheme = "/wisdomclass/interface/discussion/findFreeDiscussionInfo";

    /**
     * 自由分组提问的加入
     */
    public static final String GetDiscussGroupAnstor = "/wisdomclass/interface/discussion/updateStudentGroup";

    /**
     * 聊天的图片上传
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/usercenter/returnPicUrl?attachement=
     */
    public static final String DiscussImgUpload = "/wisdomclass/interface/usercenter/returnPicUrl";

    /**
     * 分组讨论记录员提交总结
     * 示例：
     * http://172.16.5.159:8081/wisdomclass/interface/discussion/insertGroupConclusion?
     * discussGroupId=1&groupConclusion=再见再也不见&file=
     */
    public static final String DiscussConclusion = "/wisdomclass/interface/discussion/insertGroupConclusion";

    /**
     * 课堂记录
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/countInteration/queryByStudentId?
     * studentId=9c5e8fc2cc6e5a19d98680b25034d88e51a653d9d439243eecde5abcee47aef15b4bce7658e91718dfb747bdf8fbdc72938759ef9d3673e0
     * &pageNo=1&pageSize=10
     */
    public static final String ClassRecord = "/wisdomclass/interface/countInteration/queryByStudentId";

    /**
     * 课堂记录新版
     * 示例：
     * http://172.16.5.159:8081/wisdomclass/interface/operationLog/findClassRecort?studentId=123&pageNo=1&pageSize=1
     */
    public static final String ClassRecordTwo = "/wisdomclass/interface/operationLog/findClassRecort";

    /**
     * 选择互动班级
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/usercenter/queryOnlineIpBySchoolId?schoolId=bb89f706c3a04cfdbb69ea37e481c6ae
     */
    public static final String SelectClass = "/wisdomclass/interface/usercenter/queryOnlineIpBySchoolId";

    /**
     * 收藏随堂练习查询
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/mycollect/queryMyCollect?userId=&pageNo=1&pageSize=10
     */
    public static final String CollectQuery = "/wisdomclass/interface/mycollect/queryMyCollect";

    /**
     * 添加收藏随堂练习
     * collectType 1 作业 2 资源
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/mycollect/saveMyCollect?collectId=&collectName=&collectType=&userId=
     */
    public static final String CollectAdd = "/wisdomclass/interface/mycollect/saveMyCollect";

    /**
     * 取消收藏随堂练习
     * 示例：
     * http://172.16.4.40:8090/wisdomclass/interface/mycollect/cancelMyCollect?id=
     */
    public static final String CollectCancel = "/wisdomclass/interface/mycollect/cancelMyCollect";

    /**
     * 网盘分享的文件
     * http://172.16.5.167:8090/api/v3/previewClass/getShareFile?classId=1277825
     */
    public static final String ShareFile = "/api/v3/previewClass/getShareFile";

    /**
     * 查询网盘文件夹下的文件
     * http://172.16.5.160:8090/api/v3/disk/stuQueryDir?ids=128
     */
    public static final String stuQueryDir = "/api/v3/disk/stuQueryDirNew";

    /**
     * 新增用户评论接口
     * 示例：
     * http://172.16.5.167:8090/api/v3/disk/saveUserComment?userId=66666740531&shareId=776646
     * &shareTitle=%E6%96%87%E4%BB%B6%E5%88%86%E4%BA%AB701&resId=113&resName=0&resComment=4343eewewe&resStars=6
     */
    public static final String SaveUserComment = "/api/v3/disk/saveUserComment";

    //评论保存后的接口
    public static final String CommitUserComment = "/api/v3/disk/getUserCommentById";

    /**
     * 为了课堂记录查到数据，这里接收道教师截屏的图片文件后，再次插入给服务器
     * 示例：
     * 172.16.5.159:8081/wisdomclass/interface/screenShotShare/saveStudentShareFile?
     * userId=66666740531&classId=01dfa139-8373-4b92-8cee-9f83af21ee7f
     * &type=18&fileUrl=/filesystem/screenShotShare/20191225/1577241303240.jpg
     */
    public static final String ToServerShotShare = "/wisdomclass/interface/screenShotShare/saveStudentShareFile";


    /**
     * 课堂记录的结果查询：注意一个接口依据type的值得到不同的返回Javabean,不同的界面调用
     * 示例：
     * http://172.16.5.159:8081/wisdomclass/interface/operationLog/findRecordById?recordId=24&type=3
     */
    public static final String QueryRecordInfo = "/wisdomclass/interface/operationLog/findRecordById";

    //--------------------------------埋点Url----------------------------------
    /**
     * 自主学习埋点
     * 示例：http://172.16.4.62:8080/wisdomclass/wisdomclass/interface/learnEmotionSupervise/insertSelfLearning?SelfLearning=
     * {"sId": 1,"gradeId": 2,"classId": 3,"subjectId": 4,"subjectName": "自主学习","flag": 0}
     */
    public static final String AutoLearingPoint = "/wisdomclass/wisdomclass/interface/learnEmotionSupervise/insertSelfLearning";

    /**
     * 课前预习日志记录
     */
    public static final String PreLearningPoint = "/wisdomclass/interface/previewClass/recordPreviewClass";

    /**
     * 学生端埋点的地址
     */
    public static final String student_operation_Log = "/wisdomclass/interface/operationLog/logRecord";


}
