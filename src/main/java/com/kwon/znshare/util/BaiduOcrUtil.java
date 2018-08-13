package com.kwon.znshare.util;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.swing.filechooser.FileSystemView;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class BaiduOcrUtil {

    //通用文字识别
    private static String GENERAL = "https://aip.baidubce.com/rest/2.0/ocr/v1/accurate_basic";

    public static String general(String filePath) {
        String accessToken = getAuth("FKocpBFvKqGqHzmCZ9xgz399", "RwVT2TzkSwrf4HMPb15v2h2HSW4pwwCz");
        String url = BaiduOcrUtil.GENERAL + "?access_token=" + accessToken;
        String contentType = "application/x-www-form-urlencoded";
        String base64FileStr = Base64Util.encodeFileToBase64(filePath);
        String params = getURLEncoderParams("image", base64FileStr);
        params += "&" + getURLEncoderParams("probability", "true");
        String result = baiduOcrHelper(url, params, contentType);
//        System.out.println("百度OCR-->\t" + result);
        JSONObject jsonObject = new JSONObject(result);
        JSONArray ja = jsonObject.getJSONArray("words_result");
        String res = "";
        for (int i = 0; i < ja.length(); i++) {
            if (ja.getJSONObject(i).getString("words").length() == 4) {
                res = ja.getJSONObject(i).getString("words");
            }
        }
        return res;
    }

    public static String baiduOcrHelper(String url, String params, String contentType) {
        String resBody = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-type", contentType);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(params);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            // 将返回的输入流转换成字符串
            InputStream inputStream = conn.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "utf-8");
            in = new BufferedReader(inputStreamReader);
            String line;
            while ((line = in.readLine()) != null) {
                resBody += line;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return resBody;
    }

    public static String getAuth(String ak, String sk) {
        // 获取token地址
        String authHost = "https://aip.baidubce.com/oauth/2.0/token?";
        String getAccessTokenUrl = authHost
                // 1. grant_type为固定参数
                + "grant_type=client_credentials"
                // 2. 官网获取的 API Key
                + "&client_id=" + ak
                // 3. 官网获取的 Secret Key
                + "&client_secret=" + sk;
        String accessToken = "";
        try {
            URL realUrl = new URL(getAccessTokenUrl);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();
            // 定义 BufferedReader输入流来读取URL的响应
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String result = "";
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }

            JSONObject jsonObject = new JSONObject(result);
            accessToken = jsonObject.getString("access_token");
            return accessToken;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return accessToken;
    }

    public static String getURLEncoderParams(String paramType, String paramData) {
        try {
            return URLEncoder.encode(paramType, "UTF-8") + "=" + URLEncoder.encode(paramData, "UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static void main(String[] args) {

        System.out.println(general(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\JD_authcode.jpg"));

    }

}