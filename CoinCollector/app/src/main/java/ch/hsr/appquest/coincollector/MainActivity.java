package ch.hsr.appquest.coincollector;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import org.altbeacon.beacon.Beacon;
import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Region;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class MainActivity extends AppCompatActivity implements BeaconConsumer {

    private static final String appUuid = "52495334-5696-4DAE-BEC7-98D44A30FFDA";
    private static final String beaconLayout = "m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24";

    private static final int PERMISSION_REQUEST_FINE_LOCATION = 1;
    private static final int PERMISSION_REQUEST_BACKGROUND_LOCATION = 2;
    private static final String TAG = "MainActivity";

    private BeaconManager beaconManager;
    private CoinManager coinManager;
    private SectionedRecyclerViewAdapter sectionAdapter;
    private NotificationUtil notificationUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setupBeaconManager();
        setupCoinManager();
        setupSectionedRecyclerView();
        setupNotificationUtil();
    }

    private void setupBeaconManager() {
        // TODO: Erstelle und konfiguriere den BeaconManager.


        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                beaconManager.bind(this);
            } else {
                if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_BACKGROUND_LOCATION)) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("This app needs background location access");
                    builder.setMessage("Please grant location access so this app can detect beacons in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> requestPermissions(new String[]{Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                            PERMISSION_REQUEST_BACKGROUND_LOCATION));
                    builder.show();
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons in the background.  Please go to Settings -> Applications -> Permissions and grant background location access to this app.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.setOnDismissListener(dialog -> {
                    });
                    builder.show();
                }

            }
        } else {
            if (this.shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION)) {
                requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_BACKGROUND_LOCATION},
                        PERMISSION_REQUEST_FINE_LOCATION);
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Functionality limited");
                builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.  Please go to Settings -> Applications -> Permissions and grant location access to this app.");
                builder.setPositiveButton(android.R.string.ok, null);
                builder.show();
            }

        }
    }

    private void setupCoinManager() { coinManager = new CoinManager(this); }

    private void setupSectionedRecyclerView() {
        sectionAdapter = new SectionedRecyclerViewAdapter();
        addCoinRegionSections();
        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), 3);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                switch (sectionAdapter.getSectionItemViewType(position)) {
                    case SectionedRecyclerViewAdapter.VIEW_TYPE_HEADER:
                        return 3;
                    default:
                        return 1;
                }
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(sectionAdapter);
    }

    private void setupNotificationUtil() {
        notificationUtil = new NotificationUtil(this);
        notificationUtil.createNotificationChannel();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST_FINE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "fine location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since location access has not been granted, this app will not be able to discover beacons.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }
                break;
            }
            case PERMISSION_REQUEST_BACKGROUND_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.d(TAG, "background location permission granted");
                } else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setTitle("Functionality limited");
                    builder.setMessage("Since background location access has not been granted, this app will not be able to discover beacons when in the background.");
                    builder.setPositiveButton(android.R.string.ok, null);
                    builder.show();
                }
            }
        }

        if (this.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_BACKGROUND_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                // TODO: Falls die Permissions erst hier vorhanden sind, musst du den BeaconManager hier konfigurieren.
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // TODO: Der BeaconManager wird hier nicht mehr gebraucht.
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuItem menuItem = menu.add("Reset");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(item -> {
            sectionAdapter.removeAllSections();
            coinManager.reset();
            addCoinRegionSections();
            sectionAdapter.notifyDataSetChanged();
            return true;
        });
        menuItem = menu.add("Log");
        menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);
        menuItem.setOnMenuItemClickListener(item -> {
            onLogAction();
            return false;
        });
        return true;
    }

    @Override
    public void onBeaconServiceConnect() {
        beaconManager.addRangeNotifier((beacons, region) -> {
            // TODO: Für jeden Beacon in der beacons Collection, rufe die Methode collectBeacon() auf.
            collectBeacon((Beacon) beacons);
        });
        // TODO: Starte hier das Suchen nach Beacons.
    }

    private void onLogAction() {
        // TODO: Vorbereitung und Absenden des Logbuch-Eintrags gemäss der vorgegebenen Formatierung. Verwende dazu die logJson() Methode der Klasse CoinManager.
    }

    private void addCoinRegionSections() {
        for (CoinRegion coinRegion : coinManager.getCoinRegions()) {
            sectionAdapter.addSection(new CoinRegionSection(coinRegion, this));
        }
    }

    private void collectBeacon(Beacon beacon) {
        int major = beacon.getId2().toInt();
        int minor = beacon.getId3().toInt();
        updateCoin(major, minor);
    }

    private void updateCoin(int major, int minor) {
        // TODO: Setze die Minor Nummer der gefundenen Münze und speichere das Ergebnis. Danach muss man auch noch die SectionedRecyclerView neu laden.
        coinManager.addCoin(major,minor);
        // TODO (optional): Zeige dem User eine lokale Notification. Dazu kannst Du die Klasse NotificationUtil verwenden.
    }

}
