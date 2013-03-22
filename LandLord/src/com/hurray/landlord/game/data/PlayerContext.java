
package com.hurray.landlord.game.data;

import com.hurray.landlord.game.CardType;
import com.hurray.landlord.game.CardUtil;
import com.hurray.landlord.utils.ToastUtil;

public class PlayerContext {

    public static final int THINKING_IDLE = 0;

    public static final int THINKING_SHOW_CARD = 1;

    public static final int THINKING_FOLLOW_CARD = 2;

    public int mPlayerState = THINKING_IDLE;

    public InfoMyself mInfoMyself;

    public InfoCommon mInfoCommon;
    
    public int mTouchedCardsCount = 0;

    public PlayerContext() {
        mInfoMyself = new InfoMyself();
        mInfoCommon = new InfoCommon();
    }

    public int[] getSelfCardIds() {
        return mInfoMyself.mSelfCardIds;
    }

    public void setSelfCardIds(int[] selfCardIds) {
        mInfoMyself.mSelfCardIds = selfCardIds;
    }

    public boolean[] getSelfCardSelects() {
        return mInfoMyself.mSelects;
    }

    public void setSelfCardSelects(boolean[] selects) {
        mInfoMyself.mSelects = selects;
    }

    public boolean[] getSelfCardTouches() {
        return mInfoMyself.mToucheds;
    }

    public void setSelfCardTouches(boolean[] touches) {
        mInfoMyself.mToucheds = touches;
    }

    public int[] getBottomCardIds() {
        return mInfoCommon.mBottomCardIds;
    }

    public void setBottomCardIds(int[] bottomCardIds) {
        mInfoCommon.mBottomCardIds = bottomCardIds;
    }

    public boolean isSelfLandLord() {
        return (mInfoCommon.mLordPlayerId == mInfoMyself.mSelfPlayerId);
    }

    public boolean isNextPlayerLandLord() {
        return (mInfoCommon.mLordPlayerId == mInfoMyself.mRightPlayerId);
    }

    public boolean isPrevPlayerLandLord() {
        return (mInfoCommon.mLordPlayerId == mInfoMyself.mLeftPlayerId);
    }

    public boolean isPrevShowLandLord() {
        return (mInfoCommon.mPrevShowPlayerId == mInfoCommon.mLordPlayerId);
    }

    public boolean isPrevShowNextOfMe() {
        return (mInfoCommon.mPrevShowPlayerId == mInfoMyself.mRightPlayerId);
    }

    public boolean isLandLord(int playerId) {
        return (mInfoCommon.mLordPlayerId == playerId);
    }

    public boolean isEnemy(int selfPlayerId, int otherPlayerId) {
        if (isLandLord(selfPlayerId)) {
            return true;
        } else if (isLandLord(otherPlayerId)) {
            return true;
        }

        return false;
    }

    public boolean isEnemy(int otherPlayerId) {
        return isEnemy(mInfoMyself.mSelfPlayerId, otherPlayerId);
    }

    // ---------------------------------------------

    public int prevShowLeftCardNum() {
        if (mInfoCommon.mPrevShowPlayerId != -1) {
            return mInfoCommon.mLeftCardNums[mInfoCommon.mPrevShowPlayerId];
        }
        return -1;
    }

    public int prevPlayerLeftCardNum() {
        if (mInfoMyself.mLeftPlayerId != -1) {
            return mInfoCommon.mLeftCardNums[mInfoMyself.mLeftPlayerId];
        }
        return -1;
    }

    public int nextPlayerLeftCardNum() {
        if (mInfoMyself.mRightPlayerId != -1) {
            return mInfoCommon.mLeftCardNums[mInfoMyself.mRightPlayerId];
        }
        return -1;
    }

    // ---------------------------------------------

    public int[] getLeftCardNums() {
        return mInfoCommon.mLeftCardNums;
    }

    public void setLeftCardNums(int[] leftCardNums) {
        for (int i = 0; i < leftCardNums.length; i++) {
            mInfoCommon.mLeftCardNums[i] = leftCardNums[i];
        }
    }

    public void setLeftCardNum(int playerId, int leftCardNum) {
        mInfoCommon.mLeftCardNums[playerId] = leftCardNum;
    }

    public void setSelfPlayerLeftCardNum(int selfPlayerCardNum) {
        mInfoCommon.mLeftCardNums[mInfoMyself.mSelfPlayerId] = selfPlayerCardNum;
    }

    public void setLeftPlayerLeftCardNum(int leftPlayerCardNum) {
        mInfoCommon.mLeftCardNums[mInfoMyself.mLeftPlayerId] = leftPlayerCardNum;
    }

    public void setRightPlayerLeftCardNum(int rightPlayerCardNum) {
        mInfoCommon.mLeftCardNums[mInfoMyself.mRightPlayerId] = rightPlayerCardNum;
    }

    // ---------------------------------------------

    public boolean isCurrShowPlayerSelf() {
        return (mInfoCommon.mCurrShowPlayerId == mInfoMyself.mSelfPlayerId);
    }

    public boolean isCurrShowPlayerPrev() {
        return (mInfoCommon.mCurrShowPlayerId == mInfoMyself.mLeftPlayerId);
    }

    public boolean isCurrShowPlayerNext() {
        return (mInfoCommon.mCurrShowPlayerId == mInfoMyself.mRightPlayerId);
    }

    // ---------------------------------------------

    public boolean isPrevShowPlayerInSelfPos() {
        return (mInfoCommon.mPrevShowPlayerId == mInfoMyself.mSelfPlayerId);
    }

    public boolean isPrevShowPlayerInPrevPos() {
        return (mInfoCommon.mPrevShowPlayerId == mInfoMyself.mLeftPlayerId);
    }

    public boolean isPrevShowPlayerInNextPos() {
        return (mInfoCommon.mPrevShowPlayerId == mInfoMyself.mRightPlayerId);
    }

    // ---------------------------------------------

    public boolean isPlayerSelf(int playerId) {
        return (playerId == mInfoMyself.mSelfPlayerId);
    }

    public boolean isPlayerLeft(int playerId) {
        return (playerId == mInfoMyself.mLeftPlayerId);
    }

    public boolean isPlayerRight(int playerId) {
        return (playerId == mInfoMyself.mRightPlayerId);
    }

    // ---------------------------------------------

    public boolean isThinkingShowCard() {
        return (mPlayerState == THINKING_SHOW_CARD);
    }

    public boolean isThinkingFollowCard() {
        return (mPlayerState == THINKING_FOLLOW_CARD);
    }

    public boolean isThinkingIdle() {
        return (mPlayerState == THINKING_IDLE);
    }
    
    //lhx 单独处理得到的牌，不做错误判断
    
    public int[] justGetSelectedCards(){
        int count = 0;
        int[] selfCardIds = getSelfCardIds();
        int cardNum = selfCardIds.length;
        for (int i = 0; i < cardNum; i++) {
            if (mInfoMyself.mSelects[i]) {
                count++;
            }
        }

        if (count <= 0) {
            return null;
        }

        int[] selectCardIds = new int[count];
        int j = 0;
        for (int i = 0; i < cardNum; i++) {
            if (mInfoMyself.mSelects[i]) {
                selectCardIds[j++] = selfCardIds[i];
            }
        }
        return selectCardIds;
    }

    // ---------------------------------------------

    public int[] selectedCardIds() {
        int count = 0;
        int[] selfCardIds = getSelfCardIds();
        int cardNum = selfCardIds.length;
        for (int i = 0; i < cardNum; i++) {
            if (mInfoMyself.mSelects[i]) {
                count++;
            }
        }

        if (count <= 0) {
            return null;
        }

        int[] selectCardIds = new int[count];
        int j = 0;
        for (int i = 0; i < cardNum; i++) {
            if (mInfoMyself.mSelects[i]) {
                selectCardIds[j++] = selfCardIds[i];
            }
        }

        int cardType = CardUtil.getCardType(selectCardIds);
        if (cardType == CardType.LL_NONE) {
            ToastUtil.show("牌型组合错误");
            return null;
        }

        // 此轮第一个出牌
        if (isThinkingShowCard()) {
            return selectCardIds;
        }

        // 跟牌
        if (mInfoCommon.mPrevShowCardIds!=null && CardUtil.compare(selectCardIds, mInfoCommon.mPrevShowCardIds)) {
            return selectCardIds;
        } else {
            ToastUtil.show("牌太小 或 张数错误");
            return null;
        }
    }

    public void setCardIdsSelect(int[] cardIds) {
        int[] selfCardIds = getSelfCardIds();
        for (int i = selfCardIds.length - 1; i >= 0; i--) {
            for (int j = cardIds.length - 1; j >= 0; j--) {
                if (selfCardIds[i] == cardIds[j]) {
                    mInfoMyself.mSelects[i] = true;
                }
            }
        }
    }

    public void resetCardIdsSelect() {
        for (int i = mInfoMyself.mSelects.length - 1; i >= 0; i--) {
            mInfoMyself.mSelects[i] = false;
        }
    }

    public void reset() {
        mPlayerState = THINKING_IDLE;
        mInfoMyself.reset();
        mInfoCommon.reset();
    }

    public void initRound() {
        mInfoCommon.mRoundIndex = 0;
        mInfoMyself.mWinWinNum = 0;
    }

    public void increaseRound(int roundNum) {
        mInfoCommon.mRoundIndex++;
        mInfoCommon.mRoundIndex %= roundNum;
    }

    public int getRoundIndex() {
        return mInfoCommon.mRoundIndex;
    }

    public void increaseWinWinNum() {
        mInfoMyself.mWinWinNum++;
    }

    public void resetWinWinNum() {
        mInfoMyself.mWinWinNum = 0;
    }

    public void increaseWinNum() {
        mInfoMyself.mWinNum++;
    }

    public void increaseLoseNum() {
        mInfoMyself.mLoseNum++;
    }

    public void resetWinLoseNum() {
        mInfoMyself.mWinNum = 0;
        mInfoMyself.mLoseNum = 0;
    }

    public int getWinWinNum() {
        return mInfoMyself.mWinWinNum;
    }

    public int getWinNum() {
        return mInfoMyself.mWinNum;
    }

    public int getLoseNum() {
        return mInfoMyself.mLoseNum;
    }

    public int getDeclareNum() {
        return mInfoCommon.mDeclareNum;
    }

    public int getRate() {
        return mInfoCommon.mRate;
    }

    public int getSelfPlayerId() {
        return mInfoMyself.mSelfPlayerId;
    }

    public void setSelfPlayerId(int playerId) {
        mInfoMyself.mSelfPlayerId = playerId;
    }

    public void setRightPlayerId(int playerId) {
        mInfoMyself.mRightPlayerId = playerId;
    }

    public void setLeftPlayerId(int playerId) {
        mInfoMyself.mLeftPlayerId = playerId;
    }

    // ------------------------------------------------

    public int[] getPrevShowCardIds() {
        return mInfoCommon.mPrevShowCardIds;
    }

    public void setPrevShowCardIds(int[] prevShowCardIds) {
        mInfoCommon.mPrevShowCardIds = prevShowCardIds;
    }

    public int getPrevShowPlayerId() {
        return mInfoCommon.mPrevShowPlayerId;
    }

    public void setPrevShowPlayerId(int playerId) {
        mInfoCommon.mPrevShowPlayerId = playerId;
    }

    // ------------------------------------------------

    public int getBeautyId(int playerId) {
        if (playerId == mInfoMyself.mLeftPlayerId) {
            return mInfoMyself.mLeftBeautyId;
        } else if (playerId == mInfoMyself.mRightPlayerId) {
            return mInfoMyself.mRightBeautyId;
        }

        return -1;
    }

}
