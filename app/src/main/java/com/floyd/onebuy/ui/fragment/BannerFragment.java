package com.floyd.onebuy.ui.fragment;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.floyd.onebuy.IMChannel;
import com.floyd.onebuy.IMImageCache;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.biz.constants.EnvConstants;
import com.floyd.onebuy.biz.vo.AdvVO;
import com.floyd.onebuy.utils.CommonUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by yuanyi.rss on 2015/8/24.
 */
public class BannerFragment extends BaseFragment {

    public static final String Banner = "Banner";
    public static final String Position = "position";
    public static final String Height = "height";


    private ImageLoader mImageLoader;
    private AdvVO mDataList;
    private int mPosition;
    private NetworkImageView mImageView;
    private int mImageSize;
    private ImagerClickListener imagerClickListener;

    private int mScreenWidth;

    public static BannerFragment newInstance(Bundle args, ImagerClickListener imagerClickListener) {
        final BannerFragment f = new BannerFragment();
        f.setArguments(args);
        f.setRetainInstance(false);
        f.imagerClickListener = imagerClickListener;
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RequestQueue mQueue = Volley.newRequestQueue(this.getActivity());
        IMImageCache wxImageCache = IMImageCache.findOrCreateCache(
                IMChannel.getApplication(), EnvConstants.imageRootPath);
        this.mImageLoader = new ImageLoader(mQueue, wxImageCache);
        mImageLoader.setBatchedResponseDelay(0);
        DisplayMetrics dm = this.getActivity().getResources()
                .getDisplayMetrics();
        mScreenWidth = dm.widthPixels;
        if (mScreenWidth >= 720) {
            mImageSize = 720;
        } else {
            mImageSize = 480;
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle args = getArguments();
        int height = (int) getActivity().getResources().getDimension(R.dimen.BANNER_HEIGHT_IN_DP);

        if (args != null) {
            mDataList = args.getParcelable(Banner);
            mPosition = args.getInt(Position);
            height = args.getInt(Height);
        }

        mImageView = new NetworkImageView(getActivity());
        mImageView.setDefaultImageResId(R.drawable.tupian);
        mImageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, CommonUtil.dip2px(getActivity(), height)));
        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (imagerClickListener != null) {
                    imagerClickListener.onClick(v, mDataList);
                }
            }
        });
        mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        mImageView.setErrorImageResId(R.drawable.pic_loading);
        mImageView.setDefaultImageResId(R.drawable.pic_loading);
        if (mDataList != null) {
            String fullUrl = mDataList.getImgUrl();
            mImageView.setImageUrl(fullUrl, mImageLoader);
        }
        return mImageView;
    }

    private void callAction(final List<String> actions, final String param, final AdvVO banner) {

//        Intent intent = new Intent(getActivity(), H5Activity.class);
//        String url = APIConstants.HOST + APIConstants.API_ADV_DETAIL_INFO + "?id=" + mDataList.id;
//        H5Activity.H5Data h5Data = new H5Activity.H5Data();
//        h5Data.dataType = H5Activity.H5Data.H5_DATA_TYPE_URL;
//        h5Data.data = url;
//        h5Data.showProcess = true;
//        h5Data.showNav = true;
//        intent.putExtra(H5Activity.H5Data.H5_DATA, h5Data);
//        Intent intent = new Intent(getActivity(), MoteDetailActivity.class);
//        intent.putExtra(MoteDetailActivity.MOTE_ID, mDataList.id);
//        startActivity(intent);

    }

    private String getActionParam(List<String> actions) {
        JSONObject jsonObject = new JSONObject();
        JSONArray jsonArray = new JSONArray();
        try {
            for (int i = 0; i < actions.size(); i++) {
                jsonArray.put(i, actions.get(i));
            }
            jsonObject.put("action", jsonArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }


    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onShow() {

    }

    @Override
    public void clearGestureLayout() {

    }

    public static interface ImagerClickListener {

        void onClick(View v, AdvVO advVO);

    }

}
