
package com.hurray.landlord.game.data;

import com.hurray.landlord.Constants;
import com.hurray.landlord.game.online.UidException;
import com.hurray.landlord.utils.LogUtil;
import com.hurray.lordserver.protocol.message.card.RoomInfoPush.PlayerInfo;

import java.io.Serializable;
import java.util.ArrayList;

public class RoomInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 7394196376280620973L;

    private static final String TAG = "RoomInfo";

    private static final int ONE_SEC = 1000;
    
    private int mInningNum;

    private long mPlsReadyTime;

    private long mRoomId;

    private int mMaxRound;

    private int mGameType;

    private SignInPlayer mLeftPlayer;

    private SignInPlayer mRightPlayer;

    private SignInPlayer mSelfPlayer;

    public static boolean checkPositionType(PlayerInfo[] infoList) {
        int[] types = new int[Constants.PLAYER_NUM];
        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            types[i] = infoList[i].positionType;
            LogUtil.d(TAG, "checkPositionType i=" + i + " type=" + types[i]);
        }

        // 降序
        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            for (int j = i + 1; j < Constants.PLAYER_NUM; j++) {
                if (types[i] < types[j]) {
                    int temp = types[i];
                    types[i] = types[j];
                    types[j] = temp;
                }
            }
        }

        if ((types[0] > types[1] && types[0] == (types[1] + 1))
                && (types[1] > types[2] && types[1] == (types[2] + 1))) {
            LogUtil.d(TAG, "checkPositionType true");
            return true;
        }

        LogUtil.d(TAG, "checkPositionType false");
        return false;
    }

    public int getMaxRound() {
        return mMaxRound;
    }

    public void setMaxRound(int maxRound) {
        this.mMaxRound = maxRound;
    }

    public long getPlsReadyTime() {
        return mPlsReadyTime;
    }

    public void setPlsReadyTime(long second) {
        mPlsReadyTime = second * ONE_SEC;
    }

    public long getRoomId() {
        return mRoomId;
    }

    public void setRoomId(long roomId) {
        mRoomId = roomId;
    }

    public int getGameType() {
        return mGameType;
    }

    public void setGameType(int gameType) {
        mGameType = gameType;
    }

    public void setPlayers(PlayerInfo[] infoList) {
        ArrayList<SignInPlayer> playerList = new ArrayList<SignInPlayer>(Constants.PLAYER_NUM);
        for (PlayerInfo p : infoList) {
            SignInPlayer sip = new SignInPlayer(p);
            playerList.add(sip);
        }

        setPlayers(playerList);
        playerList.clear();
        playerList = null;
    }

    /***
     * playerId和位置 2(prev left) 1(next right) 0(self)
     */
    private void setPlayers(ArrayList<SignInPlayer> playerList) {
        for (SignInPlayer p : playerList) {
            if (p.isSelf()) {
                mSelfPlayer = p;
                mSelfPlayer.setPlayerId(0);
                playerList.remove(p);
                break;
            }
        }

        for (SignInPlayer p : playerList) {
            if (p.isLeft(mSelfPlayer)) {
                mLeftPlayer = p;
                mLeftPlayer.setPlayerId(2);
            } else if (p.isRight(mSelfPlayer)) {
                mRightPlayer = p;
                mRightPlayer.setPlayerId(1);
            }
        }

        LogUtil.d(TAG, "mLeftPlayer.positionType=" + mLeftPlayer.mPositionType);
        LogUtil.d(TAG, "mRightPlayer.positionType=" + mRightPlayer.mPositionType);
        LogUtil.d(TAG, "mSelfPlayer.positionType=" + mSelfPlayer.mPositionType);
    }

    public SignInPlayer getLeftPlayers() {
        return mLeftPlayer;
    }

    public SignInPlayer getRightPlayers() {
        return mRightPlayer;
    }

    public SignInPlayer getSelfPlayers() {
        return mSelfPlayer;
    }

    public String[] getNickNames() {
        String[] nickNames = new String[Constants.PLAYER_NUM];
        nickNames[mLeftPlayer.getPlayerId()] = mLeftPlayer.getNickName();
        nickNames[mRightPlayer.getPlayerId()] = mRightPlayer.getNickName();
        nickNames[mSelfPlayer.getPlayerId()] = mSelfPlayer.getNickName();
        return nickNames;
    }

    public int[] getSexs() {
        int[] sexs = new int[Constants.PLAYER_NUM];
        sexs[mLeftPlayer.getPlayerId()] = mLeftPlayer.getSex();
        sexs[mRightPlayer.getPlayerId()] = mRightPlayer.getSex();
        sexs[mSelfPlayer.getPlayerId()] = mSelfPlayer.getSex();
        return sexs;
    }

    /***
     * playerId和位置 2(prev left) 1(next right) 0(self)
     * 
     * @throws Exception
     */
    public int getPlayerId(long uid) throws UidException {
        if (uid >= 0) {
            if (mLeftPlayer.getUid() == uid) {
                return mLeftPlayer.getPlayerId();
            } else if (mRightPlayer.getUid() == uid) {
                return mRightPlayer.getPlayerId();
            } else if (mSelfPlayer.getUid() == uid) {
                return mSelfPlayer.getPlayerId();
            }
        }

        throw new UidException("UNKNOWN PLAYER UID=" + uid);
    }

    public boolean isLeftPlayer(long uid) throws UidException {
        return (mLeftPlayer.getPlayerId() == getPlayerId(uid));
    }

    public boolean isRightPlayer(long uid) throws UidException {
        return (mRightPlayer.getPlayerId() == getPlayerId(uid));
    }

    public boolean isSelfPlayer(long uid) throws UidException {
        return (mSelfPlayer.getPlayerId() == getPlayerId(uid));
    }

    public static class SignInPlayer implements Serializable {

        /**
         * 
         */
        private static final long serialVersionUID = 2027406017854518751L;

        public SignInPlayer(PlayerInfo p) {
            mUid = p.uid;
            mNickName = p.nickName;
            mPlayerType = p.playerType;
            mPositionType = p.positionType;

            String temp = p.avatar;

            String[] temps = temp.split(";");

            if (temps[0].length() > 0) {

                if (temps[0].substring(0, 1).equals("h")) {

                    String arg1 = temps[0].trim().toString();

                    mHairId = Integer.valueOf(arg1.substring(1, arg1.length()));

                }
                if (temps[1].substring(0, 1).equals("c")) {

                    String arg2 = temps[1].trim().toString();

                    mClothId = Integer.valueOf(arg2.substring(1, arg2.length()));

                }
            }

            mSex = p.sex;
        }

        public boolean isSelf() {
            return (mPlayerType == 0);
        }

        public boolean isLeft(SignInPlayer self) {
            if (!isSelf()) {
                int selfPosIndex = self.mPositionType - 1;
                int leftPosIndex = (selfPosIndex - 1 + Constants.PLAYER_NUM) % Constants.PLAYER_NUM;
                int posIndex = mPositionType - 1;
                if (leftPosIndex == posIndex) {
                    return true;
                }
            }

            return false;

        }

        public boolean isRight(SignInPlayer self) {
            if (!isSelf()) {
                int selfPosIndex = self.mPositionType - 1;
                int rightPosIndex = (selfPosIndex + 1) % Constants.PLAYER_NUM;
                int posIndex = mPositionType - 1;
                if (rightPosIndex == posIndex) {
                    return true;
                }
            }

            return false;
        }

        public String getNickName() {
            return mNickName;
        }

        public int getHairId() {
            return mHairId;
        }

        public int getClothId() {
            return mClothId;
        }

        public int getSex() {
            if (isMale()) {
                return Sex.MALE;
            } else if (isFemale()) {
                return Sex.FEMALE;
            }

            return Sex.UNKNOWN;
        }

        public boolean isMale() {
            return (mSex == 0);
        }

        public boolean isFemale() {
            return (mSex == 1);
        }

        public long getUid() {
            return mUid;
        }

        public int getPlayerId() {
            return mPlayerId;
        }

        public void setPlayerId(int playerId) {
            mPlayerId = playerId;
        }

        private int mPlayerId;

        // uid
        private long mUid;
        // 昵称
        private String mNickName;
        // 玩家身份:SELF(0)表示自己, OP(1)表示其他玩家
        private int mPlayerType;
        // 座位编号 1,2,3
        private int mPositionType;
        // 发型id
        private int mHairId;
        // 服装id
        private int mClothId;
        // 性别 男0 女1
        private int mSex;
    }

    public int getmInningNum() {
        return mInningNum;
    }

    public void setmInningNum(int mInningNum) {
        this.mInningNum = mInningNum;
    }


}
