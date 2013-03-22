
package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;
import com.hurray.landlord.utils.LogUtil;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class LogInRequest extends HttpRequest {
    /**
     * 
     */
    private static final long serialVersionUID = 9072399521404284294L;
    
    private static final String TAG = "LogInRequest";

    private String mUserName;

    private String mPassword;

    public LogInRequest(String url, String userName, String pwd) {
        super(url);
        mUserName = userName;
        mPassword = pwd;
    }

    @Override
    public StringEntity pack() throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        try {
            json.put("username", mUserName);
            json.put("password", mPassword);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        LogUtil.d(TAG, json.toString());

        StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
        entity.setContentType(mContentType);

        return entity;
    }

    @Override
    public String toString() {
        return "LogInRequest [mPassword=" + mPassword + ", mUserName=" + mUserName + ", mReqURL="
                + mReqURL + "]";
    }
}
