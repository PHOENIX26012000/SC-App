package de.ifgi.sc.smartcitiesapp.messaging;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.zone.Zone;

/**
 * Created by SAAD on 5/11/2016.
 */
public class Messenger implements de.ifgi.sc.smartcitiesapp.interfaces.Messenger {

    private Context ourContext;

    public static Messenger instance; // global singleton instance

    public static void initInstance(Context c){
        if (instance == null){
            // Create the instance
            instance = new Messenger();
            instance.ourContext = c;
        }
    }

    public Messenger(){
    }

    public static Messenger getInstance(){
        // Return the instance
        return instance;
    }


    @Override
    public synchronized void updateMessengerFromConnect(ArrayList<Message> msgs){
        //Checking size of Arraylist
        int size;
        size= msgs.size();
        Message t_msg;

        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();

        for(int i=0;i<size;i++){
            Log.i("This is Msg "+i," Number");
            t_msg= msgs.get(i);
            if(db.messageAlreadyExist(t_msg) == false){
                    db.createEntry(t_msg.getClient_ID(),t_msg.getMessage_ID(),t_msg.getZone_ID(), t_msg.getCreated_At(),t_msg.getLatitude(),
                    t_msg.getLongitude(), t_msg.getExpired_At(),t_msg.getTopic(),
                    t_msg.getTitle(),t_msg.getMsg());

            }

        }
       // db.getAllMessages();
        Log.i("Messages "," Fetched");
        db.close();
    }

    //Need to be implemented yet
    @Override
    public synchronized void updateMessengerFromUI(ArrayList<Message> msgs) {

    }


    public synchronized ArrayList<Message> getAllMessages(){
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        ArrayList<Message> msgs= db.getAllMessages();
        db.close();

        return msgs;
    }

}
