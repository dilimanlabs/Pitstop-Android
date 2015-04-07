package com.dilimanlabs.pitstop.jobs;

import com.dilimanlabs.pitstop.events.GetPagesEvent;
import com.dilimanlabs.pitstop.okhttp.Headers;
import com.dilimanlabs.pitstop.persistence.Page;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.List;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class GetPagesJob extends Job {
    @Inject
    PitstopService mPitstopService;

    private final String mBusinessUrl;

    public GetPagesJob(String businessUrl) {
        super(new Params(JobPriority.HIGH));

        mBusinessUrl = businessUrl.substring(1);
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mPitstopService.getPages(mBusinessUrl, new Callback<PitstopService.ResponseWrapper<PitstopService.PagesResponse>>() {
            @Override
            public void success(PitstopService.ResponseWrapper<PitstopService.PagesResponse> responseWrapper, Response response) {
                if (response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))
                        || !response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))) {
                    List<Page> pages = responseWrapper.response.pages;

                    EventBus.getDefault().post(new GetPagesEvent("/" + mBusinessUrl, pages));
                }
            }

            @Override
            public void failure(RetrofitError error) {

            }
        });
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return true;
    }

    @Override
    protected int getRetryLimit() {
        return 3;
    }
}
