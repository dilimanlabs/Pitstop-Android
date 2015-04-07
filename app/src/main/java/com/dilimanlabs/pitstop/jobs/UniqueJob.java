package com.dilimanlabs.pitstop.jobs;

import android.util.Log;

import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.Params;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

public class UniqueJob extends Job {
    @Inject
    PitstopService pitstopService;

    private static final AtomicBoolean jobLock = new AtomicBoolean();

    public static void tryAdd(JobManager jobManager) {
        Log.d("jobLock is", String.valueOf(jobLock.get()));
        if (jobLock.compareAndSet(false, true)) {
            Log.d("jobLock is now", String.valueOf(jobLock.get()));
            jobManager.addJobInBackground(new UniqueJob());
        }
    }

    public UniqueJob() {
        super(new Params(JobPriority.HIGH).persist().groupBy(JobGroup.AUTHENTICATION));
    }


    @Override
    public void onAdded() {
    }

    @Override
    public void onRun() throws Throwable {
        int max = 10;
        for (int i=1; i<=max; i++) {
            Log.d("UniqueJob", i + "/" + "max");
            Thread.sleep(1000);
        }
        throw new Exception();
        //Log.d("UniqueJob", "end");
        //jobLock.set(false); //release the lock
    }

    @Override
    protected void onCancel() {
        Log.d("UniqueJob", "canceled");
        jobLock.set(false); //release the loc
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        Log.d("UniqueJob", "shouldReRunOnThrowable");
        return false;
    }
}
