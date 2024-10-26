package com.newsultimate.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.newsultimate.R;
import com.squareup.picasso.Picasso;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.jzvd.Jzvd;
import cn.jzvd.JzvdStd;

public class VideoInfoActivity extends AppCompatActivity {
    private TextView video_title_view, video_source_view, video_time_view;
    private LinearLayout layout;
    private JzvdStd jzvdStd;
    private String url, imageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_info);

        initVideoInfoView();

        // 使用AsyncTask执行网络请求
        new FetchVideoInfoTask().execute(url);
    }

    @Override
    public void onBackPressed() {
        if (Jzvd.backPress()) {
            return;
        }
        super.onBackPressed();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Jzvd.releaseAllVideos();
    }

    private void initVideoInfoView() {
        video_title_view = findViewById(R.id.video_title_view);
        video_source_view = findViewById(R.id.video_source_view);
        video_time_view = findViewById(R.id.video_time_view);

        jzvdStd = findViewById(R.id.video_jzvd_view);

        layout = findViewById(R.id.video_info_view);

        Intent intent = getIntent();
        url = intent.getStringExtra("url");
        imageUrl = intent.getStringExtra("imageUrl");
    }

    private class FetchVideoInfoTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            String url = urls[0];
            try {
                Connection.Response response = Jsoup.connect(url)
                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/VERSION Safari/537.36") // 替换 VERSION 为有效的 Chrome 版本号
                        .execute();
                Log.d("response",response.body());
                return response.parse();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }


        @Override
        protected void onPostExecute(Document doc) {
            if (doc != null) {
                // 解析标题
                Elements titleElements = doc.select("div.content_title div.title");
                if (!titleElements.isEmpty()) {
                    video_title_view.setText(titleElements.first().text());
                }

                // 解析来源和时间
                Elements sourceElements = doc.select("div.content_title div.left p");
                if (!sourceElements.isEmpty()) {
                    Element sourceElement = sourceElements.first();
                    String text = sourceElement.text();

                    // 解析来源
                    String source = "来源：" + text.split("来源：")[1].trim();
                    video_source_view.setText(source);

                    // 解析发布时间
                    String timeString = text.split("发布时间：")[1].split("来源：")[0].trim();

                    // 定义两种时间格式
                    SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm");
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    try {
                        // 将旧格式的时间字符串解析为 Date 对象
                        Date date = oldFormat.parse(timeString);

                        // 使用新格式将 Date 对象格式化为时间字符串
                        String time = newFormat.format(date);

                        // 更新 UI
                        video_time_view.setText("时间：" + time);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // 处理解析错误
                        video_time_view.setText("时间：解析错误");
                    }
                }

                // 解析视频封面
                ImageView coverImageView = jzvdStd.posterImageView;
                Picasso.get().load(imageUrl).into(coverImageView);

                // 解析视频
                // 查找所有 script 标签
                Elements scripts = doc.select("script");
                // 遍历 script 标签
                for (Element script : scripts) {
                    // 获取 script 标签的文本内容
                    String scriptText = script.data();

                    // 检查文本内容是否包含 MP4 URL
                    if (scriptText.contains("http://poss-videocloud.cns.com.cn/oss/")) {
                        // 提取 MP4 URL
                        String videoUrl = scriptText.substring(
                                scriptText.indexOf("http://poss-videocloud.cns.com.cn/oss/"),
                                scriptText.indexOf(".mp4") + 4
                        );
                        jzvdStd.setUp(videoUrl, titleElements.first().text());
                        break;
                    }
                }

                // 解析 div.content_desc 内容
                Elements contentDescElements = doc.select("div.content_desc p");
                for (Element contentDescElement : contentDescElements) {
                    // 排除 p.content_editor 和 div.banquan 的内容
                    if (contentDescElement.hasClass("content_editor") || contentDescElement.parent().hasClass("banquan")) {
                        continue;
                    }
                    // 去掉首行的缩进字符
                    String contentText = contentDescElement.text().replaceFirst("^\\s+", "");

                    // 创建 TextView
                    TextView textView = new TextView(VideoInfoActivity.this);
                    textView.setText(contentText);
                    textView.setTextColor(getResources().getColor(R.color.black));
                    textView.setTextSize(18);
                    textView.setPadding(0, 32, 0, 32);

                    // 添加到布局中
                    layout.addView(textView);
                }

            }
        }
    }
}
