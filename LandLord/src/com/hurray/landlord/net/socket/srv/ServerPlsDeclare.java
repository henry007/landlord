
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerPlsDeclare extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 4865773932823337947L;

    private int mPlayerId;

    private long mTimeLeft;

    public ServerPlsDeclare(SocketStream stream) {
        super(stream);
    }
    
    public ServerPlsDeclare(int playerId, long timeLeft) {
        super(SRV_PLS_DECLARE);
        mPlayerId = playerId;
        mTimeLeft = timeLeft;
    }

    public int getPlayerId() {
        return mPlayerId;
    }
    
    public long getTimeLeft() {
        return mTimeLeft;
    }

    @Override
    protected void unPackJson(String jsonString) {
        
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlayerId = getJsonInt(json, "player_id", -1);
            mTimeLeft = getJsonLong(json, "time_left", -1L);
        }
        
    }

}
