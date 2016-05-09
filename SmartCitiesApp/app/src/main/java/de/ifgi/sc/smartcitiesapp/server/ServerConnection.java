package de.ifgi.sc.smartcitiesapp.server;

import org.json.JSONArray;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.messaging.Message;


public class ServerConnection implements Connection {

    JSONParser jsonParser = new JSONParser();
    ArrayList<Message> messages = new ArrayList<Message>();
    JSONArray jsonArray = new JSONArray();

    /**
     * Constructor
     */
    public ServerConnection() {
        getMessages();
    }

    @Override
    /**
     *  gets a set of Messages in form of an ArrayList and pushs it to the Server
     */
    public void shareMessage(ArrayList<Message> messages) {
        this.messages = messages;
        jsonArray = jsonParser.parseMessagetoJSON(messages);

        // todo push jsonArray to Server
    }

    /**
     *  requests the server for Messages and shares them with the Messanger
     */
    public void getMessages(){

        //todo request to server for Messages as JSONArray

        messages = jsonParser.parseJSONtoMessage(jsonArray);
        //todo call Messanger and pull Messages
    }


}
