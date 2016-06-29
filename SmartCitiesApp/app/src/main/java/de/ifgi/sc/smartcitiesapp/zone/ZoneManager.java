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
    private Context ourContext;
    private PolyUtil polyUtil;
    private Zone zone;
    private ArrayList<LatLng> polygon;
    private Zone currentZone;

    public static ZoneManager instance; // global singleton instance

    public static void initInstance(Context c)
    {
        if (instance == null){
            // Create the instance
            instance = new ZoneManager();
            instance.currentZones = new ArrayList<>();
            instance.ourContext = c;
        }
    }

    public ZoneManager(){

    }

    public static ZoneManager getInstance()
    {
        // Return the instance
        return instance;
    }

    /**
     * returns all Zones in which the User is, determined by his current position
     * @param position LatLng
     * @return ArrayList<Zone>
     */
    public synchronized ArrayList<Zone> getCurrentZones (LatLng position) {
        currentZones.clear();
        allZones = getAllZonesfromDatabase();
        for(int i=0; i< allZones.size();i++){
            zone = allZones.get(i);
            polygon = zone.getPolygon();
            Boolean inside = polyUtil.containsLocation(position,polygon,false);
            if(inside){
                currentZones.add(zone);
            }
        }
        return currentZones;
    }


    /**
     * This methods will store all zones in database having unique Zone_IDs.
     * Zone with prematching zoneID will simply be ignored
     * @param zones
     */
    public synchronized void updateZonesInDatabase(ArrayList<Zone> zones){
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
    public synchronized ArrayList<Zone> getAllZonesfromDatabase(){
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
    public synchronized String getCurrentZoneID(){
        return currentZone.getZoneID();
    }

    
    /**
     * setting current Zone
     * @param zone
     */
    public synchronized void setCurrentZone(Zone zone){
        this.currentZone = zone;
    }

    public synchronized Zone getCurrentZone() throws NoZoneCurrentlySelectedException{
        if (currentZone!=null)
            return currentZone;
        throw new NoZoneCurrentlySelectedException();
    }

}
