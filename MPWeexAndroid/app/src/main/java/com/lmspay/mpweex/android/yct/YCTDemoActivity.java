package com.lmspay.mpweex.android.yct;

// Created by saint on 2019-11-23.

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONObject;
import com.lmspay.mpweex.MPWeexSDK;
import com.lmspay.mpweex.android.R;
import com.lmspay.mpweex.ui.WXHostActivity;
import com.lmspay.mpweex.util.StatusBarCompat;
import com.lmspay.mpweexheader.MPWeexHeader;
import com.lmspay.springview.widget.SpringView;
import com.taobao.weex.dom.CSSShorthand;
import com.taobao.weex.ui.view.border.BorderDrawable;

public class YCTDemoActivity extends AppCompatActivity {
    // 二楼标题栏背景色
    private static final int SECONDFLOOR_NAV_BG_COLOR = 0xFF7488C3;
    // 二楼标题栏前景色
    private static final int SECONDFLOOR_NAV_TINT_COLOR = 0xFF98A5CC;

    private SpringView mSpringView;
    private MPWeexHeader mMPWeexHeader;
    private View mStatusBarView;

    private boolean mSecondFloorOpened = false;

    private BorderDrawable mToolbarBg;
    private int mNavbarTintColor;
    private int mNavbarBgColor;

    private Toolbar mToolbar;
    private AppCompatTextView mToolbarTitleView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yctdemo);

        mToolbar = findViewById(R.id.mpweexToolbar);
        setSupportActionBar(mToolbar);
        setupToolbar();

        mNavbarBgColor = 0xFF3F51B5;
        mNavbarTintColor = Color.WHITE;
        mToolbarBg = new BorderDrawable();

        mToolbarBg.setColor(mNavbarBgColor);
        mToolbar.setBackground(mToolbarBg);

        mSpringView = findViewById(R.id.mpweexRootView);
        View toolbarRoot = findViewById(R.id.mpweexToolbarRoot);

        // 状态栏高度
        mStatusBarView = findViewById(R.id.mpweexStatusBar);
        final int statusH = StatusBarCompat.getStatusBarHeight(this);
        StatusBarCompat.setViewHeightEqStatusbar(this, mStatusBarView);

        // 状态栏沉浸式
        StatusBarCompat.translucentStatusBar(this, true);

        MPWeexHeader mpWeexHeader = new MPWeexHeader(this, toolbarRoot);
        mpWeexHeader.setIndexPage("/index_wx.js");
        mpWeexHeader.setListener(new MPWeexHeader.MPWeexHeaderListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDropAnim(View rootView, int dy, float percent) {
                float statusPercent = Math.min(1.0F, 1.0F - (dy * 1.0F / statusH));
                if(statusPercent <= 0) {
                    statusPercent = 0.0F;
                }
                // 设置statusBar占位View的透明度
                mStatusBarView.setAlpha(statusPercent);
                if(statusPercent > 0.2F) {
                    mToolbarBg.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_LEFT, 0);
                    mToolbarBg.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_RIGHT, 0);
                }

                // 设置标题栏渐变颜色
                mToolbarBg.setColor(evaluate(percent, mNavbarBgColor, SECONDFLOOR_NAV_BG_COLOR));

                // 设置标题栏的字体渐变颜色

                int tintColor = evaluate(percent, mNavbarTintColor, SECONDFLOOR_NAV_TINT_COLOR);
                ColorStateList colorStateList = getTintColor(tintColor);
                mToolbarTitleView.setTextColor(colorStateList);
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinishAnim(boolean secondFloorOpened) {
                mSecondFloorOpened = secondFloorOpened;
                if(secondFloorOpened) { // 如果是二楼
                    mToolbarBg.setColor(SECONDFLOOR_NAV_BG_COLOR);
                    float radius = getResources().getDimension(com.taobao.weex.R.dimen.mpweex_space12);
                    mToolbarBg.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_LEFT, radius);
                    mToolbarBg.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_RIGHT, radius);

                    mToolbarTitleView.setTextColor(SECONDFLOOR_NAV_TINT_COLOR);
                }else { // 如果不是二楼
                    mStatusBarView.setAlpha(1.0F);
                    mToolbarBg.setColor(mNavbarBgColor);
                    mToolbarBg.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_LEFT, 0);
                    mToolbarBg.setBorderRadius(CSSShorthand.CORNER.BORDER_TOP_RIGHT, 0);

                    mToolbarTitleView.setTextColor(Color.WHITE);
                }
            }
        });

        // 点击标题栏，弹回二楼
        findViewById(R.id.mpweexToolbar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mSecondFloorOpened) {
                    mSpringView.onFinishFreshAndLoad();
                }
            }
        });

        mSpringView.setHeader(mpWeexHeader);
        mSpringView.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {

            }

            @Override
            public void onLoadmore() {

            }
        });

        mMPWeexHeader = mpWeexHeader;
        mMPWeexHeader.onActivityCreate();
        mMPWeexHeader.setBackgroundColor(0xFF252239);
    }

    public void onMPClicked(View view) {
        // 跳转到小程序页面


        Intent intent = new Intent(YCTDemoActivity.this, WXHostActivity.class);
        // 设置为系统小程序
        JSONObject resObj = new JSONObject();
        // 小程序ID
        resObj.put("mpid", MPWeexSDK.SYSTEM_MPID);
        // 小程序LOGO
        resObj.put("logo", "images/AAFKqgAAAW3yx8QhAQI");
        // 小程序类别为宿主
        resObj.put("systemtype", 0);

        // 指定页面
        resObj.put("page", "/pages/wxmp.js");
        // 加载小程序信息，确保系统小程序正常下载
        intent.putExtra("loadMpInfo", true);
        intent.putExtra("params", resObj);

        startActivity(intent);
    }

    private void setupToolbar() {
        getSupportActionBar().setHomeButtonEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayUseLogoEnabled(false);
        getSupportActionBar().setDisplayShowCustomEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        // title view
        LinearLayout titleRootView = new LinearLayout(this);
        Toolbar.LayoutParams layoutParams = new Toolbar.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity = Gravity.CENTER;
        titleRootView.setOrientation(LinearLayout.VERTICAL);
        titleRootView.setGravity(Gravity.CENTER);
        titleRootView.setLayoutParams(layoutParams);
        titleRootView.setLayoutTransition(new LayoutTransition());
        titleRootView.setFocusable(false);
        titleRootView.setFocusableInTouchMode(false);
        titleRootView.setClickable(false);

        mToolbarTitleView = new AppCompatTextView(this);
        titleRootView.addView(mToolbarTitleView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mToolbarTitleView.setText(getTitle());
        mToolbarTitleView.setFocusable(false);
        mToolbarTitleView.setFocusableInTouchMode(false);
        mToolbarTitleView.setClickable(false);
        mToolbarTitleView.setTextColor(Color.WHITE);

        mToolbar.addView(titleRootView);
    }

    private ColorStateList getTintColor(int color) {
        int [][] toolbarColorStates = new int[][]{
                {android.R.attr.state_pressed, android.R.attr.state_enabled},
                {android.R.attr.state_enabled},
                {~android.R.attr.state_enabled}
        };
        int pressedNavBarTintColorStr = color & 0x6FFFFFFF;
        return new ColorStateList(toolbarColorStates,
                new int[]{pressedNavBarTintColorStr, color, color});
    }

    /**
     * 计算渐变颜色值 ARGB
     *
     * @param fraction
     *            变化比率 0~1
     * @param startValue
     *            初始色值
     * @param endValue
     *            结束色值
     * @return
     */
    private int evaluate(float fraction, int startValue, int endValue) {
        int startA = (startValue >> 24) & 0xff;
        int startR = (startValue >> 16) & 0xff;
        int startG = (startValue >> 8) & 0xff;
        int startB = startValue & 0xff;
        int endA = (endValue >> 24) & 0xff;
        int endR = (endValue >> 16) & 0xff;
        int endG = (endValue >> 8) & 0xff;
        int endB = endValue & 0xff;
        return Color.argb((startA + (int) (fraction * (endA - startA))),
                (startR + (int) (fraction * (endR - startR))),
                (startG + (int) (fraction * (endG - startG))),
                (startB + (int) (fraction * (endB - startB))));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityResume();
        }
    }

    @Override
    protected void onPause() {
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityPause();
        }
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityStart();
        }
    }

    @Override
    protected void onStop() {
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityStop();
        }
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityDestroy();
        }
        super.onDestroy();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
