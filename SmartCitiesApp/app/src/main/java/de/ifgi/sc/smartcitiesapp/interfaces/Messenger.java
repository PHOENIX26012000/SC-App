package de.ifgi.sc.smartcitiesapp.interfaces;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;


public interface Messenger {

    /**
     * To be called by P2P to forward new retrieved Messages to the messenger
     *
     * @param msgs
     */
    public void updateMessengerFromP2P(ArrayList<Message> msgs);

    /**
     * To be called by Server to forward new retrieved Messages to the messenger
     *
     * @param msgs
     */
    public void updateMessengerFromServer(ArrayList<Message> msgs);

    /**
     * To be called by UI Class to update Messages in Messenger
     *
     * @param msgs
     */
    public void updateMessengerFromUI(ArrayList<Message> msgs);

}
