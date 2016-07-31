package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
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
import com.floyd.onebuy.ui.activity.WinningDetailActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-4.
 */
public class HistoryPrizeAdapter extends BaseDataAdapter<HistoryPrizeVO> {

    private ImageLoader mImageLoader;
    private long productId;

    public HistoryPrizeAdapter(Context context, List<HistoryPrizeVO> records, long productId) {
        super(context, records);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        this.productId = productId;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.history_prize_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.title_view, R.id.head_image_view, R.id.prizer_view, R.id.client_id_view, R.id.good_luck_view, R.id.joined_count_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final HistoryPrizeVO vo) {

        TextView titleView = (TextView) holder.get(R.id.title_view);
        NetworkImageView headImageView = (NetworkImageView) holder.get(R.id.head_image_view);
        TextView winnerNameView = (TextView) holder.get(R.id.prizer_view);
        TextView clientIdView = (TextView) holder.get(R.id.client_id_view);
        TextView goodsLuckView = (TextView) holder.get(R.id.good_luck_view);
        TextView joinedView = (TextView) holder.get(R.id.joined_count_view);

        StringBuilder titleDes = new StringBuilder();
        titleDes.append("(第").append(vo.ProductLssueCode).append("期)").append("揭晓时间：");

        String prizeTime = vo.PrizeTime;
        if (!TextUtils.isEmpty(prizeTime)) {
            String dateStr = DateUtil.getDateTime(Long.parseLong(vo.PrizeTime)*1000);
            titleDes.append(dateStr);
        }

        titleView.setText(titleDes.toString());
        titleView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoDetail(vo.ProductLssueID, productId);
            }
        });

        headImageView.setDefaultImageResId(R.drawable.tupian);
        headImageView.setImageUrl(vo.getClientPic(), mImageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, mContext.getResources().getDimension(R.dimen.cycle_head_image_size));
            }
        });

        winnerNameView.setText("获奖者：" + vo.ClientName);
        clientIdView.setText("用户ID：" + vo.ClientID);
        joinedView.setText(Html.fromHtml("本次参与：<font color=\"red\">" + vo.JoinedCount + "</font>人次"));
        goodsLuckView.setText("幸运号码："+vo.PrizeCode);
    }

    private void gotoDetail(Long lssueId, long productId) {
        Intent it = new Intent(mContext, WinningDetailActivity.class);
        it.putExtra("id", lssueId);
        it.putExtra("productId", productId);
        mContext.startActivity(it);
    }
}
