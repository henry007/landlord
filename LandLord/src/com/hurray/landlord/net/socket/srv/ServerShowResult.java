
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerShowResult extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = -2472948554789259869L;

    private boolean mIsShow; // or follow

    private int mLastShowPlayerId;

    private int[] mLastShowCardIds;

    public ServerShowResult(SocketStream stream) {
        super(stream);
    }

    public ServerShowResult(int lastShowPlayerId, int[] lastShowCardIds, boolean isShow) {
        super(SRV_SHOW_RESULT);
        mLastShowPlayerId = lastShowPlayerId;
        mLastShowCardIds = lastShowCardIds;
        mIsShow = isShow;
    }

    public int getLastShowPlayerId() {
        return mLastShowPlayerId;
    }

    public int[] getLastShowCardIds() {
        return mLastShowCardIds;
    }

    public boolean IsShow() {
        return mIsShow;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mIsShow = getJsonBoolean(json, "is_show", false);
            mLastShowPlayerId = getJsonInt(json, "last_show_player_id", -1);

            JSONArray array = getJsonArray(json, "last_show_cards");
            if (array != null) {
                int length = array.length();
                mLastShowCardIds = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject element = getJsonObj(array, i);
                    mLastShowCardIds[i] = getJsonInt(element, "c", -1);
                }
            }

        }

    }
}
