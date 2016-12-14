package com.yyg365.interestbar.ui.webview;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.webkit.ConsoleMessage;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;

public class DiamondWebChromeClient extends WebChromeClient {
    private static final String TAG = "HybridWebChromeClient";
    private static final long MAX_QUOTA = 20 * 1024 * 1024;
    protected Context mContext;

    @Deprecated
    public DiamondWebChromeClient() {
    }

    public DiamondWebChromeClient(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
        Log.i(TAG, "onConsoleMessage:" + consoleMessage.message() + " at " + consoleMessage.sourceId() + ": " + consoleMessage.lineNumber());
        return super.onConsoleMessage(consoleMessage);
    }

    public void onProgressChanged(WebView view, int newProgress) {
        super.onProgressChanged(view, newProgress);
    }

    @TargetApi(5)
    public void onGeolocationPermissionsShowPrompt(String origin, android.webkit.GeolocationPermissions.Callback callback) {
        super.onGeolocationPermissionsShowPrompt(origin, callback);
        callback.invoke(origin, true, false);
    }

    @TargetApi(5)
    public void onExceededDatabaseQuota(String url, String databaseIdentifier,
                                        long quota, long estimatedDatabaseSize, long totalQuota,
                                        WebStorage.QuotaUpdater quotaUpdater) {
        if (estimatedDatabaseSize < MAX_QUOTA) {
            quotaUpdater.updateQuota(estimatedDatabaseSize);
        } else {
            quotaUpdater.updateQuota(quota);
        }
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
        if (mContext instanceof Activity) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
            dlg.setMessage(message);
            dlg.setTitle("提示");
            dlg.setCancelable(true);
            dlg.setPositiveButton(android.R.string.ok,
                    new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            dlg.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    });
            dlg.create();
            dlg.show();
            return true;
        } else {
            return super.onJsAlert(view, url, message, result);
        }
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, final JsResult result) {
        if (mContext instanceof Activity) {
            AlertDialog.Builder dlg = new AlertDialog.Builder(mContext);
            dlg.setMessage(message);
            dlg.setTitle("提示");
            dlg.setCancelable(true);
            dlg.setPositiveButton(android.R.string.ok,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.confirm();
                        }
                    });
            dlg.setNegativeButton(android.R.string.cancel,
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            result.cancel();
                        }
                    });
            dlg.setOnCancelListener(
                    new DialogInterface.OnCancelListener() {
                        public void onCancel(DialogInterface dialog) {
                            result.cancel();
                        }
                    });
            dlg.create();
            dlg.show();
            return true;
        } else {
            return super.onJsConfirm(view, url, message, result);
        }
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue, JsPromptResult result) {
        return false;
    }

}
