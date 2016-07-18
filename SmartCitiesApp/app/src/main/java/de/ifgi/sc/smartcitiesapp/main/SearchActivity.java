package de.ifgi.sc.smartcitiesapp.main;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import de.ifgi.sc.smartcitiesapp.R;
import de.ifgi.sc.smartcitiesapp.messaging.Message;
import de.ifgi.sc.smartcitiesapp.messaging.Messenger;

public class SearchActivity extends Activity {

    private SimpleDateFormat D_format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        // Change default behaviour of the edittextSearch: On Enter: close EditText and conduct the search:
        final EditText edt_lookup = (EditText) findViewById(R.id.edt_lookup);
        edt_lookup.setFocusableInTouchMode(true);
        edt_lookup.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if ((actionId == EditorInfo.IME_ACTION_DONE) || ((event.getKeyCode() == KeyEvent.KEYCODE_ENTER) && (event.getAction() == KeyEvent.ACTION_DOWN))) {
                    // if the "enter"-key was pressed, close the shown Keyboard
                    InputMethodManager in = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

                    in.hideSoftInputFromWindow(v.getWindowToken(),
                            InputMethodManager.HIDE_NOT_ALWAYS);
                    // conduct the search:
                    searchMessagesForResults(edt_lookup.getText()+"");
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    private void searchMessagesForResults(String lookup){
        ArrayList<Message> results = new ArrayList<Message>();
        // 1. go through all msgs in the current selected zone

        // 2. go through all msgs in the zones, the user is currently in

        // 3. go through all msgs
        ArrayList<Message> msgs = Messenger.getInstance().getAllMessages();
        for (Message m : msgs){
            if ((m.getTitle().contains(lookup)) || (m.getMsg().contains(lookup))) {
                results.add(m);
            }
        }

        // add the Scrollview with the result msgs:
        LinearLayout ll_results = (LinearLayout) findViewById(R.id.ll_resultmsgs);
        ll_results.removeAllViews();

        for (Message m : results){
            // create MessageView from msg for the layout:
            Date exp_date = new Date();
            try {
                exp_date = D_format.parse(m.getExpired_At());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            long timeDiffInMillis = exp_date.getTime() - new Date().getTime();
            int days = (int) timeDiffInMillis / (1000 * 60 * 60 * 24);
            long restDiff = timeDiffInMillis - days * (1000 * 60 * 60 * 24);
            int hours = (int) restDiff / (1000 * 60 * 60);
            restDiff = restDiff - hours * (1000 * 60 * 60);
            int mins = (int) restDiff / (1000 * 60);
            String expiresIn = days + "d " + hours + "h " + mins + "m";
            MessageView mv = new MessageView(this,m.getTitle(),m.getMsg(),expiresIn,false);
            ll_results.addView(mv);
        }
    }
}