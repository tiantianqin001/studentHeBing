package com.telit.zhkt_three.MediaTools;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.Gson.CommentCommitBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * saveUserComment接口需要分享的id
 */
public class CommentCommitActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.comment_back)
    ImageView comment_back;
    @BindView(R.id.comment_words)
    TextView comment_words;
    @BindView(R.id.comment_score)
    TextView comment_score;
    @BindView(R.id.comment_rating_bar)
    RatingBar comment_rating_bar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment_commit);
        unbinder = ButterKnife.bind(this);

        if (getIntent() == null) {
            return;
        }

        //获取到传递过来的参数 shareId shareTitle resId resName
        String shareId = getIntent().getStringExtra("shareId");
        String shareTitle = getIntent().getStringExtra("shareTitle");
        String resId = getIntent().getStringExtra("resId");
        String resName = getIntent().getStringExtra("resName");
        String resStars = getIntent().getStringExtra("resStars");
        int commentId = getIntent().getIntExtra("commentId",0);

        if (!TextUtils.isEmpty(resStars)){
            float avgStar = Float.valueOf(resStars) / 2f;
            comment_rating_bar.setRating(avgStar);
            comment_score.setText(resStars + "分");
            comment_words.setText(shareTitle);
            return;
        }

        comment_rating_bar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                comment_score.setText((rating * 2) + "分");
            }
        });

        comment_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        CircleProgressDialogFragment circleProgressDialogFragment = new CircleProgressDialogFragment();
        circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

        String url = UrlUtils.BaseUrl + UrlUtils.CommitUserComment;
        Map<String, String> paraMap = new HashMap<>();
        paraMap.put("userId", UserUtils.getUserId());
        paraMap.put("shareId", shareId);
        paraMap.put("shareTitle", shareTitle);
        paraMap.put("resId", resId);
        paraMap.put("resName", resName);
        paraMap.put("resComment", comment_words.getText().toString().trim());
        paraMap.put("resStars", (int) (comment_rating_bar.getRating() * 2) + "");
        paraMap.put("commentId", commentId+ "");
        OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        QZXTools.popToast(CommentCommitActivity.this, "网络失败", false);
                        if (circleProgressDialogFragment != null)
                            circleProgressDialogFragment.dismissAllowingStateLoss();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String result = response.body().string();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Gson gson = new Gson();
                        CommentCommitBean preShareFilesBeans = gson.fromJson(result, CommentCommitBean.class);
                        if ( preShareFilesBeans.getErrorCode().equals("1")){
                            try {
                                CommentCommitBean.ResultBean resultBean = preShareFilesBeans.getResult().get(0);
                                comment_words.setText(resultBean.getResComment());
                                comment_score.setText((resultBean.getResStars() + "分"));
                                comment_rating_bar.setRating(resultBean.getResStars()/2f);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                       // QZXTools.popToast(CommentCommitActivity.this, msg, false);

                        if (circleProgressDialogFragment != null)
                            circleProgressDialogFragment.dismissAllowingStateLoss();

                       // finish();
                    }
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }
}
