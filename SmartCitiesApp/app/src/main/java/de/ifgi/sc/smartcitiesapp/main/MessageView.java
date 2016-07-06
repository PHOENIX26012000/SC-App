package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;

import de.ifgi.sc.smartcitiesapp.R;

/**
 * Created by Maurin on 22.05.2016.
 */
public class MessageView extends LinearLayout {

    View rootView;
    TextView titleView;
    TextView expDateView;
    TextView textView;
    TextView newView;
    boolean newObtainedMsg;

    public MessageView(Context context, AttributeSet attrs, String title, String message, String expDate, boolean newObtained){
        super(context, attrs);
        init(context, title, message, expDate, newObtained);
    }

    public MessageView(Context context, String title, String message, String expDate, boolean newObtained){
        super(context);
        init(context, title, message, expDate, newObtained);
    }

    // adding title, message, expiredate to textviews:
    private void init(Context context, String title, String message, String expDate, boolean newObtained){
        rootView = inflate(context, R.layout.row_msg_layout, this);
        newObtainedMsg = newObtained;
        titleView = (TextView) rootView.findViewById(R.id.msg_title);
        expDateView = (TextView) rootView.findViewById(R.id.msg_expdate);
        textView = (TextView) rootView.findViewById(R.id.msg_text);
        newView = (TextView) rootView.findViewById(R.id.msg_new);

        if (newObtainedMsg) {
            // change the look of the new obtained msg... colorful and BOLD
            newView.setVisibility(VISIBLE);
        } else {
            // use standard color and non-BOLD:
            newView.setVisibility(GONE);
        }

        titleView.setTextColor(Color.BLACK);
        titleView.setText(title);
        textView.setText(message);
        expDateView.setText("expires in: "+expDate);
    }
}
