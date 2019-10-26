package com.reactlibrary;

import android.util.Log;

import com.facebook.react.bridge.ReactContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class ReactNativeEventUtil {
    private static final String TAG = "ReactNativeEventUtil";

    public static void sendEvent(ReactContext reactContext, String eventName, Object params) {
        if (reactContext != null)
            reactContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                    .emit(eventName, params);
        else
            Log.e(TAG, "Could not submit event for a null context...");
    }
}
