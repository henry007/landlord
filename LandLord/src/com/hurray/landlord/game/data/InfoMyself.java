
package com.hurray.landlord.game.data;

public class InfoMyself {

    public int mSelfPlayerId;

    public int mRightPlayerId;

    public int mLeftPlayerId;

    public int mSelfPlayerSex;

    public int mNextPlayerSex;

    public int mPrevPlayerSex;

    public String mSelfNickName;

    public int[] mSelfCardIds;

    public boolean[] mSelects;

    public boolean[] mToucheds;

    public int mWinWinNum;
    
    public int mWinNum;
    
    public int mLoseNum;
    
    public int mLeftBeautyId;
    
    public int mRightBeautyId;

    public InfoMyself() {
        reset();
    }

    public void reset() {
        mSelfPlayerId = -1;
        mRightPlayerId = -1;
        mLeftPlayerId = -1;

        mSelfPlayerSex = Sex.MALE;
        mNextPlayerSex = Sex.FEMALE;
        mPrevPlayerSex = Sex.FEMALE;

        mSelfNickName = null;
        mSelfCardIds = null;
        mSelects = null;
        mToucheds = null;
    }

}
