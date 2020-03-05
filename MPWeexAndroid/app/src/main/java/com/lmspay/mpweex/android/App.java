package com.lmspay.mpweexsdk;

// Created by saint on 2019-09-26.

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;
import android.support.multidex.MultiDexApplication;
import android.support.v7.app.AppCompatDelegate;
import android.webkit.WebView;

import com.lmspay.mpweexsdk.example.BuildConfig;
import com.lmspay.mpweexsdk.mpweex.AmapGeoAdapter;
import com.lmspay.mpweexsdk.mpweex.GlideImageAdapter;
import com.lmspay.mpweexsdk.mpweex.UserInfoAdpater;
import com.lmspay.zq.MPWeexSDK;

import org.apache.weex.InitConfig;

import java.util.List;

public class App extends MultiDexApplication {
    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        String curProcessName = getProcessName(this, android.os.Process.myPid());
        if(curProcessName != null && curProcessName.equalsIgnoreCase(this.getPackageName())) {
            MPWeexSDK.getInstance().setBasePath(Constants.MPHOST);
            MPWeexSDK.getInstance().setAppId(Constants.APPID);

            // 初始化mpweex
            InitConfig initConfig = new InitConfig.Builder()
                    .setImgAdapter(new GlideImageAdapter())
                    .setGeoAdapter(new AmapGeoAdapter())
                    .setUserInfoAdapter(new UserInfoAdpater())
                    .build();
            MPWeexSDK.getInstance().initialize(this, initConfig, BuildConfig.DEBUG);
        } else {
            //初始化其它进程的资源
        }
    }

    public static String getProcessName(Context cxt, int pid) {
        ActivityManager am = (ActivityManager) cxt.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
        if (runningApps == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo procInfo : runningApps) {
            if (procInfo.pid == pid) {
                return procInfo.processName;
            }
        }
        return null;
    }
}
