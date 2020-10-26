package com.zuccessful.trueharmony.activities;

import android.Manifest;
import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.opencsv.CSVWriter;
import com.zuccessful.trueharmony.DatabaseRoom;
import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.RoomEntity.DailyProgressEntity;
import com.zuccessful.trueharmony.RoomEntity.LogEntity;
import com.zuccessful.trueharmony.RoomEntity.MedicineProgressEntity;
import com.zuccessful.trueharmony.application.SakshamApp;
import com.zuccessful.trueharmony.fragments.HomeFragment;
import com.zuccessful.trueharmony.pojo.Patient;
import com.zuccessful.trueharmony.receivers.MyReceiver;
import com.zuccessful.trueharmony.receivers.NetworkReceiver;
import com.zuccessful.trueharmony.services.MedService;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import static com.zuccessful.trueharmony.utilities.Utilities.changeLanguage;
import static com.zuccessful.trueharmony.utilities.Utilities.isInternetOn;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String TITLE = "TITLE";
    private static final String FRAGMENT_TAG = "FRAGMENT_TAG";
    private String selectedFragmentId;
    private String selectedFragmentTitle;
    FirebaseUser user;
    private TextView userNameText, userEmailText;
    private ImageView userProfPic;
    private FirebaseAuth mAuth;
    private SakshamApp app;
    private FirebaseFirestore db;
    private Patient patient;
    File file = null;
    CSVWriter cw;
    FileWriter fw;
    MyReceiver dateReceiver;
    NetworkReceiver network_receiver;
     IntentFilter intentFilter;
    IntentFilter filter;
//    private DrawerLayout mDrawer;
//    GoogleSignInClient mGoogleSignInClient;
    private final int WRITE_EXTERNAL_STORAGE_CODE=1;

    boolean doubleBackToExitPressedOnce = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // changeLanguage(getApplicationContext());
        setContentView(R.layout.activity_main);
        Log.v("Recreate","recreate");
        changeLanguage(MainActivity.this);

        //recreate();
        app = SakshamApp.getInstance();
        db = app.getFirebaseDatabaseInstance();
        patient = app.getAppUser(null);
     //   createNotificationChannel();

        askPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE,WRITE_EXTERNAL_STORAGE_CODE);


        dateReceiver=new MyReceiver();
       network_receiver=new NetworkReceiver();
         filter = new IntentFilter();
        filter.addAction(Intent.ACTION_TIME_CHANGED);
        filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        filter.addAction(Intent.ACTION_DATE_CHANGED);

         intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");

        mAuth = FirebaseAuth.getInstance();
//        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
//                .requestIdToken(getString(R.string.default_web_client_id))
//                .requestEmail()
//                .build();
//        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

       // toolbar.setNavigationIcon(getDrawable(R.drawable.ic_launcher_white_bg));
        toolbar.setNavigationIcon(getDrawable(R.drawable.ic_launcher_white));

        toolbar.setPadding(10,2,2,0);

        toolbar.setTitle(getResources().getString(R.string.app_name) + " - " + SakshamApp.getInstance().getPatientID());

        scheduleAlarm();

        if(Utilities.isInternetOn(getApplicationContext()))
        {
            Log.d("TAG"," service started");
            Intent intent= new Intent(getApplicationContext(), MedService.class);
            getApplicationContext().startService(intent);


        }



//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Asking for Help", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

//        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
//        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
//                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
//        mDrawer.addDrawerListener(toggle);
//        toggle.syncState();
//
////        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
//        navigationView.setNavigationItemSelectedListener(this);
//
//        userNameText = navigationView.getHeaderView(0).findViewById(R.id.user_display_name);
//        userEmailText = navigationView.getHeaderView(0).findViewById(R.id.user_email);
//        userProfPic = navigationView.getHeaderView(0).findViewById(R.id.user_display_img);

//        user = mAuth.getCurrentUser();

//        if (user != null && user.getEmail() != null) {
//            String email = user.getEmail();
//
//        }

//        if (savedInstanceState == null) {
//            navigationView.setCheckedItem(R.id.nav_h`ome);
//            navigationView.getMenu().performIdentifierAction(R.id.nav_home, 0);
//        } else {
//            setTitle(savedInstanceState.getString(TITLE, "TrueHarmony"));
//            setSelectedFragmentTitle(savedInstanceState.getString(TITLE, "TrueHarmony"));
//            setSelectedFragmentId(savedInstanceState.getString(FRAGMENT_TAG, HomeFragment.class.getSimpleName()));
//        }
        Fragment fragment = null;
        fragment = new HomeFragment();
//        if (fragment != null && !fragment.getClass().getSimpleName().equals(getSelectedFragmentId())) {
            replaceAndSetFragment(fragment);
//            item.setChecked(true);
            setTitle(getResources().getString(R.string.app_name));
            setSelectedFragmentTitle(getResources().getString(R.string.app_name));
//        }

    }
//    private void createNotificationChannel() {
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.channel_name);
//            String description = getString(R.string.channel_description);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("channel1", name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    @Override
    protected void onResume() {
        super.onResume();

        //registerReceiver(dateReceiver,filter);
       registerReceiver(network_receiver,intentFilter);
     }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //unregisterReceiver(dateReceiver);
        unregisterReceiver(network_receiver);
    }

    public void askPermission(String permission, int requestCode)
    {
        if (ContextCompat.checkSelfPermission(MainActivity.this,permission)!= PackageManager.PERMISSION_GRANTED)
        {
            // we dont have permission
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{permission},requestCode);
        }
        else
        {
            //we have permission

        }


    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case WRITE_EXTERNAL_STORAGE_CODE:
                if (grantResults.length>0 && grantResults[0]==PackageManager.PERMISSION_GRANTED){
                    //granted
                }
                break;


        }
    }



//    @Override
//    protected void onStart() {
//        super.onStart();
//        if (user != null) {
//            userDetailsText.setText("User: " + user.getDisplayName() + "\nEmail: " + user.getEmail() + "\nAccess: Granted");
//            userNameText.setText(user.getDisplayName());
//            userEmailText.setText(user.getEmail());
//            Picasso.get().load(user.getPhotoUrl()).placeholder(R.mipmap.ic_launcher).into(userProfPic);
//        } else {
//            userDetailsText.setText("Not Logged In!");
//            finish();
//        }
//    }

//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString(TITLE, getSelectedFragmentTitle());
//        outState.putString(FRAGMENT_TAG, getSelectedFragmentId());
//    }

    @Override
    public void onBackPressed() {
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        if (drawer.isDrawerOpen(GravityCompat.START)) {
//            drawer.closeDrawer(GravityCompat.START);
//        } else {
            if (doubleBackToExitPressedOnce) {
                Intent homeIntent = new Intent(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);
                homeIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(homeIntent);
            } else {
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
                doubleBackToExitPressedOnce = true;
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }


//            super.onBackPressed();
//        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        menu.add(SakshamApp.getInstance().getPatientID());
//        menu.add("About me");
//        menu.add("Alarm preferences");

        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
//
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        boolean res= false;
        switch (item.getItemId()) {
            case R.id.action_about_me:
                // about me
                Intent intent = new Intent(this,AboutMe.class);
                startActivity(intent);
                break;
            case R.id.action_alarm_pref:
                // alarm preferences;
                Intent intent2 = new Intent(this,AlarmPref.class);
                startActivity(intent2);
                break;

            case R.id.user_profile:
                // alarm preferences;
                Intent userProfileIntent = new Intent(this,
                        UserProfileActivity.class);
                startActivity(userProfileIntent);
                break;
            case R.id.download_data:
                Toast.makeText(getApplicationContext(),"Files downloaded",Toast.LENGTH_LONG).show();
                downloadData();
                break;

            default:
                res= super.onOptionsItemSelected(item);
        }
        return res;

    }
    public void downloadData() {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                DatabaseRoom database = DatabaseRoom.getInstance(getApplicationContext());
                int res_size;
                try {
                    file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Care_log.csv");
                    if (file.exists()) {
                        file.delete();
                    }

                    boolean b = file.createNewFile();
                    Log.d("Download_Data", b + " file created " + file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Experience", "Hardships"});
                    List<LogEntity> log_list = database.logRecords().getLogRecords();
                    res_size = log_list.size();
                    Log.d("Download_Data", res_size + " entries present");
                    for (int i = 0; i < res_size; i++) {
                        Log.d("Download_Data", "Getting entry number: " + i);
                        LogEntity temp = log_list.get(i);
                        String arr[] = new String[4];
                        arr[0] = String.valueOf(temp.getSerialNo());
                        arr[1] = temp.getDate();
                        arr[2] = temp.getExperience();
                        arr[3] = temp.getHardships();
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data", "DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"MedicineProgress_care_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Med Name","Time", "Patient Status","CareGiver Status"});
                    List<MedicineProgressEntity> list= database.medicineProgressRecords().getMedicineProgressRecords();
                    res_size=list.size();
                    Log.d("Download_Data",res_size+" entries present");
                    for(int i=0;i<res_size;i++)
                    {
                        Log.d("Download_Data","Getting entry number: "+i);
                        MedicineProgressEntity temp=list.get(i);
                        String arr[]=new String[6];
                        arr[0]= String.valueOf(temp.getSerialNo());
                        arr[1]= temp.getDate();
                        arr[2]= temp.getMedicineName();
                        arr[3]= temp.getTime();
                        arr[4]= String.valueOf(temp.getPatient_status());
                        arr[5]= String.valueOf(temp.getCaregiver_status());
                        Log.d("Download_Data ",arr[0]+" "+arr[1]+" "+arr[2]+" "+arr[3]+" "+arr[3]);
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data","DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }

                try {
                    file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"DailyProgress_log.csv");
                    if(file.exists())
                    { file.delete(); }

                    boolean b=file.createNewFile();
                    Log.d("Download_Data",b+" file created "+file.getAbsoluteFile());
                    fw = new FileWriter(file.getAbsolutePath(), true);
                    cw = new CSVWriter(fw);
                    cw.writeNext(new String[]{"S.No", "Date", "Activity Name","Status"});
                    List<DailyProgressEntity> list= database.dailyProgressRecords().getDailyProgressRecords();

                    res_size=list.size();
                    Log.d("Download_Data",res_size+" entries present");
                    for(int i=0;i<res_size;i++)
                    {
                        Log.d("Download_Data","Getting entry number: "+i);
                        DailyProgressEntity temp=list.get(i);
                        String arr[]=new String[4];
                        arr[0]= String.valueOf(temp.getSerialNo());
                        arr[1]= temp.getDate();
                        arr[2]= temp.getActivityName();
                        arr[3]= String.valueOf(temp.getStatus());
                        cw.writeNext(arr);
                    }
                    Log.d("Download_Data","DONE");
                    cw.flush();
                    cw.close();
                } catch (IOException e) { e.printStackTrace(); }



            }
        });
    }

//    @SuppressWarnings("StatementWithEmptyBody")
//    @Override
//    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//        // Handle navigation view item clicks here.
//        selectFragment(item);
//        DrawerLayout drawer = findViewById(R.id.drawer_layout);
//        drawer.closeDrawer(GravityCompat.START);
//        return true;
//    }


//    private void signOut() {
//        Log.d(TAG, "Sign Out Called");
//        mAuth.signOut();
//
//        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                // Nothing
//            }
//        });
//        finish();
//    }


//    private void selectFragment(MenuItem item) {
//        Fragment fragment = null;
//        switch (item.getItemId()) {
//            case android.R.id.home: //Menu icon
//                Log.d("EventsFragment", "Clicked");
////                openDrawer();
//                break;
//
//            case R.id.nav_home:
//                f
//                break;
//
//            case R.id.nav_settings:
////                Intent i = new Intent(this, SettingsActivity.class);
////                i.putExtra(PreferenceActivity.EXTRA_NO_HEADERS, true);
////                startActivity(i);
//                Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
//                break;
//
//            case R.id.nav_logout:
////                signOut();
//                break;
//
//            default:
//                return;
//        }
//
//    }

    private void replaceAndSetFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment, fragment.getClass().getSimpleName())
                .commit();
        setSelectedFragmentId(fragment.getClass().getSimpleName());
    }
//
    public String getSelectedFragmentId() {
        return selectedFragmentId;
    }

    public void setSelectedFragmentId(String selectedFragmentId) {
        this.selectedFragmentId = selectedFragmentId;
    }
//
//    public String getSelectedFragmentTitle() {
//        return selectedFragmentTitle;
//    }
//
    public void setSelectedFragmentTitle(String selectedFragmentTitle) {
        this.selectedFragmentTitle = selectedFragmentTitle;
    }

//    public void openDrawer() {
//        Log.d(TAG, "Open Drawer Clicked");
//        mDrawer.openDrawer(GravityCompat.START);
//    }

    // Setup a recurring alarm every half hour
    public void scheduleAlarm() {
        // Construct an intent that will execute the AlarmReceiver
        Intent intent = new Intent(getApplicationContext(), MyReceiver.class);
        // Create a PendingIntent to be triggered when the alarm goes off
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, MyReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        // Setup periodic alarm every every half hour from this point onwards
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis()); //do I even need this?
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // First parameter is the type: ELAPSED_REALTIME, ELAPSED_REALTIME_WAKEUP, RTC_WAKEUP
        // Interval can be INTERVAL_FIFTEEN_MINUTES, INTERVAL_HALF_HOUR, INTERVAL_HOUR, INTERVAL_DAY
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY, pIntent);
    }
}
