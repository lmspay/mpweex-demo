package com.lmspay.mpweexsdk.example.fragment;

// Created by saint on 2020-03-18.

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lmspay.zq.proxy.WXDialogProxy;
import com.lmspay.zq.util.StatusBarCompat;

public class MineFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        TextView textView = new TextView(getContext());
        textView.setText("Mine");
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);

        linearLayout.addView(textView, ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        // Fragment父布局FrameLayout为match_parent，所以需要设置padding，以防底部内容被tabBar遮挡
        // 如果父布局不为match_parent，则不好实现tabBar跟随隐藏动画
        linearLayout.setPadding(0, 0, 0, WXDialogProxy.dp2px(getContext(), 50.5F));

        return linearLayout;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 状态栏使用深色前景色
        StatusBarCompat.setStatusTextColor(true, getActivity());
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if(!hidden) {
            // 状态栏使用深色前景色
            StatusBarCompat.setStatusTextColor(true, getActivity());
        }
    }
}
