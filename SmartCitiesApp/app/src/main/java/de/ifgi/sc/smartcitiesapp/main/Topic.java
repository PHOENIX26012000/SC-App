package de.ifgi.sc.smartcitiesapp.main;

import java.util.ArrayList;

/**
 * Created by Maurin on 22.05.2016.
 */
public class Topic {

    private String name;
    private ArrayList<String> messages; // TODO: replace with messages

    public Topic(String name){
        this.name = name;
        messages = new ArrayList<String>();
    }

    public String getName(){
        return this.name;
    }

    public String getFirstMsg(){
        return messages.get(0);
    }

    public void addMsg(String msg){
        this.messages.add(msg);
    }
}
