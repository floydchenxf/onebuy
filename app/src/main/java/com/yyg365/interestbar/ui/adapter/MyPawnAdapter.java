package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.Html;
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
public class MyPawnAdapter extends BaseDataAdapter<DangPuItemVO> {

    private ImageLoader mImageLoader;

    public MyPawnAdapter(Context context, List<DangPuItemVO> records, ImageLoader mImageLoader) {
        super(context, records);
        this.mImageLoader = mImageLoader;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.my_pawn_product_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.product_pic, R.id.pawn_product_tilte_view, R.id.pawn_info_view, R.id.pawn_action_view, R.id.pawn_info_status_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, DangPuItemVO dangPuItemVO) {
        NetworkImageView productPicView = (NetworkImageView) holder.get(R.id.product_pic);
        TextView productTileView = (TextView) holder.get(R.id.pawn_product_tilte_view);
        TextView pawnInfoView = (TextView) holder.get(R.id.pawn_info_view);

        TextView pawnActionView = (TextView) holder.get(R.id.pawn_action_view);
        TextView pawnInfoStatusView = (TextView) holder.get(R.id.pawn_info_status_view);

        productPicView.setDefaultImageResId(R.drawable.tupian);
        productPicView.setImageUrl(dangPuItemVO.getProductUrl(), mImageLoader);

        productTileView.setText(dangPuItemVO.ProTitle);
        Spanned s = Html.fromHtml("您于&nbsp;<font color=\"#3787d2\">" + DateUtil.getDateTime("yyyy-MM-dd HH:mm", dangPuItemVO.getPawnTime()) + "</font>,&nbsp;以<font color=\"red\">" + dangPuItemVO.PawnPrice + "</font>成功典當了本期商品!");
        pawnInfoView.setText(s);

        if (dangPuItemVO.isRedeem()) {
            pawnActionView.setText("已赎回");
            pawnActionView.setBackgroundResource(R.drawable.common_round_green_bord_bg);
            pawnActionView.setTextColor(Color.GREEN);
            pawnActionView.setClickable(false);
            Spanned t = Html.fromHtml("您已经以&nbsp;<font color=\"red\">" + dangPuItemVO.RealRedeemPrice + "</font>&nbsp;元成功赎回该商品！");
            pawnInfoStatusView.setText(t);
        } else {
            pawnActionView.setText("赎回");
            if (dangPuItemVO.RedeemDays >= 0) {
                pawnActionView.setClickable(true);
                Spanned t = Html.fromHtml("该商品赎回期剩余&nbsp;<font color=\"red\">" + dangPuItemVO.RedeemDays + "</font>&nbsp;天!");
                pawnInfoStatusView.setText(t);
            } else {
                pawnActionView.setClickable(false);
                Spanned t = Html.fromHtml("<font color=\"red\">该商品已过赎回期!</font>");
                pawnInfoStatusView.setText(t);
            }
        }
    }
}
