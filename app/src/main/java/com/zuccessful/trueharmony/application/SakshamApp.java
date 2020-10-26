package com.zuccessful.trueharmony.application;

import android.app.Application;

import com.google.firebase.FirebaseApp;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.zuccessful.trueharmony.pojo.Caregiver;
import com.zuccessful.trueharmony.pojo.Patient;

public class SakshamApp extends Application {
    private static SakshamApp instance;
    private static FirebaseFirestore db;
    private static Patient patient;
    private static Caregiver caregiver;
    private FirebaseAnalytics mFirebaseAnalytics;
    private static String patientID;
    private static String caregiverID;



    synchronized public static SakshamApp getInstance() {
        if (instance == null) {
            instance = new SakshamApp();
        }
        return instance;
    }

    synchronized public FirebaseFirestore getFirebaseDatabaseInstance() {
        if (db == null) {
            db = FirebaseFirestore.getInstance();
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(true)
                    .build();
            db.setFirestoreSettings(settings);
        }
        return db;
    }

    synchronized public Patient getAppUser(Patient p) {
        if (p != null) {
            patient = p;
            db.collection("patients").document(p.getId()).set(p);
        } else if (patient == null)
            patient = new Patient();
        return patient;
    }

    synchronized public Caregiver getAppCaregiverUser(Caregiver c) {
        if (c != null) {
            caregiver = c;
            db.collection("caregivers").document(c.getId()).set(c);
        } else if (caregiver == null)
            caregiver = new Caregiver();
        return caregiver;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        getFirebaseDatabaseInstance();
        instance = this;
        FirebaseApp.initializeApp(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

//        FirebaseMessaging.getInstance().subscribeToTopic("events");
    }

    synchronized public FirebaseAnalytics getFirebaseAnalyticsObj(){
        if(mFirebaseAnalytics!=null) {
            return mFirebaseAnalytics;
        }else{
            mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
            return mFirebaseAnalytics;
        }
    }


    public void setPatientID(String id) {
        patientID = id;
    }


    public String getPatientID() {
        return patientID;
    }

    public void clearPatientId(){
        patientID = null;
    }

    public void setCaregiverID(String id) {
        caregiverID = id;
    }

    public String getCaregiverID() {
        return caregiverID;
    }

    public void clearCaregiverId(){
        caregiverID = null;
    }

}
