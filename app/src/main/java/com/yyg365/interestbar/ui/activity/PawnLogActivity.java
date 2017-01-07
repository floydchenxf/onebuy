package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.PawnManager;
import com.yyg365.interestbar.biz.vo.json.PawnLevelVO;
import com.yyg365.interestbar.biz.vo.json.PawnLogInfoVO;
import com.yyg365.interestbar.biz.vo.json.PawnLogVO;
import com.yyg365.interestbar.ui.DialogCreator;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.PawnLevelAdapter;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;

import java.util.ArrayList;
import java.util.List;

/**
 * 创建典当的信息
 */
public class PawnLogActivity extends Activity implements View.OnClickListener {

    public static final String PAWN_PRODUCT_ID = "PAWN_PRODUCT_ID";
    public static final String PAWN_PRODUCT_LSSUE_ID = "PAWN_PRODUCT_LSSUE_ID";

    private NetworkImageView productPicView;
    private TextView productPriceView;
    private TextView pawnPriceView;
    private TextView productTitleView;
    private ListView pawnLeveListView;
    private PawnLevelAdapter mAdapter;
    private ImageLoader mImageLoader;
    private DefaultDataLoadingView dataLoadingView;
    private Dialog dataLoadingDialog;

    private TextView pawnPriceTipView;//价格提示
    private TextView dangButton;

    private boolean isFirst = true;

    private Long proId;
    private Long lssueId;
    private Long userId;

    private Double pawnMsPrice = 0d;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pawn_log);

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("典當");
        titleNameView.setVisibility(View.VISIBLE);

        findViewById(R.id.title_back).setOnClickListener(this);

        proId = getIntent().getLongExtra(PAWN_PRODUCT_ID, 0l);
        lssueId = getIntent().getLongExtra(PAWN_PRODUCT_LSSUE_ID, 0l);
        userId = LoginManager.getLoginInfo(this).ID;

        mImageLoader = ImageLoaderFactory.createImageLoader();

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isFirst = true;
                loadData();
            }
        });

        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this);

        productPicView = (NetworkImageView) findViewById(R.id.product_pic);
        productPriceView = (TextView) findViewById(R.id.product_price_view);
        productTitleView = (TextView) findViewById(R.id.pawn_product_tilte_view);
        pawnPriceView = (TextView) findViewById(R.id.pawn_price_view);
        pawnLeveListView = (ListView) findViewById(R.id.pawn_listview);
        dangButton = (TextView) findViewById(R.id.dang_button);
        dangButton.setOnClickListener(this);
        pawnPriceTipView = (TextView) findViewById(R.id.pawn_price_tip_view);

        mAdapter = new PawnLevelAdapter(this, new ArrayList<PawnLevelVO>(), new PawnLevelAdapter.CheckedListener() {
            @Override
            public void onChecked(PawnLevelVO vo) {
                Spanned s = Html.fromHtml("典當价格:&nbsp;<font color=\"red\">" + ((int)(pawnMsPrice * vo.PawnLevelRatio)) / 100 + "</font>元");
                pawnPriceTipView.setText(s);
            }
        });

        pawnLeveListView.setAdapter(mAdapter);

        loadData();
    }

    private void loadData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }
        PawnManager.fetchPawnLogVO(userId, proId, lssueId).startUI(new ApiCallback<PawnLogVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(PawnLogVO pawnLogVO) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                if (pawnLogVO == null) {
                    return;
                }

                PawnLogInfoVO info = pawnLogVO.pawnLogInfoVO;
                pawnMsPrice = info.ProMSPrice;

                Integer ratio = 0;
                List<PawnLevelVO> levelVOs = pawnLogVO.pawnLevelVOs;
                if (levelVOs != null && levelVOs.size() > 1) {
                    PawnLevelVO d = levelVOs.get(1);
                    ratio = d.PawnLevelRatio;
                    mAdapter.setDefaultChecked(d.PawnLevelID);
                    Spanned s = Html.fromHtml("典當价格:&nbsp;<font color=\"red\">" + ((int)(pawnMsPrice * ratio)) / 100 + "</font>元");
                    pawnPriceTipView.setText(s);
                }


                Spanned s = Html.fromHtml("官方价格:&nbsp;<font color=\"red\">" + info.ProMSPrice + "</font>元");
                productPriceView.setText(s);
                Spanned s2 = Html.fromHtml("典當价格:&nbsp;<font color=\"red\">" + ((int)(info.ProMSPrice * ratio)) / 100 + "</font>元");
                pawnPriceView.setText(s2);
                productTitleView.setText(info.ProTitle);

                productPicView.setDefaultImageResId(R.drawable.tupian);
                productPicView.setImageUrl(info.getProductImage(), mImageLoader);
                mAdapter.addAll(levelVOs, isFirst);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.dang_button:

                dataLoadingDialog.show();
                Long levelId = mAdapter.getCheckedId();

                PawnManager.createPawnLog(userId,proId, lssueId, levelId).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        dataLoadingDialog.dismiss();
                        Toast.makeText(PawnLogActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dataLoadingDialog.dismiss();
                        Toast.makeText(PawnLogActivity.this, "典当成功", Toast.LENGTH_SHORT).show();
                        PawnLogActivity.this.finish();
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });

                break;
            case R.id.title_back:
                this.finish();
                break;
        }
    }
}
