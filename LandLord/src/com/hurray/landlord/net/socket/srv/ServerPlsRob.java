
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerPlsRob extends ServerMessage {


    /**
     * 
     */
    private static final long serialVersionUID = -8100289186271594572L;

    private int mPlayerId;

    private long mTimeLeft;

    public ServerPlsRob(SocketStream stream) {
        super(stream);
    }
    
    public ServerPlsRob(int playerId, long timeLeft) {
        super(SRV_PLS_ROB);
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
