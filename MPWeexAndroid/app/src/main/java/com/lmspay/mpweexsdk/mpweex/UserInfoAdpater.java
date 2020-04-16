package com.lmspay.mpweexsdk.mpweex;

// Created by saint on 2019-10-18.

import android.app.Activity;

import com.lmspay.zq.adapter.IWXUserInfoAdapter;
import com.lmspay.zq.model.WXUserInfoModel;


public class UserInfoAdpater implements IWXUserInfoAdapter {
    @Override
    public WXUserInfoModel getUserInfo(Activity activity) {
        // deprecated, ignore it.
        return null;
    }

    /**
     * 用于判断APP用户是否已登录，请将方法内逻辑替换为APP判断用户是否已登录的逻辑。
     * @param activity 上下文
     * @return true if logged.
     */
    @Override
    public boolean ensureLogged(Activity activity) {
        // 用户已登录则返回true

        // 如果未登录，则跳转至登录界面，返回false
//        if( ! logged ) {
//            // 跳转至登录页或弹toast进行提示
//            Intent intent = new Intent(activity, LoginActivity.class);
//            activity.startActivity(intent);
//            return false;
//        }

        return true;
    }
}
