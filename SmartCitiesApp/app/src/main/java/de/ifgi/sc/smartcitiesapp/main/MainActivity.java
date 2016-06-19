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

    /**
     * P2P Manager that handles the main p2p message sharing of the app
     */
    public P2PManager mP2PManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Start P2P Messaging
        mP2PManager = new P2PManager(this);

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
        mP2PManager.unpublish();
        mP2PManager.unsubscribe();
        mP2PManager.disconnect();
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
}
