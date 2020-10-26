package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "MedicineProgress_Records")
public class MedicineProgressEntity
{
    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String date;
    private String medicineName;


    private String time;
    private Boolean patient_status;
    private Boolean caregiver_status;


    public MedicineProgressEntity(String date, String medicineName, String time, Boolean patient_status, Boolean caregiver_status)
    {
     this.medicineName=medicineName;
     this.date=date;
     this.caregiver_status=caregiver_status;
     this.time=time;
     this.patient_status=patient_status;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getMedicineName() {
        return medicineName;
    }

    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public Boolean getPatient_status() {
        return patient_status;
    }

    public void setPatient_status(Boolean patient_status) {
        this.patient_status = patient_status;
    }

    public Boolean getCaregiver_status() {
        return caregiver_status;
    }

    public void setCaregiver_status(Boolean caregiver_status) {
        this.caregiver_status = caregiver_status;
    }


}
