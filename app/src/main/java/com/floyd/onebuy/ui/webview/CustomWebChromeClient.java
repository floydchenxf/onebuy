
package com.floyd.onebuy.ui.webview;

import android.view.View;
import android.webkit.WebView;
import android.widget.ProgressBar;

public class CustomWebChromeClient extends DiamondWebChromeClient {
	private ProgressBar progressbar;
	public CustomWebChromeClient(ProgressBar progressbar){
		this.progressbar = progressbar;
	}
    @Override
    public void onProgressChanged(WebView view, int newProgress) {
    	progressbar.setProgress(newProgress);  
        if(newProgress==100){  
        	progressbar.setVisibility(View.GONE);
        }  
        super.onProgressChanged(view, newProgress);  
    }  

}

