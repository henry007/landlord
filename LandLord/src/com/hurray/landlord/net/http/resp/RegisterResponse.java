
package com.hurray.landlord.net.http.resp;

import com.hurray.landlord.net.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterResponse extends HttpResponse {

    /**
     * 
     */
    private static final long serialVersionUID = 6234874892279037659L;

    private boolean mSuccess;

    private int mUserId;

    private int mRegisterFlag;

    public boolean getSuccess() {
        return mSuccess;
    }

    public int getUserId() {
        return mUserId;
    }

    public int getRegisterFlag() {
        return mRegisterFlag;
    }

    @Override
    public String unPack(byte[] respContent) {

        String respMes = null;
        try {
            respMes = new String(respContent, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e1) {
            e1.printStackTrace();
        }

        JSONObject json = getRespJsonObject(respMes);
        if (null != json) {
            mSuccess = getJsonBoolean(json, "success", false);
            mRegisterFlag = getJsonInt(json, "flag", -1);
            if (mSuccess) {
                JSONArray array = getJsonArray(json, "result");
                if (array != null && array.length() > 0) {
                    JSONObject obj = getJsonObj(array, 0);
                    if (obj != null) {
                        mUserId = getJsonInt(obj, "huserid", -1);
                    }
                }
            }

        }

        return respMes;
    }

}
