
package com.hurray.landlord.net.http.resp;

import com.hurray.landlord.net.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LogInResponse extends HttpResponse {
    /**
     * 
     */
    private static final long serialVersionUID = 2516284018961425541L;

    private boolean mSuccess;

    private int mLogInFlag;

    private int mUserId;

    private String mNickName;

    public boolean getSuccess() {
        return mSuccess;
    }

    public int getLogInFlag() {
        return mLogInFlag;
    }

    public int getUserId() {
        return mUserId;
    }

    public String getNickName() {
        return mNickName;
    }

    @Override
    public String unPack(byte[] respContent) {
        String respMes = null;
        try {
            respMes = new String(respContent, HTTP.UTF_8);
        } catch (UnsupportedEncodingException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        JSONObject json = getRespJsonObject(respMes);
        if (null != json) {

            mSuccess = getJsonBoolean(json, "success", false);
            mLogInFlag = getJsonInt(json, "flag", -1);
            if (mSuccess) {
                JSONArray array = getJsonArray(json, "result");
                if (array != null && array.length() > 0) {
                    JSONObject obj = getJsonObj(array, 0);
                    if (obj != null) {
                        mUserId = getJsonInt(obj, "huserid", -1);
                        mNickName = getJsonString(obj, "nickname");
                    }
                }
            }
        }
        return respMes;
    }
}
