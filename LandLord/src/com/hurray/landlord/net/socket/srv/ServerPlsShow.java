
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerPlsShow extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = -2801352564170985595L;

    private int mPlayerId;

    private long mTimeLeft;

    public ServerPlsShow(SocketStream stream) {
        super(stream);
    }
    
    public ServerPlsShow(int playerId, long timeLeft) {
        super(SRV_PLS_SHOW);
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
