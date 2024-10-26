package com.newsultimate.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.TypeReference;
import com.newsultimate.bean.News;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CrawlerUtil {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";

    public static List<News> getNews(String category) {
        String upperCategory = category.toUpperCase();
        switch (upperCategory) {
            case "HOT":
                return getHotNews();
            case "DOMESTIC":
                return getDomesticNews();
            case "FOREIGN":
                return getForeignNews();
            case "MILITARY":
                return getMilitaryNews();
            case "SPORTS":
                return getSportsNews();
            case "TECHNOLOGY":
                return getTechnologyNews();
            case "FINANCE":
                return getFinanceNews();
            case "EDUCATION":
                return getEducationNews();
            case "CULTURE":
                return getCultureNews();
            case "GAME":
                return getGameNews();
            case "ENTERTAINMENT":
                return getEntertainmentNews();
            case "DIGITAL":
                return getDigitalNews();
            case "STOCK":
                return getStockNews();
            case "ART":
                return getArtNews();
            case "VIDEO":
                return getVideo();
            default:
                throw new IllegalArgumentException("Invalid category: " + category);
        }
    }

    public static List<News> getHotNews() {
        String url = "https://news.163.com/special/cm_yaowen20200213/?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("HOT");
        }

        return list;
    }

    public static List<News> getDomesticNews() {
        String url = "https://news.163.com/special/cm_guonei/?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("DOMESTIC");
        }

        return list;
    }

    public static List<News> getForeignNews() {
        String url = "https://news.163.com/special/cm_guoji/?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("FOREIGN");
        }

        return list;
    }

    public static List<News> getMilitaryNews() {
        String url = "https://news.163.com/special/cm_war/?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("MILITARY");
        }

        return list;
    }

    public static List<News> getSportsNews() {
        String url = "https://sports.163.com/special/000587PR/newsdata_n_index.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("SPORTS");
        }

        return list;
    }

    public static List<News> getTechnologyNews() {
        String url = "https://tech.163.com/special/00097UHL/tech_datalist.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("TECHNOLOGY");
        }

        return list;
    }

    public static List<News> getFinanceNews() {
        String url = "https://money.163.com/special/00259BVP/news_flow_index.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("FINANCE");
        }

        return list;
    }

    public static List<News> getEducationNews() {
        String url = "https://edu.163.com/special/002987KB/newsdata_edu_hot.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("EDUCATION");
        }

        return list;
    }

    public static List<News> getCultureNews() {
        String url = "https://culture.163.com/special/datalist_wenhua_api/?callback=data_callback&_=1725323126935";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("CULTURE");
        }

        return list;
    }

    public static List<News> getGameNews() {
        String url = "https://tech.163.com/special/00099BPN/game_newsdata_all.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("GAME");
        }

        return list;
    }

    public static List<News> getEntertainmentNews() {
        String url = "https://ent.163.com/special/000380VU/newsdata_index.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("ENTERTAINMENT");
        }

        return list;
    }

    public static List<News> getDigitalNews() {
        String url = "https://digi.163.com/special/index_datalist/?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("DIGITAL");
        }

        return list;
    }

    public static List<News> getStockNews() {
        String url = "https://money.163.com/special/00259K2L/data_stock_redian.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("STOCK");
        }

        return list;
    }

    public static List<News> getArtNews() {
        String url = "https://art.163.com/special/00999815/art_redian_api.js?callback=data_callback";

        List<News> list = getData(url);
        for (News n : list) {
            n.setCategory("ART");
        }

        return list;
    }

    private static List<News> getData(String url) {
        String json = "";
        OkHttpClient client = new OkHttpClient().newBuilder()
                .build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader("User-Agent", USER_AGENT)
                .build();
        try {
            Response response = client.newCall(request).execute();
            json = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>(); // 返回一个空的列表
        }

        // 尝试处理 JSON 格式
        if (json.startsWith("data_callback(") && json.endsWith(")")) {
            // 移除 'data_callback(' 和 ')'
            json = json.substring("data_callback(".length(), json.length() - 1);
        }

        try {
            List<News> list = JSON.parseObject(
                    json,
                    new TypeReference<List<News>>() {
                    }
            );

            List<News> filteredList = new ArrayList<>();
            for (News news : list) {
                if (news.getTime() != null && !news.getTime().isEmpty() &&
                        news.getImageUrl() != null && !news.getImageUrl().isEmpty()) {
                    filteredList.add(news);
                }
            }
            return filteredList;
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>(); // 返回一个空的列表
        }
    }

    public static List<News> getVideo() {
        String[] urlList = {
                "https://channel.chinanews.com.cn/video/cns/lm/rd-rd.shtml?&pager=0&pagenum=50&t=5_55",
                "http://channel.chinanews.com.cn/video/cns/roll/0/4.shtml?&pager=0&pagenum=50&t=7_42",
        };

        List<News> allNews = new ArrayList<>();
        OkHttpClient client = new OkHttpClient().newBuilder().build();

        for (String url : urlList) {
            String json = "";
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", USER_AGENT)
                    .build();
            try {
                Response response = client.newCall(request).execute();
                json = response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                continue; // 跳过当前 URL，继续处理下一个
            }

            // 提取 JSON 字符串
            String jsonString = "";
            try {
                // 找到 JSON 对象的开始位置
                int startIndex = json.indexOf("specialcnsdata = ");

                // 检查 startIndex 是否找到
                if (startIndex == -1) {
                    throw new IllegalArgumentException("JSON 数据的开始位置未找到");
                }

                startIndex += "specialcnsdata = ".length();

                // 找到 JSON 对象的结束位置
                int endIndex = json.indexOf(";" + '\n' + "newslist = specialcnsdata", startIndex);

                // 检查 endIndex 是否找到
                if (endIndex == -1) {
                    throw new IllegalArgumentException("JSON 数据的结束位置未找到");
                }

                // 提取 JSON 字符串
                jsonString = json.substring(startIndex, endIndex);
                jsonString = jsonString.trim();

            } catch (Exception e) {
                e.printStackTrace();
                continue; // 跳过当前 URL，继续处理下一个
            }

            // 解析 JSON 字符串
            try {
                // 解析为 JSONObject
                JSONObject jsonObject = JSON.parseObject(jsonString);
                // 获取 "docs" 数组
                JSONArray docsArray = jsonObject.getJSONArray("docs");
                // 将 "docs" 数组解析为 List<News>
                List<News> list = JSON.parseArray(docsArray.toJSONString(), News.class);
                for (News news : list) {
                    news.setCategory("VIDEO");
                    String time = news.getTime();
                    SimpleDateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat targetFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

                    Date original = originalFormat.parse(time);
                    String target = targetFormat.format(original);
                    news.setTime(target);
                }
                allNews.addAll(list);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return allNews;
    }

}
