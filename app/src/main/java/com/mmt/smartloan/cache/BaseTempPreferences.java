package com.mmt.smartloan.cache;

import com.alibaba.fastjson.JSONArray;

import java.util.ArrayList;
import java.util.List;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 * Desc: 用户参数缓存(当前登录用户，注销时清空)
 */
public class BaseTempPreferences extends BaseDiskCache {
    //当前用户登录信息
    private static final String KEY_IS_LOGIN = "KEY_IS_LOGIN";//是否登陆
    private static final String KEY_USER_ID = "KEY_USER_ID";//用户id
    private static final String KEY_ROLE_ID = "KEY_ROLE_ID";//角色id
    private static final String KEY_TOKEN = "KEY_TOKEN";//token

    public BaseTempPreferences() {
        super(BaseTempPreferences.class.getSimpleName(), 1);
    }

    public void setToken(String token) {
        put(KEY_TOKEN, token);
    }

    public String getToken() {
        return getString(KEY_TOKEN, "");
    }

    public void setUserId(String userId) {
        put(KEY_USER_ID, userId);
        //执行完后运行用户缓存初始化
        BaseCacheManager.initUserPreferences(userId);
    }
    public String getUserId() {
        return getString(KEY_USER_ID, "");
    }
}
