package com.kwon.znshare.util;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

public class XfOcrUtil {

    //通用文字识别
    private static String GENERAL = "http://webapi.xfyun.cn/v1/service/v1/ocr/general";
    //手写文字识别
    private static String HANDWRITING = "http://webapi.xfyun.cn/v1/service/v1/ocr/handwriting";
    //身份证识别
    private static String IDCARD = "http://webapi.xfyun.cn/v1/service/v1/ocr/idcard";
    //银行卡识别
    private static String BANKCARD = "http://webapi.xfyun.cn/v1/service/v1/ocr/bankcard";
    //营业执照识别
    private static String BUSINESS_LICENSE = "http://webapi.xfyun.cn/v1/service/v1/ocr/business_license";
    //名片识别
    private static String BUSINESS_CARD = "http://webapi.xfyun.cn/v1/service/v1/ocr/business_card";

    public static String general(String filePath) {
        String xAppid = "5b35990c";
        String apiKey = "8488f98d9c832d4fec5aba92f934abec";
        String xParam = "{\"language\": \"cn|en\", \"location\": \"false\"}";
        String base64FileStr = "image=" + Base64Util.encodeFileToBase64(filePath);
        String contentType = "application/x-www-form-urlencoded; charset=utf-8";
        String result = xfHelper(XfOcrUtil.GENERAL, xAppid, apiKey, xParam, base64FileStr, contentType);
//        System.out.println("讯飞OCR-->\t" + result);
        JSONObject jsonObject = new JSONObject(result);
        JSONArray words = new JSONArray();
        if(jsonObject.getJSONObject("data").getJSONArray("block").getJSONObject(0).getJSONArray("line").length()>0){
            words = jsonObject.getJSONObject("data").getJSONArray("block").getJSONObject(0).getJSONArray("line").getJSONObject(0).getJSONArray("word");
        }
        String res = "";
        for (int i = 0; i < words.length(); i++) {
            if (words.getJSONObject(i).getString("content").length() == 4) {
                res = words.getJSONObject(i).getString("content");
            }
        }
        return res;
    }

    public static String xfHelper(String xfUrl, String appId, String apiKey, String params, String base64FileStr, String contentType) {
        String curTime = String.valueOf(System.currentTimeMillis() / 1000);
        String xParamBase64 = Base64.getEncoder().encodeToString(params.getBytes());
        String token = apiKey + curTime + xParamBase64;
        String xCheckSum = DigestUtils.md5Hex(token);
        String resBody = "";
        PrintWriter out = null;
        BufferedReader in = null;
        try {
            URL realUrl = new URL(xfUrl);
            // 打开和URL之间的连接
            HttpURLConnection conn = (HttpURLConnection) realUrl.openConnection();
            conn.setReadTimeout(3000);
            conn.setConnectTimeout(5000);
            conn.setRequestMethod("POST");
            // 发送POST请求必须设置如下两行
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestProperty("X-Appid", appId);
            conn.setRequestProperty("X-CurTime", curTime);
            conn.setRequestProperty("X-Param", xParamBase64);
            conn.setRequestProperty("X-CheckSum", xCheckSum);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-type", contentType);
            // 获取URLConnection对象对应的输出流
            out = new PrintWriter(conn.getOutputStream());
            // 发送请求参数
            out.print(base64FileStr);
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

    public static void main(String[] args) {
//        System.out.println(general(FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\JD_authcode.jpg"));

        float a = 5;
        float b = 6;
        System.out.println(a/b);

    }

}
