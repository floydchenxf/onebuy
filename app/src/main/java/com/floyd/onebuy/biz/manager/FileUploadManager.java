package com.floyd.onebuy.biz.manager;

/**
 * Created by floyd on 15-11-29.
 */
public class FileUploadManager {


//    public static AsyncJob<String> uploadFiles(String accessToken, File file) {
//        String url = APIConstants.HOST + APIConstants.API_UPDATE_MONTE_AVART;
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("token", accessToken);
//        Map<String, FileItem> files = new HashMap<String, FileItem>();
//        FileItem fileItem = new FileItem(file);
//        files.put("image", fileItem);
//        final AsyncJob<String> httpJob = HttpJobFactory.createFileJob(url, params,files, HttpMethod.POST).map(new StringFunc());
//
//        return new AsyncJob<String>() {
//            @Override
//            public void start(ApiCallback<String> callback) {
//                httpJob.start(new AbstractJsonApiCallback<String>(callback) {
//                    @Override
//                    protected String convert2Obj(String s, String data) throws JSONException {
//                        return data;
//                    }
//                });
//
//            }
//        };
//    }
}
