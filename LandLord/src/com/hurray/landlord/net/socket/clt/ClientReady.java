
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientReady extends ClientMessage {

    /**
     * 
     */
    private static final long serialVersionUID = -9015634281749223566L;
    
    private int mPlayerId;

    public ClientReady(int playerId) {
        super(CLT_READY);
        mPlayerId = playerId;
    }
    
    public int getPlayerId() {
        return mPlayerId;
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
