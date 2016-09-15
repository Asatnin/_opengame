package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.parse.ParsePush;

import java.util.HashMap;
import java.util.Map;

public class RemoveUserFromFriendsRequest extends Request<Void> {

    private Response.Listener listener;
    private String userId;
    private String applicantId;
    private Context context;

    public RemoveUserFromFriendsRequest(Response.Listener listener, Response.ErrorListener errorListener,
                                        Context context, String userId, String applicantId) {
        super(Method.POST, Constants.REMOVE_FROM_FRIENDS, errorListener);
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
        params.put("friend_id", applicantId);
        return params;
    }
}
