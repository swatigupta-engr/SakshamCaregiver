package com.zuccessful.trueharmony.utilities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.util.TypedValue;
import android.widget.Toast;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.pojo.DailyRoutine;
import com.zuccessful.trueharmony.pojo.Medication;
import com.zuccessful.trueharmony.pojo.MyDayQuestions;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.content.Context.MODE_PRIVATE;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_CID;
import static com.zuccessful.trueharmony.activities.LoginActivity.PREF_PID;

public class Utilities {

    private static final String MY_PREFS_NAME = "Saksham_Pref";
    public static final String KEY_HEIGHT = "height";
    public static final String KEY_WEIGHT = "weight";
    public static final String KEY_NAME = "name";
    public static final String KEY_ALARM_PREF = "alarmPref";
    public static final String KEY_LANGUAGE_PREF = "langPref";
    public static final String KEY_PHY_ACT_LIST = "physicalActList";
    public static final String KEY_LEISURE_ACT_LIST = "leisureActList";
    public static final String KEY_MIDICINES_LIST = "medicinesList";
    public static String KEY_MYDAY_LIST;// = "myDayList";
    public static final String KEY_BREAKFAST = "breakfast";
    public static final String KEY_LUNCH = "lunch";
    public static final String KEY_DINNER = "dinner";

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    private static Context updateResources(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration configuration = context.getResources().getConfiguration();
        configuration.setLocale(locale);
        return context.createConfigurationContext(configuration);
    }

    public static void changeLanguage(Context context){
        String langPrefType = Utilities.getDataFromSharedpref(context,Utilities.KEY_LANGUAGE_PREF);
        String languageToLoad;
        if(langPrefType!=null) {
            try {
                int lang = Integer.parseInt(langPrefType);
                if (lang == 1) {
                    languageToLoad = "hi"; // your language
                } else {
                    languageToLoad = "en";
                }
            }catch (Exception e){
                languageToLoad ="en";
            }

        }else{
            languageToLoad = "en"; // default language
        }
        Locale locale = new Locale(languageToLoad);
        Locale.setDefault(locale);
        updateResources(context,languageToLoad);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config,
                context.getResources().getDisplayMetrics());

    }

    public static ArrayList<DailyRoutine> getListOfDailyRoutineAlarms(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_ALARM_LIST , "");
        ArrayList<DailyRoutine> alarmArrayList = gson.fromJson(response, new TypeToken<List<DailyRoutine>>(){}.getType());
        return alarmArrayList;
    }
    public static void saveDailyRoutineAlarms(DailyRoutine dailyRoutine)
    {
        ArrayList<DailyRoutine> alarmArrayList;
        alarmArrayList = getListOfDailyRoutineAlarms();

        if(alarmArrayList == null)
        {
            alarmArrayList = new ArrayList<>();
            Log.d("saumya","NULL LIST ");
          //  Log.d("saumya","size before adding NULL CASE"+alarmArrayList.size());
        }
       // Log.d("saumya","size before adding "+alarmArrayList.size());

        String name= dailyRoutine.getName();
        for(int i=0;i<alarmArrayList.size();i++)
        {
            DailyRoutine temp= alarmArrayList.get(i);
            if(temp.getName()==name) {
                alarmArrayList.remove(temp);
                break;
            }
        }
        alarmArrayList.add(dailyRoutine);
       // Log.d("saumya","size after adding "+alarmArrayList.size());

        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());

        Gson gson = new Gson();
        String json = gson.toJson(alarmArrayList);

        editor = shref.edit();
        editor.remove(Constants.KEY_ALARM_LIST).commit();
        editor.putString(Constants.KEY_ALARM_LIST, json);
        editor.commit();
    }


    public static void saveListToSharedPref(ArrayList<String> list,String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString(key, json);
        editor.apply();
    }

    public static ArrayList<String> getListFromSharedPref(String key){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = prefs.getString(key, null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        if(gson.fromJson(json, type)!=null) {
            return gson.fromJson(json, type);
        }else{
            return new ArrayList<>();
        }
    }

    public static void saveMedicineToList(Medication medication){
        ArrayList<Medication> medicationArrayList;
        medicationArrayList = getListOfMedication();
        if(medicationArrayList == null) medicationArrayList = new ArrayList<>();
        medicationArrayList.add(medication);
        saveListOfMedicine(medicationArrayList);
    }




    public static void saveListOfMedicine(ArrayList<Medication> medicationArrayList){
        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = gson.toJson(medicationArrayList);

        editor = shref.edit();
        editor.remove(KEY_MIDICINES_LIST).commit();
        editor.putString(KEY_MIDICINES_LIST, json);
        editor.commit();
    }

    public static void saveMyDayAnswers(MyDayQuestions myDayQuestions){
        SharedPreferences shref;
        SharedPreferences.Editor editor;
        shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd ");
        Date date = new Date();
        String current_date = dateFormat.format(date);

        KEY_MYDAY_LIST= current_date;
        Gson gsonmyday = new Gson();
        String json = gsonmyday.toJson(myDayQuestions);

        editor = shref.edit();
        //editor.remove(KEY_MYDAY_LIST).commit();
        editor.putString(KEY_MYDAY_LIST, json);
        Log.d("savemyday", json);
        editor.commit();
        editor.apply();
    }

    public static ArrayList<Medication> getListOfMedication(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(KEY_MIDICINES_LIST , "");
        ArrayList<Medication> medicationArrayList = gson.fromJson(response,
                new TypeToken<List<Medication>>(){}.getType());
        return medicationArrayList;
    }

    public static void getMyDay(){
        Log.d("myday","retrieving data");

        Context context = SakshamApp.getInstance().getApplicationContext();
        SharedPreferences sharedprefs = context.getSharedPreferences(MY_PREFS_NAME,MODE_PRIVATE);
        String response=sharedprefs.getString(KEY_MYDAY_LIST , "");
Log.d("response", response);
    }

    public static ArrayList<MyDayQuestions> getListOfMyDay(){
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(KEY_MYDAY_LIST , "");
        ArrayList<MyDayQuestions> myDayArrayList = gson.fromJson(response,
                new TypeToken<List<MyDayQuestions>>(){}.getType());
        return myDayArrayList;
    }



    public static void removeListFromSharedPref(String key,String value){
        ArrayList<String> actList = getListFromSharedPref(key);
        if(actList.contains(value)){
            actList.remove(value);
            saveListToSharedPref(actList,key);
        }
    }



    public static void saveDataInSharedpref(Context context,String key,String value){
        SharedPreferences.Editor editor = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        editor.putString(key, value);
        Log.d("putstring", key);
        editor.apply();
    }

    public static String getDataFromSharedpref(Context context,String key){
        SharedPreferences prefs = context.getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String restoredText = prefs.getString(key, "");
        //Log.d("responseinget", restoredText);
        return restoredText;
    }

    public static void saveArrayListTimers(ArrayList<String> list){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String json = gson.toJson(list);
        editor.putString("timerlist", json);
        editor.apply();     // This line is IMPORTANT !!!
    }

    public static ArrayList<String> getArrayListTimers(){
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String json = prefs.getString("timerlist", null);
        Type type = new TypeToken<ArrayList<String>>() {}.getType();
        return gson.fromJson(json, type);
    }

    public static void addToTimersList(String notID){
        ArrayList<String> timerList = getArrayListTimers();
        if(timerList==null) timerList = new ArrayList<>();
        timerList.add(notID);
        saveArrayListTimers(timerList);
    }

    public static void removeFromTimersList(int notID){
        ArrayList<String> timerList = getArrayListTimers();
        if(timerList==null) return;
        timerList.remove(String.valueOf(notID));
        saveArrayListTimers(timerList);
    }

    public static void incrementTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int count = preferences.getInt("timerCounter", 0);
        preferences.edit().putInt("timerCounter", count+1).commit();
    }

    public static void resetTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        preferences.edit().putInt("timerCounter", 0).commit();
    }

    public static int getTimerCount(Context context){
        SharedPreferences preferences = context.getSharedPreferences(
                "com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int count = preferences.getInt("timerCounter", 0);
        return count;
    }

    public static int getNextAlarmId(Context context) {

        // TODO: fix if the app is reinstalled, sync with servers max value
        SharedPreferences preferences = context.getSharedPreferences("com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        int id = preferences.getInt("new_alarm_index", 0);
        setNextAlarmId(context, id + 1);
        return id;
    }

    private static void setNextAlarmId(Context context, int i) {
        SharedPreferences preferences = context.getSharedPreferences("com.zuccessful.trueharmony.ALARM_PREFERENCES", MODE_PRIVATE);
        preferences.edit().putInt("new_alarm_index", i).apply();
    }

    public static Intent setExtraForIntent(Intent intent, String key, Object object) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = null;
        try {
            out = new ObjectOutputStream(bos);
            out.writeObject(object);
            out.flush();
            byte[] data = bos.toByteArray();
            intent.putExtra(key, data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bos.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return intent;
    }

    public static Object getExtraFromIntent(Intent intent, String key) {
        Object object = null;
        byte[] rawObj = intent.getByteArrayExtra(key);
        if (rawObj != null && rawObj.length > 0) {
            ByteArrayInputStream bis = new ByteArrayInputStream(rawObj);
            ObjectInput in = null;
            try {
                in = new ObjectInputStream(bis);
                object = in.readObject();
            } catch (ClassNotFoundException | IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (in != null) {
                        in.close();
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
        return object;
    }

    public static int getPixelValue(Context context, int dimenId) {
        Resources resources = context.getResources();
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dimenId,
                resources.getDisplayMetrics()
        );
    }

    public static SimpleDateFormat getSimpleDateFormat(){
        return new SimpleDateFormat("MM-dd-yy", Locale.US);
    }

    public static void clearPatientId(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_PID, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }
    public static void clearCaregiverId(Context context){
        SharedPreferences preferences = context.getSharedPreferences(PREF_CID, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.clear();
        editor.commit();
    }

    public static Context onAttach(Context newBase) {
        String langPrefType = Utilities.getDataFromSharedpref(newBase,Utilities.KEY_LANGUAGE_PREF);
        String languageToLoad;
        if(langPrefType!=null) {
            int lang;
            if(langPrefType.equals(""))
            {
                lang=1;
            }
            else
            {
                lang = Integer.parseInt(langPrefType);
            }

            if(lang==1) {
                languageToLoad = "hi"; // your language
            }else{
                languageToLoad = "en";
            }
        }else {
            languageToLoad = "en"; // default language
        }

        return updateResource(newBase, languageToLoad);
    }
    public static Context updateResource(Context context, String language) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return updateResources(context, language);
        }
        return updateResourcesLegacy(context, language);
    }

    private static Context updateResourcesLegacy(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);

        Resources resources = context.getResources();

        Configuration configuration = resources.getConfiguration();
        configuration.locale = locale;

        resources.updateConfiguration(configuration, resources.getDisplayMetrics());

        return context;
    }

    public static final boolean isInternetOn(Context context) {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);

        // Check for network connections
        if ( connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {

            // if connected with internet

            Toast.makeText(context, " Connected ", Toast.LENGTH_LONG).show();
            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {

            Toast.makeText(context, " Not Connected  to Internet", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }

    public static void removeEntryDailyLog(String activityName,String date)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);
        editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
        HashMap<String, HashMap<String,Boolean>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Boolean>>>(){}.getType());
        HashMap<String ,Boolean> temp=measurementMap.get(date);
        Log.d("saumya","PREVIOUS LENGTH OF MAP for current date "+temp.size());
        temp.remove(activityName);
        measurementMap.put(date,temp);
        Log.d("saumya","AFTER LENGTH OF MAP "+temp.size());
        String jsonList = gson.toJson(measurementMap);
        editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
        editor.commit();
    }
    public static void saveDailyLog(final String activityName, final String date, final String v,final Boolean caregiver,final Context c)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);

        if(response==null)
        {
            HashMap<String,HashMap<String,Boolean>> measurementMap = new HashMap<>();

            HashMap<String ,Boolean> temp=new HashMap<String, Boolean>();
          //  temp.put(activityName+""+v,caregiver);
            temp.put(activityName,caregiver);
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.apply();
            Log.d("swati","saved the new daily routine map to shared preferences");
        }
        else
        {
            editor.remove(Constants.KEY_DAILY_LOG_LIST).commit();
            HashMap<String,HashMap<String,Boolean>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Boolean>>>(){}.getType());
            HashMap<String, Boolean> temp;
            if(measurementMap.containsKey(date)==true)
            {
                temp =  measurementMap.get(date);
            }
            else {
                temp = new HashMap<>();
            }
          //  temp.put(activityName+""+v,caregiver);
            temp.put(activityName,caregiver);

            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_DAILY_LOG_LIST,jsonList);
            editor.commit();
        }
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                boolean f=false;
                if(v.equals(1))
                {
                    f=true;
                }

             /*   boolean c_status=false;
                if(caregiver==true)
                {
                    c_status=true;
                }*/
                List<DailyProgressEntity> ret = database.dailyProgressRecords().getDailyProgressRecords();
                for(int i=0;i<ret.size();i++)
                {
                    DailyProgressEntity temp= ret.get(i);
                    if(temp.getDate().equals(date) && temp.getActivityName().equals(activityName))
                    {
                        database.dailyProgressRecords().delete(temp);
                    }
                }
                DailyProgressEntity ce = new DailyProgressEntity(date,activityName,caregiver);
                database.dailyProgressRecords().addDailyProgressRecord(ce);
                ret = database.dailyProgressRecords().getDailyProgressRecords();
                Log.d("Logg", " after size of retrieved arraylist11111111 : " + ret.size());
            }
        });
    }

     public static HashMap<String,HashMap<String,Integer>> getDailyLog()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);
        HashMap<String,HashMap<String,Integer>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Integer>>>(){}.getType());
        return measurementMap;
    }
    public static HashMap<String,HashMap<String,Boolean>> getDailyLog_bool()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_DAILY_LOG_LIST , null);
        HashMap<String,HashMap<String,Boolean>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Boolean>>>(){}.getType());
        return measurementMap;
    }
    public static void saveMedLog(final String medName, final String time, final String date, final Boolean patient, final Boolean caregiver ,final Context c)
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        SharedPreferences.Editor editor = shref.edit();
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MED_LOG_LIST , null);

        if(response==null)
        {
            HashMap<String,HashMap<String,Boolean>> measurementMap = new HashMap<>();
            HashMap<String ,Boolean> temp=new HashMap<String, Boolean>();
            temp.put(medName+","+time,caregiver);
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_MED_LOG_LIST,jsonList);
            editor.apply();
            Log.d("logss","saved the new med routine map to shared preferences");
        }
        else
        {
            editor.remove(Constants.KEY_MED_LOG_LIST).commit();
            HashMap<String,HashMap<String,Boolean>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Boolean>>>(){}.getType());
            HashMap<String, Boolean> temp;
            if(measurementMap.containsKey(date)==true)
            {
                temp =  measurementMap.get(date);
            }
            else {
                temp = new HashMap<>();
            }
            temp.put(medName+","+time,caregiver);
            Log.d("saumya ",temp+"");
            measurementMap.put(date,temp);
            String jsonList = gson.toJson(measurementMap);
            editor.putString(Constants.KEY_MED_LOG_LIST,jsonList);
            editor.commit();
        }

        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(c);
                boolean c_status=false;
                if(caregiver==true)
                {
                    c_status=true;
                }
                List<MedicineProgressEntity> ret = database.medicineProgressRecords().getMedicineProgressRecords();
                Log.d("Logg", " before size of retrieved arraylist222222222222: " + ret.size());
                for(int i=0;i<ret.size();i++)
                {
                    MedicineProgressEntity temp= ret.get(i);
                    if(temp.getDate().equals(date) && temp.getMedicineName().equals(medName) && temp.getTime().equals(time))
                    {
                        Log.d("saumya "," delete from room entry for "+medName);
                        database.medicineProgressRecords().delete(temp);
                    }
                }
                MedicineProgressEntity ce = new MedicineProgressEntity(date,medName,time,patient,c_status);
                database.medicineProgressRecords().addMedicineProgressRecord(ce);
                ret = database.medicineProgressRecords().getMedicineProgressRecords();
                Log.d("Logg", " after size of retrieved arraylist 222222222: " + ret.size());
            }
        });
    }

    public static HashMap<String,HashMap<String,Boolean>> getMedLog()
    {
        SharedPreferences shref = PreferenceManager.getDefaultSharedPreferences(SakshamApp.getInstance());
        Gson gson = new Gson();
        String response=shref.getString(Constants.KEY_MED_LOG_LIST , null);
        HashMap<String,HashMap<String,Boolean>> measurementMap = gson.fromJson(response, new TypeToken<HashMap<String,HashMap<String,Boolean>>>(){}.getType());
        return measurementMap;
    }

    /*
        ## Script to lookup alarms in the device
        echo "Please set a search filter"
        read search

        adb shell dumpsys alarm | grep $search | (while read i; do echo $i; _DT=$(echo $i | grep -Eo 'when\s+([0-9]{10})' | tr -d '[[:alpha:][:space:]]'); if [ $_DT ]; then echo -e "\e[31m$(date -d @$_DT)\e[0m"; fi; done;)

     */
}
