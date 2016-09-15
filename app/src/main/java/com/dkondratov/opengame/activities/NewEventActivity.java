package com.dkondratov.opengame.activities;

import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseHelper;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.database.HelperFactory;
import com.dkondratov.opengame.fragments.NewEventFragment;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;

import java.sql.SQLException;
import java.util.ArrayList;

public class NewEventActivity extends ActionBarActivity implements NewEventFragment.EventAddRequestCallback{

    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setupActionBar();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_in_right,
                R.anim.slide_out_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               super.onBackPressed();
        }
        return true;
    }

    private void setupActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText(getString(R.string.new_event));
    }

    @Override
    public void onEventAdded(Event event) {
        Log.e("event", event.toString());
        Log.e("event", event.getEvent_id());
        Log.e("event", event.getTitle());
        new AsyncCaller().execute(event);
        this.finish();
    }

    @Override
    public void onEventAddingFailed(String reason) {
        Toast.makeText(this, reason, Toast.LENGTH_SHORT).show();
    }

    private class AsyncCaller extends AsyncTask<Event, Void, Void> {

        @Override
        protected Void doInBackground(Event... params) {
            try {
                //params[0].setEvent_id("" + System.currentTimeMillis());
                params[0].setField_id(((Field)getIntent().getExtras().getParcelable("field")).getFieldId());
                DatabaseManager.writeEvent(params[0]);
            } catch (SQLException ex) {
                Log.e("exception", ex.getLocalizedMessage() + " : " + ex.getSQLState());
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }

    }

}
