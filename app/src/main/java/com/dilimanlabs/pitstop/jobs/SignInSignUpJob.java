package com.dilimanlabs.pitstop.jobs;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.dilimanlabs.pitstop.events.BaseEvent.Event;
import com.dilimanlabs.pitstop.events.SignUpSignInEvent;
import com.dilimanlabs.pitstop.events.SignUpSignInEvent.Type;
import com.dilimanlabs.pitstop.persistence.Account;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.path.android.jobqueue.Job;
import com.path.android.jobqueue.JobManager;
import com.path.android.jobqueue.Params;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.inject.Inject;

import de.greenrobot.event.EventBus;

public class SignInSignUpJob extends Job {
    // Injected via JobManager
    @Inject
    PitstopService mPitstopService;

    private static final AtomicBoolean jobLock = new AtomicBoolean();

    private final String mId;
    private final String mPassword;
    private final String mName;

    public static void tryAdd(JobManager jobManager, String id, String password, String name) {
        if (jobLock.compareAndSet(false, true)) {
            final long jobId = jobManager.addJob(new SignInSignUpJob(id, password, name));
        }
    }

    private SignInSignUpJob(String id, String password, String name) {
        super(new Params(JobPriority.HIGH).groupBy(JobGroup.AUTHENTICATION));

        mId = id;
        mPassword = password;
        mName = name;
    }

    @Override
    public void onAdded() {
        if (mName == null) {
            EventBus.getDefault().post(new SignUpSignInEvent(Type.SIGNIN, Event.START));
        } else {
            EventBus.getDefault().post(new SignUpSignInEvent(Type.SIGNUP, Event.START));
        }
    }

    @Override
    public void onRun() throws Throwable {
        if (mName == null) {
            PitstopService.ResponseWrapper<PitstopService.AccountSigninResponse> accountSigninResponseWrapper = mPitstopService.accountSignin(
                    new PitstopService.AccountWrapper(mId, mPassword, null)
            );
            final String authToken = accountSigninResponseWrapper.response.authToken;

            if (authToken != null) {
                ActiveAndroid.beginTransaction();
                new Delete().from(Account.class).execute();
                new Account(mId, mPassword, authToken).save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
            }

            EventBus.getDefault().post(new SignUpSignInEvent(Type.SIGNIN, Event.END));
        } else {
            PitstopService.ResponseWrapper<PitstopService.AccountSignupResponse> accountSignupResponseWrapper = mPitstopService.accountSignup(
                    new PitstopService.AccountWrapper(mId, mPassword, mName)
            );
            final String authToken = accountSignupResponseWrapper.response.authToken;

            if (authToken != null) {
                ActiveAndroid.beginTransaction();
                new Delete().from(Account.class).execute();
                new Account(mId, mPassword, authToken).save();
                ActiveAndroid.setTransactionSuccessful();
                ActiveAndroid.endTransaction();
            }

            EventBus.getDefault().post(new SignUpSignInEvent(Type.SIGNUP, Event.END));
        }
        jobLock.set(false);
    }

    @Override
    protected void onCancel() {
        if (mName == null) {
            EventBus.getDefault().post(new SignUpSignInEvent(Type.SIGNIN, Event.END));
        } else {
            EventBus.getDefault().post(new SignUpSignInEvent(Type.SIGNUP, Event.END));
        }
        jobLock.set(false);
    }

    @Override
    protected boolean shouldReRunOnThrowable(Throwable throwable) {
        return false;
    }
}