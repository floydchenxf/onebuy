package com.floyd.onebuy.ui.fragment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.floyd.onebuy.R;
import com.floyd.onebuy.ui.DialogCreator;
import com.floyd.onebuy.ui.ImageLoaderFactory;
import com.floyd.onebuy.ui.activity.FeeRecordActivity;
import com.floyd.onebuy.ui.loading.DataLoadingView;
import com.floyd.onebuy.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyFragment extends BackHandledFragment implements View.OnClickListener {

    private static final String TAG = "MyFragment";

    private static final int CODE_GALLERY_REQUEST = 80;
    private static final int CROP_PICTURE_REQUEST = 81;

    private List<Map<String, Object>> lstImageItem = new ArrayList<Map<String, Object>>();


    private ProgressDialog avatorDialog;

    private ImageLoader mImageLoader;

    private DataLoadingView dataLoadingView;
    private Dialog dataLoadingDialog;

    private TextView userNameView;
    private TextView feeView;
    private TextView addFeeView;
    private GridView operateGridView;

    private String[] texts = new String[]{"充值记录", "夺宝记录", "中奖记录", "我的积分", "我的公益", "我的晒单", "快乐星期五", "邀请好友"};
    private int[] images = new int[]{R.drawable.icon, R.drawable.icon, R.drawable.icon, R.drawable.icon, R.drawable.icon, R.drawable.icon,R.drawable.icon, R.drawable.icon};
    private Class[] clazzs = new Class[]{FeeRecordActivity.class, FeeRecordActivity.class,FeeRecordActivity.class,FeeRecordActivity.class,FeeRecordActivity.class,FeeRecordActivity.class,FeeRecordActivity.class,FeeRecordActivity.class};

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        mImageLoader = ImageLoaderFactory.createImageLoader();

        for (int i = 0; i < texts.length; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage",images[i]);//添加图像资源的ID
            map.put("ItemText", texts[i]);//按序号做ItemText
            lstImageItem.add(map);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my, container, false);

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(view, this);
        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this.getActivity());

        userNameView = (TextView) view.findViewById(R.id.user_name);
        feeView = (TextView) view.findViewById(R.id.fee);
        addFeeView = (TextView) view.findViewById(R.id.add_fee);
        operateGridView = (GridView) view.findViewById(R.id.operate_gridview);

        SimpleAdapter saImageItems = new SimpleAdapter(this.getActivity(),
                lstImageItem,
                R.layout.night_item,
                new String[]{"ItemImage", "ItemText"},
                new int[]{R.id.ItemImage, R.id.ItemText});
        operateGridView.setAdapter(saImageItems);
        operateGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent it = new Intent(MyFragment.this.getActivity(), clazzs[position]);
                MyFragment.this.getActivity().startActivity(it);
            }
        });

        loadData(true, true);
        return view;
    }

    public void onResume() {
        super.onResume();
        loadData(false, false);
    }

    private void loadData(final boolean needDialog, final boolean isFirst) {
//        if (needDialog) {
//            if (isFirst) {
//                dataLoadingView.startLoading();
//            } else {
//                dataLoadingDialog.show();
//            }
//        }
    }

    @Override
    public boolean onBackPressed() {
        return true;
    }

    @Override
    public void onClick(View v) {

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
    }
}
