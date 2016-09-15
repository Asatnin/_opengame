package com.dkondratov.opengame.network;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;

import org.json.JSONException;
import org.json.JSONObject;

import static com.dkondratov.opengame.Constants.CREATE_USER;

public class UserRegisterRequest extends Request<String> {

    private Response.Listener listener;

    public UserRegisterRequest(Response.Listener listener, Response.ErrorListener errorListener) {
        super(Method.POST, CREATE_USER, errorListener);
        this.listener = listener;
    }

    @Override
    protected void deliverResponse(String response) {
        listener.onResponse(response);
    }

    @Override
    protected Response<String> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            return Response.success(jsonObject.getString("user_id"), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }
}
