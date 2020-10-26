package com.zuccessful.trueharmony.receivers;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.services.NewAlarmService;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_CID;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class MyReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 12345;
    //public static final String ACTION = "com.zuccessful.trueharmony.receivers";
    @Override

    public void onReceive(Context context, Intent intent) {


        Log.d("API123", "" + intent.getAction());


          /*  if (intent.getAction().equals(Intent.ACTION_DATE_CHANGED)||intent.getAction().equals(Intent.ACTION_TIME_CHANGED)) {*/
                Log.v("", "ACTION_DATE_CHANGED received");
              //  Toast.makeText(context, "date change is received", Toast.LENGTH_LONG).show();
        //        if(Utilities.isInternetOn(context))
                {      //
                    //      Toast.makeText(context, "ACTION_DATE_CHANGED service started ", Toast.LENGTH_LONG).show();

                    Log.d("TAG"," service started");
                    /*Intent intentnew= new Intent(context, NewAlarmService.class);
                    context.startService(intentnew);*/
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        Log.v("TAG","after oreo");
                        context.startForegroundService(new Intent(context, NewAlarmService.class));
                    } else {
                        context.startService(new Intent(context, NewAlarmService.class));
                        Log.v("TAG","befoire oreo");

                    }

                }

        }}




//}