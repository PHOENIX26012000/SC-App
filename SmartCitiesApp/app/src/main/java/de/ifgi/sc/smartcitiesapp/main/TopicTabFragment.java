package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

public class TopicTabFragment extends Fragment {

    private View v;
    private Button btn_writeMsg;
    private Zone current_selected_zone;
    private ArrayList<Message> msgs;
    private ArrayList<Message> msgs_in_current_zone;
    private LinearLayout ll_topics;
    private ScrollView slv_topics;
    private ArrayList<TopicView> shown_topics;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // create/update all views:
        createAllViews();
    }

    public void createAllViews(){
        // Get the current selected zone:
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e) {
            e.printStackTrace();
            Log.d("AppZone", "No Zone selected error!");
        }

        // get all msgs for this current selected zone:
        msgs = Messenger.getInstance().getAllMessages();
        msgs_in_current_zone = new ArrayList<Message>();
        ArrayList<Message> newObtained = UIMessageManager.getInstance().getNew_obtained_msgs();

        ArrayList<String> newObtainedIDs = new ArrayList<String>();
        if (newObtained != null) {
            for (Message m : newObtained)
                newObtainedIDs.add(m.getMessage_ID());
        }
        ArrayList<String> newObtainedIDsInThisTopics = new ArrayList<String>();

        // for each message m from the Messenger:
        for (Message m : msgs) {
            // if m is inside current selected zone
            if (m.getZone_ID().equals(current_selected_zone.getZoneID()))
                msgs_in_current_zone.add(m);
        }
        Log.d("HS_msgs", msgs_in_current_zone.size() + "");

        // get all topics within that zone:
        String[] topics = current_selected_zone.getTopics();
        Boolean[] topic_hasNewEntry = new Boolean[topics.length + 1];
        Boolean[] topic_hasAMsg = new Boolean[topics.length + 1];
        String[] topic_msgs = new String[topics.length + 1];

        // get the latest msg for each topic:
        shown_topics = new ArrayList<TopicView>();
        // for each topic i in the current selected zone:
        for (int i = 0; i < topics.length; i++) {
            // init markers for topic i:
            topic_hasNewEntry[i] = false;
            topic_hasAMsg[i] = false;
            topic_msgs[i] = "";
        }
        // for each message m from the Messenger:
        for (Message m : msgs_in_current_zone) {
            // get topic from m:
            String m_topic = m.getTopic();
            for (int i = 0; i < topics.length; i++) {
                if (isTopicPreferred(current_selected_zone.getZoneID(), m_topic)) {
                    if (m_topic.equals(topics[i])) {
                        topic_hasAMsg[i] = true;
                        if (newObtainedIDs.contains(m.getMessage_ID())) {
                            topic_hasNewEntry[i] = true;
                            topic_msgs[i] = m.getTitle() + ":" + m.getMsg();
                        }
                        if (!topic_hasNewEntry[i])
                            topic_msgs[i] = m.getTitle() + ":" + m.getMsg();
                    }
                }
            }
        }

        // create TopicViews for each topic:
        for (int i = 0; i < topics.length; i++) {
            if (isTopicPreferred(current_selected_zone.getZoneID(),topics[i])) {
                TopicView tv;
                if (topic_hasNewEntry[i]) {
                    // highlight it:
                    tv = new TopicView(getContext(), topics[i], topic_msgs[i], true);
                } else {
                    // don't highlight it:
                    tv = new TopicView(getContext(), topics[i], topic_msgs[i], false);
                }
                // add a onClickListener for the TopicView:
                tv.setClickable(true);

                shown_topics.add(tv);
            }
        }

        // add TopicViews to ScrollView into LinearLayout:
        ll_topics.removeAllViews();

        // add all messages onto the scrollview:
        for (TopicView tv : shown_topics) {
            ll_topics.addView(tv);
        }
    }

    /**
     * checks, if a topic of a zone is subscribed by the user
     * @param zone_id - the zone, the topic is part of
     * @param topic - the topic, that is to be subscribed
     * @return true - if the topic is subscribed in that zone, false otherwise
     */
    private boolean isTopicPreferred(String zone_id,String topic){
        return PreferenceManager.getDefaultSharedPreferences(getActivity().getApplicationContext())
                .getBoolean("pref_"+zone_id+"_"+topic,true);
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
            ll_topics = (LinearLayout) v.findViewById(R.id.ll_topics);
            slv_topics = (ScrollView) v.findViewById(R.id.slv_topics);
            btn_writeMsg = (Button) v.findViewById(R.id.btn_writeMsg);
        } catch (InflateException e) {
        /* btn_writeMsg is already there, just return view as it is */

            // create/update all views:
            createAllViews();
        }

        btn_writeMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), WriteMsgActivity.class);
                // open the write msg activity and use the first topic of the zone as default selection:
                String defaultTopic = current_selected_zone.getTopics()[0];
                intent.putExtra("TOPIC", defaultTopic);
                startActivity(intent);
            }
        });
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        // create/update all views:
        createAllViews();
    }

}