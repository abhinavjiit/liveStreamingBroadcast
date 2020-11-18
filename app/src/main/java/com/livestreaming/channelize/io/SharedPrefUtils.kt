package com.livestreaming.channelize.io

import android.content.Context

class SharedPrefUtils private constructor() {


    companion object {

        private const val COMMON_PREF_FILE: String = "big_step_LSC_prefs"
        private const val LOGGED_IN: String = "login"

        fun setLoggedInFlag(context: Context, isLoggedIn: Boolean) {
            val sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putBoolean(LOGGED_IN, isLoggedIn)
            editor.commit()
        }

        fun getLoggedInFlag(context: Context): Boolean {
            val sharedPref = context.getSharedPreferences(COMMON_PREF_FILE, Context.MODE_PRIVATE)
            return sharedPref.getBoolean(LOGGED_IN, false)
        }
    }


}