package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.EventDetailActivity;
import com.dkondratov.opengame.activities.MainActivity;
import com.dkondratov.opengame.adapters.CategoriesListAdapter;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.model.Event;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class CalendarFragment extends Fragment {

    private ArrayList<Event> events;
    private MaterialCalendarView calendar;

    public CalendarFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        new AsyncCaller().execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View fragmentView = inflater.inflate(R.layout.fragment_calendar, container, false);
        calendar = (MaterialCalendarView) fragmentView.findViewById(R.id.calendar);
        return fragmentView;
    }

    private void setUpCalendar() {
        final DayViewDecorator dayViewDecorator = new DayViewDecorator() {
            @Override
            public boolean shouldDecorate(CalendarDay calendarDay) {
                for (int i = 0; i < events.size(); i ++) {
                    CalendarDay day = new CalendarDay(new Date(events.get(i).getStart()));
                    if (day.equals(calendarDay)) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void decorate(DayViewFacade dayViewFacade) {
                dayViewFacade.addSpan(new DotSpan(7f, getResources().getColor(R.color.gold_color)));
            }
        };
        calendar.setOnDateChangedListener(new OnDateChangedListener() {
            @Override
            public void onDateChanged(MaterialCalendarView materialCalendarView, CalendarDay calendarDay) {
                /*for (int i = 0; i < events.size(); i ++) {
                    CalendarDay day = new CalendarDay(new Date(events.get(i).getStart()));
                    if (day.equals(calendarDay)) {
                        Intent eventActivity = new Intent(getActivity(), EventDetailActivity.class);
                        eventActivity.putParcelableArrayListExtra("events", events);
                        eventActivity.putExtra("from_field", false);
                        eventActivity.putExtra("day", calendarDay);
                        startActivity(eventActivity);
                        return;
                    }
                }*/
                Intent eventActivity = new Intent(getActivity(), EventDetailActivity.class);
                eventActivity.putParcelableArrayListExtra("events", events);
                eventActivity.putExtra("from_field", false);
                eventActivity.putExtra("day", calendarDay);
                startActivity(eventActivity);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });
        calendar.addDecorator(dayViewDecorator);
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            events = new ArrayList<>();
            try {
                List<Event> readedEvents = DatabaseManager.getAllEvents();
                if (readedEvents != null) {
                    events.addAll(readedEvents);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            if (events != null) {
                setUpCalendar();
            }
        }

    }
}
