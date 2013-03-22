package com.hurray.landlord.game.local;

public interface OnAiContextListener {
    
    public void onMyPassedCountIsTooMuch(int playerId);

    public void onMyPassStillCanHoldByMyself(int playerId);
    
    public void onMyShowTotalPassedTooMuchByOthers(int playerId);
    
    public void onMyShowPassedByEnemy(int playerId);
    
    public void onMyShowFollowed(int playerId);
    public void onMyShowFollowedByEnemy(int playerId);
    public void onMyShowFollowedByFriend(int playerId);
    
    public void onLordsShowFollowedByFriendWhichPassedByMeEarlier(int myPlayerId);
    
    public void onLordsShowFollowedByMeWhichPassedByFriendEarlier(int myPlayerId);
    
    public void onMyShowIsBig(int playerId);
    
    public void onMyFriendShowIsBig(int playerId);
    
    public void onMyEnemyShowIsBig(int playerId);    
    
    public void onIWinMatch(int playerId);
    
    public void onILostMatch(int playerId);
    
    public void onMyLeftCardNumLess(int playerId);
    
    public void onMyFriendCardNumLess(int playerId);
    
    public void onMyEnemyCardNumLess(int playerId);
    
    public void onShowCardType(int playerId, int cardType);
}
