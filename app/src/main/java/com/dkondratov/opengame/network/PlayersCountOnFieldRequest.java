package com.dkondratov.opengame.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.FieldDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.dkondratov.opengame.Constants.FIELDS_BY_ID;

/**
 * Created by andrew on 12.04.2015.
 */
public class PlayersCountOnFieldRequest extends Request<List<User>> {

    private Response.Listener listener;
    private Gson gson;

    public PlayersCountOnFieldRequest(Response.Listener listener, Response.ErrorListener errorListener, String fieldId) {
        super(Method.GET, String.format(Constants.FIELD_ID, fieldId), errorListener);
        Log.e("request",  String.format(Constants.FIELD_ID, fieldId));
        this.listener = listener;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Field.class, new FieldDeserializer())
                .create();
    }

    @Override
    protected Response<List<User>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONArray jsonArray = new JSONObject(new String(response.data)).optJSONArray("players");
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
