package de.ifgi.sc.smartcitiesapp.zone;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Clara on 12.06.2016.
 */
public class Zone {

    private String name;
    private String zoneID;
    private String expiredAt;
    private String[] topics;
    private ArrayList<LatLng> polygon;

    public Zone (String name, String zoneID, String expiredAt, String[] topics, ArrayList<LatLng> polygon){
        this.name = name;
        this.zoneID = zoneID;
        this.expiredAt = expiredAt;
        for(int i = 0; i < topics.length; i++){
            this.topics[i]= topics[i];
        }
        for(int j = 0; j < polygon.size(); j++){
            this.polygon.add(j,polygon.get(j));
        }

    }

    public void setName(String name){
        this.name = name;
    }

    public void setZoneID (String zoneID){
        this.zoneID = zoneID;
    }

    public void setExpiredAt (String expiredAt){
        this.expiredAt = expiredAt;
    }

    public void setTopics (String[] topics){
        for(int i = 0; i < topics.length; i++){
            this.topics[i]= topics[i];
        }
    }

    public void setPolygon (ArrayList<LatLng> polygon){
        for(int j = 0; j < polygon.size(); j++){
            this.polygon.add(j,polygon.get(j));
        }
    }

    public String getName (){
        return this.name;
    }

    public String getZoneID (){
        return this.zoneID;
    }

    public String getExpiredAt () {
        return this.expiredAt;
    }

    public String[] getTopics () {
        return this.topics;
    }

    public ArrayList<LatLng> getPolygon () {
        return this.polygon;
    }

}
