package com.dilimanlabs.pitstop.ui.common;

import android.support.v4.app.Fragment;

import java.util.List;

public abstract class BaseFragment extends Fragment {
    public abstract List<Object> getModules();

    public abstract boolean onBackPressed();
}
