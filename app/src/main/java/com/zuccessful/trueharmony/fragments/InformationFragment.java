package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.Toaster;
import com.zuccessful.trueharmony.adapters.VerticlePagerAdapter;
import com.zuccessful.trueharmony.utilities.VerticalViewPager;

import java.util.ArrayList;

public class InformationFragment extends Fragment {

    ImageButton leftNav, rightNav;
    View view;
    String[] ques;
    String[] ans;
    private VerticalViewPager verticalViewPager;
    private Context mContext;
    private TypedArray imagesArray;

    public InformationFragment() {
        // Required empty public constructor
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_information, container, false);
        mContext = getContext();

        //access data from the calling activity, i.e., question and answers
        Bundle bundle = this.getArguments();
        ArrayList<String> q = bundle.getStringArrayList("questions");
        ques = (q).toArray(new String[q.size()]);
        ArrayList<String> a = bundle.getStringArrayList("answers");
        ans = (a).toArray(new String[a.size()]);
        int mod = bundle.getInt("PsychoImages");
        if (mod == 1){
            imagesArray = getResources().obtainTypedArray(R.array.psycho_edu_images);
        }else{
            imagesArray = getResources().obtainTypedArray(R.array.med_adh_images);
        }

        verticalViewPager = view.findViewById(R.id.verticalViewPager);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        verticalViewPager.setOffscreenPageLimit(1);
        verticalViewPager.setAdapter(new VerticlePagerAdapter(mContext, ques, ans,imagesArray));
        leftNav = view.findViewById(R.id.left_nav);
        rightNav = view.findViewById(R.id.right_nav);

        verticalViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if(position==0){
                    leftNav.setVisibility(View.INVISIBLE);
                }
                else
                    leftNav.setVisibility(View.VISIBLE);

                if(position==ques.length-1)
                    rightNav.setVisibility(View.INVISIBLE);
                else
                    rightNav.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



        // Images left navigation
        leftNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verticalViewPager.arrowScroll(View.FOCUS_LEFT);
            }
        });

        // Images right navigatin
        rightNav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    verticalViewPager.arrowScroll(View.FOCUS_RIGHT);
                }catch (Exception e){
                    Toaster.showShortMessage("Extra Page!");
                }
            }
        });
    }
}

