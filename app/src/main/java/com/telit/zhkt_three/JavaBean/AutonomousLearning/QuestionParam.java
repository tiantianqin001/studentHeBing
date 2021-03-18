package com.telit.zhkt_three.JavaBean.AutonomousLearning;

import android.widget.LinearLayout;

import java.util.List;

/**
 * @ClassName QuestionParam
 * @Description TODO
 * @Author baoXu
 * @Date 2019/5/15 20:46
 */
public class QuestionParam {

    private List<QuestionType> questionTypes;

    private List<QuestionGrade> questionGrades;

    private List<QuestionSubject> questionSubjects;

    private List<QuestionDifficult> questionDifficults;

    private List<QuestionKnowledge> questionKnowledge;

    /**
     * 因为questionEdition、questionGradeVolum、questionUnit与QuestionKnowledge实体字段一模一样
     */
    private List<QuestionKnowledge> questionEdition;
    private List<QuestionKnowledge> questionGradeVolum;
    private List<QuestionKnowledge> questionUnit;


    public List<QuestionType> getQuestionTypes() {
        return questionTypes;
    }

    public void setQuestionTypes(List<QuestionType> questionTypes) {
        this.questionTypes = questionTypes;
    }

    public List<QuestionGrade> getQuestionGrades() {
        return questionGrades;
    }

    public void setQuestionGrades(List<QuestionGrade> questionGrades) {
        this.questionGrades = questionGrades;
    }

    public List<QuestionSubject> getQuestionSubjects() {
        return questionSubjects;
    }

    public void setQuestionSubjects(List<QuestionSubject> questionSubjects) {
        this.questionSubjects = questionSubjects;
    }

    public List<QuestionDifficult> getQuestionDifficults() {
        return questionDifficults;
    }

    public void setQuestionDifficults(List<QuestionDifficult> questionDifficults) {
        this.questionDifficults = questionDifficults;
    }

    public List<QuestionKnowledge> getQuestionKnowledge() {
        return questionKnowledge;
    }

    public void setQuestionKnowledge(List<QuestionKnowledge> questionKnowledge) {
        this.questionKnowledge = questionKnowledge;
    }

    public List<QuestionKnowledge> getQuestionEdition() {
        return questionEdition;
    }

    public void setQuestionEdition(List<QuestionKnowledge> questionEdition) {
        this.questionEdition = questionEdition;
    }

    public List<QuestionKnowledge> getQuestionGradeVolum() {
        return questionGradeVolum;
    }

    public void setQuestionGradeVolum(List<QuestionKnowledge> questionGradeVolum) {
        this.questionGradeVolum = questionGradeVolum;
    }

    public List<QuestionKnowledge> getQuestionUnit() {
        return questionUnit;
    }

    public void setQuestionUnit(List<QuestionKnowledge> questionUnit) {
        this.questionUnit = questionUnit;
    }

    @Override
    public String toString() {
        return "QuestionParam{" +
                "questionTypes=" + questionTypes +
                ", questionGrades=" + questionGrades +
                ", questionSubjects=" + questionSubjects +
                ", questionDifficults=" + questionDifficults +
                ", questionKnowledge=" + questionKnowledge +
                ", questionEdition=" + questionEdition +
                ", questionGradeVolum=" + questionGradeVolum +
                ", questionUnit=" + questionUnit +
                '}';
    }
}
