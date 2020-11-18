/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.channelize.apisdk.model.User;


public class ChannelizePreferences {

    // User preference
    private static final String CHANNELIZE_USER_PREF = "channelize_current_user_pref";
    private static final String CHANNELIZE_UI_PREF = "channelize_ui_pref";
    private static final String CHANNELIZE_USER_ACCESS_TOKEN = "channelize_current_user_access_token";
    private static final String CHANNELIZE_USER = "channelize_current_user";
    private static final String CHANNELIZE_USER_ID = "channelize_current_user_id";
    private static final String CHANNELIZE_IS_MESSENGER_ACTIVE = "channelize_ismessenger_active";
    private static final String CHANNELIZE_IS_RECENTCHAT_VISIBLE = "channelize_isrecentchat_visible";
    private static final String CHANNELIZE_USER_NAME = "channelize_current_user_name";
    private static final String CHANNELIZE_USER_PROFILE_IMAGE = "channelize_current_user_profile_image";
    private static final String CHANNELIZE_USER_FCM_TOKEN = "channelize_current_user_fcm_token";
    private static final String CHANNELIZE_ENABLED_MODULES = "channelize_enabled_modules";


    /* Used to clear all the stored preferences*
     *  basically used at the time of SignOut  */
    public static void clearSharedPreferences(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    /**
     * Method to get the current user's access token.
     *
     * @param context Context of calling class.
     * @return Return's the logged-in user's access token.
     */
    public static String getAccessToken(Context context) {
        return context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_USER_ACCESS_TOKEN, null);
    }

    /**
     * Method to set current user access token in channelize app.
     *
     * @param context     Context of calling class.
     * @param accessToken Access token of the current user.
     */
    public static void setAccessToken(Context context, String accessToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHANNELIZE_USER_ACCESS_TOKEN, accessToken);
        editor.apply();
    }

    /**
     * Method to get the current user's id.
     *
     * @param context Context of calling class.
     * @return Return's the logged-in user's userId.
     */
    public static String getCurrentUserId(Context context) {
        return context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_USER_ID, null);
    }

    /**
     * Method to set current user id in channelize app.
     *
     * @param context       Context of calling class.
     * @param currentUserId UserId of the current user.
     */
    public static void setCurrentUserId(Context context, String currentUserId) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHANNELIZE_USER_ID, currentUserId);
        editor.apply();
    }


    public static void setMessengerActive(Context context, boolean isMessengerActive) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_UI_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANNELIZE_IS_MESSENGER_ACTIVE, isMessengerActive);
        editor.apply();
    }

    public static boolean getMessengerActive(Context context) {
        return context.getSharedPreferences(CHANNELIZE_UI_PREF, Context.MODE_PRIVATE)
                .getBoolean(CHANNELIZE_IS_MESSENGER_ACTIVE, false);
    }

    public static void setRecentChatVisible(Context context, boolean isRecentChatVisible) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_UI_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(CHANNELIZE_IS_RECENTCHAT_VISIBLE, isRecentChatVisible);
        editor.apply();
    }

    public static boolean getRecentChatVisible(Context context) {
        return context.getSharedPreferences(CHANNELIZE_UI_PREF, Context.MODE_PRIVATE)
                .getBoolean(CHANNELIZE_IS_RECENTCHAT_VISIBLE, false);
    }


    /**
     * Method to set current user profile image into channelize app.
     *
     * @param context          Context of calling class.
     * @param userProfileImage Profile Image url of the current user.
     */
    public static void setCurrentUserProfileImage(Context context, String userProfileImage) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHANNELIZE_USER_PROFILE_IMAGE, userProfileImage);
        editor.apply();
    }

    /**
     * Method to get the current user profile image.
     *
     * @param context Context of calling class.
     * @return Returns the logged-in user's profile image.
     */
    public static String getCurrentUserProfileImage(Context context) {
        return context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_USER_PROFILE_IMAGE, null);
    }


    /**
     * Method to set current user display name into channelize app.
     *
     * @param context          Context of calling class.
     * @param userName Display name of the current user.
     */
    public static void setCurrentUserName(Context context, String userName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHANNELIZE_USER_NAME, userName);
        editor.apply();
    }

    /**
     * Method to get the current user profile image.
     *
     * @param context Context of calling class.
     * @return Returns the logged-in user's name.
     */
    public static String getCurrentUserName(Context context) {
        return context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_USER_NAME, null);
    }

    /**
     * Method to get the current user's last registered fcm token.
     *
     * @param context Context of calling class.
     * @return Return's the logged-in user's fcm token.
     */
    public static String getCurrentUserFcmToken(Context context) {
        return context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_USER_FCM_TOKEN, null);
    }

    /**
     * Method to set current user fcm token in channelize app.
     *
     * @param context  Context of calling class.
     * @param fcmToken FCM token of the current user.
     */
    public static void setCurrentUserFcmToken(Context context, String fcmToken) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHANNELIZE_USER_FCM_TOKEN, fcmToken);
        editor.apply();
    }


    /**
     * Method to set User model which contains the logged-in user info.
     *
     * @param context Context of calling class.
     * @param user    User instance which contains the logged-in user info.
     */
    public static void setChannelizeCurrentUser(Context context, User user) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if (user != null) {
            Gson gson = new Gson();
            String json = gson.toJson(user);
            editor.putString(CHANNELIZE_USER, json);
        } else {
            editor.putString(CHANNELIZE_USER, null);
        }
        editor.apply();
    }

    /**
     * Method to get the current user's object.
     *
     * @param context Context of the calling class.
     * @return Returns the User.
     */
    public static User getCurrentUser(Context context) {
        Gson gson = new Gson();
        String json = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_USER, null);
        if (json != null) {
            return gson.fromJson(json, User.class);
        } else {
            return null;
        }
    }

    /**
     * Method to set enabled modules list into channelize app.
     *
     * @param context        Context of calling class.
     * @param enabledModules List of enabled modules.
     */
    public static void setChannelizeEnabledModules(Context context, String enabledModules) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(CHANNELIZE_ENABLED_MODULES, enabledModules);
        editor.apply();
    }

    /**
     * Method to get the enabled modules list.
     *
     * @param context Context of calling class.
     * @return Returns the enabled modules list.
     */
    public static String getChannelizeEnabledModules(Context context) {
        return context.getSharedPreferences(CHANNELIZE_USER_PREF, Context.MODE_PRIVATE)
                .getString(CHANNELIZE_ENABLED_MODULES, null);
    }

}
