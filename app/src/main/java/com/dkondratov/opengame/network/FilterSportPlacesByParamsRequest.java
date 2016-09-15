package com.dkondratov.opengame.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Field;
import com.dkondratov.opengame.network.gson_deserializers.FieldDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by andrew on 12.04.2015.
 */
public class FilterSportPlacesByParamsRequest extends Request<List<Field>> {

    private Response.Listener listener;
    private Gson gson;

    public FilterSportPlacesByParamsRequest(Response.Listener listener, Response.ErrorListener errorListener, String cover, String sportId) {
        super(Method.GET, String.format(Constants.PARAMS, cover, sportId), errorListener);
        this.listener = listener;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Field.class, new FieldDeserializer())
                .create();
    }

    @Override
    protected Response<List<Field>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            JSONArray jsonArray = jsonObject.optJSONArray("fields");
            Type collectionType = new TypeToken<List<Field>>(){}.getType();
            return Response.success((List<Field>) gson.fromJson(jsonArray.toString(), collectionType), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<Field> response) {
        listener.onResponse(response);
    }
}
