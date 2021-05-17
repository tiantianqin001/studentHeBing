package com.telit.zhkt_three.MediaTools;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.Constant.UrlUtils;
import com.telit.zhkt_three.Fragment.CircleProgressDialogFragment;
import com.telit.zhkt_three.JavaBean.PreView.PreViewDisplayBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.OkHttp3_0Utils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.eventbus.EventBus;

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
public class CommentActivity extends BaseActivity {

    private Unbinder unbinder;
    @BindView(R.id.comment_back)
    ImageView comment_back;
    @BindView(R.id.comment_words)
    EditText comment_words;
    @BindView(R.id.comment_score)
    TextView comment_score;
    @BindView(R.id.comment_rating_bar)
    RatingBar comment_rating_bar;
    @BindView(R.id.comment_btn_commit)
    TextView comment_btn_commit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        unbinder = ButterKnife.bind(this);

        if (getIntent() == null) {
            return;
        }

        //获取到传递过来的参数 shareId shareTitle resId resName
        String shareId = getIntent().getStringExtra("shareId");
        String shareTitle = getIntent().getStringExtra("shareTitle");
        String resId = getIntent().getStringExtra("resId");
        String resName = getIntent().getStringExtra("resName");
        int curPosition = getIntent().getIntExtra("curPosition",-1);

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

        comment_btn_commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(comment_words.getText().toString().trim())) {
                    QZXTools.popToast(CommentActivity.this, "还没写评语呢！", false);
                    comment_words.setSelection(0);
                    return;
                }

                if (comment_rating_bar.getRating() == 0) {
                    QZXTools.popToast(CommentActivity.this, "至少给一点星吧！", false);
                    return;
                }

                CircleProgressDialogFragment circleProgressDialogFragment = new CircleProgressDialogFragment();
                circleProgressDialogFragment.show(getSupportFragmentManager(), CircleProgressDialogFragment.class.getSimpleName());

                String url = UrlUtils.BaseUrl + UrlUtils.SaveUserComment;
                Map<String, String> paraMap = new HashMap<>();
                paraMap.put("userId", UserUtils.getUserId());
                paraMap.put("shareId", shareId);
                paraMap.put("shareTitle", shareTitle);
                paraMap.put("resId", resId);
                paraMap.put("resName", resName);
                paraMap.put("resComment", comment_words.getText().toString().trim());
                paraMap.put("resStars", (int) (comment_rating_bar.getRating() * 2) + "");
                OkHttp3_0Utils.getInstance().asyncPostOkHttp(url, paraMap, new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                QZXTools.popToast(CommentActivity.this, "评论失败", false);
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
                                Map<String, Object> resultMap = gson.fromJson(result, new TypeToken<Map<String, Object>>() {
                                }.getType());

                                String msg = (String) resultMap.get("msg");

                                QZXTools.popToast(CommentActivity.this, msg, false);

                                if (circleProgressDialogFragment != null)
                                    circleProgressDialogFragment.dismissAllowingStateLoss();

                                PreViewDisplayBean preViewDisplayBean = new PreViewDisplayBean();
                                preViewDisplayBean.setCurPosition(curPosition);
                                preViewDisplayBean.setAvgStar(comment_rating_bar.getRating() * 2);
                                preViewDisplayBean.setShareTitle(shareTitle);

                                EventBus.getDefault().post(preViewDisplayBean, Constant.click_cloud_item_ping_jia_submit);

                                finish();
                            }
                        });
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
