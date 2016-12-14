package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.vo.commonweal.CommonwealVO;
import com.yyg365.interestbar.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-7-26.
 */
public class CommonwealAdapter extends BaseDataAdapter<CommonwealVO> {

    private ImageLoader mImageLoader;

    public CommonwealAdapter(Context context, List<CommonwealVO> records, ImageLoader imageLoader) {
        super(context, records);
        this.mImageLoader = imageLoader;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.commonweal_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.commonweal_pic_view, R.id.commonweal_title_view, R.id.commonweal_content_view, R.id.commonweal_fee_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, CommonwealVO commonwealVO) {

        if (commonwealVO == null) {
            return;
        }

        NetworkImageView commonwealPicView = (NetworkImageView) holder.get(R.id.commonweal_pic_view);
        TextView commonwealTitleView = (TextView) holder.get(R.id.commonweal_title_view);
        TextView commonwealContentView = (TextView) holder.get(R.id.commonweal_content_view);
        TextView commonwealFeeView = (TextView) holder.get(R.id.commonweal_fee_view);

        commonwealPicView.setDefaultImageResId(R.drawable.tupian);
        if (commonwealVO != null && !TextUtils.isEmpty(commonwealVO.Pictures)) {
            commonwealPicView.setImageUrl(commonwealVO.getPicUrl(), mImageLoader);
        } else {
            commonwealPicView.setImageUrl("", mImageLoader);
        }

        commonwealTitleView.setText(commonwealVO.FoundationName);
        String content = commonwealVO.Brief;
        commonwealContentView.setText(content);
        commonwealFeeView.setText(Html.fromHtml("公益金额：<font color=\"red\">" + commonwealVO.RaiseMoney + "</font>"));
    }
}
