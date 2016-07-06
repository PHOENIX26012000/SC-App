package de.ifgi.sc.smartcitiesapp.main;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.interfaces.MessagesObtainedListener;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class MsgActivity extends AppCompatActivity {

    ArrayList<MessageView> shown_messages = new ArrayList<MessageView>();
    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    private String selected_topic;
    private Zone current_selected_zone;
    private ArrayList<Message> msgs;
    private ArrayList<Message> msgs_in_current_zone;

    @Override
    protected void onResume() {
        super.onResume();
        Log.d("MsgActivity","test #-2 reached");
        // fill message activity with messages of selected topic type
        LinearLayout ll_messages = (LinearLayout) findViewById(R.id.ll_messages);
        ll_messages.removeAllViews();
        shown_messages = new ArrayList<MessageView>();

        Log.d("MsgActivity","test #-1 reached");
        // Get the current selected zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e){
            // TODO: if no zone is currently selected
            e.printStackTrace();
        }

        Log.d("MsgActivity","test #0 reached");
        // get all msgs for this current selected zone:
        msgs = Messenger.getInstance().getAllMessages();
        msgs_in_current_zone = new ArrayList<Message>();
        Log.d("MsgActivity","test #1 reached");
        ArrayList<Message> newObtained = UIMessageManager.getInstance().getNew_obtained_msgs();
        Log.d("MsgActivity","test #2 reached");

        ArrayList<String> newObtainedIDs = new ArrayList<String>();
        Log.d("MsgActivity","test #3 reached");
        if (newObtained!=null) {
            Log.e("MsgActivity","NullException? " + newObtained.size());
            for (Message m : newObtained)
                newObtainedIDs.add(m.getMessage_ID());
        }
        Log.d("MsgActivity","test #4 reached");
        ArrayList<String> newObtainedIDsInThisTopics = new ArrayList<String>();

        // for each message m from the Messenger:
        for (Message m : msgs){
            // if m is inside current selected zone
            if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                msgs_in_current_zone.add(m);
        }
        Log.d("HS_msgs",msgs_in_current_zone.size()+"");

        // add messages into UI:
        for (Message msg: msgs_in_current_zone){
            if (msg.getTopic().equals(selected_topic)) {
                // create MessageView from msg for the layout:
                Date exp_date = new Date();
                try {
                    exp_date = D_format.parse(msg.getExpired_At());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                long timeDiffInMillis = exp_date.getTime() - new Date().getTime();
                int days = (int) timeDiffInMillis / (1000 * 60 * 60 * 24);
                long restDiff = timeDiffInMillis - days * (1000 * 60 * 60 * 24);
                int hours = (int) restDiff / (1000 * 60 * 60);
                restDiff = restDiff - hours * (1000 * 60 * 60);
                int mins = (int) restDiff / (1000 * 60);
                String expiresIn = days + "d " + hours + "h " + mins + "m";
                MessageView mv;
                if (newObtainedIDs.contains(msg.getMessage_ID())) {
                    // highlight, if its new:
                    Log.e("HIGH","LIGHTING message: "+msg.getTitle());
                    mv = new MessageView(this, msg.getTitle(), msg.getMsg(), expiresIn, true);
                    newObtainedIDsInThisTopics.add(msg.getMessage_ID());
                } else {
                    // don't hightlight, otherwise:
                    mv = new MessageView(this, msg.getTitle(), msg.getMsg(), expiresIn, false);
                }
                shown_messages.add(mv);
            }
        }

        // add all messages onto the scrollview:
        for (MessageView mv : shown_messages) {
            ll_messages.addView(mv);
        }

        // scroll the ScrollView down to the bottom, so that the latest message is in focus:
        final ScrollView scrollview = ((ScrollView) findViewById(R.id.slv_messages));
        scrollview.post(new Runnable() {
            @Override
            public void run() {
                scrollview.fullScroll(ScrollView.FOCUS_DOWN);
            }
        });

        // finally: mark all new obtained msgs shown here as 'not new anymore':
        if (newObtained != null)
            UIMessageManager.getInstance().markMessagesAsOld(newObtainedIDsInThisTopics);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);

        // add Back Button on Actionbar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // which topic was selected?
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selected_topic = extras.getString("TOPIC");
            setTitle(selected_topic);
        }

        Button btn_writeMsg = (Button) findViewById(R.id.btn_writeMsg);
        btn_writeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open writing a msg Activity for result
                Intent intent = new Intent(getApplicationContext(), WriteMsgActivity.class);
                intent.putExtra("TOPIC", selected_topic);
                startActivity(intent);
            }
        });

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
