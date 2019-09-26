package com.lmspay.mpweex.android;

// Created by saint on 2019-09-26.

import android.app.ActivityManager;
import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.lmspay.mpweex.MPWeexSDK;
import com.taobao.weex.InitConfig;

import java.util.List;

public class App extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();

        String curProcessName = getProcessName(this, android.os.Process.myPid());
        if(curProcessName != null && curProcessName.equalsIgnoreCase(this.getPackageName())) {
            // 初始化mpweex
            InitConfig initConfig = new InitConfig.Builder()
                    .setImgAdapter(new GlideImageAdapter())
                    .setGeoAdapter(new AmapGeoAdapter())
                    .setWebSocketAdapterFactory(new DefaultWebSocketAdapterFactory())
                    .build();
            MPWeexSDK.getInstance().initialize(this, initConfig, BuildConfig.DEBUG);

            MPWeexSDK.getInstance().setBasePath("mpweex api host");
            MPWeexSDK.getInstance().setAppId("your appid");
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
