package com.zuccessful.trueharmony.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zuccessful.trueharmony.R;
import com.zuccessful.trueharmony.adapters.MyDayAdapter;
import com.zuccessful.trueharmony.pojo.MyDayQuestions;

import java.util.List;

public class MyDayFragment extends Fragment {

    private RecyclerView myDayRecyclerView;
    List<MyDayQuestions> quesList;
    private OnFragmentInteractionListener mListener;

    public MyDayFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        MyDayQuestions Q1 = new MyDayQuestions();
        MyDayQuestions Q2 = new MyDayQuestions();
        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_day, container, false);

        myDayRecyclerView = (RecyclerView) view.findViewById(R.id.myDayRecyclerView);
        myDayRecyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        // 3. create an adapter
        MyDayAdapter qAdapter = new MyDayAdapter(quesList);
        // 4. set adapter
        myDayRecyclerView.setAdapter(qAdapter);
        // 5. set item animator to DefaultAnimator
        myDayRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}

