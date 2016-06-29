package de.ifgi.sc.smartcitiesapp.server;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.zone.Zone;



public class JSONParser {

    ArrayList<Message> msglist = new ArrayList<Message>();

    JSONObject jsonObject = new JSONObject();
    JSONArray jsonMsgArray = new JSONArray();
    JSONObject jsonMsg = new JSONObject();
    JSONObject jsonLoc = new JSONObject();
    JSONArray jsonCoords = new JSONArray();
    String clientID = new String();
    String messageID = new String();
    String zoneID = new String();
    String crDt = new String();
    String exDt = new String();
    Date expiry = new Date();
    String topic = new String();
    String title = new String();
    Double latitude;
    Double longitude;
    String msg = new String();
    Message message;
    private Zone zone;
    ArrayList<Zone> zonelist = new ArrayList<>();

    JSONArray jsonZoneArray = new JSONArray();
    JSONObject zoneMsg = new JSONObject();

    String name = new String();
    String zonerID = new String();
    String expiredAt= new String();
    String Topic= new String();
    ArrayList<LatLng> polygon= new ArrayList<LatLng>();



    /**
     * Constructor
     */
    public JSONParser(){

    }

    /**
     * parses a ArrayList of Messages to an JSONObject, containing a JSONArray with messages
     * @param //ArrayList<Message>
     * @return JSONObject
     */

    public JSONObject parseMessagetoJSON(ArrayList<Message> msglist){

        this.msglist = msglist;

        //clear the jsonObject and the jsonArrays
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
        this.jsonLoc.remove("Coordinate");
        for(int j = 0; j<jsonCoords.length();j++){
            this.jsonCoords.remove(j);
        }

        // get Messages from msglist and write them into the jsonArray
        for(int i = 0; i < msglist.size(); i++){

            message = msglist.get(i);
            clientID = message.getClient_ID();
            messageID = message.getMessage_ID();
            zoneID = message.getZone_ID();
             crDt = message.getCreated_At();
            latitude = message.getLatitude();
            longitude= message.getLongitude();
            topic = message.getTopic();
            title = message.getTitle();
            exDt = message.getExpired_At();
            msg = message.getMsg();
            longitude = message.getLongitude();
            latitude = message.getLatitude();

            try {
                this.jsonMsg.put("Client-id", clientID);
                this.jsonMsg.put("Message-id", messageID);
                this.jsonMsg.put("Zone-id", zoneID);
                this.jsonMsg.put("Created-at",crDt);
                this.jsonMsg.put("Expired-at", exDt);
                this.jsonMsg.put("Topic", topic);
                this.jsonMsg.put("Title", title);
                this.jsonMsg.put("Message", msg);

                this.jsonCoords.put(0,latitude+","+longitude);
                this.jsonLoc.put("Coordinate",jsonCoords);
                this.jsonMsg.put("Location",jsonLoc);

                this.jsonMsgArray.put(i,jsonMsg);

            } catch (JSONException e) {
                e.printStackTrace();
                Log.w("msgToJson","Writing into Jsonarray of Messages didn't work");
            }

        }

        try {
            jsonObject.put("Messages",jsonMsgArray);
        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("MsgToJSON","putting jsonArray into jsonObject didn't work");
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
                clientID = (String) jsonMsg.get("Client-id");
                messageID = (String) jsonMsg.get("Message-id");
                zoneID = (String) jsonMsg.get("Zone-id");
                crDt = (String) jsonMsg.get("Created-at");
                exDt = (String) jsonMsg.get("Expired-at");
                topic = (String) jsonMsg.get("Topic");
                title = (String) jsonMsg.get("Title");
                msg = (String) jsonMsg.get("Message");
                jsonLoc = jsonMsg.getJSONObject("Location");
                jsonCoords = jsonMsg.getJSONArray("Coordinate");
                for(int j = 0; i < jsonCoords.length(); i++){
                    jsonCoords.get(i);

                }
                //todo get location
                //latitude = (double) jsonMsg.getJSONObject("Latitude").get("Latitude");
                //longitude = (double) jsonMsg.getJSONObject("Longitude").get("Longitude");

                message.setClient_ID(clientID);
                message.setMessage_ID(messageID);
                message.setZone_ID(zoneID);
                message.setCreated_At(crDt);
                message.setExpired_At(expiry);
                //message.setTopic(topic);
                message.setTitle(title);
                message.setMsg(msg);
                message.setLatitude(latitude);
                message.setLongitude(longitude);
                //todo set location
                this.msglist.add(message);

            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.w("JsonToMessage", "no such JSONObject");
        }

        return this.msglist;
        // todo testing the method
    }


    /**
     *
     * @param jsonObject
     */
    public void parseJSONtoZone(JSONObject jsonObject){

    this.jsonObject=jsonObject;
    this.zonelist.clear();

        try{
            jsonZoneArray= jsonObject.getJSONArray("Zones");
            for (int i = 0; i < jsonZoneArray.length(); i++){
                zoneMsg = jsonZoneArray.getJSONObject(i);
                name = (String) zoneMsg.getJSONObject("Name").get("Name");
                zonerID = (String) zoneMsg.getJSONObject("Zone-id").get("Zone-id");
                Topic = (String) zoneMsg.getJSONObject("Topics").get("Topics");
                expiredAt = (String) zoneMsg.getJSONObject("Expired-at").get("Expired-at");
                polygon = (ArrayList<LatLng>) zoneMsg.getJSONObject("Polygon").get("Polygon");


            }


        }
        catch (JSONException e) {
            e.printStackTrace();
            Log.w("JsonToZone", "no such JSONObject");
        }

    }

}