package de.ifgi.sc.smartcitiesapp.messaging;


import android.util.Log;
import java.io.Serializable;
import java.security.PrivateKey;
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

    // Topic of message
    private String topic;

    // Title of the message
    private String title;

    // Content of the message
    private String message;

    // Related coordinates
    private Double latitude;
    private Double longitude;



    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Message(String m_id, String z_id, Date crDt, Double lat,Double lon, Date exDt, String top, String tit, String msg){

        message_ID=m_id;
        zone_ID=z_id;
        cr_Dt = changeDateFormat(crDt);
        latitude=lat;
        longitude=lon;
        ex_Dt=changeDateFormat(exDt);
        topic=top;
        title=tit;
        message=msg;

    }

    //This constructor will be called when user does not specify coordinates of location
    public Message(String m_id, String z_id, Date crDt, Date exDt, String top, String tit, String msg){

        message_ID=m_id;
        zone_ID=z_id;
        cr_Dt = changeDateFormat(crDt);
        latitude= null;
        longitude=null;
        ex_Dt=changeDateFormat(exDt);
        topic=top;
        title=tit;
        message=msg;

    }

    public void setMessage_ID(String id){
        message_ID=id;
    }
    public void setZone_ID(String id){
        zone_ID=id;
    }
    public void setCreated_At(Date dt){cr_Dt=changeDateFormat(dt);}
    public void setLatitude(Double lat)  { latitude=lat; };
    public void setLongitude(Double lon) {longitude=lon; };
    public void setExpired_At(Date dt){
        ex_Dt=changeDateFormat(dt);
    }
    public void setCategory(String top){topic=top;}
    public void setTitle(String tit){   title=tit;}
    public void setMsg(String   m){message=m;}


    public String getMessage_ID(){return message_ID;};
    public String getZone_ID(){return zone_ID;};
    public String getCreated_At(){return cr_Dt;};
    public Double getLatitude(){return latitude;};
    public Double getLongitude(){return longitude;};
    public String getExpired_At(){return ex_Dt;};
    public String getTopic(){return topic;};
    public String getTitle(){return title;};
    public String getMsg(){return message;};

    //This method takes Date as input and convert it in specific format
    // D_format= "yyyy-MM-dd'T'HH:mm:ss.SSSZ"
    //to store Expired date in this format in database.
    private String changeDateFormat(Date ex_time) {

        Log.i("Date format changed to ",D_format.format(ex_time));

        return D_format.format(ex_time);
//        }
    }

    public String toString() {
        return "client_ID," + this.client_ID +
                ", message_ID," + this.message_ID +
                ", zone_ID," + this.zone_ID +
                ", cr_Dt," + this.cr_Dt +
                ", ex_Dt," + this.ex_Dt +
                ", topic," + this.topic +
                ", title," + this.title +
                ", message," + this.message +
                ", latitude," + this.latitude +
                ", longitude," + this.longitude;


    }
}
