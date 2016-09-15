package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;

import java.util.HashMap;
import java.util.Map;

public class AddUserToTeamRequest extends Request<Void> {

    private Response.Listener listener;
    private String userId;
    private String applicantId;
    private Context context;

    public AddUserToTeamRequest(Response.Listener listener, Response.ErrorListener errorListener,
                                Context context, String userId, String applicantId) {
        super(Method.POST, Constants.ADD_TO_USER_TO_TEAM, errorListener);
        this.listener = listener;
        this.userId = userId;
        this.applicantId = applicantId;
        this.context = context;
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
        params.put("team_member_id", applicantId);
        return params;
    }
}
