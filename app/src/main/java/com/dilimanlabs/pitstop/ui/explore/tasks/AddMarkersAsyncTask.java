package com.dilimanlabs.pitstop.ui.explore.tasks;

import android.os.AsyncTask;

import com.dilimanlabs.pitstop.R;
import com.dilimanlabs.pitstop.events.AddMarkersEndEvent;
import com.dilimanlabs.pitstop.persistence.Establishment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.common.collect.BiMap;

import java.lang.ref.WeakReference;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import de.greenrobot.event.EventBus;

public class AddMarkersAsyncTask extends AsyncTask<Void, Establishment, Boolean> {
    private final WeakReference<GoogleMap> mMap;
    private final WeakReference<BiMap<Marker, String>> mMarkers;
    private final String[] categories;
    private final LatLngBounds mBounds;

    private final BitmapDescriptor mIcon;

    public AddMarkersAsyncTask(GoogleMap gmap, BiMap<Marker, String> markers, String[] categories) {
        this.mMap = new WeakReference<>(gmap);
        this.mMarkers = new WeakReference<>(markers);
        this.categories = categories;
        this.mBounds = gmap.getProjection().getVisibleRegion().latLngBounds;

        mIcon = BitmapDescriptorFactory.fromResource(R.drawable.ic_marker);
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected Boolean doInBackground(Void... params) {

        List<Establishment> mEstablishments = Establishment.getEstablishmentsWithinBoundsWithCategories(mBounds, Arrays.asList(categories));

        for (Establishment est : emptyIfNull(mEstablishments)) {
            if (isCancelled()) {
                break;
            }

            publishProgress(est);
        }

        return true;
    }

    @Override
    protected void onProgressUpdate(Establishment... est) {
        String url = est[0].url;
        double lat = est[0].lat;
        double lng = est[0].lon;

        if (canContinue() && !mMarkers.get().containsValue(url)) {
            Marker markerFoo = mMap.get().addMarker(
                    new MarkerOptions()
                            .position(new LatLng(lat, lng))
                            .icon(mIcon)
                            .anchor(0.5f, 0.5f)
            );
            mMarkers.get().put(markerFoo, url);
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        EventBus.getDefault().post(new AddMarkersEndEvent());
        super.onPostExecute(result);
    }

    private boolean canContinue() {
        GoogleMap map = mMap.get();
        BiMap<Marker, String> markers = mMarkers.get();

        return map != null && markers != null;
    }

    private <T> Iterable<T> emptyIfNull(Iterable<T> iterable) {
        return iterable == null ? Collections.<T>emptyList() : iterable;
    }
}
