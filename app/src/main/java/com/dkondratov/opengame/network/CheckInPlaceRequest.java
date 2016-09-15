package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;

import java.util.HashMap;
import java.util.Map;

public class CheckInPlaceRequest extends Request<Void> {

    private Response.Listener listener;
    private String userId;
    private String fieldId;
    private String lat;
    private String lon;

    public CheckInPlaceRequest(Response.Listener listener, Response.ErrorListener errorListener,
                               String userId, String fieldId, String lat, String lon) {
        super(Method.POST, Constants.CHECK_IN_REQUEST, errorListener);
        this.listener = listener;
        this.userId = userId;
        this.fieldId = fieldId;
        this.lat = lat;
        this.lon = lon;
    }

    @Override
    protected void deliverResponse(Void response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<Void> parseNetworkResponse(NetworkResponse response) {
        return Response.success(null, null);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("user_id", userId);
        params.put("field_id", fieldId);
        params.put("lat", lat);
        params.put("lon", lon);
        return params;
    }
}
