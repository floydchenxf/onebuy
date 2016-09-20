package com.floyd.onebuy.channel.request;

import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.zip.GZIPInputStream;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * 抽象的WEB请求实现<br>
 *
 * @author floydchenxf
 */
public abstract class AbstractWebUtils {

    public static final int DOWN_PROGRESS = 2;
    public static final int UPLOAD_PROGRESS = 1;
    public static final int ALL_PROGRESS = 0;

    private static final String TAG = AbstractWebUtils.class.getSimpleName();

    public static final String DEFAULT_CHARSET = "UTF-8";

    static {
        if (Build.VERSION.SDK_INT < 9) {
            Log.i(TAG, "HTTP connection reuse which was buggy pre-froyo,disabled.");
            System.setProperty("http.keepAlive", "false");
        }
    }

    /**
     * 设置UserAgent
     *
     * @return
     */
    protected abstract String getUserAgent();


    /**
     * 执行HTTP POST请求。
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param headers
     * @param connectTimeout
     * @param readTimeout
     * @return
     * @throws IOException
     */
    public Response doPost(String url, Map<String, String> params,
                           Map<String, String> headers, int connectTimeout,
                           int readTimeout) throws IOException {
        return doPost(url, params, headers, DEFAULT_CHARSET, connectTimeout,
                readTimeout);
    }

    /**
     * 执行HTTP POST请求。
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param headers
     * @param charset        字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout
     * @param readTimeout
     * @return
     * @throws IOException
     */
    public Response doPost(String url, Map<String, String> params,
                           Map<String, String> headers, String charset,
                           int connectTimeout, int readTimeout)
            throws IOException {
        String ctype = "application/x-www-form-urlencoded;charset=" + charset;
        String query = buildQuery(params, charset);
        byte[] content = {};
        if (query != null) {
            content = query.getBytes(charset);
        }
        return doPost(url, ctype, content, headers, connectTimeout, readTimeout);
    }

    /**
     * 执行带文件上传的HTTP POST请求
     * @param url
     * @param params
     * @param headers
     * @param fileParams
     * @param connectTimeout
     * @param readTimeout
     * @return
     * @throws IOException
     */
    public Response doPost(String url, Map<String, String> params,
                           Map<String, String> headers, Map<String, FileItem> fileParams,
                           int connectTimeout, int readTimeout) throws IOException {
        Log.i(TAG, "post Url:" + url);
        if (fileParams == null || fileParams.isEmpty()) {
            return doPost(url, params, headers, DEFAULT_CHARSET, connectTimeout,
                    readTimeout);
        } else {
            return doPost(url, params, headers, fileParams, DEFAULT_CHARSET,
                    connectTimeout, readTimeout);
        }
    }

    /**
     * 执行Post请求
     * @param url 请求地址
     * @param ctype 请求类型
     * @param content 请求字节数组
     * @param headers 请求header
     * @param connectTimeout
     * @param readTimeout
     * @return 响应
     * @throws IOException
     */
    public Response doPost(String url, String ctype, byte[] content,
                           Map<String, String> headers,int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        OutputStream out = null;
        Response rsp = null;
        try {
            try {
                conn = getConnection(new URL(url), HttpMethod.POST.name(), ctype);
                addHeaders(headers, conn);

                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            } catch (IOException e) {
                throw e;
            }

            try {
                out = conn.getOutputStream();
                out.write(content);
                rsp = getResponse(conn);
            } catch (IOException e) {
                throw e;
            }

        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    /**
     * 执行上传文件的Post请求
     * @param url 请求地址
     * @param params
     * @param headers 请求header
     * @param fileParams 文件
     * @param charset
     * @param connectTimeout
     * @param readTimeout
     * @return 响应
     * @throws IOException
     */
    public Response doPost(String url,Map<String, String> params,
                           Map<String, String> headers,Map<String, FileItem> fileParams,String charset,
                           int connectTimeout, int readTimeout) throws IOException {
        String boundary = System.currentTimeMillis() + ""; // 随机分隔线
        HttpURLConnection conn = null;
        OutputStream out = null;
        Response rsp = null;
        try {
            try {
                String ctype = "multipart/form-data;charset=" + charset	+ ";boundary=" + boundary;
                conn = getConnection(new URL(url), HttpMethod.POST.name(), ctype);
                addHeaders(headers, conn);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            } catch (IOException e) {
                throw e;
            }

            try {
                out = conn.getOutputStream();

                byte[] entryBoundaryBytes = ("\r\n--" + boundary + "\r\n")
                        .getBytes(charset);

                // 组装文本请求参数
                Set<Map.Entry<String, String>> textEntrySet = params.entrySet();
                for (Map.Entry<String, String> textEntry : textEntrySet) {
                    byte[] textBytes = getTextEntry(textEntry.getKey(),
                            textEntry.getValue(), charset);
                    out.write(entryBoundaryBytes);
                    out.write(textBytes);
                }

                // 组装文件请求参数
                Set<Map.Entry<String, FileItem>> fileEntrySet = fileParams
                        .entrySet();
                int totalSize = this.getFilesTotalSize(fileParams.values());
                int currentPos = 0;
                int progress = 0;
                if (totalSize > 0) {
                    invokeProgress(UPLOAD_PROGRESS, 0);
                }
                for (Map.Entry<String, FileItem> fileEntry : fileEntrySet) {
                    FileItem fileItem = fileEntry.getValue();
                    byte[] fileBytes = getFileEntry(fileEntry.getKey(),
                            fileItem.getFileName(), fileItem.getMimeType(),
                            charset);
                    out.write(entryBoundaryBytes);
                    out.write(fileBytes);

                    // 支持字节流上传
                    byte[] content = fileItem.getContent();
                    if (content != null) {
                        out.write(content);
                        continue;
                    }

                    File file = fileItem.getFile();
                    if (file != null && file.exists()) {
                        InputStream in = null;
                        try {
                            in = new FileInputStream(file);
                            int num = 0;
                            byte[] block = new byte[1024];
                            while ((num = in.read(block)) != -1) {
                                out.write(block, 0, num);
                                currentPos += num;
                                if (totalSize > 0) {
                                    progress = currentPos * 100 / totalSize;
                                    invokeProgress(UPLOAD_PROGRESS, progress);
                                }
                            }
                        } finally {
                            if (in != null) {
                                in.close();
                            }
                        }
                    }
                }

                // 添加请求结束标志
                byte[] endBoundaryBytes = ("\r\n--" + boundary + "--\r\n")
                        .getBytes(charset);
                out.write(endBoundaryBytes);
                rsp = getResponse(conn);
            } catch (IOException e) {
                throw e;
            }

        } finally {
            if (out != null) {
                out.close();
            }
            if (conn != null) {
                conn.disconnect();
            }
        }

        return rsp;
    }

    private int getFilesTotalSize(Collection<FileItem> fileItems) {
        int totalSize = 0;
        if (fileItems.isEmpty()) {
            return totalSize;
        }
        for (FileItem fileItem : fileItems) {
            File file = fileItem.getFile();
            if (file == null || !file.exists()) {
                continue;
            }
            totalSize += fileItem.getFile().length();
        }

        return totalSize;

    }

    private static byte[] getTextEntry(String fieldName, String fieldValue,
                                       String charset) throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\"\r\nContent-Type:text/plain\r\n\r\n");
        entry.append(fieldValue);
        return entry.toString().getBytes(charset);
    }

    private void addHeaders(Map<String, String> headers,
                            HttpURLConnection conn) {
        if (headers != null) {
            for (Map.Entry<String, String> ent: headers.entrySet()) {
                conn.addRequestProperty(ent.getKey(), ent.getValue());
            }

        }
    }

    private static byte[] getFileEntry(String fieldName, String fileName,
                                       String mimeType, String charset) throws IOException {
        StringBuilder entry = new StringBuilder();
        entry.append("Content-Disposition:form-data;name=\"");
        entry.append(fieldName);
        entry.append("\";filename=\"");
        entry.append(fileName);
        entry.append("\"\r\nContent-Type:");
        entry.append(mimeType);
        entry.append("\r\n\r\n");
        return entry.toString().getBytes(charset);
    }

    /**
     * 执行HTTP GET请求。
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param connectTimeout
     * @param readTimeout
     * @return 响应
     * @throws IOException
     */
    public Response doGet(String url, Map<String, String> params,
                          int connectTimeout, int readTimeout) throws IOException {
        return doGet(url, params, null, DEFAULT_CHARSET, connectTimeout, readTimeout);
    }

    /**
     * 执行HTTP GET请求。
     *
     * @param url            请求地址
     * @param params         请求参数
     * @param header
     * @param charset        字符集，如UTF-8, GBK, GB2312
     * @param connectTimeout
     * @param readTimeout
     * @return 响应
     * @throws IOException
     */
    public Response doGet(String url, Map<String, String> params,
                          Map<String, String> header, String charset, int connectTimeout, int readTimeout) throws IOException {
        HttpURLConnection conn = null;
        Response response = null;

        try {
            String ctype = "application/x-www-form-urlencoded;charset="
                    + charset;
            String query = buildQuery(params, charset);
            try {
                conn = getConnection(buildGetUrl(url, query), HttpMethod.GET.name(),
                        ctype);
                addHeaders(header, conn);
                conn.setConnectTimeout(connectTimeout);
                conn.setReadTimeout(readTimeout);
            } catch (IOException e) {
                throw e;
            }

            try {
                response = getResponse(conn);
            } catch (IOException e) {
                throw e;
            }
        } finally {
            if (conn != null) {
                conn.disconnect();
            }
        }

        return response;
    }

    private HttpURLConnection getConnection(URL url, String method,
                                            String ctype) throws IOException {

        HttpURLConnection conn = null;
        if ("https".equals(url.getProtocol())) {
            SSLContext ctx = null;
            try {
                ctx = SSLContext.getInstance("TLS");
                ctx.init(new KeyManager[0],
                        new TrustManager[]{new DefaultTrustManager()},
                        new SecureRandom());
            } catch (Exception e) {
                throw new IOException(e.getMessage());
            }
            HttpsURLConnection connHttps = null;

            connHttps = (HttpsURLConnection) url.openConnection();

            connHttps.setSSLSocketFactory(ctx.getSocketFactory());
            connHttps.setHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;// 默认都认证通过
                }
            });
            conn = connHttps;
        } else {
            conn = (HttpURLConnection) url.openConnection();
        }

        conn.setRequestMethod(method);
        conn.setDoInput(true);
        if ("post".equalsIgnoreCase(method)) {
            conn.setDoOutput(true);
        }
        conn.setRequestProperty("Accept", "text/xml,text/javascript,application/xhtml+xml,text/html,application/json,image/*");
        conn.setRequestProperty("User-Agent", getUserAgent());
        conn.setRequestProperty("Content-Type", ctype);
        conn.setRequestProperty("Accept-Encoding", "gzip");//support gzip
        return conn;
    }

    /**
     * 如果只需要构建一个字符串的url，建议使用这个方法，性能较好
     *
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public String buildGetUrlForString(String url, Map<String, String> params, String charset) throws UnsupportedEncodingException {
        if (TextUtils.isEmpty(url)) {
            throw new IllegalArgumentException("url must not empty");
        }
        String queryString = buildQuery(params, charset);
        StringBuilder base = new StringBuilder(url);
        if (url.indexOf("?") > -1) {
            if (!url.endsWith("&")) {
                base.append("&");
            }
            base.append(queryString);
        } else {
            base.append("?");
            base.append(queryString);
        }
        return base.toString();
    }

    /**
     * @param url
     * @param params
     * @param charset
     * @return
     * @throws IOException
     */
    public URL buildGetUrl(String url, Map<String, String> params,
                           String charset) throws IOException {
        String queryString = buildQuery(params, charset);
        return buildGetUrl(url, queryString);
    }

    /**
     * @param strUrl
     * @param query
     * @return
     * @throws IOException
     */
    private URL buildGetUrl(String strUrl, String query)
            throws IOException {
        URL url = new URL(strUrl);
        if (TextUtils.isEmpty(query)) {
            return url;
        }

        if (TextUtils.isEmpty(url.getQuery())) {
            if (strUrl.endsWith("?")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "?" + query;
            }
        } else {
            if (strUrl.endsWith("&")) {
                strUrl = strUrl + query;
            } else {
                strUrl = strUrl + "&" + query;
            }
        }
        Log.i(TAG, "get Url:" + strUrl);
        return new URL(strUrl);
    }

    /**
     * @param params
     * @param charset
     * @return
     * @throws UnsupportedEncodingException
     */
    public String buildQuery(Map<String, String> params, String charset)
            throws UnsupportedEncodingException {
        if (params == null || params.isEmpty()) {
            return null;
        }
        if (TextUtils.isEmpty(charset)) {
            charset = DEFAULT_CHARSET;
        }

        StringBuilder query = new StringBuilder();
        Set<Map.Entry<String, String>> entries = params.entrySet();
        boolean hasParam = false;

        for (Map.Entry<String, String> entry : entries) {
            String name = entry.getKey();
            String value = entry.getValue();
            // 忽略参数名或参数值为空的参数
            if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(value)) {
                if (hasParam) {
                    query.append("&");
                } else {
                    hasParam = true;
                }

                query.append(name).append("=")
                        .append(URLEncoder.encode(value, charset));
            }
        }

        return query.toString();
    }

    /**
     * 获取http响应内容<br>
     * 当响应状态码不等于200时，如果要换取响应的内容responseError参数请输入true。否则会抛出IOException
     *
     * @param conn
     * @return
     * @throws IOException
     */
    @SuppressLint("DefaultLocale")
    protected Response getResponse(HttpURLConnection conn) throws IOException {
        String charset = getResponseCharset(conn.getContentType());
        String header = conn.getHeaderField("Content-Encoding");
        boolean isGzip = false;
        if (header != null && header.toLowerCase().contains("gzip")) {
            isGzip = true;
        }
        Response response = new Response();
        response.setResponseCode(conn.getResponseCode());
        response.setResponseMessage(conn.getResponseMessage());
        response.setHeaderFields(conn.getHeaderFields());

        //获取返回总长度
        int totalSize = 0;
        String fileLength = conn.getHeaderField("Content-Length");
        if (!TextUtils.isEmpty(fileLength)) {
            totalSize = Integer.parseInt(fileLength);
        }
        Log.i(TAG, "download file length:" + totalSize);

        InputStream es = conn.getErrorStream();
        //没有返回http错误信息
        if (es == null) {
            InputStream input = conn.getInputStream();
            if (isGzip) {
                input = new GZIPInputStream(input);
            }
            response.setChatset(charset);
            response.setContent(getStreamAsBytes(input, totalSize));
        } else { //包含http错误
            if (isGzip) {
                es = new GZIPInputStream(es);
            }
            byte[] msg = getStreamAsBytes(es, totalSize);
            String msgContent = new String(msg, charset);
            ApiErrorInfo error = new ApiErrorInfo();
            if (!TextUtils.isEmpty(msgContent)) {
                error.setErrorCode(response.getResponseCode());
                error.setErrorDesc(msgContent);
                response.setChatset(charset);
                response.setContent(msg);
            }
            response.setRequestError(new RequestError(null, error));
        }
        return response;
    }

    private byte[] getStreamAsBytes(InputStream stream, int totalSize)
            throws IOException {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int progress = 0;
            int start = 0;
            if (totalSize > 0) {
                progress = start * 100 / totalSize;
                invokeProgress(DOWN_PROGRESS, progress);
            }
            byte[] block = new byte[2048];
            int count = 0;
            while((count = stream.read(block)) != -1) {
                baos.write(block, 0, count);
                start +=count;
                if (totalSize > 0) {
                    progress = start * 100 / totalSize;
                    invokeProgress(DOWN_PROGRESS, progress);
                }
            }

            return baos.toByteArray();
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private String getResponseCharset(String ctype) {
        String charset = DEFAULT_CHARSET;

        if (!TextUtils.isEmpty(ctype)) {
            String[] params = ctype.split(";");
            for (String param : params) {
                param = param.trim();
                if (param.startsWith("charset")) {
                    String[] pair = param.split("=", 2);
                    if (pair.length == 2) {
                        if (!TextUtils.isEmpty(pair[1])) {
                            charset = pair[1].trim();
                        }
                    }
                    break;
                }
            }
        }

        return charset;
    }

    /**
     * 使用默认的UTF-8字符集反编码请求参数值。
     *
     * @param value 参数值
     * @return 反编码后的参数值
     */
    public String decode(String value) {
        return decode(value, DEFAULT_CHARSET);
    }

    /**
     * 使用默认的UTF-8字符集编码请求参数值。
     *
     * @param value 参数值
     * @return 编码后的参数值
     */
    public String encode(String value) {
        return encode(value, DEFAULT_CHARSET);
    }

    /**
     * 使用指定的字符集反编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 反编码后的参数值
     */
    public String decode(String value, String charset) {
        String result = null;
        if (!TextUtils.isEmpty(value)) {
            try {
                result = URLDecoder.decode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 使用指定的字符集编码请求参数值。
     *
     * @param value   参数值
     * @param charset 字符集
     * @return 编码后的参数值
     */
    public String encode(String value, String charset) {
        String result = null;
        if (!TextUtils.isEmpty(value)) {
            try {
                result = URLEncoder.encode(value, charset);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return result;
    }

    /**
     * 从URL中提取所有的参数。
     *
     * @param query URL地址
     * @return 参数映射
     */
    public static Map<String, String> splitUrlQuery(String query) {
        Map<String, String> result = new HashMap<String, String>();

        String[] pairs = query.split("&");
        if (pairs != null && pairs.length > 0) {
            for (String pair : pairs) {
                String[] param = pair.split("=", 2);
                if (param != null && param.length == 2) {
                    result.put(param[0], param[1]);
                }
            }
        }

        return result;
    }


    /**
     * @author floyd.chenxf
     */
    public static class DefaultTrustManager implements X509TrustManager {
        public X509Certificate[] getAcceptedIssuers() {
            return null;
        }

        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }

        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
        }
    }

    public abstract void invokeProgress(int progressType, int progress);

}
