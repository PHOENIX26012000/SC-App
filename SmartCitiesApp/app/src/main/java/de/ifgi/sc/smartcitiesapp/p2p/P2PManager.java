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
import com.google.android.gms.nearby.messages.MessageFilter;
import com.google.android.gms.nearby.messages.MessageListener;
import com.google.android.gms.nearby.messages.PublishCallback;
import com.google.android.gms.nearby.messages.PublishOptions;
import com.google.android.gms.nearby.messages.Strategy;
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
    private ArrayList<Message> mPubMessages;

    /**
     * Sets the publishing time in seconds
     */
    private static final int PUB_TTL_IN_SECONDS = 5;

    /**
     * Sets the time in seconds for a published message to live.
     */
    private static final Strategy PUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(PUB_TTL_IN_SECONDS).build();

    /**
     * Sets the subscription time in seconds
     */
    private static final int SUB_TTL_IN_SECONDS = 5;

    /**
     * Sets the strategy for a published message, in this case time in seconds to live.
     */
    private static final Strategy SUB_STRATEGY = new Strategy.Builder()
            .setTtlSeconds(SUB_TTL_IN_SECONDS).build();

    /**
     * Sets the Message filter to apply to the subscriptions
     */
    private MessageFilter MESSAGE_FILTER = new MessageFilter.Builder()
            .includeNamespacedType ("namespace", "type").build();


    /**
     * Constructor
     * @param activity
     */
    public P2PManager(MainActivity activity) {
        mActivity = activity;
        mPubMessages = new ArrayList<Message>();
        init();
    }

    /**
     * Google API Client Builder
     */
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


    /**
     * Initialize the p2p messaging
     */
    private void init() {
        // Listener for receiving Messages
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                // When receives a message
                String messageAsString = new String(message.getContent());
                Log.d(MainActivity.TAG + " P2P", "Found message: " + messageAsString);
                //TODO forward message to messenger
            }

            @Override
            public void onLost(Message message) {
                // When other device stops publishing
                String messageAsString = new String(message.getContent());
                Log.d(MainActivity.TAG + " P2P", "Lost sight of message: " + messageAsString);
            }
        };

        setMessageFilter();

        buildGoogleApiClient();
    }


    /**
     * To be called by the messenger to share a message to peers
     * @param m Message
     */
    @Override
    public void shareMessage(ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> m) {
        //TODO
    }


    /**
     * Sets the message filter to the available topics the user is subscribes to
     */
    private void setMessageFilter() {
        //TODO
    }


    /**
     * Change the message filter when the user subscribes to other topics he is interested in
     */
    private void resetMessageFilter() {
        //TODO
    }


    /**
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnected");

        // Pub/sub for testing
        publish("Hello World");
        publish("HeyHo");
        subscribe();
    }


    /**
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.e(MainActivity.TAG + " P2P", "GoogleApiClient disconnected with cause: " + cause);
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnectionSuspended");
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.e(MainActivity.TAG + " P2P", "GoogleApiClient connection failed");
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnectionFailed");
    }


    /*
     * Publish and Subscribe Methods
     */
    private void publish(String message) {
        final Message mPubMessage = new Message(message.getBytes());
        mPubMessages.add(mPubMessage);

        Log.i(MainActivity.TAG + " P2P", "Publishing " + message);
        PublishOptions options = new PublishOptions.Builder()
                .setStrategy(PUB_STRATEGY)  // Publish expires after specified time
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(MainActivity.TAG + " P2P", "No longer publishing");
                        // TODO remove published message from array
                        mPubMessages.remove(mPubMessage);
                    }
                }).build();

        Nearby.Messages.publish(mGoogleApiClient, mPubMessage, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(MainActivity.TAG + " P2P", "Published successfully.");
                        } else {
                            Log.i(MainActivity.TAG + " P2P", "Could not publish, status = " + status);
                        }
                    }
                });
    }

    public void unpublish() {
        Log.i(MainActivity.TAG + " P2P", "Unpublishing.");
        if (mPubMessages.size() > 0) {
            for (Message mPubMessage : mPubMessages) {
                Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
            }
            mPubMessages.clear();
        }
    }

    private void subscribe() {
        Log.i(MainActivity.TAG + " P2P", "Subscribing");
        SubscribeOptions options = new SubscribeOptions.Builder()
                //.setStrategy(SUB_STRATEGY)
                //.setFilter(MESSAGE_FILTER)  //defines topics to subscribe to
                .setCallback(new SubscribeCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(MainActivity.TAG + " P2P", "No longer subscribing");
                    }
                }).build();

        Nearby.Messages.subscribe(mGoogleApiClient, mMessageListener, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(MainActivity.TAG + " P2P", "Subscribed successfully.");
                        } else {
                            Log.i(MainActivity.TAG + " P2P", "Could not subscribe, status = " + status);
                        }
                    }
                });
    }

    public void unsubscribe() {
        Log.i(MainActivity.TAG + " P2P", "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
    }
}
