
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ClientShowCards extends ClientMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 373041790836079894L;

    private int mPlayerId;

    private int[] mShowCardIds;
    
    private boolean mIsFollowCards;

    public int getPlayerId() {
        return mPlayerId;
    }

    public int[] getShowCardIds() {
        return mShowCardIds;
    }
    
    public boolean isFollowCards() {
        return mIsFollowCards;
    }

    public ClientShowCards(int playerId, int[] showCardIds, boolean isFollowCards) {
        super(CLT_SHOW_CARDS);
        mPlayerId = playerId;
        mShowCardIds = showCardIds;
        mIsFollowCards = isFollowCards;
    }

    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);
            
            json.put("is_follow", mIsFollowCards);

            JSONArray array = new JSONArray();
            for (int i = 0; i < mShowCardIds.length; i++) {
                JSONObject element = new JSONObject();
                element.put("c", mShowCardIds[i]);
                array.put(element);
            }

            json.put("cards", array);
            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
