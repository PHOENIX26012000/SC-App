package de.ifgi.sc.smartcitiesapp.server;

import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

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

    @Override
    /**
     *  gets a set of Messages in form of an ArrayList and pushs it to the Server
     */
    public void shareMessage(ArrayList<Message> messages) {
        JSONParser parser = new JSONParser();
        this.obj = parser.parseMessagetoJSON(messages);
        Log.i("JSONCHECK",obj+"");

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
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                // Create JSONObject:
                String jsonString = obj.toString();
                Log.i("JSON Object",jsonString+"");

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
                Log.i("ServerConnection","Successful sending");
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
            // maybe add other codes as well such as 201, or all between >=200 <400
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

                BufferedReader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                StringBuilder sb = new StringBuilder();

                String line = null;
                while ((line = reader.readLine()) != null){
                    sb.append(line);
                }

                String contentAsString = sb.toString();

                responseString = contentAsString;
                Log.i("Server","blubb1");
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
            Log.i("Server","Blubb2");
            if(result.charAt(0)== '{'){
                try {
                    JSONObject obj = new JSONObject(result);
                    messages = parser.parseJSONtoMessage(obj);
                    Log.i("Server","GetMessages, display Message: "+ messages);
                    //todo give messages to messenger

                } catch (JSONException e) {
                    e.printStackTrace();
                }
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
            Log.i("ServerResponse", result);
            if(result.charAt(0) == '{'){
                //todo if abfrage für wenn leer

                try {
                    JSONObject obj = new JSONObject(result);
                    zones = parser.parseJSONtoZone(obj);
                    ZoneManager.getInstance().updateZonesInDatabase(zones);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }


        }

    }
}
