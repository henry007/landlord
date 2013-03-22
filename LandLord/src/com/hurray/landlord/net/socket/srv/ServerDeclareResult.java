
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerDeclareResult extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = -1470013988354447708L;

    private boolean mResult;

    private int mFinalDeclareNum;

    private int mLordPlayerId;

    private int[] mBottomCardIds;

    public ServerDeclareResult(SocketStream stream) {
        super(stream);
    }
    
    public ServerDeclareResult(boolean result, int finalDeclareNum, int lordPlayerId,
            int[] bottomCardIds) {
        super(SRV_DECLARE_RESULT);
        mResult = result;
        mFinalDeclareNum = finalDeclareNum;
        mLordPlayerId = lordPlayerId;
        mBottomCardIds = bottomCardIds;
    }
    
    public boolean getResult() {
        return mResult;
    }
    
    public int getFinalDeclareNum() {
        return mFinalDeclareNum;
    }
    
    public int getLordPlayerId() {
        return mLordPlayerId;
    }
    
    public int[] getBottomCardIds() {
        return mBottomCardIds;
    }

    @Override
    protected void unPackJson(String jsonString) {
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mResult = getJsonBoolean(json, "result", false);
            if (mResult) {
                mFinalDeclareNum = getJsonInt(json, "final_declare_num", 0);
                mLordPlayerId = getJsonInt(json, "lord_player_id", -1);
                
                JSONArray array = getJsonArray(json, "bottom_cards");
                if (array != null) {
                    int length = array.length();
                    mBottomCardIds = new int[length];
                    for (int i = 0; i < length; i++) {
                        JSONObject element = getJsonObj(array, i);
                        mBottomCardIds[i] = getJsonInt(element, "c", -1);
                    }
                }
            }
        }

    }
}
