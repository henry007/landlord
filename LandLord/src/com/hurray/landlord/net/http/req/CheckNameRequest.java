
package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CheckNameRequest extends HttpRequest {

    /**
     *
     */
    private static final long serialVersionUID = -7962526790040095252L;

    private String mName;

    public CheckNameRequest(String url, String name) {
        super(url);
        mName = name;
    }

    @Override
    public StringEntity pack() throws UnsupportedEncodingException {
        JSONObject json = new JSONObject();
        try {
            json.put("name", mName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        StringEntity entity = new StringEntity(json.toString(), HTTP.UTF_8);
        entity.setContentType(mContentType);

        return entity;
    }

    @Override
    public String toString() {
        return "CheckNameRequest [mName=" + mName + ", mReqURL=" + mReqURL + "]";
    }

}
