package de.ifgi.sc.smartcitiesapp.main;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;

public class SelectZoneActivity extends AppCompatActivity implements OnMapReadyCallback{

    GoogleMap map;
    ArrayList<EnhancedPolygon> zones;
    ArrayList<PolygonOptions> polygons;
    Spinner spn_zoneSelecter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_zone);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map_zoneselect);
        mapFragment.getMapAsync(this);

        // create hardcoded zones:
        ArrayList<LatLng> polygon1 = new ArrayList<LatLng>();
        polygon1.add(new LatLng(51.9607, 7.6261));
        polygon1.add(new LatLng(51.96, 7.617));
        polygon1.add(new LatLng(51.978,7.622));
        int[] rgb = {255,0,0};
        EnhancedPolygon ep1 = new EnhancedPolygon(polygon1, rgb, "ZONE ONE");

        ArrayList<LatLng> polygon2 = new ArrayList<LatLng>();
        polygon2.add(new LatLng(51.955, 7.623));
        polygon2.add(new LatLng(51.963, 7.618));
        polygon2.add(new LatLng(51.965,7.638));

        int[] rgb2 = {0,255,0};
        EnhancedPolygon ep2 = new EnhancedPolygon(polygon2, rgb2, "ZONE TWO");

        zones = new ArrayList<EnhancedPolygon>();
        polygons = new ArrayList<PolygonOptions>();
        zones.add(ep1);
        zones.add(ep2);

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
                    } else {
                        Polygon selected = zones.get(position).getPolygonRef();
                        selected.setFillColor(Color.argb(200, 255, 255, 255));
                        selected.setZIndex(110);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;

        // add zones onto map + safe the polygon references to the EnhancedPolygon objects:
        zones.get(0).setPolygon(map.addPolygon(zones.get(0).getPolygon()));
        zones.get(1).setPolygon(map.addPolygon(zones.get(1).getPolygon()));
        // make zones clickable:
        zones.get(0).getPolygonRef().setClickable(true);
        zones.get(1).getPolygonRef().setClickable(true);

        // make polygons clickable:
        map.setOnPolygonClickListener(new GoogleMap.OnPolygonClickListener() {
            @Override
            public void onPolygonClick(Polygon polygon) {
                for (int i=0; i<2; i++){
                    Polygon selected = zones.get(i).getPolygonRef();
                    if (selected.equals(polygon)) {
                        selected.setFillColor(Color.argb(200, 255, 255, 255));
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
    }
}
