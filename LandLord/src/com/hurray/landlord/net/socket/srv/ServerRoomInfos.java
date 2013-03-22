
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush.PlayerInfo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ServerRoomInfos extends ServerMessage {

    private int gameType; // 报名游戏类型 目前只有一种: QUICK_GAME(10)
    private long teamId; // 组id, 就是一桌的id
    private int maxRound;// 房间打牌最大轮次
    private long mPlsReadyTime;
    private PlayerInfo[] players = new PlayerInfo[0];
    
    private int inningNum;

    public ServerRoomInfos(int gameType, long teamId, int maxRound,
            long mPlsReadyTime, PlayerInfo[] players) {
        super(SRV_ROOM_INFOS);
        this.gameType = gameType;
        this.teamId = teamId;
        this.maxRound = maxRound;
        this.players = players;
        this.mPlsReadyTime = mPlsReadyTime;
    }

    public ServerRoomInfos() {
        super(SRV_ROOM_INFOS);
    }

    private static final long serialVersionUID = 2886309629472632885L;

    @Override
    protected void unPackJson(String jsonString) {
        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {

            gameType = getJsonInt(json, "gameType", -1);
            teamId = getJsonInt(json, "teamId", -1);
            maxRound = getJsonInt(json, "maxRound", -1);
            mPlsReadyTime = getJsonInt(json, "mPlsReadyTime", -1);
            JSONArray listJson = json.optJSONArray("players");
            PlayerInfo[] infos = new PlayerInfo[listJson.length()];
            for (int i = 0; i < infos.length; i++) {
                infos[i] = new PlayerInfo();
                JSONObject playerJson = null;
                try {
                    playerJson = listJson.getJSONObject(i);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                infos[i].uid = playerJson.optLong("uid");
                infos[i].nickName = playerJson.optString("nickName");
                infos[i].playerType = playerJson.optInt("playerType");
                infos[i].positionType = playerJson.optInt("positionType");
                infos[i].hairId = playerJson.optInt("hairId");
                infos[i].clothId = playerJson.optInt("clothId");
                infos[i].sex = playerJson.optInt("sex");
                infos[i].avatar = playerJson.optString("avatar");
            }
            this.players = infos;

        }

    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getMaxRound() {
        return maxRound;
    }

    public void setMaxRound(int maxRound) {
        this.maxRound = maxRound;
    }

    public PlayerInfo[] getPlayers() {
        return players;
    }

    public void setPlayers(PlayerInfo[] players) {
        this.players = players;
    }

    public long getmPlsReadyTime() {
        return mPlsReadyTime;
    }

    public void setmPlsReadyTime(long mPlsReadyTime) {
        this.mPlsReadyTime = mPlsReadyTime;
    }

    public int getInningNum() {
        return inningNum;
    }

    public void setInningNum(int inningNum) {
        this.inningNum = inningNum;
    }
}
