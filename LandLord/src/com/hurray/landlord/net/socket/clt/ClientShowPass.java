
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientShowPass extends ClientMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 6471673541452692654L;
    
    private int mPlayerId;

    public int getPlayerId() {
        return mPlayerId;
    }

    public ClientShowPass(int playerId) {
        super(CLT_SHOW_PASS);
        mPlayerId = playerId;
    }

    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);

            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
