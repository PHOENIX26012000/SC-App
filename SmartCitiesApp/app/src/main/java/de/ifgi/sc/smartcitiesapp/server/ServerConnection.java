package de.ifgi.sc.smartcitiesapp.server;

//import org.json.JSONArray;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.util.Log;

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
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
//import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;

import java.util.logging.Level;
import java.util.logging.Logger;

import de.ifgi.sc.smartcitiesapp.interfaces.Connection;
import de.ifgi.sc.smartcitiesapp.messaging.Message;

//abstract class RetrieveTasks extends AsyncTask <ArrayList,Void, Void>
//{
//
//}

public class ServerConnection implements Connection{

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

    public void shareMessage(ArrayList<Message> messages) {
        /*
        this.messages = messages;
         */
       // jsonObject = jsonParser.parseMessagetoJSON(messages);
        ///JSONArray share = new JSONArray();
        //share.put(jsonObject);
        new PostMsgTask().execute("http://giv-project6.uni-muenster.de:8080/api/addmessages");

    }
       /* URL url = null;
        HttpURLConnection client = null;
        try {
            url = new URL("http://giv-project6.uni-muenster.de:8080/api/addmessages");
            client = (HttpURLConnection) url.openConnection();

            client.setRequestMethod("POST");
            client.connect();
            Log.d("MSG", "Connection Successful");
            client.setRequestProperty("Key","Value");
            client.setDoOutput(true);
            client.setChunkedStreamingMode(0);
            OutputStreamWriter out = new OutputStreamWriter(client.getOutputStream());
            //writeStream(out);
            out.write(jsonObject.toString());
            out.flush();
            out.close();

            Log.d("MSG","Message Sent");

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



    */


    /**
     *  requests the server for Messages and shares them with the Messanger
     */
    public void getMessages() {
        new GetMsgTask().execute("http://giv-project6.uni-muenster.de:8080/api/messages/");
        //todo request to server for Messages as JSONArray
    }

        /*try{
        URL obj = new URL("http://giv-project6.uni-muenster.de:8080/api/messages/");
        HttpURLConnection conn = null;

            conn = (HttpURLConnection) obj.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            Log.d("MSG","Connected to Server");
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
            Log.d("MSG","Successful");
            {
                if (conn != null);
                conn.disconnect();
            }
        }


        catch (JSONException e) {
            throw new RuntimeException(e);
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } */

    public void getZones() {

        new GetZoneTask().execute("http://giv-project6.uni-muenster.de:8080/api/zones");

    }
     /*   URL obj = null;
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

        catch (IOException e){
            e.printStackTrace();
        }
        finally {
            if (zoneCon != null);
            zoneCon.disconnect();
        }
    }
*/
    /**
     * Created by Shahzeib on 7/5/2016.
     */
    public class PostMsgTask extends AsyncTask<String,Integer, String>{
        JSONParser jsonParser = new JSONParser();
        ArrayList<Message> messages = new ArrayList<Message>();
        JSONObject jsonObject = new JSONObject();
        String responseString= "";
        int response;
        InputStream is = null;

        @Override
        protected String doInBackground(String... urls) {
            //DataOutputStream wr= null;
            try
            {
                URL url = new URL("http://giv-project6.uni-muenster.de:8080/api/addmessages");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000 /* milliseconds */);
                conn.setConnectTimeout(15000 /* milliseconds */);
                conn.setRequestMethod("POST");
                conn.setDoOutput(true);
                conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");

                this.messages= messages;
                jsonObject=jsonParser.parseMessagetoJSON(messages);

                OutputStreamWriter out = new OutputStreamWriter(conn.getOutputStream());

                out.write(jsonObject.toString());
                out.close();

                conn.connect();
                int response = conn.getResponseCode();
                if (response >= 200 && response <=399){
                    is = conn.getInputStream();
                } else {
                    is = conn.getErrorStream();
                }

                // Convert the InputStream into a string
                //String contentAsString = readIt(is, 200);
                //responseString = contentAsString;
                conn.disconnect();

                //} catch (Exception e) {
                // responseString = "error occured: "+e + "|||  " + responseString;
            }

            catch (MalformedURLException e) {
                e.printStackTrace();
            }   catch (ProtocolException e) {
                e.printStackTrace();
            }   catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (is != null){
                    try { is.close();} catch (Exception e) {}
                }
            }

            return response+responseString;

        }

        //protected void onPostExecute(String result){
        // data submitted successfully?
        //if (result.contains("200")) {
        // maybe add other codes as well such as 201, or all between >=200 <400

    }

    public class GetMsgTask extends AsyncTask<String,Integer, String> {
        JSONParser jsonParser = new JSONParser();
        ArrayList<Message> messages = new ArrayList<Message>();
        //JSONObject jsonObject = new JSONObject();

        String responseString = "";
        int response;
        InputStream is = null;

        @Override


        protected String doInBackground(String... urls) {
            try {
                URL url = new URL("http://giv-project6.uni-muenster.de:8080/api/messages/");
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                response = conn.getResponseCode();
                is = conn.getInputStream();

                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[100];
                reader.read(buffer);
                String contentAsString = new String(buffer);

                responseString = contentAsString;

                JSONObject jsonObject = new JSONObject(responseString);

                messages = jsonParser.parseJSONtoMessage(jsonObject);

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
            return response + responseString;
        }

        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("200")) {
                Log.d("Connection:","Connected");
            }

        }
    }

    public class GetZoneTask extends AsyncTask <String, Integer, String> {

        JSONParser jsonParser = new JSONParser();
        String responseString="";
        int response;
        InputStream is= null;

        @Override
        protected String doInBackground(String... url) {
            try{
                URL urls = new URL("http://giv-project6.uni-muenster.de:8080/api/zones");
                HttpURLConnection conn = (HttpURLConnection) urls.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();

                response = conn.getResponseCode();
                is = conn.getInputStream();

                Reader reader = new InputStreamReader(is, "UTF-8");
                char[] buffer = new char[100];
                reader.read(buffer);
                String contentAsString = new String(buffer);

                JSONObject jsonObject = new JSONObject(contentAsString);

                jsonParser.parseJSONtoZone(jsonObject);

                conn.disconnect();

            }

            catch (Exception e) {
                responseString = "error occured: " + e;
            }
            finally {
                if (is != null) {
                    try {
                        is.close();
                    } catch (Exception e) {
                    }
                }
            }

            return response+responseString;
        }

       /* protected void onPostExecute(String result) {
        super.onPostExecute(result);
        if (result.contains("200")) {
            Log.d("TAG", "onPostExecute: Success ");
        } */
    }

}
