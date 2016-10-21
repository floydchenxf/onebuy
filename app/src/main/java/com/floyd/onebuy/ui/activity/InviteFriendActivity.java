package com.floyd.onebuy.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.fragment.InviteFriendFragment;
import com.floyd.onebuy.ui.fragment.InviteFriendRecordFragment;
import com.floyd.onebuy.ui.fragment.ShowShareFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 晒单页面
 */
public class InviteFriendActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";
    public static final String CURRENT_USER_ID = "current_user_id";
    public static final String IS_PRODUCT = "IS_PRODUCT";

    private ViewPager inviteFriendPager;
    private CheckedTextView tabInviteView;
    private CheckedTextView tabRecordView;

    private int currentIndex;

    private InviteFriendFragmentAdapter mFragmentAdapter;

    private long userId;
    private boolean isProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_record);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("邀请好友");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        inviteFriendPager = (ViewPager) findViewById(R.id.page_invite);
        tabInviteView = (CheckedTextView) findViewById(R.id.tab_invite);
        tabRecordView = (CheckedTextView) findViewById(R.id.tab_invite_record);
        tabInviteView.setOnClickListener(this);
        tabRecordView.setOnClickListener(this);
        userId = getIntent().getLongExtra(CURRENT_USER_ID, 0l);
        isProduct = getIntent().getBooleanExtra(IS_PRODUCT, false);

        initFragment();
    }

    private void initFragment() {
        mFragmentAdapter = new InviteFriendFragmentAdapter(this.getSupportFragmentManager(), userId);
        inviteFriendPager.setAdapter(mFragmentAdapter);

        if (currentIndex > mFragmentAdapter.getCount()) {
            currentIndex = mFragmentAdapter.getCount() - 1;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        inviteFriendPager.setCurrentItem(currentIndex);
        inviteFriendPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabInviteView.setChecked(true);
                        tabRecordView.setChecked(false);
                        break;
                    case 1:
                        tabInviteView.setChecked(false);
                        tabRecordView.setChecked(true);
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
            case R.id.tab_invite:
                inviteFriendPager.setCurrentItem(0);
                break;
            case R.id.tab_invite_record:
                inviteFriendPager.setCurrentItem(1);
                break;
        }

    }

    public static class InviteFriendFragmentAdapter extends FragmentStatePagerAdapter {
        private Long userId;
        private List<Fragment> fragmentList = new ArrayList<Fragment>();

        public InviteFriendFragmentAdapter(FragmentManager fm, Long userId) {
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
                        f = InviteFriendFragment.newInstance(userId);
                        fragmentList.add(f);
                        break;
                    case 1:
                        f = InviteFriendRecordFragment.newInstance(userId);
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
