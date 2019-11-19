package com.lmspay.mpweex.android;

// Created by saint on 2019-11-18.

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.lmspay.mpweex.MPWeexSDK;
import com.lmspay.mpweex.ui.WXAdsView;

import java.util.ArrayList;
import java.util.List;

public class HAdsActivity extends AppCompatActivity {
    private WXAdsView mRecommendAdsView_4X4;
    private WXAdsView mRecommendAdsView_4X8;

    private WXAdsView mRecentAdsView_4X4;
    private WXAdsView mRecentAdsView_4X8;

    private WXAdsView mMyAdsView_4X4;
    private WXAdsView mMyAdsView_4X8;

    private List<WXAdsView> mAdsViews;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ads_horizontal);

        mAdsViews = new ArrayList<>();

        mRecommendAdsView_4X4 = findViewById(R.id.recommendMP_4X4);
        mAdsViews.add(mRecommendAdsView_4X4);
        setupAds(mRecommendAdsView_4X4, 4, "/mpweex/getrecommendlist");

        mRecommendAdsView_4X8 = findViewById(R.id.recommendMP_4X8);
        mAdsViews.add(mRecommendAdsView_4X8);
        setupAds(mRecommendAdsView_4X8, 8, "/mpweex/getrecommendlist");

        mRecentAdsView_4X4 = findViewById(R.id.recentMP_4X4);
        mAdsViews.add(mRecentAdsView_4X4);
        setupAds(mRecentAdsView_4X4, 4, "/users/getaccesslog");

        mRecentAdsView_4X8 = findViewById(R.id.recentMP_4X8);
        mAdsViews.add(mRecentAdsView_4X8);
        setupAds(mRecentAdsView_4X8, 8, "/users/getaccesslog");

        mMyAdsView_4X4 = findViewById(R.id.myMP_4X4);
        mAdsViews.add(mMyAdsView_4X4);
        setupAds(mMyAdsView_4X4, 4, "/users/getownmpweexlist");

        mMyAdsView_4X8 = findViewById(R.id.myMP_4X8);
        mAdsViews.add(mMyAdsView_4X8);
        setupAds(mMyAdsView_4X8, 8, "/users/getownmpweexlist");

        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
        setTitle("水平方向广告位");
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

    public void setupAds(WXAdsView adsView, int limit, String apiUrl) {
        JSONObject params = new JSONObject();
        params.put("mpid", MPWeexSDK.SYSTEM_MPID);
        params.put("page", "/hads.js");

        // 特殊指定的主题，不继承（即子页面不继承此主题，只在当前View生效）
        JSONObject specTheme = new JSONObject();
        // 加载进度前景色
        specTheme.put("loaderTintColor", "#CCCCCC");

        JSONObject pageParams = new JSONObject();
        pageParams.put("apiUrl", apiUrl);
        pageParams.put("limit", limit);
        pageParams.put("tintColor", "#333333");

        adsView.setupAds(params, LinearLayout.HORIZONTAL, specTheme, pageParams);
    }

    @Override
    protected void onStart() {
        super.onStart();
        for (WXAdsView adsView: mAdsViews) {
            adsView.onActivityStart();
        }
    }

    @Override
    protected void onStop() {
        for (WXAdsView adsView: mAdsViews) {
            adsView.onActivityStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        for (WXAdsView adsView: mAdsViews) {
            adsView.onActivityDestroy();
        }
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        for (WXAdsView adsView: mAdsViews) {
            adsView.onActivityPause();
        }
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        for (WXAdsView adsView: mAdsViews) {
            adsView.onActivityResume();
        }
    }
}
