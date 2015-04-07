package com.dilimanlabs.pitstop.okhttp;

import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class CacheHitInterceptor implements Interceptor {

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();

        Response response = chain.proceed(request);

        if (response.cacheResponse() == null) {
            response = response.newBuilder().addHeader(Headers.IS_CACHED, "False").build();
        } else {
            response = response.newBuilder().addHeader(Headers.IS_CACHED, "True").build();
        }

        return response;
    }
}
