package com.mmt.smartloan.rxjava.network;
import com.mmt.smartloan.base.AddressConfig;
import com.mmt.smartloan.rxjava.fastjson.FastJsonConverterFactory;
import com.mmt.smartloan.utils.LogUtils;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class NetworkFactory {
    private static Retrofit dslRetrofit = initRetrofit();

    private static Retrofit initRetrofit() {
        try {
            return new Retrofit.Builder()
                    .baseUrl(AddressConfig.API_URL)
                    .addConverterFactory(FastJsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .client(getOkHttpClient())
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e.toString());
        }
        return null;
    }

    private static OkHttpClient getOkHttpClient() {
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .sslSocketFactory(SSLSocketFactoryUtils.createSSLSocketFactory(), SSLSocketFactoryUtils.createTrustAllManager())
                .addInterceptor(new MyInterceptor())
                .addInterceptor(new LoggingInterceptor());
        return builder.build();
    }

    /**
     * 获取接口
     *
     * @param clazz
     * @param <T>
     * @return
     */
    public static <T> T getInterface(Class<T> clazz) {
        T service = null;
        try {
            service = dslRetrofit.create(clazz);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return service;
    }
}
