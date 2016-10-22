package com.floyd.onebuy.ui.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.BackgroundColorSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.JobFactory;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.manager.ServerTimeManager;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.biz.vo.json.OwnerExtVO;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.biz.vo.product.JoinVO;
import com.floyd.onebuy.biz.vo.product.OwnerVO;
import com.floyd.onebuy.biz.vo.product.ProgressVO;
import com.floyd.onebuy.biz.vo.product.WinningDetailInfo;
import com.floyd.onebuy.event.LoginEvent;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.MainActivity;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.BannerImageAdapter;
import com.floyd.onebuy.ui.adapter.JoinRecordAdapter;
import com.floyd.onebuy.ui.adapter.JoinedNumAdapter;
import com.floyd.onebuy.ui.buycar.BuycarOperator;
import com.floyd.onebuy.ui.buycar.FridayBuycarOperator;
import com.floyd.onebuy.ui.buycar.FundBuycarOperator;
import com.floyd.onebuy.ui.buycar.NormalProductBuycarOperator;
import com.floyd.onebuy.ui.fragment.BannerFragment;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;
import com.floyd.onebuy.ui.pageindicator.CircleLoopPageIndicator;
import com.floyd.onebuy.ui.share.ShareConstants;
import com.floyd.onebuy.ui.share.ShareManager;
import com.floyd.onebuy.utils.WXUtil;
import com.floyd.onebuy.view.LeftDownPopupWindow;
import com.floyd.onebuy.view.LoopViewPager;
import com.floyd.pullrefresh.widget.PullToRefreshBase;
import com.floyd.pullrefresh.widget.PullToRefreshListView;
import com.umeng.socialize.media.UMImage;

import java.io.File;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;

public class WinningDetailActivity extends FragmentActivity implements View.OnClickListener {

    public static final int DETAIL_TYPE_PRODUCT = 0;
    public static final int DETAIL_TYPE_FUND = 1;
    public static final int DETAIL_TYPE_FRI = 2;
    public static final int TIME_EVENT = 1;
    public static final String DETAIL_TYPE = "DETAIL_TYPE";

    private static final String TAG = "WinningDetailActivity";
    public static final String LASTEST = "lastest";
    public static final String PRODUCT_ID = "PRODUCT_ID";
    public static final String LSSUE_ID = "id";
    private static final int PAGE_SIZE = 10;


    private Long id;
    private Long productId;
    private boolean isLatest;
    private int pageNo = 1;
    private int detailType; //类型

    private WinningDetailInfo winningDetailInfo;

    private DataLoadingView dataLoadingView;

    private View mHeaderView;
    private View mViewPagerContainer;//整个广告
    private LoopViewPager mHeaderViewPager;//广告
    private CircleLoopPageIndicator mHeaderViewIndicator;//广告条索引
    private BannerImageAdapter mBannerImageAdapter;

    private TextView titleAndStatusView;//状态和标题

    private View progressLayout;//进度view,已揭晓隐藏
    private View priceTimeLayout; //开奖倒计时layout
    private View ownerLayout; //中奖者layout

    private View joinNumberLayout;
    private ProgressBar progressBar;//进度
    private TextView totalView;//总需人数

    private TextView leftView;//剩余人数
    private TextView noJoinView; //无参与提示

    private View detailLinkView;//详情连接
    private View lastWinnerView;//往期揭晓
    private View showShareView;//晒单分享

    private View allRecordLayout;

    private PullToRefreshListView mPullToRefreshListView;
    private ListView mListView;
    private JoinRecordAdapter adapter;

    private View joinLayout;
    private View gotoJoinLayout;

    private TextView addBuyCarView;
    private LeftDownPopupWindow buyCarPopup;

    private LeftDownPopupWindow joinedPopupWindow;

    private TextView buyCarSubView;
    private TextView buyCarAddView;
    private EditText buyCarNumberView;
    private TextView buyCarAddButton;
    private TextView buyAtOnceButton;
    private TextView gotoDetailView;

    private TextView popProductCodeView;
    private TextView popProductTitleView;
    private TextView popJoinedCountView;
    private ListView popJoinNumListView;
    private ImageView redDotView;
    private JoinedNumAdapter joinedNumAdapter;

    private ImageLoader mImageLoader;

    private ShareManager shareManager;

    private int callTime = 0;
    private boolean isFirst;

    private ImageView buyCarImageView;

    private ImageView typeIconView;

    private BuycarOperator buycarOperator;

    private String sharePicUrl;
    private String shareContent;
    private String shareUrl;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int what = msg.what;
            switch (what) {
                case TIME_EVENT:

                    MsgObj o = (MsgObj) msg.obj;
                    long id = o.id;

                    SoftReference<TextView> view = o.timeView;
                    if (view == null && view.get() == null) {
                        return;
                    }

                    TextView timeView = view.get();
                    if (timeView == null) {
                        return;
                    }


                    long priceTime = (Long) timeView.getTag(R.id.LEFT_TIME_ID);
                    if (winningDetailInfo.status != WinningInfo.STATUS_LOTTERY) {
                        return;
                    }

                    long left = priceTime - ServerTimeManager.getServerTime();

                    if (left <= 0) {
                        timeView.setText("正在计算...");
                        getWinnerInfo();
                        return;
                    } else {
                        String dateLeft = DateUtil.getDateBefore(priceTime, ServerTimeManager.getServerTime());
                        timeView.setText(dateLeft);
                    }

                    Message newMsg = new Message();
                    newMsg.what = TIME_EVENT;
                    newMsg.obj = o;
                    mHandler.sendMessageDelayed(newMsg, 252);
                    break;
            }
        }
    };

    private ApiCallback<WinningDetailInfo> apiCallback = new ApiCallback<WinningDetailInfo>() {
        @Override
        public void onError(int code, String errorInfo) {
            if (isFirst) {
                dataLoadingView.loadFail();
            }
            Toast.makeText(WinningDetailActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            WinningDetailActivity.this.finish();
        }

        @Override
        public void onSuccess(final WinningDetailInfo winningDetailInfo) {
            if (isFirst) {
                dataLoadingView.loadSuccess();
            }

            WinningDetailActivity.this.winningDetailInfo = winningDetailInfo;
            id = winningDetailInfo.id;
            buycarOperator.setId(id);
            StringBuilder titleAndStatusSb = new StringBuilder();
            int status = winningDetailInfo.status;

            if (winningDetailInfo.productType == 1) {
                typeIconView.setVisibility(View.VISIBLE);
                typeIconView.setImageResource(R.drawable.ten_icon);
            } else if (winningDetailInfo.productType == 2) {
                typeIconView.setVisibility(View.VISIBLE);
                typeIconView.setImageResource(R.drawable.hun_icon);
            }
            if (status == WinningInfo.STATUS_CHOOSE) {
                joinLayout.setVisibility(View.VISIBLE);
                gotoJoinLayout.setVisibility(View.GONE);
                joinLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.VISIBLE);
                priceTimeLayout.setVisibility(View.GONE);
                ownerLayout.setVisibility(View.GONE);
                ProgressVO progressVO = winningDetailInfo.progressVO;
                totalView.setText(Html.fromHtml("总需<font color=\"red\">" + progressVO.TotalCount + "</font>人次"));
                leftView.setText(Html.fromHtml("剩余<font color=\"red\">" + (progressVO.TotalCount - progressVO.JonidedCount) + "</font>人次"));
                progressBar.setProgress(progressVO.getPrecent());
                titleAndStatusSb.append("进行中    ");
            } else if (status == WinningInfo.STATUS_LOTTERY) {
                titleAndStatusSb.append("开奖中    ");
                progressLayout.setVisibility(View.GONE);
                priceTimeLayout.setVisibility(View.VISIBLE);

                TextView priceTimeView = (TextView) priceTimeLayout.findViewById(R.id.price_time_view);
                long priceTime = winningDetailInfo.priceTime;
                priceTimeView.setTag(R.id.LEFT_TIME_ID, priceTime);

                Message msg = new Message();
                msg.what = TIME_EVENT;
                MsgObj msgObj = new MsgObj();
                msgObj.id = id;
                msgObj.timeView = new SoftReference<TextView>(priceTimeView);
                msg.obj = msgObj;
                mHandler.sendMessage(msg);
                ownerLayout.setVisibility(View.GONE);

            } else if (status == WinningInfo.STATUS_LOTTERYED) {
                joinLayout.setVisibility(View.GONE);
                gotoJoinLayout.setVisibility(View.VISIBLE);
                progressLayout.setVisibility(View.GONE);
                priceTimeLayout.setVisibility(View.GONE);
                ownerLayout.setVisibility(View.VISIBLE);

                final OwnerVO vo = winningDetailInfo.ownerVO;
                if (vo == null) {
                    ownerLayout.setVisibility(View.GONE);
                } else {
                    NetworkImageView ownerHeadView = (NetworkImageView) ownerLayout.findViewById(R.id.owner_head_view);
                    TextView ownerNameView = (TextView) ownerLayout.findViewById(R.id.owner_name_view);
                    TextView ownerClientIdView = (TextView) ownerLayout.findViewById(R.id.owner_client_id_view);
                    TextView ownerJoinNumView = (TextView) ownerLayout.findViewById(R.id.owner_join_num_view);
                    TextView ownerPriceTimeView = (TextView) ownerLayout.findViewById(R.id.owner_price_time_view);
                    TextView winnerNumberView = (TextView) ownerLayout.findViewById(R.id.luck_number_view);
                    TextView computeView = (TextView) ownerLayout.findViewById(R.id.compute_desc_view2);
                    ownerHeadView.setDefaultImageResId(R.drawable.tupian);
                    ownerHeadView.setImageUrl(vo.getHeadImage(), mImageLoader);
                    ownerNameView.setText(vo.userName);
                    ownerJoinNumView.setText((winningDetailInfo.myRecords == null ? 0 : winningDetailInfo.myRecords.size()) + "人次");
                    String k = DateUtil.getDateTime(vo.winTime);
                    ownerPriceTimeView.setText(k);
                    ownerClientIdView.setText(vo.userId + "");
                    winnerNumberView.setText(vo.winNumber);

                    ownerHeadView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent it = new Intent(WinningDetailActivity.this, PersionProfileActivity.class);
                            it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            it.putExtra(PersionProfileActivity.CURRENT_USER_ID, vo.userId);
                            startActivity(it);
                        }
                    });

                    computeView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent it = new Intent(WinningDetailActivity.this, WinnerCalActivity.class);
                            it.putExtra(WinnerCalActivity.LSSUE_ID, id);
                            startActivity(it);
                        }
                    });
                }

                joinLayout.setVisibility(View.GONE);
                titleAndStatusSb.append("已揭晓    ");
            }

            titleAndStatusSb.append("(第").append(winningDetailInfo.code).append("期)");

            titleAndStatusSb.append(winningDetailInfo.productTitle);

            SpannableString message = new SpannableString(titleAndStatusSb.toString());
            message.setSpan(new BackgroundColorSpan(Color.parseColor("#ff424c")), 0, 3, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            titleAndStatusView.setText(message);

            List<AdvVO> advVOs = winningDetailInfo.advVOList;
            if (advVOs != null && !advVOs.isEmpty()) {
                sharePicUrl = advVOs.get(0).imgUrl;
            }
            shareContent = winningDetailInfo.productTitle;
            mBannerImageAdapter.addItems(advVOs);
            mHeaderViewIndicator.setTotal(mBannerImageAdapter.getCount());
            mHeaderViewIndicator.setIndex(0);
            if (advVOs.size() == 1) {
                mHeaderViewIndicator.setVisibility(View.GONE);
            } else {
                mHeaderViewIndicator.setVisibility(View.VISIBLE);
            }

            UserVO userVO = LoginManager.getLoginInfo(WinningDetailActivity.this);
            if (userVO == null) {
                noJoinView.setVisibility(View.VISIBLE);
                noJoinView.setText(R.string.no_login);
                joinNumberLayout.setVisibility(View.GONE);
                noJoinView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent it = new Intent(WinningDetailActivity.this, LoginActivity.class);
                        startActivity(it);
                    }
                });
            } else {
                if (winningDetailInfo.myRecords != null && !winningDetailInfo.myRecords.isEmpty()) {

                    List<String> joinedNums = winningDetailInfo.myRecords;
                    noJoinView.setVisibility(View.GONE);
                    joinNumberLayout.setVisibility(View.VISIBLE);
                    TextView buyNoDescView = (TextView) joinNumberLayout.findViewById(R.id.buy_no_desc);
                    buyNoDescView.setText(Html.fromHtml("您参与了：<font color=\"blue\">" + joinedNums.size() + "</font>人次"));
                    LinearLayout joinedNumLayout = (LinearLayout) joinNumberLayout.findViewById(R.id.joined_num_layout);
                    joinedNumLayout.removeAllViews();
                    joinedNumLayout.setVisibility(View.VISIBLE);

                    List<String> tmpList = new ArrayList<String>();
                    boolean hasMore = false;
                    if (joinedNums.size() >= 8) {
                        for (int i = 0; i < 7; i++) {
                            tmpList.add(joinedNums.get(i));
                        }
                        tmpList.add("查看更多");
                        hasMore = true;
                    } else {
                        tmpList.addAll(joinedNums);
                        hasMore = false;
                    }

                    int lines = tmpList.size() % 4 == 0 ? tmpList.size() / 4 : tmpList.size() / 4 + 1;
                    for (int i = 0; i < lines; i++) {
                        LinearLayout layout = (LinearLayout) View.inflate(WinningDetailActivity.this, R.layout.joined_num_item, null);
                        joinedNumLayout.addView(layout);
                        TextView text1 = (TextView) layout.findViewById(R.id.join_number_1);
                        TextView text2 = (TextView) layout.findViewById(R.id.join_number_2);
                        TextView text3 = (TextView) layout.findViewById(R.id.join_number_3);
                        TextView text4 = (TextView) layout.findViewById(R.id.join_number_4);
                        text4.setVisibility(View.VISIBLE);
                        text4.setOnClickListener(null);
                        int k = i * 4;
                        if (k < tmpList.size()) {
                            text1.setText(tmpList.get(k));
                            text1.setVisibility(View.VISIBLE);
                        } else {
                            text1.setVisibility(View.GONE);
                        }

                        if (k + 1 < tmpList.size()) {
                            text2.setText(tmpList.get(k + 1));
                            text2.setVisibility(View.VISIBLE);
                        } else {
                            text2.setVisibility(View.GONE);
                        }

                        if (k + 2 < tmpList.size()) {
                            text3.setText(tmpList.get(k + 2));
                            text3.setVisibility(View.VISIBLE);
                        } else {
                            text3.setVisibility(View.GONE);
                        }

                        if (k + 3 < tmpList.size()) {
                            text4.setText(tmpList.get(k + 3));
                            text4.setVisibility(View.VISIBLE);
                        } else {
                            text4.setVisibility(View.GONE);
                        }

                        if (i == 1 && hasMore) {
                            text4.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popProductCodeView.setText("第" + winningDetailInfo.code + "期");
                                    popProductTitleView.setText(winningDetailInfo.productTitle);
                                    popJoinedCountView.setText(winningDetailInfo.myRecords.size() + "人次");
                                    List<String> list = winningDetailInfo.myRecords;
                                    joinedNumAdapter.addAll(list, true);
                                    joinedPopupWindow.showPopUpWindow();
                                }
                            });
                        } else {
                            text4.setOnClickListener(null);
                        }
                    }


                    noJoinView.setOnClickListener(null);
                } else {
                    noJoinView.setVisibility(View.VISIBLE);
                    noJoinView.setText(R.string.no_join_desc);
                    joinNumberLayout.setVisibility(View.GONE);
                    noJoinView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            buyNow();
                        }
                    });
                }
            }

            adapter.addAll(winningDetailInfo.allJoinedRecords, true);

            if (adapter.getFeeRecords() == null || adapter.getFeeRecords().isEmpty()) {
                allRecordLayout.setVisibility(View.GONE);
            }
        }

        @Override
        public void onProgress(int progress) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winning_detail);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        shareManager = ShareManager.getInstance(this);
        EventBus.getDefault().register(this);
        findViewById(R.id.title_back).setOnClickListener(this);
        findViewById(R.id.title_name).setVisibility(View.VISIBLE);
        findViewById(R.id.share_view).setOnClickListener(this);
        findViewById(R.id.index_view).setOnClickListener(this);
        ((TextView) findViewById(R.id.title_name)).setText("商品详情");
        id = getIntent().getLongExtra(LSSUE_ID, 0l);
        isLatest = getIntent().getBooleanExtra(LASTEST, true);
        detailType = getIntent().getIntExtra(DETAIL_TYPE, DETAIL_TYPE_PRODUCT);
        productId = getIntent().getLongExtra(PRODUCT_ID, 0l);

        buycarOperator = null;
        if (detailType == DETAIL_TYPE_PRODUCT) {
            buycarOperator = new NormalProductBuycarOperator(this, id, productId);
        } else if (detailType == DETAIL_TYPE_FRI) {
            buycarOperator = new FridayBuycarOperator(this, id, productId);
        } else {
            buycarOperator = new FundBuycarOperator(this, id, productId);
        }

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), this);

        initListViewHeader();
        joinLayout = findViewById(R.id.join_layout);
        addBuyCarView = (TextView) joinLayout.findViewById(R.id.join_buy_car_view);
        addBuyCarView.setOnClickListener(this);
        buyAtOnceButton = (TextView) findViewById(R.id.buy_now_view);
        buyAtOnceButton.setOnClickListener(this);
        gotoJoinLayout = findViewById(R.id.goto_join_layout);
        gotoDetailView = (TextView) findViewById(R.id.goto_detail_view);
        gotoDetailView.setOnClickListener(this);

        buyCarImageView = (ImageView) findViewById(R.id.view_buy_car);
        buyCarImageView.setOnClickListener(this);

        mPullToRefreshListView = (PullToRefreshListView) findViewById(R.id.join_list);
        mPullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_UP_TO_REFRESH);
        mPullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2() {
            @Override
            public void onPullDownToRefresh() {

            }

            @Override
            public void onPullUpToRefresh() {
                pageNo++;
                loadJoinedRecords();
                mPullToRefreshListView.onRefreshComplete(false, true);

            }
        });
        mListView = mPullToRefreshListView.getRefreshableView();
        mListView.addHeaderView(mHeaderView);
        adapter = new JoinRecordAdapter(this, new ArrayList<JoinVO>());
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                JoinVO vo = adapter.getFeeRecords().get(pos - 2);
                Intent it = new Intent(WinningDetailActivity.this, PersionProfileActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.putExtra(PersionProfileActivity.CURRENT_USER_ID, Long.parseLong(vo.ClientID));
                startActivity(it);
            }
        });
        buyCarPopup = new LeftDownPopupWindow(this);
        buyCarPopup.initView(R.layout.choose_number, new LeftDownPopupWindow.ViewInit() {
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
                        buyCarNumberView.setSelection((num + "").length());
                    }
                });

                buyCarAddButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        buyCarPopup.hidePopUpWindow();
                    }
                });
            }

        });

        joinedPopupWindow = new LeftDownPopupWindow(this);
        joinedPopupWindow.initView(R.layout.pop_join_num, new LeftDownPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                popProductCodeView = (TextView) v.findViewById(R.id.pop_product_code_view);
                popProductTitleView = (TextView) v.findViewById(R.id.pop_product_title_view);
                popJoinedCountView = (TextView) v.findViewById(R.id.pop_joined_count_view);
                popJoinNumListView = (ListView) v.findViewById(R.id.pop_joined_num_listview);
                joinedNumAdapter = new JoinedNumAdapter(WinningDetailActivity.this, new ArrayList<String>());
                popJoinNumListView.setAdapter(joinedNumAdapter);
            }
        });

        redDotView = (ImageView) findViewById(R.id.red_dot_view);
        isFirst = true;
        loadData();
    }


    private void loadJoinedRecords() {
        ProductManager.getProductLssueJoinedList(PAGE_SIZE, pageNo, id).startUI(new ApiCallback<List<JoinVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(WinningDetailActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<JoinVO> joinVOs) {
                adapter.addAll(joinVOs, false);
                if (adapter.getFeeRecords() == null || adapter.getFeeRecords().isEmpty()) {
                    allRecordLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }


    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        long userId = 0;
        UserVO userVO = LoginManager.getLoginInfo(this);
        if (userVO != null) {
            userId = userVO.ID;
        }

        Long lssueId = null;
        if (id != null && id != 0l) {
            lssueId = id;
        }

        shareUrl = String.format(ShareConstants.WINNING_DETAIL_SHARE_URL_FORMAT, lssueId);

        AsyncJob<WinningDetailInfo> job = null;

        if (!isLatest) {
            job = ProductManager.fetchProductLssuePageData(lssueId, userId);
        } else {
            job = ProductManager.fetchProductLssueDetail(lssueId, productId, userId);
        }

        job.startUI(apiCallback);


    }

    private void initListViewHeader() {
        mHeaderView = View.inflate(this, R.layout.detail_head, null);
        allRecordLayout = mHeaderView.findViewById(R.id.all_record_layout);
        mViewPagerContainer = mHeaderView.findViewById(R.id.detail_pager_layout);
        mHeaderViewPager = (LoopViewPager) mHeaderView.findViewById(R.id.detail_loopViewPager);
        mHeaderViewIndicator = (CircleLoopPageIndicator) mHeaderView.findViewById(R.id.detail_indicator);
        mBannerImageAdapter = new BannerImageAdapter(this.getSupportFragmentManager(), null, null, BannerFragment.SCALE_FIT_CENTER);
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

        titleAndStatusView = (TextView) mHeaderView.findViewById(R.id.title_and_status_view);
        progressLayout = mHeaderView.findViewById(R.id.progress_layout);
        priceTimeLayout = mHeaderView.findViewById(R.id.price_time_layout);
        ownerLayout = mHeaderView.findViewById(R.id.owner_info_layout);
        typeIconView = (ImageView) mHeaderView.findViewById(R.id.type_icon_view);
        typeIconView.setVisibility(View.GONE);
        progressBar = (ProgressBar) progressLayout.findViewById(R.id.progress_present_view);
        totalView = (TextView) progressLayout.findViewById(R.id.total_view);
        leftView = (TextView) progressLayout.findViewById(R.id.left_view);


        noJoinView = (TextView) mHeaderView.findViewById(R.id.no_join_view);
        joinNumberLayout = mHeaderView.findViewById(R.id.join_number_layout);

        detailLinkView = mHeaderView.findViewById(R.id.detail_view);
        detailLinkView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //详情
                Intent detailIntent = new Intent(WinningDetailActivity.this, H5Activity.class);
                H5Activity.H5Data h5Data = new H5Activity.H5Data();
                h5Data.dataType = H5Activity.H5Data.H5_DATA_TYPE_URL;
                String detailUrl = String.format(APIConstants.PRODUCT_DETAIL_URL_FORMAT, productId);
                h5Data.data = detailUrl;
                h5Data.showProcess = true;
                h5Data.showNav = true;
                h5Data.title = "商品详情";
                detailIntent.putExtra(H5Activity.H5Data.H5_DATA, h5Data);
                startActivity(detailIntent);
            }
        });

        lastWinnerView = mHeaderView.findViewById(R.id.last_view);
        lastWinnerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //往期
                Intent prizeIntent = new Intent(WinningDetailActivity.this, HistoryPrizeActivity.class);
                prizeIntent.putExtra(APIConstants.PRO_ID, winningDetailInfo.proId);
                startActivity(prizeIntent);
            }
        });

        showShareView = mHeaderView.findViewById(R.id.show_share_view);
        showShareView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent it = new Intent(WinningDetailActivity.this, ShowShareActivity.class);
                it.putExtra(ShowShareActivity.CURRENT_USER_ID, productId);
                it.putExtra(ShowShareActivity.IS_PRODUCT, true);
                startActivity(it);
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
                isFirst = true;
                loadData();
                break;
            case R.id.join_buy_car_view:
                redDotView.setVisibility(View.VISIBLE);
                buycarOperator.addBuyCar();
                break;
            case R.id.buy_now_view:
                buyNow();
                break;
            case R.id.goto_detail_view:
                Intent it = new Intent(this, WinningDetailActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.putExtra(PRODUCT_ID, productId);
                it.putExtra(LASTEST, true);
                it.putExtra(DETAIL_TYPE, detailType);
                startActivity(it);
                this.finish();
                break;
            case R.id.view_buy_car:
                buycarOperator.viewBuyCar();
                break;
            case R.id.share_view:
                if (!TextUtils.isEmpty(sharePicUrl)) {
                    final String md5Name = WXUtil.getMD5FileName(sharePicUrl);
                    File f = new File(EnvConstants.imageRootPath + File.separator + md5Name);
                    if (f.exists()) {
                        UMImage image = new UMImage(this, f);
                        shareManager.setUmImager(image);
                    }
                }

                shareManager.setShareContent(ShareConstants.WINNING_DETAIL_SHARE_TITLE, shareContent, shareUrl);
                shareManager.show(false);
                break;
            case R.id.index_view:
                Intent gotoIndex = new Intent(this, MainActivity.class);
                gotoIndex.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                gotoIndex.putExtra(MainActivity.TAB_INDEX, R.id.tab_index_page);
                startActivity(gotoIndex);
                break;
        }
    }

    private void buyNow() {
        buycarOperator.buyAtOnce();
        this.finish();
    }

    @Subscribe
    public void onEventMainThread(LoginEvent event) {
        isFirst = false;
        loadData();
    }

    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);

    }

    private synchronized void getWinnerInfo() {
        if (callTime > 3) {
            return;
        }

        ProductManager.getWinnerInfo(id).startUI(new ApiCallback<OwnerExtVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                callTime++;
            }

            @Override
            public void onSuccess(OwnerExtVO ownerExtVO) {
                callTime++;
                winningDetailInfo.ownerVO = ownerExtVO;
                winningDetailInfo.status = ownerExtVO.status;
                isFirst = false;
                JobFactory.createJob(winningDetailInfo).startUI(apiCallback);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    public static class MsgObj {
        public SoftReference<TextView> timeView;
        public long id;
    }
}
