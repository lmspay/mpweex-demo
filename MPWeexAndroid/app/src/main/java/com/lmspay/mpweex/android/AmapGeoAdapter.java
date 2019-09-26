package com.lmspay.mpweex.android;

import android.content.Context;
import android.location.Location;
import android.os.Build;

import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.DPoint;
import com.lmspay.mpweex.adapter.IWXGeoAdapter;

/**
 * Created by saint on 2019/1/21.
 */

public class AmapGeoAdapter implements IWXGeoAdapter, AMapLocationListener {
    private final static double a = 6378245.0; // 长半轴
    private final static double pi = 3.14159265358979324; // π
    private final static double ee = 0.00669342162296594323; // e²

    private AMapLocationClient mLocationClient;
    private AMapLocationClientOption mLocationOption;
    private WXGeoListener mListener;
    private boolean mOnece;

    @Override
    public void getLocation(final Context context, JSONObject options, final boolean onece,
                            final WXGeoListener listener) {
        if(mLocationClient == null) {
            mLocationClient = new AMapLocationClient(context);
            mLocationClient.setLocationListener(this);//设置定位监听
        }
        if(mLocationOption == null) {
            //设置定位参数
            mLocationOption = new AMapLocationClientOption();
            mLocationOption.setHttpTimeOut(10000);// 可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
            mLocationOption.setInterval(1000);// 可选，设置定位间隔。默认为2秒
            mLocationOption.setNeedAddress(false);// 可选，设置是否返回逆地理地址信息。默认是ture
            //设置是否允许模拟位置,默认为false，不允许模拟位置
            mLocationOption.setMockEnable(false);
        }

        String model = "highAccuracy";
        try {
            if(options != null) {
                model = options.containsKey("model") ? (String) options.get("model") : model;
            }
        } catch (Exception e) {
        }

        if(model.equals("highAccuracy")) {
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        }
        if(options != null && options.containsKey("gpsFirst")) {
            // 可选，设置是否gps优先，只在高精度模式下有效。默认关闭
            mLocationOption.setGpsFirst(options.getBooleanValue("gpsFirst"));
        }
        mLocationOption.setOnceLocation(onece);// 可选，设置是否单次定位。默认是false

        mListener = listener;
        mOnece = onece;
        mLocationClient.startLocation();
    }

    @Override
    public void stop(Context context) {
        if(mLocationClient != null) {
            mListener = null;
            mLocationClient.stopLocation();
        }
    }

    @Override
    public void destroy(Context context) {
        if(mLocationClient != null) {
            mLocationClient.onDestroy();
            mLocationClient = null;
        }
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (mListener != null && aMapLocation != null) {
            if(aMapLocation.getErrorCode() == 0) {
                DPoint p = toGPSPoint(aMapLocation.getLatitude(), aMapLocation.getLongitude());

                Location loc = new Location(aMapLocation.getProvider());
                loc.setAccuracy(aMapLocation.getAccuracy());
                loc.setBearing(aMapLocation.getBearing());
                loc.setSpeed(aMapLocation.getSpeed());
                loc.setLongitude(p.getLongitude());
                loc.setLatitude(p.getLatitude());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    loc.setBearingAccuracyDegrees(aMapLocation.getBearingAccuracyDegrees());
                }

                mListener.onLocationChanged(loc);
            }else {
                mListener.onError(aMapLocation.getErrorCode(), aMapLocation.getErrorInfo());
            }
        }
        if(mOnece) {
            stop(null);
        }
    }

    // GCJ-02 to WGS-84
    public static DPoint toGPSPoint(double latitude, double longitude) {
        DPoint dev = calDev(latitude, longitude);
        double retLat = latitude - dev.getLatitude();
        double retLon = longitude - dev.getLongitude();
        for (int i = 0; i < 1; i++) {
            dev = calDev(retLat, retLon);
            retLat = latitude - dev.getLatitude();
            retLon = longitude - dev.getLongitude();
        }
        return new DPoint(retLat, retLon);
    }

    // 计算偏差
    private static DPoint calDev(double wgLat, double wgLon) {
        if (isOutOfChina(wgLat, wgLon)) {
            return new DPoint(0, 0);
        }
        double dLat = calLat(wgLon - 105.0, wgLat - 35.0);
        double dLon = calLon(wgLon - 105.0, wgLat - 35.0);
        double radLat = wgLat / 180.0 * pi;
        double magic = Math.sin(radLat);
        magic = 1 - ee * magic * magic;
        double sqrtMagic = Math.sqrt(magic);
        dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
        dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
        return new DPoint(dLat, dLon);
    }

    // 判断坐标是否在国外
    private static boolean isOutOfChina(double lat, double lon) {
        if (lon < 72.004 || lon > 137.8347)
            return true;
        if (lat < 0.8293 || lat > 55.8271)
            return true;
        return false;
    }

    // 计算纬度
    private static double calLat(double x, double y) {
        double resultLat = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y
                + 0.2 * Math.sqrt(Math.abs(x));
        resultLat += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        resultLat += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
        resultLat += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
        return resultLat;
    }

    // 计算经度
    private static double calLon(double x, double y) {
        double resultLon = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1
                * Math.sqrt(Math.abs(x));
        resultLon += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
        resultLon += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
        resultLon += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0
                * pi)) * 2.0 / 3.0;
        return resultLon;
    }
}
