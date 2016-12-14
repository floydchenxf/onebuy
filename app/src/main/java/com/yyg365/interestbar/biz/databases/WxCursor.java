package com.yyg365.interestbar.biz.databases;

import android.database.Cursor;
import android.database.CursorWrapper;

/**
 * 用于检测cursor泄漏
 * @author yuanyi.rss
 *
 */
public class WxCursor extends CursorWrapper {
    private static final String TAG = "WxCursor";
    private boolean mIsClosed = false;
    private Throwable mTrace;
    private Cursor mWrapperCursor;
    
    public WxCursor(Cursor c) {
        super(c);
        this.mWrapperCursor=c;
        mTrace = new Throwable("Explicit termination method 'close()' not called");
    }
    
    @Override
    public void close() {
		if (mWrapperCursor == null) {
			mIsClosed = true;
			return;
		}
    	super.close();
        mIsClosed = true;
    }
    
    @Override
    protected void finalize() throws Throwable {
        try {
            if (mIsClosed != true) {
            }
        } finally {
            super.finalize();
        }
    }
}