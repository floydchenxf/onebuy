package com.yyg365.interestbar.ui.adapter;

import android.content.Context;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.constants.BuyCarType;
import com.yyg365.interestbar.biz.manager.DBManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.CarItemVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.channel.threadpool.WxDefaultExecutor;
import com.yyg365.interestbar.ui.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

/**
 * Created by floyd on 16-4-18.
 */
public class BuyCarAdapter extends BaseAdapter {

    private Context mContext;
    private ImageLoader mImageLoader;
    private List<CarItemVO> records = new ArrayList<CarItemVO>();

    private BuyClickListener buyClickListener;
    private CheckedListener checkedListener;
    private boolean shwoRadio = false;

    private Set<Long> deleteList = new ConcurrentSkipListSet<>();

    private static byte[] lock = new byte[0];

    public BuyCarAdapter(Context context, List<CarItemVO> args, ImageLoader imageLoader, BuyClickListener buyClickListener, CheckedListener checkedListener) {
        this.mContext = context;
        if (args != null && !args.isEmpty()) {
            this.records.addAll(args);
        }
        this.mImageLoader = imageLoader;
        this.buyClickListener = buyClickListener;
        this.checkedListener = checkedListener;
    }

    public void addAll(List<CarItemVO> records, boolean needClear) {
        synchronized (lock) {
            if (needClear) {
                this.records.clear();
            }
            this.records.addAll(records);
            this.notifyDataSetChanged();
        }
    }

    public void remove(Collection<Long> carIds) {
        synchronized (lock) {
            final List<Long> deleteLssueIds = new ArrayList<Long>();
            for (Long carId : carIds) {
                Iterator<CarItemVO> s = records.iterator();
                CarItemVO info = null;
                while (s.hasNext()) {
                    info = s.next();
                    if (info.CarID == carId) {
                        s.remove();
                    }
                }

                deleteList.remove(carId);
            }

            this.notifyDataSetChanged();
        }
    }

    public void showRadiio(boolean showRadio) {
        this.shwoRadio = showRadio;
        this.notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return this.records.size();
    }

    @Override
    public CarItemVO getItem(int position) {
        return this.records.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            convertView = View.inflate(mContext, R.layout.buy_car_item, null);
            holder = new ViewHolder();
            holder.proudctImageView = (NetworkImageView) convertView.findViewById(R.id.product_image_view);
            holder.totalLeftView = (TextView) convertView.findViewById(R.id.time_info_view);
            holder.subView = (TextView) convertView.findViewById(R.id.sub_view);
            holder.productTitleView = (TextView) convertView.findViewById(R.id.product_title_view);
            holder.addView = (TextView) convertView.findViewById(R.id.add_view);
            holder.numberView = (EditText) convertView.findViewById(R.id.number_view);
            holder.buyLeftView = (CheckedTextView) convertView.findViewById(R.id.buy_left_view);
            holder.radioButton = (CheckBox) convertView.findViewById(R.id.delete_radio);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final CarItemVO info = getItem(position);
        holder.proudctImageView.setDefaultImageResId(R.drawable.default_image);
        holder.proudctImageView.setImageUrl(info.getPicUrl(), mImageLoader);
        holder.totalLeftView.setText(Html.fromHtml("总需" + info.TotalCount + "次, 剩余<font color=\"#ffaa66\">" + (info.TotalCount - info.JoinedCount) + "</font>次"));
        holder.numberView.setText(info.CarCount + "");
        if (shwoRadio) {
            final long carId = info.CarID;
            holder.radioButton.setVisibility(View.VISIBLE);
            holder.radioButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                    if (isChecked) {
                        deleteList.add(carId);
                    } else {
                        deleteList.remove(carId);
                    }
                    buttonView.setChecked(isChecked);
                    if (checkedListener != null) {
                        checkedListener.onChecked(buttonView, isChecked);
                    }
                }
            });

            if (deleteList.contains(carId)) {
                holder.radioButton.setChecked(true);
            } else {
                holder.radioButton.setChecked(false);
            }
        } else {
            holder.radioButton.setVisibility(View.GONE);
            holder.radioButton.setOnCheckedChangeListener(null);
        }

        holder.productTitleView.setText(info.ProName);

        holder.subView.setTag(info);
        holder.subView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 1;
                String numStr = holder.numberView.getText().toString();
                if (!TextUtils.isEmpty(numStr)) {
                    num = Integer.parseInt(holder.numberView.getText().toString());
                }
                if (num <= 1) {
                    holder.numberView.setText("1");
                    num = 2;
                }

                int nn = --num;
                holder.numberView.setText(nn + "");
                if (buyClickListener != null) {
                    buyClickListener.onClick(holder.subView, holder.numberView, info.ProductLssueID, nn, info.CarCount);
                }
            }
        });

        holder.addView.setTag(info);
        holder.addView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int num = 1;
                String numStr = holder.numberView.getText().toString();
                if (!TextUtils.isEmpty(numStr)) {
                    num = Integer.parseInt(holder.numberView.getText().toString());
                }
                int left = info.TotalCount - info.JoinedCount;
                if (num >= left) {
                    Toast.makeText(mContext, "数字大于尾数!", Toast.LENGTH_SHORT).show();
                    num = --left;
                }

                int nn = ++num;
                holder.numberView.setText(nn + "");
                if (buyClickListener != null) {
                    buyClickListener.onClick(holder.addView, holder.numberView, info.ProductLssueID, nn, info.CarCount);
                }
            }
        });


        if (info.CarCount >= (info.TotalCount - info.JoinedCount)) {
            holder.buyLeftView.setChecked(false);
        } else {
            holder.buyLeftView.setChecked(true);
        }

        holder.buyLeftView.setTag(info);
        holder.buyLeftView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int left = info.TotalCount - info.JoinedCount;
                holder.numberView.setText(left + "");
                if (info.CarCount >= left) {
                    holder.buyLeftView.setChecked(false);
                } else {
                    holder.buyLeftView.setChecked(true);
                }
                if (buyClickListener != null) {
                    buyClickListener.onClick(holder.buyLeftView, holder.numberView, info.ProductLssueID, left, info.CarCount);
                }
            }
        });
        return convertView;
    }

    public Set<Long> getDeleteList() {
        return this.deleteList;
    }

    public List<CarItemVO> getRecords() {
        return this.records;
    }

    public static class ViewHolder {
        public NetworkImageView proudctImageView;
        public TextView totalLeftView;
        public TextView subView;
        public TextView productTitleView;
        public TextView addView;
        public EditText numberView;
        public CheckedTextView buyLeftView;
        public CheckBox radioButton;

    }

    public interface BuyClickListener {
        void onClick(View v, EditText numberView, long lssueId, int currentNum, int buyCount);
    }

    public interface CheckedListener {
        void onChecked(View v, boolean isChecked);
    }
}
