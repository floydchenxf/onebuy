package com.yyg365.interestbar.ui;

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
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yyg365.interestbar.aync.ApiCallback;
import com.yyg365.interestbar.biz.constants.APIConstants;
import com.yyg365.interestbar.biz.constants.BuyCarType;
import com.yyg365.interestbar.biz.manager.CarManager;
import com.yyg365.interestbar.biz.manager.LoginManager;
import com.yyg365.interestbar.biz.vo.json.CarItemVO;
import com.yyg365.interestbar.biz.vo.json.CarListVO;
import com.yyg365.interestbar.biz.vo.json.IconAdvVO;
import com.yyg365.interestbar.biz.vo.json.UserVO;
import com.yyg365.interestbar.biz.vo.model.WinningInfo;
import com.yyg365.interestbar.event.BuyCarNumEvent;
import com.yyg365.interestbar.event.PaySuccessEvent;
import com.yyg365.interestbar.event.TabSwitchEvent;
import com.yyg365.interestbar.ui.activity.H5Activity;
import com.yyg365.interestbar.ui.activity.WinningDetailActivity;
import com.yyg365.interestbar.ui.fragment.AllProductFragemnt;
import com.yyg365.interestbar.ui.fragment.BackHandledFragment;
import com.yyg365.interestbar.ui.fragment.BuyCarFragment;
import com.yyg365.interestbar.ui.fragment.FragmentTabAdapter;
import com.yyg365.interestbar.ui.fragment.IndexFragment;
import com.yyg365.interestbar.ui.fragment.MyFragment;
import com.yyg365.interestbar.ui.fragment.NewOwnerFragment;
import com.yyg365.interestbar.ui.fragment.PawnFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import de.greenrobot.event.EventBus;
import de.greenrobot.event.Subscribe;


public class MainActivity extends FragmentActivity implements BackHandledInterface, View.OnClickListener {

    public static final String PAY_MODE = APIConstants.PAY_MODE;
    public static final String PAY_RESULT = "pay_result";
    public static final String RESULT_DATA = "result_data";

    private static final String TAG = "MainActivity";

    public static final String TAB_INDEX = "TAB_INDEX";

    public static final String ADV_OBJECt = "ADV_OBJECT";

    private FragmentTransaction fragmentTransaction;

    private BackHandledFragment mBackHandedFragment;

    private RadioGroup rgs;

    private List<Fragment> fragments = new ArrayList<Fragment>();
    private FragmentTabAdapter tabAdapter;

    private long exitTime = 0;

    private TextView redRotNumView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        IconAdvVO advVO = (IconAdvVO) getIntent().getSerializableExtra(ADV_OBJECt);
        // 广告跳转
        if (advVO != null) {
            long proId = advVO.ProID;
            if (proId > 0) {
                Intent detailIntent = new Intent(this, WinningDetailActivity.class);
                detailIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                detailIntent.putExtra(WinningDetailActivity.PRODUCT_ID, proId);
                startActivity(detailIntent);
            } else {
                if (!TextUtils.isEmpty(advVO.Url)) {
                    Intent detailIntent = new Intent(this, H5Activity.class);
                    H5Activity.H5Data h5Data = new H5Activity.H5Data();
                    h5Data.dataType = H5Activity.H5Data.H5_DATA_TYPE_URL;
                    h5Data.data = advVO.Url;
                    h5Data.showProcess = true;
                    h5Data.showNav = true;
                    h5Data.title = "广告信息";
                    detailIntent.putExtra(H5Activity.H5Data.H5_DATA, h5Data);
                    startActivity(detailIntent);
                }
            }
        }

        EventBus.getDefault().register(this);
        fragments.add(new IndexFragment());
        fragments.add(new AllProductFragemnt());
//        fragments.add(new NewOwnerFragment());
        fragments.add(new PawnFragment());
        fragments.add(new BuyCarFragment());
        fragments.add(new MyFragment());

        rgs = (RadioGroup) findViewById(R.id.id_ly_bottombar);
        findViewById(R.id.tab_my).setOnClickListener(this);
        findViewById(R.id.tab_buy_car).setOnClickListener(this);

        redRotNumView = (TextView) findViewById(R.id.red_dot_view);
        redRotNumView.setVisibility(View.GONE);

        tabAdapter = new FragmentTabAdapter(this, fragments, R.id.id_content, rgs);
        tabAdapter.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
            @Override
            public void OnRgsExtraCheckedChanged(RadioGroup radioGroup, int checkedId, int index) {
            }
        });

        tabAdapter.setOnLoginCheck(new FragmentTabAdapter.OnLoginCheck() {
            @Override
            public boolean needLogin(RadioGroup radioGroup, int preCheckedId, int idx) {
                if (idx == 4 || idx == 3) {
                    UserVO loginVO = LoginManager.getLoginInfo(MainActivity.this);
                    if (loginVO == null) {
                        return true;
                    }
                    return false;
                }
                return false;
            }
        });

        checkRedDot();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        int tabId = intent.getIntExtra(TAB_INDEX, -1);
        if (tabId != -1) {
            if (tabId >= 3 && !LoginManager.isLogin(this)) {
                return;
            }
            rgs.check(tabId);
        }
    }

    private void checkRedDot() {
        UserVO vo = LoginManager.getLoginInfo(this);
        if (vo != null) {
            Long userId = vo.ID;
            CarManager.fetchCarList(BuyCarType.NORMAL, userId).startUI(new ApiCallback<CarListVO>() {
                @Override
                public void onError(int code, String errorInfo) {

                }

                @Override
                public void onSuccess(CarListVO carListVO) {
                    List<CarItemVO> items = carListVO.list;
                    if (items != null && items.size() > 0) {
                        redRotNumView.setVisibility(View.VISIBLE);
                        redRotNumView.setText(items.size()+"");
                    } else {
                        redRotNumView.setVisibility(View.GONE);
                    }

                }

                @Override
                public void onProgress(int progress) {

                }
            });
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
            if (event.tabId >=3 && event.tabId != R.id.tab_all_product && !LoginManager.isLogin(this)) {
                return;
            }

            rgs.check(event.tabId);

            Map<String, Object> data = event.data;
            Object o = data.get(APIConstants.INDEX_PRODUCT_TYPE_ID);
            if (o != null) {
                Long typeId = (Long) o;
                ((AllProductFragemnt) fragments.get(1)).switchType(typeId);
            }
        }
    }

    @Subscribe
    public void onEventMainThread(BuyCarNumEvent event) {
        if (!this.isFinishing()) {
            checkRedDot();
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
            case R.id.tab_buy_car:
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

        if (requestCode == 10) {
            String msg = "";
            String str = data.getExtras().getString(PAY_RESULT);
            if (str.equalsIgnoreCase("success")) {
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

    }

    private boolean verify(String dataOrg, String sign, String payMode) {
        return true;
    }

}
