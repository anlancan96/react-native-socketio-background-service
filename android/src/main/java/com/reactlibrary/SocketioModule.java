package com.reactlibrary;

import android.util.Log;

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

public class SocketioModule extends ReactContextBaseJavaModule {
    private static final String TAG = "SOCKETIOMODULE";
    private Socket mSocket;
    private final ReactApplicationContext reactContext;

    public SocketioModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
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
}
