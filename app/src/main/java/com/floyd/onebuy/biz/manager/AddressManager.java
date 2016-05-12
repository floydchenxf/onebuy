package com.floyd.onebuy.biz.manager;

import android.content.Context;
import android.text.TextUtils;

import com.floyd.onebuy.aync.ApiCallback;
import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.aync.HttpJobFactory;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.biz.constants.APIError;
import com.floyd.onebuy.biz.func.StringFunc;
import com.floyd.onebuy.biz.tools.PrefsTools;
import com.floyd.onebuy.biz.vo.json.AddressVO;
import com.floyd.onebuy.biz.vo.json.GoodsAddressVO;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by floyd on 16-5-8.
 */
public class AddressManager {

    private static final String DEFAULT_ADDRESS_INFO = "DEFAULT_ADDRESS_INFO";


    /**
     * 获取默认地址
     * @param context
     * @return
     */
    public GoodsAddressVO getDefaultAddressInfo(Context context) {
        String data = PrefsTools.getStringPrefs(context, DEFAULT_ADDRESS_INFO, "");
        if (TextUtils.isEmpty(data)) {
            return null;
        }

        GoodsAddressVO result = GsonHelper.getGson().fromJson(data, GoodsAddressVO.class);
        return result;
    }

    public static void saveDefaultAddressInfo(Context context, GoodsAddressVO vo) {
        Gson gson = new Gson();
        String data = gson.toJson(vo);
        PrefsTools.setStringPrefs(context, DEFAULT_ADDRESS_INFO, data);
    }


    /**
     * 获取省市地区
     *
     * @param pid
     * @return
     */
    public static AsyncJob<List<AddressVO>> getLocation(final long pid) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ADDRESS_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getLoaction");
        params.put("codeId", pid + "");
        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<List<AddressVO>>>() {
            @Override
            public AsyncJob<List<AddressVO>> call(final String s) {
                return new AsyncJob<List<AddressVO>>() {
                    @Override
                    public void start(ApiCallback<List<AddressVO>> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONArray data = j.getJSONArray("data");
                                List<AddressVO> addressVOs = convert2List(data, pid);
                                if (addressVOs == null || addressVOs.isEmpty()) {
                                    callback.onError(APIError.API_CONTENT_EMPTY, "内容为空");
                                    return;
                                }
                                callback.onSuccess(addressVOs);
                            } else {
                                String msg = j.getString("info");
                                callback.onError(APIError.API_BIZ_ERROR, msg);
                            }
                        } catch (JSONException e) {
                            callback.onError(APIError.API_JSON_PARSE_ERROR, e.getMessage());
                        }
                    }
                };
            }


        });
    }

    private static List<AddressVO> convert2List(JSONArray data, long pid) throws JSONException {
        List<AddressVO> result = new ArrayList<AddressVO>();
        if (data == null || data.length() <= 0) {
            return result;
        }

        for (int i = 0; i < data.length(); i++) {
            AddressVO vo = new AddressVO();
            JSONObject jj = data.getJSONObject(i);
            vo.CodeID = jj.getLong("CodeID");
            vo.CodeName = jj.getString("CodeName");
            vo.pid = pid;
            result.add(vo);
        }
        return result;
    }

    /**
     * 获取添加的地址
     *
     * @param userId
     * @param pageSize
     * @param pageNum
     * @return
     */
    public static AsyncJob<List<GoodsAddressVO>> getMyAddressList(final Context context, long userId, int pageSize, int pageNum) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ADDRESS_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "getMyList");
        params.put("userId", userId + "");
        params.put("pageSize", pageSize + "");
        params.put("pageNum", pageNum + "");
        return HttpJobFactory.createHttpJob(url, params, HttpMethod.GET).map(new StringFunc()).flatMap(new Func<String, AsyncJob<List<GoodsAddressVO>>>() {
            @Override
            public AsyncJob<List<GoodsAddressVO>> call(final String s) {
                return new AsyncJob<List<GoodsAddressVO>>() {
                    @Override
                    public void start(ApiCallback<List<GoodsAddressVO>> callback) {
                        JSONObject j = null;
                        try {
                            j = new JSONObject(s);
                            int status = j.getInt("status");
                            if (status == 1) {
                                JSONObject data = j.getJSONObject("data");
                                JSONArray jsonArray = data.getJSONArray("AddressList");
                                List<GoodsAddressVO> addressVOs = convert2AddressList(context, jsonArray);
                                if (addressVOs == null) {
                                    callback.onError(APIError.API_CONTENT_EMPTY, "内容为空");
                                    return;
                                }
                                callback.onSuccess(addressVOs);
                            } else {
                                String msg = j.getString("info");
                                callback.onError(APIError.API_BIZ_ERROR, msg);
                            }
                        } catch (JSONException e) {
                            callback.onError(APIError.API_JSON_PARSE_ERROR, e.getMessage());
                        }
                    }
                };
            }


        });
    }

    private static List<GoodsAddressVO> convert2AddressList(Context context, JSONArray data) throws JSONException {
        List<GoodsAddressVO> result = new ArrayList<GoodsAddressVO>();
        if (data == null || data.length() <= 0) {
            return result;
        }

        for (int i = 0; i < data.length(); i++) {
            GoodsAddressVO vo = new GoodsAddressVO();
            JSONObject jj = data.getJSONObject(i);
            vo.id = jj.getLong("ID");
            vo.linkName = jj.getString("LinkName");
            vo.mobile = jj.getString("Moblie");
            vo.province = jj.getString("Province");
            vo.city = jj.getString("City");
            vo.area = jj.getString("Town");
            vo.detailAdr = jj.getString("Address");
            vo.isDefault = jj.getBoolean("IsDefault")?1:0;
            vo.provinceId = jj.getLong("ProvinceId");
            vo.cityId = jj.getLong("CityId");
            vo.areaId = jj.getLong("TownId");
            if (vo.isDefault > 0) {
                saveDefaultAddressInfo(context, vo);
            }
            result.add(vo);
        }
        return result;
    }

    /**
     * 删除地址
     *
     * @param addressId
     * @return
     */
    public static AsyncJob<Boolean> deleteGoodsAddress(long addressId) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ADDRESS_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "del");
        params.put("id", addressId + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, Boolean.class);
    }

    public static AsyncJob<Long> addAddress(GoodsAddressVO goodsAddressVO) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ADDRESS_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "addAddress");
        params.put("mobile", goodsAddressVO.mobile);
        params.put("linkName", goodsAddressVO.linkName);
        params.put("provinceId", goodsAddressVO.provinceId+"");
        params.put("cityId", goodsAddressVO.cityId+"");
        params.put("areaId", goodsAddressVO.areaId+"");
        params.put("detailAdr", goodsAddressVO.detailAdr);
        params.put("userId", goodsAddressVO.userId+"");
        params.put("isDefault", goodsAddressVO.isDefault + "");
        Type type = new TypeToken<Map<String, Long>>(){}.getType();
        AsyncJob<Map<String, Long>> result = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, type);
        return result.map(new Func<Map<String, Long>, Long>() {
            @Override
            public Long call(Map<String, Long> stringLongMap) {
                return stringLongMap.get("ID");
            }
        });
    }

    public static AsyncJob<Boolean> modifiyAddress(GoodsAddressVO goodsAddressVO) {
        String url = APIConstants.HOST_API_PATH + APIConstants.ADDRESS_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "modifyAddress");
        params.put("id",goodsAddressVO.id+"");
        params.put("mobile", goodsAddressVO.mobile);
        params.put("linkName", goodsAddressVO.linkName);
        params.put("provinceId", goodsAddressVO.provinceId+"");
        params.put("cityId", goodsAddressVO.cityId+"");
        params.put("areaId", goodsAddressVO.areaId+"");
        params.put("detailAdr", goodsAddressVO.detailAdr);
        params.put("userId", goodsAddressVO.userId+"");
        params.put("isDefault", goodsAddressVO.isDefault + "");
        return JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.GET, Boolean.class);
    }

}
