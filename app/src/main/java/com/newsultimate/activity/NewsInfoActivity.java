package com.newsultimate.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class NewsInfoActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_info);

        TextView titleView = findViewById(R.id.titleView);
        TextView sourceView = findViewById(R.id.sourceView);
        TextView timeView = findViewById(R.id.timeView);
        LinearLayout contentLayout = findViewById(R.id.contentLayout); // 用来动态添加图片和文本

        Intent intent = getIntent();
        String fileName = intent.getStringExtra("fileName");
        String subDirectory = intent.getStringExtra("subDirectory");

        try {
            // 从私有存储的子目录读取HTML文件
            File dataDirectory = new File(getFilesDir(), subDirectory);
            File file = new File(dataDirectory, fileName);
            InputStream inputStream = new FileInputStream(file);
            Document doc = Jsoup.parse(inputStream, "UTF-8", "");

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
            // 提取时间
            String time = postInfo.text().split("来源:")[0].trim();
            // 提取来源
            String source = postInfo.select("a").first().ownText(); // ownText() 只获取a标签内的文本
            timeView.setText("时间: " + time);
            sourceView.setText("来源: " + source);

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
                        TextView textView = new TextView(this);
                        Spanned spannedText = Html.fromHtml(element.html(), Html.FROM_HTML_MODE_LEGACY);
                        textView.setText(spannedText);
                        textView.setTextSize(18);
                        textView.setTextColor(ContextCompat.getColor(this, R.color.black));

                        LinearLayout.LayoutParams textParams = new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT);
                        textParams.setMargins(0, 32, 0, 32);  // 上下各8dp的间距
                        textView.setLayoutParams(textParams);
                        contentLayout.addView(textView);
                    }
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
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