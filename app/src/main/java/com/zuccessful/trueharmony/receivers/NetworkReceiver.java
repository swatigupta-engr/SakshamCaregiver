package com.zuccessful.trueharmony.receivers;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import com.zuccessful.trueharmony.services.NewAlarmService;

public class NetworkReceiver extends BroadcastReceiver {
    @Override

    public void onReceive(Context context, Intent intent) {

        Log.d("API123", "" + intent.getAction());


        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
        if (isConnected) {
            try {
              //  Toast.makeText(context, "Network is connected", Toast.LENGTH_LONG).show();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    context.startForegroundService(new Intent(context, NewAlarmService.class));
                } else {
                    context.startService(new Intent(context, NewAlarmService.class));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
         // Toast.makeText(context, "Network is changed or reconnected", Toast.LENGTH_LONG).show();

    }
        }}
