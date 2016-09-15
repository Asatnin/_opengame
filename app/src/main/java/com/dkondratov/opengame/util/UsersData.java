package com.dkondratov.opengame.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.dkondratov.opengame.model.User;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.parse.ParsePush;

import java.util.ArrayList;
import java.util.List;

public class UsersData {


    private static String USERS = "users_data.emp";
    private static String FRIENDS = "friends";
    private static String FOLLOWERS = "followers";
    private static String TEAMMATES = "teammates";

    private static SharedPreferences mSettings;

    private static SharedPreferences getInstance(Context context) {
        if (mSettings == null) {
            mSettings = context.getSharedPreferences(USERS, Context.MODE_PRIVATE);
        }
        return mSettings;
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getInstance(context).edit();
    }

    public static ArrayList<User> getFriends(Context context) {
        Gson gson = new Gson();
        String json = getInstance(context).getString(FRIENDS, "");
        if (json == "") {
            return new ArrayList<User>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<User>>() {
        }.getType());
    }

    public static void setFriends(Context context, List<User> emails) {
        ArrayList<User> friends = getFriends(context);
        for (int i = 0; i < friends.size(); i ++) {
            ParsePush.unsubscribeInBackground("friend_android_" + friends.get(i).getUserId());
        }
        for (int i = 0; i < emails.size(); i ++) {
            ParsePush.subscribeInBackground("friend_android_" + emails.get(i).getUserId());
        }
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(FRIENDS);
        Gson gson = new Gson();
        String json = gson.toJson(emails);
        editor.putString(FRIENDS, json);
        editor.commit();
    }

    public static ArrayList<User> getFollowers(Context context) {
        Gson gson = new Gson();
        String json = getInstance(context).getString(FOLLOWERS, "");
        if (json == "") {
            return new ArrayList<User>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<User>>() {
        }.getType());
    }

    public static void setFollowers(Context context, List<User> emails) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(FOLLOWERS);
        Gson gson = new Gson();
        String json = gson.toJson(emails);
        editor.putString(FOLLOWERS, json);
        editor.commit();
    }


    public static ArrayList<User> getTeammates(Context context) {
        Gson gson = new Gson();
        String json = getInstance(context).getString(TEAMMATES, "");
        if (json == "") {
            return new ArrayList<User>();
        }
        return gson.fromJson(json, new TypeToken<ArrayList<User>>() {
        }.getType());
    }

    public static void setTeammates(Context context, List<User> emails) {
        ArrayList<User> friends = getTeammates(context);
        for (int i = 0; i < friends.size(); i ++) {
            ParsePush.unsubscribeInBackground("teamate_android_" + friends.get(i).getUserId());
        }
        for (int i = 0; i < emails.size(); i ++) {
            ParsePush.subscribeInBackground("teamate_android_" + emails.get(i).getUserId());
        }
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(TEAMMATES);
        Gson gson = new Gson();
        String json = gson.toJson(emails);
        editor.putString(TEAMMATES, json);
        editor.commit();
    }
}
