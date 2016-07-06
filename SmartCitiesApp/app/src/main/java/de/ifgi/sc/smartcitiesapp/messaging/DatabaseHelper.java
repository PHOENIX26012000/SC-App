package de.ifgi.sc.smartcitiesapp.messaging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.zone.Zone;

/**
 * Created by SAAD on 5/18/2016.
 */
public class DatabaseHelper {

    private static final String  MESSAGE_ID= "M_id";
    private static final String  ZONE_ID= "Z_id";
    private static final String  CREATED_AT= "Cr_time";
    private static final String  LATITUDE= "Latitude";
    private static final String  LONGITUDE= "Longitude";
    private static final String  EXPIRED_AT= "Exp_time";
    private static final String  TOPIC = "Topic";
    private static final String  TITLE= "Title";
    private static final String  MESSAGE= "Msg_Body";

    private static final String DATABASE_NAME = "PeersData";
    private static final String TABLE_NAME = "TABLE_1";
    private static final int DATABASE_VERSION = 1;


    private static final String ZONE_NAME="Z_name";
    private static final String ZONE_TOPICS= "Topics";
    private static final String COORDINATES="Coordinates";

    private static final String ZONE_TABLE_NAME = "TABLE_2";



    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");



    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;

    public boolean zoneAlreadyExist(Zone zn) {
        boolean match = false;

        Cursor res =  ourDatabase.rawQuery( "select * from TABLE_2", null );
        res.moveToFirst();

        while(!res.isAfterLast() & match != true){

            if(zn.getZoneID().equals(res.getString(res.getColumnIndex(ZONE_ID))))
            {
                match = true;
            }

            else
            {res.moveToNext();
                match = false;
            }

        }

        return match;
    }


    private static class DbHelper extends SQLiteOpenHelper {

        /**
         * Create a helper object to create, open, and/or manage a database.
         * This method always returns very quickly.  The database is not actually
         * created or opened until one of {@link #getWritableDatabase} or
         * {@link #getReadableDatabase} is called.
         *
         * @param context to use to open or create the database
         */
        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Called when the database is created for the first time. This is where the
         * creation of tables and the initial population of the tables should happen.
         *
         * @param db The database.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            createMessagesTable(db);
            createZoneTable(db);

        }

        private void createMessagesTable(SQLiteDatabase db) {
            String query="CREATE TABLE " + TABLE_NAME + "(" + MESSAGE_ID + " TEXT NOT NULL, " +
                    ZONE_ID + " TEXT NOT NULL, " + CREATED_AT + " DATETIME, "+LATITUDE + " DOUBLE, " + LONGITUDE + " DOUBLE, " +
                    EXPIRED_AT + " DATETIME, " + TITLE + " TEXT NOT NULL, " +
                    TOPIC + " TEXT NOT NULL, " + MESSAGE + " TEXT NOT NULL);";
            Log.i("Msg table Created ", query);
            db.execSQL(query);
        }
        private void createZoneTable(SQLiteDatabase db) {
            String query="CREATE TABLE " + ZONE_TABLE_NAME + "(" + ZONE_NAME +
                    " TEXT NOT NULL, " +
                    ZONE_ID + " TEXT NOT NULL, " +
                    EXPIRED_AT + " DATETIME, " +
                    ZONE_TOPICS + " TEXT NOT NULL, " + COORDINATES + " TEXT NOT NULL);";
            db.execSQL(query);
            Log.i("Zone table Created ", query);
        }

        /**
         * Called when the database needs to be upgraded. The implementation
         * should use this method to drop tables, add tables, or do anything else it
         * needs to upgrade to the new schema version.
         * <p/>
         * <p>
         * The SQLite ALTER TABLE documentation can be found
         * <a href="http://sqlite.org/lang_altertable.html">here</a>. If you add new columns
         * you can use ALTER TABLE to insert them into a live table. If you rename or remove columns
         * you can use ALTER TABLE to rename the old table, then create the new table and then
         * populate the new table with the contents of the old table.
         * </p><p>
         * This method executes within a transaction.  If an exception is thrown, all changes
         * will automatically be rolled back.
         * </p>
         *
         * @param db         The database.
         * @param oldVersion The old database version.
         * @param newVersion The new database version.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            Log.i("Database Dropped", "yes");
            onCreate(db);
        }
    }

    public DatabaseHelper(Context c) {
        ourContext = c;
    }

    public DatabaseHelper open() throws SQLException {

        try {
            ourHelper = new DbHelper(ourContext);

            ourDatabase = ourHelper.getWritableDatabase();
            Log.i("Database created ", "no exception");

        } catch (Exception e) {
            Log.i("Database not created ", "exception raised");

        }
        return this;
    }

    public void close() {
        try {
            ourHelper.close();
            Log.i("Database closed ", "no exception raised");
        } catch (Exception e) {
            Log.i("Database  not closed ", "exception raised");

        }
    }

    public void createZoneEntry(String z_name, String z_id, String ex_time,String[] topics, ArrayList<LatLng> Coords){
        try {
            ContentValues cv = new ContentValues();
            cv.put(ZONE_NAME, z_name);
            cv.put(ZONE_ID, z_id);
            cv.put(EXPIRED_AT, ex_time);
            String temp=convCordsToString(Coords);
            Log.i("Coordinates converted ", temp);
            cv.put(COORDINATES, temp);
            temp=convertTopToString(topics);
            Log.i("Topics converted ", temp);
            cv.put(ZONE_TOPICS, temp);

            ourDatabase.insert(ZONE_TABLE_NAME, null, cv);
            Log.i("Z Database Entry", "Entry Sucessful");

        } catch (Exception e) {
            Log.i("Database Entry", "Entry failed");
        }
    }

    //This method will convert Array of LatLong to type String to be stored in table
    //Output will be Lat Long comma separated values
    private String convCordsToString(ArrayList<LatLng> Coords) {
        int size;
        size= Coords.size();
        LatLng pt;
        String temp="";
        for(int i=0;i<size;i++){
            pt=Coords.get(i);
            temp= temp+String.valueOf(pt.latitude)+","+String.valueOf(pt.longitude)+",";

        }
        return temp.substring(0,temp.length()-1); // Deleting last comma
    }
    //This method will convert Array of Topics to comma separated values in String to be stored in table
    private String convertTopToString(String[] topics) {
        int size;
        size=topics.length;
        String temp="";
        for(int i=0;i<size;i++){

            temp= temp+topics[i]+",";

        }
        return temp.substring(0,temp.length()-1); // Deleting last comma
    }


    public void createEntry(String m_id, String z_id, String cr_time, Double lat,Double lon, String  ex_time, String top, String title, String msg) {
        try {
            ContentValues cv = new ContentValues();

            cv.put(MESSAGE_ID, m_id);
            cv.put(ZONE_ID, z_id);
            cv.put(CREATED_AT, cr_time);
            cv.put(LATITUDE, lat);
            cv.put(LONGITUDE, lon);
            cv.put(EXPIRED_AT, ex_time);
            cv.put(TOPIC, top);
            cv.put(TITLE, title);
            cv.put(MESSAGE, msg);
            ourDatabase.insert(TABLE_NAME, null, cv);

        } catch (Exception e) {
            Log.i("Database Entry", "Entry failed");
        }


    }




    public ArrayList<Message> getAllMessages()
    {
        Date ex_date = null;
        Date cr_date = null;
        Message mes;
        ArrayList<Message> array_list = new ArrayList<Message>();

        Cursor res =  ourDatabase.rawQuery( "select * from TABLE_1", null );

        res.moveToFirst();

        while(res.isAfterLast() == false){


            try {

                ex_date= D_format.parse(res.getString(res.getColumnIndex(EXPIRED_AT)));
                cr_date = D_format.parse(res.getString(res.getColumnIndex(CREATED_AT)));

            }catch (ParseException e) {


                e.printStackTrace();
            }
            Log.i("Lat and long",res.getString(res.getColumnIndex(LATITUDE))+" "+ res.getString(res.getColumnIndex(LONGITUDE)));
            //Checking for null value in database otherwise will raise exception while parsing for Double
            if(res.getString(res.getColumnIndex(LATITUDE))!=null) {
                Double lat = Double.parseDouble(res.getString(res.getColumnIndex(LATITUDE)));
                Double lon = Double.parseDouble(res.getString(res.getColumnIndex(LONGITUDE)));
                mes = new Message(res.getString(res.getColumnIndex(MESSAGE_ID)),
                        res.getString(res.getColumnIndex(ZONE_ID)), cr_date,
                        lat,
                        lon,
                        ex_date,
                        res.getString(res.getColumnIndex(TOPIC)),
                        res.getString(res.getColumnIndex(TITLE)), res.getString(res.getColumnIndex(MESSAGE)));
            }else{
                 mes = new Message(res.getString(res.getColumnIndex(MESSAGE_ID)),
                        res.getString(res.getColumnIndex(ZONE_ID)), cr_date,

                        ex_date,
                        res.getString(res.getColumnIndex(TOPIC)),
                        res.getString(res.getColumnIndex(TITLE)),res.getString(res.getColumnIndex(MESSAGE)));
            }


            array_list.add(mes);
            res.moveToNext();

        }
        return array_list;


    }

    public boolean messageAlreadyExist(Message msg) {
        boolean match = false;

        Cursor res =  ourDatabase.rawQuery( "select * from TABLE_1", null );
        res.moveToFirst();

        while(!res.isAfterLast() & match != true){

            if(msg.getMessage_ID().equals(res.getString(res.getColumnIndex(MESSAGE_ID))))
            {
                match = true;
            }

            else
            {res.moveToNext();
                match = false;
          }

        }

        return match;

    }
    public ArrayList<Zone> getAllZones_DB(){
        Date ex_date = null;

        ArrayList<Zone> array_list = new ArrayList<Zone>();

        Cursor res =  ourDatabase.rawQuery( "select * from TABLE_2", null );
        res.moveToFirst();
        while(res.isAfterLast() == false){

           ArrayList<LatLng> coords=stringToCoords(res.getString(res.getColumnIndex(COORDINATES)));
            String[] top= (res.getString(res.getColumnIndex(ZONE_TOPICS))).split(",");
           Zone zn = new Zone(res.getString(res.getColumnIndex(ZONE_NAME)),
                   res.getString(res.getColumnIndex(ZONE_ID)),
                   res.getString(res.getColumnIndex(EXPIRED_AT)),top,
                   coords);


            array_list.add(zn);
            res.moveToNext();
        }
        return array_list;
    }

    private ArrayList<LatLng> stringToCoords(String string) {
        String[] list=string.split(",");
        ArrayList<LatLng> coords=new ArrayList<LatLng>();
        int size=list.length;
        for(int i=0;i<size;i=i+2){
            LatLng LL = new LatLng(Double.parseDouble(list[i]), Double.parseDouble(list[i+1]));
            coords.add(LL);
        }
        return coords;
    }

    public void deleteExpiredMnZ() {
        ourDatabase.execSQL("Delete from TABLE_1 where Exp_time <= datetime(date('now'))");
        ourDatabase.execSQL("Delete from TABLE_2 where Exp_time <= datetime(date('now'))");
    }
}
