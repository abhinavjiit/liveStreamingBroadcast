/*
 *   Copyright (c) 2018 BigStep Technologies Private Limited.
 *
 *   The distribution of this source code is prohibited.
 */

package com.channelize.apisdk.utils;


import android.util.Log;

public class Logcat {

    private static final String LOG_PREFIX = "socialengine";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    private static boolean LOGGING_ENABLED = true;

    public static void setLoggingEnabled(boolean loggingEnabled) {
        LOGGING_ENABLED = loggingEnabled;
    }

    public static String makeLogTag(String str) {
        if (str.length() > MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH) {
            return LOG_PREFIX + str.substring(0, MAX_LOG_TAG_LENGTH - LOG_PREFIX_LENGTH - 1);
        }

        return LOG_PREFIX + str;
    }

    /**
     * Don't use this when obfuscating class names!
     */
    public static String makeLogTag(Class cls) {
        return makeLogTag(cls.getSimpleName());
    }

    public static void d(final Class cls, String message) {
        if (LOGGING_ENABLED) {
            Log.d(cls.getSimpleName(), message);
        }
    }

    public static void d(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.d(tag, message);
        }
    }

    public static void d(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.d(tag, message, cause);
        }
    }

    public static void v(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.v(tag, message);
        }
    }

    public static void v(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.v(tag, message, cause);
        }
    }

    public static void i(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message);
        }
    }

    public static void i(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.i(tag, message, cause);
        }
    }

    public static void w(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message);
        }
    }

    public static void w(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.w(tag, message, cause);
        }
    }

    public static void e(final String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message);
        }
    }

    public static void e(final String tag, String message, Throwable cause) {
        if (LOGGING_ENABLED) {
            Log.e(tag, message, cause);
        }
    }

    private Logcat() {
    }

}
