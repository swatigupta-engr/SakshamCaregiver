package com.zuccessful.trueharmony.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.utilities.CustomSpinner;
import com.zuccessful.trueharmony.utilities.Utilities;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 9220;
    private static final String TAG = LoginActivity.class.getSimpleName();
    public static final String PREF_PID = "patient_id";
    public static final String PREF_CID = "caregiver_id";
    private String patientId;
    private String caregiverId;
    private SakshamApp app;
    private FirebaseAuth mAuth;
    private EditText patientIdEt, passworEt, caregiverIdEt;
    private ProgressDialog progressDialog;
    private AnimationDrawable animationDrawable;
    private ConstraintLayout constraintLayout;
    private SharedPreferences preferences;
    private static FirebaseFirestore db;
    Button signInButton;
    private Button registerButton;
    int lang_code=1;
    private CustomSpinner languagePrefSpinner;
//SharedPreferences sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
    GoogleSignInClient mGoogleSignInClient;
    Button change_lang;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);





         preferences = this.getSharedPreferences(PREF_PID, MODE_PRIVATE);
        preferences = this.getSharedPreferences(PREF_CID, MODE_PRIVATE);
        patientId = preferences.getString(PREF_PID, null);
        caregiverId = preferences.getString(PREF_CID, null);

        languagePrefSpinner = (CustomSpinner)findViewById( R.id.language_spinner );

        change_lang= findViewById(R.id.lang);

        app = SakshamApp.getInstance();

        app.setPatientID(patientId);

        constraintLayout = findViewById(R.id.linear_layout);
        animationDrawable = (AnimationDrawable) constraintLayout.getBackground();
        animationDrawable.setEnterFadeDuration(5000);
        animationDrawable.setExitFadeDuration(2000);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Signing In...");



        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);
        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(),RegisterActivity.class);
                startActivity(intent);
            }
        });
        patientIdEt = findViewById(R.id.patient_id_et);
        caregiverIdEt = findViewById(R.id.caregiver_id_et);

        passworEt = findViewById(R.id.password_et);

        if (patientId != null) {
            signIn();
            return;
        }

        languagePrefSpinner.setOnItemSelectedListener(new CustomSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id, boolean userSelected) {
                if(userSelected) {

                    Utilities.saveDataInSharedpref(getApplicationContext(), Utilities.KEY_LANGUAGE_PREF,
                            String.valueOf(position));

                    lang_code=position;

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        change_lang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {



                Utilities.saveDataInSharedpref(getApplicationContext(), Utilities.KEY_LANGUAGE_PREF,
                        String.valueOf(lang_code));
                changeLanguage(LoginActivity.this);

                recreate();


            }
        });


    }

    private void signIn() {


        updateUI(null);
    }



    @Override
    protected void onResume() {
        super.onResume();
        animationDrawable.start();
    }

    private void updateUI(FirebaseUser currentUser) {
        progressDialog.dismiss();
//        Log.d(TAG, patientId);
        if (
//                currentUser != null &&
                patientId != null) {
            Patient p = new Patient(patientId, patientId, patientId, caregiverId);
            app.getAppUser(p);


            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_TASK_ON_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }
    }


    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.sign_in_button) {

            final String password = passworEt.getText().toString();
            patientId = patientIdEt.getText().toString().toUpperCase();
            caregiverId = caregiverIdEt.getText().toString().toUpperCase();
            progressDialog.show();
            mAuth.signInWithEmailAndPassword(caregiverId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,task.getException().getMessage(),Toast.LENGTH_LONG).show();

                    }
                    else
                    {
                        preferences.edit().putString(PREF_PID, patientId).apply();
                        preferences.edit().putString(PREF_CID, caregiverId).apply();


                        app.setPatientID(patientId);
                        Toast.makeText(LoginActivity.this,"Signed In",Toast.LENGTH_LONG).show();
                        signIn();
                    }
                }
            });

        }

    }

}