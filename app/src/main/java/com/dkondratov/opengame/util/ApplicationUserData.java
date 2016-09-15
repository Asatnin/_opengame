package com.dkondratov.opengame.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.dkondratov.opengame.model.Cover;
import com.parse.ParsePush;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ApplicationUserData {

    private final static String USER_INFO_PREFS = "user_info";
    private final static String USER_ID = "user_id";
    private final static String USER_NAME = "user_name";
    private final static String USER_SURNAME = "user_surname";
    private final static String USER_ABOUT = "user_about";
    private final static String USER_BIRTHDAY = "user_birthday";
    private final static String NEW_USER_NUMBER = "new_user_number";
    private final static String INVITATION_NUMBER = "invitation_number";
    private final static String USER_CITY = "user_city";
    private final static String COVERS = "covers";
    private final static String ADDED_FIELD = "new_field";
    private final static String COVERS_JSON = "covers_json";
    private final static String CATEGORIES = "categories";
    private final static String CATEGORIES_JSON = "categories_json";
    private final static String USER_PREFERRED_SPORT = "user_preferred_sport";
    private final static String USER_IMAGE_URL = "user_image_url";
    private static final String FRIENDS_NOTIFICATIONS = "friends_notifications";
    private static final String PLACES_NOTIFICATIONS = "places_notifications";

    private static SharedPreferences settings;

    private static SharedPreferences getInstance(Context context) {
        if (settings == null) {
            settings = context.getSharedPreferences(USER_INFO_PREFS, Context.MODE_PRIVATE);
        }
        return settings;
    }

    public static SharedPreferences.Editor getEditor(Context context) {
        return getInstance(context).edit();
    }

    public static void clear(Context context) {
        getEditor(context).clear().apply();
    }

    public static void saveUserId(Context context, String userId) {
        Log.e("userID", userId);
        ParsePush.subscribeInBackground("user_android_" + userId);
        getEditor(context).putString(USER_ID, userId).apply();
    }

    public static String loadUserId(Context context) {
        return getInstance(context).getString(USER_ID, "");
    }

    public static void saveUserName(Context context, String userName) {
        getEditor(context).putString(USER_NAME, userName).apply();
    }

    public static String loadUserName(Context context) {
        return getInstance(context).getString(USER_NAME, "");
    }

    public static void saveUserSurname(Context context, String userSurname) {
        getEditor(context).putString(USER_SURNAME, userSurname).apply();
    }

    public static String loadUserSurname(Context context) {
        return getInstance(context).getString(USER_SURNAME, "");
    }

    public static void saveUserAbout(Context context, String userAbout) {
        getEditor(context).putString(USER_ABOUT, userAbout).apply();
    }

    public static String loadUserAbout(Context context) {
        return getInstance(context).getString(USER_ABOUT, null);
    }

    public static void saveUserBirthday(Context context, String birthday) {
        getEditor(context).putString(USER_BIRTHDAY, birthday).apply();
    }

    public static String loadUserBirthday(Context context) {
        return getInstance(context).getString(USER_BIRTHDAY, null);
    }

    public static void saveUserCity(Context context, String city) {
        getEditor(context).putString(USER_CITY, city).apply();
    }

    public static String loadUserCity(Context context) {
        return getInstance(context).getString(USER_CITY, "");
    }

    public static void saveUserPreferredSport(Context context, String sport) {
        getEditor(context).putString(USER_PREFERRED_SPORT, sport).apply();
    }

    public static String loadUserPreferredSport(Context context) {
        return getInstance(context).getString(USER_PREFERRED_SPORT, null);
    }

    public static void saveUserImageUrl(Context context, String url) {
        getEditor(context).putString(USER_IMAGE_URL, url).apply();
    }

    public static String loadUserImageUrl(Context context) {
        return getInstance(context).getString(USER_IMAGE_URL, null);
    }


    public static boolean getFriendsNotificationsFlag(Context context) {
        return getInstance(context).getBoolean(FRIENDS_NOTIFICATIONS, true);
    }

    public static void setFriendsNotificationsFlag(Context context, Boolean notificationsEnabled) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(FRIENDS_NOTIFICATIONS);
        editor.putBoolean(FRIENDS_NOTIFICATIONS, notificationsEnabled);
        editor.commit();
    }

    public static boolean getPlacesNotificationsFlag(Context context) {
        return getInstance(context).getBoolean(PLACES_NOTIFICATIONS, true);
    }

    public static void setPlacesNotificationsFlag(Context context, Boolean notificationsEnabled) {
        SharedPreferences.Editor editor = getEditor(context);
        editor.remove(PLACES_NOTIFICATIONS);
        editor.putBoolean(PLACES_NOTIFICATIONS, notificationsEnabled);
        editor.commit();
    }

    public static void setUserNumber(Context context, int userNumber) {
        getEditor(context).putInt(NEW_USER_NUMBER, userNumber).apply();
    }

    public static int getUserNumber(Context context) {
        return getInstance(context).getInt(NEW_USER_NUMBER, 0);
    }

    public static void setInvitationNumber(Context context, int invitationNumber) {
        getEditor(context).putInt(INVITATION_NUMBER, invitationNumber).apply();
    }

    public static int getInvitationNumber(Context context) {
        return getInstance(context).getInt(INVITATION_NUMBER, 0);
    }


    public static void saveCovers(Context context, Set<String> covers) {
        getEditor(context).putStringSet(COVERS + "!", covers).apply();
    }

    public static Set<String> loadCovers(Context context) {
        return getInstance(context).getStringSet(COVERS + "!", null);
    }

    public static void saveCategories(Context context, String categories) {
        getEditor(context).putString(CATEGORIES, categories).apply();
    }

    public static String loadCategories(Context context) {
        return getInstance(context).getString(CATEGORIES, "");
    }

    public static void saveCoversJSON(Context context, String covers) {
        getEditor(context).putString(COVERS, covers).apply();
    }

    public static List<Cover> loadCoversJSON(Context context) {
        try {
            List<Cover> covers = new ArrayList<>();
            JSONArray array = new JSONArray(getInstance(context).getString(COVERS, ""));
            for (int i=0; i<array.length(); i++)
                covers.add(new Cover(array.getJSONObject(i).optString("id").toString(), array.getJSONObject(i).optString("name").toString()));
            return covers;
        } catch (JSONException ex) {
            ex.printStackTrace();
            return new ArrayList<>();
        }
    }

}
