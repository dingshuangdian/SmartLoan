package com.mmt.smartloan.cache;


import com.mmt.smartloan.base.AddressConfig;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class BaseSystemPreferences extends BaseDiskCache {
    public BaseSystemPreferences() {
        super(BaseSystemPreferences.class.getSimpleName(), 1);
    }

    //连接服务器
    public static final String KEY_ADDRESS_URL_WEB = "KEY_ADDRESS_URL_WEB";//web地址
    public static final String KEY_ADDRESS_URL_API = "KEY_ADDRESS_URL_API";//api地址

    /**
     * 设置地址
     *
     * @param webUrl 网页地址
     * @param apiUrl Api地址
     */
    public void setAddressUrl(String webUrl, String apiUrl) {
        putSync(KEY_ADDRESS_URL_WEB, webUrl);
        putSync(KEY_ADDRESS_URL_API, apiUrl);
    }

    public String getAddressUrlWeb() {
        return getString(KEY_ADDRESS_URL_WEB, AddressConfig.WEB_URL_PRODUCE);
    }

    public String getAddressUrlApi() {
        return getString(KEY_ADDRESS_URL_API, AddressConfig.API_URL_PRODUCE);
    }

    public void setAddressUrlWeb(String url) {
        put(KEY_ADDRESS_URL_WEB, url);
    }

    public void setAddressUrlApi(String url) {
        put(KEY_ADDRESS_URL_API, url);
    }

    /**
     * 加载地址
     */
    public void loadAddressUrl() {
        AddressConfig.WEB_URL = getAddressUrlWeb();
        AddressConfig.API_URL = getAddressUrlApi();

    }
}
