package com.mmt.smartloan.base;

import android.os.Build;

import com.mmt.smartloan.BuildConfig;
import com.mmt.smartloan.utils.device.DeviceUtils;
import com.mmt.smartloan.utils.device.JSDCardUtils;

import java.util.HashMap;
import java.util.Map;

public class BaseParameter {
    public static Map<String, Object> addActive(String gaid, String installReferce, String installReferceClickTime, String installStartTime) {
        Map<String, Object> map = new HashMap<>();
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
        map.put("ipAddress", DeviceUtils.getOutNetIP(BaseApplication.getAppContext(), 0));
        map.put("localeIso3Country", DeviceUtils.getDefaultIsoCountry());
        map.put("localeIso3Language", DeviceUtils.getDefaultIsoLanguage());
        map.put("timeZone", DeviceUtils.getTimeZone());
        map.put("sensorList", DeviceUtils.getSensorList(BaseApplication.getAppContext()));
        map.put("networkOperatorName", "");
        map.put("isUsingProxyPort", DeviceUtils.isUsingVPN());
        map.put("channelId", "SmartLoan");
        map.put("afId", "afId");
        map.put("imagesExternal", JSDCardUtils.getImagesExternalCount(BaseApplication.getAppContext()));
        return map;
    }
}
