
package com.hurray.landlord.game.local;

// 反馈给房间的消息
public interface OnLandLordPlayerListener {

    public void declareLord(int playerId, int declareLordNum);
    
    public void robLord(int playerId, boolean isRob);

    public void showCards(int playerId, int[] showCards);

    public void showPass(int playerId);

    public void ready(int playerId);
    
    public void gotCards(int playerId);

    public void chat(int playerId, String message, int chatId);
    
    public void emotion(int playerId, int emotionId);
    
    public void goon(int playerId);

}
