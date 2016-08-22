package com.floyd.onebuy.ui.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.FragmentAdapter;
import com.floyd.onebuy.ui.adapter.ProfileFragmentAdapter;
import com.floyd.onebuy.ui.fragment.ProfilePrizeFragment;
import com.floyd.onebuy.ui.fragment.ProfileShowShareFragment;
import com.floyd.onebuy.ui.fragment.ProfileWinningFragment;

import java.util.ArrayList;
import java.util.List;

public class PersionProfileActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";

    private int currentIndex;

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ProfileFragmentAdapter mFragmentAdapter;
    private ViewPager profilePager;
    private CheckedTextView tabWinningView;
    private CheckedTextView tabPrizeView;
    private CheckedTextView tabShowShareView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion_profile);
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("个人中心");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        currentIndex = getIntent().getIntExtra(CURRENT_PAGE_INDEX, 0);

        profilePager = (ViewPager) findViewById(R.id.page_profile);
        tabWinningView = (CheckedTextView) findViewById(R.id.tab_winning_view);
        tabPrizeView = (CheckedTextView) findViewById(R.id.tab_prize_view);
        tabShowShareView = (CheckedTextView) findViewById(R.id.tab_show_share_view);

        initFragment();
    }

    private void initFragment() {
        ProfileWinningFragment winningFragment = new ProfileWinningFragment();
        ProfilePrizeFragment prizeFragment = new ProfilePrizeFragment();
        ProfileShowShareFragment showShareFragment = new ProfileShowShareFragment();

        mFragmentList.add(winningFragment);
        mFragmentList.add(prizeFragment);
        mFragmentList.add(showShareFragment);
        mFragmentAdapter = new ProfileFragmentAdapter(this.getSupportFragmentManager(), mFragmentList);
        profilePager.setAdapter(mFragmentAdapter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.title_back:
                this.finish();
                break;

        }

    }
}
