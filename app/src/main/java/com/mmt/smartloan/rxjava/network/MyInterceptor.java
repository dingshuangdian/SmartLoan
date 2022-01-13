package com.mmt.smartloan.rxjava.network;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.mmt.smartloan.BuildConfig;
import com.mmt.smartloan.base.BaseApplication;
import com.mmt.smartloan.utils.LogUtils;
import com.mmt.smartloan.utils.UserInfoUtils;
import com.mmt.smartloan.utils.device.DeviceUtils;

import java.io.IOException;

import okhttp3.HttpUrl;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okio.Buffer;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class MyInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
        HttpUrl httpUrl = request.url();
        if (!httpUrl.url().getPath().contains("login")) {
            Request.Builder requestBuilder = request.newBuilder();
            if (!TextUtils.isEmpty(UserInfoUtils.getToken()) && !httpUrl.url().getPath().contains("register")) {
                requestBuilder.addHeader("Authorization", "Bearer" + UserInfoUtils.getToken());
            }
            requestBuilder.addHeader("Content-Type", "application/json; charset=UTF-8");
            requestBuilder.addHeader("packageName", BuildConfig.APPLICATION_ID);
            requestBuilder.addHeader("appName", DeviceUtils.getAppName(BaseApplication.getAppContext()));
            requestBuilder.addHeader("lang", "es");
            requestBuilder.addHeader("afid", "afid");
            request = requestBuilder.build();
        }
        return chain.proceed(request);
    }

    private String bodyToString(final RequestBody request) {
        try {
            final RequestBody copy = request;
            final Buffer buffer = new Buffer();
            if (copy != null)
                copy.writeTo(buffer);
            else
                return "";
            return buffer.readUtf8();
        } catch (final IOException e) {
            return "did not work";
        }
    }
}
