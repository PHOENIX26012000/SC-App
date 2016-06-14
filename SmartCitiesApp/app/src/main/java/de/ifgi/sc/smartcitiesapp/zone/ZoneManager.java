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
        zoneDBList = getZonesfromDB();
        //todo  compare zones from db to new zones from server
        //todo save new zone in db
    }

    /**
     * returns all Zones containing the parameter position
     * @param position
     * @return
     */
    public ArrayList<Zone> getZone (LatLng position) {
        zoneDBList = getZonesfromDB();
        //todo polygone contains position?, yes -> add to zoneList
        return zoneList;
    }

    public ArrayList<Zone> getZonesfromDB (){
        //todo get Zone from the database
        //todo check if Zone from database are expired, if so delete them from db

        return zoneDBList;
    }
}
