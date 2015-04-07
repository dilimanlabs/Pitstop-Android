package com.dilimanlabs.pitstop.ui.authentication;

import com.dilimanlabs.pitstop.PitstopModule;
import com.path.android.jobqueue.JobManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module(
        injects = SignUpSignInViewImpl.class,
        addsTo = PitstopModule.class,
        complete = false
)
public class SignUpSignInModule {
    private SignUpSignInView mSignUpSignInView;

    public SignUpSignInModule(SignUpSignInView signUpSignInView) {
        mSignUpSignInView = signUpSignInView;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Provides
    @Singleton
    public SignUpSignInView provideView() {
        return mSignUpSignInView;
    }

    @SuppressWarnings("UnusedDeclaration")
    @Singleton
    @Provides
    public SignUpSignInPresenter providePresenter(SignUpSignInView signUpSignInView, JobManager jobManager) {
        return new SignUpSignInPresenterImpl(signUpSignInView, jobManager);
    }
}
