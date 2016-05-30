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
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {

    private final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 10042; // just a random int resource.

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempForMessage();
        setContentView(R.layout.activity_main);

        // create some sample topics:
        Topic traffic = new Topic("Traffic");
        Topic sports = new Topic("Sports");
        Topic restaurants = new Topic("Restaurants");
        Topic shopping = new Topic("Shopping");
        // add some msgs to the topics:
        traffic.addMsg("Traffic Jam in the city center");
        sports.addMsg("students beachvolleyball tournament at the castle");
        restaurants.addMsg("recyclable \\\"to-go\\\"-coffee cups at Franks Copy Shop");
        shopping.addMsg("Missed Black friday? Clothes are 100% off at my place");

        // store topics into sharedpref:
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();

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
    }

    private void tempForMessage(){
        Log.i("Main Activity","Activity Started");
        Date date = new Date();
        Log.i( date.toString(),"is Date");
        Message m =new Message("123","136",23,36.989823,89.002323,date,"dads","dsad","sd");
        Message m2 =new Message("345","156",23,34.45454,74.34324,date,"dads","dsad","sd");
        ArrayList<Message> msgList= new ArrayList<Message>();
        msgList.add(m);
        msgList.add(m2);
        Log.i("Array list ", "Created");

        Messenger msgr=new Messenger(this);
        msgr.updateMessengerFromConnect(msgList);
        msgr.getAllMessages();
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
