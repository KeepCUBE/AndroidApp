package io.keepcube.kcapp.Fragment.Tab;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class TabDashboardFragment extends Fragment {


    public TabDashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.tab_fragment_dashboard, container, false);
    }

}