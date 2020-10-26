package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.adapters.DailyRoutineTaskAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;

import java.util.ArrayList;

public class DailyRoutineManageFrag extends Fragment {

    private SakshamApp app;
    private FirebaseFirestore db;
    private Context mContext;

    private static final int VEHICLE_LOADER = 0;

    private ArrayList<DailyRoutine> dailyRoutines = new ArrayList<>();
    private RecyclerView mListView;
    private DailyRoutineTaskAdapter adapter;

    public DailyRoutineManageFrag() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_daily_routine_manage, container, false);

        mListView = view.findViewById(R.id.daily_rout_record_list);
        adapter = new DailyRoutineTaskAdapter(getContext(), dailyRoutines);




        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mListView.addItemDecoration(new DividerItemDecoration(getContext(), layoutManager.getOrientation()));
        mListView.setLayoutManager(layoutManager);
        mListView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateTaskRecords();
    }

    private void updateTaskRecords() {
        dailyRoutines = new ArrayList<>();

        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.brush)));
        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.walk)));
        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.wake)));
        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.bath)));
        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.sleep)));
        dailyRoutines.add(new DailyRoutine(getResources().getString(R.string.meal)));

        adapter.replaceRouts(dailyRoutines);

        db.collection("alarms/" + app.getAppUser(null).getId() + "/daily_routine/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        Log.i("DailyRoutine", "in on success get dailt tasks");
                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<Medication> m = queryDocumentSnapshots.toObjects(Medication.class);
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                DailyRoutine m = ds.toObject(DailyRoutine.class);
                                updateTaskAddView(m);
                            }
                            Log.i("DailyRoutine", "DAILY SZE = " + dailyRoutines.size());

                            adapter.replaceRouts(dailyRoutines);
                        }
                    }
                });
    }

    private void updateTaskAddView(DailyRoutine routine) {
        if (getResources().getString(R.string.brush).equals(routine.getName())) {
            dailyRoutines.remove(0);
            dailyRoutines.add(0, routine);
        }
        if (getResources().getString(R.string.walk).equals(routine.getName())) {
            dailyRoutines.remove(1);
            dailyRoutines.add(1, routine);
        }
        if (getResources().getString(R.string.wake).equals(routine.getName())) {
            dailyRoutines.remove(2);
            dailyRoutines.add(2, routine);
        }
        if (getResources().getString(R.string.bath).equals(routine.getName())) {
            dailyRoutines.remove(3);
            dailyRoutines.add(3, routine);
        }
        if (getResources().getString(R.string.sleep).equals(routine.getName())) {
            dailyRoutines.remove(4);
            dailyRoutines.add(4, routine);
        }
        if (getResources().getString(R.string.meal).equals(routine.getName())) {
            dailyRoutines.remove(5);
            dailyRoutines.add(5, routine);
        }
    }

}
