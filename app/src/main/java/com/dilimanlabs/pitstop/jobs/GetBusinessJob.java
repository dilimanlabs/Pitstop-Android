package com.dilimanlabs.pitstop.jobs;

import com.dilimanlabs.pitstop.events.GetBusinessEvent;
import com.dilimanlabs.pitstop.okhttp.Headers;
import com.dilimanlabs.pitstop.persistence.Business;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class GetBusinessJob extends Job {
    @Inject
    PitstopService mPitstopService;

    private final String mBusinessUrl;

    public GetBusinessJob(String businessUrl) {
        super(new Params(JobPriority.HIGH));

        mBusinessUrl = businessUrl.substring(1);
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mPitstopService.getBusiness(mBusinessUrl, new Callback<PitstopService.ResponseWrapper<PitstopService.BusinessResponse>>() {
            @Override
            public void success(PitstopService.ResponseWrapper<PitstopService.BusinessResponse> responseWrapper, Response response) {
                if (response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))
                        || !response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))) {
                    Business business = responseWrapper.response.business;

                    business.save();

                    EventBus.getDefault().post(new GetBusinessEvent(business));
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
