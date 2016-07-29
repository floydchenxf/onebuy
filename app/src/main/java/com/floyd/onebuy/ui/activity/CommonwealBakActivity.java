package com.floyd.onebuy.ui.activity;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.FragmentAdapter;
import com.floyd.onebuy.ui.fragment.CommonwealFragment;
import com.floyd.onebuy.ui.fragment.FundFragment;

import java.util.ArrayList;
import java.util.List;

public class CommonwealBakActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private FragmentAdapter mFragmentAdapter;
    private ViewPager commonwealPager;

    private int currentIndex;

    private CheckedTextView tabCommonwealView;
    private CheckedTextView tabFundView;
    private int currentPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonweal_bak);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("公益");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);


        currentIndex = getIntent().getIntExtra(CURRENT_PAGE_INDEX, 0);
        commonwealPager = (ViewPager) findViewById(R.id.id_page_commonweal);
        tabCommonwealView = (CheckedTextView) findViewById(R.id.tab_commonweal_view);
        tabFundView = (CheckedTextView) findViewById(R.id.tab_fund_view);
        initFragments();
        tabCommonwealView.setOnClickListener(this);
        tabFundView.setOnClickListener(this);
    }

    private void initFragments() {
        CommonwealFragment commonwealFragment = new CommonwealFragment();
        FundFragment fundFragment = new FundFragment();
        mFragmentList.add(commonwealFragment);
        mFragmentList.add(fundFragment);
        mFragmentAdapter = new FragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        commonwealPager.setAdapter(mFragmentAdapter);
        if (currentIndex > mFragmentList.size()) {
            currentIndex = mFragmentList.size() - 1;
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
                currentPager = position;
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
}
