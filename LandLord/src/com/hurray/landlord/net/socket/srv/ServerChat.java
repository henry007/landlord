
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.entity.ChatInfo;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONObject;

public class ServerChat extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 299098896947532685L;

    private ChatInfo mChatInfo;

    public ServerChat(SocketStream stream) {
        super(stream);
    }
    
    public ServerChat(int playerId, String message, int chatId) {
        super(SRV_CHAT);
        mChatInfo = new ChatInfo();
        mChatInfo.mPlayerId = playerId;
        mChatInfo.mMessage = message;
        mChatInfo.mChatId = chatId;
    }

    public ChatInfo getChatInfo() {
        return mChatInfo;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mChatInfo = new ChatInfo();
            mChatInfo.mPlayerId = getJsonInt(json, "player_id", -1);
            mChatInfo.mMessage = getJsonString(json, "msg");
            mChatInfo.mChatId = getJsonInt(json, "chat_id", -1);
        }

    }

}
