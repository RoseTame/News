package com.newsultimate.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsultimate.R;
import com.newsultimate.adapter.NewsRecyclerAdapter;
import com.newsultimate.bean.News;
import com.newsultimate.db.NewsDataHelper;

import java.util.List;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NewsRecyclerAdapter adapter;
    private NewsDataHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dbHelper = new NewsDataHelper(this);
        List<News> historyList = dbHelper.getHistory();

        adapter = new NewsRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        adapter.updateData(historyList);
    }
}
