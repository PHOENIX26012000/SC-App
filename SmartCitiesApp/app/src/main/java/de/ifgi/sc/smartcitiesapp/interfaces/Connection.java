package de.ifgi.sc.smartcitiesapp.interfaces;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by helo on 26/04/16.
 */
public interface Connection {

    /**
     * To be called by the Messenger to get a Message shared
     * @param m Message
     */
    public void shareMessage(Message m);

}
