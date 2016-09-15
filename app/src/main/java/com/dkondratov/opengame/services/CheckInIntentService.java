package com.dkondratov.opengame.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.os.Handler;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.dkondratov.opengame.network.SynchronizeCheckInPlaceRequest;
import com.dkondratov.opengame.util.ApplicationUserData;


public class CheckInIntentService extends IntentService implements Runnable, Response.ErrorListener, Response.Listener {

    private static final String ACTION_START_SYNCHRONIZE = "com.dkondratov.opengame.services.action.starts_synchronize";
    private static final String ACTION_STOP_SYNCHRONIZE = "com.dkondratov.opengame.services.action.stop_synchronize";

    private static final String ARG_FIELD = "field_id";

    private static final int TIMEOUT = 10*60*1000;

    private RequestQueue queue;
    private Handler handler;
    public static String fieldId;

    private GPSTracker tracker;

    public static void startSynchronizeCheckInPlace(Context context, String fieldId) {
        Intent intent = new Intent(context, CheckInIntentService.class);
        intent.setAction(ACTION_START_SYNCHRONIZE);
        intent.putExtra(ARG_FIELD, fieldId);
        context.startService(intent);
    }

    public static void stopSynchronizeCheckInPlace(Context context, String fieldId) {
        Intent intent = new Intent(context, CheckInIntentService.class);
        intent.setAction(ACTION_STOP_SYNCHRONIZE);
        intent.putExtra(ARG_FIELD, fieldId);
        context.startService(intent);
    }



    public CheckInIntentService() {
        super("CheckInIntentService");
    }


    @Override
    public void onCreate() {
        super.onCreate();
        queue = Volley.newRequestQueue(getApplicationContext());
        tracker = new GPSTracker(getApplicationContext());
        handler = new Handler();
    }


    @Override
    protected synchronized void onHandleIntent(Intent intent) {
        if (intent.getAction().equals(ACTION_START_SYNCHRONIZE)) {
            this.fieldId = intent.getStringExtra(ARG_FIELD);
            handler.postDelayed(this, TIMEOUT);
        }
        if (intent.getAction().equals(ACTION_STOP_SYNCHRONIZE)) {
            this.fieldId = intent.getStringExtra(ARG_FIELD);
            queue.cancelAll(this);
            handler.removeCallbacksAndMessages(this);
            fieldId = null;
            stopSelf();
        }
    }

    @Override
    public void run() {
        queue.cancelAll(this);
        GPSTracker gpsTracker = new GPSTracker(this);
        String lat = "0";
        String lon = "0";
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            lat = String.valueOf(gpsTracker.latitude);
            lon = String.valueOf(gpsTracker.longitude);
        }
        SynchronizeCheckInPlaceRequest request = new SynchronizeCheckInPlaceRequest(this, this, getApplicationContext(),
                ApplicationUserData.loadUserId(getApplicationContext()), fieldId, lat, lon);
        request.setTag(this);
        queue.add(request);
    }

    @Override
    public synchronized void onErrorResponse(VolleyError error) {
        queue.cancelAll(this);
        handler.removeCallbacksAndMessages(null);
        fieldId = null;
        stopSelf();
    }

    @Override
    public synchronized void onResponse(Object response) {
        handler.postDelayed(this, TIMEOUT);
    }
}
