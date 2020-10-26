package com.zuccessful.trueharmony.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.fragments.MeasurementFragment;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.StatsFragment;

import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class HealthMonitorActivity extends AppCompatActivity {

    FragmentManager fragmentManager;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
                    return true;
                case R.id.navigation_dashboard:
                    //putting extra info to fragment
                    Bundle bundle = new Bundle();
                    ArrayList<String> questions= new ArrayList<>();
                    questions.addAll(Arrays.asList( getResources().getStringArray(R.array.questions)));
                    bundle.putStringArrayList("questions",questions);
                    ArrayList<String> answers= new ArrayList<>();
                    answers.addAll(Arrays.asList( getResources().getStringArray(R.array.answers)));
                    bundle.putStringArrayList("answers", answers);

                    //fragment
                    InformationFragment infoFrag = new InformationFragment();
                    infoFrag.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();
                    return true;
                case R.id.navigation_notifications:
                    fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_monitor_tabbed);
        getSupportActionBar().setTitle(getResources().getString(R.string.title_activity_health_monitor_tabbed));

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment(),"measurement_frag_tag").commit();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Fragment fragment = fragmentManager.findFragmentByTag("measurement_frag_tag");
        if(fragment==null){
            Toast.makeText(this, "fragment is null", Toast.LENGTH_SHORT).show();
        }else {
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

}
