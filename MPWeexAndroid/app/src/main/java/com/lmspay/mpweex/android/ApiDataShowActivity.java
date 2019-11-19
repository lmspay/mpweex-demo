package com.lmspay.mpweex.android;

// Created by saint on 2019-11-19.

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.lmspay.mpweex.MPWeexSDK;
import com.taobao.weex.WXSDKManager;
import com.taobao.weex.adapter.IWXImgLoaderAdapter;
import com.taobao.weex.adapter.URIAdapter;
import com.taobao.weex.common.WXImageStrategy;
import com.taobao.weex.dom.WXImageQuality;

import java.util.Map;

public class ApiDataShowActivity extends AppCompatActivity implements MPWeexSDK.ResponseCallback {
    private static final int DEFAULT_LIMIT = 99;

    private JSONArray mJsonArray;
    private MPAdapter mAdapter;

    // 如果需要加载第二页的数据，则第二页的offset=原offset+limit
    private int mOffset = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_api_datashow);

        RecyclerView mMpListView = findViewById(R.id.mpList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mMpListView.setLayoutManager(layoutManager);

        mJsonArray = new JSONArray();
        mAdapter = new MPAdapter(this, mJsonArray);
        mMpListView.setAdapter(mAdapter);

        // 加载推荐数据
        MPWeexSDK.getInstance().getRecommendList(mOffset, DEFAULT_LIMIT, this);

        setTitle("接口数据展示");
        ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            // 返回键的处理
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(boolean ok, int statusCode, final Object data, Map<String, String> headers) {
        if(ok && data instanceof JSONArray) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mJsonArray.addAll((JSONArray) data);
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private static class MPAdapter extends RecyclerView.Adapter<MPViewHolder> {
        private LayoutInflater mInflater;
        private JSONArray mJsonData;
        private Context mContext;

        MPAdapter(Context context, JSONArray jsonArray) {
            super();
            mInflater = LayoutInflater.from(context);
            mJsonData = jsonArray;
            mContext = context;
        }

        @NonNull
        @Override
        public MPViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.layout_mpitem, viewGroup, false);
            return new MPViewHolder(mContext, view);
        }

        @Override
        public void onBindViewHolder(@NonNull MPViewHolder mpViewHolder, int i) {
            if(mJsonData != null && i < mJsonData.size()) {
                JSONObject object = mJsonData.getJSONObject(i);
                mpViewHolder.bindData(object);
            }
        }

        @Override
        public int getItemCount() {
            return mJsonData != null ? mJsonData.size() : 0;
        }
    }

    private static class MPViewHolder extends RecyclerView.ViewHolder {
        AppCompatTextView mNameTxt;
        AppCompatTextView mDescTxt;
        AppCompatImageView mLogoImg;
        Context mContext;
        JSONObject mObj;

        MPViewHolder(Context context, @NonNull View itemView) {
            super(itemView);

            mContext = context;
            mNameTxt = itemView.findViewById(R.id.name);
            mDescTxt = itemView.findViewById(R.id.desc);
            mLogoImg = itemView.findViewById(R.id.logo);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MPWeexSDK.getInstance().jumpToMP(
                            (Activity) mContext,
                            mObj.getString("mpid"),
                            mObj.getString("logo"),
                            mObj.getIntValue("systemtype"),
                            mObj.getIntValue("canoffline"));
                }
            });
        }

        void bindData(JSONObject object) {
            mObj = object;

            mNameTxt.setText(object.getString("mininame"));
            mDescTxt.setText(object.getString("mpdesc"));

            // 先处理OSS图片
            URIAdapter uriAdapter = WXSDKManager.getInstance().getURIAdapter();
            Uri uri = uriAdapter.rewrite(null, null, URIAdapter.IMAGE,
                    Uri.parse("mposs://" + object.getString("logo")));

            // 再用图片加载器加载
            IWXImgLoaderAdapter imgLoaderAdapter = WXSDKManager.getInstance().getIWXImgLoaderAdapter();
            if(imgLoaderAdapter != null) {
                imgLoaderAdapter.setImage(mContext, uri.toString(),
                        mLogoImg, WXImageQuality.AUTO, new WXImageStrategy());
            }
        }
    }
}
