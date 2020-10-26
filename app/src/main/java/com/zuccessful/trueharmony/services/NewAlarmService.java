package com.zuccessful.trueharmony.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_CID;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class NewAlarmService extends Service {
    private FirebaseFirestore db;
    private SakshamApp app;
    SimpleDateFormat sdf;
    Uri soundUri;
    private ArrayList<DailyRoutine> routineArrayList;
    private ArrayList<DailyRoutineRecord> routineList=new ArrayList<>();
    private ArrayList<MedicineRecord> medList=new ArrayList<>();
     private ArrayList<DailyRoutine> all_alarm_saved_list=new ArrayList<>(); // list that is stored if no internet
    Context context;
    private ArrayList<Medication> medicationArrayList;
    private ArrayList<Medication> all_meds_stored=new ArrayList<>(); // list that is stored if no internet

    public NewAlarmService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate()
    {
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "my_channel_01";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE)).createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setContentTitle("")
                    .setContentText("").build();

            startForeground(1, notification);
        }        Log.d("TAG","newalarmService");
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        sdf = Utilities.getSimpleDateFormat();
         context = this;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
          updateDailyrecords();
        updateMedRecords();
        return START_STICKY;
    }

    public void onDestroy()
    {
        super.onDestroy();
    }
    public void updateMedRecords()
    {
        medicationArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if(Utilities.isInternetOn(context))
        {
            db.collection("alarms/" + app.getAppUser(null).getId() + "/medication").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("logss", "success");
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Medication medication = snapshot.toObject(Medication.class);
                            all_meds_stored.add(medication);
                            medicationArrayList.add(medication);
                        }
                        for (int j = 0; j < medicationArrayList.size(); j++)
                        {
                            Map<String, Boolean> days = medicationArrayList.get(j).getDays();
                            Log.v("Days__",days +"____________"+ current_date +"");
                            if (days.containsKey(current_date))
                            {
                                ArrayList<String> slot_list = medicationArrayList.get(j).getReminders();
                                int slots = slot_list.size();
                                final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                                for (int k = 0; k < slots; k++)
                                {
                                    final String time=slot_list.get(k);
                                    final MedicineRecord med = new MedicineRecord(medicationArrayList.get(j).getName(), slot_list.get(k), days);
                                    DocumentReference dr= db.collection("patient_med_reports/" + context.getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                            "/" + sdf.format(new Date())).document(med.getName()+" "+slot_list.get(k));
                                    dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            if(task.isSuccessful())
                                            {
                                                DocumentSnapshot ds= task.getResult();
                                                Log.d("logss",ds.getId()+" "+ds.exists());
                                                if (ds.exists())
                                                {
                                                    Log.d("logss","ALREADY ENTRY PRESENT");
                                                }
                                                else
                                                {   Log.d("logss","again setting data to firebase");
                                                    db.collection("patient_med_reports/" + context.getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                            "/" + sdf.format(new Date())).document(med.getName()+" "+time).set(med, SetOptions.merge());
                                                    Log.d("logss","settt to false-----------");

                                                }
                                            }
                                        }
                                    });

                                  /* dr.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<Medication> m = queryDocumentSnapshots.toObjects(Medication.class);
                                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                                //Medication i = ds.toObject(Medication.class);
                                                Injection i = ds.toObject(Injection.class);
                                                Injections.add(i);
                                                Log.d(TAG, i.toString());
                                                //Medications.add(i);
                                            }

//                            Toast.makeText(getContext(), "List: " + Medications, Toast.LENGTH_SHORT).show();
                                        }
                                    }});*/
                                    medList.add(med);
                                }
                            } else {
                                Log.d("logss", "NO MED ON THIS DAY");
                            }
                        }

                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

        }
    }

    // updating daily routine logs

    public void updateDailyrecords()
    {
        routineArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if(Utilities.isInternetOn( this))
        {
            db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("logging", "success");
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            Log.d("logging", "task."+task.getResult());

                            DailyRoutine daily_routine = snapshot.toObject(DailyRoutine.class);
                            all_alarm_saved_list.add(daily_routine);
                            routineArrayList.add(daily_routine);

                            Utilities.saveDailyRoutineAlarms(daily_routine);
                            Log.v("routine",routineArrayList.get(0).getDays()+toString());
                        }
                        for (int j = 0; j < routineArrayList.size(); j++)
                        {
                            Map<String, Boolean> days = routineArrayList.get(j).getDays();
                            try{
                                if ( days.containsKey(current_date))
                                //  if(true)
                                {
                                    ArrayList<String> slot_list = routineArrayList.get(j).getReminders();
                                    int slots = slot_list.size();
                                    final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                                    for (int k = 0; k < slots; k++)
                                    {
                                        final String time=slot_list.get(k);
                                        final DailyRoutineRecord rec = new DailyRoutineRecord(routineArrayList.get(j).getName(), slot_list.get(k), days);
                                        DocumentReference dr= db.collection("patient_daily_routine_reports/" + context.getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                "/" + sdf.format(new Date())).document(rec.getName()+" "+slot_list.get(k));
                                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful())
                                                {
                                                    DocumentSnapshot ds= task.getResult();
                                                    //      Log.d("logss",ds.getId()+" "+ds.exists());
                                                    if (ds.exists())
                                                    {
                                                        Log.d("logging","ALREADY ENTRY PRESENT"+     ds.get("name")+","+
                                                                ds.get("done"));
                                                        int val=Integer.parseInt(ds.get("done").toString());
                                                        if(val==0){
                                                            {                                                    Log.v("Value_0:",val+"");

                                                                //hmap.put(ds.get("name").toString(),false);
                                                                Utilities.saveDailyLog(ds.get("name").toString(), current_date, time, false, context);
                                                            }
                                                        }
                                                        else{
                                                            //  hmap.put(ds.get("name").toString(),true);


                                                            Log.v("Value_1:",val+"");
                                                            Utilities.saveDailyLog(ds.get("name").toString(),current_date,time,true,context);
                                                        }


                                                    }
                                                    else
                                                    {   Log.d("logging","again setting data to firebase");
                                                        try{
                                                            db.collection("patient_daily_routine_reports/" + context.getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                                    "/" + sdf.format(new Date())).document(rec.getName()+" "+time).set(rec, SetOptions.merge());
                                                            Log.d("logss","settt to false-----------");}
                                                        catch (Exception e){
                                                            e.printStackTrace();}

                                                    }
                                                }
                                            }
                                        });

                                        routineList.add(rec);
                                    }
                                } else {
                                    Log.d("logss", "NO MED ON THIS DAY");
                                }}
                            catch(Exception e){
                                e.printStackTrace();
                            }
                        }




                    }
                }
            })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });

        }
    }
}
