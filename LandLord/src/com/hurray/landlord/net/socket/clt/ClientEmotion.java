
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientEmotion extends ClientMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 308652676011077735L;

    private int mPlayerId;

    private int mEmotionId;

    public ClientEmotion(int playerId, int emotionId) {
        super(CLT_EMOTION);
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
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);

            if (mEmotionId >= 0) {
                json.put("emotion_id", mEmotionId);
            }

            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
