package com.zuccessful.trueharmony.activities;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.receivers.ReminderReceiver;
import com.zuccessful.trueharmony.services.NotificationScheduler;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.util.Calendar;
import java.util.Random;


public class AddInjActivity extends AppCompatActivity {


    private Spinner injectionSpinner,repeatSpinner;
    private String  injectionSelected,repeatSelected;
    private LinearLayout injTimeLayout;
    private SakshamApp app;
    private FirebaseFirestore db;
    private final String TAG = "ADDINJ_TAG";


    private static String year, month, day;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_inj_sch);

        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();

        injTimeLayout = findViewById(R.id.inj_time_pref);

        injectionSpinner = findViewById(R.id.inj_name_et);
        repeatSpinner = findViewById(R.id.spinner_repeat);


        final Calendar c = Calendar.getInstance();
        year = Integer.toString(c.get(Calendar.YEAR));
        month = Integer.toString(c.get(Calendar.MONTH));
        day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.injectionList,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        injectionSpinner.setAdapter(adapter);
        Intent intent =  getIntent();
        String name = intent.getStringExtra("editName");
        if (name!=null)
        {
            int spinnerPosition = adapter.getPosition(name);
            injectionSpinner.setSelection(spinnerPosition);
        }

        injectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                injectionSelected = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        repeatSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                repeatSelected = adapterView.getItemAtPosition(i).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }

    public void setDate(View view)
    {
        DialogFragment newFragment = new DateFrag();
        newFragment.show(getFragmentManager(),"date picker");
    }




    public void submitInj(View view) {
        String name = injectionSelected;
        String repeated = repeatSelected;
        Calendar calendar = Calendar.getInstance();

//        Calendar setcalendar = Calendar.getInstance();
//        setcalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day));
//        setcalendar.set(Calendar.MONTH, Integer.parseInt(month));
//        setcalendar.set(Calendar.YEAR, Integer.parseInt(year));
//
//        if(setcalendar.before(calendar) || setcalendar.getTimeInMillis()==calendar.getTimeInMillis()){
//            if(repeated.equals("Weekly"))
//            {
//                while(setcalendar.before(calendar))
//                    setcalendar.add(Calendar.DAY_OF_YEAR,7);
//            }
//            else if(repeated.equals("Fortnight"))
//            {
//                while(setcalendar.before(calendar))
//                    setcalendar.add(Calendar.DAY_OF_YEAR,14);
//            }
//
//            else if(repeated.equals("Once in 3 weeks"))
//            {
//                while(setcalendar.before(calendar))
//                    setcalendar.add(Calendar.DAY_OF_YEAR,21);
//            }
//
//            else if(repeated.equals("Monthly"))
//            {
//                while(setcalendar.before(calendar))
//                    setcalendar.add(Calendar.DAY_OF_YEAR,30);
//            }
//
//        }
//        day = Integer.toString(setcalendar.DAY_OF_MONTH);
//        month = Integer.toString(setcalendar.MONTH);
//        year = Integer.toString(setcalendar.YEAR);
        Toast.makeText(AddInjActivity.this,name+" Injection Submitted", Toast.LENGTH_SHORT).show();
        setInjAlarm(name,repeated);
        finish();
    }


    private void setInjAlarm(String name, String repeated) {

        Random rand = new Random();
        Injection injectionObj = new Injection(AddInjActivity.this);
        injectionObj.setName(name);
        injectionObj.setId(name);
        injectionObj.setDay(day);
        injectionObj.setMonth(month);
        injectionObj.setYear(year);
        Log.d(TAG, "setInjAlarm: "+injectionObj.getDay()+injectionObj.getMonth()+injectionObj.getYear());
        injectionObj.setRepeated(repeated);
        injectionObj.setStatus("Not yet");
        //reqcode set
        int reqcode = (int) System.currentTimeMillis();
        injectionObj.setReqCode(String.valueOf(reqcode));
        if(repeated.equals("Weekly"))
        {
            injectionObj.setHour(Integer.toString(11));
            injectionObj.setMin(Integer.toString(7));
        }
        else
        {
            injectionObj.setHour(Integer.toString(11));
            injectionObj.setMin(Integer.toString(8));
        }
        injectionObj.setTitle(name + " Injection Day!");
        injectionObj.setContent("Have you taken it?");
        injectionObj.setType("two");

        Intent mIntent = new Intent(AddInjActivity.this, ReminderReceiver.class);
        Utilities.setExtraForIntent(mIntent, "injRaw", injectionObj);
        DocumentReference documentReference = db.collection("alarms/" +
                app.getAppUser(null).getId() + "/injection").document(name);
        documentReference.set(injectionObj);
        scheduleAlarm(injectionObj);
    }

    private void scheduleAlarm(Injection localData) {
        int h = Integer.parseInt(localData.getHour());
        int m = Integer.parseInt(localData.getMin());
        int mn = Integer.parseInt(localData.getMonth());
        int y = Integer.parseInt(localData.getYear());
        int d = Integer.parseInt(localData.getDay());
        Log.d("Hour Set",Integer.toString(h));
        Log.d("Minute Set",Integer.toString(m));
        Log.d("Title Set",localData.getTitle());
        //Log.d("ReqCode",Integer.toString(localData.get_reqcode()));
        NotificationScheduler.setReminder(AddInjActivity.this,
                ReminderReceiver.class,
                h,
                m,
                d,
                mn,
                y,
                localData.getRepeated(),
                Integer.parseInt(localData.getReqCode()));
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class DateFrag extends DialogFragment
    {
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            int y = Integer.parseInt(year);
            int m = Integer.parseInt(month);
            int d = Integer.parseInt(day);
            return new DatePickerDialog(getActivity(), dateSetListener, y, m, d);
        }
        private DatePickerDialog.OnDateSetListener dateSetListener =
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker view,int y,int m,int d) {

                        year = Integer.toString(y);
                        month = Integer.toString(m+1);
                        day = Integer.toString(d);
                        Toast.makeText(getActivity(), "selected date is " + day +
                                " / " + month +
                                " / " + year, Toast.LENGTH_SHORT).show();
                    }
                };
    }
}