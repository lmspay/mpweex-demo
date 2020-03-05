package com.lmspay.mpweexsdk;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatTextView;

import com.lmspay.mpweexsdk.example.MainActivity;
import com.lmspay.mpweexsdk.example.R;

import java.util.Calendar;

public class SplashActivity extends Activity {
    private static final int MSG_SUGGEST_LAUNCH = 0x01;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_SUGGEST_LAUNCH:
                    suggestLaunch();
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AppCompatTextView cpyTxt = findViewById(R.id.app_cpy);
        cpyTxt.setText(getString(R.string.app_cpy,
                String.valueOf(Calendar.getInstance().get(Calendar.YEAR))));

        mHandler.sendEmptyMessageDelayed(MSG_SUGGEST_LAUNCH, 2000);
    }

    private void suggestLaunch() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
