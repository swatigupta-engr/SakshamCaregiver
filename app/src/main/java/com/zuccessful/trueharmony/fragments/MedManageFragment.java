package com.zuccessful.trueharmony.fragments;


import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.widget.DividerItemDecoration;
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
import com.zuccessful.trueharmony.activities.AddMedRecActivity;
import com.zuccessful.trueharmony.adapters.MedsAdapter;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class MedManageFragment extends Fragment {

    private SakshamApp app;
    private FirebaseFirestore db;
    private ArrayList<Medication> medications;
    private LinearLayout addMedView;
    private RecyclerView mMedListView;
    private TextView mBlankMedRecord;
    private MedsAdapter adapter;
    private Context mContext;

    private static final int VEHICLE_LOADER = 0;


    public MedManageFragment() {
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
        View view = inflater.inflate(R.layout.fragment_med_manage, container, false);

        addMedView = view.findViewById(R.id.add_med_btn);
        mMedListView = view.findViewById(R.id.med_record_list);
        mBlankMedRecord = view.findViewById(R.id.blank_med_record);


        adapter = new MedsAdapter(medications);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        mMedListView.setLayoutManager(layoutManager);
        mMedListView.setAdapter(adapter);

        addMedView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMedRecord(view);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        updateMedRecords();
    }

    public void updateMedRecords() {
        medications = new ArrayList<>();
        db.collection("alarms/" + app.getAppUser(null).getId() + "/medication/")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (!queryDocumentSnapshots.isEmpty()) {
//                            List<Medication> m = queryDocumentSnapshots.toObjects(Medication.class);
                            for (DocumentSnapshot ds : queryDocumentSnapshots.getDocuments()) {
                                Medication m = ds.toObject(Medication.class);
                                medications.add(m);
                            }
                            if (medications.size() > 0) {
                                adapter.replaceMeds(medications);
                                mBlankMedRecord.setVisibility(View.GONE);
                                mMedListView.setVisibility(View.VISIBLE);
                            } else {
                                mBlankMedRecord.setVisibility(View.VISIBLE);
                                mMedListView.setVisibility(View.GONE);
                            }
//                            Toast.makeText(getContext(), "List: " + medications, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void addNewMedRecord(View view) {
        startActivity(new Intent(getContext(), AddMedRecActivity.class));
//        Toast.makeText(getContext(), "Add New Medicine Information", Toast.LENGTH_SHORT).show();
    }
}
