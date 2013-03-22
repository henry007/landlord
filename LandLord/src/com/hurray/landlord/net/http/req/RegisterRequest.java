
package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class RegisterRequest extends HttpRequest {
    /**
     * 
     */
    private static final long serialVersionUID = -8450145312919252351L;

    private String mUserName;

    private String mNickName;

    private String mPassword;

    private String mSex;

    public RegisterRequest(String url, String username, String nickname, String password, String sex) {
        super(url);
        mUserName = username;
        mNickName = nickname;
        mPassword = password;
        mSex = sex;
    }

    @Override
    public StringEntity pack() throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        try {
            json.put("username", mUserName);
            json.put("nickname", mNickName);
            json.put("password", mPassword);
            json.put("sex", mSex);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
        entity.setContentType(mContentType);

        return entity;
    }  

    @Override
    public String toString() {
        return "RegisterRequest [mUserName=" + mUserName + ", mNickName=" + mNickName
                + ", mPassword=" + mPassword + ", mReqURL=" + mReqURL
                + "]";
    }
}
