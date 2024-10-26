package com.newsultimate.activity;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsultimate.R;
import com.newsultimate.adapter.NewsRecyclerAdapter;
import com.newsultimate.bean.News;
import com.newsultimate.db.NewsDataHelper;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {
    private NewsRecyclerAdapter adapter;
    private RecyclerView recyclerView;
    private SearchView searchView;
    private NewsDataHelper db;
    private List<News> newsList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        initSearchView();

        Intent intent = getIntent();
        String query = intent.getStringExtra("query");

        newsList = db.searchNews(query);
        adapter.updateData(newsList);
        adapter.notifyDataSetChanged();

    }

    private void initSearchView() {
        recyclerView = findViewById(R.id.result_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        adapter = new NewsRecyclerAdapter();
        recyclerView.setAdapter(adapter);

        db = new NewsDataHelper(SearchActivity.this);

        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                newsList = db.searchNews(query);
                adapter.updateData(newsList);
                adapter.notifyDataSetChanged();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
}