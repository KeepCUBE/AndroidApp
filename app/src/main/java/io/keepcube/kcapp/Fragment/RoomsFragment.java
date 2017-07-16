package io.keepcube.kcapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RoomsFragment extends Fragment {


    public RoomsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_rooms, container, false);
        AppCompatActivity activity = (AppCompatActivity) getActivity();
        Context context = getContext();


        return view;
    }

}
