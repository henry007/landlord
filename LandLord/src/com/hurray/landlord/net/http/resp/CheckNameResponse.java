
package com.hurray.landlord.net.http.resp;

import com.hurray.landlord.net.http.HttpResponse;

import org.apache.http.protocol.HTTP;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class CheckNameResponse extends HttpResponse {

    /**
     *
     */
    private static final long serialVersionUID = -7712171486184082055L;

    private boolean mSuccess;

    public boolean getSuccess() {
        return mSuccess;
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
        }
        return respMes;
    }

}
