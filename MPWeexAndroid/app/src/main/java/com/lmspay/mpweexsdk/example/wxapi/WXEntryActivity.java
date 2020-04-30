package com.lmspay.mpweexsdk.example.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * @author yqr
 * @date 20-4-7
 * description: 微信分享回调Activity
 */
public class WXEntryActivity extends AppCompatActivity {
    private static final String TAG = "WXEntryActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ignore callback.
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.e(TAG, "onNewIntent");
        super.onNewIntent(intent);
        setIntent(intent);
    }
}
