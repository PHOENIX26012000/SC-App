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

        shown_messages.add(
                new MessageView(this,"title 1","Lorem ipsum dolor sit amet, consetetur sadipscing elitr, sed diam nonumy eirmod tempor invidunt ut labore et dolore magna aliquyam erat, sed diam voluptua. At vero eos et accusam et justo duo dolores et ea rebum."
                , dateFormatter.format(now)));
        shown_messages.add(
                new MessageView(this,"title 2","Stet clita kasd gubergren, no sea takimata sanctus est Lorem ipsum dolor sit amet. Lorem ipsum dolor sit amet, consetetur sadipscing elitr."
                        , dateFormatter.format(now)));

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
