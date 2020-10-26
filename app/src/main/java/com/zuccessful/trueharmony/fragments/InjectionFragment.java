package com.zuccessful.trueharmony.fragments;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddInjActivity;
import com.zuccessful.trueharmony.adapters.InjAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Injection;
import com.zuccessful.trueharmony.pojo.Medication;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class InjectionFragment extends Fragment {
    private SakshamApp app;
    private FirebaseFirestore db;
    private ArrayList<Injection> Injections;
    private LinearLayout addInjView;
    private RecyclerView InjListView;
    private TextView iBlankInjRecord;
    private InjAdapter adapter;
    private Context iContext;

    private static final int VEHICLE_LOADER = 0;
    private final String TAG = "InjectionTAG";

    public InjectionFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        iContext = getContext();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_injection_layout, container, false);

        addInjView = view.findViewById(R.id.add_inj_btn);
        InjListView = view.findViewById(R.id.inj_record_list);
        iBlankInjRecord = view.findViewById(R.id.blank_inj_record);


        adapter = new InjAdapter(Injections);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        InjListView.setLayoutManager(layoutManager);
        InjListView.setAdapter(adapter);

        addInjView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewInjRecord(view);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateInjRecords();
    }

    public void updateInjRecords() {
        Injections = new ArrayList<>();
        db.collection("alarms/" + app.getAppUser(null).getId() + "/injection/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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
                            if (Injections.size() > 0) {
                                adapter.replaceInjs(Injections);
                                iBlankInjRecord.setVisibility(View.GONE);
                                InjListView.setVisibility(View.VISIBLE);
                            } else {
                                iBlankInjRecord.setVisibility(View.VISIBLE);
                                InjListView.setVisibility(View.GONE);
                            }
//                            Toast.makeText(getContext(), "List: " + Medications, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addNewInjRecord(View view) {
        startActivity(new Intent(getContext(), AddInjActivity.class));
//        Toast.makeText(getContext(), "Add New Medicine Information", Toast.LENGTH_SHORT).show();
    }

}