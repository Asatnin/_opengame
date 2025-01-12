package com.dkondratov.opengame.network;

import android.util.Log;

import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.UserDeserializer;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.dkondratov.opengame.Constants.FRIENDS;

/**
 * Created by andrew on 27.04.2015.
 */
public class FriendRequest extends Request<List<User>> {

    private Response.Listener listener;
    private Gson gson;

    public FriendRequest(Response.Listener listener, Response.ErrorListener errorListener, String userId) {
        super(Method.GET, String.format(FRIENDS, userId), errorListener);
        this.listener = listener;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Override
    protected Response<List<User>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            JSONArray jsonArray = jsonObject.getJSONArray("friends");
            Log.e("friends responce", jsonObject.toString());
            Type collectionType = new TypeToken<List<User>>(){}.getType();
            return Response.success((List<User>) gson.fromJson(jsonArray.toString(), collectionType), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<User> response) {
        listener.onResponse(response);
    }
}
