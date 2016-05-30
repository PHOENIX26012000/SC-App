package de.ifgi.sc.smartcitiesapp.server;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.ifgi.sc.smartcitiesapp.messaging.Message;

/**
 * Created by Clara on 09.05.2016.
 */
public class JSONParser {


    private static final Logger logger =
            Logger.getLogger("JSONParser");

    ArrayList<Message> msglist = new ArrayList<Message>();
    JSONObject jsonObject = new JSONObject();
    JSONArray jsonMsgArray = new JSONArray();
    JSONObject jsonMsg = new JSONObject();
    String clientID = new String();
    String messageID = new String();
    Integer zoneID;
    Date expiredAt;
    String topic = new String();
    String title = new String();
    String msg = new String();
    Message message = new Message();
    //LatLng location = new LatLng();


    /**
     * Constructor
     */
    public JSONParser(){

    }

    /**
     * parses a ArrayList of Messages to an JSONObject, containing a JSONArray with messages
     * @param ArrayList<Message>
     * @return JSONObject
     */
    public JSONObject parseMessagetoJSON(ArrayList<Message> msglist){

        this.msglist = msglist;

        //clear the jsonObject
        this.jsonObject.remove("Messages");
        for(int j = 0; j<jsonMsgArray.length();j++) {
            this.jsonMsgArray.remove(j);
        }
        this.jsonMsg.remove("Client-id");
        this.jsonMsg.remove("Message-id");
        this.jsonMsg.remove("Zone-id");
        this.jsonMsg.remove("Expired-at");
        this.jsonMsg.remove("Topic");
        this.jsonMsg.remove("Title");
        this.jsonMsg.remove("Message");
        //todo clear jsonMsg from location

        // get Messages from msglist and write them into the jsonArray
        for(int i = 0; i < msglist.size(); i++){

            message = msglist.get(i);
            clientID = message.getClient_ID();
            messageID = message.getMessage_ID();
            zoneID = message.getZone_ID();
            expiredAt = message.getExpired_At();
            topic = message.getTopic();
            title = message.getTitle();
            msg = message.getMsg();
            //todo get location form message

            try {
                this.jsonMsg.put("Client-id", clientID);
                this.jsonMsg.put("Message-id", messageID);
                this.jsonMsg.put("Zone-id", zoneID);
                this.jsonMsg.put("Expired-at", expiredAt);
                this.jsonMsg.put("Topic", topic);
                this.jsonMsg.put("Title", title);
                this.jsonMsg.put("Message", message);
                //todo put location into the array
                this.jsonMsgArray.put(i,jsonMsg);

            } catch (JSONException e) {
                e.printStackTrace();
                logger.log(Level.WARNING,"Writing into Jsonarray didn't work");
            }

        }

        try {
            jsonObject.put("Messages",jsonMsgArray);
        } catch (JSONException e) {
            e.printStackTrace();
            logger.log(Level.WARNING,"putting jsonArray into jsonObject didn't work");
        }
        return jsonObject;
    }

    /**
     * parses a jsonObject, containing a JsonArray, to an ArrayList of messages
     * @param jsonObject
     * @return ArrayList<Message>
     */
    public ArrayList<Message> parseJSONtoMessage(JSONObject jsonObject) {

        this.jsonObject = jsonObject;
        this.msglist.clear();

        try {
            jsonMsgArray = jsonObject.getJSONArray("Messages");
            for(int i = 0; i < jsonMsgArray.length(); i++){
                jsonMsg = jsonMsgArray.getJSONObject(i);
                clientID = (String) jsonMsg.getJSONObject("Client-id").get("Client-id");
                messageID = (String) jsonMsg.getJSONObject("Message-id").get("Message-id");
                zoneID = (Integer) jsonMsg.getJSONObject("Zone-id").get("Zone-id");
                expiredAt = (Date) jsonMsg.getJSONObject("Expired-at").get("Expired-at");
                topic = (String) jsonMsg.getJSONObject("Topic").get("Topic");
                title = (String) jsonMsg.getJSONObject("Title").get("Title");
                msg = (String) jsonMsg.getJSONObject("Message").get("Message");
                //todo get location


                message.setClient_ID(clientID);
                message.setMessage_ID(messageID);
                message.setZone_ID(zoneID);
                message.setExpired_At(expiredAt);
                message.setTopic(topic);
                message.setTitle(title);
                message.setMsg(msg);
                //todo set location
                this.msglist.add(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "no such JSONObject");
        }

        return this.msglist;
        // todo testing the method
    }


}
