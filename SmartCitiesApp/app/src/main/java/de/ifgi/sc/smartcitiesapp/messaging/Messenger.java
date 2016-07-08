package de.ifgi.sc.smartcitiesapp.messaging;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.main.MainActivity;
import de.ifgi.sc.smartcitiesapp.main.UIMessageManager;
import de.ifgi.sc.smartcitiesapp.p2p.P2PManager;
import de.ifgi.sc.smartcitiesapp.zone.Zone;

/**
 * Created by SAAD on 5/11/2016.
 */
public class Messenger implements de.ifgi.sc.smartcitiesapp.interfaces.Messenger {

    private Context ourContext;

    public static Messenger instance; // global singleton instance
    private P2PManager mP2PManager;


    public static void initInstance(Context c){
        if (instance == null){
            // Create the instance
            instance = new Messenger();
            instance.ourContext = c;
        }
    }

    public Messenger(){
        // leave it empty, cause its a singleton now.
    }

    public static Messenger getInstance(){
        // Return the instance
        return instance;
    }

    /** This method will receive messages from Server and PeerConnection separately
     * and write them in database
     * Conditions:
     * User need to be in at least one zone inorder to store messages locally
     * Only messages from one particular zones will be received
     * Messages need to have unique MessageID, otherwise they won't get stored.
     * @param msgs Raw Array list of Messages received from Server or Peers
     */
    @Override
    public synchronized void updateMessengerFromP2P(ArrayList<Message> msgs){

        Log.d(MainActivity.TAG + " Messenger", "updateMessageFromP2P: " + msgs);
        //Checking size of Arraylist
        int size;
        size = msgs.size();
        Message t_msg;
        ArrayList<Message> uarray_list = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        String userZoneID=currentUserZone();
        for(int i=0;i<size;i++){
            Log.i(MainActivity.TAG + " Messenger", "This is Msg "+ i + " Number");
            t_msg= msgs.get(i);
            //will change to this once implemented by ZoneManager
            //db.messageAlreadyExist(t_msg) == false && userZoneID!=null && userZoneID.equals(t_msg.getZone_ID())
            if(!db.messageAlreadyExist(t_msg)){
                Log.i(" Messenger", "Message not existing in db yet. " + t_msg.getMessage_ID());
                createMessageEntry(db,t_msg);
                uarray_list.add(t_msg);

            }

        }

        db.close();
//        UIMessageManager.getInstance().enqueueMessagesIntoUIFromP2P(uarray_list);
//        mP2PManager.shareMessage(uarray_list);
    }
    public synchronized void updateMessengerFromServer(ArrayList<Message> msgs){

        //Checking size of Arraylist
        int size;
        size = msgs.size();
        Message t_msg;
        ArrayList<Message> uarray_list = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        String userZoneID=currentUserZone();
        for(int i=0;i<size;i++){
            Log.i("This is Msg "+i," Number");
            t_msg= msgs.get(i);
            //will change to this once implemented by ZoneManager
            //db.messageAlreadyExist(t_msg) == false && userZoneID!=null && userZoneID.equals(t_msg.getZone_ID())
            if(!db.messageAlreadyExist(t_msg) && !t_msg.messageExpired() ){
                createMessageEntry(db,t_msg);

                // share Messages with P2PManager, if still active and new
                // mP2PManager.shareMessage(...);
                // foreward it to UI
            }

        }

        db.close();
        UIMessageManager.getInstance().enqueueMessagesIntoUIFromServer(uarray_list);
        mP2PManager.shareMessage(uarray_list);
    }

    //This function will get user zoneID from ZoneManager Class and return it
    private String currentUserZone() {
        return null;
    }

    /**This method will be called by main Interface to write and store message locally
     *
     * @param msgs New message written by user
     */
    @Override
    public void updateMessengerFromUI(ArrayList<Message> msgs) {
        //Checking size of Arraylist
        int size;
        size= msgs.size();
        Message t_msg;
        ArrayList<Message> parray_list = new ArrayList<>();
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();

        for(int i=0;i<size;i++){
            Log.i("This is Msg "+i," Number");
            t_msg= msgs.get(i);
            if(!db.messageAlreadyExist(t_msg) && !t_msg.messageExpired()){
                    createMessageEntry(db,t_msg);
                    parray_list.add(t_msg);
            }

        }

        Log.i("Messages "," Fetched");
        db.close();
        mP2PManager.shareMessage(parray_list);

    }

    /**
     * This method will create a Message entry in database
     * @param db
     * @param t_msg
     */
    private void createMessageEntry(DatabaseHelper db,Message t_msg){
        db.createEntry(t_msg.getMessage_ID(),t_msg.getZone_ID(), t_msg.getCreated_At(),t_msg.getLatitude(),
                t_msg.getLongitude(), t_msg.getExpired_At(),t_msg.getTopic(),
                t_msg.getTitle(),t_msg.getMsg());
    }

    public synchronized void setP2PManager(P2PManager p2pmanager){
        instance.mP2PManager = p2pmanager;
    }

    public void initialStartup() {
        // 1) make a server connection and save messages to database

        // 2) getAllMessages from DB and send them to P2P
    }

    /**
     * This methods will read all Messages from local database and return them in the form of
     * Message array
     * @return return all Messages stored locally in database
     */
    public ArrayList<Message> getAllMessages(){
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        ArrayList<Message> msgs = db.getAllMessages();
        db.close();

        return msgs;
    }

    //This methods will store all zones in database having unique Zone_IDs.
    //Zone with prematching zoneID will simply be ignored
    public void updateZonesInDatabase(ArrayList<Zone> zones){
        //Checking size of Arraylist
        int size;
        size= zones.size();
        Zone zn;

        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();

        for(int i=0;i<size;i++){
            Log.i("This is Zone  "+i," Number");
            zn= zones.get(i);
            if(db.zoneAlreadyExist(zn) == false){
                db.createZoneEntry(zn.getName(),zn.getZoneID(),zn.getExpiredAt(),zn.getTopics(),zn.getPolygon());

            }

        }
        // db.getAllMessages();
        Log.i("Zones  "," stored");
        db.close();

    }

    //This method will return all zones stored in Database
    public ArrayList<Zone> getAllZonesfromDatabase(){
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        ArrayList<Zone> zones= db.getAllZones_DB();
        db.close();

        return zones;
    }


    public void deleteZoneMessage()
    {
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        db.deleteExpiredMnZ();
        db.close();


    }
}
