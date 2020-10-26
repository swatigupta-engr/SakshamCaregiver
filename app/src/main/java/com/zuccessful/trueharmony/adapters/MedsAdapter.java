package com.zuccessful.trueharmony.adapters;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.activities.AddMedRecActivity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.receivers.AlarmReceiver;
import com.zuccessful.trueharmony.utilities.Constants;

import java.util.ArrayList;

public class MedsAdapter extends RecyclerView.Adapter<MedsAdapter.MedsViewHolder> {
    private ArrayList<Medication> mMeds;

    public MedsAdapter() {
        mMeds = new ArrayList<>();
    }

    public MedsAdapter(ArrayList<Medication> mMeds) {
        this.mMeds = mMeds;
    }

    @NonNull
    @Override
    public MedsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.med_reminder_list_item, parent, false);
        return new MedsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedsViewHolder holder, int position) {
        Medication med = mMeds.get(position);
        holder.mItem = med;
        String name = med.getName();
        holder.mNameView.setText(name);
        holder.mTimesView.setText(getRemindersFormatted(med.getReminders()));
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
        return mMeds.size();
    }

    public void updateMeds(ArrayList<Medication> medList) {
        mMeds.addAll(medList);
        notifyDataSetChanged();
    }

    public void replaceMeds(ArrayList<Medication> medList) {
        mMeds = medList;
        notifyDataSetChanged();
    }

    public void removeMeds(Medication med) {
        mMeds.remove(med);
        notifyDataSetChanged();
    }

    public ArrayList<Medication> getMeds() {
        return mMeds;
    }


    class MedsViewHolder extends RecyclerView.ViewHolder {

        TextView mNameView;
        TextView mTimesView;
        Medication mItem;
        ImageView mDeleteIcon;

        MedsViewHolder(View itemView) {
            super(itemView);
            mNameView = itemView.findViewById(R.id.med_item_name);
            mTimesView = itemView.findViewById(R.id.med_item_times);
            mDeleteIcon = itemView.findViewById(R.id.deleteIv);

            mDeleteIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlarmManager alarmManager = (AlarmManager)
                            v.getContext().getSystemService(Context.ALARM_SERVICE);
                    Intent myIntent = new Intent(v.getContext(),
                            AlarmReceiver.class);

                    final ArrayList<Integer> alarmIds = mItem.getAlarmIds();

                    for(int id : alarmIds) {
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                v.getContext(), id, myIntent, 0);
                        alarmManager.cancel(pendingIntent);
                    }

                    SakshamApp.getInstance().getFirebaseDatabaseInstance().collection("alarms/" +
                            SakshamApp.getInstance().getAppUser(null).getId() +
                            "/medication").document(mItem.getName()).delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(SakshamApp.getInstance().getApplicationContext(),
                                    "Deleted "
                            +mItem.getName()+" successfully.",Toast.LENGTH_SHORT).show();
                            removeMeds(mItem);
                        }

                    });
                }
            });

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
