package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import de.ifgi.sc.smartcitiesapp.R;


public class MessageView extends LinearLayout {

    View rootView;
    TextView titleView;
    TextView expDateView;
    TextView textView;
    TextView newView;
    boolean newObtainedMsg;

    /**
     * First constructor: Message with attributes
     *
     * @param context
     * @param attrs
     * @param title
     * @param message
     * @param expDate
     * @param newObtained
     */
    public MessageView(Context context, AttributeSet attrs, String title, String message, String expDate, boolean newObtained) {
        super(context, attrs);
        init(context, title, message, expDate, newObtained);
    }

    /**
     * Second constructor: Message without attributes
     *
     * @param context
     * @param title
     * @param message
     * @param expDate
     * @param newObtained
     */
    public MessageView(Context context, String title, String message, String expDate, boolean newObtained) {
        super(context);
        init(context, title, message, expDate, newObtained);
    }

    /**
     * Adding title, message, expiredate to textviews:
     *
     * @param context
     * @param title
     * @param message
     * @param expDate
     * @param newObtained
     */
    private void init(Context context, String title, String message, String expDate, boolean newObtained) {
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
        expDateView.setText("expires in: " + expDate);
    }
}
