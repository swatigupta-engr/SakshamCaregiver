package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.Med_Record_Adapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.DailyRoutineManageFrag;
import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.fragments.MedManageFragment;
import com.zuccessful.trueharmony.fragments.MedStatsFrag;
import com.zuccessful.trueharmony.fragments.MyDayFragment;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.fragments.ReportFragment;
import com.zuccessful.trueharmony.fragments.SarthiFrag;
import com.zuccessful.trueharmony.fragments.StatsFragment;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_CID;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;
import static com.zuccessful.trueharmony.receivers.AlarmActionReceiver.DIALOGE_MESSAGE;

public class Report extends AppCompatActivity {
    private List<MedicineRecord> medList = new ArrayList<>();
    private RecyclerView recyclerView;
    private Med_Record_Adapter mAdapter;
    private ArrayList<Medication> medicationArrayList;
    private SakshamApp app = SakshamApp.getInstance();
    private FirebaseFirestore db = app.getFirebaseDatabaseInstance();

        private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
                = new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                switch (item.getItemId()) {
                    case R.id.navigation_home:
//                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
                        ReportFragment reportFrag = new ReportFragment();
                        fragmentTransaction.replace(R.id.repframeLayout, reportFrag).commit();
                        return true;
                    case R.id.navigation_dashboard:
                        //putting extra info to fragment
//                        Bundle bundle = new Bundle();
//                        ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.daily_routine_que)));
//                        bundle.putStringArrayList("questions", questions);
//                        ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.daily_routine_ans)));
//                        bundle.putStringArrayList("answers", answers);
//
//                        //fragment
//                        InformationFragment infoFrag = new InformationFragment();
//                        infoFrag.setArguments(bundle);
//                        fragmentTransaction.replace(R.id.repframeLayout, infoFrag).commit();
                      /*  PdfRenderFragment p= new PdfRenderFragment();
                        Bundle bundle = new Bundle();
                        bundle.putString("filename","medical_adherence.pdf");
                        bundle.putString("filename_hindi","medical_adherence_hindi.pdf");

                        p.setArguments(bundle);
                        fragmentTransaction.replace(R.id.repframeLayout, p).commit();*/
                        Intent pIntent = new Intent(Report.this, PDFRenderActivity.class);
                        pIntent.putExtra("filename","medical_adherence.pdf");
                        pIntent.putExtra("filename_hindi","medical_adherence_hindi.pdf");

                        startActivity(pIntent);
                        return true;
                    case R.id.navigation_notifications:
                        Context context = SakshamApp.getInstance();
                        Intent dialogeActivityIntent = new Intent(context, DialogeActivity.class);
                        dialogeActivityIntent.putExtra(DIALOGE_MESSAGE, context.getString(R.string.motivational_quote));
                        context.startActivity(dialogeActivityIntent);
                 //   fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
                        return true;
                    case R.id.navigation_sarthi:
                        Bundle b1=new Bundle();
                        b1.putString("saarthi",getApplicationContext().getString(R.string.med_saarthi));
                        SarthiFrag f = new SarthiFrag();
                        f.setArguments(b1);
                        fragmentTransaction.replace(R.id.repframeLayout, f).commit();
                        return true;
                }
                return false;
            }
        };

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
          //  fetchData();

            Utilities.changeLanguage(this);
            setContentView(R.layout.activity_report);

            BottomNavigationView navigation = findViewById(R.id.navigation);
            navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            navigation.getMenu().getItem(2).setVisible(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.report));


            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            Log.d("logss","almost here");
            fragmentTransaction.replace(R.id.repframeLayout, new ReportFragment()).commit();
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

}
