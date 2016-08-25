package com.floyd.onebuy.ui.fragment;

import android.support.v4.app.Fragment;

import java.util.Map;

/**
 * Created by floyd on 16-8-5.
 */
public abstract  class CommonwealBaseFragment extends Fragment {

    protected static final String USER_ID = "USER_ID";

    protected SwitchTabListener listener;

    public void initSwitchTabListener(SwitchTabListener listener) {
        this.listener = listener;
    }

    public void doSwitchCall() {
        if (this.listener != null) {
            Map<String, Object> datas = getSwitchData();
            this.listener.onCallback(datas);
        }
    }

    abstract Map<String,Object> getSwitchData();

    public static interface SwitchTabListener {
        public void onCallback(Map<String, Object> data);
    }
}
