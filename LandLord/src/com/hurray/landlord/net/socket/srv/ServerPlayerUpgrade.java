
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerPlayerUpgrade extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 2468760483460162780L;

    private int mLevel;

    public ServerPlayerUpgrade(SocketStream stream) {
        super(stream);
    }

    public ServerPlayerUpgrade(int level) {
        super(SRV_PLAYER_UPGRADE);
        mLevel = level;
    }

    public int getLevel() {
        return mLevel;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mLevel = getJsonInt(json, "level", 0);
        }
    }

}
