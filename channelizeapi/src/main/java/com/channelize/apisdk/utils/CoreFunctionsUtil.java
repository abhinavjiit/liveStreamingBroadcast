/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.utils;


import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import androidx.annotation.NonNull;
import android.webkit.MimeTypeMap;

import com.channelize.apisdk.network.response.GenericResponse;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.channelize.apisdk.network.api.ChannelizeOkHttpUtil.GSON_INSTANCE;


public class CoreFunctionsUtil {

    // Checking the network connectivity
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = null;
        if (cm != null) {
            activeNetwork = cm.getActiveNetworkInfo();
        }
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    /**
     * Used to build url string.
     *
     * @param queryParams query parameters to be added in url
     * @param requestUrl  - url in which the parameters will be added
     */
    public static String buildQueryString(String requestUrl, Map<String, Object> queryParams) {
        for (Map.Entry<String, Object> entry : queryParams.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            requestUrl = Uri.parse(requestUrl)
                    .buildUpon()
                    .appendQueryParameter(key, String.valueOf(value))
                    .build().toString();
        }
        return requestUrl;
    }

    /**
     * Method to get the current time stamp.
     *
     * @return Returns the current time stamp.
     */
    public static String getCurrentTimeStamp() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ",
                Locale.getDefault());
        format.setTimeZone(TimeZone.getTimeZone("UTC"));
        return format.format(date).replace("+0000", "Z");
    }

    /**
     * Method to capitalize the first letter of each word.
     *
     * @param capString String title which needs to be capitalize.
     * @return Returns the updated string.
     */
    public static String capitalizeTitle(String capString) {
        if (capString != null && !capString.isEmpty()) {
            StringBuffer capBuffer = new StringBuffer();
            Matcher capMatcher = Pattern.compile("([a-z])([a-z]*)", Pattern.CASE_INSENSITIVE).matcher(capString);
            while (capMatcher.find()) {
                capMatcher.appendReplacement(capBuffer, capMatcher.group(1).toUpperCase() + capMatcher.group(2).toLowerCase());
            }

            return capMatcher.appendTail(capBuffer).toString();
        } else {
            return capString;
        }
    }

    /**
     * Method to get the mime type of the file.
     *
     * @param filePath File path of the file.
     * @return Returns the mime type.
     */
    public static String getMimeType(String filePath) {
        Uri uri = Uri.parse(filePath);
        String fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString());
        String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        if (mimeType == null || mimeType.isEmpty()) {
            filePath = filePath.toLowerCase();
            if (filePath.contains(".jpg")) {
                mimeType = "image/jpeg";
            } else if (filePath.contains("mp4")) {
                mimeType = "video/mp4";
            } else if (filePath.contains("mp3")) {
                mimeType = "audio/mpeg";
            } else {
                mimeType = "image/*";
            }
        }
        return mimeType;
    }

    /**
     * Method used to get the meta value from a particular type.
     *
     * @param context      Context of calling class.
     * @param metaDataName Name of the meta key value from which needs to be fetched.
     * @return Returns the meta data value.
     */
    public static String getMetaDataValue(Context context, String metaDataName) {
        try {
            ApplicationInfo ai = context.getPackageManager().getApplicationInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            if (ai.metaData != null) {
                return ai.metaData.getString(metaDataName);
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * Method to get the generic response from the response body.
     *
     * @param responseBody  ResponseBody which contains the response.
     * @param responseClass Class in which data needs to be parsed.
     * @return Returns the model.
     */
    public static <T extends GenericResponse> T readJsonResponse(String responseBody, @NonNull Class<T> responseClass) {
        return GSON_INSTANCE.fromJson(responseBody, responseClass);
    }

}
