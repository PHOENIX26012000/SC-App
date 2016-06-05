package de.ifgi.sc.smartcitiesapp.main;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;

public class MsgActivity extends AppCompatActivity {

    ArrayList<MessageView> shown_messages = new ArrayList<MessageView>();
    private String selected_topic;

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

        // fill message activity with messages of selected topic type
        LinearLayout ll_messages = (LinearLayout) findViewById(R.id.ll_messages);
        shown_messages = new ArrayList<MessageView>();

        // add some random messages:
        Date now = new Date();
        SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        now.setTime(now.getTime()+1000*60*60*2);

        ArrayList<String[]> msgs = new ArrayList<String[]>(); // TODO: Replace String[] with Message
        msgs.add(new String[] {"title 1",
                "Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum."
                , dateFormatter.format(now)}
        );

        now.setTime(now.getTime()+1000*60*30);
        msgs.add(new String[] {"title 2",
                "Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr.",
                dateFormatter.format(now)}
        );
        // add message from UIMEssageManager:
        ArrayList<Message> msgs2 = UIMessageManager.getInstance().getActiveMessages();

        String[] s2 = new String[3];
        s2[0] = msgs2.get(0).getTitle();
        s2[1] = "bla";
        s2[2] = "bla";
        msgs.add(s2);

        // add each message to the user interface
        for (String[] s : msgs){
            // compute timedifference between expire date and right now:
            Date currentTime = new Date();
            long difference = ((now.getTime() - currentTime.getTime())/(60*1000));

            int minutes = (int) (difference) % 60;
            int hours = (int) (difference/60) % 24;
            int days = (int) (difference/(60*24)) % 7;
            String expireTime = days > 0? days + "days "+hours+"hours "+minutes+"min"
                    : hours > 0 ? hours + "hours " + minutes +"min"
                        : minutes+"min";

            MessageView mv = new MessageView(this, s[0], s[1], expireTime);
            shown_messages.add(mv);
        }

        // add all messages onto the scrollview:
        for (MessageView mv : shown_messages) {
            ll_messages.addView(mv);
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
