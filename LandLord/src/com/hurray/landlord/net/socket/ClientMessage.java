
package com.hurray.landlord.net.socket;

import com.hurray.landlord.net.json.JsonHelper;
import com.hurray.landlord.net.socket.clt.ClientMessageType;
import com.hurray.landlord.utils.LogUtil;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;

public abstract class ClientMessage extends JsonHelper implements Serializable, ClientMessageType {

    private static final String TAG = "ClientMessage";

    private static final long serialVersionUID = 1481472863480192328L;

    private int mMsgType;

    public ClientMessage(int msgType) {
        mMsgType = msgType;
    }

    public void setMsgType(int msgType) {
        mMsgType = msgType;
    }

    public int getMsgType() {
        return mMsgType;
    }

    public OutputStream out(OutputStream os) throws IOException {
        if (os != null) {
            DataOutputStream dos = new DataOutputStream(os);
            dos.writeInt(mMsgType);
            String jsonString = packJson();
            LogUtil.d(TAG, "jsonString=" + jsonString);
            if (jsonString != null) {
                dos.writeUTF(jsonString);
            } else {
                dos.writeUTF("");
            }
            return os;
        }
        return null;
    }

    abstract protected String packJson();

}
