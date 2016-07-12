package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
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
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
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

public class MapTabFragment extends Fragment implements OnMapReadyCallback {

    private View v;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Context mContext;
    private Zone current_selected_zone;
    private EnhancedPolygon current_zone;
    private ArrayList<Message> msgs;
    private ArrayList<Message> msgs_in_current_zone;
    private ArrayList<Bitmap> markerBitmaps;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();


        // Get the current selected zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e) {
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager:
            current_selected_zone = ZoneManager.getInstance().getAllZonesfromDatabase().get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }

        // get all msgs for this current selected zone:
        msgs = Messenger.getInstance().getAllMessages();
        msgs_in_current_zone = new ArrayList<Message>();
        markerBitmaps = new ArrayList<Bitmap>();
        // for each message m from the Messenger:
        for (Message m : msgs) {
            // if m is inside current selected zone
            if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                // if m has location information
                if ((m.getLatitude() != null) && (m.getLongitude() != null)) {
                    msgs_in_current_zone.add(m);
                    // create custom marker image for message m:
                    LinearLayout ll = (LinearLayout) this.getLayoutInflater(Bundle.EMPTY)
                            .inflate(R.layout.custom_marker, null, false);
                    ll.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                    ll.layout(0, 0, ll.getMeasuredWidth(), ll.getMeasuredHeight());
                    ll.setDrawingCacheEnabled(true);
                    ll.buildDrawingCache();
                    Bitmap bm = ll.getDrawingCache();
                    markerBitmaps.add(bm);
                }
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
            Log.d("PlacesTab", "Places tab newly opened.");
            try {
                current_selected_zone = ZoneManager.getInstance().getCurrentZone();
            } catch (NoZoneCurrentlySelectedException nzcse) {
                // what do, if no zone is currently selected?
                // select the first zone of the zonemanager:
                current_selected_zone = ZoneManager.getInstance().getAllZonesfromDatabase().get(0);
                ZoneManager.getInstance().setCurrentZone(current_selected_zone);
            }
            updateMapMarkers();
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
                // what do, if no zone is currently selected?
                // select the first zone of the zonemanager:
                current_selected_zone = ZoneManager.getInstance().getAllZonesfromDatabase().get(0);
                ZoneManager.getInstance().setCurrentZone(current_selected_zone);
            }

            updateMapMarkers();

            // create a map polygon for the current selected Zone:
            int[] rgb = {100, 255, 100};
            current_zone = new EnhancedPolygon(
                    current_selected_zone.getPolygon(),
                    rgb,
                    current_selected_zone.getName());

            // add zone's polygon onto the map + safe its polygon reference:
            current_zone.setPolygon(mMap.addPolygon(current_zone.getPolygon()));

            // zoom into the polygon of the current selected zone:
            LatLngBounds.Builder builder = new LatLngBounds.Builder();
            for (LatLng point : current_zone.getPoints()) {
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
            boolean network_enabled = false;

            try {
                network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
            } catch (Exception ex) {
            }

            if (!network_enabled) {
                Toast.makeText(getActivity(), "Please enable location service", Toast.LENGTH_LONG).show();
                // activate Location Service
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                MapTabFragment.this.startActivity(myIntent);
            }
        } catch (SecurityException e) {
            Log.d("maptab", "Security Exception: " + e);
            // request location permission to the user:

        } finally {

        }

        updateMapMarkers();

        // change the showInfoWindow behaviour:
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            /**
             * show the title and the text of the message accordingly to it's marker
             */
            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());

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
        int[] rgb = {100, 255, 100};
        current_zone = new EnhancedPolygon(
                current_selected_zone.getPolygon(),
                rgb,
                current_selected_zone.getName());

        // add zone's polygon onto the map + safe its polygon reference:
        current_zone.setPolygon(mMap.addPolygon(current_zone.getPolygon()));

        // zoom into the polygon of the current selected zone:
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (LatLng point : current_zone.getPoints()) {
            builder.include(point);
        }
        LatLngBounds bounds = builder.build();
        int padding = 0;
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
        mMap.animateCamera(cu);
    }

    private void updateMapMarkers(){
        // get all msgs for the current selected zone:
        msgs = Messenger.getInstance().getAllMessages();
        msgs_in_current_zone = new ArrayList<Message>();
        markerBitmaps = new ArrayList<Bitmap>();
        // for each message m from the Messenger:
        initColors();
        for (Message m : msgs) {
            // if m's topic is subscribed:
            if (isTopicPreferred(current_selected_zone.getZoneID(),m.getTopic())) {
                // if m is inside current selected zone
                if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                    // if m has location information:
                    if ((m.getLatitude() != null) && (m.getLongitude() != null)) {
                        msgs_in_current_zone.add(m);

                        // create custom marker image for message m:
                        LinearLayout ll = new LinearLayout(getActivity());
                        ll.setBackgroundColor(Color.TRANSPARENT);
                        ll.setOrientation(LinearLayout.VERTICAL);

                        LinearLayout ll_text = new LinearLayout(getActivity());
                        ll_text.setBackgroundColor(Color.WHITE);
                        ll_text.setOrientation(LinearLayout.VERTICAL);

                        TextView topic = new TextView(getActivity());
                        int[] rgb = rgbColorFromTopic(m.getTopic());
                        topic.setTextColor(Color.rgb(rgb[0], rgb[1], rgb[2]));
                        topic.setText(m.getTopic());
                        topic.setGravity(Gravity.CENTER_HORIZONTAL);

                        TextView title = new TextView(getActivity());
                        title.setText(m.getTitle());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER_HORIZONTAL);

                        ll_text.addView(topic);
                        ll_text.addView(title);

                        TextView anchor = new TextView(getActivity());
                        anchor.setText("▼");
                        anchor.setTextColor(Color.WHITE);
                        anchor.setGravity(Gravity.CENTER_HORIZONTAL);

                        ll.addView(ll_text);
                        ll.addView(anchor);

                        ll.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
                        ll.layout(0, 0, ll.getMeasuredWidth(), ll.getMeasuredHeight());
                        ll.setDrawingCacheEnabled(true);
                        ll.buildDrawingCache();
                        Bitmap bm = ll.getDrawingCache();
                        markerBitmaps.add(bm);
                    }
            }
        }
        // remove previous markers
        mMap.clear();
        // add new msg markers:
        // for each msg m in the current selected zone
        for (int i = 0; i < msgs_in_current_zone.size(); i++) {
            Message m = msgs_in_current_zone.get(i);
            Bitmap bm = markerBitmaps.get(i);
            // add a marker on the map:
            Marker currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(m.getLatitude(), m.getLongitude()))
                    .snippet(m.getMsg())
                    .icon(BitmapDescriptorFactory.fromBitmap(bm))
            );
        }
    }

    /**
     * assignes a predefined color to a topic
     * @param topic the topic which a color needs to be assigned to
     * @return the color assigned to the topic
     */
    private int[] rgbColorFromTopic(String topic) {
        // if topic has alrdy assigned color, return it:
        for (int i = 0; i < used_topics.size(); i++) {
            if (used_topics.get(i).equals(topic)) {
                return used_colors.get(i);
            }
        }
        // otherwise, assign new color and return it:
        used_topics.add(topic);
        if (used_topics.size()>used_colors.size())
            used_colors.add(new int[] {(int) (Math.random()*255),(int) (Math.random()*255),(int) (Math.random()*255)});
        return used_colors.get(used_topics.size() - 1);
    }

    ArrayList<String> used_topics;
    ArrayList<int[]> used_colors;

    private void initColors() {
        used_colors = new ArrayList<int[]>();
        used_topics = new ArrayList<String>();
        used_colors.add(new int[]{255, 0, 0});
        used_colors.add(new int[]{0, 255, 0});
        used_colors.add(new int[]{0, 0, 255});
        used_colors.add(new int[]{255, 255, 0});
        used_colors.add(new int[]{0, 255, 255});
        used_colors.add(new int[]{255, 0, 255});
        used_colors.add(new int[]{255, 128, 0});
        used_colors.add(new int[]{0, 128, 255});
        used_colors.add(new int[]{128, 0, 255});
        used_colors.add(new int[]{128, 255, 0});
        used_colors.add(new int[]{0, 255, 128});
        used_colors.add(new int[]{255, 0, 128});
        used_colors.add(new int[]{128, 255, 128});
        used_colors.add(new int[]{128, 255, 128});
        used_colors.add(new int[]{255, 128, 128});
    }

    /**
     * checks, if a topic of a zone is subscribed by the user
     * @param zone_id - the zone, the topic is part of
     * @param topic - the topic, that is to be subscribed
     * @return true - if the topic is subscribed in that zone, false otherwise
     */
    private boolean isTopicPreferred(String zone_id,String topic){
        return PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getBoolean("pref_"+zone_id+"_"+topic,true);
    }
}
