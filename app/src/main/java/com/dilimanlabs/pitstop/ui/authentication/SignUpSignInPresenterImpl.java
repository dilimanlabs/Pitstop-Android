package com.dilimanlabs.pitstop.ui.authentication;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;

import com.dilimanlabs.pitstop.events.SignUpSignInEvent;
import com.dilimanlabs.pitstop.jobs.SignInSignUpJob;
import com.dilimanlabs.pitstop.persistence.Account;
import com.dilimanlabs.pitstop.ui.MainActivity;
import com.path.android.jobqueue.JobManager;

import de.greenrobot.event.EventBus;

public class SignUpSignInPresenterImpl implements SignUpSignInPresenter {
    private enum ViewMode {
        NONE, SIGNIN, SIGNUP
    }

    private SignUpSignInView mView;
    private JobManager mJobManager;

    private ViewMode mCurrentViewMode = ViewMode.NONE;

    public SignUpSignInPresenterImpl(SignUpSignInView signUpSignInView, JobManager jobManager) {
        mView = signUpSignInView;
        mJobManager = jobManager;

        mCurrentViewMode = ViewMode.SIGNIN;
        mView.signInMode();

        final Account account = Account.getAccount();
        if (account != null) {
            if (!TextUtils.isEmpty(account.id) && !TextUtils.isEmpty(account.password)) {
                mView.setID(account.id);
                mView.setPassword(account.password);
            } else if (!TextUtils.isEmpty(account.id)) {
                mView.setID(account.id);
            }
        }
    }

    @Override
    public boolean onBackPressed() {
        if (mCurrentViewMode == ViewMode.SIGNUP) {
            mCurrentViewMode = ViewMode.SIGNIN;
            mView.signInMode();

            return true;
        }

        return false;
    }

    @Override
    public void onRegister() {
        EventBus.getDefault().register(this);
        check();
    }

    @Override
    public void onUnregister() {
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onToggleClick() {
        switch (mCurrentViewMode) {
            case SIGNIN: {
                mCurrentViewMode = ViewMode.SIGNUP;
                mView.signUpMode();

                break;
            } case SIGNUP: {
                mCurrentViewMode = ViewMode.SIGNIN;
                mView.signInMode();

                break;
            }
        }
    }

    @Override
    public void onSubmitClick() {
        if (EventBus.getDefault().getStickyEvent(SignUpSignInEvent.class) == null) {
            switch (mCurrentViewMode) {
                case SIGNIN: {
                    SignInSignUpJob.tryAdd(mJobManager, mView.getID(), mView.getPassword(), null);

                    break;
                } case SIGNUP: {
                    SignInSignUpJob.tryAdd(mJobManager, mView.getID(), mView.getPassword(), mView.getName());

                    break;
                }
            }
        }
    }

    private boolean check() {
        final Account account = Account.getAccount();
        if (account != null) {
            if (!TextUtils.isEmpty(account.authToken)) {
                final Intent intent = new Intent(mView.getViewContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);// | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                mView.getViewContext().startActivity(intent);
                ((ActionBarActivity) mView.getViewContext()).finish();

                return true;
            }
        }

        return false;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(SignUpSignInEvent event) {
        switch (event.event) {
            case START: {
                mView.lock();

                break;
            } case END: {
                // For cases when event was not handled and view recreated.
                mView.lock();

                if (!check()) {
                    mView.unlock();
                }

                break;
            }
        }
    }
}
