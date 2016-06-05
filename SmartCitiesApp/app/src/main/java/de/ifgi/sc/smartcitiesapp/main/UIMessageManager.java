package de.ifgi.sc.smartcitiesapp.main;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.MessageUIManager;
import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Maurin on 30.05.2016.
 */
public class UIMessageManager implements MessageUIManager {

    private ArrayList<Message> activeMessages; // all messages on the device
    private static UIMessageManager instance;  // global singleton instance

    private UIMessageManager()
    {
        // Constructor hidden because this is a singleton
    }

    public static void initInstance()
    {
        if (instance == null)
        {
            // Create the instance
            instance = new UIMessageManager();
            instance.activeMessages = new ArrayList<Message>();
        }
    }

    public static UIMessageManager getInstance()
    {
        // Return the instance
        return instance;
    }

    /**
     * To be called by Messenger to add new retrieved Messages into UserInterface
     * @param msgs
     */
    @Override
    public synchronized void enqueueMessagesIntoUI(ArrayList<Message> msgs) {
        this.activeMessages.addAll(msgs);
    }

    /**
     * return all unfiltered messages retrieved
     * @return
     */
    public synchronized ArrayList<Message> getActiveMessages(){
        return this.activeMessages;
    }

    public synchronized ArrayList<Message> getFilteredMessages(){
        ArrayList<Message> filtered = new ArrayList<>();
        for (Message m : activeMessages){
            // TODO: add m into filtered, if it fits.
        }
        return filtered;
    }

}