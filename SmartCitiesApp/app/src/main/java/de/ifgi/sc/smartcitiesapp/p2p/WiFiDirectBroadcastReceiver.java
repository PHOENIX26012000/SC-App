package de.ifgi.sc.smartcitiesapp.p2p;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.main.MainActivity;
import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * A BroadcastReceiver that notifies of important Wi-Fi p2p events.
 */
public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;

    private ArrayList<WifiP2pDevice> mPeers = new ArrayList<WifiP2pDevice>();


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

    }


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P onReceive - " + action);

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Respond to state changes

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                mActivity.setIsWifiP2pEnabled(true);
            } else {
                // Wi-Fi P2P is not enabled
                mActivity.setIsWifiP2pEnabled(false);
            }
            Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P state changed - " + state);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()

            Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P peers changed");
            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P peers available");

                        mPeers.clear();
                        mPeers.addAll(peers.getDeviceList());

                        if (peers.getDeviceList().isEmpty()) {
                            Log.d(MainActivity.TAG, "No devices found");
                            return;
                        }

                        //Connect to every peer
                        for (int i = 0; i < mPeers.size(); i++) {
                            Log.i(MainActivity.TAG + "BroadcastReceiver", "Peer number " + i + " is " + mPeers.get(i).toString());
                            WifiP2pDevice device = mPeers.get(i);
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = device.deviceAddress;
                            mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
                                @Override
                                public void onSuccess() {
                                    Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P connection to peer successful");
                                }

                                @Override
                                public void onFailure(int reason) {
                                    Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P connection to peer failed");
                                }
                            });
                        }
                    }
                });
            }


        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections

            Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P connection changed");
            if (mManager == null) {
                return;
            }
            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            if (networkInfo.isConnected()) {
                // we are connected with the other device, request connection
                // info to find group owner IP
                Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P connection is connected");
                mManager.requestConnectionInfo(mChannel, null);
            } else {
                // It's a disconnect
                Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P connection is disconnected");
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing

            Log.i(MainActivity.TAG + "BroadcastReceiver", "P2P device changed");
        }
    }

}
