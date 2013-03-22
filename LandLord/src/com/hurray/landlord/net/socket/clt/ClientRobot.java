
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientRobot extends ClientMessage {
    
    /**
     * 
     */
    private static final long serialVersionUID = -826657596710233839L;
    
    private int mPlayerId;
    
    private boolean mRobot;

    public ClientRobot(int playerId, boolean robot) {
        super(CLT_ROBOT);
        mPlayerId = playerId;
        mRobot = robot;
    }
    
    public int getPlayerId() {
        return mPlayerId;
    }
    
    public boolean isRobot() {
        return mRobot;
    }

    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);
            json.put("robot", mRobot);

            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
