package com.kwon.znshare.util;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.CookieStore;
import org.apache.http.impl.client.BasicCookieStore;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.swing.filechooser.FileSystemView;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JdLogin {

    private static CookieStore cookieStore = new BasicCookieStore();
    private static Map<String, String> headers = new HashMap<String, String>();

    static {
        headers.put("Host", "passport.jd.com");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
    }

    public CookieStore login(String userName, String password) {
        Map<String, String> params = getLoginData();
        String authCode = "";
//        if (needAuthCode(userName)) {
            authCode = getAuthCode(params.get("uuid"));
//        }
        String loginUrl = "https://passport.jd.com/uc/loginService?uuid=" + params.get("uuid") + "&r=" + Math.random() + "&versoin=2015";
        String nloginpwd = encryptPwd(password, params.get("pubKey"));
        if(null == nloginpwd){
            cookieStore = new BasicCookieStore();
            return login(userName,password);
        }
        params.put("authcode", authCode);
        params.put("loginname", userName);
        params.put("nloginpwd", nloginpwd);
        headers.put("Host", "passport.jd.com");
        headers.put("Origin", "https://passport.jd.com");
        headers.put("X-Requested-With", "XMLHttpRequest");
        Map<String, String> map = HttpClientTools.httpPost(loginUrl, headers, params, cookieStore);
        System.out.println(map);
        if (!map.get("content").contains("success")) {
            return login(userName, password);
        }
        System.out.println("登录成功");
        return cookieStore;
    }

    public static Map<String, String> getLoginData() {
        Map<String, String> res = HttpClientTools.httpGet("https://passport.jd.com/new/login.aspx", headers, cookieStore);
        Document doc = Jsoup.parse(res.get("content"));
        Elements elements = doc.select("form[id=formlogin] input[type=hidden]");
        Map<String, String> map = new HashMap<String, String>();
        String k, v;
        for (Element input : elements) {
            k = input.attr("name");
            v = input.attr("value");
            if (StringUtils.isNotBlank(k)) {
                map.put(k, v);
            }
        }
        map.put("eid", "XJAWIOCQHEXZSYPHKMLGUTWBEP65XN5CHU74XPBRKZSYFNJWG2IKVGGGR6G6ULDKASHFVTO42PNVHBVGYYLNGB77IA");
        map.put("fp", "84ecf1100c625a2256dc64ff97ff1e77");
        return map;
    }

    private static String getAuthCode(String uuid) {
        String authCode = "";
        try {
            String imageFile = FileSystemView.getFileSystemView().getHomeDirectory().getPath() + "\\JD_authcode.jpg";
            String url = "https://authcode.jd.com/verify/image";
            Map<String, String> params = new HashMap<>();
            params.put("a", "1");
            params.put("acid", uuid);
            params.put("a", uuid);
            params.put("yys", String.valueOf(System.currentTimeMillis()));
            headers.put("Host", "authcode.jd.com");
            headers.put("Referer", "https://passport.jd.com/uc/login");
            boolean flag = HttpClientTools.getFile(url, headers, params, imageFile, "", "POST");
            if (flag) {
                authCode = XfOcrUtil.general(imageFile);
                if (!checkAuthCode(authCode)) {
                    authCode = BaiduOcrUtil.general(imageFile);
                    if(!checkAuthCode(authCode)){
                       return getAuthCode(uuid);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authCode;
    }

    private static boolean checkAuthCode(String authCode){
        String regex = "[0-9A-Z]{4}";
        return Pattern.matches(regex, authCode);
    }


//    private static boolean needAuthCode(String userName) {
//        String url = "https://passport.jd.com/uc/showAuthCode";
//        url += "?r=" + Math.random() + "&version=2015";
//        Map<String, String> params = new HashMap<String, String>();
//        params.put("loginName", userName);
//        Map<String, String> res = HttpClientTools.httpPost(url, headers, params, new BasicCookieStore());
//        if (res.get("code").equals("200")) {
//            String s = res.get("content").substring(1, res.get("content").length() - 1);
//            JSONObject jsonObject = new JSONObject(s);
//            return jsonObject.getBoolean("verifycode");
//        } else {
//            return needAuthCode(userName);
//        }
//    }

    public static String encryptPwd(String data, String publicKey) {
        return RSAUtils.publicEncrypt(data, RSAUtils.getPublicKey(publicKey));
    }

}
