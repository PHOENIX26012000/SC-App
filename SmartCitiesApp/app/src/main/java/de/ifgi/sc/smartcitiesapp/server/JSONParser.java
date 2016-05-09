package de.ifgi.sc.smartcitiesapp.server;

import org.json.JSONArray;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Clara on 09.05.2016.
 */
public class JSONParser {


    ArrayList<Message> messages = new ArrayList<Message>();
    JSONArray jsonArray = new JSONArray();

    /**
     * Constructor
     */
    public JSONParser(){

    }

    /**
     *
     * @param messages ArrayList<Message>
     * @return JSONArray
     */
    public JSONArray parseMessageToJSON(ArrayList<Message> messages){

        //todo parse ArrayList of Messages to an JSONArray

        return jsonArray;
    }

    /**
     *
     * @param jsonArray
     * @return ArrayList<Message>
     */
    public ArrayList<Message> parseJSONtoMessage(JSONArray jsonArray){

        //todo parse JSONArray to an ArrayList of Messages

        return messages;
    }


}
