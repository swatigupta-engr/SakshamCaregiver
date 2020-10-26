package com.zuccessful.trueharmony.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.zuccessful.trueharmony.activities.AddInjActivity;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.services.NotificationScheduler;

public class ReminderReceiver extends BroadcastReceiver {
    String TAG = "ReminderReceiver";
    String title = "";
    String content = "";
    @Override
    public void onReceive(Context context,Intent intent){
        // TODO Auto-generated method stub
        Injection localData = new Injection(context);
        if (intent.getAction() != null && context != null) {
            if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
                // Set the alarm here.
                Log.d(TAG, "onReceive: BOOT_COMPLETED");

                int h = Integer.parseInt(localData.getHour());
                int m = Integer.parseInt(localData.getMin());
                int d = Integer.parseInt(localData.getDay());
                int y = Integer.parseInt(localData.getYear());
                int mn = Integer.parseInt(localData.getMonth());

                String repeated = localData.getRepeated();
                //int reqcode = localData.get_reqcode();
                int reqcode = Integer.parseInt(localData.getReqCode());
                NotificationScheduler.setReminder(context, ReminderReceiver.class, h,m,d,mn ,y,repeated,reqcode);
                return;
            }
        }

        Log.d(TAG, "onReceive: Notification Received");
        title = localData.getTitle();
        content = localData.getContent();
        int day = Integer.parseInt(localData.getDay());
        int month = Integer.parseInt(localData.getMonth());
        int year = Integer.parseInt(localData.getYear());
        int reqcode = Integer.parseInt(localData.getReqCode());
        String type = localData.getType();
        NotificationScheduler.showNotification(context, AddInjActivity.class,
                title, content,
                day,
                month,
                year,
                reqcode,type);


    }
}
