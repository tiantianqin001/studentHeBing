package com.zbv.docxmodel;

import android.util.Log;

import org.apache.poi.xwpf.usermodel.BreakClear;
import org.apache.poi.xwpf.usermodel.UnderlinePatterns;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;

import java.io.FileOutputStream;
import java.util.List;

/**
 * author: qzx
 * Date: 2020/4/1 9:43
 */
public class DocxUtils {

    public static final int Single_Choose = 0;
    public static final int Multi_Choose = 1;
    public static final int Fill_Blank = 2;
    public static final int Subject_Item = 3;
    public static final int Linked_Line = 4;
    public static final int Judge_Item = 5;

    /**
     * 依据内容创建一个docx文档
     */
    public static void createXWPF(List<DocBean> docBeanList, String savedPath, DocxCallback docxCallback) {

        docxCallback.isDocxing(true);

        XWPFDocument xwpfDocument = new XWPFDocument();

        //公用SB
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < docBeanList.size(); i++) {

            XWPFParagraph xwpfParagraph = xwpfDocument.createParagraph();

            //题型头
            XWPFRun xwpfRun_type = xwpfParagraph.createRun();
            int qIndex = docBeanList.get(i).getqIndex();
            int qType = docBeanList.get(i).getqType();
            String type = "";
            switch (qType) {
                case Fill_Blank:
                    type = qIndex + "、[填空题]";
                    break;
                case Multi_Choose:
                    type = qIndex + "、[多选题]";
                    break;
                case Judge_Item:
                    type = qIndex + "、[判断题]";
                    break;
                case Single_Choose:
                    type = qIndex + "、[单选题]";
                    break;
                case Subject_Item:
                    type = qIndex + "、[主观题]";
                    break;
            }
            xwpfRun_type.setText(type);
//        xwpfRun_type.setColor("FF9600");


            //题目标题
            XWPFRun xwpfRun_title = xwpfParagraph.createRun();
            String title = docBeanList.get(i).getqTitle();
            xwpfRun_title.setText(title);
//        xwpfRun_title.setColor("5D5D5D");

            //题目得分情况
            XWPFRun xwpfRun_score = xwpfParagraph.createRun();
            int totalScore = docBeanList.get(i).getqTotalScore();
            double myScore = docBeanList.get(i).getqMyScore();
            stringBuilder.append("(总分是：");
            stringBuilder.append(totalScore);
            stringBuilder.append("分，我的得分：");
            stringBuilder.append(myScore);
            stringBuilder.append("分)");
            xwpfRun_score.setText(stringBuilder.toString().trim());
//        xwpfRun_score.setColor("ff4444");
            xwpfRun_score.addBreak(BreakClear.ALL);

            //重置sb
            stringBuilder.setLength(0);

            List<OptionBean> optionBeans = docBeanList.get(i).getOptionBeans();
            for (OptionBean optionBean : optionBeans) {
                XWPFParagraph xwpfParagraph_option = xwpfDocument.createParagraph();
                XWPFRun xwpfRun_option_index = xwpfParagraph_option.createRun();
                xwpfRun_option_index.setText(optionBean.getIndex());
                xwpfRun_option_index.setFontSize(15);
//            xwpfRun_option_index.setColor("4562CF");

                XWPFRun xwpfRun_option_content = xwpfParagraph_option.createRun();
                xwpfRun_option_content.setText(optionBean.getOptionContent());

                if (qType == Fill_Blank) {
                    xwpfRun_option_content.setUnderline(UnderlinePatterns.WORDS);
                }

                xwpfRun_option_content.setFontSize(15);
//            xwpfRun_option_content.setColor("000000");
            }

            //题目答案
            XWPFParagraph xwpfParagraph_answer = xwpfDocument.createParagraph();
            XWPFRun xwpfRun_answer = xwpfParagraph_answer.createRun();
            xwpfRun_answer.addBreak(BreakClear.ALL);
            stringBuilder.append("正确答案：");
            stringBuilder.append(docBeanList.get(i).getqRightAnswer());
            xwpfRun_answer.setText(stringBuilder.toString().trim());
            stringBuilder.setLength(0);
            xwpfRun_answer.setFontSize(12);
            xwpfRun_answer.addBreak(BreakClear.ALL);//html <br/>
        }

        try {
            FileOutputStream fileOutputStream = new FileOutputStream(savedPath, true);
            xwpfDocument.write(fileOutputStream);

            docxCallback.isDocxing(false);
            docxCallback.onCompleted();

            Log.e("zbv", "createXWPF Over", null);
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("zbv", "createXWPF Exception", e);
        }
//        finally {
//            try {
//                xwpfDocument.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//                Log.e("zbv", "createXWPF Close Exception", e);
//            }
//        }

    }
}
