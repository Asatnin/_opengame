package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;

import java.util.HashMap;
import java.util.Map;

import static com.dkondratov.opengame.util.ApplicationUserData.loadUserId;

public class FieldEventsRequest extends Request<Void> {

    private Response.Listener listener;
    private String fieldID;
    private Context mContext;

    public FieldEventsRequest(Response.Listener listener, Response.ErrorListener errorListener,
                              Context context, String fieldID) {
        super(Method.POST, Constants.CREATE_EVENT, errorListener);
        this.listener = listener;
        this.fieldID = fieldID;
        mContext = context;
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
        String userId = loadUserId(mContext);
        params.put("user_id", userId);
        params.put("field_id", fieldID);
        return params;
    }
}
