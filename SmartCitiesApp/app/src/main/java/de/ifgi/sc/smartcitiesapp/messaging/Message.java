package de.ifgi.sc.smartcitiesapp.messaging;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private String client_ID;
    private String message_ID;
    private Integer zone_ID;
    private String ex_Dt;
    private String topic;
    private String title;
    private String message;
    private double latitude;
    private double longitude;
    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    public Message(String c_id, String m_id, int z_id,double lat,double lon, Date exDt, String top, String tit, String msg){
        client_ID=c_id;
        message_ID=m_id;
        zone_ID=z_id;
        latitude=lat;
        longitude=lon;
        ex_Dt=changeDateFormat(exDt);
        topic=top;
        title=tit;
        message=msg;
        Log.i("Msg "+c_id, " Created ");
    }

    public void setClient_ID(String id){
        client_ID=id;
    }
    public void setMessage_ID(String id){
        message_ID=id;
    }
    public void setZone_ID(Integer id){
        zone_ID=id;
    }
    public void setLatitude(double lat)  { latitude=lat; };
    public void setLongitude(double lon) {longitude=lon; };
    public void setExpired_At(Date dt){
        ex_Dt=changeDateFormat(dt);
    }
    public void setCategory(String top){topic=top;}
    public void setTitle(String tit){   title=tit;}
    public void setMsg(String   m){message=m;}

    public String getClient_ID(){return client_ID;};
    public String getMessage_ID(){return message_ID;};
    public Integer getZone_ID(){return zone_ID;};
    public double getLatitude(){return latitude;};
    public double getLongitude(){return longitude;};
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

}
