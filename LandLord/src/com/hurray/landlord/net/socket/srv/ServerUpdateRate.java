
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerUpdateRate extends ServerMessage {


    /**
     * 
     */
    private static final long serialVersionUID = -707333605240652854L;

    private int mRate;

    public ServerUpdateRate(SocketStream stream) {
        super(stream);
    }
    
    public ServerUpdateRate(int rate) {
        super(SRV_UPDATE_RATE);
        mRate = rate;
    }

    public int getRate() {
        return mRate;
    }
    
    @Override
    protected void unPackJson(String jsonString) {
        
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mRate = getJsonInt(json, "rate", 1);
        }
        
    }

}
