package com.reactlibrary;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReadableNativeMap;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class SocketioModule extends ReactContextBaseJavaModule {
    private static final String TAG = "SOCKETIOMODULE";
    private Socket mSocket;
    private final ReactApplicationContext reactContext;
    private boolean allowRunBackground = false;
    private LifecycleOwner lifecycleOwner;
    public SocketioModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
        InitBroadcastReceiver();
    }

    @Override
    public String getName() {
        return "Socketio";
    }

    @ReactMethod
    public void sampleMethod(String stringArgument, int numberArgument, Callback callback) {
        // TODO: Implement some actually useful functionality
        callback.invoke("Received numberArgument: " + numberArgument + " stringArgument: " + stringArgument);
    }

    @ReactMethod
    public void initialize(String connection, ReadableMap options) {
        try {
            this.mSocket = IO.socket(connection, MapUtil.mapToOptions((ReadableNativeMap) options));
            mSocket.on(Socket.EVENT_CONNECT, onNewMessage("connect"));
            mSocket.on(Socket.EVENT_DISCONNECT, onNewMessage("disconnect"));
            mSocket.on(Socket.EVENT_CONNECT_ERROR, onNewMessage("connect_error"));
        }
        catch(URISyntaxException exception) {
            Log.e(TAG, "Socket Initialization error: ", exception);
        }
    }

    @ReactMethod
    public void StartBackgroundWorker(String connection){
        AlarmManager alarmManager = (AlarmManager)this.getAppContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this.getAppContext(), SyncService.class);
        intent.setAction(SyncService.ACTION_SYNC);
        intent.putExtra(SyncService.EXTRA_CONNECTION, connection);
        PendingIntent pendingIntent = PendingIntent.getService(this.getAppContext(), 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.ELAPSED_REALTIME_WAKEUP,  3000, pendingIntent);
        }
        else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            alarmManager.setExact(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, pendingIntent);
        }
        else {
            alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, 3000, pendingIntent);
        }
    }

    @ReactMethod
    public void emit(String event, ReadableMap data) {
        try {
            JSONObject json = MapUtil.toJSONObject(data);
            if (mSocket != null) {
                mSocket.emit(event, json);
            }
            else {
                Log.e(TAG, "Cannot execute emit. mSocket is null. Initialize socket first!!!");
            }
        }catch (JSONException e){
            Log.e(TAG, e.getMessage());
        }
    }

    @ReactMethod
    public void connect() {
        if (mSocket != null) {
            mSocket.connect();
        }
        else {
            Log.e(TAG, "Cannot execute connect. mSocket is null. Initialize socket first!!!");
        }
    }

    @ReactMethod
    public void disconnect() {
        if (mSocket != null) {
            mSocket.disconnect();
        }
        else {
            Log.e(TAG, "Cannot execute disconnect. mSocket is null. Initialize socket first!!!");
        }
    }

    private Emitter.Listener onNewMessage (final String event){
        return new Emitter.Listener(){
            @Override
            public void call(final Object... args) {
                WritableMap params = Arguments.createMap();
                params.putString("name", event);
                WritableArray items = MapUtil.objectsFromJSON(args);
                if (items != null) {
                    params.putArray("data", items);
                }
                ReactNativeEventUtil.sendEvent(reactContext, "SocketEvent", params);
            }
        };
    };

    @ReactMethod
    public void on(String event) {
        if (mSocket != null) {
            mSocket.on(event, onNewMessage(event));
        }
        else {
            Log.e(TAG, "Cannot execute on. mSocket is null. Initialize socket first!!!");
        }
    }

    private void InitBroadcastReceiver(){
        LocalBroadcastManager.getInstance(this.getAppContext()).registerReceiver(mMessageReceiver,
                new IntentFilter("local.incomingcall"));
    }

    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            // Get extra data included in the Intent
            String message = intent.getStringExtra("message");
            Log.d("receiver", "Got message: " + message);
        }
    };

    private Context getAppContext() {
        return this.reactContext.getApplicationContext();
    }
}
