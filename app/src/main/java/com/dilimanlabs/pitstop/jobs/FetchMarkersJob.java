package com.dilimanlabs.pitstop.jobs;

import android.util.Log;

import com.dilimanlabs.androidlibrary.MapUtils;
import com.dilimanlabs.pitstop.events.FetchMarkersEndEvent;
import com.dilimanlabs.pitstop.okhttp.Headers;
import com.dilimanlabs.pitstop.persistence.Contact;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Location;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.google.android.gms.maps.model.LatLngBounds;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class FetchMarkersJob extends Job {
    // Injected via JobManager
    @Inject
    PitstopService mPitstopService;

    private static final AtomicInteger jobCounter = new AtomicInteger(0);

    private final int mJobID;
    private final LatLngBounds mBounds;
    private final String mAuthToken;

    public FetchMarkersJob(LatLngBounds bounds, String authToken) {
        super(new Params(JobPriority.HIGH).groupBy(JobGroup.FETCH_MARKERS).delayInMs(2000));

        mJobID = jobCounter.incrementAndGet();
        mBounds = bounds;
        mAuthToken = authToken;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        if(mJobID != jobCounter.get()) {
            //looks like other fetch jobs has been added after me. no reason to keep fetching
            //many times, cancel me, let the other one fetch markers.
            Log.d("JOBS","job canceled");
            return;
        }

        for (String tile : MapUtils.getTileNames(mBounds)) {
            mPitstopService.listEstablishmentsFromTile(tile, new Callback<PitstopService.ResponseWrapper<PitstopService.MarkersResponse>>() {
                @Override
                public void success(PitstopService.ResponseWrapper<PitstopService.MarkersResponse> responseWrapper, Response response) {
                    if(response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))
                            || !response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))) {
                        List<Establishment> establishments = responseWrapper.response.establishments;
                        for (Establishment est : establishments) {
                            Location location = est.location;
                            location.save();
                            Contact contact = est.contact;
                            contact.save();

                            new Establishment(est.categories, contact, location, est.name, est.primaryImage, est.url).save();
                        }

                        EventBus.getDefault().post(new FetchMarkersEndEvent());
                    }
                }

                @Override
                public void failure(RetrofitError error) {

                }
            });
        }
    }

    @Override
    protected void onCancel() {

    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        if(throwable instanceof RetrofitError) {
            Response r = ((RetrofitError)throwable).getResponse();
            if (r != null && r.getStatus() == 401) {
                return false;
            }
        }

        return true;
    }

    @Override
    protected int getRetryLimit() {
        return 3;
    }
}