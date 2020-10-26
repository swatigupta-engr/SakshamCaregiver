
package com.zuccessful.trueharmony.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.JsonReader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.MyDayActivity;
import com.zuccessful.trueharmony.adapters.DailyRoutineAdapterLocal;
import com.zuccessful.trueharmony.adapters.Daily_Report_Adapter_Global;
import com.zuccessful.trueharmony.adapters.Med_Record_Adapter;
import com.zuccessful.trueharmony.adapters.MyDayAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.ActivityStat;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.pojo.Testdata;
//import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
//import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.zuccessful.trueharmony.utilities.Utilities;

import org.json.JSONArray;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_CID;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;


public class DailyRoutine_Report extends Fragment {

    private FirebaseFirestore db;
    private SakshamApp app;
    Patient patient;
    private ArrayList<DailyRoutineRecord> routineList=new ArrayList<>();
    private ArrayList<DailyRoutine> all_alarm_saved_list=new ArrayList<>(); // list that is stored if no internet
    private RecyclerView mRecyclerView;
    private TextView mBlankMedReport;
    private Daily_Report_Adapter_Global adapter;
    private DailyRoutineAdapterLocal adapter_local;

    private ArrayList<DailyRoutine> routineArrayList;

    final HashMap<String,List> dateMedRecMap = new HashMap<>();
    String TAG = "ReportFrag";
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    public static final String MyPREFERENCES = "SavedReport" ;
    public static final String Status ="caregiver_status" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private Boolean report_entry_status=false;
    ProgressDialog progressDialog;
    public DailyRoutine_Report() { }
  public  static HashMap<String , Boolean> hmap = new HashMap<String, Boolean>();
ArrayList<String>uniquenames=new ArrayList<>();



    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        editor = sharedpreferences.edit();

    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState)
    {
        Log.d("logss","onCreateView");
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.report_recyclerView);
        mBlankMedReport= view.findViewById(R.id.blank_med_report);
        mContext=getContext();

        if(Utilities.isInternetOn(getContext()))
        {


            progressDialog = new ProgressDialog(mContext);
            progressDialog.setMessage("Please Wait..."); // Setting Message
            progressDialog.setTitle("Updating data"); // Setting Title
            progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); // Progress Dialog Style Spinner
            progressDialog.show(); // Display Progress Dialog
            progressDialog.setCancelable(false);


            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    try {

                        adapter=new Daily_Report_Adapter_Global(routineList,mContext);
                        //  adapter=new DailyRoutineAdapterLocal(routineList,mContext);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(layoutManager);

                        mRecyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();                }
            }, 1000);
           /* new Thread(new Runnable() {
                public void run() {
                    try {

                     adapter=new Daily_Report_Adapter_Global(routineList,mContext);
                        //  adapter=new DailyRoutineAdapterLocal(routineList,mContext);

                        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                        mRecyclerView.setLayoutManager(layoutManager);

                        mRecyclerView.setAdapter(adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    progressDialog.dismiss();
                }
            }).start();*/
        }

        else
        { Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
            Log.d("logss"," current date is : "+current_date);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<DailyRoutine>>() {}.getType();
            try{
                ArrayList<DailyRoutine> all_saved_meds = gson.fromJson(sharedpreferences.getString("act_daily", ""), type);
                ArrayList<DailyRoutineRecord> meds_today=new ArrayList<>();
                for(int j=0;j<all_saved_meds.size();j++) {
                    Map<String, Boolean> days = all_saved_meds.get(j).getDays();
                    if (days.containsKey(current_date)) {
                        ArrayList<String> slot_list = all_saved_meds.get(j).getReminders();
                        int slots = slot_list.size();
                        for (int k = 0; k < slots; k++) {
                            Log.d("logss", "slot list entry for: " + all_alarm_saved_list.get(j).getName() + " : " + slot_list.get(k));
                            DailyRoutineRecord med = new DailyRoutineRecord(all_alarm_saved_list.get(j).getName(), slot_list.get(k), days);
                            meds_today.add(med);
                        }
                    } else {
                        Log.d("logss", "NO MED ON THIS DAY of " + all_saved_meds.get(j));
                    }

                }

                Log.d("logss",meds_today.size()+" is the size of meds of current date");
                if (meds_today.size() > 0) {
                    Log.d("logss", "presentttt");
                    adapter=new Daily_Report_Adapter_Global(meds_today,mContext);
                    LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                    mRecyclerView.setLayoutManager(layoutManager);
                    mRecyclerView.setAdapter(adapter);
                    mBlankMedReport.setVisibility(View.GONE);
                    mRecyclerView.setVisibility(View.VISIBLE);
                }
            }catch(Exception e){}
        }
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        routineList.clear();
        if(isNetworkAvailable())
            updateMedRecords();



    }

    public void updateMedRecords()
    {
        routineArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if(Utilities.isInternetOn(getContext()))
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
                                        DocumentReference dr= db.collection("patient_daily_routine_reports/" + getContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
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
                                                            {                                                    Log.v("Value_0:",val+"");

                                                                hmap.put(ds.get("name").toString(),false);
                                                                Utilities.saveDailyLog(ds.get("name").toString(), current_date, time, false, mContext);
                                                            }
                                                        }
                                                        else{
                                                            hmap.put(ds.get("name").toString(),true);


                                                            Log.v("Value_1:",val+"");
                                                            Utilities.saveDailyLog(ds.get("name").toString(),current_date,time,true,mContext);
                                                        }


                                                    }
                                                    else
                                                    {   Log.d("logss","again setting data to firebase");
                                                        try{
                                                            db.collection("patient_daily_routine_reports/" + getContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                                    "/" + sdf.format(new Date())).document(rec.getName()+" "+time).set(rec, SetOptions.merge());
                                                            Log.d("logss","settt to false-----------");}
                                                        catch (Exception e){e.printStackTrace();
                                                            Toast.makeText(getContext(),"Internet Offline..Kindly reconnect..",Toast.LENGTH_LONG).show();
                                                        }

                                                    }
                                                }
                                            }
                                        });

                                        if(!uniquenames.contains(rec.getName()))
                                        { routineList.add(rec);
                                        Log.v("adding data",routineList.size()+"");
                                        uniquenames.add(rec.getName());}
                                    }
                                } else {
                                    Log.d("logss", "NO MED ON THIS DAY");
                                }}
                            catch(Exception e){}
                        }



                        if (routineList.size() > 0) {
                            try {
                                adapter.replaceMeds(routineList);
                            }catch (Exception e){}
                            mBlankMedReport.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String json = gson.toJson(all_alarm_saved_list);
                            editor.putString("Med", json);
                            editor.apply();
                        } else {
                            mBlankMedReport.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
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
    /*public void updateMedRecords()
    {
        routineArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if(Utilities.isInternetOn(getContext()))
        {
            db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        Log.d("logss", "success");
                        for (QueryDocumentSnapshot snapshot : task.getResult()) {
                            DailyRoutine medication = snapshot.toObject(DailyRoutine.class);
                            all_alarm_saved_list.add(medication);
                            routineArrayList.add(medication);
                        }
                        for (int j = 0; j < routineArrayList.size(); j++)
                        {
                            Map<String, Boolean> days=null;
                           *//* Map<String, Boolean> days =null;
                            days.put("Mon",true);
                            days.put("Tue",true);
                            days.put("Wed",true);
                            days.put("Thu",true);
                            days.put("Fri",true);
                            days.put("Sat",true);
                            days.put("Sun",true);*//*
 //if(true)

                        if (days.containsKey(current_date))
                            {
                                Log.v("Swati",routineArrayList.get(j).getName());
                                ArrayList<String> slot_list = routineArrayList.get(j).getReminders();
                                int slots = slot_list.size();

                                final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                                for (int k = 0; k < slots; k++)
                                {
                                    final String time=slot_list.get(k);
                                    final DailyRoutineRecord med = new DailyRoutineRecord(routineArrayList.get(j).getName(), slot_list.get(k), days);
                                    DocumentReference dr= db.collection("patient_daily_routine_reports/" + getContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
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
                                                    db.collection("patient_daily_routine_reports/" + getContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                            "/" + sdf.format(new Date())).document(med.getName()+" "+time).set(med, SetOptions.merge());
                                                    Log.d("logss","settt to false-----------");

                                                }
                                            }
                                        }
                                    });

                                    routineList.add(med);
                                }
                            } else {
                                Log.d("logss", "NO MED ON THIS DAY");
                            }
*//*
                            Set<DailyRoutineRecord> set = new HashSet<>(routineList);
                            routineList.clear();
                            routineList.addAll(set);*//*
                        }
                        if (routineList.size() > 0) {
                            adapter.replaceMeds(routineList);
                            mBlankMedReport.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String json = gson.toJson(all_alarm_saved_list);
                            editor.putString("act_daily", json);
                            editor.apply();
                        } else {
                            mBlankMedReport.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
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
    }*/

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

}

