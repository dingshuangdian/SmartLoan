package com.mmt.smartloan.utils;


import android.util.Log;


/**
 * 日志信息输出类
 * <p>该类可自动或手动配置不同等级日志在发布模式下是否允许输出，
 * 并使用android.util.Log输出日志内容</p>
 */
public class LogUtils {

    /**
     * 新增快捷调试，过滤标识为TAG
     */
    protected static String tag = "TAG";

    public static void d(String tag, String msg) {
        if (DebugUtils.isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void i(String tag, String msg) {
        if (DebugUtils.isDebug()) {
            Log.i(tag, msg);
        }
    }

    public static void v(String tag, String msg) {
        if (DebugUtils.isDebug()) {
            Log.v(tag, msg);
        }
    }

    public static void w(String tag, String msg) {
        if (DebugUtils.isDebug()) {
            Log.w(tag, msg);
        }
    }

    public static void e(String tag, String msg) {
        if (DebugUtils.isDebug()) {
            Log.e(tag, msg);
        }
    }

    public static void d(String msg) {
        if (DebugUtils.isDebug()) {
            Log.d(tag, msg);
        }
    }

    public static void i(String msg) {
        if (DebugUtils.isDebug()) {
            Log.i(tag, msg);
        }
    }

    public static void v(String msg) {
        if (DebugUtils.isDebug()) {
            Log.v(tag, msg);
        }
    }

    public static void w(String msg) {
        if (DebugUtils.isDebug()) {
            Log.w(tag, msg);
        }
    }

    public static void e(String msg) {
        if (DebugUtils.isDebug()) {
            Log.e(tag, msg);
        }
    }
}
