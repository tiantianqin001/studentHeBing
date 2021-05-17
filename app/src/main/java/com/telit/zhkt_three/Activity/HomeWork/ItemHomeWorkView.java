package com.telit.zhkt_three.Activity.HomeWork;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.telit.zhkt_three.Activity.HomeWork.HomeWorkStailInterface;
import com.telit.zhkt_three.JavaBean.HomeWork.QuestionInfo;
import com.telit.zhkt_three.R;

import java.util.List;

public class ItemHomeWorkView implements HomeWorkStailInterface {
    QuestionInfo questionInfo;

    public ItemHomeWorkView(QuestionInfo questionInfo) {

        this.questionInfo = questionInfo;
    }


    public int getLayout(int quintType) {
        switch (quintType) {
            case 0:
                //单选题
                return R.layout.single_choose_layout;
            case 1:
                //多选题
                return R.layout.single_choose_layout;
            case 2:
                //填空题题
                return R.layout.fill_blank_layout;
            case 3:
                //主观题
                return R.layout.subject_item_layout;
            case 4:
                //连线题题
                return R.layout.linked_line_layout;
            case 5:
                //判断题题
                return R.layout.judgeselect_two_layout;
        }
        return 0;
    }


    @Override
    public View getView(Context mContext, View convertView,  int quintType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(getLayout(quintType), null);
        //TODO 数据展示
        return convertView;
    }
}
