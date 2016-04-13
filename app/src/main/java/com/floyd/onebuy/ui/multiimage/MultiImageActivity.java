package com.floyd.onebuy.ui.multiimage;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.R;
import com.floyd.onebuy.ui.multiimage.base.MulitImageVO;
import com.floyd.onebuy.ui.multiimage.base.PicViewObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 相册进入的大图预览界面，用于大图模式预览相册照片决定是否发送
 *
 * @author yiqiu.wsh
 *         edit by @author floydchenxf
 *         edit by @author shuheng
 */

public class MultiImageActivity extends FragmentActivity implements ImageDetailFragment.OnImageFragmentListener,
        OnPageChangeListener, OnClickListener {

    public static final String IMAGE_DATA_EXTRA = "extra_image_data";

    public static final String CURRENT_MESSAGE = "current_message";

    public static final String MULIT_IMAGE_VO = "mulit_image_vo";

    public static final String MULIT_IMAGE_PICK_MODE = "mulit_image_pick_mode"; // 多图选择模式
    public static final String MULIT_IMAGE_CHECKED_LIST = "mulit_image_checked_list"; // 多图选择模式下选中的图片
    public static final String MULIT_IMAGE_RESULT_CHECKED_LIST = "mulit_image_result_checked_list"; // 多图选择模式下返回的选中图片
    public static final String MULIT_IMAGE_RESULT_UNCHECKED_LIST = "mulit_image_result_unchecked_list"; // 多图选择模式下返回的取消选中图片
    public static final String MULIT_IMAGE_RESULT_PREVIEW_LIST = "mulit_image_result_preview_list"; // 多图选择删除模式下返回的结果

    public static final String MULIT_IMAGE_SINGLE_TOUCH_BACK = "mulit_image_single_touch_back"; //

    public static final String MULIT_IMAGE_HIDE_TITLE = "mulit_image_hide_title"; //


    public static final String MULIT_IMAGE_TITLE_BUTTON_VISABLE = "mulit_image_title_button_visable";

    public static final String MAX_COUNT = "maxCount"; // 最大可选择数目
    public static final String MAX_TOAST = "max_toast"; // 超过数量上限提示


    public static final int MULIT_IMAGE_PICK_MODE_PREVIEW = 0; // 预览模式
    public static final int MULIT_IMAGE_PICK_MODE_SELECT = 1; // 多图选择模式
    public static final int MULIT_IMAGE_PICK_MODE_DELETE = 2; // 多图删除模式

    public static final int ACTIVITY_REQUEST_CODE = 24;
    public static final int SELECT_PREVIEW_REQUEST_CODE = 25;

    private static final String TITLE_VISIBLE = "title_visible";
    public static final String SEND_ORIGINAL = "send_orginal";


    public static Map<Long, Boolean> mFailImageMap;

    private int mCurrentPage = 0;

    private ImagePagerAdapter mAdapter;
    private ViewPager mPager;
    private TextView mTxtImagePage;
    private View mImagePageLayout;
    private View mImageTitleShadowLayout;
    private ImageView mDefaultImageView;

    private View mSelectMultiImageLayout;
    private View mSelectLayout;
    private ImageView mImageCheck;
    private TextView mSelectedCount;

    private View mDeleteMultiImageLayout;
    private TextView mDeleteMultiImageTextview;
    private TextView mDeleteTitleBack;
    private View mDeleteImageBtn;

    protected List<PicViewObject> mImageViewList = new ArrayList<PicViewObject>();
    private HighDefinitionImageLoader mHighDefinitionImageLoader;
    private int mMode = 0;
    private ArrayList<String> mCheckedList; // 预先选中的图片
    private ArrayList<String> mPreCheckedListCopy; //
    private int mMaxCount; // 最大可选择数目
    private String mMaxToast;
    private boolean mSingleBack = false;
    private boolean mHideTitle = false;


    private boolean titleButtonVisable = true;
    private RelativeLayout mLeftButton;
    private boolean mUseOrignal = false;
//    private ImageView mSendOriginalCheck;
    private TextView mSendOriginal;
    private View mPreview;
    private Button mSelectFinish;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            Toast.makeText(this, R.string.insert_sdcard, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        if (mFailImageMap == null) {
            mFailImageMap = new HashMap<Long, Boolean>();
        }

        setContentView(R.layout.multi_image_player);
        mMode = getIntent().getIntExtra(MULIT_IMAGE_PICK_MODE,
                MULIT_IMAGE_PICK_MODE_PREVIEW);
        mCheckedList = getIntent().getStringArrayListExtra(
                MULIT_IMAGE_CHECKED_LIST);
        titleButtonVisable = getIntent().getBooleanExtra(MULIT_IMAGE_TITLE_BUTTON_VISABLE, true);
        mSingleBack = getIntent().getBooleanExtra(MULIT_IMAGE_SINGLE_TOUCH_BACK, false);
        mHideTitle = getIntent().getBooleanExtra(MULIT_IMAGE_HIDE_TITLE, false);

        init(true);
        mDefaultImageView.setVisibility(View.VISIBLE);

        MulitImageVO mulitImageVO = (MulitImageVO) getIntent().getBundleExtra(MULIT_IMAGE_VO).getSerializable(MULIT_IMAGE_VO);
        mDefaultImageView.setVisibility(View.GONE);
        mCurrentPage = mulitImageVO.getCurrentPage();
        mImageViewList.clear();
        mImageViewList.addAll(mulitImageVO.getPicViewList());

        init(false);

    }

    @SuppressWarnings("deprecation")
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void init(boolean isNeedInitView) {
        if (isNeedInitView) {
            mDefaultImageView = (ImageView) findViewById(R.id.image_detail_default_view);
            mTxtImagePage = (TextView) findViewById(R.id.multi_image_textview);
            mAdapter = new ImagePagerAdapter(getSupportFragmentManager());
            mPager = (ViewPager) findViewById(R.id.multi_image_viewpager);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
                Point outSize = new Point();
                getWindowManager().getDefaultDisplay().getSize(outSize);
                LayoutParams pagerParams = mPager.getLayoutParams();
                if (pagerParams == null) {
                    pagerParams = new LayoutParams(outSize.x, outSize.y);
                } else {
                    pagerParams.width = outSize.x;
                    pagerParams.height = outSize.y;
                }
                mPager.setLayoutParams(pagerParams);
            } else {
                int Width = getWindowManager().getDefaultDisplay().getWidth();
                int Height = getWindowManager().getDefaultDisplay().getHeight();
                LayoutParams pagerParams = mPager.getLayoutParams();
                if (pagerParams == null) {
                    pagerParams = new LayoutParams(Width, Height);
                } else {
                    pagerParams.width = Width;
                    pagerParams.height = Height;
                }
                mPager.setLayoutParams(pagerParams);
            }
            mPager.setAdapter(mAdapter);
            mPager.setPageMargin(50);
            mPager.setOffscreenPageLimit(1);
            mPager.setOnClickListener(this);
            mPager.setOnPageChangeListener(this);

            if (mMode == MULIT_IMAGE_PICK_MODE_PREVIEW) {
                mImagePageLayout = findViewById(R.id.multi_image_textview_layout);
                mImagePageLayout.setVisibility(View.VISIBLE);
                if (mSingleBack) {
                    mImagePageLayout.setVisibility(View.GONE);
                }
                mImageTitleShadowLayout = findViewById(R.id.multi_image_shadow_view_layout);
                findViewById(R.id.title_back).setOnClickListener(this);

                View titleButton = findViewById(R.id.aliwx_title_button);
                if (titleButtonVisable) {
                    titleButton.setVisibility(View.VISIBLE);
                    titleButton.setOnClickListener(this);
                } else {
                    titleButton.setVisibility(View.GONE);
                }

            } else if (mMode == MULIT_IMAGE_PICK_MODE_SELECT) {
                mImagePageLayout = findViewById(R.id.multi_image_textview_layout);
                mImagePageLayout.setVisibility(View.INVISIBLE);
                mSelectMultiImageLayout = findViewById(R.id.select_multi_image_layout);
                mSelectMultiImageLayout.setVisibility(View.VISIBLE);
                mSelectedCount = (TextView) findViewById(R.id.selected_count);
                mSelectFinish=(Button)findViewById(R.id.select_finish);
                mSelectFinish.setOnClickListener(this);
                mSelectLayout = findViewById(R.id.selectLayout);

                initLeftBottomButton();
                mPreview=findViewById(R.id.preview);
                mPreview.setVisibility(View.VISIBLE);
                mSelectLayout.setVisibility(View.VISIBLE);


                mMaxCount = getIntent().getIntExtra(MAX_COUNT, -1);
                mMaxToast = getIntent().getStringExtra(MAX_TOAST);

                findViewById(R.id.select_title_back).setOnClickListener(this);
                mImageCheck = (ImageView) findViewById(R.id.image_check);
                mImageCheck.setOnClickListener(this);
                updateCheckedCount();

                if (mCheckedList != null) {
                    mPreCheckedListCopy = new ArrayList<String>(mCheckedList);
                } else {
                    mPreCheckedListCopy = new ArrayList<String>();
                }
            } else if (mMode == MULIT_IMAGE_PICK_MODE_DELETE) {
                mDeleteMultiImageLayout = findViewById(R.id.delete_multi_image_layout);
                mDeleteMultiImageLayout.setVisibility(View.VISIBLE);
                mDeleteMultiImageTextview = (TextView) findViewById(R.id.delete_multi_image_textview);
                mDeleteTitleBack = (TextView) findViewById(R.id.delete_title_back);
                mDeleteTitleBack.setOnClickListener(this);
                mDeleteImageBtn = findViewById(R.id.delete_image_btn);
                mDeleteImageBtn.setOnClickListener(this);
            }
            mHighDefinitionImageLoader = HighDefinitionImageLoader
                    .getInstance();
        }
        if (mImageViewList.isEmpty()) {
            mTxtImagePage.setText(String
                    .format(getResources().getString(
                            R.string.aliwx_multi_image_brower), 0, 0));
        } else {
            mTxtImagePage.setText(String.format(
                    getResources().getString(R.string.aliwx_multi_delete_image_title),
                    mCurrentPage + 1, mImageViewList.size()));
        }
        if (!mImageViewList.isEmpty()) {
            ImageDetailFragment.setCurrentImageId(mImageViewList.get(
                    mCurrentPage).getPicId());
        }
        mPager.setCurrentItem(mCurrentPage);
        mAdapter.notifyDataSetChanged();
        if (!isNeedInitView) {
            updateCheckedState();
        }

        if (!isNeedInitView && mMode == MULIT_IMAGE_PICK_MODE_DELETE) {
            mDeleteMultiImageTextview.setText(String.format(
                    getResources().getString(R.string.aliwx_multi_delete_image_title),
                    mCurrentPage + 1, mImageViewList.size()));
        }

    }

    private void updateCheckedCount(){
        int size = mCheckedList.size();
        if (size > 0) {
            String sentText = new StringBuilder("发送").append("(").append(size).append(")").toString();
            mSelectFinish.setText(sentText);
        } else {
            String sentText = new StringBuilder("发送").toString();
            mSelectFinish.setText(sentText);
        }
    }

    private void initLeftBottomButton() {
//        mLeftButton = (RelativeLayout) findViewById(R.id.left_button);
//        mLeftButton.setVisibility(View.VISIBLE);
//        mLeftButton.setOnClickListener(this);
//        mSendOriginalCheck = (ImageView) findViewById(R.id.send_original_check);
//        mSendOriginal = (TextView) findViewById(R.id.send_original);
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
//		ImageDetailFragment.clearInstanceList();
        if (mHighDefinitionImageLoader != null) {
            mHighDefinitionImageLoader.recycle();
        }
        if (mImageViewList != null) {
            mImageViewList.clear();
        }
        super.onDestroy();
    }

    /**
     * The main adapter that backs the ViewPager. A subclass of
     * FragmentStatePagerAdapter as there could be a large number of items in
     * the ViewPager and we don't want to retain them all in memory at once but
     * create/destroy them on the fly.
     */

    private class ImagePagerAdapter extends FragmentStatePagerAdapter {

        public ImagePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public int getCount() {
            return mImageViewList.size();
        }

        @Override
        public Fragment getItem(int position) {
            PicViewObject picView = mImageViewList.get(position);
            ImageDetailFragment flagement = ImageDetailFragment.newInstance(
                    picView, mHighDefinitionImageLoader);
            if (mCurrentPage == position) {
                checkAndUpdateSendOrignalState();
            }
            return flagement;
        }

        @Override
        public int getItemPosition(Object object) {
            if (mMode == MULIT_IMAGE_PICK_MODE_DELETE) {
                return PagerAdapter.POSITION_NONE;
            } else {
                return PagerAdapter.POSITION_UNCHANGED;
            }
        }
    }


    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {

        if (mCurrentPage != arg0) {
            if (mMode==MULIT_IMAGE_PICK_MODE_PREVIEW) {
                //mImagePageLayout.setVisibility(View.GONE);
                mImageTitleShadowLayout.setVisibility(View.GONE);
            }
        }

        mCurrentPage = arg0;
        if (mMode == MULIT_IMAGE_PICK_MODE_PREVIEW) {
            mTxtImagePage.setText(String.format(
                    getResources().getString(R.string.aliwx_multi_image_brower),
                    mCurrentPage + 1, mImageViewList.size()));
        } else if (mMode == MULIT_IMAGE_PICK_MODE_SELECT) {
            updateCheckedState();
        }
        else if(mMode == MULIT_IMAGE_PICK_MODE_DELETE){
            mDeleteMultiImageTextview.setText(String.format(
                    getResources().getString(R.string.aliwx_multi_delete_image_title),
                    mCurrentPage + 1, mImageViewList.size()));
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.title_back
                || id == R.id.select_title_back) {
            if (mMode == MULIT_IMAGE_PICK_MODE_SELECT) {
                Intent intent = getSelectResultIntent();
                setResult(RESULT_CANCELED, intent);
                finish();
            } else {
                finish();
            }
        } else if (id == R.id.aliwx_title_button) {
            if (!mImageViewList.isEmpty() && mCurrentPage < mImageViewList.size()) {
                Fragment instance = (Fragment) mAdapter.instantiateItem(mPager, mCurrentPage);
                if (instance != null) {
                    ((ImageDetailFragment) instance).saveImage();
                }
            }
        } else if (id == R.id.image_check){
            PicViewObject picViewObject = mImageViewList.get(mCurrentPage);
            String urlString = picViewObject.getPicUrl();
            if (mCheckedList != null && mCheckedList.contains(urlString)) {
                mCheckedList.remove(urlString);
                mImageCheck.setImageResource(R.drawable.aliwx_picture_unselect_titlebar);
                ;
            } else if (mCheckedList != null
                    && !mCheckedList.contains(urlString)) {
                int size = mCheckedList.size();
                if (size >= mMaxCount) {
                    return;
                }
                mCheckedList.add(urlString);
                mImageCheck.setImageResource(R.drawable.aliwx_picture_select_titlebar);

            }
//            updateCheckedCount();
            checkAndUpdateSendOrignalState();
        } else if (id == R.id.select_finish) {
            Intent intent = getSelectResultIntent();
            setResult(RESULT_OK, intent);
            finish();
        } else if (id == R.id.delete_image_btn) {
            boolean needRemove = false;
            int c = 0;
            if (mCurrentPage > 0) {
                c = mCurrentPage - 1;
                needRemove = true;
            } else if (mCurrentPage == 0 && mImageViewList.size() > 1) {
                c = 0;
                needRemove = true;
            } else {
                mImageViewList.remove(mCurrentPage);
                setDeleteResult();
                return;
            }
            if (mMode == MULIT_IMAGE_PICK_MODE_DELETE
                    && mImageViewList.size() > 1) {
                mDeleteMultiImageTextview.setText(String.format(getResources()
                                .getString(R.string.aliwx_multi_delete_image_title),
                        c + 1, needRemove ? mImageViewList.size() - 1 : mImageViewList.size()));
            }
            if (needRemove) {
                mImageViewList.remove(mCurrentPage);
                mAdapter.notifyDataSetChanged();
                mPager.setCurrentItem(c);
            }
        } else if (id == R.id.delete_title_back) {
            setDeleteResult();
//        } else if (id == R.id.left_button) {
//            changeSendOrignalState();
        } else {
        }
    }

    /**
     * 切换［原图］按钮开启状态
     */
//    private void changeSendOrignalState() {
//        if (mLeftButton.getVisibility() == View.VISIBLE && !mUseOrignal) {
//            mSendOriginal.setText(getCurrentTotalPicSize());
//            mSendOriginal.setTextColor(R.color.aliwx_color_blue);
//            mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_on);
//            mUseOrignal = true;
//        } else if (mLeftButton.getVisibility() == View.VISIBLE && mUseOrignal) {
//            mSendOriginal.setText(getCurrentTotalPicSize());
//            mSendOriginal.setTextColor(R.color.aliwx_color_gray_02);
//            mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_off);
//            mUseOrignal = false;
//        }
//    }

    /**
     * 更新［原图］按钮状态
     */
    private void checkAndUpdateSendOrignalState() {
//        if (mLeftButton.getVisibility() == View.VISIBLE && mUseOrignal) {
//            mSendOriginal.setText(getCurrentTotalPicSize());
//            mSendOriginal.setTextColor(R.color.aliwx_color_blue);
//            mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_on);
//        } else if (mLeftButton.getVisibility() == View.VISIBLE && !mUseOrignal) {
//            mSendOriginal.setText(getCurrentTotalPicSize());
//            mSendOriginal.setTextColor(R.color.aliwx_color_gray_02);
//            mSendOriginalCheck.setImageResource(R.drawable.aliwx_send_original_btn_off);
//        }
    }

    //FIXME shuheng 获取所有图片的大小
//    private String getCurrentTotalPicSize() {
//        long totalFileSize = 0;
//        if (mCheckedList != null)
//            for (String picLocalPath : mCheckedList) {
//                if (picLocalPath != null) {
//                    File f = new File(picLocalPath);
//                    if (f.exists() && f.isFile()) {
//                        totalFileSize += f.length();
//                    }
//                }
//
//            }
//        if (totalFileSize > 0) {
//            StringBuilder sendOriginalText = new StringBuilder(R.string.aliwx_send_original).append("(共").append(Util.bytes2KOrM(totalFileSize)).append(")");
//            return sendOriginalText.toString();
//        }
//        return new StringBuilder(ResourceLoader.getStringByName("aliwx_send_original")).toString();
//    }


    public void onSingleTouch() {
        if (mMode == MULIT_IMAGE_PICK_MODE_PREVIEW) {
            if (mSingleBack) {
                finish();
                return;
            }

            if (mImagePageLayout.getVisibility() != View.VISIBLE) {
                mImagePageLayout.setVisibility(View.VISIBLE);
                mImageTitleShadowLayout.setVisibility(View.VISIBLE);
            } else {
                mImagePageLayout.setVisibility(View.GONE);
                mImageTitleShadowLayout.setVisibility(View.GONE);
            }
        } else if (mMode == MULIT_IMAGE_PICK_MODE_SELECT) {
            if (mSelectMultiImageLayout.getVisibility() != View.VISIBLE) {
                mSelectMultiImageLayout.setVisibility(View.VISIBLE);
                mSelectLayout.setVisibility(View.VISIBLE);
            } else {
                mSelectMultiImageLayout.setVisibility(View.GONE);
                mSelectLayout.setVisibility(View.GONE);
            }
        } else if (mMode == MULIT_IMAGE_PICK_MODE_DELETE) {
            if (mDeleteMultiImageLayout.getVisibility() != View.VISIBLE) {
                mDeleteMultiImageLayout.setVisibility(View.VISIBLE);
            } else {
                mDeleteMultiImageLayout.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onDialogClick() {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState == null) {
            outState = new Bundle();
        }
        if (mMode == MULIT_IMAGE_PICK_MODE_PREVIEW) {
            if (mImageViewList != null && mImageViewList.size() > mCurrentPage) {
                outState.putSerializable(CURRENT_MESSAGE,
                        mImageViewList.get(mCurrentPage));
                if (mImagePageLayout.getVisibility() != View.VISIBLE) {
                    outState.putBoolean(TITLE_VISIBLE, false);
                } else {
                    outState.putBoolean(TITLE_VISIBLE, true);
                }
            }
        }
        super.onSaveInstanceState(outState);
    }

//    private void updateCheckedCount() {
//        int size = mCheckedList.size();
//        if (size > 0) {
//            String sentText = new StringBuilder(ResourceLoader.getStringByName("aliwx_send")).append("(").append(size).append(")").toString();
//            mSelectFinish.setText(sentText);
//        } else {
//            String sentText = new StringBuilder(ResourceLoader.getStringByName("aliwx_send")).toString();
//            mSelectFinish.setText(sentText);
//        }
//    }

    private void updateCheckedState() {
        if (mMode == MULIT_IMAGE_PICK_MODE_SELECT
                && mCurrentPage < mImageViewList.size() && mCurrentPage >= 0) {
            PicViewObject picViewObject = mImageViewList.get(mCurrentPage);
            String urlString = picViewObject.getPicUrl();
            if (mCheckedList != null && mCheckedList.contains(urlString)) {
                mImageCheck.setImageResource(R.drawable.aliwx_picture_select_titlebar);
            } else {
                mImageCheck.setImageResource(R.drawable.aliwx_picture_unselect_titlebar);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == SELECT_PREVIEW_REQUEST_CODE) {

            }
        }
    }

    private Intent getSelectResultIntent() {
        Intent intent = new Intent();
        ArrayList<String> intersectCheckedList = new ArrayList<String>(
                mPreCheckedListCopy);
        intersectCheckedList.retainAll(mCheckedList);
        mPreCheckedListCopy.removeAll(intersectCheckedList);
        mCheckedList.removeAll(intersectCheckedList);
        intent.putStringArrayListExtra(MULIT_IMAGE_RESULT_CHECKED_LIST,
                mCheckedList);
        intent.putStringArrayListExtra(
                MULIT_IMAGE_RESULT_UNCHECKED_LIST, mPreCheckedListCopy);
        intent.putExtra(SEND_ORIGINAL, mUseOrignal);
        return intent;
    }

    @Override
    public void onBackPressed() {
        if (mMode == MULIT_IMAGE_PICK_MODE_SELECT) {
            Intent intent = getSelectResultIntent();
            setResult(RESULT_CANCELED, intent);
            finish();
        } else if (mMode == MULIT_IMAGE_PICK_MODE_DELETE) {
            setDeleteResult();
        } else {
            super.onBackPressed();
        }
    }

    private void setDeleteResult() {
        Intent intent = new Intent();
        ArrayList<String> urlList = new ArrayList<String>();
        for (PicViewObject picViewObject : mImageViewList) {
            urlList.add(picViewObject.getPicUrl());
        }
        intent.putStringArrayListExtra(MULIT_IMAGE_RESULT_PREVIEW_LIST, urlList);
        setResult(RESULT_OK, intent);
        finish();
    }


}
