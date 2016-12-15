package com.margin.mgms.util;

import android.util.Log;

import com.margin.mgms.BuildConfig;
import com.margin.mgms.misc.Config;

/**
 * Created on March 23, 2016.
 *
 * @author Marta.Ginosyan
 */
@SuppressWarnings("unused")
public class LogUtils {
    private static final String LOG_PREFIX = "COM_";
    private static final int LOG_PREFIX_LENGTH = LOG_PREFIX.length();
    private static final int MAX_LOG_TAG_LENGTH = 23;

    private static final String PREFIX_VVV = "vvv";

    private LogUtils() {
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

    public static void LOGD(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Config.IS_DOGFOOD_BUILD || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message);
        }
    }

    public static void LOGD(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG || Config.IS_DOGFOOD_BUILD || Log.isLoggable(tag, Log.DEBUG)) {
            Log.d(tag, message, cause);
        }
    }

    public static void LOGV(final String tag, String message) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message);
        }
    }

    public static void LOGV(final String tag, String message, Throwable cause) {
        //noinspection PointlessBooleanExpression,ConstantConditions
        if (BuildConfig.DEBUG && Log.isLoggable(tag, Log.VERBOSE)) {
            Log.v(tag, message, cause);
        }
    }

    public static void LOGI(final String tag, String message) {
        Log.i(tag, message);
    }

    public static void LOGI(final String tag, String message, Throwable cause) {
        Log.i(tag, message, cause);
    }

    public static void LOGW(final String tag, String message) {
        Log.w(tag, message);
    }

    public static void LOGW(final String tag, String message, Throwable cause) {
        Log.w(tag, message, cause);
    }

    public static void LOGE(final String tag, String message) {
        Log.e(tag, message);
    }

    public static void LOGE(final String tag, String message, Throwable cause) {
        Log.e(tag, message, cause);
    }

    public static void wtf(String msg) {
        if (BuildConfig.DEBUG) Log.wtf(PREFIX_VVV, msg);
    }

    public static void i(String msg) {
        if (BuildConfig.DEBUG) Log.i(PREFIX_VVV, msg);
    }

    public static void e(String msg) {
        if (BuildConfig.DEBUG) Log.wtf(PREFIX_VVV, msg);
    }

    public static void wtf(int msg) {
        wtf("" + msg);
    }

    public static void i(int msg) {
        i("" + msg);
    }

    public static void e(int msg) {
        e("" + msg);
    }

    public static void wtf(boolean msg) {
        wtf("" + msg);
    }

    public static void i(boolean msg) {
        i("" + msg);
    }

    public static void e(boolean msg) {
        e("" + msg);
    }

    public static void wtf(float msg) {
        wtf("" + msg);
    }

    public static void i(float msg) {
        i("" + msg);
    }

    public static void e(float msg) {
        e("" + msg);
    }

}
