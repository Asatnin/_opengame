package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.gson_deserializers.FieldDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by andrew on 12.04.2015.
 */
public class CreateFieldRequest extends Request<Field> {


    private Response.Listener listener;
    private Gson gson;
    private Map<String, String> params;

    public CreateFieldRequest(Response.Listener listener, Response.ErrorListener errorListener, Map<String, String> params) {
        super(Method.POST, Constants.CREATE_FIELD_REQUEST, errorListener);
        this.listener = listener;
        this.params = params;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Field.class, new FieldDeserializer())
                .create();
    }

    @Override
    protected Response<Field> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonArray = new JSONObject(new String(response.data));
            Type collectionType = new TypeToken<Field>(){}.getType();
            return Response.success((Field) gson.fromJson(jsonArray.toString(), collectionType), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(Field response) {
        listener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        return params;
    }
}
