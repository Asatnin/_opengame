package com.dkondratov.opengame.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.UserDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.dkondratov.opengame.Constants.FRIENDS;
import static com.dkondratov.opengame.Constants.USER_INFO;

/**
 * Created by andrew on 27.04.2015.
 */
public class UserInfoRequest extends Request<JSONObject> {

    private Response.Listener listener;
    private Gson gson;
    private Context mContext;

    public UserInfoRequest(Response.Listener listener, Response.ErrorListener errorListener, Context mContext) {
        super(Method.GET, String.format(USER_INFO, ApplicationUserData.loadUserId(mContext)), errorListener);
        this.listener = listener;
        this.mContext = mContext;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));

            //ApplicationUserData.saveUserName(mContext, jsonObject.optString("name"));
            //ApplicationUserData.saveUserSurname(mContext, jsonObject.optString("surname"));
           // ApplicationUserData.saveUserId(mContext, jsonObject.optString("user_id"));

            ApplicationUserData.setUserNumber(mContext, jsonObject.optInt("new_user_number"));
            ApplicationUserData.setInvitationNumber(mContext, jsonObject.optInt("invitation_number"));

           // ApplicationUserData.saveUserBirthday(mContext, jsonObject.optString("birth"));
           // ApplicationUserData.saveUserAbout(mContext, jsonObject.optString("description"));

           // ApplicationUserData.saveUserCity(mContext, jsonObject.optString("city"));
          //  ApplicationUserData.saveUserCity(mContext, jsonObject.optString("city"));
          //  ApplicationUserData.saveUserImageUrl(mContext, jsonObject.optString("image_url"));
            //if (jsonObject.optJSONArray("sports").length() > 0) {
            //    ApplicationUserData.saveUserPreferredSport(mContext, jsonObject.optJSONArray("sports").optString(0));
           // }
            return Response.success(jsonObject, null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }
}
