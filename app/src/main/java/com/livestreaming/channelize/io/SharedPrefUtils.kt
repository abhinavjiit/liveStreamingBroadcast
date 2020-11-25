package com.livestreaming.channelize.io

import android.content.Context

class SharedPrefUtils private constructor() {


    companion object {

        private const val COMMON_PREF_FILE: String = "big_step_LSC_prefs"
        private const val LOGGED_IN: String = "login"
        private const val PUBLIC_API_KEY = "publicApiKey"
        private const val CAMERA_PERMISSION_FLAG = "camera"
        private const val MICROPHONE_PERMISSION_FLAG = "microPhone"
        private const val INSTRUCTIONS_SHOWN_FLAG = "instructionsFlag"

        private const val STORE_URL = "storeUrl"
        fun setLoggedInFlag(context: Context, isLoggedIn: Boolean) {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putBoolean(LOGGED_IN, isLoggedIn)
            editor.commit()
        }

        fun getLoggedInFlag(context: Context): Boolean {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(LOGGED_IN, false)
        }


        fun setPublicApiKey(context: Context, apiKey: String?) {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString(PUBLIC_API_KEY, apiKey)
            editor.commit()
        }


        fun getPublicApiKey(context: Context): String? {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getString(PUBLIC_API_KEY, null)
        }


        fun clearSharedPref(context: Context) {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.clear()
            editor.commit()
        }


        fun cameraPermissionGranted(context: Context, flag: Boolean) {

        }

        fun microPhonePermissionGranted(context: Context, flag: Boolean) {

        }

        fun getCameraPermissionFlag(context: Context): Boolean {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(CAMERA_PERMISSION_FLAG, false)

        }

        fun getMicrophonePermissionFlag(context: Context): Boolean {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(MICROPHONE_PERMISSION_FLAG, false)
        }


        fun setInstructionsShownFlag(context: Context, flag: Boolean) {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putBoolean(INSTRUCTIONS_SHOWN_FLAG, flag)
            editor.commit()
        }

        fun getInstructionsShownFlag(
            context: Context
        ): Boolean {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getBoolean(INSTRUCTIONS_SHOWN_FLAG, false)
        }


        fun getStoreUrl(context: Context): String? {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getString(STORE_URL, null)
        }

        fun setStoreUrl(context: Context, url: String) {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString(STORE_URL, url)
            editor.commit()
        }

        fun setAppID(context: Context, appId: String) {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            val editor = sharedPref.edit()
            editor.putString("appID", appId)
            editor.commit()
        }

        fun getAppId(context: Context): String? {
            val sharedPref = context.applicationContext.getSharedPreferences(
                COMMON_PREF_FILE,
                Context.MODE_PRIVATE
            )
            return sharedPref.getString("appID", null)
        }
    }


}