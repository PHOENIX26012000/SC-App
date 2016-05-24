package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import de.ifgi.sc.smartcitiesapp.R;

public class MapTabFragment extends Fragment implements OnMapReadyCallback {

    private View v;
    private SupportMapFragment mapFragment;
    private GoogleMap mMap;
    private Context mContext;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = getActivity();
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
        /* map is already there, just return view as it is */
        }

        return v;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Germany and move the camera
        LatLng germany = new LatLng(51.9615, 7.6225);

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

        Marker trafficjam = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.96, 7.62))
                .title("Traffic")
                .snippet("Traffic Jam in the city center"));

        //trafficjam.setAlpha(0.0f);
        //trafficjam.setInfoWindowAnchor(.5f, 1.0f);
        trafficjam.showInfoWindow();

        Marker coffeecups = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(51.963, 7.625))
                .title("Restaurant")
                .snippet("Coffee “To-Go” with re-" + "\n" + "cycling cups at peet’s coffee."));

        //coffeecups.setAlpha(0.0f);
        //coffeecups.setInfoWindowAnchor(.5f, 1.0f);
        coffeecups.showInfoWindow();

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(germany, 14));
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                marker.showInfoWindow();
                return true;
            }
        });
    }
}
