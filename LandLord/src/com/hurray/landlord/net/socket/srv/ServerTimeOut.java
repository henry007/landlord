
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerTimeOut extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 8494452538620756424L;

    private int mPlayerId;

    public ServerTimeOut(SocketStream stream) {
        super(stream);
    }
    
    public int getPlayerId() {
        return mPlayerId;
    }    

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlayerId = getJsonInt(json, "player_id", -1);
        }

    }
}
