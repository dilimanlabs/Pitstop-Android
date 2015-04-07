package com.dilimanlabs.pitstop;

import android.app.Application;
import android.content.Context;

import com.dilimanlabs.pitstop.jobs.FetchMarkersJob;
import com.dilimanlabs.pitstop.jobs.GetBusinessJob;
import com.dilimanlabs.pitstop.jobs.GetEstablishmentJob;
import com.dilimanlabs.pitstop.jobs.GetPagesJob;
import com.dilimanlabs.pitstop.jobs.SignInSignUpJob;
import com.dilimanlabs.pitstop.jobs.UniqueJob;
import com.dilimanlabs.pitstop.jobs.UploadPhotoJob;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.JobManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects={
                Pitstop.class,
                FetchMarkersJob.class,
                GetBusinessJob.class,
                GetEstablishmentJob.class,
                GetPagesJob.class,
                UploadPhotoJob.class,
                UniqueJob.class,
                SignInSignUpJob.class
        },
        library = true
)

public class PitstopModule {
    private final Pitstop pitstop;

    public PitstopModule(Pitstop pitstop){
        this.pitstop = pitstop;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Provides @Singleton
    public Application provideApplication(){
        return pitstop;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Provides @Singleton
    public Context provideContext(){
        return pitstop.getApplicationContext();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Provides @Singleton
    public PitstopService providePitstopService() {
        return pitstop.getPitstopService();
    }

    @SuppressWarnings("UnusedDeclaration")
    @Provides @Singleton
    public JobManager provideJobManager() {
        return pitstop.getJobManager();
    }
}
