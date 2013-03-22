
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.game.data.Sex;
import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

import org.json.JSONArray;
import org.json.JSONObject;

public class ServerRoomInfo extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = 5447121161052304347L;

    private int mGameType;

    private long mRoomId;

    private int mMyPlayerId;

    private String[] mNickNames;

    private int[] mSexs;

    public ServerRoomInfo(SocketStream stream) {
        super(stream);
    }

    public ServerRoomInfo(int gameType, long roomId, String[] nickNames, int[] sexs, int myPlayerId) {
        super(SRV_ROOM_INFO);
        mGameType = gameType;
        mRoomId = roomId;
        mNickNames = nickNames;
        mSexs = sexs;
        mMyPlayerId = myPlayerId;
    }

    public int getGameType() {
        return mGameType;
    }

    public long getRoomId() {
        return mRoomId;
    }

    public int getMyPlayerId() {
        return mMyPlayerId;
    }

    public String[] getNickNames() {
        return mNickNames;
    }

    public int[] getSexs() {
        return mSexs;
    }

    @Override
    protected void unPackJson(String jsonString) {
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mGameType = getJsonInt(json, "game_type", -1);
            mRoomId = getJsonInt(json, "room_id", -1);
            mMyPlayerId = getJsonInt(json, "my_player_id", -1);

            JSONArray array = getJsonArray(json, "nickname_arr");
            if (array != null) {
                int length = array.length();
                mNickNames = new String[length];
                mSexs = new int[length];
                for (int i = 0; i < length; i++) {
                    JSONObject element = getJsonObj(array, i);
                    int playerId = getJsonInt(element, "player_id", -1);
                    String nickname = getJsonString(element, "nickname");
                    int sex = getJsonInt(element, "sex", Sex.MALE);
                    mNickNames[playerId] = nickname;
                    mSexs[playerId] = sex;
                }
            }

        }

    }

}
