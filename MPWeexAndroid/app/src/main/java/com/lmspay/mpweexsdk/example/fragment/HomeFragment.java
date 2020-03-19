package com.lmspay.mpweexsdk.example.fragment;

// Created by saint on 2020-03-18.

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lmspay.mpweexheader.MPWeexHeader;
import com.lmspay.mpweexsdk.example.R;
import com.lmspay.springview.widget.SpringView;
import com.lmspay.zq.proxy.WXDialogProxy;
import com.lmspay.zq.util.StatusBarCompat;

import org.apache.weex.dom.CSSShorthand;
import org.apache.weex.ui.view.border.BorderDrawable;

import java.lang.ref.WeakReference;

public class HomeFragment extends Fragment {
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

    private Context mContext;
    private WeakReference<MPWeexHeader.MPWeexHeaderListener> mHeaderListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_secondfloor, container, false);

        mContext = getContext();

        mToolbar = rootView.findViewById(R.id.mpweexToolbar);
        setupToolbar();

        mNavbarBgColor = 0xFF3F51B5;
        mNavbarTintColor = Color.WHITE;
        mToolbarBg = new BorderDrawable();

        mToolbarBg.setColor(mNavbarBgColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mToolbar.setBackground(mToolbarBg);
        }else {
            mToolbar.setBackgroundDrawable(mToolbarBg);
        }

        mSpringView = (SpringView) rootView;
        View toolbarRoot = rootView.findViewById(R.id.mpweexToolbarRoot);

        // 给内容设置Padding，因为父布局是match_parent，tabbar会遮挡部分内容
        rootView.findViewById(R.id.contentRootView).setPadding(0, 0, 0,
                WXDialogProxy.dp2px(mContext, 50.5F));

        // 状态栏高度
        mStatusBarView = rootView.findViewById(R.id.mpweexStatusBar);
        final int statusH = StatusBarCompat.getStatusBarHeight(mContext);
        StatusBarCompat.setViewHeightEqStatusbar(mContext, mStatusBarView);

        MPWeexHeader mpWeexHeader = new MPWeexHeader(mContext, toolbarRoot);
        mpWeexHeader.setIndexPage("/index_wx.js");

        if(mHeaderListener != null && mHeaderListener.get() != null) {
            // 如果tabBar跟随隐藏，则不需要保留tabBar部分的滚动高度
        }else {
            // tabbar 高度 + 分隔线高度
            mpWeexHeader.setReservedSpringHeight(WXDialogProxy.dp2px(mContext, 50.5F));

            // 如果父布局为match_parent，而不是above tabBar，则需要设置InnerReservedSpringHeight为0
            mpWeexHeader.setInnerReservedSpringHeight(0);
        }

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
                if(mHeaderListener != null) {
                    MPWeexHeader.MPWeexHeaderListener listener = mHeaderListener.get();
                    if(listener != null) {
                        listener.onDropAnim(rootView, dy, percent);
                    }
                }
            }

            @SuppressLint("RestrictedApi")
            @Override
            public void onFinishAnim(boolean secondFloorOpened) {
                mSecondFloorOpened = secondFloorOpened;
                if(secondFloorOpened) { // 如果是二楼
                    mToolbarBg.setColor(SECONDFLOOR_NAV_BG_COLOR);
                    float radius = getResources().getDimension(com.lmspay.zq.R.dimen.mpweex_space12);
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
                if(mHeaderListener != null) {
                    MPWeexHeader.MPWeexHeaderListener listener = mHeaderListener.get();
                    if(listener != null) {
                        listener.onFinishAnim(secondFloorOpened);
                    }
                }
            }
        });

        // 点击标题栏，弹回二楼
        rootView.findViewById(R.id.mpweexToolbar).setOnClickListener(new View.OnClickListener() {
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

        return rootView;
    }

    private void setupToolbar() {
        // title view
        LinearLayout titleRootView = new LinearLayout(mContext);
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

        mToolbarTitleView = new AppCompatTextView(mContext);
        titleRootView.addView(mToolbarTitleView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        mToolbarTitleView.setText("众圈小程序");
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
    public void onResume() {
        super.onResume();
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityResume();
        }
    }

    @Override
    public void onPause() {
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityPause();
        }
        super.onPause();
    }

    @Override
    public void onStart() {
        super.onStart();
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityStart();
        }
    }

    @Override
    public void onStop() {
        if(mMPWeexHeader != null) {
            mMPWeexHeader.onActivityStop();
        }
        super.onStop();
    }

    @Override
    public void onDestroy() {
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

    public void setHeaderListener(MPWeexHeader.MPWeexHeaderListener headerListener) {
        this.mHeaderListener = new WeakReference<>(headerListener);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 状态栏使用白色前景色
        StatusBarCompat.setStatusTextColor(false, getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            // 状态栏使用白色前景色
            StatusBarCompat.setStatusTextColor(false, getActivity());
        }
    }
}
