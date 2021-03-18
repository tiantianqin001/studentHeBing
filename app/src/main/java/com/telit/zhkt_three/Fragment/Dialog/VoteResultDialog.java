package com.telit.zhkt_three.Fragment.Dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.CustomView.VoteResult.VoteCountView;
import com.telit.zhkt_three.JavaBean.InterActive.VoteResultTwoBean;
import com.telit.zhkt_three.R;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/6/23 17:50
 * <p>
 * 按返回键不消失
 */
public class VoteResultDialog extends DialogFragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.vote_main_result_layout)
    LinearLayout vote_main_result_layout;
    @BindView(R.id.vote_result_topic)
    TextView vote_result_topic;
    @BindView(R.id.vote_result_close)
    Button vote_result_close;

    /**
     * 投票结果所需的详情数据
     */
//    private VoteResultBean voteResultBean;
    private List<VoteResultTwoBean> voteResultTwoBeans;
    private int totalNum;

    public void setVoteResultBean(List<VoteResultTwoBean> voteResultTwoBeans, int total) {
        this.voteResultTwoBeans = voteResultTwoBeans;
        this.totalNum = total;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //使用和忘记密码同样的样式
        setStyle(DialogFragment.STYLE_NO_FRAME, R.style.dialogForgetPwdTwo);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_vote_result_layout, container, false);
        unbinder = ButterKnife.bind(this, view);


//        String title = voteResultBean.getTitle();

//        String chooseType;
//        if (voteResultBean.getIsMultiplecheck().equals("0")) {
//            //单选
//            chooseType = "[单选]";
//        } else {
//            //多选
//            chooseType = "[多选]";
//        }

        //设置投票的主题
//        vote_result_topic.setText(title.concat(chooseType));

        addVoteItem();

        vote_result_close.setOnClickListener(this);

        return view;
    }

    @Override
    public void onDestroyView() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroyView();
    }

    /**
     * 添加投票子项
     */
    private void addVoteItem() {
        for (int i = 0; i < voteResultTwoBeans.size(); i++) {
            if (i == 0) {
                vote_result_topic.setText(voteResultTwoBeans.get(i).getText());
            }
            VoteCountView voteCountView = new VoteCountView(getContext());
            voteCountView.setVoteResultDisplay(Integer.parseInt(voteResultTwoBeans.get(i).getNumberOfVote()),
                    totalNum, voteResultTwoBeans.get(i).getText(), voteResultTwoBeans.get(i).getImageUrl());
            vote_main_result_layout.addView(voteCountView);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_result_close:
                dismiss();
                break;
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        setBackNoEffect();
    }

    /**
     * 返回键不消失
     */
    private void setBackNoEffect() {
        getDialog().setCancelable(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_SEARCH) {
                    return true;
                } else {
                    return false;
                }
            }
        });
    }
}
