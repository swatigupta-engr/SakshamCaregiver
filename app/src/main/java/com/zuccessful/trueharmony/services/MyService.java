package com.zuccessful.trueharmony.services;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import java.util.Calendar;

public class MyService extends Service {
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Query the database and show alarm if it applies

        // I don't want this service to stay in memory, so I stop it
        // immediately after doing what I wanted it to do.
        stopSelf();

        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        // I want to restart this service again in one hour
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 00);
        calendar.set(Calendar.MINUTE,00);
        calendar.set(Calendar.SECOND,00);

        calendar.add(Calendar.DATE,1);
        AlarmManager alarm = (AlarmManager)getSystemService(ALARM_SERVICE);
        alarm.set(
            alarm.RTC_WAKEUP,
                calendar.getTimeInMillis(),
            PendingIntent.getService(this, 0, new Intent(this, MyService.class), 0)
        );
    }
}