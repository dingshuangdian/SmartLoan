package com.mmt.smartloan.rxjava.network;

import com.mmt.smartloan.utils.DebugUtils;

import com.orhanobut.logger.Logger;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/10<p>
 */
public class LogManager {
    private static final String TAG_API = "API";
    private static final String TAG_EXCEPTION = "EXCEPTION";

    /**
     * 异常api
     */
    public static void exception(String msg, Throwable throwable) {
        if (DebugUtils.isDebug()) {
            Logger.t(TAG_EXCEPTION).e(throwable, msg);
        }
    }

    /**
     * 接口api
     */
    public static void api(String msg) {
        if (DebugUtils.isDebug()) {
            Logger.t(TAG_API).e(msg);
        }
    }

}

