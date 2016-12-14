package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.tools.ImageUtils;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealPoolVO;
import com.yyg365.interestbar.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/10/7.
 */
public class CommonwealPoolPersionAdapter extends BaseDataAdapter<CommonwealPoolVO> {

    private ImageLoader mImageLoader;
    private float dp;


    public CommonwealPoolPersionAdapter(Context context, List<CommonwealPoolVO> records, ImageLoader imageLoader) {
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
    void processHolder(Map<Integer, View> holder, CommonwealPoolVO vo) {

        NetworkImageView headView = (NetworkImageView) holder.get(R.id.head);
        headView.setDefaultImageResId(R.drawable.default_head);

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
        feeView.setText(vo.Money);
        
        TextView remarkView = (TextView) holder.get(R.id.remark_view);
        TextView payTimeView = (TextView) holder.get(R.id.time_view);
        payTimeView.setText(vo.getPayTime());
        remarkView.setText(vo.Remark);

    }
}
