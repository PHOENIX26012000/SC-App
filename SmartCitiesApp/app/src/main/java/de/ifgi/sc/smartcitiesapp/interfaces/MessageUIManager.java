package de.ifgi.sc.smartcitiesapp.interfaces;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Maurin on 30.05.2016.
 */
public interface MessageUIManager {

    /**
     * To be called by Messenger to notify the UserInterface about new retrieved Messages from the P2P
     * @param msgs
     */
    public void enqueueMessagesIntoUIFromP2P(ArrayList<Message> msgs);

    /**
     * To be called by Messenger to notify the UserInterface about new retrieved Messages from the server
     * @param msgs
     */
    public void enqueueMessagesIntoUIFromServer(ArrayList<Message> msgs);
}