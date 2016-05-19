package de.ifgi.sc.smartcitiesapp.messaging;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SAAD on 5/11/2016.
 */
public class Messenger implements de.ifgi.sc.smartcitiesapp.interfaces.Messenger {

    private int size;

    private final Context ourContext;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void updateMessengerFromConnect(ArrayList<Message> msgs){
        //Checking size of Arraylist
        size= msgs.size();
        Message t_msg;

        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        for(int i=0;i<size;i++){
            Log.i("This is Msg "+i," Number");
            t_msg= msgs.get(i);
            db.createEntry(t_msg.getClient_ID(),t_msg.getMessage_ID(),t_msg.getZone_ID(),
                    t_msg.getExpired_At().toString(),t_msg.getCategory(),t_msg.getTitle(),t_msg.getMsg());

        }
        db.close();
    }

    @Override
    public void updateMessengerFromUI(ArrayList<Message> msgs) {

    }
    public Messenger(Context C){
            ourContext = C;
    }

}
