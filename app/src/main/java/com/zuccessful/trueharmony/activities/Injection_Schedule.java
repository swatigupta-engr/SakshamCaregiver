package com.zuccessful.trueharmony.activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.fragments.InjectionFragment;
import com.zuccessful.trueharmony.fragments.MeasurementFragment;
import com.zuccessful.trueharmony.fragments.MedManageFragment;
import com.zuccessful.trueharmony.fragments.StatsFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class Injection_Schedule extends AppCompatActivity{
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
                    InjectionFragment injFrag = new InjectionFragment();
                    fragmentTransaction.replace(R.id.fragment_content, injFrag).commit();
                    return true;
                case R.id.navigation_dashboard:
                    //putting extra info to fragment
                    Bundle bundle = new Bundle();
                    ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_que)));
                    bundle.putStringArrayList("questions", questions);
                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.med_adh_ans)));
                    bundle.putStringArrayList("answers", answers);

                    //fragment
                    InformationFragment infoFrag = new InformationFragment();
                    infoFrag.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();
                    return true;
                case R.id.navigation_notifications:
//                    fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_injection_schedule);
        BottomNavigationView navigation = findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, new InjectionFragment()).commit();
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