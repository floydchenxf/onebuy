package com.yyg365.interestbar.ui.fragment;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.biz.tools.FileTools;
import com.yyg365.interestbar.biz.vo.AdvVO;
import com.yyg365.interestbar.channel.threadpool.WxDefaultExecutor;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.utils.CommonUtil;
import com.yyg365.interestbar.utils.WXUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.List;

/**
 * Created by yuanyi.rss on 2015/8/24.
 */
public class BannerFragment extends BaseFragment {

    public static final String Banner = "Banner";
    public static final String Position = "position";
    public static final String Height = "height";
    public static final String SCALE_TYPE = "scale_type";

    public static final int SCALE_CENTER_CROP = 0;
    public static final int SCALE_FIT_CENTER = 1;
    public static final int SCALE_FIT_XY = 2;


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

        this.mImageLoader = ImageLoaderFactory.createImageLoader();
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
        int saleType = 0;

        if (args != null) {
            mDataList = args.getParcelable(Banner);
            mPosition = args.getInt(Position);
            height = args.getInt(Height);
            saleType = args.getInt(SCALE_TYPE, 0);
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


        if (saleType == SCALE_FIT_CENTER) {
            mImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        } else if (saleType == SCALE_FIT_XY) {
            mImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        } else {
            mImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        }

        mImageView.setErrorImageResId(R.drawable.pic_loading);
        mImageView.setDefaultImageResId(R.drawable.pic_loading);
        if (mDataList != null) {
            final String fullUrl = mDataList.imgUrl;
            mImageView.setImageUrl(fullUrl, mImageLoader, new BitmapProcessor() {
                @Override
                public Bitmap processBitmap(final Bitmap bitmap) {
                    WxDefaultExecutor.getInstance().submitHighPriority(new Runnable() {
                        @Override
                        public void run() {
                            final String md5Name = WXUtil.getMD5FileName(fullUrl);
                            FileTools.writeBitmap(EnvConstants.imageRootPath + File.separator + md5Name, bitmap, 100);
                        }
                    });
                    return bitmap;
                }
            });
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
