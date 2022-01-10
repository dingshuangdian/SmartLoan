package com.mmt.smartloan.rxjava.network;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.text.MessageFormat;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.internal.http.HttpHeaders;
import okio.Buffer;
import okio.BufferedSource;

/**
 * <p>版权©️所有：大参林医药集团<p>
 * <p>作者：dingshuangdian<p>
 * <p>创建时间：2020/6/4<p>
 */
public class LoggingInterceptor implements Interceptor {
    private OnLoggingInterface onLoggingInterface;

    public LoggingInterceptor() {
    }

    public LoggingInterceptor(OnLoggingInterface onLoggingInterface) {
        this.onLoggingInterface = onLoggingInterface;
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        Request request = chain.request();

        Response response = chain.proceed(request);
        String requestStr = requestBodyToString(request);
        String responseStr = responseBodyToString(response);

        String logMsg = MessageFormat.format("{0}\n{1}\nrequest={2}\nresponse={3}", request.url(), request.headers().toString(), requestStr, responseStr);
        //String logMsg = MessageFormat.format("request={0}\n{1}\nresponse={2}", request.url(), request.headers().toString(), responseStr);
        LogManager.api(logMsg);
        if (onLoggingInterface != null) {
            onLoggingInterface.onRequest(request, response, responseStr);
        }
        return response;
    }

    /**
     * Request请求内容转String
     */
    public static String requestBodyToString(final Request request) {
        try {
            final Buffer buffer = new Buffer();
            final Request newRequest = request.newBuilder().build();
            RequestBody requestBody = newRequest.body();
            if (requestBody != null) {
                requestBody.writeTo(buffer);
            }
            return buffer.readUtf8();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    /**
     * Response内容转String
     */
    public static String responseBodyToString(final Response response) {
        try {
            ResponseBody responseBody = response.body();
            long contentLength = 0;
            if (responseBody != null) {
                contentLength = responseBody.contentLength();
            }

            if (HttpHeaders.hasBody(response)) {
                if (responseBody != null) {
                    BufferedSource source = responseBody.source();
                    source.request(Long.MAX_VALUE); // Buffer the entire body.
                    Buffer buffer = source.buffer();
                    if (contentLength != 0) {
                        return buffer.clone().readUtf8();
                    }
                }
            }
            return "";
        } catch (Exception e) {
            return e.getMessage();
        }
    }

    public interface OnLoggingInterface {
        void onRequest(Request request, Response response, String responseStr);
    }
}
