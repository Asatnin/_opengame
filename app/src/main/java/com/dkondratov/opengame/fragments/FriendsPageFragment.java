package com.dkondratov.opengame.fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.activities.UserActivity;
import com.dkondratov.opengame.adapters.FriendsAdapter;
import com.dkondratov.opengame.model.MenuItem;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.AddUserToTeamRequest;
import com.dkondratov.opengame.network.FollowerRequest;
import com.dkondratov.opengame.network.FriendRequest;
import com.dkondratov.opengame.network.RemoveUserFromApplicationsRequest;
import com.dkondratov.opengame.network.RemoveUserFromFriendsRequest;
import com.dkondratov.opengame.network.RemoveUserFromTeamRequest;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.dkondratov.opengame.util.UsersData;

import java.util.ArrayList;
import java.util.List;




/**
 * A simple {@link Fragment} subclass.
 */
public class FriendsPageFragment extends Fragment implements AdapterView.OnItemClickListener, Response.ErrorListener, Response.Listener, AdapterView.OnItemLongClickListener, SwipeRefreshLayout.OnRefreshListener {

    private FriendPageFragmentCallbacks mCallbacks;
    private FriendsAdapter adapter;
    private ListView friendsList;
    private SwipeRefreshLayout swipeRefreshLayout;

    private RequestQueue queue;
    private ProgressDialog dialog;
    private ProgressBar progressBar;

    private BroadcastReceiver receiver;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mCallbacks = (FriendPageFragmentCallbacks) activity;
    }




    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeRefreshLayout.setOnRefreshListener(this);
        loadUsers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View fragmentView = inflater.inflate(R.layout.fragment_friend_pager, container, false);
        friendsList = (ListView) fragmentView.findViewById(R.id.friends_list);
        progressBar = (ProgressBar) fragmentView.findViewById(R.id.progress_bar);
        swipeRefreshLayout = (SwipeRefreshLayout) fragmentView.findViewById(R.id.swipe_refresh_layout);
        return fragmentView;
    }



    private void loadUsers() {
        adapter = new FriendsAdapter(new ArrayList<User>(), getActivity(), getActivity().getLayoutInflater());
        friendsList.setAdapter(adapter);
        progressBar.setVisibility(View.VISIBLE);

        Request<List<User>> request = null;
        switch (getArguments().getInt("position", -1)) {
            case 0:
                request = new FollowerRequest(this, this, ApplicationUserData.loadUserId(getActivity()));
                break;
            case 1:
                request = new FriendRequest(this, this, ApplicationUserData.loadUserId(getActivity()));
                break;
            case 2:
                progressBar.setVisibility(View.INVISIBLE);
                adapter = new FriendsAdapter(UsersData.getTeammates(getActivity()), getActivity(), getActivity().getLayoutInflater());
                friendsList.setAdapter(adapter);
                friendsList.setOnItemClickListener(this);
                friendsList.setOnItemLongClickListener(this);
                swipeRefreshLayout.setRefreshing(false);
                break;
        }
        if (request==null)
            return;
        request.setTag(this);
        queue.add(request);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        queue = Volley.newRequestQueue(getActivity());
        dialog = new ProgressDialog(getActivity());
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (FriendsPageFragment.this.isResumed())
                    loadUsers();
            }
        };
        getActivity().registerReceiver(receiver, new IntentFilter(FriendsFragment.ACTION_UPDATE_FRIENDS));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        //mCallbacks.onFriendItemSelected(adapter.getItem(position), position);
        User user = (User) parent.getAdapter().getItem(position);
        Intent intent = new Intent(getActivity(), UserActivity.class);
        intent.putExtra("user",user);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_left);
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        progressBar.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        Toast.makeText(getActivity(), "Ошибка загрузки пользователей",Toast.LENGTH_SHORT).show();

        switch (getArguments().getInt("position", -1)) {
            case 0:
                adapter = new FriendsAdapter(UsersData.getFollowers(getActivity()), getActivity(), getActivity().getLayoutInflater());
                break;
            case 1:
                adapter = new FriendsAdapter(UsersData.getFriends(getActivity()), getActivity(), getActivity().getLayoutInflater());
                break;
        }

        friendsList.setAdapter(adapter);
        friendsList.setOnItemClickListener(this);
        friendsList.setOnItemLongClickListener(this);
    }

    @Override
    public void onResume() {
        Log.e("on resume fragment", ""+getArguments().getInt("position", -1));
        super.onResume();
        loadUsers();
    }

    @Override
    public void onResponse(Object response) {
        progressBar.setVisibility(View.INVISIBLE);
        swipeRefreshLayout.setRefreshing(false);
        switch (getArguments().getInt("position", -1)) {
            case 0:
                UsersData.setFollowers(getActivity(), (List<User>) response);
                break;
            case 1:
                UsersData.setFriends(getActivity(), (List<User>) response);
                break;
        }
        adapter = new FriendsAdapter((List<User>) response, getActivity(), getActivity().getLayoutInflater());
        friendsList.setAdapter(adapter);
        friendsList.setOnItemClickListener(this);
        friendsList.setOnItemLongClickListener(this);
    }

    @Override
    public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
        AlertDialog dialog;
        switch (getArguments().getInt("position", -1)) {
            case 0:
                List<MenuItem> followers = new ArrayList<>();
                followers.add(new MenuItem(R.drawable.circle, "Отклонить заявку"));
                followers.add(new MenuItem(-1, "Отмена"));

                ArrayAdapter<MenuItem> adapter = new ArrayAdapter<MenuItem>(getActivity(), R.layout.menu_item, followers){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = getActivity().getLayoutInflater().inflate(R.layout.menu_item, null ,false);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        title.setText(getItem(position).getTitle());
                        return view;
                    }
                };
                dialog = new AlertDialog.Builder(getActivity())
                        .setAdapter(adapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        rejectFriendsRequest((User) parent.getAdapter().getItem(position));
                                        break;
                                    case 1:
                                        break;
                                }

                            }
                        })

                        .create();
                dialog.show();
                break;
            case 1:
                List<MenuItem> friends = new ArrayList<>();
                friends.add(new MenuItem(R.drawable.circle, "Удалить из друзей"));
                friends.add(new MenuItem(R.drawable.circle, "Добавить в команду"));
                friends.add(new MenuItem(-1, "Отмена"));

                ArrayAdapter<MenuItem> friendsAdapter = new ArrayAdapter<MenuItem>(getActivity(), R.layout.menu_item, friends){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = getActivity().getLayoutInflater().inflate(R.layout.menu_item, null ,false);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        title.setText(getItem(position).getTitle());
                        return view;
                    }
                };
                dialog = new AlertDialog.Builder(getActivity())
                        .setAdapter(friendsAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        removeFromFriends((User) parent.getAdapter().getItem(position));
                                        break;
                                    case 1:
                                        addUserToTeam((User)parent.getAdapter().getItem(position));
                                        break;
                                }

                            }
                        })

                        .create();
                dialog.show();
                /*
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle(((User)parent.getAdapter().getItem(position)).toString())
                        .setNegativeButton("Удалить из друзей",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        removeFromFriends((User)parent.getAdapter().getItem(position));
                                    }
                                }
                        )
                        .setPositiveButton("Добавить в команду",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        addUserToTeam((User)parent.getAdapter().getItem(position));
                                    }
                                }
                        )
                        .setNeutralButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialog.show();
                */
                break;
            case 2:
                List<MenuItem> team = new ArrayList<>();
                team.add(new MenuItem(R.drawable.circle, "Удалить из команды"));
                team.add(new MenuItem(-1, "Отмена"));

                ArrayAdapter<MenuItem> teamAdapter = new ArrayAdapter<MenuItem>(getActivity(), R.layout.menu_item, team){
                    @Override
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = getActivity().getLayoutInflater().inflate(R.layout.menu_item, null ,false);
                        TextView title = (TextView) view.findViewById(R.id.title);
                        title.setText(getItem(position).getTitle());
                        return view;
                    }
                };
                dialog = new AlertDialog.Builder(getActivity())
                        .setAdapter(teamAdapter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which) {
                                    case 0:
                                        removeUserFromTeam((User) parent.getAdapter().getItem(position));
                                        break;
                                    case 1:
                                        break;
                                }

                            }
                        })

                        .create();
                dialog.show();
                /*
                dialog = new AlertDialog.Builder(getActivity())
                        .setTitle("Удалить из команды?")
                        .setPositiveButton("Удалить",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        removeUserFromTeam((User) parent.getAdapter().getItem(position));
                                    }
                                }
                        )
                        .setNegativeButton("Отмена", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .create();
                dialog.show();
                */
                break;
        }
        return true;
    }

    private void rejectFriendsRequest(User user) {
        queue.cancelAll(this);
        dialog.setMessage("Отклонение заявки на добавление в друзья...");
        dialog.show();
        RemoveUserFromApplicationsRequest request = new RemoveUserFromApplicationsRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                dialog.hide();
                Toast.makeText(getActivity(), "Отклонение прошло успешно!", Toast.LENGTH_LONG).show();
                loadUsers();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Toast.makeText(getActivity(), "Ошибка отклонения!", Toast.LENGTH_LONG).show();

            }
        },
                getActivity(),
                ApplicationUserData.loadUserId(getActivity()), user.getUserId());
        request.setTag(this);
        queue.add(request);
    }

    private void removeFromFriends(final User user) {
        queue.cancelAll(this);
        dialog.setMessage("Удаление из друзей...");
        dialog.show();
        RemoveUserFromFriendsRequest request = new RemoveUserFromFriendsRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                dialog.hide();
                Toast.makeText(getActivity(), "Удаление прошло успешно!", Toast.LENGTH_LONG).show();
                List<User> team = UsersData.getTeammates(getActivity());
                team.remove(user);
                UsersData.setTeammates(getActivity(), team);
                loadUsers();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Toast.makeText(getActivity(), "Ошибка удаления!", Toast.LENGTH_LONG).show();
            }
        },
                getActivity(),
                ApplicationUserData.loadUserId(getActivity()), user.getUserId());
        request.setTag(this);
        queue.add(request);
    }

    @Override
    public void onRefresh() {
        loadUsers();
    }

    public interface FriendPageFragmentCallbacks {
        void onFriendItemSelected(User user, int position);
    }

    private void addUserToTeam(final User user) {
        queue.cancelAll(this);
        dialog.setMessage("Добавление в команду...");
        dialog.show();
        AddUserToTeamRequest request = new AddUserToTeamRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                dialog.hide();
                Toast.makeText(getActivity(), "Пользователь добавлен в команду!", Toast.LENGTH_LONG).show();
                List<User> team = UsersData.getTeammates(getActivity());
                team.add(user);
                UsersData.setTeammates(getActivity(), team);
                loadUsers();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Toast.makeText(getActivity(), "Ошибка добавления", Toast.LENGTH_LONG).show();
            }
        },
                getActivity(),
                ApplicationUserData.loadUserId(getActivity()), user.getUserId());
        request.setTag(this);
        queue.add(request);
    }

    private void removeUserFromTeam(final User user) {
        queue.cancelAll(this);
        dialog.setMessage("Удаление из команды...");
        dialog.show();
        RemoveUserFromTeamRequest request = new RemoveUserFromTeamRequest(new Response.Listener() {
            @Override
            public void onResponse(Object response) {
                dialog.hide();
                Toast.makeText(getActivity(), "Пользователь удален из команды!", Toast.LENGTH_LONG).show();
                List<User> team = UsersData.getTeammates(getActivity());
                team.remove(user);
                UsersData.setTeammates(getActivity(), team);
                loadUsers();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dialog.hide();
                Toast.makeText(getActivity(), "Ошибка удаления", Toast.LENGTH_LONG).show();
            }
        },
                getActivity(),
                ApplicationUserData.loadUserId(getActivity()), user.getUserId());
        request.setTag(this);
        queue.add(request);
    }
    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(receiver);
        queue.cancelAll(this);
        dialog.dismiss();
        super.onDestroy();
    }

}
