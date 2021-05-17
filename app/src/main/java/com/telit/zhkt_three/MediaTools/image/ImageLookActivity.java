package com.telit.zhkt_three.MediaTools.image;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.MediaTools.CommentActivity;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.ActivityUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


/**
 * 首先约定：
 * 1、传入资源字符串
 * 2、传入当前的资源下标位置
 * <p>
 * 使用示例：
 * Intent intent_img = new Intent(getContext(), ImageLookActivity.class);
 * ArrayList<String> imgFilePathList = new ArrayList<>();
 * imgFilePathList.add(clickTV.getPreviewUrl());
 * intent_img.putStringArrayListExtra("imgResources", imgFilePathList);
 * intent_img.putExtra("curImgIndex", 0);
 * <p>
 * intent_img.putExtra("shareId", clickTV.getShareId() + "");
 * intent_img.putExtra("shareTitle", clickTV.getShareTitle());
 * intent_img.putExtra("resId", clickTV.getResId());
 * intent_img.putExtra("resName", clickTV.getFileName());
 * <p>
 * getContext().startActivity(intent_img);
 * <p>
 * 大图展示规则:
 * 如果是通过剪裁的图片，按照屏幕一半的大小放大、缩小
 * 如果没有通过剪裁，图片进行质量压缩或者尺寸大小压缩，同样的小于屏幕一半可以缩放
 * <p>
 * 应用的场景包括：
 * PersonInfoActivity 换头像【拍照、相册；需要裁剪 300x300】
 * BankSubjectiveToDoView 主观题的拍照作答，没有裁剪，尺寸压缩dstWidth=800
 * GroupDiscussFragment 发送拍照图片或者相册图片，需要剪裁 300x300
 */
public class ImageLookActivity extends BaseActivity implements View.OnClickListener {

    private Unbinder unbinder;

    @BindView(R.id.photo_tv_page)
    TextView photo_pages;
    @BindView(R.id.photoViewPager)
    PhotoViewPager photoViewPager;
    @BindView(R.id.photo_note)
    LinearLayout photo_note;
    @BindView(R.id.photo_prev)
    TextView photo_prev;
    @BindView(R.id.photo_next)
    TextView photo_next;

    private List<String> resPathList;

    private int currentPosition;

    private String shareId;
    private String shareTitle;
    private String resId;
    private String resName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_look);
        unbinder = ButterKnife.bind(this);

        QZXTools.logE("图片预览",null);

        EventBus.getDefault().register(this);

        Intent intent = getIntent();
        boolean needComment = intent.getBooleanExtra("NeedComment", true);
        if (!needComment) {
            photo_note.setVisibility(View.GONE);
        }

        //获取资源字符串
        resPathList = intent.getStringArrayListExtra("imgResources");
        //获取当前资源的下标位置
        currentPosition = intent.getIntExtra("curImgIndex", 0);

        //评论接口需要
        shareId = intent.getStringExtra("shareId");
        shareTitle = intent.getStringExtra("shareTitle");
        resId = intent.getStringExtra("resId");
        resName = intent.getStringExtra("resName");

        String flag = intent.getStringExtra("flag");
        String type = intent.getStringExtra("type");

        //是否存在评论内容
        String resComment = intent.getStringExtra("resComment");

        if (TextUtils.isEmpty(shareId)) {
            //隐藏评论
            photo_note.setVisibility(View.GONE);
        } else {
            //防止重复提交
            if (resComment == null) {
                photo_note.setVisibility(View.GONE);
            } else {
                photo_note.setVisibility(View.GONE);
            }
        }


        if (resPathList.size() == 1) {
            photo_next.setVisibility(View.INVISIBLE);
            photo_prev.setVisibility(View.INVISIBLE);
        }

        if (currentPosition == 0) {
            photo_prev.setVisibility(View.INVISIBLE);
        } else if (currentPosition == resPathList.size() - 1) {
            photo_next.setVisibility(View.INVISIBLE);
        }

        if (resPathList == null || resPathList.size() <= 0) {
            QZXTools.popCommonToast(this, "图片资源地址无效", false);
            return;
        }

        PhotoViewAdapter photoViewAdapter = new PhotoViewAdapter(resPathList, this,flag,type);
        photoViewPager.setAdapter(photoViewAdapter);

        photoViewPager.setCurrentItem(currentPosition, false);
        photo_pages.setText(currentPosition + 1 + "/" + resPathList.size());
        photoViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                currentPosition = position;
                photo_pages.setText(currentPosition + 1 + "/" + resPathList.size());

                QZXTools.logE("position=" + position, null);

                if (position >= (resPathList.size() - 1)) {
                    photo_next.setVisibility(View.INVISIBLE);
                    photo_prev.setVisibility(View.VISIBLE);
                } else if (position <= 0) {
                    photo_prev.setVisibility(View.INVISIBLE);
                    photo_next.setVisibility(View.VISIBLE);
                } else {
                    //注意如果只有0和1这个是完全不走的呀，所以上面的两个判断还是要写全，显示和隐藏
                    photo_prev.setVisibility(View.VISIBLE);
                    photo_next.setVisibility(View.VISIBLE);
                }
            }
        });

        photo_note.setOnClickListener(this);
        photo_prev.setOnClickListener(this);
        photo_next.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }

        EventBus.getDefault().unregister(this);

        super.onDestroy();
    }


    @Subscriber(tag = Constant.Close_Discuss_Img, mode = ThreadMode.MAIN)
    public void closeDiscussImg(String desc) {
        if (ActivityUtils.isForeground(this)) {
            finish();
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_note:

                QZXTools.logE("shareId=" + shareId + ";shareTitle=" + shareTitle +
                        ";resId=" + resId + ";resName=" + resName, null);

                if (TextUtils.isEmpty(shareId) || TextUtils.isEmpty(shareTitle)
                        || TextUtils.isEmpty(resId) || TextUtils.isEmpty(resName)) {
                    QZXTools.popToast(ImageLookActivity.this, "缺少评论所需的参数！", false);
                    return;
                }

                Intent intent_comment = new Intent(ImageLookActivity.this, CommentActivity.class);
                intent_comment.putExtra("shareId", shareId);
                intent_comment.putExtra("shareTitle", shareTitle);
                intent_comment.putExtra("resId", resId);
                intent_comment.putExtra("resName", resName);
                startActivity(intent_comment);
                break;
            case R.id.photo_prev:
                currentPosition--;
                if (currentPosition >= 0) {
                    if (currentPosition == 0) {
                        photo_prev.setVisibility(View.INVISIBLE);
                    } else {
                        photo_prev.setVisibility(View.VISIBLE);
                    }
                    photo_next.setVisibility(View.VISIBLE);
                    photoViewPager.setCurrentItem(currentPosition, true);
                }
                break;
            case R.id.photo_next:
                currentPosition++;
                if (currentPosition <= resPathList.size() - 1) {
                    if (currentPosition == resPathList.size() - 1) {
                        photo_next.setVisibility(View.INVISIBLE);
                    } else {
                        photo_next.setVisibility(View.VISIBLE);
                    }
                    photo_prev.setVisibility(View.VISIBLE);
                    photoViewPager.setCurrentItem(currentPosition, true);
                }
                break;
        }
    }
}
