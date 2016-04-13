package com.floyd.onebuy.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.floyd.onebuy.R;
import com.floyd.onebuy.biz.vo.mote.MoteInfoVO;
import com.floyd.onebuy.ui.ImageLoaderFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 15-11-21.
 */
public class IndexMoteAdapter extends BaseAdapter {

    private List<MoteInfoVO> mList = new ArrayList<MoteInfoVO>();
    private LayoutInflater infalter;

    private ImageLoader imageLoader;

    private boolean isEnd;
    private Context mContext;

    public IndexMoteAdapter(Context context, List<MoteInfoVO> args) {
        this.imageLoader = ImageLoaderFactory.createImageLoader();
        infalter = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mList.addAll(args);
        mContext = context;
    }

    public void add(MoteInfoVO k) {
        mList.add(k);
        this.notifyDataSetChanged();
    }

    public void addAll(List<MoteInfoVO> motes, boolean needClear) {
        if (needClear) {
            mList.clear();
        }
        mList.addAll(motes);
        this.notifyDataSetChanged();
    }

    public List<MoteInfoVO> getMoteList() {
        return mList;
    }

    @Override
    public int getCount() {
        return mList.size() % 3 == 0 ? mList.size() / 3 : mList.size() / 3 + 1;
    }

    @Override
    public MoteInfoVO getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = infalter.inflate(R.layout.simple_adapter, null);
            holder = new ViewHolder();
            holder.moteViewImage1 = (NetworkImageView) convertView.findViewById(R.id.moteImage1);
            holder.moteViewImage2 = (NetworkImageView) convertView.findViewById(R.id.moteImage2);
            holder.moteViewImage3 = (NetworkImageView) convertView.findViewById(R.id.moteImage3);

            holder.layout1 = convertView.findViewById(R.id.moteLayout1);
            holder.layout2 = convertView.findViewById(R.id.moteLayout2);
            holder.layout3 = convertView.findViewById(R.id.moteLayout3);

            holder.moteNickView1 = (TextView)convertView.findViewById(R.id.moteNick1);
            holder.moteNickView2 = (TextView)convertView.findViewById(R.id.moteNick2);
            holder.moteNickView3 = (TextView)convertView.findViewById(R.id.moteNick3);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.moteViewImage1.setDefaultImageResId(R.drawable.tuqian);
        holder.moteViewImage2.setDefaultImageResId(R.drawable.tuqian);
        holder.moteViewImage3.setDefaultImageResId(R.drawable.tuqian);

        int start = position * 3;
        int end = position * 3 + 2;


        if (mList.size() - 1 >= end) {
            final MoteInfoVO mote1 = mList.get(start);
            holder.moteViewImage1.setImageUrl(mote1.getHeadUrl(), imageLoader);
            holder.moteNickView1.setText(mote1.nickname);
            holder.layout1.setVisibility(View.VISIBLE);
//            holder.layout1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        Intent it = new Intent(mContext, MoteDetailActivity.class);
//                        it.putExtra("moteId", mote1.id);
//                        mContext.startActivity(it);
//
//                }
//            });
            final MoteInfoVO mote2 = mList.get(start + 1);
            holder.moteViewImage2.setImageUrl(mote2.getHeadUrl(), imageLoader);
            holder.moteNickView2.setText(mote2.nickname);
            holder.layout2.setVisibility(View.VISIBLE);
//            holder.layout2.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        Intent it = new Intent(mContext, MoteDetailActivity.class);
//                        it.putExtra("moteId", mote2.id);
//                        mContext.startActivity(it);
//
//                }
//            });
            final MoteInfoVO mote3 = mList.get(start + 2);
            holder.moteViewImage3.setImageUrl(mote3.getHeadUrl(), imageLoader);
            holder.moteNickView3.setText(mote3.nickname);
            holder.layout3.setVisibility(View.VISIBLE);
//            holder.layout3.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        Intent it = new Intent(mContext, MoteDetailActivity.class);
//                        it.putExtra("moteId", mote3.id);
//                        mContext.startActivity(it);
//
//                }
//            });
        } else {
            final MoteInfoVO mote1 = mList.get(start);
            holder.moteViewImage1.setImageUrl(mote1.getHeadUrl(), imageLoader);
            holder.moteNickView1.setText(mote1.nickname);
            holder.layout1.setVisibility(View.VISIBLE);
//            holder.layout1.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                        Intent it = new Intent(mContext, MoteDetailActivity.class);
//                        it.putExtra("moteId", mote1.id);
//                        mContext.startActivity(it);
//
//                }
//            });

            if (mList.size() - 1 < start + 1) {
                holder.layout2.setVisibility(View.INVISIBLE);
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout2.setOnClickListener(null);
                holder.layout3.setOnClickListener(null);
            } else {
                final MoteInfoVO mote2 = mList.get(start + 1);
                holder.moteViewImage2.setImageUrl(mote2.getHeadUrl(), imageLoader);
                holder.moteNickView2.setText(mote2.nickname);
                holder.layout2.setVisibility(View.VISIBLE);
//                holder.layout2.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                            Intent it = new Intent(mContext, MoteDetailActivity.class);
//                            it.putExtra("moteId", mote2.id);
//                            mContext.startActivity(it);
//                    }
//                });
                holder.layout3.setVisibility(View.INVISIBLE);
                holder.layout3.setOnClickListener(null);
            }
        }

        return convertView;
    }

    public static class ViewHolder {

        private NetworkImageView moteViewImage1;
        private NetworkImageView moteViewImage2;
        private NetworkImageView moteViewImage3;

        private TextView moteNickView1;
        private TextView moteNickView2;
        private TextView moteNickView3;

        private View layout1;
        private View layout2;
        private View layout3;


    }

}
