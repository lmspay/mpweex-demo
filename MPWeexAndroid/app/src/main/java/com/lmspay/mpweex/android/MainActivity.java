package com.lmspay.mpweex.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.lmspay.mpweex.MPWeexSDK;
import com.lmspay.mpweex.proxy.WXDialogProxy;
import com.lmspay.mpweex.util.MPWeexUtils;
import com.lmspay.mpweex.util.WXHttpUtils;
import com.taobao.weex.utils.WXLogUtils;

import java.util.Map;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.jumpToMP).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPWeexSDK.getInstance().jumpToMP(
                        MainActivity.this,
                        "MPAAABa7vz87hIvbIAq1lG",
                        "images/AAFLlQAAAWzlpHpnAgI",
                        1);
            }
        });

        findViewById(R.id.openAcc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 实现开户，程序内应登录成功后调用，Playground默认用户调用
                WXHttpUtils.getInstance().openAccount("F100", "13800000000",
                        null, null, null, null,
                        null, null, null, new WXHttpUtils.ResponseCallback() {
                            @Override
                            public void onResponse(final boolean ok, int statusCode, final Object data, Map<String, String> headers) {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(ok && data instanceof JSONObject) {
                                            WXDialogProxy.createToastAndShow(MainActivity.this,
                                                    Toast.LENGTH_SHORT, "center", "open account success.");
                                            try {
                                                JSONObject resObj = (JSONObject) data;
                                                MPWeexSDK.getInstance().setUnionId(MPWeexUtils.getStringOptionOrDef(resObj, "unionid", null));
                                                MPWeexSDK.getInstance().setSalt(MPWeexUtils.getStringOptionOrDef(resObj, "salt", null));
                                                MPWeexSDK.getInstance().setSignKey(MPWeexUtils.getStringOptionOrDef(resObj, "privatekey", null));
                                            } catch (Exception e) {
                                                WXLogUtils.e("openAccount exception: " + e.toString());
                                            }
                                        }else {
                                            WXDialogProxy.createToastAndShow(MainActivity.this,
                                                    Toast.LENGTH_SHORT, "center", "open account failed.");
                                        }
                                    }
                                });

                            }
                        });
            }
        });
    }
}
