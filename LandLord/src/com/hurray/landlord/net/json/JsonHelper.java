
package com.hurray.landlord.net.json;

import com.hurray.landlord.utils.LogUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public abstract class JsonHelper {

    private static final String TAG = "JsonHelper";

    protected String filter(String jsonMessage) {
        String s = jsonMessage;
        s = s.replaceAll("\b", "");
        s = s.replaceAll("\t", "");
        s = s.replaceAll("\n", "");
        s = s.replaceAll("\r\n", "");
        s = s.replaceAll("\f", "");
        s = s.replaceAll("\r", "");
        return s;
    }

    protected JSONObject getRespJsonObject(String respMes) {
        String jsonString = filter(respMes);
        JSONObject json = null;
        try {
            json = new JSONObject(jsonString);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        return json;
    }

    protected int getJsonInt(JSONObject json, String key, int defaultValue) {
        try {
            if (!json.isNull(key)) {
                return json.getInt(key);
            } else {
                LogUtil.d(TAG, key + ": does not exist");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
    
    protected long getJsonLong(JSONObject json, String key, long defaultValue) {
        try {
            if (!json.isNull(key)) {
                return json.getLong(key);
            } else {
                LogUtil.d(TAG, key + ": does not exist");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }    

    protected double getJsonDouble(JSONObject json, String key, double defaultValue) {
        try {
            if (!json.isNull(key)) {
                return json.getDouble(key);
            } else {
                LogUtil.d(TAG, key + ": does not exist");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }
    
    protected boolean getJsonBoolean(JSONObject json, String key, boolean defaultValue) {
        try {
            if (!json.isNull(key)) {
                return json.getBoolean(key);
            } else {
                LogUtil.d(TAG, key + ": does not exist");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return defaultValue;
    }    

    protected String getJsonString(JSONObject json, String key) {
        try {
            if (!json.isNull(key)) {
                return json.getString(key);
            } else {
                LogUtil.d(TAG, key + ": does not exist");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return "";
    }

    protected JSONArray getJsonArray(JSONObject json, String key) {
        try {
            if (!json.isNull(key)) {
                return json.getJSONArray(key);
            } else {
                LogUtil.d(TAG, key + ": does not exist");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    protected JSONObject getJsonObj(JSONArray array, int index) {
        try {
            if (!array.isNull(index)) {
                return array.getJSONObject(index);
            } else {
                LogUtil.d(TAG, "Json array does not contain elemet: " + index);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
