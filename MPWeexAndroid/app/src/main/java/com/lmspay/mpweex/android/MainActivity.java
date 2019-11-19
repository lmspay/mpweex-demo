package com.lmspay.mpweex.android;

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

import com.alibaba.fastjson.JSONObject;
import com.lmspay.mpweex.MPWeexSDK;
import com.lmspay.mpweex.ui.WXHostActivity;
import com.taobao.weex.utils.WXLogUtils;

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

        mExamplesList.add(new String[]{"group", "API"});
        mExamplesList.add(new String[]{"API", "搜索小程序(游客)", "searchMP", "SearchMpweexModel"});
        mExamplesList.add(new String[]{"API", "获取推荐的小程序(游客)", "getRecommendList", "RecommendModel"});
        mExamplesList.add(new String[]{"API", "开户(游客，幂等)", "openAccount", "OpenAccountModel"});

        mExamplesList.add(new String[]{"API", "获取我的小程序", "getMyMPList", "OwnmpweexModel"});
        mExamplesList.add(new String[]{"API", "获取最近使用的小程序", "getRecentMPList", "AccesslogModel"});
        mExamplesList.add(new String[]{"API", "添加到我的小程序", "addFootprint1", "CommonModel"});
        mExamplesList.add(new String[]{"API", "从我的小程序中移除", "addFootprint2", "CommonModel"});

        mExamplesList.add(new String[]{"group", "Pages"});
        mExamplesList.add(new String[]{"Pages", "最近使用的小程序", "/pages/setting/mpList.js", "/users/getaccesslog"});
        mExamplesList.add(new String[]{"Pages", "我的小程序", "/pages/setting/mpList.js", "/users/getownmpweexlist"});
        mExamplesList.add(new String[]{"Pages", "推荐的小程序", "/pages/setting/mpList.js", "/mpweex/getrecommendlist"});
        mExamplesList.add(new String[]{"Pages", "搜索小程序", "/pages/search.js", ""});

        mExamplesList.add(new String[]{"group", "Widget"});
        mExamplesList.add(new String[]{"Widget", "水平方向广告位", "hads"});

        mExamplesList.add(new String[]{"group", "其它"});
        mExamplesList.add(new String[]{"Others", "跳转至小程序", "jumpToMP"});
        mExamplesList.add(new String[]{"Others", "接口数据展示", "apiDataShow"});

        GroupListAdapter adapter = new GroupListAdapter(this, mExamplesList);
        mListView.setAdapter(adapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] itemArr = mExamplesList.get(position);
                if("API".equals(itemArr[0])) {
                    Intent intent = new Intent(MainActivity.this, ApiExampleActivity.class);
                    intent.putExtra("desc", itemArr[1]);
                    intent.putExtra("api", itemArr[2]);
                    intent.putExtra("model", itemArr[3]);
                    startActivity(intent);
                }else if("Pages".equals(itemArr[0])) {
                    Intent intent = new Intent(MainActivity.this, WXHostActivity.class);
                    // 设置为系统小程序
                    JSONObject resObj = new JSONObject();
                    // 小程序ID
                    resObj.put("mpid", MPWeexSDK.SYSTEM_MPID);
                    // 小程序LOGO
                    resObj.put("logo", "images/AAFKqgAAAW3yx8QhAQI");
                    // 小程序类别为宿主
                    resObj.put("systemtype", 0);

                    // 指定标题
                    resObj.put("title", itemArr[1]);
                    // 指定页面
                    resObj.put("page", itemArr[2]);
                    // 加载小程序信息，确保系统小程序正常下载
                    intent.putExtra("loadMpInfo", true);
                    intent.putExtra("params", resObj);

                    // 指定页面参数
                    JSONObject pageParams = new JSONObject();
                    // 指定API
                    pageParams.put("apiUrl", itemArr[3]);
                    intent.putExtra("pageParams", pageParams.toJSONString());

                    startActivity(intent);
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
                                "images/AAFLlQAAAWzlpHpnAgI",
                                1, 1);
                    }else if("apiDataShow".equals(itemArr[2])) {
                        Intent intent = new Intent(MainActivity.this, ApiDataShowActivity.class);
                        startActivity(intent);
                    }
                }
            }
        });

        // 实现开户，程序内应登录成功后调用，Playground默认用户调用
        MPWeexSDK.getInstance().openAccount(Constants.DEFAULT_APP_UID, Constants.DEFAULT_APP_PHONE,
                null, null, null, null,
                null, null, null, new MPWeexSDK.ResponseCallback() {
                    @Override
                    public void onResponse(final boolean ok, int statusCode, final Object data, Map<String, String> headers) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(ok && data instanceof JSONObject) {
                                    try {
                                        JSONObject resObj = (JSONObject) data;
                                        MPWeexSDK.getInstance().setUnionId(resObj.getString("unionid"));
                                        MPWeexSDK.getInstance().setSalt(resObj.getString("salt"));
                                        MPWeexSDK.getInstance().setSignKey(resObj.getString("privatekey"));
                                    } catch (Exception e) {
                                        WXLogUtils.e("openAccount exception: " + e.toString());
                                    }
                                }
                            }
                        });

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
