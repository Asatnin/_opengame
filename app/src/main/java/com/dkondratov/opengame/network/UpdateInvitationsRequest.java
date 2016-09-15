package com.dkondratov.opengame.network;

import android.content.Context;
import android.text.TextUtils;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.Event;
import com.dkondratov.opengame.model.Invitation;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.UserDeserializer;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

import static com.dkondratov.opengame.Constants.USER_INFO;

/**
 * Created by andrew on 27.04.2015.
 */
public class UpdateInvitationsRequest extends Request<List<Invitation>> {

    private Response.Listener listener;
    private Gson gson;
    private Context mContext;

    public UpdateInvitationsRequest(Response.Listener listener, Response.ErrorListener errorListener, Context mContext) {
        super(Method.GET, String.format(Constants.NEW_INVITATION, ApplicationUserData.loadUserId(mContext)), errorListener);
        this.listener = listener;
        this.mContext = mContext;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Override
    protected Response<List<Invitation>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONObject jsonObject = new JSONObject(new String(response.data));
            Type collectionType = new TypeToken<List<Invitation>>(){}.getType();
            List<Invitation> invitations = (List<Invitation>) gson.fromJson(jsonObject.optString("invitations"), collectionType);
            for (Invitation invitation : invitations) {
                Event event = invitation.getEvent();
                String creator = "";
                if (!TextUtils.isEmpty(event.getCreator().getName())) {
                    creator += event.getCreator().getName() + " ";
                }
                if (!TextUtils.isEmpty(event.getCreator().getSurname())) {
                    creator += event.getCreator().getSurname();
                }
                event.setCreatorString(creator);
            }
            return Response.success(invitations, null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<Invitation> response) {
        listener.onResponse(response);
    }
}
