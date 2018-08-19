package com.kwon.znshare.service;

import com.kwon.znshare.entity.MeiNv;
import com.kwon.znshare.util.DateUtil;
import com.kwon.znshare.util.HttpClientTools;
import com.kwon.znshare.util.HttpClientUtil;
import org.jsoup.Jsoup;
import org.jsoup.helper.DataUtil;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.*;

public class MeiNvService {

    static String host = "https://www.nvshens.com";

    public static void main(String[] args) throws IOException {
        List<Map<String, String>> typesMapList = new ArrayList<>();
        Map<String, String> map = HttpClientUtil.baseAccessURL("https://www.nvshens.com/gallery/", false);
        if (map.get("responseStatusCode").equals("200")) {
            Document doc = Jsoup.parse(map.get("responseBody"));
            Elements tag_divs = doc.select("div.tag_div");
            for (Element ele : tag_divs) {
                Elements links = ele.select("a[href]");
                Map<String, String> typeMap = null;
                for (Element link : links) {
                    typeMap = new HashMap<>();
                    typeMap.put("href", link.attr("href"));
                    typeMap.put("type", link.html());
                    typesMapList.add(typeMap);
                }
            }
        }

        for (Map<String, String> type : typesMapList) {
            boolean flag = true;
            int page = 1;
            do {
                String url = host + type.get("href");
                if (page != 1) {
                    url += page + ".html";
                }
                map = HttpClientUtil.baseAccessURL(url, false);
                if (map.get("responseStatusCode").equals("200")) {
                    Document doc = Jsoup.parse(map.get("responseBody"));
                    Elements gallerylis = doc.select("li.galleryli");
                    //如果size小于0表示当前类型已经抓取完毕，跳过这个类型获取其他类型
                    if (gallerylis.size() > 0) {
                        List<MeiNv> meiNvList = new ArrayList<>();
                        MeiNv mv;
                        String cover, urlTemp;
                        for (Element gallery : gallerylis) {
                            mv = new MeiNv();
                            cover = gallery.select("img").first().attr("data-original");
                            mv.setCover(cover);
                            mv.setTitle(gallery.select("a.caption").first().html());
                            mv.setKey(cover.substring((cover.indexOf("gallery") + 7), cover.indexOf("cover")));
                            urlTemp = gallery.select("a.caption").attr("href");
                            mv = getMeiNvImgInfo(mv, urlTemp);
                            if (!DateUtil.isSameDate(new Date(), mv.getCreatTime())) {
                                flag = false;
                                break;
                            }
                            meiNvList.add(mv);
                        }

                    } else {
                        flag = false;
                    }
                    page++;
                }
            } while (flag);
        }

    }

    /**
     * 获取图片总数和创建日期信息
     *
     * @param mv
     * @param urlTemp
     * @return
     */
    private static MeiNv getMeiNvImgInfo(MeiNv mv, String urlTemp) {
        Map<String, String> map = HttpClientUtil.baseAccessURL(host + urlTemp, false);
        if (map.get("responseStatusCode").equals("200")) {
            Document doc = Jsoup.parse(map.get("responseBody"));
            String totalTemp = doc.select("div#dinfo > span").first().html();
            String timeTemp = doc.select("div#dinfo").first().html();
            String creatTimeTemp = timeTemp.substring((timeTemp.indexOf("，在 ") + 3), timeTemp.indexOf(" 创建"));
            mv.setTotal(totalTemp.substring(0, totalTemp.indexOf("张照片")));
            mv.setCreatTime(DateUtil.parseStrToDate(creatTimeTemp, "yyyy/MM/dd"));
        }
        return mv;
    }

}
