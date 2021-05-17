package com.telit.zhkt_three.Fragment.Dialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.JavaBean.InterActive.VoteBean;
import com.telit.zhkt_three.MediaTools.image.ImageLookActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * author: qzx
 * Date: 2019/6/23 17:50
 * <p>
 * 一、对获取到的投票选项按照index排序
 * <p>
 * 图片是全路径
 * <p>
 * 按返回键不消失
 */
public class VoteDialog extends DialogFragment implements View.OnClickListener {

    private Unbinder unbinder;
    @BindView(R.id.vote_main_layout)
    LinearLayout vote_main_layout;
    @BindView(R.id.vote_topic)
    TextView vote_topic;
    @BindView(R.id.vote_confirm)
    Button vote_confirm;

    /**
     * 投票所需的详情数据
     */
    private VoteBean voteBean;

    public void setVoteBean(VoteBean voteBean) {
        this.voteBean = voteBean;
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
        View view = inflater.inflate(R.layout.dialog_vote_layout, container, false);
        unbinder = ButterKnife.bind(this, view);

        String title = voteBean.getTitle();

        String chooseType;
        if (voteBean.getIsMultiplecheck() == 0) {
            //单选
            chooseType = "[单选]";
        } else {
            //多选
            chooseType = "[多选]";
        }

        //设置投票的主题
       // vote_topic.setText(title.concat(chooseType));
        vote_topic.setText(title);

        //添加投票的选项
        addVoteItem();

        vote_confirm.setOnClickListener(this);

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
        //排序
        Collections.sort(voteBean.getVoteOptions());
        for (int i = 0; i < voteBean.getVoteOptions().size(); i++) {
            View view = LayoutInflater.from(getContext()).inflate(R.layout.view_item_vote, null);
            TextView vote_item_text = view.findViewById(R.id.vote_item_text);
            ImageView vote_item_img = view.findViewById(R.id.vote_item_img);
            LinearLayout vote_item_linear = view.findViewById(R.id.vote_item_linear);
            if (TextUtils.isEmpty(voteBean.getVoteOptions().get(i).getText())){
                voteBean.getVoteOptions().get(i).setText("选项"+(i+1));
            }
            if (TextUtils.isEmpty(voteBean.getVoteOptions().get(i).getImageUrl())){
                Glide.with(getContext()).load(R.mipmap.ic_launcher_round)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .into(vote_item_img);
            }

            if (!TextUtils.isEmpty(voteBean.getVoteOptions().get(i).getText())) {
                vote_item_text.setText(voteBean.getVoteOptions().get(i).getText());
                vote_item_text.setOnClickListener(this);
            }

            if (!TextUtils.isEmpty(voteBean.getVoteOptions().get(i).getImageUrl())) {
                // String imgUrl = UrlUtils.BaseUrl + voteBean.getVoteOptions().get(i).getImageUrl();
                String imgUrl = voteBean.getVoteOptions().get(i).getImageUrl();
                // Caused by: java.lang.IllegalArgumentException: You must not call setTag() on a view Glide is targeting
                Glide.with(getContext()).load(imgUrl)
                        .error(R.mipmap.ic_launcher_round)
                        .placeholder(R.mipmap.ic_launcher_round)
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model,
                                                        Target<Drawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target,
                                                           DataSource dataSource, boolean isFirstResource) {
                                vote_item_linear.setTag(imgUrl);
                                vote_item_linear.setOnClickListener(VoteDialog.this);
                                return false;
                            }
                        })
                        .into(vote_item_img);

            }
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT);
            layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
            vote_main_layout.addView(view, layoutParams);
        }
    }

    private View preSelectedView = null;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.vote_confirm:
               vote_confirm.setEnabled(true);
                commitVote();
                break;
            case R.id.vote_item_text:
                if (voteBean.getIsMultiplecheck() == 0) {
                    //单选
                    if (preSelectedView != null) {
                        preSelectedView.setSelected(false);
                    }
                    v.setSelected(true);
                    preSelectedView = v;
                } else {
                    //多选
                    if (v.isSelected()) {
                        v.setSelected(false);
                    } else {
                        v.setSelected(true);
                    }
                }
                break;
            case R.id.vote_item_linear:
                Intent intent = new Intent(getContext(), ImageLookActivity.class);
                ArrayList<String> imgFilePathList = new ArrayList<>();
                imgFilePathList.add((String) v.getTag());
                intent.putStringArrayListExtra("imgResources", imgFilePathList);
                intent.putExtra("curImgIndex", 0);
                intent.putExtra("flag", "2");
                getContext().startActivity(intent);
                break;
        }
    }

    /**
     * 提交投票结果
     */
    public void commitVote() {
        String answerCommit;
        StringBuilder stringBuilder = new StringBuilder();
        //从1开始是因为第一个子类是投票标题
        for (int i = 1; i < vote_main_layout.getChildCount(); i++) {
            //因为vote_item不是直系子类
            RelativeLayout relativeLayout = (RelativeLayout) vote_main_layout.getChildAt(i);
            //view_item_vote.xml中是第二个子类，所以为1
            if (relativeLayout.getChildAt(1).isSelected()) {
                int index = voteBean.getVoteOptions().get(i - 1).getIndex();
                stringBuilder.append(index);
                stringBuilder.append(",");
            }
        }
        //如果为空的话就传入空字符串
        if (TextUtils.isEmpty(stringBuilder.toString())) {
            answerCommit = stringBuilder.toString();
        } else {
            answerCommit = stringBuilder.toString().substring(0, stringBuilder.toString().lastIndexOf(","));
        }
        EventBus.getDefault().post(answerCommit, Constant.Vote_Commit);
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
