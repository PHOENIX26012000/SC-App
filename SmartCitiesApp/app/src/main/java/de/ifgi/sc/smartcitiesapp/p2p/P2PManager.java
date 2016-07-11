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

import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.main.MainActivity;


public class P2PManager implements Connection, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MainActivity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private Boolean subscribed = false;
    private Boolean isConnected = false;

    /**
     * The {@link Message} object used to broadcast information about the device to nearby devices.
     */
    private ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> mPubMessages;

    private ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> mReceivedMessages;

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
            .includeNamespacedType("namespace", "type").build();


    /**
     * Constructor
     *
     * @param activity
     */
    public P2PManager(MainActivity activity) {
        mActivity = activity;
        mPubMessages = new ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message>();
        mReceivedMessages = new ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message>();
        init();
    }


    /**
     * Initialize the p2p messaging
     */
    private void init() {
        // Listener for receiving Messages
        mMessageListener = new MessageListener() {
            @Override
            public void onFound(Message message) {
                Log.i(MainActivity.TAG, "Message found " + message);
                // When receives a message
                String messageIn;
                try {
                    messageIn = (String) Serializer.deserialize(message.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(MainActivity.TAG, "Could not read message" + e);
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.i(MainActivity.TAG, "Could not find message class");
                    return;
                }

                mReceivedMessages.clear();
                mReceivedMessages.add(toMessage(messageIn));
                Log.d(MainActivity.TAG + " P2P", "Found message: " + messageIn);

                // forward message to messenger
                de.ifgi.sc.smartcitiesapp.messaging.Messenger.getInstance().updateMessengerFromP2P(mReceivedMessages);
            }

            @Override
            public void onLost(Message message) {
                // When other device stops publishing
                String messageAsString = new String(message.getContent());
                Log.d(MainActivity.TAG + " P2P", "Lost sight of message: " + messageAsString);
            }
        };

        //setMessageFilter();

        buildGoogleApiClient();
    }

    private de.ifgi.sc.smartcitiesapp.messaging.Message toMessage(String str) {
        List<String> list = Arrays.asList(str.split(","));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        de.ifgi.sc.smartcitiesapp.messaging.Message m = null;
        try {
            m = new de.ifgi.sc.smartcitiesapp.messaging.Message(list.get(1), list.get(3),
                    format.parse(list.get(5)), Double.parseDouble(list.get(15)), Double.parseDouble(list.get(17)), format.parse(list.get(7)), list.get(9),
                    list.get(11),list.get(13), Boolean.parseBoolean(list.get(15)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        m.toString();
        return m;
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
     * To be called by the messenger to share a message to peers
     *
     * @param messages Messages
     */
    @Override
    public void shareMessage(ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> messages) {
        //TODO
        for (de.ifgi.sc.smartcitiesapp.messaging.Message message : messages) {
            // try publish a message if GoogleAPIClient is connected
            if (isConnected) {
                int duration = calculateDuration(message);
                mPubMessages.add(message);
                publish(message, duration);
            } else {
                mPubMessages.add(message);
            }
        }
    }


    /**
     * Check if the message isn't expired yet
     * @return
     */
    private boolean isActive(de.ifgi.sc.smartcitiesapp.messaging.Message m) {
        //TODO
        return true;
    }


    /**
     * Calculate remaining time until expiration of message in seconds.
     * @param m
     * @return
     */
    private int calculateDuration(de.ifgi.sc.smartcitiesapp.messaging.Message m) {
        //TODO
        return 10;
    }


    /**
     * Disconnects the Google API Client
     */
    public void disconnect() {
        this.mGoogleApiClient.disconnect();
    }


    /**
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnected");
        this.isConnected = true;

        if (!subscribed) {
            subscribe();
        }

        // Start publishing all active messages in the list
        //TODO correct error here

        for (de.ifgi.sc.smartcitiesapp.messaging.Message mPubMessage : mPubMessages) {
            if (isActive(mPubMessage)) {
                //int duration = calculateDuration(mPubMessage);
                publish(mPubMessage, 5);

            } else {
                unpublish(mPubMessage);
            }
        }


        //publish(new de.ifgi.sc.smartcitiesapp.messaging.Message("c_id", "m_id", "z_id", new Date(), 51, 7, new Date(2016,6,21), "top", "tit", "msg"), 5);
    }

    public void setDisconnected() {
        Log.i(MainActivity.TAG + " P2P", "P2PManager setDisconnected");
        this.isConnected = false;
    }


    /**
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnectionSuspended");
        //Log.e(MainActivity.TAG + " P2P", "GoogleApiClient disconnected with cause: " + cause);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnectionFailed");
        //Log.e(MainActivity.TAG + " P2P", "GoogleApiClient connection failed");
    }


    /**
     * Publish and Subscribe Methods
     * @param message Message to share
     * @param duration Time in seconds until message expires
     */
    private void publish(final de.ifgi.sc.smartcitiesapp.messaging.Message message, int duration) {
        Log.i(MainActivity.TAG + " P2P", "Publishing " + message);

        final Message mPubMessage;

        try {
            mPubMessage = new Message(Serializer.serialize(message.toString()));
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        final PublishOptions options = new PublishOptions.Builder()
                //.setStrategy(PUB_STRATEGY)  // Publish expires after specified time
                .setStrategy(new Strategy.Builder().setTtlSeconds(duration).build()) //maximum publishing time is the time until the message expires
                .setCallback(new PublishCallback() {
                    @Override
                    public void onExpired() {
                        super.onExpired();
                        Log.i(MainActivity.TAG + " P2P", "No longer publishing");
                        // TODO remove published message from array
                        mPubMessages.remove(message);
                    }
                }).build();

        Nearby.Messages.publish(mGoogleApiClient, mPubMessage, options)
                .setResultCallback(new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        if (status.isSuccess()) {
                            Log.i(MainActivity.TAG + " P2P", "Published successfully.");
                            mPubMessages.add(message);
                        } else {
                            Log.i(MainActivity.TAG + " P2P", "Could not publish, status = " + status);
                        }
                    }
                });
    }

    public void unpublish() {
        Log.i(MainActivity.TAG + " P2P", "Unpublishing.");
        if (mPubMessages.size() > 0) {
            for (de.ifgi.sc.smartcitiesapp.messaging.Message message : mPubMessages) {
                Message mPubMessage;
                try {
                    mPubMessage = new Message(Serializer.serialize(message));
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
                Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
            }
            mPubMessages.clear();
        }
    }

    public void unpublish(de.ifgi.sc.smartcitiesapp.messaging.Message message) {
        Log.i(MainActivity.TAG + " P2P", "Unpublishing message m.");
        if (mPubMessages.contains(message)) {
            mPubMessages.remove(message);
            Message mPubMessage;
            try {
                mPubMessage = new Message(Serializer.serialize(message));
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
            Nearby.Messages.unpublish(mGoogleApiClient, mPubMessage);
            Log.i(MainActivity.TAG + " P2P", "Unpublished message successfully.");
        } else {
            Log.i(MainActivity.TAG + " P2P", "No such message in list");
        }
    }

    private void subscribe() {
        Log.i(MainActivity.TAG + " P2P", "Subscribing");
        SubscribeOptions options = new SubscribeOptions.Builder()
                //.setStrategy(SUB_STRATEGY)
                .setStrategy(Strategy.BLE_ONLY)
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
                            subscribed = true;
                        } else {
                            Log.i(MainActivity.TAG + " P2P", "Could not subscribe, status = " + status);
                        }
                    }
                });
    }

    public void unsubscribe() {
        Log.i(MainActivity.TAG + " P2P", "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
        subscribed = false;
    }
}
