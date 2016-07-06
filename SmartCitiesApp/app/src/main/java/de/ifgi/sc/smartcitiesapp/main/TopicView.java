package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;

import de.ifgi.sc.smartcitiesapp.R;

/**
 * Created by Maurin on 22.05.2016.
 */
public class TopicView extends LinearLayout implements View.OnClickListener {

    View rootView;
    TextView topicView;
    TextView msgView;
    TextView newView;
    boolean newObtainedMsg;
    private OnClickListener ocl;

    public TopicView(Context context, AttributeSet attrs, String topic, String message, boolean newObtained){
        super(context, attrs);
        init(context, topic, message, newObtained);
    }

    public TopicView(Context context, String topic, String message, boolean newObtained){
        super(context);
        init(context, topic, message, newObtained);
    }

    // adding title, message, expiredate to textviews:
    private void init(Context context, String topic, String message, boolean newObtained){
        rootView = inflate(context, R.layout.topic_row_layout, this);
        newObtainedMsg = newObtained;
        topicView = (TextView) rootView.findViewById(R.id.topic_title);
        msgView = (TextView) rootView.findViewById(R.id.topic_msg);
        newView = (TextView) rootView.findViewById(R.id.topic_new);

        if (newObtainedMsg) {
            // change the look of the new obtained msg... colorful and BOLD
            newView.setVisibility(VISIBLE);
        } else {
            // use standard color and non-BOLD:
            newView.setVisibility(GONE);
        }
        topicView.setText(topic);
        msgView.setText(message);
        this.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        Log.d("Topics","Topic clicked: "+ topicView.getText());
        Toast.makeText(getContext(), topicView.getText() + " selected", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(getContext(), MsgActivity.class);
        intent.putExtra("TOPIC", topicView.getText());
        getContext().startActivity(intent);
    }
}
