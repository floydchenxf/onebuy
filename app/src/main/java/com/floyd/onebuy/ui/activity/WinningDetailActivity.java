package com.floyd.onebuy.ui.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.WinningManager;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.product.JoinVO;
import com.floyd.onebuy.biz.vo.product.ProgressVO;
import com.floyd.onebuy.biz.vo.product.WinningDetailInfo;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.adapter.JoinRecordAdapter;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.onebuy.view.MyPopupWindow;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;

import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class WinningDetailActivity extends FragmentActivity implements View.OnClickListener {

    private static final String TAG = "WinningDetailActivity";

    private Long id;
    private int pageNo = 1;

    private DataLoadingView dataLoadingView;

    private View mHeaderView;
    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告
    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private BannerImageAdapter mBannerImageAdapter;

    private TextView titleAndStatusView;//状态和标题

    private View progressLayout;//进度view,已揭晓隐藏
    private ProgressBar progressBar;//进度
    private TextView totalView;//总需人数
    private TextView leftView;//剩余人数

    private TextView noJoinView; //无参与提示
    private TextView userNickView;//用户名称
    private TextView joinNumberView;//是否是listview

    private View detailLinkView;//详情连接
    private View lastWinnerView;//往期揭晓
    private View showShareView;//晒单分享

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private JoinRecordAdapter adapter;

    private View joinLayout;
    private View gotoJoinLayout;

    private TextView addBuyCarView;
    private MyPopupWindow buyCarPopup;

    private TextView buyCarSubView;
    private TextView buyCarAddView;
    private EditText buyCarNumberView;
    private TextView buyCarAddButton;
    private TextView buyAtOnceButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_detail);
        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.title_name).setVisibility(View.VISIBLE);
        ((TextView)findViewById(R.id.title_name)).setText("商品详情");
        id = getIntent().getLongExtra("id", 0l);
        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        initListViewHeader();
        joinLayout = findViewById(R.id.join_layout);
        addBuyCarView = (TextView)joinLayout.findViewById(R.id.join_buy_car_view);
        addBuyCarView.setOnClickListener(this);
//        addBuyCarView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                buyCarPopup.showPopUpWindow();
//            }
//        });
        buyAtOnceButton = (TextView) findViewById(R.id.buy_now_view);
        buyAtOnceButton.setOnClickListener(this);
        gotoJoinLayout = findViewById(R.id.goto_join_layout);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.join_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                loadData(false);
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.addHeaderView(mHeaderView);
        adapter = new JoinRecordAdapter(this, new ArrayList<JoinVO>());
        mListView.setAdapter(adapter);
        buyCarPopup = new MyPopupWindow(this);
        buyCarPopup.initView(R.layout.choose_number,new MyPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                buyCarAddButton = (TextView) v.findViewById(R.id.operate_button);
                buyCarAddView = (TextView) v.findViewById(R.id.add);
                buyCarSubView = (TextView) v.findViewById(R.id.sub);
                buyCarNumberView = (EditText) v.findViewById(R.id.add_number);
                buyCarNumberView.setSelection(buyCarNumberView.getText().toString().length());

                buyCarAddView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String numStr = buyCarNumberView.getText().toString();
                        int num = Integer.parseInt(TextUtils.isEmpty(numStr) ? "0" : numStr);
                        buyCarNumberView.setText(++num + "");
                        buyCarNumberView.setSelection((num + "").length());
                    }
                });

                buyCarSubView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String numStr = buyCarNumberView.getText().toString();
                        int num = Integer.parseInt(TextUtils.isEmpty(numStr) ? "0" : numStr);
                        if (num > 1) {
                            buyCarNumberView.setText(--num + "");
                        }
                        buyCarNumberView.setSelection((num+"").length());
                    }
                });

                buyCarAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //TODO 加入购物车
                        buyCarPopup.hidePopUpWindow();
                    }
                });
            }

        });
        loadData(true);
    }



    private void loadData(final boolean isFirst) {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        WinningManager.fetchWinningDetailInfoById(id).startUI(new ApiCallback<WinningDetailInfo>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(WinningDetailInfo winningDetailInfo) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                int status = winningDetailInfo.status;
                if (status == 1) {
                    joinLayout.setVisibility(View.VISIBLE);
                    gotoJoinLayout.setVisibility(View.GONE);
                    progressLayout.setVisibility(View.VISIBLE);
                    ProgressVO progressVO = winningDetailInfo.progressVO;
                    totalView.setText(Html.fromHtml("总需<font color=\"red\">"+progressVO.total+"</font>"));
                    leftView.setText(Html.fromHtml("剩余<font color=\"red\">" + progressVO.left + "</font>"));
                    progressBar.setProgress(progressVO.getPrecent());
                    joinLayout.setVisibility(View.VISIBLE);
                    gotoJoinLayout.setVisibility(View.GONE);

                } else if (status == 3) {
                    joinLayout.setVisibility(View.GONE);
                    gotoJoinLayout.setVisibility(View.VISIBLE);
                    progressLayout.setVisibility(View.GONE);
                    joinLayout.setVisibility(View.GONE);
                    gotoJoinLayout.setVisibility(View.VISIBLE);
                }

                List<AdvVO> advVOs = winningDetailInfo.advVOList;
                mBannerImageAdapter.addItems(advVOs);
                mHeaderViewIndicator.setTotal(mBannerImageAdapter.getCount());
                mHeaderViewIndicator.setIndex(0);
                mHeaderViewPager.setAdapter(mBannerImageAdapter);
                if (advVOs.size() == 1) {
                    mHeaderViewIndicator.setVisibility(View.GONE);
                } else {
                    mHeaderViewIndicator.setVisibility(View.VISIBLE);
                }
                List<JoinVO> myJoinedRecords = winningDetailInfo.myJoinedRecords;
                if (myJoinedRecords == null||myJoinedRecords.isEmpty()) {
                    noJoinView.setVisibility(View.VISIBLE);
                    noJoinView.setText(R.string.no_join_desc);
                    userNickView.setVisibility(View.GONE);
                    joinNumberView.setVisibility(View.GONE);
                } else {
                    noJoinView.setVisibility(View.GONE);
                    userNickView.setVisibility(View.VISIBLE);
                    joinNumberView.setVisibility(View.VISIBLE);
                    joinNumberView.setText(myJoinedRecords.get(0).joinNumber);
                }


                adapter.addAll(winningDetailInfo.allJoinedRecords, true);
            }

            @Override
            public void onProgress(int progress) {

            }
        });


    }

    private void initListViewHeader() {
        mHeaderView = View.inflate(this, R.layout.detail_head, null);
        mViewPagerContainer = mHeaderView.findViewById(R.id.pager_layout);
        mHeaderViewPager = (LoopViewPager) mHeaderView.findViewById(R.id.loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) mHeaderView.findViewById(R.id.indicator);
        mBannerImageAdapter = new BannerImageAdapter(this.getSupportFragmentManager(), null, null);
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

        progressLayout = mHeaderView.findViewById(R.id.progress_layout);
        progressBar = (ProgressBar) progressLayout.findViewById(R.id.progress_present_view);
        totalView = (TextView) progressLayout.findViewById(R.id.total_view);
        leftView = (TextView) progressLayout.findViewById(R.id.left_view);


        noJoinView = (TextView)mHeaderView.findViewById(R.id.no_join_view);
        userNickView = (TextView)mHeaderView.findViewById(R.id.user_nick_view);
        joinNumberView = (TextView) mHeaderView.findViewById(R.id.join_number_view);

        detailLinkView = mHeaderView.findViewById(R.id.detail_view);
        detailLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //详情
            }
        });

        lastWinnerView = mHeaderView.findViewById(R.id.last_view);
        lastWinnerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //往期
            }
        });

        showShareView = mHeaderView.findViewById(R.id.show_share_view);
        showShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //晒单
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.act_ls_fail_layout:
                pageNo = 1;
                loadData(true);
                break;
            case R.id.join_buy_car_view:
                buyCarPopup.showPopUpWindow();
                break;
            case R.id.buy_now_view:
                EventBus.getDefault().post(new TabSwitchEvent(R.id.tab_buy_car));
                this.finish();
                break;
        }
    }
}
