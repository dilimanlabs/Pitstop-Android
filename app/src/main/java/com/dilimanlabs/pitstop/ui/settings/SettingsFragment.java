package com.dilimanlabs.pitstop.ui.settings;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.ui.common.BaseFragment;

import java.util.List;

public class SettingsFragment extends BaseFragment {

    public static String TAG = "Settings";

    public static Fragment newInstance() {
        final Fragment fragment = new SettingsFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        //ButterKnife.inject(this, rootView);

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public List<Object> getModules() {
        return null;
    }

    @Override
    public boolean onBackPressed() {
        return false;
    }
}
