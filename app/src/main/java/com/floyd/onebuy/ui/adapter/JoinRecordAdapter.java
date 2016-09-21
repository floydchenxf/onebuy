package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.biz.tools.DateUtil;
import com.floyd.onebuy.biz.tools.ImageUtils;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.biz.vo.product.JoinVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-17.
 */
public class JoinRecordAdapter extends BaseAdapter {
    private List<JoinVO> joinRecordVOList = new ArrayList<JoinVO>();
    private Context mContext;
    private ImageLoader imageLoader;
    private float onDp;

    public JoinRecordAdapter(Context context, List<JoinVO> args) {
        joinRecordVOList.addAll(args);
        this.imageLoader = ImageLoaderFactory.createImageLoader();
        mContext = context;
        onDp = context.getResources().getDimension(R.dimen.one_dp);
    }

    public void add(JoinVO k) {
        joinRecordVOList.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<JoinVO> records, boolean needClear) {
        if (needClear) {
            joinRecordVOList.clear();
        }
        joinRecordVOList.addAll(records);
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return joinRecordVOList.size();
    }

    @Override
    public JoinVO getItem(int position) {
        return joinRecordVOList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public List<JoinVO> getFeeRecords() {
        return this.joinRecordVOList;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.join_record_item, null);
            holder = new ViewHolder();
            holder.headImage = (NetworkImageView) convertView.findViewById(R.id.head);
            holder.userNameView = (TextView) convertView.findViewById(R.id.user_name_view);
            holder.joinNumberView = (TextView) convertView.findViewById(R.id.join_number_view);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        JoinVO vo = getItem(position);
        holder.headImage.setDefaultImageResId(R.drawable.default_head);
        holder.headImage.setImageUrl(vo.getClientPic(), imageLoader, new BitmapProcessor() {
            @Override
            public Bitmap processBitmap(Bitmap bitmap) {
                return ImageUtils.getCircleBitmap(bitmap, 40 * onDp);
            }
        });
        holder.userNameView.setText(vo.ClientName + "(" + vo.ClientIP + ")");
        String dateTimeStr = "";
        if (!TextUtils.isEmpty(vo.InTime) && TextUtils.isDigitsOnly(vo.InTime)) {
            Long t = Long.parseLong(vo.InTime);
            dateTimeStr = DateUtil.getDateTime(t * 1000);
        }
        holder.joinNumberView.setText(Html.fromHtml("参与了<font color=\"red\">" + vo.Number + "</font>次 " + dateTimeStr));
        return convertView;
    }

    public static class ViewHolder {

        public NetworkImageView headImage;
        public TextView userNameView;
        public TextView joinNumberView;

    }
}
