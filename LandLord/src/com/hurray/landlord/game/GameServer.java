
package com.hurray.landlord.game;

import com.hurray.landlord.net.socket.ClientMessage;

public interface GameServer {

    public void connect();
    
    public void disconnect();

    public void send(ClientMessage cltMsg);

    public void setOnGameEventListener(OnGameEventListener listener);
}
