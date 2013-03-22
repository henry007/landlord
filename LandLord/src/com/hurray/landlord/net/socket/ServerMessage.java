
package com.hurray.landlord.net.socket;

import com.hurray.landlord.net.json.JsonHelper;
import com.hurray.landlord.net.socket.srv.ServerMessageType;
import com.hurray.landlord.utils.LogUtil;

import java.io.Serializable;

public abstract class ServerMessage extends JsonHelper implements Serializable, ServerMessageType {

    /**
     * 
     */
    private static final long serialVersionUID = -6567405272324966271L;

    private static final String TAG = "ServerMessage";

    private int mMsgType;
    
    public ServerMessage(SocketStream stream) {
        mMsgType = stream.getMsgType();
        String json = stream.getJsonString();
        LogUtil.d(TAG, "json=" + json);
        if (json != null) {
            unPackJson(json);
        } else {
            unPackJson("");
        }
    }

    public ServerMessage(int msgType) {
        mMsgType = msgType;
    }

    public int getMsgType() {
        return mMsgType;
    }

    abstract protected void unPackJson(String jsonString);
}
