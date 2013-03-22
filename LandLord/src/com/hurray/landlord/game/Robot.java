
package com.hurray.landlord.game;

import com.hurray.landlord.game.data.PlayerContext;

public class Robot {

    private PlayerContext mPlayerContext;

    public Robot(PlayerContext playerContext) {
        mPlayerContext = playerContext;
    }
    
    public PlayerContext getPlayerContext(){
        
        return mPlayerContext;
        
    }

    public int suggestDeclareLordNum() {
        int[] selfCardIds = mPlayerContext.getSelfCardIds();
        int num = CardUtil.suggestDeclareLordNum(selfCardIds);
        return num;
    }

    public boolean suggestRobLord() {
        int[] selfCardIds = mPlayerContext.getSelfCardIds();
        return CardUtil.suggestRobLord(selfCardIds);
    }

    public int[] suggestFirstShow() {
        int nextEnemyLeftCardNum;
        if (mPlayerContext.isSelfLandLord() || mPlayerContext.isNextPlayerLandLord()) { // 下家是敌人
            nextEnemyLeftCardNum = mPlayerContext.nextPlayerLeftCardNum();
        } else {
            nextEnemyLeftCardNum = -1;
        }

        int[] selfCardIds = mPlayerContext.getSelfCardIds();

        int[] leadShow = CardUtil.firstShowCards(selfCardIds, nextEnemyLeftCardNum);

        return leadShow;
    }

    public int[] suggestFollowShow() {

        int[] followCardIds = CardUtil.followCards(mPlayerContext);
        if (followCardIds != null) {
            int[] prevCards = mPlayerContext.mInfoCommon.mPrevShowCardIds;
            if (CardUtil.compare(followCardIds, prevCards)) {
                return followCardIds;
            }
        }

        return null;

    }

}
