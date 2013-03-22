
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerRobot extends ServerMessage {


    /**
     * 
     */
    private static final long serialVersionUID = -2680383763190831591L;
    
    private int mPlayerId;
    
    private boolean mRobot;

    public ServerRobot(SocketStream stream) {
        super(stream);
    }
    
    public ServerRobot(int playerId, boolean robot) {
        super(SRV_ROBOT);
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
    protected void unPackJson(String jsonString) {
        
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlayerId = getJsonInt(json, "player_id", -1);
            mRobot = getJsonBoolean(json, "robot", false);
        }
        
    }

}
