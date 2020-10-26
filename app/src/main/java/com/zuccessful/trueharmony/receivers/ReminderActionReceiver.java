package com.zuccessful.trueharmony.receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.AppConstant;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.services.NotificationScheduler;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class ReminderActionReceiver extends BroadcastReceiver {
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();
    @Override
    public void onReceive(Context context, Intent intent){
        String action = intent.getAction();
        Injection localData = new Injection(context);
            if (AppConstant.YES_ACTION.equals(action)) {
                int code = Integer.parseInt(intent.getStringExtra("reqcode"));

                onYes(code,context,localData);
            }
            else  if (AppConstant.NO_ACTION.equals(action)) {
                int code = Integer.parseInt(intent.getStringExtra("reqcode"));
                int day = Integer.parseInt(intent.getStringExtra("day"));
                int month = Integer.parseInt(intent.getStringExtra("month"));
                int year = Integer.parseInt(intent.getStringExtra("year"));
                onNo(context,code,day,year,month,localData);
            }

    }
    public void onYes(int code,Context context,Injection localData){
        String name = localData.getName();
        NotificationScheduler.clearNotification(code,context);
        localData.setStatus("Done");
        try {db.collection("alarms")
                .document(app.getAppUser(null)
                        .getId())
                .collection("injection")
                .document(name)
                .set(localData);
        } catch (Exception e) { e.printStackTrace(); }

        Log.d("ActionSelected","Yes");
    }
    public void onNo(Context context,int code, int day,int year,int month, Injection localData){

        Calendar calendar = Calendar.getInstance();
        Calendar mycalendar = Calendar.getInstance();
        mycalendar.set(Calendar.DAY_OF_MONTH,day);
        mycalendar.set(Calendar.YEAR,year);
        mycalendar.set(Calendar.MONTH,month);

        long current_time = calendar.getTimeInMillis();
        long scheduled_time = mycalendar.getTimeInMillis();
        long diff = current_time - scheduled_time;
        String name = localData.getName();
        if(diff >= Calendar.MILLISECOND*TimeUnit.DAYS.toMillis(1)*3)
        {
            localData.setStatus("Missed");
            NotificationScheduler.clearNotification(code,context);

            try {db.collection("alarms")
                    .document(app.getAppUser(null)
                            .getId())
                    .collection("injection")
                    .document(name)
                    .set(localData);
            } catch (Exception e) { e.printStackTrace(); }

        }

        Log.d("ActionSelected","NO");
    }
}
