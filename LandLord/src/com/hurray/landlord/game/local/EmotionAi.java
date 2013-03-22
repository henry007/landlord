
package com.hurray.landlord.game.local;

public class EmotionAi implements OnAiContextListener {

    // private boolean[] mIsGotFace;

    private OnEmotionListener mOnEmotionListener;

    public EmotionAi() {
        // mIsGotFace = new boolean[] {
        // false, false, false
        // };
    }

    // public void resetFace() {
    // for (int i = 0; i < Constants.PLAYER_NUM; i++) {
    // mIsGotFace[i] = false;
    // }
    // }

    public void setOnEmotionListener(OnEmotionListener l) {
        mOnEmotionListener = l;
    }

    // --------------------------------------------------

    @Override
    public void onMyPassedCountIsTooMuch(int playerId) {
        mOnEmotionListener.onCry(playerId);
    }

    @Override
    public void onMyPassStillCanHoldByMyself(int playerId) {
        mOnEmotionListener.onCute(playerId);
    }

    @Override
    public void onMyShowTotalPassedTooMuchByOthers(int playerId) {
        mOnEmotionListener.onLaugh(playerId);
    }

    @Override
    public void onMyShowPassedByEnemy(int playerId) {
        mOnEmotionListener.onProud(playerId);
    }

    @Override
    public void onMyShowFollowedByEnemy(int playerId) {
        mOnEmotionListener.onAnger(playerId);
    }

    @Override
    public void onLordsShowFollowedByFriendWhichPassedByMeEarlier(int myPlayerId) {
        mOnEmotionListener.onLuck(myPlayerId);
    }
    
    @Override
    public void onLordsShowFollowedByMeWhichPassedByFriendEarlier(int myPlayerId) {
        mOnEmotionListener.onProud(myPlayerId);
    }

    @Override
    public void onMyShowIsBig(int playerId) {
        mOnEmotionListener.onProud(playerId);
    }

    @Override
    public void onMyEnemyShowIsBig(int playerId) {
        mOnEmotionListener.onAnger(playerId);
    }

    @Override
    public void onIWinMatch(int playerId) {
        mOnEmotionListener.onLaugh(playerId);
    }

    @Override
    public void onILostMatch(int playerId) {
        mOnEmotionListener.onCry(playerId);
    }

    @Override
    public void onMyShowFollowed(int playerId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMyShowFollowedByFriend(int playerId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMyFriendShowIsBig(int playerId) {
        // TODO Auto-generated method stub
        
    }
    
    @Override
    public void onMyLeftCardNumLess(int playerId) {
        mOnEmotionListener.onLaugh(playerId);
    }
    
    public static interface OnEmotionListener {

        public void onCry(int playerId);

        public void onCute(int playerId);

        public void onLaugh(int playerId);

        public void onLuck(int playerId);

        public void onProud(int playerId);

        public void onAnger(int playerId);
    }

    @Override
    public void onMyFriendCardNumLess(int playerId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onMyEnemyCardNumLess(int playerId) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onShowCardType(int playerId, int cardType) {
        // TODO Auto-generated method stub
        
    }

}
