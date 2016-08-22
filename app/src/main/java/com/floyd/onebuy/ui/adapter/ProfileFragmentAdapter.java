package com.floyd.onebuy.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.floyd.onebuy.ui.fragment.CommonwealBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/8/23.
 */
public class ProfileFragmentAdapter extends FragmentStatePagerAdapter  {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();

    public ProfileFragmentAdapter(FragmentManager fm, List<Fragment> pagers) {
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
