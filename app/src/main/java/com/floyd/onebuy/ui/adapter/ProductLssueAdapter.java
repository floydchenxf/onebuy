package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.manager.CarManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.manager.ProductManager;
import com.floyd.onebuy.biz.manager.ServerTimeManager;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.json.OwnerExtVO;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.WinningDetailActivity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by floyd on 16-4-24.
 */
public class ProductLssueAdapter extends BaseAdapter {

    private Context mContext;
    private List<WinningInfo> records = new ArrayList<WinningInfo>();
    private ImageLoader mImageLoader;

    private static final int TIME_EVENT = 1;

    private Set<Long> requestSet = new ConcurrentSkipListSet<>();
    private Map<Long, Integer> callTimes = new ConcurrentHashMap<Long, Integer>();

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
                    mHandler.sendMessageDelayed(newMsg, 252);
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

                    ProductLssueAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

    public ProductLssueAdapter(Context context, List<WinningInfo> records, ImageLoader imageLoader) {
        this.mContext = context;
        this.records = records;
        this.mImageLoader = imageLoader;
    }

    public void addAll(List<WinningInfo> products, boolean needClear) {
        if (needClear) {
            records.clear();
        }
        records.addAll(products);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.records.size() % 2 == 0 ? this.records.size() / 2 : this.records.size() / 2 + 1;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.product_lssue_item, null);
            viewHolder = new ViewHolder();
            viewHolder.layout1 = convertView.findViewById(R.id.product_item_1);
            viewHolder.layout2 = convertView.findViewById(R.id.product_item_2);
            viewHolder.chooseLayout1 = convertView.findViewById(R.id.choose_layout_1);
            viewHolder.chooseLayout2 = convertView.findViewById(R.id.choose_layout_2);
            viewHolder.addBuyCarView1 = (ImageView) convertView.findViewById(R.id.add_buy_list_view_1);
            viewHolder.addBuyCarView2 = (ImageView) convertView.findViewById(R.id.add_buy_list_view_2);
            viewHolder.progressPrecentDescView1 = (TextView) convertView.findViewById(R.id.progress_precent_desc_1);
            viewHolder.progressPrecentDescView2 = (TextView) convertView.findViewById(R.id.progress_precent_desc_2);
            viewHolder.productImageView1 = (NetworkImageView) convertView.findViewById(R.id.product_pic_1);
            viewHolder.productImageView2 = (NetworkImageView) convertView.findViewById(R.id.product_pic_2);
            viewHolder.productTitleView1 = (TextView) convertView.findViewById(R.id.product_title_view_1);
            viewHolder.productTitleView2 = (TextView) convertView.findViewById(R.id.product_title_view_2);
            viewHolder.productCodeLayout1 = convertView.findViewById(R.id.product_code_layout_1);
            viewHolder.productCodeLayout2 = convertView.findViewById(R.id.product_code_layout_2);
            viewHolder.productCodeView1 = (TextView) convertView.findViewById(R.id.product_code_view_1);
            viewHolder.productCodeView2 = (TextView) convertView.findViewById(R.id.product_code_view_2);
            viewHolder.progressPrecentView1 = (ProgressBar) convertView.findViewById(R.id.progress_present_1);
            viewHolder.progressPrecentView2 = (ProgressBar) convertView.findViewById(R.id.progress_present_2);

            viewHolder.lottestLayout1 = convertView.findViewById(R.id.lottest_layout_1);
            viewHolder.lottestLayout2 = convertView.findViewById(R.id.lottest_layout_2);
            viewHolder.leftTimeView1 = (TextView) convertView.findViewById(R.id.left_time_view_1);
            viewHolder.leftTimeView2 = (TextView) convertView.findViewById(R.id.left_time_view_2);

            viewHolder.ownerLayout1 = convertView.findViewById(R.id.owner_layout_1);
            viewHolder.ownerLayout2 = convertView.findViewById(R.id.owner_layout_2);
            viewHolder.winningOwnerView1 = (TextView) convertView.findViewById(R.id.winning_owner_view_1);
            viewHolder.winningOwnerView2 = (TextView) convertView.findViewById(R.id.winning_owner_view_2);
            viewHolder.joinWinningTimesView1 = (TextView) convertView.findViewById(R.id.join_winning_time_view_1);
            viewHolder.joinWinningTimesView2 = (TextView) convertView.findViewById(R.id.join_winning_time_view_2);
            viewHolder.goodLuckNumView1 = (TextView) convertView.findViewById(R.id.good_luck_num_view_1);
            viewHolder.goodLuckNumView2 = (TextView) convertView.findViewById(R.id.good_luck_num_view_2);
            viewHolder.lottestTimeView1 = (TextView) convertView.findViewById(R.id.lottest_time_view_1);
            viewHolder.lottestTimeView2 = (TextView) convertView.findViewById(R.id.lottest_time_view_2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.productImageView1.setDefaultImageResId(R.drawable.tuqian);
        viewHolder.productImageView2.setDefaultImageResId(R.drawable.tuqian);
        int start = position * 2;
        int end = position * 2 + 1;
        if (records.size() - 1 >= end) {
            final WinningInfo winningInfo = records.get(start);
            final WinningInfo winningInfo2 = records.get(start + 1);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.VISIBLE);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, mImageLoader);
            viewHolder.productImageView2.setImageUrl(winningInfo2.productUrl, mImageLoader);
            viewHolder.productTitleView1.setText(winningInfo.title);
            viewHolder.productTitleView2.setText(winningInfo2.title);
            if (TextUtils.isEmpty(winningInfo.code)) {
                viewHolder.productCodeLayout1.setVisibility(View.GONE);
            } else {
                viewHolder.productCodeLayout1.setVisibility(View.VISIBLE);
                viewHolder.productCodeView1.setText("第" + winningInfo.code + "期");
            }

            if (TextUtils.isEmpty(winningInfo2.code)) {
                viewHolder.productCodeLayout2.setVisibility(View.GONE);
            } else {
                viewHolder.productCodeLayout2.setVisibility(View.VISIBLE);
                viewHolder.productCodeView2.setText("第" + winningInfo2.code + "期");
            }
            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo.lssueId);
                    it.putExtra("productId", winningInfo.productId);
                    mContext.startActivity(it);
                }
            });

            viewHolder.layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo2.lssueId);
                    it.putExtra("productId", winningInfo2.productId);
                    mContext.startActivity(it);
                }
            });

            viewHolder.addBuyCarView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBuyCar(winningInfo.lssueId, 1);
                }
            });

            viewHolder.addBuyCarView2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBuyCar(winningInfo2.lssueId, 1);
                }
            });

            viewHolder.leftTimeView1.setTag(R.id.LEFT_TIME_ID, winningInfo);
            viewHolder.leftTimeView2.setTag(R.id.LEFT_TIME_ID, winningInfo2);
            if (winningInfo.status == WinningInfo.STATUS_CHOOSE) {
                viewHolder.chooseLayout1.setVisibility(View.VISIBLE);
                viewHolder.lottestLayout1.setVisibility(View.GONE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);
                int precent = 0;
                if (winningInfo.totalCount > 0) {
                    precent = winningInfo.joinedCount * 100 / winningInfo.totalCount;
                }
                viewHolder.progressPrecentView1.setProgress(precent);
                viewHolder.progressPrecentDescView1.setText(Html.fromHtml("开奖进度:" + winningInfo.processPrecent));
            } else if (winningInfo.status == WinningInfo.STATUS_LOTTERY) {
                viewHolder.chooseLayout1.setVisibility(View.GONE);
                viewHolder.lottestLayout1.setVisibility(View.VISIBLE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);

                long left = winningInfo.lotteryTime - ServerTimeManager.getServerTime();
                if (left <= 0) {
                    viewHolder.leftTimeView1.setText("正在计算...");
                    getWinnerInfo(winningInfo);
                } else {
                    String dateleft = DateUtil.getDateBefore(winningInfo.lotteryTime, ServerTimeManager.getServerTime());
                    viewHolder.leftTimeView1.setText(dateleft);

                    Message msg = new Message();
                    msg.what = TIME_EVENT;
                    MsgObj msgObj = new MsgObj();
                    msgObj.id = winningInfo.id;
                    msgObj.timeView = new SoftReference<TextView>(viewHolder.leftTimeView1);
                    msg.obj = msgObj;
                    mHandler.sendMessage(msg);
                }

            } else {
                viewHolder.chooseLayout1.setVisibility(View.GONE);
                viewHolder.lottestLayout1.setVisibility(View.GONE);
                viewHolder.ownerLayout1.setVisibility(View.VISIBLE);
                viewHolder.winningOwnerView1.setText(winningInfo.ownerVO.userName);
                viewHolder.joinWinningTimesView1.setText(winningInfo.ownerVO.joinNumber + "人次");
                viewHolder.goodLuckNumView1.setText(winningInfo.ownerVO.winNumber);
                String dateString = DateUtil.getDateTime(winningInfo.ownerVO.winTime);
                viewHolder.lottestTimeView1.setText(dateString);
            }

            if (winningInfo2.status == WinningInfo.STATUS_CHOOSE) {
                viewHolder.chooseLayout2.setVisibility(View.VISIBLE);
                viewHolder.lottestLayout2.setVisibility(View.GONE);
                viewHolder.ownerLayout2.setVisibility(View.GONE);
                int precent = 0;
                if (winningInfo2.totalCount > 0) {
                    precent = winningInfo2.joinedCount * 100 / winningInfo2.totalCount;
                }
                viewHolder.progressPrecentView2.setProgress(precent);
                viewHolder.progressPrecentDescView2.setText(Html.fromHtml("开奖进度:" + winningInfo2.processPrecent));
            } else if (winningInfo2.status == WinningInfo.STATUS_LOTTERY) {
                long left = winningInfo2.lotteryTime - ServerTimeManager.getServerTime();
                viewHolder.chooseLayout2.setVisibility(View.GONE);
                viewHolder.lottestLayout2.setVisibility(View.VISIBLE);
                viewHolder.ownerLayout2.setVisibility(View.GONE);
                if (left <= 0) {
                    viewHolder.leftTimeView2.setText("正在计算...");
                    getWinnerInfo(winningInfo2);
                } else {
                    String dateleft2 = DateUtil.getDateBefore(winningInfo2.lotteryTime, ServerTimeManager.getServerTime());
                    viewHolder.leftTimeView2.setText(dateleft2);

                    Message msg = new Message();
                    msg.what = TIME_EVENT;
                    MsgObj msgObj = new MsgObj();
                    msgObj.id = winningInfo2.id;
                    msgObj.timeView = new SoftReference<TextView>(viewHolder.leftTimeView2);
                    msg.obj = msgObj;
                    mHandler.sendMessage(msg);
                }
            } else {
                viewHolder.chooseLayout2.setVisibility(View.GONE);
                viewHolder.lottestLayout2.setVisibility(View.GONE);
                viewHolder.ownerLayout2.setVisibility(View.VISIBLE);
                viewHolder.winningOwnerView2.setText(winningInfo2.ownerVO.userName);
                viewHolder.joinWinningTimesView2.setText(winningInfo2.ownerVO.joinNumber + "人次");
                viewHolder.goodLuckNumView2.setText(winningInfo2.ownerVO.winNumber);
                String dateString = DateUtil.getDateTime(winningInfo2.ownerVO.winTime);
                viewHolder.lottestTimeView2.setText(dateString);
            }
        } else {
            final WinningInfo winningInfo = records.get(start);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.INVISIBLE);
            viewHolder.addBuyCarView1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addBuyCar(winningInfo.lssueId, 1);
                }
            });
            viewHolder.addBuyCarView2.setOnClickListener(null);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, mImageLoader);
            viewHolder.productTitleView1.setText(winningInfo.title);

            if (TextUtils.isEmpty(winningInfo.code)) {
                viewHolder.productCodeLayout1.setVisibility(View.GONE);
            } else {
                viewHolder.productCodeLayout1.setVisibility(View.VISIBLE);
                viewHolder.productCodeView1.setText("第" + winningInfo.code + "期");
            }

            viewHolder.leftTimeView1.setTag(R.id.LEFT_TIME_ID, winningInfo);
            viewHolder.leftTimeView2.setTag(R.id.LEFT_TIME_ID, null);
            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo.lssueId);
                    it.putExtra("productId", winningInfo.productId);
                    mContext.startActivity(it);
                }
            });
            viewHolder.layout2.setOnClickListener(null);

            if (winningInfo.status == WinningInfo.STATUS_CHOOSE) {
                viewHolder.chooseLayout1.setVisibility(View.VISIBLE);
                viewHolder.lottestLayout1.setVisibility(View.GONE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);
                int precent = 0;
                if (winningInfo.totalCount > 0) {
                    precent = winningInfo.joinedCount * 100 / winningInfo.totalCount;
                }
                viewHolder.progressPrecentView1.setProgress(precent);
                viewHolder.progressPrecentDescView1.setText(Html.fromHtml("开奖进度:" + winningInfo.processPrecent));
            } else if (winningInfo.status == WinningInfo.STATUS_LOTTERY) {
                viewHolder.chooseLayout1.setVisibility(View.GONE);
                viewHolder.lottestLayout1.setVisibility(View.VISIBLE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);

                long left = winningInfo.lotteryTime - ServerTimeManager.getServerTime();
                if (left <= 0) {
                    viewHolder.leftTimeView1.setText("正在计算...");
                    getWinnerInfo(winningInfo);
                    //TODO 获取数据
                } else {
                    String dateleft = DateUtil.getDateBefore(winningInfo.lotteryTime, ServerTimeManager.getServerTime());
                    viewHolder.leftTimeView1.setText(dateleft);

                    Message msg = new Message();
                    msg.what = TIME_EVENT;
                    MsgObj msgObj = new MsgObj();
                    msgObj.id = winningInfo.id;
                    msgObj.timeView = new SoftReference<TextView>(viewHolder.leftTimeView1);
                    msg.obj = msgObj;
                    mHandler.sendMessage(msg);
                }
            } else {
                viewHolder.chooseLayout1.setVisibility(View.GONE);
                viewHolder.lottestLayout1.setVisibility(View.GONE);
                viewHolder.ownerLayout1.setVisibility(View.VISIBLE);
                viewHolder.winningOwnerView1.setText(winningInfo.ownerVO.userName);
                viewHolder.joinWinningTimesView1.setText(winningInfo.ownerVO.joinNumber + "人次");
                viewHolder.goodLuckNumView1.setText(winningInfo.ownerVO.winNumber);
                String dateString = DateUtil.getDateTime(winningInfo.ownerVO.winTime);
                viewHolder.lottestTimeView1.setText(dateString);
            }
        }

        return convertView;
    }

    public static class ViewHolder {

        public View layout1;
        public View layout2;
        public NetworkImageView productImageView1;
        public NetworkImageView productImageView2;
        public TextView productTitleView1; //产品标题１
        public TextView productTitleView2; //产品标题２
        public View productCodeLayout1;
        public View productCodeLayout2;
        public TextView productCodeView1;//期号1
        public TextView productCodeView2;//期号2
        public View chooseLayout1; //选择商品布局１
        public View chooseLayout2; //选择商品布局２
        public TextView progressPrecentDescView1; //进度条描述1
        public TextView progressPrecentDescView2; //进度条描述2
        public ProgressBar progressPrecentView1; //进度条１
        public ProgressBar progressPrecentView2; //进度条２
        public ImageView addBuyCarView1;
        public ImageView addBuyCarView2;

        public View lottestLayout1; //开奖布局１
        public View lottestLayout2; //开奖布局２
        public TextView leftTimeView1; //剩余时间１
        public TextView leftTimeView2; //剩余时间２

        public View ownerLayout1;
        public View ownerLayout2;
        public TextView winningOwnerView1;//产品获得者１
        public TextView winningOwnerView2; //产品获取者２
        public TextView joinWinningTimesView1;//参与人次1
        public TextView joinWinningTimesView2; //参与人次２
        public TextView goodLuckNumView1;//幸运号码１
        public TextView goodLuckNumView2;//幸运号码２
        public TextView lottestTimeView1;//开奖时间１
        public TextView lottestTimeView2;//开奖时间２
    }

    public static class MsgObj {
        public SoftReference<TextView> timeView;
        public long id;
    }

    public void addBuyCar(long lssueId, int num) {
        if (LoginManager.isLogin(mContext)) {
            long userId = LoginManager.getLoginInfo(mContext).ID;
            CarManager.addCar(lssueId, userId, num).startUI(new ApiCallback<Boolean>() {
                @Override
                public void onError(int code, String errorInfo) {
                    Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onSuccess(Boolean s) {
                    Toast.makeText(mContext, "添加购物车成功", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onProgress(int progress) {

                }
            });
        }
    }

}
