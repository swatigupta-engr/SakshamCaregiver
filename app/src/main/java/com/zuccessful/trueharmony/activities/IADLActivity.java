package com.zuccessful.trueharmony.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.DailyRoutineManageFrag;
import com.zuccessful.trueharmony.fragments.IADLHomeFragment;
import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.fragments.SarthiFrag;

import java.util.ArrayList;
import java.util.Arrays;

public class IADLActivity extends AppCompatActivity {

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            switch (item.getItemId()) {
                case R.id.navigation_home:
//                    fragmentTransaction.replace(R.id.fragment_content, new MeasurementFragment()).commit();
                    IADLHomeFragment medFrag = new IADLHomeFragment();
                    fragmentTransaction.replace(R.id.fragment_content, medFrag).commit();
                    return true;
                case R.id.navigation_dashboard:
                    //putting extra info to fragment
                    Bundle bundle = new Bundle();
                    ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.iadl_que)));
                    bundle.putStringArrayList("questions", questions);
                    ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.iadl_ans)));
                    bundle.putStringArrayList("answers", answers);

                    //fragment
                    InformationFragment infoFrag = new InformationFragment();
                    infoFrag.setArguments(bundle);
                    fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();
                    return true;
                case R.id.navigation_notifications:
//                    fragmentTransaction.replace(R.id.fragment_content, new StatsFragment()).commit();
                    return true;
                case R.id.navigation_sarthi:
                    Bundle b1=new Bundle();
                    b1.putString("saarthi",getApplicationContext().getString(R.string.self_saarthi));
                    SarthiFrag f = new SarthiFrag();
                    f.setArguments(b1);
                    fragmentTransaction.replace(R.id.fragment_content, f).commit();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_iadl);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.IADL));

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragment_content, new IADLHomeFragment()).commit();
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
