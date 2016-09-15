package com.dkondratov.opengame.network;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.model.Category;
import com.google.gson.annotations.Expose;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.dkondratov.opengame.database.HelperFactory.getHelper;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserAbout;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserBirthday;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserCity;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserImageUrl;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserName;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserPreferredSport;
import static com.dkondratov.opengame.util.ApplicationUserData.loadUserSurname;

import static com.dkondratov.opengame.Constants.SCHEME;
import static com.dkondratov.opengame.Constants.AUTHORITY;
import static com.dkondratov.opengame.Constants.API;
import static com.dkondratov.opengame.Constants.USER;
import static com.dkondratov.opengame.Constants.UPDATE;

public class UserUpdateRequest extends Request<String> {

    private Response.Listener listener;
    private static SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    public UserUpdateRequest(Response.Listener listener, Response.ErrorListener errorListener,
                             Context context) {
        super(Method.POST, createUrl(context), errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonArray = new JSONObject(new String(response.data));
            Log.e("response", "no error " + jsonArray.toString());
            if (jsonArray.optBoolean("user_id")) {
                return Response.success("OK", null);
            } else {
                return Response.success(jsonArray.optString("user_id"), null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
    
    private static String createUrl(Context context) {
        String userId = loadUserId(context);
        String imageUrl = loadUserImageUrl(context);
        String name = loadUserName(context);
        String surname = loadUserSurname(context);
        String aboutUser = loadUserAbout(context);
        String birthdayUser = getTimeIntervalSince1970(context);
        String userCity = loadUserCity(context);
        String sport = loadUserPreferredSport(context);


        String sportId = null;
        try {
            for (Category category: getHelper().getCategoryDao().getAllCategories())
                if (category.getName().trim().toLowerCase().equals(sport.trim().toLowerCase())) {
                    sportId = category.getSportId();
                    Log.e("equals", "yep");
                }

        } catch (Exception e) {
            e.printStackTrace();
        }

        Uri.Builder builder = new Uri.Builder();
        builder.scheme(SCHEME)
                .authority(AUTHORITY)
                .appendPath(API)
                .appendPath(USER)
                .appendPath(UPDATE)
                .appendQueryParameter("user_id", emptyStringIfNull(userId))
                .appendQueryParameter("name", emptyStringIfNull(name))
                .appendQueryParameter("surname", emptyStringIfNull(surname))
                .appendQueryParameter("city", emptyStringIfNull(userCity))
                .appendQueryParameter("image_url", emptyStringIfNull(imageUrl))
                .appendQueryParameter("birth", emptyStringIfNull(birthdayUser))
                .appendQueryParameter("sport_ids", emptyStringIfNull(sportId))
                .appendQueryParameter("description", emptyStringIfNull(aboutUser));

        String res = builder.build().toString();
        return res;
    }

    private static String getTimeIntervalSince1970(Context context) {
        String birthdayUser = loadUserBirthday(context);

        Date date = new Date();
        try{
            date = dateFormat.parse(birthdayUser);
        } catch (Exception e) { }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return String.valueOf(c.getTimeInMillis() / 1000L);
    }

    private static String emptyStringIfNull(String s) {
        if (s == null) {
            return emptyString;
        }
        return s;
    }

    private static final String emptyString = "";
}
