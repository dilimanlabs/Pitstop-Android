package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.dilimanlabs.pitstop.Pitstop;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.events.GetBusinessEvent;
import com.dilimanlabs.pitstop.events.GetEstablishmentEvent;
import com.dilimanlabs.pitstop.events.GetPagesEvent;
import com.dilimanlabs.pitstop.jobs.GetBusinessJob;
import com.dilimanlabs.pitstop.jobs.GetEstablishmentJob;
import com.dilimanlabs.pitstop.jobs.GetPagesJob;
import com.dilimanlabs.pitstop.persistence.Business;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.persistence.Page;
import com.dilimanlabs.pitstop.ui.widgets.PageFragmentPagerAdapter;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;

public class DetailsActivity extends AppCompatActivity {

    @InjectView(R.id.toolbar)
    public Toolbar mToolbar;

    private Establishment mEst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ButterKnife.inject(this);

        final Bundle data = getIntent().getBundleExtra("DATA");
        mEst = Establishment.getEstablishmentByUrl(data.getString("EST_URL"));
        if (mEst == null) {
            finish();
        }

        mToolbar.setTitle("");
        setSupportActionBar(mToolbar);
        // These need to be set after the call to setSupportActionBar()
        //mToolbar.setNavigationIcon(R.drawable.ic_stub_circle);
        //mToolbar.setLogo();
        //mToolbar.setTitle("");
        //mToolbar.setSubtitle("");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        if (getSupportFragmentManager().findFragmentByTag("DETAILS_FRAGMENT") == null) {
            final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.fragment_stub, DetailsFragment.newInstance(data.getString("EST_URL")), "DETAILS_FRAGMENT");
            ft.commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_details, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();

                return true;
            }
            case R.id.action_details: {
                if (mEst != null) {

                    final AlertDialog d = new AlertDialog.Builder(this, R.style.AppCompatAlertDialogStyle)
                            .setTitle(mEst.name)
                            .create();

                    final View li = getLayoutInflater().inflate(R.layout.dialog_est, null);
                    ((TextView) li.findViewById(R.id.address)).setText(mEst.location.address);
                    ((TextView) li.findViewById(R.id.cityOrMunicipality)).setText(mEst.location.cityOrMunicipality);
                    ((TextView) li.findViewById(R.id.country)).setText(mEst.location.country);
                    ((TextView) li.findViewById(R.id.cc)).setText(mEst.location.cc);

                    int dp20 = (int) getResources().getDimension(R.dimen.dp20);
                    int dp24 = (int) getResources().getDimension(R.dimen.dp24);
                    d.setView(li, dp24, dp20, dp24, dp24);
                    d.show();
                }

                return true;
            } default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }
}
