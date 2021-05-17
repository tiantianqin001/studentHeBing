package com.telit.zhkt_three.CustomView.AfterWork;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.HomeWork.HomeWorkDetailActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.AfterHomework.AfterHomeworkBean;
import com.telit.zhkt_three.JavaBean.HomeWorkAnswerSave.LocalTextAnswersBean;
import com.telit.zhkt_three.MyApplication;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.util.Date;

/**
 * author: qzx
 * Date: 2019/7/1 14:35
 */
public class ItemAfterHomeworkView extends RelativeLayout {
    /**
     * 数据类
     */
    private AfterHomeworkBean afterHomeworkBean;

    private RelativeLayout after_homework_item_layout;
    private ImageView after_homework_img_subject;
    private TextView after_homework_tv_desc;
    private TextView after_homework_tv_commit_date;
    private TextView after_homework_tv_enter;
    private ImageView after_homework_img_enter;
    private String comType;
    private int types;

    private TextView after_homework_tv_export;


    public void setAfterHomeworkBean(AfterHomeworkBean afterHomeworkBean) {
        this.afterHomeworkBean = afterHomeworkBean;
        initData();
    }

    public ItemAfterHomeworkView(Context context) {
        this(context, null,0);
    }

    public ItemAfterHomeworkView(Context context,OnExportClickListener onExportClickListener) {
        this(context, null,0);
        this.onExportClickListener = onExportClickListener;
    }

    public ItemAfterHomeworkView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemAfterHomeworkView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.rv_after_homework_sub_layout, this, true);

        after_homework_item_layout = view.findViewById(R.id.after_homework_item_layout);
        after_homework_img_subject = view.findViewById(R.id.after_homework_img_subject);
        after_homework_tv_desc = view.findViewById(R.id.after_homework_tv_desc);
        after_homework_tv_commit_date = view.findViewById(R.id.after_homework_tv_commit_date);
        after_homework_tv_enter = view.findViewById(R.id.after_homework_tv_enter);
        after_homework_img_enter = view.findViewById(R.id.after_homework_img_enter);

        after_homework_tv_export = view.findViewById(R.id.after_homework_tv_export);
    }

    private void initData() {
        StringBuilder stringBuilder = new StringBuilder();

        if (afterHomeworkBean.getStatus() == null || afterHomeworkBean.getStatus().equals(Constant.Todo_Status)
                || afterHomeworkBean.getStatus().equals(Constant.Retry_Status) ||
                afterHomeworkBean.getStatus().equals(Constant.Save_Status)) {
            if (afterHomeworkBean.getStatus() == null) {
                afterHomeworkBean.setStatus("0");
            }
            after_homework_tv_commit_date.setTextColor(0xFFFF7200);

            String endDate = afterHomeworkBean.getEndDate();
            endDate = endDate.replace('-', '/');
            long endTime = Date.parse(endDate);
            long time = new Date().getTime();
            String status = afterHomeworkBean.getStatus();
            if (status.equals(Constant.Todo_Status)){
                if (time>endTime){
                    after_homework_tv_enter.setText("去补交");
                }else {
                    after_homework_tv_enter.setText("去完成");
                }
            }else if (status.equals(Constant.Retry_Status)){
                after_homework_tv_enter.setText("打回重做");
            }else if (status.equals(Constant.Save_Status)){
                after_homework_tv_enter.setText("作业已保存");
            }


            after_homework_tv_enter.setTextColor(0xFF008AFF);
            after_homework_img_enter.setImageResource(R.mipmap.todo_arrow);

            stringBuilder.append("提交截止日期：");

            after_homework_tv_export.setVisibility(GONE);
        } else {
            after_homework_tv_commit_date.setTextColor(0xFF93A5B9);
            after_homework_tv_enter.setText("查看报告");
            after_homework_tv_enter.setTextColor(0xFF93A5B9);
            after_homework_img_enter.setImageResource(R.mipmap.complete_arrow);

            stringBuilder.append("作业完成日期：");

            after_homework_tv_export.setVisibility(VISIBLE);
        }

        String subject = afterHomeworkBean.getSubjectId();
        if (!TextUtils.isEmpty(subject)) {
            after_homework_img_subject.setImageResource(
                    UserUtils.getSubjectIcon(Integer.parseInt(subject)));
        }

        after_homework_tv_desc.setText(afterHomeworkBean.getName());

        String endDate = afterHomeworkBean.getEndDate();
        endDate = endDate.replace("-", "/");
        stringBuilder.append(endDate);
        after_homework_tv_commit_date.setText(stringBuilder.toString());

        after_homework_item_layout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (QZXTools.canClick()) {


                    /**
                     * 进入作业详情做作业
                     *   作业的点击事件
                     * 以前是区分是否todo来进入报告，暂时有问题改为都进入作业详情，然后可以查看报告
                     * */
                    Intent intent = new Intent(getContext(), HomeWorkDetailActivity.class);
                    intent.putExtra("homeworkId", afterHomeworkBean.getId());
                    intent.putExtra("status", afterHomeworkBean.getStatus());
                    intent.putExtra("byHand", afterHomeworkBean.getByHand());//1-byHand
                    intent.putExtra("comType",comType);
                    intent.putExtra("types",types);
                    getContext().startActivity(intent);
                }
            }
        });

        after_homework_tv_export.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onExportClickListener!=null){
                    onExportClickListener.onExportClick(v,afterHomeworkBean.getId(),afterHomeworkBean.getByHand(),afterHomeworkBean.getName(),afterHomeworkBean.getStatus());
                }
            }
        });
    }

    public void setType(String comType) {

        this.comType = comType;
    }

    public void setTypes(int types) {

        this.types = types;
    }

    private OnExportClickListener onExportClickListener;

    public interface OnExportClickListener {
        void onExportClick(View view, String homeworkId,String byHand,String homeworkName,String status);
    }
}
