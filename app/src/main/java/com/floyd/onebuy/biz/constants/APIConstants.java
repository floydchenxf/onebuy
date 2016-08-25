package com.floyd.onebuy.biz.constants;

/**
 * Created by floyd on 15-11-28.
 */
public class APIConstants {
    public static final String HOST = "http://yiyuan.zeego.cn/";
    public static final String HOST_API_PATH = HOST + "AppInterface/";
    public static final String PRODUCT_DETAIL_URL_FORMAT = HOST + "ProDescriptionForApp.aspx?ProID=%d";

    public static final String USER_MODULE = "UserService.ashx";
    public static final String PRODUCT_MODULE = "ProductService.ashx";
    public static final String JIFENG_MODULE = "JiFengRecordService.ashx";
    public static final String CAR_MODULE = "CarService.ashx";
    public static final String ORDER_MODULE = "OrderService.ashx";
    public static final String IMGAEINFO_MODULE = "ImgaeInfoService.ashx";
    public static final String ADDRESS_MODULE = "AddressService.ashx";
    public static final String APP_HANDLE_MODULE = "AppHandler.ashx";

    public static final String COMMONWEAL_MODULE = "CommonwealService.ashx";
    public static final String API_GET_ADVERT_LIST = "api/index/getAdvertList";//获取广告
    public static final String API_GET_MOTE_LIST = "api/index/getMoteList"; //获取首页模特信息

    public static final String API_INDEX_INFO = "api/index/getIndexInfo";//获取首页导航栏目
    public static final String PIC_PATH_110 = HOST + "/UploadImg/products/110/";
    public static final String PIC_PATH_180 = HOST + "/UploadImg/products/180/";
    public static final String PIC_PATH_280 = HOST + "/UploadImg/products/280/";
    public static final String PIC_PATH_360 = HOST + "/UploadImg/products/360/";
    public static final String PIC_PATH_640 = HOST + "/UploadImg/products/640/";


    public static final String PIC_PATH_SOURCE = HOST + "/UploadImg/products/source/";
    public static final String PRO_ID = "PRO_ID";//产品id
    public static final String INDEX_PRODUCT_TYPE_ID = "INDEX_PRODUCT_TYPE_ID";
    public static final String PAY_ORDER_NO = "PAY_ORDER_NO";

    public static final String ADDRESS_MANAGEMENT_VO = "ADDRESS_MANAGEMENT_VO";
}
