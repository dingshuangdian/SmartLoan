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
    private static final String KEY_PHONE = "KEY_PHONE";//角色id
    private static final String KEY_TOKEN = "KEY_TOKEN";//token
    private static final String INSTALLREFERCE = "INSTALLREFERCE";//installreferce
    private static final String GAID = "GAID";//gaid
    private static final String UTMSOURCE = "UTMSOURCE";//utmSource
    private static final String IPADDRESS = "IPADDRESS";//IPADDRESS

    public BaseTempPreferences() {
        super(BaseTempPreferences.class.getSimpleName(), 1);
    }

    public void setToken(String token) {
        put(KEY_TOKEN, token);
    }

    public String getToken() {
        return getString(KEY_TOKEN, "");
    }

    public void setUtmSource(String utmSource) {
        put(UTMSOURCE, utmSource);
    }

    public void setIpaddress(String ipAddress) {
        put(IPADDRESS, ipAddress);
    }

    public String getIpaddress() {
        return getString(IPADDRESS, "");
    }

    public void setPhone(String phone) {
        put(KEY_PHONE, phone);
    }

    public String getPhone() {
        return getString(KEY_PHONE, "");
    }

    public String getUtmSource() {
        return getString(UTMSOURCE, "");
    }

    public void setINSTALLREFERCE(String installreferce) {
        put(INSTALLREFERCE, installreferce);
    }

    public String getINSTALLREFERCE() {
        return getString(INSTALLREFERCE, "");
    }

    public void setGaid(String gaid) {
        put(GAID, gaid);
    }

    public String getGaid() {
        return getString(GAID, "");
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
