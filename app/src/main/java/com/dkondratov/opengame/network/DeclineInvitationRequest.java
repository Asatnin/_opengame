package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.UserDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static com.dkondratov.opengame.Constants.USER_INFO;

/**
 * Created by andrew on 27.04.2015.
 */
public class DeclineInvitationRequest extends Request<JSONObject> {

    private Response.Listener listener;
    private Gson gson;
    private Context mContext;
    private String invitationID;

    public DeclineInvitationRequest(Response.Listener listener, Response.ErrorListener errorListener, String invitationID, Context mContext) {
        super(Method.GET, String.format(Constants.DECLINE_INVITATION), errorListener);
        this.listener = listener;
        this.mContext = mContext;
        this.invitationID = invitationID;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            return Response.success(jsonObject, null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        listener.onResponse(response);
    }

    @Override
    protected Map<String, String> getParams() throws AuthFailureError {
        Map<String, String> params = new HashMap<String, String>();
        params.put("invitation_id", invitationID);
        return params;
    }
}
