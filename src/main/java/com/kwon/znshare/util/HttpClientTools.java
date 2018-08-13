package com.kwon.znshare.util;

import org.apache.http.Consts;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class HttpClientTools {

    public static Map<String, String> httpGet(String url) {
        return httpGet(url, new HashMap<String, String>(), new BasicCookieStore());
    }

    public static Map<String, String> httpGet(String url, Map<String, String> headers, CookieStore cookieStore) {
        HttpGet get = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            get.addHeader(entry.getKey(), entry.getValue());
        }
        return request(get, cookieStore);
    }

    public static Map<String, String> httpPost(String url, Map<String, String> headers, Map<String, String> params) {
        return httpPost(url, headers, params, new BasicCookieStore());
    }

    public static Map<String, String> httpPost(String url, Map<String, String> headers, Map<String, String> params, CookieStore cookieStore) {
        List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
        entity.setContentType("application/x-www-form-urlencoded");
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }
        return request(post, cookieStore);
    }

    public static Map<String, String> request(HttpRequestBase method, CookieStore cookieStore) {
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setRedirectsEnabled(false).setConnectTimeout(10000).build();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        Map<String, String> resultMap = new HashMap<>();

        try {
            response = httpClient.execute(method, context);//执行GET/POST请求
            resultMap.put("code", String.valueOf(response.getStatusLine().getStatusCode()));
            if (response.getStatusLine().getStatusCode() == 301 || response.getStatusLine().getStatusCode() == 302) {
                Header header = response.getFirstHeader("Location");
                if (null != header) {
                    resultMap.put("content", header.getValue());
                }
                return resultMap;
            }
            HttpEntity entity = response.getEntity();//获取响应实体
            if (entity != null) {
                String cookies = "";
                Header[] headers = response.getHeaders("Set-Cookie");
                for (Header header : headers) {
                    cookies += header.getValue();
                }
                Charset charset = ContentType.getOrDefault(entity).getCharset();
                resultMap.put("content", EntityUtils.toString(entity, charset));
                resultMap.put("cookies", cookies);
                EntityUtils.consume(entity);
            }
        } catch (ConnectTimeoutException e) {
            PrintUtil.println("发生了一次 ConnectTimeoutException ！");
            return request(method, cookieStore);
        } catch (SocketTimeoutException e) {
            PrintUtil.println("发生了一次 SocketTimeoutException ！");
            return request(method, cookieStore);
        }catch(UnknownHostException e){
            PrintUtil.println("发生了一次 UnknownHostException ！");
            return request(method, cookieStore);
        } catch (Exception e) {
            resultMap.put("code", "10008");
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return resultMap;
    }

    public static boolean downloadGet(String url, Map<String, String> headers, String filePath) {
        HttpGet get = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            get.addHeader(entry.getKey(), entry.getValue());
        }
        return download(get, filePath);
    }

    public static boolean downloadPost(String url, Map<String, String> headers, Map<String, String> params, String filePath) {
        List<NameValuePair> valuePairs = new LinkedList<NameValuePair>();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            valuePairs.add(new BasicNameValuePair(entry.getKey(), entry.getValue()));
        }
        UrlEncodedFormEntity entity = new UrlEncodedFormEntity(valuePairs, Consts.UTF_8);
        entity.setContentType("application/x-www-form-urlencoded");
        HttpPost post = new HttpPost(url);
        post.setEntity(entity);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            post.addHeader(entry.getKey(), entry.getValue());
        }
        return download(post, filePath);
    }

    public static boolean download(HttpRequestBase method, String filePath) {
        RequestConfig globalConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).setRedirectsEnabled(false).build();
        CookieStore cookieStore = new BasicCookieStore();
        HttpClientContext context = HttpClientContext.create();
        context.setCookieStore(cookieStore);
        CloseableHttpClient httpClient = HttpClients.custom().setDefaultRequestConfig(globalConfig).setDefaultCookieStore(cookieStore).build();
        CloseableHttpResponse response = null;
        OutputStream out = null;
        try {
            response = httpClient.execute(method, context);//执行GET/POST请求
            HttpEntity entity = response.getEntity();//获取响应实体
            out = new FileOutputStream(new File(filePath));
            entity.writeTo(out);
            EntityUtils.consume(entity);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                    out.close();
                    httpClient.close();
                }
                if (out != null) {
                    out.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public static boolean getFile(String urlPath, Map<String, String> headers, Map<String, String> params, String filePath, String cookie, String requestType) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = null;
        OutputStream os = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlPath);
            // 获得连接对象
            connection = (HttpURLConnection) url.openConnection();
            // 设置属性
            connection.setRequestMethod(requestType);
            connection.setReadTimeout(5000);
            connection.setConnectTimeout(5000);
            // 设置输入流和输出流,都设置为true
            connection.setDoInput(true);
            if (requestType.equals("POST")) {
                connection.setDoOutput(true);
            }
            //设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
            //connection.setRequestProperty("Cookie", cookie);
            // 判断是否要往服务器传递参数。如果不传递参数，那么就没有必要使用输出流了。
            if (null != params && params.size() > 0) {
                String pa = "";
                for (Map.Entry<String, String> entry : params.entrySet()) {
                    pa += pa.length() == 0 ? entry.getKey() + "=" + entry.getValue() : "&" + entry.getKey() + "=" + entry.getValue();
                }
                // 设置文件长度
                connection.setRequestProperty("Content-Length", String.valueOf(pa.getBytes().length));
                bos = new BufferedOutputStream(connection.getOutputStream());
                bos.write(pa.getBytes());
                bos.flush();
                bos.close();
            }

            // 判断访问网络的连接状态
            if (connection.getResponseCode() == 200) {
                os = new FileOutputStream(filePath);
                bis = new BufferedInputStream(connection.getInputStream());
                int c = 0;
                byte[] buffer = new byte[8 * 1024];
                while ((c = bis.read(buffer)) != -1) {
                    os.write(buffer, 0, c);
                    os.flush();
                }
                // 将获取到的输入流转成字节数组
                return true;
            }
        } catch (UnknownHostException e) {
            PrintUtil.println("发生了一次 UnknownHostException ！");
            return getFile(urlPath, headers, params, filePath, cookie, requestType);
        } catch (SocketTimeoutException e) {
            PrintUtil.println("发生了一次 SocketTimeoutException ！");
            return getFile(urlPath, headers, params, filePath, cookie, requestType);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bis != null) {
                    bis.close();
                }
                if (bos != null) {
                    bos.close();
                }
                if (os != null) {
                    os.close();
                }
                connection.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }
}
