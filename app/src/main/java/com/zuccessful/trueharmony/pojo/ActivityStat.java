package com.zuccessful.trueharmony.pojo;

public class ActivityStat {
    String activity;
    String statusPatient;
    Boolean statusCaregiver;

    public ActivityStat(String activity, String statusPatient, Boolean statusCaregiver) {
        this.activity = activity;
        this.statusPatient = statusPatient;
        this.statusCaregiver = statusCaregiver;
    }

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public String getStatusPatient() {
        return statusPatient;
    }

    public void setStatusPatient(String statusPatient) {
        this.statusPatient = statusPatient;
    }

    public Boolean getStatusCaregiver() {
        return statusCaregiver;
    }

    public void setStatusCaregiver(Boolean statusCaregiver) {
        this.statusCaregiver = statusCaregiver;
    }
}
