#---------------------------------基本指令区----------------------------------
# 指定代码的压缩级别5 (0 ~ 7)
-optimizationpasses 5
# 不使用大小写混合
-dontusemixedcaseclassnames
# 不混淆第三方jar
-dontskipnonpubliclibraryclasses
# 混淆时不做预校验
-dontpreverify
#不压缩输入的类文件
-dontshrink
# 混淆时是否记录日志
-verbose
# 混淆时所采用的算法
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

# 不混淆安卓api
-keep class android.**{*;}
-keep public class * extends android.app.Fragment
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.**
-keep public class com.android.vending.licensing.ILicensingService
-keep public class * extends android.webkit.**
-keep public class * extends com.squareup.okhttp.**
# support-v7-appcompat
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}
# support-design
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * { native <methods>;}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet);
}

# 保持自定义控件类不被混淆
-keepclasseswithmembers class * {
 public <init>(android.content.Context, android.util.AttributeSet, int);
}

# 保持自定义控件类不被混淆
-keepclassmembers class * extends android.app.Activity {
 public void *(android.view.View);
}

# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
 public static **[] values();
 public static ** valueOf(java.lang.String);
}

# 保持 Parcelable 不被混淆
-keep class * implements android.os.Parcelable {
 public static final android.os.Parcelable$Creator *;
}
#----------------------------------------------------------------------------
#---------------------------------webview------------------------------------
#-keepclassmembers class fqcn.of.javascript.interface.for.Webview {
#   public *;
#}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String, android.graphics.Bitmap);
    public boolean *(android.webkit.WebView, java.lang.String);
}
-keepclassmembers class * extends android.webkit.WebViewClient {
    public void *(android.webkit.WebView, java.lang.String);
}
-keep class android.net.http.** {*;}
-dontwarn android.net.http.**

#---glide---
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

# weex
-keep class org.apache.weex.bridge.**{*;}
-keep class org.apache.weex.dom.**{*;}
-keep class org.apache.weex.layout.**{*;}
-keep class org.apache.weex.base.**{*;}
-keep class org.apache.weex.common.**{*;}
-keep class * implements org.apache.weex.IWXObject{*;}
-keep class org.apache.weex.ui.**{*;}
-keep class org.apache.weex.ui.component.**{*;}
-keep class org.apache.weex.utils.**{
    public <fields>;
    public <methods>;
    }
-keepclassmembers class * {
    @org.apache.weex.annotation.JSMethod *;
}

-keep class org.apache.weex.base.SystemMessageHandler { *; }
-dontwarn org.apache.weex.bridge.**

# mpweex
-keep class com.lmspay.zq.widget.indicators.**{*;}
-keep class com.lmspay.zq.model.**{*;}

# fastjosn
-keep class com.alibaba.fastjson.** {
 *;
}
-dontwarn com.alibaba.fastjson.**

## --- yismcore ---
-keep class net.yiim.yismcore.** {
    public *;
    native <methods>;
}

# 不混淆银联
-keep class com.unionpay.** {*;}
-keep interface com.unionpay.** {*;}
-keep class cn.gov.pbc.tsm.** {*;}
-keep interface cn.gov.pbc.tsm.** {*;}
-keep class org.simalliance.openmobileapi.** {*;}
-dontwarn com.unionpay.**
-dontwarn cn.gov.pbc.tsm.**

#--- okhttp ---
-dontwarn com.squareup.okhttp.**
-keep class com.squareup.okhttp.** { *;}
-keep class okio.** {*;}
-dontwarn okio.**

-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**

#--- org.apache.http ---
-keep class org.apache.http.** { *; }
-dontwarn android.net.**
-keep class android.net.SSLCertificateSocketFactory{*;}