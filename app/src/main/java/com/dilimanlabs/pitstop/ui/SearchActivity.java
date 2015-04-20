package com.dilimanlabs.pitstop.ui;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.activeandroid.util.Log;
import com.devspark.robototextview.widget.RobotoTextView;
import com.dilimanlabs.androidlibrary.NetworkUtils;
import com.dilimanlabs.pitstop.Pitstop;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.events.SearchEstablishmentsEvent;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.dilimanlabs.pitstop.ui.widgets.SearchResultsAdapter;
import com.dilimanlabs.pitstop.ui.widgets.SimpleDividerItemDecoration;
import com.pnikosis.materialishprogress.ProgressWheel;

import java.util.Collections;
import java.util.concurrent.TimeUnit;

import butterknife.ButterKnife;
import butterknife.InjectView;
import de.greenrobot.event.EventBus;
import retrofit.RetrofitError;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.android.view.ViewObservable;
import rx.android.widget.WidgetObservable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class SearchActivity extends ActionBarActivity {

    @InjectView(R.id.toolbar)
    public Toolbar mToolbar;

    @InjectView(R.id.search_view)
    public EditText mSearchView;

    @InjectView(R.id.recycler_view)
    public RecyclerView mRecyclerView;

    @InjectView(R.id.message)
    public RobotoTextView mMessage;

    @InjectView(R.id.progress_wheel)
    public ProgressWheel mProgressWheel;

    public String mQuery;
    public String mNextPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search);
        ButterKnife.inject(this);

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

        mSearchView.setHint("Search establishments");

        BehaviorSubject<String> asd = BehaviorSubject.create();

        BehaviorSubject<PitstopService.ResponseWrapper<PitstopService.EstablishmentsResponse>>
                dasd = BehaviorSubject.create();

        WidgetObservable.text(mSearchView)
                .doOnNext(e -> invalidateOptionsMenu())
                .map(e -> e.text().toString())
                .filter(s -> s.length() >= 3)
                .debounce(1000, TimeUnit.MILLISECONDS)
                .subscribe(s -> asd.onNext(s));

        asd.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(s -> {
                            mRecyclerView.setVisibility(View.GONE);
                            mMessage.setVisibility(View.GONE);
                            mProgressWheel.setVisibility(View.VISIBLE);

                            ((Pitstop) getApplication()).getPitstopService().searchEstablishments(s)
                                    .doOnError(err -> {
                                        mProgressWheel.setVisibility(View.GONE);
                                        mMessage.setText("Something went wrong");
                                        mMessage.setVisibility(View.VISIBLE);
                                    })
                                    .subscribe(rw -> dasd.onNext(rw));
                        }
                );

        dasd.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(rw -> {
                    mProgressWheel.setVisibility(View.GONE);
                    if (rw.response.establishments.size() == 0) {
                        mMessage.setText("No results");
                        mMessage.setVisibility(View.VISIBLE);
                    } else {
                        mRecyclerView.swapAdapter(new SearchResultsAdapter(this, rw.response.establishments), false);
                        mRecyclerView.setVisibility(View.VISIBLE);
                    }
                });

        mSearchView.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (!TextUtils.isEmpty(v.getText())) {
                    asd.onNext(v.getText().toString());
                }
                return true;
            }
            return false;
        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new SearchResultsAdapter(this, Collections.<Establishment>emptyList()));
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_search, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSearchView.getText().length() == 0) {
            menu.findItem(R.id.action_clear).setVisible(false);
        } else {
            menu.findItem(R.id.action_clear).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home: {
                finish();

                return true;
            }
            case R.id.action_clear: {
                mSearchView.setText("");

                return true;
            }
            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void finish() {
        super.finish();

        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
