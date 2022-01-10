package com.mmt.smartloan.utils;

import com.mmt.smartloan.cache.BaseCacheManager;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2022/1/10<p>
 */
public class UserInfoUtils {
    /**
     * 获取用户token
     */
    public static String getToken() {
        return BaseCacheManager.getUserTemp().getToken();
    }


    /**
     * 获取用户id
     */
    public static String getUserId() {
        return BaseCacheManager.getUserTemp().getUserId();
    }
}
