package com.telit.zhkt_three.JavaBean;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/4/16 9:29
 *
 * 课程表Json 嵌套
 * 一、TimeTableInfo.class
 * 二、SubjectInfo.class 静态内部类
 * 三、ClassInfo.class 静态内部类
 */
public class TimeTableInfo {
    private int total;
    private int morningCount;
    private int afternoonCount;
    private int nightCount;
    private List<SubjectInfo> weekInfoList;

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public int getMorningCount() {
        return morningCount;
    }

    public void setMorningCount(int morningCount) {
        this.morningCount = morningCount;
    }

    public int getAfternoonCount() {
        return afternoonCount;
    }

    public void setAfternoonCount(int afternoonCount) {
        this.afternoonCount = afternoonCount;
    }

    public int getNightCount() {
        return nightCount;
    }

    public void setNightCount(int nightCount) {
        this.nightCount = nightCount;
    }

    public List<SubjectInfo> getWeekInfoList() {
        return weekInfoList;
    }

    public void setWeekInfoList(List<SubjectInfo> weekInfoList) {
        this.weekInfoList = weekInfoList;
    }

    @Override
    public String toString() {
        return "TimeTableInfo{" +
                "total=" + total +
                ", morningCount=" + morningCount +
                ", afternoonCount=" + afternoonCount +
                ", nightCount=" + nightCount +
                ", weekInfoList=" + weekInfoList +
                '}';
    }

    public static class SubjectInfo {
        private String weekName;
        private String weekid;
        private List<ClassInfo> subjectList;

        public String getWeekName() {
            return weekName;
        }

        public void setWeekName(String weekName) {
            this.weekName = weekName;
        }

        public String getWeekid() {
            return weekid;
        }

        public void setWeekid(String weekid) {
            this.weekid = weekid;
        }

        public List<ClassInfo> getSubjectList() {
            return subjectList;
        }

        public void setSubjectList(List<ClassInfo> subjectList) {
            this.subjectList = subjectList;
        }

        public static class ClassInfo {
            private String section;
            private String startTime;
            private String endTime;
            private String subjectId;
            private String subjectName;

            public String getSection() {
                return section;
            }

            public void setSection(String section) {
                this.section = section;
            }

            public String getStartTime() {
                return startTime;
            }

            public void setStartTime(String startTime) {
                this.startTime = startTime;
            }

            public String getEndTime() {
                return endTime;
            }

            public void setEndTime(String endTime) {
                this.endTime = endTime;
            }

            public String getSubjectId() {
                return subjectId;
            }

            public void setSubjectId(String subjectId) {
                this.subjectId = subjectId;
            }

            public String getSubjectName() {
                return subjectName;
            }

            public void setSubjectName(String subjectName) {
                this.subjectName = subjectName;
            }

            @Override
            public String toString() {
                return "ClassInfo{" +
                        "section='" + section + '\'' +
                        ", startTime='" + startTime + '\'' +
                        ", endTime='" + endTime + '\'' +
                        ", subjectId='" + subjectId + '\'' +
                        ", subjectName='" + subjectName + '\'' +
                        '}';
            }
        }

        @Override
        public String toString() {
            return "SubjectInfo{" +
                    "weekName='" + weekName + '\'' +
                    ", weekid='" + weekid + '\'' +
                    ", subjectList=" + subjectList +
                    '}';
        }
    }

}
