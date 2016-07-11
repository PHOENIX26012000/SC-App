package mojioms.smartcitiesserverrequesttests;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new GetZonesTask().execute("http://giv-project6.uni-muenster.de:8080/api/zones");
        String zone_id = "1";  // <-- change to zone-id, which u want to get all msgs of form the server
        // new GetMsgFromZoneTask().execute("http://giv-project6.uni-muenster.de:8080/api/messages?zone="+zone_id);
    }

    public class GetMsgFromZoneTask extends AsyncTask<String, Integer, String> {
        String responseString = "";
        int response;
        InputStream is = null;
        private boolean errorOccured = false;

        @Override
        protected String doInBackground(String... urls) {
            try {
                // connect to server url:
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout(10000);
                conn.setConnectTimeout(15000);
                conn.setRequestMethod("GET");
                conn.setDoInput(true);
                conn.connect();
                response = conn.getResponseCode();
                is = conn.getInputStream();

                // Convert the InputStream into a string:
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
            return response + responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("201")) {
                // on response, do show leaderboard:
                Log.d("SERVERRESPONSE", result);
                ArrayList<String> zones = new ArrayList();
                try {
                    Log.d("blub","#1");
                    JSONArray jsonarray = new JSONArray(result.substring(result.indexOf("["), result.lastIndexOf("]") + 1));
                    Log.d("blub","#2");

                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        Log.d("blub","#3");
                        JSONObject row = jsonarray.getJSONObject(i);
                        Log.d("blub","#4");
                        // parse zonename example:
                        String zonename = row.getString("Name");

                        // parse Geometry example:
                        JSONObject zonegeometry = row.getJSONObject("Geometry");

                        // parse zoneid example:
                        String zoneid = row.getString("Zone-id");

                        // parse topics example:
                        JSONArray topicarray = row.getJSONArray("Topics");

                        // parse expire date example:
                        String expDate = row.getString("Expired-at");

                        Log.d("blub","#5");

                        Log.d("Response", "Zone: "+ zonename + ";" + zonegeometry + ";" + zoneid +";"+ topicarray.toString() +";" +expDate);
                    }
                } catch (Exception e){
                    Log.e("ServerResponse", "Some error: "+ e);
                }

            } else {
                Log.e("SERVERRESPONSE", "with error: " + result);
            }
        }
    }

    public class GetZonesTask extends AsyncTask<String, Integer, String> {
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

                // Convert the InputStream into a string:
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
            return response + responseString;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if (result.contains("201")) {
                // on response, do show leaderboard:
                Log.d("SERVERRESPONSE", result);
                ArrayList<String> zones = new ArrayList();
                try {
                    Log.d("blub","#1");
                    JSONArray jsonarray = new JSONArray(result.substring(result.indexOf("["), result.lastIndexOf("]") + 1));
                    Log.d("blub","#2");

                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        Log.d("blub","#3");
                        JSONObject row = jsonarray.getJSONObject(i);
                        Log.d("blub","#4");
                        // parse zonename example:
                        String zonename = row.getString("Name");

                        // parse Geometry example:
                        JSONObject zonegeometry = row.getJSONObject("Geometry");

                        // parse zoneid example:
                        String zoneid = row.getString("Zone-id");

                        // parse topics example:
                        JSONArray topicarray = row.getJSONArray("Topics");

                        // parse expire date example:
                        String expDate = row.getString("Expired-at");

                        Log.d("blub","#5");

                        Log.d("Response", "Zone: "+ zonename + ";" + zonegeometry + ";" + zoneid +";"+ topicarray.toString() +";" +expDate);
                    }
                } catch (Exception e){
                    Log.e("ServerResponse", "Some error: "+ e);
                }

            } else {
                Log.e("SERVERRESPONSE", "with error: " + result);
            }
        }

    }

}
