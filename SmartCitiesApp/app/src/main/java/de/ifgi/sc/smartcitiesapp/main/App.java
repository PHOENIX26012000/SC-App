package de.ifgi.sc.smartcitiesapp.main;

import android.app.Application;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

/**
 * Base class for maintaining global application state.
 * The Application class is instantiated before any other class
 * when the process for your application/package is created.
 */
public class App extends Application {

    private static Zone defaultZone;    // default zone, available if user is not inside any zone.
    private final SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public final String DEFAULT_ZONE_ID = "UMPA-UMPA-UMPA-TÖTÖRÖÖÖ";

    @Override
    public void onCreate() {
        super.onCreate();
        initSingletons();
    }

    /**
     * initialise all global singleton variables
     */
    protected void initSingletons() {

        UIMessageManager.initInstance();        // Singleton of UIMessageManager
        ZoneManager.initInstance(this);         // Singleton of ZoneManager
        Messenger.initInstance(this);           // Singleton of Messenger
        MyLocationManager.initInstance(this);   // Singleton of MyLocationManager

    }

    /**
     * To be called, if server is not accessible or userlocation is not in any zone.
     *
     * @return
     */
    protected Zone getDefaultZone(LatLng userLocation) {
        // create the default zone:
        long expDateMillis = new Date().getTime() + 1000 * 3600 * 24 * 365; // 1 year
        Date expDate = new Date(expDateMillis);
        String[] topics = new String[6];
        topics[0] = "Eat & Drink";
        topics[1] = "Events";
        topics[2] = "Nightlive";
        topics[3] = "Sports";
        topics[4] = "Traffic";
        topics[5] = "Others";
        ArrayList<LatLng> pts = new ArrayList<LatLng>();
        // take some points around the user's location:
        // take a length of 0.01 Lat units
        // (note: mercator projection distortions 1 Lat != 1 Lon!
        // => result is ellipse, not circle [unless u're @ equator]):
        double length = 0.01d;
        for (int degree = 0; degree < 360; degree = degree + 10) {
            LatLng nextPoint = new LatLng(0, 0.1);
            // rotate the next Point by degree:
            nextPoint = new LatLng(
                    Math.sin(Math.toRadians(degree)) * length + userLocation.latitude,
                    Math.cos(Math.toRadians(degree)) * length + userLocation.longitude
            );
            pts.add(nextPoint);
        }

        defaultZone = new Zone("Default", DEFAULT_ZONE_ID, D_format.format(expDate), topics, pts);
        return defaultZone;
    }

}