package de.ifgi.sc.smartcitiesapp.p2p;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.nearby.Nearby;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.messaging.Message;


public class P2PManager implements Connection, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    GoogleApiClient mGoogleApiClient;

    public P2PManager(FragmentActivity activity) {
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
