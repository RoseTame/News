package com.newsultimate.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsultimate.R;
import com.newsultimate.adapter.NewsRecyclerAdapter;
import com.newsultimate.bean.News;
import com.newsultimate.db.NewsDataHelper;

import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private NewsRecyclerAdapter adapter;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        recyclerView = findViewById(R.id.result_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        adapter = new NewsRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");

        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsDataHelper db = new NewsDataHelper(SearchActivity.this);
                List<News> newsList = db.searchNews(query);
                adapter.updateData(newsList);
                adapter.notifyDataSetChanged();
            }
        });
    }
}