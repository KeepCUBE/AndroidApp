package io.keepcube.kcapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gordonwong.materialsheetfab.MaterialSheetFab;

import io.keepcube.kcapp.Fragment.AccessoriesFragment;
import io.keepcube.kcapp.Fragment.RoomsFragment;
import io.keepcube.kcapp.Tools.MaterialAnimatedFab;

public class MainActivity extends AppCompatActivity {

    FragmentManager fragManager = getSupportFragmentManager();
    AccessoriesFragment accessoriesFragment = new AccessoriesFragment();
    RoomsFragment roomsFragment = new RoomsFragment();
    private MaterialSheetFab materialSheetFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Context context = this;
        setContentView(R.layout.activity_main);


        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                int id = item.getItemId();

                switch (id) {
//                    case R.id.nav_dashboard:
//                        fragManager.beginTransaction().replace(R.id.fragment_container, dashboardFragment).commit();
//                        break;

                    case R.id.nav_rooms:
                        fragManager.beginTransaction().replace(R.id.fragment_container, roomsFragment).commit();
                        break;

                    case R.id.nav_accessories:
                        fragManager.beginTransaction().replace(R.id.fragment_container, accessoriesFragment).commit();
                        break;

//                    case R.id.nav_tools:
//                        fragManager.beginTransaction().replace(R.id.fragment_container, roomsFragment).commit();
//                        break;

                }

                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });


        MaterialAnimatedFab fab = (MaterialAnimatedFab) findViewById(R.id.fab);
        View sheetView = findViewById(R.id.fab_sheet);
        View overlay = findViewById(R.id.overlay);
        int sheetColor = ContextCompat.getColor(context, R.color.background_card);
        int fabColor = ContextCompat.getColor(context, R.color.colorAccent);

        // Initialize material sheet FAB
        materialSheetFab = new MaterialSheetFab<>(fab, sheetView, overlay, sheetColor, fabColor);


        findViewById(R.id.fab_sheet_item_add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                materialSheetFab.hideSheet();

                Toast.makeText(context, "divajs", Toast.LENGTH_SHORT).show();

                new MaterialDialog.Builder(context)
                        .title("Create device")
                        .content("This wizard will helps you with adding a new dwvice.")
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                Snackbar.make(v, "Device added successfully!", Snackbar.LENGTH_SHORT).show();
                                Toast.makeText(context, "Device added successfully!", Toast.LENGTH_SHORT).show();
                                // TODO: 12.7.17 pouzivat casteji ty toasty!
                            }
                        })
                        .show();

            }
        });


        findViewById(R.id.fab_sheet_item_add_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();
                Toast.makeText(context, "rum", Toast.LENGTH_SHORT).show();
            }
        });


        fragManager.beginTransaction().replace(R.id.fragment_container, new AccessoriesFragment()).commit();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Zettingz", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refreš", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else if (materialSheetFab.isSheetVisible()) materialSheetFab.hideSheet();
        else super.onBackPressed();
    }
}
