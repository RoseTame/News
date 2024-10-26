package com.newsultimate.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.newsultimate.R;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class NewsInfoActivity extends AppCompatActivity {

    private TextView titleView;
    private TextView sourceView;
    private TextView timeView;
    private LinearLayout contentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);

        titleView = findViewById(R.id.titleView);
        sourceView = findViewById(R.id.sourceView);
        timeView = findViewById(R.id.timeView);
        contentLayout = findViewById(R.id.contentLayout); // 用来动态添加图片和文本

        Intent intent = getIntent();
        String url = intent.getStringExtra("url");

        // 使用AsyncTask执行网络请求
        new FetchNewsTask().execute(url);
    }

    private class FetchNewsTask extends AsyncTask<String, Void, Document> {

        @Override
        protected Document doInBackground(String... urls) {
            String url = urls[0];
            try {
                Connection.Response response = Jsoup.connect(url)
                        .header("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36")
                        .execute();
                return response.parse();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(Document doc) {
            if (doc != null) {
                // 清理不需要的标签，但保留样式和脚本
                doc.select("head").remove();
                doc.select("style").remove();
                doc.select("script").remove();

                // 提取标题
                Element titleElement = doc.selectFirst(".post_title, .otitle");
                if (titleElement != null) {
                    String title = titleElement.text();
                    titleView.setText(title);
                }

                // 提取来源和时间
                Element postInfo = doc.selectFirst(".post_info");
                if (postInfo != null) {
                    // 提取时间
                    String timeString = postInfo.text().split("来源:")[0].trim();

                    // 定义两种时间格式
                    SimpleDateFormat oldFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    SimpleDateFormat newFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");

                    try {
                        // 将旧格式的时间字符串解析为 Date 对象
                        Date date = oldFormat.parse(timeString);

                        // 使用新格式将 Date 对象格式化为时间字符串
                        String time = newFormat.format(date);

                        // 提取来源
                        String source = postInfo.select("a").first().ownText(); // ownText() 只获取a标签内的文本

                        // 更新 UI
                        timeView.setText("时间：" + time);
                        sourceView.setText("来源：" + source);
                    } catch (ParseException e) {
                        e.printStackTrace();
                        // 处理解析错误
                        timeView.setText("时间：解析错误");
                    }
                }


                // 提取正文和图片
                Elements paragraphs = doc.select(".post_body > *");
                for (Element element : paragraphs) {
                    if (element.tagName().equals("p")) {
                        if (element.hasClass("f_center")) {
                            // 处理图片
                            Element imgElement = element.selectFirst("img");
                            if (imgElement != null) {
                                String imgUrl = imgElement.absUrl("src");  // 获取绝对URL
                                addImageView(contentLayout, imgUrl);
                            }
                        } else {
                            // 处理普通段落
                            TextView textView = new TextView(NewsInfoActivity.this);
                            Spanned spannedText = Html.fromHtml(element.html(), Html.FROM_HTML_MODE_LEGACY);
                            textView.setText(spannedText);
                            textView.setTextSize(18);
                            textView.setTextColor(ContextCompat.getColor(NewsInfoActivity.this, R.color.black));

                            LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                    LinearLayout.LayoutParams.MATCH_PARENT,
                                    LinearLayout.LayoutParams.WRAP_CONTENT);
                            textParams.setMargins(0, 32, 0, 32);  // 上下各8dp的间距
                            textView.setLayoutParams(textParams);
                            contentLayout.addView(textView);
                        }
                    }
                }
            }
        }
    }

    private void addImageView(ViewGroup parent, String imageUrl) {
        ImageView imageView = new ImageView(this);
        // 使用Picasso加载图片
        Picasso.get().load(imageUrl).into(new Target() {
            @Override
            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                // 计算图片宽高比
                float imageAspectRatio = (float) bitmap.getWidth() / bitmap.getHeight();

                // 设置ImageView的布局参数，使其宽度匹配TextView，且高度自适应
                LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        (int) (parent.getWidth() / imageAspectRatio));
                imageParams.setMargins(0, 8, 0, 8); // 图片上下各8dp的间距
                imageView.setLayoutParams(imageParams);

                // 设置图片
                imageView.setImageBitmap(bitmap);
            }

            @Override
            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                imageView.setImageDrawable(errorDrawable); // 设置错误图片
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
                imageView.setImageDrawable(placeHolderDrawable); // 图片加载前的占位图
            }
        });

        parent.addView(imageView);
    }
}