package io.keepcube.kcapp;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.util.ArrayList;

import io.keepcube.kcapp.Fragment.AccessoriesFragment;
import io.keepcube.kcapp.Fragment.RoomsFragment;
import io.keepcube.kcapp.Tools.MaterialAnimatedFab;
import io.keepcube.kcapp.Tools.Snacker;

public class MainActivity extends AppCompatActivity {
    public static AccessoriesFragment accessoriesFrag = new AccessoriesFragment();
    public static RoomsFragment roomsFrag = new RoomsFragment();
    FragmentManager fragManager = this.getSupportFragmentManager();
    String TAG = "MainActivity";
    private MaterialSheetFab materialSheetFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        final Context context = this;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
//                        fragManager.beginTransaction().replace(R.id.fragment_container, dashboardFragment).commit();
                        Toast.makeText(context, "dashboard", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_rooms:
                        fragManager.beginTransaction().replace(R.id.fragment_container, roomsFrag).commit();
                        break;

                    case R.id.nav_accessories:
                        fragManager.beginTransaction().replace(R.id.fragment_container, accessoriesFrag).commit();
                        break;

                    case R.id.nav_tools:
//                        fragManager.beginTransaction().replace(R.id.fragment_container, roomsFrag).commit();
                        Toast.makeText(context, "tůlz", Toast.LENGTH_SHORT).show();
                        break;
                }

                ((DrawerLayout) findViewById(R.id.drawer_layout)).closeDrawer(GravityCompat.START);
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
        materialSheetFab.setEventListener(new MaterialSheetFabEventListener() {
            @Override
            public void onSheetShown() {
                if (Snacker.isShown()) materialSheetFab.showFab(0, Snacker.getHeight());
            }
        });


        findViewById(R.id.fab_sheet_item_add_device).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                materialSheetFab.hideSheet();

                new MaterialDialog.Builder(context)
                        .title("Create device")
                        .customView(R.layout.di_device_input, true /*wrapInScrollView*/)
                        // TODO: 20.7.17 .autoDismiss(false)
                        .positiveText(R.string.agree)
                        .negativeText(R.string.disagree)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new MaterialDialog.Builder(context)
                                        .title("Scan QR code")
                                        .content("TODO: QR reader")
                                        .positiveText(R.string.agree)
                                        .negativeText(R.string.disagree)
                                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                                            @Override
                                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                new MaterialDialog.Builder(context)
                                                        .title("Choose a room")
                                                        .positiveText(R.string.choose)
                                                        .negativeText(R.string.disagree)
                                                        .items(roomsFrag.getRoomsNamesList()) // TODO: 20.7.17 brát z globálních seznamů
                                                        .itemsCallbackSingleChoice(accessoriesFrag.getSelTabPos(), new MaterialDialog.ListCallbackSingleChoice() {
                                                            @Override
                                                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                                Log.d(TAG, "onSelection: which:" + which);
                                                                Log.d(TAG, "onSelection: text:" + text);
                                                                // samozřejmě že ne
                                                                Snacker.make(v, "Device added successfully!", Snacker.LENGTH_SHORT).show();
                                                                return true;
                                                            }
                                                        }).show();
                                            }
                                        }).show();
                            }
                        }).show();
            }
        });


        findViewById(R.id.fab_sheet_item_add_room).

                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        materialSheetFab.hideSheet();

                        final View roomInputLayout = getLayoutInflater().inflate(R.layout.di_room_input, null);


                        final TextInputLayout roomNameInputLayout = (TextInputLayout) roomInputLayout.findViewById(R.id.roomNameInputLayout);
                        final ArrayList<String> roomsNamesList = roomsFrag.getRoomsNamesList();

                        ((TextInputEditText) roomInputLayout.findViewById(R.id.roomNameInput)).addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {

                            }

                            @Override
                            public void afterTextChanged(Editable s) {
                                if (roomsNamesList.contains(s.toString()))
                                    roomNameInputLayout.setError("This name is already taken!");
                                else if (roomNameInputLayout.isErrorEnabled())
                                    roomNameInputLayout.setErrorEnabled(false);
                            }
                        });


                        new MaterialDialog.Builder(context)
                                .title(R.string.add_room)
                                .customView(roomInputLayout, true /*wrapInScrollView*/)
                                .positiveText(R.string.agree)
                                .negativeText(R.string.disagree)
                                .autoDismiss(false)
                                .onPositive(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        View view = dialog.getCustomView();

                                        TextInputEditText roomName = (TextInputEditText) view.findViewById(R.id.roomNameInput);
                                        String name = roomName.getText().toString();

                                        if (roomsFrag.getRoomsNamesList().contains(name)) {
                                            Toast.makeText(context, "Choose another name, this is already taken", Toast.LENGTH_SHORT).show();
                                        } else {
                                            roomsFrag.adapter.add(name, 0); // TODO: 20.7.17 dat vedet serveru o zmene
                                            dialog.dismiss();
                                        }
                                    }
                                })
                                .onNegative(new MaterialDialog.SingleButtonCallback() {
                                    @Override
                                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                        dialog.dismiss();
                                    }
                                })
                                .show();

                    }
                });


        fragManager.beginTransaction().

                replace(R.id.fragment_container, accessoriesFrag).

                commit();

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

