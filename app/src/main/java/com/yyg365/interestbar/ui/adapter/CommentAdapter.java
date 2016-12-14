package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.tools.ImageUtils;
import com.yyg365.interestbar.biz.vo.json.ShowCommentVO;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.activity.PersionProfileActivity;

import java.util.List;
import java.util.Map;

/**
 * Created by chenxiaofeng on 16/9/6.
 */
public class CommentAdapter extends BaseDataAdapter<ShowCommentVO> {

    private ImageLoader mImageLoader;
    private float oneDp;

    public CommentAdapter(Context context, List<ShowCommentVO> records, ImageLoader mImageLoader) {
        super(context, records);
        this.mImageLoader = mImageLoader;
        oneDp = mContext.getResources().getDimension(R.dimen.one_dp);
    }

    @Override
    View getLayoutView() {
        return View.inflate(mContext, R.layout.comment_item, null);
    }

    @Override
    int[] cacheViews() {
        return new int[]{R.id.head, R.id.user_name_view, R.id.comment_content_view, R.id.comment_time_view};
    }

    @Override
    void processHolder(Map<Integer, View> holder, final ShowCommentVO vo) {
        NetworkImageView headView = (NetworkImageView) holder.get(R.id.head);
        TextView userNameView = (TextView) holder.get(R.id.user_name_view);
        TextView commentContentView = (TextView) holder.get(R.id.comment_content_view);
        TextView commentTimeView = (TextView) holder.get(R.id.comment_time_view);

        headView.setDefaultImageResId(R.drawable.default_head);
        headView.setImageUrl(vo.getPicUrl(), mImageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, 60 * oneDp);
            }
        });
        headView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent it = new Intent(mContext, PersionProfileActivity.class);
                it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                it.putExtra(PersionProfileActivity.CURRENT_USER_ID, vo.ClientID);
                mContext.startActivity(it);
            }
        });
        commentContentView.setText(vo.Comment);
        userNameView.setText(vo.ClientName);
        if (vo.AddTime != null && vo.AddTime > 0) {
            String s = DateUtil.getDateStr(vo.AddTime * 1000);
            commentTimeView.setText(s);
        }

    }
}
