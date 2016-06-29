package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class TopicTabFragment extends ListFragment {

    private View v;
    private Button btn_writeMsg;
    private Zone current_selected_zone;
    private ArrayList<Message> current_msgs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Get the current selected zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e){
            e.printStackTrace();
        }
        // get all topics within that zone:
        String[] topics = current_selected_zone.getTopics();
        Boolean[] done_topics = new Boolean[topics.length+1];

        // get 1st msg from each topics:
        String[] msgs = new String[topics.length+1];
        current_msgs = Messenger.getInstance().getAllMessages();
        // for each topic i in the current selected zone:
        for (int i=0;i<topics.length;i++){
            done_topics[i] = false;
            // for each message m from the Messenger:
            for (Message m : current_msgs){
                // if m is inside current selected zone
                if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                    // if m has topic i and topic i undone
                    if ((m.getTopic().equals(topics[i])) && (!done_topics[i])) {
                        // msgs[i] = m.msgtext;
                        msgs[i] = m.getMsg();
                        // topic i done.
                        done_topics[i] = true;
                    }
            }
        }
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), topics, msgs);
        setListAdapter(adapter);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (v != null) {
            ViewGroup parent = (ViewGroup) v.getParent();
            if (parent != null)
                parent.removeView(v);
        }
        try {
            v = inflater.inflate(R.layout.fragment_tab, container, false);
            btn_writeMsg = (Button) v.findViewById(R.id.btn_writeMsg);
        } catch (InflateException e) {
        /* btn_writeMsg is already there, just return view as it is */
        }

        btn_writeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: open writing a msg Activity for result
                Intent intent = new Intent(getActivity(), WriteMsgActivity.class);
                intent.putExtra("TOPIC", "Traffic");
                startActivity(intent);
            }
        });

        return v;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        String item = (String) getListAdapter().getItem(position);
        Toast.makeText(getActivity(), item + " selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getActivity(), MsgActivity.class);
        intent.putExtra("TOPIC", item);
        startActivity(intent);
    }

}