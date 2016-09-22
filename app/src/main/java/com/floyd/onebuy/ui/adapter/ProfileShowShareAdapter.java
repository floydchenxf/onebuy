package com.floyd.onebuy.ui.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.vo.json.PrizeShowVO;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.activity.ShareShowDetailActivity;
import com.floyd.onebuy.ui.multiimage.MultiImageActivity;
import com.floyd.onebuy.ui.multiimage.base.MulitImageVO;
import com.floyd.onebuy.ui.multiimage.base.PicViewObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/3.
 */
public class ProfileShowShareAdapter extends BaseDataAdapter<PrizeShowVO> {

    private ImageLoader mImageLoader;

    private int width;
    private float oneDp;
    private int eachWidth;

    public ProfileShowShareAdapter(Context context, List<PrizeShowVO> records, ImageLoader mImageLoader) {
        super(context, records);
        this.mImageLoader = mImageLoader;
        width = ((Activity) mContext).getWindowManager().getDefaultDisplay().getWidth();
        oneDp = mContext.getResources().getDimension(R.dimen.one_dp);
        eachWidth = (int) ((width - 40 * oneDp) / 2);

    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.fragment_profile_show_share_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.guest_title_view, R.id.guest_content_view, R.id.comment_button, R.id.guest_pic_layout, R.id.guest_time_view};
    }

    @Override
    void processHolder(Map holder, final PrizeShowVO vo) {
        TextView guestTitleView = (TextView) holder.get(R.id.guest_title_view);
        TextView guestContentView = (TextView) holder.get(R.id.guest_content_view);
        LinearLayout guestPicLayout = (LinearLayout) holder.get(R.id.guest_pic_layout);
        ImageView commentButton = (ImageView) holder.get(R.id.comment_button);
        TextView guestTimeView = (TextView) holder.get(R.id.guest_time_view);

        guestTitleView.setText(vo.GuestTitle);
        guestContentView.setText(vo.GuestContent);

        if (vo.isVerify > 0) {
            commentButton.setVisibility(View.VISIBLE);
            commentButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent(mContext, ShareShowDetailActivity.class);
                    it.putExtra(ShareShowDetailActivity.GUEST_ID, vo.GuestID);
                    mContext.startActivity(it);
                }
            });
        } else {
            commentButton.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(vo.GuestTime)) {
            guestTimeView.setVisibility(View.GONE);
        } else {
            guestTimeView.setVisibility(View.VISIBLE);
            String timeStr = DateUtil.getDateTime(Long.parseLong(vo.GuestTime) * 1000);
            guestTimeView.setText(" " + timeStr + " ");
        }

        List<String> picList = vo.getMediaUrls();
        if (picList.isEmpty()) {
            guestPicLayout.setVisibility(View.GONE);
            guestPicLayout.removeAllViews();
        } else {
            guestPicLayout.setVisibility(View.VISIBLE);
            guestPicLayout.removeAllViews();

            int len = picList.size() > 2 ? 2 : picList.size();
            for (int i = 0; i < len; i++) {
                final String picUrl = picList.get(i);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(eachWidth, 3 * eachWidth / 4);
                lp.setMargins(0, 0, (int) (10 * oneDp), 0);

                NetworkImageView networkImage = new NetworkImageView(mContext);
                networkImage.setDefaultImageResId(R.drawable.tupian);
                networkImage.setImageUrl(picUrl, mImageLoader);
                networkImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                networkImage.setLayoutParams(lp);
                final int k = i;
                guestPicLayout.addView(networkImage);
            }
        }

    }
}
