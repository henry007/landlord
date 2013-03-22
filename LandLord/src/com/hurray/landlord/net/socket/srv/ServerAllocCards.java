
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerAllocCards extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 2500700010316091773L;

    private int mPlayerId;

    private int[] mAllocCardIds;
    
    public ServerAllocCards(SocketStream stream) {
        super(stream);
    }
    
    public ServerAllocCards(int playerId, int[] cardIds) {
        super(SRV_ALLOC_CARDS);
        mPlayerId = playerId;
        mAllocCardIds = cardIds;
    }
    
    public int getPlayerId() {
        return mPlayerId;
    }    
    
    public int[] getAllocCardIds() {
        return mAllocCardIds;
    }

    @Override
    protected void unPackJson(String jsonString) {
        
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlayerId = getJsonInt(json, "player_id", -1);
            JSONArray array = getJsonArray(json, "cards");
            if (array != null) {
                int length = array.length();
                mAllocCardIds = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject element = getJsonObj(array, i);
                    mAllocCardIds[i] = getJsonInt(element, "c", -1);
                }
            }
        }
        
    }

}
