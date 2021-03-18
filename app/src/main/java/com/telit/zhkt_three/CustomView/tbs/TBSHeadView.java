package com.telit.zhkt_three.CustomView.tbs;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebSettings;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Fragment.TBSMoreFragment;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.ShareUtil;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;


/**
 * author: qzx
 * Date: 2019/9/3 10:11
 */
public class TBSHeadView extends RelativeLayout implements View.OnClickListener {

    private ImageView img_delete;
    private TextView tv_url;
    private ImageView img_more;

    private Context context;

    private WebSettings webSettings;

    public void setWebSettings(WebSettings webSettings) {
        this.webSettings = webSettings;
    }

    public TBSHeadView(Context context) {
        this(context, null);
    }

    public TBSHeadView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TBSHeadView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        EventBus.getDefault().register(this);

        this.context = context;

        View view = LayoutInflater.from(context).inflate(R.layout.view_tbs_head, this, true);
        img_delete = view.findViewById(R.id.tbs_img_delete);
        tv_url = view.findViewById(R.id.tbs_text_url);
        img_more = view.findViewById(R.id.tbs_img_more);

        img_delete.setOnClickListener(this);
        img_more.setOnClickListener(this);

    }

    @Override
    protected void onDetachedFromWindow() {
        EventBus.getDefault().unregister(this);
        super.onDetachedFromWindow();
    }

    /**
     * 更新URL
     */
    public void updateTextUrl(String newUrl) {
        tv_url.setText(newUrl);
    }

    private TBSMoreFragment tbsMoreFragment;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tbs_img_delete:
                ((Activity) context).finish();
                break;
            case R.id.tbs_img_more:
                if (tbsMoreFragment == null) {
                    tbsMoreFragment = new TBSMoreFragment();
                }
                if (tbsMoreFragment.isVisible()) {
                    tbsMoreFragment.reset();
                    tbsMoreFragment.dismissAllowingStateLoss();
                }
                //TODO 这块被我改出问题了，延期修改
               // tbsMoreFragment.show((FragmentManager) ((Activity) context).getWindowManager(), TBSMoreFragment.class.getSimpleName());
                break;
        }
    }

    @Subscriber(tag = "BrowserMore", mode = ThreadMode.MAIN)
    public void receiverMore(String type) {
        QZXTools.logE("BrowsweMore type=" + type, null);
        switch (type) {
            case "share":
                ShareUtil.shareText(context, tv_url.getText().toString().trim(), "url");
                break;
            case "browser":
                Uri uri = Uri.parse(tv_url.getText().toString().trim());
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                context.startActivity(intent);
                break;
        }

        if (type.contains("font")) {
            String[] splits = type.split(":");
            switch (Integer.parseInt(splits[1])) {
                case 0:
                    webSettings.setTextSize(WebSettings.TextSize.SMALLEST);
                    break;
                case 1:
                    webSettings.setTextSize(WebSettings.TextSize.SMALLER);
                    break;
                case 2:
                    webSettings.setTextSize(WebSettings.TextSize.NORMAL);
                    break;
                case 3:
                    webSettings.setTextSize(WebSettings.TextSize.LARGER);
                    break;
                case 4:
                    webSettings.setTextSize(WebSettings.TextSize.LARGEST);
                    break;
            }
        }
    }
}
