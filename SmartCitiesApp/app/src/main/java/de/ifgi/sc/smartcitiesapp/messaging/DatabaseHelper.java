package de.ifgi.sc.smartcitiesapp.messaging;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by SAAD on 5/18/2016.
 */
public class DatabaseHelper {
    private static final String  CLIENT_ID= "C_id";
    private static final String  MESSAGE_ID= "M_id";
    private static final String  ZONE_ID= "Z_id";
    private static final String  EXPIRED_AT= "Exp_time";
    private static final String  CATEGORY= "Category";
    private static final String  TITLE= "Title";
    private static final String  MESSAGE= "Msg_Body";


    private static final String DATABASE_NAME = "PeersData";
    private static final String TABLE_NAME = "TABLE_1";
    private static final int DATABASE_VERSION = 1;

    private SimpleDateFormat D_format = new SimpleDateFormat("EEE MMM DD HH:mm:ss z yyyy");
    private Date startDate;

    private DbHelper ourHelper;
    private final Context ourContext;
    private SQLiteDatabase ourDatabase;


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
            db.execSQL("CREATE TABLE " + TABLE_NAME + "(" + CLIENT_ID +
                    " INTEGER NOT NULL, " + MESSAGE_ID + " INTEGER NOT NULL, " +
                    ZONE_ID + " INTEGER NOT NULL, " + EXPIRED_AT + " DATETIME, " + TITLE + " TEXT NOT NULL, " +
                    CATEGORY + " TEXT NOT NULL, " + MESSAGE + " TEXT NOT NULL);");
            Log.i("Database Created", "yes");
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

    //Need to change type ot ex_time here
    public void createEntry(String c_id, String m_id, int z_id, String ex_time, String cat, String title, String msg) {
        try {
            ContentValues cv = new ContentValues();
            cv.put(CLIENT_ID, c_id);
            cv.put(MESSAGE_ID, m_id);
            cv.put(ZONE_ID, z_id);
            cv.put(EXPIRED_AT, ex_time);
            cv.put(CATEGORY, cat);
            cv.put(TITLE, title);
            cv.put(MESSAGE, msg);
            ourDatabase.insert(TABLE_NAME, null, cv);
            Log.i("Data Saved Msg: " + c_id, " Successfull");
        } catch (Exception e) {
            Log.i("Database Entry", "Entry failed");
        }


    }

    public ArrayList<Message> getAllMessages()
    {
        ArrayList<Message> array_list = new ArrayList<Message>();
        DbHelper d= new DbHelper(ourContext);
        Log.i("Messages ", "Retrieved");
        SQLiteDatabase db1 = d.getReadableDatabase();

        Log.i("Database ", "Opened");
        Cursor res =  db1.rawQuery( "select * from Table_1", null );
        res.moveToFirst();


        while(res.isAfterLast() == false){

            try {
                startDate = D_format.parse(res.getString(res.getColumnIndex(EXPIRED_AT)));
                String newDateString = D_format.format(startDate);
            }


            catch (ParseException e) {
                e.printStackTrace();
            }

            Message mes = new Message(res.getString(res.getColumnIndex(CLIENT_ID)),res.getString(res.getColumnIndex(MESSAGE_ID)),
                    Integer.parseInt(res.getString(res.getColumnIndex(ZONE_ID))), startDate,res.getString(res.getColumnIndex(CATEGORY)),
                    res.getString(res.getColumnIndex(TITLE)),res.getString(res.getColumnIndex(MESSAGE)));
            Log.i("Messages"+mes, "Ok");
            array_list.add(mes);
            res.moveToNext();
        }
        return array_list;


    }



}
