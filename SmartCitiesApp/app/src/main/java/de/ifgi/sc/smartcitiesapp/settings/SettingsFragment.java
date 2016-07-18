package de.ifgi.sc.smartcitiesapp.settings;


import android.graphics.Color;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.zone.NoZoneCurrentlySelectedException;
import de.ifgi.sc.smartcitiesapp.zone.Zone;
import de.ifgi.sc.smartcitiesapp.zone.ZoneManager;

/**
 * A simple {@link Fragment} subclass.
 */
public class SettingsFragment extends PreferenceFragment {


    public SettingsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource: just a Dummy-item.
        addPreferencesFromResource(R.xml.preferences);

        // get the current zone:
        ArrayList<Zone> zonesFromDB = ZoneManager.getInstance().getAllZonesfromDatabase();
        Zone current_selected_zone;
        try {
            current_selected_zone = ZoneManager.getInstance().getCurrentZone();
        } catch (NoZoneCurrentlySelectedException e) {
            // what do, if no zone is currently selected?
            // select the first zone of the zonemanager
            current_selected_zone = zonesFromDB.get(0);
            ZoneManager.getInstance().setCurrentZone(current_selected_zone);
        }

        // create the preference screen
        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());

        // create preference category manually:
        PreferenceCategory category = new PreferenceCategory(screen.getContext());
        category.setTitle("Subscribed topics of zone " + current_selected_zone.getName());
        screen.addPreference(category);

        String zoneID = current_selected_zone.getZoneID();

        // get all topics from the current selected zone:
        String[] topics = current_selected_zone.getTopics();

        // create a preference item for each topic:
        for (String topic : topics) {
            // create the pref item:
            SwitchPreference traffic_topic = new SwitchPreference(getActivity().getApplicationContext());
            // preference key format: pref_<zoneID>_<topic>
            traffic_topic.setKey("pref_" + zoneID + "_" + topic);
            traffic_topic.setDefaultValue(true);

            traffic_topic.setTitle(Html.fromHtml("<font color='black'><b>" + topic + "</b></font>"));
            traffic_topic.setSummary(Html.fromHtml("<font color='black'> Show all messages of topic " + topic + "</font>"));

            category.addPreference(traffic_topic);
        }

        setPreferenceScreen(screen);
    }

}

