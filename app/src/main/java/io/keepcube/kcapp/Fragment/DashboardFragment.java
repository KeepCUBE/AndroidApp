package io.keepcube.kcapp.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.keepcube.kcapp.Fragment.Tab.TabDashboardFragment;
import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashboardFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private FragAdapter adapter;


    public DashboardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();
        final Context context = getContext();


        // Title
        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar)).setTitle(getString(R.string.dashboard));


        // Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Tabs
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        adapter = new FragAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        adapter.addFrag(new TabDashboardFragment(/* blank template */), "Group 1");
//        adapter.addFrag(new TabDashboardFragment(/* blank template */), "Group 2");
//        adapter.addFrag(new TabDashboardFragment(/* blank template */), "Group 3");
        adapter.notifyDataSetChanged();


        return view;
    }


    public int getSelTabPos() {
        return tabLayout.getSelectedTabPosition();
    }


    private static class FragAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragList = new ArrayList<>();
        private final List<String> fragTitleList = new ArrayList<>();

        FragAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragList.get(position);
        }

        @Override
        public int getCount() {
            return fragList.size();
        }

        void addFrag(Fragment fragment, String title) {
            fragList.add(fragment);
            fragTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragTitleList.get(position);
        }
    }


}
