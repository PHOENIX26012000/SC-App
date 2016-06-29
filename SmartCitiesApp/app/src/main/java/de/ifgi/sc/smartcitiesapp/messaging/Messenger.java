package de.ifgi.sc.smartcitiesapp.messaging;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.p2p.P2PManager;
import de.ifgi.sc.smartcitiesapp.zone.Zone;

/**
 * Created by SAAD on 5/11/2016.
 */
public class Messenger implements de.ifgi.sc.smartcitiesapp.interfaces.Messenger {


    private final Context ourContext;

    private P2PManager mP2PManager;


    @Override
    public void updateMessengerFromConnect(ArrayList<Message> msgs) {
        //Checking size of Arraylist
        int size;
        size = msgs.size();
        Message t_msg;

        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();

        for (int i = 0; i < size; i++) {
            Log.i("This is Msg " + i, " Number");
            t_msg = msgs.get(i);
            if (db.messageAlreadyExist(t_msg) == false) {
                db.createEntry(t_msg.getClient_ID(), t_msg.getMessage_ID(), t_msg.getZone_ID(), t_msg.getCreated_At(), t_msg.getLatitude(),
                        t_msg.getLongitude(), t_msg.getExpired_At(), t_msg.getTopic(),
                        t_msg.getTitle(), t_msg.getMsg());

                // share Messages with P2PManager, if still active and new
                // mP2PManager.shareMessage(...);
                // foreward it to UI
            }

        }
        // db.getAllMessages();
        Log.i("Messages ", " Fetched");
        db.close();


    }

    //Need to be implemented yet


    @Override
    public void updateMessengerFromUI(ArrayList<Message> msgs) {
        // share Messages with P2PManagers
        // mP2PManager.shareMessage(...);
    }

    public Messenger(Context C, P2PManager p2pmanager) {
        ourContext = C;
        mP2PManager = p2pmanager;
    }

    public void initialStartup() {
        // 1) make a server connection and save messages to database

        // 2) getAllMessages from DB and send them to P2P
    }

    public ArrayList<Message> getAllMessages() {
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        ArrayList<Message> msgs = db.getAllMessages();
        db.close();

        return msgs;
    }

}
