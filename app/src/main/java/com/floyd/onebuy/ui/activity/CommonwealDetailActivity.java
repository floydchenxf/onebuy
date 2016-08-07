package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CommonwealManager;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealDetailVO;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHelperVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.fragment.BannerFragment;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.view.LoopViewPager;

import java.util.List;

public class CommonwealDetailActivity extends FragmentActivity implements View.OnClickListener {

    public static final String PRODUCT_ID = "PRODUCT_ID";

    private long pid;

    private View operatorView; //捐款按钮
    private ProgressBar progressBar;
    private TextView totalMoneyView; //总需公益金额
    private TextView raiseMoneyView; //已筹金额
    private TextView progressView;
    private TextView raiseCountView; //已经捐款人数
    private TextView raiseCountDescView; //捐款人数描述
    private TextView contentView;
    private TextView proNameView;

    private View personLayout;
    private LinearLayout personListView;
    private DataLoadingView dataLoadingView;

    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告
    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private BannerImageAdapter mBannerImageAdapter;

    private int screenWith;
    private float onedp;
    private ImageLoader mImageLoader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_commonweal_detail);
        screenWith = this.getWindowManager().getDefaultDisplay().getWidth();
        onedp = this.getResources().getDimension(R.dimen.one_dp);
        pid = getIntent().getLongExtra(PRODUCT_ID, 0);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("项目详情");
        titleNameView.setVisibility(View.VISIBLE);

        findViewById(R.id.title_back).setOnClickListener(this);

        mViewPagerContainer = findViewById(R.id.detail_pager_layout);
        mHeaderViewPager = (LoopViewPager) findViewById(R.id.detail_loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) findViewById(R.id.detail_indicator);
        mBannerImageAdapter = new BannerImageAdapter(this.getSupportFragmentManager(), null, null, BannerFragment.SCALE_CENTER_CROP);
        mHeaderViewPager.setAdapter(mBannerImageAdapter);
        mHeaderViewPager.setOnPageChangeListener(new LoopViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int index) {
                mHeaderViewIndicator.setIndex(index);
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }
        });

        operatorView = findViewById(R.id.operate_layout);
        progressBar = (ProgressBar) findViewById(R.id.progress_present);
        totalMoneyView = (TextView) findViewById(R.id.total_money_view);
        raiseMoneyView = (TextView) findViewById(R.id.raise_money_view);
        progressView = (TextView) findViewById(R.id.progress_in_view);
        raiseCountView = (TextView) findViewById(R.id.raise_count_view);
        raiseCountDescView = (TextView) findViewById(R.id.total_count_textview);
        contentView = (TextView) findViewById(R.id.content_view);

        personListView = (LinearLayout) findViewById(R.id.person_list_view);
        personLayout = findViewById(R.id.person_layout);
        proNameView = (TextView) findViewById(R.id.proName_view);
        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        CommonwealManager.fetchCommonwealDetail(pid).startUI(new ApiCallback<CommonwealDetailVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(CommonwealDetailVO vo) {
                List<AdvVO> advVOs = vo.advVOList;
                if (advVOs == null||advVOs.isEmpty()) {
                    mViewPagerContainer.setVisibility(View.GONE);
                } else {
                    mViewPagerContainer.setVisibility(View.VISIBLE);
                    mBannerImageAdapter.addItems(advVOs);
                    mHeaderViewIndicator.setTotal(mBannerImageAdapter.getCount());
                    mHeaderViewIndicator.setIndex(0);
                    if (advVOs.size() == 1) {
                        mHeaderViewIndicator.setVisibility(View.GONE);
                    } else {
                        mHeaderViewIndicator.setVisibility(View.VISIBLE);
                    }

                    proNameView.setText(vo.ProName);
                }

                progressBar.setProgress(vo.percentNum);

                dataLoadingView.loadSuccess();
                totalMoneyView.setText(vo.TotalMoney);
                raiseMoneyView.setText(vo.RaiseMoney);
                progressView.setText(vo.Percent);
                raiseCountView.setText(vo.RaiseCount + "");
                contentView.setText(vo.Content);
                raiseCountDescView.setText("感谢" + vo.RaiseCount + "位爱心朋友");

                List<CommonwealHelperVO> helpers = vo.PersonList;
                if (helpers == null || helpers.isEmpty()) {
                    raiseCountDescView.setOnClickListener(null);
                    personLayout.setVisibility(View.GONE);
                } else {
                    raiseCountDescView.setOnClickListener(CommonwealDetailActivity.this);
                    final float eachWidth = (screenWith - 42 * onedp) / 6;
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams((int) eachWidth, (int) eachWidth);
                    lp.setMargins((int) (2 * onedp), 0, (int) (2 * onedp), 0);
                    int idx = 0;
                    for (CommonwealHelperVO helperVO : helpers) {
                        NetworkImageView imageView = new NetworkImageView(CommonwealDetailActivity.this);
                        imageView.setLayoutParams(lp);
                        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
                        imageView.setDefaultImageResId(R.drawable.tupian);
                        imageView.setImageUrl(helperVO.getClientPic(), mImageLoader, new BitmapProcessor() {
                            @Override
                            public Bitmap processBitmap(Bitmap bitmap) {
                                return ImageUtils.getCircleBitmap(bitmap, eachWidth);
                            }
                        });
                        personListView.addView(imageView);
                        idx++;
                    }
                }


            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.act_ls_fail_layout:
                loadData();
                break;
            case R.id.title_back:
                this.finish();
                break;
            case R.id.total_count_textview:
                Intent helperIntent = new Intent(this, CommonwealHelperActivity.class);
                helperIntent.putExtra(CommonwealHelperActivity.PRODUCT_ID, pid);
                startActivity(helperIntent);
                break;
        }

    }
}