package com.floyd.onebuy.biz.manager;

import com.floyd.onebuy.aync.AsyncJob;
import com.floyd.onebuy.aync.Func;
import com.floyd.onebuy.biz.constants.APIConstants;
import com.floyd.onebuy.channel.request.FileItem;
import com.floyd.onebuy.channel.request.HttpMethod;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by floyd on 16-5-8.
 */
public class ImagerInfoManager {

    /**
     * 修改头像
     *
     * @param userId
     * @param file
     * @return
     */
    public static AsyncJob<String> uploadVideo(long userId, long productLssueId, File file) {
        String url = APIConstants.HOST_API_PATH + APIConstants.IMGAEINFO_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "upvideo");
        params.put("productLssueID", productLssueId + "");
        params.put("userId", userId + "");
        Map<String, FileItem> files = new HashMap<String, FileItem>();
        FileItem fileItem = new FileItem(file, "video/mpeg4");
        files.put("File", fileItem);
        AsyncJob<Map<String, String>> aa = JsonHttpJobFactory.getJsonAsyncJob(url, params, files, HttpMethod.GET, Map.class);
        return aa.map(new Func<Map<String, String>, String>() {
            @Override
            public String call(Map<String, String> tt) {
                return tt.get("url");
            }
        });
    }

    public static AsyncJob<String> uploadImage(long userId, long productLssueId, File file) {
        String url = APIConstants.HOST_API_PATH + APIConstants.IMGAEINFO_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "upImage");
        params.put("productLssueID", productLssueId + "");
        params.put("userId", userId + "");
        Map<String, FileItem> files = new HashMap<String, FileItem>();
        FileItem fileItem = new FileItem(file, "image/jpeg");
        files.put("File", fileItem);
        AsyncJob<Map<String, String>> aa = JsonHttpJobFactory.getJsonAsyncJob(url, params, files, HttpMethod.POST, Map.class);
        return aa.map(new Func<Map<String, String>, String>() {
            @Override
            public String call(Map<String, String> tt) {
                return tt.get("url");
            }
        });
    }

    /**
     * 晒单
     * @param userId 用户id
     * @param lssueId 期数
     * @param proId 商品id
     * @param title 标题
     * @param content 内容
     * @param type 类型
     * @param surl
     * @return
     */
    public static AsyncJob<Long> shareImage(long userId, long lssueId, long proId, String title, String content, int type, String surl, String pic) {
        String url = APIConstants.HOST_API_PATH + APIConstants.IMGAEINFO_MODULE;
        Map<String, String> params = new HashMap<String, String>();
        params.put("pageType", "shareImage");
        params.put("productlssueid", lssueId + "");
        params.put("userid", userId + "");
        params.put("typeid", type + "");
        params.put("title", title);
        params.put("content", content);
        params.put("url", surl);
        params.put("proid", proId + "");
        params.put("pic", pic);
        Type typeClazz = new TypeToken<Map<String, Long>>(){}.getType();
        AsyncJob<Map<String, Long>> aa = JsonHttpJobFactory.getJsonAsyncJob(url, params, HttpMethod.POST, typeClazz);
        return aa.map(new Func<Map<String, Long>, Long>() {
            @Override
            public Long call(Map<String, Long> bb) {
                return bb.get("ID");
            }
        });
    }
}
