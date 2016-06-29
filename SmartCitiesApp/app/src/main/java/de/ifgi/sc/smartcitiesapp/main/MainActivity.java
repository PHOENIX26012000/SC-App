package de.ifgi.sc.smartcitiesapp.main;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.nearby.Nearby;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.p2p.P2PManager;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;


public class MainActivity extends AppCompatActivity {

    protected App app;
	private final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 10042; // just a random int resource.

    public static final String TAG = MainActivity.class.getSimpleName();

    private Zone current_selected_zone;
    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private String clientID;

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

        // create an example zone:
        long expDateMillis = new Date().getTime()+1000*3600*24*14; // 2 weeks
        Date expDate = new Date(expDateMillis);
        String[] topics = new String[3];
        topics[0] = "Traffic"; topics[1] = "Sports"; topics[2] = "Restaurants";
        ArrayList<LatLng> pts = new ArrayList<LatLng>();
        pts.add(new LatLng(51.9607, 7.6261));
        pts.add(new LatLng(51.96, 7.617));
        pts.add(new LatLng(51.978,7.622));
        Zone zone1 = new Zone("examplezone1",UUID.randomUUID().toString(),D_format.format(expDate), topics,pts);
        // create another example zone:
        expDateMillis = new Date().getTime()+1000*3600*3; // 3 hours
        expDate = new Date(expDateMillis);
        topics = new String[2];
        topics[0] = "Traffic"; topics[1] = "Shopping";
        pts = new ArrayList<LatLng>();
        pts.add(new LatLng(51.9607, 7.6261));
        pts.add(new LatLng(51.978,7.622));
        pts.add(new LatLng(51.968,7.631));
        Zone zone2 = new Zone("examplezone2",UUID.randomUUID().toString(),D_format.format(expDate), topics,pts);

        // add zone1, zone2 to ZoneManager:
        ArrayList<Zone> zones = new ArrayList<Zone>();
        zones.add(zone1);
        zones.add(zone2);
        // done once, so do not repeat the next line at several app starts!
        // ZoneManager.getInstance().updateZonesInDatabase(zones);

        // test if the saved zones are loaded from the ZoneManager methods:
        ArrayList<Zone> zonesFromDB = new ArrayList<Zone>();
        zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        for (Zone z : zonesFromDB){
            Log.d(TAG,"zone from db: "+z.getName());
        }

        // generate the Client_ID:
        clientID = UUID.randomUUID().toString();

        // create an example msg:
        Date creationDate = new Date(); // now
        expDateMillis = creationDate.getTime()+1000*3600*18; // 18 hours
        expDate = new Date(expDateMillis);
        Message msg1 = new Message(
                clientID, UUID.randomUUID().toString(),
                zonesFromDB.get(0).getZoneID(), creationDate,
                51.9707, 7.6281, expDate, "Traffic", "Traffic Jam in the city center",
                "There is a traffic jam in the city center"
        );

        // send msg1 to the Messenger:
        ArrayList<Message> msgs = new ArrayList<Message>();
        msgs.add(msg1);
        // once done, dont repeat the next line at several app starts!
        // Messenger.getInstance().updateMessengerFromConnect(msgs);

        // test if the saved msgs are loaded from the Messenger methods:
        ArrayList<Message> msgsFromMessenger = new ArrayList<Message>();
        msgsFromMessenger = Messenger.getInstance().getAllMessages();
        for (Message m : msgsFromMessenger){
            Log.d(TAG,"msg from Messenger:" + m.getTitle()+":"+m.getMsg());
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

        // Start Messenger
        mMessenger = new Messenger(getApplicationContext(), mP2PManager);
        mMessenger.initialStartup(); // do server connection ...

        // create some sample topics:
        Topic traffic = new Topic("Traffic");
        Topic sports = new Topic("Sports");
        Topic restaurants = new Topic("Restaurants");
        Topic shopping = new Topic("Shopping");
        Topic cafe = new Topic("cafe");
        Topic bars = new Topic("Bars");

        // add some msgs to the topics:
        long expTime = System.currentTimeMillis()+1000*60*((int)Math.random()*7*24*60);
        Date expDate = new Date(expTime);
        Message m1 = new Message("Client_ID??", UUID.randomUUID().toString(),"ZONE ID??", new Date(),
                (Math.random()/2+49), (Math.random()/2+7.5), expDate,"Traffic", "Traffic Jam in the City center", "Explosions, fireballz, collisions, burning people.");
        traffic.addMsg(m1);

        /**
        traffic.addMsg("Better to walk rather than drive near.....");
        sports.addMsg("students beachvolleyball tournament at the castle");
        restaurants.addMsg("recyclable \\\"to-go\\\"-coffee cups at Franks Copy Shop");
        restaurants.addMsg("Visit Paradise for a nice Biriyani");
        shopping.addMsg("Missed Black friday? Clothes are 100% off at my place");
        cafe.addMsg("visit DarkCafe for a strong coffe");
        bars.addMsg("Enjoy at ......... ");
         */

        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                TopicTabFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("PLACES", null),
                MapTabFragment.class, null);

        // ask for permission ACCESS_COARSE_LOCATION:
        ActivityCompat.requestPermissions( MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                MY_PERMISSION_ACCESS_COARSE_LOCATION);

        //Zone-Select-Button:
        Button btn_selectZone = (Button) findViewById(R.id.btn_selectZone);
        btn_selectZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentSettings = new Intent(getApplicationContext(), SelectZoneActivity.class);
                startActivityForResult(intentSettings, 1);
            }
        });
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
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_item_settings:
                // Open the settings activity
                Intent intentSettings = new Intent(getApplicationContext(), SettingsActivity.class);
                startActivity(intentSettings);
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
                        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
                            Toast.makeText(this, "Please activate Location service.", Toast.LENGTH_LONG).show();
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            this.startActivity(myIntent);
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

}
