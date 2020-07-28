package com.lmspay.mpweexsdk.example;

// Created by saint on 2019-11-13.

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.lmspay.mpweexsdk.Constants;

import com.lmspay.zq.MPWeexSDK;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;
    private List<String[]> mExamplesList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        mListView = findViewById(R.id.list);

        List<Map<String, Object>> mapList = new ArrayList<>();
        mExamplesList.add(new String[]{"group", "Pages"});
        mExamplesList.add(new String[]{"Pages", "小程序主页", "index"});
        mExamplesList.add(new String[]{"Pages", "最近使用", "recent"});
        mExamplesList.add(new String[]{"Pages", "我关注的", "mymp"});
        mExamplesList.add(new String[]{"Pages", "精彩推荐", "recommend"});
        mExamplesList.add(new String[]{"Pages", "搜索小程序", "search"});

        mExamplesList.add(new String[]{"group", "Widget"});
        mExamplesList.add(new String[]{"Widget", "推荐位", "hads"});

        mExamplesList.add(new String[]{"group", "其它"});
        mExamplesList.add(new String[]{"Others", "跳转至小程序", "jumpToMP"});
        mExamplesList.add(new String[]{"Others", "仿微信下拉", "secondFloor"});
        mExamplesList.add(new String[]{"Others", "带TAB下拉", "secondFloor2"});
        mExamplesList.add(new String[]{"Others", "带TAB下拉（隐藏TabBar）", "secondFloor2_1"});

        GroupListAdapter adapter = new GroupListAdapter(this, mExamplesList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] itemArr = mExamplesList.get(position);
                if("Pages".equals(itemArr[0])) {
                    MPWeexSDK.MPPage mpPage = MPWeexSDK.MPPage.valuesOf(itemArr[2]);
                    if(mpPage != null) {
                        MPWeexSDK.getInstance().jumpToPage(MainActivity.this, mpPage);
                    }
                }else if("Widget".equals(itemArr[0])) {
                    if("hads".equals(itemArr[2])) {
                        Intent intent = new Intent(MainActivity.this, HAdsActivity.class);
                        startActivity(intent);
                    }
                }else if("Others".equals(itemArr[0])) {
                    if("jumpToMP".equals(itemArr[2])) {
                        MPWeexSDK.getInstance().jumpToMP(
                                MainActivity.this,
                                "MPAAABa7vz87hIvbIAq1lG",
                                MPWeexSDK.getInstance().getSystemMPIcon(),
                                1, 1, "");
                    }if("secondFloor".equals(itemArr[2])) {
                        Intent intent = new Intent(MainActivity.this, SecondFloorActivity.class);
                        startActivity(intent);
                    }else if("secondFloor2".equals(itemArr[2])) {
                        Intent intent = new Intent(MainActivity.this, SecondFloor2Activity.class);
                        intent.putExtra("hideTabBar", false);
                        startActivity(intent);
                    }else if("secondFloor2_1".equals(itemArr[2])) {
                        Intent intent = new Intent(MainActivity.this, SecondFloor2Activity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        // 实现开户，程序内应登录成功后调用，Playground默认用户调用
        MPWeexSDK.getInstance().syncAccount(Constants.DEFAULT_APP_UID, Constants.DEFAULT_APP_PHONE,
                new MPWeexSDK.ResponseCallback() {
                    @Override
                    public void onResponse(final boolean ok, int statusCode, final Object data, Map<String, String> headers) {
                        // your code if needed.
                    }
                });
    }

    static class GroupListAdapter extends ArrayAdapter<String[]> {
        GroupListAdapter(Context context, List<String[]> objects) {
            super(context, 0, objects);
        }

        @Override
        public boolean isEnabled(int position) {
            String[] item = getItem(position);
            if(item != null && "group".equals(item[0])) {
                return false;
            }
            return super.isEnabled(position);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = convertView;
            String[] item = getItem(position);
            // 未考虑view复用，请勿在商用APP中使用本适配器
            if(item != null && "group".equals(item[0])) {
                view = LayoutInflater.from(getContext()).inflate(R.layout.layout_header_item, null);
            }else{
                view = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_1, null);
            }
            TextView textView = (TextView) view.findViewById(android.R.id.text1);
            if(item != null && textView != null) {
                textView.setText(item[1]);
            }
            return view;
        }
    }
}
