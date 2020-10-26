package com.zuccessful.trueharmony.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.PsychoAdapter;
import com.zuccessful.trueharmony.fragments.InformationFragment;
import com.zuccessful.trueharmony.fragments.PdfRenderFragment;

import java.util.ArrayList;
import java.util.Arrays;

public class PSychoeducation extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String[] psychoQueDataset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_psychoeducation);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.psycho_education));

        /*Toolbar myToolbar = findViewById(R.id.my_toolbar);
        myToolbar.setTitle(R.string.psycho_edu_ques);
        setSupportActionBar(myToolbar);*/

    /*
        mRecyclerView = findViewById(R.id.my_recycler_view);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        psychoQueDataset = getResources().getStringArray(R.array.psycho_edu_que);

        // specify an adapter (see also next example)
        mAdapter = new PsychoAdapter(psychoQueDataset,this);
        mRecyclerView.setAdapter(mAdapter);*/
        /*FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        PdfRenderFragment p= new PdfRenderFragment();
        Bundle bundle = new Bundle();
        bundle.putString("filename","about_illness.pdf");
        bundle.putString("filename_hindi","health_monitoring_hindi.pdf");

        p.setArguments(bundle);
        fragmentTransaction.replace(R.id.fragment_content, p).commit();*/

        Intent pIntent = new Intent(PSychoeducation.this, PDFRenderActivity.class);
        pIntent.putExtra("filename","about_illness.pdf");
        pIntent.putExtra("filename_hindi","health_monitoring_hindi.pdf");

        startActivity(pIntent);

//        Bundle bundle = new Bundle();
//        ArrayList<String> questions = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.psycho_edu_que)));
//        bundle.putStringArrayList("questions", questions);
//        ArrayList<String> answers = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.psycho_edu_ans)));
//        bundle.putStringArrayList("answers", answers);
//        ArrayList<String> images = new ArrayList<>(Arrays.asList(getResources().getStringArray(R.array.psycho_edu_images)));
//        bundle.putStringArrayList("images", answers);
//        bundle.putInt("PsychoImages",1);
//        //fragment
//        InformationFragment infoFrag = new InformationFragment();
//        infoFrag.setArguments(bundle);
//        fragmentTransaction.replace(R.id.fragment_content, infoFrag).commit();
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


