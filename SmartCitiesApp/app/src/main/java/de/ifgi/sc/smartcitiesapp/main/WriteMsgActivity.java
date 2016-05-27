package de.ifgi.sc.smartcitiesapp.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.ifgi.sc.smartcitiesapp.R;

public class WriteMsgActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    private final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1012;
    private GoogleMap mMap;
    private LinearLayout mapcontainer;
    private Marker msgLocMarker = null;
    private boolean markerPlacedPreviously = false;

    private String selected_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_msg);

        // add Back Button on Actionbar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // which topic was selected?
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selected_topic = extras.getString("TOPIC");
            setTitle(selected_topic);
        }

        // Add categories to the spinner:
        Spinner spn_category = (Spinner) findViewById(R.id.spn_category);
        final String[] values = new String[]{"Traffic", "Sports", "Restaurants",
                "Shopping", "placeholder1", "placeholder2", "placeholder3", "placeholder4",
                "placeholder5", "placeholder6"};
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, values);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_category.setAdapter(adapter);
        // check out position of selected_topic in values:
        int index = 0;
        for (int i = 0; i < values.length; i++) {
            if (selected_topic.equals(values[i])) {
                index = i;
                break;
            }
        }
        // set previously selected topic as default:
        spn_category.setSelection(index);

        // select expire date:
        Spinner spn_selectExpireTime = (Spinner) findViewById(R.id.spn_expireTime);
        final String[] expireDefaults = new String[]{"1 week", "5 days", "3 days", "2 days", "24 hours", "18 hours", "12 hours", "6 hours", "3 hours"};
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, expireDefaults);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_selectExpireTime.setAdapter(adapter2);
        spn_selectExpireTime.setSelection(0);

        // show/hide map according to checkbox "add a location?" - selection:
        CheckBox chb_addlocation = (CheckBox) findViewById(R.id.chb_addlocation);

        mapcontainer = (LinearLayout) findViewById(R.id.ll_mapcontainer);
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        chb_addlocation.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    // checkbox checked -> show map:
                    mapcontainer.setVisibility(View.VISIBLE);
                    mapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(GoogleMap googleMap) {
                            mMap = googleMap;
                            mMap.getUiSettings().setMapToolbarEnabled(false);
                            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    return true;
                                }
                            });
                            try {
                                mMap.setMyLocationEnabled(true);

                                // enable location service on phone if its not enabled already:
                                LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                                boolean gps_enabled = false;
                                boolean network_enabled = false;

                                try {
                                    gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                } catch (Exception ex) {
                                }

                                try {
                                    network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                                } catch (Exception ex) {
                                }

                                if (!gps_enabled && !network_enabled) {
                                    Toast.makeText(getApplicationContext(),"Please enable location service",Toast.LENGTH_LONG).show();
                                    // activate Location Service
                                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                    WriteMsgActivity.this.startActivity(myIntent);
                                }
                            } catch (SecurityException e) {

                                // request location permission to the user:
                                ActivityCompat.requestPermissions(WriteMsgActivity.this,
                                        new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                        MY_PERMISSION_ACCESS_COARSE_LOCATION);

                            } finally {

                            }
                            mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                                @Override
                                public void onMapClick(LatLng latLng) {
                                    if (markerPlacedPreviously) {
                                        msgLocMarker.remove();
                                    }
                                    msgLocMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                                    markerPlacedPreviously = true;
                                }
                            });
                        }
                    });
                } else {
                    // checkbox unchecked -> hide map:
                    mapcontainer.setVisibility(View.INVISIBLE);

                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    try {
                        mMap.setMyLocationEnabled(true);
                        // enable Location service on phone if its not enabled already:
                        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        boolean gps_enabled = false;
                        boolean network_enabled = false;

                        try {
                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch (Exception ex) {
                        }

                        try {
                            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        } catch (Exception ex) {
                        }

                        if (!gps_enabled && !network_enabled) {
                            // activate Location Service
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            WriteMsgActivity.this.startActivity(myIntent);
                        }
                    } catch (SecurityException e) {
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Grant location permission for HappyShare in your phone settings for a location-button.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
