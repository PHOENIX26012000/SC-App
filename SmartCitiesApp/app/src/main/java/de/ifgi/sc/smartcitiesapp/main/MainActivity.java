package de.ifgi.sc.smartcitiesapp.main;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import org.json.JSONArray;
import org.json.JSONObject;

import de.ifgi.sc.smartcitiesapp.R;
//import de.ifgi.sc.smartcitiesapp.messaging.DatabaseHelper;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i("Main Activity","Activity Started");
       // SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        Log.i( date.toString(),"is Date");
        //Message m =new Message("123","356",23,date,"dads","dsad","sd");
        //Message m2 =new Message("345","356",23,date,"dads","dsad","sd");
        ArrayList<Message> msgList= new ArrayList<Message>();
        //msgList.add(m);
        //msgList.add(m2);
        Log.i("Array list ", "Created");

        //Messenger msgr=new Messenger(this);
        //msgr.updateMessengerFromConnect(msgList);
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
}
