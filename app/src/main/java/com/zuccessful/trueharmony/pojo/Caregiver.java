package com.zuccessful.trueharmony.pojo;

public class Caregiver {

    private String id;
    private String name;
    private String uid;
    private String patientID;

    public Caregiver() {
    }

    public Caregiver(String id, String name, String uid, String patientID) {
        this.id = id;
        this.name = name;
        this.uid = uid;
        this.patientID = patientID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPatientID() {
        return patientID;
    }

    public void setPatientID(String patientID) {
        this.patientID = patientID;
    }
}
