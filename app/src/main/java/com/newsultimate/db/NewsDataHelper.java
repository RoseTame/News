package com.newsultimate.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.Nullable;

import com.newsultimate.bean.News;
import com.newsultimate.util.CrawlerUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class NewsDataHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "news";
    private static final String COLUMN_URL = "url";
    private static final String COLUMN_TITLE = "title";
    private static final String COLUMN_IMAGE_URL = "image_url";
    private static final String COLUMN_CATEGORY = "category";
    private static final String COLUMN_TIME = "time";

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    public NewsDataHelper(@Nullable Context context) {
        super(context, TABLE_NAME + ".db", null, 1);
    }

    /* 只会调用一次, 没有数据库启动程序时 */
    /* 创建数据库 */
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql = "CREATE TABLE " + TABLE_NAME + " ("
                + COLUMN_URL + " TEXT NOT NULL, "
                + COLUMN_TITLE + " TEXT NOT NULL, "
                + COLUMN_IMAGE_URL + " TEXT, "
                + COLUMN_TIME + " TEXT, "
                + COLUMN_CATEGORY + " TEXT NOT NULL, "
                + "PRIMARY KEY (" + COLUMN_URL + ", " + COLUMN_CATEGORY + ")"
                + ");";

        db.execSQL(sql);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public String insertList(List<News> list) {
        SQLiteDatabase db = this.getWritableDatabase();
        int batchSize = 1000;
        int totalSize = list.size();
        int processed = 0;
        boolean hasError = false;

        while (processed < totalSize) {
            int end = Math.min(processed + batchSize, totalSize);
            List<News> batch = list.subList(processed, end);
            db.beginTransaction();
            try {
                for (News news : batch) {
                    ContentValues cv = new ContentValues();
                    cv.put(COLUMN_URL, news.getUrl());
                    cv.put(COLUMN_TITLE, news.getTitle());
                    cv.put(COLUMN_IMAGE_URL, news.getImageUrl());
                    cv.put(COLUMN_TIME, news.getTime());
                    cv.put(COLUMN_CATEGORY, news.getCategory());

                    try {
                        long result = db.insertOrThrow(TABLE_NAME, COLUMN_URL, cv);
                        if (result == -1) {
                            hasError = true;
                            break;
                        }
                    } catch (SQLiteConstraintException e) {
                        // 忽略 UNIQUE 约束失败的插入
                        Log.w("NewsDataHelper", "Skipping duplicate news: " + news.getUrl());
                    }
                }
                if (!hasError) {
                    db.setTransactionSuccessful();
                }
            } finally {
                db.endTransaction();
//                db.close();
            }
            processed += batchSize;
        }

        return hasError ? "FAIL" : "SUCCESS";
    }

    @SuppressLint("Range")
    public List<News> getNews(String category) {
        List<News> newsList = new ArrayList<>();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_CATEGORY + " = ? ORDER BY " + COLUMN_TIME + " DESC;";
        String[] selectionArgs = {category};

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(query, selectionArgs);

        if (cursor.moveToFirst()) {
            do {
                News news = new News();
                news.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                news.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                news.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                news.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                news.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                newsList.add(news);
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();

        return newsList;
    }


    public void saveNewsToDatabase() {
        new Thread(() -> {
            ExecutorService executorService = Executors.newFixedThreadPool(5); // 创建固定大小的线程池
            List<Future<List<News>>> futures = new ArrayList<>();

            List<String> categoryList = Arrays.asList(
                    "HOT", "VIDEO", "DOMESTIC", "FOREIGN", "MILITARY", "SPORTS",
                    "TECHNOLOGY", "FINANCE", "EDUCATION", "CULTURE", "GAME",
                    "ENTERTAINMENT", "DIGITAL", "STOCK", "ART"
            );

            // 提交任务到线程池
            for (String category : categoryList) {
                Future<List<News>> future = executorService.submit(() -> {
                    try {
                        return CrawlerUtil.getNews(category);
                    } catch (IllegalArgumentException e) {
                        Log.e("NewsDataHelper", "Invalid category: " + category, e);
                        return new ArrayList<>();
                    }
                });

                futures.add(future);
            }

            // 等待所有任务完成
            List<News> allNews = new ArrayList<>();
            for (Future<List<News>> future : futures) {
                try {
                    synchronized (allNews) {
                        allNews.addAll(future.get()); // 获取并合并结果
                    }
                } catch (InterruptedException | ExecutionException e) {
                    Log.e("NewsDataHelper", "Failed to get news data", e);
                }
            }

            // 批量写入数据库
            insertList(allNews);

            // 关闭线程池
            executorService.shutdown();

            // 在主线程中处理结果
            mainHandler.post(() -> {
                Log.i("NewsDataHelper", "news saved to database.");
            });
        }).start();
    }

    public void saveToHistory(News news) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues cv = new ContentValues();
            cv.put(COLUMN_URL, news.getUrl());
            cv.put(COLUMN_TITLE, news.getTitle());
            cv.put(COLUMN_IMAGE_URL, news.getImageUrl());
            cv.put(COLUMN_TIME, news.getTime());
            cv.put(COLUMN_CATEGORY, "HISTORY");  // 设置历史记录类别

            try {
                long result = db.insertOrThrow(TABLE_NAME, null, cv);
                if (result == -1) {
                    Log.w("NewsDataHelper", "Failed to insert history: " + news.getUrl());
                }
            } catch (SQLiteConstraintException e) {
                // 忽略 UNIQUE 约束失败的插入
                Log.w("NewsDataHelper", "Skipping duplicate history: " + news.getUrl());
            }

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    public List<News> getHistory() {
        return getNews("HISTORY");
    }


    @SuppressLint("Range")
    public List<News> searchNews(String query) {
        List<News> resultList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        String sql = "SELECT * FROM news WHERE title LIKE ?";
        Cursor cursor = db.rawQuery(sql, new String[]{"%" + query + "%"}); // 使用 % 来匹配任意字符
        if (cursor.moveToFirst()) {
            do {
                News news = new News();
                news.setUrl(cursor.getString(cursor.getColumnIndex(COLUMN_URL)));
                news.setTitle(cursor.getString(cursor.getColumnIndex(COLUMN_TITLE)));
                news.setImageUrl(cursor.getString(cursor.getColumnIndex(COLUMN_IMAGE_URL)));
                news.setTime(cursor.getString(cursor.getColumnIndex(COLUMN_TIME)));
                news.setCategory(cursor.getString(cursor.getColumnIndex(COLUMN_CATEGORY)));
                // 添加更多字段
                resultList.add(news);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return resultList;
    }

    public boolean delete(Context context) {
        return context.deleteDatabase(TABLE_NAME + ".db");
    }
}

