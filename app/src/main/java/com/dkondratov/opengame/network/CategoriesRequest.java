package com.dkondratov.opengame.network;

import android.content.Context;

import com.dkondratov.opengame.model.Category;
import com.dkondratov.opengame.network.gson_deserializers.CategoryDeserializer;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.dkondratov.opengame.util.ApplicationUserData;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Type;
import java.util.List;

import static com.dkondratov.opengame.Constants.CATEGORIES;

/**
 * Created by andrew on 09.04.2015.
 */
public class CategoriesRequest extends Request<List<Category>> {

    private Response.Listener listener;
    private Gson gson;
    private Context mContext;

    public CategoriesRequest(Response.Listener listener, Response.ErrorListener errorListener, Context mContext) {
        super(Method.GET, CATEGORIES, errorListener);
        this.listener = listener;
        this.mContext = mContext;
        this.gson = new GsonBuilder()
                .registerTypeAdapter(Category.class, new CategoryDeserializer())
                .create();
    }

    @Override
    protected Response<List<Category>> parseNetworkResponse(NetworkResponse response) {
        try {
            JSONArray json = new JSONArray(new String(response.data));
            ApplicationUserData.saveCategories(mContext, json.toString());
            Type collectionType = new TypeToken<List<Category>>(){}.getType();
            return Response.success((List<Category>) gson.fromJson(json.toString(), collectionType), null);
        } catch (JSONException e) {
            e.printStackTrace();
            return Response.error(new ParseError());
        }
    }

    @Override
    protected void deliverResponse(List<Category> response) {
        listener.onResponse(response);
    }
}
