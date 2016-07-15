package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.interfaces.LocationChangedListener;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class SelectZoneActivity extends AppCompatActivity implements OnMapReadyCallback, LocationChangedListener{

    public final static int ZONE_SELECTED_SUCCESSFUL = 15335815;

    private GoogleMap map;
    private ArrayList<EnhancedPolygon> zones;
    private Spinner spn_zoneSelecter;
    private ArrayList<Zone> zonesFromDB;
    private LatLng userLocation;
    private LocationManager lm;
    private Zone current_selected_zone;
    private int current_focused_zone_index = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_zoneselect);
        mapFragment.getMapAsync(this);
        ImageButton img_nextZone = (ImageButton) findViewById(R.id.btn_moveToNextZone);
        img_nextZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // animate Camera to next zone:
                current_focused_zone_index = (current_focused_zone_index+1) % zones.size();
                // zoom into polygons:
                LatLngBounds.Builder builder = new LatLngBounds.Builder();
                    for (LatLng pt : zones.get(current_focused_zone_index).getPoints()) {
                        builder.include(pt);
                    }
                LatLngBounds bounds = new LatLngBounds(new LatLng(0,0), new LatLng(0,0));
                try {
                    bounds = builder.build();
                } catch (IllegalStateException ise){
                    // if here, then no zone contains the user location... er..
                    // okay... let's give some feedback to the user, and finish the useless zoneselection Activity:
                    Log.e("SelectZone", "Error on next zone: " + ise.getMessage());
                }
                int padding = 0;
                CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                try {
                    map.animateCamera(cu);
                } catch (IllegalStateException ise) {
                    Log.e("SelectZone", "Error on next zone: " + ise.getMessage());
                }
                // select the zone in the UI:
                for (int i=0; i<zones.size(); i++){
                    Polygon selected = zones.get(i).getPolygonRef();
                    if (i == current_focused_zone_index) {
                        selected.setFillColor(Color.argb(200, 50, 255, 50));
                        selected.setZIndex(110);
                        spn_zoneSelecter.setSelection(i);
                    } else {
                        zones.get(i).setDefaultColor();
                        selected.setZIndex(100);
                    }
                }
            }
        });

        // access the current user location:
        try {
            userLocation = MyLocationManager.getInstance().getUserLocation();
        } catch (NoLocationKnownException e){
            Log.e("SelectZone", "No Location known. Finishing Activity...");
            finish();
        }

        // access the ZoneManager and get a list of all zones containing current user location:
        zonesFromDB = new ArrayList<Zone>();
        zonesFromDB = ZoneManager.getInstance().getCurrentZones(userLocation);
        // if the user is still in int he current selected zone, preselect it as default:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e) {
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }
        int index_selected = 0;
        for (int i=0; i<zonesFromDB.size();i++){
            if (current_selected_zone.getZoneID().equals(zonesFromDB.get(i).getZoneID()))
                index_selected = i;
        }

        zones = new ArrayList<EnhancedPolygon>();

        for (Zone z : zonesFromDB){
            // create enhanced Polygon for zone z with random color:
            int[] rgb = {((int) (Math.random()*255)),
                    ((int) (Math.random()*255)),
                    ((int) (Math.random()*255))};
            EnhancedPolygon ep = new EnhancedPolygon(z.getPolygon(), rgb, z.getName());
            zones.add(ep);
            Log.d("zons","zone name:"+z.getName());
        }

        // add zone names into spinner:
        spn_zoneSelecter = (Spinner) findViewById(R.id.spn_zoneSelecter);
        final String[] zoneNames = new String[zones.size()];
        for (int i=0; i<zones.size();i++){
            zoneNames[i] = zones.get(i).getName();
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, zoneNames);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_zoneSelecter.setAdapter(adapter);
        spn_zoneSelecter.setSelection(index_selected);

        spn_zoneSelecter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                for (int i=0; i<zones.size();i++){
                    if (i != position){
                        zones.get(i).setDefaultColor();
                        zones.get(i).getPolygonRef().setZIndex(100);
                    } else {
                        Polygon selected = zones.get(position).getPolygonRef();
                        selected.setFillColor(Color.argb(200, 50, 255, 50));
                        selected.setZIndex(110);
                        // animate camera to focus the current selected zone:
                        // zoom into polygons:
                        LatLngBounds.Builder builder = new LatLngBounds.Builder();
                        for (LatLng pt : zones.get(position).getPoints()) {
                            builder.include(pt);
                        }
                        LatLngBounds bounds = new LatLngBounds(new LatLng(0,0), new LatLng(0,0));
                        try {
                            bounds = builder.build();
                        } catch (IllegalStateException ise){
                            // if here, then no zone contains the user location... er..
                            // okay... let's give some feedback to the user, and finish the useless zoneselection Activity:
                            Log.e("SelectZone", "Error on next zone: " + ise.getMessage());
                        }
                        int padding = 0;
                        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

                        try {
                            map.animateCamera(cu);
                        } catch (IllegalStateException ise) {
                            Log.e("SelectZone", "Error on next zone: " + ise.getMessage());
                        }

                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });

        Button btn_selectZone = (Button) findViewById(R.id.btn_confirmZoneSelection);
        btn_selectZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get + safe current selected zone
                int zoneNumber = spn_zoneSelecter.getSelectedItemPosition();
                Zone selectedZone = zonesFromDB.get(zoneNumber);
                ZoneManager.getInstance().setCurrentZone(selectedZone);
                finish();
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // add zones onto map + safe the polygon references to the EnhancedPolygon objects:
        for (EnhancedPolygon ep : zones){
            ep.setPolygon(map.addPolygon(ep.getPolygon()));
            // make polygon clickable:
            ep.getPolygonRef().setClickable(true);
        }

        // tell app what to do when a polygon is clicked:
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                for (int i=0; i<zones.size(); i++){
                    Polygon selected = zones.get(i).getPolygonRef();
                    if (selected.equals(polygon)) {
                        selected.setFillColor(Color.argb(200, 50, 255, 50));
                        selected.setZIndex(110);
                        spn_zoneSelecter.setSelection(i);
                    } else {
                        zones.get(i).setDefaultColor();
                        selected.setZIndex(100);
                    }
                }
            }
        });

        // zoom into polygons:
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (EnhancedPolygon ep : zones) {
            for (LatLng pt : ep.getPoints()) {
                builder.include(pt);
            }
        }
        LatLngBounds bounds = new LatLngBounds(new LatLng(0,0), new LatLng(0,0));
        try {
            bounds = builder.build();
        } catch (IllegalStateException ise){
            // if here, then no zone contains the user location... er..
            // okay... let's give some feedback to the user, and finish the useless zoneselection Activity:
            Toast.makeText(getApplicationContext(),"You are currently in no zone. Default zone is selected til you enter an existing one", Toast.LENGTH_LONG ).show();
            finish();
        }
        int padding = 0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        try {
            map.animateCamera(cu);
        } catch (IllegalStateException ise) {
            finish();
        }
        try {
            map.setMyLocationEnabled(true);

            // enable location service on phone if its not enabled already:
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
            boolean network_enabled = false;

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!network_enabled) {
                Toast.makeText(this,"Please enable location service",Toast.LENGTH_LONG).show();
                // activate Location Service
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                SelectZoneActivity.this.startActivity(myIntent);
            }
        } catch (SecurityException e) {
            Log.d("maptab","Security Exception: " + e);
            // request location permission to the user:

        } finally {

        }
    }

    @Override
    public void onLocationChanged(LatLng newLocation) {
        userLocation = new LatLng(
                newLocation.latitude,
                newLocation.longitude
        );
    }

    @Override
    public void onBackPressed() {
        // do not close the Activity. The user _must_ select a zone!
        // do nothing.
        Toast.makeText(getApplicationContext(), "You have to select a zone!", Toast.LENGTH_LONG).show();
    }
}
