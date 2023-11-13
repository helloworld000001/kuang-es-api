package com.kuang.utils;

import com.kuang.pojo.Content;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.http.converter.json.GsonBuilderUtils;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @auther 陈彤琳
 * @Description $
 * 2023/11/10 22:53
 */
@Component
public class HtmlParseUtil {
    public static void main(String[] args) throws Exception {
        new HtmlParseUtil().parseJD("心理学").forEach(System.out::println);
    }
    public List<Content> parseJD(String keywords) throws Exception {
        // 获取请求 https://search.jd.com/Search?keyword=java
        String url = "https://search.jd.com/Search?keyword=" + keywords;

        // 解析网页
        Document document = Jsoup.parse(new URL(url), 30000);
        // 所有在js可以使用的方法这里都能用
        Element element = document.getElementById("J_goodsList");
        System.out.println(element.html());
        // 获取所有的li标签
        Elements elements = element.getElementsByTag("li");

        List<Content> goodsList = new ArrayList<>();
        // 获取元素中的所有的内容
        for (Element el : elements) {
            // 对于这种图片特别多的网站，所有的图片都是延迟加载的data-lazy-img
            String img = el.getElementsByTag("img").eq(0).attr("data-lazy-img");
            String price = el.getElementsByClass("p-price").eq(0).text();
            String title = el.getElementsByClass("p-name").eq(0).text();

            Content content = new Content();
            content.setImg(img);
            content.setPrice(price);
            content.setTitle(title);

            goodsList.add(content);
        }
        return goodsList;
    }
}
