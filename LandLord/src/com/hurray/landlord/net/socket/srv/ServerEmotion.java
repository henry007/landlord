
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerEmotion extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 2089743807532176536L;
    
    private int mPlayerId;

    private int mEmotionId;

    public ServerEmotion(SocketStream stream) {
        super(stream);
    }
    
    public ServerEmotion(int playerId, int emotionId) {
        super(SRV_EMOTION);
        mPlayerId = playerId;
        mEmotionId = emotionId;
    }

    public int getPlayerId() {
        return mPlayerId;
    }
    
    public int getEmotionId() {
        return mEmotionId;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPlayerId = getJsonInt(json, "player_id", -1);
            mEmotionId = getJsonInt(json, "emotion_id", -1);
        }

    }

}
