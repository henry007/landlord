
package com.hurray.landlord.net.socket;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;

public class SocketStream {

    private int mMsgType;

    private String mJsonString;

    public int getMsgType() {
        return mMsgType;
    }

    public void setMsgType(int msgType) {
        mMsgType = msgType;
    }

    public String getJsonString() {
        return mJsonString;
    }

    public void setJsonString(String jsonString) {
        mJsonString = jsonString;
    }

    public InputStream in(InputStream is) throws IOException {
        if (is != null) {
            DataInputStream dis = new DataInputStream(is);
            mMsgType = dis.readInt();
            mJsonString = dis.readUTF();
            return is;
        }

        return null;
    }

}
