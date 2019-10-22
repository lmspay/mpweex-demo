package com.lmspay.mpweex.android;

// Created by saint on 2019-10-18.

import com.lmspay.mpweex.adapter.IWXUserInfoAdapter;
import com.lmspay.mpweex.model.WXUserInfoModel;

public class UserInfoAdpater implements IWXUserInfoAdapter {
    @Override
    public WXUserInfoModel getUserInfo() {
        WXUserInfoModel userInfoModel = new WXUserInfoModel();
        userInfoModel.setGender(0);
        userInfoModel.setNickname("小众");
        userInfoModel.setAvatar("http://39.100.48.104:18080/avatar.png");
        return userInfoModel;
    }
}
