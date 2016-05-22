package de.ifgi.sc.smartcitiesapp.messaging;


import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Message {

    private String Client_ID;
    private String Message_ID;
    private Integer Zone_ID;
    private SimpleDateFormat D_form = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private Date Ex_Dt;
    private String Category;
    private String Title;
    private String Msg;

    public Message(String c_id, String m_id, int z_id, Date ex_time, String cat, String title, String msg){
        Client_ID=c_id;
        Message_ID=m_id;
        Zone_ID=z_id;
        Ex_Dt=ex_time;
        Category=cat;
        Title=title;
        Msg=msg;
        Log.i("Msg "+c_id, " Created ");
    }

    public void setClient_ID(String id){
        Client_ID=id;
    }
    public void setMessage_ID(String id){
        Message_ID=id;
    }
    public void setZone_ID(Integer id){
        Zone_ID=id;
    }
    public void setExpired_At(Date dt){
        Ex_Dt=dt;
    }
    public void setCategory(String cat){
        Category=cat;
    }
    public void setTitle(String tit){   Title=tit;}
    public void setMsg(String   m){
        Msg=m;
    }

    public String getClient_ID(){return Client_ID;};
    public String getMessage_ID(){return Message_ID;};
    public Integer getZone_ID(){return Zone_ID;};
    public Date getExpired_At(){return Ex_Dt;};
    public String getCategory(){return Category;};
    public String getTitle(){return Title;};
    public String getMsg(){return Msg;};


}
