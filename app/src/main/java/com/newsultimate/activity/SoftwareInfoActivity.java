package com.newsultimate.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.newsultimate.R;
import com.newsultimate.db.NewsDataHelper;

import es.dmoral.toasty.Toasty;

public class SoftwareInfoActivity extends AppCompatActivity {
    private Button btn_delete;
    private Button btn_history;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_software_info);

        Toasty.Config.getInstance()
                .tintIcon(true)
                .setTextSize(16)
                .allowQueue(true)
                .setGravity(Gravity.BOTTOM, 0, 200)
                .supportDarkTheme(true)
                .setRTL(false)
                .apply(); // required

        btn_delete = findViewById(R.id.btn_delete_database);
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NewsDataHelper db = new NewsDataHelper(SoftwareInfoActivity.this);
                if (db.delete(SoftwareInfoActivity.this)) {
                    Toasty.success(SoftwareInfoActivity.this, "清除成功", Toasty.LENGTH_LONG).show();
                } else {
                    Toasty.error(SoftwareInfoActivity.this, "清除失败", Toasty.LENGTH_LONG).show();
                }
                db.close();
                finish();
            }
        });

        btn_history = findViewById(R.id.btn_history);
        btn_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SoftwareInfoActivity.this,HistoryActivity.class);
                startActivity(intent);
            }
        });
    }

}