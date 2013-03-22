
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;
import com.hurray.lordserver.protocol.message.card.MatchEndPush.PlayersRank;

import org.json.JSONObject;

import android.util.Base64;

public class ServerMatchResult extends ServerMessage {

    /**
     * 
     */
    private static final long serialVersionUID = 1623847073136332164L;

    // 玩家积分
    private int mPoint;
    // 玩家金币
    private int mMoneyGold;
    // 玩家红桃
    private int mMoneyHeart;
    // 玩家经验
    private int mCurrExp;

    // 玩家排名
    private int mRank;
    
    //比赛类型
    private int mGameType =-1;

    // 是否晋级
    public int isRised = -1;

    private PlayersRank[] results = null;

    public ServerMatchResult(SocketStream stream) {
        super(stream);
    }

    public ServerMatchResult(int point, int gold, int heart, int exp, int rank, int isRised,
            PlayersRank[] results) {
        super(SRV_MATCH_RESULT);
        mPoint = point;
        mMoneyGold = gold;
        mMoneyHeart = heart;
        mCurrExp = exp;
        mRank = rank;
        this.isRised = isRised;
        this.results = results;
    }

    public int getPoint() {
        return mPoint;
    }

    public int getGold() {
        return mMoneyGold;
    }

    public int getHeart() {
        return mMoneyHeart;
    }

    public int getCurrExp() {
        return mCurrExp;
    }

    @Override
    protected void unPackJson(String jsonString) {

        JSONObject json = getRespJsonObject(jsonString);
        if (json != null) {
            mPoint = getJsonInt(json, "point", 0);
            mMoneyGold = getJsonInt(json, "gold", 0);
            mMoneyHeart = getJsonInt(json, "heart", 0);
            mCurrExp = getJsonInt(json, "exp", 0);
            mRank = getJsonInt(json, "rank", 0);
        }
    }

    public int getRank() {
        return mRank;
    }

    public void setRank(int rank) {
        this.mRank = rank;
    }

    public int isRised() {
        return isRised;
    }

    public void setRised(int isRised) {
        this.isRised = isRised;
    }

    public PlayersRank[] getResults() {
        return results;
    }

    public void setResults(PlayersRank[] results) {
        this.results = results;
    }

    public int getmGameType() {
        return mGameType;
    }

    public void setmGameType(int mGameType) {
        this.mGameType = mGameType;
    }

}
