
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientDeclareLord extends ClientMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 837907720335999979L;

    private int mPlayerId;

    private int mDeclareNum;

    public int getPlayerId() {
        return mPlayerId;
    }

    public int getDeclareLordNum() {
        return mDeclareNum;
    }

    public ClientDeclareLord(int playerId, int declareNum) {
        super(CLT_DECLARE_LORD);
        mPlayerId = playerId;
        mDeclareNum = declareNum;
    }

    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);
            json.put("declare_num", mDeclareNum);

            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
