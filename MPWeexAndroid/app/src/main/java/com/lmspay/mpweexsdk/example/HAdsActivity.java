package com.lmspay.mpweexsdk.example;

// Created by saint on 2019-11-18.

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.lmspay.zq.widget.WXAdsWidget;

public class HAdsActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_horizontal);

        // 显示标题栏，一行
        WXAdsWidget adsWidget = findViewById(R.id.mp_row_one);
        adsWidget.setup(true, false, WXAdsWidget.TYPE_ROW_ONE);

        // 显示标题栏，两行
        adsWidget = findViewById(R.id.mp_row_two);
        adsWidget.setup(true, false, WXAdsWidget.TYPE_ROW_TWO);

        // 显示标题栏，三行
        adsWidget = findViewById(R.id.mp_row_three);
        adsWidget.setup(true, false, WXAdsWidget.TYPE_ROW_THREE);

        // 显示标题栏，横向滚动
        adsWidget = findViewById(R.id.mp_horizontal);
        adsWidget.setup(true, false, WXAdsWidget.TYPE_HORIZONTAL_SCROLL);

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("推荐位");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            // 返回键的处理
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
