package com.dilimanlabs.pitstop.ui.account;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.dilimanlabs.pitstop.Pitstop;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.persistence.Account;
import com.dilimanlabs.pitstop.persistence.Contact;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Location;
import com.dilimanlabs.pitstop.persistence.Position;
import com.dilimanlabs.pitstop.ui.StartActivity;
import com.dilimanlabs.pitstop.ui.common.BaseFragment;
import com.squareup.okhttp.Authenticator;

import java.util.List;

import butterknife.ButterKnife;

public class AccountFragment extends BaseFragment {

    public static String TAG = "Account";

    public static Fragment newInstance() {
        final Fragment fragment = new AccountFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_account, container, false);
        ButterKnife.inject(this, rootView);

        setHasOptionsMenu(true);

        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_account, menu);
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout: {
                /*new MaterialDialog.Builder(getActivity())
                        .title("Logout of Pitstop")
                        .content("Are you sure you want to leave?")
                        .positiveText("Logout")
                        .negativeText("Cancel")
                        .callback(new MaterialDialog.ButtonCallback() {
                            @Override
                            public void onPositive(MaterialDialog dialog) {
                                ((Pitstop) getActivity().getApplication()).getJobManager().stop();
                                ((Pitstop) getActivity().getApplication()).getJobManager().clear();

                                ActiveAndroid.beginTransaction();
                                new Delete().from(Contact.class).execute();
                                new Delete().from(Location.class).execute();
                                new Delete().from(Establishment.class).execute();
                                new Delete().from(Position.class).execute();

                                final Account account = Account.getAccount();
                                if (account != null) {
                                    account.password = null;
                                    account.authToken = null;
                                    account.save();
                                }

                                ActiveAndroid.setTransactionSuccessful();
                                ActiveAndroid.endTransaction();

                                ((Pitstop) getActivity().getApplication()).getJobManager().start();

                                final Intent intent = new Intent(getActivity(), StartActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                            }

                            @Override
                            public void onNegative(MaterialDialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .show();*/

                return true;
            } default: {

                return super.onOptionsItemSelected(item);
            }
        }
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
