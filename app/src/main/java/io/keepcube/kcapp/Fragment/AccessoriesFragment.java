package io.keepcube.kcapp.Fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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

import io.keepcube.kcapp.Data.Home;
import io.keepcube.kcapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class AccessoriesFragment extends Fragment {
    private TabLayout tabLayout;
    private ViewPager viewPager;

    public AccessoriesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_accessories, container, false);
        final AppCompatActivity activity = (AppCompatActivity) getActivity();


        // Title
        ((CollapsingToolbarLayout) view.findViewById(R.id.collapsing_toolbar)).setTitle(getString(R.string.accessories));


        // Toolbar
        Toolbar toolbar = (Toolbar) view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(activity, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();


        // Tabs
        FragAdapter adapter = new FragAdapter(getChildFragmentManager());
        viewPager = (ViewPager) view.findViewById(R.id.viewpager);
        tabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);


        // Rooms listener
        Home.setOnRoomChangedListener(adapter);


        return view;
    }

    public int getSelTabPos() {
        return tabLayout == null ? 0 : tabLayout.getSelectedTabPosition();
    }


    private class FragAdapter extends FragmentPagerAdapter implements Home.OnRoomChangedListener {

        FragAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public void onRoomAdded(int position, @NonNull String name, @Nullable String description) {
            notifyDataSetChanged();
            tabLayout.setScrollPosition(position, 0f, true);
            viewPager.setCurrentItem(position);
        }

        @Override
        public void onRoomRemoved(int position, @NonNull String name, @Nullable String description) {
            notifyDataSetChanged();
        }

        @Override
        public Fragment getItem(int position) {
            return Home.room(position).getDevicesFrag();
        }

        @Override
        public int getCount() {
            return Home.numberOfRooms();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return Home.room(position).name;
        }

    }
}
