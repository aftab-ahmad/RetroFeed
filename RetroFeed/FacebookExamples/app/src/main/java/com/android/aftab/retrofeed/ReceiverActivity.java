/*******************************************************************************
 * ReceiverActivity.java: Classs to listen for changes in network connectivity
 *
 * NOT FUNCTIONAL, NOT COMPLETED!!!!
 *
 * Authors: Aftab Ahmad
 * Last Updated: March 1st, 2015
 *******************************************************************************/


package com.android.aftab.retrofeed;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

public class ReceiverActivity extends BroadcastReceiver {

    /*                      onReceive()                             */
    /* Receives an intent which signals a change in connectivity    */
	@Override
    public void onReceive(Context context, Intent intent) {

        /* Check state of connectivity */
        if(isConnected(context))
            Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(context, "Lost Connection", Toast.LENGTH_SHORT).show();
    }

    public boolean isConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        return isConnected;
    }
}
