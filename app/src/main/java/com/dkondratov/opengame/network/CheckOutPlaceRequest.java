package com.dkondratov.opengame.network;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;

import java.util.HashMap;
import java.util.Map;

public class CheckOutPlaceRequest extends Request<Void> {

    private Response.Listener listener;
    private String userId;
    private String fieldId;

    public CheckOutPlaceRequest(Response.Listener listener, Response.ErrorListener errorListener,
                                String userId, String fieldId) {
        super(Method.POST, Constants.CHECK_OUT_REQUEST, errorListener);
        this.listener = listener;
        this.userId = userId;
        this.fieldId = fieldId;
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
        return params;
    }
}
