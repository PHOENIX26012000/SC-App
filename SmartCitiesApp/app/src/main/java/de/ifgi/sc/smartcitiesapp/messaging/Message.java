package de.ifgi.sc.smartcitiesapp.messaging;


import java.security.PrivateKey;
import java.text.SimpleDateFormat;

public class Message {

    private String Client_ID;
    private String Message_ID;
    private Integer Zone_ID;
    SimpleDateFormat Expired_At = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private String Category;
    private String Title;
    private String Msg;
}
