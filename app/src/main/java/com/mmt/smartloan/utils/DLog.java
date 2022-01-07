package com.mmt.smartloan.utils;


/**
 * 日志信息输出类
 * <p>该类可自动或手动配置不同等级日志在发布模式下是否允许输出，
 * 并使用android.util.Log输出日志内容</p>
 */
public class DLog extends LogUtils {

    private static final String TAG = DLog.class.getCanonicalName();

    public static void e(String msg) {
        e(TAG, msg);
    }

    public static void v(String msg) {
        v(TAG, msg);
    }

    public static void d(String msg) {
        d(TAG, msg);
    }

}
