package com.reactlibrary;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.util.Log;

import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

public class SyncService extends IntentService {
    public static final String ACTION_SYNC = "com.reactlibrary.action.SYNC";
    public static final String EXTRA_CONNECTION = "com.reactlibrary.extra.CONNECTION";
    private final String TAG = "SyncService";
    private Gson gson;
    public SyncService() {
        super("SyncService");
        gson = new Gson();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SYNC.equals(action)) {
                final String connection = intent.getStringExtra(EXTRA_CONNECTION);
                HandlerSyncData(connection);
            }
        }
    }

    private void HandlerSyncData(String connection){
        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, connection,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                        NotificationModel notify = gson.fromJson(response, NotificationModel.class);
                        
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                NetworkResponse response = error.networkResponse;
                String errorMsg = "";
                if(response != null && response.data != null){
                    String errorString = new String(response.data);
                    Log.e(TAG, errorString);
                }
            }
        });
        queue.add(stringRequest);
    }
}
