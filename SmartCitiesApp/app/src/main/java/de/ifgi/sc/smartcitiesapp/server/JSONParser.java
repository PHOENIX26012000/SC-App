package de.ifgi.sc.smartcitiesapp.server;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.zone.Zone;

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
    String zoneID = new String();
    String crDt = new String();
    String exDt = new String();
    Date expiry = new Date();
    String topic = new String();
    String title = new String();
    Double latitude;
    Double longitude;
    String msg = new String();
    Message message = new Message();
    Zone zone = new Zone();
    //LatLng location = new LatLng();
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
             crDt = message.getCreated_At();
            latitude = message.getLatitude();
            longitude= message.getLongitude();
            topic = message.getTopic();
            title = message.getTitle();
            exDt = message.getExpired_At();
            msg = message.getMsg();
            //todo get location form message

            try {
                this.jsonMsg.put("Client-id", clientID);
                this.jsonMsg.put("Message-id", messageID);
                this.jsonMsg.put("Zone-id", zoneID);
                this.jsonMsg.put("Created-at",crDt);
                this.jsonMsg.put("Expired-at", exDt);
                this.jsonMsg.put("Topic", topic);
                this.jsonMsg.put("Title", title);
                this.jsonMsg.put("Latitude",latitude);
                this.jsonMsg.put("Longitude",longitude);
                this.jsonMsg.put("Message", msg);

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
                zoneID = (String) jsonMsg.getJSONObject("Zone-id").get("Zone-id");
                crDt = (String) jsonMsg.getJSONObject("Created-at").get("Created-at");
                exDt = (String) jsonMsg.getJSONObject("Expired-at").get("Expired-at");
                latitude = (double) jsonMsg.getJSONObject("Latitude").get("Latitude");
                longitude = (double) jsonMsg.getJSONObject("Longitude").get("Longitude");
                topic = (String) jsonMsg.getJSONObject("Topic").get("Topic");
                title = (String) jsonMsg.getJSONObject("Title").get("Title");
                msg = (String) jsonMsg.getJSONObject("Message").get("Message");

                //todo get location


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
            logger.log(Level.WARNING, "no such JSONObject");
        }

        return this.msglist;
        // todo testing the method
    }

    public ArrayList<Zone> parseJSONtoZone(JSONObject jsonObject){

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



                zone.name(name);
                zone.zoneID(zonerID);
                zone.expiredAt(expiredAt);
                zone.polygon(polygon);
                zone.topics(Topic);

                this.zonelist.add(zone);

            }


        }
        catch (JSONException e) {
            e.printStackTrace();
            logger.log(Level.WARNING, "no such JSONObject");
        }
        return this.zonelist;
    }

}