package de.ifgi.sc.smartcitiesapp.p2p;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.main.MainActivity;
import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver implements Connection {

    private String TAG = "SmartCity";

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

    }

    @Override
    public void shareMessage(ArrayList<Message> m) {
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i( TAG + "BroadcastReceiver", "Discover peers succeeded");
            }

            @Override
            public void onFailure(int reasonCode) {
                Log.i( TAG + "BroadcastReceiver", "Discover peers failed" + reasonCode);
            }
        });

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            stateChangedAction(context, intent);
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // Call WifiP2pManager.requestPeers() to get a list of current peers

            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            peersChangedAction(context, intent);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            connectionChangedAction(context, intent);
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
            deviceChangedAction(context, intent);
        }
    }

    /**
     * Call when the state of the WIFI P2P changed, e.g. is enabled at the startup.
     * @param context
     * @param intent
     */
    private void stateChangedAction(Context context, Intent intent) {
        int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
        if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
            // Wifi P2P is enabled
            Log.i( TAG + "BroadcastReceiver", "Wifi P2P state changed: is enabled");
            //Testing
            shareMessage(new ArrayList<Message>());
        } else {
            // Wi-Fi P2P is not enabled
            Log.i( TAG + "BroadcastReceiver", "Wifi P2P is state changed: is not enabled");
        }
    }

    /**
     * Call when the peer of the WIFI P2P changed.
     * @param context
     * @param intent
     */
    private void peersChangedAction(Context context, Intent intent) {
        Log.i( TAG + "BroadcastReceiver", "Wifi P2P peers changed");
        if (mManager != null) {
            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                @Override
                public void onPeersAvailable(WifiP2pDeviceList peers) {
                    Log.i( TAG + "BroadcastReceiver", "Available peers" + peers);
                    //Connect to every peer
                    for (  WifiP2pDevice peer : peers.getDeviceList()) {
                        final WifiP2pDevice device = peer;
                        WifiP2pConfig config = new WifiP2pConfig();
                        config.deviceAddress = device.deviceAddress;
                        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                            @Override
                            public void onSuccess() {
                                Log.i( TAG + "BroadcastReceiver", "Successfully connected to peer" + device);
                            }
                            @Override
                            public void onFailure(int reason) {
                                Log.i( TAG + "BroadcastReceiver", "Connection to peer failed" + device);
                            }
                        });
                    }
                }
            });
        }
    }

    /**
     * Call when the connection of the WIFI P2P changed.
     * @param context
     * @param intent
     */
    private void connectionChangedAction(Context context, Intent intent) {
        Log.i( TAG + "BroadcastReceiver", "Wifi P2P connection changed");
        //TODO
    }

    /**
     * Call when the device of the WIFI P2P changed.
     * @param context
     * @param intent
     */
    private void deviceChangedAction(Context context, Intent intent) {
        Log.i( TAG + "BroadcastReceiver", "Wifi P2P device changed");
        //TODO
    }
}
