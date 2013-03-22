
package com.hurray.landlord.net.socket.srv;

import com.hurray.landlord.net.socket.ServerMessage;
import com.hurray.landlord.net.socket.SocketStream;

public class ServerWaitRoom extends ServerMessage {
    /**
     * 
     */
    private static final long serialVersionUID = -912825657656295984L;

    public ServerWaitRoom(SocketStream stream) {
        super(stream);
    }

    @Override
    protected void unPackJson(String jsonString) {
        
    }

}
