# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /root/Android/Sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript class
# class:
#-keepclassmembers class fqcn.of.javascript.class.for.webview {
#   public *;
#}

-keepattributes LocalVariableTable

 -keep public class com.channelize.apisdk.ChannelizeConfig {
     public static *;
     public *;
 }

  -keep public class com.channelize.apisdk.ChannelizeConfig$* {
     public *;
  }

 -keep public class com.channelize.apisdk.Channelize {
     public static *;
     public *;
 }

 -keep public class com.channelize.apisdk.ApiConstants {
     public static *;
     public *;
 }

 -keep public class com.channelize.apisdk.Utils {
     public static *;
     public *;
 }

 -keep public class com.channelize.apisdk.model.** {
    *;
 }

 -keep public class com.channelize.apisdk.utils.Logcat {
     public static *;
     public *;
 }

 -keep public class com.channelize.apisdk.utils.OkHttpUtils {
      public static *;
      public *;
  }

 -keep public class com.channelize.apisdk.utils.CoreFunctionsUtil {
     public static *;
     public *;
 }

 -keep public class com.channelize.apisdk.utils.ChannelizePreferences {
     public static *;
     public *;
 }

 -keep public class com.channelize.apisdk.network.api.ChannelizeApi {
    *;
 }

 -keep public class com.channelize.apisdk.network.api.ChannelizeApiClient {
     public *;
 }

 -keep public class com.channelize.apisdk.network.mqtt.ChannelizeConnectionHandler {
    *;
 }

 -keep public class com.channelize.apisdk.network.mqtt.ChannelizeUserEventHandler {
    *;
 }

 -keep public class com.channelize.apisdk.network.mqtt.ChannelizeConversationEventHandler {
    *;
 }

 -keep public class com.channelize.apisdk.network.services.ApiService {
     public *;
 }

 -keep public class com.channelize.apisdk.network.services.query.** {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.ChannelizeError {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.CompletionHandler {
     *;
 }

 -keep public class com.channelize.apisdk.network.response.ListConversationResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.ListMemberResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.ListMessageResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.ListUserResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.LoginResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.RequestResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.TotalCountResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.UserBlockStatusResponse {
     public *;
 }

 -keep public class com.channelize.apisdk.network.response.GenericResponse {
     *;
 }


# To keep all classes in a package.
# -keep class package.** {
#    *;
#  }

# To keep a class and its inner classes.
# -keep public class package.ClassName {
#    *;
# }
#
# -keep public class package.ClassName$* {
#    *;
# }

# -keep public class * implements mypackage.MyInterface
