package com.dilimanlabs.pitstop.ui.authentication;

import com.dilimanlabs.pitstop.ui.common.BaseView;

public interface SignUpSignInView extends BaseView {

    void signInMode();

    void signUpMode();

    String getID();

    void setID(String id);

    String getPassword();

    void setPassword(String password);

    String getVerifyPassword();

    String getName();

    void lock();

    void unlock();
}
