package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.DangPuItemVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsDetailVO;
import com.yyg365.interestbar.biz.vo.json.JFGoodsVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.MyJFGoodsDetailActivity;
import com.yyg365.interestbar.ui.activity.RedeemLogActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 17/1/5.
 */
public class MyJFGoodsAdapter extends BaseDataAdapter<JFGoodsVO> {

    private ImageLoader mImageLoader;

    public MyJFGoodsAdapter(Context context, List<JFGoodsVO> records, ImageLoader mImageLoader) {
        super(context, records);
        this.mImageLoader = mImageLoader;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.my_jf_goods_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.product_pic, R.id.product_tilte_view, R.id.jf_view, R.id.duihuan_time, R.id.jf_layout};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final JFGoodsVO vo) {
        View jfLayout = holder.get(R.id.jf_layout);
        jfLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext, MyJFGoodsDetailActivity.class);
                it.putExtra(MyJFGoodsDetailActivity.GOODS_ID, vo.ID);
                mContext.startActivity(it);
            }
        });
        NetworkImageView productPicView = (NetworkImageView) holder.get(R.id.product_pic);
        TextView productTileView = (TextView) holder.get(R.id.product_tilte_view);
        TextView jfView = (TextView) holder.get(R.id.jf_view);
        TextView duihuanTimeView = (TextView) holder.get(R.id.duihuan_time);
        productPicView.setDefaultImageResId(R.drawable.tupian);
        productPicView.setImageUrl(vo.getPicUrl(), mImageLoader);

        productTileView.setText(vo.Name);
        Spanned s = Html.fromHtml("兑换时间:&nbsp;<font color=\"red\">" + DateUtil.getDateTime("yyyy-MM-dd HH:mm", vo.OrderTime * 1000) + "</font>");
        duihuanTimeView.setText(s);

        Spanned jfSpanned = Html.fromHtml("兑换积分:&nbsp;<font color=\"red\">" + vo.PayJiFen + "</font>");
        jfView.setText(jfSpanned);


    }
}
