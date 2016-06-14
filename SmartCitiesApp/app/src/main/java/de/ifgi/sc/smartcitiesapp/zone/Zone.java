package de.ifgi.sc.smartcitiesapp.zone;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * Created by Clara on 12.06.2016.
 */
public class Zone {

    private String name;
    private String zoneID;
    private String expiredAt;
    private String[] topics;
    private ArrayList<LatLng> polygon;
    private static final Logger logger =
            Logger.getLogger("Zone");

    public Zone (String name, String zoneID, String expiredAt, String[] topics, ArrayList<LatLng> polygon){
        this.name = name;
        this.zoneID = zoneID;
        this.expiredAt = expiredAt;
        this.polygon = polygon;
        this.topics = topics;
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
