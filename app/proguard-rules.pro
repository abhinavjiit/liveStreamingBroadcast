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
-dontobfuscate
#-keep class io.agora.**{*;}
#modules
-keepclassmembers class com.livestreaming.channelize.io.model.**{*;}

# GSON
-keepattributes Signature
-keepattributes *Annotation*
-dontwarn sun.misc.**
-keep class com.google.gson.examples.android.model.** { *; }
-keep class * implements com.google.gson.TypeAdapterFactory
-keep class * implements com.google.gson.JsonSerializer
-keep class * implements com.google.gson.JsonDeserializer

-dontwarn com.squareup.picasso.OkHttpDownloader
-dontwarn kotlinx.coroutines.flow.**
-dontwarn org.reactivestreams.FlowAdapters
-dontwarn org.reactivestreams.**
-dontwarn java.util.concurrent.flow.**
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembernames class kotlinx.** {
    volatile <fields>;
}
-keep class org.json.** { *; }


#channelizeApiSdk module

-keepclassmembers class com.channelize.apisdk.network.response.**{*;}

-keepattributes LocalVariableTable

 -keepclassmembers class com.channelize.apisdk.ChannelizeConfig {
      public static *;
         public *;
 }

  -keepclassmembers class com.channelize.apisdk.ChannelizeConfig$* {
     public *;
  }

 -keepclassmembers class com.channelize.apisdk.Channelize {
     public static *;
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.ApiConstants {
     public static *;
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.Utils {
     public static *;
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.model.** {
    *;
 }

 -keepclassmembers class com.channelize.apisdk.utils.Logcat {
     public static *;
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.utils.OkHttpUtils {
      public static *;
      public *;
  }

 -keepclassmembers class com.channelize.apisdk.utils.CoreFunctionsUtil {
     public static *;
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.utils.ChannelizePreferences {
     public static *;
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.network.api.ChannelizeApi {
    *;
 }

 -keepclassmembers class com.channelize.apisdk.network.api.ChannelizeApiClient {
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.network.mqtt.ChannelizeConnectionHandler {
    *;
 }

 -keepclassmembers class com.channelize.apisdk.network.mqtt.ChannelizeUserEventHandler {
    *;
 }

 -keepclassmembers class com.channelize.apisdk.network.mqtt.ChannelizeConversationEventHandler {
    *;
 }

 -keepclassmembers class com.channelize.apisdk.network.services.ApiService {
     public *;
 }

 -keepclassmembers class com.channelize.apisdk.network.services.query.** {
     public *;
 }
