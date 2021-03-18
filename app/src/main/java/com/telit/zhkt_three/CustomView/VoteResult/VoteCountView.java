package com.telit.zhkt_three.CustomView.VoteResult;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.R;

/**
 * author: qzx
 * Date: 2019/6/24 8:42
 */
public class VoteCountView extends RelativeLayout {

    private ImageView vote_item_img;
    private TextView vote_item_name;
    private VoteProgressView voteProgressView;

    private int curVoteCount;
    private int totalVoteCount;
    private String voteItem;

    public VoteCountView(Context context) {
        this(context, null);
    }

    public VoteCountView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoteCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_vote_result_layout, this, true);
        vote_item_img = view.findViewById(R.id.vote_item_img);
        vote_item_name = view.findViewById(R.id.vote_item_name);
        voteProgressView = view.findViewById(R.id.voteProgressView);
    }

    /**
     * 展示结果：投票项、进度以及投票数
     */
    public void setVoteResultDisplay(int curVoteCount, int totalVoteCount, String voteItem, String imgUrl) {
        this.curVoteCount = curVoteCount;
        this.totalVoteCount = totalVoteCount;
        this.voteItem = voteItem;
        vote_item_name.setText(voteItem);
        if (!TextUtils.isEmpty(imgUrl)) {
            String actualImgUrl = UrlUtils.BaseUrl + imgUrl;
            Glide.with(getContext()).load(actualImgUrl)
                    .placeholder(R.mipmap.icon_user)
                    .error(R.mipmap.icon_user)
                    .into(vote_item_img);
        }
        voteProgressView.setVoteCount(curVoteCount, totalVoteCount);
        invalidate();
    }
}
