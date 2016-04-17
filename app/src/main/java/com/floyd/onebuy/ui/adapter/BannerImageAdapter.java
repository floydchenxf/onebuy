package com.floyd.onebuy.ui.adapter;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.ui.fragment.BannerFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by floyd on 16-4-16.
 */
public class BannerImageAdapter extends FragmentPagerAdapter {

    public static int BANNER_HEIGHT_IN_DP = 300;

    List<AdvVO> dataLists = new ArrayList<AdvVO>();
    private BannerFragment.ImagerClickListener mImagerClickListener;

    public BannerImageAdapter(FragmentManager fm, List<AdvVO> dataList, BannerFragment.ImagerClickListener imagerClickListener) {
        super(fm);
        if (dataList != null && !dataList.isEmpty()) {
            this.dataLists.addAll(dataList);
        }
        this.mImagerClickListener = imagerClickListener;
    }

    public void addItems(List<AdvVO> dataList) {
        this.dataLists.clear();
        this.dataLists.addAll(dataList);
        this.notifyDataSetChanged();
    }

    @Override
    public Fragment getItem(int position) {
        Log.d("test", "BannerImageAdapter getItem");
        Bundle args = new Bundle();
        args.putParcelable(BannerFragment.Banner, dataLists.get(position));
        args.putInt(BannerFragment.Position, position);
        args.putInt(BannerFragment.Height, BANNER_HEIGHT_IN_DP);
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
}

