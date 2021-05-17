package com.telit.zhkt_three.Activity.PreView;

import android.Manifest;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gyf.immersionbar.ImmersionBar;
import com.telit.zhkt_three.Activity.BaseActivity;
import com.telit.zhkt_three.Adapter.PreView.PreViewContentVPAdapter;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.CustomHeadLayout;
import com.telit.zhkt_three.Fragment.PreView.CollectionResourcesFragment;
import com.telit.zhkt_three.Fragment.PreView.PreCloudFragment;
import com.telit.zhkt_three.Fragment.PreView.PreEleFragment;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.BuriedPointUtils;
import com.telit.zhkt_three.Utils.QZXTools;
import com.telit.zhkt_three.Utils.UserUtils;
import com.telit.zhkt_three.Utils.ZBVPermission;
import com.telit.zhkt_three.Utils.eventbus.EventBus;
import com.telit.zhkt_three.Utils.eventbus.Subscriber;
import com.telit.zhkt_three.Utils.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * notes 平台资源废弃
 */
public class PreViewActivity extends BaseActivity implements View.OnClickListener, ZBVPermission.PermPassResult {

    private Unbinder unbinder;

    @BindView(R.id.preview_head_layout)
    CustomHeadLayout customHeadLayout;

    @BindView(R.id.pre_left_layout)
    LinearLayout pre_left_layout;

    @BindView(R.id.pre_cloud_click_layout)
    LinearLayout pre_cloud_click_layout;
    @BindView(R.id.pre_ele_click_layout)
    LinearLayout pre_ele_click_layout;

    @BindView(R.id.pre_left_pull)
    ImageView pre_left_pull;
    @BindView(R.id.pre_cloud_img)
    ImageView pre_cloud_img;
    @BindView(R.id.pre_cloud_layout)
    LinearLayout pre_cloud_layout;
    @BindView(R.id.pre_ele_img)
    ImageView pre_ele_img;
    @BindView(R.id.pre_ele_layout)
    LinearLayout pre_ele_layout;
    @BindView(R.id.collect_resources_img)
    ImageView collect_resources_img;
    @BindView(R.id.collect_resources_layout)
    LinearLayout collect_resources_layout;

    @BindView(R.id.collection_resources_click_layout)
    LinearLayout collection_resources_click_layout;

    @BindView(R.id.pre_cache_click_layout)
    LinearLayout pre_cache_click_layout;
    @BindView(R.id.pre_cache_size)
    TextView pre_cache_size;

    @BindView(R.id.pre_vp_content)
    ViewPager pre_vp_content;

    private PreCloudFragment preCloudFragment;
    private static final String[] NeedPermission = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_view);
        //设置导航栏的颜色
        ImmersionBar.with(this).navigationBarColor(R.color.colorPrimary).init();
        unbinder = ButterKnife.bind(this);

        //请求SD卡权限
        ZBVPermission.getInstance().setPermPassResult(this);
        if (ZBVPermission.getInstance().hadPermissions(this, NeedPermission)) {
            isSDEnable = true;
            handlerCacheSizeShow();
        } else {
            ZBVPermission.getInstance().requestPermissions(this, NeedPermission);
        }

        EventBus.getDefault().register(this);

        //设置头像信息等

        customHeadLayout.setHeadInfo(UserUtils.getAvatarUrl(), UserUtils.getStudentName(), UserUtils.getClassName());

        pre_cloud_click_layout.setOnClickListener(this);
        pre_ele_click_layout.setOnClickListener(this);
        collection_resources_click_layout.setOnClickListener(this);
        pre_left_pull.setOnClickListener(this);
        pre_cloud_img.setOnClickListener(this);
        pre_ele_img.setOnClickListener(this);
        collect_resources_img.setOnClickListener(this);

        pre_cache_click_layout.setOnClickListener(this);

        //处理ViewPager
        List<Fragment> list = new ArrayList<>();

        preCloudFragment = new PreCloudFragment();
        PreEleFragment preEleFragment = new PreEleFragment();
        CollectionResourcesFragment collectionResourcesFragment = new CollectionResourcesFragment();

        list.add(preCloudFragment);
        list.add(preEleFragment);
        list.add(collectionResourcesFragment);
        PreViewContentVPAdapter preViewContentVPAdapter = new PreViewContentVPAdapter(getSupportFragmentManager());
        preViewContentVPAdapter.setFragmentList(list);
        pre_vp_content.setAdapter(preViewContentVPAdapter);
        pre_vp_content.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }
            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    pre_cloud_click_layout.setSelected(true);
                    pre_ele_click_layout.setSelected(false);
                    collection_resources_click_layout.setSelected(false);

                    pre_cloud_img.setVisibility(View.GONE);
                    pre_cloud_layout.setVisibility(View.VISIBLE);
                    pre_ele_img.setVisibility(View.VISIBLE);
                    pre_ele_layout.setVisibility(View.GONE);
                    collect_resources_img.setVisibility(View.VISIBLE);
                    collect_resources_layout.setVisibility(View.GONE);
                }else if (position == 1) {
                    pre_cloud_click_layout.setSelected(false);
                    pre_ele_click_layout.setSelected(true);
                    collection_resources_click_layout.setSelected(false);

                    pre_cloud_img.setVisibility(View.VISIBLE);
                    pre_cloud_layout.setVisibility(View.GONE);
                    pre_ele_img.setVisibility(View.GONE);
                    pre_ele_layout.setVisibility(View.VISIBLE);
                    collect_resources_img.setVisibility(View.VISIBLE);
                    collect_resources_layout.setVisibility(View.GONE);
                } else {
                    pre_cloud_click_layout.setSelected(false);
                    pre_ele_click_layout.setSelected(false);
                    collection_resources_click_layout.setSelected(true);

                    pre_cloud_img.setVisibility(View.VISIBLE);
                    pre_cloud_layout.setVisibility(View.GONE);
                    pre_ele_img.setVisibility(View.VISIBLE);
                    pre_ele_layout.setVisibility(View.GONE);
                    collect_resources_img.setVisibility(View.GONE);
                    collect_resources_layout.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        pre_cloud_click_layout.performClick();
        pre_cloud_click_layout.setSelected(true);
    }
    @Override
    protected void onDestroy() {
        if (unbinder != null) {
            unbinder.unbind();
        }
        EventBus.getDefault().unregister(this);

        ZBVPermission.getInstance().recyclerAll();
        super.onDestroy();

        BuriedPointUtils.buriedPoint("2028","","","","");
    }
    /**
     * 跟新缓存大小视图
     */
    @Subscriber(tag = Constant.UPDATE_CACHE_VIEW, mode = ThreadMode.MAIN)
    public void updateCacheView(String sign) {
        QZXTools.logE("update sign=" + sign, null);
        if (sign.equals("update_cache")) {
            handlerCacheSizeShow();
        }
    }
    /**
     * 显示缓存大小
     */
    private void handlerCacheSizeShow() {
        String saveRecordPath = QZXTools.getExternalStorageForFiles(this, null) + File.separator + "disk";
        File savedCache = new File(saveRecordPath);
        if (savedCache.exists()) {
            if (savedCache.isDirectory()) {
                File[] savedFiles = savedCache.listFiles();
                long totalSize = 0;
                for (File file : savedFiles) {
                    totalSize += file.length();
                }
                //字节转化成M
                if (totalSize > 0) {
                    String stringSize = QZXTools.transformBytes(totalSize);
                    pre_cache_size.setText(stringSize);
                } else {
                    pre_cache_size.setText("0");
                }
            } else {
                pre_cache_size.setText("0");
            }
        } else {
            pre_cache_size.setText("0");
        }
    }
    /**
     * 清空缓存,包括记录的序列化文件 xxx/disk/preRecord.txt
     * notes：
     * /storage/emulated/0/Android/data/com.ahtelit.zbv.myapplication/files/disk/
     */
    private void clearCache() {
        String saveRecordPath = QZXTools.getExternalStorageForFiles(this, null) + File.separator + "disk";
        File savedCache = new File(saveRecordPath);
        if (savedCache.exists()) {
            if (savedCache.isDirectory()) {
                File[] savedFiles = savedCache.listFiles();
                for (File file : savedFiles) {
                    //一个一个文件删除
                    boolean success = file.delete();
                }
            }
        }
        pre_cache_size.setText("0");
    }

    //--------------权限
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ZBVPermission.getInstance().onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        ZBVPermission.getInstance().onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    //--------------权限

    //左侧栏是否处于隐藏状态，默认是展开的
    private boolean isLeftHiden = false;

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pre_cloud_click_layout:
            case R.id.pre_cloud_img:
                pre_vp_content.setCurrentItem(0, true);
                break;
            case R.id.pre_ele_click_layout:
            case R.id.pre_ele_img:
                pre_vp_content.setCurrentItem(1, true);
                break;
            case R.id.collection_resources_click_layout:
            case R.id.collect_resources_img:
                pre_vp_content.setCurrentItem(2, true);
                break;
            case R.id.pre_left_pull:
                //隐藏/显示左侧栏
                if (isLeftHiden) {
                    pre_left_layout.setVisibility(View.VISIBLE);
                } else {
                    pre_left_layout.setVisibility(View.GONE);
                }
                isLeftHiden = !isLeftHiden;
                break;
            case R.id.pre_cache_click_layout:
                //清理缓存files/disk/
                if (isSDEnable) {
                    clearCache();
                } else {
                    QZXTools.popToast(this, "缺少SDCard读取权限!!!", false);
                }
                break;
        }
    }

    /**
     * 返回键处理
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onBackPressed() {
        if (preCloudFragment != null) {
            if (!preCloudFragment.handlerBackKey()) {
                super.onBackPressed();
            }
        }
    }
    private boolean isSDEnable = false;
    @Override
    public void grantPermission() {
        isSDEnable = true;
        handlerCacheSizeShow();
    }
    @Override
    public void denyPermission() {
        isSDEnable = false;
    }


}
