
package com.hurray.landlord.net.socket.clt;

import com.hurray.landlord.entity.ChatInfo;
import com.hurray.landlord.net.socket.ClientMessage;

import org.json.JSONException;
import org.json.JSONObject;

public class ClientChat extends ClientMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 3011005915025330916L;

    private int mPlayerId;

    private String mMessage;

    private int mChatId;

    public ClientChat(ChatInfo chatInfo) {
        super(CLT_CHAT);
        mPlayerId = chatInfo.mPlayerId;
        mMessage = chatInfo.mMessage;
        mChatId = chatInfo.mChatId;
    }

    public int getPlayerId() {
        return mPlayerId;
    }

    public String getMessage() {
        return mMessage;
    }

    public int getChatId() {
        return mChatId;
    }

    @Override
    protected String packJson() {
        String jsonString = "";
        try {
            JSONObject json = new JSONObject();
            json.put("player_id", mPlayerId);
            if (mMessage != null) {
                json.put("msg", mMessage);
            }
            if (mChatId >= 0) {
                json.put("chat_id", mChatId);
            }

            jsonString += json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return jsonString;
    }

}
