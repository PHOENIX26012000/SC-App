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
import java.util.Date;
import java.util.List;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.main.MainActivity;

/**
 * Created by Heinrich.
 * The P2PManager handles the connection between different peers in the network.
 */
public class P2PManager implements Connection, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private MainActivity mActivity;
    private GoogleApiClient mGoogleApiClient;
    private MessageListener mMessageListener;
    private Boolean subscribed = false;
    private Boolean isConnected = false;

    /**
     * The {@link Message} objects used to broadcast information about the device to nearby devices.
     */
    private ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> mPubMessages;

    /**
     * The {@link Message} objects that temporarily stores the received messages.
     */
    private ArrayList<de.ifgi.sc.smartcitiesapp.messaging.Message> mReceivedMessages;


    /**
     * Constructor: Initializes the P2PManager and starts the service.
     *
     * @param activity the activity
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
                Log.i(MainActivity.TAG + " P2P", "Message found " + message);
                // When receives a message
                String messageIn;
                try {
                    messageIn = (String) Serializer.deserialize(message.getContent());
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.i(MainActivity.TAG + " P2P", "Could not read message" + e);
                    return;
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                    Log.i(MainActivity.TAG + " P2P", "Could not find message class");
                    return;
                }

                mReceivedMessages.clear();
                mReceivedMessages.add(toMessage(messageIn));
                //Log.d(MainActivity.TAG + " P2P", "Found message: " + messageIn);

                // forward message to messenger
                de.ifgi.sc.smartcitiesapp.messaging.Messenger.getInstance().updateMessengerFromP2P(mReceivedMessages);
            }

            @Override
            public void onLost(Message message) {
                // When other device stops publishing
                Log.d(MainActivity.TAG + " P2P", "Lost sight of message: " + message);
            }
        };

        //setMessageFilter();

        buildGoogleApiClient();
    }

    /**
     * Parses the incomming Messages for the peers to a Message object
     *
     * @param str: Message string to be parsed into a message
     * @return Returns the Message object
     */
    private de.ifgi.sc.smartcitiesapp.messaging.Message toMessage(String str) {
        List<String> list = Arrays.asList(str.split(","));
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        de.ifgi.sc.smartcitiesapp.messaging.Message m = null;
        try {
            m = new de.ifgi.sc.smartcitiesapp.messaging.Message(list.get(1), list.get(3),
                    format.parse(list.get(5)), Double.parseDouble(list.get(15)), Double.parseDouble(list.get(17)), format.parse(list.get(7)), list.get(9),
                    list.get(11), list.get(13), Boolean.parseBoolean(list.get(15)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        m.toString();
        return m;
    }


    /**
     * Google API Client Builder handles the P2P connetion
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
        for (de.ifgi.sc.smartcitiesapp.messaging.Message message : messages) {
            // try publish a message if GoogleAPIClient is connected
            if (isConnected) {
                int duration = calculateDuration(message);
                mPubMessages.add(message);
                if (duration < 0) {
                    if (duration > 86400) {
                        publish(message, 86400);
                    } else {
                        publish(message, duration);
                    }
                }
            } else {
                mPubMessages.add(message);
            }
        }
    }


    /**
     * Calculate remaining time until expiration of message in seconds.
     *
     * @param m
     * @return
     */
    private int calculateDuration(de.ifgi.sc.smartcitiesapp.messaging.Message m) {
        //TODO
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
        Date now = new Date();
        Date expire = null;
        try {
            expire = format.parse(m.getExpired_At());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        int seconds = (int) (expire.getTime() - now.getTime()) / 1000;
        Log.i(MainActivity.TAG + " P2P", "Message time in seconds: " + seconds);
        return seconds;
    }


    /**
     * Disconnects the Google API Client
     */
    public void disconnect() {
        this.mGoogleApiClient.disconnect();
    }


    /**
     * Is called when the device is connected to the nearby service.
     *
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnected");
        this.isConnected = true;

        //if (!subscribed) {
        subscribe();
        init();
        //}

        // share all Messages that are in the publication array
        for (de.ifgi.sc.smartcitiesapp.messaging.Message mPubMessage : mPubMessages) {
            int duration = calculateDuration(mPubMessage);
            if (duration > 0) {
                if (duration > 86400) {
                    publish(mPubMessage, 86400);
                } else {
                    publish(mPubMessage, duration);
                }
            } else {
                unpublish(mPubMessage);
            }
        }
    }

    /**
     * Sets the service running boolean to false when the application stops.
     */
    public void setDisconnected() {
        Log.i(MainActivity.TAG + " P2P", "P2PManager setDisconnected");
        this.isConnected = false;
    }


    /**
     * Just logs when the connection is suspended.
     *
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnectionSuspended");
        //Log.e(MainActivity.TAG + " P2P", "GoogleApiClient disconnected with cause: " + cause);
    }


    /**
     * Just logs when the connection fails.
     *
     * @param result
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult result) {
        Log.i(MainActivity.TAG + " P2P", "P2PManager onConnectionFailed");
        //Log.e(MainActivity.TAG + " P2P", "GoogleApiClient connection failed");
    }


    /**
     * Publish Messages for a specified duration.
     *
     * @param message  Message to share
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

    /**
     * Unpublishes all messages when the application is closed.
     */
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

    /**
     * Unpublishes a single message
     *
     * @param message the message
     */
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

    /**
     * Subscribes the app to incomming messages
     */
    private void subscribe() {
        Log.i(MainActivity.TAG + " P2P", "Subscribing");
        SubscribeOptions options = new SubscribeOptions.Builder()
                //.setStrategy(SUB_STRATEGY)
                //.setStrategy(Strategy.BLE_ONLY)
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

    /**
     * Unsubscribes the app for incomming messages.
     */
    public void unsubscribe() {
        Log.i(MainActivity.TAG + " P2P", "Unsubscribing.");
        Nearby.Messages.unsubscribe(mGoogleApiClient, mMessageListener);
        subscribed = false;
    }
}
