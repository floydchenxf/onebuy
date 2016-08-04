package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.vo.json.ProductLssueVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-8-5.
 */
public class FundAdapter extends BaseDataAdapter<ProductLssueVO> {
    private ImageLoader mImageLoader;

    public FundAdapter(Context context, List<ProductLssueVO> records, ImageLoader imageLoader) {
        super(context, records);
        this.mImageLoader = imageLoader;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.fund_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.fund_pic_view, R.id.fund_title_view, R.id.fund_content_view, R.id.fund_total_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, ProductLssueVO productLssueVO) {

        NetworkImageView fundPicView = (NetworkImageView) holder.get(R.id.fund_pic_view);
        TextView fundTitleView = (TextView) holder.get(R.id.fund_title_view);
        TextView fundContentView = (TextView) holder.get(R.id.fund_content_view);
        TextView fundTotalView = (TextView) holder.get(R.id.fund_total_view);

        fundPicView.setDefaultImageResId(R.drawable.tupian);
        if (productLssueVO != null && !TextUtils.isEmpty(productLssueVO.Pictures)) {
            fundPicView.setImageUrl(productLssueVO.getPicUrl(), mImageLoader);
        } else {
            fundPicView.setImageUrl("", mImageLoader);
        }

        fundTitleView.setText(productLssueVO.ProName);
        fundTotalView.setText(Html.fromHtml("总需：<font color=\"red\">" + productLssueVO.TotalCount + "</font>人次"));
    }
}
