package de.ifgi.sc.smartcitiesapp.server;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
//import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
//import java.io.OutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
//import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.interfaces.Messenger;
import de.ifgi.sc.smartcitiesapp.messaging.Message;

import de.ifgi.sc.smartcitiesapp.zone.Zone;

import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;


public class ServerConnection implements Connection{

    private JSONObject obj = new JSONObject();



    public ServerConnection() {

    }

    @Override
    /**
     *  gets a set of Messages in form of an ArrayList and pushs it to the Server
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
        Log.i("Server","JSON"+ obj);

        new PostMsgTask().execute("http://giv-project6.uni-muenster.de:8080/api/addmessages");
    }



    /**
     *  requests the server for Messages and shares them with the Messenger
     */
    public void getMessages(String zoneID) {
        new GetMsgTask().execute("http://giv-project6.uni-muenster.de:8080/api/messages?zone="+zoneID);
    }


    public void getZones() {

        new GetZoneTask().execute("http://giv-project6.uni-muenster.de:8080/api/zones");
        Log.i("ServerConnection","called method getZones()");

    }



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
            //DataOutputStream wr= null;
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
                Log.i("Server postMessages","JSONObject"+jsonString);

                conn.setRequestProperty("Content-length", jsonString.getBytes().length + "");
                conn.setDoInput(true);
                conn.setUseCaches(false);
                conn.setAllowUserInteraction(false);

                OutputStream os = conn.getOutputStream();
                os.write(jsonString.getBytes("UTF-8"));
                Log.i("ServerConnection","Writing Message");
                os.close();

                conn.connect();

                response = conn.getResponseCode();
                Log.d("Server Response","The response is:"+response);
                if (response == 201){
                    is = conn.getInputStream();
                } else if (response == 404) {
                    is = conn.getErrorStream();
                }

                // Convert the InputStream into a string
                String contentAsString = readIt(is, 2000);
                responseString = contentAsString;
                Log.i("Check Input Stream",responseString);
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
                Log.i("Successful submission", result);
            }
            else if (result.contains("404")) {
                Log.i("Failure to Write", result);
            }
        }

    }

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

                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[8000];
                reader.read(buffer);
                String contentAsString = new String(buffer);

                responseString = contentAsString;
                Log.i("Server","response"+ responseString);
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
                    Log.i("Server getMessages","JSONobj: "+obj);
                    if(obj.getJSONArray("Messages").isNull(0)== false) {
                        messages = parser.parseJSONtoMessage(obj);
                        Log.i("Server getMessage","Message: "+messages);
                        //todo give messages to messenger
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
                Log.i("ServerConnection","blubb");
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                response = conn.getResponseCode();
                is = conn.getInputStream();

                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[8000];
                reader.read(buffer);
                String contentAsString = new String(buffer);

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
                        Log.i("Server getZones","ZoneID of the first zone"+ zones.get(0).getZoneID());
                        ZoneManager.getInstance().updateZonesInDatabase(zones);
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
