package de.ifgi.sc.smartcitiesapp.main;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Maurin on 22.05.2016.
 */
public class Topic {

    private String name;
    private ArrayList<Message> messages; // TODO: replace with messages

    public Topic(String name){
        this.name = name;
        messages = new ArrayList<Message>();
    }

    public String getName(){
        return this.name;
    }

    public Message getFirstMsg(){
        return messages.get(0);
    }

    public void addMsg(Message msg){
        this.messages.add(msg);
    }
}
