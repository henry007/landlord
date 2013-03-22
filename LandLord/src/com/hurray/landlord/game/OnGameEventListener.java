
package com.hurray.landlord.game;

import com.hurray.landlord.net.socket.ClientMessage;
import com.hurray.landlord.net.socket.ServerMessage;

public interface OnGameEventListener {

    public void onGameStart(boolean success);

    public void onGameOver();

    public void onMessageSent(ClientMessage cMsg);

    public void onMessageReceived(ServerMessage sMsg);
}
