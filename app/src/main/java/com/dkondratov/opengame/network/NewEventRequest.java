package com.dkondratov.opengame.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.gson_deserializers.EventDeserializer;
import com.dkondratov.opengame.network.gson_deserializers.FieldDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

public class NewEventRequest extends Request<Event> {

    private Response.Listener listener;
    private String fieldID;
    private String title;
    private String description;
    private long start;
    private Gson gson;
    private long end;
    private boolean friends;
    private boolean team;
    private boolean favo;
    private Context mContext;

    public NewEventRequest(Response.Listener listener, Response.ErrorListener errorListener,
                           Context context, String fieldID, String title, String description, long start, long end,
                           boolean friends, boolean team, boolean favo) {
        super(Method.POST, Constants.CREATE_EVENT, errorListener);
        this.listener = listener;
        this.fieldID = fieldID;
        this.title = title;
        this.description = description;
        this.start = start;
        this.end = end;
        Log.e("time start", "" + start);
        Log.e("time end" , "" + end);
        this.friends = friends;
        this.team = team;
        this.favo = favo;
        mContext = context;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Event.class, new EventDeserializer())
                .create();
    }

    @Override
    protected void deliverResponse(Event response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<Event> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonArray = new JSONObject(new String(response.data));
            Log.e("response", jsonArray.toString());
            Type collectionType = new TypeToken<Event>() {}.getType();
            Event event = (Event) gson.fromJson(jsonArray.toString(), collectionType);
            event.setStart(event.getStart() * 1000);
            event.setStart(event.getEnd() * 1000);
            return Response.success(event, null);

        } catch (Exception ex) {
            ex.printStackTrace();
            return Response.success(null, null);
        }
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        String userId = loadUserId(mContext);
        params.put("user_id", userId);
        params.put("field_id", fieldID);
        params.put("title", title);
        params.put("description", description);
        params.put("start", "" + start / 1000);
        params.put("end", "" + end / 1000);
        Log.e("time start", "" + start);
        Log.e("time end", "" + end);
        params.put("to_friends", "" + friends);
        params.put("to_team", "" + team);
        params.put("to_favourites", "" + favo);
        return params;
    }
}
