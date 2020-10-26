package com.zuccessful.trueharmony.fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class IADLHomeFragment extends Fragment {
    private SakshamApp app;
    private FirebaseFirestore db;
    private Context mContext;
    private Patient patient;

    private static final int VEHICLE_LOADER = 0;

    private boolean[] checkedItems = new boolean[7];
    private String[] dailyTasks;
    private List<String> selectedDailyTasks = new ArrayList<>();

    private Button pickTasksButton;

    private SimpleDateFormat sdf;


    private void displayMultiTaskSelectDialog() {
        dailyTasks = getResources().getStringArray(R.array.daily_tasks);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        dialogBuilder.setTitle(R.string.select_daily_tasks);
        dialogBuilder.setMultiChoiceItems(dailyTasks, checkedItems, new DialogInterface.OnMultiChoiceClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                if (isChecked) {
                    selectedDailyTasks.add(dailyTasks[which]);
                } else {
                    selectedDailyTasks.remove(dailyTasks[which]);
                }
            }
        });

        dialogBuilder.setPositiveButton(R.string.done, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showSelectedTasks();
            }
        });
        dialogBuilder.create().show();
    }

    private void showSelectedTasks() {
        Toast.makeText(getContext(), selectedDailyTasks.toString(), Toast.LENGTH_SHORT).show();

        Map<String, Boolean> values = new HashMap<>();
            for(int index = 0; index < dailyTasks.length; index++){
                values.put(dailyTasks[index], selectedDailyTasks.contains(dailyTasks[index]));
            }

        db.collection("dailyTasks/" + patient.getId() + "/patient")
                .document(sdf.format(new Date()))
                .set(values, SetOptions.merge())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(mContext, "Successfully Uploaded Tasks Info!",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }


    public IADLHomeFragment() {
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
        View view = inflater.inflate(R.layout.fragment_iadlhome, container, false);

        pickTasksButton = (Button) view.findViewById(R.id.pickDailyTasks);
        patient = app.getAppUser(null);
        sdf = Utilities.getSimpleDateFormat();

        pickTasksButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayMultiTaskSelectDialog();
            }
        });


        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}
