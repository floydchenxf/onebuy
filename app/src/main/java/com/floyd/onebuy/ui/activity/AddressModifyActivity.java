package com.floyd.onebuy.ui.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.manager.AddressManager;
import com.floyd.onebuy.biz.manager.LoginManager;
import com.floyd.onebuy.biz.vo.json.AddressVO;
import com.floyd.onebuy.biz.vo.json.GoodsAddressVO;
import com.floyd.onebuy.event.AddressModifiedEvent;
import com.floyd.onebuy.ui.R;
import com.floyd.onebuy.ui.adapter.AddressAdapter;
import com.floyd.onebuy.view.MyPopupWindow;
import com.floyd.onebuy.view.UIAlertDialog;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AddressModifyActivity extends Activity implements View.OnClickListener {

    private EditText linkNameEditView;
    private EditText mobileEditView;
    private TextView provinceCityView;
    private EditText addressDetailEditText;
    private View defaultAddressLayout;
    private RadioButton defaultButton;
    private TextView saveButton;
    private TextView deleteButton;
    private View deleteLine;
    private MyPopupWindow popupWindow;

    private CheckedTextView popProvinceView;
    private CheckedTextView popCityView;
    private CheckedTextView popTownView;

    private ListView popAddressListView;
    private AddressAdapter addressAdapter;
    private boolean canClicked = true;
    private List<AddressVO> chooseAddresses;
    private CheckedTextView[] checkedViews;

    private String provinceName;
    private long provinceId;
    private String cityName;
    private long cityId;
    private String townName;
    private long townId;
    private GoodsAddressVO mGoodsAddressVO;
    private boolean isModified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_modify);
        TextView titleName = (TextView) findViewById(R.id.title_name);
        titleName.setText("添加收货地址");
        titleName.setVisibility(View.VISIBLE);
        findViewById(R.id.title_back).setOnClickListener(this);
        linkNameEditView = (EditText) findViewById(R.id.linkname_view);
        mobileEditView = (EditText) findViewById(R.id.mobile_view);
        provinceCityView = (TextView) findViewById(R.id.province_city_view);
        addressDetailEditText = (EditText) findViewById(R.id.address_detail_view);
        defaultAddressLayout = findViewById(R.id.default_address_layout);
        defaultButton = (RadioButton) findViewById(R.id.default_address_button);
        deleteButton = (TextView) findViewById(R.id.delete_button);
        deleteLine = findViewById(R.id.delete_line);
        saveButton = (TextView) findViewById(R.id.save_addess_button);

        provinceCityView.setOnClickListener(this);
        defaultAddressLayout.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        chooseAddresses = new LinkedList<AddressVO>();

        mGoodsAddressVO = getIntent().getParcelableExtra(APIConstants.ADDRESS_MANAGEMENT_VO);
        isModified = mGoodsAddressVO != null;
        if (isModified) {
            provinceId = mGoodsAddressVO.provinceId;
            cityId = mGoodsAddressVO.cityId;
            townId = mGoodsAddressVO.areaId;
            provinceName = mGoodsAddressVO.province;
            cityName = mGoodsAddressVO.city;
            townName = mGoodsAddressVO.area;

            AddressVO provinceVO = new AddressVO();
            provinceVO.CodeName = mGoodsAddressVO.province;
            provinceVO.CodeID = mGoodsAddressVO.provinceId;
            provinceVO.pid = 0l;

            AddressVO cityVO = new AddressVO();
            cityVO.CodeName = mGoodsAddressVO.city;
            cityVO.CodeID = mGoodsAddressVO.cityId;
            cityVO.pid = mGoodsAddressVO.provinceId;

            AddressVO townVO = new AddressVO();
            townVO.CodeName = mGoodsAddressVO.area;
            townVO.CodeID = mGoodsAddressVO.areaId;
            townVO.pid = mGoodsAddressVO.cityId;

            chooseAddresses.add(provinceVO);
            chooseAddresses.add(cityVO);
            chooseAddresses.add(townVO);

            linkNameEditView.setText(mGoodsAddressVO.linkName);
            mobileEditView.setText(mGoodsAddressVO.mobile);
            addressDetailEditText.setText(mGoodsAddressVO.detailAdr);
            provinceCityView.setText(provinceName + " " + cityName + " " + townName);
            if (mGoodsAddressVO.isDefault > 0) {
                defaultButton.setChecked(true);
            } else {
                defaultButton.setChecked(false);
            }

            deleteButton.setVisibility(View.VISIBLE);
            deleteLine.setVisibility(View.VISIBLE);
            deleteButton.setOnClickListener(this);
        } else {
            deleteButton.setVisibility(View.GONE);
            deleteLine.setVisibility(View.GONE);
            deleteButton.setOnClickListener(null);
        }

        popupWindow = new MyPopupWindow(this);
        popupWindow.initView(R.layout.address_pop, new MyPopupWindow.ViewInit() {
            @Override
            public void initView(View v) {
                popProvinceView = (CheckedTextView) v.findViewById(R.id.pop_province_view);
                popProvinceView.setOnClickListener(AddressModifyActivity.this);
                popCityView = (CheckedTextView) v.findViewById(R.id.pop_city_view);
                popCityView.setOnClickListener(AddressModifyActivity.this);
                popTownView = (CheckedTextView) v.findViewById(R.id.pop_town_view);
                popTownView.setOnClickListener(AddressModifyActivity.this);
                checkedViews = new CheckedTextView[]{popProvinceView, popCityView, popTownView};

                popAddressListView = (ListView) v.findViewById(R.id.pop_address_list);
                addressAdapter = new AddressAdapter(AddressModifyActivity.this, new ArrayList<AddressVO>());
                popAddressListView.setAdapter(addressAdapter);

                popAddressListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if (canClicked) {
                            canClicked = false;
                            long codeId = 0l;
                            AddressVO addressVO = addressAdapter.getRecords().get(position);
                            if (chooseAddresses.size() < 3) {
                                chooseAddresses.add(addressVO);
                            }

                            if (chooseAddresses.size() >= 3) {
                                chooseAddresses.set(chooseAddresses.size() - 1, addressVO);
                                StringBuilder addressStr = new StringBuilder();
                                for (int i = 0; i < 3; i++) {
                                    AddressVO a = chooseAddresses.get(i);
                                    if (i == 0) {
                                        provinceName = a.CodeName;
                                        provinceId = a.CodeID;
                                    } else if (i == 1) {
                                        cityId = a.CodeID;
                                        cityName = a.CodeName;
                                    } else {
                                        townId = a.CodeID;
                                        townName = a.CodeName;
                                    }
                                    addressStr.append(a.CodeName).append(" ");
                                }
                                provinceCityView.setText(addressStr.toString().substring(0, addressStr.toString().length() - 1));
                                popupWindow.hidePopUpWindow();
                                return;
                            }

                            for (int i = 0; i < checkedViews.length; i++) {
                                checkedViews[i].setVisibility(View.GONE);
                            }

                            for (int i = 0; i < chooseAddresses.size(); i++) {
                                checkedViews[i].setChecked(false);
                                checkedViews[i].setVisibility(View.VISIBLE);
                                checkedViews[i].setText(chooseAddresses.get(i).CodeName);
                                codeId = chooseAddresses.get(i).CodeID;
                            }

                            if (chooseAddresses.size() < 3) {
                                checkedViews[chooseAddresses.size()].setChecked(true);
                                checkedViews[chooseAddresses.size()].setVisibility(View.VISIBLE);
                                checkedViews[chooseAddresses.size()].setText("请选择");
                            } else {
                                checkedViews[chooseAddresses.size() - 1].setChecked(true);
                            }

                            AddressManager.getLocation(codeId).startUI(new ApiCallback<List<AddressVO>>() {
                                @Override
                                public void onError(int code, String errorInfo) {
                                    popupWindow.hidePopUpWindow();
                                    chooseAddresses.clear();
                                    Toast.makeText(AddressModifyActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onSuccess(List<AddressVO> addressVOs) {
                                    addressAdapter.addAll(addressVOs, true);
                                    canClicked = true;
                                    popAddressListView.setSelection(0);
                                }

                                @Override
                                public void onProgress(int progress) {

                                }
                            });
                        }
                    }
                });
            }
        });
    }

    private void showDeleteLayout() {
        deleteButton.setVisibility(View.VISIBLE);
        deleteLine.setVisibility(View.VISIBLE);
    }

    private void hiddenDeleteLayout() {
        deleteButton.setVisibility(View.GONE);
        deleteLine.setVisibility(View.GONE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.title_back:
                this.finish();
                break;
            case R.id.default_address_layout:
                boolean isChecked = defaultButton.isChecked();
                defaultButton.setChecked(!isChecked);
                break;
            case R.id.delete_button:
                UIAlertDialog.Builder clearBuilder = new UIAlertDialog.Builder(this);
                SpannableString message = new SpannableString("亲！您确认删除该地址？");
                message.setSpan(new RelativeSizeSpan(2), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                message.setSpan(new ForegroundColorSpan(Color.parseColor("#d4377e")), 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                clearBuilder.setMessage(message)
                        .setCancelable(true)
                        .setPositiveButton("确认",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int id) {
                                        dialog.dismiss();
                                        long userId = LoginManager.getLoginInfo(AddressModifyActivity.this).ID;
                                        AddressManager.deleteGoodsAddress(mGoodsAddressVO.id).startUI(new ApiCallback<Boolean>() {
                                            @Override
                                            public void onError(int code, String errorInfo) {
                                                Toast.makeText(AddressModifyActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
                                            }

                                            @Override
                                            public void onSuccess(Boolean aBoolean) {
                                                AddressModifyActivity.this.finish();
                                                EventBus.getDefault().post(new AddressModifiedEvent());
                                            }

                                            @Override
                                            public void onProgress(int progress) {

                                            }
                                        });
                                    }
                                })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog2 = clearBuilder.create();
                dialog2.show();
                break;
            case R.id.save_addess_button:
                String linkName = linkNameEditView.getText().toString();
                if (TextUtils.isEmpty(linkName)) {
                    Toast.makeText(this, "请输入收货人姓名!", Toast.LENGTH_SHORT).show();
                    return;
                }
                String mobile = mobileEditView.getText().toString();
                if (TextUtils.isEmpty(mobile)) {
                    Toast.makeText(this, "请输入收货人手机号码!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String addressDetail = addressDetailEditText.getText().toString();
                if (TextUtils.isEmpty(addressDetail)) {
                    Toast.makeText(this, "请输入有效的详细地址!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String address = provinceCityView.getText().toString();
                if (TextUtils.isEmpty(address)) {
                    Toast.makeText(this, "请选择省市区!", Toast.LENGTH_SHORT).show();
                    return;
                }

                int isDefault = defaultButton.isChecked() ? 1 : 0;

                long uid = LoginManager.getLoginInfo(this).ID;
                final GoodsAddressVO goodsAddressVO = new GoodsAddressVO();
                goodsAddressVO.isDefault = isDefault;
                goodsAddressVO.detailAdr = addressDetail;
                goodsAddressVO.provinceId = provinceId;
                goodsAddressVO.cityId = cityId;
                goodsAddressVO.areaId = townId;
                goodsAddressVO.userId = uid;
                goodsAddressVO.mobile = mobile;
                goodsAddressVO.linkName = linkName;

                if (isModified) {
                    goodsAddressVO.id = mGoodsAddressVO.id;
                    AddressManager.modifiyAddress(goodsAddressVO).startUI(new ApiCallback<Boolean>() {
                        @Override
                        public void onError(int code, String errorInfo) {

                        }

                        @Override
                        public void onSuccess(Boolean a) {
                            AddressModifyActivity.this.finish();
                            Toast.makeText(AddressModifyActivity.this, "添加地址成功", Toast.LENGTH_SHORT).show();
                            if (goodsAddressVO.isDefault > 0) {
                                AddressManager.saveDefaultAddressInfo(AddressModifyActivity.this, goodsAddressVO);
                            }
                            //刷新页面
                            EventBus.getDefault().post(new AddressModifiedEvent());
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                } else {
                    AddressManager.addAddress(goodsAddressVO).startUI(new ApiCallback<Long>() {
                        @Override
                        public void onError(int code, String errorInfo) {

                        }

                        @Override
                        public void onSuccess(Long aLong) {
                            AddressModifyActivity.this.finish();
                            Toast.makeText(AddressModifyActivity.this, "添加地址成功", Toast.LENGTH_SHORT).show();
                            if (goodsAddressVO.isDefault > 0) {
                                AddressManager.saveDefaultAddressInfo(AddressModifyActivity.this, goodsAddressVO);
                            }
                            //刷新页面
                            EventBus.getDefault().post(new AddressModifiedEvent());
                        }

                        @Override
                        public void onProgress(int progress) {

                        }
                    });
                }
                break;
            case R.id.province_city_view:
                checkAddress(true);
                break;
            case R.id.pop_province_view:
                canClicked = false;
                chooseAddresses.clear();
                checkAddress(false);
                break;
            case R.id.pop_city_view:
                canClicked = false;
                if (chooseAddresses.size() > 1) {
                    List<AddressVO> removeList = new LinkedList<AddressVO>();
                    for (int j = chooseAddresses.size() - 1; j > 0; j--) {
                        removeList.add(chooseAddresses.get(j));
                    }
                    chooseAddresses.removeAll(removeList);
                }
                checkAddress(false);
                break;
            case R.id.pop_town_view:
                canClicked = false;
                if (chooseAddresses.size() > 2) {
                    List<AddressVO> removeList = new LinkedList<AddressVO>();
                    for (int j = chooseAddresses.size() - 1; j > 1; j--) {
                        removeList.add(chooseAddresses.get(j));
                    }
                    chooseAddresses.removeAll(removeList);
                }
                checkAddress(false);

                break;
        }

    }


    public void onBackPressed() {
        super.onBackPressed();
        if (popupWindow.isShow()) {
            popupWindow.hidePopUpWindow();
        }
    }
    private void checkAddress(final boolean showPop) {
        long codeId = 0l;

        for (int i = 0; i < checkedViews.length; i++) {
            checkedViews[i].setVisibility(View.GONE);
        }

        for (int i = 0; i < chooseAddresses.size(); i++) {
            checkedViews[i].setChecked(false);
            checkedViews[i].setVisibility(View.VISIBLE);
            checkedViews[i].setText(chooseAddresses.get(i).CodeName);
            if (showPop) {
                codeId = chooseAddresses.get(i).pid;
            } else {
                codeId = chooseAddresses.get(i).CodeID;
            }
        }

        if (chooseAddresses.size() < 3) {
            checkedViews[chooseAddresses.size()].setChecked(true);
            checkedViews[chooseAddresses.size()].setVisibility(View.VISIBLE);
            checkedViews[chooseAddresses.size()].setText("请选择");
        } else {
            checkedViews[chooseAddresses.size() - 1].setChecked(true);
        }


        AddressManager.getLocation(codeId).startUI(new ApiCallback<List<AddressVO>>() {
            @Override
            public void onError(int code, String errorInfo) {
                Toast.makeText(AddressModifyActivity.this, errorInfo, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(List<AddressVO> addressVOs) {
                addressAdapter.addAll(addressVOs, true);
                if (showPop) {
                    popupWindow.showPopUpWindow();
                }
                canClicked = true;
            }

            @Override
            public void onProgress(int progress) {

            }
        });
    }
}
