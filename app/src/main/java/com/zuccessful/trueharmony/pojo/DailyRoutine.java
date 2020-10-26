package com.zuccessful.trueharmony.pojo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Admin on 06-08-2018.
 */

public class DailyRoutine implements Serializable {
    private String id;
    private ArrayList<Integer> alarmIds;
    private String name;
    private Map<String,Boolean> days;
    String time;


    private ArrayList<String> reminders;

    public DailyRoutine() {
    }

    public DailyRoutine(String name, ArrayList<String> reminders) {
        this.alarmIds = new ArrayList<>();
        this.name = name;
        this.reminders = reminders;
    }

    public DailyRoutine(String name) {
        this.alarmIds = new ArrayList<>();
        this.name = name;
        this.id = name;
        this.reminders = new ArrayList<>();
    }
    public DailyRoutine(String name,String time,Map<String,Boolean> days) {
        this.name = name;
        this.time = time;
        this.days=days;
    }

    public DailyRoutine(String name, ArrayList<String> reminders, String id,Map<String, Boolean> days) {
        this.alarmIds = new ArrayList<>();
        this.name = name;
        this.reminders = reminders;
        this.id = String.valueOf(id);
        this.days = days;

    }
    public String getTime(){return time;}
    public void setTime(String time){this.time=time;}

    public Map<String, Boolean> getDays() {
        return days;
    }

    public void setDays(Map<String, Boolean> days) {
        this.days = days;
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

    public ArrayList<String> getReminders() {
        return reminders;
    }

    public void setReminders(ArrayList<String> reminders) {
        this.reminders = reminders;
    }

    public ArrayList<Integer> getAlarmIds() {
        return alarmIds;
    }

    public void setAlarmIds(ArrayList<Integer> alarmIds) {
        this.alarmIds = alarmIds;
    }

    public void addAlarmId(int alarm_id) {
        if (!this.alarmIds.contains(alarm_id))
            this.alarmIds.add(alarm_id);
    }
}