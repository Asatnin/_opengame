package com.dkondratov.opengame.network;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by andrew on 09.04.2015.
 */
public class NetworkUtilities {

    private static NetworkUtilities instance;
    private RequestQueue requestQueue;
    private static Context context;

    private NetworkUtilities(Context ctx) {
        context = ctx;
        requestQueue = getRequestQueue();
    }

    public static synchronized NetworkUtilities networkUtilities(Context ctx) {
        if (instance == null) {
            instance = new NetworkUtilities(ctx);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(context.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> request) {
        getRequestQueue().add(request);
    }
}
