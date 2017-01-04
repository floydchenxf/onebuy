package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.media.Image;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.ui.R;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 17/1/5.
 */
public class PawnAdapter extends BaseDataAdapter<DangPuItemVO> {

    private ImageLoader mImageLoader;

    public PawnAdapter(Context context, List<DangPuItemVO> records, ImageLoader mImageLoader) {
        super(context, records);
        this.mImageLoader = mImageLoader;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.pawn_product_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.product_pic, R.id.pawn_product_tilte_view, R.id.pawn_info_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, DangPuItemVO dangPuItemVO) {
        NetworkImageView productPicView = (NetworkImageView) holder.get(R.id.product_pic);
        TextView productTileView = (TextView) holder.get(R.id.pawn_product_tilte_view);
        TextView pawnInfoView = (TextView) holder.get(R.id.pawn_info_view);

        productPicView.setDefaultImageResId(R.drawable.tupian);
        productPicView.setImageUrl(dangPuItemVO.getProductUrl(), mImageLoader);

        productTileView.setText(dangPuItemVO.ProTitle);
        Spanned s = Html.fromHtml("<font color=\"#3787d2\">" + dangPuItemVO.ClientLevelName + "</font>&nbsp;于&nbsp;<font color=\"#3787d2\">" + DateUtil.getDateTime("yyyy-MM-dd HH:mm", dangPuItemVO.PawnTime) + "</font>&nbsp;成功典當了该期商品!");

        pawnInfoView.setText(s);
    }
}
