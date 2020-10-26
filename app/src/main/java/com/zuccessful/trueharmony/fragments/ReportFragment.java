
package com.zuccessful.trueharmony.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.zuccessful.trueharmony.adapters.Med_Record_Adapter;
import com.zuccessful.trueharmony.adapters.MyDayAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.ActivityStat;
import com.zuccessful.trueharmony.pojo.Injection;
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
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_CID;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;


public class ReportFragment extends Fragment {

    private FirebaseFirestore db;
    private SakshamApp app;
    Patient patient;
    private ArrayList<MedicineRecord> medList=new ArrayList<>();
    private ArrayList<Medication> all_meds_stored=new ArrayList<>(); // list that is stored if no internet
    private RecyclerView mRecyclerView;
    private TextView mBlankMedReport;
    private Med_Record_Adapter adapter;
    private ArrayList<Medication> medicationArrayList;
    final HashMap<String,List> dateMedRecMap = new HashMap<>();
    String TAG = "ReportFrag";
    LinearLayoutManager linearLayoutManager;
    private Context mContext;
    public static final String MyPREFERENCES = "SavedReport" ;
    public static final String Status ="caregiver_status" ;
    SharedPreferences sharedpreferences;
    SharedPreferences.Editor editor;
    private Boolean report_entry_status=false;
    public ReportFragment() { }


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
            adapter=new Med_Record_Adapter(medList,mContext);
            LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(layoutManager);
            mRecyclerView.setAdapter(adapter);
        }
        else
        { Calendar calendar = Calendar.getInstance();
            Date date = calendar.getTime();
            String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
            Log.d("logss"," current date is : "+current_date);
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<Medication>>() {}.getType();
           ArrayList<Medication> all_saved_meds = gson.fromJson(sharedpreferences.getString("Med", ""), type);
           ArrayList<MedicineRecord> meds_today=new ArrayList<>();
           try{
            for(int j=0;j<all_saved_meds.size();j++)
            {  Map<String,Boolean> days=all_saved_meds.get(j).getDays();
                if(days.containsKey(current_date))
                { ArrayList<String>slot_list=all_saved_meds.get(j).getReminders();
                    int slots=slot_list.size();
                    for(int k=0;k<slots;k++)
                    {
                        Log.d("logss", "slot list entry for: "+all_saved_meds.get(j).getName()+" : "+ slot_list.get(k));
                        MedicineRecord med = new MedicineRecord(all_saved_meds.get(j).getName(), slot_list.get(k),days);
                        meds_today.add(med);
                    }
                }
                else {
                    Log.d("logss","NO MED ON THIS DAY of "+all_saved_meds.get(j));
                }
            }
            Log.d("logss",meds_today.size()+" is the size of meds of current date");
            if (meds_today.size() > 0) {
                Log.d("logss", "presentttt");
                adapter=new Med_Record_Adapter(meds_today,mContext);
                LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(layoutManager);
                mRecyclerView.setAdapter(adapter);
                mBlankMedReport.setVisibility(View.GONE);
                mRecyclerView.setVisibility(View.VISIBLE);
            }
        }catch(Exception e){}}
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        medList.clear();
        updateMedRecords();
    }
    public void updateMedRecords()
    {
        medicationArrayList = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        final String current_date=new SimpleDateFormat("EE", Locale.ENGLISH).format(date.getTime()).toLowerCase();
        if(Utilities.isInternetOn(getContext()))
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
                                    try {
                                        DocumentReference dr = db.collection("patient_med_reports/" + getContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                "/" + sdf.format(new Date())).document(med.getName() + " " + slot_list.get(k));
                                        dr.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot ds = task.getResult();
                                                    Log.d("logss", ds.getId() + " " + ds.exists());
                                                    if (ds.exists()) {
                                                        Log.d("logss", "ALREADY ENTRY PRESENT");
                                                    } else {
                                                        Log.d("logss", "again setting data to firebase");
                                                        db.collection("patient_med_reports/" + getContext().getSharedPreferences(PREF_CID, MODE_PRIVATE).getString(PREF_PID, "error") +
                                                                "/" + sdf.format(new Date())).document(med.getName() + " " + time).set(med, SetOptions.merge());
                                                        Log.d("logss", "settt to false-----------");

                                                    }
                                                }
                                            }
                                        });
                                    }catch (Exception e){}


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
                        if (medList.size() > 0) {
                            adapter.replaceMeds(medList);
                            mBlankMedReport.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.VISIBLE);
                            Gson gson = new Gson();
                            String json = gson.toJson(all_meds_stored);
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



}

