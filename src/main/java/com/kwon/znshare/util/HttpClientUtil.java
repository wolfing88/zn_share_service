package com.kwon.znshare.util;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

public class HttpClientUtil {

    public static String get(String url, Map<String, String> headers) {
        CloseableHttpResponse response = null;
        String result = "";
        HttpGet get = new HttpGet(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            get.addHeader(entry.getKey(), entry.getValue());
        }
        CloseableHttpClient httpclient = HttpClients.custom().build();
        try {
            response = httpclient.execute(get);
            HttpEntity entity = response.getEntity();//获取响应实体
            Charset charset = ContentType.getOrDefault(entity).getCharset();
            result = EntityUtils.toString(entity, charset);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }

}