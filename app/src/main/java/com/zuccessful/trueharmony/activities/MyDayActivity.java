package com.zuccessful.trueharmony.activities;

import android.content.res.TypedArray;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.MyDayFragment;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MyDayQuestions;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.internal.Util;

public class MyDayActivity extends AppCompatActivity implements MyDayFragment.OnFragmentInteractionListener {


    private SimpleDateFormat sdf;
    private FirebaseFirestore db;
    private SakshamApp app;
    Patient patient;
    String TAG = "TAG";
    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
    Date date = new Date();
    String current_date = dateFormat.format(date);


    Spinner ans1Spinner,ans2Spinner,ans3Spinner;





    String [] ques,ans1,ans2,ans3;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_day);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView q1TextView = (TextView) findViewById(R.id.myDayQuestion1);
        TextView q2TextView = (TextView) findViewById(R.id.myDayQuestion2);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.myday));
         ans1Spinner = (Spinner) findViewById(R.id.myDayAnswer1);
         ans2Spinner = (Spinner) findViewById(R.id.myDayAnswer2);


        ArrayList<String> quesArray = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.myDayQuestions)));
        ArrayList<String> ansArray1 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.myDayAns1)));
        ArrayList<String> ansArray2 = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.myDayAns2)));

        ques = (quesArray).toArray(new String[quesArray.size()]);
        ans1 = (ansArray1).toArray(new String[ansArray1.size()]);
        ans2 = (ansArray2).toArray(new String[ansArray2.size()]);

        q1TextView.setText(ques[0]);
        q2TextView.setText(ques[1]);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ans1);
        dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ans1Spinner.setAdapter(dataAdapter1);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ans2);
        dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        ans2Spinner.setAdapter(dataAdapter2);



        app = SakshamApp.getInstance();
        patient = app.getAppUser(null);
        db = app.getFirebaseDatabaseInstance();

        System.out.println(current_date);

        Button myDaySubmitButton = (Button) findViewById(R.id.myDaySubmit);
        myDaySubmitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String qAns1 = ans1Spinner.getSelectedItem().toString();
                final String qAns2 = ans2Spinner.getSelectedItem().toString();
                AsyncTask.execute(new Runnable() {
                    @Override
                    public void run() {
                        DatabaseRoom database = DatabaseRoom.getInstance(getApplicationContext());
                        Log.d("Logg", " " + qAns1 + " " + qAns2);
                        SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
                        String t_date = sdf.format(new Date());
                        // database.logRecords().deleteAll();
                        LogEntity ce = new LogEntity(t_date, qAns1, qAns2);
                        database.logRecords().addLogRecord(ce);
                        List<LogEntity> ret = database.logRecords().getLogRecords();
                        Log.d("Logg", " size of retrieved arraylist : " + ret.size());
                        //   writeToCSV(arr);

                    }
                });
                Toast.makeText(getBaseContext(), "Log Added ", Toast.LENGTH_LONG).show();


//
//                final Map<String, Object> test = new HashMap<>();
//                test.put("Answer1", qAns1);
//                test.put("Answer2", qAns2);
//
//                final MyDayQuestions myDayObj = new MyDayQuestions(patient.getId(),current_date, qAns1,qAns2);
//
//
//                Gson gson = new Gson();
//                //String value = gson.toJson(test);
//                String value = gson.toJson(myDayObj);
//
//
//                ArrayList <String> myDayList =  new ArrayList<>();
//
//                myDayList.add(value);
//                String restoredText = Utilities.getDataFromSharedpref(getApplicationContext(), "mydaylist");
//                myDayList.add(restoredText);
//
//
//                String update_value = gson.toJson(myDayList);
//                Utilities.saveDataInSharedpref(view.getContext().getApplicationContext(), "mydaylist", update_value);
//                Log.d("responsein submit", restoredText);


               // Utilities.saveDataInSharedpref(view.getContext().getApplicationContext(), "mydaylist", value);

//                db.collection("CaregiverMyday/" + patient.getId() + "/Date").document(current_date)
//                        .set(test)
//                        .addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void aVoid) {
//                                Log.d(TAG, "DocumentSnapshot successfully written!");
//                            }
//                        })
//                        .addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Log.w(TAG, "Error writing document", e);
//                            }
//                        });
//                Toast.makeText(getApplicationContext(),"My Day Submit",Toast.LENGTH_SHORT).show();



            }
        });





    }
    @Override
    public void onFragmentInteraction(Uri uri){
        //you can leave it empty
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
