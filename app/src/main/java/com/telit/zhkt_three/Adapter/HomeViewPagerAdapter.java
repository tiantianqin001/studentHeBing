package com.telit.zhkt_three.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.telit.zhkt_three.Activity.InteractiveScreen.SelectClassActivity;
import com.telit.zhkt_three.Constant.Constant;
import com.telit.zhkt_three.CustomView.SummerLayout;
import com.telit.zhkt_three.Fragment.HomeStopOneFragment;
import com.telit.zhkt_three.JavaBean.AppInfo;
import com.telit.zhkt_three.R;
import com.telit.zhkt_three.Utils.QZXTools;

import java.util.ArrayList;
import java.util.List;

/**
 * author: qzx
 * Date: 2019/2/26 16:49
 * <p>
 * PagerAdapter、FragmentPagerAdapter、FragmentStatePagerAdapter
 */
public class HomeViewPagerAdapter extends FragmentPagerAdapter {

    private final List<Fragment> fragments;
    private List<AppInfo> mList;
    private List<AppInfo> appInfos = new ArrayList<>();
    private int orientation;
    private Context mContext;
    private int pageCount;
    private List<AppInfo> appFistInfos;
    private List<AppInfo> appTwoInfos;

    public HomeViewPagerAdapter(FragmentManager fm,List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    /**
     * 返回总共的页码
     */
    public int totalPage() {
        return fragments.size();
    }


    private boolean isNeedChange = false;

    //计算正图的页面
    private int extraPage;
    private int currentPosition=0;



    private List<Integer> typesList;

    public void setNeedChange(boolean needChange) {
        isNeedChange = needChange;
    }

    public void setmList(List<AppInfo> mList) {
        this.mList = mList;
    }


    @Override
    public int getCount() {
//        QZXTools.logE("HomeViewPager getCount", null);
        return fragments.size();
    }


    @Override
    public Fragment getItem(int i) {
        return fragments.get(i);
    }


}
