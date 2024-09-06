package com.newsultimate.util;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchWebPageUtil {
    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) Chrome/128.0.0.0 Safari/537.36";

    public static void fetch(final Context context, final String url, final String subDirectory, final String fileName, final Runnable callback) {
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            OkHttpClient client = new OkHttpClient().newBuilder()
                    .build();
            Request request = new Request.Builder()
                    .url(url)
                    .addHeader("User-Agent", USER_AGENT)
                    .build();

            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

                // 获取文件保存路径
                File dataDirectory = new File(context.getFilesDir(), subDirectory);
                if (!dataDirectory.exists()) {
                    dataDirectory.mkdirs(); // 创建子目录
                }
                File file = new File(dataDirectory, fileName);

                // 写入文件
                try (InputStream in = response.body().byteStream();
                     FileOutputStream out = new FileOutputStream(file)) {
                    byte[] buffer = new byte[4096];
                    int read;
                    while ((read = in.read(buffer)) != -1) {
                        out.write(buffer, 0, read);
                    }
                    out.flush();
                }

                // 打印文件路径
                Log.d("FileLocation", "File saved to: " + file.getAbsolutePath());

                // 在主线程中执行回调
                new Handler(Looper.getMainLooper()).post(callback);
            } catch (IOException e) {
                e.printStackTrace();
                // 在主线程中执行回调
                new Handler(Looper.getMainLooper()).post(callback);
            } finally {
                executor.shutdown(); // 关闭executor以避免内存泄漏
            }
        });
    }

    public static boolean fileExists(Context context, String subDirectory, String fileName) {
        File dataDirectory = new File(context.getFilesDir(), subDirectory);
        if (!dataDirectory.exists()) {
            return false; // 子目录不存在，文件肯定不存在
        }
        File file = new File(dataDirectory, fileName);
        return file.exists();
    }
}