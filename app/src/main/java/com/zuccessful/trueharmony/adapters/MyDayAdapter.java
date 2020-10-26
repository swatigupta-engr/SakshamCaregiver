package com.zuccessful.trueharmony.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Movie;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.activities.AdvisoryActivity;
import com.zuccessful.trueharmony.activities.PSychoeducation;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.MyDayQuestions;
import com.zuccessful.trueharmony.utilities.FirebaseAnalyticsHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MyDayAdapter extends RecyclerView.Adapter<com.zuccessful.trueharmony.adapters.MyDayAdapter.MyDayViewHolder>{
    private List<MyDayQuestions> questions;
    public MyDayAdapter(List<MyDayQuestions> questions) {
        this.questions = questions;
    }
    public MyDayViewHolder onCreateViewHolder(ViewGroup parent,
                                         int viewType) {
        // create a new view
        View itemLayoutView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.myday_questions, null);

        // create ViewHolder

        MyDayViewHolder viewHolder = new MyDayViewHolder(itemLayoutView);
        return viewHolder;
    }

    public void onBindViewHolder(final MyDayViewHolder viewHolder, final int position) {

        // - get data from your itemsData at this position
        // - replace the contents of the view with that itemsData



        viewHolder.questTextView.setText("Question1");






    }

    public int getItemCount() {
        return questions.size();
    }

    public static class MyDayViewHolder extends RecyclerView.ViewHolder {

        public TextView questTextView;

        public MyDayViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            questTextView = (TextView) itemLayoutView.findViewById(R.id.myDayQuestions);

        }
    }

}

