package com.dilimanlabs.pitstop.persistence;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Product implements Parcelable {
    public String description;
    public List<Intent> intents;
    public boolean isPromo;
    public boolean isVisible;
    public String name;
    public int order;
    public String primaryImage;
    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

        dest.writeString(description);
        dest.writeTypedList(intents);
        dest.writeByte((byte) (isPromo ? 1 : 0));
        dest.writeByte((byte) (isVisible ? 1 : 0));
        dest.writeString(name);
        dest.writeInt(order);
        dest.writeString(primaryImage);
        dest.writeString(url);
    }

    public static final Creator<Product> CREATOR = new Creator<Product>() {
        @Override
        public Product createFromParcel(Parcel source) {
            return new Product(source);
        }

        @Override
        public Product[] newArray(int size) {
            return new Product[size];
        }
    };

    private Product(Parcel in) {
        description = in.readString();
        in.readTypedList(intents, Intent.CREATOR);
        isPromo = in.readByte() != 0;
        isVisible = in.readByte() != 0;
        name = in.readString();
        order = in.readInt();
        primaryImage = in.readString();
        url = in.readString();
    }
}
