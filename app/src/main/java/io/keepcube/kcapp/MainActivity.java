package io.keepcube.kcapp;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.SurfaceTexture;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;
import com.gordonwong.materialsheetfab.MaterialSheetFab;
import com.gordonwong.materialsheetfab.MaterialSheetFabEventListener;

import java.io.IOException;

import io.keepcube.kcapp.Data.Dashboard;
import io.keepcube.kcapp.Data.Device;
import io.keepcube.kcapp.Data.Home;
import io.keepcube.kcapp.Data.Type;
import io.keepcube.kcapp.Fragment.AccessoriesFragment;
import io.keepcube.kcapp.Fragment.DashboardFragment;
import io.keepcube.kcapp.Fragment.RoomsFragment;
import io.keepcube.kcapp.Tools.Barcode.AutoFitTextureView;
import io.keepcube.kcapp.Tools.Barcode.Camera2Source;
import io.keepcube.kcapp.Tools.Barcode.Utils;
import io.keepcube.kcapp.Tools.MaterialAnimatedFab;
import io.keepcube.kcapp.Tools.Snacker;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CAMERA_RESULT = 1;
    final Handler handler = new Handler();
    private DashboardFragment dashFrag = new DashboardFragment();
    private AccessoriesFragment accessoriesFrag = new AccessoriesFragment();
    private RoomsFragment roomsFrag = new RoomsFragment();
    private View savedBarcodeSnackView;
    private FragmentManager fragManager = this.getSupportFragmentManager();
    private String TAG = "MainActivity";
    private MaterialSheetFab materialSheetFab;
    private AppCompatActivity activity = this;
    private String barcodeMessage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.activity_main);
        final Context context = this;
        Home.loadSync(context);
//        Home.Cube.setIP("192.168.0.2");
//        Home.autoSave(context, 10);

        Log.e("Paper keys", Paper.book().getAllKeys().toString());

        fadeFragment(dashFrag);

        final NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                // Handle navigation view item clicks here.
                switch (item.getItemId()) {
                    case R.id.nav_dashboard:
                        fadeFragment(dashFrag);
                        break;
                    case R.id.nav_rooms:
                        fadeFragment(roomsFrag);
                        break;
                    case R.id.nav_accessories:
                        fadeFragment(accessoriesFrag);
                        break;


                    case R.id.nav_tools:
                        Toast.makeText(context, "tůlz", Toast.LENGTH_SHORT).show();
                        break;

                    case R.id.nav_own_device:
                        Toast.makeText(context, "own device", Toast.LENGTH_SHORT).show();
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
                continueBarcodeInit(context, v);
            }
        });


        findViewById(R.id.fab_sheet_item_add_room).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                materialSheetFab.hideSheet();

                final View roomInputLayout = View.inflate(context, R.layout.di_room_input, null);
                final TextInputLayout roomNameInputLayout = (TextInputLayout) roomInputLayout.findViewById(R.id.roomNameInputLayout);
                ((TextInputEditText) roomInputLayout.findViewById(R.id.roomNameInput)).addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {

                    }

                    @Override
                    public void afterTextChanged(Editable name) {
                        if (Home.hasRoom(name.toString()))
                            roomNameInputLayout.setError(getString(R.string.name_taken));
                        else if (roomNameInputLayout.isErrorEnabled())
                            roomNameInputLayout.setErrorEnabled(false);
                    }
                });

                new MaterialDialog.Builder(context)
                        .title(R.string.add_room)
                        .customView(roomInputLayout, true /*wrapInScrollView*/)
                        .positiveText(R.string.positive_text)
                        .negativeText(R.string.negative_text)
                        .autoDismiss(false)
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                dialog.dismiss();
                            }
                        })
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                View view = dialog.getCustomView();
                                String name = ((TextInputEditText) view.findViewById(R.id.roomNameInput)).getText().toString();
                                String description = ((TextInputEditText) view.findViewById(R.id.roomDescriptionInput)).getText().toString();
                                if (description.isEmpty()) description = getResources().getString(R.string.no_description);

                                if (Home.hasRoom(name)) {
                                    Toast.makeText(context, R.string.choose_another_name, Toast.LENGTH_SHORT).show();
                                } else {
                                    dialog.dismiss();
                                    Home.addRoom(name, description);
                                }
                            }
                        }).show();

            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case REQUEST_CAMERA_RESULT:
                if (grantResults.length > 0 && grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(activity, R.string.cannot_camera_permission, Toast.LENGTH_LONG).show();
                } else {
                    continueBarcodeInit(this, null);
                }
                break;


            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }


    }

    void continueBarcodeInit(final Context context, final View xv) {
        if (xv != null) savedBarcodeSnackView = xv;
        final View v = savedBarcodeSnackView;

        new MaterialDialog.Builder(context)
                .title(R.string.create_device)
                .customView(R.layout.di_device_input, true /*wrapInScrollView*/)
                // TODO: 20.7.17 .autoDismiss(false)
                .positiveText(R.string.positive_text)
                .negativeText(R.string.negative_text)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull final MaterialDialog dialog, @NonNull DialogAction which) {
                        final String name = ((EditText) dialog.getCustomView().findViewById(R.id.deviceNameInput)).getText().toString();

                        // re-check permission
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);
                            return;
                        }


                        // setup the QR reader
                        final View qrPreviewLayout = View.inflate(context, R.layout.di_qr_preview, null);

                        TextView c2ninfo = (TextView) qrPreviewLayout.findViewById(R.id.c2ninfo);

                        final MaterialDialog qrDiag = new MaterialDialog.Builder(context)
                                .title(R.string.scan_code)
                                .customView(qrPreviewLayout, false /*wrapInScrollView*/)
                                .negativeText(R.string.negative_text)
                                .show();


                        final AutoFitTextureView cameraView = (AutoFitTextureView) qrPreviewLayout.findViewById(R.id.camera_view);
//                      final TextView barcodeInfo = (TextView) qrPreviewLayout.findViewById(R.id.code_info);


                        final BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context)
                                .setBarcodeFormats(Barcode.ALL_FORMATS)
                                .build();


                        final Camera2Source cameraSource = new Camera2Source.Builder(context, barcodeDetector)
                                .setFocusMode(Camera2Source.CAMERA_AF_CONTINUOUS_VIDEO)
                                .setFlashMode(Camera2Source.CAMERA_FLASH_AUTO)
                                .setFacing(Camera2Source.CAMERA_FACING_BACK)
                                .build();


                        c2ninfo.setText("Camera2Native = " + String.valueOf(cameraSource.isCamera2NativeCustom()));


                        cameraView.setSurfaceTextureListener(new TextureView.SurfaceTextureListener() {
                            @Override
                            public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
                                try {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                            cameraSource.start(cameraView, Utils.getScreenRotation(context));
                                        } else {
                                            if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                                                Toast.makeText(context, R.string.no_permission_camera, Toast.LENGTH_SHORT).show();
                                            }
                                            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_RESULT);
                                            qrDiag.dismiss(); // dialog will be showed again
                                        }
                                    } else {
                                        cameraSource.start(cameraView, Utils.getScreenRotation(context));
                                    }
                                } catch (IOException ie) {
                                    Log.e("CAMERA SOURCE", ie.getMessage());
                                }
                            }

                            @Override
                            public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {}

                            @Override
                            public void onSurfaceTextureUpdated(SurfaceTexture surface) {}

                            @Override
                            public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
                                cameraSource.stop();
                                return false;
                            }
                        });


                        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
                            @Override
                            public void receiveDetections(Detector.Detections<Barcode> detections) {
                                final SparseArray<Barcode> barcodes = detections.getDetectedItems();
                                if (barcodes.size() != 0) {
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            barcodeMessage = barcodes.valueAt(0).displayValue;
                                            qrDiag.dismiss();
                                            barcodeDetector.release();
                                        }
                                    });
                                }
                            }

                            @Override
                            public void release() {
                                new MaterialDialog.Builder(context)
                                        .title(R.string.choose_room)
                                        .content(barcodeMessage) // TODO: 9.8.17 remove
                                        .positiveText(R.string.choose)
                                        .negativeText(R.string.negative_text)
                                        .items(Home.getRoomsNamesList())
                                        .itemsCallbackSingleChoice(accessoriesFrag.getSelTabPos(), new MaterialDialog.ListCallbackSingleChoice() {
                                            @Override
                                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                                Log.d(TAG, "onSelection: which:" + which);
                                                Log.d(TAG, "onSelection: text:" + text);
                                                // samozřejmě že ne
                                                Snacker.make(v, getString(R.string.device_added_successfully), Snacker.LENGTH_SHORT).show();


//                                              int type = parseBarcodeMessage().getType(); // TODO: 10.8.17 !
//                                              int type = Type.LED;
                                                int type = Integer.parseInt(barcodeMessage);

                                                switch (type) {
                                                    case Type.LED:
                                                        Home.room(which).addDevice(new Device.Led(name));
                                                        Dashboard.registerDevice(which, Home.room(which).numberOfDevices() - 1); // TODO: 11.8.17 !!!!!!!!!!!!!!!!!!!!!
                                                        break;

                                                    case Type.SWITCH:
//                                                        Home.room(which).addDevice(new Device.Switch(name /*...*/));
                                                        break;

                                                    case Type.TEMPERATURE:

                                                        break;


                                                    default:
                                                        // TODO: 9.8.17 custom
                                                        break;
                                                }


                                                return true;
                                            }
                                        }).show();
                            }
                        });

                    }
                }).show();

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

        if (id == R.id.action_refresh) {
            Toast.makeText(this, "Refreš", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Zettingz", Toast.LENGTH_SHORT).show();
            return true;
        }
        if (id == R.id.action_clear_mem) {
            Paper.book().destroy();
            return true;
        }


        return super.onOptionsItemSelected(item);
    }


    private void fadeFragment(Fragment fragment) {
        fragManager.beginTransaction()
                .setCustomAnimations(R.anim.frag_in, R.anim.frag_out)
                .replace(R.id.fragment_container, fragment)
                .commit();
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) drawer.closeDrawer(GravityCompat.START);
        else if (materialSheetFab.isSheetVisible()) materialSheetFab.hideSheet();
        else super.onBackPressed();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Home.save();
    }


    @Override
    protected void onStop() {
        super.onStop();
        Home.save();
    }


}





