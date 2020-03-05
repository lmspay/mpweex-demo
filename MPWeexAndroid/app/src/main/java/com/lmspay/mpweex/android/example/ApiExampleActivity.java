package com.lmspay.mpweexsdk.example;

// Created by saint on 2019-11-13.

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.sdk.android.oss.common.utils.IOUtils;
import com.lmspay.mpweexsdk.Constants;
import com.lmspay.zq.MPWeexSDK;
import com.yuyh.jsonviewer.library.JsonRecyclerView;

import java.util.Map;


public class ApiExampleActivity extends AppCompatActivity implements MPWeexSDK.ResponseCallback {
    private static final int ROUND_TYPE_LEFT_TOP = 0x01;
    private static final int ROUND_TYPE_LEFT_BOTTOM = 0x02;
    private static final int ROUND_TYPE_RIGHT_TOP = 0x04;
    private static final int ROUND_TYPE_RIGHT_BOTTOM = 0x08;

    private JsonRecyclerView mResponseTxt;
    private RecyclerView mModelList;

    private JSONObject mModelObj;
    private JSONObject mApiObj;

    private String mModelName;
    private String mDescName;
    private String mApiName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apiexample);

        mResponseTxt = findViewById(R.id.responseTxt);
        mModelList = findViewById(R.id.modelList);
        mResponseTxt.setTextSize(14.0F);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        layoutManager.setOrientation(OrientationHelper.VERTICAL);
        mModelList.setLayoutManager(layoutManager);

        initModelsAndApis();

        mDescName = getIntent().getStringExtra("desc");
        mApiName = getIntent().getStringExtra("api");
        mModelName = getIntent().getStringExtra("model");

        ApiAdapter apiAdapter = new ApiAdapter(this, mModelObj.getJSONObject(mModelName));
        mModelList.setAdapter(apiAdapter);

        setTitle(mDescName);

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
        }else if(item.getItemId() == R.id.menu_api) {
            // API详情处理
            createDialogAndShow(this, mDescName, genApiDesc(mApiName), "OK");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_apis, menu);
        return true;
    }

    public void onExecClick(View v) {
        switch (mApiName) {
            case "getRecommendList":
                MPWeexSDK.getInstance().getRecommendList(0, 4, this);
                break;
            case "searchMP":
                MPWeexSDK.getInstance().searchMP("程", 0, 4, this);
                break;
            case "onLogin":
                MPWeexSDK.getInstance().onLogin(
                        Constants.DEFAULT_APP_UID,
                        Constants.DEFAULT_APP_PHONE,
                        null, 1, null, null,
                        "小众", null,
                        null, this);
                break;
            case "getRecentMPList":
                // 开户后则可以调用
                MPWeexSDK.getInstance().getRecentMPList(0, 4, this);
                break;
            case "getMyMPList":
                // 开户后则可以调用
                MPWeexSDK.getInstance().getMyMPList(0, 4, this);
                break;
            case "addFootprint1":
                // 开户后则可以调用
                MPWeexSDK.getInstance().addFootprint("MPAAABa7vz87hIvbIAq1lG", 1, this);
                break;
            case "addFootprint2":
                // 开户后则可以调用
                MPWeexSDK.getInstance().addFootprint("MPAAABa7vz87hIvbIAq1lG", 2, this);
                break;
        }
    }

    private void initModelsAndApis() {
        try {
            mModelObj = JSONObject.parseObject(IOUtils.readStreamAsString(
                    getResources().openRawResource(R.raw.models), "UTF-8"));

            mApiObj = JSONObject.parseObject(IOUtils.readStreamAsString(
                    getResources().openRawResource(R.raw.apis), "UTF-8"));
        }catch (Exception ex) {

        }
    }

    @Override
    public void onResponse(boolean ok, int statusCode, final Object data, Map<String, String> headers) {
        if(data != null){
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mResponseTxt.bindJson(data.toString());
                }
            });
        }
    }

    private static class ApiAdapter extends RecyclerView.Adapter<ApiViewHolder> {
        private Context mContext;
        private JSONObject mModelObj;
        private LayoutInflater mInflater;
        private JSONObject mPropertiesObj;
        private String[] mPropertiesKeys;

        public ApiAdapter(Context context, JSONObject modelObj) {
            super();
            mContext = context;
            mModelObj = modelObj;
            mPropertiesObj = modelObj.getJSONObject("properties");
            mPropertiesKeys = mPropertiesObj.keySet().toArray(new String[0]);
            mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public ApiViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = mInflater.inflate(R.layout.layout_api_item, viewGroup, false);
            ApiViewHolder holder= new ApiViewHolder(view);
            return holder;
        }

        @Override
        public void onBindViewHolder(@NonNull ApiViewHolder apiViewHolder, int i) {
            String key = mPropertiesKeys[i];
            apiViewHolder.setData(key, mPropertiesObj.getJSONObject(key));
        }

       @Override
        public int getItemCount() {
            return mPropertiesKeys.length;
        }
    }

    private static class ApiViewHolder extends RecyclerView.ViewHolder {
        TextView mFiledTxt;
        TextView mTypeTxt;
        TextView mDescTxt;

        ApiViewHolder(@NonNull View itemView) {
            super(itemView);
            mFiledTxt = itemView.findViewById(R.id.fieldTxt);
            mTypeTxt = itemView.findViewById(R.id.typeTxt);
            mDescTxt = itemView.findViewById(R.id.descTxt);
        }

        void setData(String key, JSONObject obj) {
            mFiledTxt.setText(key);
            String type = obj.getString("type");
            if("integer".equals(type)) {
                String format = obj.getString("format");
                if("int32".equals(format)) {
                    mTypeTxt.setText("int");
                }else {
                    mTypeTxt.setText("long");
                }
            }else {
                mTypeTxt.setText(type);
            }
            mDescTxt.setText(obj.getString("description"));
        }
    }

    private Spanned genApiDesc(String which) {
        JSONArray params = mApiObj.getJSONArray(which);

        StringBuilder sb = new StringBuilder();
        sb.append("<span style=\"color:#a50000;\">");

        // 参数
        for (Object param: params) {
            JSONObject paramObj = (JSONObject) param;
            sb.append("@param&nbsp;");
            sb.append(paramObj.getString("name"));
            sb.append("&nbsp;");
            sb.append(paramObj.getString("desc"));
            sb.append("<br />");
        }

        sb.append("@param callback 回调<br /><br />");
        sb.append("</span>");
        sb.append("MPWeexSDK.getInstance()<br />");

        sb.append("&nbsp;&nbsp;&nbsp;&nbsp;.<span style=\"color:#0000ff\">");
        sb.append(which.replaceAll("\\d", ""));
        sb.append("</span>(");

        // 参数
        for (Object param: params) {
            JSONObject paramObj = (JSONObject) param;
            sb.append("<br />&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
            sb.append("<span style=\"color:#00ff00\">");
            sb.append(paramObj.getString("type"));
            sb.append("</span>&nbsp;");
            sb.append(paramObj.getString("name"));
            sb.append(",");
        }

        sb.append("<br /> &nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;");
        sb.append("<span style=\"color:#00ff00\">ResponseCallback</span> callback);");

        return Html.fromHtml(sb.toString());
    }

    private Dialog createDialogAndShow(
            Context context, String title, Spanned message,
            String ok) {
        final Dialog dialog = new Dialog(context, com.lmspay.zq.R.style.mpweexDialogTheme);

        View view = LayoutInflater.from(context).inflate(
                com.lmspay.zq.R.layout.mpweex_dialog, null);
        dialog.setContentView(view);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            view.setBackground(createBgDrawable(context, 0x0F, false));
        }else {
            view.setBackgroundDrawable(createBgDrawable(context, 0x0F, false));
        }

        TextView titleTV = view.findViewById(com.lmspay.zq.R.id.mpweexDialogTitle);
        TextView messageTV = view.findViewById(com.lmspay.zq.R.id.mpweexDialogMessage);

        Button cancelBtn = view.findViewById(com.lmspay.zq.R.id.mpweexDialogCancelBtn);
        Button okBtn = view.findViewById(com.lmspay.zq.R.id.mpweexDialogOKBtn);

        final EditText editText = view.findViewById(com.lmspay.zq.R.id.mpweexDialogEdit);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    dialog.dismiss();
                }
                return false;
            }
        });

        if(!TextUtils.isEmpty(title)) {
            titleTV.setText(title);
            titleTV.setVisibility(View.VISIBLE);
        }else {
            titleTV.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(message)) {
            messageTV.setGravity(Gravity.LEFT);
            messageTV.setText(message);
            messageTV.setVisibility(View.VISIBLE);
        }else {
            messageTV.setVisibility(View.GONE);
        }

        if(!TextUtils.isEmpty(ok)) {
            okBtn.setText(ok);
            okBtn.setVisibility(View.VISIBLE);
            okBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });
        }else {
            okBtn.setVisibility(View.GONE);
        }


        cancelBtn.setVisibility(View.GONE);

        // SET BackGround
        if(okBtn.getVisibility() == View.VISIBLE) {
            if(cancelBtn.getVisibility() == View.VISIBLE) {
                // 两个按钮都显示
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    okBtn.setBackground(createBgDrawable(context, ROUND_TYPE_RIGHT_BOTTOM, true));
                    cancelBtn.setBackground(createBgDrawable(context, ROUND_TYPE_LEFT_BOTTOM, true));
                }else {
                    okBtn.setBackgroundDrawable(createBgDrawable(context, ROUND_TYPE_RIGHT_BOTTOM, true));
                    cancelBtn.setBackgroundDrawable(createBgDrawable(context, ROUND_TYPE_LEFT_BOTTOM, true));
                }

            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    okBtn.setBackground(createBgDrawable(context,
                            ROUND_TYPE_LEFT_BOTTOM|ROUND_TYPE_RIGHT_BOTTOM, true));
                }else {
                    okBtn.setBackgroundDrawable(createBgDrawable(context,
                            ROUND_TYPE_LEFT_BOTTOM|ROUND_TYPE_RIGHT_BOTTOM, true));
                }
            }
        }else {
            if(cancelBtn.getVisibility() == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    cancelBtn.setBackground(createBgDrawable(context,
                            ROUND_TYPE_LEFT_BOTTOM|ROUND_TYPE_RIGHT_BOTTOM, true));
                }else {
                    cancelBtn.setBackgroundDrawable(createBgDrawable(context,
                            ROUND_TYPE_LEFT_BOTTOM|ROUND_TYPE_RIGHT_BOTTOM, true));
                }
            }
        }

        editText.setVisibility(View.GONE);

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {
                dialog.dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();

        Window window = dialog.getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        params.width = (int)(dm.widthPixels * 0.85F);
        window.setAttributes(params);

        return dialog;
    }

    private GradientDrawable createBgDrawable(Context context, int roundType, int color) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);

        float radius = context.getResources().getDimension(com.lmspay.zq.R.dimen.mpweex_dialog_radius);
        float [] radii = new float[] {0,0,0,0,0,0,0,0};

        if((roundType & ROUND_TYPE_LEFT_TOP) > 0) {
            radii[0] = radii[1] = radius;
        }

        if((roundType & ROUND_TYPE_RIGHT_TOP) > 0) {
            radii[2] = radii[3] = radius;
        }

        if((roundType & ROUND_TYPE_LEFT_BOTTOM) > 0) {
            radii[6] = radii[7] = radius;
        }

        if((roundType & ROUND_TYPE_RIGHT_BOTTOM) > 0) {
            radii[4] = radii[5] = radius;
        }

        drawable.setCornerRadii(radii);
        return drawable;
    }

    private Drawable createBgDrawable(Context context, int roundType, boolean stateColor) {
        if(stateColor) {
            StateListDrawable stateListDrawable = new StateListDrawable();
            stateListDrawable.addState(new int[]{android.R.attr.state_pressed, android.R.attr.state_enabled},
                    createBgDrawable(context, roundType,
                            context.getResources().getColor(
                                    com.lmspay.zq.R.color.mpweexDialogPressedBgColor)));
            stateListDrawable.addState(new int[]{android.R.attr.state_enabled},
                    createBgDrawable(context, roundType,
                            context.getResources().getColor(
                                    com.lmspay.zq.R.color.mpweexDialogBgColor)));
            return stateListDrawable;
        }else {
            return createBgDrawable(context, roundType,
                    context.getResources().getColor(
                            com.lmspay.zq.R.color.mpweexDialogBgColor));
        }
    }
}
