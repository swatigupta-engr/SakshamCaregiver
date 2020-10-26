package com.zuccessful.trueharmony.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;
import com.zuccessful.trueharmony.pojo.FileDownloader;
import com.zuccessful.trueharmony.utilities.Utilities;

import java.io.File;
import java.io.IOException;

public class LibraryActivity extends AppCompatActivity {

    private TextView title;
    private TextView description;
    ImageView diet,physical,health;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.resource_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        getSupportActionBar().setTitle(getResources().getString(R.string.library));

        ImageView view_image = (ImageView) findViewById(R.id.view);
        diet = (ImageView) findViewById(R.id.diet);
        physical = (ImageView) findViewById(R.id.physical);
        health = (ImageView) findViewById(R.id.health);
        title = (TextView) findViewById(R.id.pdf_item_name);
        description = (TextView) findViewById(R.id.pdf_des);
        title.setText(getResources().getString(R.string.psycho_education));
        description.setText(getResources().getString(R.string.psycho_education));


        StrictMode.VmPolicy.Builder builder = new StrictMode.VmPolicy.Builder();
        StrictMode.setVmPolicy(builder.build());


        view_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                download(v);
            }
        });

        diet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               /* PdfRenderFragment p= new PdfRenderFragment();
                Intent i= new Intent(getApplicationContext(),LibraryPDF.class);
                i.putExtra("filename","diet.pdf");
                i.putExtra("filename_hindi","eating_right_hindi.pdf");

                startActivity(i);*/
                Intent pIntent = new Intent(LibraryActivity.this, PDFRenderActivity.class);
                pIntent.putExtra("filename","diet.pdf");
                pIntent.putExtra("filename_hindi","eating_right_hindi.pdf");

                startActivity(pIntent);
            }
        });
        physical.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*
                PdfRenderFragment p= new PdfRenderFragment();
                Intent i= new Intent(getApplicationContext(),LibraryPDF.class);
                i.putExtra("filename","physical.pdf");
                i.putExtra("filename_hindi","physical_activity_hindi.pdf");
                startActivity(i);*/
                Intent pIntent = new Intent(LibraryActivity.this, PDFRenderActivity.class);
                pIntent.putExtra("filename","physical.pdf");
                pIntent.putExtra("filename_hindi","physical_activity_hindi.pdf");

                startActivity(pIntent);
            }
        });

        health.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*PdfRenderFragment p= new PdfRenderFragment();
                Intent i= new Intent(getApplicationContext(),LibraryPDF.class);
                i.putExtra("filename","health.pdf");
                i.putExtra("filename_hindi","health_monitoring_hindi.pdf");
                startActivity(i);*/
                Intent pIntent = new Intent(LibraryActivity.this, PDFRenderActivity.class);
                pIntent.putExtra("filename","health.pdf");
                pIntent.putExtra("filename_hindi","health_monitoring_hindi.pdf");

                startActivity(pIntent);

            }
        });




    }

    public void download(View v)
    {

        String langPrefType  = Utilities.getDataFromSharedpref( getApplicationContext(), Utilities.KEY_LANGUAGE_PREF);
        if(langPrefType!=null) {
            int lang = Integer.parseInt(langPrefType);
            Log.v("Lang",langPrefType+" "+lang);

            if(lang==0) {
                String url="https://drive.google.com/open?id=19TQxKmwXR6Fj_OBrRQWVSqDT0KYpMSPZ";

                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }else{
                //language is english
                PdfRenderFragment p= new PdfRenderFragment();
                Intent i= new Intent(getApplicationContext(),LibraryPDF.class);
                 i.putExtra("filename_hindi","about_illness_hindi.pdf");
                startActivity(i);

            }

        }else{
//            language is english by default
            String url="https://drive.google.com/open?id=19TQxKmwXR6Fj_OBrRQWVSqDT0KYpMSPZ";

            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse(url));
            startActivity(i);

        }

    }

    public void view(View v)
    {
        TextView titleView = (TextView) findViewById(R.id.pdf_item_name);
        String title = titleView.getText().toString()+".pdf";

        File pdfFile = new File(Environment.getExternalStorageDirectory().toString() + "/Saksham/" + title);  // -> filename = maven.pdf
        Log.d("LIB-DIR", "view: "+pdfFile.toString());

        Uri path = Uri.fromFile(pdfFile);
        Intent pdfIntent = new Intent(Intent.ACTION_VIEW);
        pdfIntent.setDataAndType(path, "application/pdf");
        pdfIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);

        Intent intent = Intent.createChooser(pdfIntent, "Open File");

        try{
            startActivity(intent);
        }catch(ActivityNotFoundException e){
            Toast.makeText(getApplicationContext(), "No Application available to view PDF", Toast.LENGTH_SHORT).show();
        }
    }

    private class DownloadFile extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... strings) {
            String fileUrl = strings[0];   // -> http://maven.apache.org/maven-1.x/maven.pdf
            String fileName = strings[1];  // -> maven.pdf

            String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
            File folder = new File(extStorageDirectory, "Saksham");
            if(folder.mkdir())
               Log.d("LIB-DIR","CREATED");
            else
                Log.d("LIB-DIR","NOT CREATED");


            File pdfFile = new File(folder, fileName);

            try{
                pdfFile.createNewFile();
            }catch (IOException e){
                e.printStackTrace();
            }
            FileDownloader.downloadFile(fileUrl, pdfFile);
            return null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


}
