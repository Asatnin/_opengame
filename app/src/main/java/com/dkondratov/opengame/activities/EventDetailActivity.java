package com.dkondratov.opengame.activities;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.adapters.EventsAdapter;
import com.dkondratov.opengame.adapters.EventsMenuItem;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.database.HelperFactory;
import com.dkondratov.opengame.fragments.PlacesMapFragment;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.prolificinteractive.materialcalendarview.CalendarDay;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventDetailActivity extends ActionBarActivity {

    private Toolbar toolbar;
    private List<Event> events;
    private ListView listView;
    private TextView noEvents;
    private boolean fromField;
    private CalendarDay calendarDay;
    private String fieldID;
    private ArrayList<Field> fields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_detail);
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        listView = (ListView) findViewById(R.id.events_listivew);
        noEvents = (TextView) findViewById(R.id.no_events);

        fromField = getIntent().getBooleanExtra("from_field", false);
        events = getIntent().getParcelableArrayListExtra("events");
        calendarDay = getIntent().getParcelableExtra("day");
        fieldID = getIntent().getStringExtra("field_id");
        fields = new ArrayList<>();

        setUpActionBar();
        setUpViews();

        new AsyncCaller().execute();
    }

    private void setUpViews() {
        noEvents.setVisibility(View.VISIBLE);
        if (fromField) {
            noEvents.setText(getString(R.string.no_events_field));
        } else {
            noEvents.setText(getString(R.string.no_events_calendar));
        }
    }

    private void setUpListView() {
        Log.e("events number", "number: " + events.size());
        noEvents.setVisibility(View.GONE);
        final EventsAdapter adapter = new EventsAdapter(this, new ArrayList<EventsMenuItem>(), false);
        List<EventsMenuItem> items = new ArrayList<>();
        items.add(new EventsMenuItem(getString(R.string.my_events), EventsMenuItem.CATEGORY_ITEM, null, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //nothing
            }
        }));
        boolean myEventFound = false;
        for (final Event event : events) {
            if (event.isMy()) {
                myEventFound = true;
                items.add(new EventsMenuItem(getString(R.string.my_events), EventsMenuItem.EVENT_ITEM, event, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (event.getField_id() == null) {
                            startPlaceMapActivity(fieldID);
                        } else {
                            startPlaceMapActivity(event.getField_id());
                        }
                    }
                }));
            }
        }
        if (!myEventFound) {
            items.remove(0);
        }

        if (fromField) {
            boolean notMyEventFound = false;
            items.add(new EventsMenuItem(getString(R.string.all_events), EventsMenuItem.CATEGORY_ITEM, null, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //nothing
                }
            }));
            for (final Event event : events) {
                if (!event.isMy()) {
                    notMyEventFound = true;
                    items.add(new EventsMenuItem(getString(R.string.all_events), EventsMenuItem.EVENT_ITEM, event, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (event.getField_id() == null) {
                                startPlaceMapActivity(fieldID);
                            } else {
                                startPlaceMapActivity(event.getField_id());
                            }

                        }
                    }));
                }
            }
            if (!notMyEventFound) {
                items.remove(items.size() - 1);
            }
        }


        adapter.addAll(items);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, View view, int i, long l) {
                ((EventsMenuItem) adapterView.getAdapter().getItem(i)).getOnClickListener().onClick(view);
            }
        });

    }

    private void startPlaceMapActivity(String fieldID) {
        Intent intent = new Intent(this, PlacesActivity.class);
        intent.putExtra("hide", true);
     //   Bundle args = new Bundle();
        for (int i = 0; i < fields.size(); i ++) {
          //  Log.e("find method", fieldID + " vs " + fields.get(i).getFieldId());
            if (fieldID.equals(fields.get(i).getFieldId())) {
                ArrayList<Field> field = new ArrayList<>();
                field.add(fields.get(i));
                intent.putParcelableArrayListExtra("fields", field);
                //intent.putExtras(args);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                return;
            }
        }
        Toast.makeText(EventDetailActivity.this, getString(R.string.field_not_found), Toast.LENGTH_SHORT).show();
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

    private void setUpActionBar() {
        setSupportActionBar(toolbar);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView titleTextView = (TextView) findViewById(R.id.toolbar_title);
        titleTextView.setText(getString(R.string.events));
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Void... params) {
            for (int i = 0; i < events.size(); i ++) {
                CalendarDay day = new CalendarDay(new Date(events.get(i).getStart()));
                if (!day.equals(calendarDay)) {
                    events.remove(i);
                    i--;
                } else {
                    try {
                        String localFieldID = events.get(i).getField_id();
                        Log.e("localFieldID", "" + localFieldID);
                        if (localFieldID == null) {
                            localFieldID = fieldID;
                        }
                        Log.e("localFieldID", "" + localFieldID);
                        fields.addAll(DatabaseManager.getAllFieldsByFieldId(localFieldID));
                        Log.e("was here", "fields size: " + fields.size());
                    } catch (SQLException ex) {
                        ex.printStackTrace();
                    }
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (events != null && events.size() > 0) {
                setUpListView();
            }
        }

    }

}
