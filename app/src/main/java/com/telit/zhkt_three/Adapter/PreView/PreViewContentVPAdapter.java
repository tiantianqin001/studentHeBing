package com.telit.zhkt_three.Adapter.PreView;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * author: qzx
 * Date: 2019/12/20 10:58
 */
public class PreViewContentVPAdapter extends FragmentStatePagerAdapter {
    private List<Fragment> fragmentList;

    /**
     * 传入Fragment的列表对象
     */
    public void setFragmentList(List<Fragment> fragmentList) {
        this.fragmentList = fragmentList;
    }

    public PreViewContentVPAdapter(FragmentManager fm) {
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
