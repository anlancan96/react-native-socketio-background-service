package com.reactlibrary;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReadableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableType;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.ReadableMapKeySetIterator;
import com.facebook.react.bridge.ReadableNativeMap;
import com.github.nkzawa.socketio.client.IO;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;

public class MapUtil {
    private static final String TAG = "MapUtil";

    public static JSONObject toJSONObject(ReadableMap readableMap) throws JSONException {
        JSONObject jsonObject = new JSONObject();

        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = readableMap.getType(key);

            switch (type) {
                case Null:
                    jsonObject.put(key, null);
                    break;
                case Boolean:
                    jsonObject.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    jsonObject.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    jsonObject.put(key, readableMap.getString(key));
                    break;
                case Map:
                    jsonObject.put(key, MapUtil.toJSONObject(readableMap.getMap(key)));
                    break;
                case Array:
                    jsonObject.put(key, ArrayUtil.toJSONArray(readableMap.getArray(key)));
                    break;
            }
        }

        return jsonObject;
    }

    public static Map<String, Object> toMap(JSONObject jsonObject) throws JSONException {
        Map<String, Object> map = new HashMap<>();
        Iterator<String> iterator = jsonObject.keys();

        while (iterator.hasNext()) {
            String key = iterator.next();
            Object value = jsonObject.get(key);

            if (value instanceof JSONObject) {
                value = MapUtil.toMap((JSONObject) value);
            }
            if (value instanceof JSONArray) {
                value = ArrayUtil.toArray((JSONArray) value);
            }

            map.put(key, value);
        }

        return map;
    }

    public static Map<String, Object> toMap(ReadableMap readableMap) {
        Map<String, Object> map = new HashMap<>();
        ReadableMapKeySetIterator iterator = readableMap.keySetIterator();

        while (iterator.hasNextKey()) {
            String key = iterator.nextKey();
            ReadableType type = readableMap.getType(key);

            switch (type) {
                case Null:
                    map.put(key, null);
                    break;
                case Boolean:
                    map.put(key, readableMap.getBoolean(key));
                    break;
                case Number:
                    map.put(key, readableMap.getDouble(key));
                    break;
                case String:
                    map.put(key, readableMap.getString(key));
                    break;
                case Map:
                    map.put(key, MapUtil.toMap(readableMap.getMap(key)));
                    break;
                case Array:
                    map.put(key, ArrayUtil.toArray(readableMap.getArray(key)));
                    break;
            }
        }

        return map;
    }

    public static WritableMap toWritableMap(Map<String, Object> map) {
        WritableMap writableMap = Arguments.createMap();
        Iterator iterator = map.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry pair = (Map.Entry)iterator.next();
            Object value = pair.getValue();

            if (value == null) {
                writableMap.putNull((String) pair.getKey());
            } else if (value instanceof Boolean) {
                writableMap.putBoolean((String) pair.getKey(), (Boolean) value);
            } else if (value instanceof Double) {
                writableMap.putDouble((String) pair.getKey(), (Double) value);
            } else if (value instanceof Integer) {
                writableMap.putInt((String) pair.getKey(), (Integer) value);
            } else if (value instanceof String) {
                writableMap.putString((String) pair.getKey(), (String) value);
            } else if (value instanceof Map) {
                writableMap.putMap((String) pair.getKey(), MapUtil.toWritableMap((Map<String, Object>) value));
            } else if (value.getClass() != null && value.getClass().isArray()) {
                writableMap.putArray((String) pair.getKey(), ArrayUtil.toWritableArray((Object[]) value));
            }

            iterator.remove();
        }

        return writableMap;
    }

    public static IO.Options mapToOptions(ReadableNativeMap options) {
        ReadableMapKeySetIterator iterator = options.keySetIterator();
        IO.Options opts = new IO.Options();
        while (iterator.hasNextKey()) {
            String key = iterator.nextKey().toLowerCase();

            switch (key) {
                case "force new connection":
                case "forcenew":
                    opts.forceNew = options.getBoolean(key);
                    break;
                case "multiplex":
                    opts.multiplex = options.getBoolean(key);
                    break;
                case "reconnection":
                    opts.reconnection = options.getBoolean(key);
                    break;
                case "connect_timeout":
                    opts.timeout = options.getInt(key);
                    break;
                case "reconnectionAttempts":
                    opts.reconnectionAttempts = options.getInt(key);
                    break;
                case "reconnectionDelay":
                    opts.reconnectionDelay = options.getInt(key);
                    break;
                case "reconnectionDelayMax":
                    opts.reconnectionDelayMax = options.getInt(key);
                default:
                    Log.e(TAG, "Could not convert object with key: " + key + ".");
            }
        }
        return opts;
    }

    public static WritableArray objectsFromJSON(Object... args) {
        if (args != null && args.length > 0) {
            WritableArray items = Arguments.createArray();
            for (Object object : args) {
                if (object == null) {
                    items.pushNull();
                } else if (object instanceof JSONObject) {
                    items.pushMap(jsonObjectToWritableMap((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    items.pushArray(jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Pushing null to the array. " +
                            "Original unidentfied object = " + object);
                    items.pushNull();
                }
            }
            return items;
        }
        return null;
    }

    public static WritableMap jsonObjectToWritableMap(JSONObject jsonObject) {
        WritableMap items = Arguments.createMap();
        Iterator<String> iterator = jsonObject.keys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            try {
                Object object = jsonObject.get(key);
                if (object == null) {
                    items.putNull(key);
                } else if (object instanceof Boolean) {
                    items.putBoolean(key, ((Boolean) object));
                } else if (object instanceof Integer) {
                    items.putInt(key, ((Integer) object));
                } else if (object instanceof Double) {
                    items.putDouble(key, ((Double) object));
                } else if (object instanceof Float) {
                    items.putDouble(key, ((Float) object).doubleValue());
                } else if (object instanceof Long) {
                    items.putDouble(key, ((Long) object).doubleValue());
                } else if (object instanceof String) {
                    items.putString(key, object.toString());
                } else if (object instanceof JSONObject) {
                    items.putMap(key, jsonObjectToWritableMap((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    items.putArray(key, jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Inserting Null object for key "
                            + key + " unidentfied object = " + object);
                    items.putNull(key);
                }
            } catch (JSONException error) {
                Log.e(TAG, "objectsFromJSON JSONException = " + error);
            }
        }
        return items;
    }

    public static WritableArray jsonArrayToWritableArray(JSONArray jsonArray) {
        WritableArray items = Arguments.createArray();
        for (int i=0; i < jsonArray.length(); i++) {
            try {
                Object object = jsonArray.get(i);
                if (object == null) {
                    items.pushNull();
                } else if (object instanceof Boolean) {
                    items.pushBoolean((Boolean) object);
                } else if (object instanceof Integer) {
                    items.pushInt((Integer) object);
                } else if (object instanceof Double) {
                    items.pushDouble((Double) object);
                } else if (object instanceof Float) {
                    items.pushDouble(((Float) object).doubleValue());
                } else if (object instanceof Long) {
                    items.pushDouble(((Long) object).doubleValue());
                } else if (object instanceof String) {
                    items.pushString(object.toString());
                } else if (object instanceof JSONObject) {
                    items.pushMap(jsonObjectToWritableMap((JSONObject) object));
                } else if (object instanceof JSONArray) {
                    items.pushArray(jsonArrayToWritableArray((JSONArray) object));
                } else {
                    Log.e(TAG, "Cannot identify JSONObject. Inserting Null object. " +
                            "Original unidentfied object = " + object);
                    items.pushNull();
                }
            } catch (JSONException error) {
                Log.e(TAG, "objectsFromJSON JSONException = " + error);
            }
        }
        return items;
    }
}
