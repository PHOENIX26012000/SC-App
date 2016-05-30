package de.ifgi.sc.smartcitiesapp.p2p;


import android.net.wifi.p2p.WifiP2pManager;

public class MyWifiP2PManager {

    private WifiP2pManager mManager;

    public MyWifiP2PManager(WifiP2pManager manager) {
        this.mManager = manager;
        connectP2P();
    }

    private void connectP2P() {
        // TODO start p2p exchange of messages
    }
}
