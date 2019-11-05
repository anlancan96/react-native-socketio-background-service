package com.reactlibrary;

import android.content.Context;
import android.content.Intent;
import android.provider.SyncStateContract;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

public class SyncDataWorker extends Worker {
    private boolean isWorkerSuccess;
    private final String TAG = "SyncDataWorker";
    private Context context;
    public static final String WORKER_RESULT_KEY = "Data";
    public static final String WORKER_URL = "URL";
    public SyncDataWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String url = getInputData().getString(WORKER_URL);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        isWorkerSuccess = true;
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                isWorkerSuccess = false;
            }
        });
        queue.add(stringRequest);
        Intent intent = new Intent("local.incomingcall");
        intent.putExtra("message", "This is my message!");
        LocalBroadcastManager.getInstance(this.context).sendBroadcast(intent);
        if (isWorkerSuccess)
        {
            return Result.success();
        }
        return  Result.failure();
    }
}
