# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# ================== basic =====================
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** v(...);
}

-assumenosideeffects class java.lang.System {
    public static *** out(...);
}

-printmapping proguardMapping.txt #输出混淆前后代码映射关系
-keepattributes Signature #保留泛型
-keepattributes SourceFile, LineNumberTable #崩溃抛出异常时,保留源码文件名和源码行号

# ================== common =====================

#android framework
-keep class android.app.**  {*;}

#eventBus
-keepattributes *Annotation*
-keepclassmembers class ** {
    @org.greenrobot.eventbus.Subscribe <methods>;
}
-keep enum org.greenrobot.eventbus.ThreadMode { *; }

#kuyou IPC
-keep class kuyou.common.ipc**  {*;}
-keep class * extends kuyou.common.ipcRemoteEventConverter {
    *;
}

#kuyou device local config
-keep class kuyou.common.ku09.config.**  {*;}

# ================== private =====================

-dontwarn kuyou.sdk.jt808.oksocket.client.**
-dontwarn kuyou.sdk.jt808.oksocket.common.**
-dontwarn kuyou.sdk.jt808.oksocket.server.**
-dontwarn kuyou.sdk.jt808.oksocket.core.**

-keep class kuyou.sdk.jt808.oksocket.client.** { *; }
-keep class kuyou.sdk.jt808.oksocket.common.** { *; }
-keep class kuyou.sdk.jt808.oksocket.server.** { *; }
-keep class kuyou.sdk.jt808.oksocket.core.** { *; }

-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
-keep class kuyou.sdk.jt808.oksocket.client.sdk.client.OkSocketOptions$* {
    *;
}
-keep class kuyou.sdk.jt808.oksocket.server.impl.OkServerOptions$* {
    *;
}