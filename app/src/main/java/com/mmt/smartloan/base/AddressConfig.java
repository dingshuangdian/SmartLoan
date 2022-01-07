package com.mmt.smartloan.base;

import com.mmt.smartloan.utils.DebugUtils;

public class AddressConfig {
    //public static final String API_URL_PRODUCE = "http://alliance-test.dslbuy.com";
    public static final String API_URL_TEST = "http://alliance-test.dslbuy.com";
    public static final String API_URL_PRODUCE = "https://alliance.dslbuy.com";
    public static final String API_URL_SHUYI = "http://10.0.9.102:8097";

    public static final String WEB_URL_TEST = "";
    public static final String WEB_URL_PRODUCE = "";


    //web页面
    public static String WEB_URL = DebugUtils.isDebug() ? WEB_URL_TEST : WEB_URL_PRODUCE;
    //java接口
    public static String API_URL = DebugUtils.isDebug() ? API_URL_TEST : API_URL_PRODUCE;
}
