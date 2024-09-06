package com.newsultimate.bean;

import com.alibaba.fastjson2.annotation.JSONField;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class News {
    @JSONField(name = "docurl")
    private String url;

    @JSONField(name = "title")
    private String title;

    @JSONField(name = "imgurl")
    private String imageUrl;

    @JSONField(name = "time")
    private String time;

    private String category;
}
