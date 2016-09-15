package com.dkondratov.opengame.network;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.gson_deserializers.FieldDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

/**
 * Created by andrew on 12.04.2015.
 */
public class EventsForFieldRequest extends Request<List<Event>> {

    private Response.Listener listener;
    private Gson gson;

    public EventsForFieldRequest(Response.Listener listener, Response.ErrorListener errorListener, String fieldID, Context mContext) {
        super(Method.GET, String.format(Constants.EVENT_LIST, loadUserId(mContext), fieldID), errorListener);
        this.listener = listener;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Field.class, new FieldDeserializer())
                .create();
    }

    @Override
    protected Response<List<Event>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            Type collectionType = new TypeToken<List<Field>>(){}.getType();
            List<Event> events = new ArrayList<>();
            List<Event> events1 = (List<Event>) gson.fromJson(jsonObject.optJSONArray("friend_events").toString(), collectionType);
            for (Event event : events1) {
                event.setMy(1);
                String creator = "";
                if (!TextUtils.isEmpty(event.getCreator().getName())) {
                    creator += event.getCreator().getName() + " ";
                }
                if (!TextUtils.isEmpty(event.getCreator().getSurname())) {
                    creator += event.getCreator().getSurname();
                }
                event.setCreatorString(creator);
            }
            events.addAll(events1);
            List<Event> events2 = (List<Event>) gson.fromJson(jsonObject.optJSONArray("other_events").toString(), collectionType);
            for (Event event : events2) {
                event.setMy(1);
                String creator = "";
                if (!TextUtils.isEmpty(event.getCreator().getName())) {
                    creator += event.getCreator().getName() + " ";
                }
                if (!TextUtils.isEmpty(event.getCreator().getSurname())) {
                    creator += event.getCreator().getSurname();
                }
                event.setCreatorString(creator);
            }
            events.addAll(events2);
            List<Event> events3 = (List<Event>) gson.fromJson(jsonObject.optJSONArray("user_events").toString(), collectionType);
            for (Event event : events3) {
                event.setMy(1);
                String creator = "";
                if (!TextUtils.isEmpty(event.getCreator().getName())) {
                    creator += event.getCreator().getName() + " ";
                }
                if (!TextUtils.isEmpty(event.getCreator().getSurname())) {
                    creator += event.getCreator().getSurname();
                }
                event.setCreatorString(creator);
            }
            events.addAll(events3);
            return Response.success(events, null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<Event> response) {
        listener.onResponse(response);
    }

}
