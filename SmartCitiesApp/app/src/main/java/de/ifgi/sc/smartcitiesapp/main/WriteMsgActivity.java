package de.ifgi.sc.smartcitiesapp.main;

import android.Manifest;
import android.app.Application;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.PolyUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

/**
 * Activity that is opened when the user clicked on "Write Message..." in order to specify the message content
 * such as Title, Text, location, expiretime, topic.
 */
public class WriteMsgActivity extends AppCompatActivity {

    private SupportMapFragment mapFragment;
    ;
    private ScrollView mScrollView;
    private final int MY_PERMISSION_ACCESS_COARSE_LOCATION = 1012;
    private GoogleMap mMap;
    private LinearLayout mapcontainer;
    private Marker msgLocMarker = null;
    private boolean markerPlacedPreviously = false;

    private String selected_topic;
    private String msg_title = "";
    private String msg_txt = "";
    private LatLng msg_pos = null;
    private Date msg_exp = null;
    private Date msg_create = null;
    private String msg_topic = "";
    private Boolean msg_locationChecked;
    private Boolean msg_shareToServer;
    private Zone current_selected_zone;
    private EnhancedPolygon current_zone;
    private boolean expTimeCreatedCustomly = false;

    private DatePicker dp;
    private TimePicker tp;
    private int expDate_mins;
    private int expDate_hours;
    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    public final String DEFAULT_ZONE_ID = "UMPA-UMPA-UMPA-TÖTÖRÖÖÖ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_msg);

        // add Back Button on Actionbar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // Change default behaviour of the edittext MessageTitle: On Enter: close EditText:
        final EditText edt_msgTitle = (EditText) findViewById(R.id.edt_msgTitle);
        edt_msgTitle.setFocusableInTouchMode(true);
        edt_msgTitle.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    // if the "enter"-key was pressed, close the shown Keyboard
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                } else {
                    return false;
                }
            }
        });

        // Change default behaviour of the edittext for the MessageText: On Enter: close EditText:
        final EditText edt_msgText = (EditText) findViewById(R.id.edt_msgText);
        edt_msgText.setFocusableInTouchMode(true);
        edt_msgText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    // if the "enter"-key was pressed, close the shown Keyboard
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    return true;
                } else {
                    return false;
                }
            }
        });

        // which topic was selected?
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selected_topic = extras.getString("TOPIC");
            setTitle(selected_topic);
        }

        // Add categories to the spinner:
        Spinner spn_category = (Spinner) findViewById(R.id.spn_category);

        // Get the current selected zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e) {
            e.printStackTrace();
        }

        // if we're in the default zone, disable 'shareToServerOption':
        CheckBox chb_shareToServer = (CheckBox) findViewById(R.id.chb_allowServerSharing);
        if (current_selected_zone.getZoneID().equals(this.DEFAULT_ZONE_ID)){
            // disable shareToServerOption:
            chb_shareToServer.setChecked(false);
            chb_shareToServer.setEnabled(false);
        } else {
            // enable shareToServerOption:
            chb_shareToServer.setChecked(true);
            chb_shareToServer.setEnabled(true);
        }

        // get all topics within that zone:
        String[] allTopics = current_selected_zone.getTopics();

        ArrayList<String> subscribed_topics = new ArrayList<String>();
        for (String s : allTopics) {
            if (PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean("pref_" + current_selected_zone.getZoneID() + "_" + s, true)) {
                subscribed_topics.add(s);
            }
        }

        String[] topics = new String[subscribed_topics.size()];
        for (int i = 0; i < subscribed_topics.size(); i++) {
            topics[i] = subscribed_topics.get(i);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, topics);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_category.setAdapter(adapter);
        // check out position of selected_topic in values:
        int index = 0;
        for (int i = 0; i < topics.length; i++) {
            if (selected_topic.equals(topics[i])) {
                index = i;
                break;
            }
        }
        // set previously selected topic as default:
        spn_category.setSelection(index);

        // select expire date:
        Spinner spn_selectExpireTime = (Spinner) findViewById(R.id.spn_expireTime);
        final String[] expireDefaults = new String[]{"1 week", "5 days", "3 days", "2 days", "24 hours", "18 hours", "12 hours", "6 hours", "3 hours", "custom..."};
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter2 = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, expireDefaults);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_selectExpireTime.setAdapter(adapter2);
        spn_selectExpireTime.setSelection(0);
        spn_selectExpireTime.setOnItemSelectedListener(new Spinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // if "custom..." is selected, open a time chooser dialog
                if (position == 9) {
                    expTimeCreatedCustomly = true;
                    Log.d("WriteMsgActivity", "onCustomTimeSelected");
                    final Dialog dialog = new Dialog(WriteMsgActivity.this);
                    dialog.setContentView(R.layout.custom_dialog);
                    dialog.setTitle("Pick the expiring date");

                    dp = (DatePicker) dialog.findViewById(R.id.datePicker1);
                    dp.setMinDate(new Date().getTime());                // minimum day is in today.
                    dp.setMaxDate(new Date().getTime() + 1000 * 3600 * 24 * 7); // maximum day is 1 week in future.

                    tp = (TimePicker) dialog.findViewById(R.id.timePicker1);

                    tp.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                        @Override
                        public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                            expDate_hours = hourOfDay;
                            expDate_mins = minute;
                            Log.d("OnTimeChanged","hours: "+hourOfDay+ ", mins: " + minute);
                        }
                    });

                    Button btn_setExpireDate = (Button) dialog.findViewById(R.id.btn_selectExpireTime);
                    btn_setExpireDate.setOnClickListener(new View.OnClickListener() {

                        /**
                         * @return a java.util.Date
                         */
                        private Date getDateFromPickers() {
                            // create the date:
                            int day = dp.getDayOfMonth();
                            int month = dp.getMonth();
                            int year = dp.getYear();

                            Calendar calendar = Calendar.getInstance();
                            calendar.set(year, month, day);
                            calendar.set(Calendar.HOUR_OF_DAY, expDate_hours);
                            calendar.set(Calendar.MINUTE, expDate_mins);

                            Log.i("WriteMsgActivity", "Calendar Time: " + calendar.getTime());

                            // add the hours and minutes:
                            Date expDate = calendar.getTime();
                            return expDate;
                        }

                        @Override
                        public void onClick(View v) {
                            try {
                                msg_exp = getDateFromPickers();
                            } catch (NullPointerException npe) {
                                Log.e("WriteMsgActivity", " confirm custom expiring time selected without selected date and time before: " + npe.getMessage());
                            }
                            Log.d("WriteMsgActivity", "Expire Date selected: " + msg_exp);
                            Date now = new Date(new Date().getTime() - 1000 * 3600 * 24);
                            if (new Date().after(msg_exp)) {
                                Log.i("WriteMsgActivity", "Selected Date is in the past! User must select another Date");
                                Toast.makeText(getApplicationContext(), "The selected expiring Date is in past.", Toast.LENGTH_LONG).show();
                            } else {
                                Log.i("WriteMsgActivity", "Selected Date is fine! now: " + new Date() + " < " + msg_exp);
                                TextView txt_expiresAt = (TextView) findViewById(R.id.txt_expiresAt);
                                txt_expiresAt.setText("Expires at: " + msg_exp);
                                dialog.cancel();
                            }
                        }
                    });

                    dialog.show();
                } else {
                    expTimeCreatedCustomly = false;
                    // calculate expiring Date based upon current Date:
                    msg_create = new Date(); // now
                    long theFuture = 0;
                    switch (position) {
                        case 0: // + 7 days
                            theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
                            break;
                        case 1: // + 5 days
                            theFuture = System.currentTimeMillis() + (86400 * 5 * 1000);
                            break;
                        case 2:
                            theFuture = System.currentTimeMillis() + (86400 * 3 * 1000);
                            break;
                        case 3:
                            theFuture = System.currentTimeMillis() + (86400 * 2 * 1000);
                            break;
                        case 4:
                            theFuture = System.currentTimeMillis() + (86400 * 1 * 1000);
                            break;
                        case 5:
                            theFuture = System.currentTimeMillis() + (18 * 3600 * 1000);
                            break;
                        case 6:
                            theFuture = System.currentTimeMillis() + (12 * 3600 * 1000);
                            break;
                        case 7:
                            theFuture = System.currentTimeMillis() + (6 * 3600 * 1000);
                            break;
                        case 8:
                            theFuture = System.currentTimeMillis() + (3 * 3600 * 1000);
                            break;
                        default:
                            break;
                    }
                    msg_exp = new Date(theFuture);
                    TextView txt_expiresAt = (TextView) findViewById(R.id.txt_expiresAt);
                    txt_expiresAt.setText("Expires at: " + msg_exp);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // do nothing.
            }
        });

        // show/hide map according to checkbox "add a location?" - selection:
        final CheckBox chb_addlocation = (CheckBox) findViewById(R.id.chb_addlocation);

        mapcontainer = (LinearLayout)

                findViewById(R.id.ll_mapcontainer);

        mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);
        mScrollView = (ScrollView) findViewById(R.id.sv_container);
        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).setListener(new WorkaroundMapFragment.OnTouchListener() {
            @Override
            public void onTouch() {
                Log.d("MAP MOVING", "blob");
                mScrollView.requestDisallowInterceptTouchEvent(true);
            }
        });
        chb_addlocation.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener()

                                                   {

                                                       @Override
                                                       public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                                                           if (isChecked) {

                                                               // checkbox checked -> show map:
                                                               mapcontainer.setVisibility(View.VISIBLE);
                                                               mapFragment.getMapAsync(new OnMapReadyCallback() {
                                                                   @Override
                                                                   public void onMapReady(GoogleMap googleMap) {
                                                                       mMap = googleMap;
                                                                       mMap.getUiSettings().setMapToolbarEnabled(false);
                                                                       mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                                                           @Override
                                                                           public boolean onMarkerClick(Marker marker) {
                                                                               return true;
                                                                           }
                                                                       });
                                                                       try {
                                                                           mMap.setMyLocationEnabled(true);

                                                                           // enable location service on phone if its not enabled already:
                                                                           LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                                                                           boolean gps_enabled = false;
                                                                           boolean network_enabled = false;

                                                                           try {
                                                                               gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                                                                           } catch (Exception ex) {
                                                                           }

                                                                           try {
                                                                               network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                                                                           } catch (Exception ex) {
                                                                           }

                                                                           if (!gps_enabled && !network_enabled) {
                                                                               Toast.makeText(getApplicationContext(), "Please enable location service", Toast.LENGTH_LONG).show();
                                                                               // activate Location Service
                                                                               Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                                                                               WriteMsgActivity.this.startActivity(myIntent);
                                                                           }
                                                                       } catch (SecurityException e) {

                                                                           // request location permission to the user:
                                                                           ActivityCompat.requestPermissions(WriteMsgActivity.this,
                                                                                   new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                                                                                   MY_PERMISSION_ACCESS_COARSE_LOCATION);

                                                                       } finally {

                                                                       }
                                                                       mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

                                                                           @Override
                                                                           public void onMapClick(LatLng latLng) {
                                                                               // check, if the click was inside the polygon:
                                                                               if (PolyUtil.containsLocation(latLng, current_selected_zone.getPolygon(), true)) {
                                                                                   if (markerPlacedPreviously) {
                                                                                       msgLocMarker.remove();
                                                                                   }
                                                                                   msgLocMarker = mMap.addMarker(new MarkerOptions().position(latLng));
                                                                                   markerPlacedPreviously = true;
                                                                               } else {
                                                                                   // if click was outside mapregion: do nothing
                                                                               }
                                                                           }
                                                                       });

                                                                       // create a map polygon for the current selected Zone:
                                                                       int[] rgb = {100, 255, 100};
                                                                       current_zone = new EnhancedPolygon(
                                                                               current_selected_zone.getPolygon(),
                                                                               rgb,
                                                                               current_selected_zone.getName());

                                                                       // add zone's polygon onto the map + safe its polygon reference:
                                                                       current_zone.setPolygon(mMap.addPolygon(current_zone.getPolygon()));

                                                                       // zoom into the polygon of the current selected zone:
                                                                       LatLngBounds.Builder builder = new LatLngBounds.Builder();
                                                                       for (LatLng point : current_zone.getPoints()) {
                                                                           builder.include(point);
                                                                       }
                                                                       LatLngBounds bounds = builder.build();
                                                                       int padding = 0;
                                                                       CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, padding);
                                                                       mMap.animateCamera(cu);
                                                                   }
                                                               });
                                                           } else {
                                                               // checkbox unchecked -> hide map:
                                                               mapcontainer.setVisibility(View.GONE);
                                                           }
                                                       }
                                                   }

        );
        Button btn_submit = (Button) findViewById(R.id.btn_submitMsg);
        btn_submit.setOnClickListener(new View.OnClickListener()

                                      {
                                          @Override
                                          public void onClick(View v) {
                                              if (isFormFilled()) {
                                                  Message newMessage = null;

                                                  String zoneID = current_selected_zone.getZoneID();
                                                  String msg_id = UUID.randomUUID().toString();

                                                  if (msg_locationChecked) {
                                                      newMessage = new Message(msg_id, zoneID, msg_create, msg_pos.latitude, msg_pos.longitude, msg_exp, msg_topic, msg_title, msg_txt, msg_shareToServer);
                                                  } else {
                                                      newMessage = new Message(msg_id, zoneID, msg_create, msg_exp, msg_topic, msg_title, msg_txt, msg_shareToServer);
                                                  }

                                                  // create new UUID
                                                  ArrayList<Message> msgs = new ArrayList<Message>();
                                                  msgs.add(newMessage);
                                                  Messenger.getInstance().updateMessengerFromUI(msgs);
                                                  finish();
                                              } else {
                                                  // no title set:
                                                  if (msg_title.length() < 1) {
                                                      Toast.makeText(getApplicationContext(), "You must set a title!", Toast.LENGTH_LONG).show();
                                                  }
                                                  // no title set:
                                                  if (msg_txt.length() < 1) {
                                                      Toast.makeText(getApplicationContext(), "You must set a message text!", Toast.LENGTH_LONG).show();
                                                  }
                                                  // no location marked, but checked:
                                                  if ((msg_locationChecked) && (msg_pos == null)) {
                                                      Toast.makeText(getApplicationContext(), "You must mark a position if u select adding a location!", Toast.LENGTH_LONG).show();
                                                  }
                                              }
                                          }
                                      }

        );
    }

    private boolean isFormFilled() {
        boolean allFilled = true;
        EditText edt_msgTitle = (EditText) findViewById(R.id.edt_msgTitle);
        EditText edt_msgText = (EditText) findViewById(R.id.edt_msgText);
        Spinner spn_selectTopic = (Spinner) findViewById(R.id.spn_category);
        Spinner spn_selectExpire = (Spinner) findViewById(R.id.spn_expireTime);
        CheckBox chb_shareToServer = (CheckBox) findViewById(R.id.chb_allowServerSharing);
        CheckBox chb_addLocation = (CheckBox) findViewById(R.id.chb_addlocation);

        // get title, and check if it's set
        msg_title = edt_msgTitle.getText().toString();
        if (msg_title.length() < 1)
            allFilled = false;

        // get text, and check if it's set
        msg_txt = edt_msgText.getText().toString();
        if (msg_txt.length() < 1)
            allFilled = false;

        // calculate expiring Date based upon current Date:
        expTimeCreatedCustomly = false;
        msg_create = new Date(); // now
        long theFuture = 0;
        switch (spn_selectExpire.getSelectedItemPosition()) {
            case 0: // + 7 days
                theFuture = System.currentTimeMillis() + (86400 * 7 * 1000);
                break;
            case 1: // + 5 days
                theFuture = System.currentTimeMillis() + (86400 * 5 * 1000);
                break;
            case 2:
                theFuture = System.currentTimeMillis() + (86400 * 3 * 1000);
                break;
            case 3:
                theFuture = System.currentTimeMillis() + (86400 * 2 * 1000);
                break;
            case 4:
                theFuture = System.currentTimeMillis() + (86400 * 1 * 1000);
                break;
            case 5:
                theFuture = System.currentTimeMillis() + (18 * 3600 * 1000);
                break;
            case 6:
                theFuture = System.currentTimeMillis() + (12 * 3600 * 1000);
                break;
            case 7:
                theFuture = System.currentTimeMillis() + (6 * 3600 * 1000);
                break;
            case 8:
                theFuture = System.currentTimeMillis() + (3 * 3600 * 1000);
                break;
            case 9:
                expTimeCreatedCustomly = true;
                break;
        }
        if (expTimeCreatedCustomly){
            // msg_exp is set alrdy.
        } else
            msg_exp = new Date(theFuture);

        // Add a location?
        if (chb_addLocation.isChecked()) {
            msg_locationChecked = true;
            // get marked position:
            if (msgLocMarker == null) {
                // no position marked
                msg_pos = null;
                allFilled = false;
            } else {
                // position is marked
                msg_pos = msgLocMarker.getPosition();
                allFilled = true;
            }
        } else {
            msg_locationChecked = false;
        }

        // Share to server?
        if (chb_shareToServer.isChecked()) {
            msg_shareToServer = true;
        } else {
            msg_shareToServer = false;
        }

        // get selected topic:
        msg_topic = spn_selectTopic.getSelectedItem().toString();

        return allFilled;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_ACCESS_COARSE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Log.d("MapTap", "Permission granted, hooray");
                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    try {
                        mMap.setMyLocationEnabled(true);
                        // enable Location service on phone if its not enabled already:
                        LocationManager lm = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        boolean gps_enabled = false;
                        boolean network_enabled = false;

                        try {
                            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
                        } catch (Exception ex) {
                        }

                        try {
                            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
                        } catch (Exception ex) {
                        }

                        if (!gps_enabled && !network_enabled) {
                            // activate Location Service
                            Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                            WriteMsgActivity.this.startActivity(myIntent);
                        }
                    } catch (SecurityException e) {
                        Log.d("MapTap", "2nd attempt also failed on security:" + e);
                    }

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(getApplicationContext(), "Grant location permission for HappyShare in your phone settings for a location-button.", Toast.LENGTH_LONG).show();
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
