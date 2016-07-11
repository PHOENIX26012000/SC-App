package de.ifgi.sc.smartcitiesapp.main;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.interfaces.MessageUIManager;
import de.ifgi.sc.smartcitiesapp.interfaces.MessagesObtainedListener;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.p2p.P2PManager;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;


public class MainActivity extends AppCompatActivity implements MessagesObtainedListener{

    protected App app;
	private final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 10042; // just a random unique int resource.
    private final int MY_PERMISSION_ACCESS_FINE_LOCATION = 10043;   // just a random unique int resource.

    public static final String TAG = MainActivity.class.getSimpleName();

    private Zone current_selected_zone;
    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private String clientID;
    private ArrayList<Message> relevant_msgs;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private LatLng userLocation;

    /**
     * P2P Manager that handles the main p2p message sharing of the app
     */
    public P2PManager mP2PManager;

    public Messenger mMessenger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

		// Create the application context and its global state variables.
        app = (App)getApplication();
        
        // Start P2P Messaging
        mP2PManager = new P2PManager(this);

        // in case of the notification about new retrieved msgs was clicked:
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null)
            {
                // Cry about not being clicked on
            }
            else if (extras.getBoolean("NotiClick"))
            {
                // notification was clicked, do some cool stuff now ;-)

                // update the TopicTabFragment:
                //ttf.
            }

        }

        // create an example zone:
        long expDateMillis = new Date().getTime()+1000*3600*24*14; // 2 weeks
        Date expDate = new Date(expDateMillis);
        String[] topics = new String[3];
        topics[0] = "Traffic"; topics[1] = "Sports"; topics[2] = "Restaurants";
        ArrayList<LatLng> pts = new ArrayList<LatLng>();
        pts.add(new LatLng(51.971, 7.530));
        pts.add(new LatLng(51.976, 7.613));
        pts.add(new LatLng(51.970, 7.644));
        pts.add(new LatLng(51.950, 7.641));
        pts.add(new LatLng(51.950, 7.535));
        Zone zone1 = new Zone("Münster",UUID.randomUUID().toString(),D_format.format(expDate), topics,pts);

        // create another example zone:
        expDateMillis = new Date().getTime()+1000*3600*3; // 3 hours
        expDate = new Date(expDateMillis);
        topics = new String[2];
        topics[0] = "Traffic"; topics[1] = "Shopping";
        pts = new ArrayList<LatLng>();
        pts.add(new LatLng(51.981, 7.565));
        pts.add(new LatLng(51.979, 7.617));
        pts.add(new LatLng(51.966, 7.599));
        pts.add(new LatLng(51.961, 7.564));
        Zone zone2 = new Zone("SodaSopa MS",UUID.randomUUID().toString(),D_format.format(expDate), topics,pts);

        // add zone1, zone2 to ZoneManager:
        ArrayList<Zone> zones = new ArrayList<Zone>();
        zones.add(zone1);
        zones.add(zone2);
        // If there are no zones in the DB, store the 2 example zone into it.
        if (ZoneManager.getInstance().getAllZonesfromDatabase().size()==0)
            ZoneManager.getInstance().updateZonesInDatabase(zones);

        // test if the saved zones are loaded from the ZoneManager methods:
        ArrayList<Zone> zonesFromDB = new ArrayList<Zone>();
        zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        for (Zone z : zonesFromDB){
            Log.d(TAG,"zone from db: "+z.getName());
        }

        // create an example msg:
        Date creationDate = new Date(); // now
        expDateMillis = creationDate.getTime()+1000*3600*18; // 18 hours
        expDate = new Date(expDateMillis);
        Message msg1 = new Message(UUID.randomUUID().toString(),
                zonesFromDB.get(0).getZoneID(), creationDate,
                51.9707, 7.6281, expDate, "Traffic", "Traffic Jam in the city center",
                "There is a traffic jam in the city center"
        );

        // send msg1 to the Messenger:
        ArrayList<Message> msgs = new ArrayList<Message>();
        msgs.add(msg1);
        // if there are no msgs stored in the DB yet, add the 1 example msg to the first example zone.
        if (Messenger.getInstance().getAllMessages().size()==0)
            Messenger.getInstance().updateMessengerFromConnect(msgs);

        // test if the saved msgs are loaded from the Messenger methods:
        ArrayList<Message> msgsFromMessenger = new ArrayList<Message>();
        msgsFromMessenger = Messenger.getInstance().getAllMessages();
        for (Message m : msgsFromMessenger){
            Log.d(TAG,"msg from Messenger:" +m.getZone_ID() +"-"+ m.getTitle()+":"+m.getMsg());
        }

        zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        for (Zone z : zonesFromDB){
            Log.d(TAG,"zone from db: "+z.getName());
        }

        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e){
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }

        // TODO: use setter instead:
        Messenger.getInstance().setP2PManager(mP2PManager);
        Messenger.getInstance().initialStartup();

        try {
            // enable Location service on phone if its not enabled already:
            locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

            // Define a listener that responds to location updates
            locationListener = new LocationListener() {
                public void onLocationChanged(Location location) {
                    Log.d(TAG,"onLocationChanged");
                    // Called when a new location is found by the network location provider.
                    userLocation = new LatLng(
                            location.getLatitude(),
                            location.getLongitude()
                    );
                }

                public void onStatusChanged(String provider, int status, Bundle extras) {
                    Log.d(TAG,"onStatusChanged");
                }

                public void onProviderEnabled(String provider) {
                    Log.d(TAG,"onProviderEnabled");
                }

                public void onProviderDisabled(String provider) {
                    Log.d(TAG,"onProviderDisabled");
                }
            };
            // Register the listener with the Location Manager to receive location updates
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

        } catch (SecurityException se) {
            // in case of forbidden permission to access the user location, ask for it:
            // ask for permission ACCESS_FINE_LCCATION:
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        //Zone-Select-Button:
        Button btn_selectZone = (Button) findViewById(R.id.btn_selectZone);
        btn_selectZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.e(TAG,"userlocation=("+userLocation.latitude+","+userLocation.longitude+")");
                Intent intentSettings = new Intent(getApplicationContext(), SelectZoneActivity.class);
                //intentSettings.putExtra("UserLocLat",userLocation.latitude);
                //intentSettings.putExtra("UserLocLon",userLocation.longitude);
                startActivityForResult(intentSettings, 1);
            }
        });

        // add the MessagesObtainedListener to the UIMessageManager:
        UIMessageManager.getInstance().setMessageObtainedListener(this);

        // Listener-Test-Button:
        Button btn_test = (Button) findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create a testing msg:
                ArrayList<Zone> zones = ZoneManager.getInstance().getAllZonesfromDatabase();
                Log.d("onClick","#zones: "+zones.size());
                Message msg = new Message(
                        UUID.randomUUID().toString(),
                        zones.get(0).getZoneID(), new Date(),
                        51.666, 7.622, new Date(new Date().getTime() + 1000 * 360),
                        "Sports", "Tennis", "Lorem ipssum dolor amet... Created at "+new Date()
                );
                Message msg2 = new Message(
                        UUID.randomUUID().toString(),
                        zones.get(0).getZoneID(), new Date(),
                        51.646, 7.632, new Date(new Date().getTime() + 1000 * 360),
                        "Restaurants", "Barcafe XY", "Lorem ipssum... Created at "+new Date()
                );
                ArrayList<Message> msgs = new ArrayList<Message>();
                msgs.add(msg);
                msgs.add(msg2);
                Messenger.getInstance().updateMessengerFromConnect(msgs);
                // update UI:
                UIMessageManager.getInstance().enqueueMessagesIntoUIFromP2P(msgs);
            }
        });

        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                TopicTabFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("PLACES", null),
                MapTabFragment.class, null);
    }

    @Override
    protected void onResume() {
        Log.i(TAG + " Main", "OnResume");
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.i(TAG + " Main", "OnPause");
        super.onPause();
    }

    @Override
    protected void onStart() {
        Log.i(TAG + " Main", "OnStart");
        super.onStart();
    }

    @Override
    protected void onStop() {
        Log.i(TAG + " Main", "OnStop");
        mP2PManager.setDisconnected();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG + " Main", "OnDestroy");
        try {
            mP2PManager.unpublish();
            mP2PManager.unsubscribe();
            mP2PManager.disconnect();
        } catch (java.lang.IllegalStateException e) {
            e.printStackTrace();
            Log.i(MainActivity.TAG, "GoogleAPIClient is currently not connected");
            // Add message to mPubMessages list anyway to be published on next connection
        }
        super.onDestroy();
    }

    // --- Menu ---

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }


    @Override
    /**
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                // Open the settings activity
                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentSettings);
                return true;
            case R.id.menu_item_about:
                return true;
            case R.id.menu_item_search:
                // Open the search activity
                Intent intentSearch = new Intent(getApplicationContext(), SearchActivity.class);
                startActivity(intentSearch);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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
                        // enable Location service on phone if its not enabled already:
                        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        boolean gps_enabled = false;

                        try {
                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch (Exception ex) {
                            Log.d(TAG,"gps provider access exception: "+ex);
                        }

                        if (!gps_enabled) {
                            // activate Location Service
                            Toast.makeText(this, "Please enable location service.", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            MainActivity.this.startActivity(myIntent);
                        }
                    } catch (SecurityException e) {
                        Log.d("Maptab", "another security exception: " + e);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Grant location permission for HappyShare in your phone settings for an enabled location-based service.", Toast.LENGTH_LONG).show();
                }

                return;
            }

            case MY_PERMISSION_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    try {
                        // enable Location service on phone if its not enabled already:
                        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);

                        // Define a listener that responds to location updates
                        locationListener = new LocationListener() {
                            public void onLocationChanged(Location location) {
                                Log.d(TAG,"onLocationChanged");
                                // Called when a new location is found by the network location provider.
                                userLocation = new LatLng(
                                        location.getLatitude(),
                                        location.getLongitude()
                                );
                            }

                            public void onStatusChanged(String provider, int status, Bundle extras) {
                                Log.d(TAG,"onStatusChanged");
                            }

                            public void onProviderEnabled(String provider) {
                                Log.d(TAG,"onProviderEnabled");
                            }

                            public void onProviderDisabled(String provider) {
                                Log.d(TAG,"onProviderDisabled");
                            }
                        };

                        // Register the listener with the Location Manager to receive location updates
                        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);

                        try {
                            // enable Location service on phone if its not enabled already:
                            LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                            boolean gps_enabled = false;

                            try {
                                gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                            } catch (Exception ex) {
                                Log.d(TAG,"gps provider access exception: "+ex);
                            }

                            if (!gps_enabled) {
                                // activate Location Service
                                Toast.makeText(this, "Please enable location service.", Toast.LENGTH_LONG).show();
                                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                MainActivity.this.startActivity(myIntent);
                            } else {
                                userLocation = new LatLng(
                                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLatitude(),
                                        locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER).getLongitude()
                                );
                            }
                        } catch (SecurityException e) {
                            Log.d("Maptab", "another security exception: " + e);
                        }
                    } catch (SecurityException e) {
                        Log.d("Maptab", "another security exception: " + e);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Grant location permission for HappyShare in your phone settings for a location-button.", Toast.LENGTH_LONG).show();
                }

                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public void onMessagesObtainedFromServer(ArrayList<Message> obtained) {
        boolean msg_within_current_zone_obtained = false;
        // messages obtained from server:
        // 1. check if new obtained msgs are within the current selected zone
        // figure out, which zone is currently selected:
        ArrayList<Zone> zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e){
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }
        // for each obtained msg m: check if m is inside the current_selected_zone:
        for (Message m : obtained){
            // m inside current selected zone?
            if (m.getZone_ID().equals(current_selected_zone.getZoneID())){
                // remember at least 1 obtained msg is inside the current selected zone:
                msg_within_current_zone_obtained = true;
                // mark this msg as relevant for updating the UI:
                relevant_msgs.add(m);
            }
        }
        // 2. if so, update the UI and create a notification for the user
        if (msg_within_current_zone_obtained) {
            // 2. if so, update the UI
            // update the UI:
            FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
            mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
            mTabHost.clearAllTabs();
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                    TopicTabFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator("PLACES", null),
                    MapTabFragment.class, null);
        }
    }

    @Override
    public void onMessagesObtainedFromP2P(ArrayList<Message> obtained) {
        boolean msg_within_current_zone_obtained = false;
        relevant_msgs = new ArrayList<Message>();
        // 1. check if new obtained msgs are within the current selected zone
        // figure out, which zone is currently selected:
        ArrayList<Zone> zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e){
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }
        // for each obtained msg m: check if m is inside the current_selected_zone:
        for (Message m : obtained){
            // m inside current selected zone?
            if (m.getZone_ID().equals(current_selected_zone.getZoneID())){
                // remember at least 1 obtained msg is inside the current selected zone:
                msg_within_current_zone_obtained = true;
                // mark this msg as relevant for updating the UI:
                relevant_msgs.add(m);
            }
        }
        // 2. if so, update the UI and create a notification for the user
        if (msg_within_current_zone_obtained) {

            // show notification:
            Uri sound = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sound_notify);
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                            .setColor(Color.argb(200, 230, 210, 130))
                            .setSound(sound)
                            .setAutoCancel(true)
                            .setSmallIcon(R.drawable.logo_happyshare)
                            .setContentTitle("HAPPY SHARE")
                            .setContentText("Messages retrieved from HappyShare users!");
            Intent resultIntent = new Intent(this, MainActivity.class);
            resultIntent.putExtra("NotiClick", true);
            // Because clicking the notification opens a new ("special") activity, there's
            // no need to create an artificial back stack.
            PendingIntent resultPendingIntent =
                    PendingIntent.getActivity(
                            this,
                            0,
                            resultIntent,
                            PendingIntent.FLAG_UPDATE_CURRENT
                    );
            mBuilder.setContentIntent(resultPendingIntent);

            // Sets an ID for the notification
            int mNotificationId = 001;
            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());

            // update the UI:
            FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
            mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
            mTabHost.clearAllTabs();
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                    TopicTabFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator("PLACES", null),
                    MapTabFragment.class, null);
        }

    }
}
