
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerLastDeclare extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 5765362157445279096L;

    private int mCurrDeclarePlayerId;

    private int mCurrDeclareNum;

    private int mMaxDeclareNum;

    public ServerLastDeclare(SocketStream stream) {
        super(stream);
    }
    
    public ServerLastDeclare(int playerId, int declareNum, int maxDeclareNum) {
        super(SRV_LAST_DECLARE);
        mCurrDeclarePlayerId = playerId;
        mCurrDeclareNum = declareNum;
        mMaxDeclareNum = maxDeclareNum;
    }
    
    public int getCurrDeclarePlayerId() {
        return mCurrDeclarePlayerId;
    }
    
    public int getCurrDeclareNum() {
        return mCurrDeclareNum;
    }
    
    public int getMaxDeclareNum() {
        return mMaxDeclareNum;
    }

    @Override
    protected void unPackJson(String jsonString) {
        
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mCurrDeclarePlayerId = getJsonInt(json, "player_id", -1);
            mCurrDeclareNum = getJsonInt(json, "declare_num", -1);
            mMaxDeclareNum = getJsonInt(json, "max_delcare_num", -1);
        }

    }

}
