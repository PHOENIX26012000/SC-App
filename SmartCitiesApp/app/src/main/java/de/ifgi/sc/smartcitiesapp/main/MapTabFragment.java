package de.ifgi.sc.smartcitiesapp.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class MapTabFragment extends Fragment implements OnMapReadyCallback{

    private View v;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Context mContext;
    private Zone current_selected_zone;
    private EnhancedPolygon current_zone;
    private ArrayList<Message> msgs;
    private ArrayList<Message> msgs_in_current_zone;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();

        // Get the current selected zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e){
            // TODO: if no zone is currently selected
            e.printStackTrace();
        }

        // get all msgs for this current selected zone:
        msgs = Messenger.getInstance().getAllMessages();
        msgs_in_current_zone = new ArrayList<Message>();
        // for each message m from the Messenger:
        for (Message m : msgs){
            // if m is inside current selected zone
            if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                    // TODO: and m has location information:
                    //&& ())
                msgs_in_current_zone.add(m);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_map, container, false);
            mapFragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this);
        } catch (InflateException e) {
        /* map is already there: dont redraw it, so that the cameraposition still shows the same excerpt, but replace
           the msgs' markers of the previous selected zone with those from the current selected zone: */
            // Get the current selected zone:
            Log.d("PlacesTab","Places tab newly opened.");
            try {
                current_selected_zone = ZoneManager.getInstance().getCurrentZone();
            } catch (NoZoneCurrentlySelectedException nzcse){
                // TODO: if no zone is currently selected
                nzcse.printStackTrace();
            }
            // get all msgs for the current selected zone:
            msgs = Messenger.getInstance().getAllMessages();
            msgs_in_current_zone = new ArrayList<Message>();
            // for each message m from the Messenger:
            for (Message m : msgs){
                // if m is inside current selected zone
                if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                    // TODO: and m has location information:
                    //&& ())
                    msgs_in_current_zone.add(m);
            }
            // remove previous msg markers:
            mMap.clear();
            // add new msg markers:
            // for each msg m in the current selected zone
            for (Message m : msgs_in_current_zone){
                // add a marker on the map:
                Marker currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(m.getLatitude(), m.getLongitude()))
                        .title(m.getTitle())
                        .snippet(m.getMsg()));
                // show the info-window:
                currentMarker.showInfoWindow();
            }
        }


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        // remove msgs' markers from previously selected zone and put msgs' markers for the current selected zone:
        if (mMap != null) {
            // Get the current selected zone:
            Log.d("PlacesTab", "Places tab newly opened.");
            try {
                current_selected_zone = ZoneManager.getInstance().getCurrentZone();
            } catch (NoZoneCurrentlySelectedException nzcse) {
                // TODO: if no zone is currently selected
                nzcse.printStackTrace();
            }
            // get all msgs for the current selected zone:
            msgs = Messenger.getInstance().getAllMessages();
            msgs_in_current_zone = new ArrayList<Message>();
            // for each message m from the Messenger:
            for (Message m : msgs) {
                // if m is inside current selected zone
                if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                    // TODO: and m has location information:
                    //&& ())
                    msgs_in_current_zone.add(m);
            }
            // remove previous msg markers:
            mMap.clear();
            // add new msg markers:
            // for each msg m in the current selected zone
            for (Message m : msgs_in_current_zone) {
                // add a marker on the map:
                Marker currentMarker = mMap.addMarker(new MarkerOptions()
                        .position(new LatLng(m.getLatitude(), m.getLongitude()))
                        .title(m.getTitle())
                        .snippet(m.getMsg()));
                // show the info-window:
                currentMarker.showInfoWindow();
            }

            // create a map polygon for the current selected Zone:
            int[] rgb = {100,255,100};
            current_zone = new EnhancedPolygon(
                    current_selected_zone.getPolygon(),
                    rgb,
                    current_selected_zone.getName());

            // add zone's polygon onto the map + safe its polygon reference:
            current_zone.setPolygon(mMap.addPolygon(current_zone.getPolygon()));

            // zoom into the polygon of the current selected zone:
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : current_zone.getPoints()){
                builder.include(point);
            }
            LatLngBounds bounds = builder.build();
            int padding = 0;
            CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
            mMap.animateCamera(cu);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setMapToolbarEnabled(false);

        // enable location button
        try {
            mMap.setMyLocationEnabled(true);

            // enable location service on phone if its not enabled already:
            LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
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
                Toast.makeText(getActivity(),"Please enable location service",Toast.LENGTH_LONG).show();
                // activate Location Service
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapTabFragment.this.startActivity(myIntent);
            }
        } catch (SecurityException e) {
            Log.d("maptab","Security Exception: " + e);
            // request location permission to the user:

        } finally {

        }

        // for each msg m in the current selected zone
        for (Message m : msgs_in_current_zone){
            // add a marker on the map:
            Marker currentMarker = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(m.getLatitude(), m.getLongitude()))
                .title(m.getTitle())
                .snippet(m.getMsg()));
            // show the info-window:
            currentMarker.showInfoWindow();
        }

        // change the showInfoWindow behaviour:
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

                info.addView(title);
                info.addView(snippet);

                return info;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });

        // create a map polygon for the current selected Zone:
        int[] rgb = {100,255,100};
        current_zone = new EnhancedPolygon(
                current_selected_zone.getPolygon(),
                rgb,
                current_selected_zone.getName());

        // add zone's polygon onto the map + safe its polygon reference:
        current_zone.setPolygon(mMap.addPolygon(current_zone.getPolygon()));

        // zoom into the polygon of the current selected zone:
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : current_zone.getPoints()){
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        int padding = 0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }
}
