package com.zuccessful.trueharmony.services;

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
import android.widget.Toast;

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

public class MedService extends Service {
    private FirebaseFirestore db;
    private SakshamApp app;
     SimpleDateFormat sdf;
    Uri soundUri;
    private ArrayList<DailyRoutine> routineArrayList;
    private ArrayList<DailyRoutineRecord> routineList=new ArrayList<>();

    private ArrayList<DailyRoutine> all_alarm_saved_list=new ArrayList<>(); // list that is stored if no internet
    Context context;
    public MedService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
    public void onCreate()
    {
        Log.d("TAG"," MedService");
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        sdf = Utilities.getSimpleDateFormat();
        soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
          context = this;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        getMedDetails();
        updateDailyrecords();
        //Intent intent2= new Intent(getApplicationContext(), MyService.class);
       // getApplicationContext().startService(intent2);
        return START_STICKY;
    }

    public void onDestroy()
    {
        super.onDestroy();
    }

    public void getMedDetails()
    {
        db.collection("patient_med_reports/" + getApplicationContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                "/" + sdf.format(new Date())).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots,
                                        @Nullable FirebaseFirestoreException e) {

                        if (e != null) {
                            Log.w("TAG", "Listen failed.", e);
                            return;
                        }

                        for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                            switch (doc.getType()) {
                                case ADDED:
                                    Log.d("TAG", "New Msg: " + doc.getDocument().toObject(Message.class));
                                    break;
                                case MODIFIED:
                                    MedicineRecord ob=doc.getDocument().toObject(MedicineRecord.class);
                                    Log.d("TAG", "Modified Msg: " + ob.getName()+" "+ob.getTime());
                                    NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),"channel1")
                                            .setSmallIcon(R.drawable.ic_saksham_icon)
                                            .setContentTitle("Saksham Notification")
                                            .setContentText("New response for : "+ob.getName()+" "+ob.getTime())
                                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                                            .setSound(soundUri);
                                    NotificationManager mNotificationManager = (NotificationManager) getApplication().getSystemService(Context.NOTIFICATION_SERVICE);

                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                                    {
                                        String channelId = "Mychannel_id";
                                        CharSequence name = getString(R.string.channel_name);
                                        String description = getString(R.string.channel_description);
                                        int importance = NotificationManager.IMPORTANCE_DEFAULT;
                                        NotificationChannel channel = new NotificationChannel(channelId, name,importance);
                                        mNotificationManager.createNotificationChannel(channel);
                                        builder.setChannelId(channelId);
                                    }

                                    mNotificationManager.notify(0, builder.build());

                                    break;
                                case REMOVED:
                                    Log.d("TAG", "Removed Msg: " + doc.getDocument().toObject(Message.class));
                                    break;
                            }
                        }
                        Log.d("TAG","--- change occured");
                    }
                });
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
                        Log.d("logss", "success");
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
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
                                                        Log.d("logss","ALREADY ENTRY PRESENT"+     ds.get("name")+","+
                                                                ds.get("done"));
                                                        int val=Integer.parseInt(ds.get("done").toString());
                                                        if(val==0){

                                                            {    Log.v("Value_0:",val+"");

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
                                                    {   Log.d("logss","again setting data to firebase");
                                                        try{
                                                            db.collection("patient_daily_routine_reports/" + context.getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                                    "/" + sdf.format(new Date())).document(rec.getName()+" "+time).set(rec, SetOptions.merge());
                                                            Log.d("logss","settt to false-----------");}
                                                        catch (Exception e){
                                                            Toast.makeText(context,"Internet Offline..Kindly reconnect..",Toast.LENGTH_LONG).show();
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
                            catch(Exception e){}
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
