package de.ifgi.sc.smartcitiesapp.main;

import android.Manifest;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.interfaces.LocationChangedListener;
import de.ifgi.sc.smartcitiesapp.interfaces.MessagesObtainedListener;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.p2p.P2PManager;
import de.ifgi.sc.smartcitiesapp.server.ServerConnection;
import de.ifgi.sc.smartcitiesapp.settings.AboutActivity;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class MainActivity extends AppCompatActivity implements MessagesObtainedListener, LocationChangedListener {

    protected App app;

    // request codes:
    public static final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 10042; // just a random unique int resource.
    public static final int MY_PERMISSION_ACCESS_FINE_LOCATION = 10043;   // just a random unique int resource.
    public static final int LOCATION_SERVICE_PROMPT = 10044;              // ID for the location service prompt.
    public static final int ZONE_SELECTION_PROMPT = 10045;                // ID ZoneSelection.

    public static final String TAG = MainActivity.class.getSimpleName();

    private Zone current_selected_zone;
    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private ArrayList<Message> relevant_msgs;
    private LatLng userLocation;
    private int number_of_containing_zones = -1;
    private UpdateUiThread initThread;

    /**
     * P2P Manager that handles the main p2p message sharing of the app
     */
    public P2PManager mP2PManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "OnCreate");

        // Create the application context and its global state variables.
        if (app == null)
            app = (App) getApplication();

        Messenger.getInstance().deleteZoneMessage();

        // set default zone:
        current_selected_zone = app.getDefaultZone(new LatLng(51.96958, 7.5956));
        ZoneManager.getInstance().setCurrentZone(current_selected_zone);

        // add the MessagesObtainedListener to the UIMessageManager:
        UIMessageManager.getInstance().setMessageObtainedListener(this);

        // Start P2P Messaging
        mP2PManager = new P2PManager(this);

        // Forward P2PManager to the Messenger
        Messenger.getInstance().setP2PManager(mP2PManager);
        Messenger.getInstance().initialStartup();

        // uncomment next line and uninstall + run the app again to remove test zones and msgs.
        initialiseTestZonesAndMessages();

        /*
        // Testing
        ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> mPubMessagesTest = new ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> ();
        mPubMessagesTest.add(new de.ifgi.sc.smartcitiesapp.messaging.Message("m_id2", "z_id", new Date(), 51.0, 7.0, new Date(new Date().getTime()+600000), "top", "tit", "msg", true));
        mPubMessagesTest.add(new de.ifgi.sc.smartcitiesapp.messaging.Message("m_id3", "z_id1", new Date(), 52.0, 8.0, new Date(new Date().getTime()+600000), "top1", "tit1", "msg1", true));
        mP2PManager.shareMessage(mPubMessagesTest);
        */

        // in case of the notification about new retrieved msgs was clicked:
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras == null) {
                // Activity created without notification. do nothing.
            } else if (extras.getBoolean("NotiClick")) {
                // Activity created from notification, update the UserInterface with new messages:

                // select the previously selected zone:
                String zone_ID = extras.getString("ZoneID");
                // find that zone:
                ArrayList<Zone> zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
                int index = 0;
                do {
                    // select it:
                    current_selected_zone = zonesFromDB.get(index);
                    index++;
                } while (!current_selected_zone.getZoneID().equals(zone_ID));
                ZoneManager.getInstance().setCurrentZone(current_selected_zone);
            }
        }

        ServerConnection ser = new ServerConnection();
        ser.getZones();

        FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
        mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);

        mTabHost.addTab(
                mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                TopicTabFragment.class, null);
        mTabHost.addTab(
                mTabHost.newTabSpec("tab2").setIndicator("PLACES", null),
                MapTabFragment.class, null);

        initThread = new UpdateUiThread();
        initThread.start();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG + " Main", "OnResume");
        // enable location service on phone if its not enabled already:
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean network_enabled = false;
        boolean gps_enabled = false;

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }
        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.PASSIVE_PROVIDER);
        } catch (Exception ex) {

        }

        if (!network_enabled)
            try {
                MyLocationManager.getInstance().setLocationChangedListener(this, MY_PERMISSION_ACCESS_COARSE_LOCATION);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
        if (!gps_enabled)
            try {
                MyLocationManager.getInstance().setLocationChangedListener(this, MY_PERMISSION_ACCESS_FINE_LOCATION);
            } catch (SecurityException se) {
                se.printStackTrace();
            }
    }

    @Override
    protected void onPause() {
        Log.i(TAG + " Main", "OnPause");
        ServerConnection ser = new ServerConnection();
        ArrayList<Message> msg = Messenger.getInstance().getAllMessages();
        ser.shareMessage(msg);
        super.onPause();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG + " Main", "OnStart");
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
                // Open the settings activity
                Intent intentAbout = new Intent(getApplicationContext(), AboutActivity.class);
                startActivity(intentAbout);
                return true;
            case R.id.menu_item_search:
                // Open the search activity
                Intent intentSearch = new Intent(getApplicationContext(), SearchActivity.class);
                startActivityForResult(intentSearch,123);
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
                    askUserToEnableLocationService();
                    MyLocationManager.getInstance().setLocationChangedListener(MainActivity.this, MY_PERMISSION_ACCESS_COARSE_LOCATION);
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

                    askUserToEnableLocationService();
                    MyLocationManager.getInstance().setLocationChangedListener(MainActivity.this, MY_PERMISSION_ACCESS_FINE_LOCATION);
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(this, "Grant location permission for HappyShare in your phone settings for an enabled location-based service.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    private void askUserToEnableLocationService() {
        // enable location service on phone if its not enabled already:
        LocationManager lm = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception ex) {
        }

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception ex) {
        }

        if ((!gps_enabled) && (!network_enabled)) {
            Toast.makeText(this, "Please enable the GPS location service", Toast.LENGTH_LONG).show();
            // activate Location Service
            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            this.startActivityForResult(myIntent, LOCATION_SERVICE_PROMPT);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.w(TAG, "onActResult");
        if (requestCode == LOCATION_SERVICE_PROMPT) {
            Log.w(TAG, "requestCode 123");
            // user enabled location service on his device.
            // initialise the custom locationManager!
            MyLocationManager.getInstance().setLocationChangedListener(MainActivity.this, MY_PERMISSION_ACCESS_FINE_LOCATION);
            MyLocationManager.getInstance().setLocationChangedListener(MainActivity.this, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        } else if (requestCode == ZONE_SELECTION_PROMPT) {
            if (resultCode == SelectZoneActivity.ZONE_SELECTED_SUCCESSFUL) {
                // get the selected zone:
                try {
                    current_selected_zone = ZoneManager.getInstance().getCurrentZone();
                } catch (NoZoneCurrentlySelectedException nzcse) {
                    // select default zone:
                    if (userLocation != null)
                        current_selected_zone = app.getDefaultZone(userLocation);
                    else {
                        try {
                            current_selected_zone = app.getDefaultZone(MyLocationManager.getInstance().getUserLocation());
                        } catch (NoLocationKnownException nlke) {
                            current_selected_zone = app.getDefaultZone(new LatLng(
                                    51.969879, 7.595277 // muenster (at the GEO1)
                            ));
                        }
                    }
                }
                updateUIcurrentZoneName();
            } else if (resultCode == SelectZoneActivity.ZONE_SELECTION_ABORTED){
                // do nothing;
            }
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
        } catch (NoZoneCurrentlySelectedException e) {
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }
        // for each obtained msg m: check if m is inside the current_selected_zone:
        for (Message m : obtained) {
            // check if m's topic is subscribed
            if (isTopicPreferred(current_selected_zone.getZoneID(), m.getTopic())) {
                // m inside current selected zone?
                if (m.getZone_ID().equals(current_selected_zone.getZoneID())) {
                    // remember at least 1 obtained msg is inside the current selected zone:
                    msg_within_current_zone_obtained = true;
                    // mark this msg as relevant for updating the UI:
                    relevant_msgs.add(m);
                }
            }
        }
        // 2. if so, update the UI
        if (msg_within_current_zone_obtained) {
            // update the UI:
            updateUI();
        }
    }

    /**
     * checks, if a topic of a zone is subscribed by the user
     *
     * @param zone_id - the zone, the topic is part of
     * @param topic   - the topic, that is to be subscribed
     * @return true - if the topic is subscribed in that zone, false otherwise
     */
    private boolean isTopicPreferred(String zone_id, String topic) {
        return PreferenceManager.getDefaultSharedPreferences(getApplicationContext())
                .getBoolean("pref_" + zone_id + "_" + topic, true);
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
        } catch (NoZoneCurrentlySelectedException e) {
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }
        // for each obtained msg m: check if m is inside the current_selected_zone:
        for (Message m : obtained) {
            // is m's topic subscribed?
            if (isTopicPreferred(current_selected_zone.getZoneID(), m.getTopic())) {
                // m inside current selected zone?
                if (m.getZone_ID().equals(current_selected_zone.getZoneID())) {
                    // remember at least 1 obtained msg is inside the current selected zone:
                    msg_within_current_zone_obtained = true;
                    // mark this msg as relevant for updating the UI:
                    relevant_msgs.add(m);
                }
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
            resultIntent.putExtra("ZoneID", current_selected_zone.getZoneID());
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
            updateUI();
        }
    }

    private void updateUI() {
        // update the UI:
        try {
            FragmentTabHost mTabHost = (FragmentTabHost) findViewById(android.R.id.tabhost);
            // try to add a tap:
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                    TopicTabFragment.class, null);
            // adding a tap does not work, if the updateUI() was called outside of this mainActivity.
            // --> catch exception and do nothing
            // if it worked, clear the tabs and redraw them updated:
            mTabHost.setup(this, getSupportFragmentManager(), android.R.id.tabcontent);
            mTabHost.clearAllTabs();
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab1").setIndicator("TOPICS", null),
                    TopicTabFragment.class, null);
            mTabHost.addTab(
                    mTabHost.newTabSpec("tab2").setIndicator("PLACES", null),
                    MapTabFragment.class, null);
        } catch (Exception e) {
            // MainActivity is not in foreground: do nothing
            e.printStackTrace();
        }
    }

    private void updateNumberOfZonesView() {
        TextView txt_numberZones = (TextView) findViewById(R.id.txt_zoneNumber);
        Button btn_selectZone = (Button) findViewById(R.id.btn_selectZone);
        if (number_of_containing_zones > 1) {
            txt_numberZones.setText(number_of_containing_zones + " zones available.");
            // enable select zone button.
            btn_selectZone.setEnabled(true);
        } else {
            txt_numberZones.setText(number_of_containing_zones + " zone available.");
            // disable select zone button.
            btn_selectZone.setEnabled(false);
        }
    }

    private void updateUIcurrentZoneName(){
        TextView txt_currentZone = (TextView) findViewById(R.id.txt_currentZone);
        txt_currentZone.setText("You are currently in: " + current_selected_zone.getName());
    }

    @Override
    public void onLocationChanged(LatLng newLocation) {
        Log.w(TAG, "New Location obtained: (" + newLocation.latitude + "," + newLocation.longitude + ")");
        userLocation = new LatLng(
                newLocation.latitude,
                newLocation.longitude
        );
        // get current zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException nzcse) {
            current_selected_zone = app.getDefaultZone(userLocation);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }
        // get available zones:
        ArrayList<Zone> zonesContainingUserLocation = ZoneManager.getInstance().getCurrentZones(userLocation);
        if (zonesContainingUserLocation.size() != number_of_containing_zones) {
            number_of_containing_zones = zonesContainingUserLocation.size();
            updateNumberOfZonesView();
        }
        // check state of current zone:
        if (number_of_containing_zones == 0) {
            // case 1: no zones available --> select default zone
            current_selected_zone = app.getDefaultZone(userLocation);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
            updateUI();
        } else {
            if (current_selected_zone.getZoneID().equals(app.DEFAULT_ZONE_ID)) {
                // case 2: real zones are available and current is the default
                if (number_of_containing_zones == 1) {
                    // case 2a: only one real zone available --> select it automatically
                    current_selected_zone = zonesContainingUserLocation.get(0);
                    ZoneManager.getInstance().setCurrentZone(current_selected_zone);
                    updateUI();
                    Toast.makeText(getApplicationContext(), "You entered Zone: " + current_selected_zone.getName(), Toast.LENGTH_LONG).show();
                } else {
                    // case 2b: more than 1 real zones available --> select one automatically:
                    current_selected_zone = zonesContainingUserLocation.get(0);
                    ZoneManager.getInstance().setCurrentZone(current_selected_zone);
                    updateUI();
                    Toast.makeText(getApplicationContext(), "You entered Zone: " + current_selected_zone.getName(), Toast.LENGTH_LONG).show();
                }
            } else {
                // case 3: real zones are available and current selected is not default:
                if (PolyUtil.containsLocation(userLocation, current_selected_zone.getPolygon(), false)) {
                    // case 3a: we are still in the same zone.
                    // don't do anything.
                } else {
                    // case 3b: we left the zone --> select the other one automatically:
                    current_selected_zone = zonesContainingUserLocation.get(0);
                    ZoneManager.getInstance().setCurrentZone(current_selected_zone);
                    updateUI();
                    Toast.makeText(getApplicationContext(), "You entered Zone: " + current_selected_zone.getName(), Toast.LENGTH_LONG).show();
                }
            }
        }
        updateUIcurrentZoneName();
    }

    public void initialiseTestZonesAndMessages() {
        // create an example zone:
        long expDateMillis = new Date().getTime() + 1000 * 3600 * 24 * 14; // 2 weeks
        Date expDate = new Date(expDateMillis);
        String[] topics = new String[9];
        topics[0] = "Traffic";
        topics[1] = "Sports";
        topics[2] = "Restaurants";
        topics[3] = "Blub";
        topics[4] = "Drink & Food";
        topics[5] = "Events";
        topics[6] = "Concerts";
        topics[7] = "Pokémon Go";
        topics[8] = "Others";
        ArrayList<LatLng> pts = new ArrayList<LatLng>();

        pts.add(new LatLng(51.969879, 7.595277));
        pts.add(new LatLng(51.969742, 7.596379));
        pts.add(new LatLng(51.969038, 7.596273));
        pts.add(new LatLng(51.969251, 7.595171));
        String zone1_id = "zone_id_125_62486_29465458";
        Zone zone1 = new Zone("IfGI", zone1_id, D_format.format(expDate), topics, pts);
        Log.d("Zone1", "expire-at:" + zone1.getExpiredAt());

        // create another example zone:
        expDateMillis = new Date().getTime() + 1000 * 3600 * 24 * 3; // 3 days
        expDate = new Date(expDateMillis);
        topics = new String[2];
        topics[0] = "Traffic";
        topics[1] = "Shopping";
        pts = new ArrayList<LatLng>();
        pts.add(new LatLng(51.969167, 7.596247));
        pts.add(new LatLng(51.967718, 7.596182));
        pts.add(new LatLng(51.967701, 7.594269));
        pts.add(new LatLng(51.969315, 7.595183));

        String zone_id2 = "kjfhg274j-gq725lgq-l16adug246fqf";
        Zone zone2 = new Zone("Gievenbeck", zone_id2, D_format.format(expDate), topics, pts);

        // create another example zone:
        expDateMillis = new Date().getTime() + 1000 * 3600 * 24 * 14; // 14 days
        expDate = new Date(expDateMillis);
        topics = new String[4];
        topics[0] = "Clubs/Nightlive";
        topics[1] = "Events";
        topics[2] = "Neues auf dem Markt";
        topics[3] = "Freizeitgestaltung";
        pts = new ArrayList<LatLng>();
        pts.add(new LatLng(52.293736, 7.438334));
        pts.add(new LatLng(52.291743, 7.46296));
        pts.add(new LatLng(52.28827, 7.4850));
        pts.add(new LatLng(52.2722, 7.4750));
        pts.add(new LatLng(52.2637, 7.43557));
        pts.add(new LatLng(52.2742, 7.41326));
        Zone zone3 = new Zone("Rheine", UUID.randomUUID().toString(), D_format.format(expDate), topics, pts);

        // add zone1, zone2 to ZoneManager:
        ArrayList<Zone> zones = new ArrayList<Zone>();
        //zones.add(zone1);
        //zones.add(zone2);
        zones.add(zone3);
        // If there are no zones in the DB, store the 2 example zones into it.
        if (ZoneManager.getInstance().getAllZonesfromDatabase().size() == 0) {
            ZoneManager.getInstance().updateZonesInDatabase(zones);
        }
        // use the default zone meanwhile:
        if (current_selected_zone == null) {
            // the zone is null per default, so set it to the default zone.
            current_selected_zone = app.getDefaultZone(new LatLng(51.96958, 7.5956));
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }

        // test if the saved zones are loaded from the ZoneManager methods:
        ArrayList<Zone> zonesFromDB = new ArrayList<Zone>();
        zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        for (Zone z : zonesFromDB) {
            Log.d(TAG, "zone from db: " + z.getName());
        }

        // create an example msg:
        Date creationDate = new Date(); // now
        expDateMillis = creationDate.getTime() + 1000 * 3600 * 18; // 18 hours
        expDate = new Date(expDateMillis);
        Message msg1 = new Message(UUID.randomUUID().toString(),
                zonesFromDB.get(0).getZoneID(), creationDate,
                51.9707, 7.6281, expDate, "Traffic", "Traffic Jam in the city center",
                "There is a traffic jam in the city center", true
        );
        // send msg1 to the Messenger:
        ArrayList<Message> msgs = new ArrayList<Message>();
        msgs.add(msg1);
        // if there are no msgs stored in the DB yet, add the 1 example msg to the first example zone.
        if (Messenger.getInstance().getAllMessages().size() == 0)
            Messenger.getInstance().updateMessengerFromP2P(msgs);

        // test if the saved msgs are loaded from the Messenger methods:
        ArrayList<Message> msgsFromMessenger = new ArrayList<Message>();
        msgsFromMessenger = Messenger.getInstance().getAllMessages();
        for (Message m : msgsFromMessenger) {
            Log.d(TAG, "msg from Messenger:" + m.getZone_ID() + "-" + m.getTitle() + ":" + m.getMsg());
        }
    }

    public void doBackgroundWork() {
        if (userLocation != null) {
            ArrayList<Zone> zonesFromDB = ZoneManager.getInstance().getCurrentZones(userLocation);
            for (Zone z : zonesFromDB) {
                Log.d(TAG, "zone from db: " + z.getName());
            }

            try {
                current_selected_zone = ZoneManager.getInstance().getCurrentZone();
            } catch (NoZoneCurrentlySelectedException e) {
                // what do, if no zone is currently selected?
                // select the first zone of the zonemanager
                current_selected_zone = zonesFromDB.get(0);
                ZoneManager.getInstance().setCurrentZone(current_selected_zone);
            }
        }

        try {
            // enable Location service on phone if its not enabled already:
            MyLocationManager.getInstance().setLocationChangedListener(this, MY_PERMISSION_ACCESS_COARSE_LOCATION);
        } catch (SecurityException se) {
            // in case of forbidden permission to access the user location, ask for it:
            // ask for permission ACCESS_COARSE_LOCATION:
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    MY_PERMISSION_ACCESS_COARSE_LOCATION);
        }

        try {
            // enable Location service on phone if its not enabled already:
            MyLocationManager.getInstance().setLocationChangedListener(this, MY_PERMISSION_ACCESS_FINE_LOCATION);
        } catch (SecurityException se) {
            // in case of forbidden permission to access the user location, ask for it:
            // ask for permission ACCESS_FINE_LCCATION:
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSION_ACCESS_FINE_LOCATION);
        }
        askUserToEnableLocationService();
        //Zone-Select-Button:
        Button btn_selectZone = (Button) findViewById(R.id.btn_selectZone);
        btn_selectZone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    userLocation = MyLocationManager.getInstance().getUserLocation();
                } catch (NoLocationKnownException e) {
                    // no location known:
                    // make a user feedback-Toast
                    Toast.makeText(getApplicationContext(), "The application did not retrieve a location yet.", Toast.LENGTH_LONG).show();
                    return;
                }
                Intent intentSettings = new Intent(getApplicationContext(), SelectZoneActivity.class);
                if (userLocation != null) {
                    Log.i(TAG, "userlocation=(" + userLocation.latitude + "," + userLocation.longitude + ")");
                } else
                    Log.e(TAG, "no userlocation obtained!");
                MainActivity.this.startActivityForResult(intentSettings, ZONE_SELECTION_PROMPT);
            }
        });

        initThread.interrupt();
    }

    private class UpdateUiThread extends Thread {
        @Override
        public void run() {
            try {
                while (!Thread.currentThread().isInterrupted()) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            doBackgroundWork();
                        }
                    });
                    Thread.sleep(15000);
                }
            } catch (InterruptedException consumed) {
            }
        }
    }
}