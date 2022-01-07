package com.mmt.smartloan.rxjava.network;

import com.dsl.league.LeagueApplication;
import com.dsl.league.utils.AndroidUtils;
import com.dsl.league.utils.DslUserInfoUtils;

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
//            FormBody.Builder builder = new FormBody.Builder();
//            builder.add("app_token", DslUserInfoUtils.getToken());
//            Request.Builder requestBuilder = request.newBuilder();
//            RequestBody requestBody = builder.build();
//            String postBodyString = bodyToString(request.body());
//            postBodyString += ((postBodyString.length() > 0) ? "&" : "") + bodyToString(requestBody);
//            requestBuilder.addHeader("Content-Type", "application/json; charset=UTF-8");
//            requestBuilder.post(RequestBody.create(MediaType.parse("application/x-www-form-urlencoded;charset=UTF-8"), postBodyString));
//            request = requestBuilder.build();

            Request.Builder requestBuilder = request.newBuilder();
            HttpUrl url = request.url().newBuilder()
                    .setQueryParameter("app_token", DslUserInfoUtils.getToken())
                    .setQueryParameter("devicesystem", "Android")
                    .setQueryParameter("appversion", AndroidUtils.getVersionName(LeagueApplication.getConText()))
                    .build();
            requestBuilder.addHeader("Content-Type", "application/json; charset=UTF-8");
            request = requestBuilder.url(url).build();
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
