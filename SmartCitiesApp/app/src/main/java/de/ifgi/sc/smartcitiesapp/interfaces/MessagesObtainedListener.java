package de.ifgi.sc.smartcitiesapp.interfaces;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Maurin on 05.07.2016.
 */
public interface MessagesObtainedListener {

    /**
     * To be called from the UIMessageManager to notify the UI about new obtained msgs from the server
     * @param msgs List of new retrieved msgs
     */
    public void onMessagesObtainedFromServer(ArrayList<Message> msgs);

    /**
     * To be called from the UIMessageManager to notify the UI about new obtained msgs from a Peer
     * @param msgs List of new retrieved msgs
     */
    public void onMessagesObtainedFromP2P(ArrayList<Message> msgs);

}
