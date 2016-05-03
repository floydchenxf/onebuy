package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.biz.vo.json.HistoryPrizeVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-4.
 */
public class HistoryPrizeAdapter extends BaseDataAdapter<HistoryPrizeVO> {

    private ImageLoader mImageLoader;

    public HistoryPrizeAdapter(Context context, List<HistoryPrizeVO> records) {
        super(context, records);
        mImageLoader = ImageLoaderFactory.createImageLoader();
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.history_prize_item, null);
    }

    @Override
    void initHolder(View view, Map<Integer, View> holder) {
        holder.put(R.id.lssue_code_view, view.findViewById(R.id.lssue_code_view));
        holder.put(R.id.prize_time_view, view.findViewById(R.id.prize_time_view));
        holder.put(R.id.head_image_view, view.findViewById(R.id.head_image_view));
        holder.put(R.id.prizer_view, view.findViewById(R.id.prizer_view));
        holder.put(R.id.ip_address_view, view.findViewById(R.id.ip_address_view));
        holder.put(R.id.good_luck_view, view.findViewById(R.id.good_luck_view));
        holder.put(R.id.joined_count_view, view.findViewById(R.id.joined_count_view));

    }

    @Override
    void processHolder(Map<Integer, View> holder, HistoryPrizeVO vo) {

        TextView lssueCodeView = (TextView) holder.get(R.id.lssue_code_view);
        TextView prizeTimeView = (TextView) holder.get(R.id.prize_time_view);
        NetworkImageView headImageView = (NetworkImageView) holder.get(R.id.head_image_view);
        TextView prizerView = (TextView) holder.get(R.id.prizer_view);
        TextView ipAddressView = (TextView) holder.get(R.id.ip_address_view);
        TextView goodsLuckView = (TextView) holder.get(R.id.good_luck_view);
        TextView joinedView = (TextView) holder.get(R.id.joined_count_view);

        lssueCodeView.setText(Html.fromHtml("获奖者：<font color=\"red\">"+vo.ProductLssueCode+"</font>"));
        String dateStr = DateUtil.getDateTime(vo.PrizeTime);
        prizeTimeView.setText("揭晓时间："+dateStr);

        headImageView.setImageUrl(vo.getClientPic(), mImageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, mContext.getResources().getDimension(R.dimen.cycle_head_image_size));
            }
        });

        ipAddressView.setText("IP地址：" + vo.ClientIP);
        joinedView.setText(Html.fromHtml("本次参与：<font color=\"red\">"+vo.JoinedCount+"</font>人次"));
    }
}
