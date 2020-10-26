package com.zuccessful.trueharmony.pojo;

import java.util.ArrayList;
import java.util.Date;


public class MyDayQuestions {


    private String ID;
    private String date;
    private String qAnswer1;
    private String qAnswer2;

    public MyDayQuestions() {
    }

    public MyDayQuestions(String ID, String date, String qAnswer1, String qAnswer2) {
        this.ID = ID;
        this.date = date;
        this.qAnswer1 = qAnswer1;
        this.qAnswer2 = qAnswer2;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getqAnswer1() {
        return qAnswer1;
    }

    public void setqAnswer1(String qAnswer1) {
        this.qAnswer1 = qAnswer1;
    }

    public String getqAnswer2() {
        return qAnswer2;
    }

    public void setqAnswer2(String qAnswer2) {
        this.qAnswer2 = qAnswer2;
    }
}