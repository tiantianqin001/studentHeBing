package com.telit.zhkt_three.Adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/5/15 11:18
 */
public class VPLearningAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList;

    /**
     * 传入Fragment的列表对象
     */
    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public VPLearningAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int i) {
        return fragmentList.get(i);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
