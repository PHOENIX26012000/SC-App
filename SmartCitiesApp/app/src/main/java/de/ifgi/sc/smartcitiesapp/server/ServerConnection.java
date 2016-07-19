package de.ifgi.sc.smartcitiesapp.server;

import android.os.AsyncTask;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

/**
 *  Server Connection Class responsible for exchange of messages and zones with the server
 */

public class ServerConnection implements Connection{

    private JSONObject obj = new JSONObject();



    public ServerConnection() {

    }

    @Override
    /**
     *  Method that gets a set of Messages in form of an ArrayList and pushes it to the Server
     */
    public void shareMessage(ArrayList<Message> messages) {
        ArrayList<Message> listmsg = new ArrayList<>();
        for (Message m: messages){
            if(m.getShareWithServer() &&  m.getZone_ID() != "UMPA-UMPA-UMPA-TÖTÖRÖ"){
                listmsg.add(m);
            }
        }
        JSONParser parser = new JSONParser();
        this.obj = parser.parseMessagetoJSON(listmsg);

        new PostMsgTask().execute("http://giv-project6.uni-muenster.de:8080/api/addmessages");
    }


    /**
     *  Method that requests the server for Messages and shares them with the Messenger
     */
    public void getMessages(String zoneID) {

        new GetMsgTask().execute("http://giv-project6.uni-muenster.de:8080/api/messages?zone="+zoneID);

    }

    /**
     *  Method to Get all the zones from the server
     */
    public void getZones() {

        new GetZoneTask().execute("http://giv-project6.uni-muenster.de:8080/api/zones");

    }

    /**
     *  PostMsgTask Class to make an http url connection to the server and POST messages to the server
     *  extends AsyncTask to allow network operations to be done in the background
     */
    public class PostMsgTask extends AsyncTask<String,Integer, String>{

        String responseString= "";
        int response;
        InputStream is = null;

        public String readIt(InputStream stream, int len) throws IOException {
            Reader reader = null;
            reader = new InputStreamReader(stream, "UTF-8");
            char[] buffer = new char[len];
            reader.read(buffer);
            return new String(buffer);
        }

        @Override
        protected String doInBackground(String... urls) {
            try
            {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                // Create JSONObject:
                String jsonString = obj.toString();

                conn.setRequestProperty("Content-length", jsonString.getBytes().length + "");
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);

                OutputStream os = conn.getOutputStream();
                os.write(jsonString.getBytes("UTF-8"));
                os.close();

                conn.connect();

                response = conn.getResponseCode();
                if (response == 201){
                    is = conn.getInputStream();
                } else if (response == 404) {
                    is = conn.getErrorStream();
                }

                // Convert the InputStream into a string
                String contentAsString = readIt(is, 2000);
                responseString = contentAsString;
                conn.disconnect();

                } catch (Exception e) {
                 responseString = "error occured: "+e + "|||  " + responseString;
            }

            finally {
                if (is != null){
                    try { is.close();} catch (Exception e) {}
                }
            }

            return responseString+response;


        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("201")) {
                Log.i("Server","Successful"+result);
            }
            else if (result.contains("404")) {
                Log.i("Server","Failure"+result);
            }
        }

    }

    /**
     *  GetMsgTask Class to make an http url connection to the server and GET messages from the server
     *  extends AsyncTask to allow network operations to be done in the background
     */
    public class GetMsgTask extends AsyncTask<String,Integer, String> {

        JSONParser parser = new JSONParser();
        ArrayList<Message> messages = new ArrayList<Message>();

        String responseString = "";
        int response;
        InputStream is = null;
        private boolean errorOccured = false;

        @Override

        protected String doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                response = conn.getResponseCode();
                is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line);
                }

                String contentAsString = sb.toString();

                responseString = contentAsString;
                conn.disconnect();

            } catch (Exception e) {
                responseString = "error occured: " + e;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                    }
                }
            }
            return responseString;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            Log.i("Server getMessages", "test: "+result.charAt(0)+" "+result.charAt(result.length()-1));
            if(result.charAt(0) == '{' && result.charAt(result.length()-1)== '}'){
                Log.i("Server getMessage","Response: Success");
                try {
                    JSONObject obj = new JSONObject(result);
                    if(obj.getJSONArray("Messages").isNull(0)== false) {
                        messages = parser.parseJSONtoMessage(obj);
                        de.ifgi.sc.smartcitiesapp.messaging.Messenger.getInstance().updateMessengerFromServer(messages);
                    }
                    else{
                        Log.i("Server getMessage", "Response is empty JSONObject: "+result);
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.i("Server getMessage","Response: Failure "+result);
            }

        }

    }

    /**
     *  GetZoneTask Class to make an http url connection to the server and GET Zones from the server
     *  extends AsyncTask to allow network operations to be done in the background
     */
    public class GetZoneTask extends AsyncTask <String, Integer, String> {

        JSONParser parser = new JSONParser();
        ArrayList<Zone> zones = new ArrayList<>();
        String responseString = "";
        int response;
        InputStream is = null;
        private boolean errorOccured = false;

        @Override
        protected String doInBackground(String... urls) {
            try {

                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                response = conn.getResponseCode();
                is = conn.getInputStream();

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line);
                }

                String contentAsString = sb.toString();

                responseString = contentAsString;
                conn.disconnect();

            } catch (Exception e) {
                responseString = "error occured: " + e;
            } finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                    }
                }
            }

            return responseString;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            //test if response can be a JSON object
            if(result.charAt(0) == '{' && result.charAt(result.length()-1)== '}'){
                Log.i("Server getZones","Response: Success");
                try {
                    JSONObject obj = new JSONObject(result);
                    //test if response is empty
                    if(obj.getJSONArray("Zones").isNull(0)){
                        Log.i("Server getZones", "Response is empty JSONObject: "+result);
                    }
                    else{
                        zones = parser.parseJSONtoZone(obj);
                        ZoneManager.getInstance().updateZonesInDatabase(zones);

                        // get all Messages for all Zones
                        for (Zone z: zones){
                            getMessages(z.getZoneID());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            else{
                Log.i("Server getZones","Response: Failure "+result);
            }
        }
    }
}
