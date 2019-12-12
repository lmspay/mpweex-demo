package com.lmspay.mpweex.android.mpweex;

// Created by saint on 2019-10-18.

import android.app.Activity;

import com.lmspay.mpweex.adapter.IWXUserInfoAdapter;
import com.lmspay.mpweex.model.WXUserInfoModel;

public class UserInfoAdpater implements IWXUserInfoAdapter {

    @Override
    public WXUserInfoModel getUserInfo(Activity activity) {
        WXUserInfoModel userInfoModel = new WXUserInfoModel();
        userInfoModel.setGender(0);
        userInfoModel.setNickname("小众");
        userInfoModel.setAvatar("http://39.100.48.104:18080/avatar.png");
        return userInfoModel;
    }

    @Override
    public boolean ensureLogged(Activity activity) {
        // 用户已登录则返回true

        // 如果未登录，则跳转至登录界面，返回false
//        if(BuildConfig.DEBUG) {
//            // 测试跳转
//            Intent intent = new Intent(activity, LoginActivity.class);
//            activity.startActivity(intent);
//            return false;
//        }

        return true;
    }
}
