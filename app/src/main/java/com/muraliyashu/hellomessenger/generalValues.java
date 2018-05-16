package com.muraliyashu.hellomessenger;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by MuraliYashu on 8/18/2017.
 */

public class generalValues
{
    public static boolean checkingConnection(Context context)
    {
        Context getContext = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
}