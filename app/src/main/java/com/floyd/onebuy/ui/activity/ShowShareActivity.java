package com.floyd.onebuy.ui.activity;

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
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.ProfileFragmentAdapter;
import com.floyd.onebuy.ui.fragment.ProfilePrizeFragment;
import com.floyd.onebuy.ui.fragment.ProfileShowShareFragment;
import com.floyd.onebuy.ui.fragment.ProfileWinningFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * 晒单页面
 */
public class ShowShareActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";
    public static final String CURRENT_USER_ID = "current_user_id";

    private ViewPager showSharePager;
    private CheckedTextView tabPicView;
    private CheckedTextView tabVideoView;

    private int currentIndex;

    private ImageLoader mImageLoader;
    private ShowShareFragmentAdapter mFragmentAdapter;

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_share);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("晒单分享");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        showSharePager = (ViewPager) findViewById(R.id.page_show_share);
        tabPicView = (CheckedTextView) findViewById(R.id.tab_pic);
        tabVideoView = (CheckedTextView) findViewById(R.id.tab_video);
        tabPicView.setOnClickListener(this);

        userId = getIntent().getLongExtra(CURRENT_USER_ID, 0l);

        initFragment();
    }

    private void initFragment() {
        mFragmentAdapter = new ShowShareFragmentAdapter(this.getSupportFragmentManager(), userId);
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
        private List<Fragment> fragmentList = new ArrayList<Fragment>();

        public ShowShareFragmentAdapter(FragmentManager fm, Long userId) {
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
