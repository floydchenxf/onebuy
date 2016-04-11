package com.floyd.onebuy.ui.webview;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.webkit.SslErrorHandler;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by floyd on 16-1-9.
 */
public class DiamondWebViewClient extends WebViewClient {
    private Context mContext;
    private WebViewErrorListener mWebViewErrorListener;
    private WebViewPageCallback mWebViewPageCallback;

    public DiamondWebViewClient(Context context) {
        this.mContext = context;
    }

    @Override
    public void onPageStarted(WebView view, String url, Bitmap favicon) {
        super.onPageStarted(view, url, favicon);
        if (mWebViewPageCallback != null) {
            mWebViewPageCallback.onPageStarted(view, url, favicon);
        }
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        if (mWebViewPageCallback != null) {
            mWebViewPageCallback.onPageFinished(view, url);
        }
    }

    @Override
    public void onReceivedError(WebView view, int errorCode,
                                String description, String failingUrl) {
        super.onReceivedError(view, errorCode, description, failingUrl);
        if (mWebViewErrorListener != null) {
            mWebViewErrorListener.onReceivedError();
        }
    }

    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        return super.shouldOverrideUrlLoading(view, url);
    }

    public void setWebViewErrorListener(
            WebViewErrorListener webViewErrorListener) {
        this.mWebViewErrorListener = webViewErrorListener;
    }

    public void setmWebViewPageCallback(WebViewPageCallback webViewPageCallback) {
        this.mWebViewPageCallback = webViewPageCallback;
    }

    public void onReceivedSslError(final WebView view, final SslErrorHandler handler,
                                   SslError error) {
        super.onReceivedSslError(view, handler, error);
        if (mWebViewErrorListener != null) {
            mWebViewErrorListener.onReceivedError();
        }
    }

    public interface WebViewErrorListener {
        void onReceivedError();
    }

    public interface WebViewPageCallback {
        void onPageStarted(WebView view, String url, Bitmap favicon);

        void onPageFinished(WebView view, String url);
    }
}
