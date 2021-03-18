package com.telit.zhkt_three.JavaBean.AutonomousLearning;

/**
 * @ClassName QuestionType
 * @Description 题库题目类型实体类
 * @Author baoXu
 * @Date 2019/5/15 19:59
 *
 * 试题题型
 */
public class QuestionType {

    //主键
    private Integer id;
    //题库题目类型id
    private Integer questionChannelType;
    //题库题目类型名称
    private String questionTypeName;
    //学科
    private Integer chid;
    //学科名称
    private String chname;

    public QuestionType(){

    }

    public QuestionType(Integer chid){
        this.chid = chid;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getChid() {
        return chid;
    }

    public void setChid(Integer chid) {
        this.chid = chid;
    }

    public String getChname() {
        return chname;
    }

    public void setChname(String chname) {
        this.chname = chname;
    }

    @Override
    public String toString() {
        return "QuestionType{" +
                "id=" + id +
                ", questionChannelType=" + questionChannelType +
                ", questionTypeName='" + questionTypeName + '\'' +
                ", chid=" + chid +
                ", chname='" + chname + '\'' +
                '}';
    }
}
