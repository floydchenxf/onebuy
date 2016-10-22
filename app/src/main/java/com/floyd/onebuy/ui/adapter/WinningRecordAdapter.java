package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.manager.ServerTimeManager;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.json.OwnerExtVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.biz.vo.product.OwnerVO;
import com.floyd.onebuy.ui.R;

import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by floyd on 16-4-14.
 */
public class WinningRecordAdapter extends BaseDataAdapter<WinningInfo> {

    private static final int TIME_EVENT = 1;

    private ImageLoader mImageLoader;

    private boolean isSelf = true; //是否是自己的夺宝记录,用于区分
    private Set<Long> requestSet = new ConcurrentSkipListSet<>();
    private Map<Long, Integer> callTimes = new ConcurrentHashMap<Long, Integer>();

    private ViewJoinNumberClickListener viewJoinNumberClickListener;

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

                    WinningInfo itemVO = (WinningInfo) timeView.getTag(R.id.LEFT_TIME_ID);
                    if (itemVO == null || itemVO.id == 0l || id != itemVO.id) {
                        return;
                    }

                    if (itemVO.status != WinningInfo.STATUS_LOTTERY) {
                        return;
                    }

                    long left = itemVO.lotteryTime - ServerTimeManager.getServerTime();
                    if (left <= 0) {
                        timeView.setText("正在计算...");
                        getWinnerInfo(itemVO);
                        return;
                    } else {
                        String dateLeft = DateUtil.getDateBefore(itemVO.lotteryTime, ServerTimeManager.getServerTime());
                        timeView.setText(dateLeft);
                    }

                    Message newMsg = new Message();
                    newMsg.what = TIME_EVENT;
                    newMsg.obj = o;
                    mHandler.sendMessageDelayed(newMsg, 300);
                    break;
            }
        }
    };

    private synchronized void getWinnerInfo(final WinningInfo itemVO) {
        long lssueId = itemVO.lssueId;
        if (!requestSet.contains(lssueId)) {
            Integer callNum = callTimes.get(lssueId);
            if (callNum == null) {
                callNum = 0;
                callTimes.put(lssueId, callNum);
            }

            if (callNum > 1) {
                return;
            }
            Log.i("ProductLssueAdapter", "-----------request winner for" + itemVO.lssueId);
            requestSet.add(lssueId);
            ProductManager.getWinnerInfo(lssueId).startUI(new ApiCallback<OwnerExtVO>() {
                @Override
                public void onError(int code, String errorInfo) {
                    long tmpId = itemVO.lssueId;
                    requestSet.remove(tmpId);
                    Integer num = callTimes.get(tmpId);
                    num++;
                    callTimes.put(tmpId, num);
                }

                @Override
                public void onSuccess(OwnerExtVO ownerExtVO) {
                    for (WinningInfo info : records) {
                        if (info.lssueId == ownerExtVO.lssueId) {
                            info.ownerVO = ownerExtVO;
                            info.status = ownerExtVO.status;
                            break;
                        }
                    }

                    WinningRecordAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    public void setViewJoinNumberClickListener(ViewJoinNumberClickListener clickListener) {
        this.viewJoinNumberClickListener = clickListener;
    }


    public WinningRecordAdapter(Context context, ImageLoader imageLoader, List<WinningInfo> records, boolean isSelf) {
        super(context, records);
        this.mImageLoader = imageLoader;
        this.isSelf = isSelf;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.winning_record_adapter, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.product_pic, R.id.code_title_view, R.id.total_count_view,
                R.id.my_join_num_view, R.id.view_my_codes_view, R.id.lottest_time_layout,
                R.id.owner_info_layout, R.id.owner_name_view, R.id.good_luck_num_view,
                R.id.type_icon_view,
                R.id.owner_join_num_view, R.id.owner_price_time_view, R.id.lottest_time_view, R.id.follow_buy_view, R.id.progress_layout, R.id.progress_present};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final WinningInfo winningInfo) {
        NetworkImageView productPicView = (NetworkImageView) holder.get(R.id.product_pic); //产品图片
        TextView codeTileView = (TextView) holder.get(R.id.code_title_view); //期数标题
        TextView totalCountView = (TextView) holder.get(R.id.total_count_view); //总需次数开奖
        TextView myJoinNumView = (TextView) holder.get(R.id.my_join_num_view);//我的参与次数
        TextView viewMyCodesView = (TextView) holder.get(R.id.view_my_codes_view);//查看我的号码
        View lottestLayout = holder.get(R.id.lottest_time_layout);//开奖layout
        TextView lottestTimeView = (TextView) holder.get(R.id.lottest_time_view);//开奖倒计时
        View ownerInfoLayout = holder.get(R.id.owner_info_layout);
        TextView ownerNameView = (TextView) holder.get(R.id.owner_name_view); //获奖者名称
        TextView ownerGoodLuckNumView = (TextView) holder.get(R.id.good_luck_num_view);//幸运号码
        TextView ownerJoinNumView = (TextView) holder.get(R.id.owner_join_num_view);//获奖者参与次数
        TextView ownerPriceTimeView = (TextView) holder.get(R.id.owner_price_time_view);//揭晓时间
        TextView followBuyView = (TextView) holder.get(R.id.follow_buy_view);
        ProgressBar progressBar = (ProgressBar) holder.get(R.id.progress_present);
        ImageView typeIconView = (ImageView) holder.get(R.id.type_icon_view);
        View progressLayout = holder.get(R.id.progress_layout);

        lottestLayout.setVisibility(View.GONE);
        ownerInfoLayout.setVisibility(View.GONE);
        typeIconView.setVisibility(View.GONE);

        productPicView.setDefaultImageResId(R.drawable.tupian);
        productPicView.setImageUrl(winningInfo.productUrl, mImageLoader);
        StringBuilder codeTitleSB = new StringBuilder();
        codeTitleSB.append("第").append(winningInfo.code).append("期：").append(winningInfo.title);
        codeTileView.setText(codeTitleSB.toString());
        totalCountView.setText("共需" + winningInfo.totalCount + "人次");
        myJoinNumView.setText(Html.fromHtml("<font color=\"red\">" + winningInfo.myPrizeCodes.size() + "人次</font>"));
        if (isSelf) {
            viewMyCodesView.setText("看我的号码");
        } else {
            viewMyCodesView.setText("看TA的号码");
        }
        viewMyCodesView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (viewJoinNumberClickListener != null) {
                    viewJoinNumberClickListener.onClick(winningInfo);
                }
            }
        });

        int status = winningInfo.status;

        int productType = winningInfo.productType;
        if (productType == 1) {
            typeIconView.setVisibility(View.VISIBLE);
            typeIconView.setImageResource(R.drawable.ten_icon);
        } else if (productType == 2) {
            typeIconView.setVisibility(View.VISIBLE);
            typeIconView.setImageResource(R.drawable.hun_icon);
        }

        if (status == 1) {
            long left = winningInfo.lotteryTime - ServerTimeManager.getServerTime();
            progressLayout.setVisibility(View.GONE);
            progressBar.setProgress(100);
            followBuyView.setVisibility(View.GONE);
            if (left <= 0) {
                lottestTimeView.setText("正在计算...");
                getWinnerInfo(winningInfo);
            } else {
                String dateleft = DateUtil.getDateBefore(winningInfo.lotteryTime, ServerTimeManager.getServerTime());
                lottestTimeView.setText(dateleft);
                lottestTimeView.setTag(R.id.LEFT_TIME_ID, winningInfo);

                Message msg = new Message();
                msg.what = TIME_EVENT;
                MsgObj msgObj = new MsgObj();
                msgObj.id = winningInfo.id;
                msgObj.timeView = new SoftReference<TextView>(lottestTimeView);
                msg.obj = msgObj;
                mHandler.sendMessage(msg);
            }
            lottestLayout.setVisibility(View.VISIBLE);
            //开奖中
        } else if (status == 2) {
            progressLayout.setVisibility(View.GONE);
            progressBar.setProgress(100);
            followBuyView.setVisibility(View.GONE);
            //已经揭晓
            OwnerVO ownerVO = winningInfo.ownerVO;
            if (ownerVO != null) {
                ownerNameView.setText(ownerVO.userName);
                ownerJoinNumView.setText(Html.fromHtml("<font color=\"red\">" + ownerVO.joinNumber + "人次</font>"));
                ownerGoodLuckNumView.setText(Html.fromHtml("<font color=\"red\">" + ownerVO.winNumber + "</font>"));
                String dateString = DateUtil.getDateTime(ownerVO.winTime);
                ownerPriceTimeView.setText(dateString);
                ownerInfoLayout.setVisibility(View.VISIBLE);
            }
        } else if (status == 0) {

            int precent = 0;
            if (winningInfo.totalCount > 0) {
                precent = winningInfo.joinedCount * 100 / winningInfo.totalCount;
            }
            progressBar.setProgress(precent);
            if (!isSelf) {
                followBuyView.setVisibility(View.VISIBLE);
            } else {
                followBuyView.setVisibility(View.GONE);
            }
            progressLayout.setVisibility(View.VISIBLE);
        }

    }


    public static class MsgObj {
        public SoftReference<TextView> timeView;
        public long id;
    }

    public static interface ViewJoinNumberClickListener {
        void onClick(WinningInfo winningInfo);
    }
}
