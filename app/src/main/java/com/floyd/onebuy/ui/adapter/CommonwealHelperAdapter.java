package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.biz.vo.commonweal.CommonwealHelperVO;
import com.floyd.onebuy.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-7-30.
 */
public class CommonwealHelperAdapter extends BaseDataAdapter<CommonwealHelperVO> {
    private ImageLoader mImageLoader;
    private float dp;

    public CommonwealHelperAdapter(Context context, List<CommonwealHelperVO> records, ImageLoader imageLoader) {
        super(context, records);
        this.mImageLoader = imageLoader;
        dp = mContext.getResources().getDimension(R.dimen.one_dp);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.commonweal_helper_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.head, R.id.helper_name_view, R.id.fee_view, R.id.remark_view, R.id.time_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, CommonwealHelperVO vo) {
        NetworkImageView headView = (NetworkImageView) holder.get(R.id.head);
        headView.setDefaultImageResId(R.drawable.tupian);

        if (!TextUtils.isEmpty(vo.getClientPic())) {
            headView.setImageUrl(vo.getClientPic(), mImageLoader, new BitmapProcessor() {
                @Override
                public Bitmap processBitmap(Bitmap bitmap) {
                    return ImageUtils.getCircleBitmap(bitmap, 60 * dp);
                }
            });
        } else {
            headView.setImageUrl(null, mImageLoader);
        }

        TextView helpNameView = (TextView) holder.get(R.id.helper_name_view);
        helpNameView.setText(vo.ClientName);
        TextView feeView = (TextView) holder.get(R.id.fee_view);
        feeView.setText(vo.PayMoney);

        TextView remarkView = (TextView) holder.get(R.id.remark_view);
        remarkView.setText(vo.Remark);

        TextView payTimeView = (TextView) holder.get(R.id.time_view);
        payTimeView.setText(DateUtil.getDateTime(vo.PayTime * 1000));
    }
}
