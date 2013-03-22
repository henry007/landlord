
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerPlsReady extends ServerMessage {


    /**
     * 
     */
    private static final long serialVersionUID = -9145108388151925041L;

    private long mTimeLeft;

    public ServerPlsReady(SocketStream stream) {
        super(stream);
    }
    
    public ServerPlsReady(long timeLeft) {
        super(SRV_PLS_READY);
        mTimeLeft = timeLeft;
    }

    public long getTimeLeft() {
        return mTimeLeft;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mTimeLeft = getJsonLong(json, "time_left", -1L);
        }

    }

}
