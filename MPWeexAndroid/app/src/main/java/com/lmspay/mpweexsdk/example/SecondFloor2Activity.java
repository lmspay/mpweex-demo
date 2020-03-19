package com.lmspay.mpweexsdk.example;

// Created by saint on 2019-11-23.

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.lmspay.mpweexheader.MPWeexHeader;
import com.lmspay.mpweexsdk.example.fragment.HomeFragment;
import com.lmspay.mpweexsdk.example.fragment.MineFragment;
import com.lmspay.mpweexsdk.example.fragment.QrcodeFragment;
import com.lmspay.zq.MPWeexSDK;
import com.lmspay.zq.util.StatusBarCompat;


public class SecondFloor2Activity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, MPWeexHeader.MPWeexHeaderListener {
    private static final String TAB_CURRENT = "currentTab";

    View tabBarRoot;
    RadioGroup mainRadioGroup;
    RadioButton homeTabBtn;
    RadioButton qrcodeTabBtn;
    RadioButton mineTabBtn;
    ImageView qrCodeImageView;

    FragmentManager mFragmentManager;
    private int currentTab = 0;

    HomeFragment homeFragment;
    QrcodeFragment qrcodeFragment;
    MineFragment mineFragment;

    FrameLayout contentLayout;
    boolean hideTabBar = true;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_secondfloor2);

        tabBarRoot = findViewById(R.id.tabBarRoot);
        mainRadioGroup = findViewById(R.id.mainRadioGroup);
        homeTabBtn = findViewById(R.id.homeTabBtn);
        qrcodeTabBtn = findViewById(R.id.qrcodeTabButton);
        mineTabBtn = findViewById(R.id.mineTabBtn);
        qrCodeImageView = findViewById(R.id.qrCodeImageView);

        contentLayout = findViewById(R.id.contentFrame);

        mFragmentManager = getSupportFragmentManager();
        mainRadioGroup.setOnCheckedChangeListener(this);

        // 状态栏沉浸式
        StatusBarCompat.translucentStatusBar(this, true);
        hideTabBar = getIntent().getBooleanExtra("hideTabBar", true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (outState != null){
            FragmentTransaction transaction = mFragmentManager.beginTransaction();
            //隐藏碎片 避免重叠
            hideFragment(transaction);
            transaction.commitAllowingStateLoss();

            outState.putInt(TAB_CURRENT, currentTab);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        if (savedInstanceState != null){
            //隐藏碎片 避免重叠
            hideFragment(transaction);
            transaction.commitAllowingStateLoss();

            currentTab = savedInstanceState.getInt(TAB_CURRENT);
        }
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setTabSelection(currentTab);
    }

    public void onMPClicked(View view) {
        // 跳转到小程序页面

        MPWeexSDK.getInstance().jumpToPage(this,
                MPWeexSDK.MPPage.PAGE_INDEX);
    }

    /**
     * 底部菜单栏选中
     */
    private void setTabSelection(int index){
        //清除选中
        clearSelection();
        FragmentTransaction transaction = mFragmentManager.beginTransaction();
        //隐藏碎片 避免重叠
        hideFragment(transaction);

        currentTab = index;
        switch (index){
            //首页
            case 0:
                homeTabBtn.setChecked(true);
                //判断碎片是否为空 以免重复建立 影响性能
                if (homeFragment == null){
                    homeFragment = new HomeFragment();
                    if(hideTabBar) {
                        homeFragment.setHeaderListener(this);
                    }
                    transaction.add(R.id.contentFrame, homeFragment);
                }else {
                    transaction.show(homeFragment);
                }
                break;
            //乘车码
            case 1:
                qrcodeTabBtn.setChecked(true);
                //判断碎片是否为空 以免重复建立 影响性能
                if (qrcodeFragment == null){
                    qrcodeFragment = new QrcodeFragment();
                    transaction.add(R.id.contentFrame, qrcodeFragment);
                }else {
                    transaction.show(qrcodeFragment);
                }
                break;
            //我的
            case 2:
                mineTabBtn.setChecked(true);
                //判断碎片是否为空 以免重复建立 影响性能
                if (mineFragment == null){
                    mineFragment = new MineFragment();
                    transaction.add(R.id.contentFrame, mineFragment);
                }else {
                    transaction.show(mineFragment);
                }
            default:
        }
        transaction.commitAllowingStateLoss();
    }

    /**
     * 清除选中
     */
    private void clearSelection(){
        homeTabBtn.setChecked(false);
        qrcodeTabBtn.setChecked(false);
        mineTabBtn.setChecked(false);
    }

    private void hideFragment(FragmentTransaction transaction){
        if (homeFragment != null){
            transaction.hide(homeFragment);
        }
        if (qrcodeFragment != null){
            transaction.hide(qrcodeFragment);
        }
        if (mineFragment != null){
            transaction.hide(mineFragment);
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.homeTabBtn:
                setTabSelection(0);
                break;
            case R.id.qrcodeTabButton:
                setTabSelection(1);
                break;
            case R.id.mineTabBtn:
                setTabSelection(2);
                break;
            default:
        }
    }

    public void onQrcodeClicked(View v) {
        setTabSelection(1);
    }

    @Override
    public void onDropAnim(View rootView, int dy, float percent) {
        tabBarRoot.setTranslationY(dy);
        qrCodeImageView.setTranslationY(dy);
    }

    @Override
    public void onFinishAnim(boolean secondFloorOpened) {

    }
}
