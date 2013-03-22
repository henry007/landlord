
package com.hurray.landlord.game.data;

import com.hurray.landlord.Constants;

public class InfoCommon {

    public int mGameType;

    public int mRoomId;

    public String[] mNickNames;

    public int mLordPlayerId;

    public int[] mBottomCardIds;

    // 各家剩的牌数

    public int[] mLeftCardNums = new int[Constants.PLAYER_NUM];

    // 上一手牌的那家信息

    public int mPrevShowPlayerId;

    public int[] mPrevShowCardIds;

    // 准备出牌玩家

    public int mCurrShowPlayerId;

    // 分数

    public int mDeclareNum;

    public int mRate;

    public long[] mPlus = new long[Constants.PLAYER_NUM];

    // public long[] mMarks = new long[Constants.PLAYER_NUM];
    //
    // public long[] mTotalMarks = new long[Constants.PLAYER_NUM];
    
    public int mRoundIndex;
    
    public InfoCommon() {
        reset();
    }

    public void reset() {
        mGameType = -1;
        mRoomId = -1;
        mNickNames = null;
        mLordPlayerId = -1;
        mBottomCardIds = null;

        resetLeftCardNum();

        // 上一手牌的那家信息
        mPrevShowPlayerId = -1;
        mPrevShowCardIds = null;

        // 准备出牌玩家
        mCurrShowPlayerId = -1;

        mDeclareNum = -1;
        mRate = -1;
        for (int i = mPlus.length - 1; i >= 0; i--) {
            mPlus[i] = 0;
        }

        // mMarks = null;
        // mTotalMarks = null;
    }
    
    public void resetLeftCardNum() {
        // 各家剩的牌数
        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            mLeftCardNums[i] = -1;
        }
    }

}
