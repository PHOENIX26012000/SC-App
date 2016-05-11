package de.ifgi.sc.smartcitiesapp.server;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Clara on 09.05.2016.
 */
public class JSONParser {


    ArrayList<Message> messages = new ArrayList<Message>();
    JSONObject jsonObject = new JSONObject();

    /**
     * Constructor
     */
    public JSONParser(){

    }

    /**
     *
     * @param messages ArrayList<Message>
     * @return JSONObject
     */
    public JSONObject parseMessagetoJSON(ArrayList<Message> messages){

        //todo parse ArrayList of Messages to an JSONArray

        return jsonObject;
    }

    /**
     *
     * @param jsonObject
     * @return ArrayList<Message>
     */
    public ArrayList<Message> parseJSONtoMessage(JSONObject jsonObject){

        //todo parse JSONArray to an ArrayList of Messages

        return messages;
    }


}
