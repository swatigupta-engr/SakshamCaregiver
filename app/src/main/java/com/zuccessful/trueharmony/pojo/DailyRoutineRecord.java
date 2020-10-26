package com.zuccessful.trueharmony.pojo;

import java.util.Map;

/**
 * Created by Admin on 07-08-2018.
 */

public class DailyRoutineRecord {
    private String dailyRoutId;
    private String name;
    private long timestamp;
    private int slot;
    private String time;
    private Map<String,Boolean> days;

    private int done;

    public DailyRoutineRecord() {
    }

    public DailyRoutineRecord(String dailyRoutId, String name, long timestamp, int slot, int done) {
        this.dailyRoutId = dailyRoutId;
        this.name = name;
        this.timestamp = timestamp;
        this.slot = slot;
        this.done = done;
    }
    public DailyRoutineRecord(String name, String time, Map<String,Boolean> days) {
        this.name = name;
        this.time = time;
        this.days=days;
    }

    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public void setDays(Map<String,Boolean>days)
    {
        this.days=days;
    }
    public Map<String,Boolean> getDays(){return days;}
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public int getSlot() {
        return slot;
    }

    public void setSlot(int slot) {
        this.slot = slot;
    }

    public String getDailyRoutId() {
        return dailyRoutId;
    }

    public void setDailyRoutId(String dailyRoutId) {
        this.dailyRoutId = dailyRoutId;
    }

/*    public int isDone() {
        return done;
    }*/

/*    public void setDone(int done) {
        this.done = done;
    }*/

    public int getDone() {
        return done;
    }
}