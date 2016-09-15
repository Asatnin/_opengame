package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.MainActivity;
import com.dkondratov.opengame.adapters.FriendsPagerAdapter;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.NewEventRequest;
import com.dkondratov.opengame.network.UserUpdateRequest;
import com.dkondratov.opengame.widgets.SlidingTabLayout;

import org.json.JSONObject;
import org.w3c.dom.Text;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

/**
 * A simple {@link Fragment} subclass.
 */
public class NewEventFragment extends Fragment {

    private EditText titleTextView;
    private EditText notesTextView;
    private View fragmentView;
    private Switch favoSwitch;
    private Switch teamSwitch;
    private Switch friendsSwitch;
    private TextView dateStartTextView;
    private TextView timeStartTextView;
    private TextView dateEndTextView;
    private TextView timeEndTextView;
    private TextView notificationDateTextView;
    private TextView notificationTimeTextView;
    private RelativeLayout startLayout;
    private RelativeLayout endLayout;
    private RelativeLayout notificationLayout;
    private Button addButton;
    private RelativeLayout friendsLayout;
    private RelativeLayout teamLayout;
    private RelativeLayout favoLayout;
    private Calendar startCalendar;
    private Calendar endCalendar;
    private Calendar notificationCalendar;

    private Field field;
    private EventAddRequestCallback mCallbacks;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (EventAddRequestCallback) activity;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity().getIntent().getExtras() != null && getActivity().getIntent().getExtras().containsKey("field")) {
            field = getActivity().getIntent().getExtras().getParcelable("field");
        }
    }

    public NewEventFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_new_event, container, false);
        setupFragment();
        setupOnClickListeners();
        return fragmentView;
    }

    private void setupFragment() {
        titleTextView = (EditText) fragmentView.findViewById(R.id.event_name);
        notesTextView = (EditText) fragmentView.findViewById(R.id.event_notes);
        favoSwitch = (Switch) fragmentView.findViewById(R.id.favo_switch);
        teamSwitch = (Switch) fragmentView.findViewById(R.id.team_switch);
        friendsSwitch = (Switch) fragmentView.findViewById(R.id.friends_switch);
        dateStartTextView = (TextView) fragmentView.findViewById(R.id.date_start);
        timeStartTextView = (TextView) fragmentView.findViewById(R.id.time_start);
        dateEndTextView = (TextView) fragmentView.findViewById(R.id.date_end);
        timeEndTextView = (TextView) fragmentView.findViewById(R.id.time_end);
        notificationDateTextView = (TextView) fragmentView.findViewById(R.id.date_notification);
        notificationTimeTextView = (TextView) fragmentView.findViewById(R.id.time_notification);
        startLayout = (RelativeLayout) fragmentView.findViewById(R.id.start_layout);
        endLayout = (RelativeLayout) fragmentView.findViewById(R.id.end_layout);
        notificationLayout = (RelativeLayout) fragmentView.findViewById(R.id.notification_layout);
        addButton = (Button) fragmentView.findViewById(R.id.add_button);
        friendsLayout = (RelativeLayout) fragmentView.findViewById(R.id.friends_layout);
        teamLayout = (RelativeLayout) fragmentView.findViewById(R.id.team_layout);
        favoLayout = (RelativeLayout) fragmentView.findViewById(R.id.favo_layout);

        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        notificationCalendar = Calendar.getInstance();
    }

    private void setupOnClickListeners() {
        startLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(dateStartTextView, 0, true, timeStartTextView);
            }
        });
        endLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(dateEndTextView, 1, true, timeEndTextView);
            }
        });
        notificationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(notificationDateTextView, 2, true, notificationTimeTextView);
            }
        });
        friendsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                friendsSwitch.setChecked(!friendsSwitch.isChecked());
            }
        });
        teamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                teamSwitch.setChecked(!teamSwitch.isChecked());
            }
        });
        favoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                favoSwitch.setChecked(!favoSwitch.isChecked());
            }
        });
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEventAndSendIfOk();
            }
        });
        dateStartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(dateStartTextView, 0, false, timeStartTextView);
            }
        });
        dateEndTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(dateEndTextView, 1, false, timeEndTextView);
            }
        });
        notificationDateTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog(notificationDateTextView, 2, false, notificationTimeTextView);
            }
        });
        timeStartTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(timeStartTextView, 0);
            }
        });
        timeEndTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(timeEndTextView, 1);
            }
        });
        notificationTimeTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timePickerDialog(notificationTimeTextView, 2);
            }
        });
    }

    private void datePickerDialog(final TextView dateTextView, final int typeIndex, final boolean showTimePicker, final TextView timeTextView) {
        Calendar currentCalendar = Calendar.getInstance();
        switch (typeIndex) {
            case 0:
                startCalendar = Calendar.getInstance();
                currentCalendar = startCalendar;
                break;
            case 1:
                endCalendar = Calendar.getInstance();
                currentCalendar = endCalendar;
                break;
            case 2:
                notificationCalendar = Calendar.getInstance();
                currentCalendar = notificationCalendar;
                break;
        }
        //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm");
        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
                String stringResult = "";
                switch (typeIndex) {
                    case 0:
                        startCalendar.set(year, monthOfYear, dayOfMonth);
                        stringResult = sdf.format(startCalendar.getTime());
                        break;
                    case 1:
                        endCalendar.set(year, monthOfYear, dayOfMonth);
                        stringResult = sdf.format(endCalendar.getTime());
                        if (startCalendar.getTimeInMillis() - endCalendar.getTimeInMillis() > 100)
                        break;
                    case 2:
                        notificationCalendar.set(year, monthOfYear, dayOfMonth);
                        stringResult = sdf.format(notificationCalendar.getTime());
                        break;
                }
                dateTextView.setText(stringResult);

                if (showTimePicker) {
                    timePickerDialog(timeTextView, typeIndex);
                }
            }
        }, currentCalendar.get(Calendar.YEAR), currentCalendar.get(Calendar.MONTH), currentCalendar.get(Calendar.DAY_OF_MONTH));
        datePickerDialog.show();
    }

    private void timePickerDialog(final TextView timeTextView, final int typeIndex) {
        Date currentDate = new Date(System.currentTimeMillis());
        //SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, HH:mm");
        TimePickerDialog timePickerDialog = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
                String stringResult = "";
                switch (typeIndex) {
                    case 0:
                        startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        startCalendar.set(Calendar.MINUTE, minute);
                        stringResult = sdf.format(startCalendar.getTime());
                        break;
                    case 1:
                        endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        endCalendar.set(Calendar.MINUTE, minute);
                        stringResult = sdf.format(endCalendar.getTime());
                        break;
                    case 2:
                        notificationCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        notificationCalendar.set(Calendar.MINUTE, minute);
                        stringResult = sdf.format(notificationCalendar.getTime());
                        break;
                }
                timeTextView.setText(stringResult);
            }
        }, currentDate.getHours(), currentDate.getMinutes(), true);
        timePickerDialog.show();
    }

    private void checkEventAndSendIfOk() {
        if (loadUserId(getActivity()) == null) {
            new DialogFragment() {
                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage(R.string.for_registered_user)
                            .setPositiveButton(R.string.register_me, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .setNegativeButton(R.string.ok_word, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    return builder.create();
                }
            }.show(getActivity().getSupportFragmentManager(), "register_dialog");
            return;
        } else {
        /*
        String title, String description, long start, long end,
                                    boolean friends, boolean team, boolean favo
         */
            NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(new NewEventRequest(new Response.Listener() {
                @Override
                public void onResponse(Object response) {
                    Event event = (Event) response;
                    String creator = "";
                    if (!TextUtils.isEmpty(event.getCreator().getName())) {
                        creator += event.getCreator().getName() + " ";
                    }
                    if (!TextUtils.isEmpty(event.getCreator().getSurname())) {
                        creator += event.getCreator().getSurname();
                    }
                    event.setCreatorString(creator);
                    Log.e("event", event.getCreatorString() + event.getTitle());
                    mCallbacks.onEventAdded(event);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
                        Log.e("error", jsonObject.toString());
                        if (jsonObject.has("description")) {
                            mCallbacks.onEventAddingFailed(jsonObject.optString("description"));
                            Toast.makeText(getActivity(), jsonObject.optString("description"), Toast.LENGTH_SHORT).show();
                            return;
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    mCallbacks.onEventAddingFailed(getString(R.string.check_network));

                }
            }, getActivity(), field.getFieldId(), titleTextView.getText().toString(),
                    notesTextView.getText().toString(),
                    startCalendar.getTimeInMillis(), endCalendar.getTimeInMillis(),
                    friendsSwitch.isChecked(), teamSwitch.isChecked(), favoSwitch.isChecked()
            ));
        }
    }

    public interface EventAddRequestCallback {
        void onEventAdded(Event event);
        void onEventAddingFailed(String reason);
    }
}
