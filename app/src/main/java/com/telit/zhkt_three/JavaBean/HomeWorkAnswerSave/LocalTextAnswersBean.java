package com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave;

import org.greenrobot.greendao.annotation.Convert;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

import java.util.List;

/**
 * 项目名称：desktop
 * 类名称：LocalTextAnswersBean
 * 类描述：学生本地答案的保存
 * 创建人：luxun
 * 创建时间：2017/4/12 0012 9:51
 * 修改人：luxun
 * 修改时间：2017/4/12 0012 9:51
 * 当前版本：v1.0
 * <p>
 * 修改成保存到greendao数据库：在数据库中依据questionId查询保存的答案
 */
@Entity
public class LocalTextAnswersBean {
    public String userId;//学生Id

    public String homeworkId;//家庭作业ID
    @Id
    public String questionId;//问题ID
    public int questionType;//问题类型
    public String answerContent;//答案

    public String answer;//问题

    public List<String> getAnswers() {
        return answers;
    }

    public void setAnswers(List<String> answers) {
        this.answers = answers;
    }
    @Convert(columnType = String.class, converter = StringListConverter.class)
    public List<String> answers;

    @Convert(columnType = String.class, converter = AnswerItemListConverter.class)
    public List<AnswerItem> list;//答案列表---用于单选题、多选题、填空题

    @Convert(columnType = String.class, converter = StringListConverter.class)
    public List<String> imageList;//用于主观题图片Url地址保存



    @Generated(hash = 861116850)
    public LocalTextAnswersBean(String userId, String homeworkId, String questionId,
            int questionType, String answerContent, String answer, List<String> answers,
            List<AnswerItem> list, List<String> imageList) {
        this.userId = userId;
        this.homeworkId = homeworkId;
        this.questionId = questionId;
        this.questionType = questionType;
        this.answerContent = answerContent;
        this.answer = answer;
        this.answers = answers;
        this.list = list;
        this.imageList = imageList;
    }

    @Generated(hash = 2009341410)
    public LocalTextAnswersBean() {
    }

    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHomeworkId() {
        return this.homeworkId;
    }

    public void setHomeworkId(String homeworkId) {
        this.homeworkId = homeworkId;
    }

    public String getQuestionId() {
        return this.questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public int getQuestionType() {
        return this.questionType;
    }

    public void setQuestionType(int questionType) {
        this.questionType = questionType;
    }

    public String getAnswerContent() {
        return this.answerContent;
    }

    public void setAnswerContent(String answerContent) {
        this.answerContent = answerContent;
    }

    public List<AnswerItem> getList() {
        return this.list;
    }

    public void setList(List<AnswerItem> list) {
        this.list = list;
    }

    public List<String> getImageList() {
        return this.imageList;
    }

    public void setImageList(List<String> imageList) {
        this.imageList = imageList;
    }

    @Override
    public String toString() {
        return "LocalTextAnswersBean{" +
                "userId='" + userId + '\'' +
                ", homeworkId='" + homeworkId + '\'' +
                ", questionId='" + questionId + '\'' +
                ", questionType=" + questionType +
                ", answerContent='" + answerContent + '\'' +
                ", list=" + list +
                ", imageList=" + imageList +
                '}';
    }

    public String getAnswer() {
        return this.answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }
}
