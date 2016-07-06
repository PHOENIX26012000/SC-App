package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.security.Security;
import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class SelectZoneActivity extends AppCompatActivity implements OnMapReadyCallback{

    public final static int ZONE_SELECTED_SUCCESSFUL = 15335815;

    private GoogleMap map;
    private ArrayList<EnhancedPolygon> zones;
    private Spinner spn_zoneSelecter;
    private ArrayList<Zone> zonesFromDB;
    private LatLng userLocation;
    private LocationManager lm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_zoneselect);
        mapFragment.getMapAsync(this);

        // access the current user location:
        try {
            lm = (LocationManager) getSystemService(LOCATION_SERVICE);
            Location loc = lm
                    .getLastKnownLocation(LocationManager.GPS_PROVIDER);

            userLocation = new LatLng(
                    loc.getLatitude(),
                    loc.getLongitude()
            );

            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, new LocationListener() {
                @Override
                public void onLocationChanged(Location location) {

                }

                @Override
                public void onStatusChanged(String provider, int status, Bundle extras) {

                }

                @Override
                public void onProviderEnabled(String provider) {

                }

                @Override
                public void onProviderDisabled(String provider) {

                }
            });
        } catch ( SecurityException e){
            Log.e("selectZone","SecurityException: "+e);
        }

        // access the ZoneManager and get a list of all zones containing current user location:
        zonesFromDB = new ArrayList<Zone>();
        zonesFromDB = ZoneManager.getInstance().getCurrentZones(userLocation);

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
        spn_zoneSelecter.setSelection(0);

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

        // deprecated, but still working and updating the users location.
        map.setOnMyLocationChangeListener(new GoogleMap.OnMyLocationChangeListener() {
            @Override
            public void onMyLocationChange(Location location) {
                userLocation = new LatLng(
                        location.getLatitude(),
                        location.getLongitude()
                );
            }
        });

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
        LatLngBounds bounds = builder.build();
        int padding = 0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);

        map.animateCamera(cu);
        try {
            map.setMyLocationEnabled(true);

            // enable location service on phone if its not enabled already:
            lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
}
