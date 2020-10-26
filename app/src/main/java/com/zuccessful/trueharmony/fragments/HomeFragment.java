package com.zuccessful.trueharmony.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.zuccessful.trueharmony.activities.LibraryActivity;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.DailyRoutineActivity;
import com.zuccessful.trueharmony.activities.HealthMonitorActivity;
import com.zuccessful.trueharmony.activities.Injection_Schedule;
import com.zuccessful.trueharmony.activities.MedicalAdherenceActivity;
import com.zuccessful.trueharmony.activities.MyDayActivity;
import com.zuccessful.trueharmony.activities.PDFRenderActivity;
import com.zuccessful.trueharmony.activities.PSychoeducation;
import com.zuccessful.trueharmony.activities.Report;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper;

import static com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper.sendMapAnalytics;

public class HomeFragment extends Fragment {
    private LinearLayout mReportCard;
    private LinearLayout mMedAdherenceCard;
    private LinearLayout mDailyRoutineCard;
    private LinearLayout mIADLCard;
    private LinearLayout libraryCard;
    private LinearLayout mPsychoEduCard;
    private LinearLayout mMydayCard;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view =inflater.inflate(R.layout.fragment_home, container, false);
        mReportCard = view.findViewById(R.id.report_linear_layout);
        mReportCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getContext(), Report.class));
            }
        });

        libraryCard = view.findViewById(R.id.libray_linear_layout);
        libraryCard.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                startActivity(new Intent(getContext(), LibraryActivity.class));
            }
        });

        mMydayCard = view.findViewById(R.id.myday_linear_layout);
        mMydayCard.setOnClickListener(new View.OnClickListener(){

            public void onClick(View view){
                startActivity(new Intent(getContext(), MyDayActivity.class));
            }
        });

        mPsychoEduCard= view.findViewById(R.id.psycho_edu_linear_layout);
        mPsychoEduCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getContext(), PSychoeducation.class));

                Intent pIntent = new Intent(getContext(), PDFRenderActivity.class);
                pIntent.putExtra("filename","about_illness.pdf");
                pIntent.putExtra("filename_hindi","health_monitoring_hindi.pdf");

                startActivity(pIntent);
                sendMapAnalytics(FirebaseAnalyticsHelper.FirebaseAnalyConst.ACTIVITY_NAME,
                        getString(R.string.psycho_education));

            }
        });

        mDailyRoutineCard= view.findViewById(R.id.daily_linear_layout);
        mDailyRoutineCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
             //   startActivity(new Intent(getContext(), DailyRoutineActivity.class));
                startActivity(new Intent(getContext(), DailyRoutineActivity.class));

                sendMapAnalytics(FirebaseAnalyticsHelper.FirebaseAnalyConst.ACTIVITY_NAME,
                        getString(R.string.psycho_education));

            }
        });

        return view;
    }




}
