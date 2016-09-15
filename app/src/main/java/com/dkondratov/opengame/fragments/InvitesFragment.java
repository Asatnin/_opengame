package com.dkondratov.opengame.fragments;


import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.EventDetailActivity;
import com.dkondratov.opengame.activities.MainActivity;
import com.dkondratov.opengame.activities.PlacesActivity;
import com.dkondratov.opengame.adapters.EventsAdapter;
import com.dkondratov.opengame.adapters.EventsMenuItem;
import com.dkondratov.opengame.adapters.InvitesPagerAdapter;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.model.Invitation;
import com.dkondratov.opengame.network.NetworkUtilities;
import com.dkondratov.opengame.network.UpdateInvitationsRequest;
import com.dkondratov.opengame.network.UserInfoRequest;
import com.dkondratov.opengame.widgets.SlidingTabLayout;

import java.util.ArrayList;
import java.util.List;

public class InvitesFragment extends Fragment {

    private View fragmentView;
    private ListView listView;
    private ProgressBar progressBar;
    private TextView noInvitations;
    private SwipeRefreshLayout swipeRefreshLayout;


    public InvitesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e("invite on create", "invite");
        // Inflate the layout for this fragment
        fragmentView = inflater.inflate(R.layout.fragment_invites, container, false);
        listView = (ListView) fragmentView.findViewById(R.id.listView);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progressBar);
        noInvitations = (TextView) fragmentView.findViewById(R.id.no_invitations);
        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.invites_layout);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadInvitations();
            }
        });


        loadInvitations();

        return fragmentView;
    }

    private void loadInvitations() {
        noInvitations.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        listView.setVisibility(View.GONE);

        NetworkUtilities.networkUtilities(getActivity()).addToRequestQueue(new UpdateInvitationsRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                List<Invitation> invitations = (List<Invitation>) response;
                List<Event> events = new ArrayList<Event>();
                for (int i = 0; i < invitations.size(); i++) {
                    Event event = invitations.get(i).getEvent();
                    event.setInvitationID(invitations.get(i).getInvitation_id());
                    events.add(invitations.get(i).getEvent());
                }
                setUpListView(events);
                listView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                if (invitations.size() == 0) {
                    noInvitations.setVisibility(View.VISIBLE);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
                noInvitations.setVisibility(View.VISIBLE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, getActivity()));
    }

    private void setUpListView(List<Event> events) {

        Log.e("invite oncreate", "set up list view");
        List<EventsMenuItem> items = new ArrayList<>();
        for (final Event event : events) {
            items.add(new EventsMenuItem(getString(R.string.my_events), EventsMenuItem.EVENT_ITEM, event, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Log.e("was", "here3");
                    if (event.getField() != null) {
                        Intent intent = new Intent(getActivity(), PlacesActivity.class);
                        intent.putExtra("hide", true);
                        ArrayList<Field> field = new ArrayList<>();
                        field.add(event.getField());
                        intent.putParcelableArrayListExtra("fields", field);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
                    } else {
                        Toast.makeText(getActivity(), getString(R.string.field_not_found), Toast.LENGTH_SHORT).show();
                    }
                }
            }));
        }

        EventsAdapter adapter = new EventsAdapter(getActivity(), items, true);
        listView.setAdapter(adapter);

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.e("was", "here4");
                ((EventsMenuItem) parent.getAdapter().getItem(position)).getOnClickListener().onClick(view);
            }
        });
        */
    }


}
