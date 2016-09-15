package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.NetworkResponse;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.EventDetailActivity;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.AddFieldToFavouriteRequest;
import com.dkondratov.opengame.network.CheckInPlaceRequest;
import com.dkondratov.opengame.network.CheckOutPlaceRequest;
import com.dkondratov.opengame.network.CoversRequest;
import com.dkondratov.opengame.network.EventsForFieldRequest;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.NewEventRequest;
import com.dkondratov.opengame.network.PlayersCountOnFieldRequest;
import com.dkondratov.opengame.network.RemoveFieldFromFavouriteRequest;
import com.dkondratov.opengame.services.CheckInIntentService;
import com.dkondratov.opengame.services.GPSTracker;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.parse.ParsePush;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateChangedListener;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static com.dkondratov.opengame.database.DatabaseManager.updateField;
import static com.dkondratov.opengame.database.HelperFactory.getHelper;
import static com.dkondratov.opengame.network.NetworkUtilities.networkUtilities;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlaceDetailFragment extends Fragment
        implements View.OnClickListener {

    private Switch favoritesSwitch;
    private Switch checkinSwitch;
    private MaterialCalendarView calendar;
    private PlaceDetailFragmentCallbacks mCallbacks;
    private String[] placeParamsNames;
    private int usersNow;
    private TextView maxUsersTextView;
    private static final int[] paramsImages = new int[] {
            R.drawable.lightning,
            R.drawable.cover,
            R.drawable.players_now,
            R.drawable.charge,
            R.drawable.winter,
            R.drawable.timetable,
            R.drawable.phone,
            R.drawable.address
    };
    private Field field;
    private ArrayList<Event> events;

    private RequestQueue queue;

    public PlaceDetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();
        new AsyncCaller().execute();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
        field = getActivity().getIntent().getExtras().getParcelable("field");
        NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(new EventsForFieldRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                if (events == null) events = new ArrayList<>();
                events.addAll((List<Event>) response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "SHIT", Toast.LENGTH_LONG);
            }
        }, field.getFieldId(), getActivity()));
        NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(new PlayersCountOnFieldRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                int count = ((List<User>) response).size();
                usersNow = count;
                if (maxUsersTextView != null) {
                    maxUsersTextView.setText("" + field.getMax_users() + "/" + usersNow);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Toast.makeText(getActivity(), "SHIT", Toast.LENGTH_LONG);
            }
        }, field.getFieldId()));
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (PlaceDetailFragmentCallbacks) activity;
    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            favoritesSwitch.setChecked(field.getFavorite().booleanValue());
        } catch (Exception ex) {
            ex.printStackTrace();
            favoritesSwitch.setChecked(false);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_place_detail, container, false);

        View background = view.findViewById(R.id.background_scroll_view);

        if (field.getSportId().equals("5666083260334080")) {
            background.setBackgroundResource(R.drawable.basket_background);
        }
        if (field.getSportId().equals("5669544198668288")) {
            background.setBackgroundResource(R.drawable.volley_background);
        }
        if (field.getSportId().equals("5681034041491456")) {
            background.setBackgroundResource(R.drawable.tennis_background);
        }
        if (field.getSportId().equals("6217263929622528")) {
            background.setBackgroundResource(R.drawable.hockey_background);
        }

        if (field.getDescription() == null || field.getDescription().isEmpty()) {
            (view.findViewById(R.id.place_description_layout)).setVisibility(View.GONE);
        }

        placeParamsNames = getActivity().getResources().getStringArray(R.array.place_param_array);

        TextView placeDesc = (TextView) view.findViewById(R.id.place_desc);
        placeDesc.setText(field.getDescription());

        calendar = (MaterialCalendarView) view.findViewById(R.id.calendar);

        favoritesSwitch = (Switch) view.findViewById(R.id.favorites_switch);
        favoritesSwitch.setOnClickListener(this);
        try {
            favoritesSwitch.setChecked(new Boolean(field.getFavorite()));
            PlaceDetailFragment.this.getActivity().getIntent().putExtra("switch", field.getFavorite());
        } catch (NullPointerException ex) {
            ex.printStackTrace();
            favoritesSwitch.setChecked(false);
        }
        favoritesSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                PlaceDetailFragment.this.getActivity().getIntent().putExtra("switch", isChecked);

                if (TextUtils.isEmpty(loadUserId(getActivity()))) {
                    Toast.makeText(getActivity(), getString(R.string.for_registered_user), Toast.LENGTH_LONG).show();
                    favoritesSwitch.setChecked(false);
                    return;
                }
                field.setFavorite(new Boolean(isChecked));
                if (isChecked) {
                    NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(
                            new AddFieldToFavouriteRequest(
                                    new Response.Listener<Object>() {
                                        @Override
                                        public void onResponse(Object response) {
                                            String resp = (String) response;
                                            if (resp.equals("OK")) {
                                                ParsePush.subscribeInBackground("field_android_" + field.getFieldId());
                                                new AsyncCallerFavourite().execute();
                                                favoritesSwitch.setChecked(true);
                                            } else {
                                                favoritesSwitch.setChecked(false);
                                                Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    favoritesSwitch.setChecked(false);
                                    Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                                }
                            }, getActivity(), field.getFieldId()
                            ));

                } else {
                    NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(
                            new RemoveFieldFromFavouriteRequest(
                                    new Response.Listener<Object>() {
                                        @Override
                                        public void onResponse(Object response) {
                                            String resp = (String) response;
                                            if (resp.equals("OK")) {
                                                ParsePush.unsubscribeInBackground("field_android_" + field.getFieldId());
                                                new AsyncCallerFavouriteRemove().execute();
                                                favoritesSwitch.setChecked(false);
                                            } else {
                                                favoritesSwitch.setChecked(true);
                                                Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    favoritesSwitch.setChecked(true);
                                    Toast.makeText(getActivity(), getString(R.string.check_internet), Toast.LENGTH_SHORT).show();
                                }
                            }, getActivity(), field.getFieldId()
                            ));
                }
            }
        });

        checkinSwitch = (Switch) view.findViewById(R.id.checkin_switch);
        checkinSwitch.setChecked(!TextUtils.isEmpty(CheckInIntentService.fieldId) && CheckInIntentService.fieldId.equals(field.getFieldId()));
        checkinSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (TextUtils.isEmpty(loadUserId(getActivity()))) {
                    Toast.makeText(getActivity(), getString(R.string.for_registered_user), Toast.LENGTH_LONG).show();
                    checkinSwitch.setChecked(false);
                    return;
                }
                if (isChecked)
                    checkIn();
                else
                    checkOut();
            }
        });

        LinearLayout placeLayout = (LinearLayout) view.findViewById(R.id.place_params_layout);
        for (int i = 0; i < placeParamsNames.length; i++) {
            View paramView = inflater.inflate(R.layout.place_param, placeLayout, false);
            setUpParamView(paramView, i);
            placeLayout.addView(paramView);
        }

        ImageButton newEventButton = (ImageButton) view.findViewById(R.id.new_event_button);
        newEventButton.setOnClickListener(this);

        //
        setUpCalendar();

        return view;
    }

    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Геолокация недоступна");
        alertDialog.setMessage("Включите геолокацию для работы приложения");

        //On Pressing Setting button
        alertDialog.setPositiveButton(R.string.action_settings, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        });

        alertDialog.setNegativeButton("Отмена", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which)
            {
                dialog.cancel();
            }
        });

        alertDialog.show();
    }

    private void checkIn() {
        queue.cancelAll(this);
        GPSTracker tracker = new GPSTracker(getActivity());
        if (!tracker.getIsGPSTrackingEnabled()) {
            showSettingsAlert();
            checkinSwitch.setChecked(false);
            return;
        }

        String lat = String.valueOf(tracker.latitude);
        String lon = String.valueOf(tracker.longitude);

        CheckInPlaceRequest request = new CheckInPlaceRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                usersNow += 1;
                if (maxUsersTextView != null) {
                    maxUsersTextView.setText("" + field.getMax_users() + "/" + usersNow);
                }
                CheckInIntentService.startSynchronizeCheckInPlace(getActivity(), field.getFieldId());
            }
        },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        NetworkResponse response = error.networkResponse;
                        if(response != null && response.data != null){
                            switch(response.statusCode){
                                case 400:
                                    try {
                                    String json = new String(response.data);
                                        JSONObject object = new JSONObject(json);
                                        Toast.makeText(getActivity(), object.getString("description").toString(), Toast.LENGTH_SHORT).show();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        Toast.makeText(getActivity(), "Ошибка, проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                                    }
                                    break;
                                default:
                                    Toast.makeText(getActivity(), "Ошибка, проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();

                            }
                        } else
                        Toast.makeText(getActivity(), "Ошибка, проверьте соединение с интернетом", Toast.LENGTH_SHORT).show();
                        checkinSwitch.setChecked(false);
                    }
                }, ApplicationUserData.loadUserId(getActivity()), field.getFieldId(), lat, lon);
        request.setTag(this);
        queue.add(request);
    }

    private void checkOut() {
        queue.cancelAll(this);
        CheckOutPlaceRequest request = new CheckOutPlaceRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                CheckInIntentService.stopSynchronizeCheckInPlace(getActivity(), field.getFieldId());
                usersNow -= 1;
                if (maxUsersTextView != null) {
                    maxUsersTextView.setText("" + field.getMax_users() + "/" + usersNow);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                CheckInIntentService.stopSynchronizeCheckInPlace(getActivity(), field.getFieldId());

            }
        }, ApplicationUserData.loadUserId(getActivity()), field.getFieldId());
        request.setTag(this);
        queue.add(request);
    }

    private void setUpParamView(View paramView, int number) {
        TextView paramName = (TextView) paramView.findViewById(R.id.place_param_name);
        paramName.setText(placeParamsNames[number]);

        ImageView paramImage = (ImageView) paramView.findViewById(R.id.place_param_image);
        paramImage.setImageResource(paramsImages[number]);

        TextView paramValue = (TextView) paramView.findViewById(R.id.place_param_value);
        setParamText(paramValue, number);
    }

    private void setUpCalendar() {
        final ArrayList<Event> events = new ArrayList<>();
        try {
            List<Event> readedEvents = DatabaseManager.getAllEvents();
            if (readedEvents != null) {
                events.addAll(readedEvents);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }

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
                Intent eventActivity = new Intent(getActivity(), EventDetailActivity.class);
                eventActivity.putParcelableArrayListExtra("events", events);
                eventActivity.putExtra("from_field", true);
                eventActivity.putExtra("day", calendarDay);
                eventActivity.putExtra("field_id", field.getFieldId());
                startActivity(eventActivity);
                getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
            }
        });

        calendar.addDecorator(dayViewDecorator);
    }

    private void setParamText(TextView paramValue, int index) {
        int text;
        switch (index) {
            case 0:
                text = R.string.yes_rus;
                if (field.getLighting() == null || !field.getLighting()) {
                    text = R.string.no_rus;
                }
                paramValue.setText(text);
                break;
            case 1:
                paramValue.setText(ApplicationUserData.loadCoversJSON(getActivity()).get(Integer.parseInt(field.getCover())).getName());
                break;
            case 2:
                maxUsersTextView = paramValue;
                paramValue.setText("" + field.getMax_users() + "/" + usersNow);
                break;
            case 3:
                paramValue.setText(field.getPrice().equals("0") ? "free" : field.getPrice());
                break;
            case 4:
                text = R.string.yes_rus;
                if (field.getWinter() == null || !field.getLighting()) {
                    text = R.string.no_rus;
                }
                paramValue.setText(text);
                break;
            case 5:
                paramValue.setText(field.getSchedule());
                break;
            case 6:
                paramValue.setText(field.getPhone());
                break;
            case 7:
                paramValue.setText(field.getAddress());
                break;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.favorites_switch:
                boolean on = ((Switch) v).isChecked();
                if (on) {
                    field.setFavorite(true);
                } else {
                    field.setFavorite(false);
                }

                try {
                    updateField(field);
                } catch (SQLException e) {
                    Log.e("UpdateFieldException", "UpdateFavoriteFieldException");
                }

                break;

            default:
                mCallbacks.onNewEventButtonClicked(field);
                break;
        }
    }

    public interface PlaceDetailFragmentCallbacks {
        void onNewEventButtonClicked(Field fieid);
    }

    private class AsyncCaller extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (events == null) {
                events = new ArrayList<>();
            }
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

    private class AsyncCallerFavourite extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DatabaseManager.updateField(field);
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    private class AsyncCallerFavouriteRemove extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                DatabaseManager.removeFieldFromFavourite(field.getFieldId());
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

}
