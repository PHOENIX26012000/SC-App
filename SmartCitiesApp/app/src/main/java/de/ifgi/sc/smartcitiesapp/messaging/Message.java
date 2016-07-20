package de.ifgi.sc.smartcitiesapp.messaging;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Message {

    private String message_ID;

    // Unique zone ID
    private String zone_ID;

    // Date when message was created
    private String cr_Dt;

    // Date when message expires
    private String ex_Dt;

    // opic of message
    private String topic;

    // Title of the message
    private String title;

    // Content of the message
    private String message;

    // Related coordinates
    private Double latitude;
    private Double longitude;

    //If message to be shared with server
    private boolean shareWithServer;


    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    /**
     * First constructor with lat lng
     *
     * @param m_id
     * @param z_id
     * @param crDt
     * @param lat
     * @param lon
     * @param exDt
     * @param top
     * @param tit
     * @param msg
     * @param share
     */
    public Message(String m_id, String z_id, Date crDt, Double lat, Double lon, Date exDt, String top, String tit, String msg, boolean share) {

        message_ID = m_id;
        zone_ID = z_id;
        cr_Dt = changeDateFormat(crDt);
        latitude = lat;
        longitude = lon;
        ex_Dt = changeDateFormat(exDt);
        topic = top;
        title = tit;
        message = msg;
        shareWithServer = share;

    }

    /**
     * Second constructor
     * This constructor will be called when user does not specify coordinates of location
     *
     * @param m_id
     * @param z_id
     * @param crDt
     * @param exDt
     * @param top
     * @param tit
     * @param msg
     * @param share
     */
    public Message(String m_id, String z_id, Date crDt, Date exDt, String top, String tit, String msg, boolean share) {

        message_ID = m_id;
        zone_ID = z_id;
        cr_Dt = changeDateFormat(crDt);
        latitude = null;
        longitude = null;
        ex_Dt = changeDateFormat(exDt);
        topic = top;
        title = tit;
        message = msg;
        shareWithServer = share;

    }

    /**
     * Setter message id
     *
     * @param id
     */
    public void setMessage_ID(String id) {
        message_ID = id;
    }

    /**
     * Setter zone id
     *
     * @param id
     */
    public void setZone_ID(String id) {
        zone_ID = id;
    }

    /**
     * Setter date
     *
     * @param dt
     */
    public void setCreated_At(Date dt) {
        cr_Dt = changeDateFormat(dt);
    }

    /**
     * Setter lat
     *
     * @param lat
     */
    public void setLatitude(Double lat) {
        latitude = lat;
    }

    /**
     * Setter lng
     *
     * @param lon
     */
    public void setLongitude(Double lon) {
        longitude = lon;
    }

    /**
     * Setter expiry date
     *
     * @param dt
     */
    public void setExpired_At(Date dt) {
        ex_Dt = changeDateFormat(dt);
    }

    /**
     * Setter category
     *
     * @param top
     */
    public void setCategory(String top) {
        topic = top;
    }

    /**
     * Setter title
     *
     * @param tit
     */
    public void setTitle(String tit) {
        title = tit;
    }

    /**
     * Setter message
     *
     * @param m
     */
    public void setMsg(String m) {
        message = m;
    }

    /**
     * Setter boolean share with server
     *
     * @param share
     */
    public void setShareWithServer(boolean share) {
        shareWithServer = share;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getMessage_ID() {
        return message_ID;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getZone_ID() {
        return zone_ID;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getCreated_At() {
        return cr_Dt;
    }

    /**
     * Getter
     *
     * @return
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * Getter
     *
     * @return
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getExpired_At() {
        return ex_Dt;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getTopic() {
        return topic;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getTitle() {
        return title;
    }

    /**
     * Getter
     *
     * @return
     */
    public String getMsg() {
        return message;
    }

    /**
     * Getter
     *
     * @return
     */
    public boolean getShareWithServer() {
        return shareWithServer;
    }


    /**
     * This method takes Date as input and convert it in specific format
     * D_format= "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
     * to store Expired date in this format in database.
     *
     * @param ex_time
     * @return
     */
    private String changeDateFormat(Date ex_time) {

        //Log.i("Date format changed to ",D_format.format(ex_time));

        return D_format.format(ex_time);
    }

    /**
     * To string
     *
     * @return
     */
    public String toString() {
        return "message_ID," + this.message_ID +
                ", zone_ID," + this.zone_ID +
                ", cr_Dt," + this.cr_Dt +
                ", ex_Dt," + this.ex_Dt +
                ", topic," + this.topic +
                ", title," + this.title +
                ", message," + this.message +
                ", latitude," + this.latitude +
                ", longitude," + this.longitude +
                ", shareWithServer," + this.shareWithServer;


    }

    /**
     * Message expired check
     *
     * @return
     */
    public boolean messageExpired() {
        boolean match = false;
        Date expired = null;
        Date current = new Date();
        try {
            expired = D_format.parse(getExpired_At());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (current.after(expired)) {
            match = true;
        } else {
            match = false;
        }
        return match;
    }
}
