
package com.hurray.landlord.net.http.req;

import com.hurray.landlord.net.http.HttpRequest;

import org.apache.http.entity.StringEntity;
import org.apache.http.protocol.HTTP;

import java.io.UnsupportedEncodingException;

public class GetApkRequest extends HttpRequest {


    /**
     * 
     */
    private static final long serialVersionUID = 6931438316840534627L;

    private static final String TAG = "GetApkRequest";

    public GetApkRequest(String fileUrl) {
        super(fileUrl);
        super.setReqMethod(HttpRequest.GET);
    }

    @Override
    public StringEntity pack() throws UnsupportedEncodingException {
        StringEntity entity = new StringEntity("", HTTP.UTF_8);
        entity.setContentType(mContentType);

        return entity;
    }

    @Override
    public String toString() {
        return "GetApkRequest [mReqURL=" + mReqURL + "]";
    }

}
