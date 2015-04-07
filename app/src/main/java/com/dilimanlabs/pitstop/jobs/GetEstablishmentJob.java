package com.dilimanlabs.pitstop.jobs;

import com.dilimanlabs.pitstop.events.GetEstablishmentEvent;
import com.dilimanlabs.pitstop.okhttp.Headers;
import com.dilimanlabs.pitstop.persistence.Contact;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Location;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class GetEstablishmentJob extends Job {
    @Inject
    PitstopService mPitstopService;

    private final String mEstablishmentUrl;

    public GetEstablishmentJob(String establishmentUrl) {
        super(new Params(JobPriority.HIGH));

        mEstablishmentUrl = establishmentUrl.substring(1);
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        mPitstopService.getEstablishment(mEstablishmentUrl, new Callback<PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse>>() {
            @Override
            public void success(PitstopService.ResponseWrapper<PitstopService.EstablishmentResponse> responseWrapper, Response response) {
                if(response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))
                        || !response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))) {
                    Establishment establishment = responseWrapper.response.establishment;
                    Location location = establishment.location;
                    location.save();
                    Contact contact = establishment.contact;
                    contact.save();

                    new Establishment(establishment.categories, contact, location, establishment.name, establishment.primaryImage, establishment.url).save();

                    EventBus.getDefault().post(new GetEstablishmentEvent(establishment));
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
