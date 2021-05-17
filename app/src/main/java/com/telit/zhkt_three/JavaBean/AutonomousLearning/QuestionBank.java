package com.telit.zhkt_three.JavaBean.AutonomousLearning;

import android.os.Parcel;
import android.os.Parcelable;

import com.telit.zhkt_three.JavaBean.WorkOwnResult;

import java.util.List;

/**
 * @ClassName QuestionBank
 * @Description 题库实体类
 * @Author baoXu
 * @Date 2019/5/14 21:03
 */
public class QuestionBank implements Parcelable {

    //存放我写过的答案
//    private List<String> ownList;
    //原先是字符串现改成类【】
    private List<WorkOwnResult> ownList;

    protected QuestionBank(Parcel in) {
        homeworkId = in.readString();
        homeworkTitle = in.readString();
        if (in.readByte() == 0) {
            id = null;
        } else {
            id = in.readInt();
        }
        if (in.readByte() == 0) {
            xd = null;
        } else {
            xd = in.readInt();
        }
        if (in.readByte() == 0) {
            chid = null;
        } else {
            chid = in.readInt();
        }
        if (in.readByte() == 0) {
            questionId = null;
        } else {
            questionId = in.readInt();
        }
        if (in.readByte() == 0) {
            questionType = null;
        } else {
            questionType = in.readInt();
        }
        if (in.readByte() == 0) {
            questionChannelType = null;
        } else {
            questionChannelType = in.readInt();
        }
        questionTypeName = in.readString();
        title = in.readString();
        questionText = in.readString();
        answerOptions = in.readString();
        answerJson = in.readString();
        answer = in.readString();
        explanation = in.readString();
        knowledge = in.readString();
        tKnowledge = in.readString();
        tKnowledgeIds = in.createStringArray();
        category = in.readString();
        parentId = in.readString();
        paperId = in.readString();
        if (in.readByte() == 0) {
            examType = null;
        } else {
            examType = in.readInt();
        }
        examName = in.readString();
        if (in.readByte() == 0) {
            difficultIndex = null;
        } else {
            difficultIndex = in.readInt();
        }
        difficultName = in.readString();
        if (in.readByte() == 0) {
            isObjective = null;
        } else {
            isObjective = in.readInt();
        }
        isCollect = in.readString();
        extraFile = in.readString();
        collectId = in.readString();
        if (in.readByte() == 0) {
            saveNum = null;
        } else {
            saveNum = in.readInt();
        }
        newFlag = in.readString();
        if (in.readByte() == 0) {
            isUse = null;
        } else {
            isUse = in.readInt();
        }
        questionSource = in.readString();
        list = in.readString();
        comment = in.readString();
        subjectId = in.readString();
        answerText = in.readString();
        questionBanks = in.createTypedArrayList(QuestionBank.CREATOR);
        isShownAnswer = in.readByte() != 0;
        answerPublishDate = in.readString();
        press = in.readString();
        chapter = in.readString();
        if (in.readByte() == 0) {
            gradeId = null;
        } else {
            gradeId = in.readInt();
        }
        sortType = in.readString();
        score = in.readString();
        ownscore = in.readString();
        teaDescFile = in.createStringArrayList();
        imgFile = in.createStringArrayList();
        createTime = in.readString();
        collectTitle = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(homeworkId);
        dest.writeString(homeworkTitle);
        if (id == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(id);
        }
        if (xd == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(xd);
        }
        if (chid == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(chid);
        }
        if (questionId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(questionId);
        }
        if (questionType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(questionType);
        }
        if (questionChannelType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(questionChannelType);
        }
        dest.writeString(questionTypeName);
        dest.writeString(title);
        dest.writeString(questionText);
        dest.writeString(answerOptions);
        dest.writeString(answerJson);
        dest.writeString(answer);
        dest.writeString(explanation);
        dest.writeString(knowledge);
        dest.writeString(tKnowledge);
        dest.writeStringArray(tKnowledgeIds);
        dest.writeString(category);
        dest.writeString(parentId);
        dest.writeString(paperId);
        if (examType == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(examType);
        }
        dest.writeString(examName);
        if (difficultIndex == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(difficultIndex);
        }
        dest.writeString(difficultName);
        if (isObjective == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isObjective);
        }
        dest.writeString(isCollect);
        dest.writeString(extraFile);
        dest.writeString(collectId);
        if (saveNum == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(saveNum);
        }
        dest.writeString(newFlag);
        if (isUse == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(isUse);
        }
        dest.writeString(questionSource);
        dest.writeString(list);
        dest.writeString(comment);
        dest.writeString(subjectId);
        dest.writeString(answerText);
        dest.writeTypedList(questionBanks);
        dest.writeByte((byte) (isShownAnswer ? 1 : 0));
        dest.writeString(answerPublishDate);
        dest.writeString(press);
        dest.writeString(chapter);
        if (gradeId == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(gradeId);
        }
        dest.writeString(sortType);
        dest.writeString(score);
        dest.writeString(ownscore);
        dest.writeStringList(teaDescFile);
        dest.writeStringList(imgFile);
        dest.writeString(createTime);
        dest.writeString(collectTitle);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<QuestionBank> CREATOR = new Creator<QuestionBank>() {
        @Override
        public QuestionBank createFromParcel(Parcel in) {
            return new QuestionBank(in);
        }

        @Override
        public QuestionBank[] newArray(int size) {
            return new QuestionBank[size];
        }
    };

    public List<WorkOwnResult> getOwnList() {
        return ownList;
    }

    public void setOwnList(List<WorkOwnResult> ownList) {
        this.ownList = ownList;
    }

    //新增一次任务的总Id
    private String homeworkId;

    private String homeworkTitle;

    //主键
    private Integer id;
    //学段
    private Integer xd;
    //学科
    private Integer chid;
    //题目id
    private Integer questionId;
    //题目类型
    private Integer questionType;
    /**
     * 1单项选择题 2多项选择题 3判断题 4填空题 9问答题 10语言表达
     * 经过映射：questionType : 0 单项选择题 1多项选择题 2填空题 3解答题 4连线题 5判断题
     */
    private Integer questionChannelType;
    //题目类型名称
    private String questionTypeName;
    //题目名称
    private String title;
    //题目内容
    private String questionText;
    //题目选项
    private String answerOptions;
    //答案
    private String answerJson;
    //正确答案
    private String answer;
    //答案解析
    private String explanation;
    //考点
    private String knowledge;
    //知识点
    private String tKnowledge;
    //知识点数组
    private String[] tKnowledgeIds;
    //分类
    private String category;

    private String parentId;

    private String paperId;
    //考试类别
    private Integer examType;
    //考试类别名称
    private String examName;
    //困难标识
    private Integer difficultIndex;
    //困难标识名称
    private String difficultName;

    private Integer isObjective;
    private String isCollect;
    private String extraFile;
    private String collectId;
    //保存次数
    private Integer saveNum;
    //是否是新题
    private String newFlag;
    //是否使用过
    private Integer isUse;
    //题目来源
    private String questionSource;

    private String list;

    //老师批改
    private String comment;

    private String subjectId;

    /**
     * 新增文本答案
     */
    private String answerText;

    /**
     * 代替解析String list
     */
    private List<QuestionBank> questionBanks;

    //----------------------------------------
    /**
     * 保存当前答题痕迹,以逗号分隔
     */
    private List<TempSaveItemInfo> saveInfos;

    /**
     * 是否显示答案状态，默认不显示
     */
    private boolean isShownAnswer;
    //----------------------------------------

    private String answerPublishDate;

    //出版社
    private String press;
    //章节
    private String chapter;
    //年级
    private Integer gradeId;
    //排序类型
    private String sortType;

    //新增题目分值和该题的得分
    private String score;
    private String ownscore;

    //新增教师批阅图片
    private List<String> teaDescFile;

    //新增学生填写的图片
    private List<String> imgFile;

    private String createTime;

    private String collectTitle;

    private boolean checked;

    public boolean isChecked() {
        return checked;
    }

    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public List<String> getImgFile() {
        return imgFile;
    }

    public void setImgFile(List<String> imgFile) {
        this.imgFile = imgFile;
    }

    public List<String> getTeaDescFile() {
        return teaDescFile;
    }

    public void setTeaDescFile(List<String> teaDescFile) {
        this.teaDescFile = teaDescFile;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }

    public String getOwnscore() {
        return ownscore;
    }

    public void setOwnscore(String ownscore) {
        this.ownscore = ownscore;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public QuestionBank() {

    }

    public QuestionBank(Integer xd, Integer chid, String press, String chapter, Integer questionChannelType,
                        Integer difficultIndex, Integer gradeId, String sortType) {
        this.xd = xd;
        this.chid = chid;
        this.press = press;
        this.chapter = chapter;
        this.questionChannelType = questionChannelType;
        this.difficultIndex = difficultIndex;
        this.gradeId = gradeId;
        this.sortType = sortType;
    }

    public String getAnswerPublishDate() {
        return answerPublishDate;
    }

    public void setAnswerPublishDate(String answerPublishDate) {
        this.answerPublishDate = answerPublishDate;
    }

    public String getHomeworkId() {
        return homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public boolean isShownAnswer() {
        return isShownAnswer;
    }

    public void setShownAnswer(boolean shownAnswer) {
        isShownAnswer = shownAnswer;
    }

    public List<TempSaveItemInfo> getSaveInfos() {
        return saveInfos;
    }

    public void setSaveInfos(List<TempSaveItemInfo> saveInfos) {
        this.saveInfos = saveInfos;
    }

    public String getAnswerText() {
        return answerText;
    }

    public void setAnswerText(String answerText) {
        this.answerText = answerText;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getQuestionId() {
        return questionId;
    }

    public void setQuestionId(Integer questionId) {
        this.questionId = questionId;
    }

    public Integer getQuestionType() {
        return questionType;
    }

    public void setQuestionType(Integer questionType) {
        this.questionType = questionType;
    }

    public Integer getQuestionChannelType() {
        return questionChannelType;
    }

    public void setQuestionChannelType(Integer questionChannelType) {
        this.questionChannelType = questionChannelType;
    }

    public String getQuestionTypeName() {
        return questionTypeName;
    }

    public void setQuestionTypeName(String questionTypeName) {
        this.questionTypeName = questionTypeName;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getAnswerOptions() {
        return answerOptions;
    }

    public void setAnswerOptions(String answerOptions) {
        this.answerOptions = answerOptions;
    }

    public String getAnswerJson() {
        return answerJson;
    }

    public void setAnswerJson(String answerJson) {
        this.answerJson = answerJson;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getExplanation() {
        return explanation;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public String getKnowledge() {
        return knowledge;
    }

    public void setKnowledge(String knowledge) {
        this.knowledge = knowledge;
    }

    public String gettKnowledge() {
        return tKnowledge;
    }

    public void settKnowledge(String tKnowledge) {
        this.tKnowledge = tKnowledge;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getPaperId() {
        return paperId;
    }

    public void setPaperId(String paperId) {
        this.paperId = paperId;
    }

    public Integer getXd() {
        return xd;
    }

    public void setXd(Integer xd) {
        this.xd = xd;
    }

    public Integer getChid() {
        return chid;
    }

    public void setChid(Integer chid) {
        this.chid = chid;
    }

    public Integer getExamType() {
        return examType;
    }

    public void setExamType(Integer examType) {
        this.examType = examType;
    }

    public String getExamName() {
        return examName;
    }

    public void setExamName(String examName) {
        this.examName = examName;
    }

    public Integer getDifficultIndex() {
        return difficultIndex;
    }

    public void setDifficultIndex(Integer difficultIndex) {
        this.difficultIndex = difficultIndex;
    }

    public String getDifficultName() {
        return difficultName;
    }

    public void setDifficultName(String difficultName) {
        this.difficultName = difficultName;
    }

    public Integer getIsObjective() {
        return isObjective;
    }

    public void setIsObjective(Integer isObjective) {
        this.isObjective = isObjective;
    }

    public String getIsCollect() {
        return isCollect;
    }

    public void setIsCollect(String isCollect) {
        this.isCollect = isCollect;
    }

    public String getExtraFile() {
        return extraFile;
    }

    public void setExtraFile(String extraFile) {
        this.extraFile = extraFile;
    }

    public Integer getSaveNum() {
        return saveNum;
    }

    public void setSaveNum(Integer saveNum) {
        this.saveNum = saveNum;
    }

    public String getNewFlag() {
        return newFlag;
    }

    public void setNewFlag(String newFlag) {
        this.newFlag = newFlag;
    }

    public Integer getIsUse() {
        return isUse;
    }

    public void setIsUse(Integer isUse) {
        this.isUse = isUse;
    }

    public String getQuestionSource() {
        return questionSource;
    }

    public void setQuestionSource(String questionSource) {
        this.questionSource = questionSource;
    }

    public String getList() {
        return list;
    }

    public void setList(String list) {
        this.list = list;
    }

    public List<QuestionBank> getQuestionBanks() {
        return questionBanks;
    }

    public void setQuestionBanks(List<QuestionBank> questionBanks) {
        this.questionBanks = questionBanks;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getChapter() {
        return chapter;
    }

    public void setChapter(String chapter) {
        this.chapter = chapter;
    }

    public Integer getGradeId() {
        return gradeId;
    }

    public void setGradeId(Integer gradeId) {
        this.gradeId = gradeId;
    }

    public String getSortType() {
        return sortType;
    }

    public void setSortType(String sortType) {
        this.sortType = sortType;
    }

    public String[] gettKnowledgeIds() {
        return tKnowledgeIds;
    }

    public void settKnowledgeIds(String[] tKnowledgeIds) {
        this.tKnowledgeIds = tKnowledgeIds;
    }

    public String getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(String subjectId) {
        this.subjectId = subjectId;
    }

    public String getHomeworkTitle() {
        return homeworkTitle;
    }

    public void setHomeworkTitle(String homeworkTitle) {
        this.homeworkTitle = homeworkTitle;
    }

    public String getCollectId() {
        return collectId;
    }

    public void setCollectId(String collectId) {
        this.collectId = collectId;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getCollectTitle() {
        return collectTitle;
    }

    public void setCollectTitle(String collectTitle) {
        this.collectTitle = collectTitle;
    }


}
