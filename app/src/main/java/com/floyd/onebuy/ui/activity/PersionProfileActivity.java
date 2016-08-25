package com.floyd.onebuy.ui.activity;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckedTextView;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.FragmentAdapter;
import com.floyd.onebuy.ui.adapter.ProfileFragmentAdapter;
import com.floyd.onebuy.ui.fragment.CommonwealBaseFragment;
import com.floyd.onebuy.ui.fragment.ProfilePrizeFragment;
import com.floyd.onebuy.ui.fragment.ProfileShowShareFragment;
import com.floyd.onebuy.ui.fragment.ProfileWinningFragment;

import java.util.ArrayList;
import java.util.List;

public class PersionProfileActivity extends FragmentActivity implements View.OnClickListener {

    public static final String CURRENT_PAGE_INDEX = "current_page_index";
    public static final String CURRENT_USER_ID = "current_user_id";

    private Long userId;

    private int currentIndex;
    private int currentPager;

    private List<Fragment> mFragmentList = new ArrayList<Fragment>();
    private ProfileFragmentAdapter mFragmentAdapter;
    private ViewPager profilePager;
    private CheckedTextView tabWinningView;
    private CheckedTextView tabPrizeView;
    private CheckedTextView tabShowShareView;

    private ImageLoader mImageLoader;

    private View infoLayout;
    private NetworkImageView headImageView;
    private TextView userNameView;
    private TextView userIdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_persion_profile);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("个人中心");
        titleNameView.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);

        infoLayout = findViewById(R.id.persion_info);
        headImageView = (NetworkImageView) findViewById(R.id.head);
        userNameView = (TextView) findViewById(R.id.user_name_view);
        userIdView = (TextView) findViewById(R.id.user_id_view);

        currentIndex = getIntent().getIntExtra(CURRENT_PAGE_INDEX, 0);

        profilePager = (ViewPager) findViewById(R.id.page_profile);
        tabWinningView = (CheckedTextView) findViewById(R.id.tab_winning_view);
        tabPrizeView = (CheckedTextView) findViewById(R.id.tab_prize_view);
        tabShowShareView = (CheckedTextView) findViewById(R.id.tab_show_share_view);
        tabWinningView.setOnClickListener(this);
        tabPrizeView.setOnClickListener(this);
        tabShowShareView.setOnClickListener(this);

        userId = getIntent().getLongExtra(CURRENT_USER_ID, 0l);

        initFragment();

        loadData();
    }

    private void loadData() {
        LoginManager.fetchUserInfo(userId).startUI(new ApiCallback<UserVO>() {
            @Override
            public void onError(int code, String errorInfo) {

            }

            @Override
            public void onSuccess(UserVO userVO) {
                infoLayout.setVisibility(View.VISIBLE);
                headImageView.setImageUrl(userVO.getFullPic(), mImageLoader, new BitmapProcessor() {
                    @Override
                    public Bitmap processBitmap(Bitmap bitmap) {
                        return ImageUtils.getCircleBitmap(bitmap, getResources().getDimension(R.dimen.cycle_head_image_size));
                    }
                });
                userIdView.setText(("ID:" + userId));
                userNameView.setText(userVO.getUserName());
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void initFragment() {
        mFragmentAdapter = new ProfileFragmentAdapter(this.getSupportFragmentManager(), userId);
        profilePager.setAdapter(mFragmentAdapter);

        if (currentIndex > mFragmentAdapter.getCount()) {
            currentIndex = mFragmentAdapter.getCount() - 1;
        }

        if (currentIndex < 0) {
            currentIndex = 0;
        }

        profilePager.setCurrentItem(currentIndex);
        profilePager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                currentPager = position;
                switch (position) {
                    case 0:
                        tabWinningView.setChecked(true);
                        tabPrizeView.setChecked(false);
                        tabShowShareView.setChecked(false);
                        break;
                    case 1:
                        tabWinningView.setChecked(false);
                        tabPrizeView.setChecked(true);
                        tabShowShareView.setChecked(false);
                        break;
                    case 2:
                        tabWinningView.setChecked(false);
                        tabPrizeView.setChecked(false);
                        tabShowShareView.setChecked(true);
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
            case R.id.tab_winning_view:
                profilePager.setCurrentItem(0);
                break;
            case R.id.tab_prize_view:
                profilePager.setCurrentItem(1);
                break;
            case R.id.tab_show_share_view:
                profilePager.setCurrentItem(2);
                break;

        }

    }
}
