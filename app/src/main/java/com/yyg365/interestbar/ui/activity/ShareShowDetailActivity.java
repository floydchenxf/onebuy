package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.tools.DateUtil;
import com.yyg365.interestbar.biz.vo.json.ShareShowDetailVO;
import com.yyg365.interestbar.biz.vo.json.ShowCommentVO;
import com.yyg365.interestbar.ui.DialogCreator;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.ui.adapter.CommentAdapter;
import com.yyg365.interestbar.ui.multiimage.MultiImageActivity;
import com.yyg365.interestbar.ui.multiimage.base.MulitImageVO;
import com.yyg365.interestbar.ui.multiimage.base.PicViewObject;

import java.util.ArrayList;
import java.util.List;

public class ShareShowDetailActivity extends CommonActivity implements View.OnClickListener {
    public static final String GUEST_ID = "share_show_guest_id";

    private long guestId;
    private TextView prizeTimeView;
    private TextView productTitleView;
    private LinearLayout urlLayout;
    private TextView guestTimeView;
    private TextView commentCountView;
    private TextView guestContentView;

    private ImageLoader mImageLoader;
    private CommentAdapter adapter;
    private LinearLayout operateLayout;
    private EditText commentContentView;
    private TextView addCommentButton;
    private Dialog dataLoadingDialog;
    private float oneDp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initData() {
        guestId = getIntent().getLongExtra(GUEST_ID, 0l);
        mImageLoader = ImageLoaderFactory.createImageLoader();
        oneDp = this.getResources().getDimension(R.dimen.one_dp);
        adapter = new CommentAdapter(this, new ArrayList<ShowCommentVO>(), mImageLoader);
    }

    protected void initView() {
        mListView.setDividerHeight(0);
        mListView.setDivider(null);
        dataLoadingDialog = DialogCreator.createDataLoadingDialog(this);
        operateLayout = (LinearLayout) findViewById(R.id.operate_layout);
        View addLayout = View.inflate(this, R.layout.add_comment_layout, null);

        commentContentView = (EditText) addLayout.findViewById(R.id.comment_content_view);
        addCommentButton = (TextView) addLayout.findViewById(R.id.add_comment_button);
        addCommentButton.setOnClickListener(this);
        operateLayout.addView(addLayout);
        operateLayout.setVisibility(View.VISIBLE);
    }

    @Override
    protected String fillTitleName() {
        return "晒单详情";
    }

    protected void initListViewHeader(ListView mListView) {
        View v = View.inflate(this, R.layout.share_show_detail_head, null);
        productTitleView = (TextView) v.findViewById(R.id.product_title_view);
        urlLayout = (LinearLayout) v.findViewById(R.id.url_layout);
        guestTimeView = (TextView) v.findViewById(R.id.guest_time_view);
        guestContentView = (TextView) v.findViewById(R.id.guest_content_view);
        commentCountView = (TextView) v.findViewById(R.id.comment_count_view);
        mListView.addHeaderView(v);
    }

    @Override
    protected void initListView(ListView mListView) {
        mListView.setAdapter(adapter);
    }

    @Override
    protected void loadIndexData() {
        if (isFirst) {
            dataLoadingView.startLoading();
        }

        ProductManager.fetchShowDetail(guestId).startUI(new ApiCallback<ShareShowDetailVO>() {
            @Override
            public void onError(int code, String errorInfo) {
                if (isFirst) {
                    dataLoadingView.loadFail();
                }
            }

            @Override
            public void onSuccess(final ShareShowDetailVO vo) {
                if (isFirst) {
                    dataLoadingView.loadSuccess();
                }

                Spanned cc = Html.fromHtml("共<font color=\"red\">" + vo.CommentNum + "</font>个评论");
                commentCountView.setText(cc);

                productTitleView.setText(vo.GuestTitle);
                String guestTime = DateUtil.getDateTime(vo.GuestTime * 1000);
                guestTimeView.setText(guestTime);
                guestContentView.setText(vo.GuestContent);

                urlLayout.removeAllViews();
                List<String> urls = vo.getMediaUrls();
                final List<PicViewObject> result = new ArrayList<PicViewObject>();
                long idx = 1;
                for (String path : urls) {
                    PicViewObject view = new PicViewObject();
                    view.setPicPreViewUrl(path);
                    view.setPicUrl(path);
                    view.setPicId(idx++);
                    view.setPicType(PicViewObject.IMAGE);
                    result.add(view);
                }
                for (int j =0; j < urls.size(); j++) {
                    final String u = urls.get(j);
                    LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    lp.setMargins(0, (int) (10 * oneDp), 0, 0);

                    NetworkImageView networkImage = new NetworkImageView(ShareShowDetailActivity.this);
                    networkImage.setDefaultImageResId(R.drawable.tupian);
                    networkImage.setImageUrl(u, mImageLoader);
                    networkImage.setScaleType(ImageView.ScaleType.CENTER_CROP);
                    networkImage.setLayoutParams(lp);
                    final int k = j;

                    networkImage.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (vo.isPic()) {
                                MulitImageVO vo = new MulitImageVO(k, result);
                                Intent it = new Intent(ShareShowDetailActivity.this, MultiImageActivity.class);
                                Bundle bundle = new Bundle();
                                bundle.putSerializable(MultiImageActivity.MULIT_IMAGE_VO, vo);
                                it.putExtra(MultiImageActivity.MULIT_IMAGE_VO, bundle);
                                it.putExtra(MultiImageActivity.MULIT_IMAGE_PICK_MODE,
                                        MultiImageActivity.MULIT_IMAGE_PICK_MODE_PREVIEW);
                                startActivity(it);
                            } else {
                                Intent intent = new Intent(Intent.ACTION_VIEW);
                                intent.setDataAndType(Uri.parse(u), "video/mp4");
                                startActivity(intent);
                            }
                        }
                    });

                    urlLayout.addView(networkImage);
                }

                List<ShowCommentVO> sss = vo.ShowComment;
                if (sss == null || sss.isEmpty()) {
                    sss = new ArrayList<ShowCommentVO>();
                    //解决listView没数据导致head不显示问题
                    sss.add(null);
                }

                adapter.addAll(sss, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });

    }

    @Override
    protected void loadNextPageData() {
        ProductManager.fetchShowCommentPage(guestId, pageNo, PAGE_SIZE).startUI(new ApiCallback<List<ShowCommentVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(ShareShowDetailActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<ShowCommentVO> showCommentVOs) {
                adapter.addAll(showCommentVOs, needClear);
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.add_comment_button:
                if (!LoginManager.isLogin(this)) {
                    return;
                }
                Long userId = LoginManager.getLoginInfo(this).ID;
                String comment = commentContentView.getText().toString();
                if (TextUtils.isEmpty(comment)) {
                    Toast.makeText(ShareShowDetailActivity.this, "请输入评论", Toast.LENGTH_SHORT).show();
                    return;
                }
                dataLoadingDialog.show();
                ProductManager.addShowComment(guestId, userId, comment).startUI(new ApiCallback<Boolean>() {
                    @Override
                    public void onError(int code, String errorInfo) {
                        dataLoadingDialog.dismiss();
                        Toast.makeText(ShareShowDetailActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onSuccess(Boolean aBoolean) {
                        dataLoadingDialog.dismiss();
                        if (aBoolean) {
                            commentContentView.setText("");
                            isFirst = false;
                            loadIndexData();
                            Toast.makeText(ShareShowDetailActivity.this, "评论成功", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onProgress(int progress) {

                    }
                });
                break;
        }
    }
}
