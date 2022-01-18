package com.mmt.smartloan.base;

import android.os.Build;

import com.google.gson.Gson;
import com.mmt.smartloan.BuildConfig;
import com.mmt.smartloan.cache.BaseCacheManager;
import com.mmt.smartloan.utils.device.DeviceUtils;
import com.mmt.smartloan.utils.device.JSDCardUtils;

import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;

public class BaseParameter {
    public static RequestBody addActiveParams(String gaid, String installReferce, String installReferceClickTime, String installStartTime) {
        Map<String, Object> map = new HashMap<>();
        map.put("ipAddress", BaseCacheManager.getUserTemp().getIpaddress());
        map.put("gaid", gaid);
        map.put("installReferce", installReferce);
        map.put("installReferceClickTime", installReferceClickTime);
        map.put("installStartTime", installStartTime);
        map.put("adrVersion", Build.VERSION.SDK_INT);
        map.put("appVersion", BuildConfig.VERSION_NAME);
        map.put("androidId", DeviceUtils.getAndroidId(BaseApplication.getAppContext()));//设备id
        map.put("imei", DeviceUtils.getIMEI(BaseApplication.getAppContext()));
        map.put("packageName", BuildConfig.APPLICATION_ID);
        map.put("packageSource", "GooglePlay");
        map.put("appName", DeviceUtils.getAppName(BaseApplication.getAppContext()));
        map.put("meid", DeviceUtils.getMEID(BaseApplication.getAppContext()));
        map.put("imei1", DeviceUtils.getIMEI1(BaseApplication.getAppContext()));
        map.put("imei2", DeviceUtils.getIMEI2(BaseApplication.getAppContext()));
        map.put("mac", DeviceUtils.getMac(BaseApplication.getAppContext()));
        map.put("gsfid", DeviceUtils.getGsfAndroidId(BaseApplication.getAppContext()));
        map.put("serial", Build.SERIAL);
        map.put("imsi1", DeviceUtils.getIMSI1(BaseApplication.getAppContext()));
        map.put("imsi2", DeviceUtils.getIMSI2(BaseApplication.getAppContext()));
        map.put("iccid1", DeviceUtils.getICCID1(BaseApplication.getAppContext()));
        map.put("iccid2", DeviceUtils.getICCID2(BaseApplication.getAppContext()));
        map.put("memorySize", DeviceUtils.getTotalMem());
        map.put("ramTotalSize", DeviceUtils.getRamTotalSize(BaseApplication.getAppContext()));
        map.put("imagesInternal", JSDCardUtils.getImagesInternalCount(BaseApplication.getAppContext()));
        map.put("releaseDate", Build.TIME);
        map.put("deviceName", Build.PRODUCT);
        map.put("phoneBrand", Build.BRAND);
        map.put("isRooted", DeviceUtils.isRoot());
        map.put("sysVersion", Build.VERSION.RELEASE);
        map.put("language", DeviceUtils.getDefaultLanguage());
        map.put("localeDisplayLanguage", DeviceUtils.getDefaultDisplayLanguage());
        map.put("timeZoneId", DeviceUtils.getTimeZoneId());
        map.put("apiLevel", Build.VERSION.SDK_INT);
        map.put("localeIso3Country", DeviceUtils.getDefaultIsoCountry());
        map.put("localeIso3Language", DeviceUtils.getDefaultIsoLanguage());
        map.put("timeZone", DeviceUtils.getTimeZone());
        map.put("sensorList", DeviceUtils.getSensorList(BaseApplication.getAppContext()));
        map.put("networkOperatorName", DeviceUtils.getNetWorkOperatorName(BaseApplication.getAppContext()));
        map.put("isUsingProxyPort", DeviceUtils.isUsingVPN());
        map.put("channelId", "SmartLoan");
        map.put("afId", "afId");
        map.put("imagesExternal", JSDCardUtils.getImagesExternalCount(BaseApplication.getAppContext()));
        return RequestBody.create(null, new Gson().toJson(map));
    }

    public static Map<String, Object> existsByMobile(String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        return map;

    }

    public static RequestBody getVerifyCode(String mobile, int type, String gaid) {
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("type", type);//Int  1-登录 2-注册 3-重置密码 5验证银行卡号码
        map.put("gaid", gaid);
        map.put("androidId", DeviceUtils.getAndroidId(BaseApplication.getAppContext()));
        map.put("versionCode", BuildConfig.VERSION_CODE);

        return RequestBody.create(null, new Gson().toJson(map));

    }

    public static Map<String, Object> loginSys(String mobile, String verifyCode, boolean auto) {
        Map<String, Object> map = new HashMap<>();
        map.put("mobile", mobile);
        map.put("verifyCode", verifyCode);
        map.put("gaid", BaseCacheManager.getUserTemp().getGaid());
        map.put("adrVersion", Build.VERSION.SDK_INT);
        map.put("appVersion", BuildConfig.VERSION_NAME);
        map.put("channelId", "SmartLoan");
        map.put("imei", DeviceUtils.getIMEI(BaseApplication.getAppContext()));
        map.put("packageName", BuildConfig.APPLICATION_ID);
        map.put("deviceId", Build.ID);
        map.put("installReferce", BaseCacheManager.getUserTemp().getINSTALLREFERCE());
        map.put("apiLevel", Build.VERSION.SDK_INT);
        map.put("meid", DeviceUtils.getMEID(BaseApplication.getAppContext()));
        map.put("imei1", DeviceUtils.getIMEI1(BaseApplication.getAppContext()));
        map.put("imei2", DeviceUtils.getIMEI2(BaseApplication.getAppContext()));
        map.put("mac", DeviceUtils.getMac(BaseApplication.getAppContext()));
        map.put("androidId", DeviceUtils.getAndroidId(BaseApplication.getAppContext()));
        map.put("gsfid", DeviceUtils.getGsfAndroidId(BaseApplication.getAppContext()));
        map.put("serial", Build.SERIAL);
        map.put("imsi1", DeviceUtils.getIMSI1(BaseApplication.getAppContext()));
        map.put("imsi2", DeviceUtils.getIMSI2(BaseApplication.getAppContext()));
        map.put("iccid1", DeviceUtils.getICCID1(BaseApplication.getAppContext()));
        map.put("iccid2", DeviceUtils.getICCID2(BaseApplication.getAppContext()));
        map.put("releaseDate", Build.TIME);
        map.put("deviceName", Build.PRODUCT);
        map.put("phoneBrand", Build.BRAND);
        map.put("isRooted", DeviceUtils.isRoot());
        map.put("sysVersion", Build.VERSION.RELEASE);
        map.put("language", DeviceUtils.getDefaultLanguage());
        map.put("localeDisplayLanguage", DeviceUtils.getDefaultDisplayLanguage());
        map.put("timeZoneId", DeviceUtils.getTimeZoneId());
        map.put("timeZone", DeviceUtils.getTimeZone());
        map.put("localeIso3Country", DeviceUtils.getDefaultIsoCountry());
        map.put("localeIso3Language", DeviceUtils.getDefaultIsoLanguage());
        map.put("ipAddress", BaseCacheManager.getUserTemp().getIpaddress());
        map.put("packageSource", "GooglePlay");
        map.put("appName", DeviceUtils.getAppName(BaseApplication.getAppContext()));
        map.put("verified", !auto);
        return map;

    }

    public static RequestBody registerSys(String mobile, String verifyCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("adrVersion", Build.VERSION.SDK_INT);
        map.put("channelId", "SmartLoan");
        map.put("appVersion", BuildConfig.VERSION_NAME);
        map.put("androidId", DeviceUtils.getAndroidId(BaseApplication.getAppContext()));
        map.put("gaid", BaseCacheManager.getUserTemp().getGaid());
        map.put("imei", DeviceUtils.getIMEI(BaseApplication.getAppContext()));
        map.put("installReferce", BaseCacheManager.getUserTemp().getINSTALLREFERCE());
        map.put("mobile", mobile);
        map.put("packageName", BuildConfig.APPLICATION_ID);
        map.put("packageSource", "GooglePlay");
        map.put("utmSource", BaseCacheManager.getUserTemp().getUtmSource());
        map.put("verifyCode", verifyCode);
        map.put("verified", true);
        return RequestBody.create(null, new Gson().toJson(map));

    }

    public static RequestBody postEventLog() {
        Map<String, Object> map = new HashMap<>();
        map.put("packageName", BuildConfig.APPLICATION_ID);
        map.put("afId", "afId");
        map.put("gaid", BaseCacheManager.getUserTemp().getGaid());
        map.put("androidId", DeviceUtils.getAndroidId(BaseApplication.getAppContext()));
        map.put("imei", DeviceUtils.getIMEI(BaseApplication.getAppContext()));
        map.put("phoneNumber", BaseCacheManager.getUserTemp().getPhone());
        map.put("userId", BaseCacheManager.getUserTemp().getUserId());
        map.put("eventList", "Array[item]");
        map.put("channelID", "afId");
        map.put("merchantID", "000");// 默认（“000”）
        map.put("country", DeviceUtils.getDefaultCountry());
        map.put("utm_source", BaseCacheManager.getUserTemp().getINSTALLREFERCE());
        return RequestBody.create(null, new Gson().toJson(map));

    }
}
