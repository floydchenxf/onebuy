package com.floyd.onebuy.ui.activity;

import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.CheckedTextView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.floyd.onebuy.biz.vo.commonweal.TypeVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.FragmentAdapter;
import com.floyd.onebuy.ui.fragment.CommonwealBaseFragment;
import com.floyd.onebuy.ui.fragment.CommonwealFragment;
import com.floyd.onebuy.ui.fragment.FundFragment;
import com.floyd.onebuy.view.BasePopupWindow;
import com.floyd.onebuy.view.LeftDownPopupWindow;
import com.floyd.onebuy.view.RightTopPopupWindow;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommonwealBakActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";

    private List<CommonwealBaseFragment> mFragmentList = new ArrayList<CommonwealBaseFragment>();
    private FragmentAdapter mFragmentAdapter;
    private ViewPager commonwealPager;

    private int currentIndex;

    private CheckedTextView tabCommonwealView;
    private CheckedTextView tabFundView;
    private int currentPager;
    private ImageView moreView;

    private ListView fundTypeListView;
    private SimpleAdapter simpleAdapter;

    private RightTopPopupWindow myPopupWindow;

    private List<TypeVO> typeVOs;

    private float onedp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonweal_bak);

        onedp = this.getResources().getDimension(R.dimen.one_dp);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("公益");
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

        myPopupWindow = new RightTopPopupWindow(this);
        myPopupWindow.initView(R.layout.fund_type_pop, new BasePopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                fundTypeListView = (ListView) v.findViewById(R.id.fund_type_listview);
            }
        });
    }

    private void initFragments() {
        CommonwealFragment commonwealFragment = new CommonwealFragment();
        commonwealFragment.initSwitchTabListener(new CommonwealBaseFragment.SwitchTabListener() {
            @Override
            public void onCallback(Map<String, Object> data) {
                moreView.setVisibility(View.GONE);
            }
        });
        FundFragment fundFragment = new FundFragment();
        fundFragment.initSwitchTabListener(new CommonwealBaseFragment.SwitchTabListener() {
            @Override
            public void onCallback(Map<String, Object> data) {
                moreView.setVisibility(View.VISIBLE);
                myPopupWindow.setLocationView(moreView);

                typeVOs = (List<TypeVO>) data.get("result");
                List<Map<String, String>> aa = new ArrayList<Map<String, String>>();
                if (typeVOs != null && !typeVOs.isEmpty()) {
                    for (TypeVO v :typeVOs) {
                        Map<String, String> t = new HashMap<String, String>();
                        t.put("fundNameView", v.CodeName);
                        aa.add(t);
                    }
                }

                String[] from = {"fundNameView"};
                int[] to = {R.id.fund_type_view};
                simpleAdapter = new SimpleAdapter(CommonwealBakActivity.this, aa, R.layout.fund_type_item, from, to);
                fundTypeListView.setAdapter(simpleAdapter);
            }
        });
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
                        CommonwealBaseFragment baseFragment1 = mFragmentList.get(position);
                        baseFragment1.doSwitchCall();
                        break;
                    case 1:
                        tabCommonwealView.setChecked(false);
                        tabFundView.setChecked(true);
                        CommonwealBaseFragment baseFragment2 = mFragmentList.get(position);
                        baseFragment2.doSwitchCall();
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
            case R.id.right:
                myPopupWindow.showPopUpWindow();
                break;
        }
    }
}
