package com.dilimanlabs.pitstop.ui.authentication;

import android.animation.LayoutTransition;
import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.devspark.robototextview.widget.RobotoButton;
import com.dilimanlabs.pitstop.Pitstop;
import com.dilimanlabs.pitstop.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

public class SignUpSignInViewImpl extends LinearLayout implements SignUpSignInView {
    @Inject
    SignUpSignInPresenter mPresenter;

    @InjectView(R.id.id)
    MaterialEditText mID;

    @InjectView(R.id.password)
    MaterialEditText mPassword;

    @InjectView(R.id.verifyPassword)
    MaterialEditText mVerifyPassword;

    @InjectView(R.id.name)
    MaterialEditText mName;

    @InjectView(R.id.submit)
    RobotoButton mSubmit;

    @InjectView(R.id.switchmode)
    RobotoButton mSwitchMode;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.switchmode)
    void onSwitchModeClick(View view) {
        mPresenter.onToggleClick();
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.submit)
    void onSubmitClick(View view) {
        mPresenter.onSubmitClick();
    }

    public SignUpSignInViewImpl(Context context) {
        this(context, null);
    }

    public SignUpSignInViewImpl(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SignUpSignInViewImpl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);
        setLayoutTransition(new LayoutTransition());

        inflate(getContext(), R.layout.view_signupsignin, this);
        ButterKnife.inject(this);

        ((Pitstop) ((ActionBarActivity) getContext())
                .getApplication())
                .createScopedGraph(getModules().toArray())
                .inject(this);
    }

    @Override
    public boolean onBackPressed() {
        return mPresenter.onBackPressed();
    }

    @Override
    public void onRegister() {
        mPresenter.onRegister();
    }

    @Override
    public void onUnregister() {
        mPresenter.onUnregister();
    }

    @Override
    public Context getViewContext() {
        return getContext();
    }

    @Override
    public void signInMode() {
        mVerifyPassword.setVisibility(View.INVISIBLE);
        mName.setVisibility(View.INVISIBLE);

        mSubmit.setText("Sign In");
        mSwitchMode.setText("Sign Up for Pitstop");
    }

    @Override
    public void signUpMode() {
        mVerifyPassword.setText("");
        mVerifyPassword.setVisibility(View.VISIBLE);
        mName.setText("");
        mName.setVisibility(View.VISIBLE);

        mSubmit.setText("Create Account");
        mSwitchMode.setText("Sign In");
    }

    @Override
    public String getID() {
        return mID.getText().toString();
    }

    @Override
    public void setID(String id) {
        mID.setText(id);
    }

    @Override
    public String getPassword() {
        return mPassword.getText().toString();
    }

    @Override
    public void setPassword(String password) {
        mPassword.setText(password);
    }

    @Override
    public String getVerifyPassword() {
        return mVerifyPassword.getText().toString();
    }

    @Override
    public String getName() {
        return mName.getText().toString();
    }

    private List<Object> getModules() {
        return Arrays.<Object>asList(new SignUpSignInModule(this));
    }

    @Override
    public void lock() {
        mSubmit.setEnabled(false);
        mSubmit.setTextColor(getResources().getColor(R.color.black_disabled_or_hint_text));
        mSubmit.setBackgroundColor(0);

        mSwitchMode.setEnabled(false);
        mSwitchMode.setTextColor(getResources().getColor(R.color.black_disabled_or_hint_text));
    }

    @Override
    public void unlock() {
        mSubmit.setEnabled(true);
        mSubmit.setTextColor(getResources().getColor(R.color.white_text));
        mSubmit.setBackgroundColor(getResources().getColor(R.color.light_blue_500));

        mSwitchMode.setEnabled(true);
        mSwitchMode.setTextColor(getResources().getColor(R.color.light_blue_200));
    }
}