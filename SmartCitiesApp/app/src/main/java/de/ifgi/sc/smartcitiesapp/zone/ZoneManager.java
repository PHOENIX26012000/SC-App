package de.ifgi.sc.smartcitiesapp.zone;


import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.PolyUtil;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.messaging.DatabaseHelper;


/**
 * Created by Clara on 13.06.2016.
 */
public class ZoneManager {
    private ArrayList<Zone> allZones;
    private ArrayList<Zone> currentZones;
    private final Context ourContext;
    private PolyUtil polyUtil;
    private Zone zone;
    private ArrayList<LatLng> polygon;
    private Zone currentZone;

    public ZoneManager(Context c){
        ourContext = c;
    }

    /**
     * returns all Zones in which the User is, determined by his current position
     * @param position LatLng
     * @return ArrayList<Zone>
     */
    public ArrayList<Zone> getZone (LatLng position) {
        currentZones.clear();
        allZones = getAllZonesfromDatabase();
        Log.i("Info","Position: "+position);
        for(int i=0; i< allZones.size();i++){
            zone = allZones.get(i);
            polygon = zone.getPolygon();
            Log.i("Info","Comparing Zone: "+ zone.getZoneID());
            if(polyUtil.containsLocation(position,polygon,true)){
                currentZones.add(zone);
                Log.i("Info","CurrentZone: "+ zone.getZoneID());
            }
        }
        Log.i("Info", "Current Zones checked");
        return currentZones;
    }


    /**
     * This methods will store all zones in database having unique Zone_IDs.
     * Zone with prematching zoneID will simply be ignored
     * @param zones
     */
    public void updateZonesInDatabase(ArrayList<Zone> zones){
        //Checking size of Arraylist
        int size;
        size= zones.size();
        Zone zn;

        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();

        for(int i=0;i<size;i++){
            Log.i("This is Zone  "+i," Number");
            zn= zones.get(i);
            if(db.zoneAlreadyExist(zn) == false){
                db.createZoneEntry(zn.getName(),zn.getZoneID(),zn.getExpiredAt(),zn.getTopics(),zn.getPolygon());

            }

        }
        Log.i("Zones  "," stored");
        db.close();

    }

    /**
     * This method will return all zones stored in Database
     */
    public ArrayList<Zone> getAllZonesfromDatabase(){
        DatabaseHelper db = new DatabaseHelper(ourContext);
        db.open();
        ArrayList<Zone> zones= db.getAllZones_DB();
        db.close();

        return zones;
    }

    /**
     * return the ZoneID from the Zone in which the User is currently
     * @return String ZoneID
     */
    public String getCurrentZoneID(){
        return currentZone.getZoneID();
    }

    
    /**
     * setting current Zone
     * @param zone
     */
    public void setCurrentZone(Zone zone){
        this.currentZone = zone;
    }


}
