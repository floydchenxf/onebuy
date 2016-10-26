package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.fragment.CommissionFragment;
import com.floyd.onebuy.ui.fragment.InviteFriendFragment;
import com.floyd.onebuy.ui.fragment.InviteFriendRecordFragment;

import java.util.ArrayList;
import java.util.List;

public class UserCommissionActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";
    public static final String CURRENT_USER_ID = "current_user_id";

    private ViewPager commissionPager;
    private CheckedTextView sourceView;
    private CheckedTextView consumeView;

    private int currentIndex;

    private CommissionFragmentAdapter mFragmentAdapter;

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_commission);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("我的佣金");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        commissionPager = (ViewPager) findViewById(R.id.page_commission);
        consumeView = (CheckedTextView) findViewById(R.id.tab_consume);
        sourceView = (CheckedTextView) findViewById(R.id.tab_source);
        consumeView.setOnClickListener(this);
        sourceView.setOnClickListener(this);
        userId = getIntent().getLongExtra(CURRENT_USER_ID, 0l);

        initFragment();
    }

    private void initFragment() {
        mFragmentAdapter = new CommissionFragmentAdapter(this.getSupportFragmentManager(), userId);
        commissionPager.setAdapter(mFragmentAdapter);

        if (currentIndex > mFragmentAdapter.getCount()) {
            currentIndex = mFragmentAdapter.getCount() - 1;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        commissionPager.setCurrentItem(currentIndex);
        commissionPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        sourceView.setChecked(true);
                        consumeView.setChecked(false);
                        break;
                    case 1:
                        sourceView.setChecked(false);
                        consumeView.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.tab_source:
                commissionPager.setCurrentItem(0);
                break;
            case R.id.tab_consume:
                commissionPager.setCurrentItem(1);
                break;
        }

    }

    public static class CommissionFragmentAdapter extends FragmentStatePagerAdapter {
        private Long userId;
        private List<Fragment> fragmentList = new ArrayList<Fragment>();

        public CommissionFragmentAdapter(FragmentManager fm, Long userId) {
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
                        f = CommissionFragment.newInstance(userId, 1);
                        fragmentList.add(f);
                        break;
                    case 1:
                        f = CommissionFragment.newInstance(userId, 2);
                        fragmentList.add(f);
                        break;
                }
            }
            return f;
        }

        @Override
        public int getCount() {
            return 2;
        }
    }
}
