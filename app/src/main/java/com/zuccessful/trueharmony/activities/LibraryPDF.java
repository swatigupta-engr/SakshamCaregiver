package com.zuccessful.trueharmony.activities;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;

public class LibraryPDF extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_library_pdf);
        Intent i=getIntent();
        String pdfName=null,pdfName_hindi;
        if(i!=null)
        {
            try {
                pdfName = i.getStringExtra("filename");
            }catch (Exception e){}
            pdfName_hindi=i.getStringExtra("filename_hindi");
            FragmentManager fragmentManager = getSupportFragmentManager();
           /* final FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            PdfRenderFragment p= new PdfRenderFragment();
            Bundle bundle = new Bundle();
            try {
                bundle.putString("filename", pdfName);
            }catch (Exception e){}
            bundle.putString("filename_hindi",pdfName_hindi);

            p.setArguments(bundle);
            fragmentTransaction.replace(R.id.fragment_content, p).commit();*/

            Intent pIntent = new Intent(LibraryPDF.this, PDFRenderActivity.class);
            pIntent.putExtra("filename",pdfName);
            pIntent.putExtra("filename_hindi",pdfName_hindi);

            startActivity(pIntent);
        }

    }
}
