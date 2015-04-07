package com.dilimanlabs.androidlibrary;

import com.google.android.gms.maps.model.LatLngBounds;
import com.google.common.collect.ContiguousSet;
import com.google.common.collect.DiscreteDomain;
import com.google.common.collect.Range;

import java.util.ArrayList;

public class MapUtils {
    public static int TILE_ZOOM = 15;
    public static float MIN_ZOOM = 13.0f; // minimum zoom level to enable markers
    public static float VERY_MIN_ZOOM = 7.0f;

    private static float LOW_MID_ZOOM_LEVEL_BOUNDARY = 11.0f;
    private static float MID_HIGH_ZOOM_LEVEL_BOUNDARY = 13.0f;

    public enum ZoomLevelTransition {
        LOW_TO_LOW, MID_TO_LOW, MID_TO_MID, LOW_TO_MID, HIGH_TO_MID, HIGH_TO_HIGH, MID_TO_HIGH
    }

    public static ArrayList<String> getTileNames(LatLngBounds bounds) {
        int left = (int) Math.floor((bounds.southwest.longitude + 180) / 360 * (1 << TILE_ZOOM));
        int right = (int) Math.floor((bounds.northeast.longitude + 180) / 360 * (1 << TILE_ZOOM));
        int top = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(bounds.northeast.latitude)) + 1 / Math.cos(Math.toRadians(bounds.northeast.latitude))) / Math.PI) / 2 * (1 << TILE_ZOOM));
        int bottom = (int) Math.floor((1 - Math.log(Math.tan(Math.toRadians(bounds.southwest.latitude)) + 1 / Math.cos(Math.toRadians(bounds.southwest.latitude))) / Math.PI) / 2 * (1 << TILE_ZOOM));

        if (left < 0)
            left = 0;
        if (left >= (1 << TILE_ZOOM))
            left = ((1 << TILE_ZOOM) - 1);
        if (bottom < 0)
            bottom = 0;
        if (bottom >= (1 << TILE_ZOOM))
            bottom = ((1 << TILE_ZOOM) - 1);

        if (right < 0)
            right = 0;
        if (right >= (1 << TILE_ZOOM))
            right = ((1 << TILE_ZOOM) - 1);
        if (top < 0)
            top = 0;
        if (top >= (1 << TILE_ZOOM))
            top = ((1 << TILE_ZOOM) - 1);

        ArrayList<String> valuesList = new ArrayList<String>();
        for(int x : ContiguousSet.create(Range.closed(left, right), DiscreteDomain.integers())) {
            for(int y : ContiguousSet.create(Range.closed(top, bottom), DiscreteDomain.integers())) {
                valuesList.add(TILE_ZOOM + "/" + x + "/" + y);
            }
        }

        return valuesList;
    }
}
