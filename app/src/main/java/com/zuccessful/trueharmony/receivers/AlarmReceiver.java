package com.zuccessful.trueharmony.receivers;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;

import com.zuccessful.trueharmony.services.AlarmService;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

//        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
//        if (alarmUri == null) {
//            alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
//        }
//        Ringtone ringtone = RingtoneManager.getRingtone(context, alarmUri);
//        ringtone.play();


//        Medication medication = (Medication) Utilities.getExtraFromIntent(intent, "medRaw");


        Uri alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        AlarmService.ringtone = RingtoneManager.getRingtone(context.getApplicationContext(), alarmUri);

        AlarmService.ringtone.play();
        int alarm_id = intent.getIntExtra("alarm_id", 0);
        ComponentName comp = new ComponentName(context.getPackageName(), AlarmService.class.getName());
        AlarmService.enqueueWork(context, alarm_id, intent.setComponent(comp));

        setResultCode(Activity.RESULT_OK);
    }
}
