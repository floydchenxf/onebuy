package com.floyd.onebuy.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-7-24.
 */
public class FragmentAdapter extends FragmentPagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    public FragmentAdapter(FragmentManager fm, List<Fragment> pagers) {
        super(fm);
        this.fragmentList = pagers;
    }

    @Override
    public Fragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
