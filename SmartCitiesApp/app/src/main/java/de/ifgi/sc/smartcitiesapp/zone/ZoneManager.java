package de.ifgi.sc.smartcitiesapp.zone;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by Clara on 13.06.2016.
 */
public class ZoneManager {
    private ArrayList<Zone> zoneList;
    private ArrayList<Zone> zoneDBList;


    /**
     * updates Zones in the Database
     * @param zoneList
     */
    public void updateZones(ArrayList<Zone> zoneList){
        //todo save new zone in db
    }

    /**
     * returns all Zones containing the parameter position
     * @param position
     * @return
     */
    public ArrayList<Zone> getZone (LatLng position) {
        //todo get Zone from the database
        //todo polygone contains position?, yes -> add to zoneList
        return zoneList;
    }

}
