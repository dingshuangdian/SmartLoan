package com.mmt.smartloan.utils;

import android.content.pm.ApplicationInfo;

/**
 * @desc: 判断是否为debug模式
 */
public class DebugUtils {

    private static Boolean isDebug = null;

    public static boolean isDebug() {
        return isDebug != null && isDebug;
    }

    /**
     * Sync lib debug with app's debug value. Should be called in module Application
     */
    public static void initDebugState() {
        if (isDebug == null) {
            isDebug = ContextHolder.getContext().getApplicationInfo() != null &&
                    (ContextHolder.getContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        }
    }

}
