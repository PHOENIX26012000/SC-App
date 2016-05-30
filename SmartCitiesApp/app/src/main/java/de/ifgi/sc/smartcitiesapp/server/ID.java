package de.ifgi.sc.smartcitiesapp.server;

import java.util.UUID;

/**
 * Created by Clara on 30.05.2016.
 */
public class ID {

    String id = new String();

    /**
     * Constructor
     * generate a new random ID
     */
    public ID (){
        id = UUID.randomUUID().toString();
    }


    /**
     * get ID as a String
     * @return String
     */
    public String getID () {
        return id;
    }

    /**
     * sets id to null
     */
    public void clear (){
        id = null;
    }

    /**
     * changes the id to a new random one
     * @return
     */
    public String changeID (){
        return id = UUID.randomUUID().toString();
    }

}
