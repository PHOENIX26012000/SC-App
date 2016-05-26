package de.ifgi.sc.smartcitiesapp.main;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.DatabaseHelper;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tempForMessage();
        setContentView(R.layout.activity_main);

    }

    private void tempForMessage(){
        Log.i("Main Activity","Activity Started");
        Date date = new Date();
        Log.i( date.toString(),"is Date");
        Message m =new Message("123","356",23,36.989823,89.002323,date,"dads","dsad","sd");
        Message m2 =new Message("345","356",23,34.45454,74.34324,date,"dads","dsad","sd");
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
}
