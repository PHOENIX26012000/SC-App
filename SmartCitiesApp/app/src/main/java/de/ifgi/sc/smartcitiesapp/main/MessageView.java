package de.ifgi.sc.smartcitiesapp.main;

import android.content.Context;
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

    public MessageView(Context context, AttributeSet attrs, String title, String message, String expDate){
        super(context, attrs);
        init(context, title, message, expDate);
    }

    public MessageView(Context context, String title, String message, String expDate){
        super(context);
        init(context, title, message, expDate);
    }

    // adding title, message, expiredate to textviews:
    private void init(Context context, String title, String message, String expDate){
        rootView = inflate(context, R.layout.row_msg_layout, this);
        titleView = (TextView) rootView.findViewById(R.id.msg_title);
        expDateView = (TextView) rootView.findViewById(R.id.msg_expdate);
        textView = (TextView) rootView.findViewById(R.id.msg_text);

        titleView.setText(title);
        textView.setText(message);
        expDateView.setText(expDate);
    }
}
