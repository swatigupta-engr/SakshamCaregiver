package com.zuccessful.trueharmony.adapters;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
public class Med_Record_Adapter extends RecyclerView.Adapter<Med_Record_Adapter.MyViewHolder>
{

        private List<MedicineRecord> medList;
        private SakshamApp app=SakshamApp.getInstance();;
        private FirebaseFirestore db=app.getFirebaseDatabaseInstance();
        private SimpleDateFormat sdf;
        private String t_date;
        private HashMap<String,Boolean> map;
        private Boolean setFirebase=false;
        private Context mContext;
        public class MyViewHolder extends RecyclerView.ViewHolder
        {
            public TextView name, time;
            public CheckBox checkBox_p, checkBox_c;

            public MyViewHolder(View view) {
                super(view);
                name = (TextView) view.findViewById(R.id.med_name);
                time = (TextView) view.findViewById(R.id.med_time);
                checkBox_p = (CheckBox) view.findViewById(R.id.checkbox_patient);
                checkBox_c= (CheckBox) view.findViewById(R.id.checkbox_caregiver);
            }
        }

        public Med_Record_Adapter(List<MedicineRecord> medList,Context c)
        {
            this.medList = medList;
            sdf = Utilities.getSimpleDateFormat();
            t_date=sdf.format(new Date());
            mContext=c;
            if(Utilities.getMedLog()!=null && Utilities.getMedLog().containsKey(t_date)==true)
            {
                Log.d("logss","Entry found for today");
                map= Utilities.getMedLog().get(t_date);
                Log.d("logss",map+"");
            }
        }

        @Override
        public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.medicine_list_row, parent, false);
            Log.d("logss","on create view holder");
            return new MyViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final MyViewHolder holder, int position) {
            Log.d("logss","In bind view holder");
            SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
            final String today = sdf.format(new Date());
            MedicineRecord med = medList.get(position);
            final String name=med.getName();
            final String time=med.getTime();
            holder.name.setText(med.getName());
            holder.time.setText(med.getTime());
            Log.v("med",name);


            DocumentReference ref=db.collection("patient_med_reports/"+app.getAppUser(null).getId() + "/" + today+"/").document(name+" "+time);
            ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists())
                        {
                            Log.d("logss"," setting using firebase");
                            Boolean status = document.getBoolean("taken");
                            holder.checkBox_p.setChecked(status);
                         //   Boolean status2 = document.getBoolean("caregiver_status");
                           // holder.checkBox_c.setChecked(status2);
                            //setFirebase=true;
                        }

                    } else {
                        Log.d("logss", "get failed with ", task.getException());
                    }
                }
            });
            final Boolean patient_status=holder.checkBox_p.isChecked();
            Log.d("logss"," status for patient "+name+" "+time+" "+patient_status);

            if(setFirebase==false && map!=null && map.containsKey(name+","+time))
            {
                Log.d("logss"," setting using local storage");
                boolean v= map.get(name+","+time);
                if(v==true) {
                    holder.checkBox_c.setChecked(true);
                }
                else
                {
                    holder.checkBox_c.setChecked(false);
                }
            }

            holder.checkBox_c.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox checkBox = (CheckBox)v;
                    if(checkBox.isChecked())
                    {
                        Log.d("logss",name+" checkbox tick");
                  //      db.collection("patient_med_reports/"+app.getAppUser(null).getId() + "/" + today+"/").document(name+" "+time).update("caregiver_status",true);
                        Utilities.saveMedLog(name,time,t_date,patient_status,true,mContext);
                    }
                    else
                    {
                        Log.d("logss",name+ " checkbox Un-tick");
                  //      db.collection("patient_med_reports/"+app.getAppUser(null).getId() + "/" + today+"/").document(name+" "+time).update("caregiver_status",false);
                        Utilities.saveMedLog(name,time,t_date,patient_status,false,mContext);
                    }
                }
            });
        }

    public void replaceMeds(ArrayList<MedicineRecord> medList) {
            Log.d("logss","replacing medss");
        medList = medList;
        notifyDataSetChanged();
    }

        @Override
        public int getItemCount() {
            return medList.size();
        }
    }

