package com.zuccessful.trueharmony.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DailyRoutineAdapterLocal extends RecyclerView.Adapter<DailyRoutineAdapterLocal.ViewHolder>
{
    private List<String> activitylist;
    private LayoutInflater mInflater;
    private Context mContext;
    private HashMap<String,Integer> map;
    private SimpleDateFormat sdf;
    String t_date;

    public DailyRoutineAdapterLocal(Context context, List<String> data) {
        //Log.d("saumya","IN ADAPTER"+" SIZE: "+data.size());
        this.mInflater = LayoutInflater.from(context);
        this.activitylist = data;
        mContext = context;
        sdf = Utilities.getSimpleDateFormat();
        t_date=sdf.format(new Date());
        if(Utilities.getDailyLog()!=null && Utilities.getDailyLog().containsKey(t_date)==true)
        {
            Log.d("saumya","Entry found for today");
             map= Utilities.getDailyLog().get(t_date);

        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
     //   Log.d("saumya","IN ADAPTER"+" SIZE: "+activitylist.size());
        View view = mInflater.inflate(R.layout.activity_list_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        final String name = activitylist.get(i);
        viewHolder.activityName.setText(name);
        if(map!=null && map.containsKey(name))
        {
            Log.d("saumya","Entry for today exists");

            int v= map.get(name);
            Log.v("swati:",name+"::"+v );
            if(v==1) {
                viewHolder.yes.setChecked(true);
            }
            else
            {
                viewHolder.no.setChecked(true);
            }
        }

        viewHolder.yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked())
                {
                    Log.d("saumya",name+ " checkbox-yes tick");
                  //  Utilities.saveDailyLog(name,t_date,1,mContext);   // 1 means YES checkbox selected
                }
                else
                {
                    Log.d("saumya",name+ " checkbox-yes Un-tick");
                    Utilities.removeEntryDailyLog(name,t_date);     // delete the entry from savedpreference for this activity
                }

            }
        });

        viewHolder.no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckBox checkBox = (CheckBox)v;
                if(checkBox.isChecked())
                {
                Log.d("saumya",name+" checkbox-no tick");
                //Utilities.saveDailyLog(name,t_date,0,mContext);   //0 means NO checkbox selected
                }
                else
                {
                    Log.d("saumya",name+ " checkbox-no Un-tick");
                    Utilities.removeEntryDailyLog(name,t_date);  //  delete the entry from savedpreference for this activity
                }
            }
        });
        if (name.equalsIgnoreCase("walk"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_walk)); }
        else if (name.equalsIgnoreCase("run"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_walk)); }
        else if (name.equalsIgnoreCase("play"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.leisureactivities)); }
        else if (name.equalsIgnoreCase("swimming"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_smiley)); }
        else if (name.equalsIgnoreCase("cycling"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_walk)); }
        else if (name.equalsIgnoreCase("exercise"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_physical_health)); }
        else if (name.equalsIgnoreCase("wake-up"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.wakingup)); }
        else if (name.equalsIgnoreCase("brush"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.brushingteeth)); }
        else if (name.equalsIgnoreCase("bath"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.bath)); }
        else if (name.equalsIgnoreCase("sleep"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.sleeping)); }
        else if (name.equalsIgnoreCase("breakfast"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eating)); }
        else if (name.equalsIgnoreCase("lunch"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eating)); }
        else if (name.equalsIgnoreCase("dinner"))
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.eating)); }
        else
        { viewHolder.imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_avatar)); }

    }

    @Override
    public int getItemCount() {
        return  activitylist.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView activityName;
        ImageView imageView;
        CheckBox yes,no;
        String t_date;
        String name;
        LinearLayout act_item;
        ViewHolder(View itemView) {
            super(itemView);
            act_item=itemView.findViewById(R.id.act_item);
            activityName = itemView.findViewById(R.id.activity_name);
            imageView = itemView.findViewById(R.id.activityImage);
            yes= itemView.findViewById(R.id.checkbox_yes);
            no= itemView.findViewById(R.id.checkbox_no);
        }

        @Override
        public void onClick(View view) {

        }
    }
}
