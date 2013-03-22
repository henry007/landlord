
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerSyncCards extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = -919023194287801487L;

    private int[] mSelfCardIds;

    private int mLeftCardNum;

    private int mRightCardNum;

    public ServerSyncCards(SocketStream stream) {
        super(stream);
    }

    public ServerSyncCards(int[] selfCardIds, int leftCardNum, int rightCardNum) {
        super(SRV_SYNC_CARDS);
        mSelfCardIds = selfCardIds;
        mLeftCardNum = leftCardNum;
        mRightCardNum = rightCardNum;
    }

    public int[] getSelfCardIds() {
        return mSelfCardIds;
    }

    public int getLeftCardNum() {
        return mLeftCardNum;
    }

    public int getRightCardNum() {
        return mRightCardNum;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mLeftCardNum = getJsonInt(json, "left_num", -1);
            mRightCardNum = getJsonInt(json, "right_num", -1);
            JSONArray array = getJsonArray(json, "self_cards");
            if (array != null) {
                int length = array.length();
                mSelfCardIds = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject element = getJsonObj(array, i);
                    mSelfCardIds[i] = getJsonInt(element, "c", -1);
                }
            }
        }

    }

}
