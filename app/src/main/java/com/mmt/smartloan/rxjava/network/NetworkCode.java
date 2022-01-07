package com.mmt.smartloan.rxjava.network;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class NetworkCode {
    public static final int ERROR_SUCCESS = 200;
    public static final int ERROR_UNLOGIN = 300;
    private static final Map<String, String> messages = new HashMap<>();

    static {
        messages.put(String.valueOf(ERROR_UNLOGIN), "登录过期，请重新登录");
    }

    public static String getMessage(String code) {
        String msg = null;
        if (messages != null) {
            msg = messages.get(code);
        }
        return msg;
    }
}
