package de.ifgi.sc.smartcitiesapp.interfaces;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;


/**
 * Created by Maurin on 30.05.2016.
 */
public interface MessageUIManager {

    /**
     * To be called by Messenger to add new retrieved Messages into UserInterface
     * @param msgs
     */
    public void enqueueMessagesIntoUI(ArrayList<Message> msgs);


}