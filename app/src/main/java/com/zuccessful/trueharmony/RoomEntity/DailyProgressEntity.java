package com.zuccessful.trueharmony.RoomEntity;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity(tableName = "DailyProgress_Records")
public class DailyProgressEntity
{
    @PrimaryKey(autoGenerate = true)
    private int serialNo;
    private String date;
    private String activityName;
    private Boolean status;


    public DailyProgressEntity(String date, String activityName, Boolean status)
    {
     this.date=date;
     this.activityName=activityName;
     this.status=status;
    }

    public int getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(int serialNo) {
        this.serialNo = serialNo;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
