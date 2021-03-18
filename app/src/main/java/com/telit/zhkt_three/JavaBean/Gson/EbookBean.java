package com.telit.zhkt_three.JavaBean.Gson;

import com.telit.zhkt_three.JavaBean.Resource.EUnitBean;

import java.util.List;

/**
 * 项目名称：TeachingEvaluation
 * 类名称：${CLASS_NAME}
 * 类描述：
 * 创建人：luxun
 * 创建时间：2017/6/5 0005 14:03
 * 修改人：luxun
 * 修改时间：2017/6/5 0005 14:03
 * 当前版本：v1.0
 */

public class EbookBean {

    /**
     * {
     * "pageNum": 128,
     * "title": "一年级语文苏教版上学期",
     * "subject": "语文",
     * "press": "苏教版",
     * "period": "小学",
     * "grade": "一年级",
     * "term": "上学期",
     * "list": [{
     * "chapterName": "拼音第一单元",
     * "start": 1,
     * "lson": [{
     * "chapterName": "培养良好的学习习惯",
     * "start": 1
     * }, {
     * "chapterName": "1、a o e",
     * "start": 4
     * }, {
     * "chapterName": "2、i u ü",
     * "start": 6
     * }, {
     * "chapterName": "3、b p m f",
     * "start": 8
     * }, {
     * "chapterName": "认一认",
     * "start": 11
     * }]
     * }, {
     * "chapterName": "拼音第二单元",
     * "start": 13,
     * "lson": [{
     * "chapterName": "4、d t n l",
     * "start": 13
     * }, {
     * "chapterName": "5、g k h",
     * "start": 16
     * }, {
     * "chapterName": "6、j q x",
     * "start": 19
     * }, {
     * "chapterName": "认一认（2）",
     * "start": 22
     * }]
     * }, {
     * "chapterName": "拼音第三单元",
     * "start": 24,
     * "lson": [{
     * "chapterName": "7、z c s",
     * "start": 24
     * }, {
     * "chapterName": "8、zh ch sh r",
     * "start": 27
     * }, {
     * "chapterName": "9、y w",
     * "start": 31
     * }, {
     * "chapterName": "认一认（3）",
     * "start": 33
     * }]
     * }, {
     * "chapterName": "拼音第四单元",
     * "start": 35,
     * "lson": [{
     * "chapterName": "10、ai ei ui",
     * "start": 35
     * }, {
     * "chapterName": "11、ao ou iu",
     * "start": 37
     * }, {
     * "chapterName": "12、ie ue er",
     * "start": 39
     * }, {
     * "chapterName": "认一认（4）",
     * "start": 41
     * }]
     * }, {
     * "chapterName": "拼音第五单元",
     * "start": 43,
     * "lson": [{
     * "chapterName": "13、an en in",
     * "start": 43
     * }, {
     * "chapterName": "14、un un",
     * "start": 45
     * }, {
     * "chapterName": "15、ang eng ing ong",
     * "start": 47
     * }, {
     * "chapterName": "认一认（5）",
     * "start": 49
     * }, {
     * "chapterName": "练习",
     * "start": 51
     * }]
     * }, {
     * "chapterName": "识字第一单元",
     * "start": 55,
     * "lson": [{
     * "chapterName": "识字1",
     * "start": 55
     * }, {
     * "chapterName": "识字2",
     * "start": 57
     * }, {
     * "chapterName": "识字3",
     * "start": 59
     * }, {
     * "chapterName": "识字4",
     * "start": 61
     * }, {
     * "chapterName": "练习1",
     * "start": 63
     * }]
     * }, {
     * "chapterName": "课文第二单元",
     * "start": 67,
     * "lson": [{
     * "chapterName": "1、人有两个宝",
     * "start": 67
     * }, {
     * "chapterName": "2、家",
     * "start": 70
     * }, {
     * "chapterName": "3、升国旗",
     * "start": 73
     * }, {
     * "chapterName": "联系2",
     * "start": 76
     * }]
     * }, {
     * "chapterName": "课文第三单元",
     * "start": 79,
     * "lson": [{
     * "chapterName": "4、雨点",
     * "start": 79
     * }, {
     * "chapterName": "5、秋姑娘",
     * "start": 82
     * }, {
     * "chapterName": "6、看菊花",
     * "start": 85
     * }, {
     * "chapterName": "练习3",
     * "start": 88
     * }]
     * }, {
     * "chapterName": "识字第四单元",
     * "start": 91,
     * "lson": [{
     * "chapterName": "识字5",
     * "start": 91
     * }, {
     * "chapterName": "识字6",
     * "start": 93
     * }, {
     * "chapterName": "识字7",
     * "start": 95
     * }, {
     * "chapterName": "识字8",
     * "start": 97
     * }, {
     * "chapterName": "练习4",
     * "start": 99
     * }]
     * }, {
     * "chapterName": "课文第五单元",
     * "start": 104,
     * "lson": [{
     * "chapterName": "7、给刘洋阿姨的信",
     * "start": 104
     * }, {
     * "chapterName": "8、河里的月亮",
     * "start": 107
     * }, {
     * "chapterName": "9、怀素写字",
     * "start": 110
     * }, {
     * "chapterName": "练习5",
     * "start": 114
     * }]
     * }, {
     * "chapterName": "课文第六单元",
     * "start": 117,
     * "lson": [{
     * "chapterName": "10、小雪花",
     * "start": 117
     * }, {
     * "chapterName": "11、北风和小鱼",
     * "start": 120
     * }, {
     * "chapterName": "12、小河与青草",
     * "start": 123
     * }, {
     * "chapterName": "练习6",
     * "start": 126
     * }]
     * }]
     * }
     */
    private int pageNum;
    private String title;
    private String subject;
    private String press;
    private String period;
    private String grade;
    private String term;
    private List<EUnitBean> list;

    public int getPageNum() {
        return pageNum;
    }

    public void setPageNum(int pageNum) {
        this.pageNum = pageNum;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getPress() {
        return press;
    }

    public void setPress(String press) {
        this.press = press;
    }

    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public List<EUnitBean> getList() {
        return list;
    }

    public void setList(List<EUnitBean> list) {
        this.list = list;
    }

    @Override
    public String toString() {
        return "EbookBean{" +
                "pageNum=" + pageNum +
                ", title='" + title + '\'' +
                ", subject='" + subject + '\'' +
                ", press='" + press + '\'' +
                ", period='" + period + '\'' +
                ", grade='" + grade + '\'' +
                ", term='" + term + '\'' +
                ", list=" + list +
                '}';
    }
}
