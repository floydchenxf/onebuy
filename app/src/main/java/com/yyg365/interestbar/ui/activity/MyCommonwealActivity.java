package com.yyg365.interestbar.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.yyg365.interestbar.biz.vo.commonweal.TypeVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.fragment.MyCommonwealFragment;
import com.yyg365.interestbar.ui.fragment.MyFundFragment;

import java.util.ArrayList;
import java.util.List;

public class MyCommonwealActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";

    private MyCommonwealAdapter mFragmentAdapter;
    private ViewPager commonwealPager;

    private int currentIndex;

    private CheckedTextView tabCommonwealView;
    private CheckedTextView tabFundView;
    private ImageView moreView;

    private ListView fundTypeListView;
    private SimpleAdapter simpleAdapter;

    private List<TypeVO> typeVOs;

    private float onedp;

    private Long userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_commonweal);

        onedp = this.getResources().getDimension(R.dimen.one_dp);
        userId = getIntent().getLongExtra("USER_ID", 0);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("我的公益");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);
        moreView = (ImageView) findViewById(R.id.right);
        moreView.setOnClickListener(this);

        currentIndex = getIntent().getIntExtra(CURRENT_PAGE_INDEX, 0);
        commonwealPager = (ViewPager) findViewById(R.id.id_page_commonweal);
        tabCommonwealView = (CheckedTextView) findViewById(R.id.tab_commonweal_view);
        tabFundView = (CheckedTextView) findViewById(R.id.tab_fund_view);
        initFragments();
        tabCommonwealView.setOnClickListener(this);
        tabFundView.setOnClickListener(this);
    }

    private void initFragments() {
        mFragmentAdapter = new MyCommonwealAdapter(this.getSupportFragmentManager(), userId);
        commonwealPager.setAdapter(mFragmentAdapter);

        if (currentIndex > mFragmentAdapter.getCount()) {
            currentIndex = mFragmentAdapter.getCount() - 1;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        commonwealPager.setCurrentItem(currentIndex);
        commonwealPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        tabCommonwealView.setChecked(true);
                        tabFundView.setChecked(false);
                        break;
                    case 1:
                        tabCommonwealView.setChecked(false);
                        tabFundView.setChecked(true);
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }


        });

        if (currentIndex == 0) {
            tabCommonwealView.setChecked(true);
            tabFundView.setChecked(false);
        } else {
            tabCommonwealView.setChecked(false);
            tabFundView.setChecked(true);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_fund_view:
                commonwealPager.setCurrentItem(1);
                break;
            case R.id.tab_commonweal_view:
                commonwealPager.setCurrentItem(0);
                break;
            case R.id.title_back:
                this.finish();
                break;
        }
    }

    public static class MyCommonwealAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> fragmentList = new ArrayList<Fragment>();
        private Long userId;

        public MyCommonwealAdapter(FragmentManager fm, Long userId) {
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
                        f = MyCommonwealFragment.newInstance(userId);
                        fragmentList.add(f);
                        break;
                    case 1:
                        f = MyFundFragment.newInstance(userId);
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
