package com.dilimanlabs.pitstop.jobs;

import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.Params;

import java.io.File;

import javax.inject.Inject;

import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class UploadPhotoJob extends Job {
    // Injected via JobManager
    @Inject
    PitstopService pitstopService;

    private String accountId, authToken;
    private TypedFile image;
    private String title;

    public UploadPhotoJob(String accountId, String authToken, File image, String title) {
        super(new Params(JobPriority.HIGH).requireNetwork().groupBy(JobGroup.FETCH_MARKERS));
        this.accountId = accountId;
        this.authToken = authToken;
        this.image = new TypedFile("image/jpg", image);
        this.title = title;
    }

    @Override
    public void onAdded() {

    }

    @Override
    public void onRun() throws Throwable {
        pitstopService.uploadImage(authToken, accountId, image, title);
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
}
