package com.kwon.znshare.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLContextBuilder;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.SSLContext;
import java.io.*;
import java.net.*;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author ws
 */

public class HttpClientUtil {

    protected static final Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final String utf8 = "UTF-8";

    private String requestEncoding;

    private static String responseEncoding;

    private String requesParamstEncoding;

    public HttpClientUtil() {

        super();

        this.requestEncoding = utf8;

        this.responseEncoding = utf8;

        this.requesParamstEncoding = utf8;

    }

    public HttpClientUtil(String requestEncoding, String responseEncoding, String requesParamstEncoding) {

        super();

        this.requestEncoding = requestEncoding;

        this.responseEncoding = responseEncoding;

        this.requesParamstEncoding = requesParamstEncoding;

    }

    public HttpClientUtil(String responseEncoding) {

        super();

        this.requestEncoding = utf8;

        this.responseEncoding = responseEncoding;

        this.requesParamstEncoding = utf8;

    }

    public String getRequestEncoding() {

        return requestEncoding;

    }

    public void setRequestEncoding(String requestEncoding) {

        this.requestEncoding = requestEncoding;

    }

    public String getResponseEncoding() {

        return responseEncoding;

    }

    public void setResponseEncoding(String responseEncoding) {

        this.responseEncoding = responseEncoding;

    }

    public String getRequesParamstEncoding() {

        return requesParamstEncoding;

    }

    public void setRequesParamstEncoding(String requesParamstEncoding) {

        this.requesParamstEncoding = requesParamstEncoding;

    }

    /**
     * 直接访问URL，可以自主设置是否以POST方式访问
     *
     * @param finalUrl URL
     * @param post     访问方式，是否是Post访问
     * @return
     * @author Lumia_Zeng
     * @date 2016年10月24日 上午10:00:11
     */

    public static Map<String, String> baseAccessURL(String finalUrl, boolean post) {

        Map<String, String> map = new HashMap<String, String>();

        String msg = "访问接口失败";

        String exceptionMsg = null;

        map.put("success", "false");

        logger.info("URL路径：" + finalUrl);

        RequestConfig config = RequestConfig.custom().setSocketTimeout(40000).setConnectTimeout(40000).setConnectionRequestTimeout(40000).build();

        Long startTime = System.currentTimeMillis();

        CloseableHttpClient httpclient = createCloseableHttpClient(finalUrl, config);

        HttpUriRequest httpMethod = null;

        if (post) {

            HttpPost pMethod = new HttpPost(finalUrl);

            pMethod.setConfig(config);

            httpMethod = pMethod;

        } else {

            HttpGet gMethod = new HttpGet(finalUrl);

            gMethod.setConfig(config);

            httpMethod = gMethod;

        }

        try {

            HttpResponse resp = httpclient.execute(httpMethod);

            Long endTime = System.currentTimeMillis();

            logger.info("耗时 " + (endTime - startTime) + " 毫秒");

            Integer statusCode = resp.getStatusLine().getStatusCode();

            String respContent = IOUtils.toString(resp.getEntity().getContent(), responseEncoding);

            respContent = respContent == null ? "" : respContent;

            map.put("success", "true");

            msg = "访问接口成功";

            map.put("responseBody", respContent);

            map.put("responseStatusCode", statusCode.toString());

        } catch (UnsupportedEncodingException e) {

            exceptionMsg = "不支持的编码格式!";

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            exceptionMsg = "ClientProtocolException";

            e.printStackTrace();

        } catch (SocketException e) {

            logger.info("发生了一次SocketException");
            baseAccessURL(finalUrl, post);

        } catch (IOException e) {

            exceptionMsg = "IOException";

            e.printStackTrace();

        } catch (IllegalStateException e) {

            exceptionMsg = "IllegalStateException";

            e.printStackTrace();

        } catch (Exception e) {

            exceptionMsg = "Exception";

            e.printStackTrace();

        }

        map.put("msg", msg);

        map.put("exceptionMsg", exceptionMsg);

        return map;

    }

    /**
     * 以POST方式访问URL，参数以JSON形式传递
     *
     * @param finalUrl
     * @param params
     * @return
     * @author Lumia_Zeng
     * @date 2016年10月27日 下午4:07:49
     */

    public Map<String, String> baseAccessURLJSON(String finalUrl, Map<String, String> params) {

        return accessURL(finalUrl, params, true);

    }

    /**
     * 以POST方式访问URL
     *
     * @param finalUrl url
     * @param params   参数
     * @return
     * @author Lumia_Zeng
     * @date 2016年10月27日 下午4:07:02
     */

    public Map<String, String> baseAccessURL(String finalUrl, Map<String, String> params) {

        return accessURL(finalUrl, params, false);

    }

    /**
     * 访问URL，带参数
     *
     * @param finalUrl URL
     * @param params   参数
     * @param isJson   是否以JSON方式传递参数
     * @return
     * @author Lumia_Zeng
     * @date 2016年10月24日 上午10:02:50
     */

    public Map<String, String> accessURL(String finalUrl, Map<String, String> params, boolean isJson) {

        Map<String, String> map = new HashMap<String, String>();

        String msg = "访问接口失败";

        String exceptionMsg = null;

        map.put("success", "false");

        logger.info("baseAccessURL：" + finalUrl);

        Long startTime = System.currentTimeMillis();

        RequestConfig config = RequestConfig.custom().setSocketTimeout(40000).setConnectTimeout(40000).setConnectionRequestTimeout(40000).build();

        HttpClient httpclient;

        httpclient = createCloseableHttpClient(finalUrl, config);

        HttpUriRequest httpMethod = null;

        HttpPost pMethod = new HttpPost(finalUrl);

        pMethod.setConfig(config);

        try {

            if (isJson && params != null) {

                pMethod.addHeader("Content-Type", "application/json; charset=" + requestEncoding);
                String strParams = JSONObject.toJSONString(params);

                //logger.info("JSON参数：" + strParams);

                StringEntity entity = new StringEntity(strParams, requesParamstEncoding);

                pMethod.setEntity(entity);

            } else {

                List<BasicNameValuePair> nvps = new ArrayList<BasicNameValuePair>();

                if (params != null) {

                    Set<String> set = params.keySet();

                    for (String key : set) {

                        nvps.add(new BasicNameValuePair(key, params.get(key)));

                    }

                }

                logger.info("参数：" + nvps);

                pMethod.setEntity(new UrlEncodedFormEntity(nvps, requesParamstEncoding));

            }

            httpMethod = pMethod;

            HttpResponse resp = httpclient.execute(httpMethod);

            Integer statusCode = resp.getStatusLine().getStatusCode();

            String respContent = IOUtils.toString(resp.getEntity().getContent(), responseEncoding);

            respContent = respContent == null ? "" : respContent;

            map.put("success", "true");

            msg = "访问接口成功";

            map.put("responseBody", respContent);

            map.put("responseStatusCode", statusCode.toString());

        } catch (ConnectTimeoutException e) {

            exceptionMsg = "ConnectTimeoutException";

            e.printStackTrace();

        } catch (SocketTimeoutException e) {

            exceptionMsg = "SocketTimeoutException";

            e.printStackTrace();

        } catch (UnsupportedEncodingException e) {

            exceptionMsg = "UnsupportedEncodingException";

            e.printStackTrace();

        } catch (ClientProtocolException e) {

            exceptionMsg = "ClientProtocolException";

            e.printStackTrace();

        } catch (IOException e) {

            exceptionMsg = "IOException";

            e.printStackTrace();

        } catch (IllegalStateException e) {

            exceptionMsg = "IllegalStateException";

            e.printStackTrace();

        } catch (Exception e) {

            exceptionMsg = "Exception";

            e.printStackTrace();

        } finally {

            Long endTime = System.currentTimeMillis();

            logger.info("耗时 " + (endTime - startTime) + " 毫秒");

        }

        map.put("msg", msg);

        map.put("exceptionMsg", exceptionMsg);

        return map;

    }

    /***

     * 传入JSON数据，以输出流的方式，发送请求

     * @param urlStr 目标地址

     * @param jsonStr JSON数据

     * @param isPost 是否POST

     * @return

     * @author Lumia_Zeng

     * @date 2016年11月1日 下午2:55:10

     */

    public Map<String, String> accessURLByJsonStream(String urlStr, String jsonStr, boolean isPost) {

        logger.info(String.format("以JSON方式访问:%s;是否POST:%s;参数:%s", urlStr, isPost, jsonStr));

        Map<String, String> map = new HashMap<String, String>();

        String msg = "访问接口失败";

        String exceptionMsg = null;

        map.put("success", "false");

        try {

            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setDoOutput(true);

            connection.setDoInput(true);

            if (isPost) {

                connection.setRequestMethod("POST");

            }

            connection.setUseCaches(false);

            connection.setInstanceFollowRedirects(true);

            connection.setRequestProperty("Content-Type", "application/json; charset=" + requestEncoding);

            connection.connect();

            // POST请求

            DataOutputStream out = new DataOutputStream(connection.getOutputStream());

            out.write(jsonStr.getBytes(requesParamstEncoding));

            out.flush();

            out.close();

            // 读取响应

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder sb = new StringBuilder("");

            String lines;

            while ((lines = reader.readLine()) != null) {

                lines = new String(lines.getBytes(), responseEncoding);

                sb.append(lines);

            }

            reader.close();

            Integer statusCode = connection.getResponseCode();

            // 断开连接

            connection.disconnect();

            map.put("success", "true");

            msg = "访问接口成功";

            map.put("responseBody", sb.toString());

            map.put("responseStatusCode", statusCode.toString());

        } catch (MalformedURLException e) {

            exceptionMsg = e.getMessage();

            e.printStackTrace();

        } catch (UnsupportedEncodingException e) {

            exceptionMsg = e.getMessage();

            e.printStackTrace();

        } catch (IOException e) {

            exceptionMsg = e.getMessage();

            e.printStackTrace();

        } catch (Exception e) {

            exceptionMsg = e.getMessage();

            e.printStackTrace();

        } finally {

            map.put("msg", msg);

            map.put("exceptionMsg", exceptionMsg);

        }

        return map;

    }

    /**
     * @param finalUrl
     * @param config
     * @return
     */

    private static CloseableHttpClient createCloseableHttpClient(String finalUrl, RequestConfig config) {

        HttpClientBuilder builder = HttpClients.custom();

        builder.setDefaultRequestConfig(config);

        if (finalUrl.startsWith("https")) {

            try {

                SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy() {

                    // 信任所有

                    public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) {

                        return true;

                    }

                }).build();

                SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(sslContext, SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

                builder.setSSLSocketFactory(sslsf);

            } catch (KeyManagementException e) {

                e.printStackTrace();

            } catch (NoSuchAlgorithmException e) {

                e.printStackTrace();

            } catch (KeyStoreException e) {

                e.printStackTrace();

            } catch (Exception e) {

                e.printStackTrace();

            }

        }

        return builder.build();

    }

}