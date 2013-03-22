
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientRobLord extends ClientMessage {


    /**
     * 
     */
    private static final long serialVersionUID = -8554016823917428823L;

    private int mPlayerId;

    private boolean mIsRob;

    public int getPlayerId() {
        return mPlayerId;
    }

    public boolean isRob() {
        return mIsRob;
    }

    public ClientRobLord(int playerId, boolean isRob) {
        super(CLT_ROB_LORD);
        mPlayerId = playerId;
        mIsRob = isRob;
    }

    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);
            json.put("is_rob", mIsRob);

            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
