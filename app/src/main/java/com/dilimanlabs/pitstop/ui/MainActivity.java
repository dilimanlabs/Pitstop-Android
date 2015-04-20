package com.dilimanlabs.pitstop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.ui.account.AccountFragment;
import com.dilimanlabs.pitstop.ui.common.BaseFragment;
import com.dilimanlabs.pitstop.ui.explore.ExploreFragment;
import com.dilimanlabs.pitstop.ui.settings.SettingsFragment;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnCheckedChanged;

public class MainActivity extends ActionBarActivity {

    @InjectView(R.id.drawer_layout)
    public DrawerLayout mDrawerLayout;

    @InjectView(R.id.drawer)
    public ScrollView mDrawer;

    @InjectView(R.id.nav)
    public RadioGroup mNav;

    @InjectView(R.id.content)
    public LinearLayout mContent;

    @InjectView(R.id.toolbar)
    public Toolbar mToolbar;

    private Fragment mAccountFragment, mExploreFragment, mSettingsFragment;

    @SuppressWarnings("UnusedDeclaration")
    @OnCheckedChanged({R.id.nav_account, R.id.nav_explore, R.id.nav_social, R.id.nav_settings})
    void onCheckChanged(CompoundButton view, boolean isChecked) {
        switch (view.getId()) {
            case R.id.nav_account: {
                if (mAccountFragment == null) {
                    mAccountFragment = getSupportFragmentManager().findFragmentByTag(AccountFragment.TAG);
                }

                if (isChecked) {
                    if (mAccountFragment == null) {
                        mAccountFragment = AccountFragment.newInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_frame, mAccountFragment, AccountFragment.TAG)
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(mAccountFragment)
                                .commit();
                    }

                    setTitle(AccountFragment.TAG);
                } else {
                    if (mAccountFragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mAccountFragment)
                                .commit();
                    }
                }
                break;
            }
            case R.id.nav_explore: {
                if (mExploreFragment == null) {
                    mExploreFragment = getSupportFragmentManager().findFragmentByTag(ExploreFragment.TAG);
                }

                if (isChecked) {
                    if (mExploreFragment == null) {
                        mExploreFragment = ExploreFragment.newInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_frame, mExploreFragment, ExploreFragment.TAG)
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(mExploreFragment)
                                .commit();
                    }

                    setTitle(ExploreFragment.TAG);
                } else {
                    if (mExploreFragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mExploreFragment)
                                .commit();
                    }
                }
                break;
            }
            case R.id.nav_social: {

                break;
            }
            case R.id.nav_settings: {
                if (mSettingsFragment == null) {
                    mSettingsFragment = getSupportFragmentManager().findFragmentByTag(SettingsFragment.TAG);
                }

                if (isChecked) {
                    if (mSettingsFragment == null) {
                        mSettingsFragment = SettingsFragment.newInstance();
                        getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.fragment_frame, mSettingsFragment, SettingsFragment.TAG)
                                .commit();
                    } else {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .show(mSettingsFragment)
                                .commit();
                    }

                    setTitle(SettingsFragment.TAG);
                } else {
                    if (mSettingsFragment != null) {
                        getSupportFragmentManager()
                                .beginTransaction()
                                .hide(mSettingsFragment)
                                .commit();
                    }
                }

                break;
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        setSupportActionBar(mToolbar);
        // These need to be set after the call to setSupportActionBar()
        mToolbar.setNavigationIcon(R.drawable.ic_pitstop);
        //mToolbar.setLogo();
        //mToolbar.setTitle();
        //mToolbar.setSubtitle();

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        mDrawerLayout.setDrawerListener(new StubDrawerListener(){
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                mContent.setTranslationX(mDrawer.getWidth() * slideOffset);
            }
        });

        if (savedInstanceState !=  null) {
            mAccountFragment = getSupportFragmentManager().getFragment(savedInstanceState, AccountFragment.TAG);
            if (mAccountFragment != null && savedInstanceState.getBoolean(AccountFragment.TAG + "isHidden", false)) {
                getSupportFragmentManager().beginTransaction().hide(mAccountFragment).commit();
            }

            mExploreFragment = getSupportFragmentManager().getFragment(savedInstanceState, ExploreFragment.TAG);
            if (mExploreFragment != null && savedInstanceState.getBoolean(ExploreFragment.TAG + "isHidden", false)) {
                getSupportFragmentManager().beginTransaction().hide(mExploreFragment).commit();
            }

            mSettingsFragment = getSupportFragmentManager().getFragment(savedInstanceState, SettingsFragment.TAG);
            if (mSettingsFragment != null && savedInstanceState.getBoolean(SettingsFragment.TAG + "isHidden", false)) {
                getSupportFragmentManager().beginTransaction().hide(mSettingsFragment).commit();
            }
        } else {
            mDrawerLayout.openDrawer(mDrawer);
            mNav.check(R.id.nav_explore);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mDrawerLayout.isDrawerOpen(mDrawer)) {
            mContent.setTranslationX(getResources().getDimension(R.dimen.dp72));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                mDrawerLayout.openDrawer(mDrawer);

                return true;
            } default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt("mNav", mNav.getCheckedRadioButtonId());

        if (mAccountFragment != null) {
            getSupportFragmentManager().putFragment(outState, AccountFragment.TAG, mAccountFragment);
            outState.putBoolean(AccountFragment.TAG + "isHidden", mAccountFragment.isHidden());
        }
        if (mExploreFragment != null) {
            getSupportFragmentManager().putFragment(outState, ExploreFragment.TAG, mExploreFragment);
            outState.putBoolean(ExploreFragment.TAG + "isHidden", mExploreFragment.isHidden());
        }
        if (mSettingsFragment != null) {
            getSupportFragmentManager().putFragment(outState, SettingsFragment.TAG, mSettingsFragment);
            outState.putBoolean(SettingsFragment.TAG + "isHidden", mSettingsFragment.isHidden());
        }

        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        mNav.check(savedInstanceState.getInt("mNav", -1));

        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onBackPressed() {
        boolean handled = false;

        switch (mNav.getCheckedRadioButtonId()) {
            case R.id.nav_account: {

                break;
            } case R.id.nav_explore: {
                BaseFragment fragment = (BaseFragment) getSupportFragmentManager().findFragmentByTag(ExploreFragment.TAG);
                if (fragment != null) {
                    handled = fragment.onBackPressed();
                }

                break;
            }case R.id.nav_settings: {

                break;
            }
        }

        if (!handled) {
            super.onBackPressed();
        }
    }
}
