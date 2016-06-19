package de.ifgi.sc.smartcitiesapp.server;

//import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
//import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.messaging.Message;



public class ServerConnection implements Connection {

    JSONParser jsonParser = new JSONParser();
    ArrayList<Message> messages = new ArrayList<Message>();
    JSONObject jsonObject = new JSONObject();

    /**
     * Constructor
     */
    // public ServerConnection() {
    //     getMessages();
    // }

    @Override
    /**
     *  gets a set of Messages in form of an ArrayList and pushs it to the Server
     */
    public void shareMessage(ArrayList<Message> messages){
        this.messages = messages;
        jsonObject = jsonParser.parseMessagetoJSON(messages);
        //JSONArray share = new JSONArray();
        //share.put(jsonObject);
        URL url = null;
        HttpURLConnection client = null;
        try {
            url = new URL("http://giv-project6.uni-muenster.de:8080/api/addmessages/");
            client = (HttpURLConnection) url.openConnection();
            client.setRequestMethod("POST");
            client.setRequestProperty("Key","Value");
            client.setDoOutput(true);
            client.setChunkedStreamingMode(0);
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
            //writeStream(out);
            out.write(jsonObject.toString());
            out.flush();
            out.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(client != null) // Make sure the connection is not null.
                client.disconnect();
        }



    }

    /**
     *  requests the server for Messages and shares them with the Messanger
     */
    public void getMessages() throws MalformedURLException, IOException{

        //todo request to server for Messages as JSONArray
        URL obj = new URL("http://giv-project6.uni-muenster.de:8080/api/messages/");
        HttpURLConnection conn = null;
        try{
            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            conn.setRequestProperty("Key","Value");
            InputStream in = new BufferedInputStream(conn.getInputStream());
            BufferedReader buff= new BufferedReader(new InputStreamReader(in));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while((inputStr=buff.readLine())!=null)
                responseStrBuilder.append(inputStr);

            //String result = buff.readLine();
            //JSONArray newJ= new JSONArray();
            //newJ.put(result);
            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

            messages = jsonParser.parseJSONtoMessage(jsonObject);
            //todo call Messanger and push Messages
        }
        catch (JSONException e) {
            throw new RuntimeException(e);
        }
        finally {
            if (conn != null);
            conn.disconnect();
        }
    }

    public void getZones() throws IOException {
        URL obj = null;
        try {
            obj = new URL("http://giv-project6.uni-muenster.de:8080/api/zones");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        HttpURLConnection zoneCon = null;
        try{
            zoneCon = (HttpURLConnection) obj.openConnection();
            zoneCon.setRequestMethod("GET");
            zoneCon.connect();
            zoneCon.setRequestProperty("Key","Value");
            InputStream in = new BufferedInputStream(zoneCon.getInputStream());
            BufferedReader buff= new BufferedReader(new InputStreamReader(in));
            StringBuilder responseStrBuilder = new StringBuilder();

            String inputStr;
            while((inputStr=buff.readLine())!=null)
                responseStrBuilder.append(inputStr);



            JSONObject jsonObject = new JSONObject(responseStrBuilder.toString());

            jsonParser.parseJSONtoZone(jsonObject);
        }

        catch (JSONException e) {
            throw new RuntimeException(e);
        }

        finally {
            if (zoneCon != null);
            zoneCon.disconnect();
        }
    }
}
