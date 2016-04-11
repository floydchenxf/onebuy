package com.floyd.onebuy.ui.fragment;

import android.view.View;

/**
 * Created by floyd on 16-4-12.
 */
public class AllProductFragemnt extends BackHandledFragment implements View.OnClickListener{

    public static AllProductFragemnt newInstance(String param1, String param2) {
        AllProductFragemnt fragment = new AllProductFragemnt();
        return fragment;
    }

    public AllProductFragemnt() {
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }

    @Override
    public void onClick(View v) {

    }
}
