package com.mmt.smartloan.rxjava.network;

import com.mmt.smartloan.utils.UserInfoUtils;

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
        if (!(request.body() instanceof MultipartBody)) {  //不是Multipart的时候才添加token
            Request.Builder requestBuilder = request.newBuilder();
            requestBuilder.addHeader("Authorization", "Bearer" + UserInfoUtils.getToken());
            requestBuilder.addHeader("Content-Type", "application/json; charset=UTF-8");
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
