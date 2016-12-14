package com.yyg365.interestbar.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yyg365.interestbar.ui.fragment.CommonwealBaseFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-7-24.
 */
public class FragmentAdapter extends FragmentStatePagerAdapter {

    private List<CommonwealBaseFragment> fragmentList = new ArrayList<CommonwealBaseFragment>();

    public FragmentAdapter(FragmentManager fm, List<CommonwealBaseFragment> pagers) {
        super(fm);
        this.fragmentList = pagers;
    }

    @Override
    public CommonwealBaseFragment getItem(int position) {
        return fragmentList.get(position);
    }

    @Override
    public int getCount() {
        return fragmentList.size();
    }
}
