package com.kwon.znshare.util;

import javax.swing.filechooser.FileSystemView;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class JdLoginUtil {

    private static Map<String, String> headers = new HashMap<String, String>();
    private static String loginUrl = "https://passport.jd.com/new/login.aspx";
    private static String homeUrl = "https://home.jd.com/";

    static {
        headers.put("Host", "passport.jd.com");
        headers.put("Connection", "keep-alive");
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.181 Safari/537.36");
        headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8");
        headers.put("Accept-Encoding", "gzip, deflate, br");
        headers.put("Accept-Language", "zh-CN,zh;q=0.9,en-US;q=0.8,en;q=0.7");
    }


    /*
    public static String login(String userName, String password) {
        WebClient wc = null;
        String cookieStr = "";
        try {
            wc = new WebClient();
            URL link = new URL(loginUrl);
            WebRequest request = new WebRequest(link);
            request.setCharset(Charset.forName("UTF-8"));
//            request.setAdditionalHeader("Referer", loginUrl);
//            request.setAdditionalHeader("User-Agent", headers.get("User-Agent"));

//            wc.getOptions().setRedirectEnabled(true);   //启动重定向
//            wc.getCookieManager().setCookiesEnabled(true);//开启cookie管理
            wc.getOptions().setJavaScriptEnabled(true);//开启js解析。对于变态网页，这个是必须的
            wc.getOptions().setCssEnabled(false);//开启css解析。对于变态网页，这个是必须的。
            wc.getOptions().setThrowExceptionOnFailingStatusCode(false);
            wc.getOptions().setThrowExceptionOnScriptError(false);
            wc.getOptions().setTimeout(10000);
            for (int i = 0; i < 10; i++) {
                System.out.println("尝试第" + (i + 1) + "次登录");
                //准备工作已经做好了
                HtmlPage page = wc.getPage(request);
                // 根据form的名字获取页面表单，也可以通过索引来获取：page.getForms().get(0)
                final HtmlForm form = page.getForms().get(0);
                //看看需不需要验证码
//                if (needAuthCode(userName)) {
                String code = getAuthCode(page.getElementById("uuid").getAttribute("value"));
                HtmlInput authcode = (HtmlInput) page.getHtmlElementById("authcode");
                System.out.println("验证码是\t" + code);
                authcode.setValueAttribute(code);
//                }
                // 用户名/密码
                HtmlTextInput loginname = form.getInputByName("loginname");
                HtmlPasswordInput nloginpwd = form.getInputByName("nloginpwd");
                loginname.setText(userName);
                nloginpwd.setText(password);
                page.getHtmlElementById("loginsubmit").click();
                // 等待JS驱动dom完成获得还原后的网页
                wc.waitForBackgroundJavaScript(10000);
                //验证是否登录成功（看看能否读取到京豆的html信息）
                page = wc.getPage(homeUrl);
                List<HtmlEmphasis> jdDoulist = page.getByXPath("//*[@id=\"JingdouCount\"]/em");
                System.out.println("jdDoulist\t" + jdDoulist);
                if (jdDoulist.size() > 0) {
                    int jdCount = Integer.valueOf(jdDoulist.get(0).getTextContent());
                    if (jdCount > 0) {
                        //搞定了
                        CookieManager CM = wc.getCookieManager(); //WC = Your WebClient's name
                        for (Cookie cookie : CM.getCookies()) {
                            cookieStr += cookie.toString() + ";";
                        }
                        Constant.cookieStrs.put(userName, cookieStr);
                        System.out.println(cookieStr);
                    }
                    break;
                }
            }
            return cookieStr;
        } catch (Exception e) {
            return cookieStr;
        } finally {
            if (wc != null) {
                wc.close();
            }
        }
    }*/

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

    public static String getAuthCode(String uuid) {
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
                    if (!checkAuthCode(authCode)) {
                        return getAuthCode(uuid);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return authCode;
    }

    private static boolean checkAuthCode(String authCode) {
        String regex = "[0-9A-Z]{4}";
        return Pattern.matches(regex, authCode);
    }

    public static void main(String[] args) throws IOException {
        //login("wolfing88", "a871124");
    }

}
