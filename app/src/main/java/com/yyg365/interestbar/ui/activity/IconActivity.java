package com.yyg365.interestbar.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.toolbox.BitmapProcessor;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.yyg365.interestbar.IMChannel;
import com.yyg365.interestbar.IMImageCache;
import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.EnvConstants;
import com.yyg365.interestbar.biz.manager.BitmapDownloadFactory;
import com.yyg365.interestbar.biz.manager.FileUploadManager;
import com.yyg365.interestbar.biz.manager.ProductManager;
import com.yyg365.interestbar.biz.tools.FileTools;
import com.yyg365.interestbar.biz.vo.json.IconAdvVO;
import com.yyg365.interestbar.channel.request.HttpMethod;
import com.yyg365.interestbar.channel.threadpool.WxDefaultExecutor;
import com.yyg365.interestbar.ui.ImageLoaderFactory;
import com.yyg365.interestbar.ui.MainActivity;
import com.yyg365.interestbar.ui.R;
import com.yyg365.interestbar.utils.WXUtil;

import java.io.File;
import java.util.HashMap;

public class IconActivity extends Activity implements View.OnClickListener {

    private TextView skipView;
    private ImageView advPicView;
    private IMImageCache wxImageCache;
    private boolean isGoing;


    private Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_icon);
        wxImageCache = IMImageCache.findOrCreateCache(
                IMChannel.getApplication(), EnvConstants.imageRootPath);
        skipView = (TextView) findViewById(R.id.skip_view);
        advPicView = (ImageView) findViewById(R.id.adv_pic_view);
        skipView.setOnClickListener(this);

        loadData();

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!isGoing) {
                    isGoing = true;
                    Intent it = new Intent(IconActivity.this, MainActivity.class);
                    startActivity(it);
                    IconActivity.this.finish();
                }
            }
        }, 5000);

    }

    private void loadData() {
        ProductManager.getAdv().startUI(new ApiCallback<IconAdvVO>() {
            @Override
            public void onError(int code, String errorInfo) {

            }

            @Override
            public void onSuccess(final IconAdvVO iconAdvVO) {
                final String url = iconAdvVO.getPic();
                Bitmap b = wxImageCache.getBitmap(url);
                if (b != null) {
                    advPicView.setImageBitmap(b);
                    advPicView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            setClickEvent(iconAdvVO);
                        }
                    });
                } else {
                    BitmapDownloadFactory.getImage(url, new HashMap<String, String>(), HttpMethod.GET).startUI(new ApiCallback<Bitmap>() {
                        @Override
                        public void onError(int code, String errorInfo) {

                        }

                        @Override
                        public void onSuccess(final Bitmap bitmap) {
                            final String md5Name = WXUtil.getMD5Value(url);
                            WxDefaultExecutor.getInstance().submitHighPriority(new Runnable() {
                                @Override
                                public void run() {
                                    FileTools.writeBitmap(EnvConstants.imageRootPath + File.separator + md5Name, bitmap, 100);
                                }
                            });
                            advPicView.setImageBitmap(bitmap);
                            advPicView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    setClickEvent(iconAdvVO);
                                }
                            });
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                }
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }

    private void setClickEvent(IconAdvVO iconAdvVO) {
        if (!isGoing) {
            isGoing = true;
            Intent it = new Intent(IconActivity.this, MainActivity.class);
            it.putExtra(MainActivity.ADV_OBJECt, iconAdvVO);
            startActivity(it);
            this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.skip_view:
                if (!isGoing) {
                    isGoing = true;
                    Intent it = new Intent(IconActivity.this, MainActivity.class);
                    startActivity(it);
                    this.finish();
                }
                break;
        }
    }
}
