# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in E:\sdk/tools/proguard/proguard-android.txt
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

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile
-keep class com.codecanyon.hidephotovideo.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.adapter.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.activity.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.adepter.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.helper.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.listeners.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.picker.model.** {
 *;
}

-keep class com.codecanyon.hidephotovideo.utils.view.** {
 *;
}

-dontwarn com.samsung.android.sdk.pass.**
-keep class com.samsung.android.sdk.pass.** {*;}

-keep public class com.google.ads.** {*;}
-keep public class com.google.android.gms.** {*;}
-keep class com.google.android.gms.** {
   public *;
}
-keep class android.support.** { *; }