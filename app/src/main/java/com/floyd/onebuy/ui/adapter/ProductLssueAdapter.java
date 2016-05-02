package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.manager.ServerTimeManager;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.model.WinningInfo;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.WinningDetailActivity;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-4-24.
 */
public class ProductLssueAdapter extends BaseAdapter {

    private Context mContext;
    private List<WinningInfo> records = new ArrayList<WinningInfo>();
    private ImageLoader mImageLoader;

    private Map<Long, Boolean> requets = new HashMap<Long, Boolean>();
    private static final int TIME_EVENT = 1;

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

                    if (itemVO.status != 2) {
                        return;
                    }

                    long left = itemVO.lotteryTime - ServerTimeManager.getServerTime();
                    if (left <= 0) {
                        timeView.setText("正在计算...");
                        return;
//                        Toast.makeText(mContext, "goto web work...", Toast.LENGTH_SHORT).show();
                    } else {
                        String dateLeft = DateUtil.getDateBefore(itemVO.lotteryTime, ServerTimeManager.getServerTime());
                        timeView.setText(dateLeft);
                    }

                    Message newMsg = new Message();
                    newMsg.what = TIME_EVENT;
                    newMsg.obj = o;
                    mHandler.sendMessageDelayed(newMsg, 200);
                    break;
            }
        }
    };

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
            viewHolder.addBuyCarView1 = (TextView) convertView.findViewById(R.id.add_buy_list_view_1);
            viewHolder.addBuyCarView2 = (TextView) convertView.findViewById(R.id.add_buy_list_view_2);
            viewHolder.progressPrecentDescView1 = (TextView) convertView.findViewById(R.id.progress_precent_desc_1);
            viewHolder.progressPrecentDescView2 = (TextView) convertView.findViewById(R.id.progress_precent_desc_2);
            viewHolder.productImageView1 = (NetworkImageView) convertView.findViewById(R.id.product_pic_1);
            viewHolder.productImageView2 = (NetworkImageView) convertView.findViewById(R.id.product_pic_2);
            viewHolder.productTitleView1 = (TextView) convertView.findViewById(R.id.product_title_view_1);
            viewHolder.productTitleView2 = (TextView) convertView.findViewById(R.id.product_title_view_2);
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
            viewHolder.lottestTimeView2 = (TextView) convertView.findViewById(R.id.good_luck_num_view_2);
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
            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo.lssueId);
                    mContext.startActivity(it);
                }
            });

            viewHolder.layout2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo2.lssueId);
                    mContext.startActivity(it);
                }
            });

            viewHolder.leftTimeView1.setTag(R.id.LEFT_TIME_ID, winningInfo);
            viewHolder.leftTimeView2.setTag(R.id.LEFT_TIME_ID, winningInfo2);
            if (winningInfo.status == 1) {
                viewHolder.chooseLayout1.setVisibility(View.VISIBLE);
                viewHolder.lottestLayout1.setVisibility(View.GONE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);
                int precent = 0;
                if (winningInfo.totalCount > 0) {
                    precent = winningInfo.joinedCount* 100 / winningInfo.totalCount;
                }
                viewHolder.progressPrecentView1.setProgress(precent);
                viewHolder.progressPrecentDescView1.setText(Html.fromHtml("夺宝进度:<font color=\"blue\">" + winningInfo.processPrecent + "</font>"));
            } else if (winningInfo.status == 2) {
                viewHolder.chooseLayout1.setVisibility(View.GONE);
                viewHolder.lottestLayout1.setVisibility(View.VISIBLE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);

                long left = winningInfo.lotteryTime - ServerTimeManager.getServerTime();
                if (left <= 0) {
                    viewHolder.leftTimeView1.setText("正在计算...");
//                    Toast.makeText(mContext, "goto web work...", Toast.LENGTH_SHORT).show();
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
                //TODO
            }

            if (winningInfo2.status == 1) {
                viewHolder.chooseLayout2.setVisibility(View.VISIBLE);
                viewHolder.lottestLayout2.setVisibility(View.GONE);
                viewHolder.ownerLayout2.setVisibility(View.GONE);
                int precent = 0;
                if (winningInfo2.totalCount > 0) {
                    precent = winningInfo2.joinedCount * 100 / winningInfo2.totalCount;
                }
                viewHolder.progressPrecentView2.setProgress(precent);
                viewHolder.progressPrecentDescView2.setText(Html.fromHtml("夺宝进度:<font color=\"blue\">" + winningInfo2.processPrecent + "</font>"));
            } else if (winningInfo2.status == 2) {
                long left = winningInfo2.lotteryTime - ServerTimeManager.getServerTime();
                viewHolder.chooseLayout2.setVisibility(View.GONE);
                viewHolder.lottestLayout2.setVisibility(View.VISIBLE);
                viewHolder.ownerLayout2.setVisibility(View.GONE);
                if (left <= 0) {
                    viewHolder.leftTimeView2.setText("正在计算...");
//                    Toast.makeText(mContext, "goto web work...", Toast.LENGTH_SHORT).show();
                    //TODO 获取数据
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
                //TODO
            }
        } else {
            final WinningInfo winningInfo = records.get(start);
            viewHolder.layout1.setVisibility(View.VISIBLE);
            viewHolder.layout2.setVisibility(View.INVISIBLE);
            viewHolder.productImageView1.setImageUrl(winningInfo.productUrl, mImageLoader);
            viewHolder.productTitleView1.setText(winningInfo.title);

            viewHolder.leftTimeView1.setTag(R.id.LEFT_TIME_ID, winningInfo);
            viewHolder.leftTimeView2.setTag(R.id.LEFT_TIME_ID, null);
            viewHolder.layout1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent it = new Intent(mContext, WinningDetailActivity.class);
                    it.putExtra("id", winningInfo.lssueId);
                    mContext.startActivity(it);
                }
            });
            viewHolder.layout2.setOnClickListener(null);

            if (winningInfo.status == 1) {
                viewHolder.chooseLayout1.setVisibility(View.VISIBLE);
                viewHolder.lottestLayout1.setVisibility(View.GONE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);
                int precent = 0;
                if (winningInfo.totalCount > 0) {
                    precent = winningInfo.joinedCount * 100 / winningInfo.totalCount;
                }
                viewHolder.progressPrecentView1.setProgress(precent);
                viewHolder.progressPrecentDescView1.setText(Html.fromHtml("夺宝进度:<font color=\"blue\">" + winningInfo.processPrecent + "</font>"));
            } else if (winningInfo.status == 2) {
                viewHolder.chooseLayout1.setVisibility(View.GONE);
                viewHolder.lottestLayout1.setVisibility(View.VISIBLE);
                viewHolder.ownerLayout1.setVisibility(View.GONE);

                long left = winningInfo.lotteryTime - ServerTimeManager.getServerTime();
                if (left <= 0) {
                    viewHolder.leftTimeView1.setText("正在计算...");
//                    Toast.makeText(mContext, "goto web work...", Toast.LENGTH_SHORT).show();
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
                //TODO
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
        public View chooseLayout1; //选择商品布局１
        public View chooseLayout2; //选择商品布局２
        public TextView progressPrecentDescView1; //进度条描述1
        public TextView progressPrecentDescView2; //进度条描述2
        public ProgressBar progressPrecentView1; //进度条１
        public ProgressBar progressPrecentView2; //进度条２
        public TextView addBuyCarView1;
        public TextView addBuyCarView2;

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

}
