
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientGoon extends ClientMessage {

    /**
     * 
     */
    private static final long serialVersionUID = -145575741916326323L;
    
    private int mPlayerId;

    public ClientGoon(int playerId) {
        super(CLT_GOON);
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
