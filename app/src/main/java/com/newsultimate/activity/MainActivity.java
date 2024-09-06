package com.newsultimate.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.newsultimate.R;
import com.newsultimate.adapter.NewsPagerAdapter;
import com.newsultimate.db.NewsDataHelper;
import com.newsultimate.fragment.NewsFragment;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private LinearProgressIndicator progressIndicator;
    private SearchView searchView;
    private Handler handler = new Handler(Looper.getMainLooper());

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        viewPager = findViewById(R.id.view_pager);
        tabLayout = findViewById(R.id.tab_layout);
        progressIndicator = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.search_view);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                intent.putExtra("query", query);
                startActivity(intent);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        NewsDataHelper db = new NewsDataHelper(MainActivity.this);
        db.saveNewsToDatabase();

        setupViewPagerAndTabs();
    }


    private void setupViewPagerAndTabs() {
        String[] categories = {"HOT", "DOMESTIC", "FOREIGN", "MILITARY", "SPORTS", "TECHNOLOGY", "FINANCE", "EDUCATION", "CULTURE", "GAME", "ENTERTAINMENT", "DIGITAL", "STOCK", "ART"};
        String[] tabTitles = {"要闻", "国内", "国际", "军事", "体育", "科技", "财经", "教育", "文化", "游戏", "娱乐", "数码", "股票", "艺术"};

        List<Fragment> fragmentList = new ArrayList<>();
        for (String category : categories) {
            fragmentList.add(new NewsFragment(category));
        }

        NewsPagerAdapter pagerAdapter = new NewsPagerAdapter(this, fragmentList);
        viewPager.setAdapter(pagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText(tabTitles[position])).attach();
    }

    public void showProgressBar() {
        progressIndicator.setVisibility(View.VISIBLE);
    }

    public void hideProgressBar() {
        progressIndicator.setVisibility(View.INVISIBLE);
    }
}