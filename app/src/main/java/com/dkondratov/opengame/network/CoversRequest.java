package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.gson_deserializers.FieldDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.dkondratov.opengame.Constants.FIELDS_BY_ID;

/**
 * Created by andrew on 12.04.2015.
 */
public class CoversRequest extends Request<List<String>> {

    private Response.Listener listener;
    private Context mContext;
    private Gson gson;

    public CoversRequest(Response.Listener listener, Response.ErrorListener errorListener, Context mContext) {
        super(Method.GET, String.format(Constants.COVERS), errorListener);
        this.listener = listener;
        this.mContext = mContext;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Field.class, new FieldDeserializer())
                .create();
    }

    @Override
    protected Response<List<String>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONArray jsonArray = (new JSONObject(new String(response.data))).optJSONArray("covers");
            Set<String> covers = new HashSet<>();
            for (int i = 0; i < jsonArray.length(); i ++) {
                covers.add(jsonArray.optJSONObject(i).optString("name"));
            }
            ApplicationUserData.saveCovers(mContext, covers);
            ApplicationUserData.saveCoversJSON(mContext, jsonArray.toString());
            return Response.success(null, null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<String> response) {
        listener.onResponse(response);
    }
}
