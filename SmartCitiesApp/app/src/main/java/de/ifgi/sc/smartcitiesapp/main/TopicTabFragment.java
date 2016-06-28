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

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.settings.SettingsActivity;

public class TopicTabFragment extends ListFragment {

    private View v;
    private Button btn_writeMsg;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // TODO: Create values as topics recieved from current zone
        String[] values = new String[] { "Traffic", "Sports", "Restaurants",
                "Shopping", "placeholder1", "placeholder2", "placeholder3", "placeholder4",
                "placeholder5", "placeholder6" };
        // TODO: get 1st msg from each topic from current zone
        String[] msgs = new String[] { "Traffic Jam in the city center", "students beachvolleyball tournament at the castle",
                "recyclable \"to-go\"-coffee cups at Franks Copy Shop", "Missed Black friday? Clothes are 100% off at my place",
                "bla..","bla..","bla..","bla..","bla..","bla.."
        };
        MySimpleArrayAdapter adapter = new MySimpleArrayAdapter(getActivity(), values, msgs);
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