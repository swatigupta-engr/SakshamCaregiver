package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.wajahatkarim3.longimagecamera.LongImageCameraActivity;
import com.wajahatkarim3.longimagecamera.PreviewLongImageActivity;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.LabInvestigation;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.CustomImageCamera;
import com.zuccessful.trueharmony.utilities.Utilities;

import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static android.app.Activity.RESULT_OK;
import static com.zuccessful.trueharmony.utilities.CustomImageCamera.REPORT_NAME;


public class MeasurementFragment extends Fragment {

    private static final String TAG = MeasurementFragment.class.getSimpleName();
    private EditText heightEditText, weightEditText, otherEditText, lipidValueEt, bloodSugarEt, tshEt, waistEt, bloodPressureEt;
    private ProgressBar loadStateProgressBar;
    private LinearLayout testView;
    private TextView testUnitView, bmiTv, lipidMsgTv, bloodSugarMsgTv, tshMsgTv, bpMsgTv;
    private Spinner testSpinner;
    private Button submitCommon, submitOther, submitBloodTestButton, addReportsButton;
    private SakshamApp app;
    private Patient patient;
    private FirebaseFirestore db;
    private SimpleDateFormat sdf;
    private ArrayList<LabInvestigation> labInvestigations;
    private ArrayAdapter<String> spinnerAdapter;
    private Context mContext;
    DecimalFormat decimalFormat = new DecimalFormat(".##");

    private Map<String, String> reportMap;

    private static final int NORMAL_BMI_LOWER = 32;
    private static final int NORMAL_BMI_UPPER = 32;
    private static final int HIGH_BMI = 32;
    private static final int LOW_BMI = 32;


    private static final int NORMAL_BP_LOWER = 32;
    private static final int NORMAL_BP_UPPER = 32;

    private static final int NORMAL_LIPID_LOWER = 32;
    private static final int NORMAL_LIPID_UPPER = 32;

    private static final int NORMAL_DIABETES_LOWER = 32;
    private static final int NORMAL_DIABETES_UPPER = 32;

    private static final int NORMAL_TSH_LOWER = 32;
    private static final int NORMAL_TSH_UPPER = 32;

    private static final int NORMAL_BLOOD_SUGAR_LOWER = 32;
    private static final int NORMAL_BLOOD_SUGAR_UPPER = 32;
    private LinearLayout phyActLinearLayout;


    public MeasurementFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_measurement, container, false);
        mContext = getContext();
        labInvestigations = new ArrayList<>();
        app = SakshamApp.getInstance();
        patient = app.getAppUser(null);
        db = app.getFirebaseDatabaseInstance();
        heightEditText = view.findViewById(R.id.height_value);
        bmiTv = view.findViewById(R.id.bmi_tv);
        bpMsgTv = view.findViewById(R.id.bp_msg_tv);
        weightEditText = view.findViewById(R.id.weight_value);
        otherEditText = view.findViewById(R.id.test_value);

        bloodSugarEt = view.findViewById(R.id.blood_sugar_value);
        tshEt = view.findViewById(R.id.tsh_value);
        lipidValueEt = view.findViewById(R.id.lipid_value);

        waistEt = view.findViewById(R.id.waist_value);
        bloodPressureEt = view.findViewById(R.id.bp_value);

        loadStateProgressBar = view.findViewById(R.id.other_test_load_state);
        testView = view.findViewById(R.id.test_view);
        testSpinner = view.findViewById(R.id.test_type);

        testUnitView = view.findViewById(R.id.test_unit);
        submitCommon = view.findViewById(R.id.submit_common_values);
        submitOther = view.findViewById(R.id.submit_other_value);
        submitBloodTestButton = view.findViewById(R.id.button_submit_blood_test);
        addReportsButton = view.findViewById(R.id.add_reports_button);

        lipidMsgTv = view.findViewById(R.id.lipid_msg_tv);
        tshMsgTv = view.findViewById(R.id.tsh_msg_tv);
        bloodSugarMsgTv = view.findViewById(R.id.blood_sugar_msg_tv);

        phyActLinearLayout = view.findViewById(R.id.phy_act_linear_layout);

        reportMap = loadMap();
        if(reportMap==null){
            reportMap = new HashMap<>();
        }


        String height = Utilities.getDataFromSharedpref(getContext(), Utilities.KEY_HEIGHT);
        String weight = Utilities.getDataFromSharedpref(getContext(), Utilities.KEY_WEIGHT);

        if (height != null) heightEditText.setText(height);
        if (weight != null) weightEditText.setText(weight);
        Double bmi = getbmi(weight, height);
        if (bmi != null) {
            bmiTv.setText("BMI : " + decimalFormat.format(bmi));
        }

        sdf = Utilities.getSimpleDateFormat();

        submitCommon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "Clicked Common Button");
                final String heightStr = heightEditText.getText().toString();
                final String weightStr = weightEditText.getText().toString();
                final String waistStr = waistEt.getText().toString();
                final String bloodPressuretStr = bloodPressureEt.getText().toString();

                if (TextUtils.isEmpty(heightStr)) {
                    heightEditText.setError("Enter Valid Value");
                }

                if (TextUtils.isEmpty(weightStr)) {
                    weightEditText.setError("Enter Valid Value");
                }

                if (TextUtils.isEmpty(waistStr)) {
                    waistEt.setError("Enter Valid Value");
                }

                if (TextUtils.isEmpty(bloodPressuretStr)) {
                    bloodSugarEt.setError("Enter Valid Value");
                }

                try {

                    double height = Double.parseDouble(heightStr);
                    double weight = Double.parseDouble(weightStr);


                    if (height > 0.0 && weight > 0.0) {
                        Map<String, Double> values = new HashMap<>();
                        values.put("height", height);
                        values.put("weight", weight);

                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                                set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();

                            }
                        });

                        Double newBmi = getbmi(weightStr, heightStr);
                        if (newBmi != null) {
                            bmiTv.setVisibility(View.VISIBLE);
                            bmiTv.setText("BMI : " + decimalFormat.format(newBmi));
                        }
                    } else {
                        Toast.makeText(mContext, "Enter a valid value", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }


                try {
                    double bloodPressure = Double.parseDouble(bloodPressuretStr);

                    if (bloodPressure > 0.0) {
                        Map<String, Double> values = new HashMap<>();
                        values.put("bloodPressure", bloodPressure);

                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                                set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();

                            }
                        });

                        if (bloodPressure < NORMAL_BP_LOWER) {
                            bpMsgTv.setVisibility(View.VISIBLE);
                            bpMsgTv.setText("Your BP in lower than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else if (bloodPressure > NORMAL_BP_UPPER) {
                            bpMsgTv.setVisibility(View.VISIBLE);
                            bpMsgTv.setText("Your BP in higher than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else {
                            bpMsgTv.setVisibility(View.VISIBLE);
                            bpMsgTv.setText("Yayy! Your BP in normal!");
                        }
                    } else {
                        bloodPressureEt.setError("Enter valid value");
                    }
                } catch (Exception e) {
                    bloodPressureEt.setError("Enter valid value");

                }

                try {
                    double waist = Double.parseDouble(waistStr);

                    if (waist > 0.0) {
                        Map<String, Double> values = new HashMap<>();
                        values.put("waist", waist);

                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                                set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } else {
                        waistEt.setError("Enter valid value");
                    }


//                heightEditText.setText("");
//                weightEditText.setText("");
                } catch (Exception e) {

                }
            }
        });


        submitBloodTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG, "Clicked Common Button");
                final String lipidStr = lipidValueEt.getText().toString();
                final String tshStr = tshEt.getText().toString();
                final String bloodSugarStr = bloodSugarEt.getText().toString();

                if (TextUtils.isEmpty(lipidStr)) {
                    lipidValueEt.setError("Enter Valid Value");
                } else {

                    try {
                        double lipid = Double.parseDouble(lipidStr);

                        if (lipid < NORMAL_LIPID_LOWER) {
                            lipidMsgTv.setVisibility(View.VISIBLE);
                            lipidMsgTv.setText("Your lipid in lower than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else if (lipid > NORMAL_LIPID_UPPER) {
                            lipidMsgTv.setVisibility(View.VISIBLE);
                            lipidMsgTv.setText("Your lipid in higher than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else {
                            lipidMsgTv.setVisibility(View.VISIBLE);
                            lipidMsgTv.setText("Yayy! Your lipid in normal!");
                        }

                        Map<String, Double> values = new HashMap<>();
                        values.put("lipid", lipid);

                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                                set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } catch (Exception e) {
                        lipidValueEt.setError("Enter Valid Value");
                    }


                }

                if (TextUtils.isEmpty(tshStr)) {
                    tshEt.setError("Enter Valid Value");
                } else {
                    try {
                        double tsh = Double.parseDouble(tshStr);

                        if (tsh < NORMAL_TSH_LOWER) {
                            tshMsgTv.setVisibility(View.VISIBLE);
                            tshMsgTv.setText("Your tsh in lower than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else if (tsh > NORMAL_TSH_UPPER) {
                            tshMsgTv.setVisibility(View.VISIBLE);
                            tshMsgTv.setText("Your tsh in higher than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else {
                            tshMsgTv.setVisibility(View.VISIBLE);
                            tshMsgTv.setText("Yayy! Your tsh in normal!");
                        }

                        Map<String, Double> values = new HashMap<>();
                        values.put("tsh", tsh);

                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                                set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } catch (Exception e) {
                        tshEt.setError("Enter Valid Value");
                    }
                }

                if (TextUtils.isEmpty(bloodSugarStr)) {
                    bloodSugarEt.setError("Enter Valid Value");
                } else {
                    try {
                        double bloodSugar = Double.parseDouble(bloodSugarStr);

                        if (bloodSugar < NORMAL_BLOOD_SUGAR_LOWER) {
                            bloodSugarMsgTv.setVisibility(View.VISIBLE);
                            bloodSugarMsgTv.setText("Your blood sugar in lower than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else if (bloodSugar > NORMAL_BLOOD_SUGAR_UPPER) {
                            bloodSugarMsgTv.setVisibility(View.VISIBLE);
                            bloodSugarMsgTv.setText("Your blood sugar in higher than normal!"
                                    + "\n It is important to share this with your doctor so that you can take a timely action.");
                        } else {
                            bloodSugarMsgTv.setVisibility(View.VISIBLE);
                            bloodSugarMsgTv.setText("Yayy! Your blood sugar in normal!");
                        }

                        Map<String, Double> values = new HashMap<>();
                        values.put("bloodSugar", bloodSugar);

                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date())).
                                set(values, SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(mContext, "Successfully Uploaded Height and Weight.", Toast.LENGTH_SHORT).show();

                            }
                        });

                    } catch (Exception e) {
                        bloodSugarMsgTv.setError("Enter Valid Value");
                    }
                }


            }
        });


        db.collection("lab_investigations").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    // TODO: Make a custom adapter to simplify this process
                    final ArrayList<String> testNames = new ArrayList<>();
                    for (QueryDocumentSnapshot snapshot : task.getResult()) {
                        LabInvestigation l = snapshot.toObject(LabInvestigation.class);
                        l.setId(snapshot.getId());
                        testNames.add(l.getName());
                        labInvestigations.add(l);
                    }

                    spinnerAdapter = new ArrayAdapter<>(mContext, android.R.layout.simple_spinner_item, testNames);
                    spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    testSpinner.setAdapter(spinnerAdapter);

                    loadStateProgressBar.setVisibility(View.GONE);
                    testView.setVisibility(View.VISIBLE);
                }
            }
        });

        testSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                testUnitView.setText(labInvestigations.get(i).getUnit());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        submitOther.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int i = testSpinner.getSelectedItemPosition();
                String valueStr = otherEditText.getText().toString();
                if (valueStr.equals("")) {
                    Toast.makeText(mContext, "Enter some value before submitting!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                try {
                    Double value = Double.valueOf(valueStr);
                    if (value >= 0.0) {
                        Map<String, Double> data = new HashMap<>();
                        data.put(labInvestigations.get(i).getId(), value);
                        db.collection("records/" + patient.getId() + "/patient").document(sdf.format(new Date()))
                                .set(data, SetOptions.merge())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Toast.makeText(mContext, "Successfully Uploaded Test.", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    } else {
                        Toast.makeText(mContext, "Enter a valid input!", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Toast.makeText(mContext, "Enter a valid input!", Toast.LENGTH_SHORT).show();
                }
                otherEditText.setText("");
            }
        });


        addReportsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createSelectReportTypeDialog(getResources().getStringArray(R.array.report_types));
            }
        });

        for (Map.Entry<String,String> entry : reportMap.entrySet()) {
            addtoPhysicalActivityLayout(entry.getKey());
        }

        return view;
    }

    private void createSelectReportTypeDialog(final String[] phyActChoices) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Pick report type");
        builder.setItems(phyActChoices, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(getContext(), "Choice  : " + phyActChoices[which], Toast.LENGTH_SHORT).show();

                CustomImageCamera.launch(getActivity(),phyActChoices[which]);

                addtoPhysicalActivityLayout(phyActChoices[which]);

            }
        });
        builder.show();
    }

    private void addtoPhysicalActivityLayout(final String activity) {

        LayoutInflater inflater = LayoutInflater.from(getActivity());

        View view = inflater.inflate(R.layout.activity_user_profile, null, false);

        final View inflatedLayout = inflater.inflate(R.layout.textview_with_delete, (ViewGroup) view, false);
        ((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv)).setText(activity);
        ((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (TextUtils.isEmpty(reportMap.get(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv)).getText().toString()))) {
                    Intent intent = new Intent();
                    intent.setAction(android.content.Intent.ACTION_VIEW);
                    intent.setType("image/*");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } else {
                    Intent ii = new Intent(getActivity(), PreviewLongImageActivity.class);
                    ii.putExtra("imageName",
                            reportMap.get(((TextView) inflatedLayout.findViewById(R.id.physical_activity_tv)).getText().toString()));
                    saveMap(reportMap);
                    startActivity(ii);
                }
            }
        });
        (inflatedLayout.findViewById(R.id.delete_physical_activity_button)).setVisibility(View.GONE);

        phyActLinearLayout.addView(inflatedLayout);

    }

    private Double getbmi(String weight, String height) {
        if (height == null || weight == null) return null;
        try {
            return (Double.parseDouble(weight) / Math.pow(Double.parseDouble(height), 2)) * 10000;
        } catch (Exception e) {
            return null;
        }
    }

    String imageFileName;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == LongImageCameraActivity.LONG_IMAGE_RESULT_CODE && data != null) {
            imageFileName = data.getStringExtra(LongImageCameraActivity.IMAGE_PATH_KEY);
            reportMap.put(data.getStringExtra(REPORT_NAME),imageFileName);
            Log.e(TAG, "onActivityResult: " + imageFileName);
        }
    }

    private void saveMap(Map<String,String> inputMap){
        SharedPreferences pSharedPref = getContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        if (pSharedPref != null){
            JSONObject jsonObject = new JSONObject(inputMap);
            String jsonString = jsonObject.toString();
            SharedPreferences.Editor editor = pSharedPref.edit();
            editor.remove("My_map").commit();
            editor.putString("My_map", jsonString);
            editor.commit();
        }
    }

    private Map<String,String> loadMap(){
        Map<String,String> outputMap = new HashMap<String,String>();
        SharedPreferences pSharedPref = getContext().getSharedPreferences("MyVariables", Context.MODE_PRIVATE);
        try{
            if (pSharedPref != null){
                String jsonString = pSharedPref.getString("My_map", (new JSONObject()).toString());
                JSONObject jsonObject = new JSONObject(jsonString);
                Iterator<String> keysItr = jsonObject.keys();
                while(keysItr.hasNext()) {
                    String key = keysItr.next();
                    String value = (String) jsonObject.get(key);
                    outputMap.put(key, value);
                }
            }
        }catch(Exception e){
            e.printStackTrace();
        }
        return outputMap;
    }

}
