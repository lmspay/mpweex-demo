package com.lmspay.mpweexsdk.mpweex;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Looper;
import android.support.v7.content.res.AppCompatResources;
import android.text.TextUtils;
import android.util.Base64;
import android.widget.ImageView;

import com.bumptech.glide.DrawableTypeRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.target.Target;

import org.apache.weex.WXSDKManager;
import org.apache.weex.adapter.IWXImgLoaderAdapter;
import org.apache.weex.common.WXImageStrategy;
import org.apache.weex.dom.WXImageQuality;
import org.apache.weex.utils.WXLogUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by saint on 2018/9/10.
 */

public class GlideImageAdapter implements IWXImgLoaderAdapter {
    @Override
    public void setImage(Context context, final String url, final ImageView view, final WXImageQuality quality, final WXImageStrategy strategy) {
        glideImage(context, url, view, quality, strategy, false);
    }

    @Override
    public void downloadImage(Context context, String url, WXImageQuality quality, WXImageStrategy strategy) {
        glideImage(context, url, null, quality, strategy, true);
    }

    private void glideImage(final Context context, final String url, final ImageView view,
                            final WXImageQuality quality, final WXImageStrategy strategy, final boolean asBitmap) {
        WXLogUtils.d("weex", "glideImage: " + url);
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    if(!asBitmap && (view==null || view.getLayoutParams()==null)){
                        return;
                    }
                    if (TextUtils.isEmpty(url)) {
                        if(view != null) {
                            view.setImageBitmap(null);
                        }
                        return;
                    }

                    if(context == null) {
                        return;
                    }

                    if(!asBitmap) {
                        loadPlaceHolder(view, strategy);
                    }

                    String temp = url;

                    Pattern pattern = Pattern.compile("^data:image/[^;]+;([^,]+),([0-9a-zA-Z=/+]+)$");
                    Matcher m = pattern.matcher(temp);
                    // 如果是base64图片
                    if(m.matches()) {
                        String type = m.group(1);
                        String base64Data = m.group(2);
                        if("base64".equals(type)) {
                            loadImage(context, url, view, quality, strategy, asBitmap, base64Data, 0);
                        }
                    }else {
                        Uri imgUri = Uri.parse(temp);
                        if("file".equals(imgUri.getScheme())) {
                            loadImage(context, url, view, quality, strategy, asBitmap, temp, 1);
                        }else {
                            if (url.startsWith("//")) {
                                temp = "http:" + url;
                            }
                            loadImage(context, url, view, quality, strategy, asBitmap, temp, 2);
                        }
                    }
                }catch (Exception ex) {
                    WXLogUtils.e("weex load image failed.", ex);
                }
            }
        };
        if(Thread.currentThread() == Looper.getMainLooper().getThread()){
            runnable.run();
        }else {
            WXSDKManager.getInstance().postOnUiThread(runnable, 0);
        }
    }

    private void loadImage(final Context context, final String url, final ImageView view,
                           final WXImageQuality quality, final WXImageStrategy strategy, final boolean asBitmap, String temp, int type) {
        DrawableTypeRequest imageReq;
        if(type == 0) { // base64
            imageReq = Glide.with(context).load(Base64.decode(temp, Base64.DEFAULT));
        }else if(type == 1) { // local file
            imageReq = Glide.with(context).load(Uri.parse(temp));
        }else {
            if(temp.startsWith("http") || temp.startsWith("https")) {
                imageReq = Glide.with(context).load(new QNCacheGlideUrl(temp));
            }else {
                imageReq = Glide.with(context).load(temp);
            }
        }

        if(!asBitmap) {
            if(strategy.centerCrop) {
                imageReq.centerCrop();
            }
            if(strategy.placeHolderResource != -1) {
                imageReq.placeholder(AppCompatResources.getDrawable(context, strategy.placeHolderResource));
            }
            if(strategy.sizeWidth != -1 && strategy.sizeHeight != -1) {
                imageReq.override(strategy.sizeWidth, strategy.sizeHeight);
            }
            imageReq.listener(new RequestListener() {
                @Override
                public boolean onException(Exception e, Object model, Target target, boolean isFirstResource) {
                    return GlideImageAdapter.this.onException(url, view, quality, strategy);
                }

                @Override
                public boolean onResourceReady(Object resource, Object model, Target target, boolean isFromMemoryCache, boolean isFirstResource) {
                    return GlideImageAdapter.this.onResourceReady(resource, url, view, quality, strategy);
                }
            }).into(view);
        }else {
            imageReq.asBitmap().into(new SimpleTarget<Bitmap>() {
                @Override
                public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
                    if(strategy.getImageDownloadListener() != null) {
                        strategy.getImageDownloadListener().onImageFinish(url,
                                resource, true, null);
                    }
                }

                @Override
                public void onLoadFailed(Exception e, Drawable errorDrawable) {
                    if(strategy.getImageDownloadListener() != null) {
                        strategy.getImageDownloadListener().onImageFinish(url,
                                null, false, null);
                    }
                }
            });
        }
    }

    private void loadPlaceHolder(final ImageView view, final WXImageStrategy strategy) {
        if (!TextUtils.isEmpty(strategy.placeHolder)) {
            Target placeHoderTarget = Glide.with(view.getContext()).load(strategy.placeHolder).into(view);
            view.setTag(strategy.placeHolder.hashCode(), placeHoderTarget);
        }
    }

    private boolean onException(final String url, final ImageView view, WXImageQuality quality, final WXImageStrategy strategy) {
        if (strategy.getImageListener() != null) {
            strategy.getImageListener().onImageFinish(url, view, false, null);
        }
        return false;
    }

    private boolean onResourceReady(Object resource, final String url, final ImageView view, WXImageQuality quality, final WXImageStrategy strategy) {
        if (strategy.getImageListener() != null) {
            Map extra = new HashMap();
            if(resource instanceof Drawable) {
                extra.put("width", ((Drawable) resource).getIntrinsicWidth());
                extra.put("height", ((Drawable) resource).getIntrinsicHeight());
            }
            strategy.getImageListener().onImageFinish(url, view, true, extra);
        }

        if (!TextUtils.isEmpty(strategy.placeHolder)) {
            Target placeHoderTarget = (Target) view.getTag(strategy.placeHolder.hashCode());
            if (placeHoderTarget != null) {
                Glide.clear(placeHoderTarget);
            }
        }
        return false;
    }

    // 阿里OSS图片缓存修改
    private static class QNCacheGlideUrl extends GlideUrl {

        private String mUrl;

        public QNCacheGlideUrl(String url) {
            super(url);
            mUrl = url;
        }

        @Override
        public String getCacheKey() {
            return checkQnUrl() && !TextUtils.isEmpty(getQNCacheKey()) ? getQNCacheKey() : super.getCacheKey();
        }

        private String getQNCacheKey() {
            String cacheKey = null;
            int index = mUrl.indexOf("?");
            if(index > 0) {
                cacheKey = mUrl.substring(0, index);
                index = mUrl.indexOf("x-oss-process");
                if(index > 0) {
                    cacheKey = cacheKey + "?" + mUrl.substring(index);
                }
            }
            return cacheKey;
        }

        public boolean checkQnUrl() {
            if (!TextUtils.isEmpty(mUrl) && (mUrl.contains("&OSSAccessKeyId=") || mUrl.contains("?OSSAccessKeyId="))) {
                return true;
            }
            return false;
        }
    }
}
