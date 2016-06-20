package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import de.ifgi.sc.smartcitiesapp.R;

public class MySimpleArrayAdapter extends ArrayAdapter<String> {
    private final Context context;
    private final String[] topics;
    private final String[] msgs;

    public MySimpleArrayAdapter(Context context, String[] topics, String[] msgs) {
        super(context, R.layout.row_layout_listitem, topics);
        this.context = context;
        this.topics = topics;
        this.msgs = msgs;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView = inflater.inflate(R.layout.row_layout_listitem, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.topic);
        TextView imageView = (TextView) rowView.findViewById(R.id.label);
        textView.setText(topics[position]);
        imageView.setText(msgs[position]);

        return rowView;
    }
}