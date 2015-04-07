package com.dilimanlabs.pitstop.ui.common;

import android.content.Context;

public interface BaseView {
    boolean onBackPressed();

    void onRegister();

    void onUnregister();

    Context getViewContext();
}
