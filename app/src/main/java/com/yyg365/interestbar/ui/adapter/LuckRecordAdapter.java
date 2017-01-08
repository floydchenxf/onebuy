package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.OrderManager;
import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.ProductLssueItemVO;
import com.yyg365.interestbar.biz.vo.json.ProductLssueWithWinnerVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.AddressManagerActivity;
import com.yyg365.interestbar.ui.activity.PawnLogActivity;
import com.yyg365.interestbar.ui.activity.PrizeShareSubmitActivity;
import com.yyg365.interestbar.ui.activity.ShareShowDetailActivity;
import com.yyg365.interestbar.ui.activity.ShowShareActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-7-23.
 */
public class LuckRecordAdapter extends BaseDataAdapter<ProductLssueWithWinnerVO> {

    private ImageLoader mImageLoader;

    private boolean isSelf;
    private boolean hasSetAddress;
    private Long userId;

    public LuckRecordAdapter(Context context, List<ProductLssueWithWinnerVO> records, boolean isSelf, ImageLoader imageLoader) {
        super(context, records);
        this.mImageLoader = imageLoader;
        this.isSelf = isSelf;
        this.userId = LoginManager.getLoginInfo(mContext).ID;
    }

    public void setHasSetAddress(boolean hasSetAddress) {
        this.hasSetAddress = hasSetAddress;
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.luck_record_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.product_pic, R.id.code_title_view, R.id.product_code_view, R.id.total_count_view, R.id.my_join_num_view, R.id.luck_number_view, R.id.lottest_time_view, R.id.share_layout, R.id.upload_info, R.id.goods_address, R.id.pawn_status_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final ProductLssueWithWinnerVO vo) {

        NetworkImageView productPicView = (NetworkImageView) holder.get(R.id.product_pic);
        TextView titleView = (TextView) holder.get(R.id.code_title_view);
        TextView productCodeView = (TextView) holder.get(R.id.product_code_view);
        TextView totalCountView = (TextView) holder.get(R.id.total_count_view);
        TextView luckNumberView = (TextView) holder.get(R.id.luck_number_view);
        TextView lottestTimeView = (TextView) holder.get(R.id.lottest_time_view);
        View shareLayout = holder.get(R.id.share_layout);
        TextView uploadInfoView = (TextView) holder.get(R.id.upload_info);
        TextView goodsAddressView = (TextView) holder.get(R.id.goods_address);
        TextView pawnStatusView = (TextView) holder.get(R.id.pawn_status_view);

        if (isSelf) {
            shareLayout.setVisibility(View.VISIBLE);
            int isShared = vo.IsShow;
            int sendType = vo.IsSend;
            if (isShared == 0 && sendType > 3) {
                uploadInfoView.setBackgroundResource(R.drawable.common_round_red_bg);
                uploadInfoView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent submitIntent = new Intent(mContext, PrizeShareSubmitActivity.class);
                        submitIntent.putExtra(PrizeShareSubmitActivity.LSSUE_ID, vo.ProductLssueID);
                        submitIntent.putExtra(PrizeShareSubmitActivity.PRO_ID, vo.ProID);
                        submitIntent.putExtra(PrizeShareSubmitActivity.PRO_TITLE, vo.getFullTitle());
                        mContext.startActivity(submitIntent);
                    }
                });
            } else {
                uploadInfoView.setBackgroundResource(R.drawable.common_round_bg);
                uploadInfoView.setOnClickListener(null);
            }

            switch (sendType) {
                case 0://未发货  没有设置地址
                    if (hasSetAddress) {
                        goodsAddressView.setText("未发货");
                        goodsAddressView.setBackgroundResource(R.drawable.common_round_bg);
                        goodsAddressView.setOnClickListener(null);
                    } else {
                        goodsAddressView.setBackgroundResource(R.drawable.common_round_red_bg);
                        goodsAddressView.setText("设置地址");
                        goodsAddressView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent it = new Intent(mContext, AddressManagerActivity.class);
                                mContext.startActivity(it);
                            }
                        });
                    }
                    break;
                case 1://配送中
                case 2://发货中
                case 3://已发货
                    final long orderId = vo.OrderID;
                    final long lssueId = vo.ProductLssueID;
                    goodsAddressView.setBackgroundResource(R.drawable.common_round_red_bg);
                    goodsAddressView.setText("确认收货");
                    goodsAddressView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            OrderManager.receiptGoods(orderId, userId).startUI(new ApiCallback<Boolean>() {
                                @Override
                                public void onError(int code, String errorInfo) {
                                    Toast.makeText(mContext, errorInfo, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(Boolean aBoolean) {
                                    if (aBoolean) {
                                        for(ProductLssueWithWinnerVO aa:records) {
                                            if (aa.ProductLssueID == lssueId) {
                                                aa.IsSend = 4;
                                                LuckRecordAdapter.this.notifyDataSetChanged();
                                            }
                                        }

                                    }
                                }

                                @Override
                                public void onProgress(int progress) {

                                }
                            });

                        }
                    });
                    break;
                case 4:
                    goodsAddressView.setBackgroundResource(R.drawable.common_round_bg);
                    goodsAddressView.setText("已收货");
                    goodsAddressView.setOnClickListener(null);

                    break;
                default:
                    goodsAddressView.setBackgroundResource(R.drawable.common_round_bg);
                    goodsAddressView.setText("确认收货");
                    goodsAddressView.setOnClickListener(null);
                    break;


            }


            pawnStatusView.setVisibility(View.VISIBLE);
            if (vo.isPawn()) {
                pawnStatusView.setText("已當");
                pawnStatusView.setBackgroundResource(R.drawable.common_round_bg);
                pawnStatusView.setOnClickListener(null);
            } else {
                pawnStatusView.setBackgroundResource(R.drawable.common_round_red_bg);
                pawnStatusView.setText("當");
                pawnStatusView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent(mContext, PawnLogActivity.class);
                        it.putExtra(PawnLogActivity.PAWN_PRODUCT_ID, vo.ProID);
                        it.putExtra(PawnLogActivity.PAWN_PRODUCT_LSSUE_ID, vo.ProductLssueID);
                        mContext.startActivity(it);
                    }
                });
            }

            if (vo.isRedeem()) {
                pawnStatusView.setText("已赎回");
                pawnStatusView.setBackgroundResource(R.drawable.common_round_bg);
                pawnStatusView.setOnClickListener(null);
            }
        } else {
            pawnStatusView.setVisibility(View.GONE);
            shareLayout.setVisibility(View.GONE);
            uploadInfoView.setOnClickListener(null);
            goodsAddressView.setOnClickListener(null);
        }

        productPicView.setDefaultImageResId(R.drawable.tupian);
        String url = vo.getPicUrl();
        if (!TextUtils.isEmpty(url)) {
            productPicView.setImageUrl(url, mImageLoader);
        }

        productCodeView.setText(vo.ProductLssueCode);
        String title = vo.ProName;
        titleView.setText(title);

        int totalCount = vo.JonidedCount;
        String totalDesc = "总需：<font color=\"red\">" + totalCount + "</font>人次";
        totalCountView.setText(Html.fromHtml(totalDesc));

        String luckNumber = vo.PrizeCode;
        luckNumberView.setText(luckNumber);

        Long priceTime = vo.PriceTime;
        if (priceTime != null && priceTime != 0) {
            String priceTimeStr = DateUtil.getDateTime(priceTime * 1000);
            lottestTimeView.setText(priceTimeStr);
        }
    }
}
