package de.ifgi.sc.smartcitiesapp.p2p;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.main.MainActivity;
import de.ifgi.sc.smartcitiesapp.messaging.Message;


public class P2PManager implements Connection, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    MainActivity mActivity;
    GoogleApiClient mGoogleApiClient;


    public P2PManager(MainActivity activity) {
        mActivity = activity;
        mGoogleApiClient = new GoogleApiClient.Builder(activity)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(activity, this)
                .build();

    }

    @Override
    public void shareMessage(ArrayList<Message> m) {

    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.i(MainActivity.TAG, "P2PManager onConnected");
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(MainActivity.TAG, "GoogleApiClient disconnected with cause: " + cause);
        Log.i(MainActivity.TAG, "P2PManager onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.e(MainActivity.TAG, "GoogleApiClient connection failed");

        Log.i(MainActivity.TAG, "P2PManager onConnectionFailed");
    }
}
