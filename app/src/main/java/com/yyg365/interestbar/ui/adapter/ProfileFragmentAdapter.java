package com.yyg365.interestbar.ui.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.yyg365.interestbar.ui.fragment.CommonwealBaseFragment;
import com.yyg365.interestbar.ui.fragment.ProfilePrizeFragment;
import com.yyg365.interestbar.ui.fragment.ProfileShowShareFragment;
import com.yyg365.interestbar.ui.fragment.ProfileWinningFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenxiaofeng on 16/8/23.
 */
public class ProfileFragmentAdapter extends FragmentStatePagerAdapter {

    private List<Fragment> fragmentList = new ArrayList<Fragment>();
    private Long userId;

    public ProfileFragmentAdapter(FragmentManager fm, Long userId) {
        super(fm);
        this.userId = userId;
    }

    @Override
    public Fragment getItem(int position) {
        Fragment f = null;

        if (position < fragmentList.size()) {
           f = fragmentList.get(position);
        }

        if (f == null) {
            switch (position) {
                case 0:
                    f = ProfileWinningFragment.newInstance(userId);
                    fragmentList.add(f);
                    break;
                case 1:
                    f = ProfilePrizeFragment.newInstance(userId);
                    fragmentList.add(f);
                    break;
                case 2:
                    f = ProfileShowShareFragment.newInstance(userId);
                    fragmentList.add(f);
                    break;
            }
        }
        return f;
    }

    @Override
    public int getCount() {
        return 3;
    }
}
