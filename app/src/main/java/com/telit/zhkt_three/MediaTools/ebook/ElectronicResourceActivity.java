package com.telit.zhkt_three.MediaTools.ebook;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.AutoLearning.VPEBookAdapter;
import com.telit.zhkt_three.Adapter.tree_adpter.Node;
import com.telit.zhkt_three.Adapter.tree_adpter.NodeUtils;
import com.telit.zhkt_three.Adapter.tree_adpter.TreeViewAdapter;
import com.telit.zhkt_three.CusomPater;
import com.telit.zhkt_three.CustomView.RoundCornerImageView;
import com.telit.zhkt_three.JavaBean.Gson.EbookBean;
import com.telit.zhkt_three.JavaBean.Resource.EUnitBean;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.UUID;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * ViewPager的滑动效果电子书展示
 * <p>
 * 需要解析tbk包：传入文件路径以及封面url
 */
public class ElectronicResourceActivity extends BaseActivity implements View.OnClickListener, TreeViewAdapter.EbookClickCallback {

    private Unbinder unbinder;

    @BindView(R.id.elecRes_back)
    ImageView elecRes_back;
    @BindView(R.id.elecRes_viewpager)
    CusomPater viewPager;

    @BindView(R.id.elecRes_pull_tag)
    FrameLayout elecRes_pull_tag;
    @BindView(R.id.elecRes_pull_icon)
    ImageView elecRes_pull_icon;

    @BindView(R.id.elecRes_pull_content_layout)
    LinearLayout elecRes_pull_content_layout;
    @BindView(R.id.elecRes_pull_cover)
    RoundCornerImageView elecRes_pull_cover;
    @BindView(R.id.elecRes_pull_title)
    TextView elecRes_pull_title;
    @BindView(R.id.elecRes_rv_chapter)
    RecyclerView elecRes_rv_chapter;

    //动画
    private Animation FromRightToLeftAnimation;
    private Animation FromLeftToRightAnimation;

    private ZipFile zipFile;
    private List<String> images; //图片地址
    private EbookBean data;

    private String ebookFilePath;

    //章节树状适配器
    private TreeViewAdapter treeAdpter;
    private List<Node> nodesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_electronic_resource);
        unbinder = ButterKnife.bind(this);

        QZXTools.logE("电子书课本详情1",null);

        Intent intent = getIntent();

        ebookFilePath = intent.getStringExtra("EBookFilePath");
        String coverPath = intent.getStringExtra("CoverUrl");

        if (TextUtils.isEmpty(ebookFilePath)) {
            QZXTools.popCommonToast(this, "电子课本资源地址无效", false);
            return;
        }

        //设置封面
        if (!TextUtils.isEmpty(coverPath)) {
            Glide.with(this).load(coverPath).error(R.mipmap.no_cover).placeholder(R.mipmap.no_cover).into(elecRes_pull_cover);
        }

        FromRightToLeftAnimation = AnimationUtils.loadAnimation(this, R.anim.right_to_left_show);
        FromLeftToRightAnimation = AnimationUtils.loadAnimation(this, R.anim.left_to_right_hide);

        //初始化tbk文件，其实就是zip文件
        zipFile();

        //设置侧栏标题
        elecRes_pull_title.setText(data.getTitle());

        VPEBookAdapter vpeBookAdapter = new VPEBookAdapter(this, images, zipFile);
        viewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager.setAdapter(vpeBookAdapter);

        treeAdpter = new TreeViewAdapter();
        treeAdpter.setEbookClickCallback(this);
        nodesList = new ArrayList<>();

        treeEbookInfo(data.getList(), 0, "");

        elecRes_rv_chapter.setLayoutManager(new LinearLayoutManager(this));
        elecRes_rv_chapter.setOverScrollMode(View.OVER_SCROLL_NEVER);
        treeAdpter.setDatas(nodesList);
        NodeUtils.tidyNodes(nodesList);
        NodeUtils.setShowLevel(nodesList, 0);
        elecRes_rv_chapter.setAdapter(treeAdpter);

        elecRes_pull_tag.setOnClickListener(this);
        elecRes_back.setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        super.onDestroy();
    }

    /**
     * 递归树---章节
     * <p>
     * 如何保证id不重复？？？
     *
     * @param pid 代表其树状节点的父类id,注意不能为null,顶层父类为空字符串
     */
    private void treeEbookInfo(List<EUnitBean> eUnitBeanList, int level, String pid) {
        for (int i = 0; i < eUnitBeanList.size(); i++) {
            //自身节点树
            String id = UUID.randomUUID().toString().replaceAll("-", "");

            QZXTools.logE("id=" + id + ";pid=" + pid, null);

            Node node = new Node(id, pid, level, eUnitBeanList.get(i).getChapterName());
            node.setStart(eUnitBeanList.get(i).getStart());

            //不显示checkbox
            node.setHideCheckBox(true);

//            QZXTools.logE("node id=" + node.getId() + ";pid=" + node.getPid(), null);
            nodesList.add(node);

            //有子类
            if (eUnitBeanList.get(i).getLson() != null && eUnitBeanList.get(i).getLson().size() > 0) {
                int tempLevel = level + 1;
                treeEbookInfo(eUnitBeanList.get(i).getLson(), tempLevel, id);
            }
        }
    }

    /**
     * 解压zip文件
     */
    private void zipFile() {
        //解析image文件夹图片
        try {
            zipFile = new ZipFile(ebookFilePath);
            Enumeration<? extends ZipEntry> entries = zipFile.entries();
            if (images == null) {
                images = new ArrayList<>();
            }
            while (entries.hasMoreElements()) {
                ZipEntry zipEntry = entries.nextElement();
//                QZXTools.logE("getName=" + zipEntry.getName(), null);
                if (!zipEntry.getName().equals("index.json")) {
                    images.add(zipEntry.getName());
                }
            }

            //解析json文件
            String json = QZXTools.toConvertString(zipFile.getInputStream(zipFile.getEntry("index.json")));
            QZXTools.logE("json=" + json, null);
            if (!TextUtils.isEmpty(json)) {
                data = new Gson().fromJson(json, EbookBean.class);
//                QZXTools.logE(data.toString(), null);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private boolean isShown = false;

    private int preValue;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.elecRes_back:
                finish();
                break;
            case R.id.elecRes_pull_tag:
                preValue = 0;
                if (isShown) {
                    //设置gone是无法测量elecRes_pull_content_layout.getMeasuredWidth()
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, elecRes_pull_content_layout.getMeasuredWidth());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) elecRes_pull_tag.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.rightMargin -= offset;
                            elecRes_pull_tag.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(elecRes_pull_icon, 180.0f, 0.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            elecRes_pull_content_layout.setVisibility(View.INVISIBLE);
                            isShown = false;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    elecRes_pull_content_layout.startAnimation(FromLeftToRightAnimation);
                    valueAnimator.start();
                } else {
                    ValueAnimator valueAnimator = ValueAnimator.ofInt(0, elecRes_pull_content_layout.getMeasuredWidth());
                    valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                        @Override
                        public void onAnimationUpdate(ValueAnimator animation) {
                            RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) elecRes_pull_tag.getLayoutParams();
                            int value = (int) animation.getAnimatedValue();
                            int offset = value - preValue;
                            layoutParams.rightMargin += offset;
                            elecRes_pull_tag.setLayoutParams(layoutParams);
                            preValue = value;
                        }
                    });
                    valueAnimator.addListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            iconRotate(elecRes_pull_icon, 0f, 180.0f);
                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {
                            isShown = true;
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });
                    valueAnimator.setDuration(500);
                    elecRes_pull_content_layout.setVisibility(View.VISIBLE);
                    elecRes_pull_content_layout.startAnimation(FromRightToLeftAnimation);
                    valueAnimator.start();
                }
                break;
        }
    }

    /**
     * 图标的旋转180度
     */
    private void iconRotate(View view, float fromDegrees, float toDegrees) {
        RotateAnimation rotateAnimation = new RotateAnimation(fromDegrees, toDegrees,
                Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setDuration(500);
        rotateAnimation.setFillAfter(true);
        view.startAnimation(rotateAnimation);
    }

    /**
     * treeItem click
     */
    @Override
    public void clickItem(View view, int position) {
        int startIndex = nodesList.get(position).getStart();
        QZXTools.logE("position=" + position + ";startIndex=" + startIndex, null);
        viewPager.setCurrentItem((startIndex - 1) / 2, true);
    }
}
