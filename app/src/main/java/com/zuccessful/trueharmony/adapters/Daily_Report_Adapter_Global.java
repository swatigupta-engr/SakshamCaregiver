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
import com.zuccessful.trueharmony.fragments.DailyRoutine_Report;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.DailyRoutineRecord;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MedicineRecord;
import com.zuccessful.trueharmony.utilities.Constants;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class Daily_Report_Adapter_Global extends RecyclerView.Adapter<Daily_Report_Adapter_Global.MyViewHolder>
{

    private List<DailyRoutineRecord> dailytaskList;
    private SakshamApp app=SakshamApp.getInstance();;
    private FirebaseFirestore db=app.getFirebaseDatabaseInstance();
    private SimpleDateFormat sdf;
    private String t_date;
     private HashMap<String,Boolean> map;
     private HashMap<String,Integer> map_local;

    private Boolean setFirebase=false;
    private Context mContext;
    private ArrayList<DailyRoutine> alarms ;
    DocumentReference ref;
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
            checkBox_p.setEnabled(false);
            time.setVisibility(View.GONE);
        }
    }

    public Daily_Report_Adapter_Global(List<DailyRoutineRecord> dailylist,Context c)
    {
      this.dailytaskList = dailylist;
        sdf = Utilities.getSimpleDateFormat();
        t_date=sdf.format(new Date());
        mContext=c;



        try {
            alarms = Utilities.getListOfDailyRoutineAlarms();

        }catch (Exception e){

        }
         if(Utilities.getDailyLog_bool()!=null && Utilities.getDailyLog_bool().containsKey(t_date)==true)
        {
            Log.d("logss","Entry found for today");
            map= Utilities.getDailyLog_bool().get(t_date);
           // map_local=Utilities.getDailyLog().get(t_date);
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
        final SimpleDateFormat sdf = Utilities.getSimpleDateFormat();
        final String today = sdf.format(new Date());
        DailyRoutineRecord rec = dailytaskList.get(position);
        final String name=rec.getName();
         final String time=rec.getTime();
          int status2=rec.getDone();
        holder.name.setText(change_language(rec.getName()));

        //time of the activity.............// hidden as of now ..as for daily activites (multiple) only one toggle for taken/not-taken

          holder.time.setText(rec.getTime());
        if(map!=null && map.containsKey(name))
        {
            Log.d("saumya","Entrrrrrry for today exists"+map.get(name));
            Boolean status= map.get(name);
            if(status) {
                   holder.checkBox_c.setChecked(true);
            }
            else
            {
                holder.checkBox_c.setChecked(false);
            }
        }


try {

    for (int j = 0; j < alarms.size(); j++) {
        Map<String, Boolean> days = alarms.get(j).getDays();
        {
            ArrayList<String> slot_list = alarms.get(j).getReminders();
            int slots = slot_list.size();
            Log.v("Slots",slots+"");
            for (int k = 0; k < slots; k++) {
                final String time1 = slot_list.get(k);
                Log.v("data", alarms.get(j).getName() + "..." + name);
                if (alarms.get(j).getName().equalsIgnoreCase(name))

                    ref = db.collection("patient_daily_routine_reports/" + app.getAppUser(null).getId() + "/" + today + "/").document(name + " " + time1);

                ref.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            Log.v("Exists", document.exists() + "");

                            if (document.exists()) {

                                Log.d("logss", " setting using firebase");

                                try {
                                   // Long status = (Long) document.get("done");
                                    //   Boolean status = document.getBoolean("done");

/*

                                    int v= map_local.get(name);



                                    if (v == 1 )
                                        holder.checkBox_p.setChecked(true);
                                    else
                                        holder.checkBox_p.setChecked(false);
*/

                                       Boolean status= DailyRoutine_Report.hmap.get(name);
                                    Log.v("Swati: taken status:", name + " name" + status + "");

                                    if (status == true )
                                        holder.checkBox_p.setChecked(true);
                                    else
                                        holder.checkBox_p.setChecked(false);
                                    // Boolean status2 = document.getBoolean("caregiver_status");
                                    // holder.checkBox_c.setChecked(status2);
                                    setFirebase = true;
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }

                        } else {
                            Log.d("logss", "get failed with ", task.getException());
                        }
                    }
                });
            }
        }
    }


}catch (Exception e){}


        final Boolean patient_status=holder.checkBox_p.isChecked();
        Log.d("logss"," status for patient "+name+" "+""+" "+patient_status);

     /*   if(setFirebase==false && map!=null && map.containsKey(name+","+""))
        {
            Log.d("logss"," setting using local storage");
            Boolean v= map.get(name);

            if(v==true) {
                holder.checkBox_c.setChecked(true);
            }

            else
            {
                holder.checkBox_c.setChecked(false);
            }
        }*/

        holder.checkBox_c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked())
                {
                    Log.d("logss",name+" checkbox tick");
                      //  db.collection("patient_daily_routine_reports/"+app.getAppUser(null).getId() + "/" + today+"/").document(name+" "+time).update("caregiver_status",true);
                    Utilities.saveDailyLog(name,t_date,time,true,mContext);
                }
                else
                {
                    Log.d("logss",name+ " checkbox Un-tick");
                     //   db.collection("patient_daily_routine_reports/"+app.getAppUser(null).getId() + "/" + today+"/").document(name+" "+time).update("caregiver_status",false);
                    Utilities.removeEntryDailyLog(name,t_date);                 }
            }
        });



    }

    public void replaceMeds(ArrayList<DailyRoutineRecord> taskList) {
        Log.d("logss","replacing medss");
        dailytaskList = taskList;

        //    this.dailytaskList= removeDuplicates(taskList);
        Log.v("list!!!!",dailytaskList.size()+"");
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return dailytaskList.size();
    }

    String change_language( String activity){
         String hindi_activity=activity;
        String langPrefType   = Utilities.getDataFromSharedpref(  mContext.getApplicationContext(), Utilities.KEY_LANGUAGE_PREF);
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            Log.v("Lang",langPrefType+" "+lang);

            if(lang==1) {
                //language is hindi
                if (activity.equalsIgnoreCase("Swimming")) {
                    hindi_activity = "तैरना";
                } else if (activity.equalsIgnoreCase("Walk")) {
                    hindi_activity = "सैर करना";

                } else if (activity.equalsIgnoreCase("Run")) {
                    hindi_activity = "भागना";

                } else if (activity.equalsIgnoreCase("Play")) {
                    hindi_activity = "खेलना";

                }
                if (activity.equalsIgnoreCase("Cycling")) {
                    hindi_activity = "साइकिल चलाना";

                }

                if (activity.equalsIgnoreCase("Exercise")) {
                    hindi_activity = "व्यायाम";

                } else if (activity.equalsIgnoreCase("Other")) {
                    hindi_activity = "अन्य";

                } else if (activity.equalsIgnoreCase("Wake-Up")) {
                    hindi_activity = "उठ जाओ ";

                } else if (activity.equalsIgnoreCase("Brush")) {
                    hindi_activity = "ब्रश";

                } else if (activity.equalsIgnoreCase("Bath")) {
                    hindi_activity = "स्नान";

                } else if (activity.equalsIgnoreCase("Sleep")) {
                    hindi_activity = "सोना";

                } else if (activity.equalsIgnoreCase("Breakfast")) {
                    hindi_activity = "नाश्ता";

                } else if (activity.equalsIgnoreCase("Lunch")) {
                    hindi_activity = "दोपहर का भोजन";

                } else if (activity.equalsIgnoreCase("Dinner")) {
                    hindi_activity = "रात का भोजन";

                }
            } else{
                //language is english

                hindi_activity=activity;
            }

        }else {
//            language is english by default
hindi_activity=activity;

        }
        return hindi_activity;
    }



}

