package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.ui.authentication.SignUpSignInViewImpl;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class StartActivity extends ActionBarActivity {

    @InjectView(R.id.signupsignin)
    SignUpSignInViewImpl mSignUpSignInImpl;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_authenticator);
        ButterKnife.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mSignUpSignInImpl.onRegister();
    }

    @Override
    protected void onPause() {
        mSignUpSignInImpl.onUnregister();
        super.onPause();
    }
}
