package com.dilimanlabs.pitstop.persistence;

import android.os.Parcel;
import android.os.Parcelable;

public class Intent implements Parcelable {
    public String action;
    public String name;
    public String value;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(action);
        dest.writeString(name);
        dest.writeString(value);
    }

    public static final Creator<Intent> CREATOR = new Creator<Intent>() {
        @Override
        public Intent createFromParcel(Parcel source) {
            return new Intent(source);
        }

        @Override
        public Intent[] newArray(int size) {
            return new Intent[size];
        }
    };

    private Intent(Parcel in) {
        action = in.readString();
        name = in.readString();
        value = in.readString();
    }
}
