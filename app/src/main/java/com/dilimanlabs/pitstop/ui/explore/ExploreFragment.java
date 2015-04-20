package com.dilimanlabs.pitstop.ui.explore;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.afollestad.materialdialogs.MaterialDialog;
import com.dilimanlabs.androidlibrary.MapUtils;
import com.dilimanlabs.androidlibrary.NetworkUtils;
import com.dilimanlabs.pitstop.Pitstop;
import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.events.AddMarkersEndEvent;
import com.dilimanlabs.pitstop.events.FetchMarkersEndEvent;
import com.dilimanlabs.pitstop.jobs.FetchMarkersJob;
import com.dilimanlabs.pitstop.okhttp.Headers;
import com.dilimanlabs.pitstop.persistence.Business;
import com.dilimanlabs.pitstop.persistence.Category;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.dilimanlabs.pitstop.retrofit.PitstopService;
import com.dilimanlabs.pitstop.ui.SearchActivity;
import com.dilimanlabs.pitstop.ui.common.BaseFragment;
import com.dilimanlabs.pitstop.ui.explore.adapters.MyAdapter;
import com.dilimanlabs.pitstop.ui.explore.tasks.AddMarkersAsyncTask;
import com.dilimanlabs.pitstop.ui.explore.widgets.ExploreList;
import com.dilimanlabs.pitstop.ui.explore.widgets.InfoCard;
import com.facebook.rebound.SimpleSpringListener;
import com.facebook.rebound.Spring;
import com.facebook.rebound.SpringConfig;
import com.facebook.rebound.SpringSystem;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.path.android.jobqueue.JobManager;

import org.lucasr.twowayview.ItemClickSupport;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import de.greenrobot.event.EventBus;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Header;
import retrofit.client.Response;

public class ExploreFragment extends BaseFragment implements GoogleMap.OnCameraChangeListener, GoogleMap.OnMarkerClickListener, GoogleMap.OnMapClickListener {

    public static String TAG = "Explore";
    private static final int SEARCH_REQUEST = 1;

    @Inject
    JobManager mJobManager;

    private GoogleMap mMap; // Might be null if Google Play services APK is not available.
    private String[] selectedCategories = new String[]{};

    @InjectView(R.id.card)
    InfoCard mInfoCard;

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.card)
    void OnCardClick(View view) {
        if (mOpenedMarkerUrl != null) {
            final Establishment est = Establishment.getEstablishmentByUrl(mOpenedMarkerUrl);
            float zoom = mMap.getCameraPosition().zoom;
            if (zoom < MapUtils.MIN_ZOOM) {
                zoom = MapUtils.TILE_ZOOM; // TODO
            }

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(est.lat, est.lon), zoom));
        }
    }

    @InjectView(R.id.list)
    ExploreList mExploreList;

    private Spring mSpring;

    private AddMarkersAsyncTask mAddMarkersAsyncTask;

    private BiMap<Marker, String> mMarkers = HashBiMap.create();
    private String mOpenedMarkerUrl = null;
    private Marker mOpenedMarkerIndicator = null;
    private BitmapDescriptor mOpenedMarkerIndicatorIcon = null;

    private ArrayList<String> mArrayList;

    public CameraPosition mInitCameraPosition;

    public ExploreFragment() {

    }

    public static Fragment newInstance() {
        final Fragment fragment = new ExploreFragment();

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_explore, container, false);
        ButterKnife.inject(this, rootView);

        ((Pitstop) getActivity()
                .getApplication())
                .createScopedGraph(getModules().toArray())
                .inject(this);

        setHasOptionsMenu(true);

        if (savedInstanceState != null) {
            mInitCameraPosition = savedInstanceState.getParcelable("cameraPosition");
            mOpenedMarkerUrl = savedInstanceState.getString("openedMarkerUrl");
        }

        SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager().findFragmentByTag("Map");
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
        }

        getChildFragmentManager()
                .beginTransaction()
                .replace(R.id.map, fragment, "Map")
                .commit();

        fragment.getMapAsync(new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap googleMap) {
                mMap = googleMap;

                mMap.getUiSettings().setZoomControlsEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(false);

                mMap.setMyLocationEnabled(true);
                mMap.setOnCameraChangeListener(ExploreFragment.this);
                mMap.setOnMarkerClickListener(ExploreFragment.this);
                mMap.setOnMapClickListener(ExploreFragment.this);

                if (mInitCameraPosition != null) {
                    mMap.moveCamera(CameraUpdateFactory.
                            newLatLngZoom(mInitCameraPosition.target, mInitCameraPosition.zoom));
                }

                if (mOpenedMarkerUrl != null) {
                    displayCard(mOpenedMarkerUrl);
                }
            }
        });

        final SpringSystem ss = SpringSystem.create();
        mSpring = ss.createSpring().setSpringConfig(SpringConfig.fromOrigamiTensionAndFriction(40, 5));
        mSpring.addListener(new SimpleSpringListener() {
            @Override
            public void onSpringUpdate(Spring spring) {
                final float currentValue = (float) spring.getCurrentValue();
                mInfoCard.setTranslationY(-currentValue);
            }
        });

        mArrayList = new ArrayList<>();
        mExploreList.setRecyclerViewAdapter(new MyAdapter(getActivity(), mArrayList));
        ItemClickSupport itemClickSupport = mExploreList.getItemClickSupport();
        itemClickSupport.setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView recyclerView, View view, int i, long l) {
                displayCard(((MyAdapter) recyclerView.getAdapter()).getItem(i));
            }
        });

        itemClickSupport.setOnItemLongClickListener(new ItemClickSupport.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(RecyclerView recyclerView, View view, int i, long l) {

                return true;
            }
        });


        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        EventBus.getDefault().register(this);
    }


    @Override
    public void onPause() {
        EventBus.getDefault().unregister(this);

        super.onPause();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_explore, menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (selectedCategories.length == 0) {
            menu.findItem(R.id.action_filter_show).setIcon(R.drawable.ic_filter_light);
        } else {
            menu.findItem(R.id.action_filter_show).setIcon(R.drawable.ic_filter_highlight);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mMap != null) {
            outState.putParcelable("cameraPosition", mMap.getCameraPosition());
        } else {
            outState.putParcelable("cameraPosition", mInitCameraPosition);
        }

        outState.putString("openedMarkerUrl", mOpenedMarkerUrl);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_filter_show: {
                final List<Category> categories = Category.getAll();

                final String[] categoryNames = new String[categories.size()];
                final String[] categoryUrls = new String[categories.size()];
                for (int i = 0; i < categories.size(); i++) {
                    categoryNames[i] = categories.get(i).name;
                    categoryUrls[i] = categories.get(i).url;
                }

                final Integer[] preselected = new Integer[selectedCategories.length];
                for (int i = 0; i < categories.size(); i++) {
                    for (int j = 0; j < selectedCategories.length; j++) {
                        if (categoryUrls[i].equals(selectedCategories[j])) {
                            preselected[j] = i;
                        }
                    }
                }

                new MaterialDialog.Builder(getActivity())
                        .iconRes(R.drawable.ic_filter)
                        .title("Filters")
                        .items(categoryNames)
                        .itemsCallbackMultiChoice(preselected, new MaterialDialog.ListCallbackMultiChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog materialDialog, Integer[] which, CharSequence[] text) {
                                final String[] newSelectedCategories = new String[text.length];
                                int i = 0;
                                for (CharSequence name : text) {
                                    for (Category category : categories) {
                                        if (name.toString().equals(category.name)) {
                                            newSelectedCategories[i] = category.url;
                                            i++;
                                        }
                                    }
                                }

                                selectedCategories = newSelectedCategories;
                                updateMarkers(true);

                                getActivity().invalidateOptionsMenu();
                                return true;
                            }
                        })
                        .negativeText("CANCEL")
                        .positiveText("OK")
                        .show();

                ((Pitstop) getActivity().getApplication()).getPitstopService().getCategories(new Callback<PitstopService.ResponseWrapper<PitstopService.CategoriesResponse>>() {
                    @Override
                    public void success(PitstopService.ResponseWrapper<PitstopService.CategoriesResponse> responseWrapper, Response response) {
                        if (response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))
                                || !response.getHeaders().contains(new Header(Headers.IS_CACHED, "False"))) {
                            List<Category> categories = responseWrapper.response.categories;
                            for (Category cat : categories) {
                                new Category(cat.description, cat.name, cat.pluralName, cat.primaryImage, cat.url).save();
                            }
                        }
                    }

                    @Override
                    public void failure(RetrofitError error) {
                    }
                });

                return true;
            }
            case R.id.action_search: {
                final android.content.Intent intent = new android.content.Intent(this.getActivity(), SearchActivity.class);
                startActivityForResult(intent, SEARCH_REQUEST);
                getActivity().overridePendingTransition(0, 0);

                return true;
            }
            case R.id.action_togglelist: {
                if (mExploreList.getVisibility() == View.GONE) {
                    mExploreList.setVisibility(View.VISIBLE);
                } else {
                    mExploreList.setVisibility(View.GONE);
                }

                return true;
            }
            default: {

                return super.onOptionsItemSelected(item);
            }
        }
    }

    @Override
    public List<Object> getModules() {
        return Arrays.<Object>asList(new ExploreFragmentModule());
    }

    @Override
    public boolean onBackPressed() {
        if (mOpenedMarkerUrl != null) {
            hideCard();
            return true;
        }

        return false;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SEARCH_REQUEST) {
            if (resultCode == Activity.RESULT_OK) {

                if (!mMarkers.containsValue(data.getStringExtra("ESTABLISHMENT_URL"))) {
                    final Establishment est = Establishment.getEstablishmentByUrl(data.getStringExtra("ESTABLISHMENT_URL"));

                    final Marker markerFoo = mMap.addMarker(new MarkerOptions()
                            .position(new LatLng(est.lat, est.lon))
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_marker))
                            .anchor(0.5f, 0.5f)
                    );
                    mMarkers.put(markerFoo, est.url);
                }

                onMarkerClick(mMarkers.inverse().get(data.getStringExtra("ESTABLISHMENT_URL")));
            }
        }
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {
        if (cameraPosition.zoom >= MapUtils.MIN_ZOOM) {
            fetchMarkers();
        }

        updateMarkers(false);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        displayCard(mMarkers.get(marker));

        return false;
    }

    @Override
    public void onMapClick(LatLng latLng) {
        hideCard();
    }

    private void fetchMarkers() {
        if (NetworkUtils.isNetworkAvailable(getActivity())) {
            /*final Account account = Account.getAccount();
            if (account != null) {
                mJobManager.addJobInBackground(
                        new FetchMarkersJob(
                                mMap.getProjection().getVisibleRegion().latLngBounds,
                                account.authToken
                        )
                );
            }*/
            mJobManager.addJobInBackground(
                    new FetchMarkersJob(
                            mMap.getProjection().getVisibleRegion().latLngBounds,
                            ""
                    )
            );
        }
    }

    private void updateMarkers(boolean clearall) {
        if (clearall) {
            hideCard();
        }

        cancelAddMarkers();

        final float zoom = mMap.getCameraPosition().zoom;
        final LatLngBounds bounds = mMap.getProjection().getVisibleRegion().latLngBounds;
        boolean doNotSkip = (mOpenedMarkerUrl != null);

        Iterator<Map.Entry<Marker, String>> iterator = mMarkers.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Marker, String> item = iterator.next();

            if (clearall) {
                item.getKey().remove();
                iterator.remove();
                continue;
            }

            if (doNotSkip && item.getValue().equals(mOpenedMarkerUrl)) {
                // do not remove
                doNotSkip = false;
            } else if (zoom < MapUtils.MIN_ZOOM || !bounds.contains(item.getKey().getPosition())) {
                item.getKey().remove();
                iterator.remove();
            } else {
                // else do not remove
            }
        }

        if (zoom >= MapUtils.MIN_ZOOM) {
            addMarkers();
        }
    }

    private void addMarkers() {
        cancelAddMarkers();
        mAddMarkersAsyncTask = new AddMarkersAsyncTask(mMap, mMarkers, selectedCategories);
        mAddMarkersAsyncTask.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR);
    }

    private void cancelAddMarkers() {
        if (mAddMarkersAsyncTask != null) {
            mAddMarkersAsyncTask.cancel(true);
            mAddMarkersAsyncTask = null;
        }
    }

    public void displayCard(String markerIdUrl) {
        mOpenedMarkerUrl = markerIdUrl;
        final Establishment est = Establishment.getEstablishmentByUrl(mOpenedMarkerUrl);

        if (est == null) {
            hideCard();
            return;
        }

        if (mOpenedMarkerIndicator != null) {
            mOpenedMarkerIndicator.remove();
            mOpenedMarkerIndicator = null;
        }

        mOpenedMarkerIndicator = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(est.lat, est.lon))
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_highlight))
                        .anchor(0.5f, 0.5f)
                        .alpha(0.7f)
        );

        mSpring.setEndValue(getResources().getDimension(R.dimen.dp150));//mInfoCard.getHeight());

        final String bizUrl = mOpenedMarkerUrl.substring(0, "/businesses/12345678910".length());
        final Business biz = Business.getBusinessByUrl(bizUrl);

        mInfoCard.display(est, biz);
    }

    private void hideCard() {
        mOpenedMarkerUrl = null;

        if (mOpenedMarkerIndicator != null) {
            mOpenedMarkerIndicator.remove();
            mOpenedMarkerIndicator = null;
        }

        mSpring.setEndValue(0);
        mInfoCard.clear();
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(FetchMarkersEndEvent event) {
        updateMarkers(false);
    }

    @SuppressWarnings("UnusedDeclaration")
    public void onEventMainThread(AddMarkersEndEvent event) {
        mArrayList.clear();
        for (Map.Entry<Marker, String> item : mMarkers.entrySet()) {
            mArrayList.add(item.getValue());
        }
        mExploreList.onAdapterDataChanged();
    }
}