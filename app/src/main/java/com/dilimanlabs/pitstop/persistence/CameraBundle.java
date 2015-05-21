package com.dilimanlabs.pitstop.persistence;

import com.google.android.gms.maps.model.LatLngBounds;

public class CameraBundle {
    public final LatLngBounds latLngBounds;
    public final float zoom;

    public CameraBundle(LatLngBounds latLngBounds, float zoom) {
        this.latLngBounds = latLngBounds;
        this.zoom = zoom;
    }

}
