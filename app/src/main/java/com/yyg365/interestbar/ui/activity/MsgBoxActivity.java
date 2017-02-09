package com.yyg365.interestbar.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.fragment.CommissionFragment;
import com.yyg365.interestbar.ui.fragment.MsgBoxFragment;

import java.util.ArrayList;
import java.util.List;

public class MsgBoxActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_USER_ID = "current_user_id";

    private ViewPager msgBoxPager;
    private CheckedTextView unreadMsgBoxView;
    private CheckedTextView readMsgBoxView;

    private int currentIndex;

    private MsgBoxFragmentAdapter mFragmentAdapter;

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg_box);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("我的消息");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        msgBoxPager = (ViewPager) findViewById(R.id.page_msg_box);
        unreadMsgBoxView = (CheckedTextView) findViewById(R.id.tab_unread_msg);
        readMsgBoxView = (CheckedTextView) findViewById(R.id.tab_read_msg);
        unreadMsgBoxView.setOnClickListener(this);
        readMsgBoxView.setOnClickListener(this);
        userId = getIntent().getLongExtra(CURRENT_USER_ID, 0l);

        initFragment();
    }

    private void initFragment() {
        mFragmentAdapter = new MsgBoxFragmentAdapter(this.getSupportFragmentManager(), userId);
        msgBoxPager.setAdapter(mFragmentAdapter);

        if (currentIndex > mFragmentAdapter.getCount()) {
            currentIndex = mFragmentAdapter.getCount() - 1;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        msgBoxPager.setCurrentItem(currentIndex);
        msgBoxPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        unreadMsgBoxView.setChecked(true);
                        readMsgBoxView.setChecked(false);
                        break;
                    case 1:
                        unreadMsgBoxView.setChecked(false);
                        readMsgBoxView.setChecked(true);
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
            case R.id.tab_unread_msg:
                msgBoxPager.setCurrentItem(0);
                break;
            case R.id.tab_read_msg:
                msgBoxPager.setCurrentItem(1);
                break;
        }

    }

    public static class MsgBoxFragmentAdapter extends FragmentStatePagerAdapter {
        private Long userId;
        private List<Fragment> fragmentList = new ArrayList<Fragment>();

        public MsgBoxFragmentAdapter(FragmentManager fm, Long userId) {
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
                        f = MsgBoxFragment.newInstance(userId, 1);
                        fragmentList.add(f);
                        break;
                    case 1:
                        f = MsgBoxFragment.newInstance(userId, 2);
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
