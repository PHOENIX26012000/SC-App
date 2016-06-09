package de.ifgi.sc.smartcitiesapp.main;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.p2p.P2PManager;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;


public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();

    public P2PManager mP2PManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mP2PManager = new P2PManager(this);

    }


    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG + "Main", "OnResume");
    }

    
    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG + "Main", "OnPause");
    }


    @Override
    protected void onStop() {
        mP2PManager.unpublish();
        mP2PManager.unsubscribe();
        super.onStop();
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
