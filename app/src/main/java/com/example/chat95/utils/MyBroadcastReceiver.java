package com.example.chat95.utils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.widget.Toast;

public class MyBroadcastReceiver extends BroadcastReceiver {

    private boolean connectionFlag=false;

    @Override
    public void onReceive(Context context, Intent intent) {

        //listen to connection
        if (ConnectivityManager.CONNECTIVITY_ACTION.equals(intent.getAction())) {
            //check if there is no connection
            boolean noConnection = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
            if (noConnection) {
                connectionFlag=true;
                Toast.makeText(context, "No internet connection. Some actions might not work correctly", Toast.LENGTH_LONG).show();
            }
            else if(connectionFlag) {
                Toast.makeText(context, "Connected to internet.", Toast.LENGTH_LONG).show();
                connectionFlag=false;
            }
        }

    }
}