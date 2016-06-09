package de.ifgi.sc.smartcitiesapp.p2p;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.nearby.messages.Message;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.SubscribeCallback;
import com.google.android.gms.nearby.messages.SubscribeOptions;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.main.MainActivity;


public class P2PManager implements Connection, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MainActivity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;

    /**
     * The {@link Message} object used to broadcast information about the device to nearby devices.
     */
    private Message mPubMessage;


    public P2PManager(MainActivity activity) {
        mActivity = activity;
        init();
    }

    private void buildGoogleApiClient() {
        if (mGoogleApiClient != null) {
            return;
        }
        mGoogleApiClient = new GoogleApiClient.Builder(mActivity)
                .addApi(Nearby.MESSAGES_API)
                .addConnectionCallbacks(this)
                .enableAutoManage(mActivity, this)
                .build();
    }

    private void init() {
        buildGoogleApiClient();

        // Listener for receiving Messages
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(MainActivity.TAG, "Found message: " + messageAsString);
            }

            @Override
            public void onLost(Message message) {
                String messageAsString = new String(message.getContent());
                Log.d(MainActivity.TAG, "Lost sight of message: " + messageAsString);
            }
        };
    }

    @Override
    public void shareMessage(ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> m) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(MainActivity.TAG, "P2PManager onConnected");
        publish("Hello World");
        subscribe();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(MainActivity.TAG, "GoogleApiClient disconnected with cause: " + cause);
        Log.i(MainActivity.TAG, "P2PManager onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(MainActivity.TAG, "GoogleApiClient connection failed");

        Log.i(MainActivity.TAG, "P2PManager onConnectionFailed");
    }


    /*
     * Publish and Subscribe Methods
     */
    private void publish(String message) {
        Log.i(MainActivity.TAG, "Publishing message: " + message);
        mPubMessage = new Message(message.getBytes());

        PublishOptions options = new PublishOptions.Builder()
                //.setStrategy(mPublishStrategy)
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(MainActivity.TAG, "PublishCallback");
                    }
                })
                .build();

        Nearby.Messages.publish(mGoogleApiClient, mPubMessage, options).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.i(MainActivity.TAG, "Published successfully.");
                } else {
                    Log.i(MainActivity.TAG, "Not published successfully.");
                }
            }
        });
    }

    public void unpublish() {
        Log.i(MainActivity.TAG, "Unpublishing.");
        if (mPubMessage != null) {
            Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
            mPubMessage = null;
        }
    }

    private void subscribe() {
        Log.i(MainActivity.TAG, "Subscribing.");

        SubscribeOptions options = new SubscribeOptions.Builder()
                //.setStrategy(mSubscribeStrategy)
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(MainActivity.TAG, "SubscribeCallback");
                    }
                })
                .build();

        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                if (status.isSuccess()) {
                    Log.i(MainActivity.TAG, "Subscribed successfully.");
                } else {
                    Log.i(MainActivity.TAG, "Not subscribed successfully.");
                }
            }
        });
    }

    public void unsubscribe() {
        Log.i(MainActivity.TAG, "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }
}
