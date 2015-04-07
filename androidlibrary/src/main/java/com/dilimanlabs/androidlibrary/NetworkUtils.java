package com.dilimanlabs.androidlibrary;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkUtils {
    public static boolean isNetworkAvailable(Context context) {
        boolean isNetworkAvailable = false;

        try {
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            isNetworkAvailable = (networkInfo != null && networkInfo.isConnected());
        } catch (Exception e) {

        }

        return isNetworkAvailable;
    }
}
