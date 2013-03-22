
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerPlsFollow extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = -526617541388364189L;

    private int mPlayerId;

    private int mMaxCardIdsPlayerId;

    private int[] mMaxCardIds;

    private long mTimeLeft;

    public ServerPlsFollow(SocketStream stream) {
        super(stream);
    }
    
    public ServerPlsFollow(int playerId, int maxCardsPlayerId, int[] maxCardIds,
            long timeLeft) {
        super(SRV_PLS_FOLLOW);
        mPlayerId = playerId;
        mMaxCardIdsPlayerId = maxCardsPlayerId;
        mMaxCardIds = maxCardIds;
        mTimeLeft = timeLeft;
    }    

    public int getPlayerId() {
        return mPlayerId;
    }

    public int getMaxCardIdsPlayerId() {
        return mMaxCardIdsPlayerId;
    }

    public int[] getMaxCardIds() {
        return mMaxCardIds;
    }

    public long getTimeLeft() {
        return mTimeLeft;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {

            mPlayerId = getJsonInt(json, "player_id", -1);
            mMaxCardIdsPlayerId = getJsonInt(json, "max_cards_player_id", -1);
            mTimeLeft = getJsonLong(json, "time_left", -1L);

            JSONArray array = getJsonArray(json, "max_cards");
            if (array != null) {
                int length = array.length();
                mMaxCardIds = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject element = getJsonObj(array, i);
                    mMaxCardIds[i] = getJsonInt(element, "c", -1);
                }
            }
        }

    }

}
