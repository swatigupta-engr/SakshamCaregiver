package com.zuccessful.trueharmony.fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

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
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.MyDayActivity;
//import com.zuccessful.trueharmony.activities.ViewMeds;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.pojo.Patient;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MedStatsFrag extends Fragment  {

    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    /* private TableLayout tableLayout;
     private TableRow tableRow1;
     private TableRow tableRowX;*/
    private ArrayList<Medication> medicationArrayList;
    private final String TAG = "MEDTAG";
    final HashMap<String,List> dateMedRecMap = new HashMap<>();
    private BarChart barChart;
    public MedStatsFrag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_med_stats, container, false);
        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        patient = app.getAppUser(null);
        barChart = (BarChart) view.findViewById(R.id.barchart);
       /* tableLayout = (TableLayout) view.findViewById(R.id.table);
        tableRow1 = new TableRow(getContext());
        TableLayout.LayoutParams layoutParams = new TableLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        tableLayout.setLayoutParams(layoutParams);*/
        fetchMedicines();
        fetchMedicineRecords();
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Loading Graph");
        progressDialog.setMessage("Please wait");
        progressDialog.show();

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //createTable();
                createChart();
                progressDialog.dismiss();
            }
        },5000);
        return view;
    }

    private void createChart()
    {
        ArrayList<BarEntry> barGroupTaken = new ArrayList<>();
        ArrayList<BarEntry> barGroupMissed= new ArrayList<>();
        String[] dates = new String[100];
        int dateVal = 0;
        for (String date : dateMedRecMap.keySet())
        {
            int takenVal = 0;
            int missedVal = 0;
            //datesLabel.add(date);
            dates[dateVal]=date;
            List<MedicineRecord> medicineRecordList = dateMedRecMap.get(date);
            for (MedicineRecord medicineRecord : medicineRecordList)
            {
                Boolean taken = medicineRecord.isTaken();
                if (taken==true)
                {
                    takenVal++;
                }
                else
                {
                    missedVal++;
                }
            }
            barGroupTaken.add(new BarEntry(dateVal,takenVal));
            barGroupMissed.add(new BarEntry(dateVal,missedVal));
            dateVal++;
        }
        BarDataSet barDataSet1 = new BarDataSet(barGroupTaken,"Taken");
        barDataSet1.setColor(Color.GREEN);
        BarDataSet barDataSet2 = new BarDataSet(barGroupMissed,"Missed");
        barDataSet2.setColor(Color.RED);

        /*ArrayList<BarDataSet> dataSets = new ArrayList<>();
        dataSets.add(barDataSet1);
        dataSets.add(barDataSet2);*/

        BarData data = new BarData(barDataSet1,barDataSet2);
        barChart.setData(data);

        XAxis xAxis =  barChart.getXAxis();
        xAxis.setValueFormatter(new IndexAxisValueFormatter(dates));
        xAxis.setCenterAxisLabels(true);
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1);
        xAxis.setGranularityEnabled(true);

        barChart.setDragEnabled(true);
        barChart.setVisibleXRangeMaximum(7);

        float barSpace = 0.1f;
        float groupSpace = 0.5f;
        data.setBarWidth(0.15f);
        barChart.animateY(2500);
        barChart.getXAxis().setAxisMinimum(0);
        barChart.groupBars(0,groupSpace,barSpace);
        barChart.invalidate();

        barChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                String date = barChart.getXAxis().getValueFormatter().getFormattedValue((float) Math.floor(e.getX()),barChart.getXAxis());
                Log.d(TAG, "onValueSelected: " + date);
                Intent intent = new Intent(getContext(), MyDayActivity.class);
                intent.putExtra("MEDMAP",dateMedRecMap);
                intent.putExtra("DATE",date);
                startActivity(intent);
            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private void fetchMedicines() {
        medicationArrayList = new ArrayList<>();
        db.collection("alarms/" + patient.getId() + "/medication")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                           @Override
                                           public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                               if (task.isSuccessful()) {
                                                   for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                       Medication medication = snapshot.toObject(Medication.class);
                                                       Log.d(TAG, "onComplete: "+medication.toString());
                                                       medicationArrayList.add(medication);
                                                   }
                                               }
                                           }
                                       }

                )
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });
    }


    private void fetchMedicineRecords()
    {

        //medicationArrayList;
        final List<MedicineRecord> medicineRecordList = new ArrayList<>();

        DocumentReference documentReference = db.collection("patient_med_logs/").document("PT2001");
        documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot documentSnapshot = task.getResult();
                if (documentSnapshot.exists())
                {
                    String last = (String) documentSnapshot.getData().keySet().toArray()[documentSnapshot.getData().size()-1];

                    for (final String date : documentSnapshot.getData().keySet())
                    {
                        db.collection("patient_med_logs/" + "PT2001/" + date).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful())
                                {
                                    for (QueryDocumentSnapshot snapshot : task.getResult())
                                    {
                                        MedicineRecord medicineRecord = snapshot.toObject(MedicineRecord.class);
                                        medicineRecordList.add(medicineRecord);
                                    }

                                    dateMedRecMap.put(date, (List) ((ArrayList<MedicineRecord>) medicineRecordList).clone());
                                    try{
                                    Log.d(TAG, "onComplete1: "+dateMedRecMap.toString());}
                                    catch (Exception e){}
                                    medicineRecordList.clear();
                                }

                            }
                        });

                    }

                }

            }
        });
    }

    /*private void createTable()
    {
        String baseDir = android.os.Environment.getExternalStorageDirectory().getAbsolutePath();
        String fileName = "medication_record.csv";
        String filePath = baseDir + File.separator + fileName;
        File f = new File(filePath);
        CSVWriter writer = null;

        if (f.exists() && !f.isDirectory())
        {
            try {
                FileWriter mFileWriter = new FileWriter(filePath , false);
                writer = new CSVWriter(mFileWriter);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        else
        {
            try {
                writer = new CSVWriter(new FileWriter(filePath));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        List<String[]> data = new ArrayList<>();
        String[] row = new String[20];
        Log.d(TAG, "works");
        for (String date : dateMedRecMap.keySet())
        {
            int i =0;
            row[i]=date;
            i++;
            //Log.d(TAG, "createTable: "+date);
            List<MedicineRecord> medicineRecordList = dateMedRecMap.get(date);
            for (MedicineRecord medicineRecord : medicineRecordList)
            {
                String name = medicineRecord.getName();
                boolean taken = medicineRecord.isTaken();

                row[i] = name+": "+ Boolean.toString(taken);
                //Log.d(TAG, "createTable: "+name);
                i++;
            }
            Log.d(TAG, "Row0-"+row[0]);
            Log.d(TAG, "Row1-"+row[1]);
            data.add(row);
        }
        try
        {
            writer.writeAll(data);
            writer.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        *//*Intent i = new Intent();
        i.setAction(android.content.Intent.ACTION_VIEW);
        i.setDataAndType(Uri.fromFile(f), "text/csv");
        startActivity(i);*//*

    }*/
    public String getEmojiByUnicode(int unicode){
        return new String(Character.toChars(unicode));
    }
}
