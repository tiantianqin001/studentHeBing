package com.telit.zhkt_three.JavaBean.AutonomousLearning;

/**
 * @ClassName QuestionDifficult
 * @Description TODO
 * @Author baoXu
 * @Date 2019/5/15 20:10
 *
 * 试题难度
 */
public class QuestionDifficult {

    //主键
    private Integer id;
    //题目困难标识
    private Integer difficultIndex;
    //题目困难名称
    private String difficultName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    @Override
    public String toString() {
        return "QuestionDifficult{" +
                "id=" + id +
                ", difficultIndex=" + difficultIndex +
                ", difficultName='" + difficultName + '\'' +
                '}';
    }
}
