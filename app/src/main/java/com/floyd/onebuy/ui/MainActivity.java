package com.floyd.onebuy.ui;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.floyd.onebuy.R;
import com.floyd.onebuy.ui.fragment.AllProductFragemnt;
import com.floyd.onebuy.ui.fragment.BackHandledFragment;
import com.floyd.onebuy.ui.fragment.BuyCarFragment;
import com.floyd.onebuy.ui.fragment.FragmentTabAdapter;
import com.floyd.onebuy.ui.fragment.IndexFragment;
import com.floyd.onebuy.ui.fragment.MyFragment;
import com.floyd.onebuy.ui.fragment.NewOwnerFragment;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends FragmentActivity implements View.OnClickListener, BackHandledInterface {

    private static final String TAG = "MainActivity";

    private FragmentTransaction fragmentTransaction;

    private BackHandledFragment mBackHandedFragment;

    private RadioGroup rgs;

    private RadioButton myButton;
    private List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentTabAdapter tabAdapter;

    private long exitTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragments.add(new IndexFragment());
        fragments.add(new AllProductFragemnt());
        fragments.add(new NewOwnerFragment());
        fragments.add(new BuyCarFragment());
        fragments.add(new MyFragment());

        myButton = (RadioButton) findViewById(R.id.tab_my);
        myButton.setOnClickListener(this);

        rgs = (RadioGroup) findViewById(R.id.id_ly_bottombar);

        tabAdapter = new FragmentTabAdapter(this, fragments, R.id.id_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
            }
        });

        tabAdapter.setOnLoginCheck(new FragmentTabAdapter.OnLoginCheck() {
            @Override
            public boolean needLogin(RadioGroup radioGroup, int preCheckedId, int idx) {
//                if (idx == 2) {
//                    LoginVO loginVO = LoginManager.getLoginInfo(MainActivity.this);
//                    if (loginVO == null) {
//                        return true;
//                    }
//                    return false;
//                }
                return false;
            }
        });

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_my:
                int currentTab = tabAdapter.getCurrentTab();
                if (currentTab == 0) {
                    rgs.check(R.id.tab_index_page);
                } else if (currentTab == 1) {
                    rgs.check(R.id.tab_all_product);
                } else if (currentTab == 2){
                    rgs.check(R.id.tab_new_owner);
                } else if (currentTab == 3) {
                    rgs.check(R.id.tab_buy_car);
                } else {
                    rgs.check(R.id.tab_my);
                }

                break;
            default:
        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (mBackHandedFragment == null || !mBackHandedFragment.onBackPressed()) {
                if (getSupportFragmentManager().getBackStackEntryCount() != 0) {
                    getSupportFragmentManager().popBackStack();
                }
            }
            exit();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    public void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            View toastRoot = getLayoutInflater().inflate(R.layout.toast_layout, null);
            Toast toast = new Toast(MainActivity.this);
            toast.setView(toastRoot);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
            exitTime = System.currentTimeMillis();
        } else {
            finish();
            System.exit(0);
        }
    }


    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
