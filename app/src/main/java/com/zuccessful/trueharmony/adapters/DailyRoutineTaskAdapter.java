package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddDailyRoutActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;

import java.util.ArrayList;

/**
 * Created by Admin on 07-08-2018.
 */

public class DailyRoutineTaskAdapter extends RecyclerView.Adapter<DailyRoutineTaskAdapter.RoutineViewHolder> {

    private ArrayList<DailyRoutine> mRouts;
    private Context mContext;

    public DailyRoutineTaskAdapter(Context context, ArrayList<DailyRoutine> mRouts) {
        this.mRouts = mRouts;
        this.mContext = context;
    }

    @NonNull
    @Override
    public DailyRoutineTaskAdapter.RoutineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_reminder_list_item, parent, false);
        return new DailyRoutineTaskAdapter.RoutineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DailyRoutineTaskAdapter.RoutineViewHolder holder,
                                 int position) {
        Log.i("DailyRoutine", "ITEM == " + position);
        DailyRoutine rout = mRouts.get(position);
        holder.mItem = rout;
        final String name = rout.getName();
        holder.mNameView.setText(name);

        if (rout.getReminders().size() == 0) {
            holder.mTimesView.setText("");
            holder.mRootView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent dailyRoutIntent = new Intent(mContext, AddDailyRoutActivity.class);
                    dailyRoutIntent.putExtra("dailyRoutTaskName", name);
                    mContext.startActivity(dailyRoutIntent);
                }
            });
        } else {
            holder.mTimesView.setText(getRemindersFormatted(rout.getReminders()));
            holder.mRootView.setOnClickListener(null);
        }

        Log.i("DailyRoutine", "ITEM name == " + rout.getName());

        if(name.equalsIgnoreCase(SakshamApp.getInstance().getResources().
                getString(R.string.wake))){
            holder.taskIv.setImageResource(R.drawable.ic_wake);
        }else if(name.equalsIgnoreCase(SakshamApp.getInstance().
                getResources().getString(R.string.bath))) {
            holder.taskIv.setImageResource(R.drawable.ic_bath);
        }else if(name.equalsIgnoreCase(SakshamApp.getInstance().
                getResources().getString(R.string.sleep))) {
            holder.taskIv.setImageResource(R.drawable.ic_sleep);
        }else if(name.equalsIgnoreCase(SakshamApp.getInstance().
                getResources().getString(R.string.meal))) {
            holder.taskIv.setImageResource(R.drawable.ic_meal);
        }else if(name.equalsIgnoreCase(SakshamApp.getInstance().
                getResources().getString(R.string.brush))) {
            holder.taskIv.setImageResource(R.drawable.ic_brush);
        }else if(name.equalsIgnoreCase(SakshamApp.getInstance().
                getResources().getString(R.string.walk))) {
            holder.taskIv.setImageResource(R.drawable.ic_walk);
        }

    }

    private String getRemindersFormatted(ArrayList<String> reminders) {
        StringBuilder a = new StringBuilder();
        for (String time : reminders) {
            a.append(time).append(", ");
        }
        return a.toString().substring(0, a.length() - 2);
    }

    @Override
    public int getItemCount() {
        return mRouts.size();
    }

    public void updateRouts(ArrayList<DailyRoutine> medList) {
        mRouts.addAll(medList);
        notifyDataSetChanged();
    }

    public void replaceRouts(ArrayList<DailyRoutine> medList) {
        mRouts = medList;
        notifyDataSetChanged();
    }

    public void removeRouts(DailyRoutine med) {
        mRouts.remove(med);
        notifyDataSetChanged();
    }

    public ArrayList<DailyRoutine> getRouts() {
        return mRouts;
    }


    class RoutineViewHolder extends RecyclerView.ViewHolder {

        CardView mRootView;
        TextView mNameView;
        TextView mTimesView;
        DailyRoutine mItem;
        ImageView mDeleteIcon,taskIv;

        RoutineViewHolder(View itemView) {
            super(itemView);
            mRootView = itemView.findViewById(R.id.rootCardView);
            mNameView = itemView.findViewById(R.id.med_item_name);
            mTimesView = itemView.findViewById(R.id.med_item_times);
            mDeleteIcon = itemView.findViewById(R.id.deleteIv);
            taskIv = itemView.findViewById(R.id.leftIv);
            mDeleteIcon.setVisibility(View.GONE);

//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Toast.makeText(v.getContext(),"medClicked : " +mItem.getName()
//                    ,Toast.LENGTH_SHORT).show();
//                    Intent editMedIntent = new Intent(v.getContext(), AddMedRecActivity.class);
//                    editMedIntent.putExtra(Constants.MED_OBJ,mItem);
//                    editMedIntent.putExtra(Constants.CALLED_FROM,Constants.MED_ADAPTER);
//
//                    v.getContext().startActivity(editMedIntent);
//
//                }
//            });
        }
    }
}