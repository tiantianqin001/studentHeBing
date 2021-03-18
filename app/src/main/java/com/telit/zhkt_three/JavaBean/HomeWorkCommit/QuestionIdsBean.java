package com.telit.zhkt_three.JavaBean.HomeWorkCommit;

/**
 * 项目名称：desktop
 * 类名称：QuestionIdsBean
 * 类描述：家庭作业提交，简答题文件辅助上传
 * 创建人：luxun
 * 创建时间：2017/4/13 0013 14:59
 * 修改人：luxun
 * 修改时间：2017/4/13 0013 14:59
 * 当前版本：v1.0
 */

public class QuestionIdsBean {

    private String questionId;
    private String count;

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    @Override
    public String toString() {
        return "QuestionIdsBean{" +
                "questionId='" + questionId + '\'' +
                ", count='" + count + '\'' +
                '}';
    }
}
