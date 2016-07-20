package de.ifgi.sc.smartcitiesapp.interfaces;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;


public interface Connection {

    /**
     * To be called by the Messenger to get a (bundle of) Message(s) shared
     *
     * @param m Message
     */
    public void shareMessage(ArrayList<Message> m);

}
