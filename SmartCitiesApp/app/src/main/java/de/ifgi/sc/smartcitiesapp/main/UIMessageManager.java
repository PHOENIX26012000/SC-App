package de.ifgi.sc.smartcitiesapp.main;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.MessageUIManager;
import de.ifgi.sc.smartcitiesapp.interfaces.MessagesObtainedListener;
import de.ifgi.sc.smartcitiesapp.messaging.Message;


public class UIMessageManager implements MessageUIManager {

    private static UIMessageManager instance;  // global singleton instance

    private MessagesObtainedListener mol;
    private ArrayList<Message> new_obtained_msgs;

    private UIMessageManager() {
        // Constructor hidden because this is a singleton
    }

    /**
     * Initialize
     */
    public static void initInstance() {
        if (instance == null) {
            // Create the instance
            instance = new UIMessageManager();
            instance.new_obtained_msgs = new ArrayList<Message>();
        }
    }

    /**
     * @param mol
     */
    public void setMessageObtainedListener(MessagesObtainedListener mol) {
        instance.mol = mol;
    }

    /**
     * @return
     */
    public static UIMessageManager getInstance() {
        // Return the instance
        return instance;
    }

    /**
     * To be called by Messenger to notify the UserInterface about new retrieved Messages from Peer
     *
     * @param msgs
     */
    @Override
    public synchronized void enqueueMessagesIntoUIFromP2P(ArrayList<Message> msgs) {
        instance.new_obtained_msgs.addAll(msgs);
        instance.mol.onMessagesObtainedFromP2P(msgs);
    }

    /**
     * To be called by Messenger to notify the UserInterface about new retrieved Messages from Server
     *
     * @param msgs
     */
    @Override
    public void enqueueMessagesIntoUIFromServer(ArrayList<Message> msgs) {
        instance.mol.onMessagesObtainedFromServer(msgs);
    }

    /**
     * @return
     */
    public synchronized ArrayList<Message> getNew_obtained_msgs() {
        return instance.new_obtained_msgs;
    }

    /**
     * remove messages from being markes as "new". To be called after msgs are recognized by the user and not
     * "new" anymore.
     *
     * @param msgIDs - ArrayList of Message_IDs that are not new anymore.
     */
    public synchronized void markMessagesAsOld(ArrayList<String> msgIDs) {
        ArrayList<Message> result = new ArrayList<Message>();
        ArrayList<Message> current = instance.getNew_obtained_msgs();
        for (Message msg : current) {
            if (!msgIDs.contains(msg.getMessage_ID())) {
                result.add(msg);
            }
        }
        instance.new_obtained_msgs = result;
    }

}