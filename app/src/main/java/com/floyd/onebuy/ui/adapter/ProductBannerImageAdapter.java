package com.floyd.onebuy.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.View;

import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.ui.fragment.BannerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-16.
 */
public class ProductBannerImageAdapter extends FragmentStatePagerAdapter {

    private static final String TAG = "testProduct";

    public static int BANNER_HEIGHT_IN_DP = 300;

    private int height;

    List<AdvVO> dataLists = new ArrayList<AdvVO>();
    private BannerFragment.ImagerClickListener mImagerClickListener;

    public ProductBannerImageAdapter(FragmentManager fm, List<AdvVO> dataList, BannerFragment.ImagerClickListener imagerClickListener) {
        super(fm);
        if (dataList != null && !dataList.isEmpty()) {
            this.dataLists.addAll(dataList);
        }
        this.mImagerClickListener = imagerClickListener;
        height = BANNER_HEIGHT_IN_DP;
    }

    public void addItems(List<AdvVO> dataList) {
        this.dataLists.clear();
        this.dataLists.addAll(dataList);
        this.notifyDataSetChanged();
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public Fragment getItem(int position) {
        Log.i(TAG, "getItem---"+position);
        Bundle args = new Bundle();
        args.putParcelable(BannerFragment.Banner, dataLists.get(position));
        args.putInt(BannerFragment.Position, position);
        args.putInt(BannerFragment.Height, height);
        return BannerFragment.newInstance(args, mImagerClickListener);
    }

    @Override
    public int getCount() {
        if (dataLists != null) {
            return dataLists.size();
        }
        return 0;
    }

    public int getItemPosition(Object object) {
        return super.getItemPosition(object);
    }

    public long getItemId(int position) {
        if (position >= 0 && position < dataLists.size()) {
            AdvVO dataList = dataLists.get(position);
            if (dataList != null) {
                long id = dataList.id;
                return id;
            }
        }
        return (long) position;
    }

    public boolean isViewFromObject(View view, Object object) {
        Log.i(TAG, "isViewFromObject---view:" + view.toString() + "---object:" + object);
        return super.isViewFromObject(view, object);
    }
}

