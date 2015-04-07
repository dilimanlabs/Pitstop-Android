package com.dilimanlabs.pitstop.retrofit;

import retrofit.ErrorHandler;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PitstopErrorHandler implements ErrorHandler {
    @Override public Throwable handleError(RetrofitError cause) {
        Response r = cause.getResponse();
        if (r != null && r.getStatus() == 401) {
            // TODO Do something
        }
        return cause;
    }
}
