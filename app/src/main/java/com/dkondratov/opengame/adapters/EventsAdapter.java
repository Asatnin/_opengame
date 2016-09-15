package com.dkondratov.opengame.adapters;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.network.ApproveInvitationRequest;
import com.dkondratov.opengame.network.DeclineInvitationRequest;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.UpdateInvitationsRequest;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class EventsAdapter extends ArrayAdapter<EventsMenuItem> {

    private LayoutInflater Inflater;
    private Context mContext;
    private boolean invitations;

    public EventsAdapter(Context context, List<EventsMenuItem> objects, boolean invitations) {
        super(context, R.layout.event_item, objects);
        mContext = context;
        Inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.invitations = invitations;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getType();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        final EventsMenuItem menuItem = getItem(position);

        switch (getItemViewType(position)) {
            case EventsMenuItem.CATEGORY_ITEM:
                View view = Inflater.inflate(R.layout.event_category_item, null);
                TextView eventTextView = ((TextView) view.findViewById(R.id.event_title));
                eventTextView.setText(menuItem.getTitle());
                return view;
            case EventsMenuItem.EVENT_ITEM:
                View eventView = Inflater.inflate(R.layout.event_item, null);
                TextView userNameTextView = (TextView) eventView.findViewById(R.id.user_name);
                TextView dateTextView = (TextView) eventView.findViewById(R.id.date);
                TextView timeTextView = (TextView) eventView.findViewById(R.id.time);
                Event event = menuItem.getEvent();
                if (event != null) {
                    userNameTextView.setText(event.getCreatorString());
                    Date date = new Date();
                    if (invitations) {
                        date.setTime(event.getStart() * 1000);
                    } else {
                        date.setTime(event.getStart());
                    }
                    SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy");
                    SimpleDateFormat sdf1 = new SimpleDateFormat("HH:mm");
                    dateTextView.setText(sdf.format(date));
                    timeTextView.setText(sdf1.format(date));
                }

                Button accept = (Button) eventView.findViewById(R.id.accept_button);
                Button decline = (Button) eventView.findViewById(R.id.decline_button);

                if (!invitations) {
                    accept.setVisibility(View.GONE);
                    decline.setVisibility(View.GONE);
                } else {
                    accept.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NetworkUtilities.networkUtilities(mContext).addToRequestQueue(new ApproveInvitationRequest(new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    try {
                                        if (invitations) {
                                            menuItem.getEvent().setStart(menuItem.getEvent().getStart() * 1000);
                                        }
                                        Event event =  menuItem.getEvent();
                                        String creator = "";
                                        if (!TextUtils.isEmpty(event.getCreator().getName())) {
                                            creator += event.getCreator().getName() + " ";
                                        }
                                        if (!TextUtils.isEmpty(event.getCreator().getSurname())) {
                                            creator += event.getCreator().getSurname();
                                        }
                                        event.setCreatorString(creator);
                                        DatabaseManager.writeEvent(menuItem.getEvent());
                                        Toast.makeText(mContext, "Событие добавлено", Toast.LENGTH_SHORT);
                                    } catch (SQLException ex) {
                                        ex.printStackTrace();
                                        Toast.makeText(mContext, "Событие не сохранено", Toast.LENGTH_SHORT);
                                    }
                                    EventsAdapter.this.remove(menuItem);

                                    notifyDataSetChanged();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(mContext, mContext.getString(R.string.check_internet), Toast.LENGTH_SHORT);
                                }
                            }, menuItem.getEvent().getInvitationID(), mContext));
                        }
                    });
                    decline.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            NetworkUtilities.networkUtilities(mContext).addToRequestQueue(new DeclineInvitationRequest(new Response.Listener() {
                                @Override
                                public void onResponse(Object response) {
                                    EventsAdapter.this.remove(menuItem);
                                    notifyDataSetChanged();
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    Toast.makeText(mContext, mContext.getString(R.string.check_internet), Toast.LENGTH_SHORT);
                                }
                            }, menuItem.getEvent().getInvitationID(), mContext));
                        }
                    });
                    eventView.setOnClickListener(menuItem.getOnClickListener());
                }

                return eventView;
        }
      return convertView;
    }
}
