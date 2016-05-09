package de.ifgi.sc.smartcitiesapp.p2p;

import android.net.wifi.p2p.WifiP2pManager;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.messaging.Message;


public class P2PConnection implements Connection {

    private WifiP2pManager mManager;

    /**
     * Constructor
     */
    public P2PConnection(WifiP2pManager manager) {
        this.mManager = manager;
    }


    @Override
    public void shareMessage(ArrayList<Message> m) {

    }
}
