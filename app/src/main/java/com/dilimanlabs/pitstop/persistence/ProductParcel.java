package com.dilimanlabs.pitstop.persistence;

import android.os.Parcelable;

import java.util.List;

import auto.parcel.AutoParcel;

@AutoParcel
public abstract class ProductParcel implements Parcelable {
        public abstract String getDescription();
        public abstract List<Intent> getIntents();
        public abstract boolean isPromo();
        public abstract boolean isVisible();
        public abstract String getName();
        public abstract int getOrder();
        public abstract String getPrimaryImage();
        public abstract String getUrl();

        public static ProductParcel create(String desciption, List<Intent> intents, boolean isPromo, boolean isVisible, String name, int order, String primaryImage, String url) {
            return new AutoParcel_ProductParcel(desciption, intents, isPromo, isVisible, name, order, primaryImage, url);
        }
}
