package de.ifgi.sc.smartcitiesapp.zone;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clara on 12.06.2016.
 */
public class Zone {
    private ArrayList<Zone> zoneList;
    private String name;
    private String zoneID;
    private String expiredAt;
    private String[] topics;
    private List<LatLng> polygon;

    public Zone (String name, String zoneID, String expiredAt, String[] topics, List<LatLng> polygon){
        this.name = name;
        this.zoneID = zoneID;
        this.expiredAt = expiredAt;
        for(int i = 0; i < topics.length; i++){
            this.topics[i]= topics[i];
        }
        //todo polygon

    }

    //todo set und get für Zone Attribute

    public void updateZones(){

    }

    public ArrayList<Zone> getZone (LatLng pos) {

        return zoneList;
    }
}
