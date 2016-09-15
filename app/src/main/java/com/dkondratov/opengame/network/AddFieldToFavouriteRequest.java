package com.dkondratov.opengame.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dkondratov.opengame.Constants.FIELDS_BY_ID;

/**
 * Created by andrew on 12.04.2015.
 */
public class AddFieldToFavouriteRequest extends Request<String> {


    private Response.Listener listener;
    private Gson gson;
    private Context mContext;
    private String fieldID;

    public AddFieldToFavouriteRequest(Response.Listener listener, Response.ErrorListener errorListener, Context mContext, String fieldID) {
        super(Method.POST, String.format(Constants.ADD_FAVORITE), errorListener);
        this.listener = listener;
        this.mContext = mContext;
        this.fieldID = fieldID;
        this.gson = new GsonBuilder()
                .excludeFieldsWithoutExposeAnnotation()
                .registerTypeAdapter(Field.class, new FieldDeserializer())
                .create();
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonArray = new JSONObject(new String(response.data));
            Log.e("response", "error: " + jsonArray.toString());
            if (jsonArray.optBoolean("success")) {
                return Response.success("OK", null);
            } else {
                return Response.success(jsonArray.optString("description"), null);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", ApplicationUserData.loadUserId(mContext));
        params.put("field_id", fieldID);
        return params;
    }
}
