package com.dkondratov.opengame.network;

import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.Constants;
import com.dkondratov.opengame.model.AutocompleteAddress;
import com.dkondratov.opengame.model.AutocompleteCity;
import com.dkondratov.opengame.model.User;
import com.dkondratov.opengame.network.gson_deserializers.UserDeserializer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by andrew on 27.04.2015.
 */
public class SearchAddressRequest extends Request<List<AutocompleteAddress>> {

    private Response.Listener listener;
    private Gson gson;

    public SearchAddressRequest(Response.Listener listener, Response.ErrorListener errorListener, String city, String street) {
        super(Method.GET, String.format(Constants.BY_STREET, URLEncoder.encode(city), URLEncoder.encode(street)), errorListener);
        Log.e("url", String.format(Constants.BY_STREET, URLEncoder.encode(city),URLEncoder.encode(street)));
        this.listener = listener;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(User.class, new UserDeserializer())
                .create();
    }

    @Override
    protected Response<List<AutocompleteAddress>> parseNetworkResponse(NetworkResponse response) {
        try {
            Log.e("autocomplete response",new String(response.data));
            JSONArray jsonArray = new JSONArray(new String(response.data));
            Type collectionType = new TypeToken<List<AutocompleteAddress>>() {
            }.getType();
            return Response.success((List<AutocompleteAddress>) gson.fromJson(jsonArray.toString(), collectionType), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<AutocompleteAddress> response) {
        listener.onResponse(response);
    }
}
