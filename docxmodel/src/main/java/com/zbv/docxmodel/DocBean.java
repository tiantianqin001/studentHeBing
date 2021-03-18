package com.zbv.docxmodel;

import java.util.List;

/**
 * author: qzx
 * Date: 2020/3/30 15:54
 */
public class DocBean {

    private int qIndex;
    private int qType;
    private String qTitle;
    private int qTotalScore;
    private double qMyScore;
    private String qRightAnswer;
    private String optionOne;
    private String optionOneAnswer;
    private String optionTwo;
    private String optionTwoAnswer;
    private String optionThree;
    private String optionThreeAnswer;
    private String optionFour;
    private String optionFourAnswer;

    private List<OptionBean> optionBeans;

    public List<OptionBean> getOptionBeans() {
        return optionBeans;
    }

    public void setOptionBeans(List<OptionBean> optionBeans) {
        this.optionBeans = optionBeans;
    }

    public int getqIndex() {
        return qIndex;
    }

    public void setqIndex(int qIndex) {
        this.qIndex = qIndex;
    }

    public int getqType() {
        return qType;
    }

    public void setqType(int qType) {
        this.qType = qType;
    }

    public String getqTitle() {
        return qTitle;
    }

    public void setqTitle(String qTitle) {
        this.qTitle = qTitle;
    }

    public int getqTotalScore() {
        return qTotalScore;
    }

    public void setqTotalScore(int qTotalScore) {
        this.qTotalScore = qTotalScore;
    }

    public double getqMyScore() {
        return qMyScore;
    }

    public void setqMyScore(double qMyScore) {
        this.qMyScore = qMyScore;
    }

    public String getqRightAnswer() {
        return qRightAnswer;
    }

    public void setqRightAnswer(String qRightAnswer) {
        this.qRightAnswer = qRightAnswer;
    }

    public String getOptionOne() {
        return optionOne;
    }

    public void setOptionOne(String optionOne) {
        this.optionOne = optionOne;
    }

    public String getOptionOneAnswer() {
        return optionOneAnswer;
    }

    public void setOptionOneAnswer(String optionOneAnswer) {
        this.optionOneAnswer = optionOneAnswer;
    }

    public String getOptionTwo() {
        return optionTwo;
    }

    public void setOptionTwo(String optionTwo) {
        this.optionTwo = optionTwo;
    }

    public String getOptionTwoAnswer() {
        return optionTwoAnswer;
    }

    public void setOptionTwoAnswer(String optionTwoAnswer) {
        this.optionTwoAnswer = optionTwoAnswer;
    }

    public String getOptionThree() {
        return optionThree;
    }

    public void setOptionThree(String optionThree) {
        this.optionThree = optionThree;
    }

    public String getOptionThreeAnswer() {
        return optionThreeAnswer;
    }

    public void setOptionThreeAnswer(String optionThreeAnswer) {
        this.optionThreeAnswer = optionThreeAnswer;
    }

    public String getOptionFour() {
        return optionFour;
    }

    public void setOptionFour(String optionFour) {
        this.optionFour = optionFour;
    }

    public String getOptionFourAnswer() {
        return optionFourAnswer;
    }

    public void setOptionFourAnswer(String optionFourAnswer) {
        this.optionFourAnswer = optionFourAnswer;
    }
}
