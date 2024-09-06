package com.newsultimate.adapter;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.newsultimate.R;
import com.newsultimate.activity.NewsInfoActivity;
import com.newsultimate.bean.News;
import com.newsultimate.util.FetchWebPageUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder> {

    private List<News> newsList;

    public NewsRecyclerAdapter() {
        this.newsList = new ArrayList<>();
    }

    @NonNull
    @Override
    public NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.news_recycler_row, parent, false
        );
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsViewHolder holder, int position) {
        News news = newsList.get(position);
        holder.titleTextView.setText(news.getTitle());
        holder.timeTextView.setText(news.getTime());

        String imageUrl = news.getImageUrl();
        if (imageUrl != null && !imageUrl.isEmpty()) {
            Picasso.get().load(imageUrl)
                    .error(R.drawable.baseline_newspaper_24) // 加载失败时显示的占位图
                    .into(holder.imageView, new Callback() {
                        @Override
                        public void onSuccess() {
                            // 加载成功
                            holder.imageView.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onError(Exception e) {
                            // 加载失败
                            Log.e("Picasso", "Error loading image", e);
                            holder.imageView.setVisibility(View.INVISIBLE);
                        }
                    });
        } else {
            holder.imageView.setVisibility(View.INVISIBLE);
        }

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), NewsInfoActivity.class);
            String uniqueIdentifier = news.getUrl().substring(news.getUrl().lastIndexOf('/') + 1); // 提取URL中的唯一标识符
            String fileName = uniqueIdentifier + ".html";
            String subDirectory = "source"; // 子目录名称

            // 检查文件是否存在
            if (FetchWebPageUtil.fileExists(v.getContext(), subDirectory, fileName)) {
                Log.d("FileCheck", "File already exists: " + fileName);
                intent.putExtra("fileName", fileName);
                intent.putExtra("subDirectory", subDirectory);
                v.getContext().startActivity(intent);
            } else {
                Log.d("FileCheck", "File does not exist: " + fileName + ", downloading...");

                // 下载文件，并在下载完成后启动新的Activity
                FetchWebPageUtil.fetch(v.getContext(), news.getUrl(), subDirectory, fileName, () -> {
                    intent.putExtra("fileName", fileName);
                    intent.putExtra("subDirectory", subDirectory);
                    v.getContext().startActivity(intent);
                });
            }
        });
    }

    public void updateData(List<News> data) {
        newsList.clear();
        newsList.addAll(data);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return newsList.size();
    }

    class NewsViewHolder extends RecyclerView.ViewHolder {
        TextView titleTextView, timeTextView;
        ImageView imageView;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            titleTextView = itemView.findViewById(R.id.news_title);
            timeTextView = itemView.findViewById(R.id.news_time);
            imageView = itemView.findViewById(R.id.news_image_view);
        }
    }
}