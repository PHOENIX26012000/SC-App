package de.ifgi.sc.smartcitiesapp.zone;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * This class describes a zone in which users of this app can exchange message.
 * These Zones consist of a Name, ID, expiring Date, Polygon and Topics.
 */
public class Zone {

    private String name;
    private String zoneID;
    private String expiredAt;
    private String[] topics;
    private ArrayList<LatLng> polygon;
    private static final Logger logger =
            Logger.getLogger("Zone");

    /**
     * Constructor of Zones with all Attributes, Name, Zone-id, Expired Date, Topics, Polygon
     * @param name  Name of the Zone
     * @param zoneID    ID of the Zone
     * @param expiredAt Date on which the Zone expires and is deleted from the Database
     * @param topics    Topics of the Zone
     * @param polygon   Area of the Zone
     */
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
