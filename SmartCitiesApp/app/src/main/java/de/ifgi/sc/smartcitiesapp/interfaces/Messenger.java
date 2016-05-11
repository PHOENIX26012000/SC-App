package de.ifgi.sc.smartcitiesapp.interfaces;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by SAAD on 5/11/2016.
 */
public interface Messenger {

    /**
     * To be called by Server/Peer2Peer Classes to update Messages in Messenger
     * @param msgs
     */
    public void updateMessengerFromConnect(ArrayList<Message> msgs);


    /**
     * To be called by UI Class to update Messages in Messenger
     * @param msgs
     */
    public void updateMessengerFromUI(ArrayList<Message> msgs);

}
