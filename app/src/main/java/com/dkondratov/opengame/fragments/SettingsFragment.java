package com.dkondratov.opengame.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import com.dkondratov.opengame.R;
import com.dkondratov.opengame.database.DatabaseManager;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.UserDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.dkondratov.opengame.util.UsersData;
import com.parse.ParsePush;

import java.util.ArrayList;
import java.util.List;

public class SettingsFragment extends Fragment {

    private Switch friendsNotifications;
    private Switch placesNotifications;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_settings, container, false);
        placesNotifications = (Switch) rootView.findViewById(R.id.place_notifications);
        friendsNotifications = (Switch) rootView.findViewById(R.id.friends_notification);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        placesNotifications.setChecked(ApplicationUserData.getPlacesNotificationsFlag(getActivity()));
        friendsNotifications.setChecked(ApplicationUserData.getFriendsNotificationsFlag(getActivity()));

        placesNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ApplicationUserData.setPlacesNotificationsFlag(getActivity(), isChecked);
                final boolean flag = isChecked;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            List<Field> fieldList = DatabaseManager.getFavoriteFields();
                            if (flag) {
                                for (int i = 0; i < fieldList.size(); i++) {
                                    ParsePush.subscribeInBackground("field_android_" + fieldList.get(i).getFieldId());
                                }
                            } else {
                                for (int i = 0; i < fieldList.size(); i++) {
                                    ParsePush.unsubscribeInBackground("field_android_" + fieldList.get(i).getFieldId());
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

            }
        });
        friendsNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                ApplicationUserData.setFriendsNotificationsFlag(getActivity(), isChecked);
                final boolean flag = isChecked;
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            if (flag) {
                                ArrayList<User> friends = UsersData.getTeammates(getActivity());
                                for (int i = 0; i < friends.size(); i ++) {
                                    ParsePush.subscribeInBackground("teamate_android_" + friends.get(i).getUserId());
                                }
                                ArrayList<User> friends1 = UsersData.getFriends(getActivity());
                                for (int i = 0; i < friends1.size(); i ++) {
                                    ParsePush.subscribeInBackground("friend_android_" + friends1.get(i).getUserId());
                                }
                            } else {
                                ArrayList<User> friends = UsersData.getTeammates(getActivity());
                                for (int i = 0; i < friends.size(); i ++) {
                                    ParsePush.unsubscribeInBackground("teamate_android_" + friends.get(i).getUserId());
                                }
                                ArrayList<User> friends1 = UsersData.getFriends(getActivity());
                                for (int i = 0; i < friends1.size(); i ++) {
                                    ParsePush.unsubscribeInBackground("friend_android_" + friends1.get(i).getUserId());
                                }
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                }).start();

            }
        });
    }
}
