
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerLastRob extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 7348995032635711554L;

    private int mPlayerId;

    private boolean mIsRob;

    public ServerLastRob(SocketStream stream) {
        super(stream);
    }

    public ServerLastRob(int playerId, boolean isRob) {
        super(SRV_LAST_ROB);
        mPlayerId = playerId;
        mIsRob = isRob;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    public boolean isRob() {
        return mIsRob;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlayerId = getJsonInt(json, "player_id", -1);
            mIsRob = getJsonBoolean(json, "is_rob", false);
        }

    }

}
