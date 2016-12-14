package com.yyg365.interestbar.ui.activity;

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

import com.android.volley.toolbox.ImageLoader;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.ProfileFragmentAdapter;
import com.yyg365.interestbar.ui.fragment.ProfilePrizeFragment;
import com.yyg365.interestbar.ui.fragment.ProfileShowShareFragment;
import com.yyg365.interestbar.ui.fragment.ProfileWinningFragment;
import com.yyg365.interestbar.ui.fragment.ShowShareFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 晒单页面
 */
public class ShowShareActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";
    public static final String CURRENT_USER_ID = "current_user_id";
    public static final String IS_PRODUCT = "IS_PRODUCT";

    private ViewPager showSharePager;
    private CheckedTextView tabPicView;
    private CheckedTextView tabVideoView;

    private int currentIndex;

    private ShowShareFragmentAdapter mFragmentAdapter;

    private long userId;
    private boolean isProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_share);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("晒单分享");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        showSharePager = (ViewPager) findViewById(R.id.page_show_share);
        tabPicView = (CheckedTextView) findViewById(R.id.tab_pic);
        tabVideoView = (CheckedTextView) findViewById(R.id.tab_video);
        tabPicView.setOnClickListener(this);
        tabVideoView.setOnClickListener(this);
        userId = getIntent().getLongExtra(CURRENT_USER_ID, 0l);
        isProduct = getIntent().getBooleanExtra(IS_PRODUCT, false);

        initFragment();
    }

    private void initFragment() {
        mFragmentAdapter = new ShowShareFragmentAdapter(this.getSupportFragmentManager(), userId, isProduct);
        showSharePager.setAdapter(mFragmentAdapter);

        if (currentIndex > mFragmentAdapter.getCount()) {
            currentIndex = mFragmentAdapter.getCount() - 1;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        showSharePager.setCurrentItem(currentIndex);
        showSharePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabPicView.setChecked(true);
                        tabVideoView.setChecked(false);
                        break;
                    case 1:
                        tabPicView.setChecked(false);
                        tabVideoView.setChecked(true);
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
            case R.id.tab_pic:
                showSharePager.setCurrentItem(0);
                break;
            case R.id.tab_video:
                showSharePager.setCurrentItem(1);
                break;
        }

    }

    public static class ShowShareFragmentAdapter extends FragmentStatePagerAdapter {
        private Long userId;
        private boolean isProduct;
        private List<Fragment> fragmentList = new ArrayList<Fragment>();

        public ShowShareFragmentAdapter(FragmentManager fm, Long userId, boolean isProduct) {
            super(fm);
            this.userId = userId;
            this.isProduct = isProduct;
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
                        f = ShowShareFragment.newInstance(userId, APIConstants.SHARE_SHOW_PIC_TYPE, isProduct);
                        fragmentList.add(f);
                        break;
                    case 1:
                        f = ShowShareFragment.newInstance(userId, APIConstants.SHARE_SHOW_VIDEO_TYPE, isProduct);
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
