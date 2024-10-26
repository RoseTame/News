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
import com.newsultimate.activity.VideoInfoActivity;
import com.newsultimate.bean.News;
import com.newsultimate.db.NewsDataHelper;
import com.newsultimate.util.TimeUtil;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NewsRecyclerAdapter extends RecyclerView.Adapter<NewsRecyclerAdapter.NewsViewHolder> {

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

        String time = news.getTime();
        String formatTime;
        formatTime = TimeUtil.format(time, "MM/dd/yyyy HH:mm:ss");
        holder.timeTextView.setText(formatTime);

        Picasso.get().load(news.getImageUrl())
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


        holder.itemView.setOnClickListener(v -> {
            Intent intent;
            if (news.getCategory().equals("VIDEO")) {
                intent = new Intent(v.getContext(), VideoInfoActivity.class);
            } else {
                intent = new Intent(v.getContext(), NewsInfoActivity.class);
            }

            // 保存新闻到历史记录
            NewsDataHelper dbHelper = new NewsDataHelper(v.getContext());
            dbHelper.saveToHistory(news);

            intent.putExtra("url", news.getUrl());
            intent.putExtra("imageUrl", news.getImageUrl());
            v.getContext().startActivity(intent);
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


}