package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.afollestad.materialdialogs.MaterialDialog;
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

public class DetailsActivity extends ActionBarActivity {

    //@Inject
    //JobManager mJobManager;

    @InjectView(R.id.toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.tabs)
    public PagerSlidingTabStrip mTabs;

    @InjectView(R.id.viewpager)
    public ViewPager mViewPager;

    private Establishment mEst;
    private Business mBiz;
    private List<Page> mPages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details);
        ButterKnife.inject(this);

        final Bundle data = getIntent().getBundleExtra("DATA");
        mEst = Establishment.getEstablishmentByUrl(data.getString("EST_URL"));
        applyEst();

        final String estUrl = getIntent().getBundleExtra("DATA").getString("EST_URL");
        final String bizUrl = estUrl.substring(0, "/businesses/12345678910".length());
        mBiz = Business.getBusinessByUrl(bizUrl);
        applyBiz();

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

        ((Pitstop) getApplication()).getJobManager().addJob(new GetEstablishmentJob(estUrl));
        ((Pitstop) getApplication()).getJobManager().addJob(new GetBusinessJob(bizUrl));
        ((Pitstop) getApplication()).getJobManager().addJob(new GetPagesJob(bizUrl));
    }

    @Override
    protected void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
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
                    final MaterialDialog dialog = new MaterialDialog.Builder(this)
                            .title(mEst.name)
                            .titleColorRes(R.color.light_blue_500)
                            .customView(R.layout.dialog_est, true).build();

                    final View customView = dialog.getCustomView();
                    ((TextView) customView.findViewById(R.id.address)).setText(mEst.location.address);
                    ((TextView) customView.findViewById(R.id.cityOrMunicipality)).setText(mEst.location.cityOrMunicipality);
                    ((TextView) customView.findViewById(R.id.country)).setText(mEst.location.country);
                    ((TextView) customView.findViewById(R.id.cc)).setText(mEst.location.cc);

                    dialog.show();
                }

                return true;
            } default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onPause() {
        EventBus.getDefault().unregister(this);

        super.onPause();
    }

    private void applyEst() {
        if (mEst != null) {
            //mHeaderTitle.setText(mEst.name);
            //mAddress.setText(mEst.location.address);

            if (!"".equals(mEst.primaryImage)) {
                final String imageUrl =
                        "http://pitstop.dilimanlabs.com/api"
                                + mEst.primaryImage
                                + ".png";
                //+ "?"
                //+ "height=" + (int) getResources().getDimension(R.dimen.dp40)
                //+ "&"
                //+ "width=" + (int) getResources().getDimension(R.dimen.dp40);

                final Picasso picasso = Picasso.with(this);
                //picasso.load(imageUrl).fit().centerCrop().into(mBanner);
            }
        } else {
            finish();
        }
    }

    private void applyBiz() {
        if (mBiz != null) {
            //mToolbar.setSubtitle(mBiz.name);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GetEstablishmentEvent event) {
        if (event.establishment.url.equals(getIntent().getBundleExtra("DATA").getString("EST_URL"))) {
            mEst = event.establishment;
            applyEst();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GetBusinessEvent event) {
        final String estUrl = getIntent().getBundleExtra("DATA").getString("EST_URL");
        final String bizUrl = estUrl.substring(0, "/businesses/12345678910".length()); //TODO improve
        if (event.business.url.equals(bizUrl)) {
            mBiz = event.business;
            applyBiz();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(GetPagesEvent event) {
        mTabs.setTextColorResource(R.color.white_text);
        mViewPager.setAdapter(new PageFragmentPagerAdapter(getSupportFragmentManager(), event.pages));
        mViewPager.setOffscreenPageLimit(5);
        mTabs.setViewPager(mViewPager);
    }
}
