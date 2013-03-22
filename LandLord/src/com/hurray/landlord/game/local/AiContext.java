
package com.hurray.landlord.game.local;

import com.hurray.landlord.Constants;
import com.hurray.landlord.game.CardType;
import com.hurray.landlord.game.data.PlayerContext;

import java.util.ArrayList;
import java.util.Iterator;

public class AiContext implements OnAiContextListener {

    private PlayerContext mPlayerCtx;

    private int[] mPassedCount;

    private ArrayList<OnAiContextListener> mListeners;

    public AiContext(PlayerContext playerCtx) {

        mPlayerCtx = playerCtx;

        mPassedCount = new int[] {
                0, 0, 0
        };

        mListeners = new ArrayList<OnAiContextListener>();
    }

    protected boolean isPassTooMuch(int currPlayerId) {
        synchronized (mPassedCount) {
            return mPassedCount[currPlayerId] >= 2;
        }
    }

    protected boolean isTooMuchTotalPass() {
        synchronized (mPassedCount) {
            int count = 0;
            for (int i = 0; i < mPassedCount.length; i++) {
                count += mPassedCount[i];
            }
            return count >= 2;
        }
    }

    public void doPassed(int currPlayerId) {

        synchronized (mPassedCount) {
            mPassedCount[currPlayerId]++;
        }

        if (isPassTooMuch(currPlayerId)) {
            onMyPassedCountIsTooMuch(currPlayerId);
        } else {
            onMyPassStillCanHoldByMyself(currPlayerId);
        }

        int prevShowPlayerId = mPlayerCtx.mInfoCommon.mPrevShowPlayerId;
        if (isTooMuchTotalPass()) {
            onMyShowTotalPassedTooMuchByOthers(prevShowPlayerId);
        } else if (mPlayerCtx.isEnemy(prevShowPlayerId, currPlayerId)) { // 对家
            onMyShowPassedByEnemy(prevShowPlayerId);
        }

    }

    public void doShow(int currPlayerId, int[] showCardIds, int cardType) {
        synchronized (mPassedCount) {
            mPassedCount[currPlayerId] = 0;
        }

        doBigShow(currPlayerId, showCardIds, cardType);
    }

    public void doShowCardType(int playerId, int cardType) {
        onShowCardType(playerId, cardType);
    }

    public void doLeftCardNumDanger(int playerId) {

        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            if (playerId == i) {
                onMyLeftCardNumLess(i);
            } else if (mPlayerCtx.isEnemy(playerId, i)) {
                onMyEnemyCardNumLess(i);
            } else {
                onMyFriendCardNumLess(i);
            }
        }

    }

    public void doFollowed(int beFollowedPlayerId, int followPlayerId, int[] showCardIds, int cardType) {

        onMyShowFollowed(beFollowedPlayerId);

        // 被对家压牌
        if (mPlayerCtx.isEnemy(beFollowedPlayerId, followPlayerId)) {
            onMyShowFollowedByEnemy(beFollowedPlayerId);
        } else {
            onMyShowFollowedByFriend(beFollowedPlayerId);
        }

        // 地主出牌后被跟牌
        if (mPlayerCtx.isLandLord(beFollowedPlayerId)) {
            // 地主出牌，我没管上，下家管上了
            int nextLordId = beFollowedPlayerId + 1;
            nextLordId %= Constants.PLAYER_NUM;
            synchronized (mPassedCount) {
                if (mPassedCount[nextLordId] > 0) {
                    onLordsShowFollowedByFriendWhichPassedByMeEarlier(nextLordId);
                }
            }

            // 地主出牌，上家过牌，我管上了
            int prevCompanyId = followPlayerId - 1 + Constants.PLAYER_NUM;
            prevCompanyId %= Constants.PLAYER_NUM;
            if (beFollowedPlayerId != prevCompanyId) { // 上家农民
                synchronized (mPassedCount) {
                    if (mPassedCount[prevCompanyId] > 0) {
                        onLordsShowFollowedByMeWhichPassedByFriendEarlier(followPlayerId);
                    }
                }
            }

        }

        // 最后优先级处理 大牌表情
        doBigShow(followPlayerId, showCardIds, cardType);
    }

    public void doResult(boolean lordWin, int lordPlayerId) {
        for (int i = 0; i < Constants.PLAYER_NUM; i++) {
            if (i == lordPlayerId) {
                if (lordWin) {
                    doWin(i);
                } else {
                    doFail(i);
                }
            } else {
                if (lordWin) {
                    doFail(i);
                } else {
                    doWin(i);
                }
            }
        }
    }

    private void doBigShow(int showPlayerId, int[] showCardIds, int cardType) {
        if (showCardIds.length > 6 ||
                cardType == CardType.LL_FOUR_AS_BOMBER ||
                cardType == CardType.LL_JOKER_AS_ROCKET) {

            // 自己高兴，别人怒
            for (int i = 0; i < Constants.PLAYER_NUM; i++) {
                if (showPlayerId == i) {
                    onMyShowIsBig(showPlayerId);
                } else {
                    if (mPlayerCtx.isEnemy(i, showPlayerId)) {
                        onMyEnemyShowIsBig(i);
                    } else {
                        onMyFriendShowIsBig(i);
                    }
                }
            }
        }

    }

    private void doWin(int currPlayerId) {
        onIWinMatch(currPlayerId);

    }

    private void doFail(int currPlayerId) {
        onILostMatch(currPlayerId);
    }

    // --------------------------------------------------

    public void addListener(OnAiContextListener l) {
        mListeners.add(l);
    }

    public void removeListener(OnAiContextListener l) {
        mListeners.remove(l);
    }

    // --------------------------------------------------

    @Override
    public void onMyPassedCountIsTooMuch(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyPassedCountIsTooMuch(playerId);
        }
    }

    @Override
    public void onMyPassStillCanHoldByMyself(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyPassStillCanHoldByMyself(playerId);
        }
    }

    @Override
    public void onMyShowTotalPassedTooMuchByOthers(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyShowTotalPassedTooMuchByOthers(playerId);
        }
    }

    @Override
    public void onMyShowPassedByEnemy(int myPlayerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyShowPassedByEnemy(myPlayerId);
        }
    }

    @Override
    public void onMyShowFollowed(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyShowFollowed(playerId);
        }
    }

    @Override
    public void onMyShowFollowedByFriend(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyShowFollowedByFriend(playerId);
        }
    }

    @Override
    public void onMyShowFollowedByEnemy(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyShowFollowedByEnemy(playerId);
        }
    }

    @Override
    public void onLordsShowFollowedByFriendWhichPassedByMeEarlier(int myPlayerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onLordsShowFollowedByFriendWhichPassedByMeEarlier(myPlayerId);
        }
    }

    @Override
    public void onLordsShowFollowedByMeWhichPassedByFriendEarlier(int myPlayerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onLordsShowFollowedByMeWhichPassedByFriendEarlier(myPlayerId);
        }
    }

    @Override
    public void onMyShowIsBig(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyShowIsBig(playerId);
        }
    }

    @Override
    public void onMyFriendShowIsBig(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyFriendShowIsBig(playerId);
        }
    }

    @Override
    public void onMyEnemyShowIsBig(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyEnemyShowIsBig(playerId);
        }
    }

    @Override
    public void onIWinMatch(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onIWinMatch(playerId);
        }
    }

    @Override
    public void onILostMatch(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onILostMatch(playerId);
        }
    }

    @Override
    public void onMyLeftCardNumLess(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyLeftCardNumLess(playerId);
        }
    }

    @Override
    public void onMyFriendCardNumLess(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyFriendCardNumLess(playerId);
        }
    }

    @Override
    public void onMyEnemyCardNumLess(int playerId) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onMyEnemyCardNumLess(playerId);
        }
    }

    @Override
    public void onShowCardType(int playerId, int cardType) {
        Iterator<OnAiContextListener> it = mListeners.iterator();
        while (it.hasNext()) {
            it.next().onShowCardType(playerId, cardType);
        }
    }

    // --------------------------------------------------

}
