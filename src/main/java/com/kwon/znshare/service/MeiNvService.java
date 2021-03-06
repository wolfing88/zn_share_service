package com.kwon.znshare.service;

import com.kwon.znshare.entity.MeiNv;
import com.kwon.znshare.repository.MeiNvRepository;
import com.kwon.znshare.util.HttpClientUtil;
import com.kwon.znshare.util.Util;
import com.kwon.znshare.vo.CommonVo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MeiNvService {

    protected static final Logger logger = LoggerFactory.getLogger(MeiNvService.class);

    @Autowired
    private MeiNvRepository meiNvRepository;

    private String host = "https://www.nvshens.com";

    public void getMeiNvImg() {
        List<Map<String, String>> typesMapList = new ArrayList<>();
        Map<String, String> map = HttpClientUtil.baseAccessURL("https://www.nvshens.com/gallery/", false);
        if (map.get("responseStatusCode").equals("200")) {
            Document doc = Jsoup.parse(map.get("responseBody"));
            Elements tag_divs = doc.select("div.tag_div");
            for (Element ele : tag_divs) {
                Elements links = ele.select("a[href]");
                Map<String, String> typeMap;
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
                            mv.setType(type.get("type"));
                            mv.setCover(cover);
                            mv.setTitle(gallery.select("a.caption").first().html());
                            mv.setFragment(cover.substring((cover.indexOf("gallery") + 7), cover.indexOf("cover")));
                            urlTemp = gallery.select("a.caption").attr("href");
                            mv = getMeiNvImgInfo(mv, urlTemp);
                            if (Util.isEmpty(mv.getTotal())) {
                                continue;
                            }
                            //只获取当天更新的数据
//                            if (!DateUtil.isSameDate(new Date(), mv.getCreatTime())) {
//                                flag = false;
//                                break;
//                            }
                            meiNvList.add(mv);
                        }
                        if (meiNvList.size() > 0) {
                            System.out.println("保存" + type.get("type") + "图片，第" + page + "页");
//                            for (MeiNv m :meiNvList){
//                                System.out.println(m.getTitle());
//                            }
                            meiNvRepository.saveAll(meiNvList);
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
    private MeiNv getMeiNvImgInfo(MeiNv mv, String urlTemp) {
        Map<String, String> map = HttpClientUtil.baseAccessURL(host + urlTemp, false);
        if (map.get("responseStatusCode").equals("200")) {
            Document doc = Jsoup.parse(map.get("responseBody"));
            Elements els = doc.select("div#dinfo");
            if (els.size() > 0) {
                String totalTemp = els.select("span").first().html();
                String timeTemp = doc.select("div#dinfo").first().html();
                mv.setTotal(totalTemp.substring(0, totalTemp.indexOf("张照片")));
                mv.setCreatDate(timeTemp.substring((timeTemp.indexOf("，在 ") + 3), timeTemp.indexOf(" 创建")));
                mv.setInsertTime(new Date());
            }
        }
        return mv;
    }

    public List<MeiNv> getMeiNvList(CommonVo commonVo) {
        if (commonVo.getType().equals("ALL")) {
            return meiNvRepository.getMeiNvAll(((commonVo.getPage() - 1) * commonVo.getPageSize()), commonVo.getPageSize());
        }
        return meiNvRepository.getMeiNvByTpye(((commonVo.getPage() - 1) * commonVo.getPageSize()), commonVo.getPageSize(), commonVo.getType());
    }

}
