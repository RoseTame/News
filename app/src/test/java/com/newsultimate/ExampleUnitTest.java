package com.newsultimate;

import static org.junit.Assert.assertEquals;

import com.newsultimate.bean.News;
import com.newsultimate.util.CrawlerUtil;

import org.junit.Test;

import java.util.List;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void testGetVideo(){
        List<News> newsList = CrawlerUtil.getVideo();
        for (News news : newsList) {
            System.out.println(news);
        }
    }
}