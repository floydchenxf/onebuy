package com.floyd.onebuy.ui;

import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.UserVO;
import com.floyd.onebuy.event.PaySuccessEvent;
import com.floyd.onebuy.event.TabSwitchEvent;
import com.floyd.onebuy.ui.fragment.AllProductFragemnt;
import com.floyd.onebuy.ui.fragment.BackHandledFragment;
import com.floyd.onebuy.ui.fragment.BuyCarFragment;
import com.floyd.onebuy.ui.fragment.FragmentTabAdapter;
import com.floyd.onebuy.ui.fragment.IndexFragment;
import com.floyd.onebuy.ui.fragment.MyFragment;
import com.floyd.onebuy.ui.fragment.NewOwnerFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class MainActivity extends FragmentActivity implements BackHandledInterface, View.OnClickListener {

    public static final String PAY_MODE = "01";
    public static final String PAY_RESULT = "pay_result";
    public static final String RESULT_DATA = "result_data";

    private static final String TAG = "MainActivity";

    public static final String TAB_INDEX = "TAB_INDEX";

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

        EventBus.getDefault().register(this);
        fragments.add(new IndexFragment());
        fragments.add(new AllProductFragemnt());
        fragments.add(new NewOwnerFragment());
        fragments.add(new BuyCarFragment());
        fragments.add(new MyFragment());

        rgs = (RadioGroup) findViewById(R.id.id_ly_bottombar);
        myButton = (RadioButton) findViewById(R.id.tab_my);
        myButton.setOnClickListener(this);

        tabAdapter = new FragmentTabAdapter(this, fragments, R.id.id_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
            }
        });

        tabAdapter.setOnLoginCheck(new FragmentTabAdapter.OnLoginCheck() {
            @Override
            public boolean needLogin(RadioGroup radioGroup, int preCheckedId, int idx) {
                if (idx == 4) {
                    UserVO loginVO = LoginManager.getLoginInfo(MainActivity.this);
                    if (loginVO == null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tabId = intent.getIntExtra(TAB_INDEX, -1);
        if (tabId != -1) {
            rgs.check(tabId);
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

    @TargetApi(11)
    protected void moveToFront() {
        if (Build.VERSION.SDK_INT >= 11) { // honeycomb
            final ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
            final List<ActivityManager.RunningTaskInfo> recentTasks = activityManager.getRunningTasks(Integer.MAX_VALUE);

            for (int i = 0; i < recentTasks.size(); i++) {
                Log.d("Executed app", "Application executed : "
                        + recentTasks.get(i).baseActivity.toShortString()
                        + "\t\t ID: " + recentTasks.get(i).id + "");
                // bring to front
                if (recentTasks.get(i).baseActivity.toShortString().indexOf(MainActivity.class.getName()) > -1) {
                    activityManager.moveTaskToFront(recentTasks.get(i).id, ActivityManager.MOVE_TASK_WITH_HOME);
                }
            }
        }
    }

    @Subscribe
    public void onEventMainThread(TabSwitchEvent event) {
        if (!this.isFinishing()) {
            rgs.check(event.tabId);

            Map<String,Object> data = event.data;
            Object o = data.get(APIConstants.INDEX_PRODUCT_TYPE_ID);
            if (o != null) {
                Long typeId =(Long)o;
                ((AllProductFragemnt)fragments.get(1)).switchType(typeId);
            }
        }
    }


    @Override
    public void setSelectedFragment(BackHandledFragment selectedFragment) {
        this.mBackHandedFragment = selectedFragment;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tab_my:
                boolean isLogin = LoginManager.isLogin(this);
                if (!isLogin) {
                    int currentTab = tabAdapter.getCurrentTab();
                    if (currentTab == 0) {
                        rgs.check(R.id.tab_index_page);
                    } else if (currentTab == 1) {
                        rgs.check(R.id.tab_all_product);
                    } else if (currentTab == 2) {
                        rgs.check(R.id.tab_new_owner);
                    } else if (currentTab == 3) {
                        rgs.check(R.id.tab_buy_car);
                    } else {
                        rgs.check(R.id.tab_my);
                    }
                }

                break;
            default:
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data == null) {
            return;
        }

        String msg = "";
        String str = data.getExtras().getString(PAY_RESULT);
        if (str.equalsIgnoreCase("success")) {
            // 支付成功后，extra中如果存在result_data，取出校验
            // result_data结构见c）result_data参数说明
//            if (data.hasExtra(RESULT_DATA)) {
//                String result = data.getExtras().getString(RESULT_DATA);
//                try {
//                    JSONObject resultJson = new JSONObject(result);
//                    String sign = resultJson.getString("sign");
//                    String dataOrg = resultJson.getString("data");
                    // 验签证书同后台验签证书
                    // 此处的verify，商户需送去商户后台做验签
//                    boolean ret = verify(dataOrg, sign, PAY_MODE);
//                    if (ret) {
//                        msg = "支付成功！";
//                    } else {
//                        msg = "支付失败！";
//                    }
//                } catch (JSONException e) {
//                }
//            } else {
                // 未收到签名信息
                // 建议通过商户后台查询支付结果
//                msg = "支付成功！";
//            }

            EventBus.getDefault().post(new PaySuccessEvent());
            return;
        }

        if (str.equalsIgnoreCase("fail")) {
            msg = "支付失败！";
        } else if (str.equalsIgnoreCase("cancel")) {
            msg = "用户取消了支付";
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("支付结果通知");
        builder.setMessage(msg);
        builder.setInverseBackgroundForced(true);
        // builder.setCustomTitle();
        builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.create().show();
    }

    private boolean verify(String dataOrg, String sign, String payMode) {
        return true;
    }

}
