package com.telit.zhkt_three.JavaBean.HomeWork;

import com.telit.zhkt_three.JavaBean.AutonomousLearning.QuestionBank;
import com.telit.zhkt_three.JavaBean.WorkOwnResult;

import java.util.List;

/**
 * 项目名称：desktop
 * 类名称：QuestionInfo
 * 类描述：
 * 创建人：luxun
 * 创建时间：2017/3/14 0014 14:16
 * 修改人：luxun
 * 修改时间：2017/3/14 0014 14:16
 * 当前版本：v1.0
 * <p>
 * 注意这里因为兼容错题集的题库和图片出题展示，把题库出题的QuestionBank包含进来了，判断后用各自的View去加载
 */

public class QuestionInfo {

    /**
     * "id": "5794",
     * "homeworkId": "c1b483a2b2804e01a55a640f48f6af63",
     * "questionContent": "读一读，给划线的字选音",
     * "image": null,
     * "imageopted": null,
     * "list": [],
     * "attachment": null,
     * "knowledge": "[{'tid': '4602', 'name': '基础知识'}, {'tid': '18488', 'name': '课文库'}, {'tid': '4603', 'name': '拼音'}]",
     * "leftList": [],
     * "rightList": [],
     * "questionType": 0,
     * "byhand": 2,
     * "score": null,
     * "ownList": [],
     * "answer": null,
     * "analysis": "",
     * "answerImg": "",
     * "answerOption": null,
     * "qBankList": "[{'question_id':'5997185','question_type':'1','question_channel_type':'1','channel_type_name':'单选题','title':'','question_text':'我只看见闪闪的星星蓝蓝的天。（）','answer_options':{'A':'zhī','B':'zhǐ'},'answer_json':'None','answer':'http://webshot.zujuan.com/q/15/95/de5571099c6c80ee06e90e5e9304_5997185an.png?hash=5843c48b404a6cec16d78280b2414875&sign=3a895d367c6417e261cef9316c716d27&from=2','explanation':'','knowledge':'None','t_knowledge':[],'category':'None','parent_id':'5995401','paperid':'None','xd':'1','chid':'2','exam_type':'2','exam_name':'常考题','difficult_index':'1','difficult_name':'容易','is_objective':'1','is_collect':'False','extra_file':'None','save_num':'0','new_flag':'False','is_use':0,'question_source':'None'},{'question_id':'5997187','question_type':'1','question_channel_type':'1','channel_type_name':'单选题','title':'','question_text':'一只小鸟飞来了。（）','answer_options':{'A':'zhī','B':'zhǐ'},'answer_json':'None','answer':'http://webshot.zujuan.com/q/cd/ca/e2eb70f63759ab7d02f74e23008d_5997187an.png?hash=4fe0d3a086be91ceeb12904d8c3ae187&sign=e21e5e09fbd328e7cab01179c09946bd&from=2','explanation':'','knowledge':'None','t_knowledge':[],'category':'None','parent_id':'5995401','paperid':'None','xd':'1','chid':'2','exam_type':'2','exam_name':'常考题','difficult_index':'1','difficult_name':'容易','is_objective':'1','is_collect':'False','extra_file':'None','save_num':'0','new_flag':'False','is_use':0,'question_source':'None'}]",
     * "questionBanks": [
     * {
     * "id": null,
     * "ids": null,
     * "xd": 1,
     * "chid": 2,
     * "questionId": 5997185,
     * "questionType": 1,
     * "questionChannelType": 1,
     * "questionTypeName": null,
     * "title": "",
     * "questionText": "我只看见闪闪的星星蓝蓝的天。（）",
     * "answerOptions": "{\"A\":\"zhī\",\"B\":\"zhǐ\"}",
     * "answerJson": "None",
     * "answer": "http://webshot.zujuan.com/q/15/95/de5571099c6c80ee06e90e5e9304_5997185an.png?hash=5843c48b404a6cec16d78280b2414875&sign=3a895d367c6417e261cef9316c716d27&from=2",
     * "explanation": "",
     * "knowledge": "None",
     * "tKnowledge": "[]",
     * "tKnowledgeIds": null,
     * "category": "None",
     * "parentId": "5995401",
     * "paperId": "None",
     * "examType": 2,
     * "examName": "常考题",
     * "difficultIndex": 1,
     * "difficultName": "容易",
     * "isObjective": 1,
     * "isCollect": "False",
     * "extraFile": "None",
     * "saveNum": 0,
     * "newFlag": "False",
     * "isUse": 0,
     * "questionSource": "None",
     * "list": null,
     * "questionBanks": null,
     * "chapterIds": null,
     * "gradeId": null,
     * "sortType": null,
     * "answerText": null,
     * "score": null,
     * "ownscore": null,
     * "studentId": null,
     * "homeworkId": null,
     * "studentFile": null,
     * "teacherFile": null,
     * "teaDescFile": [],
     * "imgFile": [],
     * "voiceFile": [],
     * "ownList": []
     */
    private String id;
    private String homeworkId;
    private int questionType;
    private String questionContent;
    private String questionScore;
    private String attachment;
    private String answerNum;
    private int index;
    private String image;
    private String teaDesc;
    private String stuRemark;
    private String analysis;
    private String answer;
    //新增如果没有答案就是图片，用于错题集展示
    private String answerImg;
    private List<ResultList> resultList;
    private String imageopted;
    private List<LeftListBean> leftList;
    private List<RightListBean> rightList;
    private List<SelectBean> list;
    //同样的采用WorkOwnResult类替代字符串
//    private List<String> ownList;
    private List<WorkOwnResult> ownList;
    private List<String> imgFile;
    private List<String> voiceFile;
    private int chid;

    private String  studentSaveAnswer;

    private String studentSaveAttachment;


    public void setStudentSaveAnswer(String studentSaveAnswer){
       this.studentSaveAnswer=studentSaveAnswer;
    }
    public String getStudentSaveAnswer(){
        return studentSaveAnswer;
    }


    public void setStudentSaveAttachment(String studentSaveAttachment){
        this.studentSaveAttachment=studentSaveAttachment;
    }
    public String getStudentSaveAttachment(){
        return studentSaveAttachment;
    }

    public int getChid() {
        return chid;
    }

    public void setChid(int chid) {
        this.chid = chid;
    }

    //新增知识点数组字符串Json
    private String knowledge;

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    //难度 0易，1中等，2难
    private String level;

    //创建日期
    private String createDate;

    //题目分值
    private String score;

    //该题所得到的分值
    private String ownscore;

    //新增教师批阅图片
    private List<String> teaDescFile;

    //新增为了错题集区分图片出题还是题库出题：题库是2，图片出题是1
    private int byhand;

    //修改在这里增加题库包含
    private String answerOption;
    private List<QuestionBank> questionBanks;

    private String qBankList;

    //新增科目ID便于错题集巩固提供该参数
    private int subjectId;

    //新增学生姓名
    private String studentName;

    //新增学生头像
    private String studentPhoto;

    private boolean checked;

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    //老师评论
    private String comment;

    public String getStudentPhoto() {
        return studentPhoto;
    }

    public void setStudentPhoto(String studentPhoto) {
        this.studentPhoto = studentPhoto;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public int getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(int subjectId) {
        this.subjectId = subjectId;
    }

    public String getqBankList() {
        return qBankList;
    }

    public void setqBankList(String qBankList) {
        this.qBankList = qBankList;
    }

    public String getAnswerOption() {
        return answerOption;
    }

    public void setAnswerOption(String answerOption) {
        this.answerOption = answerOption;
    }

    public List<QuestionBank> getQuestionBanks() {
        return questionBanks;
    }

    public void setQuestionBanks(List<QuestionBank> questionBanks) {
        this.questionBanks = questionBanks;
    }

    public int getByhand() {
        return byhand;
    }

    public void setByhand(int byhand) {
        this.byhand = byhand;
    }

    public List<String> getTeaDescFile() {
        return teaDescFile;
    }

    public void setTeaDescFile(List<String> teaDescFile) {
        this.teaDescFile = teaDescFile;
    }

    public String getAnswerImg() {
        return answerImg;
    }

    public void setAnswerImg(String answerImg) {
        this.answerImg = answerImg;
    }

    public String getOwnscore() {
        return ownscore;
    }

    public void setOwnscore(String ownscore) {
        this.ownscore = ownscore;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public List<LeftListBean> getLeftList() {
        return leftList;
    }

    public List<String> getImgFile() {
        return imgFile;
    }

    public void setImgFile(List<String> imgFile) {
        this.imgFile = imgFile;
    }

    public List<String> getVoiceFile() {
        return voiceFile;
    }

    public void setVoiceFile(List<String> voiceFile) {
        this.voiceFile = voiceFile;
    }

    public List<WorkOwnResult> getOwnList() {
        return ownList;
    }

    public void setOwnList(List<WorkOwnResult> ownList) {
        this.ownList = ownList;
    }

    public void setLeftList(List<LeftListBean> leftList) {
        this.leftList = leftList;
    }

    public List<RightListBean> getRightList() {
        return rightList;
    }

    public void setRightList(List<RightListBean> rightList) {
        this.rightList = rightList;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public int getQuestionType() {
        return questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getQuestionContent() {
        return questionContent;
    }

    public void setQuestionContent(String questionContent) {
        this.questionContent = questionContent;
    }

    public String getQuestionScore() {
        return questionScore;
    }

    public void setQuestionScore(String questionScore) {
        this.questionScore = questionScore;
    }

    public String getAttachment() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }

    public String getAnswerNum() {
        return answerNum;
    }

    public void setAnswerNum(String answerNum) {
        this.answerNum = answerNum;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public List<SelectBean> getList() {
        return list;
    }

    public void setList(List<SelectBean> list) {
        this.list = list;
    }

    public String getImageopted() {
        return imageopted;
    }

    public void setImageopted(String imageopted) {
        this.imageopted = imageopted;
    }

    public String getTeaDesc() {
        return teaDesc;
    }

    public void setTeaDesc(String teaDesc) {
        this.teaDesc = teaDesc;
    }

    public String getStuRemark() {
        return stuRemark;
    }

    public void setStuRemark(String stuRemark) {
        this.stuRemark = stuRemark;
    }

    public String getAnalysis() {
        return analysis;
    }

    public void setAnalysis(String analysis) {
        this.analysis = analysis;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public List<ResultList> getResultList() {
        return resultList;
    }

    public void setResultList(List<ResultList> resultList) {
        this.resultList = resultList;
    }

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public static class SelectBean {
        /**
         * id : 12312
         * options : 12321321
         * content : A:0
         * index : 1
         */

        private String id;
        private String options;
        private String content;
        private int index;



        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getOptions() {
            return options;
        }

        public void setOptions(String options) {
            this.options = options;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        @Override
        public String toString() {
            return "SelectBean{" +
                    "id='" + id + '\'' +
                    ", options='" + options + '\'' +
                    ", content='" + content + '\'' +
                    ", index=" + index +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "QuestionInfo{" +
                "id='" + id + '\'' +
                ", homeworkId='" + homeworkId + '\'' +
                ", questionType=" + questionType +
                ", questionContent='" + questionContent + '\'' +
                ", questionScore='" + questionScore + '\'' +
                ", attachment='" + attachment + '\'' +
                ", answerNum='" + answerNum + '\'' +
                ", index=" + index +
                ", image='" + image + '\'' +
                ", teaDesc='" + teaDesc + '\'' +
                ", stuRemark='" + stuRemark + '\'' +
                ", analysis='" + analysis + '\'' +
                ", answer='" + answer + '\'' +
                ", resultList=" + resultList +
                ", imageopted='" + imageopted + '\'' +
                ", leftList=" + leftList +
                ", rightList=" + rightList +
                ", list=" + list +
                ", ownList=" + ownList +
                ", imgFile=" + imgFile +
                ", voiceFile=" + voiceFile +
                ", level=" + level +
                ", createDate=" + createDate +
                ", knowledge=" + knowledge +
                ",score=" + score +
                ",ownscore=" + ownscore +
                ",answerImg=" + answerImg +
                ",teaDescFile=" + teaDescFile +
                ",byhand=" + byhand +
                ",questionBanks=" + questionBanks +
                ",answerOption=" + answerOption +
                ",qBankList=" + qBankList +
                ",subjectId=" + subjectId +
                '}';
    }

    public static class LeftListBean {
        /**
         * id : 1
         * title : 222
         */

        private String id;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "LeftListBean{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }

    public static class ResultList {
        /**
         * state: 1
         * score: 5
         */

        private String state;
        private String score;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getScore() {
            return score;
        }

        public void setScore(String score) {
            this.score = score;
        }

        @Override
        public String toString() {
            return "ResultList{" +
                    "state='" + state + '\'' +
                    ", score='" + score + '\'' +
                    '}';
        }
    }

    public static class RightListBean {
        /**
         * id : 3
         * title : 444
         */

        private String id;
        private String title;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        @Override
        public String toString() {
            return "RightListBean{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    '}';
        }
    }
}

