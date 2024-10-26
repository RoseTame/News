package com.newsultimate.bean;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {

    // url
    @JSONField(name = "url")
    private String url;
    @JSONField(name = "docurl")
    private String docurl;

    // title
    @JSONField(name = "title")
    private String title;

    // image url
    @JSONField(name = "imgurl")
    private String imageUrl;
    @JSONField(name = "img_cns")
    private String imageUrlCns;

    // time
    @JSONField(name = "time")
    private String time;
    @JSONField(name = "pubtime")
    private String pubTime;

    // category
    private String category;

    public String getUrl() {
        return url != null ? url : docurl;
    }
    public String getImageUrl() {
        return imageUrl != null ? imageUrl : imageUrlCns;
    }
    public String getTime() {
        return time != null ? time : pubTime;
    }
}
