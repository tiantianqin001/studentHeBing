package com.telit.zhkt_three.JavaBean.Gson;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/6/9 11:14
 */
public class TypicalAnswersBeanTwo {
    private List<TypicalAnswers> fineList;
    private List<TypicalAnswers> wrongList;

    public List<TypicalAnswers> getFineList() {
        return fineList;
    }

    public void setFineList(List<TypicalAnswers> fineList) {
        this.fineList = fineList;
    }

    public List<TypicalAnswers> getWrongList() {
        return wrongList;
    }

    public void setWrongList(List<TypicalAnswers> wrongList) {
        this.wrongList = wrongList;
    }
}
