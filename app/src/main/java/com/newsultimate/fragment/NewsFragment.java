package com.newsultimate.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.newsultimate.R;
import com.newsultimate.activity.MainActivity;
import com.newsultimate.adapter.NewsRecyclerAdapter;
import com.newsultimate.bean.News;
import com.newsultimate.db.NewsDataHelper;

import java.util.List;

public class NewsFragment extends Fragment {
    private String category;
    private List<News> newsList;
    private NewsRecyclerAdapter adapter;

    public NewsFragment(String category) {
        this.category = category;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_news, container, false);
        RecyclerView recyclerView = view.findViewById(R.id.news_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new NewsRecyclerAdapter();
        recyclerView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        loadNewsData();
    }

    private void loadNewsData() {
        if (getActivity() instanceof MainActivity) {
            ((MainActivity) getActivity()).showProgressBar();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                NewsDataHelper db = new NewsDataHelper(getContext());
                newsList = db.getNews(category);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.updateData(newsList);
                        adapter.notifyDataSetChanged();

                        if (getActivity() instanceof MainActivity) {
                            ((MainActivity) getActivity()).hideProgressBar();
                        }
                    }
                });
            }
        }).start();
    }
}