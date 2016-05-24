package de.ifgi.sc.smartcitiesapp.main;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import de.ifgi.sc.smartcitiesapp.R;

public class WriteMsgActivity extends AppCompatActivity {

    private String selected_topic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_msg);

        // add Back Button on Actionbar:
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        // which topic was selected?
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            selected_topic = extras.getString("TOPIC");
            setTitle(selected_topic);
        }

        // Add categories to the spinner:
        Spinner spn_category = (Spinner) findViewById(R.id.spn_category);
        final String[] values = new String[] { "Traffic", "Sports", "Restaurants",
                "Shopping", "placeholder1", "placeholder2", "placeholder3", "placeholder4",
                "placeholder5", "placeholder6" };
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this,
                android.R.layout.simple_spinner_item, values);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spn_category.setAdapter(adapter);
        // check out position of selected_topic in values:
        int index = 0;
        for (int i=0; i<values.length;i++){
            if (selected_topic.equals(values[i])) {
                index = i;
                break;
            }
        }
        // set previously selected topic as default:
        spn_category.setSelection(index);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
