# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/guohai/Dev/android-sdk-macosx/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

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

-keep class android.app.**  {*;}
-keep class com.peergine.**  {*;}
-keep class com.thermal.**  {*;}
-keep class com.kuyou.avc.handler.photo.**  {*;}
