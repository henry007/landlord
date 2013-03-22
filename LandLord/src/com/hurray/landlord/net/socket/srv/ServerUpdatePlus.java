
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerUpdatePlus extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 722437928771700314L;

    private int mPlayerId;

    private long mPlus;

    private long mCurrPlus;

    public ServerUpdatePlus(SocketStream stream) {
        super(stream);
    }

    public ServerUpdatePlus(int playerId, long plus, long currPlus) {
        super(SRV_UPDATE_PLUS);
        mPlayerId = playerId;
        mPlus = plus;
        mCurrPlus = currPlus;
    }

    public long getPlus() {
        return mPlus;
    }

    public long getCurrPlus() {
        return mCurrPlus;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlus = getJsonLong(json, "plus", 0);
            mCurrPlus = getJsonLong(json, "curr_plus", 0);
            mPlayerId = getJsonInt(json, "player_id", -1);
        }

    }

}
