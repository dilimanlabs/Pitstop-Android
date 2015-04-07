package com.dilimanlabs.pitstop;

import android.app.Application;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.crashlytics.android.Crashlytics;
import com.dilimanlabs.pitstop.okhttp.CacheHitInterceptor;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.path.android.jobqueue.BaseJob;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.config.Configuration;
import com.path.android.jobqueue.di.DependencyInjector;
import com.path.android.jobqueue.log.CustomLogger;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import dagger.ObjectGraph;
import retrofit.ErrorHandler;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.OkClient;
import retrofit.client.Response;

public class Pitstop extends Application{
    private ObjectGraph objectGraph;
    private PitstopService pitstopService;
    private JobManager jobManager;

    @Override
    public void onCreate() {
        super.onCreate();
        Crashlytics.start(this);

        objectGraph = ObjectGraph.create(getModules().toArray());
        objectGraph.inject(this);

        ActiveAndroid.initialize(this);

        configureStetho();

        configurePitstopService();

        configureJobManager();

        //EventBus.getDefault().registerSticky(this);
    }

    private List<Object> getModules(){
        return Arrays.<Object>asList(new PitstopModule(this));
    }

    public ObjectGraph createScopedGraph(Object... modules){
        return objectGraph.plus(modules);
    }

    private void configureStetho() {
        Stetho.initialize(
                Stetho.newInitializerBuilder(this)
                        .enableDumpapp(Stetho.defaultDumperPluginsProvider(this))
                        .enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this))
                        .build());
    }

    private void configurePitstopService() {
        OkHttpClient okHttpClient  = new OkHttpClient();

        okHttpClient.interceptors().add(new CacheHitInterceptor());
        okHttpClient.networkInterceptors().add(new StethoInterceptor());

        try {
            Cache responseCache = new Cache(getCacheDir(), 10 * 1024 * 1024);
            okHttpClient.setCache(responseCache);
        } catch (IOException e) {

        }

        pitstopService = new RestAdapter.Builder()
                .setErrorHandler(new ErrorHandler() {
                    @Override
                    public Throwable handleError(RetrofitError cause) {
                        Response r = cause.getResponse();

                        /*if (r != null && r.getStatus() == 401) {
                            getJobManager().stop();
                            getJobManager().clear();

                            ActiveAndroid.beginTransaction();
                            new Delete().from(Contact.class).execute();
                            new Delete().from(Location.class).execute();
                            new Delete().from(Establishment.class).execute();
                            new Delete().from(Position.class).execute();

                            final Account account = Account.getAccount();
                            if (account != null) {
                                account.authToken = null;
                                account.save();
                            }

                            ActiveAndroid.setTransactionSuccessful();
                            ActiveAndroid.endTransaction();

                            getJobManager().start();

                            final Intent intent = new Intent(Pitstop.this, StartActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                        }*/

                        return cause;
                    }
                })
                .setEndpoint(PitstopService.API_URL)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setClient(new OkClient(okHttpClient))
                .build()
                .create(PitstopService.class);
    }

    private void configureJobManager() {
        Configuration configuration = new Configuration.Builder(this)
                .injector(new DependencyInjector() {
                    @Override
                    public void inject(BaseJob baseJob) {
                        objectGraph.inject(baseJob);
                    }
                })
                .customLogger(new CustomLogger() {
                    private static final String TAG = "JOBS";
                    @Override
                    public boolean isDebugEnabled() {
                        return true;
                    }

                    @Override
                    public void d(String text, Object... args) {
                        Log.d(TAG, String.format(text, args));
                    }

                    @Override
                    public void e(Throwable t, String text, Object... args) {
                        Log.e(TAG, String.format(text, args), t);
                    }

                    @Override
                    public void e(String text, Object... args) {
                        Log.e(TAG, String.format(text, args));
                    }
                })
                .minConsumerCount(1)//always keep at least one consumer alive
                .maxConsumerCount(3)//up to 3 consumers at a time
                .loadFactor(3)//3 jobs per consumer
                .consumerKeepAlive(120)//wait 2 minute
                .build();
        jobManager = new JobManager(this, configuration);
    }

    public PitstopService getPitstopService() {
        return pitstopService;
    }

    public JobManager getJobManager() {
        return jobManager;
    }

}