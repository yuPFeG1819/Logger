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
#####################################################
#
# 公共混淆部分，基本不需要动
#
####################################################
# 代码混淆压缩比，在0~7之间，默认为5，一般不需要改
-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写
-dontusemixedcaseclassnames
# 指定不去忽略非公共的库的类
-dontskipnonpubliclibraryclasses
# 指定不去忽略非公共的库的类的成员
-dontskipnonpubliclibraryclassmembers
# 不做预校验，可加快混淆速度
# preverify是proguard的4个步骤之一
# Android不需要preverify，去掉这一步可以加快混淆速度
-dontpreverify
# 不优化输入的类文件
-dontoptimize
# 混淆时生成日志文件，即映射文件
-verbose
# 指定映射文件的名称
-printmapping proguardMapping.txt
#混淆时所采用的算法
# 这个合并算法是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*
# 过滤保护代码中的Annotation不被混淆
-keepattributes *Annotation*
-keep class * extends java.lang.annotation.Annotation { *; }
-keep interface * extends java.lang.annotation.Annotation { *; }
# 忽略警告
#-ignorewarning
# 过滤保护泛型不被混淆
-keepattributes Signature
# 抛出异常时保留代码行号
-keepattributes SourceFile,LineNumberTable
#--------------------------默认需要保留的区域-------------------------
# 保留 R 下面的资源
-keep class **.R$* { *; }

# 保留我们使用的四大组件，自定义的 Application 等等这些类不被混淆
# 因为这些子类都有可能被外部调用
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.view.View
-dontnote com.google.vending.licensing.ILicensingService
# 保留 support 下的所有类及其内部类
-keep class android.support.** { *; }
# 保留继承support的子类
-keep public class * extends android.support.v4.**
-keep public class * extends android.support.v7.**
-keep public class * extends android.support.annotation.**

# -----------------保留androidx相关---------------------
-keep class com.google.android.material.** {*;}
-keep class androidx.** {*;}
-keep public class * extends androidx.**
-keep interface androidx.** {*;}
-dontwarn com.google.android.material.**
-dontnote com.google.android.material.**
-dontwarn androidx.**

#表示不混淆任何包含native方法的类的类名以及native方法名
-keepclasseswithmembernames class * {
    native <methods>;
}
#表示不混淆任何一个View中的setXxx()和getXxx()方法，
#因为属性动画需要有相应的setter和getter的方法实现，混淆了就无法工作了。
-keep public class * extends android.view.View{
    *** get*();
    void set*(***);
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

#这个主要是在layout 中写的onclick方法android:onclick="onClick"，不进行混淆
#表示不混淆Activity中参数是View的方法，因为有这样一种用法，在XML中配置android:onClick=”buttonClick”属性，
#当用户点击该按钮时就会调用Activity中的buttonClick(View view)方法，如果这个方法被混淆的话就找不到了
-keepclassmembers class * extends android.app.Activity{
    public void *(android.view.View);
}

# 保留枚举类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 保留 Parcelable 序列化类不被混淆
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# 保留 Serializable 序列化的类不被混淆
-keepnames class * implements java.io.Serializable
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    !private <fields>;
    !private <methods>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}