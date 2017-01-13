package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.JiFengManager;
import com.yyg365.interestbar.biz.vo.json.JFGoodsDetailVO;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.loading.DataLoadingView;
import com.yyg365.interestbar.ui.loading.DefaultDataLoadingView;

public class JFDetailGoodsActivity extends Activity {

    public static final String JF_DETAIL_GOODS_ID = "JF_DETAIL_GOODS_ID";

    private Long id;

    private DataLoadingView dataLoadingView;
    private ImageLoader mImageLoader;

    private NetworkImageView productPicView;
    private TextView productTitleView;
    private WebView productInfoWebView;

    private TextView jfView;
    private TextView duihuanButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_jfdetail);
        id = getIntent().getLongExtra(JF_DETAIL_GOODS_ID, 0l);

        mImageLoader = ImageLoaderFactory.createImageLoader();

        TextView titleNameView = (TextView) findViewById(R.id.title_name);
        titleNameView.setText("商品详情");
        titleNameView.setVisibility(View.VISIBLE);

        findViewById(R.id.title_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JFDetailGoodsActivity.this.finish();
            }
        });

        dataLoadingView = new DefaultDataLoadingView();
        dataLoadingView.initView(findViewById(R.id.act_lsloading), new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadData();
            }
        });

        productPicView = (NetworkImageView) findViewById(R.id.product_pic_view);
        productTitleView = (TextView) findViewById(R.id.product_title_view);
        productInfoWebView = (WebView) findViewById(R.id.jf_goods_webview);
        productInfoWebView.setHorizontalScrollBarEnabled(false);
        productInfoWebView.setHorizontalScrollbarOverlay(false);
        productInfoWebView.setVerticalScrollBarEnabled(false);
        productInfoWebView.setVerticalScrollbarOverlay(false);
        productInfoWebView.setScrollbarFadingEnabled(false);
        productInfoWebView.getSettings().setBuiltInZoomControls(false);

        productInfoWebView.setWebViewClient(new WebViewClient() {
            public void onPageFinished(WebView view, String url) {
                dataLoadingView.loadSuccess();
            }
        });

        jfView = (TextView) findViewById(R.id.jf_view);
        duihuanButton = (TextView) findViewById(R.id.duihuan_button);

        duihuanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO 兑换
            }
        });

        loadData();
    }

    private void loadData() {
        dataLoadingView.startLoading();
        JiFengManager.fetchJFGoodsDetail(id).startUI(new ApiCallback<JFGoodsDetailVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                dataLoadingView.loadFail();
            }

            @Override
            public void onSuccess(JFGoodsDetailVO jfGoodsDetailVO) {
                productInfoWebView.loadUrl(jfGoodsDetailVO.getContentUrl());
                productTitleView.setText(jfGoodsDetailVO.Name);
                productPicView.setImageUrl(jfGoodsDetailVO.getFirstPicUrl(), mImageLoader);
                jfView.setText(jfGoodsDetailVO.JiFen+"");
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }
}
